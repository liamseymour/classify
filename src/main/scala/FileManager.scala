package classify

import java.io.File
import java.nio.file._
import java.io.ObjectOutputStream
import java.io.ObjectInputStream
import java.io.FileOutputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import scala.io.Source
import scala.collection.View

class FileManager(var working_dir: String) {
	protected val ROOT_FILE = ".classifyroot"
	protected val COMPOSER_FILE = ".classifycomposer"
	protected val COMPOSITION_FILE = ".classifycomposition"
	protected val RECORDING_FILE = ".classifyrecording"

	working_dir = new File(working_dir).getCanonicalPath

	val root = findHostDir(working_dir, ROOT_FILE)
	val root_file: String = 
		if (root != null) Paths.get(root, ROOT_FILE).toString else null

	val composer_dir = findHostDir(working_dir, COMPOSER_FILE)
	val composer_file: String = if (composer_dir != null) 
		Paths.get(composer_dir, COMPOSER_FILE).toString else null
	val composer = loadObject(composer_file).asInstanceOf[Composer]

	val composition_dir = findHostDir(working_dir, COMPOSITION_FILE)
	val composition_file: String = if (composition_dir != null) 
		Paths.get(composition_dir, COMPOSITION_FILE).toString else null
	val composition = loadObject(composition_file).asInstanceOf[Composition]

	val recording_dir = findHostDir(working_dir, RECORDING_FILE)
	val recording_file: String = if (recording_dir != null) 
		Paths.get(recording_dir, RECORDING_FILE).toString else null
	val recording = loadObject(recording_file).asInstanceOf[Recording]

	def initialize(): Boolean = 
		if (root != null)
			false
		else
			new File(ROOT_FILE).createNewFile


	def newComposer(comp: Composer): Boolean = {
		val comp_dir = Paths.get(root, comp.name).toFile
		val success = comp_dir.mkdir()
		if (!success) { return false }
		dumpObject(comp, Paths.get(comp_dir.toString, COMPOSER_FILE).toString)
		true
	}

	def newComposition(comp: Composition): Boolean = {
		val composer_dir = Paths.get(root, comp.composer.name).toString
		val composition_dir = Paths.get(composer_dir, comp.title).toFile
		val success = composition_dir.mkdir()
		if (!success) { return false }
		dumpObject(comp, Paths.get(composition_dir.toString, COMPOSITION_FILE).toString)
		true
	}

	def newRecording(rec: Recording): Boolean = {
		val composer_dir = Paths.get(root, rec.composition.composer.name).toString
		val composition_dir = Paths.get(composer_dir, rec.composition.title).toString
		val rec_dir = Paths.get(composition_dir, rec.performersString).toFile
		val success = rec_dir.mkdir()
		if (!success) { 
			println("Unable to create recording directory.")
			return false 
		}
		if (!copyFiles(rec)) { 
			println("Unable to copy files.")
			return false 
		}
		dumpObject(rec, Paths.get(rec_dir.toString, RECORDING_FILE).toString)
		new TagSet(rec).applyTags()
		true
	}

	def getComposer(comp: String): Composer = {
		null
	}

	def printTree(dir: String): Unit = 
		printTreeRec(dir, 
			if (fileExists(dir, ROOT_FILE)) 0
			else if (fileExists(dir, COMPOSER_FILE)) 1
			else if (fileExists(dir, COMPOSITION_FILE)) 2
			else if (fileExists(dir, RECORDING_FILE)) 3
			else 4, 0)

	protected def printTreeRec(dir: String, depth: Int, indent: Int): Unit = 
		depth match {
			case 0 => /* Root layer. */
				getListOfDirs(dir).foreach( f => printTreeRec(f.getPath(), 1, indent))
			case 1 => { /* Composer layer. */
				if (fileExists(dir, COMPOSER_FILE)) {
					val composer = loadObject(
						Paths.get(dir, COMPOSER_FILE).toString())
						.asInstanceOf[Composer]
					println(s"${"  "*indent}${composer.name}")
					getListOfDirs(dir).foreach( f => printTreeRec(f.getPath(), 2, indent+1))
				} 
			}
			case 2 => { /* Composition layer. */
				if (fileExists(dir, COMPOSITION_FILE)) {
					val composition = loadObject(
						Paths.get(dir, COMPOSITION_FILE).toString())
						.asInstanceOf[Composition]
					println(s"${"  "*indent}${composition.title}")
					getListOfDirs(dir).foreach( f => printTreeRec(f.getPath(), 3, indent+1))
				}
			}
			case 3 => { /* Recording layer. */
				if (fileExists(dir, RECORDING_FILE)) {
					val recording = loadObject(
						Paths.get(dir, RECORDING_FILE).toString())
						.asInstanceOf[Recording]
					println(s"${"  "*indent}${recording.performersString}")
				}
			}
			case _ => ;
		}

	def printSearch(term: String): Unit = {

	}

	/* Move files initially pointed to by recording into the repository and
	 * apply new tags and file names. Return true if success. */
	protected def copyFiles(rec: Recording): Boolean = {
		def extention(s: String): String =
			s.slice(s.lastIndexWhere(_ == '.'), s.length)

		val old_files = rec.files.map(new File(_).getCanonicalPath().toString())
		val extentions = rec.files.map(extention(_))

		val new_path_dir = Paths.get(root, rec.composition.composer.name, 
			rec.composition.title, rec.performersString).toString

		val new_files = new View.Zip(rec.composition.movement_titles, extentions)
			.map(tup => Paths.get(new_path_dir, tup._1 + tup._2).toString).toArray

		val rets = new View.Zip(old_files, new_files)
			.map(tup => copyFile(tup._1, tup._2))
			
		if (!rets.exists(_ == false)) {
			rec.files = new_files
			true
		} else {
			false
		}
	}

	protected def copyFile(f_old: String, f_new: String): Boolean = {
		try {
			Files.copy(new File(f_old).toPath, new File(f_new).toPath, 
				StandardCopyOption.REPLACE_EXISTING)
			true
		} catch {
			case e: Throwable => {
				println(s"Cannot copy file '$f_old' to '$f_new'.")
				false
			}
		}
	}

	protected def dumpObject(obj: Serializable, path: String): Unit = {
		val oos = new ObjectOutputStream(new FileOutputStream(path))
		oos.writeObject(obj)
		oos.close()
	}

	protected def loadObject(path: String): Any = {
		if (path == null)
			return null
		try {
			val ois = new ObjectInputStream(new FileInputStream(path))
			val stock = ois.readObject
			ois.close
			stock
		} catch {
			case e: FileNotFoundException => null
			case e: IOException => null
		}
	}

	/* Walk up the file tree and look for a directory with targetFile in it.
	 * Return the directory if found, or null if root is reached. */
	protected def findHostDir(dir: String, targetFile: String): String = {
		/* Recursive solution is fine; directory trees are very short. */
		if (fileExists(dir, targetFile))
			return dir
		val wd = new File(dir)
		if (wd.getParentFile == null)
			return null
		else
			return findHostDir(wd.getParentFile.toString(), targetFile)
	}
	
	protected def getListOfFiles(dir: String): List[File] = {
		val d = new File(dir)
		if (d.exists && d.isDirectory) {
			d.listFiles.filter(_.isFile).toList
		} else {
			List[File]()
		}
	}

	protected def getListOfDirs(dir: String): List[File] = {
		val d = new File(dir)
		if (d.exists && d.isDirectory) {
			d.listFiles.filter(_.isDirectory).toList
		} else {
			List[File]()
		}
	}

	protected def fileExists(dir: String, file: String) = {
		new File(Paths.get(dir, file).toString()).exists
	}
}

package classify

import java.io.File
import java.nio.file.Paths
import java.io.ObjectOutputStream
import java.io.ObjectInputStream
import java.io.FileOutputStream
import java.io.FileInputStream
import scala.io.Source

class FileManager(val working_dir: String) {
	private val ROOT_FILE = ".classifyroot"
	private val COMPOSER_FILE = ".classifycomposer"
	private val COMPOSITION_FILE = ".classifycomposition"
	private val RECORDING_FILE = ".classifyrecording"

	val root = findHostDir(working_dir, ROOT_FILE)
	val root_file: String = if (root != null) Paths.get(root, ROOT_FILE).toString else null

	val composer_dir = findHostDir(working_dir, COMPOSER_FILE)
	val composer_file: String = if (composer_dir != null) 
		Paths.get(composer_dir, COMPOSER_FILE).toString else null

	val composition_dir = findHostDir(working_dir, COMPOSITION_FILE)
	val composition_file: String = if (composition_dir != null) 
		Paths.get(composition_dir, COMPOSITION_FILE).toString else null

	val recording_dir = findHostDir(working_dir, RECORDING_FILE)
	val recording_file: String = if (recording_dir != null) 
		Paths.get(recording_dir, RECORDING_FILE).toString else null

	def dumpObject(obj: Serializable, path: String): Unit = {
		val oos = new ObjectOutputStream(new FileOutputStream(path))
		oos.writeObject(obj)
		oos.close()
	}

	def loadObject(path: String): Any = {
		val ois = new ObjectInputStream(new FileInputStream(path))
		val stock = ois.readObject
		ois.close
		stock
	}

	/* Walk up the file tree and look for a directory with targetFile in it.
	 * Return the directory if found, or null if root is reached. */
	private def findHostDir(dir: String, targetFile: String): String = {
		/* Recursive solution is fine; directory trees are very short. */
		if (fileExists(dir, targetFile))
			return dir
		val wd = new File(dir)
		if (wd.getParentFile == null)
			return null
		else
			return findHostDir(wd.getParentFile.toString(), targetFile)
	}
	
	private def getListOfFiles(dir: String): List[File] = {
		val d = new File(dir)
		if (d.exists && d.isDirectory) {
			d.listFiles.filter(_.isFile).toList
		} else {
			List[File]()
		}
	}

	private def getListOfDirs(dir: String): List[File] = {
		val d = new File(dir)
		if (d.exists && d.isDirectory) {
			d.listFiles.filter(_.isDirectory).toList
		} else {
			List[File]()
		}
	}

	private def fileExists(dir: String, file: String) = {
		new File(Paths.get(dir, file).toString()).exists
	}
}

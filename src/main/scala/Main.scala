package classify

import classify.Composer
import classify.Composition

object Main extends App {

	val delimiter = ";"
	
	if (!execArgs()) {
		println(s"Invalid usage: classify ${args.mkString(" ")}")
		println(s"See 'classify help' for usage details.")
	}
	
	/* Execute the user command. Return false if command is malformed, true
	 * otherwise (even if the command fails). */
	def execArgs(): Boolean = {
		val argc = args.length
		if (argc <= 0)
			return false
		
		val dir_opt = getOption("-d", args) 
		val fm = if (dir_opt != null)
			new FileManager(dir_opt)
		else	
			new FileManager(System.getProperty("user.dir"))

		if (getOptions("init", args) != null) {
			if (!fm.initialize())
				printFail("Failed to initialize repository.")
			return true
		} else if (fm.root == null && args(0) != "help") {
			printFail("No classify repository located.")
		}

		/* TODO: Handle unparsable integers. */
		/* TODO: Long form of argument flags. */

		args match {
			case Array("help", _*) => usage()

			/* Adding cases: */
			case Array("add", "r", _*) => {
				val composition = fm.composition
				val conductors = nullToArray(getOptions("-c", args))
				val orchestras = nullToArray(getOptions("-o", args))
				val soloists = nullToArray(getOptions("-s", args))
				val choirs = nullToArray(getOptions("-h", args))
				val other_performers = nullToArray(getOptions("-p", args))
				val year_opt = getOption("-y", args)
				val year = if (year_opt == null) -1 else year_opt.toInt
				val files = getFiles(args)

				required(composition, "Composition: inferred")
				required(files, "Files, usage: classify add r [OPTIONS] -f FILES")

				val rec = new Recording(composition, conductors, orchestras, soloists, 
					choirs, other_performers, year, files)
				val success = fm.newRecording(rec)
				if (!success)
					println(s"Failed to add recording.")
			}
			case Array("add", "c", _*) => {
				val composer_opt = getOption("-C", args)
				val composer = if (composer_opt == null) fm.composer else fm.getComposer(composer_opt)
				val form = getOption("-f", args)
				val opus_opt = getOption("-o", args)
				val opus = if (opus_opt == null) "Op." else opus_opt
				val opus_number_opt = getOption("-O", args)
				val opus_number = if (opus_number_opt == null) -1 else opus_number_opt.toInt
				val number_opt = getOption("-n", args)
				val number = if (number_opt == null) -1 else number_opt.toInt
				val movement_names = getOptions("-m", args)
				val unique_name_opt = getOption("-u", args)
				val unique_name = if (unique_name_opt == null) "" else unique_name_opt
				val key_opt = getOption("-k", args)
				val key = if (key_opt == null) "" else key_opt

				if (unique_name == "")
					required(form, "form: -f --form")
				required(composer, "composer: -C --composer OR inferred")
				required(movement_names, "movement names: -m --movement-names")

				val comp = new Composition(composer, unique_name, form, opus,
					opus_number, number, key, movement_names)
				val success = fm.newComposition(comp)
				if (!success)
					println(s"Failed to add composition: ${comp.title}")
			}
			case Array("add", "C", name, _*) => {
				if (isSwitch(name)) printFail("Missing composer name.")
				val comp = new Composer(name)
				val success = fm.newComposer(comp)
				if (!success)
					println(s"Failed to add composer: $name")
			}
			case Array("add", "C") => 
				printFail("Missing composer name. Usage: add C [OPTIONS] NAME")

			/* List cases */
			case Array("ls", _*) => {
				val search_opt = getOption("-s", args)
				val all_opt = getOptions("-a", args)
				if (search_opt != null && search_opt == Array())
					printFail("Missing search term.")
				else if (search_opt != null) {
					fm.printSearch(search_opt)
				} else {
					fm.printTree(if (all_opt == null) fm.working_dir else fm.root)
				}
			}

			/* Search cases */
			case Array("find", term, _*) => 
				fm.printSearch(term)

			case _ => return false
		}

		true
	}

	protected def isSwitch(s: String) = s(0) == '-'

	/* Return an array of file names if present in argv. Otherwise, returns
	 * null. Files are assumed to be listed at the end of the command. */
	protected def getFiles(argv: Array[String]): Array[String] = {
		argv.indexOf("-f") match {
			case -1 => null
			case i => 
				if (i != argv.length-1) 
					argv.slice(i+1, argv.length) 
				else null
		}
	}

	/* Print and then die. */
	protected def printFail(s: String): Unit = {
		Console.err.println(s)
		sys.exit(0)
	}

	/* Variant of printFail, prints missing field message. */
	protected def required(variable: Any, name: String) = 
		if (variable == null) printFail(s"Missing required field: $name")

	/* Given command line arguments and an option to look for, returns null if
	 * option is not present, an empty array if the option is present with no
	 * associated value, otherwise returns an array of the option's values. */
	protected def getOptions(opt: String, argv: Array[String]): Array[String] = {
		val opt_i = argv.indexOf(opt)
		if (opt_i == -1) {
			null
		} else if (opt_i+1 < argv.length && !isSwitch(args(opt_i+1))) {
			argv(opt_i+1).split(delimiter).map(_.trim).filter(_.length > 0)
		} else {
			Array()
		}
	}

	/* Return null if nothing in the array, otherwise the (first) string in the
	 * array. */
	protected def oneOrNone(options: Array[String]): String = 
		if (options == null || options.length <= 0) null else options(0)

	protected def nullToArray(a: Array[String]): Array[String] = 
		if (a == null) Array() else a


	/* Variant of getOptions for when one and only one value is expected with opt. */
	protected def getOption(opt: String, argv: Array[String]): String = 
		oneOrNone(getOptions(opt, argv))

	/* Prints usage information. */
	protected def usage(): Unit = {
		println(
		s"""classify version 0.5.0
		|usage: classify COMMAND [OPTIONS] -f [FILES]
		|PREAMBLE:
		|  For conciseness, a few terms must be defined: ``Requried'' means the
		|  data must be present (either inferred or from the user). 
		|  Required fields may be ommited in the ``Inferred'' case, where data 
		|  can be inferred by the location in the file hierarchy. ``Plural'' 
		|  means that multiple values may be passed by a '$delimiter' delimited list.
		|
		|COMMAND:
		|  help      -  Displays this message.
		|  init      -  Create a new, blank repository in the working directory.
		|  add TYPE  -  Add a new composer / composition / recording.
		|               Where TYPE is r for recording, C for composer, or 
		|               c for composition. Usage:
		|                 add r [OPTIONS] FILES
		|                 add C [OPTIONS] NAME
		|                 add c [OPTIONS]
		|  ls        -  Lists cataloged music.
		|               Lists current based on working directory
		|               by default. 
		|  find TERM -  Search for something.
		|
		|OPTIONS:
		| The following options apply in any command.
		| --dir -d            -  Set the ``working directory.'' Performs as if the
		|                        command is run form this directory.
		| ls:
		|  --all, -a          -  List from repository root, not working directory.
		| add TYPE:           -  
		|
		| The following options apply to adding compositions:
		|  --form -f              - A name for the form: e.g. Symphony, 
		|                           Concerto, Violin Concerto... (Requried)
		|  --unique-name -u       - A unique name: replaces the form. From
		|                           is NOT required if this option is present.
		|                           e.g. The Planets
		|  --movement-names -m    - Movement names: does not include
		|                           number, composer, or opus number
		|                           but may include key and tempo
		|                           markings. (Requried, Plural)
		|  --opus -o              - Catalogue identifier, e.g. Op, BWV...
		|                           Default is 'Op' (Optional)
		|  --opus-number -O       - Number indexing into catalogue. (Optional)
		|  --number -n            - Number accompanying form: e.g. 9 for 
		|                           Symphony No. 9, or 2 for Concerto No. 2.
		|                           (Optional)
		|  --key, -k              - Key, e.g. C Major, D Minor, etc...
		|  --composer -C          - Composer: should probably be inferred by the 
		|                           working directory (Required, Inferred)
		|
		| The following options apply to adding recordings:
		|  --year -y              - Year of recording (Inferred, Optional)
		|  --conductors -c        - Conductors (Inferred, Plural, Optional)
		|  --orchestras -o        - Orchestras (Inferred, Plural, Optional)
		|  --soloists -s          - Soloists (Inferred,Plural, Optional)
		|  --choirs -h            - Choirs (Inferred, Plural, Optional)
		|  --other-performers -p  - Any other perfomers (Inferred, Plural, 
        |                           Optional)
		|  --files -f             - Movements as files. Everything after this
		|                           is understood to be a file.
		""".stripMargin
		)
	}
}

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

		val fm = new FileManager(System.getProperty("user.dir"))

		/* TODO: Handle unparsable integers. */
		/* TODO: Long form of argument flags. */

		args match {
			case Array("help", _*) => usage()

			/* Adding cases: */
			case Array("add", "r", _*) => {
				val composition = fm.composition
				val conductors = getOptions("-C", args)
				val orchestras = getOptions("-o", args)
				val soloists = getOptions("-s", args)
				val choirs = getOptions("-h", args)
				val other_perfromers = getOptions("-p", args)
				val year_opt = getOption("-y", args)
				val year = if (year_opt == null) -1 else year_opt.toInt

				required(composition, "Composition: inferred")

				val rec = new Recording(composition, conductors, orchestras, soloists, 
					choirs, other_perfromers, year)
				val success = fm.newRecording(rec)
				if (!success)
					println(s"Failed to add recording.")
			}
			case Array("add", "c", _*) => {
				val composer_opt = getOption("-C", args)
				val composer = if (composer_opt == null) fm.composer else fm.getComposer(composer_opt)
				val form = getOption("-f", args)
				val opus_opt = getOption("-o", args)
				val opus = if (opus_opt == null) "Op" else opus_opt
				val opus_number_opt = getOption("-O", args)
				val opus_number = if (opus_number_opt == null) -1 else opus_number_opt.toInt
				val number_opt = getOption("-n", args)
				val number = if (opus_number_opt == null) -1 else opus_number_opt.toInt
				val movement_names = getOptions("-m", args)

				required(form, "form: -f --form")
				required(composer, "composer: -C --composer OR inferred")
				required(movement_names, "movement names: -m --movement-names")

				val comp = new Composition(composer, form, opus, opus_number, number, movement_names)
				val success = fm.newComposition(comp)
				if (!success)
					println(s"Failed to add composition: ${comp.composition_title}")
			}
			case Array("add", "C", name) => {
				if (isSwitch(name)) printFail("Missing composer name.")
				val comp = new Composer(name)
				val success = fm.newComposer(comp)
				if (!success)
					println(s"Failed to add composer: $name")
			}
			case Array("add", "C") => 
				printFail("Missing composer name. Usage: add C [OPTIONS] NAME")
			/* List cases TODO */
			/* Remove cases TODO */
			case _ => return false
		}

		true
	}

	protected def printFail(s: String): Unit = {
		Console.err.println(s)
		sys.exit(0)
	}

	protected def required(variable: Any, name: String) = 
		if (variable == null) printFail(s"Missing required field: $name")

	protected def isSwitch(s: String) = s(0) == '-'

	protected def getOptions(opt: String, argv: Array[String]): Array[String] = {
		val opt_i = argv.indexOf(opt)
		if (opt_i == -1)
			null
		else if (opt_i+1 < argv.length && !isSwitch(argv(opt_i+1)))
			argv(opt_i+1).split(delimiter).map(_.trim).filter(_.length > 0)
		else
			Array()
	}

	protected def oneOrNone(options: Array[String]): String = 
		if (options == null || options.length <= 0) null else options(0)

	protected def getOption(opt: String, argv: Array[String]): String = 
		oneOrNone(getOptions(opt, argv))

	protected def usage(): Unit = {
		println(
		"""usage: classify COMMAND [OPTIONS] [FILES]
		|PREAMBLE:
		|  For conciseness, a few terms must be defined: ``Requried'' means the
		|  data must be present (either inferred or from the user). 
		|  Required fields may be ommited in the ``Inferred'' case, where data 
		|  can be inferred by the location in the file hierarchy. ``Plural'' 
		|  means that multiple values may be passed by a semicolon delimited list.
		|COMMAND:
		|  help  -  Displays this message.
		|  add   -  Add a new composer / composition / recording.
		|  rm    -  Delete a composer / composition / recording.
		|  ls    -  Lists cataloged music.
		|           Lists current based on working directory
		|           by default. Use '--search TERM' to search
		|           entire catalogue.
		|OPTIONS:
		| The following options apply in any command.
		| --dir -d        -  Set the ``working directory.'' Performs as if the
		|                    command is run form this directory.
		| ls:
		|  --search TERM  -  Search for TERM in the catalogue. (Optional)
		| add TYPE:       -  Where TYPE is r for recording, C for composer, or 
		|                    c for composition. Usage:
		|                    add r [OPTIONS] FILES
		|                    add C [OPTIONS] NAME
		|                    add c [OPTIONS]
		|
		| The following options apply to adding recordings:
		|  --year -y              - Year of recording (Inferred, Optional)
		|  --conductors -C        - Conductors (Inferred, Plural, Optional)
		|  --orchestras -o        - Orchestras (Inferred, Plural, Optional)
		|  --soloists -s          - Soloists (Inferred,Plural, Optional)
		|  --choirs -h            - Choirs (Inferred, Plural, Optional)
		|  --other-performers -p  - Any other perfomers (Inferred, Plural, 
        |                           Optional)
		|
		|  The following options apply to adding compositions:
		|  --composer -C          - Composer (Required, Inferred)
		|  --movement-names -m    - Movement names, does not include
		|                           number, composer, or opus number
		|                           but may include key and tempo
		|                           markings. (Requried, Plural)
		|  --opus -o              - Catalogue identifier, e.g. Op, BWV...
		|                           Default is 'Op' (Optional)
		|  --opus-number -O       - Number indexing into catalogue. (Optional)
		|  --form -f              - An arbitrary name for the form. E.g.
		|                           Symphony, Concerto, Violin Concerto...
		|                           (Requried)
		|  --number -n            - Number accompanying form, e.g. 9 for 
		|                           Symphony No. 9, or 2 for Concerto No. 2.
		|                           (Optional)
		""".stripMargin
		)
	}
}

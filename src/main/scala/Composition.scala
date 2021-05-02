package classify

import classify.Util

class Composition(
	val composer: Composer, /* To derive composer information. */
	val unique_name: String, /* e.g. The Planets */
	val form: String, /* Symphony, Piano Concerto, ...  */
	val opus: String, /* An opus identifier e.g. Op BVW, K ... */
	val opus_number: Int, /* Op 62, BVW 32 ... -1 indicates no associated opus number. */
	val number: Int, /* Number accompanying the name, e.g Symphony No. 5, 
					   Brandenburg Concerto No. 2 ... -1 Indicates no number. */
	val key: String, /* D Major, C Major etc... */
	val movement_names: Array[String] /* Names for individual movements, e.g. 
									     Sarabande (Andante). */
) extends Serializable {
	val title = createCompostionTitle()
	val movement_titles = createMovementTitles()
	
	protected def createCompostionTitle(): String = 
		if (unique_name != "")
			s"${composer.name}: $unique_name$keyString$opusString"
		else 
			s"${composer.name}: $form$numberString$keyString$opusString"
	
	protected def opusString: String = 
		if (opus_number != -1)
			s", $opus $opus_number"
		else
			""
	
	protected def numberString: String =
		if (number != -1)
			s" No. $number"
		else
			""
	
	protected def keyString: String =
		if (key != "")
			s" in $key"
		else
			""

	protected def createMovementTitles(): Array[String] = {
		movement_names.zipWithIndex.map {
			case (name, i) => s"$title: ${Util.arabicToRoman(i+1)}. $name"
		}
	}

	override def toString = {
		s"composer: $composer\nform: $form\nopus: $opus\nopus_number: $opus_number\nnumber: $number\nmovement_names: $movement_names"
	}
}

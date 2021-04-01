package classify

import classify.Util

class Composition(
	val composer: Composer, /* To derive composer information. */
	var form: String, /* Symphony, Piano Concerto, ...  */
	var opus: String, /* An opus identifier e.g. Op BVW, K ... */
	var opus_number: Int, /* Op 62, BVW 32 ... */
	var number: Int, /* Number accompanying the name, e.g Symphony No. 5, 
					   Brandenburg Concerto No. 2 ... -1 Indicates no number. */
	var movement_names: Array[String] /* Names for individual movements, e.g. 
									     Sarabande (Andante). */
) extends Serializable {
	val composition_title = createCompostionTitle()
	val movement_titles = createMovementTitles()
	
	private def createCompostionTitle(): String = {
		if (number != -1)
			return s"${composer.name}: $form No. $number $opus. $opus_number"
		else
			return s"${composer.name}: $form $opus. $opus_number"
	}

	private def createMovementTitles(): Array[String] = {
		movement_names.zipWithIndex.map {
			case (name, i) => s"$composition_title: ${Util.arabicToRoman(i+1)}. $name"
		}
	}

	override def toString = {
		s"composer: $composer\nform: $form\nopus: $opus\nopus_number: $opus_number\nnumber: $number\nmovement_names: $movement_names"
	}
}

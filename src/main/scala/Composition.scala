package classify

class Composition(
	val composer: Composer, /* To derive composer information. */
	var form: String, /* Symphony, Piano Concerto, ...  */
	var opus: String, /* An opus identifier e.g. Op BVW, K ... */
	var opus_number: Int, /* Op 62, BVW 32 ... */
	var number: Int, /* Number accompanying the name, e.g Symphony No. 5, 
					   Brandenburg Concerto No. 2 ... -1 Indicates no number. */
	var movement_names: Array[String] /* Names for individual movements, e.g. 
									     Sarabande (Andante). */
) {
	val composition_title = createCompostionTitle()
	val movement_titles = createMovementTitles()
	
	private def createCompostionTitle(): String = {
		if (number != -1)
			return s"${composer.name}: $form No. $number $opus. $opus_number"
		else
			return s"${composer.name}: $form $opus. $opus_number"
	}

	private def createMovementTitles(): Array[String] = {
		Array()
	}
}

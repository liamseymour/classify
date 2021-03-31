package classify

class TagSet(rec: Recording) {
	/* Fields for all tracks: */
	val year = rec.year
	val album_name = rec.composition.composition_title
	val soloists = rec.soloists
	val orchestras = rec.orchestras
	val conductors = rec.conductors
	val composer = rec.composition.composer
	val genre_number = 32

	/* Track specific fields: */
	val track_numbers = for (i <- 0 until rec.composition.movement_names.length) yield i+1
	val movement_titles = rec.composition.movement_titles

	def applyTags(files: Array[String]): Unit = {
		;
	}
}

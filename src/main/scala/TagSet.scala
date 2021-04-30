package classify

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.FieldKey
import java.io.File

class TagSet(rec: Recording) {
	/* Disable loggers */
	val pin = List(java.util.logging.Logger.getLogger("org.jaudiotagger"))

	pin.foreach(_.setLevel(java.util.logging.Level.OFF))

	/* Fields for all tracks: */
	val composition = rec.composition
	val composer = composition.composer
	val album_name = composition.title

	/* Order should be Soloists, Conductors, Orchestras, Choirs, Other Performers. */
	val artists = rec.soloists :++ rec.orchestras :++
		rec.conductors :++ rec.choirs :++ rec.other_performers

	/* Track specific fields: */
	val movement_titles = composition.movement_titles

	def applyTags(): Unit = {
		val track_total = rec.files.length

		for (i <- 0 until track_total) {
			val filename = rec.files(i)
			val f = AudioFileIO.read(new File(filename))
			val tag = f.createDefaultTag()
			if (rec.year != -1)
				tag.setField(FieldKey.YEAR, rec.year.toString)

			artists.foreach(tag.addField(FieldKey.ALBUM_ARTIST, _))

			artists.foreach(tag.addField(FieldKey.ARTIST, _))

			tag.setField(FieldKey.ALBUM, album_name)

			tag.setField(FieldKey.COMPOSER, composer.name)

			rec.conductors.foreach(tag.addField(FieldKey.CONDUCTOR, _))

			tag.setField(FieldKey.GENRE, "Classical")

			tag.setField(FieldKey.TITLE, movement_titles(i))

			tag.setField(FieldKey.TRACK, (i+1).toString)

			tag.setField(FieldKey.TRACK_TOTAL, track_total.toString)

			tag.setField(FieldKey.CATALOG_NO, composition.opus_number.toString)
			f.setTag(tag)
			f.commit()
		}
	}
}

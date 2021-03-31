package classify

import classify.Composer
import classify.Composition

object Main extends App {
	val lvb = new Composer("Ludwig van Beethoven")
	val ninth = new Composition(lvb, "Symphony", "Op", 124, 9, Array( 
		"Allegro ma non troppo, un poco maestoso", "Molto vivace", 
		"Adagio molto e cantabile", "Finale"))
	
	val moonlight = new Composition(lvb, "Moonlight Sonata", "Op", 27, -1,
		Array( "Adagio sostenuto", "Allegretto", "Presto agitato"))
	
	println(s"${ninth.composition_title}")
	println(s"${moonlight.composition_title}")

	val fm = new FileManager("/home/liam/music/classify/ludwig-van-beethoven/ninth/berliner-philharmonik-orchestra/")
	println(s"FM root: ${fm.root}")
	println(s"FM composer: ${fm.composer_file}")
	println(s"FM composition: ${fm.composition_file}")
	println(s"FM recording: ${fm.recording_file}")
}

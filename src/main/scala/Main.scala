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

	val bpo = new Recording(ninth, Array("Kirill Petrenko"), Array("Berliner Philharmoniker"), 
		Array("Marlis Petersen", "Elisabeth Kulman", "Benjamin Bruns", "Kwangchul Youn"), 
		Array("Rundfunkchor Berlin"), Array(), 2019)
	
	println(s"${ninth.composition_title}")
	ninth.movement_titles.foreach(println(_))
	println(s"${moonlight.composition_title}")
	moonlight.movement_titles.foreach(println(_))

	val fm = new FileManager("/home/liam/music/classify/ludwig-van-beethoven/ninth/berliner-philharmonik-orchestra/")
	println(s"FM root: ${fm.root}")
	println(s"FM composer: ${fm.composer_file}")
	println(s"FM composition: ${fm.composition_file}")
	println(s"FM recording: ${fm.recording_file}")

	println("Dumping composer...")
	fm.dumpObject(lvb, "/home/liam/music/classify/ludwig-van-beethoven/.classifycomposer")
	println("Loading composer...")
	val lvb2 = fm.loadObject("/home/liam/music/classify/ludwig-van-beethoven/.classifycomposer").asInstanceOf[Composer]
	println(s"Loaded: '${lvb2.name}'")

	println("Dumping beethoven 9...")
	println(ninth)
	fm.dumpObject(ninth, "/home/liam/music/classify/ludwig-van-beethoven/ninth/.classifycomposition")
	println("Loading beethoven 9...")
	val ninth2 = fm.loadObject("/home/liam/music/classify/ludwig-van-beethoven/ninth/.classifycomposition").asInstanceOf[Composition]
	println(s"Loaded title: '${ninth2.composition_title}'")
	println(s"Loaded composer: '${ninth2.composer}'")
	println(s"Loaded movement names: '${ninth2.movement_names.mkString(", ")}'")
	println(s"Loaded movement titles: '${ninth2.movement_titles.mkString(", ")}'")

	println("Dumping recording...")
	fm.dumpObject(bpo, "/home/liam/music/classify/ludwig-van-beethoven/ninth/berliner-philharmonik-orchestra/.classifyrecording")
	println("Loading recording...")
	val bpo2 = fm.loadObject( "/home/liam/music/classify/ludwig-van-beethoven/ninth/berliner-philharmonik-orchestra/.classifyrecording").asInstanceOf[Recording]
	println(s"Loaded: '$bpo2'")
}

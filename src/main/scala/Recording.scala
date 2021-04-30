package classify

class Recording(
	val composition: Composition,
	val conductors: Array[String],
	val orchestras: Array[String],
	val soloists: Array[String],
	val choirs: Array[String],
	val other_performers: Array[String],
	val year: Int,
	var files: Array[String]
) extends Serializable {
	override def toString = { 
		s"""composition: $composition\nconductors:
		$conductors\norchestras: $orchestras \nsoloists: $soloists\nchoirs:
		$choirs\nother_performers: $other_performers\nyear: $year\nfiles: $files""" 
	} 

	def performersString: String = {
		if (year != -1)
			Array(soloists, orchestras, conductors, choirs, other_performers).flatten.mkString(", ") + s" ($year)"
		else
			Array(soloists, orchestras, conductors, choirs, other_performers).flatten.mkString(", ")
	}
}

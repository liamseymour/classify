package classify

class Recording(
	var composition: Composition,
	var conductors: Array[String],
	var orchestras: Array[String],
	var soloists: Array[String],
	var choirs: Array[String],
	var otherPerformers: Array[String],
	var year: Int,
) extends Serializable {
	override def toString = { 
		s"""composition: $composition\nconductors:
		$conductors\norchestras: $orchestras \nsoloists: $soloists\nchoirs:
		$choirs\notherPerformers: $otherPerformers\nyear: $year""" 
	} 
}

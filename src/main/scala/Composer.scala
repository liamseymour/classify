package classify

class Composer(var name: String) extends Serializable {
	override def toString = {
		s"name: $name"
	}
}

package classify

import java.util.TreeMap

object Util {
	val roman_map = new TreeMap[Int, String]()
	roman_map.put(1000, "M");
	roman_map.put(900, "CM");
	roman_map.put(500, "D");
	roman_map.put(400, "CD");
	roman_map.put(100, "C");
	roman_map.put(90, "XC");
	roman_map.put(50, "L");
	roman_map.put(40, "XL");
	roman_map.put(10, "X");
	roman_map.put(9, "IX");
	roman_map.put(5, "V");
	roman_map.put(4, "IV");
	roman_map.put(1, "I");

	def arabicToRoman(n: Int): String = {
		/* Adopted from: "https://stackoverflow.com/questions/12967896/converting-integers-to-roman-numerals-java/12968022" */
		val l =  roman_map.floorKey(n);
		if ( n == l ) {
			return roman_map.get(n);
		}
		return roman_map.get(l) + arabicToRoman(n-l);
	}

}

package org.geogebra.common.cas;

/**
 * Helper class for CAS output processing
 */
public interface CasParserTools {

	/**
	 * Converts scientific number notations in input into GeoGebra notation. For
	 * example, 3.4e-5 is changed into 3.4E-5 for expChar 'e' (MathPiper) and
	 * 3.4b-5 is changed into 3.4E-5 for expChar 'b' (Maxima).
	 * 
	 * @param input
	 *            expression
	 * @return converted expression with exponent character 'E'
	 */
	String convertScientificFloatNotation(String input);

}

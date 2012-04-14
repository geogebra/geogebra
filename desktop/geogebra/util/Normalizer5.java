package geogebra.util;

/**
 * Normalizer to get string to lower case and without accents (if Java >= 1.6)
 * 
 * @author matthieu
 *
 */
public class Normalizer5 extends Normalizer {


	@Override
	public String transform(String s) {
		return s.toLowerCase();	 		
	}

}

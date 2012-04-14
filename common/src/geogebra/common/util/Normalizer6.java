package geogebra.common.util;

/**
 * Normalizer to get string to lower case and without accents (if Java >= 1.6)
 * 
 * @author matthieu
 *
 */
public class Normalizer6 extends Normalizer {


	@Override
	public String transform(String s) {
		String ret = s.toLowerCase();	 
		//remove accents
		return java.text.Normalizer.normalize(ret, java.text.Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "");
		
	}

}

package geogebra.common.util;

/**
 * Normalizer to get string to lower case and without accents (if Java >= 1.6)
 * 
 * @author matthieu
 *
 */
public class NormalizerMinimal {


	/**
	 * 
	 * @return an instance (java 5 or 6 compatible) 
	 */
	public static NormalizerMinimal getInstance() {
		return INSTANCE;
	}
	

	/**
	 * transform the string to lower case and without accents (if Java >= 1.6)
	 * @param s the string
	 * @return the string to lower case and without accents (if Java >= 1.6)
	 */
	public String transform(final String s){
		return s.toLowerCase();
	}

	private static final NormalizerMinimal INSTANCE = new NormalizerMinimal();
				
		
}

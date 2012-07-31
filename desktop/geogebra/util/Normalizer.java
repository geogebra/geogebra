package geogebra.util;

import geogebra.common.util.NormalizerMinimal;

/**
 * Normalizer to get string to lower case (and without accents if Java >= 1.6)
 * 
 * @author matthieu
 *
 */
public class Normalizer extends NormalizerMinimal {


	/**
	 * 
	 * @return an instance (java 5 or 6 compatible) 
	 */
	public static NormalizerMinimal getInstance() {
		return INSTANCE;
	}
	

	private static final NormalizerMinimal INSTANCE;
	
	static {
		try {
			INSTANCE = getNormalizerClass();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static NormalizerMinimal getNormalizerClass() {
		try {
			Class.forName("java.text.Normalizer");
			return new Normalizer6();
		} catch (final Exception e) {
			return new NormalizerMinimal();
		}
	}

}

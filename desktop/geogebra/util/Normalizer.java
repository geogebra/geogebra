package geogebra.util;

import geogebra.common.main.AbstractApplication;
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
	public static Normalizer getInstance() {
		return INSTANCE;
	}
	

	private static final Normalizer INSTANCE;
	
	static {
		try {
			INSTANCE = getNormalizerClass();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Normalizer getNormalizerClass() {
		try {
			Class.forName("java.text.Normalizer");
			return new Normalizer6();
		} catch (final Exception e) {
			AbstractApplication.debug("Java6 Normalizer not supported");
		}
		return new Normalizer();
	}
}

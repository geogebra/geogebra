package geogebra.util;

import geogebra.common.util.NormalizerMinimal;

/**
 * Normalizer to get string to lower case (and without accents if Java >= 1.6)
 * 
 * @author matthieu
 *
 */
public abstract class Normalizer extends NormalizerMinimal {


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
			INSTANCE = (Normalizer) Class.forName(getNormalizerClass())
					.getConstructor().newInstance();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String getNormalizerClass() {
		try {
			Class.forName("java.text.Normalizer");
			return "geogebra.util.Normalizer6";
		} catch (final Exception e) {
			return "geogebra.util.Normalizer";
		}
	}
}

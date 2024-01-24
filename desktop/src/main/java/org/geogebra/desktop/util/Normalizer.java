package org.geogebra.desktop.util;

import org.geogebra.common.util.NormalizerMinimal;

/**
 * Normalizer to get string to lower case (and without accents if Java &ge; 1.6)
 * 
 * @author Mathieu
 *
 */
public class Normalizer extends NormalizerMinimal {

	private static final NormalizerMinimal INSTANCE;

	/**
	 * 
	 * @return an instance (java 5 or 6 compatible)
	 */
	public static NormalizerMinimal getInstance() {
		return INSTANCE;
	}

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

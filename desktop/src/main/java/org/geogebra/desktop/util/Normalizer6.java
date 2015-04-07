package org.geogebra.desktop.util;

import java.lang.reflect.Method;

/**
 * Normalizer to get string to lower case (and without accents if Java >= 1.6)
 * 
 * @author matthieu
 *
 */
public class Normalizer6 extends Normalizer {

	private static final Method normalize;
	private static final Object NFD;

	static {
		try {
			Class<?> normalizerClass = Class.forName("java.text.Normalizer");
			Class<?>[] classes = normalizerClass.getClasses();
			Class<?> formClass = classes[0];
			NFD = formClass.getField("NFD").get(null);
			normalize = normalizerClass.getMethod("normalize",
					CharSequence.class, formClass);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String transform(String s) {
		String ret = org.geogebra.common.util.StringUtil.toLowerCase(s);
		try {
			return ((String) normalize.invoke(null, ret, NFD)).replaceAll(
					"[\u0300-\u036F]", "");
		} catch (Exception e) {
			// should work, if not
			return ret;
		}

	}

}

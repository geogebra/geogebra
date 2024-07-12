package org.geogebra.desktop.util;

import org.geogebra.common.util.NormalizerMinimal;
import org.geogebra.common.util.StringUtil;

/**
 * Normalizer to get string to lower case (and without accents if Java &ge; 1.6)
 * 
 * @author Mathieu
 *
 */
public class Normalizer extends NormalizerMinimal {

	private static final NormalizerMinimal INSTANCE = new Normalizer();

	/**
	 * 
	 * @return an instance (java 5 or 6 compatible)
	 */
	public static NormalizerMinimal getInstance() {
		return INSTANCE;
	}

	@Override
	public String transform(String s) {
		String ret = StringUtil.toLowerCaseUS(s);
		return java.text.Normalizer.normalize(ret, java.text.Normalizer.Form.NFD)
				.replaceAll("[\u0300-\u036F]", "");
	}

}

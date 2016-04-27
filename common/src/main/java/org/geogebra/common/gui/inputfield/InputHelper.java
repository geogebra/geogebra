package org.geogebra.common.gui.inputfield;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.util.Korean;

public class InputHelper {
	public static boolean needsAutocomplete(StringBuilder curWord,
			Kernel kernel) {
		if ("ko".equals(kernel.getLocalization().getLanguage())) {
			if (Korean.flattenKorean(curWord.toString()).length() < 2) {
				return false;
			}
		} else if (curWord.length() < 3) {
			return false;
		}
		return kernel.lookupLabel(curWord.toString()) == null;
	}
}

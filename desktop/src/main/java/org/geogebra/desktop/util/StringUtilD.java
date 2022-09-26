package org.geogebra.desktop.util;

import org.geogebra.common.util.StringUtil;

public class StringUtilD extends StringUtil {
	@Override
	protected boolean isRightToLeftChar(char c) {
		// CharTableImpl c;
		return Character.getDirectionality(
				c) == Character.DIRECTIONALITY_RIGHT_TO_LEFT;
	}
}

package geogebra.web.util.keyboard;

import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.common.util.Unicode;

/**
 * Keyboard modes
 * 
 * @author bencze
 */
public enum KeyboardMode {
	/**
	 * Text input mode.
	 */
	TEXT("ABC"),
	/**
	 * Number input mode.
	 */
	NUMBER("123"),
	/**
	 * greek letters.
	 */
	GREEK(Unicode.alphaBetaGamma),
	/**
	 * special characters.
	 */
	SPECIAL_CHARS(ExpressionNodeConstants.strPERPENDICULAR
	        + ExpressionNodeConstants.strAND + "%");

	private String internalName;

	KeyboardMode(String internalName) {
		this.internalName = internalName;
	}

	/**
	 * @return the internal name of this mode
	 */
	public String getInternalName() {
		return internalName;
	}
}

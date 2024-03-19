package org.geogebra.keyboard.web;

import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;

/**
 * Keyboard modes
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
	 * special characters.
	 */
	SPECIAL_CHARS(ExpressionNodeConstants.strPERPENDICULAR
			+ ExpressionNodeConstants.strAND + "%");
	private final String internalName;

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

package org.geogebra.common.util;

public enum ExtendedBoolean {
	TRUE, FALSE, UNKNOWN;

	/**
	 * @return boolean value, default false
	 */
	final public boolean boolVal() {
		switch (this) {
		case TRUE:
			return true;
		default:
			return false;
		}

	}

	/**
	 * @param b
	 *            boolean
	 * @return TRUE or FALSE
	 */
	final public static ExtendedBoolean newExtendedBoolean(boolean b) {
		return b ? TRUE : FALSE;
	}
}
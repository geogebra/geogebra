package org.geogebra.common.util;

public enum ExtendedBoolean {
	TRUE, FALSE, UNKNOWN;

	final public boolean boolVal() {
		switch (this) {
		case TRUE:
			return true;
		default:
			return false;
		}

	}

	final public static ExtendedBoolean newExtendedBoolean(boolean b) {
		return b ? TRUE : FALSE;
	}
}
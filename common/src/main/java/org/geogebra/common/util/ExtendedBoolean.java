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

	/**
	 * @return nagated value
	 */
	public ExtendedBoolean negate() {
		switch (this) {
			case UNKNOWN:
			default: return UNKNOWN;
			case TRUE: return FALSE;
			case FALSE: return TRUE;
		}
	}

	/**
	 * @return nagated value
	 */
	public ExtendedBoolean and(ExtendedBoolean other) {
		switch (this) {
		case UNKNOWN:
		default: return other == FALSE ? FALSE : UNKNOWN;
		case TRUE: return other;
		case FALSE: return FALSE;
		}
	}

	public ExtendedBoolean or(ExtendedBoolean other) {
		return negate().and(other.negate()).negate();
	}
}
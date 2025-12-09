/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.util;

/**
 * Boolean with added unknown value.
 */
public enum ExtendedBoolean {
	TRUE, FALSE, UNKNOWN;

	/**
	 * @return boolean value, default false
	 */
	final public boolean boolVal() {
		return this == TRUE;
	}

	/**
	 * @param b
	 *            boolean
	 * @return TRUE or FALSE
	 */
	public static ExtendedBoolean newExtendedBoolean(boolean b) {
		return b ? TRUE : FALSE;
	}

	/**
	 * @return negated value
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
	 * @return value of this &amp;&amp; other
	 */
	public ExtendedBoolean and(ExtendedBoolean other) {
		switch (this) {
		case UNKNOWN:
		default: return other == FALSE ? FALSE : UNKNOWN;
		case TRUE: return other;
		case FALSE: return FALSE;
		}
	}

	/**
	 * @param other extended boolean
	 * @return TRUE if at least one is true, FALSE if both false, undefined otherwise
	 */
	public ExtendedBoolean or(ExtendedBoolean other) {
		return negate().and(other.negate()).negate();
	}

	public boolean isDefined() {
		return this != UNKNOWN;
	}
}
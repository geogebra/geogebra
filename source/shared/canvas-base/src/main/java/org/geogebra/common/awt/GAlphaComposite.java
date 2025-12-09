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

package org.geogebra.common.awt;

/**
 * Alpha composite.
 */
public interface GAlphaComposite extends GComposite {

	public static final int CLEAR = 1;
	public static final int DST = 9;
	public static final int DST_ATOP = 11;
	public static final int DST_IN = 6;
	public static final int DST_OUT = 8;
	public static final int DST_OVER = 4;
	public static final int SRC = 2;
	public static final int SRC_ATOP = 10;
	public static final int SRC_IN = 5;
	public static final int SRC_OUT = 7;
	public static final int SRC_OVER = 3;
	public static final int XOR = 12;

}

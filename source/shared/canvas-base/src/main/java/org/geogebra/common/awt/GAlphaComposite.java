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

	int CLEAR = 1;
	int DST = 9;
	int DST_ATOP = 11;
	int DST_IN = 6;
	int DST_OUT = 8;
	int DST_OVER = 4;
	int SRC = 2;
	int SRC_ATOP = 10;
	int SRC_IN = 5;
	int SRC_OUT = 7;
	int SRC_OVER = 3;
	int XOR = 12;

}

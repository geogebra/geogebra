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

package org.geogebra.common.kernel.kernelND;

/**
 * Constants for GeoConic matrix lookup
 */
public class ConicMatrix {
	/** index of coefficient for x^2 in the matrix */
	public static final int XX = 0;
	/** index of coefficient for y^2 in the matrix */
	public static final int YY = 1;
	/** index of coefficient for constant in the matrix */
	public static final int CONST = 2;
	/** index of coefficient for xy in the matrix */
	public static final int XY = 3;
	/** index of coefficient for x in the matrix */
	public static final int X = 4;
	/** index of coefficient for y in the matrix */
	public static final int Y = 5;

}

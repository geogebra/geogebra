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

package org.geogebra.common.util.clipper;

public class DoubleRect {
	public double left;
	public double top;
	public double right;
	public double bottom;

	public DoubleRect() {

	}

	public DoubleRect(double l, double t, double r, double b) {
		left = l;
		top = t;
		right = r;
		bottom = b;
	}

	public DoubleRect(DoubleRect ir) {
		left = ir.left;
		top = ir.top;
		right = ir.right;
		bottom = ir.bottom;
	}
}

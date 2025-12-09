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

package org.geogebra.common.euclidian.plot.implicit;

import java.util.Arrays;

import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomial2D;
import org.geogebra.common.kernel.implicit.MarchingRect;

public class BernsteinMarchingRect implements MarchingRect {
	private final BernsteinBoundingBox box;
	private final double[] corners = new double[4];

	/**
	 *
	 * @param cell {@link BernsteinPlotCell}
	 */
	public BernsteinMarchingRect(BernsteinPlotCell cell) {
		this.box = cell.boundingBox;
		BernsteinPolynomial2D poly = cell.polynomial;
		corners[0] = poly.evaluate(0, 0);
		corners[1] = poly.evaluate(1, 0);
		corners[2] = poly.evaluate(1, 1);
		corners[3] = poly.evaluate(0, 1);
	}

	@Override
	public double x1() {
		return box.x1();
	}

	@Override
	public double y1() {
		return box.y1();
	}

	@Override
	public double x2() {
		return box.x2();
	}

	@Override
	public double y2() {
		return box.y2();
	}

	@Override
	public double topLeft() {
		return corners[0];
	}

	@Override
	public double topRight() {
		return corners[1];
	}

	@Override
	public double bottomLeft() {
		return corners[3];
	}

	@Override
	public double bottomRight() {
		return corners[2];
	}

	@Override
	public double cornerAt(int i) {
		return corners[i];
	}

	@Override
	public String toString() {
		return "BernsteinPlotRect{"
				+ "box="
				+ box
				+ ", corners="
				+ Arrays.toString(corners)
				+ '}';
	}
}

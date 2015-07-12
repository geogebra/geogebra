/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Random point
 * 
 * @author Rrubaa
 */

public class AlgoRandomPoint extends AlgoElement {

	private NumberValue a, b, c, d; // input
	private GeoPoint M; // output

	public AlgoRandomPoint(Construction cons, String label, NumberValue a,
			NumberValue b, NumberValue c, NumberValue d) {
		super(cons);
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		M = new GeoPoint(cons);

		setInputOutput();
		compute();

		M.setLabel(label);
	}

	public Commands getClassName() {
		return Commands.RandomPointIn;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[4];
		input[0] = a.toGeoElement();
		input[1] = b.toGeoElement();
		input[2] = c.toGeoElement();
		input[3] = d.toGeoElement();

		super.setOnlyOutput(M);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return random point M
	 */
	public GeoPoint getPoint() {
		return M;
	}

	@Override
	public final void compute() {

		double aNum = a.getDouble();
		double bNum = b.getDouble();
		double cNum = c.getDouble();
		double dNum = d.getDouble();

		if (input[0].isDefined() && input[1].isDefined()
				&& input[2].isDefined() && input[3].isDefined()
				&& !Double.isInfinite(aNum) && !Double.isNaN(aNum)
				&& !Double.isInfinite(bNum) && !Double.isNaN(bNum)
				&& !Double.isInfinite(cNum) && !Double.isNaN(cNum)
				&& !Double.isInfinite(dNum) && !Double.isNaN(dNum)) {

			double p = aNum + (bNum - aNum)
					* cons.getApplication().getRandomNumber();
			double q = cNum + (dNum - cNum)
					* cons.getApplication().getRandomNumber();
			getPoint().setCoords(p, q, 1.0);
		} else {
			M.setUndefined();
		}
	}
	// TODO Consider locusequability
}


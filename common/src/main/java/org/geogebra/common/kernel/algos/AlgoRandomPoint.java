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
import org.geogebra.common.kernel.SetRandomValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Random point
 * 
 * @author Rrubaa
 */

public class AlgoRandomPoint extends AlgoElement implements SetRandomValue  {
	// input
	private GeoNumberValue a;
	private GeoNumberValue b;
	private GeoNumberValue c;
	private GeoNumberValue d;
	private GeoPoint M; // output

	/**
	 * @param cons
	 *            construction
	 * @param a
	 *            x min
	 * @param b
	 *            x max
	 * @param c
	 *            y min
	 * @param d
	 *            y max
	 */
	public AlgoRandomPoint(Construction cons, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue c, GeoNumberValue d) {
		super(cons);
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		M = new GeoPoint(cons);

		setInputOutput();
		compute();
	}

	@Override
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

		if (input[0].isDefined() && input[1].isDefined() && input[2].isDefined()
				&& input[3].isDefined() && !Double.isInfinite(aNum)
				&& !Double.isNaN(aNum) && !Double.isInfinite(bNum)
				&& !Double.isNaN(bNum) && !Double.isInfinite(cNum)
				&& !Double.isNaN(cNum) && !Double.isInfinite(dNum)
				&& !Double.isNaN(dNum)) {

			double p = aNum
					+ (bNum - aNum) * cons.getApplication().getRandomNumber();
			double q = cNum
					+ (dNum - cNum) * cons.getApplication().getRandomNumber();
			getPoint().setCoords(p, q, 1.0);
		} else {
			M.setUndefined();
		}
	}

	@Override
	public boolean setRandomValue(GeoElementND val) {
		if (val instanceof GeoPointND) {
			GeoPointND pt = (GeoPointND) val;
			if (pt.getInhomX() < b.getDouble()
					&& pt.getInhomX() > a.getDouble()
					&& pt.getInhomY() < d.getDouble()
					&& pt.getInhomY() > c.getDouble()) {
				M.setCoords(pt.getCoords(), false);
				return true;
			}
		}
		return false;
	}
}

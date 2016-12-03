/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * FitLineY of a list. adapted from AlgoListMax
 * 
 * @author Michael Borcherds
 * @version 14-01-2008
 */

public class AlgoFitLineX extends AlgoElement {

	private GeoList geoList; // input
	private GeoLine g; // output

	public AlgoFitLineX(Construction cons, String label, GeoList geoList) {
		this(cons, geoList);
		g.setLabel(label);
	}

	public AlgoFitLineX(Construction cons, GeoList geoList) {
		super(cons);
		this.geoList = geoList;

		g = new GeoLine(cons);
		// ignore default (implicit)
		// for FitXXX we always want "y=..."
		g.setToExplicit();

		setInputOutput(); // for AlgoElement

		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.FitLineX;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = geoList;

		setOnlyOutput(g);
		setDependencies(); // done by AlgoElement
	}

	public GeoLine getFitLineX() {
		return g;
	}

	@Override
	public final void compute() {
		int size = geoList.size();
		if (!geoList.isDefined() || size <= 1) {
			g.setUndefined();
			return;
		}

		double sigmax = 0;
		double sigmay = 0;
		// double sigmaxx=0; not needed
		double sigmayy = 0;
		double sigmaxy = 0;

		for (int i = 0; i < size; i++) {
			GeoElement geo = geoList.get(i);
			if (geo instanceof GeoPoint) {
				double xy[] = new double[2];
				((GeoPoint) geo).getInhomCoords(xy);
				double x = xy[0];
				double y = xy[1];
				sigmax += x;
				sigmay += y;
				// sigmaxx+=x*x; not needed
				sigmaxy += x * y;
				sigmayy += y * y;
			} else {
				g.setUndefined();
				return;
			}
		}
		// x on y regression line
		// (x - sigmax / n) = (Syy / Sxy)*(y - sigmay / n)
		// rearranged to eliminate all divisions
		// g.y = size * sigmax * sigmay - size * size * sigmaxy;
		// g.x = size * size * sigmayy - size * sigmay * sigmay;
		// g.z = size * sigmay * sigmaxy - size * sigmayy * sigmax;

		// (g.x)x + (g.y)y + g.z = 0

		// more accurate, see #5230
		double Sxy = sigmaxy - sigmax * sigmay / size;
		double Syy = sigmayy - sigmay * sigmay / size;
		double mux = sigmax / size;
		double muy = sigmay / size;

		g.x = -Syy;
		g.y = Sxy;
		g.z = -Sxy * muy + Syy * mux;

		// #5294
		if (Kernel.isZero(g.x) || Kernel.isZero(g.y) || Kernel.isZero(g.z)) {
			return;
		}

		// normalize coefficients (copied from
		// GeoLine.getnormalizedCoefficients())
		// #5230
		while (Math.abs(g.x) < 0.5 && Math.abs(g.y) < 0.5
				&& Math.abs(g.z) < 0.5) {
			g.x *= 2;
			g.y *= 2;
			g.z *= 2;
		}

		while (Math.abs(g.x) > 1 && Math.abs(g.y) > 1 && Math.abs(g.z) > 1) {
			g.x /= 2;
			g.y /= 2;
			g.z /= 2;
		}
	}

	

}

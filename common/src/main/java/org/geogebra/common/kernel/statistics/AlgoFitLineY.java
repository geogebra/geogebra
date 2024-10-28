/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * FitLineY of a list. adapted from AlgoListMax
 * 
 * @author Michael Borcherds
 * @version 14-01-2008
 */

public class AlgoFitLineY extends AlgoElement {

	private GeoList geoList; // input
	private GeoLine g; // output

	/**
	 * @param cons
	 *            construction
	 * @param geoList
	 *            list of points
	 */
	public AlgoFitLineY(Construction cons, GeoList geoList) {
		super(cons);
		this.geoList = geoList;

		g = new GeoLine(cons);

		setInputOutput(); // for AlgoElement

		compute();

		// note: GeoLine's equation form is initialized from construction defaults
		EquationBehaviour equationBehaviour = kernel.getEquationBehaviour();
		if (equationBehaviour != null) {
			g.setEquationForm(equationBehaviour.getFitLineCommandEquationForm());
		}
	}

	@Override
	public Commands getClassName() {
		return Commands.FitLineY;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_FITLINE;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = geoList;

		setOnlyOutput(g);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return best fit line
	 */
	public GeoLine getFitLineY() {
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
		double sigmaxx = 0;
		// double sigmayy=0; not needed
		double sigmaxy = 0;

		for (int i = 0; i < size; i++) {
			GeoElement geo = geoList.get(i);
			if (geo.isGeoPoint()) {
				double x;
				double y;
				if (geo.isGeoElement3D()) {
					Coords coords = ((GeoPointND) geo).getInhomCoordsInD3();
					if (!DoubleUtil.isZero(coords.getZ())) {
						g.setUndefined();
						return;
					}
					x = coords.getX();
					y = coords.getY();
				} else {
					double[] xy = new double[2];
					((GeoPoint) geo).getInhomCoords(xy);
					x = xy[0];
					y = xy[1];
				}

				sigmax += x;
				sigmay += y;
				sigmaxx += x * x;
				sigmaxy += x * y;
				// sigmayy+=y*y; not needed
			} else {
				g.setUndefined();
				return;
			}
		}
		// y on x regression line
		// (y - sigmay / n) = (Sxy / Sxx)*(x - sigmax / n)
		// rearranged to eliminate all divisions
		// g.x = (sigmax * sigmay - size * sigmaxy);
		// g.y = (size * sigmaxx - sigmax * sigmax);
		// g.z = (sigmax * sigmaxy - sigmaxx * sigmay);
		// (g.x)x + (g.y)y + g.z = 0

		// more accurate, see #5230
		double Sxy = sigmaxy - sigmax * sigmay / size;
		double Sxx = sigmaxx - sigmax * sigmax / size;
		double mux = sigmax / size;
		double muy = sigmay / size;

		g.x = Sxy;
		g.y = -Sxx;
		g.z = -Sxy * mux + Sxx * muy;

		// #5294
		if (DoubleUtil.isZero(g.x) || DoubleUtil.isZero(g.y) || DoubleUtil.isZero(g.z)) {
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

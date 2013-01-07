/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.statistics;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;

/**
 * FitLineY of a list. adapted from AlgoListMax
 * 
 * @author Michael Borcherds
 * @version 14-01-2008
 */

public class AlgoFitLineY extends AlgoElement {

	private GeoList geoList; // input
	private GeoLine g; // output

	public AlgoFitLineY(Construction cons, String label, GeoList geoList) {
		this(cons, geoList);
		g.setLabel(label);
	}

	public AlgoFitLineY(Construction cons, GeoList geoList) {
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
				double xy[] = new double[2];
				((GeoPoint) geo).getInhomCoords(xy);
				double x = xy[0];
				double y = xy[1];
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
		// (y - sigmay / n) = (Sxx / Sxy)*(x - sigmax / n)
		// rearranged to eliminate all divisions
		g.x = size * sigmax * sigmay - size * size * sigmaxy;
		g.y = size * size * sigmaxx - size * sigmax * sigmax;
		g.z = size * sigmax * sigmaxy - size * sigmaxx * sigmay; // (g.x)x +
																	// (g.y)y +
																	// g.z = 0
	}

	// TODO Consider locusequability

}

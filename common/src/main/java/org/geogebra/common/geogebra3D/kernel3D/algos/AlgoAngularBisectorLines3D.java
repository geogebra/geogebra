/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoAngularBisectorLines.java
 *
 * Created on 26. Oktober 2001
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoLineND;

/**
 * Angle bisectors between two lines (3D)
 * 
 * @author mathieu
 */
public class AlgoAngularBisectorLines3D extends AlgoElement {

	private GeoLineND g, h; // input
	private GeoLine3D[] bisector; // output

	private GeoVector[] wv; // direction of bisector line bisector
	private GeoPoint3D B; // intersection point of g, h

	public AlgoAngularBisectorLines3D(Construction cons, String[] labels,
			GeoLineND g, GeoLineND h) {
		this(cons, g, h);
		GeoElement.setLabels(labels, bisector);
	}

	@Override
	public Commands getClassName() {
		return Commands.AngularBisector;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_ANGULAR_BISECTOR;
	}

	AlgoAngularBisectorLines3D(Construction cons, GeoLineND g, GeoLineND h) {
		super(cons);
		this.g = g;
		this.h = h;
		bisector = new GeoLine3D[2];
		bisector[0] = new GeoLine3D(cons);
		bisector[1] = new GeoLine3D(cons);
		setInputOutput(); // for AlgoElement

		wv = new GeoVector[2];
		wv[0] = new GeoVector(cons);
		wv[0].setCoords(0, 0, 0);
		wv[1] = new GeoVector(cons);
		wv[1].setCoords(0, 0, 0);
		B = new GeoPoint3D(cons);

		bisector[0].setStartPoint(B);
		bisector[1].setStartPoint(B);

		// compute bisectors of lines g, h
		compute();
	}

	// for AlgoElement
	@Override
	public void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) g;
		input[1] = (GeoElement) h;

		super.setOutput(bisector);
		setDependencies(); // done by AlgoElement
	}

	public GeoLine3D[] getLines() {
		return bisector;
	}

	// Made public for LocusEqu
	public GeoLineND getg() {
		return g;
	}

	// Made public for LocusEqu
	public GeoLineND geth() {
		return h;
	}

	// Made public for LocusEqu
	public GeoPoint3D getB() {
		return B;
	}

	/*
	 * @Override public boolean isNearToAlgorithm() { return true; }
	 */

	@Override
	public final void compute() {

		// lines origins and directions
		Coords o1 = getg().getStartInhomCoords();
		Coords v1 = getg().getDirectionInD3();
		Coords o2 = geth().getStartInhomCoords();
		Coords v2 = geth().getDirectionInD3();

		// normal vector
		Coords vn = v1.crossProduct4(v2).normalized();

		if (!vn.isDefined()) { // g and h are parallel
			// first bisector is parallel to g and h
			bisector[0].setCoord(o1.add(o2).mul(0.5), v1);
			// second bisector is undefined
			bisector[1].setUndefined();
		} else { // standard case: g and h are not parallel
			// nearest points
			Coords[] points = CoordMatrixUtil.nearestPointsFromTwoLines(o1, v1,
					o2, v2);
			if (!points[0].equalsForKernel(points[1])) { // lines are not
															// coplanar
				bisector[0].setUndefined();
				bisector[1].setUndefined();
			} else {
				v1 = v1.copyVector();
				v1.normalize();
				v2 = v2.copyVector();
				v2.normalize();
				bisector[0].setCoord(points[0], v1.add(v2));
				bisector[1].setCoord(points[0], v1.sub(v2));
			}
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("AngleBisectorOfAB", g.getLabel(tpl),
				h.getLabel(tpl));
	}

	/*
	 * @Override public boolean isLocusEquable() { return true; }
	 * 
	 * public EquationElementInterface buildEquationElementForGeo(GeoElement
	 * geo, EquationScopeInterface scope) { return
	 * LocusEquation.eqnAngularBisectorLines(geo, this, scope); }
	 */
}

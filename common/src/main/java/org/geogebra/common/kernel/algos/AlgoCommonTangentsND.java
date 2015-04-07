/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoCommonTangents.java, dsun48 [6/26/2011]
 *
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.AlgoIntersectND;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Two tangents through point P to conic section c
 */
public abstract class AlgoCommonTangentsND extends AlgoElement implements
		TangentAlgo {

	protected GeoPointND[] P; // tmp
	protected GeoConicND[] c; // input
	protected GeoLineND[] tangents; // output

	protected GeoLine polar, polar2;
	protected AlgoIntersectND algoIntersect, algoIntersect2;
	protected GeoPointND[] tangentPoints, tangentPoints2;
	protected boolean equalLines = false, equalLines2 = false;

	public AlgoCommonTangentsND(Construction cons, String[] labels,
			GeoConicND c, GeoConicND c2) {
		this(cons, c, c2);
		GeoElement.setLabels(labels, getOutput());
	}

	AlgoCommonTangentsND(Construction cons, GeoConicND c, GeoConicND c2) {
		super(cons);

		this.c = new GeoConicND[2];
		this.c[0] = c;
		this.c[1] = c2;

		double r = c.getCircleRadius();
		double r2 = c2.getCircleRadius();

		createPoints(cons);

		// outer
		if (Math.abs(r2 - r) > Kernel.MIN_PRECISION) {
			setCoordsAsPoint(0, (c.b.getX() * r2 - c2.b.getX() * r) / (r2 - r),
					(c.b.getY() * r2 - c2.b.getY() * r) / (r2 - r));
		} else {
			setCoordsAsVector(0, (c.b.getX() * r2 - c2.b.getX() * r),
					(c.b.getY() * r2 - c2.b.getY() * r));
		}
		// the tangents are computed by intersecting the
		// polar line of P with c
		polar = new GeoLine(cons);
		algoIntersect = createAlgo(cons, P[0], polar, this.c[0]);
		// this is only an internal Algorithm that shouldn't be in the
		// construction list
		cons.removeFromConstructionList(algoIntersect);
		tangentPoints = algoIntersect.getIntersectionPoints();

		// inner
		setCoordsAsPoint(1, (c.b.getX() * r2 + c2.b.getX() * r) / (r2 + r),
				(c.b.getY() * r2 + c2.b.getY() * r) / (r2 + r));
		// the tangents are computed by intersecting the
		// polar line of P with c
		polar2 = new GeoLine(cons);
		algoIntersect2 = createAlgo(cons, P[1], polar2, this.c[1]);
		// this is only an internal Algorithm that shouldn't be in the
		// construction list
		cons.removeFromConstructionList(algoIntersect2);
		tangentPoints2 = algoIntersect2.getIntersectionPoints();

		initTangents();

		setInputOutput(); // for AlgoElement

		compute();

		// check if both lines are equal after creation:
		// if they are equal we started with a point on the conic section
		// in this case we only want to see one tangent line,
		// so we make the second one undefined
		equalLines = tangents[0].isEqual((GeoElement) tangents[1]);
		if (equalLines) {
			tangents[1].setUndefined();
			tangentPoints[1].setUndefined();
		}
		equalLines2 = tangents[0 + 2].isEqual((GeoElement) tangents[1 + 2]);
		if (equalLines2) {
			tangents[1 + 2].setUndefined();
			tangentPoints2[1].setUndefined();
		}
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @return new point
	 */
	abstract protected void createPoints(Construction cons);

	/**
	 * set coords to the point (i.e z=1)
	 * 
	 * @param index
	 *            point index
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param z
	 *            z coord
	 */
	abstract protected void setCoordsAsPoint(int index, double x, double y);

	/**
	 * set vector coords to the point (i.e z=0)
	 * 
	 * @param index
	 *            point index
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param z
	 *            z coord
	 */
	abstract protected void setCoordsAsVector(int index, double x, double y);

	/**
	 * set tangents
	 */
	abstract protected void initTangents();

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param p
	 *            point
	 * @param line
	 *            polar line
	 * @param conic
	 *            conic
	 * @return intersect algo
	 */
	abstract protected AlgoIntersectND createAlgo(Construction cons,
			GeoPointND p, GeoLine line, GeoConicND conic);

	@Override
	public Commands getClassName() {
		return Commands.Tangent;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_TANGENTS;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = c[0];
		input[1] = c[1];

		GeoElement[] out = new GeoElement[tangents.length];
		for (int i = 0; i < tangents.length; i++) {
			out[i] = (GeoElement) tangents[i];
		}
		super.setOutput(out);
		setDependencies(); // done by AlgoElement
	}

	public GeoLineND[] getTangents() {
		return tangents;
	}

	/**
	 * 
	 * @return true if tangents will be undefined
	 */
	protected boolean checkUndefined() {
		return !c[0].isCircle() || !c[1].isCircle();
	}

	/**
	 * @param csIndex
	 *            coord sys index
	 * @param mpIndex
	 *            midpoint index
	 * @return x coord for second midpoint
	 */
	abstract protected double getMidpointX(int csIndex, int mpIndex);

	/**
	 * @param csIndex
	 *            coord sys index
	 * @param mpIndex
	 *            midpoint index
	 * @return y coord for second midpoint
	 */
	abstract protected double getMidpointY(int csIndex, int mpIndex);

	// calc tangents
	@Override
	public final void compute() {

		if (checkUndefined()) {
			for (int i = 0; i < 4; i++) {
				tangents[i].setUndefined();
			}
			return;
		}

		double r = c[0].getCircleRadius();
		double r2 = c[1].getCircleRadius();

		// outer
		if (Math.abs(r2 - r) > Kernel.MIN_PRECISION) {
			setCoordsAsPoint(0, (getMidpointX(0, 0) * r2 - getMidpointX(0, 1)
					* r)
					/ (r2 - r), (getMidpointY(0, 0) * r2 - getMidpointY(0, 1)
					* r)
					/ (r2 - r));
		} else {
			setCoordsAsVector(0, (getMidpointX(0, 0) * r2 - getMidpointX(0, 1)
					* r), (getMidpointY(0, 0) * r2 - getMidpointY(0, 1) * r));
		}
		// inner
		setCoordsAsPoint(1, (getMidpointX(1, 0) * r2 + getMidpointX(1, 1) * r)
				/ (r2 + r), (getMidpointY(1, 0) * r2 + getMidpointY(1, 1) * r)
				/ (r2 + r));

		// update polar lines
		updatePolarLines();

		// if P lies on the conic, the polar is a tangent
		if (isIntersectionPointIncident(0, c[0])) {
			setTangentFromPolar(0, polar);
			tangentPoints[0].setCoordsFromPoint(P[0]);
			// check if we had equal lines at the beginning
			// if so we still don't want to see the second line
			if (equalLines) {
				tangents[1].setUndefined();
				tangentPoints[1].setUndefined();
			} else {
				setTangentFromPolar(1, polar);
				tangentPoints[1].setCoordsFromPoint(P[0]);
			}
		}
		// if P is not on the conic, the tangents pass through
		// the intersection points of polar and conic
		else {
			// intersect polar line with conic -> tangentPoints
			algoIntersect.update();
			// calc tangents through tangentPoints
			updateTangents(tangentPoints, 0);
			// we no longer have equal lines (if we ever had them)
			equalLines = false;
		}

		// if P lies on the conic, the polar is a tangent
		if (isIntersectionPointIncident(1, c[1])) {
			setTangentFromPolar(0 + 2, polar2);
			tangentPoints2[0].setCoordsFromPoint(P[1]);
			// check if we had equal lines at the beginning
			// if so we still don't want to see the second line
			if (equalLines2) {
				tangents[1 + 2].setUndefined();
				tangentPoints2[1].setUndefined();
			} else {
				setTangentFromPolar(1 + 2, polar2);
				tangentPoints2[1].setCoordsFromPoint(P[1]);
			}
		}
		// if P is not on the conic, the tangents pass through
		// the intersection points of polar and conic
		else {
			// intersect polar line with conic -> tangentPoints
			algoIntersect2.update();
			// calc tangents through tangentPoints

			updateTangents(tangentPoints2, 1);
			// we no longer have equal lines (if we ever had them)
			equalLines2 = false;
		}

	} // end of compute

	/**
	 * update polar lines (both)
	 */
	abstract protected void updatePolarLines();

	/**
	 * @param tangentPoints
	 *            tangent points
	 * @param index
	 *            first/second tangents
	 */
	abstract protected void updateTangents(GeoPointND[] tangentPoints, int index);

	/**
	 * set tangent equal to polar
	 * 
	 * @param i
	 *            tangent index
	 * @param line
	 *            polar line
	 */
	abstract protected void setTangentFromPolar(int i, GeoLine line);

	/**
	 * 
	 * @param index
	 *            point index
	 * @param conic
	 *            conic
	 * @return true if point is on conic
	 */
	abstract protected boolean isIntersectionPointIncident(int index,
			GeoConicND conic);

	public GeoPointND getTangentPoint(GeoElement conic, GeoLine line) {
		if (conic != c[0] && conic != c[1])
			return null;
		if (conic == c[0]) {
			if (line == tangents[0]) {
				return tangentPoints[0];
			} else if (line == tangents[1]) {
				return tangentPoints[1];
			} else {
				return null;
			}
		} else if (conic == c[1]) {
			if (line == tangents[2]) {
				return tangentPoints2[0];
			} else if (line == tangents[3]) {
				return tangentPoints2[1];
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public final String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("CommonTangentOfCirclesAandB",
				c[0].getLabel(tpl), c[1].getLabel(tpl));
	}

	// TODO Consider locusequability
}

// Local Variables:
// indent-tabs-mode: nil
// c-basic-offset: 4
// tab-width: 4
// End:
// vim: set expandtab shiftwidth=4 softtabstop=4 tabstop=4

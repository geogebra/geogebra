/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.LocusEquation;
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
public abstract class AlgoTangentPointND extends AlgoElement implements
		TangentAlgo {

	protected GeoPointND P; // input
	protected GeoConicND c; // input
	protected GeoLineND[] tangents; // output

	protected GeoLine polar;
	protected AlgoIntersectND algoIntersect;
	protected GeoPointND[] tangentPoints;
	protected boolean equalLines = false;

	/*
	 * AlgoTangentPoint(Construction cons, String label, GeoPoint P, GeoConic c)
	 * { this(cons, P, c); GeoElement.setLabels(label, tangents); }
	 */

	public AlgoTangentPointND(Construction cons, String[] labels, GeoPointND P,
			GeoConicND c) {
		this(cons, P, c);
		GeoElement.setLabels(labels, getOutput());
	}

	AlgoTangentPointND(Construction cons, GeoPointND P, GeoConicND c) {
		super(cons);
		this.P = P;
		this.c = c;

		setPolar();

		setTangents();

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
	}

	/**
	 * set polar
	 */
	abstract protected void setPolar();

	/**
	 * set tangents
	 */
	abstract protected void setTangents();

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
		input[0] = (GeoElement) P;
		input[1] = c;

		GeoElement[] out = new GeoElement[2];
		for (int i = 0; i < 2; i++) {
			out[i] = (GeoElement) tangents[i];
		}
		super.setOutput(out);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * 
	 * @return true if tangents will be defined
	 */
	protected boolean checkUndefined() {
		return c.isDegenerate();
	}

	// calc tangents
	@Override
	public final void compute() {
		// degenerates should not have any tangents
		if (checkUndefined()) {
			tangents[0].setUndefined();
			tangents[1].setUndefined();
			return;
		}

		// update polar line
		updatePolarLine();

		// if P lies on the conic, the polar is a tangent
		if (isIntersectionPointIncident()) {
			setTangentFromPolar(0);
			tangentPoints[0].setCoordsFromPoint(P);

			// check if we had equal lines at the beginning
			// if so we still don't want to see the second line
			if (equalLines) {
				tangents[1].setUndefined();
				tangentPoints[1].setUndefined();
			} else {
				setTangentFromPolar(1);
				tangentPoints[1].setCoordsFromPoint(P);
			}
		}
		// if P is not on the conic, the tangents pass through
		// the intersection points of polar and conic
		else {
			// intersect polar line with conic -> tangentPoints
			algoIntersect.update();

			// calc tangents through tangentPoints
			updateTangents();

			// we no longer have equal lines (if we ever had them)
			equalLines = false;
		}
	}

	/**
	 * set tangent equal to polar
	 */
	abstract protected void setTangentFromPolar(int i);

	/**
	 * 
	 * @return true if P is on c
	 */
	abstract protected boolean isIntersectionPointIncident();

	/**
	 * update polar line
	 */
	abstract protected void updatePolarLine();

	/**
	 * update tangents
	 */
	abstract protected void updateTangents();

	@Override
	public final String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("TangentToAThroughB", c.getLabel(tpl),
				P.getLabel(tpl));

	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}

	public EquationElementInterface buildEquationElementForGeo(GeoElement geo,
			EquationScopeInterface scope) {
		return LocusEquation.eqnTangentPoint(geo, this, scope);
	}

	@Override
	public GeoPointND getTangentPoint(GeoElement geo, GeoLine line) {
		if (geo != c)
			return null;

		if (line == tangents[0])
			return tangentPoints[0];
		else if (line == tangents[1])
			return tangentPoints[1];
		else
			return null;
	}
}

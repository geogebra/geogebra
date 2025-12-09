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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 *
 * @author Markus
 */
public class AlgoOrthoLinePointVector extends AlgoElement {

	private GeoPoint P; // input
	private GeoVector v; // input
	private GeoLine g; // output

	/**
	 * Creates new algo for PerpendicularLine[point, vector]
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param P
	 *            point
	 * @param v
	 *            perpendicular vector
	 */
	public AlgoOrthoLinePointVector(Construction cons, String label, GeoPoint P,
			GeoVector v) {
		super(cons);
		this.P = P;
		this.v = v;
		g = new GeoLine(cons);
		g.setStartPoint(P);
		setInputOutput(); // for AlgoElement

		// compute line through P, Q
		compute();

		g.setLabel(label);
		addIncidence();
	}

	/**
	 * @author Tam
	 * 
	 *         for special cases of e.g. AlgoIntersectLineConic
	 */
	private void addIncidence() {
		P.addIncidence(g, true);
	}

	@Override
	public Commands getClassName() {
		return Commands.OrthogonalLine;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_ORTHOGONAL;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = P;
		input[1] = v;

		setOnlyOutput(g);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return result
	 */
	public GeoLine getLine() {
		return g;
	}

	// line through P normal to v
	@Override
	public final void compute() {
		GeoVec3D.cross(P, -v.y, v.x, 0.0, g);
	}

	// ///////////////////////////////
	// TRICKS FOR XOY PLANE
	// ///////////////////////////////

	@Override
	protected int getInputLengthForXML() {
		return getInputLengthForXMLMayNeedXOYPlane();
	}

	@Override
	protected int getInputLengthForCommandDescription() {
		return getInputLengthForCommandDescriptionMayNeedXOYPlane();
	}

	@Override
	public GeoElementND getInput(int i) {
		return getInputMaybeXOYPlane(i);
	}

	@Override
	public String toString(StringTemplate tpl) {
		return getLoc().getPlainDefault("LineThroughAPerpendicularToB",
				"Line through %0 perpendicular to %1",
				P.getLabel(tpl), v.getLabel(tpl));
	}

}

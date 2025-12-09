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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;

/**
 *
 * @author Markus
 */
public class AlgoLinePointVector extends AlgoElement {

	private GeoPoint P; // input
	private GeoVector v; // input
	private GeoLine g; // output

	/**
	 * Creates new algo for Line[point, vector]
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param P
	 *            point
	 * @param v
	 *            direction vector
	 */
	public AlgoLinePointVector(Construction cons, String label, GeoPoint P,
			GeoVector v) {
		super(cons);
		this.P = P;
		this.v = v;
		g = new GeoLine(cons);
		g.setStartPoint(P);
		setInputOutput(); // for AlgoElement

		// compute line through P, Q
		compute();

		addIncidence();

		// note: GeoLine's equation form is initialized from construction defaults
		EquationBehaviour equationBehaviour = kernel.getEquationBehaviour();
		if (equationBehaviour != null) {
			g.setEquationForm(equationBehaviour.getLineCommandEquationForm());
		}

		g.setLabel(label);
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
		return Commands.Line;
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

	public GeoLine getLine() {
		return g;
	}

	GeoPoint getP() {
		return P;
	}

	GeoVector getv() {
		return v;
	}

	// calc the line g through P and Q
	@Override
	public final void compute() {
		// g = cross(P, v)
		GeoVec3D.lineThroughPointVector(P, v, g);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("LineThroughAwithDirectionB",
				"Line through %0 with direction %1", P.getLabel(tpl),
				v.getLabel(tpl));
	}

}

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

package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Computes implicit polynomial through given points
 *
 */
public class AlgoImplicitPolyThroughPoints extends AlgoElement {
	private GeoList P; // input points
	private GeoImplicit implicitPoly; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param p
	 *            points on polynomial
	 */
	public AlgoImplicitPolyThroughPoints(Construction cons, String label,
			GeoList p) {
		super(cons);
		this.P = p;

		implicitPoly = kernel.newImplicitPoly(cons);

		setInputOutput();
		compute();

		implicitPoly.setLabel(label);
	}

	/**
	 * @return resulting polynomial
	 */
	public GeoImplicit getImplicitPoly() {
		return implicitPoly;
	}

	/**
	 * @return input list of points
	 */
	public GeoList getP() {
		return P;
	}

	@Override
	protected void setInputOutput() {
		input = P.asArray();
		setOnlyOutput(implicitPoly);
		setDependencies();
	}

	@Override
	public void compute() {
		implicitPoly.throughPoints(P);
	}

	@Override
	public Commands getClassName() {
		return Commands.ImplicitCurve;
	}

}

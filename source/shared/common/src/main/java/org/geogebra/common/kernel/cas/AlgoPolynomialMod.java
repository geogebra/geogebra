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

package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;

/**
 * Polynomial remainder
 * 
 * @author Michael Borcherds
 */
public class AlgoPolynomialMod extends AlgoElement {

	private GeoFunction f1; // input
	private GeoFunction f2; // input
	private GeoFunction g; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f1
	 *            divided function
	 * @param f2
	 *            divisor function
	 */
	public AlgoPolynomialMod(Construction cons, String label, GeoFunction f1,
			GeoFunction f2) {
		super(cons);
		this.f1 = f1;
		this.f2 = f2;

		g = new GeoFunction(cons);
		setInputOutput(); // for AlgoElement
		compute();
		g.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Mod;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = f1;
		input[1] = f2;

		setOnlyOutput(g);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return residue of division
	 */
	public GeoFunction getResult() {
		return g;
	}

	@Override
	public final void compute() {
		if (!f1.isDefined() || !f2.isDefined()) {
			g.setUndefined();
			return;
		}

		AlgoPolynomialDivision.nonCASDivision(kernel, f1, f2, null, g);

	}

}

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
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Algorithm for coefficients of a conic
 * 
 * @author Michael Borcherds
 */
public abstract class AlgoEquationCoefficients extends AlgoElement {
	/** input */
	protected GeoElementND eqn;
	/** output */
	private GeoList g;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param eqn
	 *            equation
	 */
	public AlgoEquationCoefficients(Construction cons, String label,
			GeoElementND eqn, int dim) {
		super(cons);
		this.eqn = eqn;

		g = new GeoList(cons);
		for (int i = 0; i < dim; i++) {
			g.add(new GeoNumeric(cons, 0));
		}
		setInputOutput(); // for AlgoElement
		compute();
		g.setLabel(label);
	}

	/**
	 * Constructor for extending algos
	 * 
	 * @param cons
	 *            construction
	 */
	public AlgoEquationCoefficients(Construction cons) {
		super(cons);
	}

	@Override
	public Commands getClassName() {
		return Commands.Coefficients;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = eqn.toGeoElement();

		setOnlyOutput(g);
		setDependencies(); // done by AlgoElement
	}

	@Override
	public final void compute() {
		if (!eqn.isDefined()) {
			g.setUndefined();
			return;
		}
		
		g.setDefined(true);
		extractCoefficients();
	}

	protected abstract void extractCoefficients();

	protected void setCoeff(int i, double val) {
		((GeoNumeric) g.get(i)).setValue(val);
	}

	/**
	 * @return resulting list of coefficients
	 */
	public GeoList getResult() {
		return g;
	}

}

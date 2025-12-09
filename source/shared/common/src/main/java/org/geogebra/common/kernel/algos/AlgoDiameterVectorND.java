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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 *
 * @author Markus
 */
public abstract class AlgoDiameterVectorND extends AlgoElement {

	protected GeoConicND c; // input
	protected GeoVectorND v; // input
	protected GeoLineND diameter; // output

	/**
	 * Creates new AlgoDiameterVector
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param c
	 *            conic
	 * @param v
	 *            vector
	 */
	public AlgoDiameterVectorND(Construction cons, String label, GeoConicND c,
			GeoVectorND v) {
		super(cons);
		this.v = v;
		this.c = c;
		createOutput(cons);

		setInputOutput(); // for AlgoElement

		compute();
		diameter.setLabel(label);
	}

	/**
	 * @param cons1
	 *            construction
	 */
	abstract protected void createOutput(Construction cons1);

	@Override
	public Commands getClassName() {
		return Commands.Diameter;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) v;
		input[1] = c;

		setOnlyOutput(diameter);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return result
	 */
	public GeoLineND getDiameter() {
		return diameter;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("DiameterOfAConjugateToB",
				"Diameter of %0 conjugate to %1", c.getLabel(tpl),
				v.getLabel(tpl));
	}

}

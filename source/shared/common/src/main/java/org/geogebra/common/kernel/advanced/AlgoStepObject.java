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

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Returns the construction step of a GeoElement
 * 
 * @author Markus
 */
public class AlgoStepObject extends AlgoElement {

	private GeoElement inputGeo; // input
	private GeoNumeric num; // output

	// private Construction cons;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputGeo
	 *            geo
	 */
	public AlgoStepObject(Construction cons, String label,
			GeoElement inputGeo) {
		super(cons);
		// this.cons=cons;
		this.inputGeo = inputGeo;

		num = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement

		compute();

		num.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.ConstructionStep;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputGeo;

		setOnlyOutput(num);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return construction step of the geo
	 */
	public GeoNumeric getResult() {
		return num;
	}

	@Override
	final public boolean wantsConstructionProtocolUpdate() {
		return true;
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {
		double step = inputGeo.getConstructionIndex();
		num.setValue(step + 1);
	}

}

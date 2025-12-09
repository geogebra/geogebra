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
 * Computes x-axis step
 */
public class AlgoAxisStep extends AlgoElement {

	protected GeoNumeric num; // output
	private int axis;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param axis
	 *            0 for x, 1 for y
	 */
	public AlgoAxisStep(Construction cons, String label, int axis) {
		super(cons);
		this.axis = axis;
		num = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement

		// compute angle
		compute();

		num.setLabel(label);

		// ensure we get updates
		cons.registerEuclidianViewCE(this);
	}

	@Override
	public Commands getClassName() {
		return axis == 0 ? Commands.AxisStepX : Commands.AxisStepY;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[0];

		setOnlyOutput(num);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getResult() {
		return num;
	}

	@Override
	public boolean euclidianViewUpdate() {
		compute();
		// update num and all dependent elements
		num.updateCascade();
		return false;
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {
		double[] axisSteps = kernel.getApplication().getEuclidianView1()
				.getGridDistances();
		num.setValue(axisSteps[axis]);
	}

}

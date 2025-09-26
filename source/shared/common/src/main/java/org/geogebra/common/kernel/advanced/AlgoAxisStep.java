/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

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

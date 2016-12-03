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
 * 
 * @author
 */
public class AlgoAxisStepX extends AlgoElement {

	protected GeoNumeric num; // output

	public AlgoAxisStepX(Construction cons, String label) {
		super(cons);

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
		return Commands.AxisStepX;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[0];

		setOutputLength(1);
		setOutput(0, num);
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
		double axisSteps[] = (kernel.getApplication()).getEuclidianView1()
				.getGridDistances();
		num.setValue(axisSteps[0]);
	}

	
}

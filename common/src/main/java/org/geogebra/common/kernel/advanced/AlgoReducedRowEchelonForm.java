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
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.util.GgbMat;

/**
 * ReducedRowEchelonForm a matrix. Adapted from AlgoSort
 * 
 * @author Michael Borcherds
 * @version 16-02-2008
 */

public class AlgoReducedRowEchelonForm extends AlgoElement {

	private GeoList inputList; // input
	private GeoList outputList; // output

	/**
	 * Creates new reduced echelon form algo
	 * 
	 * @param cons
	 * @param label
	 *            label for result
	 * @param inputList
	 *            original matrix
	 */
	public AlgoReducedRowEchelonForm(Construction cons, String label,
			GeoList inputList) {
		super(cons);
		this.inputList = inputList;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.ReducedRowEchelonForm;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputList;

		setOutputLength(1);
		setOutput(0, outputList);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the reduced matrix
	 * 
	 * @return reduced matrix
	 */
	public GeoList getResult() {
		return outputList;
	}

	@Override
	public final void compute() {

		GgbMat matrix = new GgbMat(inputList);

		if (matrix.isUndefined()) {
			outputList.setUndefined();
			return;
		}

		matrix.reducedRowEchelonFormImmediate();
		// ReducedRowEchelonForm[{{1,2},{3,4}}]

		matrix.getGeoList(outputList, cons);
	}

	

}

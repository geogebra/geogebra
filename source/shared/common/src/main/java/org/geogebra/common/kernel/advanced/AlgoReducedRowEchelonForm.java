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
	 *            construction
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

		setOnlyOutput(outputList);
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

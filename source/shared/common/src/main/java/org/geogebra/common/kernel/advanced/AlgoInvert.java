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
 * Invert a matrix.
 * 
 * @author Michael Borcherds
 * @version 16-02-2008
 */

public class AlgoInvert extends AlgoElement {

	private GeoList inputList; // input
	private GeoList outputList; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputList
	 *            input matrix
	 */
	public AlgoInvert(Construction cons, String label, GeoList inputList) {
		super(cons);
		this.inputList = inputList;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Invert;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputList;

		setOnlyOutput(outputList);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return outputList;
	}

	@Override
	public final void compute() {

		GgbMat matrix = new GgbMat(inputList);

		if (matrix.isUndefined() || !matrix.isSquare()) {
			outputList.setUndefined();
			return;
		}

		// needed for eg {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}
		boolean integers = matrix.hasOnlyIntegers();
		double det = Math.round(matrix.determinant());
		matrix.inverseImmediate();
		if (integers) {
			for (int i = 0; i < inputList.size(); i++) {
				for (int j = 0; j < inputList.size(); j++) {
					matrix.setEntry(i, j,
							Math.round(matrix.getEntry(i, j) * det) / det);
				}
			}
		}

		// Invert[{{1,2},{3,4}}]

		matrix.getGeoList(outputList, cons);
	}

}

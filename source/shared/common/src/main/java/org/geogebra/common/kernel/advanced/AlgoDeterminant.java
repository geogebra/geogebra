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
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.util.GgbMat;

/**
 * Reverse a list. Adapted from AlgoSort
 * 
 * @author Michael Borcherds
 * @version 16-02-2008
 */

public class AlgoDeterminant extends AlgoElement {

	private GeoList inputList; // input
	private GeoNumeric num; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputList
	 *            input matrix
	 */
	public AlgoDeterminant(Construction cons, String label, GeoList inputList) {
		super(cons);
		this.inputList = inputList;

		num = new GeoNumeric(cons);

		setInputOutput();
		compute();
		num.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Determinant;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputList;

		super.setOnlyOutput(num);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getResult() {
		return num;
	}

	@Override
	public final void compute() {

		GgbMat matrix = new GgbMat(inputList);

		if (matrix.isUndefined() || !matrix.isSquare()) {
			num.setUndefined();
			return;
		}

		double det = matrix.determinant();

		if (matrix.hasOnlyIntegers()) {
			det = Math.round(det);
		}

		num.setValue(det);

		// Determinant[{{1,2},{3,4}}]
	}

}

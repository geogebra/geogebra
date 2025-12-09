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

/**
 * Transpose a list. Adapted from AlgoSort
 * 
 * @author Michael Borcherds
 * @version 16-02-2008
 */

public class AlgoTranspose extends AlgoElement {

	private GeoList inputList; // input
	private GeoList outputList; // output

	/**
	 * @param cons
	 *            construction
	 * @param inputList
	 *            matrix
	 */
	public AlgoTranspose(Construction cons, GeoList inputList) {
		super(cons);
		this.inputList = inputList;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Transpose;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputList;

		setOnlyOutput(outputList);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return transposed list
	 */
	public GeoList getResult() {
		return outputList;
	}

	@Override
	public final void compute() {

		if (!inputList.isDefined()
				|| (inputList.size() > 0 && !inputList.get(0).isGeoList())) {
			outputList.setUndefined();
			return;
		}
		outputList.clear();
		if (inputList.size() == 0) {
			return;
		}
		int cols = ((GeoList) inputList.get(0)).size();
		for (int i = 1; i < inputList.size(); i++) {

			if (!inputList.get(i).isGeoList()
					|| ((GeoList) inputList.get(i)).size() != cols) {
				outputList.setUndefined();
				return;
			}
		}
		outputList.setDefined(true);
		for (int i = 0; i < cols; i++) {
			GeoList column = new GeoList(cons);
			for (int j = 0; j < inputList.size(); j++) {
				column.add(((GeoList) inputList.get(j)).get(i).copy());
			}
			outputList.add(column);
		}

	}

}

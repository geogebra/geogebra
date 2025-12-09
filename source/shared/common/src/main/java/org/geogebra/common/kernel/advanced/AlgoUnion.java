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
 * Computes union of two sets (lists)
 */
public class AlgoUnion extends AlgoElement {

	private GeoList inputList; // input
	private GeoList inputList2; // input
	private GeoList outputList; // output
	private int size;
	private int size2;

	/**
	 * @param cons
	 *            construction
	 * @param inputList
	 *            first list
	 * @param inputList2
	 *            second list
	 */
	public AlgoUnion(Construction cons, GeoList inputList,
			GeoList inputList2) {
		super(cons);

		this.inputList = inputList;
		this.inputList2 = inputList2;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Union;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];

		input[0] = inputList;
		input[1] = inputList2;

		setOnlyOutput(outputList);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return union of lists
	 */
	public GeoList getResult() {
		return outputList;
	}

	@Override
	public final void compute() {

		size = inputList.size();
		size2 = inputList2.size();

		if (!inputList.isDefined() || !inputList2.isDefined()) {
			outputList.setUndefined();
			return;
		}

		outputList.setDefined(true);
		outputList.clear();

		for (int i = 0; i < size; i++) {
			addToOutputList(outputList, inputList.get(i));
		}

		for (int i = 0; i < size2; i++) {
			addToOutputList(outputList, inputList2.get(i));

		}
	}

	/**
	 * checks not already in list
	 * 
	 * @param outputList
	 *            output list
	 * @param geo
	 *            new element
	 */
	protected static void addToOutputList(GeoList outputList, GeoElement geo) {
		boolean alreadyInOutputList = false;
		for (int k = 0; k < outputList.size(); k++) {
			if (geo.isEqual(outputList.get(k))) {
				alreadyInOutputList = true;
				break;
			}
		}

		if (!alreadyInOutputList) {
			outputList.add(geo.copyInternal(outputList.getConstruction()));
		}
	}

}

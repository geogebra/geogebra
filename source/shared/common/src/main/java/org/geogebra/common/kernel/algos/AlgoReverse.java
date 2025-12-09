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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Reverse a list. Adapted from AlgoSort
 * 
 * @author Michael Borcherds
 * @version 16-02-2008
 */

public class AlgoReverse extends AlgoElement {

	private GeoList inputList; // input
	private GeoList outputList; // output
	private int size;

	/**
	 * Creates new reverse list algo
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputList
	 *            input list
	 */
	public AlgoReverse(Construction cons, String label, GeoList inputList) {
		super(cons);
		this.inputList = inputList;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Reverse;
	}

	@Override
	protected void setInputOutput() {

		// make sure that x(Element[list,1]) will work even if the output list's
		// length is zero
		outputList.setTypeStringForXML(inputList.getTypeStringForXML());

		input = new GeoElement[1];
		input[0] = inputList;

		setOnlyOutput(outputList);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the reversed list
	 * 
	 * @return reversed list
	 */
	public GeoList getResult() {
		return outputList;
	}

	@Override
	public final void compute() {

		size = inputList.size();
		if (!inputList.isDefined()) {
			outputList.setUndefined();
			return;
		}

		outputList.setDefined(true);
		outputList.clear();

		if (size == 0) {
			return; // return empty list
		}

		for (int i = 0; i < size; i++) {
			// need to copy elements like eg {(1,1)} so the properties can be
			// set independently
			GeoElement geo = inputList.get(size - 1 - i);
			if (!geo.isLabelSet()) {
				geo = geo.copyInternal(cons);
			}

			outputList.add(geo);
		}
	}

}

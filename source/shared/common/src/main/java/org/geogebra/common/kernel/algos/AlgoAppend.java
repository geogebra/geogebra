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
 * Appends or prepends element to a list
 *
 */
public class AlgoAppend extends AlgoElement {

	private GeoList inputList; // input
	private GeoElement geo; // input
	private GeoList outputList; // output
	private int size;
	private int order;
	private static final int ADD_OBJECT_AT_START = 0;
	private static final int ADD_OBJECT_AT_END = 1;

	private AlgoAppend(Construction cons, String label, GeoList inputList,
			GeoElement geo, int order) {
		super(cons);

		this.order = order;

		this.inputList = inputList;
		this.geo = geo;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputList
	 *            list
	 * @param geo
	 *            appended element
	 */
	public AlgoAppend(Construction cons, String label, GeoList inputList,
			GeoElement geo) {
		this(cons, label, inputList, geo, ADD_OBJECT_AT_END);

	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputList
	 *            list
	 * @param geo
	 *            prepended element
	 */
	public AlgoAppend(Construction cons, String label, GeoElement geo,
			GeoList inputList) {
		this(cons, label, inputList, geo, ADD_OBJECT_AT_START);
	}

	@Override
	public Commands getClassName() {
		return Commands.Append;
	}

	@Override
	protected void setInputOutput() {

		// make sure that x(Element[list,1]) will work even if the output list's
		// length is zero
		outputList.setTypeStringForXML(inputList.getTypeStringForXML());

		input = new GeoElement[2];

		if (order == ADD_OBJECT_AT_END) {
			input[0] = inputList;
			input[1] = geo;
		} else { // ADD_OBJECT_AT_START
			input[0] = geo;
			input[1] = inputList;
		}

		setOnlyOutput(outputList);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return appended list
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

		if (order == ADD_OBJECT_AT_START) {
			outputList.add(geo.copyInternal(cons));
		}
		for (int i = 0; i < size; i++) {
			outputList.add(inputList.get(i).copyInternal(cons));
		}
		if (order == ADD_OBJECT_AT_END) {
			outputList.add(geo.copyInternal(cons));
		}
	}

}

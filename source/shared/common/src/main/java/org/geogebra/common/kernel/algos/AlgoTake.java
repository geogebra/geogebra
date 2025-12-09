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
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Take objects from the middle of a list
 * 
 * @author Michael Borcherds
 * @version 2008-03-04
 */

public class AlgoTake extends AlgoElement {

	private GeoList inputList; // input
	private GeoNumeric m; // input
	private GeoNumeric n; // input
	private GeoList outputList; // output
	private int size;

	/**
	 * @param cons
	 *            construction
	 * @param inputList
	 *            list
	 * @param m
	 *            start index (1 based)
	 * @param n
	 *            end index (1 based)
	 */
	public AlgoTake(Construction cons, GeoList inputList, GeoNumeric m,
			GeoNumeric n) {
		super(cons);
		this.inputList = inputList;
		this.m = m;
		this.n = n;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Take;
	}

	@Override
	protected void setInputOutput() {

		// make sure that x(Element[list,1]) will work even if the output list's
		// length is zero
		outputList.setTypeStringForXML(inputList.getTypeStringForXML());

		if (n != null) {
			input = new GeoElement[3];
			input[2] = n;
		} else {
			input = new GeoElement[2];
		}
		input[0] = inputList;
		input[1] = m;

		setOnlyOutput(outputList);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting list
	 */
	public GeoList getResult() {
		return outputList;
	}

	@Override
	public final void compute() {

		if (!m.isDefined() || (n != null && !n.isDefined())) {
			// return empty list
			outputList.setDefined(true);
			outputList.clear();
			return;
		}

		size = inputList.size();
		int start = (int) m.getDouble();
		double nVal = n == null ? size : n.getDouble();
		int end = (int) nVal;

		if (nVal == 0 && inputList.isDefined() && start > 0 && start <= size) {
			// return empty list
			outputList.setDefined(true);
			outputList.clear();
			return;
		}

		if (!inputList.isDefined() || size == 0 || start <= 0 || end > size
				|| start > end) {
			outputList.setUndefined();
			return;
		}

		outputList.setDefined(true);
		outputList.clear();

		for (int i = start; i <= end; i++) {
			outputList.add(inputList.get(i - 1).copyInternal(cons));
		}
	}

}

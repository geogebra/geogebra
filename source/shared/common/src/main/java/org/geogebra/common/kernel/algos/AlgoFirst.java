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
 * Take first n objects from a list
 * 
 * @author Michael Borcherds
 * @version 2008-03-04
 */

public class AlgoFirst extends AlgoElement {

	protected GeoElement inputList; // input
	protected GeoNumeric n; // input
	protected GeoList outputList; // output
	protected int size;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputList
	 *            input list
	 * @param n
	 *            number of elements (null for 1)
	 */
	public AlgoFirst(Construction cons, String label, GeoElement inputList,
			GeoNumeric n) {
		super(cons);
		this.inputList = inputList;
		this.n = n;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.First;
	}

	@Override
	protected void setInputOutput() {

		// make sure that x(Element[list,1]) will work even if the output list's
		// length is zero
		if (inputList.isGeoList()) {
			outputList.setTypeStringForXML(
					((GeoList) inputList).getTypeStringForXML());
		} else {
			// inputList is a Locusm see AlgoFirstLocus
			outputList.setTypeStringForXML("point");
		}

		if (n != null) {
			input = new GeoElement[2];
			input[0] = inputList;
			input[1] = n;
		} else {
			input = new GeoElement[1];
			input[0] = inputList;
		}

		setOnlyOutput(outputList);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return outputList;
	}

	@Override
	public void compute() {

		size = ((GeoList) inputList).size();
		int outsize = n == null ? 1 : (int) n.getDouble();

		if (!inputList.isDefined() || size == 0 || outsize < 0
				|| outsize > size) {
			outputList.setUndefined();
			return;
		}

		outputList.setDefined(true);
		outputList.clear();

		if (outsize == 0) {
			return; // return empty list
		}

		for (int i = 0; i < outsize; i++) {
			outputList.add(((GeoList) inputList).get(i).copyInternal(cons));
		}
	}

}

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
 * Join lists
 */
public class AlgoJoin extends AlgoElement {

	private GeoList inputList; // input
	private GeoList outputList; // output
	private int size;
	private int size2;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputList
	 *            list of lists
	 */
	public AlgoJoin(Construction cons, String label, GeoList inputList) {
		super(cons);

		this.inputList = inputList;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Join;
	}

	@Override
	protected void setInputOutput() {
		// make sure that x(Element[list,1]) will work even if the output list's
		// length is zero
		for (int i = 0; i < inputList.size(); i++) {
			if (inputList.get(i).isGeoList()) {
				String typeStringForXML = ((GeoList) inputList.get(i)).getTypeStringForXML();
				if (typeStringForXML != null) {
					outputList.setTypeStringForXML(typeStringForXML);
				}
			}
		}

		input = new GeoElement[1];

		input[0] = inputList;

		setOnlyOutput(outputList);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return joined list
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

		for (int i = 0; i < size; i++) {

			GeoElement geo = inputList.get(i);
			if (!geo.isGeoList()) {
				outputList.setUndefined();
				return;
			}

			GeoList list = (GeoList) geo;
			size2 = list.size();

			if (size2 > 0) {
				for (int j = 0; j < size2; j++) {
					outputList.add(list.get(j).copyInternal(cons));
				}
			}
		}
	}

}

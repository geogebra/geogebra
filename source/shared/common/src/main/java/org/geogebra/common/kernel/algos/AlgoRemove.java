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

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Remove undefined objects from a list
 * 
 * @author Michael Borcherds
 * @version 2008-03-04
 */

public class AlgoRemove extends AlgoElement {

	private GeoList inputList;
	private GeoList inputList2; // input
	private GeoList outputList; // output
	private int size;
	private ArrayList<GeoElement> geosToRemove = new ArrayList<>();

	/**
	 * Creates new undefined removal algo
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputList
	 *            list
	 * @param inputList2
	 *            elements to be removed from list
	 */
	public AlgoRemove(Construction cons, String label, GeoList inputList,
			GeoList inputList2) {
		super(cons);
		this.inputList = inputList;
		this.inputList2 = inputList2;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Remove;
	}

	@Override
	protected void setInputOutput() {

		// make sure that x(Element[list,1]) will work even if the output list's
		// length is zero
		outputList.setTypeStringForXML(inputList.getTypeStringForXML());

		input = new GeoElement[2];
		input[0] = inputList;
		input[1] = inputList2;

		setOnlyOutput(outputList);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the pruned list
	 * 
	 * @return pruned list
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
			return;
		}

		geosToRemove.clear();
		int undefinedToRemove = 0;
		for (int i = 0; i < inputList2.size(); i++) {
			GeoElement geo = inputList2.get(i);
			if (AlgoRemoveUndefined.isDefined(geo)) {
				geosToRemove.add(geo);
			} else {
				undefinedToRemove++;
			}
		}

		for (int i = 0; i < size; i++) {
			GeoElement geo = inputList.get(i);

			boolean lookFor = true;

			if (undefinedToRemove > 0 && !AlgoRemoveUndefined.isDefined(geo)) {
				undefinedToRemove--;
				lookFor = false;
			}

			for (int j = 0; j < geosToRemove.size() && lookFor; j++) {
				if (geo.isEqual(geosToRemove.get(j))) {
					geosToRemove.remove(j);
					lookFor = false;
				}
			}

			if (lookFor) {
				outputList.add(geo.copyInternal(cons));
			}
		}
	}

}

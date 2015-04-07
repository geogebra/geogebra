/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

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

	private GeoList inputList, inputList2; // input
	private GeoList outputList; // output
	private int size;

	/**
	 * Creates new undefined removal algo
	 * 
	 * @param cons
	 * @param label
	 * @param inputList
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

		setOutputLength(1);
		setOutput(0, outputList);
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

	private ArrayList<GeoElement> geosToRemove = new ArrayList<GeoElement>();

	@Override
	public final void compute() {

		size = inputList.size();

		if (!inputList.isDefined()) {
			outputList.setUndefined();
			return;
		}

		outputList.setDefined(true);
		outputList.clear();

		if (size == 0)
			return;

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

	// TODO Consider locusequability

}

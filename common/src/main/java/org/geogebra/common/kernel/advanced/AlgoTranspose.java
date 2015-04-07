/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Reverse a list. Adapted from AlgoSort
 * 
 * @author Michael Borcherds
 * @version 16-02-2008
 */

public class AlgoTranspose extends AlgoElement {

	private GeoList inputList; // input
	private GeoList outputList; // output

	public AlgoTranspose(Construction cons, String label, GeoList inputList) {
		super(cons);
		this.inputList = inputList;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Transpose;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputList;

		super.setOutputLength(1);
		super.setOutput(0, outputList);
		setDependencies(); // done by AlgoElement
	}

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
		for (int i = 0; i < cols; i++) {
			GeoList column = new GeoList(cons);
			for (int j = 0; j < inputList.size(); j++) {
				column.add(((GeoList) inputList.get(j)).get(i).copy());
			}
			outputList.add(column);
		}

	}

	// TODO Consider locusequability

}

/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * RootList[{1,2,3}] makes { (1,0) (2,0), (3,0) }. Adapted from AlgoSort
 * 
 * @author Michael Borcherds
 * @version 04-04-2010
 */

public class AlgoRootList extends AlgoElement {

	private GeoList inputList; // input
	private GeoList outputList; // output
	private int size;

	public AlgoRootList(Construction cons, String label, GeoList inputList) {
		super(cons);
		this.inputList = inputList;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.RootList;
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

		size = inputList.size();
		if (!inputList.isDefined() || size == 0) {
			outputList.setUndefined();
			return;
		}

		outputList.setDefined(true);
		outputList.clear();

		boolean suppressLabelCreation = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		for (int i = 0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isGeoNumeric()) {
				GeoNumeric num = (GeoNumeric) geo;
				outputList.addPoint(num.getDouble(), 0.0, 1.0,
						this);
			}
		}
		cons.setSuppressLabelCreation(suppressLabelCreation);
	}

	

}

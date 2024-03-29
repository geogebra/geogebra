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
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.debug.Log;

/**
 * Take objects from the middle of a list adapted from AlgoKeepIf
 * 
 * @author Michael Borcherds
 */

public class AlgoKeepIf3 extends AlgoElement {

	private GeoList inputList; // input
	private GeoList outputList; // output
	private GeoBoolean bool; // input
	private GeoElement var;
	private int size;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param bool
	 *            boolean filter (dependent on var)
	 * @param var
	 *            variable to be substituted
	 * @param inputList
	 *            list
	 */
	public AlgoKeepIf3(Construction cons, String label, GeoBoolean bool,
			GeoElement var, GeoList inputList) {
		super(cons);
		this.inputList = inputList;
		this.var = var;
		this.bool = bool;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.KeepIf;
	}

	@Override
	protected void setInputOutput() {
		outputList.setTypeStringForXML(inputList.getTypeStringForXML());
		input = new GeoElement[3];
		input[0] = bool;
		input[1] = var;
		input[2] = inputList;

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

		try {
			for (int i = 0; i < size; i++) {
				GeoElement geo = inputList.get(i);
				var.set(geo);
				this.setStopUpdateCascade(true);
				var.getAlgoUpdateSet()
						.updateAllUntil(bool.getParentAlgorithm());
				if (bool.getBoolean()) {
					outputList.add(geo.copyInternal(cons));
				}
				this.setStopUpdateCascade(false);
			}
		} catch (MyError e) {
			// eg KeepIf[x(A)<2,A,{(1,1),(2,2),(3,3),1}]
			Log.debug(e);
			outputList.setUndefined();
			return;
		}

	}

}

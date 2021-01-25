/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.SetRandomValue;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Shuffle a list. Adapted from AlgoSort
 * 
 * @author Michael Borcherds
 * @version 04-01-2008
 */

public class AlgoShuffle extends AlgoElement implements SetRandomValue {

	private GeoList inputList; // input
	private GeoList outputList; // output
	private int size;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputList
	 *            list to be shuffled
	 */
	public AlgoShuffle(Construction cons, String label, GeoList inputList) {
		super(cons);
		this.inputList = inputList;

		outputList = new GeoList(cons);

		cons.addRandomGeo(outputList);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Shuffle;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputList;

		setOnlyOutput(outputList);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return shuffled list
	 */
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

		ArrayList<GeoElement> list = copyInput();

		// copy the geos back into a GeoList in a random order
		outputList.setDefined(true);
		outputList.clear();
		fill(list);
	}

	private ArrayList<GeoElement> copyInput() {
		ArrayList<GeoElement> list = new ArrayList<>();

		// copy inputList into arraylist
		for (int i = 0; i < size; i++) {
			list.add(inputList.get(i));
		}
		return list;
	}

	private void fill(ArrayList<GeoElement> list) {
		int listSize = list.size();
		for (int i = 0; i < listSize; i++) {
			int pos = (int) Math.floor(
					cons.getApplication().getRandomNumber() * (listSize - i));
			outputList.add(list.get(pos));
			list.remove(pos);
		}
	}

	@Override
	public boolean setRandomValue(GeoElementND d) {
		if (d instanceof GeoList && ((GeoList) d).size() == inputList.size()) {
			GeoList lv = ((GeoList) d).copy();
			outputList.clear();
			ArrayList<GeoElement> list = copyInput();
			for (int i = 0; i < lv.size(); i++) {
				if (removeFromList(lv.get(i), list)) {
					outputList.add(lv.get(i));
				}
			}
			fill(list);
			return true;
		}
		return false;
	}

	protected static boolean removeFromList(GeoElement geoElement,
			ArrayList<GeoElement> inputCopy) {
		for (int i = 0; i < inputCopy.size(); i++) {
			if (inputCopy.get(i).isEqual(geoElement)) {
				inputCopy.remove(i);
				return true;
			}
		}
		return false;
	}
}

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
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Sample from a list. Adapted from AlgoMode
 * 
 * @author Michael Borcherds
 */

public class AlgoSample extends AlgoElement implements SetRandomValue {

	// maximum size for a sample
	private static int SAMPLE_MAXSIZE = 10000;

	private GeoList inputList; // input
	private GeoBoolean replacement;
	private GeoNumberValue num;
	private GeoList outputList; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputList
	 *            input data
	 * @param num
	 *            sample size
	 * @param replacement
	 *            use replacement?
	 */
	public AlgoSample(Construction cons, String label, GeoList inputList,
			GeoNumberValue num, GeoBoolean replacement) {
		super(cons);
		this.inputList = inputList;
		this.replacement = replacement;
		this.num = num;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Sample;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[replacement == null ? 2 : 3];
		input[0] = inputList;
		input[1] = num.toGeoElement();
		if (replacement != null) {
			input[2] = replacement;
		}

		setOutputLength(1);
		setOutput(0, outputList);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return outputList;
	}

	@Override
	public final void compute() {

		int size = (int) num.getDouble();
		if (!inputList.isDefined() || num.getDouble() < 1
				|| num.getDouble() > SAMPLE_MAXSIZE) {
			outputList.setUndefined();
			return;
		}

		int inputListSize = inputList.size();
		outputList.clear();

		if (withReplacement()) {
			for (int i = 0; i < size; i++) {
				GeoElement geo;

				geo = inputList.get((int) Math
						.floor(cons.getApplication().getRandomNumber()
								* inputListSize));
				setListElement(i, geo);
			}
		} else {

			// sampling without replacement

			if (size > inputListSize) {
				outputList.setUndefined();
				return;
			}

			ArrayList<GeoElement> list = copyInput();

			// copy the geos back into a GeoList in a random order
			for (int i = 0; i < size; i++) {
				int pos = (int) Math
						.floor(cons.getApplication().getRandomNumber()
								* (inputListSize - i));
				outputList.add(list.get(pos));
				list.remove(pos);
			}

		}

		outputList.setDefined(true);
	}

	private ArrayList<GeoElement> copyInput() {
		ArrayList<GeoElement> copy = new ArrayList<>();
		int inputListSize = inputList.size();
		// copy inputList into arraylist
		for (int i = 0; i < inputListSize; i++) {
			copy.add(inputList.get(i));
		}
		return copy;
	}

	private boolean withReplacement() {
		return replacement != null && replacement.getBoolean();
	}

	// copied from AlgoIterationList.java
	// TODO should it be centralised?
	private void setListElement(int index, GeoElement geo) {
		GeoElement listElement;
		if (index < outputList.getCacheSize()) {
			// use existing list element
			listElement = outputList.getCached(index);
			listElement.set(geo);
		} else {
			// create a new list element
			listElement = geo.copy();
			listElement.setParentAlgorithm(this);
			listElement.setConstructionDefaults();
			listElement.setUseVisualDefaults(false);
		}

		outputList.add(listElement);

	}

	@Override
	public boolean setRandomValue(GeoElementND d) {
		if (d instanceof GeoList) {
			GeoList otherList = (GeoList) d;
			if (withReplacement()) {
				for (int i = 0; i < otherList.size(); i++) {
					if (!inputContains(otherList.get(i))) {
						return false;
					}
				}
			} else {
				ArrayList<GeoElement> copy = copyInput();
				for (int i = 0; i < otherList.size(); i++) {
					if (!AlgoShuffle.removeFromList(otherList.get(i), copy)) {
						return false;
					}
				}
			}
			if (outputList != otherList) {
				outputList.set(otherList);
			}
			return true;
		}
		return false;
	}

	private boolean inputContains(GeoElement geoElement) {
		for (int i = 0; i < inputList.size(); i++) {
			if (inputList.get(i).isEqual(geoElement)) {
				return true;
			}
		}
		return false;
	}
}

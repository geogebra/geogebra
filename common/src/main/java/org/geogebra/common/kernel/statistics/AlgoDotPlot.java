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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Create a dot plot.
 * 
 * Input: list of unsorted raw numeric data Output: sorted list of points
 * forming a dot plot of the raw data
 * 
 * A dot plot is a set of points for which: x coordinates = values from a list
 * of numeric data y coordinates = number of times the x data value has occurred
 * 
 * example: raw data = { 5,11,12,12,12,5 } dot plot = { (5,1), (5,2), (11,1),
 * (12,1), (12,2), (12,3) }
 * 
 * Adapted from AlgoSort and AlgoPointList
 * 
 * @author G.Sturr
 * @version 2010-8-10
 */

public class AlgoDotPlot extends AlgoUsingUniqueAndFrequency {

	private GeoList inputList; // input
	private GeoBoolean stackAdjacentDots;// input
	private GeoNumeric scale;// input

	private GeoList outputList; // output
	private int size;

	private int oldListSize;
	private String toolTipText;
	private double scaleFactor;

	public AlgoDotPlot(Construction cons, String label, GeoList inputList) {

		this(cons, inputList, null, null);
		outputList.setLabel(label);
	}

	public AlgoDotPlot(Construction cons, GeoList inputList) {

		this(cons, inputList, null, null);
	}

	protected AlgoDotPlot(Construction cons, String label, GeoList inputList,
			GeoNumeric scale) {

		this(cons, inputList, null, scale);
		outputList.setLabel(label);

	}

	protected AlgoDotPlot(Construction cons, String label, GeoList inputList,
			GeoBoolean stackDots) {

		this(cons, inputList, stackDots, null);
		outputList.setLabel(label);

	}

	protected AlgoDotPlot(Construction cons, String label, GeoList inputList,
			GeoBoolean stackDots, GeoNumeric scale) {
		this(cons, inputList, stackDots, scale);
		outputList.setLabel(label);
	}

	protected AlgoDotPlot(Construction cons, GeoList inputList,
			GeoBoolean stackDots, GeoNumeric scale) {
		super(cons);
		this.inputList = inputList;
		this.stackAdjacentDots = stackDots;
		this.scale = scale;

		outputList = new GeoList(cons) {
			// allow custom tool tips for individual points
			@Override
			public String getTooltipText(boolean colored, boolean alwaysOn) {
				return ((AlgoDotPlot) getParentAlgorithm()).getTooltipText();
			}
		};
		setInputOutput();
		compute();

	}

	@Override
	public Commands getClassName() {
		return Commands.DotPlot;
	}

	/**
	 * set the input
	 */
	protected void setInput() {

		ArrayList<GeoElement> tempList = new ArrayList<GeoElement>();
		tempList.add(inputList);
		if (stackAdjacentDots != null)
			tempList.add(stackAdjacentDots);
		if (scale != null)
			tempList.add(scale);

		input = new GeoElement[tempList.size()];
		input = tempList.toArray(input);
	}

	@Override
	protected void setInputOutput() {

		createHelperAlgos(inputList);

		setInput();

		setOutputLength(1);
		setOutput(0, outputList);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return outputList;
	}

	@Override
	public void compute() {

		size = inputList.size();
		if (!inputList.isDefined() || size == 0) {
			outputList.setUndefined();
			return;
		}
		if (scale != null) {
			scaleFactor = scale.getValue();
		} else {
			scaleFactor = 1.0;
		}

		// ========================================
		// sort the raw data
		GeoList list1 = algoFreq.getValue();
		GeoList list2 = algoFreq.getResult();

		// prepare output list. Pre-existing geos will be recycled,
		// but extra geos are removed when outputList is too long
		outputList.setDefined(true);
		for (int i = outputList.size() - 1; i >= size; i--) {
			GeoElement extraGeo = outputList.get(i);
			extraGeo.remove();
			outputList.remove(extraGeo);
		}

		oldListSize = outputList.size();

		// ========================================
		// create dot plot points


		int index = 0;
		for (int i = 0; i < list1.size(); i++) {
			double x;
			if (list1.get(i).isGeoNumeric()) {
				x = list1.get(i).evaluateDouble();
			} else {
				// use integers 1,2,3 ... to position non-numeric data
				x = i + 1;
			}

			int height = (int) list2.get(i).evaluateDouble();

			for (int y = 1; y <= height; y++) {
				double scaledY = getScaledY(y);
				if (index < oldListSize) {
					((GeoPoint) outputList.get(index)).setCoords(x, scaledY,
							1.0);
				} else {
					outputList.addPoint(x, scaledY, 1.0, null);
				}
				index++;
			}

		}

	}

	/**
	 * 
	 * @param y
	 *            current height
	 * @return scaled y
	 */
	protected double getScaledY(int y) {

		if (scale != null) {
			return y * scaleFactor;
		}
		return y;
	}

	public GeoList getUniqueXList() {
		return algoFreq.getValue();
	}

	public GeoList getFrequencyList() {
		return algoFreq.getResult();
	}

	public String getTooltipText() {
		return toolTipText;
	}

	public void setToolTipPointText(String text) {
		toolTipText = text;
	}

	public boolean stackAdjacentDots() {
		if (stackAdjacentDots != null) {
			return stackAdjacentDots.getBoolean();
		}
		return false;
	}

	public double getScaleFactor() {
		return scaleFactor;
	}

	// TODO Consider locusequability

}

package org.geogebra.common.kernel.algos;

/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Normalize values of a list between 0 and 1.
 * 
 * @author Oana Niculaescu
 * @version 11-03-2015
 */

public class AlgoNormalize extends AlgoElement {

	private GeoList geoList; // input
	private GeoList normalList; // output

	/**
	 * @param cons
	 * @param label
	 * @param geoList
	 */
	public AlgoNormalize(Construction cons, String label, GeoList geoList) {
		this(cons, geoList);
		normalList.setLabel(label);
	}

	/**
	 * @param cons
	 * @param geoList
	 */
	public AlgoNormalize(Construction cons, GeoList geoList) {
		super(cons);
		this.geoList = geoList;
		normalList = new GeoList(cons);

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Normalize;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = geoList;

		setOutputLength(1);
		setOutput(0, normalList);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return
	 */
	public GeoList getResult() {
		return normalList;
	}

	@Override
	public final void compute() {

		int size = geoList.size();
		if (!geoList.isDefined() || size == 0) {
			normalList.setUndefined();
			return;
		}

		normalList.setDefined(true);
		normalList.clear();

		double normalXVal = 0, normalYVal = 0, xMinVal = Double.POSITIVE_INFINITY, xMaxVal = Double.NEGATIVE_INFINITY;
		double yMinVal = Double.POSITIVE_INFINITY, yMaxVal = Double.NEGATIVE_INFINITY, xVal, yVal;

		// get Min and Max values for the list of numbers/points
		GeoElement geo0 = geoList.get(0);
		GeoPoint pt = null;
		if (geo0.isGeoNumeric()) {
			for (int i = 0; i < size; i++) {
				geo0 = geoList.get(i);
				if (((NumberValue) geo0).getDouble() > xMaxVal) {
					xMaxVal = ((NumberValue) geo0).getDouble();
				}
				if (((NumberValue) geo0).getDouble() < xMinVal) {
					xMinVal = ((NumberValue) geo0).getDouble();
				}
			}
			// if the list has all elements equal or, the list has just one
			// element
			if (xMaxVal == xMinVal) {
				normalXVal = xMaxVal;
			} else {
				normalXVal = xMaxVal - xMinVal;
			}
		} else if (geo0.isGeoPoint()) {
			for (int i = 0; i < size; i++) {
				pt = (GeoPoint) geoList.get(i);
				if (pt.getX() > xMaxVal) {
					xMaxVal = pt.getX();
				}
				if (pt.getX() < xMinVal) {
					xMinVal = pt.getX();
				}
				if (pt.getY() > yMaxVal) {
					yMaxVal = pt.getY();
				}
				if (pt.getY() < yMinVal) {
					yMinVal = pt.getY();
				}
			}
			if (xMaxVal == xMinVal) {
				normalXVal = xMaxVal;
			} else {
				normalXVal = xMaxVal - xMinVal;
			}
			if (yMaxVal == yMinVal) {
				normalYVal = yMaxVal;
			} else {
				normalYVal = yMaxVal - yMinVal;
			}
		} else {
			normalList.setUndefined();
			return;
		}

		boolean suppressLabelCreation = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		if (geo0.isGeoNumeric()) {
			for (int i = 0; i < size; i++) {
				geo0 = geoList.get(i);
				// it reaches here with with normalXVal == 0 only when we have a
				// zero element
				if (normalXVal == 0) {
					xVal = 0;
				} else {
					xVal = (((NumberValue) geo0).getDouble() - xMinVal)
							/ normalXVal;
				}

				normalList.addNumber(xVal, this);
			}
		} else if (geo0.isGeoPoint()) {
			for (int i = 0; i < size; i++) {
				pt = (GeoPoint) geoList.get(i);
				if (normalXVal == 0) {
					xVal = 0;
				} else {
					xVal = (pt.getX() - xMinVal) / normalXVal;
				}
				if (normalYVal == 0) {
					yVal = 0;
				} else {
					yVal = (pt.getY() - yMinVal) / normalYVal;
				}

				normalList.addPoint(xVal, yVal, 1.0, this);
			}
		}

		cons.setSuppressLabelCreation(suppressLabelCreation);
	}

}

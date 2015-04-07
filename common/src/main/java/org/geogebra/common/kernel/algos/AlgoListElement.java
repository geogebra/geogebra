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
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.Test;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.GeoClass;

/**
 * n-th element of a GeoList object.
 * 
 * Note: the type of the returned GeoElement object is determined by the type of
 * the first list element. If the list is initially empty, a GeoNumeric object
 * is created for element.
 * 
 * @author Markus Hohenwarter
 * @version 15-07-2007
 */

public class AlgoListElement extends AlgoElement {

	private GeoList geoList; // input
	private NumberValue num;
	private NumberValue[] num2 = null; // input
	private GeoElement numGeo;
	private GeoElement element; // output

	/**
	 * Creates new labeled element algo
	 * 
	 * @param cons
	 * @param label
	 * @param geoList
	 * @param num
	 */
	public AlgoListElement(Construction cons, String label, GeoList geoList,
			NumberValue num) {
		this(cons, geoList, num);
		element.setLabel(label);
	}

	public AlgoListElement(Construction cons, GeoList geoList, NumberValue num) {
		super(cons);
		this.geoList = geoList;
		this.num = num;
		numGeo = num.toGeoElement();

		int initIndex = Math.max(0, (int) Math.round(num.getDouble()) - 1);

		// init return element as copy of initIndex list element
		if (geoList.size() > initIndex) {
			// create copy of initIndex GeoElement in list
			element = getGenericElement(geoList, initIndex).copyInternal(cons);
		}

		// if not enough elements in list:
		// init return element as copy of first list element
		else if (geoList.size() > 0) {
			// create copy of first GeoElement in list

			element = getGenericElement(geoList, 0).copyInternal(cons);
		}

		// desperate case: empty list
		else if (geoList.getTypeStringForXML() != null) {
			// if the list was non-empty at some point before saving, get the
			// same type of geo
			// saved in XML from 4.1.131.0
			element = kernel.createGeoElement(cons,
					geoList.getTypeStringForXML());
		}

		// desperate case: empty list
		else {
			// saved in XML from 4.0.18.0
			element = cons.getOutputGeo();
		}

		if (element.isGeoPolygon()) { // ensure type will not be sticked to e.g.
										// "triangle"
			((GeoPolygon) element).setNotFixedPointsLength(true);
		}

		setInputOutput();
		compute();
	}

	private static GeoElement getGenericElement(GeoList geoList, int index) {
		GeoElement toCopy = geoList.get(index);
		if (geoList.getElementType() == GeoClass.DEFAULT &&
		// we have list {2,x}, not eg Factors[2x]
				(geoList.getParentAlgorithm() == null || geoList
						.getParentAlgorithm() instanceof AlgoDependentList)
				// for {a,x} also return number a, not function
				&& !Inspecting.dynamicGeosFinder.check(toCopy)) {
			for (int i = 0; i < geoList.size(); i++) {
				if (Test.canSet(geoList.get(i), toCopy)) {
					toCopy = geoList.get(i);
				}
			}
		}
		return toCopy;
	}

	/**
	 * Creates new unlabeled element algo
	 */
	public AlgoListElement(Construction cons, String label, GeoList geoList,
			NumberValue[] num2) {
		this(cons, geoList, num2);
		element.setLabel(label);
	}

	public AlgoListElement(Construction cons, GeoList geoList,
			NumberValue[] num2) {
		super(cons);
		this.geoList = geoList;
		this.num2 = num2;

		element = null;
		GeoElement current = geoList;
		int k = 0;
		try {
			do {
				int initIndex = Math.max(0,
						(int) Math.round(num2[k].getDouble()) - 1);
				// init return element as copy of initIndex list element
				if (((GeoList) current).size() > initIndex) {
					// create copy of initIndex GeoElement in list
					current = k == num2.length - 1 ? getGenericElement(
							(GeoList) current, initIndex) : ((GeoList) current)
							.get(initIndex);
				}

				// if not enough elements in list:
				// init return element as copy of first list element
				else if (geoList.size() > 0) {
					// create copy of first GeoElement in list
					current = k == num2.length - 1 ? getGenericElement(
							(GeoList) current, 0) : ((GeoList) current).get(0);
				}
				k++;
			} while (current.isGeoList() && k < num2.length);
			element = current.copyInternal(cons);
		} catch (Exception e) {
			App.debug("error initialising list");
		}

		// desperate case: empty list, or malformed 2D array
		if (element == null) {
			// saved in XML from 4.0.18.0
			element = cons.getOutputGeo();
		}
		setInputOutput();
		compute();

	}

	@Override
	public Commands getClassName() {
		return Commands.Element;
	}

	@Override
	protected void setInputOutput() {

		if (num2 == null) {
			input = new GeoElement[2];
			input[0] = geoList;
			input[1] = numGeo;
		} else {
			input = new GeoElement[num2.length + 1];
			input[0] = geoList;
			for (int i = 0; i < num2.length; i++) {
				input[i + 1] = num2[i].toGeoElement();
			}
		}

		setOutputLength(1);
		setOutput(0, element);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns chosen element
	 * 
	 * @return chosen element
	 */
	public GeoElement getElement() {
		return element;
	}

	@Override
	public final void compute() {
		if ((numGeo != null && !numGeo.isDefined()) || !geoList.isDefined()) {
			element.setUndefined();
			return;
		}

		if (num2 == null) {
			// index of wanted element
			int n = (int) Math.round(num.getDouble()) - 1;
			if (n >= 0 && n < geoList.size()) {
				GeoElement nth = geoList.get(n);
				setElement(nth);
			} else {
				element.setUndefined();
			}

		} else {

			for (int k = 0; k < num2.length; k++)
				if (!num2[k].toGeoElement().isDefined()) {
					element.setUndefined();
					return;
				}

			int m = (int) Math.round(num2[num2.length - 1].getDouble()) - 1;
			GeoElement current = geoList;
			for (int k = 0; k < num2.length - 1; k++) {
				int index = (int) Math.round(num2[k].getDouble() - 1);
				if (index >= 0 && current.isGeoList()
						&& index < ((GeoList) current).size())
					current = ((GeoList) current).get(index);
				else {
					element.setUndefined();
					return;
				}
			}

			GeoList list = ((GeoList) current);

			if (m >= 0 && m < list.size())
				current = list.get(m);
			else {
				element.setUndefined();
				return;
			}

			setElement(current);

		}
	}

	private void setElement(GeoElement nth) {
		// check type:
		if (nth.getGeoClassType() == element.getGeoClassType()
				|| Test.canSet(element, nth)) {
			element.set(nth);
			if (nth.getDrawAlgorithm() instanceof DrawInformationAlgo)
				element.setDrawAlgorithm(((DrawInformationAlgo) nth
						.getDrawAlgorithm()).copy());

		} else {
			element.setUndefined();
		}

	}

	/*
	 * @Override public String getCommandDescription(StringTemplate tpl,boolean
	 * real) {
	 * 
	 * return super.getCommandDescription(tpl,real);
	 * 
	 * TODO re enable this for shortSyntax flag true for 5.0 sb.setLength(0);
	 * 
	 * 
	 * int length = input.length;
	 * 
	 * sb.append(geoList.getLabel()+"("); // input
	 * sb.append(real?input[1].getRealLabel():input[1].getLabel()); // Michael
	 * Borcherds 2008-05-15 added input.length>0 for Step[] for (int i = 2; i <
	 * length; ++i) { sb.append(", "); sb.append(real?
	 * input[i].getRealLabel():input[i].getLabel()); } sb.append(")"); return
	 * sb.toString();
	 * 
	 * }
	 */

	// TODO Consider locusequability

}

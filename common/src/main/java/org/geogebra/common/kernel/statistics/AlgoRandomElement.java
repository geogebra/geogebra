/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * random element of a GeoList object.
 * 
 * Note: the type of the returned GeoElement object is determined by the type of
 * the first list element. If the list is initially empty, a GeoNumeric object
 * is created for element.
 * 
 * @author Michael
 * @version 2010-06-01
 */

public class AlgoRandomElement extends AlgoElement {

	private GeoList geoList; // input
	private GeoElement element; // output

	/**
	 * Creates new random element algo
	 * 
	 * @param cons
	 * @param label
	 * @param geoList
	 */
	public AlgoRandomElement(Construction cons, String label, GeoList geoList) {
		super(cons);
		this.geoList = geoList;

		// init return element as copy of first list element
		if (geoList.size() > 0) {
			element = geoList.get(0).copyInternal(cons);
		} else if (geoList.getTypeStringForXML() != null) {
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

		setInputOutput();
		compute();
		element.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.RandomElement;
	}

	@Override
	protected void setInputOutput() {

		input = new GeoElement[1];
		input[0] = geoList;

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
		if (!geoList.isDefined() || geoList.size() == 0) {
			element.setUndefined();
			return;
		}

		GeoElement randElement = geoList.get((int) Math.floor((cons
				.getApplication().getRandomNumber() * geoList.size())));

		// check type:
		if (randElement.getGeoClassType() == element.getGeoClassType()) {
			element.set(randElement);
		} else {
			element.setUndefined();
		}
	}

	// TODO Consider locusequability

}

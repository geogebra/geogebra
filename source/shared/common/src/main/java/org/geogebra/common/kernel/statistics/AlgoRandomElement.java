/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.SetRandomValue;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoElementND;

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

public class AlgoRandomElement extends AlgoElement implements SetRandomValue {

	private GeoList geoList; // input
	private GeoElement element; // output

	/**
	 * Creates new random element algo
	 * 
	 * @param cons
	 *            construction
	 * @param geoList
	 *            list to pick from
	 */
	public AlgoRandomElement(Construction cons, GeoList geoList) {
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
		cons.addRandomGeo(element);
	}

	@Override
	public Commands getClassName() {
		return Commands.RandomElement;
	}

	@Override
	protected void setInputOutput() {

		input = new GeoElement[1];
		input[0] = geoList;

		setOnlyOutput(element);
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

		GeoElement randElement = geoList.get((int) Math.floor(
				cons.getApplication().getRandomNumber() * geoList.size()));
		// check type:
		if (randElement.getGeoClassType() == element.getGeoClassType()) {
			element.set(randElement);
		} else {
			element.setUndefined();
		}
	}

	@Override
	public boolean setRandomValue(GeoElementND rnd) {
		for (int i = 0; i < geoList.size(); i++) {
			if (geoList.get(i).isEqual(rnd)) {
				element.set(geoList.get(i));
				return true;
			}
		}
		return false;
	}

}

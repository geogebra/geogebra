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

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Selected element of a GeoList object.
 * 
 * Note: the type of the returned GeoElement object is determined by the type of
 * the first list element. If the list is initially empty, a GeoNumeric object
 * is created for element.
 * 
 */

public class AlgoSelectedElement extends AlgoElement {

	private GeoList geoList; // input
	private GeoElement element; // output

	/**
	 * Creates new selected element algo
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param geoList
	 *            list
	 */
	public AlgoSelectedElement(Construction cons, String label,
			GeoList geoList) {
		super(cons);
		this.geoList = geoList;

		// init return element as copy of first list element
		if (geoList.size() > 0) {
			// create copy of first GeoElement in list
			element = geoList.get(0).copyInternal(cons);
		}

		// desperate case: empty list
		else {
			element = new GeoNumeric(cons);
		}

		setInputOutput();
		compute();
		element.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.SelectedElement;
	}

	@Override
	protected void setInputOutput() {

		input = new GeoElement[1];
		input[0] = geoList;

		setOnlyOutput(element);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the selected element
	 * 
	 * @return selected element
	 */
	public GeoElement getElement() {
		return element;
	}

	@Override
	public final void compute() {
		if (!geoList.isDefined()) {
			element.setUndefined();
			return;
		}

		GeoElement geo = geoList.getSelectedElement();
		// check type:
		if (geo != null && geo.getGeoClassType() == element.getGeoClassType()) {
			element.set(geo);

		} else {
			element.setUndefined();
		}
	}

}

/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

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

public class AlgoSelectedIndex extends AlgoElement {

	private GeoList geoList; // input
	private GeoNumeric index; // output

	/**
	 * Creates new selected index algo
	 * 
	 * @param cons
	 * @param label
	 * @param geoList
	 */
	public AlgoSelectedIndex(Construction cons, String label, GeoList geoList) {
		super(cons);
		this.geoList = geoList;

		index = new GeoNumeric(cons);

		setInputOutput();
		compute();
		index.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.SelectedIndex;
	}

	@Override
	protected void setInputOutput() {

		input = new GeoElement[1];
		input[0] = geoList;

		setOutputLength(1);
		setOutput(0, index);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the selected index
	 * 
	 * @return the selected index
	 */
	public GeoElement getElement() {
		return index;
	}

	@Override
	public final void compute() {
		if (!geoList.isDefined()) {
			index.setUndefined();
			return;
		}

		index.setValue(geoList.getSelectedIndex() + 1);
	}

	

}

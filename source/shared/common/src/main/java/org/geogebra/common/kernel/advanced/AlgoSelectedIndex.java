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
 * Selected index of a GeoList object.
 * 
 */

public class AlgoSelectedIndex extends AlgoElement {

	private GeoList geoList; // input
	private GeoNumeric index; // output

	/**
	 * Creates new selected index algo
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param geoList
	 *            list
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

		setOnlyOutput(index);
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

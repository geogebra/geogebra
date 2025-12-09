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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Sort a list. Adapted from AlgoMax and AlgoIterationList
 * 
 * @author Michael Borcherds
 * @version 04-01-2008
 */

public class AlgoPointList extends AlgoElement {

	private GeoList inputList; // input
	private GeoList outputList; // output
	private int size;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputList
	 *            list of lists of numbers
	 */
	public AlgoPointList(Construction cons, String label, GeoList inputList) {
		super(cons);
		this.inputList = inputList;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.PointList;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputList;

		setOnlyOutput(outputList);
		setDependencies(); // done by AlgoElement
	}

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

		// copy the sorted treeset back into a list
		outputList.setDefined(true);
		outputList.clear();

		boolean suppressLabelCreation = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		for (int i = 0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isGeoList()) {
				GeoList list = (GeoList) geo;
				if (list.size() == 2) {
					GeoElement geoX = list.get(0);
					GeoElement geoY = list.get(1);
					if (geoX.isGeoNumeric() && geoY.isGeoNumeric()) {
						outputList.addPoint(((GeoNumeric) geoX).getDouble(),
								((GeoNumeric) geoY).getDouble(), 1.0, this);
					}
				}

			}

		}
		cons.setSuppressLabelCreation(suppressLabelCreation);
	}

}

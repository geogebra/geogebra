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

public class AlgoInsert extends AlgoElement {

	private GeoElement inputGeo; // input
	private GeoList inputList; // input
	private GeoNumeric n; // input
	private GeoList outputList; // output
	private int size;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputGeo
	 *            geo to insert
	 * @param inputList
	 *            list
	 * @param n
	 *            insert position
	 */
	public AlgoInsert(Construction cons, String label, GeoElement inputGeo,
			GeoList inputList, GeoNumeric n) {
		super(cons);

		this.inputGeo = inputGeo;
		this.inputList = inputList;
		this.n = n;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Insert;
	}

	@Override
	protected void setInputOutput() {

		// make sure that x(Element[list,1]) will work even if the output list's
		// length is zero
		outputList.setTypeStringForXML(inputList.getTypeStringForXML());

		input = new GeoElement[3];

		input[0] = inputGeo;
		input[1] = inputList;
		input[2] = n;

		setOnlyOutput(outputList);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return outputList;
	}

	@Override
	public final void compute() {

		// size = inputGeo.size();
		size = inputList.size();

		int insertPoint = (int) n.getDouble();

		// -1 means insert in last place, -2 means penultimate etc
		if (insertPoint < 0) {
			insertPoint = size + insertPoint + 2;
		}

		if (!inputGeo.isDefined() || !inputList.isDefined() || insertPoint <= 0
				|| insertPoint > size + 1) {
			outputList.setUndefined();
			return;
		}

		outputList.setDefined(true);
		outputList.clear();

		if (insertPoint > 1) {
			for (int i = 0; i < insertPoint - 1; i++) {
				outputList.add(inputList.get(i).copyInternal(cons));
			}
		}

		if (inputGeo.isGeoList()) {
			GeoList list = (GeoList) inputGeo;

			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					outputList.add(list.get(i).copyInternal(cons));
				}
			}
		} else {
			outputList.add(inputGeo.copyInternal(cons));
		}

		if (insertPoint <= size) {
			for (int i = insertPoint - 1; i < size; i++) {
				outputList.add(inputList.get(i).copyInternal(cons));
			}
		}
	}

}

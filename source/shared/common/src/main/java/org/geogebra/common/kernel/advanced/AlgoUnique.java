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

import java.util.Iterator;

import org.apache.commons.math3.stat.Frequency;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.statistics.FrequencyGgb;
import org.geogebra.common.plugin.GeoClass;

/**
 * Removes duplicate entries from list
 */
public class AlgoUnique extends AlgoElement {

	private GeoList dataList; // input
	private GeoList uniqueList; // output

	private Frequency f;
	private GeoClass lastElementType;

	/**
	 * @param cons
	 *            construction
	 * @param dataList
	 *            data
	 */
	public AlgoUnique(Construction cons, GeoList dataList) {
		super(cons);
		this.dataList = dataList;

		uniqueList = new GeoList(cons);

		setInputOutput();
		compute();

	}

	@Override
	public Commands getClassName() {
		return Commands.Unique;
	}

	@Override
	protected void setInputOutput() {

		// make sure that x(Element[list,1]) will work even if the output list's
		// length is zero
		uniqueList.setTypeStringForXML(dataList.getTypeStringForXML());

		input = new GeoElement[1];
		input[0] = dataList;

		setOnlyOutput(uniqueList);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return filtered list
	 */
	public GeoList getResult() {
		return uniqueList;
	}

	@Override
	public final void compute() {

		// Validate input arguments
		if (!dataList.isDefined() || dataList.size() == 0) {
			uniqueList.setUndefined();
			return;
		}

		uniqueList.setDefined(true);
		uniqueList.clear();

		GeoClass elementType = dataList.getElementType();
		if (!(elementType.equals(GeoClass.TEXT)
				|| elementType.equals(GeoClass.NUMERIC))) {
			for (int i = 0; i < dataList.size(); i++) {
				AlgoUnion.addToOutputList(uniqueList, dataList.get(i));
			}
			return;
		}

		// Load the data into f, an instance of Frequency class
		if (f == null || elementType != lastElementType) {
			f = elementType == GeoClass.TEXT ? new Frequency() : new FrequencyGgb();
			lastElementType = elementType;
		}
		f.clear();
		for (int i = 0; i < dataList.size(); i++) {
			if (elementType.equals(GeoClass.TEXT)) {
				f.addValue(dataList.get(i)
						.toValueString(StringTemplate.defaultTemplate));
			}
			if (elementType.equals(GeoClass.NUMERIC)) {
				f.addValue(
						((GeoNumeric) dataList.get(i)).getDouble());
			}
		}

		// Get the unique value list
		if (elementType.equals(GeoClass.TEXT)) {
			// handle string data
			Iterator<Comparable<?>> itr = f.valuesIterator();
			while (itr.hasNext()) {
				String s = (String) itr.next();
				GeoText text = new GeoText(cons);
				text.setTextString(s);
				uniqueList.add(text);
			}
		} else {
			// handle numeric data
			Iterator<Comparable<?>> itr = f.valuesIterator();
			while (itr.hasNext()) {
				Double n = (Double) itr.next();
				uniqueList.addNumber(n, this);
			}
		}
	}

}

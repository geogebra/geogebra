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
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * For algos that use AlgoFrequency to sort raw data into a unique value list
 * and frequency list.
 * 
 * AlgoFrequency.getValue() gives the unique value list
 * 
 * AlgoFrequency.getResult() gives the frequency list
 * 
 * @author mathieu
 * 
 */
public abstract class AlgoUsingUniqueAndFrequency extends AlgoElement {

	// helper algos
	protected AlgoFrequency algoFreq;

	/**
	 * Creates new algorithm
	 * 
	 * @param c
	 *            construction
	 */
	public AlgoUsingUniqueAndFrequency(Construction c) {
		super(c);
	}

	/**
	 * Creates new algorithm
	 * 
	 * @param c
	 *            construction
	 * @param addToConstructionList
	 *            true to add this to construction list
	 */
	public AlgoUsingUniqueAndFrequency(Construction c,
			boolean addToConstructionList) {
		super(c, addToConstructionList);
	}

	/**
	 * create helper algos about the list
	 * 
	 * @param list1
	 *            list
	 */
	protected void createHelperAlgos(GeoList list1) {
		createHelperAlgos(list1, null);
	}

	/**
	 * create helper algos about the list with scaled freq
	 * 
	 * @param list1
	 *            list
	 * @param scale
	 *            scale factor
	 */
	protected void createHelperAlgos(GeoList list1, GeoNumeric scale) {
		algoFreq = new AlgoFrequency(cons, null, null, list1, scale);
		cons.removeFromConstructionList(algoFreq);
	}

	/**
	 * remove helper algos
	 */
	protected void removeHelperAlgos() {
		if (algoFreq != null) {
			algoFreq.remove();
		}
	}

}

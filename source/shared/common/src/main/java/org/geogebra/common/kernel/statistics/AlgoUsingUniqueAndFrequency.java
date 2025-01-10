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

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.advanced.AlgoUnique;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;

/**
 * for algos using AlgoUnique and AlgoFrequency to sort raw datas
 * @author mathieu
 *
 */
public abstract class AlgoUsingUniqueAndFrequency extends AlgoElement {

	// helper algos
	protected AlgoUnique algoUnique;
	protected AlgoFrequency algoFreq;

	/**
	 * Creates new algorithm
	 * @param c construction
	 */
	public AlgoUsingUniqueAndFrequency(Construction c) {
		super(c);
	}
	
	/**
	 * Creates new algorithm
	 * @param c construction
	 * @param addToConstructionList true to add this to construction list
	 */
	public AlgoUsingUniqueAndFrequency(Construction c, boolean addToConstructionList) {
		super(c, addToConstructionList);
	}
	
	/**
	 * create helper algos about the list
	 * @param list1 list
	 */
	protected void createHelperAlgos(GeoList list1){
		createHelperAlgos(list1, null);
	}
	
	/**
	 * create helper algos about the list
	 * with scaled freq
	 * @param list1 list
	 * @param scale scale factor
	 */
	protected void createHelperAlgos(GeoList list1, GeoNumeric scale){
		algoUnique = new AlgoUnique(cons, list1);
		algoFreq = new AlgoFrequency(cons, null, null, list1, scale);
		cons.removeFromConstructionList(algoUnique);
		cons.removeFromConstructionList(algoFreq);
	}
	
	
	/**
	 * remove helper algos
	 */
	protected void removeHelperAlgos(){
		if (algoFreq != null) {
			algoFreq.remove();
		}
		if (algoUnique != null) {
			algoUnique.remove();
		}
	}

}

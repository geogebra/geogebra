package geogebra.common.kernel.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.statistics.AlgoCellRange;

/**
 * @author mathieu
 * GeoList for AlgoCellRange. We need to propagate add to addToUpdateSets() to parent algo input items
 *
 */
public class GeoListForCellRange extends GeoList {
	
	private AlgoCellRange algo;

	/**
	 * constructor
	 * @param c construction
	 * @param algo parent algo
	 */
	public GeoListForCellRange(Construction c, AlgoCellRange algo) {
		super(c);
		this.algo = algo;
	}
	
	@Override
	public void addToUpdateSets(final AlgoElement algorithm) {
		
		super.addToUpdateSets(algorithm);
		// propagate to algo parent input items
		algo.addToItemsAlgoUpdateSets(algorithm);
	}
	

}

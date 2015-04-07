package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.statistics.AlgoCellRange;

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
	public boolean addToUpdateSets(final AlgoElement algorithm) {
		
		final boolean added = super.addToUpdateSets(algorithm);
		
		// propagate to algo parent input items
		algo.addToItemsAlgoUpdateSets(algorithm);
		
		return added;
	}
	

}

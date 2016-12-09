package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;

/**
 * Common algo for Intersect
 * 
 * + used for trimmed intersections
 * 
 * + used for finding pre-existent intersect algos
 */
public abstract class AlgoIntersectAbstract extends AlgoElement {

	/**
	 * @param cons
	 *            construction
	 */
	public AlgoIntersectAbstract(Construction cons) {
		super(cons);
	}

	/**
	 * @param cons
	 *            construction
	 * @param addToConstructionList
	 *            whether to add this to XML
	 */
	public AlgoIntersectAbstract(Construction cons,
			boolean addToConstructionList) {
		super(cons, addToConstructionList);
	}

	/*
	 * This should apply to every subclass. In case it does not, a case per case
	 * should be used. Luckily, some are already implemented.
	 */

}

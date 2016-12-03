package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;

public abstract class AlgoIntersectAbstract extends AlgoElement {

	public AlgoIntersectAbstract(Construction cons) {
		super(cons);
	}

	public AlgoIntersectAbstract(Construction cons,
			boolean addToConstructionList) {
		super(cons, addToConstructionList);
	}

	/*
	 * This should apply to every subclass. In case it does not, a case per case
	 * should be used. Luckily, some are already implemented.
	 */

	

}

package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
/**
 * Abstract class for algos that use some CAS algo as helper
 */
public abstract class AlgoUsingTempCASalgo extends AlgoElement {

	/**
	 * @param c construction
	 */
	public AlgoUsingTempCASalgo(Construction c) {
		super(c);
	}

	/**
	 * @param c construction
	 * @param addToConstructionList whether we want this in construction list
	 */
	public AlgoUsingTempCASalgo(Construction c, boolean addToConstructionList) {
		super(c, addToConstructionList);
	}

	/**
	 * CAS algo helper
	 */
	protected AlgoElement algoCAS;

	@Override
	public void remove() {
		if(removed)
			return;
		super.remove();
		if (algoCAS != null)
			algoCAS.remove();
	}

}

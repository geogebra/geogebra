package geogebra.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;

public abstract class AlgoUsingTempCASalgo extends AlgoElement {

	public AlgoUsingTempCASalgo(Construction c) {
		super(c);		
	}

	public AlgoUsingTempCASalgo(Construction c, boolean addToConstructionList) {
		super(c, addToConstructionList);		
	}

	protected AlgoElement algoCAS;	

	public void remove() {  
    	super.remove();  
    	if (algoCAS != null)
    		algoCAS.remove();
    }

}

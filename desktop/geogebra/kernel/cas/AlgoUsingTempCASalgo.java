package geogebra.kernel.cas;

import geogebra.kernel.Construction;
import geogebra.kernel.algos.AlgoElement;

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

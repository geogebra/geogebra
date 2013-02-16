package geogebra.common.cas.mpreduce;

import geogebra.common.main.App;

/**
 * This enum represents the Reduce packages that are not loaded when Reduce starts, but on demand.
 * Each package remembers whether it was already loaded.
 *  
 * @author zbynek
 *
 */
public enum ReducePackage {
	DEFINT,ODESOLVE,GROEBNER,TAYLOR;
	private boolean loaded = false;
	
	public void load(CASmpreduce cas){
		if(loaded || !cas.isInitialized())
			return;
		try {
			loaded = true;
			App.debug("loading"+name());
			cas.evaluateRaw("load_package "+name().toLowerCase());
			App.debug("done");
		} catch (Throwable e) {
			App.error("Could not load "+name());
		}
		
	}
}

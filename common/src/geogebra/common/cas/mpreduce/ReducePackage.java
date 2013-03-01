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
	/** loads defint (definite integral) package */
	DEFINT,
	/** loads odesolve package (solveode, newarbconst) */
	ODESOLVE,
	/** loads groebner package (groebner cmd, prover) */
	GROEBNER,
	/** loads taylor package */
	TAYLOR;
	private boolean loaded = false;
	
	/**
	 * Load the package in given CAS
	 * @param cas cas
	 */
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

package org.rosuda.REngine;
// environments are like REXPReferences except that they cannot be resolved

/** REXPEnvironment represents an environment in R. Very much like {@link org.rosuda.REngine.REXPReference} this is a proxy object to the actual object on the R side. It provides methods for accessing the content of the environment. The actual implementation may vary by the back-end used and not all engines support environments. Check {@link org.rosuda.REngine.REngine.supportsEnvironments()} for the given engine. Environments are specific for a given engine, they cannot be passed across engines.
 */
public class REXPEnvironment extends REXP {
	/** engine associated with this environment */
	REngine eng;
	/** transparent handle that can be used by the engine to indentify the environment. It is not used by REngine API itself. */
	Object handle;
	
	/** create a new environemnt reference - this constructor should never be used directly, use {@link REngine.newEnvironment()} instead.
	 *  @param eng engine responsible for this environment
	 *  @param handle handle used by the engine to identify this environment
	 */
	public REXPEnvironment(REngine eng, Object handle) {
		super();
		this.eng = eng;
		this.handle = handle;
	}
	
	public boolean isEnvironment() { return true; }
	
	/** returns the handle used to identify this environemnt in the engine - for internal use by engine implementations only
	 *  @return handle of this environment */
	public Object getHandle() { return handle; }
	
	/** get a value from this environment
	 *  @param name name of the value
	 *  @param resolve if <code>false</code> returns a reference to the object, if <code>false</code> the reference is resolved
	 *  @return value corresponding to the symbol name or possibly <code>null</code> if the value is unbound (the latter is currently engine-specific) */
	public REXP get(String name, boolean resolve) throws REngineException {
		try {
			return eng.get(name, this, resolve);
		} catch (REXPMismatchException e) { // this should never happen because this is always guaranteed to be REXPEnv
			throw(new REngineException(eng, "REXPMismatchException:"+e+" in get()"));
		}
	}

	/** get a value from this environment - equavalent to <code>get(name, true)</code>.
	 *  @param name name of the value
	 *  @return value (see {@link #get(String,boolean)}) */
	public REXP get(String name) throws REngineException {
		return get(name, true);
	}
	
	/** assigns a value to a given symbol name
	 *  @param name symbol name
	 *  @param value value */
	public void assign(String name, REXP value) throws REngineException, REXPMismatchException {
		eng.assign(name, value, this);
	}
	
	/** returns the parent environment or a reference to it
	 *  @param resolve if <code>true</code> returns the environemnt, otherwise a reference. 
	 *  @return parent environemnt (or a reference to it) */
	public REXP parent(boolean resolve) throws REngineException {
		try {
			return eng.getParentEnvironment(this, resolve);
		} catch (REXPMismatchException e) { // this should never happen because this is always guaranteed to be REXPEnv
			throw(new REngineException(eng, "REXPMismatchException:"+e+" in parent()"));
		}
	}

	/** returns the parent environment. This is equivalent to <code>parent(true)</code>.
	 *  @return parent environemnt */
	public REXPEnvironment parent() throws REngineException {
		return (REXPEnvironment) parent(true);
	}
}

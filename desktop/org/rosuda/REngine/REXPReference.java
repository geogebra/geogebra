package org.rosuda.REngine;

/** this class represents a reference (proxy) to an R object.
 <p>
 The reference semantics works by calling {@link #resolve()} (which in turn uses {@link REngine#resolveReference(REXP)} on itself) whenever any methods are accessed. The implementation is not finalized yat and may change as we approach the JRI interface which is more ameanable to reference-style access. Subclasses are free to implement more efficient implementations. */
public class REXPReference extends REXP {
	/** engine which will be used to resolve the reference */
	protected REngine eng;
	/** an opaque (optional) handle */
	protected Object handle;
	/** resolved (cached) object */
	protected REXP resolvedValue;

	/** create an external REXP reference using given engine and handle. The handle value is just an (optional) identifier not used by the implementation directly. */
	public REXPReference(REngine eng, Object handle) {
		super();
		this.eng = eng;
		this.handle = handle;
	}

	/** shortcut for <code>REXPReference(eng, new Long(handle))</code> that is used by native code */
	REXPReference(REngine eng, long handle) {
		this(eng, new Long(handle));
	}

	/** resolve the external REXP reference into an actual REXP object. In addition, the value (if not <code>null</code>) will be cached for subsequent calls to <code>resolve</code> until <code>invalidate</code> is called. */
	public REXP resolve() {
		if (resolvedValue != null)
			return resolvedValue;
		try {
			resolvedValue = eng.resolveReference(this);
			return resolvedValue;
		} catch (REXPMismatchException me) {
			// this should never happen since we are REXPReference
		} catch(REngineException ee) {
			// FIXME: what to we do?
		}
		return null;
	}

	/** invalidates any cached representation of the reference */
	public void invalidate() {
		resolvedValue = null;
	}
	
	/** finalization that notifies the engine when a reference gets collected */
	protected void finalize() throws Throwable {
		try {
			eng.finalizeReference(this);
		} finally {
			super.finalize();
		}
	}	
	// type checks
	public boolean isString() { return resolve().isString(); }
	public boolean isNumeric() { return resolve().isNumeric(); }
	public boolean isInteger() { return resolve().isInteger(); }
	public boolean isNull() { return resolve().isNull(); }
	public boolean isFactor() { return resolve().isFactor(); }
	public boolean isList() { return resolve().isList(); }
	public boolean isLogical() { return resolve().isLogical(); }
	public boolean isEnvironment() { return resolve().isEnvironment(); }
	public boolean isLanguage() { return resolve().isLanguage(); }
	public boolean isSymbol() { return resolve().isSymbol(); }
	public boolean isVector() { return resolve().isVector(); }
	public boolean isRaw() { return resolve().isRaw(); }
	public boolean isComplex() { return resolve().isComplex(); }
	public boolean isRecursive() { return resolve().isRecursive(); }
	public boolean isReference() { return true; }

	// basic accessor methods
	public String[] asStrings() throws REXPMismatchException { return resolve().asStrings(); }
	public int[] asIntegers() throws REXPMismatchException { return resolve().asIntegers(); }
	public double[] asDoubles() throws REXPMismatchException { return resolve().asDoubles(); }
	public RList asList() throws REXPMismatchException { return resolve().asList(); }
	public RFactor asFactor() throws REXPMismatchException { return resolve().asFactor(); }

	public int length() throws REXPMismatchException { return resolve().length(); }

	public REXPList _attr() { return resolve()._attr(); }
	
	public Object getHandle() { return handle; }
	
	public REngine getEngine() { return eng; }

	public String toString() {
		return super.toString()+"{eng="+eng+",h="+handle+"}";
	}
}

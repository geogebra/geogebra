package org.rosuda.REngine;

import java.lang.reflect.Method;

/** REngine is an abstract base class for all implementations of R engines. Subclasses can implement interfaces to R in many different ways.
 Clients should always use methods this class instead of its direct subclasses in order to maintian compatibility with any R engine implementation.
 The canonical way of obtaining a new engine is to call {@link #engineForClass}. All subclasses must implement <code>createEngine()</code> method. */
public abstract class REngine {
    /** last created engine or <code>null</code> if there is none */
    protected static REngine lastEngine = null;
	
    /** this is the designated constructor for REngine classes. It uses reflection to call createEngine method on the given REngine class.
	@param klass fully qualified class-name of a REngine implementation
	@return REngine implementation or <code>null</code> if <code>createEngine</code> invokation failed */
    public static REngine engineForClass(String klass) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
		Class cl=Class.forName(klass);
		if (cl==null) throw(new ClassNotFoundException("can't find engine class "+klass));
		Method m=cl.getMethod("createEngine",(Class[])null);
		Object o=m.invoke(null,(Object[])null);
		return lastEngine=(REngine)o;
    }

	/** This is the extended constructor for REngine classes. It uses reflection to call createEngine method on the given REngine class with some additional control over the engine. Note that not all engines may support the extended version.
	 @param klass fully qualified class-name of a REngine implementation
	 @param args arguments to pass to R for initialization
	 @param callbacks delegate for REngine callbacks or <code>null</code> if callbacks won't be serviced (engine may not support callbacks)
	 @param runREPL if <code>true</code> then REPL will be started (if supported by the engine)
	 @return REngine implementation or <code>null</code> if <code>createEngine</code> invokation failed */
	public static REngine engineForClass(String klass, String[] args, REngineCallbacks callbacks, boolean runREPL) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
		Class cl = Class.forName(klass);
		if (cl == null) throw new ClassNotFoundException("can't find engine class " + klass);
		Method m = cl.getMethod("createEngine", new Class[] { String[].class, REngineCallbacks.class, Boolean.TYPE });
		Object o = m.invoke(null, new Object[] { args, callbacks, new Boolean(runREPL) });
		return lastEngine = (REngine)o;
	}
	
    /** retrieve the last created engine
		@return last created engine or <code>null</code> if no engine was created yet */
    public static REngine getLastEngine() {
		return lastEngine;
    }

	/** parse a string into an expression vector
		@param text string to parse
		@param resolve resolve the resulting REXP (<code>true</code>) or just return a reference (<code>false</code>)
	    @return parsed expression */
	public abstract REXP parse(String text, boolean resolve) throws REngineException;

	/** evaluate an expression vector
		@param what an expression (or vector of such) to evaluate
		@param where environment to evaluate in (use <code>null</code> for the global environemnt and/or if environments are not supported by the engine)
		@param resolve resolve the resulting REXP or just return a reference
		@return the result of the evaluation of the last expression */
    public abstract REXP eval(REXP what, REXP where, boolean resolve) throws REngineException, REXPMismatchException;

	/** assign into an environment
		@param symbol symbol name
		@param value value to assign
		@param env environment to assign to (use <code>null</code> for the global environemnt and/or if environments are not supported by the engine) */
    public abstract void assign(String symbol, REXP value, REXP env) throws REngineException, REXPMismatchException;

	/** get a value from an environment
		@param symbol symbol name
		@param env environment (use <code>null</code> for the global environemnt and/or if environments are not supported by the engine)
		@param resolve resolve the resulting REXP or just return a reference		
		@return value */
    public abstract REXP get(String symbol, REXP env, boolean resolve) throws REngineException, REXPMismatchException;

	/** fetch the contents of the given reference. The resulting REXP may never be REXPReference. The engine should raise a {@link #REngineException} exception if {@link #supportsReferences()} returns <code>false</code>.
		@param ref reference to resolve
		@return resolved reference */
	public abstract REXP resolveReference(REXP ref) throws REngineException, REXPMismatchException;

	/** create a reference by pushing local data to R and returning a reference to the data. If ref is a reference it is returned as-is. The engine should raise a {@link #REngineException} exception if {@link #supportsReferences()} returns <code>false</code>.
	 @param value to create reference to
	 @return reference to the value */
	public abstract REXP createReference(REXP value) throws REngineException, REXPMismatchException;

	/** removes reference from the R side. This method is called automatically by the finalizer of <code>REXPReference</code> and should never be called directly.
	 @param ref reference to finalize */
	public abstract void finalizeReference(REXP ref) throws REngineException, REXPMismatchException;
	
	/** get the parent environemnt of an environemnt
	 @param env environment to query
	 @param resolve whether to resolve the resulting environment reference
	 @return parent environemnt of env */
	public abstract REXP getParentEnvironment(REXP env, boolean resolve) throws REngineException, REXPMismatchException;
	
	/** create a new environemnt
	 @param parent parent environment
	 @param resolve whether to resolve the reference to the environemnt (usually <code>false</code> since the returned environment will be empty)
	 @return resulting environment */
	public abstract REXP newEnvironment(REXP parent, boolean resolve) throws REngineException, REXPMismatchException;
	
    /** convenince method equivalent to <code>eval(parse(text, false), where, resolve);</code>
	 @param text to parse (see {@link #parse})
	 @param where environment to evaluate in (see {@link #eval})
	 @param resolve whether to resolve the resulting reference or not (see {@link #eval})
	 @return result */
	public REXP parseAndEval(String text, REXP where, boolean resolve) throws REngineException, REXPMismatchException {
		REXP p = parse(text, false);
		return eval(p, where, resolve);
	}

    /** convenince method equivalent to <code>eval(parse(cmd, false), null, true);</code>
	 @param cmd expression to parse (see {@link #parse})
	 @return result */
    public REXP parseAndEval(String cmd) throws REngineException, REXPMismatchException { return parseAndEval(cmd, null, true); };
	
	/** performs a close operation on engines that support it. The engine may not be used after <code>close()</code> returned <code>true</code>. This operation is optional and will always return <code>false</code> if not implemented.
	 @return <code>true</code> if the close opetaion was successful, <code>false</code> otherwise. */
	public boolean close() { return false; }
	
	//--- capabilities ---
	/** check whether this engine supports references to R objects
	 @return <code>true</code> if this engine supports references, <code>false/code> otherwise */
	public boolean supportsReferences() { return false; }
	/** check whether this engine supports handing of environments (if not, {@link #eval} and {@link #assign} only support the global environment denoted by <code>null</code>).
	 @return <code>true</code> if this engine supports environments, <code>false/code> otherwise */
	public boolean supportsEnvironments() { return false; }
	/** check whether this engine supports REPL (Read-Evaluate-Print-Loop) and corresponding callbacks.
	 @return <code>true</code> if this engine supports REPL, <code>false/code> otherwise */
	public boolean supportsREPL() { return false; }
	/** check whether this engine supports locking ({@link #lock}, {@link #tryLock} and {@link #unlock}).
	 @return <code>true</code> if this engine supports REPL, <code>false/code> otherwise */
	public boolean supportsLocking() { return false; }

	//--- convenience methods --- (the REXPMismatchException catches should be no-ops since the value type is guaranteed in the call to assign)
	/** convenience method equivalent to <code>assign(symbol, new REXPDouble(d), null)</code> (see {@link #assign(String, REXP, REXP)})
	 @param symbol symbol name to assign to
	 @param d values to assign */
	public void assign(String symbol, double[] d) throws REngineException { try { assign(symbol, new REXPDouble(d), null); } catch (REXPMismatchException e) { throw(new REngineException(this, "REXPMismatchException in assign(,double[]): "+e)); } }
	/** convenience method equivalent to <code>assign(symbol, new REXPInteger(d), null)</code> (see {@link #assign(String, REXP, REXP)})
	 @param symbol symbol name to assign to
	 @param d values to assign */
	public void assign(String symbol, int[] d) throws REngineException { try { assign(symbol, new REXPInteger(d), null); } catch (REXPMismatchException e) { throw(new REngineException(this, "REXPMismatchException in assign(,int[]): "+e)); } }
	/** convenience method equivalent to <code>assign(symbol, new REXPString(d), null)</code> (see {@link #assign(String, REXP, REXP)})
	 @param symbol symbol name to assign to
	 @param d values to assign */
	public void assign(String symbol, String[] d) throws REngineException { try { assign(symbol, new REXPString(d), null); } catch (REXPMismatchException e) { throw(new REngineException(this, "REXPMismatchException in assign(,String[]): "+e)); } }
	/** convenience method equivalent to <code>assign(symbol, new REXPRaw(d), null)</code> (see {@link #assign(String, REXP, REXP)})
	 @param symbol symbol name to assign to
	 @param d values to assign */
	public void assign(String symbol, byte[] d) throws REngineException { try { assign(symbol, new REXPRaw(d), null); } catch (REXPMismatchException e) { throw(new REngineException(this, "REXPMismatchException in assign(,byte[]): "+e)); } }
	/** convenience method equivalent to <code>assign(symbol, new REXPString(d), null)</code> (see {@link #assign(String, REXP, REXP)})
	 @param symbol symbol name to assign to
	 @param d value to assign */
	public void assign(String symbol, String d) throws REngineException { try { assign(symbol, new REXPString(d), null); } catch (REXPMismatchException e) { throw(new REngineException(this, "REXPMismatchException in assign(,String[]): "+e)); } }
	/** convenience method equivalent to <code>assign(symbol, value, null)</code> (see {@link #assign(String, REXP, REXP)})
	 @param symbol symbol name to assign to
	 @param value values to assign */
	public void assign(String symbol, REXP value) throws REngineException, REXPMismatchException { assign(symbol, value, null); }
	
	//--- locking API ---
	/** attempts to obtain a lock for this R engine synchronously (without waiting for it).
	    <br>Note: check for {@link #supportsLocking()} before relying on this capability. If not implemented, always returns 0.
	 @return 0 if the lock could not be obtained (R engine is busy) and some other value otherwise -- the returned value must be used in a matching call to {@link #unlock(int)}. */
	public synchronized int tryLock() { return 0; }

	/** obtains a lock for this R engine, waiting until it becomes available.
	 <br>Note: check for {@link #supportsLocking()} before relying on this capability. If not implemented, always returns 0.
	 @return value that must be passed to {@link #unlock} in order to release the lock */
	public synchronized int lock() { return 0; }

	/** releases a lock previously obtained by {@link #lock()} or {@link #tryLock()}.
	 <br>Note: check for {@link #supportsLocking()} before relying on this capability.  If not implemented, has no effect.
	 @param lockValue value returned by {@link #lock()} or {@link #tryLock()}. */	 
	public synchronized void unlock(int lockValue) {}

	public String toString() {
		return super.toString()+((lastEngine==this)?"{last}":"");
	}
	
	public REXP wrap(Object o){
		return REXPWrapper.wrap(o); 
	}
}

// REngine - generic Java/R API
//
// Copyright (C) 2006 Simon Urbanek
// --- for licensing information see LICENSE file in the original JRclient distribution ---
//
//  RSrvException.java
//
//  Created by Simon Urbanek on Wed Jun 21 2006.
//
//  $Id: REngineException.java 3122 2009-06-17 15:47:41Z urbanek $
//

package org.rosuda.REngine;

/** <code>REngineException</code> is a generic exception that can be thrown by methods invoked on an R engine. */
public class REngineException extends Exception {
	/** engine associated with this exception */
    protected REngine engine;

	/** creates an R engine exception
	 @param engine engine associated with this exception
	 @param msg message describing the cause */
    public REngineException(REngine engine, String msg) {
        super(msg);
        this.engine = engine;
    }
 
	/** returns the engine associated with this exception
	 @return engine associated with this exception */
	public REngine getEngine() { return engine; }
}

package org.rosuda.REngine;

/** implementation of the {@link REngineOutputInterface} which uses standard output. */
public class REngineStdOutput implements REngineCallbacks, REngineOutputInterface {
	public synchronized void RWriteConsole(REngine eng, String text, int oType) {
		((oType == 0) ? System.out : System.err).print(text);
	}
	
	public void RShowMessage(REngine eng, String text) {
		System.err.println("*** "+text);
	}
	
	public void RFlushConsole(REngine eng) {
	}
}

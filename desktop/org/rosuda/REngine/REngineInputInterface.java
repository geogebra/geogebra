package org.rosuda.REngine;

/** interface defining delegate methods used by {@link REngine} to forward input callbacks from R. */
public interface REngineInputInterface {
	/** called when R enters the read stage of the event loop.
	 *  <p> Important: implementations should never use a direct return! That will cause a tigh-spinning event loop. Implementation must wait for input asynchronously (e.g., declare synchonized RReadConsole and use wait()) and return only when a complete line is available for processing.
	 *  @param eng calling engine
	 *  @param prompt prompt to display in the console
	 *  @param addToHistory flag indicating whether the input is transient (<code>false</code>) or to be recorded in the command history (<code>true</code>).
	 *  @return string to be processed as console input
	 */
	public String RReadConsole(REngine eng, String prompt, int addToHistory);
}

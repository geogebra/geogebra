package org.rosuda.REngine;

/** interface defining delegate methods used by {@link REngine} to forward console history callbacks from R. */
public interface REngineConsoleHistoryInterface {
	/** called when R wants to save the history content.
	 *  @param eng calling engine
	 *  @param filename name of the file to save command history to
	 */
	public void   RSaveHistory  (REngine eng, String filename);	
	
	/** called when R wants to load the history content.
	 *  @param eng calling engine
	 *  @param filename name of the file to load the command history from
	 */
	public void   RLoadHistory  (REngine eng, String filename);
}

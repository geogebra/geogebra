package org.rosuda.REngine;

/** interface defining delegate methods used by {@link REngine} to forward user interface callbacks from R. */
public interface REngineUIInterface {
	/** called when the busy state of R changes - usual response is to change the shape of the cursor
	 *  @param eng calling engine
	 *  @param state busy state of R (0 = not busy)
	 */
	public void   RBusyState  (REngine eng, int state);	
	
	/** called when R wants the user to choose a file.
	 *  @param eng calling engine
	 *  @param newFile if <code>true</code> then the user can specify a non-existing file to be created, otherwise an existing file must be selected.
	 *  @return full path and name of the selected file or <code>null</code> if the selection was cancelled.
	 */
	public String RChooseFile  (REngine eng, boolean newFile);
}

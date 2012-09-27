package org.rosuda.REngine;

/** interface defining delegate methods used by {@link REngine} to forward output callbacks from R. */
public interface REngineOutputInterface {
	/** called when R prints output to the console.
	 *  @param eng calling engine
	 *  @param text text to display in the console
	 *  @param oType output type (0=regular, 1=error/warning)
	 */
	public void RWriteConsole(REngine eng, String text, int oType);
	
	/** called when R wants to show a warning/error message box (not console-related).
	 *  @param eng calling engine
	 *  @param text text to display in the message
	 */
	public void RShowMessage(REngine eng, String text);
	
	/** called by R to flush (display) any pending console output.
	 *  @param eng calling engine */
	public void RFlushConsole(REngine eng);
}

package org.geogebra.web.full.gui.dialog;

/**
 * Input processing for fields.
 */
public interface ProcessInput {

	/**
	 * Runs when input has changed
	 * (paste or key event happened)
	 */
	void onInput();

}

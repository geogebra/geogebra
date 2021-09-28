package org.geogebra.web.full.gui.view.spreadsheet;

public interface CopyPasteHandler {
	/**
	 * @param text
	 *            pasted text
	 */
	void onPaste(String text);

	/** Handle Cut */
	void onCut();

	/**
	 * Handle copy
	 *
	 * @param altKey
	 *            whether alt is pressed
	 */
	void onCopy(boolean altKey);
}

package com.himamis.retex.editor.web;

/**
 * Handles CompositionEndEvents (for languages where single letter needs
 * multiple keystrokes)
 */
public interface CompositionEndHandler {

	/**
	 * @param event
	 *            composition end event
	 */
	void onCompositionEnd(CompositionEndEvent event);
}

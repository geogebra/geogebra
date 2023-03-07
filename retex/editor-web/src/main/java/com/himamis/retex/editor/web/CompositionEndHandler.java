package com.himamis.retex.editor.web;

import org.gwtproject.event.legacy.shared.EventHandler;

/**
 * Handles CompositionEndEvents (for languages where single letter needs
 * multiple keystrokes)
 */
public interface CompositionEndHandler extends EventHandler {

	/**
	 * @param event
	 *            composition end event
	 */
	void onCompositionEnd(CompositionEndEvent event);
}

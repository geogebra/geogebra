package com.himamis.retex.editor.web;

import com.google.gwt.event.shared.EventHandler;

public interface CompositionHandler extends EventHandler {

	/**
	 * Called when FocusEvent is fired.
	 * 
	 * @param event
	 *            the {@link FocusEvent} that was fired
	 */
	void onCompositionUpdate(CompositionEvent event);
}

package com.himamis.retex.editor.web;

import org.gwtproject.event.legacy.shared.EventHandler;

/**
 * Handler for composition update events
 * 
 * @author zbynek
 */
public interface CompositionHandler extends EventHandler {

	/**
	 * Called when CompositionUpdateEvent is fired.
	 * 
	 * @param event
	 *            the {@link CompositionUpdateEvent} that was fired
	 */
	void onCompositionUpdate(CompositionUpdateEvent event);

}

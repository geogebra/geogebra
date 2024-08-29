package com.himamis.retex.editor.web;

/**
 * Handler for composition update events
 * 
 * @author zbynek
 */
public interface CompositionHandler {

	/**
	 * Called when CompositionUpdateEvent is fired.
	 * 
	 * @param event
	 *            the {@link CompositionUpdateEvent} that was fired
	 */
	void onCompositionUpdate(CompositionUpdateEvent event);

}

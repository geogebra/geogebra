package org.geogebra.common.move.views;

import org.geogebra.common.move.events.BaseEvent;

/**
 * Must be implemented by views that can be registered as BaseEventView
 * 
 * @author stefan
 */
public interface EventRenderable {
	/**
	 * renders the given view
	 * 
	 * @param The
	 *            event that should be rendered
	 */
	public void renderEvent(BaseEvent event);
}

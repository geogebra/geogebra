package org.geogebra.web.plugin;

import com.google.gwt.core.client.JavaScriptObject;

public interface CloseEventHandler {
	/**
	 * Websocket closed
	 * 
	 * @param event
	 *            close event
	 */
	public void close(JavaScriptObject event);
}

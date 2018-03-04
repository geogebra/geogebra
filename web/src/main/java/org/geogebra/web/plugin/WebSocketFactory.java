package org.geogebra.web.plugin;

/**
 * Connection factory
 */
public class WebSocketFactory {

	/**
	 * @param url
	 *            WebSocket server URL
	 * @return new WebSocket connection
	 */
	public native static WebSocketConnection create(String url) /*-{
		return new $wnd.WebSocket(url);
	}-*/;

}

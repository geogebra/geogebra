package org.geogebra.web.plugin;

public class WebSocketFactory {

	/**
	 * @param url
	 * @return new WebSocket connection
	 */
	public native static WebSocketConnection create(String url) /*-{
		return new $wnd.WebSocket(url);
	}-*/;

}

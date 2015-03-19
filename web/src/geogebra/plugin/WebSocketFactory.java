package geogebra.plugin;

public class WebSocketFactory {

	public final short CONNECTING = 0;
	public final short OPEN = 1;
	public final short CLOSING = 2;
	public final short CLOSED = 3;

	/**
	 * @param url
	 * @return new WebSocket connection
	 */
	public native static WebSocketConnection create(String url) /*-{
		return new $wnd.WebSocket(url);
	}-*/;

}

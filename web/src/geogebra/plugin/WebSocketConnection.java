package geogebra.plugin;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.typedarrays.shared.ArrayBuffer;

/**
 * @author gabor Websocket connection
 *
 */
public class WebSocketConnection extends JavaScriptObject {

	protected WebSocketConnection() {
	}

	public native final String getBinaryType() /*-{
		return this.binariType;
	}-*/;

	public native final Long getBufferedAmount() /*-{
		return this.bufferedAmount;
	}-*/;

	public native final short getReadyState() /*-{
		return this.readyState;
	}-*/;

	public native final String getUrl() /*-{
		return this.url;
	}-*/;

	public native final void close() /*-{
		this.close();
	}-*/;

	public native final void send(String data) /*-{
		this.send(data);
	}-*/;

	public native final void send(ArrayBuffer data) /*-{
		this.send(data);
	}-*/;

}

package org.geogebra.web.plugin;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.typedarrays.shared.ArrayBuffer;

/**
 * @author gabor Websocket connection
 *
 */
public class WebSocketConnection extends JavaScriptObject {

	public static final short CONNECTING = 0;
	public static final short OPEN = 1;
	public static final short CLOSING = 2;
	public static final short CLOSED = 3;

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

	public native final void onClose(CloseEventHandler handler) /*-{
		this.onclose = function(e) {
			handler.@org.geogebra.web.plugin.CloseEventHandler::close(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		};
	}-*/;
	
	public native final void onOpen(OpenEventHandler handler) /*-{
		this.onopen = function(e) {
			handler.@org.geogebra.web.plugin.OpenEventHandler::open(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		};
	}-*/;
	
	public native final void onMessage(MessageEventHandler handler) /*-{
		this.onmessage = function(e) {
			handler.@org.geogebra.web.plugin.MessageEventHandler::message(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		};
	}-*/;

	public native final void onError(ErrorEventHandler handler) /*-{
		this.onerror = function(e) {
			hanlder.@org.geogebra.web.plugin.ErrorEventHandler::error(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		};
	}-*/;

}

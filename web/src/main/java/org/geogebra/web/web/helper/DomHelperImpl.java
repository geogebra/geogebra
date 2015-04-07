package org.geogebra.web.web.helper;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

public class DomHelperImpl implements DomHelper {

	public void setProperty(JavaScriptObject target, String name, String value) {
		nativeSetProperty(target, name, value);
	}

	public void setNativeEventHandler(Element element, String eventName,
	        Handler handler) {
		nativeSetNativeEventHandler(element, "on" + eventName, handler);
	}

	// Native Methods

	private native void nativeSetProperty(JavaScriptObject target, String name,
	        String value) /*-{
		target[name] = value;
	}-*/;

	public native void nativeSetNativeEventHandler(Element element,
	        String eventName, Handler handler) /*-{
		element[eventName] = function(evt) {
			handler.@org.geogebra.web.web.helper.DomHelper.Handler::handleEvent(Lcom/google/gwt/user/client/Event;)(evt);
		};
	}-*/;

}

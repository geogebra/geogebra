package org.geogebra.web.html5.event;

import org.geogebra.web.html5.util.JavaScriptEventHandler;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;

/**
 * @author gabor
 * 
 *         Helper class for creating native events
 *
 */
public class CustomEvent {

	/**
	 * @param name
	 *            for customEvent
	 * @return new CustomEvent(string);
	 */
	public static native NativeEvent getNativeEvent(String name) /*-{
		if ('CustomEvent' in $wnd) {
			return new $wnd.CustomEvent(name);
		}
		return null;
	}-*/;

	/**
	 * @param type
	 *            Event type
	 * @param el
	 *            Dom element
	 * @param funct
	 *            function to add
	 * @param bubble
	 *            to bubble or not
	 */
	public static native void addEventListener(String type, Element el,
	        JavaScriptEventHandler funct, boolean bubble) /*-{
		el
				.addEventListener(
						type,
						function(e) {
							funct.@org.geogebra.web.html5.util.JavaScriptEventHandler::execute(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
						}, bubble);
	}-*/;

}

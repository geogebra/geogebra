package org.geogebra.web.web.helper;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;

public interface DomHelper {

	void setProperty(JavaScriptObject target, String name, String value);

	void setNativeEventHandler(Element element, String eventName,
	        Handler handler);

	interface Handler {
		void handleEvent(Event nativeEvent);
	}

	abstract class SinkHandler implements Handler {

		public final void handleEvent(Event nativeEvent) {
			nativeEvent.preventDefault();
			nativeEvent.stopPropagation();
			handleSunkEvent(nativeEvent);
		}

		public abstract void handleSunkEvent(Event nativeEvent);

	}

}

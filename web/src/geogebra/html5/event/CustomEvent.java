package geogebra.html5.event;

import com.google.gwt.dom.client.NativeEvent;

/**
 * @author gabor
 * 
 * Helper class for creating native events
 *
 */
public class CustomEvent {

	/**
	 * @param string for customEvent
	 * @return new CustomEvent(string);
	 */
	public static native NativeEvent getNativeEvent(String name) /*-{
	    if ('CustomEvent' in $wnd) {
	    	return new $wnd.CustomEvent(name);
	    }
	    return null;
    }-*/;

}

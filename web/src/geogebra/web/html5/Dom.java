package geogebra.web.html5;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;

public class Dom extends DOM {
	
	/**
	 * @param className
	 * @return NodeList of elements found by className
	 */
	public static native NodeList<Element> getElementsByClassName(String className) /*-{
			return $doc.getElementsByClassName(className);
	}-*/;

	/**
	 * @param className
	 * @return first Element found by selector className
	 */
	public static native Element querySelector(String className) /*-{
	    return $doc.querySelector("."+className);
    }-*/;

}

package org.geogebra.web.html5.util;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.NodeList;

/**
 * Helper methods for finding DOM elements
 */
public final class Dom {
	private Dom() {
		// no public constructor
	}
	/**
	 * @param className
	 *            class name
	 * @return NodeList of elements found by className
	 */
	public static native NodeList<Element> getElementsByClassName(
	        String className) /*-{
		return $doc.getElementsByClassName(className);
	}-*/;

	/**
	 * @param selector
	 *            CSS selector
	 * @return Nodelist of elements found by the selector
	 */
	public static native NodeList<Element> querySelectorAll(String selector) /*-{
		return $doc.querySelectorAll(selector);
	}-*/;

	/**
	 * @param className
	 *            class name
	 * @return first Element found by selector className
	 */
	public static native Element querySelector(String className) /*-{
		return $doc.querySelector("." + className);
	}-*/;

	/**
	 * @param elem
	 *            the root element
	 * @param className
	 *            className
	 * @return first Element found by selector className
	 */
	public static native Element querySelectorForElement(JavaScriptObject elem,
			String className) /*-{
		return elem.querySelector("." + className);
	}-*/;

	/**
	 * 
	 * @param event
	 *            a native event
	 * @param element
	 *            the element to be tested
	 * @return true iff event targets the element or its children
	 */
	public static boolean eventTargetsElement(NativeEvent event, Element element) {
		EventTarget target = event.getEventTarget();
		if (Element.is(target) && element != null) {
			return element.isOrHasChild(Element.as(target));
		}
		return false;
	}

}

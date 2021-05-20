package org.geogebra.web.html5.util;

import org.geogebra.common.util.debug.Log;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.UIObject;

import elemental2.dom.DomGlobal;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLImageElement;
import jsinterop.base.Js;

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
	 * @param selector
	 *            selector
	 * @return first Element found by selector className
	 */
	public static native Element querySelectorForElement(JavaScriptObject elem,
			String selector) /*-{
		return elem.querySelector(selector);
	}-*/;

	/**
	 * @param style
	 *            style
	 * @param property
	 *            property name
	 * @param val
	 *            property value
	 */
	public static native void setImportant(Style style, String property,
			String val)/*-{
		style.setProperty(property, val, "important");
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

	/**
	 * @param ui
	 *            UI element
	 * @param className
	 *            CSS class
	 * @param add
	 *            whether to add or remove
	 */
	public static void toggleClass(UIObject ui, String className, boolean add) {
		if (add) {
			ui.getElement().addClassName(className);
		} else {
			ui.getElement().removeClassName(className);
		}
	}

	/**
	 * @param ui
	 *            UI element
	 * @param classTrue
	 *            CSS class when toggle is true
	 * @param classFalse
	 *            CSS class when toggle is false
	 * @param add
	 *            whether to add or remove
	 */
	public static void toggleClass(UIObject ui, String classTrue,
			String classFalse, boolean add) {
		toggleClass(ui.getElement(), classTrue, classFalse, add);
	}

	/**
	 * @param elem
	 *            HTML element
	 * @param classTrue
	 *            CSS class when toggle is true
	 * @param classFalse
	 *            CSS class when toggle is false
	 * @param add
	 *            whether to add or remove
	 */
	public static void toggleClass(Element elem, String classTrue,
			String classFalse, boolean add) {
		if (add) {
			elem.addClassName(classTrue);
			elem.removeClassName(classFalse);
		} else {
			elem.removeClassName(classTrue);
			elem.addClassName(classFalse);
		}
	}

	/**
	 * @return active element
	 */
	public static native Element getActiveElement() /*-{
		return $doc.activeElement;
	}-*/;

	/**
	 * Element.addEventListener extracted to static method for safe cast in tests.
	 * @param element element
	 * @param name event name
	 * @param listener listener
	 */
	public static void addEventListener(Element element, String name, EventListener listener) {
		elemental2.dom.Element el = Js.uncheckedCast(element);
		el.addEventListener(name, listener);
	}

	/**
	 * @param element element
	 * @param width CSS property name
	 * @return value, if it was a number in px; otherwise 0
	 */
	public static int getPxProperty(Element element, String width) {
		try {
			return Integer.parseInt(element.getStyle().getProperty(width)
					.replace("px", ""));
		} catch (RuntimeException ex) {
			Log.warn(ex.getMessage());
		}
		return 0;
	}

	public static HTMLImageElement createImage() {
		return (HTMLImageElement) DomGlobal.document.createElement("img");
	}
}

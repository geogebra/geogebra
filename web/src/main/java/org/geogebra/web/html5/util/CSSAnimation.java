package org.geogebra.web.html5.util;

import com.google.gwt.dom.client.Element;

/**
 * Utility class for CSS animations
 */
public class CSSAnimation {
	/**
	 * @param runnable
	 *            animation callback
	 * @param root
	 *            animated element
	 * @param classname
	 *            class to be checked for the animation and removed afterwards
	 */
	public static native void runOnAnimation(Runnable runnable, Element root,
			String classname) /*-{
		var reClass = RegExp(classname);
		var callback = function() {
			root.removeEventListener("animationend", callback);
			if (root.className.match(reClass)) {
				root.className = root.className.replace(reClass, "");
				runnable.@java.lang.Runnable::run()();
			}
		};
		if ((root.style.animation || root.style.animation === "")
				&& root.className.match(reClass)) {

			root.addEventListener("animationend", callback);
			return;
		}
		$wnd.setTimeout(callback, 0);

	}-*/;
}

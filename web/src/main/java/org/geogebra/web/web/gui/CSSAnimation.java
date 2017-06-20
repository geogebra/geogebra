package org.geogebra.web.web.gui;

import com.google.gwt.dom.client.Element;

public class CSSAnimation {
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
		window.setTimeout(callback, 0);

	}-*/;
}

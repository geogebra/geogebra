package org.geogebra.web.web;

import org.geogebra.web.cas.latex.MathQuillHelper;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;

public class LaTeXHelper {
	public void initialize() {
		// native preview handlers independent from app/applet
		// THIS IS THE SAME CODE AS IN Tablet.java!!!
		// maybe better than putting into both GeoGebraFrame / GeoGebraAppFrame
		// it it would even be better to find a common class and put there
		// although I'm not sure it's good to use AppW or something like that
		// for preloading, code block separation GWT cache JavaScript files...
		// edit: maybe put this at the end of this method in production builds?
		Event.addNativePreviewHandler(new NativePreviewHandler() {
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				switch (event.getTypeInt()) {
				// AFAIK, mouse events do not fire on touch devices,
				// and touch events do not fire on mouse devices,
				// so this will be okay (except laptops with touch
				// screens, but then also, the event will either be
				// mouse event or touch event, but not both, I think)
				case Event.ONTOUCHSTART:
					if (event.getNativeEvent() != null) {
						MathQuillHelper.escEditingHoverTapWhenElsewhere(
								event.getNativeEvent(), true);
					}
					break;
				case Event.ONMOUSEDOWN:
					if (event.getNativeEvent() != null) {
						MathQuillHelper.escEditingHoverTapWhenElsewhere(
								event.getNativeEvent(), false);
					}
					break;
				// this is an addition, only matters in Web.java, not on tablets
				case Event.ONKEYDOWN:
					if (event.getNativeEvent() != null) {
						if (event.getNativeEvent()
								.getKeyCode() == KeyCodes.KEY_ENTER) {
							// in case ENTER is pressed while the AppWapplet is
							// out of focus, then give the applet the focus
							// back! but which applet? it should have been
							// remembered!
							AppW.giveFocusBack();
						}
					}
				default:
					break;
				}
			}
		});
	}
}

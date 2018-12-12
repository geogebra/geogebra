package org.geogebra.web.html5.euclidian;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget to able screen readers to read text from
 * non-accessible areas like EV1, EV3D
 * 
 * @author laszlo
 *
 */
public class ReaderWidget extends SimplePanel {
	private Timer timer;

	/**
	 * Constructor.
	 */
	public ReaderWidget(int evNo) {
		getElement().setId("screenReader" + evNo);
		// can't be tabbed, but can get the focus programmatically
		getElement().setTabIndex(-1);
		getElement().setAttribute("role", "status");
		getElement().setAttribute("aria-live", "polite");
		getElement().setAttribute("aria-atomic", "true");
		getElement().setAttribute("aria-relevant", "additions text");
		offscreen(this);
	}

	/**
	 * @param widget
	 *            widget to hide offscreen
	 */
	public static void offscreen(Widget widget) {
		widget.getElement().getStyle().setTop(-1000.0, Unit.PX);
		widget.getElement().getStyle().setPosition(Position.ABSOLUTE);
	}

	private void createTimer() {
		timer = new Timer() {

			@Override
			public void run() {
				reset();
			}
		};
	}

	/**
	 * Set text to read.
	 * 
	 * @param text
	 *            to set.
	 */
	public void setText(String text) {
		getElement().setInnerHTML(text);
	}

	/**
	 * Resets the widget.
	 */
	public void reset() {
		setText("");
	}

	/**
	 * 
	 * @param text
	 *            to read.
	 */
	public void read(final String text) {
		Log.debug("read text: " + text);
		setText(text);
		focus();
		resetWithDelay();
	}

	private void resetWithDelay() {
		if (timer == null) {
			createTimer();
		}
		timer.schedule(1000);
	}

	private void focus() {
		getElement().focus();
	}

	/**
	 * @param text
	 *            text to read
	 * @param ui
	 *            UI to focus afterwards
	 */
	public void readAndScroll(String text, UIObject ui) {
		if (!hasParentWindow() && !Browser.isiOS()) {
			JavaScriptObject scrollState = JavaScriptObject.createObject();
			int scrolltop = getScrollTop(scrollState);
			read(text);
			ui.getElement().focus();
			setScrollTop(scrolltop, scrollState);
		}
	}

	private static native int getScrollTop(JavaScriptObject scrollState)/*-{
		scrollState.element = $doc.body;
		if ($doc.documentElement.scrollTop && !$doc.body.scrollTop) {
			scrollState.element = $doc.documentElement;
		}
		return scrollState.element.scrollTop;
	}-*/;

	private static native boolean hasParentWindow()/*-{
		return $wnd.parent != $wnd;
	}-*/;

	private static native void setScrollTop(int st,
			JavaScriptObject scrollState)/*-{
		scrollState.element.scrollTop = st;
	}-*/;
}

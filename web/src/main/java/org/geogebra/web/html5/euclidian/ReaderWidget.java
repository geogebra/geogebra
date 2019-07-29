package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.ScreenReaderAdapter;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget to able screen readers to read text from
 * non-accessible areas like EV1, EV3D
 * 
 * @author laszlo
 *
 */
public class ReaderWidget extends SimplePanel implements ScreenReaderAdapter {
	private Timer timer;
	private Element anchor;

	/**
	 * Constructor.
	 * 
	 * @param evNo
	 *            view number
	 * @param anchor
	 *            object to focus afterwards
	 */
	public ReaderWidget(int evNo, Element anchor) {
		this.anchor = anchor;
		getElement().setId("screenReader" + evNo);
		// can't be tabbed, but can get the focus programmatically
		getElement().setTabIndex(-1);
		getElement().setAttribute("role", "status");
		getElement().setAttribute("aria-live", "polite");
		getElement().setAttribute("aria-atomic", "true");
		getElement().setAttribute("aria-relevant", "additions text");
		if (Browser.needsAccessibilityView()) {
			setVisible(false);
		} else {
			offscreen(this);
		}
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
		// make sure text isn't truncated by <return>
		// https://help.geogebra.org/topic/alttext-reading-stops-at-hard-return
		setText(text.replace('\n', ' '));
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
	 */
	@Override
	public void readText(String text) {
		if (!hasParentWindow() && !Browser.needsAccessibilityView()) {
			readTextImmediate(text);
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

	@Override
	public void readTextImmediate(String text) {
		JavaScriptObject scrollState = JavaScriptObject.createObject();
		int scrolltop = getScrollTop(scrollState);
		read(text);
		anchor.focus();
		setScrollTop(scrolltop, scrollState);

	}
}

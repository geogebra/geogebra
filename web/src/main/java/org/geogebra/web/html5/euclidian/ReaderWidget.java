package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.ScreenReaderAdapter;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.web.html5.Browser;
import org.gwtproject.timer.client.Timer;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;

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
		getElement().addClassName("screenReaderStyle");
		// can't be tabbed, but can get the focus programmatically
		getElement().setTabIndex(-1);
		getElement().setAttribute("role", "status");
		getElement().setAttribute("aria-live", "polite");
		getElement().setAttribute("aria-atomic", "true");
		getElement().setAttribute("aria-relevant", "additions text");
		if (Browser.needsAccessibilityView()) {
			setVisible(false);
		}
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
	private void setText(String text) {
		getElement().setInnerHTML(text);
	}

	/**
	 * Resets the widget.
	 */
	private void reset() {
		setText("");
	}

	/**
	 * 
	 * @param text
	 *            to read.
	 */
	private void read(final String text) {
		ScreenReader.debug(text);
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

	@Override
	public void readDelayed(final String text) {
		new Timer() {
			@Override
			public void run() {
				readTextImmediate(text);
			}
		}.schedule(200);
	}

	private void readTextImmediate(String text) {
		JavaScriptObject scrollState = JavaScriptObject.createObject();
		int scrolltop = getScrollTop(scrollState);
		read(text);
		anchor.focus();
		setScrollTop(scrolltop, scrollState);
	}

	private static native int getScrollTop(JavaScriptObject scrollState)/*-{
		scrollState.element = $doc.body;
		if ($doc.documentElement.scrollTop && !$doc.body.scrollTop) {
			scrollState.element = $doc.documentElement;
		}
		return scrollState.element.scrollTop;
	}-*/;

	private static native boolean hasParentWindow()/*-{
		return $wnd.parent !== $wnd;
	}-*/;

	private static native void setScrollTop(int st,
			JavaScriptObject scrollState)/*-{
		scrollState.element.scrollTop = st;
	}-*/;
}

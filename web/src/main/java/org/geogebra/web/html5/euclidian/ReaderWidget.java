package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.ScreenReaderAdapter;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.web.html5.Browser;
import org.gwtproject.timer.client.Timer;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;

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
	private HTMLElement scrollElement;

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
		updateScrollElement();
		double scrollTop = scrollElement.scrollTop;
		read(text);
		anchor.focus();
		scrollElement.scrollTop = scrollTop;
	}

	private void updateScrollElement() {
		if (Js.isTruthy(DomGlobal.document.documentElement.scrollTop)
			&& Js.isFalsy(DomGlobal.document.body.scrollTop)) {
			scrollElement = DomGlobal.document.documentElement;
		} else {
			scrollElement = DomGlobal.document.body;
		}
	}

	private static boolean hasParentWindow() {
		return DomGlobal.window.parent != DomGlobal.window;
	}

}

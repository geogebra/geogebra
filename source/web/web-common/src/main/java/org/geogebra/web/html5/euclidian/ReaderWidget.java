/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.ScreenReaderAdapter;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.FocusUtil;
import org.gwtproject.dom.client.Element;
import org.gwtproject.timer.client.Timer;
import org.gwtproject.user.client.ui.SimplePanel;

import elemental2.core.JsString;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;

/**
 * Widget to allow screen readers to read text from
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

	/**
	 * Set text to read.
	 * 
	 * @param text
	 *            to set.
	 */
	private void setText(String text) {
		getElement().setInnerText(text);
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
		String normalized = new JsString(text).normalize();
		ScreenReader.debug(normalized);
		// make sure text isn't truncated by <return>
		// https://help.geogebra.org/topic/alttext-reading-stops-at-hard-return
		setText(normalized.replace('\n', ' '));
		focus();
		resetWithDelay();
	}

	private void resetWithDelay() {
		DomGlobal.setTimeout((ignore) -> reset(), 1000);
	}

	private void focus() {
		FocusUtil.focusNoScroll(getElement());
	}

	/**
	 * @param text
	 *            text to read
	 */
	@Override
	public void readText(String text) {
		if (!Browser.needsAccessibilityView() && !isDomSliderActive()) {
			readTextImmediate(text);
		}
	}

	@Override
	public void readDelayed(final String text) {
		timer = new Timer() {
			@Override
			public void run() {
				readTextImmediate(text);
				timer = null;
			}
		};
		timer.schedule(200);
	}

	@Override
	public void cancelReadDelayed() {
		if (timer != null) {
			timer.cancel();
		}
	}

	private void readTextImmediate(String text) {
		updateScrollElement();
		double scrollTop = scrollElement.scrollTop;
		read(text);
		FocusUtil.focusNoScroll(anchor);
		scrollElement.scrollTop = scrollTop;
	}

	private boolean isDomSliderActive() {
		Element activeElement = Dom.getActiveElement();
		return activeElement != null && activeElement.hasTagName("INPUT")
				&& "range".equals(activeElement.getAttribute("type"));
	}

	private void updateScrollElement() {
		if (Js.isTruthy(DomGlobal.document.documentElement.scrollTop)
			&& Js.isFalsy(DomGlobal.document.body.scrollTop)) {
			scrollElement = DomGlobal.document.documentElement;
		} else {
			scrollElement = DomGlobal.document.body;
		}
	}
}

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

package org.geogebra.editor.web;

import static org.geogebra.editor.web.MathFieldW.SCROLL_THRESHOLD;

import org.geogebra.editor.share.editor.MathFieldInternal;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * Class to handle scrolling of the mathfield container.
 */
public class MathFieldScroller {

	/**
	 * Scrolls content horizontally,  based on the cursor position
	 */
	public static void scrollHorizontallyToCursor(Widget parent, int rightMargin, int cursorX) {
		Element parentElement = parent.getElement();
		int parentWidth = parent.getOffsetWidth() - rightMargin;
		int scrollLeft = parentElement.getScrollLeft();

		int scroll = MathFieldInternal.getHorizontalScroll(scrollLeft, parentWidth, cursorX);
		if (scroll != scrollLeft) {
			parentElement.setScrollLeft(scroll);
		}
	}

	/**
	 * Scrolls content vertically, based on the cursor position
	 *
	 * @param margin
	 *            minimal distance from cursor to top/bottom border
	 */
	public static void scrollVerticallyToCursor(FlowPanel parent, int margin, int cursorY) {
		Element parentElement = parent.getElement();
		int height = parent.getOffsetHeight();
		int scrollTop = parentElement.getScrollTop() + margin;
		int position = cursorY < SCROLL_THRESHOLD ? 0 : cursorY;
		if (position < scrollTop
				|| position > scrollTop + height - SCROLL_THRESHOLD) {
			parentElement.setScrollTop(position);
		}
	}
}

package com.himamis.retex.editor.web;

import static com.himamis.retex.editor.web.MathFieldW.SCROLL_THRESHOLD;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Class to handle scrolling of the mathfield container.
 */
public class MathFieldScroller {

	/**
	 * Scrolls content horizontally,  based on the cursor position
	 *
	 * @param margin
	 *            minimal distance from cursor to left/right border
	 */
	public static void scrollHorizontallyToCursor(FlowPanel parent, int margin, int cursorX) {
		Element parentElement = parent.getElement();
		int parentWidth = parent.getOffsetWidth();
		int scrollLeft = parentElement.getScrollLeft();

		if (parentWidth + scrollLeft - margin < cursorX) {
			parentElement.setScrollLeft(cursorX - parentWidth + margin);
		} else if (cursorX < scrollLeft + margin) {
			parentElement.setScrollLeft(cursorX - margin);
		}
	}

	/**
	 * Scrolls content verically, based on the cursor position
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

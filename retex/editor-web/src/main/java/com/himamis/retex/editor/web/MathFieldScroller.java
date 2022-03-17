package com.himamis.retex.editor.web;

import static com.himamis.retex.editor.web.MathFieldW.SCROLL_THRESHOLD;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.himamis.retex.editor.share.editor.MathFieldInternal;

/**
 * Class to handle scrolling of the mathfield container.
 */
public class MathFieldScroller {

	/**
	 * Scrolls content horizontally,  based on the cursor position
	 */
	public static void scrollHorizontallyToCursor(FlowPanel parent, int rightMargin, int cursorX) {
		Element parentElement = parent.getElement();
		int parentWidth = parent.getOffsetWidth() - rightMargin;
		int scrollLeft = parentElement.getScrollLeft();

		int scroll = MathFieldInternal.getHorizontalScroll(scrollLeft, parentWidth, cursorX);
		if (scroll != scrollLeft) {
			parentElement.setScrollLeft(scroll);
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

package com.himamis.retex.editor.web;

import static com.himamis.retex.editor.web.MathFieldW.SCROLL_THRESHOLD;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.himamis.retex.renderer.share.CursorBox;

/**
 * Class to handle scrolling of the mathfield container.
 */
public class MathFieldScroller {
	private final FlowPanel parent;
	private final Element parentElement;

	/**
	 * Constuctor
	 *
	 * @param parent the container of the matfield.
	 *
	 */
	public MathFieldScroller(FlowPanel parent) {
		this.parent = parent;
		this.parentElement = parent.getElement();
	}

	/**
	 * Scrolls content horizontally,  based on the cursor position
	 *
	 * @param margin
	 *            minimal distance from cursor to left/right border
	 */
	public void scrollHorizontallyToCursor(int margin) {
		if (parent.getOffsetWidth() + parentElement.getScrollLeft()
				- margin < CursorBox.startX) {
			parentElement.setScrollLeft((int) CursorBox.startX
					- parent.getOffsetWidth() + margin);
		} else if (CursorBox.startX < parent.getElement().getScrollLeft()
				+ margin) {
			scrollLeft((int) CursorBox.startX - margin);
		}
	}

	/**
	 * Scrolls content verically, based on the cursor position
	 *
	 * @param margin
	 *            minimal distance from cursor to top/bottom border
	 */
	public void scrollVerticallyToCursor(int margin) {
		int height = parent.getOffsetHeight();
		int scrollTop = parentElement.getScrollTop() + margin;
		int position = CursorBox.startY < SCROLL_THRESHOLD ? 0 : (int) CursorBox.startY;
		if (position < scrollTop
				|| position > scrollTop + height - SCROLL_THRESHOLD) {
			scrollTop(position);
		}
	}

	private void scrollLeft(int position) {
		parentElement.setScrollLeft(position);
	}

	private void scrollTop(int position) {
		parentElement.setScrollTop(position);
	}
}

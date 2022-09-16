package org.geogebra.web.html5.gui.inputfield;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;

public class CursorOverlay extends FlowPanel {

	public CursorOverlay() {
		setStyleName("cursorOverlay");
	}

	/**
	 * @param cursorPos cursor position
	 * @param text textfield content
	 */
	public void update(int cursorPos, String text) {
		CursorOverlay dummyCursor = this;
		dummyCursor.clear();
		InlineLabel prefix = new InlineLabel(text.substring(0, cursorPos));
		dummyCursor.add(prefix);
		InlineLabel w = new InlineLabel("|");
		w.getElement().getStyle().setMarginLeft(-2, Style.Unit.PX);
		dummyCursor.add(w);
		dummyCursor.add(new InlineLabel(text.substring(cursorPos)));
		int offset = prefix.getOffsetWidth() - dummyCursor.getElement().getScrollLeft();
		int scrollPadding = 10;
		if (offset < 0) {
			dummyCursor.getElement().setScrollLeft(prefix.getOffsetWidth() - scrollPadding);
		} else if (offset > dummyCursor.getOffsetWidth() - scrollPadding) {
			dummyCursor.getElement().setScrollLeft(prefix.getOffsetWidth()
					- dummyCursor.getOffsetWidth() + scrollPadding);
		}
	}
}

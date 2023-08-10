package org.geogebra.web.html5.gui.inputfield;

import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.InlineLabel;

public class CursorOverlay extends FlowPanel {

	private String text = "";
	int cursorPos = -1;
	private boolean seleted = false;

	public CursorOverlay() {
		setStyleName("cursorOverlay");
	}

	/**
	 * @param cursorPos cursor position
	 * @param text textfield content
	 */
	public void update(int cursorPos, String text) {
		if (text.equals(this.text) && cursorPos == this.cursorPos) {
			return;
		}
		this.text = text;
		this.cursorPos = cursorPos;
		update();
	}

	private void update() {
		CursorOverlay dummyCursor = this;
		dummyCursor.clear();
		InlineLabel prefix = new InlineLabel(text.substring(0, cursorPos));
		dummyCursor.add(prefix);
		InlineLabel w = new InlineLabel("|");
		w.setStyleName("virtualCursor");
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

	public void setHorizontalAlignment(HorizontalAlignment alignment) {
		getElement().getStyle().setProperty("justifyContent",
				alignment.toString());
	}

	public void addFakeSelection() {
		seleted = true;
		InlineLabel selectedText = new InlineLabel(text);
		selectedText.addStyleName("select-content");
		clear();
		add(selectedText);
	}

	public void removeFakeSelection() {
		seleted = false;
		update();
	}

	public boolean hasFakeSelection() {
		return seleted;
	}
}

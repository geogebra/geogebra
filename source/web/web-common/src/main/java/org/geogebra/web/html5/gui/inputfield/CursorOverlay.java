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

package org.geogebra.web.html5.gui.inputfield;

import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.InlineLabel;

/**
 * Widget to emulate text cursor and selection on mobile platforms
 * that the native one is problematic to handle
 */
public class CursorOverlay extends FlowPanel {

	private String text = "";
	int cursorPos = -1;
	private boolean selected = false;

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
		add(prefix);
		InlineLabel w = new InlineLabel("|");
		w.setStyleName("virtualCursor");
		add(w);
		add(new InlineLabel(text.substring(cursorPos)));
		int offset = prefix.getOffsetWidth() - this.getElement().getScrollLeft();
		int scrollPadding = 10;
		if (offset < 0) {
			getElement().setScrollLeft(prefix.getOffsetWidth() - scrollPadding);
		} else if (offset > this.getOffsetWidth() - scrollPadding) {
			getElement().setScrollLeft(prefix.getOffsetWidth()
					- this.getOffsetWidth() + scrollPadding);
		}
	}

	/**
	 *
	 * @param alignment to set.
	 */
	public void setHorizontalAlignment(HorizontalAlignment alignment) {
		getElement().getStyle().setProperty("justifyContent",
				alignment.toString());
	}

	/**
	 * Adds the non-native selection widget.
	 */
	public void addFakeSelection() {
		selected = true;
		InlineLabel selectedText = new InlineLabel(text);
		selectedText.addStyleName("select-content");
		clear();
		add(selectedText);
	}

	/**
	 * Removes the non-native selection widget.
	 */
	public void removeFakeSelection() {
		selected = false;
		update();
	}

	/**
	 *
	 * @return if the selection widget is present.
	 */
	public boolean hasFakeSelection() {
		return selected;
	}
}

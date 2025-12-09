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

package org.geogebra.common.euclidian.draw.dropdown;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.gui.util.DropDownList;

class OptionScroller {
	private DrawOptions options;
	private final ScrollArrows arrows;
	private final DropDownList dropDown;
	private boolean active = false;
	private DropDownScrollMode scrollMode = DropDownScrollMode.NONE;
	private boolean dragging = false;

	OptionScroller(DropDownList dropDown) {
		this.arrows = new ScrollArrows();
		this.dropDown = dropDown;
	}

	void setOptions(DrawOptions options) {
		this.options = options;
	}

	boolean isActive() {
		return active;
	}

	void setActive(boolean active) {
		this.active = active;
	}

	void scroll() {
		if (!active) {
			return;
		}
		scroll(scrollMode);
	}

	void scroll(DropDownScrollMode mode) {
		options.cancelDrag();
		scrollBy(mode.getDirection());
	}

	void scrollBy(int diff) {
		if (!active) {
			return;
		}
		options.shiftRangeBy(diff);
	}

	void scrollByItems(int difference) {
		if (options.getDragDirection()) {
			scrollBy(difference);
		} else {
			scrollBy(-difference);
		}
	}

	boolean isDragging() {
		return active && dragging;
	}

	int getTop() {
		return DrawOptions.MARGIN;
	}

	void stop() {
		scrollMode = DropDownScrollMode.NONE;
		dropDown.stopScrollTimer();
	}

	void setDragging(boolean value) {
		dragging = value;
	}

	void clip(GGraphics2D g2, OptionItem item, int dragOffset) {
		arrows.clip(g2, item, dragOffset);
	}

	void resetClip(GGraphics2D g2) {
		if (active && arrows.isClip()) {
			g2.resetClip();
		}
	}

	int getUpBottom() {
		return arrows.getUpBottom();
	}

	int getDownBottom() {
		return arrows.getDownBottom();
	}

	void drawArrows(GGraphics2D g2, GColor backgroundColor) {
		if (active) {
			arrows.draw(g2, backgroundColor);
		}
	}

	int getUpHeight() {
		return arrows.getUpHeight();
	}

	boolean isDownArrowHit(int x, int y) {
		return active && arrows.isDownHit(x, y);
	}

	boolean isUpArrowHit(int x, int y) {
		return active && arrows.isUpHit(x, y);
	}

	boolean isArrowsHit(int x, int y) {
		return active && arrows.isHit(x, y);
	}

	void update(int left, int top, int height, GDimension dimItem) {
		arrows.update(left, top, height, dimItem);
	}

	int getDownArrowHeight() {
		return arrows.getDownHeight();
	}
}

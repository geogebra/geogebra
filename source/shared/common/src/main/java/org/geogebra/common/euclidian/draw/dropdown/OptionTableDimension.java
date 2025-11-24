package org.geogebra.common.euclidian.draw.dropdown;

import static org.geogebra.common.euclidian.draw.dropdown.DrawOptions.MARGIN;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GDimension;

class OptionTableDimension {
	private final DropDownModel settings;
	private final GDimension itemDimension;
	private final int downArrowHeight;
	private final boolean scroll;
	static final int VERTICAL_PADDING = 8;

	OptionTableDimension(DropDownModel settings, GDimension itemDimension,
			int downArrowHeight,
			boolean scroll) {
		this.settings = settings;
		this.itemDimension = itemDimension;
		this.downArrowHeight = downArrowHeight;
		this.scroll = scroll;
	}

	GDimension compute() {
		return AwtFactory.getPrototype().newDimension(computeWidth(), computeHeight());
	}

	private int computeWidth() {
		return settings.getColCount() * itemDimension.getWidth();
	}

	private int computeHeight() {
		int tableHeight = heightOfAllRows()
				+ (scroll && noOverflow() ? itemDimension.getHeight() : 0);

		if (hasOverflow()) {
			tableHeight = adjustTop(tableHeight);

			if (scroll) {
				tableHeight = heightForScroll(downArrowHeight, tableHeight);
			}
		}

		return tableHeight + 2 * VERTICAL_PADDING;
	}

	private int heightForScroll(int downArrowHeight, int tableHeight) {
		int height = tableHeight + 2 * downArrowHeight;
		if (height > settings.viewHeight()) {
			height = settings.viewHeight() - 2 * MARGIN;
		} else {
			settings.setTop((settings.viewHeight() - height) / 2);
		}
		return height;
	}

	private int adjustTop(int tableHeight) {
		int height = tableHeight;
		settings.setTop(settings.viewHeight() - tableHeight - MARGIN);
		if (settings.getTop() < MARGIN) {
			settings.setTop(MARGIN);
			if (!scroll) {
				height -= MARGIN;
			}
		}
		return height;
	}

	private boolean hasOverflow() {
		return tableBottom() >= settings.viewHeight();
	}

	private boolean noOverflow() {
		return tableBottom() <= settings.viewHeight();
	}

	private int tableBottom() {
		return settings.getTop() + heightOfAllRows() + MARGIN;
	}

	private int heightOfAllRows() {
		return settings.getRowCount() * itemDimension.getHeight();
	}
}

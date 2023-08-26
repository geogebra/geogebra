package org.geogebra.common.euclidian.draw.dropdown;

import static org.geogebra.common.euclidian.draw.dropdown.DrawOptions.MARGIN;
import static org.geogebra.common.euclidian.draw.dropdown.DrawOptions.ROUND;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.factories.AwtFactory;

class OptionTable {
	private GRectangle bounds;
	private GDimension dimension;
	private final DropDownModel model;
	private final OptionItemList items;
	private final DrawItems drawItems;
	private final OptionScroller scroller;
	private int boxWidth;

	OptionTable(DropDownModel model,
			DrawItems drawItems,
			OptionScroller scroller) {
		this.model = model;
		this.drawItems = drawItems;
		this.scroller = scroller;
		this.items = model.items();
	}

	int getWidth() {
		return dimension.getWidth();
	}

	int getHeight() {
		return dimension.getHeight();
	}

	boolean isHit(int x, int y) {
		return bounds.contains(x, y);
	}

	void update(int boxWidth, int fontSize) {
		this.boxWidth = boxWidth;
		model.setFontSize(fontSize);
		items.update(model);
		packItems();
		OptionTableDimension optionDimension =
				new OptionTableDimension(model, items.getMaxDimension(),
						scroller.getDownArrowHeight(),
						scroller.isActive());

		dimension = optionDimension.compute();

		if (model.getLeft() + getWidth() > model.viewWidth()) {
			model.setLeft(model.viewWidth() - getWidth());
		}

		bounds = AwtFactory.getPrototype().newRectangle(model.getLeft(),
				model.getTop() + MARGIN, getWidth(), getHeight());

		scroller.update(model.getLeft(), model.getTop(),
				getHeight(), items.getMaxDimension());
	}

	boolean wrapItems() {
		items.update(model);
		int maxRows = ((model.viewHeight() - 2 * MARGIN)
				/ items.getMaxHeight()) + 1;
		int maxCols = model.viewWidth() / items.getMaxWidth();
		int maxItems = items.size();
		if (maxItems < maxRows) {
			getOneColumnSettings();
			model.setRowCount(maxItems);
			return true;
		}

		model.setColCount(maxItems / maxRows + (maxItems % maxRows == 0 ? 0 : 1));
		model.setRowCount(maxRows);

		model.balanceTable(maxItems);
		return model.getColCount() < maxCols;
	}

	void getOneColumnSettings() {
		model.setColCount(1);
		items.updateMaxDimension(boxWidth);
	}

	private void packItems() {
		if (!getScrollSettings()) {
			boolean finished = false;
			while (!finished && model.isFontSizeNotMinimal()) {
				finished = wrapItems();
				model.decreaseFontSize();
			}

			if (items.noDimension()) {
				wrapItems();
			}
		}
	}

	/**
	 * Gets scroll settings: visible items, boundaries.
	 * @return if scroll really makes sense or multi-column would be better.
	 */
	private boolean getScrollSettings() {
		getOneColumnSettings();
		int visibleItems = ((model.viewHeight() - (2 * MARGIN))
				/ items.getMaxHeight()) - 1;

		if (visibleItems > items.size()) {
			// can't display more than this
			visibleItems = items.size();
			scroller.setActive(false);
		} else if (visibleItems < items.size() - 1) {
			// The two additional arrows take an item by height.
			visibleItems--;
			scroller.setActive(true);
		}
		VisibleItemRange range = model.getRange();
		range.setVisible(visibleItems);

		if (scroller.isActive()) {
			model.setRowCount(range.getVisibleItemCount());
		} else {
			range.setVisibleAll();
			model.setRowCount(items.size());
		}
		scroller.setActive(scroller.isActive() && model.getRowCount() > 2);
		if (scroller.isActive()) {
			model.setTop(scroller.getTop());
		}
		return scroller.isActive();
	}

	void draw(GGraphics2D g2) {
		drawBox(g2);
		drawItems.draw(g2);
		scroller.drawArrows(g2, model.getBackgroundColor());
	}

	private void drawBox(GGraphics2D g2) {
		g2.setPaint(model.getBackgroundColor());
		int x = model.getLeft() - 1;
		int y = model.getTop() - 1;
		g2.fillRoundRect(x, y, getWidth() + 2,
				getHeight() + 2, ROUND, ROUND);
		g2.setPaint(GColor.DARK_GRAY);
		g2.drawRoundRect(x, y, getWidth() + 2,
				getHeight() + 2, ROUND, ROUND);
	}
}
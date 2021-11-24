package org.geogebra.common.euclidian.draw.dropdown;

import static org.geogebra.common.euclidian.draw.dropdown.DrawOptions.ROUND;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.factories.AwtFactory;

class DrawItems {
	private final OptionScroller scroller;
	private final OptionItemList items;
	private final DrawDropDownList drawDropDownList;
	private final ItemSelector selector;
	private final DropDownModel model;

	DrawItems(DrawDropDownList drawDropDownList,
			DropDownModel model, ItemSelector selector, OptionScroller scroller) {
		this.model = model;
		this.drawDropDownList = drawDropDownList;
		this.selector = selector;
		this.scroller = scroller;

		items = model.items();
	}

	void draw(GGraphics2D g2) {
		int idx = model.getStartIdx();
		int startRow = 0;
		int visibleRows = model.getRowCount();

		if (scroller.isActive() && idx >= 0 && selector.getDragOffset() != 0) {
			idx--;
			startRow = -1;
			visibleRows++;
		}

		for (int col = 0; col < model.getColCount(); col++) {
			for (int row = startRow; row < visibleRows; row++) {
				if (idx >= 0 && idx < items.size()) {
					draw(g2, col, row, items.get(idx));
				}
				idx++;
			}
		}
	}

	private void draw(GGraphics2D g2, int col, int row, OptionItem item) {
		int rectLeft = model.getLeft() + items.getMaxWidth() * col;
		int rectTop = model.getTop() + items.getMaxHeight() * row;

		if (scroller.isActive()) {
			rectTop += scroller.getUpHeight() + selector.getDragOffsetWithDirection();
		}

		if (item.getRect() == null || item.getRect().getX() != rectLeft
				|| item.getRect().getY() != rectTop) {
			item.setRect(AwtFactory.getPrototype().newRectangle(rectLeft,
					rectTop, items.getMaxWidth(), items.getMaxHeight()));
		}

		if (!(item.hasNoBounds() || scroller.isActive() && noExtraItem(item))) {
			drawItem(g2, item, selector.isHovered(item));
		}
	}

	private void drawItem(GGraphics2D g2, OptionItem item, boolean hover) {
		scroller.clip(g2, item, selector.getDragOffset());

		if (hover) {
			drawHoveredItem(g2, item);
		} else {
			drawNormalItem(g2, item);
		}

		calculateItemRectangle(item);

		g2.setPaint(model.getItemColor());

		if (item.isLatex()) {
			drawItemAsLatex(g2, item);
		} else {
			drawItemAsPlain(g2, item);
		}

		scroller.resetClip(g2);
	}

	private boolean noExtraItem(OptionItem item) {
		return (item.getIndex() == model.getRange().getStart() - 1
				&& item.getBottom() < scroller.getUpBottom())
				|| (scroller.getDownBottom() < item.getBottom());
	}

	private void drawItemAsPlain(GGraphics2D g2, OptionItem item) {
		model.applyFontTo(g2);

		double x = (items.getMaxWidth() - item.getWidth()) / 2.0;
		double y = (items.getMaxHeight() - OptionItemList.PADDING);

		EuclidianStatic.drawIndexedString(model.getApp(), g2,
				item.getText(), item.getLeft() + x, item.getTop() + y, false);
	}

	private void drawItemAsLatex(GGraphics2D g2, OptionItem item) {
		int x = item.getLeft();
		int y = item.getTop();

		drawDropDownList.drawLatex(g2, model.getGeoList(), model.getFont(), item.getText(),
				x + (int) ((item.getBoundsWidth() - item.getWidth()) / 2),
				y + (int) ((item.getBoundsHeight() - item.getHeight()) / 2));
	}

	private void calculateItemRectangle(OptionItem item) {
		if (item.getRect() == null) {
			item.setRect(AwtFactory.getPrototype().newRectangle(item.getLeft(),
					item.getTop(), items.getMaxWidth(), items.getMaxHeight()));
		}
	}

	private void drawNormalItem(GGraphics2D g2, OptionItem item) {
		g2.setColor(model.getBackgroundColor());
		g2.fillRect(item.getLeft(), item.getTop(), items.getMaxWidth(), items.getMaxHeight());
	}

	private void drawHoveredItem(GGraphics2D g2, OptionItem item) {
		g2.setColor(selector.getColor());
		g2.fillRoundRect(item.getLeft(), item.getTop(), items.getMaxWidth(),
				items.getMaxHeight(), ROUND, ROUND);
	}
}

package org.geogebra.common.euclidian.draw.dropdown;

import static org.geogebra.common.main.GeoGebraColorConstants.NEUTRAL_200;
import static org.geogebra.common.main.GeoGebraColorConstants.NEUTRAL_900;
import static org.geogebra.common.main.GeoGebraColorConstants.PURPLE_100;
import static org.geogebra.common.main.GeoGebraColorConstants.PURPLE_700;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.factories.AwtFactory;

class DrawItems {
	private final OptionScroller scroller;
	private final OptionItemList items;
	private final DrawDropDownList drawDropDownList;
	private final ItemSelector selector;
	private final DropDownModel model;
	private static final int MIN_ROW_HEIGHT = 56;

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

		OptionItem focusedItem = null;
		for (int col = 0; col < model.getColCount(); col++) {
			for (int row = startRow; row < visibleRows; row++) {
				if (idx >= 0 && idx < items.size()) {
					draw(g2, col, row, items.get(idx));
					if (selector.isHovered(items.get(idx)) && selector.hasKeyboardFocus()) {
						focusedItem = items.get(idx);
					}
				}
				idx++;
			}
		}

		drawFocusedItemBorder(g2, focusedItem);
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

		GColor bgColor = hover ? (selector.hasKeyboardFocus() ? PURPLE_100 : NEUTRAL_200)
				: model.getBackgroundColor();
		g2.setColor(bgColor);
		drawItem(g2, item);

		calculateItemRectangle(item);

		g2.setPaint(hover ? NEUTRAL_900 : model.getItemColor());

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

		double x = OptionItemList.HORIZONTAL_PADDING;
		double y = items.getMaxHeight() - OptionItemList.VERTICAL_PADDING;

		EuclidianStatic.drawIndexedString(model.getApp(), g2,
				item.getText(), item.getLeft() + x, item.getTop() + y, false);
	}

	private void drawItemAsLatex(GGraphics2D g2, OptionItem item) {
		int x = item.getLeft();
		int y = item.getTop();

		drawDropDownList.drawLatex(g2, model.getGeoList(), model.getFont(), item.getText(),
				x + OptionItemList.HORIZONTAL_PADDING,
				y + (int) ((item.getBoundsHeight() - item.getHeight()) / 2));
	}

	private void calculateItemRectangle(OptionItem item) {
		if (item.getRect() == null) {
			item.setRect(AwtFactory.getPrototype().newRectangle(item.getLeft(),
					item.getTop(), items.getMaxWidth(),
					Math.max(items.getMaxHeight(), MIN_ROW_HEIGHT)));
		}
	}

	private void drawItem(GGraphics2D g2, OptionItem item) {
		g2.fillRect(item.getLeft(), item.getTop(), items.getMaxWidth(), items.getMaxHeight());
	}

	private void drawFocusedItemBorder(GGraphics2D g2, OptionItem item) {
		if (item != null) {
			g2.setStroke(AwtFactory.getPrototype().newBasicStroke(2));
			g2.setColor(PURPLE_700);
			g2.drawRect(item.getLeft(), item.getTop(), items.getMaxWidth(), items.getMaxHeight());
		}
	}
}

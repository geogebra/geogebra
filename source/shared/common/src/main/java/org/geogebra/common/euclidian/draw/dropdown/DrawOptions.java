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

import java.util.HashMap;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;

class DrawOptions implements MoveSelector {
	static final int MARGIN = 5;
	static final int ROUND = 8;
	private final DrawDropDownList drawDropDownList;
	private final DropDownModel model;
	private final GeoList geoList;
	private final EuclidianView view;
	private final App app;

	private final OptionTable table;
	private final ItemSelector selector;
	private final OptionScroller scroller;
	private final VisibleItemRange range;
	private final OptionItemList items;

	private boolean visible;
	private boolean clickStarted;

	DrawOptions(DrawDropDownList drawDropDownList,
			DropDownModel model, EuclidianView view,
			OptionScroller scroller) {
		this.drawDropDownList = drawDropDownList;
		this.view = view;
		this.model = model;
		this.scroller = scroller;
		scroller.setOptions(this);

		this.geoList = model.getGeoList();
		this.range = model.getRange();
		items = model.items();
		app = view.getApplication();

		selector = new ItemSelector(model, this);
		DrawItems drawItems = new DrawItems(drawDropDownList, model, selector, scroller);
		table = new OptionTable(model, drawItems, scroller);
	}

	void draw(GGraphics2D g2, int leftPos, int topPos) {
		if (isVisible()) {
			model.setPosition(leftPos, topPos);
			updateStartItem();

			update();
			table.draw(g2);
		}
	}

	void update() {
		items.fill(drawDropDownList);
		table.update(drawDropDownList.getBoxWidth(), drawDropDownList.getLabelFontSize());
	}

	private void updateStartItem() {
		if (range.getStart() == -1 && hasSelectedText()) {
			if (scroller.isActive()) {
				range.updateStart();
			} else {
				range.reset();
			}
		}
	}

	private boolean hasSelectedText() {
		return !"".equals(drawDropDownList.selectedText);
	}

	boolean isHit(int x, int y) {
		return isVisible() && table.isHit(x, y) || scroller.isArrowsHit(x, y);
	}

	private boolean handleUpControl(int x, int y) {
		if (scroller.isUpArrowHit(x, y)) {
			drawDropDownList.dropDown.startScrollTimer(x, y);
			scroller.scroll(DropDownScrollMode.UP);
			return true;
		}
		return false;
	}

	private boolean handleDownControl(int x, int y) {
		if (scroller.isDownArrowHit(x, y)) {
			scroller.scroll(DropDownScrollMode.DOWN);
			drawDropDownList.dropDown.startScrollTimer(x, y);
			return true;
		}

		return false;
	}

	void setHighlightIndex(int idx) {
		if (model.itemCount() <= idx) {
			items.update(model);
		}

		if (idx >= 0 && idx < items.size()) {
			selector.setHighlighted(items.get(idx));
			range.setSelected(idx);
			drawDropDownList.update();
			view.repaintView();
		}
	}

	void setKeyboardFocus(boolean keyboardFocus) {
		selector.setKeyboardFocus(keyboardFocus);
	}

	int getItemCount() {
		return items.size();
	}

	boolean onMouseDown(int x, int y) {
		if (!visible) {
			return false;
		}

		if (handleUpControl(x, y) || handleDownControl(x, y)) {
			return true;
		}

		if (scroller.isActive() && !scroller.isDragging()) {
			drawDropDownList.dropDown.startClickTimer(x, y);
		}
		clickStarted = true;
		return true;
	}

	boolean onClick(int x, int y) {
		OptionItem item = items.at(x, y);

		if (item == null || !clickStarted) {
			return false;
		}
		chooseItem(item.getIndex());
		clickStarted = false;
		return true;
	}

	private void chooseItem(int index) {
		range.setSelected(index);
		geoList.setSelectedIndexUpdate(index);
		setDragging(false);
		setVisible(false);
	}

	private void updateDragOffset(int dY) {
		selector.setDragOffset(dY);
		view.repaintView();
	}

	boolean scrollByDrag(int x, int y) {
		OptionItem item = items.at(x, y);
		if (item != null) {
			GPoint startPoint = new GPoint(x, y);
			if (selector.hasNoDraggedItem()) {
				selector.setDragged(item, startPoint);
				return true;
			}

			int d = selector.getYDifference(startPoint);
			int dY = Math.abs(d);

			int itemHeight = (int) item.getRect().getHeight();

			if (dY > 0) {
				setDragging(true);
			}

			int itemDiffs = dY / itemHeight;
			if (itemDiffs != 0) {
				selector.setDragOffset(dY % itemHeight);
				scroller.scrollByItems(itemDiffs);
				selector.setDragged(item, startPoint);
			} else if (model.isScrollBoundsValid()) {
				updateDragOffset(dY);
			}
		}
		return false;
	}

	private void setDragging(boolean value) {
		if (!scroller.isActive()) {
			return;
		}
		scroller.setDragging(value);
		if (value) {
			drawDropDownList.dropDown.stopClickTimer();
		} else {
			selector.clearDragged();
		}
	}

	void onMouseOver(int x, int y) {
		if (!drawDropDownList.isVisible) {
			return;
		}

		if (!isHit(x, y)) {
			if (scroller.isActive()) {
				scroller.stop();
				setDragging(false);
			}
			return;
		}

		if (scroller.isDragging()) {
			return;
		}
		OptionItem item = items.at(x, y);

		selector.setHighlighted(item);
		selector.setKeyboardFocus(false);
		view.repaintView();
	}

	boolean isVisible() {
		return visible;
	}

	void setVisible(boolean visible) {
		if (visible) {
			ScreenReader.readDropDownOpened(geoList);
		}
		if (this.visible != visible) {
			app.dispatchEvent(getOpenClosedEvent(visible));
			selector.clearHighlighted();
		}
		this.visible = visible;

		if (visible) {
			view.setOpenedComboBox(drawDropDownList);
			updateVisibleRange();
			range.selectStart();
			updateHighlighting();
		} else {
			clickStarted = false;
		}

		view.repaintView();
		drawDropDownList.updateOpenedComboBox();
	}

	private void updateHighlighting() {
		selector.setHighlighted(model.isScrollBoundsValid()
				? items.get(range.getSelected()) : null);
		selector.setKeyboardFocus(true);
	}

	private void updateVisibleRange() {
		if (scroller.isActive()) {
			range.adjustToSelected();
		} else {
			range.reset();
		}
	}

	void onResize(int w, int h) {
		if (model.resizeView(w, h)) {
			selector.clearHighlighted();
		}
	}

	void toggle() {
		if (visible) {
			int highlightedIndex = selector.getHighlightedIndex();
			if (highlightedIndex > -1 && selector.hasKeyboardFocus()) {
				chooseItem(highlightedIndex);
			} else {
				setVisible(false);
			}
			app.setActiveView(view.getViewID());
		} else {
			setVisible(true);
		}
	}

	@Override
	public void moveSelectorVertical(boolean moveDown) {
		selector.moveSelectorVertical(moveDown);
	}

	@Override
	public void moveSelectorHorizontal(boolean moveLeft) {
		selector.moveSelectorHorizontal(moveLeft);
	}

	int getMaxItemWidth() {
		return items.noDimension() ? 0 : items.getMaxWidth();
	}

	void onMouseUp(int x, int y) {
		if (scroller.isActive()) {
			onScrollMouseUp(x, y);
		} else {
			onClick(x, y);
		}
	}

	private void onScrollMouseUp(int x, int y) {
		scroller.stop();
		if (drawDropDownList.dropDown.isClickTimerRunning()) {
			drawDropDownList.dropDown.stopClickTimer();
			onClick(x, y);
		}
		setDragging(false);
	}

	int indexOf(OptionItem item) {
		return items.indexOf(item);
	}

	private Event getOpenClosedEvent(boolean visible) {
		EventType type = visible ? EventType.DROPDOWN_OPENED : EventType.DROPDOWN_CLOSED;
		Event evt = new Event(type, geoList);
		if (!visible) {
			HashMap<String, Object> args = new HashMap<>();
			args.put("index", geoList.getSelectedIndex());
			evt.setJsonArgument(args);
		}
		return evt;
	}

	void scrollUp() {
		scroller.scroll(DropDownScrollMode.UP);
	}

	void scrollDown() {
		scroller.scroll(DropDownScrollMode.DOWN);
	}

	void shiftRangeBy(int diff) {
		if (range.shiftBy(diff)) {
			drawDropDownList.update();
			view.repaintView();
		}
	}

	void cancelDrag() {
		selector.cancelDrag();
	}

	boolean getDragDirection() {
		return selector.isDragDirection();
	}
}
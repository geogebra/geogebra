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

class DrawOptions implements MoveSelector, OptionsInterface {
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
			items.fill(g2);
			table.update(drawDropDownList.getBoxWidth(), drawDropDownList.getLabelFontSize());
			table.draw(g2);
		}
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

	@Override
	public void setHoverIndex(int idx) {
		if (model.itemCount() <= idx) {
			items.update(model);
		}

		if (idx >= 0 && idx < items.size()) {
			selector.setHovered(items.get(idx));
			selector.setKeyboardFocus(true);
			model.setSelected(idx);
			drawDropDownList.update();
			view.repaintView();
		}
	}

	@Override
	public int getItemCount() {
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
		model.setSelected(index);
		selectCurrentItem();
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

		selector.setHovered(item);
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
			selector.clearHovered();
		}
		this.visible = visible;

		if (visible) {
			view.setOpenedComboBox(drawDropDownList);
			updateVisibleRange();
			range.selectStart();
			updateHovering();
		} else {
			clickStarted = false;
		}

		view.repaintView();
		drawDropDownList.updateOpenedComboBox();
	}

	private void updateHovering() {
		selector.setHovered(model.isScrollBoundsValid() ? items.get(model.getSelected()) : null);
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
			selector.clearHovered();
		}
	}

	void toggle() {
		if (visible) {
			int hoveredIndex = selector.hoveredIndex();
			if (hoveredIndex > -1) {
				chooseItem(hoveredIndex);
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

	void selectCurrentItem() {
		geoList.setSelectedIndexUpdate(range.getSelected());
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

	@Override
	public Object indexOf(OptionItem item) {
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

	@Override
	public void scrollUp() {
		scroller.scroll(DropDownScrollMode.UP);
	}

	@Override
	public void scrollDown() {
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
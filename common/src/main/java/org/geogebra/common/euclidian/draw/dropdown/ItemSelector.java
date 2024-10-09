package org.geogebra.common.euclidian.draw.dropdown;

import java.util.HashMap;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;

class ItemSelector implements MoveSelector {
	private final GeoList geoList;
	private final VisibleItemRange range;
	private OptionItem hovered;
	private boolean hasKeyboardFocus = false;
	private OptionItem dragged = null;
	private int dragOffset;
	private boolean dragDirection;
	private final DropDownModel model;
	private final OptionsInterface listener;
	private final App app;
	private GPoint dragStartPoint;

	ItemSelector(DropDownModel model, OptionsInterface listener) {
		this.model = model;
		this.listener = listener;
		hovered = null;
		geoList = model.getGeoList();
		app = geoList.getApp();
		range = model.getRange();
	}

	int getDragOffset() {
		return dragOffset;
	}

	int getDragOffsetWithDirection() {
		return dragDirection ? -dragOffset : dragOffset;
	}

	boolean isHovered(OptionItem item) {
		return item.isEqual(hovered);
	}

	boolean hasKeyboardFocus() {
		return hasKeyboardFocus;
	}

	public void setKeyboardFocus(boolean hasKeyboardFocus) {
		this.hasKeyboardFocus = hasKeyboardFocus;
	}

	void cancelDrag() {
		dragged = null;
		dragOffset = 0;
	}

	private void moveSelectorBy(int diff, boolean forward) {
		boolean update = false;
		boolean hasHovered = hovered != null;
		int idx = hasHovered ? hovered.getIndex() : 0;
		if (forward) {
			if (idx < listener.getItemCount() - diff) {
				idx += diff;
				update = true;
				if (idx > range.getEnd() - 1) {
					listener.scrollDown();
				}
			}
		} else {
			if (idx > diff - 1) {
				idx -= diff;
				update = true;
				if (idx < range.getStart()) {
					listener.scrollUp();
				}
			}
		}

		if (update) {
			listener.setHoverIndex(idx);
		}

		ScreenReader.readDropDownSelectorMoved(app,
				geoList, range.getSelected());
	}

	@Override
	public void moveSelectorVertical(boolean down) {
		cancelDrag();
		moveSelectorBy(1, down);
	}

	@Override
	public void moveSelectorHorizontal(boolean left) {
		moveSelectorBy(model.getRowCount(), left);
	}

	void setHovered(OptionItem item) {
		if (item == null || item.isEqual(hovered)) {
			return;
		}
		app.dispatchEvent(getFocusEvent(item));
		hovered = item;
	}

	private Event getFocusEvent(OptionItem item) {
		Event evt = new Event(EventType.DROPDOWN_ITEM_FOCUSED, geoList);
		HashMap<String, Object> args = new HashMap<>();
		args.put("index", listener.indexOf(item));
		evt.setJsonArgument(args);
		return evt;
	}

	void clearHovered() {
		hovered = null;
	}

	void clearDragged() {
		dragged = null;
		dragStartPoint = null;
	}

	boolean hasNoDraggedItem() {
		return dragged == null || dragStartPoint == null;
	}

	void setDragOffset(int dY) {
		dragOffset = dY;
	}

	void setDragged(OptionItem di, GPoint startPoint) {
		dragged = di;
		dragStartPoint = startPoint;
	}

	int getYDifference(GPoint startPoint) {
		int difference = dragStartPoint.y - startPoint.y;
		dragDirection = difference > 0;
		return difference;
	}

	boolean isDragDirection() {
		return dragDirection;
	}

	int hoveredIndex() {
		return hovered != null ? hovered.getIndex() : -1;
	}
}
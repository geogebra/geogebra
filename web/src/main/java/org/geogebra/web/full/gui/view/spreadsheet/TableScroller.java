package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.common.awt.GPoint;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.ui.AbstractNativeScrollbar;
import com.google.gwt.user.client.ui.ScrollPanel;

import elemental2.dom.Event;
import elemental2.dom.WheelEvent;

public class TableScroller extends ScrollPanel implements ScrollHandler {

	private final MyTableW table;
	private final SpreadsheetRowHeaderW rowHeader;
	private final SpreadsheetColumnHeaderW columnHeader;
	boolean doAdjustScroll = true;

	/**
	 * @param table
	 *            table
	 * @param rowHeader
	 *            row header
	 * @param columnHeader
	 *            column header
	 */
	public TableScroller(MyTableW table, SpreadsheetRowHeaderW rowHeader,
			SpreadsheetColumnHeaderW columnHeader) {
		super(table.getGridPanel());
		this.table = table;
		this.rowHeader = rowHeader;
		this.columnHeader = columnHeader;

		addScrollHandler(this);
		Dom.addEventListener(getElement(), "wheel", this::onMouseWheel);
	}

	private void onMouseWheel(Event event) {
		event.preventDefault();
		int delta = (int) Math.signum(((WheelEvent) event).deltaY);
		if (((WheelEvent) event).shiftKey) {
			adjustScroll(delta, 0);
		} else {
			adjustScroll(0, delta);
		}
	}

	/**
	 * Used by the scrollRectToVisible method to determine the proper direction
	 * and amount to move by. The integer variables are named width, but this
	 * method is applicable to height also. The code assumes that
	 * parentSize/childSize are positive and childAt can be negative.
	 */
	private static int positionAdjustment(int parentSize, int childSize,
			int childAt) {

		// +-----+
		// | --- | No Change
		// +-----+
		if (childAt >= 0 && childSize + childAt <= parentSize) {
			return 0;
		}

		// +-----+
		// --------- No Change
		// +-----+
		if (childAt <= 0 && childSize + childAt >= parentSize) {
			return 0;
		}

		// +-----+ +-----+
		// | ---- -> | ----|
		// +-----+ +-----+
		if (childAt > 0 && childSize <= parentSize) {
			return -childAt + parentSize - childSize;
		}

		// +-----+ +-----+
		// | -------- -> |--------
		// +-----+ +-----+
		if (childAt >= 0 && childSize >= parentSize) {
			return -childAt;
		}

		// +-----+ +-----+
		// ---- | -> |---- |
		// +-----+ +-----+
		if (childAt <= 0 && childSize <= parentSize) {
			return -childAt;
		}

		// +-----+ +-----+
		// -------- | -> --------|
		// +-----+ +-----+
		if (childAt < 0 && childSize >= parentSize) {
			return -childAt + parentSize - childSize;
		}

		return 0;
	}

	/**
	 * Scroll to make content visible.
	 * 
	 * @param x cell column
	 * @param y cell row
	 */
	public void scrollRectToVisible(int x, int y) {
		Scheduler.get().scheduleDeferred(() -> scrollRectToVisibleCommand(x, y));
	}

	/**
	 * Scrolls the view so that <code>Rectangle</code> within the view becomes
	 * visible.
	 * <p>
	 * This attempts to validate the view before scrolling if the view is
	 * currently not valid - <code>isValid</code> returns false. To avoid
	 * excessive validation when the containment hierarchy is being created this
	 * will not validate if one of the ancestors does not have a peer, or there
	 * is no validate root ancestor, or one of the ancestors is not a
	 * <code>Window</code> or <code>Applet</code>.
	 * <p>
	 * Note that this method will not scroll outside of the valid viewport; for
	 * example, if <code>contentRect</code> is larger than the viewport,
	 * scrolling will be confined to the viewport's bounds.
	 * 
	 */
	public void scrollRectToVisibleCommand(int x, int y) {
		Element view = this.getWidget().getElement();

		if (view == null) {
			return;
		}
		int dx, dy;

		int barHeight = AbstractNativeScrollbar.getNativeScrollbarHeight();
		int barWidth = AbstractNativeScrollbar.getNativeScrollbarWidth();
		int extentWidth = this.getOffsetWidth() - barWidth;
		int extentHeight = this.getOffsetHeight() - barHeight;
		GPoint viewPosition = getViewPosition();
		GPoint position = table.getPixelRelative(x, y);
		GPoint position2 = table.getPixelRelative(x + 1, y + 1);
		dx = positionAdjustment(extentWidth, position2.x - position.x,
				position.x - viewPosition.x);
		dy = positionAdjustment(extentHeight, position2.y - position.y,
				position.y - viewPosition.y);
		if (dx != 0 || dy != 0) {

			int viewWidth = view.getOffsetWidth();
			int viewHeight = view.getOffsetHeight();
			int startX = viewPosition.x;
			int startY = viewPosition.y;

			viewPosition.x -= dx;
			viewPosition.y -= dy;

			// TODO In the java code a check is made here for component
			// orientation, we may do this on the future?

			if (extentWidth > viewWidth) {
				viewPosition.x = viewWidth - extentWidth;
			} else {
				viewPosition.x = Math
				        .max(0, Math.min(viewWidth - extentWidth,
				                viewPosition.x));
			}

			if (viewPosition.y + extentHeight > viewHeight) {
				viewPosition.y = Math.max(0, viewHeight - extentHeight);
			} else if (viewPosition.y < 0) {
				viewPosition.y = 0;
			}
			if (viewPosition.x != startX || viewPosition.y != startY) {
				GPoint anchorPosition = getUpperLeftCellPosition(viewPosition, 0, 0);
				doAdjustScroll = false;
				setViewPosition(anchorPosition == null ? viewPosition : anchorPosition);
				doAdjustScroll = true;
			}
		}
	}

	private void setViewPosition(GPoint viewPosition) {
		this.setHorizontalScrollPosition(viewPosition.x);
		this.setVerticalScrollPosition(viewPosition.y);
		syncHeaders();
	}

	private GPoint getViewPosition() {
		return new GPoint(getHorizontalScrollPosition(),
		        getVerticalScrollPosition());
	}

	protected void adjustScroll(int dx, int dy) {
		if (!doAdjustScroll) {
			return;
		}

		GPoint viewPosition = getViewPosition();
		GPoint upperLeftCellPosition = getUpperLeftCellPosition(viewPosition, dx, dy);

		if (upperLeftCellPosition != null && upperLeftCellPosition.distance(viewPosition) > 2) {
			doAdjustScroll = false;
			setHorizontalScrollPosition(upperLeftCellPosition.x);
			setVerticalScrollPosition(upperLeftCellPosition.y);
			doAdjustScroll = true;
		}
	}

	private GPoint getUpperLeftCellPosition(GPoint viewPosition, int dx, int dy) {
		// get upper left cell coordinates
		int x = table.getIndexFromPixelRelativeX(viewPosition.x);
		int y = table.getIndexFromPixelRelativeY(viewPosition.y);
		if (x < 0 || y < 0) {
			return null;
		}

		// get new pixel coordinates to place the upper left cell exactly
		return table.getPixelRelative(x + dx, y + dy);
	}

	@Override
	public void onScroll(ScrollEvent event) {
		adjustScroll(0, 0);
		syncHeaders();
	}
	
	private void syncHeaders() {
		int t = -getVerticalScrollPosition();
		int l = -getHorizontalScrollPosition();
		rowHeader.setTop(t);
		columnHeader.setLeft(l);
	}
	
	/* 
	 * Fits the content of spreadsheet for its header on the left.
	 */
	public void syncTableTop() {
		setVerticalScrollPosition(rowHeader.getTop());
	}

	/**
	 * @param showHScrollBar
	 *            true = hide the horizontal scroll bar
	 */
	public void setShowHScrollBar(boolean showHScrollBar) {
		getScrollableElement().getStyle().setOverflowX(
		        showHScrollBar ? Style.Overflow.AUTO : Style.Overflow.HIDDEN);
	}

	/**
	 * @param showVScrollBar true = hide the vertical scroll bar
	 */
	public void setShowVScrollBar(boolean showVScrollBar) {
		getScrollableElement().getStyle().setOverflowY(
		        showVScrollBar ? Style.Overflow.AUTO : Style.Overflow.HIDDEN);
	}

}

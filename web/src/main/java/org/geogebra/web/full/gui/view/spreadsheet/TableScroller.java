package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.ui.AbstractNativeScrollbar;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ScrollPanel;

import elemental2.dom.Event;
import elemental2.dom.WheelEvent;

public class TableScroller extends ScrollPanel implements ScrollHandler {

	private final MyTableW table;
	private final Grid cellTable;
	private final SpreadsheetRowHeaderW rowHeader;
	private final SpreadsheetColumnHeaderW columnHeader;
	GRectangle contentRect;
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
		this.cellTable = table.getGrid();
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
	 * @param contentRect1
	 *            content rectangle
	 */
	public void scrollRectToVisible(GRectangle contentRect1) {
		this.contentRect = contentRect1;
		Scheduler.get().scheduleDeferred(this::scrollRectToVisibleCommand);
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
	public void scrollRectToVisibleCommand() {

		Element view = this.getWidget().getElement();

		if (view == null) {
			return;
		}
		int dx, dy;

		int barHeight = AbstractNativeScrollbar.getNativeScrollbarHeight();
		int barWidth = AbstractNativeScrollbar.getNativeScrollbarWidth();
		int extentWidth = this.getOffsetWidth() - barWidth;
		int extentHeight = this.getOffsetHeight() - barHeight;

		dx = positionAdjustment(extentWidth,
		        (int) contentRect.getWidth(), (int) contentRect.getX()
		                - getAbsoluteLeft());
		dy = positionAdjustment(extentHeight,
		        (int) contentRect.getHeight(), (int) contentRect.getY()
		                - getAbsoluteTop());

		// App.debug("-------- dx / dy : " + dx + " / " + dy);
		if (dx != 0 || dy != 0) {
			GPoint viewPosition = getViewPosition();
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
				GPoint anchorPosition = getUpperLeftCellPosition(viewPosition,
						(int) Math.signum(viewPosition.x - startX),
						(int) Math.signum(viewPosition.y - startY));
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
		int offH = cellTable.getAbsoluteLeft();
		int offV = cellTable.getAbsoluteTop();

		// get pixel coordinates of the upper left corner
		int x = viewPosition.x + offH;
		int y = viewPosition.y + offV;

		// get upper left cell coordinates
		GPoint p = table.getIndexFromPixel(x, y, 1);
		if (p == null) {
			return null;
		}

		// get new pixel coordinates to place the upper left cell exactly
		GPoint p2 = table.getPixel(p.x + dx, p.y + dy, true);
		if (p2 == null) {
			return null;
		}

		return new GPoint(p2.x - offH, p2.y - offV);
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

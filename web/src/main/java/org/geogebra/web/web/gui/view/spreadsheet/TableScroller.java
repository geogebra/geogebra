package org.geogebra.web.web.gui.view.spreadsheet;

import javax.swing.JComponent;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.ui.AbstractNativeScrollbar;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ScrollPanel;

public class TableScroller extends ScrollPanel implements ScrollHandler {

	private MyTableW table;
	private Grid cellTable;
	private SpreadsheetRowHeaderW rowHeader;
	private SpreadsheetColumnHeaderW columnHeader;

	public TableScroller(MyTableW table, SpreadsheetRowHeaderW rowHeader,
			SpreadsheetColumnHeaderW columnHeader) {
		super(table.getGridPanel());
		this.table = table;
		this.cellTable = table.getGrid();
		this.rowHeader = rowHeader;
		this.columnHeader = columnHeader;

		addScrollHandler(this);
	}

	/**
	 * Used by the scrollRectToVisible method to determine the proper direction
	 * and amount to move by. The integer variables are named width, but this
	 * method is applicable to height also. The code assumes that
	 * parentWidth/childWidth are positive and childAt can be negative.
	 */
	private int positionAdjustment(int parentWidth, int childWidth, int childAt) {

		// App.debug("parent width = " + parentWidth);
		// App.debug("child width = " + childWidth);
		// App.debug("child at = " + childAt);
		// +-----+
		// | --- | No Change
		// +-----+
		if (childAt >= 0 && childWidth + childAt <= parentWidth) {
			return 0;
		}

		// +-----+
		// --------- No Change
		// +-----+
		if (childAt <= 0 && childWidth + childAt >= parentWidth) {
			return 0;
		}

		// +-----+ +-----+
		// | ---- -> | ----|
		// +-----+ +-----+
		if (childAt > 0 && childWidth <= parentWidth) {
			return -childAt + parentWidth - childWidth;
		}

		// +-----+ +-----+
		// | -------- -> |--------
		// +-----+ +-----+
		if (childAt >= 0 && childWidth >= parentWidth) {
			return -childAt;
		}

		// +-----+ +-----+
		// ---- | -> |---- |
		// +-----+ +-----+
		if (childAt <= 0 && childWidth <= parentWidth) {
			return -childAt;
		}

		// +-----+ +-----+
		// -------- | -> --------|
		// +-----+ +-----+
		if (childAt < 0 && childWidth >= parentWidth) {
			return -childAt + parentWidth - childWidth;
		}

		return 0;
	}

	public void scrollRectToVisible(GRectangle contentRect) {
		this.contentRect = contentRect;
		Scheduler.get().scheduleDeferred(scrollRectCommand);
	}

	GRectangle contentRect;

	Scheduler.ScheduledCommand scrollRectCommand = new Scheduler.ScheduledCommand() {
		public void execute() {
			scrollRectToVisibleCommand();
		}
	};

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
	 * @param contentRect
	 *            the <code>Rectangle</code> to display
	 * @see JComponent#isValidateRoot
	 * @see java.awt.Component#isValid
	 * @see java.awt.Component#getPeer
	 */
	public void scrollRectToVisibleCommand() {

		Element view = this.getWidget().getElement();

		if (view == null) {
			return;
		}
		int dx, dy;

		int barHeight = AbstractNativeScrollbar.getNativeScrollbarHeight();
		int barWidth = AbstractNativeScrollbar.getNativeScrollbarWidth();

		dx = positionAdjustment(this.getOffsetWidth() - barWidth,
		        (int) contentRect.getWidth(), (int) contentRect.getX()
		                - getAbsoluteLeft());
		dy = positionAdjustment(this.getOffsetHeight() - barHeight,
		        (int) contentRect.getHeight(), (int) contentRect.getY()
		                - getAbsoluteTop());

		// App.debug("-------- dx / dy : " + dx + " / " + dy);
		if (dx != 0 || dy != 0) {
			GPoint viewPosition = getViewPosition();
			Dimension viewSize = new Dimension(view.getOffsetWidth(),
			        view.getOffsetHeight());
			int startX = viewPosition.x;
			int startY = viewPosition.y;
			Dimension extent = new Dimension(this.getOffsetWidth() - barWidth,
			        this.getOffsetHeight() - barHeight);

			// App.debug("viewSize w / h : " + viewSize.width + " / " +
			// viewSize.height);
			// App.debug("viewPosition x / y : " + viewPosition.x + " / " +
			// viewPosition.y);

			viewPosition.x -= dx;
			viewPosition.y -= dy;

			// TODO In the java code a check is made here for component
			// orientation, we may do this on the future?

			if (extent.width > viewSize.width) {
				viewPosition.x = viewSize.width - extent.width;
			} else {
				viewPosition.x = Math
				        .max(0, Math.min(viewSize.width - extent.width,
				                viewPosition.x));
			}

			if (viewPosition.y + extent.height > viewSize.height) {
				viewPosition.y = Math.max(0, viewSize.height - extent.height);
			} else if (viewPosition.y < 0) {
				viewPosition.y = 0;
			}

			// App.debug("viewPosition x / y : " + viewPosition.x + " / " +
			// viewPosition.y);

			if (viewPosition.x != startX || viewPosition.y != startY) {
				doAdjustScroll = false;
				setViewPosition(viewPosition);
				// NOTE: How JViewport currently works with the
				// backing store is not foolproof. The sequence of
				// events when setViewPosition
				// (scrollRectToVisible) is called is to reset the
				// views bounds, which causes a repaint on the
				// visible region and sets an ivar indicating
				// scrolling (scrollUnderway). When
				// JViewport.paint is invoked if scrollUnderway is
				// true, the backing store is blitted. This fails
				// if between the time setViewPosition is invoked
				// and paint is received another repaint is queued
				// indicating part of the view is invalid. There
				// is no way for JViewport to notice another
				// repaint has occured and it ends up blitting
				// what is now a dirty region and the repaint is
				// never delivered.
				// It just so happens JTable encounters this
				// behavior by way of scrollRectToVisible, for
				// this reason scrollUnderway is set to false
				// here, which effectively disables the backing
				// store.
				// scrollUnderway = false;
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

	private class Dimension {
		int height;
		int width;

		public Dimension(int width, int height) {
			this.width = width;
			this.height = height;
		}
	}

	boolean doAdjustScroll = true;

	protected void adjustScroll() {

		if (!doAdjustScroll) {
			return;
		}

		int offH = cellTable.getAbsoluteLeft();
		int offV = cellTable.getAbsoluteTop();

		// get pixel coordinates of the upper left corner
		int x = getHorizontalScrollPosition() + offH;
		int y = getVerticalScrollPosition() + offV;

		// get upper left cell coordinates
		GPoint p = table.getIndexFromPixel(x, y);
		if (p == null) {
			return;
		}

		// get new pixel coordinates to place the upper left cell exactly
		GPoint p2 = table.getPixel(p.x, p.y, true);
		if (p2 == null) {
			return;
		}

		// now scroll to move the upper left cell into position
		int newScrollH = p2.x - offH;
		int newScrollV = p2.y - offV;
		// App.debug("scroll: " + x + " , " + y + "  col,row: " + p.x + " , "
		// + p.y + "  scroll2: " + newScrollH + " , " + newScrollV);

		doAdjustScroll = false;
		setHorizontalScrollPosition(newScrollH);
		setVerticalScrollPosition(newScrollV);
		doAdjustScroll = true;
	}

	public void onScroll(ScrollEvent event) {
		adjustScroll();
		syncHeaders();
	}
	
	private void syncHeaders(){
		
		int t = -getVerticalScrollPosition();
		int l = -getHorizontalScrollPosition();
		rowHeader.setTop(t);
		columnHeader.setLeft(l);
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

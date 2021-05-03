/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * DrawPoint.java
 *
 * Created on 11. Oktober 2001, 23:59
 */

package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.util.DropDownList;
import org.geogebra.common.gui.util.DropDownList.DropDownListener;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.ScreenLocation;
import org.geogebra.common.main.App;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;

import com.google.j2objc.annotations.WeakOuter;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Draw a GeoList containing drawable objects
 * 
 * @author Markus Hohenwarter
 */
public final class DrawDropDownList extends CanvasDrawable
		implements DropDownListener {
	private static final int LABEL_COMBO_GAP = 10;
	private static final int COMBO_TEXT_MARGIN = 5;

	/** coresponding list as geo */
	GeoList geoList;
	/** whether this is visible */
	boolean isVisible;
	/** dropdown */
	DropDownList dropDown;
	/** selcted text */
	String selectedText;

	private GRectangle ctrlRect;
	private GDimension selectedDimension;
	private boolean latexLabel;

	private DrawOptions drawOptions;
	private boolean seLatex;

	private enum ScrollMode {
		UP, DOWN, NONE
	}

	@WeakOuter
	private class DrawOptions {
		private static final int MARGIN = 5;

		private static final int ROUND = 8;
		final EuclidianView viewOpt;
		private int viewHeight = 0;
		private int viewWidth = 0;

		// private static final int MAX_COLS_NO_FONT_CHANGE = 5;

		private static final int MIN_FONT_SIZE = 12;

		private int colCount = 1;
		private int rowCount = 1;
		private GRectangle rectTable;
		private GDimension dimItem;
		private GDimension dimTable;
		private int left;
		private int top;
		private int xPadding;
		private int yPadding;
		GFont itemFont;

		private final List<OptionItem> items;
		private OptionItem hovered;
		private GColor hoverColor;
		private boolean visible;

		private int selectedIndex;

		private int itemFontSize;

		// startIdx and endIdx defines the range of items that are visible.
		private int startIdx = -1;
		private int endIdx;
		private GRectangle rectUp;
		private GRectangle rectDown;

		private boolean scrollNeeded = false;
		private ScrollMode scrollMode = ScrollMode.NONE;
		private DraggedItem dragged = null;

		private int dragOffset;

		private boolean dragging = false;

		private boolean dragDirection;

		private class OptionItem {
			public int index;
			public int width;
			public int height;
			public String text;
			public boolean latex;
			GRectangle rect;

			public OptionItem(GGraphics2D g2, int idx) {
				index = idx;
				GeoElement geoItem = geoList.get(idx);
				if (GeoList.needsLatex(geoItem)) {
					text = geoItem.toLaTeXString(false,
							StringTemplate.latexTemplate);
					latex = true;
				} else {
					text = geoList.getItemDisplayString(geoItem,
							StringTemplate.defaultTemplate);
					latex = isLatexString(text);
				}

				if (!"".equals(text)) {
					if (latex) {
						GDimension d = measureLatex(g2, geoList, itemFont,
								text);
						width = d.getWidth();
						height = d.getHeight();
					} else {
						GTextLayout layout = getLayout(g2, text, itemFont);
						width = (int) Math.round(layout.getBounds().getWidth());
						height = (int) Math
								.round(layout.getBounds().getHeight());
					}
				}
				rect = null;
			}

			public GRectangle getRect() {
				return rect;
			}

			/**
			 * Two OptionItems are equal iff they indexes are the same.
			 * 
			 * @param item
			 *            to compare.
			 * @return if equal.
			 */
			public boolean isEqual(OptionItem item) {
				if (item == null) {
					return false;
				}
				return index == item.index;
			}

			public boolean isHit(int x, int y) {
				return rect != null && rect.contains(x, y);
			}

			public void setRect(GRectangle rect) {
				this.rect = rect;
			}
		}

		private class DraggedItem {
			public GPoint startPoint;
			public OptionItem item;

			public DraggedItem(int x, int y) {
				item = getItemAt(x, y);
				startPoint = new GPoint(x, y);
			}

			public boolean isValid() {
				return item != null;
			}
		}

		public DrawOptions(EuclidianView view) {
			this.viewOpt = view;
			items = new ArrayList<>();
			hovered = null;
			hoverColor = GColor.LIGHT_GRAY;
		}

		private int getStartIdx() {
			return getColCount() == 1 ? startIdx : 0;
		}

		private int getEndIdx() {
			return getColCount() == 1 ? endIdx : items.size();
		}

		private boolean isScrollNeeded() {
			return scrollNeeded;
		}

		public void draw(GGraphics2D graphics2, int leftPos, int topPos) {
			if (!isVisible()) {
				return;
			}

			if (startIdx == -1 && !"".equals(selectedText)) {
				startIdx = isScrollNeeded() ? geoList.getSelectedIndex() : 0;

			}

			this.left = leftPos;
			this.top = topPos;

			getMetrics(graphics2);
			drawBox(graphics2);
			drawItems(graphics2);
			if (isScrollNeeded()) {
				drawControls(graphics2);
			}
		}

		private void drawItems(GGraphics2D g2) {
			int idx = getStartIdx();
			int startRow = 0;
			int visibleRows = rowCount;

			if (isScrollNeeded() && idx >= 0 && dragOffset != 0) {
				idx--;
				startRow = -1;
				visibleRows++;
			}

			for (int col = 0; col < getColCount(); col++) {
				for (int row = startRow; row < visibleRows; row++) {
					if (idx >= 0 && idx < items.size()) {
						drawItem(g2, col, row, items.get(idx));
					}
					idx++;
				}
			}
		}

		private void drawItem(GGraphics2D g2, int col, int row, OptionItem item) {
			int rectLeft = left + dimItem.getWidth() * col;
			int rectTop = top + dimItem.getHeight() * row;

			if (isScrollNeeded()) {
				rectTop += rectUp.getHeight();
				if (dragDirection) {
					rectTop -= dragOffset;
				} else {
					rectTop += dragOffset;

				}
			}

			if (item.getRect() == null || item.getRect().getX() != rectLeft
					|| item.getRect().getY() != rectTop) {
				item.setRect(AwtFactory.getPrototype().newRectangle(rectLeft,
						rectTop, dimItem.getWidth(), dimItem.getHeight()));
			}
			drawItem(g2, item, item.isEqual(hovered));
		}

		private void drawItem(GGraphics2D g2, OptionItem item, boolean hover) {
			if (item.rect == null) {
				return;
			}
			int rectLeft = (int) item.rect.getBounds().getX();
			int rectTop = (int) item.rect.getBounds().getY();
			int rectBottom = rectTop + (int) item.rect.getBounds().getHeight();

			boolean clip = false;
			if (this.isScrollNeeded()) {
				int ctrlUpY = (int) (rectUp.getBounds().getY()
						+ rectUp.getBounds().getHeight());
				int ctrlDownBottom = (int) (rectDown.getBounds().getY()
						+ rectDown.getBounds().getHeight());

				// no extra item drawing.
				if ((item.index == startIdx - 1 && rectBottom < ctrlUpY)
						|| (ctrlDownBottom < rectBottom)) {
					return;
				}

				clip = dragOffset != 0 && ((item.rect.intersects(rectUp)
						|| item.rect.intersects(rectDown)));

				if (clip) {
					// Log.debug("CLIPPING");
					g2.setClip(rectLeft, ctrlUpY, (int) item.rect.getWidth(),
							(int) (rectDown.getY() - ctrlUpY), true);
				}
			}

			int itemHeight = dimItem.getHeight();
			if (hover) {
				g2.setColor(hoverColor);
				g2.fillRoundRect(rectLeft, rectTop, dimItem.getWidth(),
						itemHeight, ROUND, ROUND);

			} else {
				g2.setColor(geoList.getBackgroundColor());
				g2.fillRect(rectLeft, rectTop, dimItem.getWidth(), itemHeight);

			}

			if (item.getRect() == null) {
				item.setRect(AwtFactory.getPrototype().newRectangle(rectLeft,
						rectTop, dimItem.getWidth(), itemHeight));
			}

			g2.setPaint(getGeoElement().getObjectColor());

			if (item.latex) {
				GRectangle rect = item.rect.getBounds();
				int x = (int) rect.getX();
				int y = (int) rect.getY();

				drawLatex(g2, geoList, itemFont, item.text,
						x + (int) ((rect.getWidth() - item.width) / 2),
						y + (int) ((rect.getHeight() - item.height) / 2));
			} else {
				if (g2.getFont().getSize() != itemFontSize) {
					g2.setFont(itemFont.deriveFont(GFont.PLAIN, itemFontSize));
				}
				int x = (dimItem.getWidth() - item.width) / 2;
				int y = (itemHeight - yPadding);

				EuclidianStatic.drawIndexedString(viewOpt.getApplication(), g2,
						item.text, rectLeft + x, rectTop + y, false);
			}

			if (clip) {
				g2.resetClip();
			}
		}

		private void drawBox(GGraphics2D g2) {
			g2.setPaint(geoList.getBackgroundColor());
			g2.fillRoundRect(left - 1, top - 1, dimTable.getWidth() + 2,
					dimTable.getHeight() + 2, ROUND, ROUND);
			g2.setPaint(GColor.DARK_GRAY);
			g2.drawRoundRect(left - 1, top - 1, dimTable.getWidth() + 2,
					dimTable.getHeight() + 2, ROUND, ROUND);
		}

		public boolean isHit(int x, int y) {
			return isVisible()
					&& ((rectTable != null && rectTable.contains(x, y))
					|| (isScrollNeeded() && isControlHit(x, y)));
		}

		private boolean handleUpControl(int x, int y) {
			if (isScrollNeeded() && rectUp != null && rectUp.contains(x, y)) {
				scrollMode = ScrollMode.UP;
				dropDown.startScrollTimer(x, y);
				scrollUp();
				return true;
			}
			return false;
		}

		private boolean handleDownControl(int x, int y) {
			if (isScrollNeeded() && rectDown != null
					&& rectDown.contains(x, y)) {
				scrollMode = ScrollMode.DOWN;
				dropDown.startScrollTimer(x, y);
				scrollDown();
				return true;
			}

			return false;
		}

		public boolean isControlHit(int x, int y) {
			return isScrollNeeded()
					&& ((rectUp != null && rectUp.contains(x, y))
							|| (rectDown != null && rectDown.contains(x, y)));
		}

		private void setHovered(OptionItem item) {
			if (item == null || item.isEqual(hovered)) {
				return;
			}
			view.getApplication().dispatchEvent(getFocusEvent(item));
			hovered = item;
			viewOpt.repaintView();
		}

		private void setHoverIndex(int idx) {
			if (idx >= 0 && idx < items.size()) {
				setHovered(items.get(idx));
				selectedIndex = idx;
				update();

				getView().repaintView();
			}
		}

		void scrollUp() {
			cancelDrag();
			scrollBy(-1);
		}

		void scrollDown() {
			cancelDrag();
			scrollBy(1);
		}

		void scrollBy(int diff) {
			if (!isScrollNeeded()) {
				return;
			}

			if (startIdx + diff >= 0 && endIdx + diff < items.size() + 1) {
				startIdx += diff;
				selectedIndex += diff;

				update();
				getView().repaintView();
			}
		}

		public void scroll() {
			if (!isScrollNeeded()) {
				return;
			}

			switch (scrollMode) {
			case UP:
				scrollUp();
				break;
			case DOWN:
				scrollDown();
				break;
			case NONE:
				break;
			default:
				break;

			}
		}

		public boolean onMouseDown(int x, int y) {
			if (!visible) {
				return false;
			}

			if (handleUpControl(x, y) || handleDownControl(x, y)) {
				return true;
			}

			if (isScrollNeeded() && !isDragging()) {
				dropDown.startClickTimer(x, y);
			} else {
				return onClick(x, y);
			}
			return true;

		}

		private boolean isDragging() {
			if (!isScrollNeeded()) {
				return false;
			}

			return dragging;
		}

		public boolean onClick(int x, int y) {
			OptionItem item = getItemAt(x, y);

			if (item == null) {
				return false;
			}
			selectedIndex = item.index;
			selectCurrentItem();
			setDragging(false);
			setVisible(false);
			return true;
		}

		public boolean onDrag(int x, int y) {
			if (!isScrollNeeded()) {
				return false;
			}

			DraggedItem di = new DraggedItem(x, y);
			if (di.isValid()) {
				if (dragged == null || !dragged.isValid()) {
					dragged = di;
					return true;
				}

				int d = dragged.startPoint.getY() - di.startPoint.getY();
				dragDirection = d > 0;

				int dY = Math.abs(d);

				int itemHeight = (int) (di.item.getRect().getHeight());
				if (dY > 0) {
					setDragging(true);
				}

				int itemDiffs = dY / itemHeight;
				if (itemDiffs != 0) {
					dragOffset = dY % itemHeight;
					if (dragDirection) {
						scrollBy(itemDiffs);
					} else {
						scrollBy(-itemDiffs);

					}
					dragged = di;

				} else {

					if (getStartIdx() > 0 && getEndIdx() < geoList.size()) {
						dragOffset = dY;

						viewOpt.repaintView();
					}
				}

			}

			return true;
		}

		private void setDragging(boolean value) {
			if (!isScrollNeeded()) {
				return;
			}
			dragging = value;
			if (dragging) {
				dropDown.stopClickTimer();
			} else {
				dragged = null;

			}
		}

		public void onMouseOver(int x, int y) {
			if (!isVisible) {
				return;
			}

			if (!isHit(x, y)) {
				if (isScrollNeeded()) {
					stopScrolling();
					setDragging(false);
				}
				return;
			}

			if (isDragging()) {
				return;
			}
			OptionItem item = getItemAt(x, y);

			setHovered(item);
		}

		OptionItem getItemAt(int x, int y) {
			for (OptionItem item : items) {
				if (item.isHit(x, y)) {
					return item;
				}
			}

			return null;
		}

		private boolean prepareTable(GGraphics2D g2) {
			createItems(g2);
			return getTableScale();
		}

		private void getMetrics(GGraphics2D g2) {
			xPadding = 10;
			yPadding = 10;
			itemFontSize = getLabelFontSize();
			createItems(g2);
			if (!getScrollSettings()) {
				boolean finished = false;
				while (!finished && itemFontSize > MIN_FONT_SIZE) {
					finished = prepareTable(g2);
					itemFontSize--;
				}

				if (dimItem == null) {
					prepareTable(g2);
				}
			}

			int tableWidth = getColCount() * dimItem.getWidth();
			int tableHeight = rowCount * dimItem.getHeight();

			if (isScrollNeeded()
					&& (top + tableHeight + MARGIN <= viewHeight)) {
				tableHeight += dimItem.getHeight();
			}

			if (top + tableHeight + MARGIN >= viewOpt.getHeight()) {
				top = (viewOpt.getHeight() - tableHeight - MARGIN);
				if (top < MARGIN) {
					top = MARGIN;
					if (!isScrollNeeded()) {
						tableHeight -= MARGIN;
					}
				}

				if (isScrollNeeded()) {
					int h = viewOpt.getHeight();
					tableHeight += 2 * (int) rectDown.getHeight();
					if (tableHeight > h) {
						tableHeight = h - 2 * MARGIN;
					} else {
						top = (h - tableHeight) / 2;
					}
				}
			}

			dimTable = AwtFactory.getPrototype().newDimension(tableWidth,
					tableHeight);

			if (left + dimTable.getWidth() > viewOpt.getWidth()) {
				left = (viewOpt.getWidth() - dimTable.getWidth());
			}
			rectTable = AwtFactory.getPrototype().newRectangle(left,
					top + MARGIN, dimTable.getWidth(), dimTable.getHeight());

			if (isScrollNeeded()) {
				rectUp.setBounds(left, top, (int) (rectUp.getWidth()),
						(int) (rectUp.getHeight()));
				rectDown.setBounds(left,
						top + dimTable.getHeight()
								- (int) (rectDown.getHeight()),
						(int) (rectDown.getWidth()),
						(int) (rectDown.getHeight()));
			}

		}

		private void drawControls(GGraphics2D g2) {
			if (!isScrollNeeded()) {
				return;
			}

			int x = (int) rectUp.getX();
			int y = (int) rectUp.getY();
			int h = (int) rectUp.getHeight();
			int w = (int) rectUp.getWidth();
			if (y < MARGIN) {
				y = MARGIN;
			}

			g2.setPaint(geoList.getBackgroundColor());
			g2.fillRoundRect(x, y, w, h, ROUND, ROUND);
			dropDown.drawScrollUp(g2, x, y, w, h);

			int x2 = (int) rectDown.getX();
			int y2 = (int) rectDown.getY();
			int h2 = (int) rectDown.getHeight();
			int w2 = (int) rectDown.getWidth();

			g2.setPaint(geoList.getBackgroundColor());

			g2.fillRoundRect(x2, y2, w2, h2, ROUND, ROUND);
			dropDown.drawScrollDown(g2, x2, y2, w2, h2);

		}

		private void getOneColumnSettings() {
			setColCount(1);
			dimItem = AwtFactory.getPrototype()
					.newDimension(boxWidth > dimItem.getWidth() ? boxWidth
							: dimItem.getWidth(), dimItem.getHeight());

		}

		/**
		 * Gets scroll settings: visible items, boundaries.
		 * 
		 * @return if scroll really makes sense or multi-column would be better.
		 */
		private boolean getScrollSettings() {
			getOneColumnSettings();
			rectUp = AwtFactory.getPrototype().newRectangle(dimItem.getWidth(),
					dimItem.getHeight() / 2);
			rectDown = AwtFactory.getPrototype()
					.newRectangle(dimItem.getWidth(), dimItem.getHeight() / 2);

			int maxItems = geoList.size();

			int visibleItems = ((viewOpt.getHeight() - (2 * MARGIN))
					/ dimItem.getHeight()) - 1;

			if (visibleItems > maxItems) {
				// can't display more than this
				visibleItems = maxItems;
				scrollNeeded = false;
			} else if (visibleItems < maxItems - 1) {
				// The two additional arrows take an item by height.
				visibleItems--;
				scrollNeeded = true;
			}

			if (startIdx + visibleItems < maxItems) {
				endIdx = startIdx + visibleItems + 1;
			} else {
				startIdx = maxItems - visibleItems - 1;
				endIdx = maxItems;
			}
			rowCount = getVisibleItemCount();
			if (!scrollNeeded) {
				startIdx = 0;
				endIdx = maxItems;
			}

			scrollNeeded = scrollNeeded && rowCount > 2;
			if (scrollNeeded) {
				top = getScrollTop();
			}
			return scrollNeeded;
		}

		private int getScrollTop() {
			return MARGIN;
		}

		private int getVisibleItemCount() {
			int result = isScrollNeeded() ? getEndIdx() - getStartIdx()
					: items.size();
			// There will be always one row at least.
			return result > 0 ? result : 1;
		}

		/**
		 * Gets the columns and rows for options table.
		 * 
		 * @return true if all table columns are full, ie no column with fewer
		 *         items at the end.
		 */
		private boolean getTableScale() {
			int maxItems = geoList.size();
			int maxRows = ((viewOpt.getHeight() - 2 * MARGIN)
					/ dimItem.getHeight()) + 1;
			int maxCols = viewOpt.getWidth() / dimItem.getWidth();
			if (maxItems < maxRows) {
				getOneColumnSettings();
				rowCount = maxItems;
				return true;
			}

			setColCount(maxItems / maxRows + (maxItems % maxRows == 0 ? 0 : 1));
			rowCount = maxRows;

			balanceTable();
			return (colCount < maxCols);
		}

		private void balanceTable() {
			int itemCount = geoList.size();
			int resultRow = 0;

			int maxMod = 0;
			boolean found = false;
			int row = rowCount;
			while (!found && row > 2) {
				row--;
				int mod = itemCount % row;
				if (mod == 0) {
					resultRow = row;
					maxMod = 0;
					found = true;
				} else if (mod > maxMod) {
					maxMod = mod;
					resultRow = row;
				}

			}

			rowCount = resultRow == 0 ? 1 : resultRow;
			colCount = itemCount / rowCount + (maxMod == 0 ? 0 : 1);
		}

		private void createItems(GGraphics2D g2) {
			itemFont = getLabelFont().deriveFont(GFont.PLAIN, itemFontSize);
			double maxWidth = 0;
			double maxHeight = 0;
			items.clear();
			for (int i = 0; i < geoList.size(); i++) {
				OptionItem item = new OptionItem(g2, i);
				items.add(item);
				if (maxWidth < item.width) {
					maxWidth = item.width;
				}

				if (maxHeight < item.height) {
					maxHeight = item.height;
				}

			}

			dimItem = AwtFactory.getPrototype().newDimension(
					(int) (maxWidth + 2 * xPadding),
					(int) (maxHeight + 2 * yPadding));

		}

		public boolean isVisible() {
			return visible;
		}

		void setVisible(boolean visible) {
			if (visible) {
				ScreenReader.readDropDownOpened(geoList);
			}
			if (this.visible != visible) {
				view.getApplication().dispatchEvent(getOpenClosedEvent(visible));
			}
			this.visible = visible;
			if (visible) {
				viewOpt.setOpenedComboBox(DrawDropDownList.this);
				if (isScrollNeeded()) {
					int selIdx = geoList.getSelectedIndex();
					if (selIdx + getVisibleItemCount() < geoList.size()) {
						startIdx = selIdx;
					} else {
						startIdx = geoList.size() - getVisibleItemCount();
					}
				} else {
					startIdx = 0;
				}

				selectedIndex = startIdx;
				if (selectedIndex > 0 && selectedIndex < items.size()) {
					hovered = items.get(selectedIndex);
				} else {
					hovered = null;
				}
			}
			viewOpt.repaintView();
			updateOpenedComboBox();
		}

		public void onResize(int w, int h) {
			int dW = viewWidth - w;
			int dH = viewHeight - h;

			if (dW != 0 || dH != 0) {
				viewHeight = h;
				viewWidth = w;
				hovered = null;
			}

		}

		void toggle() {
			setVisible(!visible);
		}

		public void moveSelectorBy(int diff, boolean forward) {
			boolean update = false;
			boolean hasHovered = hovered != null;
			int idx = hasHovered ? hovered.index : 0;
			if (forward) {
				if (idx < items.size() - diff) {
					idx += diff;
					update = true;
					if (idx > endIdx - 1) {
						scrollDown();
					}
				}
			} else {
				if (idx > diff - 1) {
					idx -= diff;
					update = true;
					if (idx < startIdx) {
						scrollUp();
					}
				}
			}

			if (update) {
				setHoverIndex(idx);
			}

			ScreenReader.readDropDownSelectorMoved(view.getApplication(), geoList, selectedIndex);
		}

		private void cancelDrag() {
			dragged = null;
			dragOffset = 0;
		}

		public void moveSelectorVertical(boolean moveDown) {
			cancelDrag();
			moveSelectorBy(1, moveDown);
		}

		public void moveSelectorHorizontal(boolean moveLeft) {
			moveSelectorBy(rowCount, moveLeft);
		}

		public int getColCount() {
			return colCount;
		}

		public void setColCount(int colCount) {
			this.colCount = colCount;
		}

		public void selectCurrentItem() {
			geoList.setSelectedIndex(selectedIndex, true);
		}

		public int getMaxItemWidth() {
			return dimItem != null ? dimItem.getWidth() : 0;
		}

		private void stopScrolling() {
			dropDown.stopScrollTimer();
			scrollMode = ScrollMode.NONE;
		}

		public void onMouseUp(int x, int y) {
			stopScrolling();
			if (dropDown.isClickTimerRunning()) {
				dropDown.stopClickTimer();
				onClick(x, y);

			}
			setDragging(false);
		}

	}

	private Event getFocusEvent(DrawOptions.OptionItem item) {
		Event evt = new Event(EventType.DROPDOWN_ITEM_FOCUSED, geoList);
		HashMap<String, Object> args = new HashMap<>();
		args.put("index", drawOptions.items.indexOf(item));
		evt.setJsonArgument(args);
		return evt;
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

	/**
	 * Creates new drawable list
	 * 
	 * @param view
	 *            view
	 * @param geoList
	 *            list
	 */
	public DrawDropDownList(EuclidianView view, GeoList geoList) {
		this.view = view;
		this.geoList = geoList;
		geo = geoList;
		drawOptions = new DrawOptions(view);
		dropDown = new DropDownList(view.getApplication(), this);

		ctrlRect = AwtFactory.getPrototype().newRectangle();

		update();
	}

	private String getLabelText() {
		// don't need to worry about labeling options, just check if caption
		// set or not

		if (!"".equals(geo.getRawCaption())) {
			return geo.getCaption(StringTemplate.defaultTemplate);
		}

		// make sure there's something to drag
		return Unicode.NBSP + "" + Unicode.NBSP + "" + Unicode.NBSP;
	}

	@Override
	public void update() {
		isVisible = geo.isEuclidianVisible() && geoList.size() != 0;
		int fontSize = (int) (view.getFontSize()
				* geoList.getFontSizeMultiplier());
		setLabelFontSize(fontSize);

		if (!isVisible) {
			return;
		}

		// eg size changed etc
		labelDesc = getLabelText();

		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;
		labelRectangle.setBounds(xLabel, yLabel,
				(int) (getHitRect().getWidth()),
				(int) (getHitRect().getHeight()));
		geoList.setTotalWidth(getTotalWidth());
		geoList.setTotalHeight(getTotalHeight());
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (isVisible) {
			setLabelFont("");
			drawOnCanvas(g2);
		}
	}

	/**
	 * Returns whether any one of the list items is at the given screen
	 * position.
	 */
	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		DrawDropDownList opened = view.getOpenedComboBox();
		if (opened != null && opened != this && opened.isOptionsHit(x, y)) {
			return false;
		}

		return super.hit(x, y, hitThreshold) || isControlHit(x, y)
				|| isOptionsHit(x, y);
	}

	@Override
	public boolean isInside(GRectangle rect) {
		return super.isInside(rect);
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		return super.intersectsRectangle(rect);
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	public GRectangle getBounds() {

		if (getHitRect() == null) {
			return null;
		}

		return getHitRect().getBounds();
	}

	@Override
	protected void drawWidget(GGraphics2D g2) {
		updateMetrics(g2);

		String labelText = getLabelText();
		int textLeft = boxLeft + COMBO_TEXT_MARGIN;
		GColor bgColor = geo.getBackgroundColor() != null
				? geo.getBackgroundColor() : view.getBackgroundCommon();

		dropDown.drawSelected(geoList, g2, bgColor, boxLeft, boxTop, boxWidth,
				boxHeight);

		if (!geoList.hasScreenLocation() && boxWidth != 0) {
			geoList.setScreenLocation(xLabel < boxLeft ? xLabel : boxLeft,
					yLabel < boxTop ? yLabel : boxTop);
			ScreenLocation sloc = geoList.getScreenLocation();
			sloc.initWidth(boxWidth);
			sloc.initHeight(boxHeight);

		}

		g2.setPaint(GColor.LIGHT_GRAY);
		highlightLabel(g2, latexLabel);

		g2.setPaint(geo.getObjectColor());

		// Draw the selected line
		int textBottom;
		if (seLatex) {
			textBottom = boxTop
					+ (boxHeight - selectedDimension.getHeight()) / 2;
		} else {
			textBottom = alignTextToBottom(g2, boxTop, boxHeight, selectedText);
		}

		drawSelectedText(g2, textLeft, textBottom, true);

		drawControl(g2);

		if (geo.isLabelVisible()) {
			drawLabel(g2, geoList, labelText);
		}

		drawOptions.draw(g2, boxLeft, boxTop + boxHeight + 5);
	}

	private int alignTextToBottom(GGraphics2D g2, int top, int height,
			String text) {
		int base = (height + getTextDescent(g2, text)) / 2;
		return top + base + (height - base) / 2;
	}

	private int getTextDescent(GGraphics2D g2, String text) {
		// make sure layout won't be null ("" makes it null).

		GTextLayout layout = getLayout(g2, text, getLabelFont());
		return (int) (layout.getDescent());
	}

	private void drawLabel(GGraphics2D g2, GeoElement geo0, String text) {
		boolean latex = isLatexString(text);
		if (latex) {
			drawLatex(g2, geo0, getLabelFont(), text, xLabel,
					boxTop + (boxHeight - labelSize.y) / 2);

		} else {
			int textBottom = boxTop
					+ (boxHeight + getLabelFontSize() - COMBO_TEXT_MARGIN) / 2;
			g2.setPaint(geo.getObjectColor());
			g2.setFont(getLabelFont());
			EuclidianStatic.drawIndexedString(view.getApplication(), g2, text,
					xLabel, textBottom, false);
		}
	}

	@Override
	protected void highlightLabel(GGraphics2D g2, boolean latex) {
		if (geo.isLabelVisible() && isHighlighted() && latex) {
			g2.fillRect(xLabel, boxTop + (boxHeight - labelSize.y) / 2,
					labelSize.x, labelSize.y);

		} else {
			super.highlightLabel(g2, latex);
		}
	}

	private void drawControl(GGraphics2D g2) {
		g2.setPaint(GColor.BLACK);
		int left = boxLeft + boxWidth - boxHeight;

		ctrlRect.setBounds(boxLeft, boxTop, boxWidth, boxHeight);
		dropDown.drawControl(g2, left, boxTop, boxHeight, boxHeight);
	}

	@Override
	protected void calculateBoxBounds(boolean latex) {
		boxLeft = xLabel + labelSize.x + LABEL_COMBO_GAP;
		boxTop = latex
				? yLabel + (labelSize.y - getPreferredHeight()) / 2
				: yLabel;
		boxWidth = getPreferredWidth();
		boxHeight = getPreferredHeight() + COMBO_TEXT_MARGIN;
	}

	@Override
	protected void calculateBoxBounds() {
		boxLeft = xLabel + LABEL_COMBO_GAP;
		boxTop = yLabel;
		boxWidth = getPreferredWidth();
		boxHeight = getPreferredHeight();
	}

	private void updateMetrics(GGraphics2D g2) {
		drawOptions.onResize(view.getWidth(), view.getHeight());

		GeoElement geoItem = geoList.getSelectedElement();
		// boolean latex = false;
		if (GeoList.needsLatex(geoItem)) {
			selectedText = geoItem.toLaTeXString(false,
					StringTemplate.latexTemplate);
			seLatex = true;
		} else {
			// realTemplate: make sure Sequence((t,t),t,1,5) works
			selectedText = geoList.getItemDisplayString(geoItem,
					StringTemplate.realTemplate);
			seLatex = isLatexString(selectedText);
		}

		selectedDimension = drawSelectedText(g2, 0, 0, false);
		latexLabel = measureLabel(g2, geoList, getLabelText());
		labelRectangle.setBounds(boxLeft - 1, boxTop - 1, boxWidth, boxHeight);
	}

	private GDimension drawSelectedText(GGraphics2D g2, int left, int top,
			boolean draw) {

		GFont font = getLabelFont();

		if (seLatex) {
			GDimension d = null;
			d = draw ? drawLatex(g2, geoList, font, selectedText, left, top)
					: measureLatex(g2, geoList, font, selectedText);

			return d;
		}
		g2.setFont(font);

		GTextLayout layout = getLayout(g2, selectedText, font);

		final int w = (int) layout.getBounds().getWidth();

		if (draw) {
			EuclidianStatic.drawIndexedString(view.getApplication(), g2,
					selectedText, left, top, false);
		}

		return AwtFactory.getPrototype().newDimension(w,
				(int) Math.round(layout.getDescent() + layout.getAscent()));
	}

	private int getTriangleControlWidth() {
		return selectedDimension.getHeight();
	}

	@Override
	public int getPreferredWidth() {
		if (selectedDimension == null) {
			return 0;
		}
		int selectedWidth = selectedDimension.getWidth()
				+ (isLatexString(selectedText) ? 0 : 2 * COMBO_TEXT_MARGIN)
				+ getTriangleControlWidth();

		int maxItemWidth = drawOptions.getMaxItemWidth();
		return (isOptionsVisible() && maxItemWidth > selectedWidth)
				? maxItemWidth : selectedWidth;
	}

	/**
	 * 
	 * @return The whole width of the widget including the label.
	 */
	public int getTotalWidth() {
		return labelSize.getX() + getPreferredWidth();
	}

	/**
	 * 
	 * @return The height of the combo including the label
	 */
	public int getTotalHeight() {
		int h = labelSize.getY();
		return h > boxHeight ? h : boxHeight;
	}

	/**
	 * Returns if mouse is hit the options or not.
	 * 
	 * @param x
	 *            mouse x-coord
	 * @param y
	 *            mouse y-coord
	 * @return true if options rectangle hit by mouse.
	 */
	public boolean isOptionsHit(int x, int y) {

		return drawOptions.isHit(x, y);
	}

	/**
	 * Called when mouse is over options to highlight item.
	 * 
	 * @param x
	 *            mouse x-coord
	 * @param y
	 *            mouse y-coord
	 */
	public void onOptionOver(int x, int y) {
		drawOptions.onMouseOver(x, y);
	}

	/**
	 * Called when user presses down the mouse on the widget.
	 * 
	 * @param x
	 *            Mouse x coordinate.
	 * @param y
	 *            Mouse y coordinate.
	 */
	public void onMouseDown(int x, int y) {
		if (drawOptions.onMouseDown(x, y)) {
			return;
		}

		DrawDropDownList opened = view.getOpenedComboBox();
		if ((opened == null || !opened.isOptionsHit(x, y))
				&& isControlHit(x, y)) {
			boolean visible = isOptionsVisible();
			if (!visible) {
				// make sure keyboard controls work for the dropdown
				view.requestFocus();
			}
			setOptionsVisible(!visible);
		}
	}

	/**
	 * Close dropdown
	 */
	public void closeOptions() {
		setOptionsVisible(false);
	}

	/**
	 * @param x
	 *            mouse x-coord
	 * @param y
	 *            mouse y-coord
	 * @return whether control rectangle was hit
	 */
	public boolean isControlHit(int x, int y) {

		return ctrlRect != null && ctrlRect.contains(x, y);
	}

	/**
	 * @return whether dropdown is visible
	 */
	public boolean isOptionsVisible() {
		return drawOptions.isVisible();
	}

	/**
	 * @param optionsVisible
	 *            change visibility of dropdown items
	 */
	public void setOptionsVisible(boolean optionsVisible) {
		drawOptions.setVisible(optionsVisible);
	}

	/**
	 * toggle visibility of dropdown items
	 */
	public void toggleOptions() {
		drawOptions.toggle();

	}

	/**
	 * Sync selected index to GeoList
	 */
	public void selectCurrentItem() {
		if (!isOptionsVisible()) {
			return;
		}

		drawOptions.selectCurrentItem();
		closeOptions();
	}

	/**
	 * Gets DrawList for geo. No type check.
	 * 
	 * @param app
	 *            The current application.
	 * @param geo
	 *            The geo we like to get the DrawList for.
	 * @return The DrawList for the geo element;
	 * 
	 */
	public static @CheckForNull DrawDropDownList asDrawable(App app, GeoElement geo) {
		return (DrawDropDownList) app.getActiveEuclidianView()
				.getDrawableFor(geo);
	}

	/**
	 * Moves dropdown selector up or down by one item.
	 * 
	 * @param down
	 *            Sets if selection indicator should move down or up.
	 */
	public void moveSelectorVertical(boolean down) {

		drawOptions.moveSelectorVertical(down);
	}

	/**
	 * Moves the selector horizontally, if dropdown have more columns than one.
	 * 
	 * @param left
	 *            Indicates that selector should move left or right.
	 */
	public void moveSelectorHorizontal(boolean left) {
		drawOptions.moveSelectorHorizontal(left);
	}

	/**
	 * @return if combo have more columns than one.
	 */
	public boolean isMultiColumn() {
		return drawOptions.getColCount() > 1;
	}

	/**
	 * 
	 * @return if list when draw as combo, is selected.
	 */
	public boolean isSelected() {
		return geo.doHighlighting();
	}

	/**
	 * @param x
	 *            drag end x
	 * @param y
	 *            drag end y
	 * @return whether scroll was needed
	 */
	public boolean onDrag(int x, int y) {

		return drawOptions.onDrag(x, y);
	}

	@Override
	public void onScroll(int x, int y) {
		drawOptions.scroll();
	}

	/**
	 * @param delta
	 *            wheel scroll value; only sign matters
	 */
	public void onMouseWheel(double delta) {
		if (delta > 0) {
			drawOptions.scrollDown();
		} else {
			drawOptions.scrollUp();

		}
	}

	@Override
	public void onClick(int x, int y) {
		drawOptions.onClick(x, y);
	}

	/**
	 * Update the opened comobox variable for enclosing view
	 */
	void updateOpenedComboBox() {
		DrawDropDownList dl = view.getOpenedComboBox();
		if (drawOptions.isVisible()) {
			view.setOpenedComboBox(this);
		} else if (dl == this) {
			view.setOpenedComboBox(null);
		}
	}

	/**
	 * @param x
	 *            mouse x (within view)
	 * @param y
	 *            mouse y (within view)
	 */
	public void onMouseUp(int x, int y) {
		drawOptions.onMouseUp(x, y);
	}

	@Override
	public int getPreferredHeight() {
		if (selectedDimension == null) {
			return 0;
		}

		return selectedDimension.getHeight() + COMBO_TEXT_MARGIN;
	}

	/**
	 * @param idx index of the hovered item
	 */
	public void setHoverIndex(int idx) {
		if (drawOptions.items.size() <= idx) {
			drawOptions.createItems(view.getGraphicsForPen());
		}
		drawOptions.setHoverIndex(idx);
	}

	public int getOptionCount() {
		return drawOptions.items.size();
	}
}

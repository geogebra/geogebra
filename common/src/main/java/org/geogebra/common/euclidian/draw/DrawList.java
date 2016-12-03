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
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.RemoveNeeded;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.util.DropDownList;
import org.geogebra.common.gui.util.DropDownList.DropDownListener;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.WeakOuter;

/**
 * Draw a GeoList containing drawable objects
 * 
 * @author Markus Hohenwarter
 */
public final class DrawList extends CanvasDrawable
		implements RemoveNeeded, DropDownListener {
	private static final int LABEL_COMBO_GAP = 10;
	private static final int COMBO_TEXT_MARGIN = 5;

	/** coresponding list as geo */
	GeoList geoList;
	private DrawListArray drawables;
	/** whether this is visible */
	boolean isVisible;
	/** dropdown */
	DropDownList dropDown = null;
	/** selcted text */
	String selectedText;
	private int selectedHeight;
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

//		private static final int MAX_COLS_NO_FONT_CHANGE = 5;

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
				if (needsLatex(geoItem)) {
					text = geoItem.toLaTeXString(false,
							StringTemplate.latexTemplate);
					latex = true;
				} else {
					text = geoItem
							.toValueString(StringTemplate.defaultTemplate);
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

		private static final int MIN_FONT_SIZE = 12;

		private int colCount=1;
		private int rowCount=1;
		private GRectangle rectTable;
		private GDimension dimItem;
		private GDimension dimTable;
		private int left;
		private int top;
		private int xPadding;
		private int yPadding;
		GFont itemFont;

		private List<OptionItem> items;
		private OptionItem hovered;
		private GColor hoverColor;
		private GGraphics2D g2;
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

		public DrawOptions(EuclidianView view) {
			this.viewOpt = view;
			items = new ArrayList<DrawList.DrawOptions.OptionItem>();
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
			this.g2 = graphics2;

			getMetrics();
			drawBox();
			drawItems();
			if (isScrollNeeded()) {
				drawControls();
			}

		}

		private void drawItems() {

			int idx = getStartIdx();
			int startRow = 0;
			int visibleRows = rowCount;
			if (isScrollNeeded() && idx > 0 && dragOffset != 0) {
				idx--;
				startRow = -1;
				visibleRows++;
			}
				for (int col = 0; col < getColCount(); col++) {
				for (int row = startRow; row < visibleRows; row++) {
					if (idx < items.size()) {
						drawItem(col, row, items.get(idx));
					}
					idx++;
				}
			}

		}

		private void drawItem(int col, int row, OptionItem item) {

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


			drawItem(item, item.isEqual(hovered));
		}

		private void drawItem(OptionItem item, boolean hover) {
			if (item.rect == null) {
				return;
			}
			int rectLeft = (int) item.rect.getBounds().getX();
			int rectTop = (int) item.rect.getBounds().getY();
			int rectBottom = rectTop + (int) item.rect.getBounds().getHeight();

			boolean clip = false;
			if (this.isScrollNeeded()) {
				int ctrlUpY = (int) (rectUp.getBounds().getY() + rectUp
						.getBounds().getHeight());
				int ctrlDownBottom = (int) (rectDown.getBounds().getY()
						+ rectDown.getBounds().getHeight());

				// no extra item drawing.
				if ((item.index == startIdx - 1 && rectBottom < ctrlUpY)
						|| (ctrlDownBottom < rectBottom)
				) {
					return;
				}

				clip = dragOffset != 0
						&& ((item.rect.intersects(rectUp)
						|| item.rect.intersects(rectDown)));

				if (clip) {
					Log.debug("CLIPPING");
						g2.setClip(rectLeft, ctrlUpY, (int) item.rect.getWidth(),
						(int) (rectDown.getY() - ctrlUpY));
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
						item.text, rectLeft + x, rectTop + y, false,
						false);
			}
			if (clip) {
				g2.resetClip();
			}

		}

		private void drawBox() {
			g2.setPaint(geoList.getBackgroundColor());
			g2.fillRoundRect(left - 1, top - 1, dimTable.getWidth() + 2,
					dimTable.getHeight() + 2, ROUND, ROUND);
			g2.setPaint(GColor.LIGHT_GRAY);
			g2.drawRoundRect(left - 1, top - 1, dimTable.getWidth() + 2,
					dimTable.getHeight() + 2, ROUND, ROUND);
		}

		public boolean isHit(int x, int y) {
			return isVisible() && (rectTable.contains(x, y)
					|| (isScrollNeeded() && isControlHit(x, y)));
		}

		private boolean handleUpControl(int x, int y) {
			if (isScrollNeeded() && rectUp.contains(x, y)) {
				scrollMode = ScrollMode.UP;
				dropDown.startScrollTimer(x, y);
				scrollUp();
				return true;
			}
			return false;
		}

		private boolean handleDownControl(int x, int y) {
			if (isScrollNeeded() && rectDown.contains(x, y)) {
				scrollMode = ScrollMode.DOWN;
				dropDown.startScrollTimer(x, y);
				scrollDown();
				return true;
			}

			return false;
		}

		public boolean isControlHit(int x, int y) {
			return isScrollNeeded()
					&& (rectUp.contains(x, y) || rectDown.contains(x, y));
		}

		private void setHovered(OptionItem item) {

			if (item == null) {
				return;
			}

			if (item.isEqual(hovered)) {
				return;
			}

			drawHovered(false);
			hovered = item;
			drawHovered(true);
			viewOpt.repaintView();
		}

		private void drawHovered(boolean on) {
			if (hovered != null) {
				OptionItem item = items.get(hovered.index);
				drawItem(item, on);
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
				// Log.error("repaint 1");
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
						// Log.error("repaint 2");

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
			if (!isVisible) {// || isControlHit(x, y)) {
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

			// if (selectedIndex != item.index) {
			// selectedIndex = item.index;
			// viewOpt.repaintView();
			//
			// }
		}

		OptionItem getItemAt(int x, int y) {
			for (OptionItem item : items) {
				if (item.isHit(x, y)) {
					return item;
				}
			}

			return null;
		}

		// private boolean hoverIntersectControls() {
		// return isScrollNeeded() && itemHovered.rect != null
		// && (itemHovered.rect.intersects(rectUp)
		// || itemHovered.rect.intersects(rectDown));
		// }

		private boolean prepareTable() {
			itemFont = getLabelFont().deriveFont(GFont.PLAIN, itemFontSize);
			createItems();
			return getTableScale();
			
		}
		private void getMetrics() {
			xPadding = 10;
			yPadding = 10;
			itemFontSize = getLabelFontSize();

			if (!getScrollSettings()) {
				boolean finished = false;
				while (!finished && itemFontSize > MIN_FONT_SIZE) {
					finished = prepareTable();
					itemFontSize--;
				}

				if (dimItem == null) {
					prepareTable();
				}
			}

			int tableWidth = getColCount() * dimItem.getWidth();
			int tableHeight = rowCount * dimItem.getHeight();
			Log.debug("[!!] 1 tableHeight: " + tableHeight + " rowCount: "
					+ rowCount);
			if (isScrollNeeded()
					&& (top + tableHeight + MARGIN <= viewHeight))
			{
				tableHeight += dimItem.getHeight();
				// if (tableHeight + rectDown.getHeight() >= viewHeight) {
				// tableHeight = (int) (viewHeight - top - MARGIN
				// - 2 * rectDown.getHeight());
				// }
			}


			if (top + tableHeight + MARGIN >= viewOpt
					.getHeight()) {
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
					top + MARGIN,
					dimTable.getWidth(), dimTable.getHeight());

			if (isScrollNeeded()) {
				rectUp.setBounds(left, top,
						(int) (rectUp.getWidth()),
						(int) (rectUp.getHeight()));
				rectDown.setBounds(left,
						top + dimTable.getHeight()
								- (int) (rectDown.getHeight()),
						(int) (rectDown.getWidth()),
						(int) (rectDown.getHeight()));

				Log.debug("top: " + top + " tableHeight: " + tableHeight
						+ "viewHeight: " + viewOpt.getHeight());
				Log.debug("bottom: "
						+ (top + tableHeight + 2 * rectDown.getHeight())
						+ "viewHeight: " + viewOpt.getHeight());
				Log.debug("dimTable: " + dimTable.getHeight() + " down: "
						+ (top + dimTable.getHeight() + rectDown.getHeight()));
			}

		}

		private void drawControls() {
			if (!isScrollNeeded()) {
				return;
			}

			// g2.setPaint(GColor.YELLOW);
			int x = (int) rectUp.getX();
			int y = (int) rectUp.getY();
			int h = (int) rectUp.getHeight();
			int w = (int) rectUp.getWidth();
			if (y < MARGIN) {
				y = MARGIN;
			}
			// Log.debug(SCROLL_PFX + " drawing up control at (" + x + ", " + y
			// + ") w: " + w + " h: " + h);
			g2.setPaint(geoList.getBackgroundColor());
			g2.fillRoundRect(x, y, w, h, ROUND, ROUND);
			dropDown.drawScrollUp(g2, x, y, w, h,
					geoList.getBackgroundColor(), false);

			int x2 = (int) rectDown.getX();
			int y2 = (int) rectDown.getY();
			int h2 = (int) rectDown.getHeight();
			int w2 = (int) rectDown.getWidth();
			// Log.debug(SCROLL_PFX + " drawing up control at (" + x2 + ", " +
			// y2
			// + ") w: " + w2 + " h: " + h2);

			g2.setPaint(geoList.getBackgroundColor());

			g2.fillRoundRect(x2, y2, w2, h2, ROUND, ROUND);
			dropDown.drawScrollDown(g2, x2, y2, w2, h2,
					geoList.getBackgroundColor(), false);

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

			itemFont = getLabelFont().deriveFont(GFont.PLAIN, itemFontSize);
			createItems();
			getOneColumnSettings();
			rectUp = AwtFactory.getPrototype()
					.newRectangle(dimItem.getWidth(), dimItem.getHeight() / 2);
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
				// visibleItems = (viewOpt.getHeight() - (2 * MARGIN +
				// arrowsHeight))
				// / dimItem.getHeight();

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

			Log.debug(
					"[SCROLL] max: " + maxItems + " visible: " + visibleItems);
			Log.debug("[SCROLL]" + "startIdx: " + startIdx + " endIdx: "
					+ endIdx);

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
			// Log.debug("[BALANCE] mod: " + maxMod + " cols: " + colCount
			// + " rows: " + rowCount);
		}

		private void createItems() {

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

		boolean setVisible(boolean visible) {

			boolean repaintNeeded = this.visible != visible;

			this.visible = visible;
			if (visible) {
				viewOpt.setOpenedComboBox(DrawList.this);
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
				if (selectedIndex < items.size()) {
					hovered = items.get(selectedIndex);
				} else {
					hovered = null;
				}
			}
			viewOpt.repaintView();
			updateOpenedComboBox();
			// Log.error("repaint 4");

			repaintNeeded = true;
			// instead of: viewOpt.repaintView();

			return repaintNeeded;
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
				hovered = items.get(idx);
				selectedIndex = idx;
				update();
				// Log.error("repaint 6");

				getView().repaintView();
			}
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
	/**
	 * Creates new drawable list
	 * 
	 * @param view
	 *            view
	 * @param geoList
	 *            list
	 */
	public DrawList(EuclidianView view, GeoList geoList) {
		this.view = view;
		this.geoList = geoList;
		geo = geoList;
		drawOptions = new DrawOptions(view);
		dropDown = new DropDownList(view.getApplication(), this);

		ctrlRect = AwtFactory.getPrototype().newRectangle();
		reset();

		update();
	}

	private void reset() {

		if (!geoList.drawAsComboBox()) {

			if (drawables == null) {
				drawables = new DrawListArray(view);
			}
		}
	}

	private void updateWidgets() {
		isVisible = geo.isEuclidianVisible() && geoList.size() != 0;
		int fontSize = (int) (view.getFontSize()
				* geoList.getFontSizeMultiplier());
		setLabelFontSize(fontSize);
		if (geo.doHighlighting() == false) {
			hideWidget();
		}
		// box.setVisible(isVisible);

		if (!isVisible) {
			return;
		}

		// eg size changed etc
		labelDesc = getLabelText();

		// box.validate();

		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;
		labelRectangle.setBounds(xLabel, yLabel,
				(int) (getHitRect().getWidth()),
				(int) (getHitRect().getHeight()));

	}

	private String getLabelText() {
		// don't need to worry about labeling options, just check if caption
		// set or not

		if (!"".equals(geo.getRawCaption())) {
			String caption = geo.getCaption(StringTemplate.defaultTemplate);
			return caption;

		}

		// make sure there's something to drag
		return Unicode.NBSP + Unicode.NBSP + Unicode.NBSP;

	}

	@Override
	final public void update() {

		if (geoList.drawAsComboBox()) {
			updateWidgets();

		} else {
			isVisible = geoList.isEuclidianVisible();
			if (!isVisible)
				return;

			// go through list elements and create and/or update drawables
			int size = geoList.size();
			drawables.ensureCapacity(size);
			int oldDrawableSize = drawables.size();

			int drawablePos = 0;
			for (int i = 0; i < size; i++) {
				GeoElement listElement = geoList.get(i);
				if (!listElement.isDrawable())
					continue;

				// add drawable for listElement
				// if (addToDrawableList(listElement, drawablePos,
				// oldDrawableSize))
				if (drawables.addToDrawableList(listElement, drawablePos,
						oldDrawableSize, this))
					drawablePos++;

			}

			// remove end of list
			for (int i = drawables.size() - 1; i >= drawablePos; i--) {
				view.remove(drawables.get(i).getGeoElement());
				drawables.remove(i);
			}

			// draw trace
			if (geoList.getTrace()) {
				isTracing = true;
				GGraphics2D g2 = view
						.getBackgroundGraphics();
				if (g2 != null)
					drawTrace(g2);
			} else {
				if (isTracing) {
					isTracing = false;
					// view.updateBackground();
				}
			}
		}

	}

	/**
	 * This method is necessary, for example when we set another construction
	 * step, and the sub-drawables of this list should be removed as well
	 */
	final public void remove() {

		if (geoList.drawAsComboBox()) {
			// view.remove(box);
		} else {
			for (int i = drawables.size() - 1; i >= 0; i--) {
				GeoElement currentGeo = drawables.get(i).getGeoElement();
				if (!currentGeo.isLabelSet())
					view.remove(currentGeo);
			}
			drawables.clear();
		}
	}

	@Override
	protected final void drawTrace(GGraphics2D g2) {
		if (!geoList.drawAsComboBox()) {

			g2.setPaint(geo.getObjectColor());
			g2.setStroke(objStroke);
			if (isVisible) {
				int size = drawables.size();
				for (int i = 0; i < size; i++) {
					Drawable d = (Drawable) drawables.get(i);
					// draw only those drawables that have been created by this
					// list;
					// if d belongs to another object, we don't want to mess
					// with it
					// here
					if (createdByDrawList()
							|| !d.getGeoElement().isLabelSet()) {
						d.draw(g2);
					}
				}
			}
		}
	}

	@Override
	final public void draw(GGraphics2D g2) {

		if (isVisible && geoList.drawAsComboBox()) {
			drawOnCanvas(g2, "");
			return;

		}
		if (isVisible) {
			boolean doHighlight = geoList.doHighlighting();

			int size = drawables.size();
			for (int i = 0; i < size; i++) {
				Drawable d = (Drawable) drawables.get(i);
				// draw only those drawables that have been created by this
				// list;
				// if d belongs to another object, we don't want to mess
				// with it
				// here
				if (createdByDrawList() || !d.getGeoElement().isLabelSet()) {
					d.getGeoElement().setHighlighted(doHighlight);
					d.draw(g2);
				}
			}
		}
	}

	/**
	 * Returns whether any one of the list items is at the given screen
	 * position.
	 */
	@Override
	final public boolean hit(int x, int y, int hitThreshold) {
		if (geoList.drawAsComboBox()) {

			DrawList opened = view.getOpenedComboBox();
			if (opened != null && opened != this && opened.isOptionsHit(x, y)) {
				return false;
			}

			return super.hit(x, y, hitThreshold) || isControlHit(x, y)
					|| isOptionsHit(x, y);


		}

		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable d = (Drawable) drawables.get(i);
			if (d.hit(x, y, hitThreshold))
				return true;
		}
		return false;

	}

	@Override
	public boolean isInside(GRectangle rect) {
		if (geoList.drawAsComboBox()) {
			return super.isInside(rect);// || drawOptions.isInside(rect);
		}

		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable d = (Drawable) drawables.get(i);
			if (!d.isInside(rect))
				return false;
		}
		return size > 0;

	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		if (geoList.drawAsComboBox()) {
			return super.intersectsRectangle(rect);
			// || drawOptions.intersectsRectangle(rect);
		}

		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable d = (Drawable) drawables.get(i);
			if (d.intersectsRectangle(rect))
				return true;
		}
		return false;
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	final public GRectangle getBounds() {
		if (geoList.drawAsComboBox()) {
			return getHitRect().getBounds();

		}

		if (!geo.isEuclidianVisible())
			return null;

		GRectangle result = null;

		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable d = (Drawable) drawables.get(i);
			GRectangle bb = d.getBounds();
			if (bb != null) {
				if (result == null) {
					result = AwtFactory.getPrototype().newRectangle(bb);
				}
				// changed () to (bb) bugfix,
				// otherwise top-left of screen
				// is always included
				// add bounding box of list element
				result.add(bb);
			}
		}

		return result;

	}

	/**
	 * Resets the drawables when draw as combobox option is toggled
	 */
	public void resetDrawType() {

		if (geoList.drawAsComboBox()) {
			if (drawables != null) {
				for (int i = drawables.size() - 1; i >= 0; i--) {
					GeoElement currentGeo = drawables.get(i).getGeoElement();
					if (!currentGeo.isLabelSet()) {
						view.remove(currentGeo);
					}
				}
			drawables.clear();
			}
		} else {
			// view.remove(box);
		}

		reset();

		update();
	}

	@Override
	protected void drawWidget(GGraphics2D g2) {

		updateMetrics(g2);

		String labelText = getLabelText();
		int textLeft = boxLeft + COMBO_TEXT_MARGIN;
		int textBottom = boxTop + getTextBottom();
		GColor bgColor = geo.getBackgroundColor() != null
				? geo.getBackgroundColor() : view.getBackgroundCommon();

		dropDown.drawSelected(geoList, g2, bgColor, boxLeft, boxTop, boxWidth,
				boxHeight);

		g2.setPaint(GColor.LIGHT_GRAY);
		highlightLabel(g2, latexLabel);

		g2.setPaint(geo.getObjectColor());

		// Draw the selected line
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

	@Override
	protected void drawLabel(GGraphics2D g2, GeoElement geo0, String text) {

		int textBottom = boxTop + getTextBottom();
		boolean latex = isLatexString(text);
		if (latex) {
			drawLatex(g2, geo0, getLabelFont(), text, xLabel,
					boxTop + (boxHeight - labelSize.y) / 2);

		} else {
			textBottom = boxTop
					+ (boxHeight + getLabelFontSize() - COMBO_TEXT_MARGIN)
							/ 2;
			g2.setPaint(geo.getObjectColor());
			g2.setFont(getLabelFont());
			EuclidianStatic.drawIndexedString(view.getApplication(), g2, text,
					xLabel, textBottom, false, false);
		}

	}

	@Override
	protected void highlightLabel(GGraphics2D g2, boolean latex) {
		if (geo.isLabelVisible() && geo.doHighlighting() && latex) {
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
		dropDown.drawControl(g2, left, boxTop, boxHeight, boxHeight,
				geo.getBackgroundColor(), isOptionsVisible());

	}

	@Override
	protected void calculateBoxBounds(boolean latex) {
		boxLeft = xLabel + labelSize.x + LABEL_COMBO_GAP;
		boxTop = latex
				? yLabel + (labelSize.y - getPreferredSize().getHeight()) / 2
				: yLabel;
		boxWidth = getPreferredSize().getWidth();
		boxHeight = getPreferredSize().getHeight() + COMBO_TEXT_MARGIN;
	}

	@Override
	protected void calculateBoxBounds() {
		boxLeft = xLabel + LABEL_COMBO_GAP;
		boxTop = yLabel;
		boxWidth = getPreferredSize().getWidth();
		boxHeight = getPreferredSize().getHeight();
	}

	@Override
	protected int getTextBottom() {

		return isLatexString(selectedText) ? boxHeight - selectedHeight / 2
				: (getPreferredSize().getHeight() + getMultipliedFontSize())
						/ 2;
	}


	private void updateMetrics(GGraphics2D g2) {


		drawOptions.onResize(view.getWidth(), view.getHeight());

		GeoElement geoItem = geoList.getSelectedElement();
		// boolean latex = false;
		if (needsLatex(geoItem)) {
			selectedText = geoItem.toLaTeXString(false,
					StringTemplate.latexTemplate);
			seLatex = true;
		} else {
			selectedText = geoItem
					.toValueString(StringTemplate.defaultTemplate);
			seLatex = isLatexString(selectedText);
		}

		selectedDimension = drawSelectedText(g2, 0, 0, false);

		latexLabel = measureLabel(g2, geoList, getLabelText());

		labelRectangle.setBounds(boxLeft - 1, boxTop - 1, boxWidth, boxHeight);

	}


	/**
	 * @param geoItem
	 *            geo
	 * @return whether it should be painted in LaTeX
	 */
	static boolean needsLatex(GeoElement geoItem) {
		return geoItem instanceof FunctionalNVar
				|| (geoItem.isGeoText() && geoItem.isLaTeXDrawableGeo());
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
					selectedText,
					left, top, false, false);
		}

		return AwtFactory.getPrototype().newDimension(w,
				Math.round(layout.getDescent() + layout.getAscent()));
	}

	private int getTriangleControlWidth() {
		return selectedDimension.getHeight();
	}

	private int getMultipliedFontSize() {
		return (int) Math.round(
				((getLabelFont().getSize() * geoList.getFontSizeMultiplier())));
	}

	/**
	 * @return preferred width of dropdown
	 */
	int getPreferredWidth() {
		int selectedWidth = selectedDimension.getWidth()
				+ (isLatexString(selectedText) ? 0 : 2 * COMBO_TEXT_MARGIN)
				+ getTriangleControlWidth();

		int maxItemWidth = drawOptions.getMaxItemWidth();
		return (isOptionsVisible() && maxItemWidth > selectedWidth)
				? maxItemWidth
				: selectedWidth;
	}

	@Override
	public GDimension getPreferredSize() {
		if (selectedDimension == null) {
			return AwtFactory.getPrototype().newDimension(0, 0);
		}

		return AwtFactory.getPrototype().newDimension(getPreferredWidth(),
				selectedDimension.getHeight() + COMBO_TEXT_MARGIN);

	}

	@Override
	protected void showWidget() {
		// no widget
	}

	@Override
	protected void hideWidget() {
		// no widget
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

		DrawList opened = view.getOpenedComboBox();
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
	 * Open dropdown
	 */
	public void openOptions() {
		setOptionsVisible(false);
	}

	/**
	 * Close dropdown
	 * 
	 * @return whether repaint is needed
	 */
	public boolean closeOptions() {
		return setOptionsVisible(false);
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
	private boolean setOptionsVisible(boolean optionsVisible) {

		return drawOptions.setVisible(optionsVisible);

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
	public static DrawList asDrawable(App app, GeoElement geo) {
		return (DrawList) app.getActiveEuclidianView().getDrawableFor(geo);
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

	public void onClick(int x, int y) {
		drawOptions.onClick(x, y);
	}

	/**
	 * Update the opened comobox variable for enclosing view
	 */
	void updateOpenedComboBox() {

		DrawList dl = view.getOpenedComboBox();
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

}

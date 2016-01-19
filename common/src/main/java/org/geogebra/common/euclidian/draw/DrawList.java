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
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.RemoveNeeded;
import org.geogebra.common.euclidian.event.ActionEvent;
import org.geogebra.common.euclidian.event.ActionListenerI;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.util.DropDownList;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;

/**
 * Draw a GeoList containing drawable objects
 * 
 * @author Markus Hohenwarter
 */
public final class DrawList extends CanvasDrawable implements RemoveNeeded {
	private static final int LABEL_COMBO_GAP = 10;
	private static final int COMBO_TEXT_MARGIN = 5;
	/** coresponding list as geo */
	GeoList geoList;
	private DrawListArray drawables;
	private boolean isVisible;
	private String oldCaption = "";
	/** combobox */
	org.geogebra.common.javax.swing.AbstractJComboBox comboBox;
	private org.geogebra.common.javax.swing.GLabel label;
	private DropDownList dropDown = null;
	private String selectedText;
	private int selectedHeight;
	private GRectangle ctrlRect;
	private GDimension selectedDimension;
	private boolean latexLabel;
	private int viewHeight = 0;
	private int viewWidth = 0;

	private DrawOptions drawOptions;

	private class DrawOptions {
		private static final int MAX_COLS_NO_FONT_CHANGE = 5;

		private class OptionItem {
			public int index;
			public int width;
			public int height;
			public String text;
			public boolean latex;
			GRectangle rect;
			public OptionItem(GGraphics2D g2, int idx) {
				index = idx;
				text = geoList.get(idx)
						.toValueString(StringTemplate.defaultTemplate);
				latex = isLatexString(text);

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

			public boolean isHit(int x, int y) {
				return rect != null && rect.contains(x, y);
			}

			public void setRect(GRectangle rect) {
				this.rect = rect;
			}
		}

		private static final int MIN_FONT_SIZE = 12;

		private int colCount = 10;
		private int rowCount = 10;
		private GRectangle rectTable;
		private GDimension dimItem;
		private GDimension dimTable;
		private int left;
		private int top;
		private int xPadding;
		private int yPadding;
		GFont itemFont;

		private List<OptionItem> items;
		private OptionItem itemHovered;
		private GColor hoverColor;
		private GGraphics2D g2;
		private boolean visible;

		private int selectedIndex;

		private int itemFontSize;
		public DrawOptions() {
			items = new ArrayList<DrawList.DrawOptions.OptionItem>();
			itemHovered = null;
			hoverColor = GColor.LIGHT_GRAY;
		}

		public void draw(GGraphics2D g2, int left, int top) {
			if (!isVisible()) {
				return;
			}

			this.left = left;
			this.top = top;
			this.g2 = g2;
		
			getMetrics();
			drawBox();
			drawItems();
		}

		private void drawItems() {

			int idx = 0;

				for (int col = 0; col < getColCount(); col++) {
				for (int row = 0; row < rowCount; row++) {
					if (idx < items.size()) {
						drawItem(col, row, items.get(idx), false);
					}
					idx++;
				}
			}
		}

		private void drawItem(int col, int row, OptionItem item,
				boolean hover) {

			int rectLeft = left + dimItem.getWidth() * col;
			int rectTop = top + dimItem.getHeight() * row;

			if (item.getRect() == null) {
				item.setRect(AwtFactory.prototype.newRectangle(rectLeft,
						rectTop, dimItem.getWidth(), dimItem.getHeight()));
			}

			drawItem(item, hover);
		}

		private void drawItem(OptionItem item, boolean hover) {

			int rectLeft = (int) item.rect.getBounds().getX();
			int rectTop = (int) item.rect.getBounds().getY();

			g2.setColor(hover ? hoverColor : geoList.getBackgroundColor());
			g2.fillRect(rectLeft, rectTop, dimItem.getWidth(),
						dimItem.getHeight());

			// item rectangle drawing for debug.
			// g2.setPaint(GColor.ORANGE);
			//
			// g2.drawRect(rectLeft, rectTop, dimItem.getWidth(),
			// dimItem.getHeight());
			//
			if (item.getRect() == null) {
				item.setRect(AwtFactory.prototype.newRectangle(rectLeft,
						rectTop, dimItem.getWidth(), dimItem.getHeight()));
			}
			if (item.latex) {
				GRectangle rect = item.rect.getBounds();
				int x = (int) rect.getX();
				int y = (int) rect.getY();

				drawLatex(g2, geoList, itemFont, item.text,
						x + (int) ((rect.getWidth() - item.width) / 2), 
						y + (int) ((rect.getHeight() - item.height) / 2));
			} else {
				g2.setPaint(geo.getObjectColor());
				if (g2.getFont().getSize() != itemFontSize) {
					g2.setFont(itemFont.deriveFont(itemFontSize));
				}
				int x = (dimItem.getWidth() - item.width) / 2;
				int y = (dimItem.getHeight() - yPadding);

				EuclidianStatic.drawIndexedString(view.getApplication(), g2,
						item.text, rectLeft + x, rectTop + y, false,
						false);
			}
		}

		private void drawBox() {
			g2.setPaint(geoList.getBackgroundColor());
			g2.fillRect(left, top, dimTable.getWidth(), dimTable.getHeight());
			g2.setPaint(GColor.LIGHT_GRAY);
			g2.drawRect(left, top, dimTable.getWidth(), dimTable.getHeight());

		}

		public boolean isHit(int x, int y) {
			return isVisible() && rectTable.contains(x, y);
		}

		public boolean onMouseDown(int x, int y) {
			if (!visible) {
				return false;
			}

			OptionItem item = getItemAt(x, y);
			if (item == null) {
				return false;
			}

			Log.debug("[REFACTOR] selected item: " + item.text + "("
					+ item.index + ")");
			selectedIndex = item.index;
			selectCurrentItem();
			setVisible(false);
			return true;

		}

		public void onMouseOver(int x, int y) {
			if (!isHit(x, y)) {
				return;
			}
			OptionItem item = getItemAt(x, y);
			if (item == null) {
				return;
			}

			if (itemHovered != null) {
				drawItem(itemHovered, false);
			}

			drawItem(item, true);
			itemHovered = item;
			selectedIndex = item.index;
		}

		private OptionItem getItemAt(int x, int y) {
			for (OptionItem item : items) {
				if (item.isHit(x, y)) {
					return item;
				}
			}

			return null;
		}

		private void getMetrics() {
			xPadding = 10;
			yPadding = 10;
			itemFontSize = getLabelFontSize();
			boolean finished = false;

			while (!finished && itemFontSize > MIN_FONT_SIZE) {
				itemFont = getLabelFont().deriveFont(GFont.PLAIN, itemFontSize);
				createItems();
				finished = getTableScale();
				itemFontSize--;
			}

			dimTable = AwtFactory.prototype.newDimension(
					getColCount() * dimItem.getWidth(),
					rowCount * dimItem.getHeight());

			if (top + dimTable.getHeight() > view.getHeight()) {
				top = (view.getHeight() - dimTable.getHeight());
			}

			if (left + dimTable.getWidth() > view.getWidth()) {
				left = (view.getWidth() - dimTable.getWidth());
			}
			rectTable = AwtFactory.prototype.newRectangle(left, top,
					dimTable.getWidth(), dimTable.getHeight());

		}

		/**
		 * Gets the columns and rows for options table.
		 * 
		 * @return true if all table columns are full, ie no column with fewer
		 *         items at the end.
		 */
		private boolean getTableScale() {
			int maxItems = geoList.size();
			int maxRows = view.getHeight() / dimItem.getHeight();
			int maxCols = view.getWidth() / dimItem.getWidth();
			if (maxItems < maxRows) {
				setColCount(1);
				dimItem = AwtFactory.prototype
						.newDimension(
								boxWidth > dimItem.getWidth() ? boxWidth
										: dimItem.getWidth(),
						dimItem.getHeight());
				rowCount = maxItems;
				return true;
			}

			int mod = maxItems % maxRows;
			setColCount(maxItems / maxRows + (mod == 0 ? 0 : 1));
			rowCount = maxRows;
			return colCount < maxCols / 2 || mod == 0;

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

			dimItem = AwtFactory.prototype.newDimension(
					(int) (maxWidth + 2 * xPadding),
					(int) (maxHeight + 2 * yPadding));

		}

		public boolean isVisible() {
			return visible;
		}

		public void setVisible(boolean visible) {
			this.visible = visible;
			if (visible) {
				view.setOpenedComboBox(DrawList.this);
				selectedIndex = 0;
			}
			view.setOpenedComboBox(null);
			geo.updateRepaint();
		}

		public void onResize() {
			itemHovered = null;
		}

		public void toggle() {
			setVisible(!visible);
		}

		public void moveSelectorBy(int diff, boolean forward) {
			boolean update = false;
			boolean hasHovered = itemHovered != null;
			int idx = hasHovered ? itemHovered.index : 0;
			if (forward) {
				if (idx < items.size() - diff) {
					idx += diff;
					update = true;
				}
			} else {
				if (idx > diff - 1) {
					idx -= diff;
					update = true;
				}
			}

			if (update && (!hasHovered
					|| (hasHovered && itemHovered.index != idx))) {
				if (hasHovered) {
					drawItem(itemHovered, false);
				}

				itemHovered = items.get(idx);
				drawItem(itemHovered, true);

			}
		}

		public void moveSelectorVertical(boolean moveDown) {
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
		setDrawingOnCanvas(view.getApplication()
				.has(Feature.DRAW_DROPDOWNLISTS_TO_CANVAS));

		if (isDrawingOnCanvas()) {
			drawOptions = new DrawOptions();
			dropDown = view.getApplication().newDropDownList();
			GBox ctrlBox = geo.getKernel().getApplication().getSwingFactory()
					.createHorizontalBox(view.getEuclidianController());
			ctrlRect = ctrlBox.getBounds();
		}
		reset();

		update();
	}

	private void resetComboBox() {

		if (!isDrawingOnCanvas() && label == null) {
			label = view.getApplication().getSwingFactory().newJLabel("Label",
					true);
			label.setVisible(true);
		}

		if (comboBox == null) {
			comboBox = geoList.getComboBox(view.getViewID());
			comboBox.setVisible(!isDrawingOnCanvas());
			comboBox.addActionListener(AwtFactory.prototype
					.newActionListener(new DrawList.ActionListener()));
		}

		if (box == null) {
			box = view.getApplication().getSwingFactory()
					.createHorizontalBox(view.getEuclidianController());
			if (!isDrawingOnCanvas()) {
				box.add(label);
			}
			box.add(comboBox);
		}
		view.add(box);
	}

	private void reset() {

		if (geoList.drawAsComboBox()) {
			resetComboBox();
		} else {

			if (drawables == null) {
				drawables = new DrawListArray(view);
			}
		}
	}

	private void updateWidgets() {
		isVisible = geo.isEuclidianVisible() && geoList.size() != 0;
		int fontSize = (int) (view.getFontSize()
				* geoList.getFontSizeMultiplier());
		if (isDrawingOnCanvas()) {
			setLabelFontSize(fontSize);
			if (geo.doHighlighting() == false) {
				hideWidget();
			}
		}
		box.setVisible(isVisible);

		if (!isVisible) {
			return;
		}

		// eg size changed etc
		geoList.rebuildComboxBoxIfNecessary(comboBox);
		labelDesc = getLabelText();

		App app = view.getApplication();

		org.geogebra.common.awt.GFont vFont = view.getFont();
		org.geogebra.common.awt.GFont font = app.getFontCanDisplay(
				comboBox.getItemAt(0).toString(), false, vFont.getStyle(),
				fontSize);

		if (!isDrawingOnCanvas()) {
			label.setText(labelDesc);

			if (!geo.isLabelVisible()) {
				label.setText("");
			}
			label.setOpaque(false);
			label.setFont(font);
			label.setForeground(geo.getObjectColor());
		}

		comboBox.setFont(font);
		comboBox.setForeground(geo.getObjectColor());
		org.geogebra.common.awt.GColor bgCol = geo.getBackgroundColor();
		comboBox.setBackground(
				bgCol != null ? bgCol : view.getBackgroundCommon());

		comboBox.setFocusable(true);
		comboBox.setEditable(false);

		box.validate();

		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;
		org.geogebra.common.awt.GDimension prefSize = box.getPreferredSize();
		labelRectangle.setBounds(xLabel, yLabel, prefSize.getWidth(),
				prefSize.getHeight());
		box.setBounds(labelRectangle);

	}

	private String getLabelText() {
		// don't need to worry about labeling options, just check if caption
		// set or not

		if (geo.getRawCaption() != null) {
			String caption = geo.getCaption(StringTemplate.defaultTemplate);
			if (isDrawingOnCanvas()) {
				oldCaption = caption;
				return caption;

			} else if (!caption.equals(oldCaption)) {
				oldCaption = caption;
				labelDesc = GeoElement.indicesToHTML(caption, true);
			}
			return labelDesc;
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
				org.geogebra.common.awt.GGraphics2D g2 = view
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
			view.remove(box);
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
	protected final void drawTrace(org.geogebra.common.awt.GGraphics2D g2) {
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
	final public void draw(org.geogebra.common.awt.GGraphics2D g2) {
		if (isVisible && geoList.drawAsComboBox()) {
			if (isDrawingOnCanvas()) {
				drawOnCanvas(g2, "");
				return;
			}

			if (isVisible) {
				if (geo.doHighlighting()) {
					label.setOpaque(true);
					label.setBackground(GColor.LIGHT_GRAY);

				} else {
					label.setOpaque(false);
				}
			}

		} else {
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
					if (createdByDrawList()
							|| !d.getGeoElement().isLabelSet()) {
						d.getGeoElement().setHighlighted(doHighlight);
						d.draw(g2);
					}
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
			DrawList dl = view.getOpenedComboBox();
			if (dl != null && dl != this) {
				return false;
			}

			return isDrawingOnCanvas()
					? super.hit(x, y, hitThreshold) || isControlHit(x, y)
							|| isOptionsHit(x, y)
					: box.getBounds().contains(x, y);
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
			return super.isInside(rect);
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
	final public org.geogebra.common.awt.GRectangle getBounds() {
		if (geoList.drawAsComboBox()) {
			return isDrawingOnCanvas() ? box.getBounds() : null;

		}

		if (!geo.isEuclidianVisible())
			return null;

		org.geogebra.common.awt.GRectangle result = null;

		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable d = (Drawable) drawables.get(i);
			org.geogebra.common.awt.GRectangle bb = d.getBounds();
			if (bb != null) {
				if (result == null)
					result = org.geogebra.common.factories.AwtFactory.prototype
							.newRectangle(bb); // changed () to (bb) bugfix,
				// otherwise top-left of screen
				// is always included
				// add bounding box of list element
				result.add(bb);
			}
		}

		return result;

	}

	/**
	 * Listens to events in this combobox
	 * 
	 * @author Michael + Judit
	 */
	public class ActionListener
			extends org.geogebra.common.euclidian.event.ActionListener
			implements ActionListenerI {

		/**
		 * @param e
		 *            action event
		 */
		public void actionPerformed(ActionEvent e) {
			geoList.setSelectedIndex(comboBox.getSelectedIndex(), true);
		}

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
			view.remove(box);
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
		boolean latex = isLatexString(selectedText);
		if (latex) {
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
					+ (boxHeight + getMultipliedFontSize() - COMBO_TEXT_MARGIN)
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

		Log.debug("[PROFILE] updateMetrics");
		int dW = viewWidth - view.getWidth();
		int dH = viewHeight - view.getHeight();

		if (dW != 0 || dH != 0) {
			if (dW != 0 || dH != 0) {
				Log.debug("[PROFILE] resize happened.");
				viewHeight = view.getHeight();
				viewWidth = view.getWidth();
				drawOptions.onResize();
			}

		}

		selectedText = geoList.get(geoList.getSelectedIndex())
				.toValueString(StringTemplate.defaultTemplate);
		selectedDimension = drawSelectedText(g2, 0, 0, false);

		setPreferredSize(getPreferredSize());

		latexLabel = measureLabel(g2, geoList, getLabelText());

		labelRectangle.setBounds(boxLeft - 1, boxTop - 1, boxWidth, boxHeight);

	}


	private GDimension drawSelectedText(GGraphics2D g2, int left, int top,
			boolean draw) {

		GFont font = view.getFont();

		if (isLatexString(selectedText)) {
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

		return AwtFactory.prototype.newDimension(w,
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
			return AwtFactory.prototype.newDimension(0, 0);
		}

		return AwtFactory.prototype.newDimension(getPreferredWidth(),
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
		if (!isDrawingOnCanvas()) {
			return false;
		}

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
		if (!isDrawingOnCanvas()) {
			return;
		}

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
		if (!isDrawingOnCanvas()) {
			return;
		}

		if (drawOptions.onMouseDown(x, y)) {
			return;
		}

		if (isControlHit(x, y)) {
			setOptionsVisible(!isOptionsVisible());
		}
	}

	/**
	 * Open dropdown
	 */
	public void openOptions() {
		if (!isDrawingOnCanvas()) {
			return;
		}
		setOptionsVisible(false);
	}

	/**
	 * Close dropdown
	 */
	public void closeOptions() {
		if (!isDrawingOnCanvas()) {
			return;
		}
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
		if (!isDrawingOnCanvas()) {
			return false;
		}

		return ctrlRect != null && ctrlRect.contains(x, y);
	}

	/**
	 * @return whether dropdown is visible
	 */
	public boolean isOptionsVisible() {
		if (!isDrawingOnCanvas()) {
			return false;
		}
		return drawOptions.isVisible();
	}

	/**
	 * @param optionsVisible
	 *            change visibility of dropdown items
	 */
	private void setOptionsVisible(boolean optionsVisible) {
		if (!isDrawingOnCanvas()) {
			return;
		}

		drawOptions.setVisible(optionsVisible);

	}

	/**
	 * toggle visibility of dropdown items
	 */
	public void toggleOptions() {
		if (!isDrawingOnCanvas()) {
			return;
		}
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
		setOptionsVisible(false);
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
		if (!isDrawingOnCanvas()) {
			return;
		}

		drawOptions.moveSelectorVertical(down);
	}

	/**
	 * Moves the selector horizontally, if dropdown have more columns than one.
	 * 
	 * @param left
	 *            Indicates that selector should move left or right.
	 */
	public void moveSelectorHorizontal(boolean left) {
		if (!isDrawingOnCanvas()) {
			return;
		}

		drawOptions.moveSelectorHorizontal(left);
	}

	/**
	 * @return if combo have more columns than one.
	 */
	public boolean isMultiColumn() {
		if (!isDrawingOnCanvas()) {
			return false;
		}

		return drawOptions.getColCount() > 1;
	}

	/**
	 * 
	 * @return if list when draw as combo, is selected.
	 */
	public boolean isSelected() {
		return geo.doHighlighting();
	}

}

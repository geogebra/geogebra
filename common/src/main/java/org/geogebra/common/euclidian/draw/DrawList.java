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

/**
 * Draw a GeoList containing drawable objects
 * 
 * @author Markus Hohenwarter
 */
public final class DrawList extends CanvasDrawable implements RemoveNeeded {
	private static final int OPTIONSBOX_ITEM_GAP_SMALL = 20;
	private static final int OPTIONSBOX_ITEM_GAP_MEDIUM = 40;
	private static final int OPTIONSBOX_ITEM_GAP_BIG = 55;
	private static final int COMBO_TEXT_MARGIN = 5;
	private static final int OPTIONBOX_TEXT_MARGIN_LEFT = 5;
	private static final int OPTIONBOX_COMBO_GAP = 5;
	private static final int LABEL_COMBO_GAP = 10;
	private static final int TEXT_CENTER = -1;
	/** coresponding list as geo */
	GeoList geoList;
	private List<GRectangle> optionItems = new ArrayList<GRectangle>();
	private DrawListArray drawables;
	private boolean isVisible;
	private boolean optionsVisible = false;
	private String oldCaption = "";
	/** combobox */
	org.geogebra.common.javax.swing.AbstractJComboBox comboBox;
	private org.geogebra.common.javax.swing.GLabel label;
	private org.geogebra.common.javax.swing.GBox box;
	private DropDownList dropDown = null;
	private int optionsHeight;
	private int optionsWidth;
	private String selectedText;
	private int selectedHeight;
	private GBox ctrlBox;
	private GRectangle ctrlRect;
	private GRectangle optionsRect;
	private GBox optionsBox;
	private int optionsItemHeight;
	private int selectedOptionIndex;
	private GDimension selectedDimension;
	private int currentIdx;
	private float lastDescent;
	float lastAscent;
	private boolean latexLabel;
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
			dropDown = view.getApplication().newDropDownList();
			ctrlBox = geo.getKernel().getApplication().getSwingFactory()
					.createHorizontalBox(view.getEuclidianController());
			ctrlRect = ctrlBox.getBounds();
			optionsBox = geo.getKernel().getApplication().getSwingFactory()
					.createHorizontalBox(view.getEuclidianController());
			optionsRect = optionsBox.getBounds();
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
					if (createdByDrawList() || !d.getGeoElement().isLabelSet()) {
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
					if (createdByDrawList() || !d.getGeoElement().isLabelSet()) {
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
	public class ActionListener extends
			org.geogebra.common.euclidian.event.ActionListener implements
			ActionListenerI {

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
			for (int i = drawables.size() - 1; i >= 0; i--) {
				GeoElement currentGeo = drawables.get(i).getGeoElement();
				if (!currentGeo.isLabelSet()) {
					view.remove(currentGeo);
				}
			}
			drawables.clear();
		} else {
			view.remove(box);
		}

		reset();

		update();
	}

	private void updateMetrics(GGraphics2D g2) {
		// just measuring
		g2.setPaint(GColor.WHITE);
		drawOptionLines(g2, 0, 0, false);
		setPreferredSize(getPreferredSize());

		g2.setFont(getLabelFont());
		latexLabel = measureLabel(g2, geoList, getLabelText());
//		int textLeft = boxLeft + COMBO_TEXT_MARGIN;
//		int textBottom = boxTop + getTextBottom();
//
		// TF Bounds

		labelRectangle.setBounds(boxLeft - 1, boxTop - 1, boxWidth,
 boxHeight);
		
	}
	@Override
	protected void drawWidget(GGraphics2D g2) {
		updateMetrics(g2);
		String labelText = getLabelText();
		int textLeft = boxLeft + COMBO_TEXT_MARGIN;
		int textBottom = boxTop + getTextBottom();
		GColor bgColor = geo.getBackgroundColor() != null
				? geo.getBackgroundColor() : view.getBackgroundCommon();

		dropDown.drawSelected(geoList, g2, bgColor,
 boxLeft, boxTop, boxWidth,
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

		drawTextLine(g2, textLeft, textBottom, selectedText, latex, false,
				true);

		drawControl(g2);
		if (isOptionsVisible()) {
			drawOptions(g2);
		}

		if (geo.isLabelVisible()) {
			drawLabel(g2, geoList, labelText);
		}

	}

	private int alignTextToBottom(GGraphics2D g2, int top, int height,
			String text) {
		int base = (height + getTextDescent(g2, selectedText)) / 2;
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
		int width = boxHeight;
		int left = boxLeft + boxWidth - boxHeight;

		ctrlRect.setBounds(boxLeft, boxTop, boxWidth, boxHeight);
		dropDown.drawControl(g2, left, boxTop, boxHeight, boxHeight,
				geo.getBackgroundColor(),
				isOptionsVisible());

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

	private void drawOptions(GGraphics2D g2) {

		g2.setPaint(geoList.getBackgroundColor());
		int itemY = (int) optionItems.get(0).getBounds().getY();
		int optTop = boxTop + boxHeight + OPTIONBOX_COMBO_GAP;
		App.debug("[OPTTOP] boxTop: " + boxTop + " boxHeight: " + boxHeight
				+ " itemY: " + itemY + " gap: " + OPTIONBOX_COMBO_GAP
				+ " total: " + optTop);
		int viewBottom = view.getViewHeight();
		if (optTop + optionsHeight > viewBottom) {
			App.debug("[DROPDOWN] offscreen: adjusting.");
			optTop = viewBottom - optionsHeight;
		}
		optionsRect.setBounds(boxLeft, optTop, boxWidth,
 optionsHeight);
		g2.fillRect(boxLeft, optTop, boxWidth, optionsHeight);

		g2.setPaint(GColor.LIGHT_GRAY);
		g2.drawRect(boxLeft, optTop, boxWidth, optionsHeight);

		g2.setPaint(geo.getObjectColor());

		int textLeft = boxLeft + OPTIONBOX_TEXT_MARGIN_LEFT;
		int rowTop = optTop;
		drawOptionLines(g2, textLeft, rowTop, true);
	}

	private GDimension drawTextLine(GGraphics2D g2, int textLeft, int top,
 String text,
 boolean latex, boolean selected, boolean draw) {

		int left = textLeft;

		if (latex) {
			GDimension d = null;
			if (left == TEXT_CENTER) {
				g2.setPaint(GColor.WHITE);
				d = measureLatex(g2, geoList, getLabelFont(), text);
				left = boxLeft + (boxWidth - d.getWidth()) / 2;
				g2.setPaint(geo.getObjectColor());

			}

			d = draw ? drawLatex(g2, geoList, getLabelFont(), text, left, top)
					: measureLatex(g2, geoList, getLabelFont(), text);

			return d;
		} 
		g2.setFont(getLabelFont());
		GTextLayout layout = g2.getFontRenderContext().getTextLayout(text,
				getLabelFont());
		final int w = (int) layout.getBounds().getWidth();
		final int h = (int) (layout.getBounds().getHeight()
				+ layout.getDescent());

		if (left == TEXT_CENTER) {
			left = boxLeft + (boxWidth - w) / 2;
		}

		if (draw) {
			EuclidianStatic.drawIndexedString(view.getApplication(), g2, text,
					left, top, false, false);
			lastDescent = layout.getDescent();
			lastAscent = layout.getAscent();
			// g2.drawLine(boxLeft, top, left + optionsWidth, top);
			// g2.drawLine(boxLeft, top - h, left + optionsWidth, top - h);

		}

		return new GDimension() {

			@Override
			public int getWidth() {
				// TODO Auto-generated method stub
				return w;
			}

			@Override
			public int getHeight() {
				// TODO Auto-generated method stub
				return Math.round(lastAscent + lastDescent);
			}
		};
	}

	private void drawOptionLines(GGraphics2D g2, int left, int top,
			boolean draw) {
		optionsWidth = 0;
		optionsHeight = 0;
		optionsItemHeight = 0;
		selectedDimension = null;
		optionItems.clear();
		int size = geoList.size();
		int rowTop = top;
		boolean allLatex = true;
		for (int i = 0; i < size; i++) {
			GBox b = geo.getKernel().getApplication().getSwingFactory()
					.createHorizontalBox(view.getEuclidianController());
			GRectangle itemRect = b.getBounds();

			String text = geoList.get(i)
					.toValueString(StringTemplate.defaultTemplate);

			boolean latex = isLatexString(text);

			allLatex = allLatex && latex;

			boolean latexNext = i < size - 1
					? isLatexString(geoList.get(i + 1)
							.toValueString(StringTemplate.defaultTemplate))
					: allLatex;

			boolean hovered = i == selectedOptionIndex;
			int standardGap = getOptionsItemGap();

			if (i == 0 && !latex) {
				rowTop += getFullTextHeight(g2, text) + standardGap / 2;
			}

			GDimension d = drawTextLine(g2, TEXT_CENTER, rowTop, text, latex,
					true,
					draw);

			int h = d.getHeight();

			if (latex) {
				itemRect.setBounds(boxLeft, rowTop, boxWidth, h);
			} else {
				itemRect.setBounds(boxLeft, rowTop - h - standardGap / 2,
						boxWidth, (int) (h + lastDescent + standardGap));

			}
			optionItems.add(itemRect);

			if (draw && hovered && optionItems.size() > i) {
				g2.setPaint(GColor.LIGHT_GRAY);
				int rx = (int) (itemRect.getX());
				int ry = (int) (itemRect.getY());
				int rw = (int) (itemRect.getWidth());
				int rh = (int) (itemRect.getHeight());

				g2.fillRoundRect(rx, ry, rw, rh, 4, 4);

				g2.setPaint(GColor.GRAY);

				g2.drawRoundRect(rx, ry, rw, rh, 4, 4);

				g2.setPaint(geoList.getObjectColor());
				drawTextLine(g2, TEXT_CENTER, rowTop, text, latex, true,
 draw);
			}

			if (i == geoList.getSelectedIndex()) {
				selectedText = text;
				selectedDimension = d;
			}

			if (optionsWidth < d.getWidth()) {
				optionsWidth = d.getWidth();
			}

			int gap = standardGap;
			if (latex || latexNext) {
				gap = 0;
			} else if (i == size - 1) {
				gap = getTextDescent(g2, text) + standardGap;
			}

			// If the neighbors are of different kinds (LaTeX-Plain),
			// more gap is needed.

			// LaTeX - Plain
			if (latex && !latexNext) {
				if (i < size - 1) {
					gap += h;
				} else {
					gap += standardGap;
				}
			}


			optionsHeight += h + gap;
			rowTop += h + gap;

		}

		optionsWidth += 2 * COMBO_TEXT_MARGIN + getTriangleControlWidth();
	}

	private int getOptionsItemGap() {

		switch (view.getApplication().getFontSize()) {
		case 12:
		case 16:
		case 18:
			return OPTIONSBOX_ITEM_GAP_SMALL;
		case 20:
		case 24:
		case 28:
			return OPTIONSBOX_ITEM_GAP_MEDIUM;
		case 32:
		case 48:
			return OPTIONSBOX_ITEM_GAP_BIG;

		}
		return OPTIONSBOX_ITEM_GAP_SMALL;
	}

	private int getTriangleControlWidth() {
		return selectedDimension.getHeight();
	}

	private int getMultipliedFontSize() {
		return (int) Math.round(((view.getApplication().getFontSize()
				* geoList.getFontSizeMultiplier())));
	}

	/**
	 * @return preferred width of dropdown
	 */
	int getPreferredWidth() {
		return isOptionsVisible() ? optionsWidth : selectedDimension.getWidth()
				+ (isLatexString(selectedText) ? 0 : 2 * COMBO_TEXT_MARGIN)
				+ getTriangleControlWidth();
	}
	@Override
	public GDimension getPreferredSize() {
		// TODO: eliminate magic numbers
		return new GDimension() {

			@Override
			public int getWidth() {
				return getPreferredWidth();
			}

			@Override
			public int getHeight() {
				return selectedDimension.getHeight() + COMBO_TEXT_MARGIN;// (int)
														// (getMultipliedFontSize()
														// * MUL_FONT_HEIGHT);

			}
		};
	}

	@Override
	protected void showWidget() {
		// no widget
	}

	@Override
	protected void hideWidget() {
		// no widget
	}


	//
	// private void debugOptionItems() {
	// App.debug("[OPTRECT] optionItems size: " + optionItems.size());
	// for (GRectangle rect : optionItems) {
	// App.debug("[OPTRECT] (" + rect.getX() + ", " + rect.getY() + ", "
	// + (rect.getX() + rect.getWidth()) + ", "
	// + (rect.getY() + rect.getHeight()) + ")");
	// }
	//
	// }
	private int getOptionAt(int x, int y) {
		int idx = 0;
		for (GRectangle rect : optionItems) {
			boolean inside = rect != null && rect.contains(x, y);
			if (inside) {
				return idx;
			}
			idx++;

		}
		return -1;
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
		return optionsVisible && optionsRect.contains(x, y);
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
		if (!isOptionsHit(x, y)) {
			return;
		}

		currentIdx = getOptionAt(x, y);

		if (currentIdx != -1 && currentIdx != selectedOptionIndex) {
			selectedOptionIndex = currentIdx;
			geoList.updateRepaint();
		}

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

		if (isControlHit(x, y)) {
			setOptionsVisible(!isOptionsVisible());
		} else {
			onOptionDown(x, y);
		}
	}

	/**
	 * Called when user presses mouse on dropdown list
	 * 
	 * @param x
	 *            Mouse x coordinate.
	 * @param y
	 *            Mouse y coordinate.
	 */
	public void onOptionDown(int x, int y) {
		if (!isDrawingOnCanvas()) {
			return;
		}
		if (optionsRect.contains(x, y)
				|| optionsRect.getBounds().contains(x, y)) {
			currentIdx = getOptionAt(x, y);


			selectItem();
		}
	}

	private void selectItem() {
		if (currentIdx == -1) {
			return;
		}

		geoList.setSelectedIndex(currentIdx, false);
		closeOptions();
	}

	/**
	 * Open dropdown
	 */
	public void openOptions() {
		setOptionsVisible(false);
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
		return optionsVisible;
	}

	/**
	 * @param optionsVisible
	 *            change visibility of dropdown items
	 */
	private void setOptionsVisible(boolean optionsVisible) {
		this.optionsVisible = optionsVisible;
		if (optionsVisible) {
			currentIdx = 0;
		}
	}

	/**
	 * toggle visibility of dropdown items
	 */
	public void toggleOptions() {
		if (!isDrawingOnCanvas()) {
			return;
		}
		App.debug("[DROPDOWN] toggle");
		optionsVisible = !optionsVisible;
		geo.updateRepaint();
	}

	/**
	 * Sync selected index to GeoList
	 */
	public void selectCurrentItem() {
		if (!isOptionsVisible()) {
			return;
		}
		selectItem();
		geo.updateRepaint();
	}

	/**
	 * Gets DrawList for geo.
	 * 
	 */
	public static DrawList asDrawable(App app, GeoElement geo) {
		return (DrawList) app.getActiveEuclidianView().getDrawableFor(geo);
	}

	public void moveSelection(boolean down) {
		if (down) {
			if (currentIdx < optionItems.size() - 1) {
				currentIdx++;
			}
		} else {
			if (currentIdx > 0) {
				currentIdx--;
			}

		}
		selectedOptionIndex = currentIdx;
		geo.updateRepaint();
	}

}

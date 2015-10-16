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
	private static final int OPTIONSBOX_ITEM_GAP = 5;
	private static final int TRIANGLE_CONTROL_WIDTH = 20;
	private static final int COMBO_TEXT_MARGIN = 5;
	private static final int OPTIONBOX_TEXT_MARGIN_BOTTOM = 10;
	private static final int OPTIONBOX_TEXT_MARGIN_TOP = 15;
	private static final int OPTIONBOX_TEXT_MARGIN_LEFT = 5;
	private static final int OPTIONBOX_COMBO_GAP = 5;
	private static final double MUL_FONT_HEIGHT = 1.6;
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
	private int maxLength = 4;
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
					? super.hit(x, y, hitThreshold) || ctrlRect.contains(x, y)
							|| optionsVisible && optionsRect.contains(x, y)
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

	public void onOptionOver(int x, int y) {
		if (!(optionsVisible && optionsRect.contains(x, y))) {
			return;
		}

		int idx = getOptionAt(x, y);
		if (idx != -1 && idx != selectedOptionIndex) {
			selectedOptionIndex = idx;
			geoList.updateRepaint();
		}

	}
	@Override
	public boolean isInside(GRectangle rect) {

		if (geoList.drawAsComboBox()) {
			return super.isInside(labelRectangle);
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

	// /**
	// * Returns false
	// */
	// @Override
	// public boolean hitLabel(int x, int y) {
	// if (geoList.drawAsComboBox()) {
	// return false;
	// }
	//
	// return super.hitLabel(x, y);
	//
	// }
	//
	// @Override
	// final public GeoElement getGeoElement() {
	// return geo;
	// }
	//
	// @Override
	// final public void setGeoElement(GeoElement geo) {
	// this.geo = geo;
	// }

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

	@Override
	protected void drawWidget(GGraphics2D g2) {
		// just measuring
		g2.setPaint(GColor.WHITE);
		drawOptionLines(g2, NO_DRAW, NO_DRAW);

		String labelText = getLabelText();
		boolean latexLabel = measureLabel(g2, geoList, labelText);
		int textLeft = boxLeft + COMBO_TEXT_MARGIN;
		int textBottom = boxTop + getTextBottom();

		// TF Bounds

		labelRectangle.setBounds(boxLeft - 1, boxTop - 1, boxWidth,
				boxHeight - 3);
		box.setBounds(labelRectangle);
		GColor bgColor = geo.getBackgroundColor() != null
				? geo.getBackgroundColor() : view.getBackgroundCommon();

		dropDown.drawSelected(geoList, g2, bgColor,
 boxLeft, boxTop, boxWidth,
				boxHeight);

		g2.setPaint(GColor.LIGHT_GRAY);
		highlightLabel(g2, latexLabel);

		g2.setPaint(geo.getObjectColor());

		if (geo.isLabelVisible()) {
			drawLabel(g2, geoList, labelText);
		}

		
		// Draw the selected line
		boolean latex = isLatexString(selectedText);
		if (latex) {
			textBottom = boxTop + (boxHeight - selectedHeight) / 2;
		} else {
			textBottom += (boxHeight - getMultipliedFontSize()) / 2
					- COMBO_TEXT_MARGIN;

		}

		drawTextLine(g2, textLeft, textBottom, selectedText, latex, false,
				false);

		drawControl(g2);
		if (optionsVisible) {
			drawOptions(g2);
		}
	}

	private void drawControl(GGraphics2D g2) {
		g2.setPaint(GColor.BLACK);
		int width = boxHeight;
		int left = boxLeft + boxWidth - boxHeight;

		ctrlRect.setBounds(left, boxTop, width, boxHeight);
		g2.drawRect(left, boxTop, width, boxHeight);
	}

	@Override
	protected void calculateBoxBounds(boolean latex) {
		boxLeft = xLabel + labelSize.x + LABEL_COMBO_GAP;
		boxTop = latex
				? yLabel + (labelSize.y - getPreferredSize().getHeight()) / 2
				: yLabel;
		boxWidth = getPreferredSize().getWidth();
		boxHeight = optionsItemHeight + 2 * COMBO_TEXT_MARGIN;
	}

	@Override
	protected void calculateBoxBounds() {
		boxLeft = xLabel + LABEL_COMBO_GAP;
		boxTop = yLabel;
		boxWidth = getPreferredSize().getWidth();
		boxHeight = getPreferredSize().getHeight();
	}

	protected int getTextBottom() {

		return isLatexString(selectedText) ? boxHeight - selectedHeight / 2
				: (getPreferredSize().getHeight() / 2)
						+ (int) (getLabelFontSize() * 0.4);
	}

	private void drawOptions(GGraphics2D g2) {

		g2.setPaint(geoList.getBackgroundColor());
		int optHeight = optionsHeight;
		int optTop = boxTop + boxHeight + OPTIONBOX_COMBO_GAP;
		optionsRect.setBounds(boxLeft, optTop, boxWidth, optHeight);
		g2.fillRect(boxLeft, optTop, boxWidth, optHeight);

		g2.setPaint(GColor.LIGHT_GRAY);
		g2.drawRect(boxLeft, optTop, boxWidth, optHeight);

		g2.setPaint(geo.getObjectColor());

		int textLeft = boxLeft + OPTIONBOX_TEXT_MARGIN_LEFT;
		int rowTop = optTop;
		drawOptionLines(g2, textLeft, rowTop);
	}

	private int drawTextLine(GGraphics2D g2, int textLeft, int top,
 String text,
			boolean latex,
			boolean optionLine, boolean selected) {
		int w = 0;
		int h = 0;
		int left = textLeft;
		int fontHeight = getMultipliedFontSize();
		GBox b = geo.getKernel().getApplication().getSwingFactory()
				.createHorizontalBox(view.getEuclidianController());
		GRectangle rect = b.getBounds();
		if (latex) {
			GDimension d = null;
			if (left == TEXT_CENTER) {
				g2.setPaint(GColor.WHITE);
				d = drawLatex(g2, geoList, getLabelFont(), text, NO_DRAW,
						NO_DRAW);
				left = boxLeft + (boxWidth - d.getWidth()) / 2;
				g2.setPaint(geo.getObjectColor());

			}

			d = drawLatex(g2, geoList, getLabelFont(), text, left, top);

			w = d.getWidth();
			h = d.getHeight();

			if (optionLine) {
				rect.setBounds(boxLeft, top, boxWidth, h);
			}

		} else {
			w = g2.getFontRenderContext().measureTextWidth(text,
					getLabelFont());
			if (left == TEXT_CENTER) {
				left = boxLeft + (boxWidth - w) / 2;
			}
			EuclidianStatic.drawIndexedString(view.getApplication(), g2, text,
					left, top, false, false);

			h = fontHeight;

			if (optionLine) {
				rect.setBounds(boxLeft, top - h, boxWidth, h + 5);
			}


		}

		if (optionLine) {
			optionItems.add(rect);
		}

		if (w > optionsWidth) {
			optionsWidth = w;
		}

		if (h > optionsItemHeight) {
			optionsItemHeight = h;
		}

		return h;
	}

	private void drawOptionLines(GGraphics2D g2, int left, int top) {
		optionsWidth = 0;
		optionsHeight = 0;
		optionsItemHeight = 0;

		optionItems.clear();
		int rowTop = top;
		for (int i = 0; i < geoList.size(); i++) {
			String text = geoList.get(i)
					.toValueString(StringTemplate.defaultTemplate);

			boolean latex = isLatexString(text);
			boolean hovered = i == selectedOptionIndex;

			if (i == 0 && !latex) {
				rowTop += OPTIONBOX_TEXT_MARGIN_TOP;
			}
			int h = drawTextLine(g2, TEXT_CENTER, rowTop, text, latex, true,
					hovered);
			if (hovered && optionItems.size() > i) {
				GRectangle rect = optionItems.get(i);
				g2.setPaint(GColor.LIGHT_GRAY);
				g2.fillRoundRect((int) (rect.getX()), (int) (rect.getY()),
						(int) (rect.getWidth()), (int) (rect.getHeight()), 4,
						4);

				g2.setPaint(GColor.GRAY);

				g2.drawRoundRect((int) (rect.getX()), (int) (rect.getY()),
						(int) (rect.getWidth()), (int) (rect.getHeight()), 4,
						4);

				g2.setPaint(geoList.getObjectColor());
				drawTextLine(g2, TEXT_CENTER, rowTop, text, latex, true,
						hovered);
			}

			if (i == geoList.getSelectedIndex()) {
				selectedText = text;
				selectedHeight = h;
			}

			int gap = latex ? 0 : OPTIONSBOX_ITEM_GAP;
			optionsHeight += h + gap;
			rowTop += h + gap;

		}
		optionsWidth += 2 * COMBO_TEXT_MARGIN + getTriangleControlWidth();
	}

	private int getTriangleControlWidth() {
		return (int) ctrlRect.getWidth();
	}

	int getMultipliedFontSize() {
		return (int) Math.round(((view.getApplication().getFontSize()
				* geoList.getFontSizeMultiplier())));
	}

	@Override
	public GDimension getPreferredSize() {
		// TODO: eliminate magic numbers
		return new GDimension() {

			@Override
			public int getWidth() {
				return optionsWidth;// getMultipliedFontSize() * maxLength;
			}

			@Override
			public int getHeight() {
				return (int) (getMultipliedFontSize() * MUL_FONT_HEIGHT);

			}
		};
	}

	@Override
	protected void showWidget() {

	}

	@Override
	protected void hideWidget() {
	}

	private int getOptionAt(int x, int y) {
		int idx = 0;
		for (GRectangle rect : optionItems) {
			if (rect != null && rect.contains(x, y)) {
				return idx;
			}
			idx++;
		}
		return -1;
	}
	public void onControlClick(int x, int y) {
		if (!isDrawingOnCanvas()) {
			return;
		}

		if (ctrlRect.contains(x, y)) {
			optionsVisible = !optionsVisible;
			geo.updateRepaint();
		}
	}

	public void onOptionDown(int x, int y) {
		if (!isDrawingOnCanvas()) {
			return;
		}
		if (optionsRect.contains(x, y)) {
			int idx = getOptionAt(x, y);
			if (idx == -1) {
				return;
			}
			String text = geoList.get(idx)
					.toValueString(StringTemplate.defaultTemplate);
			closeOptions();
			geoList.setSelectedIndex(idx, true);

		}
	}

	public void closeOptions() {
		optionsVisible = false;
	}

}

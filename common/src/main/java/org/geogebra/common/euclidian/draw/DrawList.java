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

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.RemoveNeeded;
import org.geogebra.common.euclidian.event.ActionEvent;
import org.geogebra.common.euclidian.event.ActionListenerI;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.util.DropDownList;
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
	/** coresponding list as geo */
	GeoList geoList;
	// private ArrayList drawables = new ArrayList();
	private DrawListArray drawables;
	private boolean isVisible;

	private String oldCaption = "";
	/** combobox */
	org.geogebra.common.javax.swing.AbstractJComboBox comboBox;
	private org.geogebra.common.javax.swing.GLabel label;
	private org.geogebra.common.javax.swing.GBox box;
	private DropDownList dropDown = null;
	private int maxLength = 4;
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
			return isDrawingOnCanvas() ? super.hit(x, y, hitThreshold)
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
		maxLength = 2;
		for (int i = 0; i < geoList.size(); i++) {
			String item = geoList.get(i)
					.toValueString(StringTemplate.defaultTemplate);
			// App.debug("[DropDownList] " + item);
			if (maxLength < item.length()) {
				maxLength = item.length();
			}
		}

		String labelText = getLabelText();
		boolean latexLabel = measureLabel(g2, geoList, labelText);
		int textLeft = boxLeft + 2;
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


	}

	@Override
	protected void calculateBoxBounds(boolean latex) {
		boxLeft = xLabel + labelSize.x + 2;
		boxTop = latex
				? yLabel + (labelSize.y - getPreferredSize().getHeight()) / 2
				: yLabel;
		boxWidth = getPreferredSize().getWidth();
		boxHeight = getPreferredSize().getHeight();
	}

	@Override
	protected void calculateBoxBounds() {
		boxLeft = xLabel + 2;
		boxTop = yLabel;
		boxWidth = getPreferredSize().getWidth();
		boxHeight = getPreferredSize().getHeight();
	}

	@Override
	public GDimension getPreferredSize() {
		// TODO: eliminate magic numbers
		return new GDimension() {

			@Override
			public int getWidth() {
				return (int) Math.round(((view.getApplication().getFontSize()
						* geoList.getFontSizeMultiplier()))
 * maxLength);
			}

			@Override
			public int getHeight() {
				return (int) Math.round(((view.getApplication().getFontSize()
						* geoList.getFontSizeMultiplier()))
 * 2);

			}
		};
	}

}

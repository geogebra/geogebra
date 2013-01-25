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

package geogebra.common.euclidian.draw;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.RemoveNeeded;
import geogebra.common.euclidian.event.ActionEvent;
import geogebra.common.euclidian.event.ActionListenerI;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.App;
import geogebra.common.util.Unicode;

/**
 * Draw a GeoList containing drawable objects
 * 
 * @author Markus Hohenwarter
 */
public final class DrawList extends Drawable implements RemoveNeeded {
	/** coresponding list as geo*/
	GeoList geoList;
	// private ArrayList drawables = new ArrayList();
	private DrawListArray drawables;
	private boolean isVisible;

	private String oldCaption;
	/** combobox */
	geogebra.common.javax.swing.AbstractJComboBox comboBox;
	private geogebra.common.javax.swing.GLabel label;
	private geogebra.common.javax.swing.GBox box;


	/**
	 * Creates new drawable list
	 * 
	 * @param view view
	 * @param geoList list
	 */
	public DrawList(EuclidianView view, GeoList geoList) {
		this.view = view;
		this.geoList = geoList;
		geo = geoList;


		reset();

		update();
	}

	private void reset() {


		if (geoList.drawAsComboBox()) {

			if (label == null) {
				label = view.getApplication().getSwingFactory().newJLabel("Label");
				label.setVisible(true);
			}

			if (comboBox == null) {
				comboBox = geoList.getComboBox(view.getViewID());
				comboBox.setVisible(true);
				comboBox.addActionListener(AwtFactory.prototype.newActionListener(new DrawList.ActionListener()));				
			}


			if (box == null) {
				box = view.getApplication().getSwingFactory().createHorizontalBox();
				box.add(label);
				box.add(comboBox);
			}
			view.add(box);		

		} else {

			if (drawables == null) {
				drawables = new DrawListArray(view);
			}
		}
	}

	@Override
	final public void update() {

		if (geoList.drawAsComboBox()) {
			isVisible = geo.isEuclidianVisible() && geoList.size() != 0;
			// textField.setVisible(isVisible);
			// label.setVisible(isVisible);
			box.setVisible(isVisible);
			if (!isVisible) {
				return;
			}
			
			// eg size changed etc
			geoList.rebuildComboxBoxIfNecessary(comboBox);

			// don't need to worry about labeling options, just check if caption set or not
			if (geo.getRawCaption() != null) {
				// get caption to show r
				String caption = geo.getCaption(StringTemplate.defaultTemplate);
				if (!caption.equals(oldCaption)) {
					oldCaption = caption;
					labelDesc = GeoElement.indicesToHTML(caption, true);
				}
				label.setText(labelDesc);
			} else {
				// make sure there's something to drag
				label.setText(Unicode.NBSP + Unicode.NBSP + Unicode.NBSP);
			}

			int fontSize = (int) (view.getFontSize() * geoList.getFontSizeMultiplier());
			App app = view.getApplication();

			geogebra.common.awt.GFont vFont = view.getFont();
			geogebra.common.awt.GFont font = app.getFontCanDisplay(comboBox.getItemAt(0).toString(), false, vFont.getStyle(), fontSize);

			label.setOpaque(false);
			comboBox.setFont(font);
			label.setFont(font);
			comboBox.setForeground(geo.getObjectColor());
			label.setForeground(geo.getObjectColor());
			geogebra.common.awt.GColor bgCol = geo.getBackgroundColor();
			comboBox.setBackground(bgCol != null ? bgCol : view.getBackgroundCommon());

			comboBox.setFocusable(true);
			comboBox.setEditable(false);

			box.validate();

			xLabel = geo.labelOffsetX;
			yLabel = geo.labelOffsetY;
			geogebra.common.awt.GDimension prefSize = box.getPreferredSize();
			labelRectangle.setBounds(xLabel, yLabel, prefSize.getWidth(), prefSize.getHeight());
			box.setBounds(labelRectangle);			
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
				// if (addToDrawableList(listElement, drawablePos, oldDrawableSize))
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
				geogebra.common.awt.GGraphics2D g2 = view.getBackgroundGraphics();
				if (g2 != null)
					drawTrace(g2);
			} else {
				if (isTracing) {
					isTracing = false;
					//view.updateBackground();
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
	protected
	final void drawTrace(geogebra.common.awt.GGraphics2D g2) {
		if (!geoList.drawAsComboBox()) {

			g2.setPaint(geo.getObjectColor());
			g2.setStroke(objStroke);
			if (isVisible) {
				int size = drawables.size();
				for (int i = 0; i < size; i++) {
					Drawable d = (Drawable) drawables.get(i);
					// draw only those drawables that have been created by this
					// list;
					// if d belongs to another object, we don't want to mess with it
					// here
					if (createdByDrawList() || !d.getGeoElement().isLabelSet()) {
						d.draw(g2);
					}
				}
			}
		}
	}


	@Override
	final public void draw(geogebra.common.awt.GGraphics2D g2) {
		if (geoList.drawAsComboBox()) {
			if (isVisible) {
				if (geo.doHighlighting()) {
					label.setOpaque(true);
					label.setBackground(GColor.lightGray);

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
					// if d belongs to another object, we don't want to mess with it
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
	final public boolean hit(int x, int y) {

		if (geoList.drawAsComboBox()) {
			return box.getBounds().contains(x, y);

		} 


		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable d = (Drawable) drawables.get(i);
			if (d.hit(x, y))
				return true;
		}
		return false;

	}

	@Override
	final public boolean isInside(geogebra.common.awt.GRectangle rect) {

		if (geoList.drawAsComboBox()) {
			return rect.contains(labelRectangle);			
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
			return box.getBounds().intersects(rect);
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
	final public geogebra.common.awt.GRectangle getBounds() {
		if (geoList.drawAsComboBox()) {
			return null;
		} 

		if (!geo.isEuclidianVisible())
			return null;

		geogebra.common.awt.GRectangle result = null;

		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable d = (Drawable) drawables.get(i);
			geogebra.common.awt.GRectangle bb = d.getBounds();
			if (bb != null) {
				if (result == null)
					result = geogebra.common.factories.AwtFactory.prototype.newRectangle(bb); // changed () to (bb) bugfix,
				// otherwise top-left of screen
				// is always included
				// add bounding box of list element
				result.add(bb);
			}
		}

		return result;

	}

	/**
	 * Returns false
	 */
	@Override
	public boolean hitLabel(int x, int y) {
		if (geoList.drawAsComboBox()) {
			return false;
		}

		return super.hitLabel(x, y);

	}


	@Override
	final public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	final public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	/**
	 * Listens to events in this combobox
	 * @author Michael + Judit
	 */
	public class ActionListener extends
			geogebra.common.euclidian.event.ActionListener implements
			ActionListenerI {

		/**
		 * @param e action event
		 */
		public void actionPerformed(ActionEvent e) {

			App.debug("action performed" + comboBox.getSelectedIndex());
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


}

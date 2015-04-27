/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Checkbox for free GeoBoolean object.
 * 
 * @author Markus Hohenwarter
 */
public final class DrawBoolean extends Drawable {

	private GeoBoolean geoBool;

	private boolean isVisible;

	// private JCheckBox checkBox;
	// private boolean hit = false;
	private String oldCaption;
	// private BooleanCheckBoxListener cbl;

	private GPoint textSize = new GPoint(0, 0);

	private CheckBoxIcon checkBoxIcon;

	/**
	 * Creates new DrawText
	 * 
	 * @param view
	 *            view
	 * @param geoBool
	 *            boolean (checkbox)
	 */
	public DrawBoolean(EuclidianView view, GeoBoolean geoBool) {
		this.view = view;
		this.geoBool = geoBool;
		geo = geoBool;

		checkBoxIcon = new CheckBoxIcon(view);

		// action listener for checkBox
		// cbl = new BooleanCheckBoxListener();
		// checkBox = new JCheckBox();
		// checkBox.addItemListener(cbl);
		// checkBox.addMouseListener(cbl);
		// checkBox.addMouseMotionListener(cbl);
		// checkBox.setFocusable(false);
		// checkBox.setVisible(false);
		// view.add(checkBox);

		update();
	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		// checkBox.setVisible(isVisible);
		// return here, object is invisible, not just offscreen
		if (!isVisible) {
			return;
		}
		updateStrokes(geoBool);

		// show hide label by setting text
		if (geo.isLabelVisible()) {
			// get caption to show r
			String caption = geoBool.getCaption(StringTemplate.defaultTemplate);
			if (!caption.equals(oldCaption)) {
				oldCaption = caption;
				labelDesc = caption; // GeoElement.indicesToHTML(caption, true);
			}
			// checkBox.setText(labelDesc);
		} else {
			// don't show label
			oldCaption = "";
			labelDesc = "";
			// Michael Borcherds 2007-10-18 BEGIN changed so that vertical
			// position of checkbox doesn't change when label is shown/hidden
			// checkBox.setText("");
			// checkBox.setText(" ");
			// Michael Borcherds 2007-10-18 END
		}

		// checkBox.setOpaque(false);
		// checkBox.setFont(view.fontPoint);
		// checkBox.setForeground(geoBool.getObjectColor());

		// set checkbox state
		// checkBox.removeItemListener(cbl);
		// checkBox.setSelected(geoBool.getBoolean());
		// checkBox.addItemListener(cbl);

		updateLabel();

		// checkBox.

	}

	private void updateLabel() {
		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;
		int size = view.getBooleanSize();
		GDimension prefSize = AwtFactory.prototype.newDimension(size + 12,
				size + 12);// checkBox.getPreferredSize();
		labelRectangle.setBounds(xLabel, yLabel, prefSize.getWidth()
				+ ((textSize == null) ? 0 : textSize.x), prefSize.getHeight());

		// checkBox.setBounds(labelRectangle);
	}

	@Override
	final public void draw(org.geogebra.common.awt.GGraphics2D g2) {

		if (isVisible) {

			int size = view.getBooleanSize();

			g2.setFont(view.getFontPoint());
			g2.setStroke(EuclidianStatic.getDefaultStroke());

			checkBoxIcon.paintIcon(geoBool.getBoolean(),
					geoBool.doHighlighting(), g2, geoBool.labelOffsetX + 5,
					geoBool.labelOffsetY + 5);

			g2.setPaint(geo.getObjectColor());
			textSize = EuclidianStatic.drawIndexedString(view.getApplication(),
					g2, labelDesc, geoBool.labelOffsetX + size + 9,
					geoBool.labelOffsetY + (size + 9) / 2 + 5, false, false);

			updateLabel();
		}

		/*
		 * if (isVisible) { // the button is drawn as a swing component by the
		 * view // They are Swing components and children of the view
		 * 
		 * // draw label rectangle if (geo.doHighlighting()) {
		 * g2.setStroke(objStroke); g2.setPaint(Color.lightGray);
		 * g2.draw(labelRectangle);
		 * 
		 * Application.debug("highlight drawn");
		 * checkBox.setBorder(BorderFactory.createEtchedBorder()); } }
		 */
	}

	/**
	 * Removes button from view again
	 */
	final public void remove() {
		// view.remove(checkBox);
	}

	/**
	 * was this object clicked at? (mouse pointer location (x,y) in screen
	 * coords)
	 */
	@Override
	final public boolean hit(int x, int y, int hitThreshold) {
		return super.hitLabel(x, y);
	}

	@Override
	final public boolean isInside(GRectangle rect) {
		return rect.contains(labelRectangle);
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		return rect.intersects(labelRectangle);
	}

	/**
	 * Returns false
	 */
	@Override
	public boolean hitLabel(int x, int y) {
		return false;
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
	 * Replcement for Swing component
	 * 
	 * @author Michael
	 *
	 */
	public static class CheckBoxIcon {

		// Michael Borcherds 2008-05-11
		// used this as an outline only:
		// http://www.java2s.com/Open-Source/Java-Document/6.0-JDK-Modules-com.sun.java/swing/com/sun/java/swing/plaf/windows/WindowsIconFactory.java.htm
		// references to XPStyle removed
		// option for double-size added
		// replaced UIManager.getColor() with numbers from:
		// http://www.java2s.com/Tutorial/Java/0240__Swing/ListingUIDefaultProperties.htm

		// int csize = 13;

		private EuclidianView ev;

		/** background color when highlighted */
		public static GColor highlightBackground = AwtFactory.prototype
				.newColor(248, 248, 248);

		/**
		 * Creates new checkbox icon
		 * 
		 * @param ev
		 *            view
		 */
		public CheckBoxIcon(EuclidianView ev) {
			this.ev = ev;
		}

		private static GBasicStroke stroke13 = null;
		private static GBasicStroke stroke26 = null;

		/**
		 * Draws the checkbox on graphics
		 * 
		 * @param checked
		 *            true if checked
		 * @param highlighted
		 *            true to highlight
		 * @param g
		 *            graphics
		 * @param x
		 *            x coordinate (left edge)
		 * @param y
		 *            y coordinate (upper edge)
		 */
		public void paintIcon(boolean checked, boolean highlighted,
				GGraphics2D g, int x, int y) {

			int csize = ev.getBooleanSize();

			{
				// outer bevel
					// Draw rounded border
					g.setColor(GColor.DARK_GRAY);
					g.drawRoundRect(x, y, csize, csize, csize / 5, csize / 5);

					// Draw rectangle with rounded borders
					if (highlighted) {
						g.setColor(highlightBackground);
					} else {
						g.setColor(GColor.WHITE);
					}
					g.fillRoundRect(x + 1, y + 1, csize - 2, csize - 2,
							csize / 5, csize / 5);

					g.setColor(GColor.DARK_GRAY);

				// paint check

				if (checked) {
					if (csize == 13) {
						
						if (stroke13 == null) {
							AwtFactory.prototype
							.newBasicStroke(2f, GBasicStroke.CAP_ROUND,
									GBasicStroke.JOIN_ROUND);
						}
						
						g.setStroke(stroke13);
						g.drawLine(x + 2, y + 7, x + 5, y + 10);
						g.drawLine(x + 5, y + 10, x + 10, y + 3);

					} else { // csize == 26
						
						if (stroke26 == null) {
							stroke26 = AwtFactory.prototype
									.newBasicStroke(4f, GBasicStroke.CAP_ROUND,
											GBasicStroke.JOIN_ROUND);
						}
						g.setStroke(stroke26);
						g.drawLine(x + 5, y + 15, x + 10, y + 20);
						g.drawLine(x + 10, y + 20, x + 20, y + 6);

					}
				}
			}
		}

		/**
		 * @return checkbox width
		 */
		public int getIconWidth() {

			return ev.getBooleanSize();

		}

		/**
		 * @return checkbox height
		 */
		public int getIconHeight() {
			return ev.getBooleanSize();

		}
	}
}

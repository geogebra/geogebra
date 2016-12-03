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
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

/**
 * Checkbox for free GeoBoolean object.
 * 
 * @author Markus Hohenwarter
 */
public final class DrawBoolean extends Drawable {

	private static final int LABEL_MARGIN = 9;

	private GeoBoolean geoBool;

	private boolean isVisible;
	private String oldCaption;

	private final GPoint textSize = new GPoint(0, 0);

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
		update();
	}

	private boolean isLatexLabel() {
		return CanvasDrawable.isLatexString(labelDesc);
	}
	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();

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
		}

		updateLabel();

	}

	private void updateLabel() {
		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;
		int size = view.getBooleanSize();
		GDimension prefSize = AwtFactory.getPrototype().newDimension(size + 12,
				size + 12);// checkBox.getPreferredSize();
		labelRectangle.setBounds(xLabel, yLabel, prefSize.getWidth()
				+ textSize.x, prefSize.getHeight());

		// checkBox.setBounds(labelRectangle);
	}

	@Override
	final public void draw(GGraphics2D g2) {

		if (isVisible) {

			g2.setFont(view.getFontPoint());
			g2.setStroke(EuclidianStatic.getDefaultStroke());
			int posX = geoBool.labelOffsetX + checkBoxIcon.getIconWidth() + 5;
			int posY = geoBool.labelOffsetY;

			CheckBoxIcon.paintIcon(geoBool.getBoolean(),
					geoBool.doHighlighting(), g2, geoBool.labelOffsetX + 5,
					geoBool.labelOffsetY + 5, view.getBooleanSize());


			if (isLatexLabel()) {
				GDimension d = CanvasDrawable.measureLatex(
						view.getApplication(), g2, geoBool, g2.getFont(),
						labelDesc);

				textSize.x = d.getWidth();
				textSize.y = d.getHeight();


				if (checkBoxIcon.getIconHeight() < d.getHeight()) {
					posY -= (d.getHeight() - checkBoxIcon.getIconHeight()) / 2;
				} else {
					posY += (checkBoxIcon.getIconHeight() - d.getHeight()) / 2;

				}
				App app = view.getApplication();
				g2.setPaint(geo.getObjectColor());
				g2.setColor(GColor.RED);
				app.getDrawEquation().drawEquation(app, geoBool, g2, posX, posY,
						geoBool.getCaption(StringTemplate.defaultTemplate),
						g2.getFont(), false,
						geoBool.getObjectColor(),
						geoBool.getBackgroundColor(), false, false, null);
			} else {
				g2.setPaint(geo.getObjectColor());
				GTextLayout layout = g2.getFontRenderContext()
						.getTextLayout(labelDesc, view.getFontPoint());

				// ie labelDesc != ""
				if (layout != null) {
					int width = (int) Math.round(layout.getBounds().getWidth());
					int height = (int) Math
							.round(layout.getBounds().getHeight());
					textSize.x = width;
					int left = geoBool.labelOffsetX
							+ checkBoxIcon.getIconWidth() + LABEL_MARGIN;
					int top = geoBool.labelOffsetY
							+ checkBoxIcon.getIconWidth() / 2 + 5;
					top += height / 2;
					EuclidianStatic.drawIndexedString(view.getApplication(), g2,
							labelDesc, left, top, false, false);
				}
			}

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
		public static final GColor highlightBackground = GColor
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
		 * @param csize
		 *            size in px
		 */
		static public void paintIcon(boolean checked, boolean highlighted,
				GGraphics2D g, int x, int y, int csize) {

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
							stroke13 = AwtFactory.getPrototype()
							.newBasicStroke(2f, GBasicStroke.CAP_ROUND,
									GBasicStroke.JOIN_ROUND);
						}
						
						g.setStroke(stroke13);
						g.drawLine(x + 2, y + 7, x + 5, y + 10);
						g.drawLine(x + 5, y + 10, x + 10, y + 3);

					} else { // csize == 26
						
						if (stroke26 == null) {
							stroke26 = AwtFactory.getPrototype()
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

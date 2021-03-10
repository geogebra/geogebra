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
import org.geogebra.common.awt.GEllipse2DDouble;
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
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;

/**
 * Checkbox for free GeoBoolean object.
 * 
 * @author Markus Hohenwarter
 */
public final class DrawBoolean extends Drawable {

	/**
	 * For some reason checkboxes were drawn 5 pixels from the label offset of
	 * the geo (both vertically and horizontally). Cannot be removed, as this
	 * would break old materials :/
	 */
	public static final int LEGACY_OFFSET = 5;

	private static final int LABEL_MARGIN_TEXT = 9;
	private static final int LABEL_MARGIN_LATEX = 5;

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
	public void update() {
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
		int prefSize = size + 12;
		labelRectangle.setBounds(xLabel, yLabel,
				prefSize + textSize.x, prefSize);

	}

	@Override
	public void draw(GGraphics2D g2) {
		if (isVisible) {
			g2.setFont(view.getFontPoint());
			g2.setStroke(EuclidianStatic.getDefaultStroke());

			CheckBoxIcon.paintIcon(geoBool.getBoolean(),
					isHighlighted(), g2, geoBool.labelOffsetX + LEGACY_OFFSET,
					geoBool.labelOffsetY + LEGACY_OFFSET, view.getBooleanSize());

			if (isLatexLabel()) {
				GDimension d = CanvasDrawable.measureLatex(
						view.getApplication(), geoBool, g2.getFont(),
						labelDesc);

				textSize.x = d.getWidth();
				textSize.y = d.getHeight();

				int posX = geoBool.labelOffsetX + checkBoxIcon.getIconWidth()
						+ LABEL_MARGIN_LATEX + LEGACY_OFFSET;
				int posY = geoBool.labelOffsetY
						+ (checkBoxIcon.getIconHeight() - d.getHeight()) / 2 + LEGACY_OFFSET;

				App app = view.getApplication();
				g2.setPaint(geo.getObjectColor());
				g2.setColor(GColor.RED);

				String caption = geoBool
						.getCaption(StringTemplate.defaultTemplate);

				app.getDrawEquation().drawEquation(app, geoBool, g2, posX, posY,
						caption, g2.getFont(),
						StringUtil.startsWithFormattingCommand(caption),
						geoBool.getObjectColor(), geoBool.getBackgroundColor(),
						false, false,
						view.getCallBack(geo, firstCall));
				firstCall = false;
			} else {
				g2.setPaint(geo.getObjectColor());
				GTextLayout layout = getTextLayout(labelDesc,
						view.getFontPoint(), g2);

				// ie labelDesc != ""
				if (layout != null) {
					int width = (int) Math.round(layout.getBounds().getWidth());
					int height = (int) Math
							.round(layout.getBounds().getHeight());
					textSize.x = width;
					int left = geoBool.labelOffsetX
							+ checkBoxIcon.getIconWidth() + LABEL_MARGIN_TEXT + LEGACY_OFFSET;
					int top = geoBool.labelOffsetY
							+ (checkBoxIcon.getIconHeight() + height) / 2 + LEGACY_OFFSET;
					EuclidianStatic.drawIndexedString(view.getApplication(), g2,
							labelDesc, left, top, false);
				}
			}

			updateLabel();
		}
	}

	/**
	 * was this object clicked at? (mouse pointer location (x,y) in screen
	 * coords)
	 */
	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		return super.hitLabel(x, y);
	}

	@Override
	public boolean isInside(GRectangle rect) {
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

	/**
	 * Replcement for Swing component
	 * 
	 * @author Michael
	 *
	 */
	public static class CheckBoxIcon {

		private EuclidianView ev;
		private static GBasicStroke stroke13 = null;
		private static GBasicStroke stroke26 = null;

		// colours for the highlight circle and outline
		private static final GColor highlightBackground = GColor.newColor(0, 0,
				0, 50);
		private static final GColor highlightOutline = GColor.newColor(255, 255,
				255, 128);

		// highlight circle
		private static GEllipse2DDouble highlightCircle = AwtFactory
				.getPrototype().newEllipse2DDouble();

		/**
		 * Creates new checkbox icon
		 * 
		 * @param ev
		 *            view
		 */
		public CheckBoxIcon(EuclidianView ev) {
			this.ev = ev;
		}

		/**
		 * Draws the checkbox on graphics. Based on
		 * http://www.java2s.com/Open-Source/Java-Document/6.0-JDK-Modules-com.
		 * sun.java/swing/com/sun/java/swing/plaf/windows/WindowsIconFactory.
		 * java.htm
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

			if (highlighted) {
				// size of circle when checkbox has focus
				int highlightSize = csize * 3 / 2;

				// white border for so it works with all background colours
				int outlineWidth = 4;

				// outline
				g.setColor(highlightOutline);
				highlightCircle.setFrameFromCenter(x + csize / 2d, y + csize / 2d,
						x + highlightSize + outlineWidth,
						y + highlightSize + outlineWidth);
				g.fill(highlightCircle);

				// fill
				g.setColor(highlightBackground);
				highlightCircle.setFrameFromCenter(x + csize / 2d, y + csize / 2d,
						x + highlightSize, y + highlightSize);
				g.fill(highlightCircle);
			}

			// outer bevel
			// Draw rounded border
			g.setColor(GColor.BLACK);
			g.drawRoundRect(x, y, csize, csize, csize / 5, csize / 5);

			g.setColor(GColor.WHITE);
			g.fillRoundRect(x + 1, y + 1, csize - 2, csize - 2, csize / 5,
					csize / 5);

			g.setColor(GColor.DARK_GRAY);

			// paint check

			if (checked) {
				if (csize == 13) {

					if (stroke13 == null) {
						stroke13 = AwtFactory.getPrototype().newBasicStroke(2f,
								GBasicStroke.CAP_ROUND,
								GBasicStroke.JOIN_ROUND);
					}

					g.setStroke(stroke13);
					g.drawLine(x + 2, y + 7, x + 5, y + 10);
					g.drawLine(x + 5, y + 10, x + 10, y + 3);

				} else { // csize == 26

					if (stroke26 == null) {
						stroke26 = AwtFactory.getPrototype().newBasicStroke(4f,
								GBasicStroke.CAP_ROUND,
								GBasicStroke.JOIN_ROUND);
					}
					g.setStroke(stroke26);
					g.drawLine(x + 5, y + 15, x + 10, y + 20);
					g.drawLine(x + 10, y + 20, x + 20, y + 6);

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

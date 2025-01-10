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
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraColorConstants;
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

	public static final int LABEL_MARGIN_TEXT = 9;
	public static final int LABEL_MARGIN_LATEX = 5;

	private static final int CHECKBOX_SIZE = EuclidianConstants.DEFAULT_CHECKBOX_SIZE;

	private final GeoBoolean geoBool;

	private boolean isVisible;
	private String oldCaption;

	private int textWidth = 0;

	/**
	 * Creates new DrawBoolean
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
		if (getDynamicCaption() != null && getDynamicCaption().isEnabled()) {
			getDynamicCaption().update();
		}
	}

	private void updateLabel() {
		xLabel = geoBool.getAbsoluteScreenLocX();
		yLabel = geoBool.getAbsoluteScreenLocY();
		int prefSize = CHECKBOX_SIZE + 12;
		labelRectangle.setBounds(xLabel, yLabel,
				prefSize + textWidth, prefSize);
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (isVisible) {
			g2.setFont(view.getFontPoint());
			int checkboxX = geoBool.getAbsoluteScreenLocX() + LEGACY_OFFSET;
			int checkboxY = geoBool.getAbsoluteScreenLocY() + LEGACY_OFFSET;
			if (isHighlighted()) {
				CheckBoxIcon.highlightIcon(g2, checkboxX, checkboxY);
			}
			CheckBoxIcon.paintIcon(geoBool.getBoolean(),
					!geoBool.isSelectionAllowed(view), g2, checkboxX, checkboxY);

			if (getDynamicCaption() != null && getDynamicCaption().isEnabled()) {
				getDynamicCaption().draw(g2);
				textWidth = getDynamicCaption().getWidth();
			} else if (isLatexLabel()) {
				GDimension d = CanvasDrawable.measureLatex(
						view.getApplication(), g2.getFont(),
						labelDesc);

				textWidth = d.getWidth();

				int captionX = checkboxX + CHECKBOX_SIZE + LABEL_MARGIN_LATEX;
				int captionY = getCaptionY(true, d.getHeight());

				App app = view.getApplication();
				g2.setPaint(geo.getObjectColor());

				app.getDrawEquation().drawEquation(app, geoBool, g2, captionX, captionY,
						labelDesc, g2.getFont(),
						StringUtil.startsWithFormattingCommand(labelDesc),
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
					textWidth = width;
					int captionX = checkboxX + CHECKBOX_SIZE + LABEL_MARGIN_TEXT;
					int captionY = getCaptionY(false, height);
					EuclidianStatic.drawIndexedString(view.getApplication(), g2,
							labelDesc, captionX, captionY, false);
				}
			}

			updateLabel();
		}
	}

	@Override
	public int getCaptionY(boolean latex, int height) {
		return latex ? geoBool.getAbsoluteScreenLocY()
				+ (CHECKBOX_SIZE - height) / 2 + LEGACY_OFFSET
				: geoBool.getAbsoluteScreenLocY()
				+ (CHECKBOX_SIZE + height) / 2 + LEGACY_OFFSET;
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
	 * Replacement for Swing component
	 */
	public static class CheckBoxIcon {

		private static final int BORDER_DIAMETER = 4;
		private static GBasicStroke stroke26 = null;
		private static GGeneralPath gp;

		/**
		 * @param g graphics
		 * @param x x-coordinate of the icon
		 * @param y y-coordinate of the icon
		 */
		public static void highlightIcon(GGraphics2D g, int x, int y) {
			g.setColor(GColor.HIGHLIGHT_GRAY);
			g.setStroke(AwtFactory.getPrototype()
					.newMyBasicStroke(Drawable.UI_ELEMENT_HIGHLIGHT_WIDTH));

			g.drawRoundRect(x, y, CHECKBOX_SIZE, CHECKBOX_SIZE, BORDER_DIAMETER, BORDER_DIAMETER);
		}

		/**
		 * Draws the checkbox on graphics.
		 * 
		 * @param checked
		 *            true if checked
		 * @param disabled
		 *            true if disabled
		 * @param g
		 *            graphics
		 * @param x
		 *            x coordinate (left edge)
		 * @param y
		 *            y coordinate (upper edge)
		 */
		static public void paintIcon(boolean checked, boolean disabled,
				GGraphics2D g, int x, int y) {
			g.setStroke(AwtFactory.getPrototype().newBasicStroke(2.0));
			GColor bg = GColor.WHITE;
			if (checked) {
				bg = disabled ? GeoGebraColorConstants.NEUTRAL_500
						: GeoGebraColorConstants.PURPLE_600;
			}
			g.setColor(bg);
			g.fillRoundRect(x, y, CHECKBOX_SIZE, CHECKBOX_SIZE, BORDER_DIAMETER,
					BORDER_DIAMETER);
			// Draw rounded border
			if (!checked) {
				g.setColor(disabled
						? GeoGebraColorConstants.NEUTRAL_500
						: GeoGebraColorConstants.NEUTRAL_700);
				g.drawRoundRect(x + 1, y + 1, CHECKBOX_SIZE - 2,
						CHECKBOX_SIZE - 2, BORDER_DIAMETER, BORDER_DIAMETER);
			}
			// paint check
			if (checked) {
				if (stroke26 == null) {
					initPathAndStroke();
				}
				g.setColor(GColor.WHITE);
				g.setStroke(stroke26);
				g.translate(x, y);
				g.draw(gp);
				g.translate(-x, -y);
			}
		}

		private static void initPathAndStroke() {
			// based on ic_check_white24px.svg, scaled to 20px and offset by 3px
			//{1.73,12.91,8.1,19.28,22.79,4.59}*20/24 + 3
			//gives {4.44,13.76,9.75,19.07,21.99,6.83}
			stroke26 = AwtFactory.getPrototype().newBasicStroke(2.6,
					GBasicStroke.CAP_SQUARE,
					GBasicStroke.JOIN_MITER);
			gp = AwtFactory.getPrototype().newGeneralPath();
			gp.moveTo(4.44, 13.76);
			gp.lineTo(9.75, 19.07);
			gp.lineTo(21.99, 6.83);
		}

	}

}

/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;

/**
 * Drawable representation of text
 * 
 * @author Markus
 */
public final class DrawText extends Drawable {

	/**
	 * color used to draw rectangle around text when highlighted
	 */
	public static final GColor HIGHLIGHT_COLOR = GColor.LIGHT_GRAY;

	// private static final int SELECTION_DIAMETER_ADD = 4;
	// private static final int SELECTION_OFFSET = SELECTION_DIAMETER_ADD / 2;

	private GeoText text;
	private boolean isVisible, isLaTeX;
	private int fontSize = -1;
	private int fontStyle = -1;
	private boolean serifFont;
	private GFont textFont;
	private GeoPointND loc; // text location

	// private Image eqnImage;
	private int oldXpos, oldYpos;
	private boolean needsBoundingBoxOld;

	/**
	 * Creates new DrawText
	 * 
	 * @param view
	 *            view
	 * @param text
	 *            text
	 */
	public DrawText(EuclidianView view, GeoText text) {
		this.view = view;
		this.text = text;
		geo = text;

		textFont = view
				.getApplication()
				.getPlainFontCommon()
				.deriveFont(GFont.PLAIN, view.getFontSize());

		// this is needed as (bold) LaTeX texts are created with isLaTeX = false
		// at this stage
		updateStrokes(text);

		update();
	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible && !text.isNeedsUpdatedBoundingBox()) // needed for
																// Corner[Element[text
			return;

		if (isLaTeX) // needed eg for \sqrt
			updateStrokes(text);

		if (isLaTeX) // needed eg for \sqrt
			updateStrokes(text);

		String newText = text.getTextString();
		boolean textChanged = labelDesc == null || !labelDesc.equals(newText)
				|| isLaTeX != text.isLaTeX()
				|| text.isNeedsUpdatedBoundingBox() != needsBoundingBoxOld;
		labelDesc = newText;
		isLaTeX = text.isLaTeX();
		needsBoundingBoxOld = text.isNeedsUpdatedBoundingBox();

		// compute location of text
		if (text.isAbsoluteScreenLocActive()) {
			xLabel = text.getAbsoluteScreenLocX();
			yLabel = text.getAbsoluteScreenLocY();
		} else {
			loc = text.getStartPoint();
			if (loc == null) {
				xLabel = (int) view.getxZero();
				yLabel = (int) view.getyZero();
			} else {
				if (!loc.isDefined()) {
					isVisible = false;
					return;
				}

				// looks if it's on view
				Coords p = view.getCoordsForView(loc.getInhomCoordsInD3());
				if (!Kernel.isZero(p.getZ())) {
					isVisible = false;
					return;
				}

				xLabel = view.toScreenCoordX(p.getX());
				yLabel = view.toScreenCoordY(p.getY());
			}
			xLabel += text.labelOffsetX;
			yLabel += text.labelOffsetY;

		}

		boolean positionChanged = xLabel != oldXpos || yLabel != oldYpos;
		oldXpos = xLabel;
		oldYpos = yLabel;

		boolean fontChanged = doUpdateFontSize();

		// some commented code for LaTeX speedup removed in r22321

		// We need check for null bounding box because of
		// SetValue[text,Text["a",(1,1)]] makes it null
		if (text.isNeedsUpdatedBoundingBox()
				&& (textChanged || positionChanged || fontChanged
						|| text.getKernel().getForceUpdatingBoundingBox() || text
						.getBoundingBox() == null)) {
			// ensure that bounding box gets updated by drawing text once
			if (isLaTeX)
				drawMultilineLaTeX(view.getTempGraphics2D(textFont), textFont,
						geo.getObjectColor(), view.getBackgroundCommon());
			else
				drawMultilineText(view.getTempGraphics2D(textFont), textFont);

			// update corners for Corner[] command
			double xRW = view.toRealWorldCoordX(labelRectangle.getX());
			double yRW = view.toRealWorldCoordY(labelRectangle.getY());

			text.setBoundingBox(xRW, yRW,
					labelRectangle.getWidth() * view.getInvXscale(),
					-labelRectangle.getHeight() * view.getInvYscale());
		}
	}

	@Override
	final public void draw(GGraphics2D g2) {
		if (isVisible) {

			GColor bg = geo.getBackgroundColor();

			if (bg != null) {

				// needed to calculate labelRectangle
				if (isLaTeX) {
					drawMultilineLaTeX(view.getTempGraphics2D(textFont),
							textFont, geo.getObjectColor(),
							view.getBackgroundCommon());
				} else {
					drawMultilineText(view.getTempGraphics2D(textFont),
							textFont);
				}
				g2.setStroke(objStroke);
				g2.setPaint(bg);
				g2.fill(labelRectangle);
			}

			if (isLaTeX) {
				g2.setPaint(geo.getObjectColor());
				g2.setFont(textFont);
				g2.setStroke(objStroke); // needed eg for \sqrt
				drawMultilineLaTeX(g2, textFont, geo.getObjectColor(),
						bg != null ? bg : view.getBackgroundCommon());
			} else {
				g2.setPaint(geo.getObjectColor());
				// g2.setFont(textFont);
				drawMultilineText(g2, textFont);
			}

			// draw label rectangle
			if (geo.doHighlighting()) {
				g2.setStroke(objStroke);
				g2.setPaint(HIGHLIGHT_COLOR);
				g2.draw(labelRectangle);
			}
		}
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
		return labelRectangle.intersects(rect);
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

	private boolean doUpdateFontSize() {
		// text's font size is relative to the global font size
		int newFontSize = (int) Math.max(4,
				view.getFontSize() * text.getFontSizeMultiplier());
		int newFontStyle = text.getFontStyle();
		boolean newSerifFont = text.isSerifFont();

		if (text.getTextString() != null
				&& textFont.canDisplayUpTo(text.getTextString()) != -1
				|| fontSize != newFontSize || fontStyle != newFontStyle
				|| newSerifFont != serifFont) {
			super.updateFontSize();

			fontSize = newFontSize;
			fontStyle = newFontStyle;
			serifFont = newSerifFont;

			// if (isLaTeX) {
			// //setEqnFontSize();
			// } else {
			App app = view.getApplication();
			textFont = app.getFontCanDisplay(text.getTextString(), serifFont,
					fontStyle, fontSize);
			// }
			return true;
		}

		return false;
	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 */
	@Override
	final public GRectangle getBounds() {
		if (!geo.isDefined() || ((GeoText) geo).isAbsoluteScreenLocActive()
				|| !geo.isEuclidianVisible()) {
			return null;
		}
		return labelRectangle;
	}
}

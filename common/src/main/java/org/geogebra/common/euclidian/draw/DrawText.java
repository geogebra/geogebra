/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.TextController;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.DoubleUtil;

/**
 * Drawable representation of text
 * 
 * @author Markus
 */
public final class DrawText extends Drawable {

	/**
	 * minimum height of width
	 */
	public static final int MIN_EDITOR_WIDTH = 100;
	/**
	 * minimum height of editor
	 */
	public static final int MIN_EDITOR_HEIGHT = 20;
	/**
	 * color used to draw rectangle around text when highlighted
	 */
	public static final GColor HIGHLIGHT_COLOR = GColor.LIGHT_GRAY;

	private GeoText text;
	private boolean isVisible;
	private boolean isLaTeX;
	private int fontSize = -1;
	private int fontStyle = -1;
	private boolean serifFont;
	private GFont textFont;

	// private Image eqnImage;
	private int oldXpos;
	private int oldYpos;
	private boolean needsBoundingBoxOld;

	private BoundingBox boundingBox;
	/**
	 * thickness for the highlight frame
	 */
	static final public int HIGHLIGHT_THICKNESS = 2;
	private static GBasicStroke rectangleStroke = AwtFactory.getPrototype()
			.newBasicStroke(HIGHLIGHT_THICKNESS);

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

		textFont = view.getApplication().getPlainFontCommon()
				.deriveFont(GFont.PLAIN, view.getFontSize());

		// this is needed as (bold) LaTeX texts are created with isLaTeX = false
		// at this stage
		updateStrokes(text);

		update();
	}

	@Override
	public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible && !text.isNeedsUpdatedBoundingBox()) {
			// Corner[Element[text
			return;
		}

		if (isLaTeX) {
			updateStrokes(text);
		}

		if (isLaTeX) {
			updateStrokes(text);
		}

		String newText;
		TextController ctrl = view.getEuclidianController().getTextController();
		if (geo.getKernel().getApplication().has(Feature.MOW_TEXT_TOOL) && ctrl != null) {
			newText = ctrl.wrapText(text.getTextString(), this, this.getBounds());
		} else {
			newText = text.getTextString();
		}
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
			GeoPointND loc = text.getStartPoint();
			if (loc == null) {
				xLabel = (int) view.getXZero();
				yLabel = (int) view.getYZero();
			} else {
				if (!loc.isDefined()) {
					isVisible = false;
					return;
				}

				// looks if it's on view
				Coords p = view.getCoordsForView(loc.getInhomCoordsInD3());
				if (!DoubleUtil.isZero(p.getZ())) {
					isVisible = false;
					return;
				}

				xLabel = view.toScreenCoordX(p.getX());
				yLabel = view.toScreenCoordY(p.getY());
			}
			xLabel += text.labelOffsetX;
			yLabel += text.labelOffsetY;

			text.setTotalWidth((int) labelRectangle.getWidth());
			text.setTotalHeight((int) labelRectangle.getHeight());

		}

		boolean positionChanged = xLabel != oldXpos || yLabel != oldYpos;
		oldXpos = xLabel;
		oldYpos = yLabel;

		boolean fontChanged = doUpdateFontSize();

		// some commented code for LaTeX speedup removed in r22321

		// We need check for null bounding box because of
		// SetValue[text,Text["a",(1,1)]] makes it null
		if (text.isNeedsUpdatedBoundingBox() && (textChanged || positionChanged || fontChanged
				|| text.getKernel().getForceUpdatingBoundingBox()
				|| text.getBoundingBox() == null)) {
			// ensure that bounding box gets updated by drawing text once
			if (isLaTeX) {
				drawMultilineLaTeX(view.getTempGraphics2D(textFont), textFont, geo.getObjectColor(),
						view.getBackgroundCommon());
			} else {
				drawMultilineText(view.getTempGraphics2D(textFont), textFont);
			}

			// update corners for Corner[] command
			double xRW = view.toRealWorldCoordX(labelRectangle.getX());
			double yRW = view.toRealWorldCoordY(labelRectangle.getY());

			text.setBoundingBox(xRW, yRW, labelRectangle.getWidth() * view.getInvXscale(),
					-labelRectangle.getHeight() * view.getInvYscale());
		}

		if (boundingBox != null) {
			if (isWhiteboardText()) {
				boundingBox.setRectangle(getBounds());
				text.setMowBoundingBox(getBounds());
			} else {
				boundingBox.resetBoundingBox();
			}
		}
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (isVisible) {
			GColor bg = geo.getBackgroundColor();

			if (bg != null) {

				// nee0ded to calculate labelRectangle
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
				if (!text.isEditMode()) {
					if (geo.getAlphaValue() > 0.0) {
						g2.setPaint(geo.getFillColor());
					} else {
						g2.setPaint(geo.getObjectColor());
					}
					drawMultilineText(g2, textFont);
				}
			}

			// draw label rectangle
			if (isHighlighted()) {
				g2.setStroke(rectangleStroke);
				g2.setPaint(HIGHLIGHT_COLOR);
				g2.draw(labelRectangle);
			}
		}
	}

	/**
	 * Adjust bounding box to editor/text bounds.
	 * 
	 * @param rect
	 *            editor bounds
	 */
	public void adjustBoundingBoxToText(GRectangle rect) {
		if (rect == null || labelRectangle == null) {
			return;
		}
		labelRectangle.setBounds(rect);
		if (boundingBox != null) {
			boundingBox.setRectangle(labelRectangle);
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
		return labelRectangle.intersects(rect);
	}

	@Override
	public double getWidthThreshold() {
		return MIN_EDITOR_WIDTH;
	}

	@Override
	public double getHeightThreshold() {
		return Math.max(text.getTextHeight(), fontSize) + 2 * EuclidianStatic.EDITOR_MARGIN;
	}

	/**
	 * Returns false
	 */
	@Override
	public boolean hitLabel(int x, int y) {
		return false;
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	private boolean doUpdateFontSize() {
		// text's font size is relative to the global font size
		int newFontSize = getFontSize();
		int newFontStyle = text.getFontStyle();
		boolean newSerifFont = text.isSerifFont();

		if (incompatibleCharacters()
				|| fontSize != newFontSize || fontStyle != newFontStyle
				|| newSerifFont != serifFont) {
			super.updateFontSize();

			fontSize = newFontSize;
			fontStyle = newFontStyle;
			serifFont = newSerifFont;

			App app = view.getApplication();
			textFont = app.getFontCanDisplay(text.getTextString(), serifFont,
					fontStyle, fontSize);

			return true;
		}

		return false;
	}

	private boolean incompatibleCharacters() {
		return text.getTextString() != null
				&& textFont.canDisplayUpTo(text.getTextString()) != -1;
	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 */
	@Override
	public GRectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}
		if (text.isEditMode() && (labelRectangle.getWidth() < MIN_EDITOR_WIDTH
				|| labelRectangle.getHeight() < MIN_EDITOR_HEIGHT)) {
			labelRectangle.setBounds((int) labelRectangle.getX(),
					(int) labelRectangle.getY(),
					(int) Math.max(labelRectangle.getWidth(), MIN_EDITOR_WIDTH),
					(int) Math.max(labelRectangle.getHeight(),
							MIN_EDITOR_HEIGHT));
		} else if (view.getApplication().has(Feature.MOW_TEXT_TOOL)) {
			if (text.isMowBoundingBoxJustLoaded()) {
				labelRectangle.setBounds(text.getMowBoundingBox());
				text.setMowBoundingBoxJustLoaded(false);
			}
		}
		return labelRectangle;
	}

	@Override
	public BoundingBox getBoundingBox() {
		if (isWhiteboardText()) {
			if (boundingBox == null) {
				boundingBox = createBoundingBox(false, false);
				boundingBox.setRectangle(getBounds());
			}
			boundingBox.updateFrom(geo);
			return boundingBox;
		}
		return null;
	}

	private boolean isWhiteboardText() {
		return view.getApplication().has(Feature.MOW_TEXT_TOOL)
				&& text.isIndependent() && !text.isLaTeX();
	}

	@Override
	public void fromPoints(ArrayList<GPoint2D> pts) {
		GeoPointND startPoint = text.getStartPoint();
		double left = Math.min(pts.get(0).getX(), pts.get(1).getX())
				+ EuclidianStatic.EDITOR_MARGIN;
		double bottom = Math.min(pts.get(0).getY(), pts.get(1).getY())
				+ fontSize + EuclidianStatic.EDITOR_MARGIN;
		startPoint.setCoords(view.toRealWorldCoordX(left), view.toRealWorldCoordY(bottom), 1.0);
		int scaledWidth = (int) Math.abs(pts.get(0).getX() - pts.get(1).getX());
		int scaledHeight = (int) Math.abs(pts.get(0).getY() - pts.get(1).getY());
		// enforce min width for multiselection + resize
		labelRectangle.setSize(Math.max(scaledWidth, (int) getWidthThreshold()),
				Math.max(scaledHeight, (int) getHeightThreshold()));
	}

	/**
	 * @return font size
	 */
	public int getFontSize() {
		return (int) Math.max(4,
				view.getFontSize() * text.getFontSizeMultiplier());
	}

	/**
	 * @return the textFont
	 */
	public GFont getTextFont() {
		return textFont;
	}

	@Override
	public double getDiagonalWidthThreshold() {
		GeoText textCopy = text.copy();
		textCopy.setVisualStyle(text);
		return findDiagonalThreshold(MIN_EDITOR_WIDTH, labelRectangle.getWidth(), textCopy);
	}

	private double findDiagonalThreshold(double min, double max, GeoText textCopy) {
		if (max <= min + 1) {
			return max;
		}
		TextController ctrl = view.getEuclidianController().getTextController();
		int newWidth = (int) (min + max) / 2;
		GRectangle newBounds = AwtFactory.getPrototype().newRectangle(newWidth, view.getHeight());
		String newText = ctrl.wrapText(text.getTextString(), this, newBounds);
		
		EuclidianStatic.drawMultiLineText(
				view.getApplication(), newText, xLabel, yLabel, view.getGraphicsForPen(),
				text.isSerifFont(), textFont, newBounds, textCopy);
		double ratio = labelRectangle.getHeight() / labelRectangle.getWidth();
		if (text.getTextHeight() > ratio * newWidth) {
			return findDiagonalThreshold(newWidth, max, textCopy);
		} else {
			return findDiagonalThreshold(min, newWidth, textCopy);
		}
	}
}
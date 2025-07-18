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
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.App;
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

	public static final int DEFAULT_MARGIN = 3;
	private final AlignDrawText alignment;

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
		alignment = new AlignDrawText(text, this);
		// this is needed as (bold) LaTeX texts are created with isLaTeX = false
		// at this stage
		updateStrokes(text);

		update();
	}

	@Override
	public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible && !text.needsUpdatedBoundingBox()) {
			// Corner[Element[text
			return;
		}

		if (isLaTeX) {
			updateStrokes(text);
		}

		String newText = text.getTextString();

		final boolean textChanged = labelDesc == null || !labelDesc.equals(newText)
				|| isLaTeX != text.isLaTeX()
				|| text.needsUpdatedBoundingBox() != needsBoundingBoxOld;
		labelDesc = newText;
		isLaTeX = text.isLaTeX();
		needsBoundingBoxOld = text.needsUpdatedBoundingBox();

		// compute location of text
		updateLabelPosition();

		boolean positionChanged = xLabel != oldXpos || yLabel != oldYpos
				|| alignment.hasChanged();
		oldXpos = xLabel;
		oldYpos = yLabel;
		alignment.update();
		boolean fontChanged = doUpdateFontSize();

		// some commented code for LaTeX speedup removed in r22321

		// We need check for null bounding box because of
		// SetValue[text,Text["a",(1,1)]] makes it null
		if (needsBoundingBoxUpdate(textChanged || positionChanged || fontChanged)) {
			// ensure that bounding box gets updated by drawing text once
			updateLabelRectangle();
			if (text.hasAlignment() && text.isDefined()) {
				align();
				if (text.needsUpdatedBoundingBox()) {
					updateLabelRectangle(); // recompute again to make Corner correct
				}
			}
			// update corners for Corner[] command
			double xRW = view.toRealWorldCoordX(labelRectangle.getX());
			double yRW = view.toRealWorldCoordY(labelRectangle.getY());
			text.setBoundingBox(xRW, yRW, labelRectangle.getWidth() * view.getInvXscale(),
					- labelRectangle.getHeight() * view.getInvYscale());
		} else if (text.hasAlignment()) {
			align();
		}
	}

	private void align() {
		alignment.apply(labelRectangle.getWidth(), labelRectangle.getHeight());
	}

	private boolean needsBoundingBoxUpdate(boolean changed) {
		if (geo.getBackgroundColor() != null) {
			return true;
		}
		return (text.needsUpdatedBoundingBox() || text.hasAlignment()) && (changed
				|| text.getKernel().getForceUpdatingBoundingBox()
				|| text.getBoundingBox() == null);
	}

	private void updateLabelPosition() {
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
	}

	private void updateLabelRectangle() {
		if (isLaTeX) {
			drawMultilineLaTeX(view.getTempGraphics2D(textFont), textFont, geo.getObjectColor(),
					view.getBackgroundCommon());
		} else {
			drawMultilineText(view.getTempGraphics2D(textFont), textFont);
		}
	}

	/**
	 * Updates label rectangle assuming xLabel and yLabel have been set before
	 * @param captionWidth caption width
	 */
	void updateLabelRectangleForCaption(int captionWidth) {
		labelRectangle.setLocation(xLabel,
				isLaTeX ? yLabel : (int) (yLabel - labelRectangle.getHeight()
						+ 2 * DEFAULT_MARGIN));
		labelRectangle.setSize(captionWidth, (int) labelRectangle.getHeight());
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (isVisible) {
			GColor bg = geo.getBackgroundColor();

			if (bg != null) {
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
				if (geo.getAlphaValue() > 0.0) {
					g2.setPaint(geo.getFillColor());
				} else {
					g2.setPaint(geo.getObjectColor());
				}
				drawMultilineText(g2, textFont);
			}

			if (isHighlighted()) {
				drawHighlightRectangle(g2);
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

	/**
	 * Returns false
	 */
	@Override
	public boolean hitLabel(int x, int y) {
		return false;
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

		return labelRectangle;
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
	public boolean isHighlighted() {
		return view.getApplication().getSelectionManager().isKeyboardFocused(geo);
	}
}

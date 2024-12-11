/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * DrawSlider: draws a slider to change a number continuously
 */

package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.EdgeInsets;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;

/**
 * @author Markus Hohenwarter
 */
public class DrawSlider extends Drawable {

	private GeoNumeric number;

	private boolean isVisible;
	private boolean labelVisible;

	private double[] coordsRW = new double[2];
	private double[] coordsScreen = new double[2];
	// private GeoPoint geoPoint;
	// private DrawPointSlider drawPoint;
	// private GeoPointND P;
	private int diameter = 2 * GeoNumeric.DEFAULT_SLIDER_BLOB_SIZE + 1;
	private int lineThickness = GeoNumeric.DEFAULT_SLIDER_THICKNESS;

	// for dot and selection
	private GEllipse2DDouble circle = AwtFactory.getPrototype()
			.newEllipse2DDouble();
	private GEllipse2DDouble circleOuter = AwtFactory.getPrototype()
			.newEllipse2DDouble();
	private GEllipse2DDouble circleHighlight = AwtFactory.getPrototype()
			.newEllipse2DDouble();

	private static final GBasicStroke borderStroke = EuclidianStatic
			.getDefaultStroke();
	GBasicStroke highlightStroke = AwtFactory.getPrototype().newBasicStroke(2);

	private double[] coords = new double[2];

	private GLine2D line = AwtFactory.getPrototype().newLine2D();

	// use blue "halo" when blob is white
	private GColor blueHalo;

	/**
	 * Creates new drawable for slider
	 * @param view view
	 * @param number slider
	 */
	public DrawSlider(EuclidianView view, GeoNumeric number) {
		this.view = view;
		this.number = number;
		geo = number;
		update();
	}

	final private void updateScreenCoords() {
		// if (number.isAbsoluteScreenLocActive() && initX >= 0 && initY >= 0) {
		// number.fixPositionHorizontal(initX, view.getSettings()
		// .getFileWidth(),
		// view.getWidth());
		// number.fixPositionVertical(initY, view.getSettings()
		// .getFileHeight(),
		// view.getHeight());
		// }

		isVisible = geo.isEuclidianVisible();
		if (isVisible) {
			double widthRW;
			double widthScreen;
			boolean horizontal = number.isSliderHorizontal();
			GPoint2D location = getSliderLocation();
			// start point of horizontal line for slider
			if (number.isAbsoluteScreenLocActive()) {
				coordsScreen[0] = location.x;
				coordsScreen[1] = location.y;
				coordsRW[0] = view.toRealWorldCoordX(coordsScreen[0]);
				coordsRW[1] = view.toRealWorldCoordY(coordsScreen[1]);
				widthScreen = number.getSliderWidth();
				widthRW = horizontal ? widthScreen * view.getInvXscale()
						: widthScreen * view.getInvYscale();
			} else {
				coordsRW[0] = location.x;
				coordsRW[1] = location.y;
				coordsScreen[0] = view.toScreenCoordXd(coordsRW[0]);
				coordsScreen[1] = view.toScreenCoordYd(coordsRW[1]);
				widthRW = number.getSliderWidth();
				widthScreen = horizontal ? widthRW * view.getXscale()
						: widthRW * view.getYscale();
			}

			// point on slider that moves
			double min = number.getIntervalMin();
			double max = number.getIntervalMax();

			double param = (number.getValue() - min) / (max - min);
			// setPointSize(2 + (number.getLineThickness() + 1) / 3);

			// horizontal slider
			if (horizontal) {
				updatePoint(coordsRW[0] + widthRW * param, coordsRW[1]);
				if (labelVisible) {
					this.xLabel -= getHorizontalOffsetX();
					this.yLabel -= 5;
				}
				// horizontal line
				this.line.setLine(coordsScreen[0], coordsScreen[1],
						coordsScreen[0] + widthScreen, coordsScreen[1]);
			}
			// vertical slider
			else {
				updatePoint(coordsRW[0], coordsRW[1] + widthRW * param);
				if (labelVisible) {
					this.xLabel += 5;
					this.yLabel += 2 * number.getSliderBlobSize() + 4;
				}
				this.line.setLine(coordsScreen[0], coordsScreen[1],
						coordsScreen[0], coordsScreen[1] - widthScreen);
				// vertical line
			}

			lineThickness = number.getLineThickness();
			updateStrokes(number, number.getLineThickness());
		}
	}

	private double getHorizontalOffsetX() {
		EdgeInsets safeArea = view.getSafeAreaInsets();
		return Math.max(Math.min(xLabel - safeArea.getLeft(), 15), 0);
	}

	/**
	 * Returns the slider location either absolute or relative.
	 * @return slider location
	 */
	public GPoint2D getSliderLocation() {
		double x = number.getSliderX();
		double y = number.getSliderY();
		if (number.isAbsoluteScreenLocActive()) {
			EdgeInsets insets = view.getSafeAreaInsets();
			x = Math.max(x, insets.getLeft());
			y = Math.max(y, insets.getTop());
		}
		return new GPoint2D(x, y);
	}

	@Override
	final public void update() {
		updateScreenCoords();
		// if (needsAdjusted()) {
		// // Log.debug(ADJUST + " needed for " + geo.getNameDescription());
		// updateScreenCoords();
		// }
	}

	@Override
	final public void draw(GGraphics2D g2) {
		if (isVisible) {
			// horizontal line
			g2.setPaint(geo.getBackgroundColor() == null
					? geo.getSelColor()
					: geo.getBackgroundColor());
			g2.setStroke(objStroke);
			g2.drawStraightLine(line.getP1().getX(), line.getP1().getY(),
					line.getP2().getX(), line.getP2().getY());
			GColor selColor = geo.getSelColor();

			if (selColor.getRed() == 255 && selColor.getGreen() == 255
					&& selColor.getBlue() == 255) {

				if (blueHalo == null) {
					blueHalo = GColor.newColor(0x84, 0xbe, 0xe9,
							selColor.getAlpha());
				}
				// use blue "halo" when blob is white
				g2.setPaint(blueHalo);
			} else {
				g2.setPaint(geo.getSelColor());
			}

			if (isHighlighted()) {
				g2.fill(circleHighlight);
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(highlightStroke);
				g2.draw(circleHighlight);
			} else {
				g2.fill(circleOuter);
				g2.setStroke(borderStroke);
				g2.draw(circleOuter);
				g2.setPaint(geo.getObjectColor());
			}

			// draw a dot
			g2.fill(circle);

			// black stroke
			g2.setPaint(GColor.BLACK);
			g2.setStroke(borderStroke);
			g2.draw(circle);

			// point
			if (labelVisible) {
				g2.setFont(view.getFontPoint());
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
			}
		}
	}

	@Override
	final public boolean hit(int x, int y, int hitThreshold) {
		return hitPoint(x, y, hitThreshold) || hitSlider(x, y, hitThreshold);
	}

	@Override
	final public boolean isInside(GRectangle rect) {
		return rect.contains(circle.getBounds());
	}

	/**
	 * Returns true iff the movable point was hit
	 * @param x mouse x-coord
	 * @param y mouse y-coord
	 * @param hitThreshold threshold
	 * @return true iff the movable point was hit
	 */
	final public boolean hitPoint(int x, int y, int hitThreshold) {
		int r = hitThreshold
				+ Math.max(diameter, GeoNumeric.DEFAULT_SLIDER_BLOB_SIZE);
		double dx = coords[0] - x;
		double dy = coords[1] - y;
		return dx < r && dx > -r && dx * dx + dy * dy <= r * r;
	}

	@Override
	public boolean hitLabel(int x, int y) {
		return super.hitLabel(x, y);
	}

	/**
	 * Returns true if the slider line was hit, false for fixed sliders
	 * @param x mouse x-coord
	 * @param y mouse y-coord
	 * @param hitThreshold threshold
	 * @return true if the slider line was hit, false for fixed sliders
	 */
	public boolean hitSlider(int x, int y, int hitThreshold) {
		int r = hitThreshold
				+ Math.max(lineThickness, GeoNumeric.DEFAULT_SLIDER_THICKNESS);
		return line.intersects(x - r, y - r, 2 * r, 2 * r);
	}

	/**
	 * Returns true if the slider line was hit, false for fixed sliders
	 * @param x mouse x-coord
	 * @param y mouse y-coord
	 * @param hitThreshold threshold
	 * @return true if the slider line was hit, but not the blob
	 */
	public boolean hitSliderNotBlob(int x, int y, int hitThreshold) {
		int r = hitThreshold + Math.max(lineThickness, GeoNumeric.DEFAULT_SLIDER_THICKNESS);
		if (!line.intersects(x - r, y - r, 2 * r, 2 * r)) {
			return false;
		}
		int r2 = hitThreshold * App.DEFAULT_THRESHOLD_FACTOR_FOR_BLOB_IN_SLIDER
				+ Math.max(diameter, GeoNumeric.DEFAULT_SLIDER_BLOB_SIZE);
		return (x < coords[0] - r2 || x > coords[0] + r2)
				|| (y < coords[1] - r2 || y > coords[1] + r2);
	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 */
	@Override
	final public GRectangle getBounds() {
		if (!geo.isDefined() || ((GeoNumeric) geo).isAbsoluteScreenLocActive()
				|| !geo.isEuclidianVisible() || line == null) {
			return null;
		}
		return line.getBounds();
	}

	@Override
	final public GRectangle getBoundsForStylebarPosition() {
		if (!geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}
		return line.getBounds();
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		return circle.intersects(rect) || line.intersects(rect);
	}

	private void updatePoint(double rwX, double rwY) {

		this.coords[0] = rwX;
		this.coords[1] = rwY;

		labelVisible = geo.isLabelVisible();

		// convert to screen
		view.toScreenCoords(coords);

		double xUL = coords[0] - number.getSliderBlobSize();
		double yUL = coords[1] - number.getSliderBlobSize();

		diameter = 2 * (int) number.getSliderBlobSize() + 1;
		int HIGHLIGHT_OFFSET = (int) number.getSliderBlobSize() / 2 + 1;
		int highlightDiameter = diameter + 2 * HIGHLIGHT_OFFSET;
		// circle might be needed at least for tracing
		circle.setFrame(xUL, yUL, diameter, diameter);
		if (xUL + diameter < 0 || xUL > view.getWidth() || yUL + diameter < 0
				|| yUL > view.getHeight()) {
			labelVisible = false;
		}
		// selection area
		circleHighlight.setFrame(xUL - 2 * HIGHLIGHT_OFFSET,
				yUL - HIGHLIGHT_OFFSET * 2,
				highlightDiameter + 2 * HIGHLIGHT_OFFSET,
				highlightDiameter + 2 * HIGHLIGHT_OFFSET);

		circleOuter.setFrame(xUL - HIGHLIGHT_OFFSET, yUL - HIGHLIGHT_OFFSET,
				highlightDiameter, highlightDiameter);

		// draw trace

		if (labelVisible) {
			labelDesc = geo.getLabelDescription();
			xLabel = (int) Math.round(coords[0] + 4);
			yLabel = (int) Math.round(yUL - number.getSliderBlobSize());
			addLabelOffsetEnsureOnScreen(view.getFontPoint());
		}
	}

}

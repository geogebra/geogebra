/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * DrawSlider: draws a slider to change a number continously
 */

package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;

/**
 * 
 * @author Markus Hohenwarter
 */
public class DrawSlider extends Drawable {

	private GeoNumeric number;

	private boolean isVisible, labelVisible;

	private double[] coordsRW = new double[2];
	private double[] coordsScreen = new double[2];
	// private GeoPoint geoPoint;
	// private DrawPointSlider drawPoint;

	private int HIGHLIGHT_OFFSET;

	// used by getSelectionDiamaterMin()
	private static final int SELECTION_RADIUS_MIN = 12;

	private static final String ADJUST = "[ADJUST] ";

	// private GeoPointND P;

	private int diameter, hightlightDiameter, pointSize;

	// for dot and selection
	private GEllipse2DDouble circle = AwtFactory.prototype.newEllipse2DDouble();
	private GEllipse2DDouble circleOuter = AwtFactory.prototype
			.newEllipse2DDouble();
	private GEllipse2DDouble circleHighlight = AwtFactory.prototype
			.newEllipse2DDouble();

	private static GBasicStroke borderStroke = EuclidianStatic
			.getDefaultStroke();

	private double[] coords = new double[2];

	private GLine2D line = AwtFactory.prototype.newLine2D();

	private double initX, initY;

	private boolean adjustChecked = false;

	/**
	 * Creates new drawable for slider
	 * 
	 * @param view
	 *            view
	 * @param number
	 *            slider
	 */
	public DrawSlider(EuclidianView view, GeoNumeric number) {
		this.view = view;
		this.number = number;
		geo = number;
		initX = number.getSliderX();
		initY = number.getSliderY();

		update();
	}

	@Override
	final public void update() {
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

			// start point of horizontal line for slider
			if (number.isAbsoluteScreenLocActive()) {
				coordsScreen[0] = number.getSliderX();
				coordsScreen[1] = number.getSliderY() - 1;
				coordsRW[0] = view.toRealWorldCoordX(coordsScreen[0]);
				coordsRW[1] = view.toRealWorldCoordY(coordsScreen[1]);

				widthScreen = number.getSliderWidth();
				widthRW = horizontal ? widthScreen * view.getInvXscale()
						: widthScreen * view.getInvYscale();
			} else {
				coordsRW[0] = number.getSliderX();
				coordsRW[1] = number.getSliderY();
				coordsScreen[0] = view.toScreenCoordXd(coordsRW[0]);
				coordsScreen[1] = view.toScreenCoordYd(coordsRW[1]);

				widthRW = number.getSliderWidth();
				widthScreen = horizontal ? widthRW * view.getXscale() : widthRW
						* view.getYscale();
			}

			// point on slider that moves
			double min = number.getIntervalMin();
			double max = number.getIntervalMax();

			double param = (number.getValue() - min) / (max - min);
			setPointSize(2 + (number.getLineThickness() + 1) / 3);

			// horizontal slider
			if (horizontal) {
				updatePoint(coordsRW[0] + widthRW * param, coordsRW[1]);
				if (labelVisible) {
					this.xLabel -= 15;
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
					this.yLabel += 2 * pointSize + 4;
				}
				this.line.setLine(coordsScreen[0], coordsScreen[1],
						coordsScreen[0], coordsScreen[1] - widthScreen);
				// vertical line

			}

			updateStrokes(number, 2);
		}
		if (needsAdjusted()) {
			// Log.debug(ADJUST + " needed for " + geo.getNameDescription());
		}
	}

	@Override
	final public void draw(GGraphics2D g2) {
		if (isVisible) {
			// horizontal line
			g2.setPaint(geo.getSelColor());
			g2.setStroke(objStroke);
			g2.drawStraightLine(line.getP1().getX(), line.getP1().getY(), line
					.getP2().getX(), line.getP2().getY());
			if (geo.doHighlighting()) {
				g2.fill(circleHighlight);
				g2.setStroke(borderStroke);
				g2.draw(circleHighlight);
			} else {
				g2.fill(circleOuter);
				g2.setStroke(borderStroke);
				g2.draw(circleOuter);
			}

			// draw a dot
			g2.setPaint(geo.getObjectColor());
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
	 * 
	 * @param x
	 *            mouse x-coord
	 * @param y
	 *            mouse y-coord
	 * @param hitThreshold
	 *            threshold
	 * @return true iff the movable point was hit
	 */
	final public boolean hitPoint(int x, int y, int hitThreshold) {
		int r = hitThreshold + SELECTION_RADIUS_MIN;
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
	 * 
	 * @param x
	 *            mouse x-coord
	 * @param y
	 *            mouse y-coord
	 * @param hitThreshold
	 *            threshold
	 * @return true if the slider line was hit, false for fixed sliders
	 */
	public boolean hitSlider(int x, int y, int hitThreshold) {
		// changed: we want click on fixed slider to increment/decrement the
		// slider a bit
		// return !number.isSliderFixed() && line.intersects(x-2, y-2, 4,4);
		return line.intersects(x - hitThreshold, y - hitThreshold,
				2 * hitThreshold, 2 * hitThreshold);
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 */
	@Override
	final public GRectangle getBounds() {
		if (!geo.isDefined() || ((GeoNumeric) geo).isAbsoluteScreenLocActive()
				|| !geo.isEuclidianVisible()) {
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

		double xUL = (coords[0] - pointSize);
		double yUL = (coords[1] - pointSize);

		// Florian Sonner 2008-07-17

		// circle might be needed at least for tracing
		circle.setFrame(xUL, yUL, diameter, diameter);
		if (xUL + diameter < 0 || xUL > view.getWidth() || yUL + diameter < 0
				|| yUL > view.getHeight()) {
			labelVisible = false;
		}
		// selection area
		circleHighlight.setFrame(xUL - 2 * HIGHLIGHT_OFFSET, yUL
				- HIGHLIGHT_OFFSET * 2, hightlightDiameter + 2
				* HIGHLIGHT_OFFSET, hightlightDiameter + 2 * HIGHLIGHT_OFFSET);

		circleOuter.setFrame(xUL - HIGHLIGHT_OFFSET, yUL - HIGHLIGHT_OFFSET,
				hightlightDiameter, hightlightDiameter);

		// draw trace

		if (labelVisible) {
			labelDesc = geo.getLabelDescription();
			xLabel = (int) Math.round(coords[0] + 4);
			yLabel = (int) Math.round(yUL - pointSize);
			addLabelOffsetEnsureOnScreen(view.getFontPoint());
		}
	}

	private void setPointSize(int pointSize) {
		if (this.pointSize != pointSize) {
			diameter = 2 * pointSize;
			HIGHLIGHT_OFFSET = pointSize / 2 + 1;
			// HIGHLIGHT_OFFSET = pointSize / 2 + 1;
			hightlightDiameter = diameter + 2 * HIGHLIGHT_OFFSET;
		}
		this.pointSize = pointSize;
	}

	@Override
	public void move() {
		this.initX = -1;
		this.initY = -1;
	}

	private boolean needsAdjusted() {
		App app = view.getApplication();
		int fileWidth = app.getSettings()
				.getEuclidian(view.getEuclidianViewNo()).getFileWidth();
		int fileHeight = app.getSettings()
				.getEuclidian(view.getEuclidianViewNo()).getFileHeight();

		if (!app.has(Feature.ADJUST_SLIDERS) || fileWidth == 0
				|| fileHeight == 0) {
			return false;
		}


		if (app.getWidth() == fileWidth && app.getHeight() == fileHeight) {
			return false;
		}

		view.adjustSliderToNearestCorner(number);

		return true;
	}


}

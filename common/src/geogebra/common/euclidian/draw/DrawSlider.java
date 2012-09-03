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

package geogebra.common.euclidian.draw;

import geogebra.common.awt.GLine2D;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.plugin.EuclidianStyleConstants;

/**
 * 
 * @author Markus Hohenwarter
 */
public class DrawSlider extends Drawable {

	private GeoNumeric number;

	private boolean isVisible, labelVisible;

	private double[] coordsRW = new double[2];
	private double[] coordsScreen = new double[2];
	private GLine2D line = AwtFactory.prototype.newLine2D();
	private GeoPoint geoPoint;
	private DrawPoint drawPoint;

	/**
	 * Creates new drawable for slider
	 * 
	 * @param view view
	 * @param number slider
	 */
	public DrawSlider(EuclidianView view, GeoNumeric number) {
		this.view = view;
		hitThreshold = view.getCapturingThreshold();
		this.number = number;
		geo = number;

		// create point for slider
		geoPoint = new GeoPoint(view.getKernel().getConstruction());
		geoPoint.setPointStyle(EuclidianStyleConstants.POINT_STYLE_DOT);
		drawPoint = new DrawPoint(view, geoPoint);
		drawPoint.setGeoElement(number);

		update();
	}

	@Override
	final public void update() {
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
			geoPoint.setPointSize(2 + (number.lineThickness + 1) / 3);
			labelVisible = geo.isLabelVisible();
			geoPoint.setLabelVisible(labelVisible);

			// horizontal slider
			if (horizontal) {
				geoPoint.setCoords(coordsRW[0] + widthRW * param, coordsRW[1],
						1.0);
				drawPoint.update();
				if (labelVisible) {
					drawPoint.xLabel -= 15;
					drawPoint.yLabel -= 5;
				}

				// horizontal line
				line.setLine(coordsScreen[0], coordsScreen[1], coordsScreen[0]
						+ widthScreen, coordsScreen[1]);
			}
			// vertical slider
			else {
				geoPoint.setCoords(coordsRW[0], coordsRW[1] + widthRW * param,
						1.0);
				drawPoint.update();
				if (labelVisible) {
					drawPoint.xLabel += 5;
					drawPoint.yLabel += 2 * geoPoint.getPointSize() + 4;
				}

				// vertical line
				line.setLine(coordsScreen[0], coordsScreen[1], coordsScreen[0],
						coordsScreen[1] - widthScreen);
			}

			updateStrokes(number);
		}

		if (number == view.getEuclidianController().getRecordObject())
			recordToSpreadsheet(number);

		// G.Sturr 2010-6-28 spreadsheet trace is now handled in
		// GeoElement.update()
		// if (number.getSpreadsheetTrace())
		// recordToSpreadsheet(number);

	}

	@Override
	final public void draw(geogebra.common.awt.GGraphics2D g2) {
		if (isVisible) {
			// horizontal line
			g2.setPaint(geo.getObjectColor());
			g2.setStroke(objStroke);
			g2.draw(line);

			// point
			drawPoint.draw(g2);
		}
	}

	@Override
	final public boolean hit(int x, int y) {
		return hitPoint(x, y) || hitSlider(x, y);
	}

	@Override
	final public boolean isInside(geogebra.common.awt.GRectangle rect) {
		return drawPoint.isInside(rect);
	}

	/**
	 * Returns true iff the movable point was hit
	 * 
	 * @param x mouse x-coord
	 * @param y mouse y-coord
	 * @return true iff the movable point was hit
	 */
	final public boolean hitPoint(int x, int y) {
		return drawPoint.hit(x, y);
	}

	@Override
	public boolean hitLabel(int x, int y) {
		return drawPoint.hitLabel(x, y);
	}

	/**
	 * Returns true if the slider line was hit, false for fixed sliders
	 * 
	 * @param x mouse x-coord
	 * @param y mouse y-coord
	 * @return true if the slider line was hit, false for fixed sliders
	 */
	public boolean hitSlider(int x, int y) {
		// changed: we want click on fixed slider to move the dot to that point
		// return !number.isSliderFixed() && line.intersects(x-2, y-2, 4,4);
		return line.intersects(x - 2, y - 2, 4, 4);
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
	final public geogebra.common.awt.GRectangle getBounds() {
		if (!geo.isDefined() || ((GeoNumeric) geo).isAbsoluteScreenLocActive()
				|| !geo.isEuclidianVisible()) {
			return null;
		}
		return line.getBounds();
	}

}

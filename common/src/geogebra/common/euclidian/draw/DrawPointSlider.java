/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General private License as published by 
the Free Software Foundation.

 */

/*
 * DrawPoint.java
 *
 * Created on 11. Oktober 2001, 23:59
 */

package geogebra.common.euclidian.draw;

import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.geos.GeoElement;

/**
 * 
 * @author Markus
 * @version 2011-01-10
 */
public final class DrawPointSlider {

	private int HIGHLIGHT_OFFSET;

	// used by getSelectionDiamaterMin()
	private static final int SELECTION_RADIUS_MIN = 12;

	// private GeoPointND P;

	private int diameter, hightlightDiameter, pointSize;
	private boolean labelVisible;
	// for dot and selection
	private geogebra.common.awt.GEllipse2DDouble circle = geogebra.common.factories.AwtFactory.prototype
			.newEllipse2DDouble();
	private geogebra.common.awt.GEllipse2DDouble circleHighlight = geogebra.common.factories.AwtFactory.prototype
			.newEllipse2DDouble();

	private static geogebra.common.awt.GBasicStroke borderStroke = EuclidianStatic
			.getDefaultStroke();

	private double[] coords;

	private EuclidianView view;

	private GeoElement geo;

	/**
	 * Creates new DrawPoint
	 * 
	 * @param view
	 *            View
	 * @param geo
	 *            slider to which this belongs
	 * @param drawable
	 *            DrawSlider to which this belongs
	 */
	public DrawPointSlider(EuclidianView view, GeoElement geo, Drawable drawable) {
		this.view = view;
		this.geo = geo;
		this.drawable = drawable;
		this.coords = new double[2];
	}

	final void update(double rwX, double rwY) {

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

		// selection area
		circleHighlight.setFrame(xUL - HIGHLIGHT_OFFSET,
				yUL - HIGHLIGHT_OFFSET, hightlightDiameter, hightlightDiameter);

		// draw trace

		if (labelVisible) {
			drawable.labelDesc = geo.getLabelDescription();
			drawable.xLabel = (int) Math.round(coords[0] + 4);
			drawable.yLabel = (int) Math.round(yUL - pointSize);
			drawable.addLabelOffsetEnsureOnScreen();
		}
	}

	public void setPointSize(int pointSize) {
		if (this.pointSize != pointSize) {
			diameter = 2 * pointSize;
			HIGHLIGHT_OFFSET = pointSize / 2 + 1;
			// HIGHLIGHT_OFFSET = pointSize / 2 + 1;
			hightlightDiameter = diameter + 2 * HIGHLIGHT_OFFSET;
		}
		this.pointSize = pointSize;
	}

	public int getPointSize() {
		return this.pointSize;
	}

	private final Drawable drawable;

	final void draw(geogebra.common.awt.GGraphics2D g2) {
		if (geo.doHighlighting()) {
			g2.setPaint(geo.getSelColor());
			g2.fill(circleHighlight);
			g2.setStroke(borderStroke);
			g2.draw(circleHighlight);
		}
		// draw a dot
		g2.setPaint(geo.getObjectColor());
		g2.fill(circle);

		// black stroke
		g2.setPaint(geogebra.common.awt.GColor.black);
		g2.setStroke(borderStroke);
		g2.draw(circle);

		// label
		if (labelVisible) {
			g2.setFont(view.getFontPoint());
			g2.setPaint(geo.getLabelColor());
			drawable.drawLabel(g2);
		}
	}

	/**
	 * was this object clicked at? (mouse pointer location (x,y) in screen
	 * coords)
	 */

	final boolean hit(int x, int y, int hitThreshold) {
		int r = hitThreshold + SELECTION_RADIUS_MIN;
		double dx = coords[0] - x;
		double dy = coords[1] - y;
		return dx < r && dx > -r && dx * dx + dy * dy <= r * r;
	}

	final boolean isInside(geogebra.common.awt.GRectangle rect) {
		return rect.contains(circle.getBounds());
	}

	boolean intersectsRectangle(GRectangle rect) {
		return circle.intersects(rect);
	}

}

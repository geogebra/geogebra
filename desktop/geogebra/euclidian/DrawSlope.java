/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * DrawSlope: draws the slope triangle for the slope of a line
 */

package geogebra.euclidian;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.algos.AlgoSlope;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.kernel.Kernel;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * 
 * @author Markus Hohenwarter
 * @version
 */
public class DrawSlope extends Drawable {

	private GeoNumeric slope;
	private GeoLine g;

	private boolean isVisible, labelVisible;
	private int xLabelHor, yLabelHor;
	private String horLabel; // horizontal label, i.e. triangleSize

	private double[] coords = new double[2];
	private GeneralPathClipped gp;
	private AbstractKernel kernel;

	/**
	 * Creates new drawable for slope
	 * 
	 * @param view
	 * @param slope
	 */
	public DrawSlope(EuclidianView view, GeoNumeric slope) {
		this.view = view;
		kernel = view.getKernel();
		this.slope = slope;
		geo = slope;

		slope.setDrawable(true);

		// get parent line
		init();
		update();
	}

	private void init() {
		g = ((AlgoSlope) slope.getDrawAlgorithm()).getg();

	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		if (isVisible) {
			if (!geo.getDrawAlgorithm().equals(geo.getParentAlgorithm()))
				init();
			int slopeTriangleSize = slope.getSlopeTriangleSize();
			double rwHeight = slope.getValue() * slopeTriangleSize;
			double height = view.getYscale() * rwHeight;
			if (Math.abs(height) > Float.MAX_VALUE) {
				isVisible = false;
				return;
			}

			// get point on line g
			g.getInhomPointOnLine(coords);
			if (g.getStartPoint() == null) {
				// get point on y-axis and line g
				coords[0] = 0.0d;
				coords[1] = -g.z / g.y;
			}
			view.toScreenCoords(coords);

			// draw slope triangle
			double x = coords[0];
			double y = coords[1];
			double xright = x + view.getXscale() * slopeTriangleSize;
			if (gp == null)
				gp = new GeneralPathClipped(view);
			gp.reset();
			gp.moveTo(x, y);
			gp.lineTo(xright, y);
			gp.lineTo(xright, y - height);

			// gp on screen?
			if (!gp.intersects(0, 0, view.getWidth(), view.getHeight())) {
				isVisible = false;
				// don't return here to make sure that getBounds() works for
				// offscreen points too
			}

			// label position
			labelVisible = geo.isLabelVisible();
			if (labelVisible) {
				if (slopeTriangleSize > 1) {
					StringBuilder sb = new StringBuilder();
					switch (slope.getLabelMode()) {
					case GeoElement.LABEL_NAME_VALUE:
						sb.append(slopeTriangleSize);
						sb.append(' ');
						sb.append(geo.getLabel());
						sb.append(" = ");
						sb.append(kernel.format(rwHeight));
						break;

					case GeoElement.LABEL_VALUE:
						sb.append(kernel.format(rwHeight));
						break;

					default: // case GeoElement.LABEL_NAME:
						sb.append(slopeTriangleSize);
						sb.append(' ');
						sb.append(geo.getLabel());
						break;
					}
					labelDesc = sb.toString();
				} else {
					labelDesc = geo.getLabelDescription();
				}
				yLabel = (int) (y - height / 2.0f + 6);
				xLabel = (int) (xright) + 5;
				addLabelOffset();

				// position off horizontal label (i.e. slopeTriangleSize)
				xLabelHor = (int) ((x + xright) / 2.0);
				yLabelHor = (int) (y + view.getFontSize() + 2);
				StringBuilder sb = new StringBuilder();
				sb.append(slopeTriangleSize);
				horLabel = sb.toString();
			}
			updateStrokes(slope);
		}
	}

	@Override
	final public void draw(Graphics2D g2) {
		if (isVisible) {
			fill(g2, gp, false); // fill using default/hatching/image as
									// appropriate

			if (geo.doHighlighting()) {
				g2.setPaint(geogebra.awt.Color.getAwtColor(geo.getSelColor()));
				g2.setStroke(selStroke);
				g2.draw(gp);
			}

			if (geo.lineThickness > 0) {
				g2.setPaint(geogebra.awt.Color.getAwtColor(slope
						.getObjectColor()));
				g2.setStroke(objStroke);
				g2.draw(gp);
			}

			if (labelVisible) {
				g2.setPaint(geogebra.awt.Color.getAwtColor(slope
						.getLabelColor()));
				g2.setFont(view.fontLine);
				drawLabel(g2);
				g2.drawString(horLabel, xLabelHor, yLabelHor);
			}
		}
	}

	@Override
	final public boolean hit(int x, int y) {
		return gp != null
				&& (gp.contains(x, y) || gp.intersects(x - 3, y - 3, 6, 6));
	}

	@Override
	final public boolean isInside(Rectangle rect) {
		return false;
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
	final public Rectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}
		return gp.getBounds();
	}

}

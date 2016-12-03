/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * DrawSegment
 *
 * Created on 21. 8 . 2003
 */

package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.euclidian.clipping.ClipLine;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.MyMath;

/**
 * 
 * @author Markus Hohenwarter
 */
public class DrawRay extends Drawable implements Previewable {

	private GeoLineND ray;
	// private GeoPoint A;

	private boolean isVisible, labelVisible;
	private ArrayList<GeoPointND> points;

	private GLine2D line = AwtFactory.getPrototype().newLine2D();
	private double[] a = new double[2];
	private double[] v = new double[2];

	/**
	 * Creates new DrawRay
	 * 
	 * @param view
	 *            view
	 * @param ray
	 *            ray
	 */
	public DrawRay(EuclidianView view, GeoLineND ray) {
		this.view = view;
		this.ray = ray;
		geo = (GeoElement) ray;

		update();
	}

	/**
	 * Creates a new DrawSegment for preview.
	 * 
	 * @param view
	 *            view
	 * @param points
	 *            preview points
	 */
	public DrawRay(EuclidianView view, ArrayList<GeoPointND> points) {
		this.view = view;
		this.points = points;

		geo = view.getKernel().getConstruction().getConstructionDefaults()
				.getDefaultGeo(ConstructionDefaults.DEFAULT_RAY);

		updatePreview();
	}

	@Override
	final public void update() {
		update(true);
	}

	/**
	 * @param showLabel
	 *            true if label should be shown
	 */
	public void update(boolean showLabel) {

		isVisible = geo.isEuclidianVisible();
		if (isVisible) {
			// calc direction vector of ray in screen coords
			Coords equation = ray.getCartesianEquationVector(view.getMatrix());
			if (equation == null || !equation.isFinite()) {
				isVisible = false;
				return;
			}

			// calc start point of ray in screen coords
			Coords A = view.getCoordsForView(ray.getStartInhomCoords());

			if (tmpCoords2 == null) {
				tmpCoords2 = new Coords(2);
			}
			tmpCoords2.setX(equation.getY());
			tmpCoords2.setY(-equation.getX());
			update(A, tmpCoords2, showLabel);

		}
	}

	private Coords tmpCoords2;

	/**
	 * @param startPoint
	 *            start point
	 * @param direction
	 *            direction
	 * @param showLabel
	 *            true if label should be shown
	 */
	public void update(Coords startPoint, Coords direction, boolean showLabel) {

		labelVisible = showLabel && geo.isLabelVisible();
		updateStrokes(ray);

		// calc start point of ray in screen coords
		a[0] = startPoint.getX();
		a[1] = startPoint.getY();
		view.toScreenCoords(a);

		v[0] = direction.getX() * view.getXscale();
		v[1] = -direction.getY() * view.getYscale();

		setClippedLine();

		// line on screen?
		if (!line.intersects(0, 0, view.getWidth(), view.getHeight())) {
			isVisible = false;
			// don't return here to make sure that getBounds() works for
			// offscreen points too
		}

		// draw trace
		if (ray.getTrace()) {
			isTracing = true;
			GGraphics2D g2 = view.getBackgroundGraphics();
			if (g2 != null)
				drawTrace(g2);
		} else {
			if (isTracing) {
				isTracing = false;
				// view.updateBackground();
			}
		}

		// label position
		// use unit perpendicular vector to move away from line
		if (labelVisible) {
			labelDesc = geo.getLabelDescription();

			double nx = v[0];
			double ny = -v[1];
			double length = MyMath.length(nx, ny);
			double unit;
			if (length > 0.0) {
				unit = 16d / length;
			} else {
				nx = 0.0;
				ny = 1.0;
				unit = 16d;
			}
			xLabel = (int) (a[0] + v[0] / 2.0 + nx * unit);
			yLabel = (int) (a[1] + v[1] / 2.0 + ny * unit);
			addLabelOffset();
		}

	}

	private void setClippedLine() {
		boolean onscreenA = view.isOnScreen(a);

		// calc clip point C = a + lambda * v
		double lambda;
		if (Math.abs(v[0]) > Math.abs(v[1])) {
			if (v[0] > 0) // RIGHT
				lambda = (view.getWidth() - a[0]) / v[0];
			else
				// LEFT
				lambda = -a[0] / v[0];
		} else {
			if (v[1] > 0) // BOTTOM
				lambda = (view.getHeight() - a[1]) / v[1];
			else
				lambda = -a[1] / v[1];
		}

		if (lambda < 0) { // ray is completely out of screen
			isVisible = false;
			return;
		}

		if (onscreenA) {
			// A on screen
			line.setLine(a[0], a[1], a[0] + lambda * v[0], a[1] + lambda * v[1]);
		} else {
			// A off screen
			// clip ray at screen, that's important for huge coordinates of A
			GPoint2D[] clippedPoints = ClipLine.getClipped(
					a[0], a[1], a[0] + lambda * v[0], a[1] + lambda * v[1],
					-EuclidianStatic.CLIP_DISTANCE, view.getWidth()
							+ EuclidianStatic.CLIP_DISTANCE,
					-EuclidianStatic.CLIP_DISTANCE, view.getHeight()
							+ EuclidianStatic.CLIP_DISTANCE);
			if (clippedPoints == null) {
				isVisible = false;
			} else {
				line.setLine(clippedPoints[0].getX(), clippedPoints[0].getY(),
						clippedPoints[1].getX(), clippedPoints[1].getY());
			}
		}
	}

	@Override
	final public void draw(GGraphics2D g2) {
		if (isVisible) {
			if (geo.doHighlighting()) {
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				g2.draw(line);
			}

			g2.setPaint(getObjectColor());
			g2.setStroke(objStroke);
			g2.draw(line);

			if (labelVisible) {
				g2.setPaint(geo.getLabelColor());
				g2.setFont(view.getFontLine());
				drawLabel(g2);
			}
		}
	}

	/**
	 * @param objStroke
	 *            stroke
	 */
	final public void setStroke(GBasicStroke objStroke) {
		this.objStroke = objStroke;
	}

	@Override
	final public void drawTrace(GGraphics2D g2) {
		g2.setPaint(getObjectColor());
		g2.setStroke(objStroke);
		g2.draw(line);
	}

	final public void updatePreview() {
		isVisible = points.size() == 1;
		if (isVisible) {
			// start point
			// Coords coords = ((GeoPointND)
			// points.get(0)).getInhomCoordsInD2();
			Coords coords = view.getCoordsForView(points.get(0)
					.getInhomCoordsInD3());
			coords.get(a);
			view.toScreenCoords(a);
		}
	}

	private GPoint2D endPoint = AwtFactory.getPrototype().newPoint2D();

	final public void updateMousePos(double mouseRWx, double mouseRWy) {
		double xRW = mouseRWx;
		double yRW = mouseRWy;
		if (isVisible) {

			// need these as we don't want rounding when Alt pressed (nearest 15
			// degrees)
			double xx = view.toScreenCoordX(xRW);
			double yy = view.toScreenCoordY(yRW);

			// round angle to nearest 15 degrees if alt pressed
			if (points.size() == 1 && view.getEuclidianController().isAltDown()) {
				// double xRW = view.toRealWorldCoordX(x);
				// double yRW = view.toRealWorldCoordY(y);
				GeoPoint p = (GeoPoint) points.get(0);
				double px = p.inhomX;
				double py = p.inhomY;
				double angle = Math.atan2(yRW - py, xRW - px) * 180 / Math.PI;
				double radius = Math.sqrt((py - yRW) * (py - yRW) + (px - xRW)
						* (px - xRW));

				// round angle to nearest 15 degrees
				angle = Math.round(angle / 15) * 15;

				xRW = px + radius * Math.cos(angle * Math.PI / 180);
				yRW = py + radius * Math.sin(angle * Math.PI / 180);

				endPoint.setX(xRW);
				endPoint.setY(yRW);
				view.getEuclidianController().setLineEndPoint(endPoint);

				// don't use view.toScreenCoordX/Y() as we don't want rounding
				xx = view.getXZero() + xRW * view.getXscale();
				yy = view.getYZero() - yRW * view.getYscale();

			} else
				view.getEuclidianController().setLineEndPoint(null);

			/*
			 * a[0] = A.inhomX; a[1] = A.inhomY; view.toScreenCoords(a);
			 */
			v[0] = xx - a[0];
			v[1] = yy - a[1];
			setClippedLine();
		}
	}

	final public void drawPreview(GGraphics2D g2) {
		if (isVisible) {
			g2.setPaint(getObjectColor());
			updateStrokes(geo);
			g2.setStroke(objStroke);
			g2.draw(line);
		}
	}

	public void disposePreview() {
		// do nothing
	}

	@Override
	final public boolean hit(int x, int y, int hitThreshold) {
		return line.intersects(x - hitThreshold, y - hitThreshold,
				2 * hitThreshold, 2 * hitThreshold);
	}

	@Override
	final public boolean isInside(GRectangle rect) {
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

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		return line.intersects(rect);
	}

	/**
	 * set visible
	 */
	public void setIsVisible() {
		isVisible = true;
	}

}

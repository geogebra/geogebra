/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.euclidian.draw;

import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.GeneralPathClipped;
import geogebra.common.euclidian.Previewable;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolyLine;
import geogebra.common.kernel.kernelND.GeoPointND;

import java.util.ArrayList;

/**
 * 
 * @author Markus Hohenwarter
 */
public class DrawPolyLine extends Drawable implements Previewable {

	private GeoPolyLine poly;
	private boolean isVisible, labelVisible;

	private GeneralPathClipped gp;
	private double[] coords = new double[2];
	private ArrayList<?> points;

	/**
	 * @param view view 
	 * @param poly polyline
	 */
	public DrawPolyLine(EuclidianView view, GeoPolyLine poly) {
		this.view = view;
		this.poly = poly;
		geo = poly;

		update();
	}

	/**
	 * Creates a new DrawPolygon for preview.
	 * @param view view
	 * @param points preview points
	 */
	public DrawPolyLine(EuclidianView view, ArrayList<?> points) {
		this.view = view;
		hitThreshold = view.getCapturingThreshold();
		this.points = points;

		updatePreview();
	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		if (isVisible) {
			labelVisible = geo.isLabelVisible();
			updateStrokes(poly);

			// build general path for this polygon
			addPointsToPath(poly.getPointsND());

			// polygon on screen?
			if (!gp.intersects(0, 0, view.getWidth(), view.getHeight())) {
				isVisible = false;
				// don't return here to make sure that getBounds() works for
				// offscreen points too
			}
		

			// draw trace
			if (poly.getTrace()) {
				isTracing = true;
				geogebra.common.awt.GGraphics2D g2 = view.getBackgroundGraphics();
				if (g2 != null)
					drawTrace(g2);
			} else {
				if (isTracing) {
					isTracing = false;
					//view.updateBackground();
				}
			}
		}
	}

	@Override
	protected
	final void drawTrace(geogebra.common.awt.GGraphics2D g2) {
		if (isVisible) {
			g2.setPaint(geo.getObjectColor());
			g2.setStroke(objStroke);
			EuclidianStatic.drawWithValueStrokePure(gp, g2);
		}
	}

	private void addPointsToPath(GeoPointND[] pts) {
		if (gp == null) {
			gp = new GeneralPathClipped(view);
		} else {
			gp.reset();
		}
		
		// first point
		pts[0].getInhomCoords(coords);
		view.toScreenCoords(coords);
		gp.moveTo(coords[0], coords[1]);

		// for centroid calculation (needed for label pos)
		double xsum = coords[0];
		double ysum = coords[1];

		boolean skipNextPoint = false;

		for (int i = 1; i < pts.length; i++) {

			if (pts[i].isDefined()) {

				pts[i].getInhomCoords(coords);
				view.toScreenCoords(coords);
				if (labelVisible) {
					xsum += coords[0];
					ysum += coords[1];
				}
				if (skipNextPoint) {
					skipNextPoint = false;
					gp.moveTo(coords[0], coords[1]);

				} else {
					gp.lineTo(coords[0], coords[1]);
				}
			} else {
				// undefined point -> hole in polyline
				skipNextPoint = true;
			}
		}

		if (labelVisible) {
			labelDesc = geo.getLabelDescription();
			xLabel = (int) (xsum / pts.length);
			yLabel = (int) (ysum / pts.length);
			addLabelOffset();
		}
	}

	@Override
	final public void draw(geogebra.common.awt.GGraphics2D g2) {
		if (isVisible) {

			g2.setPaint(poly.getObjectColor());
			g2.setStroke(objStroke);
			g2.draw(gp);

			if (geo.doHighlighting()) {
				g2.setPaint(poly.getSelColor());
				g2.setStroke(selStroke);
				g2.draw(gp);
			}

			if (labelVisible) {
				g2.setPaint(poly.getLabelColor());
				g2.setFont(view.getFontPoint());
				drawLabel(g2);
			}
		}
	}

	final public void updatePreview() {
		int size = points.size();
		isVisible = size > 0;

		if (isVisible) {
			GeoPoint[] pointsArray = new GeoPoint[size];
			for (int i = 0; i < size; i++) {
				pointsArray[i] = (GeoPoint) points.get(i);
			}
			addPointsToPath(pointsArray);
		}
	}

	private geogebra.common.awt.GPoint2D endPoint = 
			geogebra.common.factories.AwtFactory.prototype.newPoint2D();

	final public void updateMousePos(double mouseRWx, double mouseRWy) {
		double xRW = mouseRWx;
		double yRW = mouseRWy;
		if (isVisible) {
			// double xRW = view.toRealWorldCoordX(mx);
			// double yRW = view.toRealWorldCoordY(my);

			int mx = view.toScreenCoordX(xRW);
			int my = view.toScreenCoordY(yRW);

			// round angle to nearest 15 degrees if alt pressed
			if (view.getEuclidianController().isAltDown()) {
				GeoPoint p = (GeoPoint) points.get(points.size() - 1);
				double px = p.inhomX;
				double py = p.inhomY;
				double angle = Math.atan2(yRW - py, xRW - px) * 180 / Math.PI;
				double radius = Math.sqrt((py - yRW) * (py - yRW) + (px - xRW)
						* (px - xRW));

				// round angle to nearest 15 degrees
				angle = Math.round(angle / 15) * 15;

				xRW = px + radius * Math.cos(angle * Math.PI / 180);
				yRW = py + radius * Math.sin(angle * Math.PI / 180);

				mx = view.toScreenCoordX(xRW);
				my = view.toScreenCoordY(yRW);

				endPoint.setX(xRW);
				endPoint.setY(yRW);
				view.getEuclidianController().setLineEndPoint(endPoint);
				gp.lineTo(mx, my);
			} else
				view.getEuclidianController().setLineEndPoint(null);
			gp.lineTo(view.toScreenCoordX(xRW), view.toScreenCoordY(yRW));
		}
	}

	final public void drawPreview(geogebra.common.awt.GGraphics2D g2) {
		if (isVisible) {

			g2.setPaint(ConstructionDefaults.colPreview);
			g2.setStroke(objStroke);
			g2.draw(gp);
		}
	}

	public void disposePreview() {
		//do nothing
	}

	@Override
	final public boolean hit(int x, int y) {
		if (isVisible) {
			if (strokedShape == null) {
				strokedShape = objStroke.createStrokedShape(gp);
			}
			return strokedShape.intersects(x - hitThreshold, y - hitThreshold,
					2 * hitThreshold, 2 * hitThreshold);
		}
		return false;
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		if (isVisible) {
			if (strokedShape == null) {
				strokedShape = objStroke.createStrokedShape(gp);
			}
			return strokedShape.intersects(rect);
		}
		return false;
	}

	@Override
	final public boolean isInside(geogebra.common.awt.GRectangle rect) {
		return gp != null && rect.contains(gp.getBounds());
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
		if (!geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}
		return gp.getBounds();
	}

}

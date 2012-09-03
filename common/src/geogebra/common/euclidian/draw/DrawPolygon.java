/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.euclidian.draw;

import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.GeneralPathClipped;
import geogebra.common.euclidian.Previewable;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.common.util.MyMath;

import java.util.ArrayList;

/**
 * 
 * @author Markus Hohenwarter
 */
public class DrawPolygon extends Drawable implements Previewable {

	private GeoPolygon poly;
	private boolean isVisible, labelVisible;

	private GeneralPathClipped gp;
	private double[] coords = new double[2];
	private ArrayList<GeoPointND> points;

	/**
	 * Creates new DrawPolygon
	 * 
	 * @param view
	 *            Euclidian view to be used
	 * @param poly
	 *            Polygon to be drawn
	 */
	public DrawPolygon(EuclidianView view, GeoPolygon poly) {
		this.view = view;
		hitThreshold = view.getCapturingThreshold();
		this.poly = poly;
		geo = poly;

		update();
	}

	/**
	 * Creates a new DrawPolygon for preview.
	 * 
	 * @param view
	 *            Euclidian view to be used
	 * @param points
	 *            vertices
	 */
	public DrawPolygon(EuclidianView view, ArrayList<GeoPointND> points) {
		this.view = view;
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
			isVisible = addPointsToPath(poly.getPointsND());
			if (!isVisible)
				return;
			gp.closePath();
			if (geo.isInverseFill()) {
				setShape(geogebra.common.factories.AwtFactory.prototype.newArea(view.getBoundingPath()));
				getShape().subtract(geogebra.common.factories.AwtFactory.prototype.newArea(gp));
			}
			// polygon on screen?
			if (!gp.intersects(0, 0, view.getWidth(), view.getHeight())
					&& !geo.isInverseFill()) {
				isVisible = false;
				// don't return here to make sure that getBounds() works for
				// offscreen points too
			}
			// draw trace
			if (poly.getTrace()) {
				isTracing = true;
				geogebra.common.awt.GGraphics2D g2 = view.getBackgroundGraphics();
				if (g2 != null)
					fill(g2, gp, false);
			} else {
				if (isTracing) {
					isTracing = false;
					//view.updateBackground();
				}
			}

		}
	}

	// return false if a point doesn't lie on the plane
	private boolean addPointsToPath(GeoPointND[] pts) {
		if (gp == null)
			gp = new GeneralPathClipped(view);
		else
			gp.reset();

		// first point
		Coords v = view.getCoordsForView(pts[0].getInhomCoordsInD(3));
		if (!Kernel.isZero(v.getZ()))
			return false;
		coords[0] = v.getX();
		coords[1] = v.getY();
		view.toScreenCoords(coords);
		gp.moveTo(coords[0], coords[1]);

		// for centroid calculation (needed for label pos)
		double xsum = coords[0];
		double ysum = coords[1];

		for (int i = 1; i < pts.length; i++) {
			v = view.getCoordsForView(pts[i].getInhomCoordsInD(3));
			if (!Kernel.isZero(v.getZ())) {
				return false;
			}
			coords[0] = v.getX();
			coords[1] = v.getY();
			view.toScreenCoords(coords);
			if (labelVisible) {
				xsum += coords[0];
				ysum += coords[1];
			}
			gp.lineTo(coords[0], coords[1]);
		}

		if (labelVisible) {
			labelDesc = geo.getLabelDescription();
			xLabel = (int) (xsum / pts.length);
			yLabel = (int) (ysum / pts.length);
			addLabelOffset();
		}

		return true;
	}

	@Override
	final public void draw(geogebra.common.awt.GGraphics2D g2) {
		if (isVisible) {
			fill(g2,  (geo.isInverseFill() ? getShape() : 
				gp), false); // fill
																	// using
																	// default/hatching/image
																	// as
			// appropriate

			if (geo.doHighlighting()) {
				g2.setPaint(poly.getSelColor());
				g2.setStroke(selStroke);
				g2.draw(gp);
			}

			// polygons (e.g. in GeoLists) that don't have labeled segments
			// should also draw their border
			else if (!poly.wasInitLabelsCalled() && poly.lineThickness > 0) {
				g2.setPaint(poly
						.getObjectColor());
				g2.setStroke(objStroke);
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
			GeoPointND[] pointsArray = new GeoPointND[size];
			for (int i = 0; i < size; i++) {
				pointsArray[i] = points.get(i);
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
			// Application.debug(xRW+" "+yRW);

			int mx = view.toScreenCoordX(xRW);
			int my = view.toScreenCoordY(yRW);

			// round angle to nearest 15 degrees if alt pressed
			if (view.getEuclidianController().isAltDown()) {

				GeoPoint p = (GeoPoint) points.get(points.size() - 1);
				double px = p.inhomX;
				double py = p.inhomY;

				if (points.size() > 1) {
					Construction cons = view.getKernel().getConstruction();
					GeoPoint intersection = new GeoPoint(cons);
					GeoLine l = new GeoLine(cons);
					GeoLine l2 = new GeoLine(cons);
					GeoPoint p2 = (GeoPoint) points.get(0);
					double px2 = p2.inhomX;
					double py2 = p2.inhomY;
					double nearestX = Double.MAX_VALUE;
					double nearestY = Double.MAX_VALUE;
					double dist = Double.MAX_VALUE;
					for (double angle = 0; angle < 180; angle += 15) {

						if (angle == 90) {
							l.setCoords(1, 0, -px);
						} else {
							double gradient = Math.tan(angle * Math.PI / 180.0);
							l.setCoords(gradient, -1.0, py - gradient * px);
						}

						for (double ang2 = 0; ang2 < 180; ang2 += 15) {
							if (angle == 90) {
								l2.setCoords(1.0, 0, -px2);
							} else {
								double gradient2 = Math.tan(ang2 * Math.PI
										/ 180.0);
								l2.setCoords(gradient2, -1.0, py2 - gradient2
										* px2);
							}

							// calculate intersection
							GeoVec3D.cross(l, l2, intersection);

							double x1 = intersection.x / intersection.z;
							double y1 = intersection.y / intersection.z;

							double d = MyMath.length(x1 - xRW, y1 - yRW);
							// Application.debug(d+" "+x1+","+y1+" "+xRW+","+yRW);
							// Application.debug(x1+","+y1);
							if (d < dist) {
								nearestX = x1;
								nearestY = y1;
								dist = d;
							}

						}
					}

					xRW = nearestX;
					yRW = nearestY;
				} else {
					double angle = Math.atan2(yRW - py, xRW - px) * 180
							/ Math.PI;
					double radius = Math.sqrt((py - yRW) * (py - yRW)
							+ (px - xRW) * (px - xRW));

					// round angle to nearest 15 degrees
					angle = Math.round(angle / 15) * 15;

					xRW = px + radius * Math.cos(angle * Math.PI / 180);
					yRW = py + radius * Math.sin(angle * Math.PI / 180);
				}

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
			g2.setPaint(ConstructionDefaults.colPreviewFill);
			g2.fill(gp);

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
		geogebra.common.awt.GShape t = geo.isInverseFill() ? getShape() : gp;
		return t != null
				&& (t.contains(x, y) || t.intersects(x - hitThreshold, y
						- hitThreshold, 2 * hitThreshold, 2 * hitThreshold));
	}

	@Override
	final public boolean isInside(geogebra.common.awt.GRectangle rect) {
		App.debug(gp.getBounds());
		App.debug(rect);
		return gp != null && gp.getBounds() != null
				&& rect.contains(gp.getBounds());
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

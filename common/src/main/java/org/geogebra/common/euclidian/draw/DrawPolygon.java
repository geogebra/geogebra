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

import org.geogebra.common.awt.GArea;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.discrete.PolygonTriangulation;
import org.geogebra.common.kernel.discrete.PolygonTriangulation.Convexity;
import org.geogebra.common.kernel.discrete.PolygonTriangulation.TriangleFan;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.util.MyMath;

/**
 * 
 * @author Markus Hohenwarter
 */
public class DrawPolygon extends Drawable implements Previewable {

	private GeoPolygon poly;
	private boolean isVisible, labelVisible;

	private GeneralPathClipped gp, gpTriangularize;
	private double[] coords = new double[2];
	private ArrayList<GeoPointND> points;
	private Coords[] extraCoords;

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
		this.poly = poly;
		geo = poly;
		extraCoords = new Coords[8];
		for (int i = 0; i < 8; i++) {
			extraCoords[i] = new Coords(0, 0);
		}
		update();

	}

	private void calculateViewCorners() {
		extraCoords[0].setX(view.getXmin());
		extraCoords[0].setY(view.getYmin());

		extraCoords[1].setX(view.getXmax());
		extraCoords[1].setY(view.getYmin());

		extraCoords[2].setX(view.getXmax());
		extraCoords[2].setY(view.getYmax());

		extraCoords[3].setX(view.getXmin());
		extraCoords[3].setY(view.getYmax());

		// // set half values for test
		// extraCoords[0].setX(0);
		// extraCoords[0].setY(0);
		//
		// extraCoords[1].setX(1);
		// extraCoords[1].setY(0);
		//
		// extraCoords[2].setX(1);
		// extraCoords[2].setY(1);
		//
		// extraCoords[3].setX(0);
		// extraCoords[3].setY(1);


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

		geo = view.getKernel().getConstruction().getConstructionDefaults()
				.getDefaultGeo(ConstructionDefaults.DEFAULT_POLYGON);

		updatePreview();
	}


	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		if (isVisible) {
			labelVisible = geo.isLabelVisible();
			updateStrokes(poly);
			
			// build general path for this polygon
			isVisible = addPointsToPath(poly.getPointsLength());
			if (!isVisible) {
				return;
			}
			gp.closePath();
			fillShape = false;

			if (!getView().getApplication().isPrerelease() ||
					isAllPointsOnScreen()) {
				if (geo.isInverseFill()) {
					createShape();
					fillShape = true;
				}
			} else {
				triangularize();
				fillShape = true;
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
				org.geogebra.common.awt.GGraphics2D g2 = view
						.getBackgroundGraphics();
				if (g2 != null) {
					fill(g2, gp, false);
				}
			} else {
				if (isTracing) {
					isTracing = false;
					// view.updateBackground();
				}
			}

		}
		
	}

	private void createShape() {
		setShape(org.geogebra.common.factories.AwtFactory.prototype
				.newArea(view.getBoundingPath()));
		getShape().subtract(
				org.geogebra.common.factories.AwtFactory.prototype.newArea(gp));

	}
	private org.geogebra.common.kernel.discrete.PolygonTriangulation pt = new PolygonTriangulation();

	private void triangularize() {

	
		pt.clear();
		pt.setPolygon(poly);

		Coords n = poly.getMainDirection();
		
		calculateCorners();

		pt.setCorners(extraCoords);

		try {
			// simplify the polygon and check if there are at least 3 points
			// left
			if (pt.updatePoints() > 2) {

				// check if the polygon is convex
				int length = poly.getPointsLength();

				Coords[] vertices = new Coords[length
						+ PolygonTriangulation.EXTRA_POINTS];
				int j = 0;
				for (int i = 0; i < poly.getPointsLength(); i++) {
					vertices[i] = poly.getPointND(i).getCoords();
					j++;
				}

				vertices[j] = poly.getPointND(0).getCoords();
				j++;

				for (int i = 0; i < PolygonTriangulation.CORNERS; i++) {
					vertices[j] = extraCoords[i];
					j++;
				}

				vertices[j] = extraCoords[0];
				j++;

				for (int i = 0; i < PolygonTriangulation.CORNERS; i++) {
					vertices[j] = extraCoords[4 + i];
					j++;
				}

				vertices[j] = extraCoords[4];
				j++;

				vertices[j] = extraCoords[0];

				Convexity convexity = pt.checkIsConvex();
				if (convexity != Convexity.NOT) {
					boolean reverse = poly.getReverseNormalForDrawing()
							^ (convexity == Convexity.CLOCKWISE);

					drawPolygonConvex(n, vertices, poly.getPointsLength(),
							reverse);
				} else {
					// set intersections (if needed) and divide the polygon into
					// non self-intersecting polygons
					pt.setIntersections();

					// convert the set of polygons to triangle fans
					pt.triangulate();

					// compute 3D coords for intersections
					Coords[] verticesWithIntersections = pt
							.getCompleteVertices(vertices,
									poly.getCoordSys(), poly.getPointsLength());

					// draw the triangle fans
					
					// needs specific path for fans
					if (gpTriangularize == null)
						gpTriangularize = new GeneralPathClipped(view);
					else
						gpTriangularize.reset();
					
					for (TriangleFan triFan : pt.getTriangleFans()) {
						// we need here verticesWithIntersections, for self-intersecting polygons
						drawTriangleFan(n, verticesWithIntersections, triFan);
					}
					
					// create the shape
					if (geo.isInverseFill()) {
						setShape(org.geogebra.common.factories.AwtFactory.prototype
								.newArea(view.getBoundingPath()));
						getShape().subtract(
								org.geogebra.common.factories.AwtFactory.prototype
										.newArea(gpTriangularize));
					}else{
						setShape(org.geogebra.common.factories.AwtFactory.prototype
								.newArea(gpTriangularize));
					}
				}

			}
		} catch (Exception e) {
			App.debug(e.getMessage());
			e.printStackTrace();
		}		
	}


	private final void calculateCorners() {
		calculateViewCorners();
		calculateBounds();
	}

	private final void calculateBounds() {
		double xmin = Double.MAX_VALUE;
		double ymin = Double.MAX_VALUE;
		double xmax = Double.MIN_VALUE;
		double ymax = Double.MIN_VALUE;

		for (int i = 0; i < poly.getPointsLength(); i++) {
			double x = poly.getPointX(i);
			double y = poly.getPointY(i);

			if (x < xmin) {
				xmin = x;
			}

			if (x > xmax) {
				xmax = x;
			}

			if (y < ymin) {
				ymin = y;
			}
			if (y > ymax) {
				ymax = y;
			}

		}

		xmin = xmin < 0 ? xmin : view.getXmin();
		xmax = xmax > view.getXmax() ? xmax : view.getXmax();

		ymin = ymin < 0 ? ymin : view.getYmin();
		ymax = ymax > view.getYmax() ? ymax : view.getYmax();

		extraCoords[4].setX(xmin);
		extraCoords[4].setY(ymin);

		extraCoords[5].setX(xmax);
		extraCoords[5].setY(ymin);

		extraCoords[6].setX(xmax);
		extraCoords[6].setY(ymax);

		extraCoords[7].setX(xmin);
		extraCoords[7].setY(ymax);
	}

	private void drawPolygonConvex(Coords n, Coords[] vertices, int length,
			boolean reverse) {
		App.debug("[POLY] drawPolygonConvex");
		Coords coordsApex = vertices[0];
		coords[0] = coordsApex.getX();
		coords[1] = coordsApex.getY();
		view.toScreenCoords(coords);
		double startX = coords[0];
		double startY = coords[1];
		gpTriangularize.moveTo(coords[0], coords[1]);
		for (int i = length - 1; i < 0; i--) {
			Coords coord = vertices[i];
			coords[0] = coord.getX();
			coords[1] = coord.getY();
			view.toScreenCoords(coords);
			gpTriangularize.lineTo(coords[0], coords[1]);
		}

		// we have to move back manually to apex since we may have new fan to
		// draw
		gpTriangularize.moveTo(startX, startY);

	}

	private void drawTriangleFan(Coords n, Coords[] v, TriangleFan triFan) {
		App.debug("[POLY] drawTriangleFan");
		
		
		Coords coordsApex = v[triFan.getApexPoint()];
		coords[0] = coordsApex.getX();
		coords[1] = coordsApex.getY();
		view.toScreenCoords(coords);
		double startX = coords[0];
		double startY = coords[1];
		gpTriangularize.moveTo(coords[0], coords[1]);
		for (int i=0; i < triFan.size(); i++) {
			Coords coord = v[triFan.getVertexIndex(i)];		
			coords[0] = coord.getX();
			coords[1] = coord.getY();
			view.toScreenCoords(coords);
			gpTriangularize.lineTo(coords[0], coords[1]);
		}
		
		// we have to move back manually to apex since we may have new fan to draw
		gpTriangularize.moveTo(startX, startY);
		
		
		
		
	}

	private Coords getCoords(int i) {
		if (poly != null) {
			return view.getCoordsForView(poly.getPoint3D(i));
		}

		return view.getCoordsForView(points.get(i).getInhomCoordsInD3());
	}

	// return false if a point doesn't lie on the plane
	private boolean addPointsToPath(int length) {
		if (gp == null)
			gp = new GeneralPathClipped(view);
		else
			gp.reset();

		if (length <= 0)
			return false;

		// first point
		Coords v = getCoords(0);
		if (!Kernel.isZero(v.getZ()))
			return false;
		coords[0] = v.getX();
		coords[1] = v.getY();
		view.toScreenCoords(coords);
		gp.moveTo(coords[0], coords[1]);

		// for centroid calculation (needed for label pos)
		double xsum = coords[0];
		double ysum = coords[1];

		for (int i = 1; i < length; i++) {
			v = getCoords(i);
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
			xLabel = (int) (xsum / length);
			yLabel = (int) (ysum / length);
			addLabelOffset();
		}

		return true;
	}

	private boolean fillShape = false;

	@Override
	final public void draw(org.geogebra.common.awt.GGraphics2D g2) {

		if (isVisible) {
			fill(g2, (fillShape ? getShape() : gp), false); // fill
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
				g2.setPaint(getObjectColor());
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
			addPointsToPath(size);
		}
	}

	private org.geogebra.common.awt.GPoint2D endPoint = org.geogebra.common.factories.AwtFactory.prototype
			.newPoint2D();

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
							if (ang2 == angle) {
								continue;
							} else if (ang2 == 90) {
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
							// App.debug("angle = "+angle+"\nang2 = "+ang2+"\n("+x1+","+y1+")");//
							// "+xRW+","+yRW);
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

	final public void drawPreview(org.geogebra.common.awt.GGraphics2D g2) {
		if (isVisible) {

			fill(g2, (geo.isInverseFill() ? getShape() : gp), false);

			g2.setPaint(getObjectColor());
			updateStrokes(geo);
			g2.setStroke(objStroke);
			g2.draw(gp);

		}
	}

	public void disposePreview() {
		// do nothing
	}

	@Override
	final public boolean hit(int x, int y, int hitThreshold) {
		org.geogebra.common.awt.GShape t = geo.isInverseFill() ? getShape()
				: gp;
		return t != null
				&& (t.contains(x, y) || t.intersects(x - hitThreshold, y
						- hitThreshold, 2 * hitThreshold, 2 * hitThreshold));
	}

	@Override
	final public boolean isInside(org.geogebra.common.awt.GRectangle rect) {
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
	final public org.geogebra.common.awt.GRectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}
		return gp.getBounds();
	}

	@Override
	public GArea getShape() {
		if (geo.isInverseFill() || super.getShape() != null)
			return super.getShape();
		setShape(AwtFactory.prototype.newArea(gp));
		return super.getShape();
	}

	private boolean isAllPointsOnScreen() {
		if (poly.getPoints() == null) {
			return false;
		}
		for (GeoPointND p : poly.getPoints()) {
			double x = view.toScreenCoordXd(p.getInhomX());
			double y = view.toScreenCoordYd(p.getInhomY());
			if (x < 0 || x > view.getWidth() || y < 0 || y > view.getHeight()) {
				return false;
			}
		}
		return true;
	}
}

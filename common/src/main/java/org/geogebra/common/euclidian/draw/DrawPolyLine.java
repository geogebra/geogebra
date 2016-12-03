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

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * 
 * @author Markus Hohenwarter
 */
public class DrawPolyLine extends Drawable implements Previewable {

	private GeoPolyLine poly;
	private boolean isVisible, labelVisible;

	private GeneralPathClipped gp;
	private double[] coords = new double[2];
	private ArrayList<? extends GeoPointND> points;
	// list of single points created by pen
	private ArrayList<GeoPointND> pointList = new ArrayList<GeoPointND>();
	private boolean startPointAdded = false;

	/**
	 * @param view
	 *            view
	 * @param poly
	 *            polyline
	 */
	public DrawPolyLine(EuclidianView view, GeoPolyLine poly) {
		this.view = view;
		this.poly = poly;
		geo = poly;

		update();
	}

	/**
	 * Creates a new DrawPolygon for preview.
	 * 
	 * @param view
	 *            view
	 * @param points
	 *            preview points
	 */
	public DrawPolyLine(EuclidianView view,
			ArrayList<? extends GeoPointND> points) {
		this.view = view;
		this.points = points;

		geo = view.getKernel().getConstruction().getConstructionDefaults()
				.getDefaultGeo(ConstructionDefaults.DEFAULT_POLYLINE);

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
				GGraphics2D g2 = view
						.getBackgroundGraphics();
				if (g2 != null)
					drawTrace(g2);
			} else {
				if (isTracing) {
					isTracing = false;
					// view.updateBackground();
				}
			}
		}
	}

	@Override
	protected final void drawTrace(GGraphics2D g2) {
		if (isVisible) {
			g2.setPaint(getObjectColor());
			g2.setStroke(objStroke);
			g2.draw(gp);
		}
	}

	private void addPointsToPath(GeoPointND[] pts) {
		if (gp == null) {
			gp = new GeneralPathClipped(view);
		} else {
			gp.reset();
		}

		pointList.clear();

		// for centroid calculation (needed for label pos)
		double xsum = 0;
		double ysum = 0;

		boolean skipNextPoint = true;
		Coords v;
		for (int i = 0; i < pts.length; i++) {

			v = getCoords(i);
			if (pts[i].isDefined() && Kernel.isZero(v.getZ())) {
				coords[0] = v.getX();
				coords[1] = v.getY();
				view.toScreenCoords(coords);
				if (labelVisible) {
					xsum += coords[0];
					ysum += coords[1];
				}
				if (skipNextPoint) {
					skipNextPoint = false;
					// collect start points
					// one point
					if (pts.length == 1 ||
					// last point
							(i - 1 >= 0 && i + 1 == pts.length
									&& !pts[i - 1].isDefined())
							// between undef points
							|| (i - 1 >= 0 && i + 1 < pts.length
									&& !pts[i - 1].isDefined()
									&& !pts[i + 1]
											.isDefined())
							// first point
							|| (i == 0 && i + 1 < pts.length
									&& !pts[i + 1].isDefined())) {
						// do not collect points remained after erasing
						if ((i - 2 >= 0 && pts[i - 2].isDefined())
								|| (i + 2 < pts.length
										&& pts[i + 2].isDefined())
								|| i == 0 || i == pts.length - 1) {
							if (!pointList.contains(pts[i])) {
								pointList.add(pts[i]);
								startPointAdded = true;
							}
						}
					}
					gp.moveTo(coords[0], coords[1]);
				} else {
					gp.lineTo(coords[0], coords[1]);
					// if point was added as start of segment
					// then remove it
					if (!pointList.isEmpty()
							&& startPointAdded && view.getEuclidianController()
									.getMode() != EuclidianConstants.MODE_ERASER) {
						pointList.remove(pointList.size() - 1);
						startPointAdded = false;
					}
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
	final public void draw(GGraphics2D g2) {
		// draw single points
		for (int i = 0; i < pointList.size(); i++) {
			Coords v = pointList.get(i).getInhomCoordsInD3();
			coords[0] = v.getX();
			coords[1] = v.getY();
			view.toScreenCoords(coords);
			GPoint p = new GPoint(Math.round((float) coords[0]),
					Math.round((float) coords[1]));
			if (poly.getLineType() == EuclidianStyleConstants.LINE_TYPE_FULL) {
				drawEllipse(g2, p);
			} else {
				drawRectangle(g2, p);
			}
		}
		if (isVisible) {

			g2.setPaint(getObjectColor());
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

	// method to draw ellipse for point created by pen tool
	// for full line type
	private void drawEllipse(GGraphics2D g2D, GPoint point) {
		GEllipse2DDouble ellipse = AwtFactory.getPrototype()
				.newEllipse2DDouble();
		ellipse.setFrameFromCenter(point.getX(), point.getY(),
				point.getX() + getLineThicknessForPoint(),
				point.getY() + getLineThicknessForPoint());
		float[] rgb = new float[3];
		getObjectColor().getRGBColorComponents(rgb);
		GColor lineDrawingColor = GColor.newColor(rgb[0],
				rgb[1],
				rgb[2], poly.getLineOpacity() / 255.0f);
		g2D.setPaint(lineDrawingColor);
		g2D.fill(ellipse);
		g2D.setStroke(EuclidianStatic.getDefaultStroke());
		g2D.draw(AwtFactory.getPrototype().newArea(ellipse));
	}

	// method to draw rectangle for point created by pen tool
	// for other line types
	private void drawRectangle(GGraphics2D g2D, GPoint point) {
		GRectangle rectangle = AwtFactory.getPrototype().newRectangle();
		rectangle.setRect(point.getX(), point.getY(),
				getLineThicknessForPoint() * 1.5,
				getLineThicknessForPoint() * 1.5);
		float[] rgb = new float[3];
		getObjectColor().getRGBColorComponents(rgb);
		GColor lineDrawingColor = GColor.newColor(rgb[0],
				rgb[1], rgb[2], poly.getLineOpacity() / 255.0f);
		g2D.setPaint(lineDrawingColor);
		g2D.fill(rectangle);
		g2D.setStroke(EuclidianStatic.getDefaultStroke());
		g2D.draw(AwtFactory.getPrototype().newArea(rectangle));
	}

	private float getLineThicknessForPoint() {
		return poly.getLineThickness() / 4.0f;
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

	private GPoint2D endPoint = AwtFactory.getPrototype().newPoint2D();

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

	final public void drawPreview(GGraphics2D g2) {
		if (isVisible) {
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

		GPathIterator it = gp.getGeneralPath().getPathIterator(null);
		it.currentSegment(coords);
		it.next();
		if (it.isDone()) {
			if (pointList != null) {
				for (int i = 0; i < pointList.size(); i++) {
					Coords v = pointList.get(i).getInhomCoordsInD3();
					coords[0] = v.getX();
					coords[1] = v.getY();
					view.toScreenCoords(coords);
					GPoint2D p = AwtFactory.getPrototype().newPoint2D();
					p.setLocation(coords[0], coords[1]);
					if (rect.contains(p)) {
						return true;
					}
				}
				return false;
			}
		}

		if (isVisible) {
			if (strokedShape == null) {
				strokedShape = objStroke.createStrokedShape(gp);
			}
			boolean intersects = strokedShape.intersects(rect);
			if (!intersects && pointList != null) {
				for (int i = 0; i < pointList.size(); i++) {
					Coords v = pointList.get(i).getInhomCoordsInD3();
					coords[0] = v.getX();
					coords[1] = v.getY();
					view.toScreenCoords(coords);
					GPoint2D p = AwtFactory.getPrototype().newPoint2D();
					p.setLocation(coords[0], coords[1]);
					if (rect.contains(p)) {
						return true;
					}
				}
			}
			return intersects;
		}

		return false;
	}

	@Override
	final public boolean isInside(GRectangle rect) {
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
	final public GRectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}
		return gp.getBounds();
	}

	private Coords getCoords(int i) {
		if (poly != null) {
			return view.getCoordsForView(poly.getPointND(i)
					.getInhomCoordsInD3());
		}

		return view.getCoordsForView(points.get(i).getInhomCoordsInD3());
	}

}

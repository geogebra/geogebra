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
import java.util.List;

import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianBoundingBoxHandler;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElement.HitType;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;

/**
 * 
 * @author Markus Hohenwarter
 */
public class DrawPolygon extends Drawable implements Previewable {
	private GeoPolygon poly;
	private boolean isVisible;
	private boolean labelVisible;

	private GeneralPathClipped gp;
	private double[] coords = new double[2];
	private ArrayList<GeoPointND> points;

	private BoundingBox boundingBox;
	private boolean isSquare = false;
	private GGeneralPath prewPolygon = AwtFactory.getPrototype()
			.newGeneralPath();
	private boolean fillShape = false;

	private GPoint2D endPoint = AwtFactory.getPrototype().newPoint2D();

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

			if (geo.isInverseFill()) {
				createShape();
				fillShape = true;
			}

			// polygon on screen?
			if (!view.intersects(gp) && !geo.isInverseFill()) {
				isVisible = false;
				// don't return here to make sure that getBounds() works for
				// offscreen points too
			}
			// draw trace
			if (poly.getTrace()) {
				isTracing = true;
				GGraphics2D g2 = view.getBackgroundGraphics();
				if (g2 != null) {
					fill(g2, gp);
				}
			} else {
				if (isTracing) {
					isTracing = false;
					// view.updateBackground();
				}
			}

		}
		if (geo.isShape() && view
				.getHitHandler() != EuclidianBoundingBoxHandler.ROTATION) {
			if (getBounds() != null) {
				getBoundingBox().setRectangle(getBounds());
				if (DoubleUtil.isEqual(getBoundingBox().getRectangle().getHeight(),
						getBoundingBox().getRectangle().getWidth(), 2)) {
					setIsSquare(true);
				}
			}
		}
	}

	private void createShape() {
		setShape(AwtFactory.getPrototype().newArea(view.getBoundingPath()));
		getShape().subtract(AwtFactory.getPrototype().newArea(gp));
	}

	private Coords getCoords(int i) {
		if (poly != null) {
			return view.getCoordsForView(poly.getPoint3D(i));
		}

		return view.getCoordsForView(points.get(i).getInhomCoordsInD3());
	}

	// return false if a point doesn't lie on the plane
	private boolean addPointsToPath(int length) {
		if (gp == null) {
			gp = new GeneralPathClipped(view);
		} else {
			gp.reset();
		}

		if (length <= 0) {
			return false;
		}

		// first point
		Coords v = getCoords(0);
		if (!DoubleUtil.isZero(v.getZ())) {
			return false;
		}
		coords[0] = v.getX();
		coords[1] = v.getY();
		view.toScreenCoords(coords);
		gp.moveTo(coords[0], coords[1]);

		// for centroid calculation (needed for label pos)
		double xsum = coords[0];
		double ysum = coords[1];

		for (int i = 1; i < length; i++) {
			v = getCoords(i);
			if (!DoubleUtil.isZero(v.getZ())) {
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

	@Override
	final public void draw(GGraphics2D g2) {
		if (isVisible) {
			// fill using default/hatching/image as appropriate
			fill(g2, (fillShape ? getShape() : gp));
			if (isHighlighted()) {
				g2.setPaint(poly.getSelColor());
				g2.setStroke(selStroke);
				g2.draw(gp);
			}

			// polygons (e.g. in GeoLists) that don't have labeled segments
			// should also draw their border
			else if (!poly.wasInitLabelsCalled()
					&& poly.getLineThickness() > 0) {
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

	@Override
	final public void updatePreview() {
		int size = points.size();
		isVisible = size > 0;

		if (isVisible) {
			addPointsToPath(size);
		}
	}

	@Override
	final public void updateMousePos(double mouseRWx, double mouseRWy) {
		double xRW = mouseRWx;
		double yRW = mouseRWy;
		if (isVisible) {
			int mx;
			int my;

			// round angle to nearest 15 degrees if alt pressed
			if (view.getEuclidianController().isAltDown()) {

				GeoPointND p = points.get(points.size() - 1);
				double px = p.getInhomX();
				double py = p.getInhomY();

				if (points.size() > 1) {
					Construction cons = view.getKernel().getConstruction();
					GeoPoint intersection = new GeoPoint(cons);
					GeoLine l = new GeoLine(cons);
					GeoLine l2 = new GeoLine(cons);
					GeoPointND p2 = points.get(0);
					double px2 = p2.getInhomX();
					double py2 = p2.getInhomY();
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
							if (DoubleUtil.isEqual(ang2, angle)) {
								continue;
							} else if (DoubleUtil.isEqual(ang2, 90)) {
								l2.setCoords(1.0, 0, -px2);
							} else {
								double gradient2 = Math
										.tan(ang2 * Math.PI / 180.0);
								l2.setCoords(gradient2, -1.0,
										py2 - gradient2 * px2);
							}

							// calculate intersection
							GeoVec3D.cross(l, l2, intersection);

							double x1 = intersection.x / intersection.z;
							double y1 = intersection.y / intersection.z;

							double d = MyMath.length(x1 - xRW, y1 - yRW);
							// Log.debug("angle = "+angle+"\nang2 =
							// "+ang2+"\n("+x1+","+y1+")");//
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
					double radius = Math.sqrt(
							(py - yRW) * (py - yRW) + (px - xRW) * (px - xRW));

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
			} else {
				view.getEuclidianController().setLineEndPoint(null);
			}
			gp.lineTo(view.toScreenCoordX(xRW), view.toScreenCoordY(yRW));
		}
	}

	@Override
	final public void drawPreview(GGraphics2D g2) {
		if (isVisible) {
			fill(g2, (geo.isInverseFill() ? getShape() : gp));

			g2.setPaint(getObjectColor());
			updateStrokes(geo);
			g2.setStroke(objStroke);
			g2.draw(gp);

		}
	}

	@Override
	public void disposePreview() {
		// do nothing
	}

	/**
	 * 
	 * @return true if it has to check it's on filling
	 */
	protected boolean checkIsOnFilling() {
		return geo.isFilled();
	}

	@Override
	final public boolean hit(int x, int y, int hitThreshold) {
		GShape t = geo.isInverseFill() ? getShape() : gp;
		
		// needed for MOW-114
		GeoSegmentND[] segmentsOfPoly = poly.getSegments();
		boolean wasSegmentHit = false;

		if (segmentsOfPoly != null) {
			// check if one of sides was hit
			for (GeoSegmentND geoSegmentND : segmentsOfPoly) {
				DrawableND d = view.getDrawableFor(geoSegmentND);
				if (d instanceof DrawSegment
						&& ((DrawSegment) d).hit(x, y, hitThreshold)) {
					wasSegmentHit = true;
					break;
				}
			}
		}
		// no filling
		if (!checkIsOnFilling()) {
			// draggable only from sides of poly
			// or from sides of boundingBox
			if (wasSegmentHit) {
				poly.setLastHitType(HitType.ON_BOUNDARY);
				return true;
			} 
			poly.setLastHitType(HitType.NONE);
			return false;
		}

		// also check for boundingBox is has filling
		return (t != null
				&& (t.contains(x, y) || t.intersects(x - hitThreshold,
						y - hitThreshold, 2 * hitThreshold, 2 * hitThreshold)));
	}

	@Override
	final public boolean isInside(GRectangle rect) {
		return gp != null && gp.getBounds() != null
				&& rect.contains(gp.getBounds());
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 */
	@Override
	final public GRectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible() || gp == null) {
			return null;
		}
		return gp.getBounds();
	}

	@Override
	public GArea getShape() {
		if (geo.isInverseFill() || super.getShape() != null) {
			return super.getShape();
		}
		setShape(AwtFactory.getPrototype().newArea(gp));
		return super.getShape();
	}

	@Override
	public BoundingBox getBoundingBox() {
		if (boundingBox == null) {
			boundingBox = createBoundingBox(false, true);
		}
		boundingBox.updateFrom(geo);
		return boundingBox;
	}

	/**
	 * @return true if is square
	 */
	public boolean isSquare() {
		return isSquare;
	}

	/**
	 * @param isSquare
	 *            - if it is square
	 */
	public void setIsSquare(boolean isSquare) {
		this.isSquare = isSquare;
	}

	/**
	 * method to update points of poly after mouse release
	 * 
	 */
	@Override
	public void updateGeo() {
		if (prewPolygon != null) {
			updateRealPointsOfPolygon();
			prewPolygon = null;
		}
		poly.updateCascade(true);
		poly.getParentAlgorithm().update();
		for (GeoSegmentND geoSeg : poly.getSegments()) {
			geoSeg.getParentAlgorithm().update();
		}
		for (GeoPointND geoPoint : poly.getPoints()) {
			geoPoint.update();
		}
		poly.setEuclidianVisible(true);
		poly.updateRepaint();
		this.update();
		view.setShapePolygon(null);
		view.setShapeRectangle(null);
		view.repaintView();
	}

	@Override
	public void fromPoints(ArrayList<GPoint2D> pts) {
		if (prewPolygon == null) {
			prewPolygon = AwtFactory.getPrototype().newGeneralPath();
		}

		// init poly
		prewPolygon.reset();
		// move to start point
		prewPolygon.moveTo(pts.get(0).getX(), pts.get(0).getY());
		// draw segments
		for (int i = 1; i < pts.size(); i++) {
			prewPolygon.lineTo(pts.get(i).getX(), pts.get(i).getY());
		}
		prewPolygon.closePath();
		updateGeo();
	}

	private void updateRealPointsOfPolygon() {
		double[] coordArr = new double[6];
		GPathIterator it = prewPolygon.getPathIterator(null);
		int i = poly.getPoints().length;
		while (!it.isDone() && i > 0) {
			i--;
			it.currentSegment(coordArr);
			poly.getPoint(i).setCoords(view.toRealWorldCoordX(coordArr[0]),
					view.toRealWorldCoordY(coordArr[1]), 1);
			it.next();
		}
	}

	@Override
	protected boolean hasRotationHandler() {
		return true;
	}

	@Override
	protected List<GPoint2D> toPoints() {
		List<GPoint2D> ret = new ArrayList<>(this.poly.getNumPoints());
		for (GeoPointND pt : this.poly.getPoints()) {
			pt.updateCoords2D();
			MyPoint screenPt = new MyPoint(view.toScreenCoordX(pt.getX2D()),
					view.toScreenCoordY(pt.getY2D()));
			ret.add(screenPt);
		}
		return ret;
	}
}

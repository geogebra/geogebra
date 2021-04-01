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

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GArc2D;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.euclidian.clipping.ClipShape;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoConicPartCircle;
import org.geogebra.common.kernel.algos.AlgoConicPartCircumcircle;
import org.geogebra.common.kernel.algos.AlgoSemicircle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElement.HitType;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

/**
 * 
 * @author Markus Hohenwarter
 */
public class DrawConicPart extends Drawable implements Previewable {

	private GeoConicPartND conicPart;

	private boolean isVisible;
	private boolean labelVisible;

	private GArc2D arc = AwtFactory.getPrototype().newArc2D();
	private GShape shape;
	// private GeoVec2D transVec;
	private double[] halfAxes;
	// private GeoVec2D center;
	private int closure;

	private static final int DRAW_TYPE_ELLIPSE = 1;
	private static final int DRAW_TYPE_SEGMENT = 2;
	private static final int DRAW_TYPE_RAYS = 3;
	private int draw_type;
	private GAffineTransform transform = AwtFactory.getPrototype()
			.newAffineTransform();

	// these are needed for degenerate arcs
	private DrawRay drawRay1;
	private DrawRay drawRay2;
	private DrawSegment drawSegment;
	// private Drawable degDrawable;

	private double[] coords = new double[2];

	// preview
	private ArrayList<GeoPointND> prevPoints;
	private GeoPoint[] previewTempPoints;
	private int previewMode;
	private int neededPrevPoints;

	private boolean isPreview = false;
	private Coords[] ev;

	/**
	 * @param view
	 *            view
	 * @param conicPart
	 *            conic part
	 */
	public DrawConicPart(EuclidianView view, GeoConicPartND conicPart) {
		this.view = view;
		isPreview = false;
		initConicPart(conicPart);
		update();
	}

	private void initConicPart(GeoConicPartND initConicPart) {
		this.conicPart = initConicPart;
		geo = (GeoElement) initConicPart;

		// center = conicPart.getTranslationVector();
		halfAxes = ((GeoConicND) initConicPart).getHalfAxes();
		// arc or sector?
		closure = initConicPart
				.getConicPartType() == GeoConicNDConstants.CONIC_PART_SECTOR
						? GArc2D.PIE : GArc2D.OPEN;
	}

	/**
	 * Creates a new DrawConicPart for preview.
	 * 
	 * @param view
	 *            view
	 * @param mode
	 *            preview mode
	 * @param points
	 *            points
	 */
	public DrawConicPart(EuclidianView view, int mode,
			ArrayList<GeoPointND> points) {
		this.view = view;
		prevPoints = points;
		previewMode = mode;
		isPreview = true;

		Construction cons = view.getKernel().getConstruction();
		neededPrevPoints = mode == EuclidianConstants.MODE_SEMICIRCLE ? 1 : 2;
		previewTempPoints = new GeoPoint[neededPrevPoints + 1];
		for (int i = 0; i < previewTempPoints.length; i++) {
			previewTempPoints[i] = new GeoPoint(cons);
		}

		initPreview();
	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible() && geo.isDefined();
		if (isVisible) {
			labelVisible = geo.isLabelVisible();
			updateStrokes((GeoConicND) conicPart);

			switch (((GeoConicND) conicPart).getType()) {
			case GeoConicNDConstants.CONIC_CIRCLE:
			case GeoConicNDConstants.CONIC_ELLIPSE:
				updateEllipse();
				break;
			case GeoConicNDConstants.CONIC_LINE:
			case GeoConicNDConstants.CONIC_PARALLEL_LINES:
				updateParallelLines();
				break;

			case GeoConicNDConstants.CONIC_SINGLE_POINT:
				isVisible = false;
				break;

			default:
				// Application.debug("DrawConicPart: unsupported conic type: " +
				// conicPart.getType());
				isVisible = false;
				return;
			}

			// shape on screen?
			if (shape != null && !view.intersects(shape)) {
				isVisible = false;
				// don't return here to make sure that getBounds() works for
				// offscreen points too
			}

			// draw trace
			if (((Traceable) conicPart).getTrace()) {
				isTracing = true;
				GGraphics2D g2 = view.getBackgroundGraphics();
				if (g2 != null) {
					drawTrace(g2);
				}
			} else {
				if (isTracing) {
					isTracing = false;
					// view.updateBackground();
				}
			}
		}
	}

	private void updateEllipse() {
		draw_type = DRAW_TYPE_ELLIPSE;

		// check for huge pixel radius
		double xradius = halfAxes[0] * view.getXscale();
		double yradius = halfAxes[1] * view.getYscale();
		if (xradius > DrawConic.HUGE_RADIUS
				|| yradius > DrawConic.HUGE_RADIUS) {
			isVisible = false;
			return;
		}

		// check if in view
		Coords M;
		if (isPreview) { // coords have been calculated in view
			M = ((GeoConicND) conicPart).getMidpoint3D().getInhomCoords();
		} else {
			M = view.getCoordsForView(((GeoConicND) conicPart).getMidpoint3D());
			if (!DoubleUtil.isZero(M.getZ())) { // check if in view
				isVisible = false;
				return;
			}
		}

		if (ev == null) {
			ev = new Coords[2];
		}
		for (int j = 0; j < 2; j++) {
			if (isPreview) { // coords have been calculated in view
				ev[j] = ((GeoConicND) conicPart).getEigenvec3D(j);
			} else {
				ev[j] = view.getCoordsForView(
						((GeoConicND) conicPart).getEigenvec3D(j));
				if (!DoubleUtil.isZero(ev[j].getZ())) { // check if in view
					isVisible = false;
					return;
				}
			}
		}

		// set arc
		arc.setArc(-halfAxes[0], -halfAxes[1], 2 * halfAxes[0], 2 * halfAxes[1],
				-Math.toDegrees(conicPart.getParameterStart()),
				-Math.toDegrees(conicPart.getParameterExtent()), closure);

		// transform to screen coords
		transform.setTransform(view.getCoordTransform());
		transform.concatenate(view.getCompanion()
				.getTransform((GeoConicND) conicPart, M, ev));

		// BIG RADIUS: larger than screen diagonal
		int BIG_RADIUS = view.getWidth() + view.getHeight(); // > view's
																// diagonal
		if (xradius < BIG_RADIUS && yradius < BIG_RADIUS) {
			shape = transform.createTransformedShape(arc);
		} else {
			// clip big arc at screen
			shape = ClipShape.clipToRect(arc, transform, -1, -1,
					view.getWidth() + 2, view.getHeight() + 2);
		}

		// label position
		if (labelVisible) {
			double midAngle = conicPart.getParameterStart()
					+ conicPart.getParameterExtent() / 2.0;
			coords[0] = halfAxes[0] * Math.cos(midAngle);
			coords[1] = halfAxes[1] * Math.sin(midAngle);
			transform.transform(coords, 0, coords, 0, 1);

			labelDesc = geo.getLabelDescription();

			xLabel = (int) (coords[0]) + 6;
			yLabel = (int) (coords[1]) - 6;
			addLabelOffset();
		}
	}

	private void updateParallelLines() {

		if (drawSegment == null
				// also needs re-initing when changing Rays <-> Segment
				|| (conicPart.positiveOrientation()
						&& draw_type != DRAW_TYPE_SEGMENT)
				|| (!conicPart.positiveOrientation()
						&& draw_type != DRAW_TYPE_RAYS)) { // init
			GeoLine[] lines = ((GeoConicND) conicPart).getLines();
			drawSegment = new DrawSegment(view, lines[0]);
			drawRay1 = new DrawRay(view, lines[0]);
			drawRay2 = new DrawRay(view, lines[1]);
			drawSegment.setGeoElement((GeoElement) conicPart);
			drawRay1.setGeoElement((GeoElement) conicPart);
			drawRay2.setGeoElement((GeoElement) conicPart);
		}
		Coords s = view.getCoordsForView(conicPart.getOrigin3D(0));
		if (!DoubleUtil.isZero(s.getZ())) {
			isVisible = false;
			return;
		}
		Coords e = view.getCoordsForView(conicPart.getSegmentEnd3D());
		if (!DoubleUtil.isZero(e.getZ())) {
			isVisible = false;
			return;
		}
		if (conicPart.positiveOrientation()) {
			draw_type = DRAW_TYPE_SEGMENT;

			drawSegment.setIsVisible();
			drawSegment.update(s, e);
		} else {
			draw_type = DRAW_TYPE_RAYS;

			Coords d = e.sub(s);
			drawRay1.setIsVisible();
			drawRay1.update(s, d.mul(-1), false); // don't show labels
			drawRay2.setIsVisible();
			drawRay2.update(conicPart.getOrigin3D(1), d, false);
		}
	}

	@Override
	final public void draw(GGraphics2D g2) {
		if (isVisible) {
			switch (draw_type) {
			default:
				// do nothing
				break;
			case DRAW_TYPE_ELLIPSE:
				fill(g2, shape); // fill using default/hatching/image as
									// appropriate

				if (isHighlighted()) {
					g2.setPaint(geo.getSelColor());
					g2.setStroke(selStroke);
					g2.draw(shape);
				}

				g2.setPaint(getObjectColor());
				g2.setStroke(objStroke);
				g2.draw(shape);

				if (labelVisible) {
					g2.setPaint(geo.getLabelColor());
					g2.setFont(view.getFontLine());
					drawLabel(g2);
				}
				break;

			case DRAW_TYPE_SEGMENT:
				drawSegment.draw(g2);
				break;

			case DRAW_TYPE_RAYS:
				drawRay1.setStroke(objStroke);
				drawRay2.setStroke(objStroke);
				drawRay1.draw(g2);
				drawRay2.draw(g2);
				break;
			}
		}
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	final public GRectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}

		switch (draw_type) {
		case DRAW_TYPE_ELLIPSE:
			if (shape == null) {
				return null;
			}
			return shape.getBounds();

		case DRAW_TYPE_SEGMENT:
			if (drawSegment == null) {
				return null;
			}
			return drawSegment.getBounds();

		default:
			return null;
		}
	}

	@Override
	protected final void drawTrace(GGraphics2D g2) {
		switch (draw_type) {
		default:
			// do nothing
			break;
		case DRAW_TYPE_ELLIPSE:
			// shape may be null in view from plane
			if (shape != null) {
				fill(g2, shape); // fill using default/hatching/image as
									// appropriate
				g2.setPaint(getObjectColor());
				g2.setStroke(objStroke);
				g2.draw(shape);
			}
			break;

		case DRAW_TYPE_SEGMENT:
			drawSegment.drawTrace(g2);
			break;

		case DRAW_TYPE_RAYS:
			drawRay1.setStroke(objStroke);
			drawRay2.setStroke(objStroke);
			drawRay1.drawTrace(g2);
			drawRay2.drawTrace(g2);
			break;
		}
	}

	private void initPreview() {
		// init the conicPart for preview
		Construction cons = previewTempPoints[0].getConstruction();
		int arcMode;
		switch (previewMode) {
		default:
			// do nothing
			break;
		case EuclidianConstants.MODE_SEMICIRCLE:
			AlgoSemicircle alg = new AlgoSemicircle(cons, previewTempPoints[0],
					previewTempPoints[1]);
			cons.removeFromConstructionList(alg);
			initConicPart(alg.getSemicircle());
			break;

		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
			arcMode = previewMode == EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS
					? GeoConicNDConstants.CONIC_PART_ARC
					: GeoConicNDConstants.CONIC_PART_SECTOR;
			AlgoConicPartCircle algo = new AlgoConicPartCircle(cons,
					previewTempPoints[0], previewTempPoints[1],
					previewTempPoints[2], arcMode);
			cons.removeFromConstructionList(algo);
			initConicPart(algo.getConicPart());
			break;

		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:

			arcMode = previewMode == EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS
					? GeoConicNDConstants.CONIC_PART_ARC
					: GeoConicNDConstants.CONIC_PART_SECTOR;
			AlgoConicPartCircumcircle algo2 = new AlgoConicPartCircumcircle(
					cons, previewTempPoints[0], previewTempPoints[1],
					previewTempPoints[2], arcMode);
			cons.removeFromConstructionList(algo2);
			initConicPart(algo2.getConicPart());
			break;
		}

		if (conicPart != null) {
			((GeoElement) conicPart).setLabelVisible(false);
		}
	}

	@Override
	final public void updatePreview() {

		// two selected points + mouse position needed for preview
		isVisible = conicPart != null && prevPoints.size() == neededPrevPoints;
		if (isVisible) {
			for (int i = 0; i < prevPoints.size(); i++) {
				Coords c = view
						.getCoordsForView(prevPoints.get(i).getCoordsInD3());
				// Log.debug("\n"+c);
				if (!DoubleUtil.isZero(c.getZ())) {
					previewTempPoints[i].setUndefined();
				} else {
					previewTempPoints[i].setCoords(c.projectInfDim(), true);
				}
			}
			previewTempPoints[0].updateCascade();
		}
	}

	@Override
	final public void updateMousePos(double xRW, double yRW) {
		if (isVisible) {

			// avoid random line when mouse is over one of the 2 initial points
			if (prevPoints.size() == 2) {
				if (DoubleUtil.isEqual(prevPoints.get(0).getInhomX(), xRW) && DoubleUtil
						.isEqual(previewTempPoints[0].getInhomY(), yRW)) {
					isVisible = false;
					return;
				}
				if (DoubleUtil.isEqual(prevPoints.get(1).getInhomX(), xRW) && DoubleUtil
						.isEqual(previewTempPoints[1].getInhomY(), yRW)) {
					isVisible = false;
					return;
				}
			}

			// double xRW = view.toRealWorldCoordX(x);
			// double yRW = view.toRealWorldCoordY(y);
			previewTempPoints[previewTempPoints.length - 1].setCoords(xRW, yRW,
					1.0);
			previewTempPoints[previewTempPoints.length - 1].updateCascade();
			update();
		}
	}

	@Override
	final public void drawPreview(GGraphics2D g2) {
		draw(g2);
	}

	@Override
	public void disposePreview() {
		if (conicPart != null) {
			((GeoConicND) conicPart).remove();
		}
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		if (!isVisible) {
			return false;
		}

		switch (draw_type) {
		case DRAW_TYPE_ELLIPSE:
			if (geo.isFilled()) {
				shape.intersects(rect);
			}
			if (strokedShape == null) {
				// AND-547, initial buffer size
				try {
					strokedShape = objStroke.createStrokedShape(shape, 130);
				} catch (Exception e) {
					Log.error("problem creating ellipse (part) shape: "
							+ e.getMessage());
					return false;
				}
			}
			return strokedShape.intersects(rect);

		/*
		 * // sector: take shape for hit testing if (closure == Arc2D.PIE) {
		 * return shape.intersects(x-2, y-2, 4, 4) && !shape.contains(x-2, y-2,
		 * 4, 4); } else { if (tempPoint == null) { tempPoint = new
		 * GeoPoint(conicPart.getConstruction()); }
		 * 
		 * double rwX = view.toRealWorldCoordX(x); double rwY =
		 * view.toRealWorldCoordY(y); double maxError = 4 * view.invXscale; //
		 * pixel tempPoint.setCoords(rwX, rwY, 1.0); return
		 * conicPart.isOnPath(tempPoint, maxError); }
		 */

		case DRAW_TYPE_SEGMENT:
			return drawSegment.intersectsRectangle(rect);

		case DRAW_TYPE_RAYS:
			return drawRay1.intersectsRectangle(rect)
					|| drawRay2.intersectsRectangle(rect);

		default:
			return false;
		}
	}

	@Override
	final public boolean hit(int x, int y, int hitThreshold) {
		if (!isVisible) {
			return false;
		}

		boolean pathHit = false, regionHit = false;
		switch (draw_type) {

		case DRAW_TYPE_ELLIPSE:
			if (objStroke.getLineWidth() > 0) {
				if (strokedShape == null) {
					// AND-547, initial buffer size
					try {
						// org.geogebra.ggbjdk.java.awt.geom.IllegalPathStateException:
						// org.geogebra.ggbjdk.java.awt.geom.Path2D$Double.needRoom
						// (Path2D.java:263)
						strokedShape = objStroke.createStrokedShape(shape, 130);
					} catch (Exception e) {
						Log.error("problem creating ellipse (part) shape: "
								+ e.getMessage());
						return false;
					}
				}
				pathHit = strokedShape.intersects(x - hitThreshold,
						y - hitThreshold, 2 * hitThreshold, 2 * hitThreshold);
			}
			if (!pathHit && geo.isFilled()) {
				regionHit = shape.intersects(x - hitThreshold, y - hitThreshold,
						2 * hitThreshold, 2 * hitThreshold);
			}
			break;

		/*
		 * // sector: take shape for hit testing if (closure == Arc2D.PIE) {
		 * return shape.intersects(x-2, y-2, 4, 4) && !shape.contains(x-2, y-2,
		 * 4, 4); } else { if (tempPoint == null) { tempPoint = new
		 * GeoPoint(conicPart.getConstruction()); }
		 * 
		 * double rwX = view.toRealWorldCoordX(x); double rwY =
		 * view.toRealWorldCoordY(y); double maxError = 4 * view.invXscale; //
		 * pixel tempPoint.setCoords(rwX, rwY, 1.0); return
		 * conicPart.isOnPath(tempPoint, maxError); }
		 */

		case DRAW_TYPE_SEGMENT:
			pathHit = drawSegment.hit(x, y, hitThreshold);
			break;
		case DRAW_TYPE_RAYS:
			pathHit = drawRay1.hit(x, y, hitThreshold)
					|| drawRay2.hit(x, y, hitThreshold);
			break;
		default:
			return false;
		}
		if (pathHit) {
			this.conicPart.setLastHitType(HitType.ON_BOUNDARY);
		} else if (regionHit) {
			this.conicPart.setLastHitType(HitType.ON_FILLING);
		} else {
			this.conicPart.setLastHitType(HitType.NONE);
		}
		return pathHit || regionHit;
	}

	@Override
	final public boolean isInside(GRectangle rect) {
		switch (draw_type) {
		case DRAW_TYPE_ELLIPSE:
			// hsape can be null in view from plane
			return shape != null && rect.contains(shape.getBounds());

		case DRAW_TYPE_SEGMENT:
			return drawSegment.isInside(rect);

		case DRAW_TYPE_RAYS:
		default:
			return false;
		}
	}

	@Override
	final public boolean hitLabel(int x, int y) {
		switch (draw_type) {
		case DRAW_TYPE_ELLIPSE:
			return super.hitLabel(x, y);

		case DRAW_TYPE_SEGMENT:
			return drawSegment.hitLabel(x, y);

		case DRAW_TYPE_RAYS:
			return drawRay1.hitLabel(x, y) || drawRay2.hitLabel(x, y);

		default:
			return false;
		}
	}

}

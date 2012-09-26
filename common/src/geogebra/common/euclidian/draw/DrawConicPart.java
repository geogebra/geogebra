/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.euclidian.draw;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GArc2D;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GShape;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.Previewable;
import geogebra.common.euclidian.clipping.ClipShape;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoConicPartCircle;
import geogebra.common.kernel.algos.AlgoConicPartCircumcircle;
import geogebra.common.kernel.algos.AlgoSemicircle;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.kernel.kernelND.GeoPointND;

import java.util.ArrayList;

/**
 * 
 * @author Markus Hohenwarter
 */
public class DrawConicPart extends Drawable implements Previewable {

	private GeoConicPart conicPart;

	private boolean isVisible, labelVisible;

	private GArc2D arc = AwtFactory.prototype.newArc2D();
	private GShape shape;
	// private GeoVec2D transVec;
	private double[] halfAxes;
	// private GeoVec2D center;
	private int closure;

	private static final int DRAW_TYPE_ELLIPSE = 1;
	private static final int DRAW_TYPE_SEGMENT = 2;
	private static final int DRAW_TYPE_RAYS = 3;
	private int draw_type;
	private GAffineTransform transform = AwtFactory.prototype.newAffineTransform();

	// these are needed for degenerate arcs
	private DrawRay drawRay1, drawRay2;
	private DrawSegment drawSegment;
	// private Drawable degDrawable;

	private double[] coords = new double[2];

	// preview
	private ArrayList<GeoPointND> prevPoints;
	private GeoPoint[] previewTempPoints;
	private int previewMode, neededPrevPoints;

	/**
	 * @param view view
	 * @param conicPart conic part
	 */
	public DrawConicPart(EuclidianView view, GeoConicPart conicPart) {
		this.view = view;
		hitThreshold = view.getCapturingThreshold();
		initConicPart(conicPart);
		update();
	}

	private void initConicPart(GeoConicPart initConicPart) {
		this.conicPart = initConicPart;
		geo = initConicPart;

		// center = conicPart.getTranslationVector();
		halfAxes = initConicPart.getHalfAxes();
		// arc or sector?
		closure = initConicPart.getConicPartType() == GeoConicPart.CONIC_PART_SECTOR ? GArc2D.PIE
				: GArc2D.OPEN;
	}

	/**
	 * Creates a new DrawConicPart for preview.
	 * @param view view
	 * @param mode preview mode
	 * @param points points
	 */
	public DrawConicPart(EuclidianView view, int mode, ArrayList<GeoPointND> points) {
		this.view = view;
		prevPoints = points;
		previewMode = mode;

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
			updateStrokes(conicPart);

			switch (conicPart.getType()) {
			case GeoConicNDConstants.CONIC_CIRCLE:
			case GeoConicNDConstants.CONIC_ELLIPSE:
				updateEllipse();
				break;

			case GeoConicNDConstants.CONIC_PARALLEL_LINES:
				updateParallelLines();
				break;

			default:
				// Application.debug("DrawConicPart: unsupported conic type: " +
				// conicPart.getType());
				isVisible = false;
				return;
			}

			// shape on screen?
			if (shape != null
					&& !shape.intersects(0, 0, view.getWidth(), view.getHeight())) {
				isVisible = false;
				// don't return here to make sure that getBounds() works for
				// offscreen points too
			}

			// draw trace
			if (conicPart.trace) {
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

	private void updateEllipse() {
		draw_type = DRAW_TYPE_ELLIPSE;

		// check for huge pixel radius
		double xradius = halfAxes[0] * view.getXscale();
		double yradius = halfAxes[1] * view.getYscale();
		if (xradius > DrawConic.HUGE_RADIUS || yradius > DrawConic.HUGE_RADIUS) {
			isVisible = false;
			return;
		}

		// set arc
		arc.setArc(-halfAxes[0], -halfAxes[1], 2 * halfAxes[0],
				2 * halfAxes[1],
				-Math.toDegrees(conicPart.getParameterStart()),
				-Math.toDegrees(conicPart.getParameterExtent()), closure);

		// transform to screen coords
		transform.setTransform(view.getCoordTransform());
		transform.concatenate(conicPart.getAffineTransform());

		// BIG RADIUS: larger than screen diagonal
		int BIG_RADIUS = view.getWidth() + view.getHeight(); // > view's diagonal
		if (xradius < BIG_RADIUS && yradius < BIG_RADIUS) {
			shape = transform.createTransformedShape(arc);
		} else {
			// clip big arc at screen
			shape = ClipShape.clipToRect(arc, transform, AwtFactory.prototype.newRectangle(-1, -1,
					view.getWidth() + 2, view.getHeight() + 2));
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
				|| (conicPart.positiveOrientation() && draw_type != DRAW_TYPE_SEGMENT)
				|| (!conicPart.positiveOrientation() && draw_type != DRAW_TYPE_RAYS)) { // init
			GeoLine[] lines = conicPart.getLines();
			drawSegment = new DrawSegment(view, lines[0]);
			drawRay1 = new DrawRay(view, lines[0]);
			drawRay2 = new DrawRay(view, lines[1]);
			drawSegment.setGeoElement(conicPart);
			drawRay1.setGeoElement(conicPart);
			drawRay2.setGeoElement(conicPart);
		}

		if (conicPart.positiveOrientation()) {
			draw_type = DRAW_TYPE_SEGMENT;
			drawSegment.update();
		} else {
			draw_type = DRAW_TYPE_RAYS;
			drawRay1.update(false); // don't show labels
			drawRay2.update(false);
		}
	}

	@Override
	final public void draw(geogebra.common.awt.GGraphics2D g2) {
		if (isVisible) {
			switch (draw_type) {
			case DRAW_TYPE_ELLIPSE:
				fill(g2, shape, false); // fill using default/hatching/image as
										// appropriate

				if (geo.doHighlighting()) {
					g2.setPaint(geo
							.getSelColor());
					g2.setStroke(selStroke);
					g2.draw(shape);
				}

				g2.setPaint(geo.getObjectColor());
				g2.setStroke(objStroke);
				g2.draw(shape);

				if (labelVisible) {
					g2.setPaint(geo
							.getLabelColor());
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
	final public geogebra.common.awt.GRectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible())
			return null;

		switch (draw_type) {
		case DRAW_TYPE_ELLIPSE:
			return shape.getBounds();

		case DRAW_TYPE_SEGMENT:
			return drawSegment.getBounds();

		default:
			return null;
		}
	}

	@Override
	protected
	final void drawTrace(geogebra.common.awt.GGraphics2D g2) {
		switch (draw_type) {
		case DRAW_TYPE_ELLIPSE:
			g2.setPaint(geo.getObjectColor());
			g2.setStroke(objStroke);
			g2.draw(shape);
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
		case EuclidianConstants.MODE_SEMICIRCLE:
			AlgoSemicircle alg = new AlgoSemicircle(cons, previewTempPoints[0],
					previewTempPoints[1]);
			cons.removeFromConstructionList(alg);
			initConicPart(alg.getSemicircle());
			break;

		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
			arcMode = previewMode == EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS ? GeoConicPart.CONIC_PART_ARC
					: GeoConicPart.CONIC_PART_SECTOR;
			AlgoConicPartCircle algo = new AlgoConicPartCircle(cons,
					previewTempPoints[0], previewTempPoints[1],
					previewTempPoints[2], arcMode);
			cons.removeFromConstructionList(algo);
			initConicPart(algo.getConicPart());
			break;

		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			arcMode = previewMode == EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS ? GeoConicPart.CONIC_PART_ARC
					: GeoConicPart.CONIC_PART_SECTOR;
			AlgoConicPartCircumcircle algo2 = new AlgoConicPartCircumcircle(
					cons, previewTempPoints[0], previewTempPoints[1],
					previewTempPoints[2], arcMode);
			cons.removeFromConstructionList(algo2);
			initConicPart(algo2.getConicPart());
			break;
		}

		if (conicPart != null)
			conicPart.setLabelVisible(false);
	}

	final public void updatePreview() {
		// two selected points + mouse position needed for preview
		isVisible = conicPart != null && prevPoints.size() == neededPrevPoints;
		if (isVisible) {
			for (int i = 0; i < prevPoints.size(); i++) {
				previewTempPoints[i].setCoords((GeoPoint) prevPoints.get(i));
			}
			previewTempPoints[0].updateCascade();
		}
	}

	final public void updateMousePos(double xRW, double yRW) {
		if (isVisible) {
			// double xRW = view.toRealWorldCoordX(x);
			// double yRW = view.toRealWorldCoordY(y);
			previewTempPoints[previewTempPoints.length - 1].setCoords(xRW, yRW,
					1.0);
			previewTempPoints[previewTempPoints.length - 1].updateCascade();
			update();
		}
	}

	final public void drawPreview(geogebra.common.awt.GGraphics2D g2) {
		draw(g2);
	}

	public void disposePreview() {
		if (conicPart != null) {
			conicPart.remove();
		}
	}
	
	

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		if (!isVisible)
			return false;

		switch (draw_type) {
		case DRAW_TYPE_ELLIPSE:
			if (isFilled()){
				shape.intersects(rect);
			}
			if (strokedShape == null) {
				strokedShape = objStroke.createStrokedShape(shape);
			}
			return strokedShape.intersects(rect);

			/*
			 * // sector: take shape for hit testing if (closure == Arc2D.PIE) {
			 * return shape.intersects(x-2, y-2, 4, 4) && !shape.contains(x-2,
			 * y-2, 4, 4); } else { if (tempPoint == null) { tempPoint = new
			 * GeoPoint(conicPart.getConstruction()); }
			 * 
			 * double rwX = view.toRealWorldCoordX(x); double rwY =
			 * view.toRealWorldCoordY(y); double maxError = 4 * view.invXscale;
			 * // pixel tempPoint.setCoords(rwX, rwY, 1.0); return
			 * conicPart.isOnPath(tempPoint, maxError); }
			 */

		case DRAW_TYPE_SEGMENT:
			return drawSegment.intersectsRectangle(rect);

		case DRAW_TYPE_RAYS:
			return drawRay1.intersectsRectangle(rect) || drawRay2.intersectsRectangle(rect);

		default:
			return false;
		}
	}

	@Override
	final public boolean hit(int x, int y) {
		if (!isVisible)
			return false;

		switch (draw_type) {
		case DRAW_TYPE_ELLIPSE:
			if (strokedShape == null) {
				strokedShape = objStroke.createStrokedShape(shape);
			}
			if (geo.getAlphaValue() > 0.0f || geo.isHatchingEnabled()) {
				return shape.intersects(x - hitThreshold, y - hitThreshold,
						2 * hitThreshold, 2 * hitThreshold);
			}
			return strokedShape.intersects(x - hitThreshold, y - hitThreshold,
					2 * hitThreshold, 2 * hitThreshold);

			/*
			 * // sector: take shape for hit testing if (closure == Arc2D.PIE) {
			 * return shape.intersects(x-2, y-2, 4, 4) && !shape.contains(x-2,
			 * y-2, 4, 4); } else { if (tempPoint == null) { tempPoint = new
			 * GeoPoint(conicPart.getConstruction()); }
			 * 
			 * double rwX = view.toRealWorldCoordX(x); double rwY =
			 * view.toRealWorldCoordY(y); double maxError = 4 * view.invXscale;
			 * // pixel tempPoint.setCoords(rwX, rwY, 1.0); return
			 * conicPart.isOnPath(tempPoint, maxError); }
			 */

		case DRAW_TYPE_SEGMENT:
			return drawSegment.hit(x, y);

		case DRAW_TYPE_RAYS:
			return drawRay1.hit(x, y) || drawRay2.hit(x, y);

		default:
			return false;
		}
	}

	@Override
	final public boolean isInside(geogebra.common.awt.GRectangle rect) {
		switch (draw_type) {
		case DRAW_TYPE_ELLIPSE:
			return rect.contains(shape.getBounds());

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

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

}

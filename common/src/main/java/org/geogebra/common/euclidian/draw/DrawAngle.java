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

import org.geogebra.common.awt.GArc2D;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoAngle;
import org.geogebra.common.kernel.algos.AlgoAnglePoints;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

/**
 * 
 * @author Markus Hohenwarter, Loic De Coq
 */
public class DrawAngle extends Drawable implements Previewable {

	private GeoAngle angle;

	private boolean isVisible;
	private boolean labelVisible;
	private boolean show90degrees;

	private AlgoAngle algo;

	// private Arc2D.Double fillArc = new Arc2D.Double();
	private GArc2D drawArc = AwtFactory.getPrototype().newArc2D();
	private GGeneralPath polygon = AwtFactory.getPrototype().newGeneralPath();
	private GEllipse2DDouble dot90degree;
	private GShape shape;
	private double[] m = new double[2];
	private double[] coords = new double[2];
	private double[] firstVec = new double[2];

	private boolean drawDot;
	private GeoPoint[] previewTempPoints;

	// For decoration
	private GShape shapeArc1;
	private GShape shapeArc2;
	private GArc2D decoArc = AwtFactory.getPrototype().newArc2D();
	private GLine2D[] tick;
	private double[] angleTick = new double[2];
	/** maximum angle distance between two ticks. */
	public static final double MAX_TICK_DISTANCE = Math.toRadians(15);
	private GGeneralPath square;

	private ArrayList<GeoPointND> prevPoints;

	private double maxRadius;

	// END

	/**
	 * @param view
	 *            Euclidian view
	 * @param angle
	 *            Angle to be drawn
	 */
	public DrawAngle(EuclidianView view, GeoAngle angle) {
		this.view = view;
		this.angle = angle;
		geo = angle;

		init();

		if (algo != null) {
			update();
		}
	}

	/**
	 * Creates a new DrawAngle for preview
	 * 
	 * @param view
	 *            view
	 * @param points
	 *            list of points
	 */
	public DrawAngle(EuclidianView view, ArrayList<GeoPointND> points) {
		this.view = view;
		prevPoints = points;

		Construction cons = view.getKernel().getConstruction();
		previewTempPoints = new GeoPoint[3];
		for (int i = 0; i < previewTempPoints.length; i++) {
			previewTempPoints[i] = new GeoPoint(cons);
		}

		initPreview();
	}

	private void init() {
		firstVec = new double[] { 1, 0 };
		m = new double[] { 0, 0 };
		if (angle.getDrawAlgorithm() instanceof AlgoAngle) {
			algo = ((AlgoAngle) angle.getDrawAlgorithm());
		}
	}

	/**
	 * 
	 * @param pt
	 *            point
	 * @return true if coords are in this view
	 */
	public boolean inView(Coords pt) {
		return true;
	}

	/**
	 * 
	 * @param p
	 *            point
	 * @return coords of the point in view
	 */
	final public Coords getCoordsInView(GeoPointND p) {
		return getCoordsInView(p.getInhomCoordsInD3());
	}

	/**
	 * 
	 * @param p
	 *            point
	 * @return coords of the point in view
	 */
	public Coords getCoordsInView(Coords p) {
		return p;
	}

	/**
	 * Used for view from plane (may be reverse oriented)
	 * 
	 * @param start
	 *            initial start
	 * @param extent
	 *            angle extent
	 * @return angle start
	 */
	protected double getAngleStart(double start, double extent) {
		return start;
	}

	private void setNotVisible() {
		isVisible = false;
		shape = null;
		labelVisible = false;
	}

	@Override
	final public void update() {
		AlgoElement drawAlgorithm = geo.getDrawAlgorithm();
		if (drawAlgorithm == null || !drawAlgorithm.equals(geo.getParentAlgorithm())) {
			init();
		}

		isVisible = true;

		if (!geo.isEuclidianVisible() || DoubleUtil.isZero(angle.getValue())) {
			setNotVisible();
			// we may return here; the object is not offscreen, but invisible.
			return;
		}
		labelVisible = geo.isLabelVisible();
		updateStrokes(angle);

		maxRadius = Double.POSITIVE_INFINITY;

		// set vertex and first vector to determine start angle
		if (algo == null) {
			setNotVisible();
			return;
		}

		if (!algo.updateDrawInfo(m, firstVec, this)) {
			setNotVisible();
			return;
		}

		// calc start angle
		double angSt = Math.atan2(firstVec[1], firstVec[0]);
		if (Double.isNaN(angSt) || Double.isInfinite(angSt)) {
			setNotVisible();
			return;
		}
		double angExt = angle.getRawAngle();
		angSt = getAngleStart(angSt, angExt);

		switch (angle.getAngleStyle()) {
		case UNBOUNDED:
			Log.error("Unbounded angle shouldn't be drawable");
			break;

		case NOTREFLEX:
			if (angExt > Math.PI) {
				angSt += angExt;
				angExt = 2.0 * Math.PI - angExt;
			}
			break;

		case ISREFLEX:
			if (angExt < Math.PI) {
				angSt += angExt;
				angExt = 2.0 * Math.PI - angExt;
			}
			break;
		default:
			// all good
		}

		double as = Math.toDegrees(angSt);
		double ae = Math.toDegrees(angExt);

		int arcSize = Math.min((int) maxRadius, angle.getArcSize());

		double r = arcSize * view.getInvXscale();

		// check whether we need to take care for a special 90 degree angle
		// appearance
		show90degrees = view
				.getRightAngleStyle() != EuclidianStyleConstants.RIGHT_ANGLE_STYLE_NONE
				&& angle.isEmphasizeRightAngle()
				&& DoubleUtil.isEqual(angExt, Kernel.PI_HALF);

		// set coords to screen coords of vertex
		coords[0] = m[0];
		coords[1] = m[1];
		view.toScreenCoords(coords);

		// for 90 degree angle
		drawDot = false;

		// SPECIAL case for 90 degree angle, by Loic and Markus
		if (show90degrees) {
			switch (view.getRightAngleStyle()) {
			default:
			case EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE:
				// set 90 degrees square
				if (square == null) {
					square = AwtFactory.getPrototype().newGeneralPath();
				} else {
					square.reset();
				}
				double length = arcSize * 0.7071067811865;
				square.moveTo(coords[0], coords[1]);
				square.lineTo((coords[0] + length * Math.cos(angSt)),
						(coords[1] - length * Math.sin(angSt)
								* view.getScaleRatio()));
				square.lineTo(
						(coords[0] + arcSize
								* Math.cos(angSt + Kernel.PI_HALF / 2)),
						(coords[1]
								- arcSize * Math.sin(angSt + Kernel.PI_HALF / 2)
										* view.getScaleRatio()));
				square.lineTo(
						(coords[0]
								+ length * Math.cos(angSt + Kernel.PI_HALF)),
						(coords[1]
								- length * Math.sin(angSt + Kernel.PI_HALF)
										* view.getScaleRatio()));
				square.lineTo(coords[0], coords[1]);
				shape = square;
				break;

			case EuclidianStyleConstants.RIGHT_ANGLE_STYLE_L:
				// Belgian offset |_
				if (square == null) {
					square = AwtFactory.getPrototype().newGeneralPath();
				} else {
					square.reset();
				}
				length = arcSize * 0.7071067811865;
				double offset = length * 0.4;
				square.moveTo(
						(coords[0] + length * Math.cos(angSt)
								+ offset * Math.cos(angSt)
								+ offset * Math.cos(angSt + Kernel.PI_HALF)),
						(coords[1]
								- length * Math.sin(angSt)
										* view.getScaleRatio()
								- offset * Math.sin(angSt)
								- offset * Math.sin(angSt + Kernel.PI_HALF)));
				square.lineTo(
						(coords[0] + offset * Math.cos(angSt)
								+ offset * Math.cos(angSt + Kernel.PI_HALF)),
						(coords[1] - offset * Math.sin(angSt)
								- offset * Math.sin(angSt + Kernel.PI_HALF)));
				square.lineTo(
						(coords[0]
								+ length * Math.cos(angSt + Kernel.PI_HALF)
								+ offset * Math.cos(angSt)
								+ offset * Math.cos(angSt + Kernel.PI_HALF)),
						(coords[1]
								- length * Math.sin(angSt + Kernel.PI_HALF)
										* view.getScaleRatio()
								- offset * Math.sin(angSt)
								- offset * Math.sin(angSt + Kernel.PI_HALF)));
				shape = square; // FIXME

				break;

			case EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT:
				// set 90 degrees dot
				drawDot = true;

				if (dot90degree == null) {
					dot90degree = AwtFactory.getPrototype()
							.newEllipse2DDouble();
				}
				int diameter = 2 * geo.getLineThickness();
				double radius = r / 1.7;
				double labelAngle = angSt + angExt / 2.0;
				coords[0] = m[0] + radius * Math.cos(labelAngle);
				coords[1] = m[1] + radius * Math.sin(labelAngle);
				view.toScreenCoords(coords);
				dot90degree.setFrame(coords[0] - geo.getLineThickness(),
						coords[1] - geo.getLineThickness(), diameter, diameter);

				// set arc in real world coords and transform to screen coords
				drawArc.setArcByCenter(m[0], m[1], r, -as, -ae, GArc2D.PIE);
				shape = view.getCoordTransform()
						.createTransformedShape(drawArc);
				break;
			}
		}
		// STANDARE case: draw arc with possible decoration
		else {
			// set arc in real world coords and transform to screen coords
			drawArc.setArcByCenter(m[0], m[1], r, -as, -ae, GArc2D.PIE);
			shape = view.getCoordTransform().createTransformedShape(drawArc);

			double rdiff;

			// For Decoration
			switch (geo.getDecorationType()) {
			default:
				// do nothing
				break;
			case GeoElementND.DECORATION_ANGLE_TWO_ARCS:
				rdiff = 4 + geo.getLineThickness() / 2d;
				r = (arcSize - rdiff) * view.getInvXscale();
				decoArc.setArcByCenter(m[0], m[1], r, -as, -ae, GArc2D.OPEN);
				// transform arc to screen coords
				shapeArc1 = view.getCoordTransform()
						.createTransformedShape(decoArc);
				break;

			case GeoElementND.DECORATION_ANGLE_THREE_ARCS:
				rdiff = 4 + geo.getLineThickness() / 2d;
				r = (arcSize - rdiff) * view.getInvXscale();
				decoArc.setArcByCenter(m[0], m[1], r, -as, -ae, GArc2D.OPEN);
				// transform arc to screen coords
				shapeArc1 = view.getCoordTransform()
						.createTransformedShape(decoArc);
				r = (arcSize - 2 * rdiff) * view.getInvXscale();
				decoArc.setArcByCenter(m[0], m[1], r, -as, -ae, GArc2D.OPEN);
				// transform arc to screen coords
				shapeArc2 = view.getCoordTransform()
						.createTransformedShape(decoArc);
				break;

			case GeoElementND.DECORATION_ANGLE_ONE_TICK:
				angleTick[0] = -angSt - angExt / 2;
				updateTick(angleTick[0], arcSize, 0);
				break;

			case GeoElementND.DECORATION_ANGLE_TWO_TICKS:
				angleTick[0] = -angSt - 2 * angExt / 5;
				angleTick[1] = -angSt - 3 * angExt / 5;
				if (Math.abs(angleTick[1] - angleTick[0]) > MAX_TICK_DISTANCE) {
					angleTick[0] = -angSt - angExt / 2 - MAX_TICK_DISTANCE / 2;
					angleTick[1] = -angSt - angExt / 2 + MAX_TICK_DISTANCE / 2;
				}
				updateTick(angleTick[0], arcSize, 0);
				updateTick(angleTick[1], arcSize, 1);
				break;

			case GeoElementND.DECORATION_ANGLE_THREE_TICKS:
				angleTick[0] = -angSt - 3 * angExt / 8;
				angleTick[1] = -angSt - 5 * angExt / 8;
				if (Math.abs(angleTick[1] - angleTick[0]) > 2
						* MAX_TICK_DISTANCE) {
					angleTick[0] = -angSt - angExt / 2 - MAX_TICK_DISTANCE;
					angleTick[1] = -angSt - angExt / 2 + MAX_TICK_DISTANCE;
				}
				updateTick(angleTick[0], arcSize, 0);
				updateTick(angleTick[1], arcSize, 1);
				// middle tick
				angleTick[0] = -angSt - angExt / 2;
				updateTick(angleTick[0], arcSize, 2);
				break;
			case GeoElementND.DECORATION_ANGLE_ARROW_ANTICLOCKWISE:
			case GeoElementND.DECORATION_ANGLE_ARROW_CLOCKWISE:

				// actual angle for arrow point
				double[] n2 = new double[2];

				// adjusted to rotate arrow slightly
				double[] n = new double[2];
				double[] v = new double[2];

				// angle to rotate arrow
				double rotateangle = 8d / arcSize;

				if (geo.getDecorationType() == GeoElementND.DECORATION_ANGLE_ARROW_CLOCKWISE) {
					n2[0] = Math.cos(angSt);
					n2[1] = Math.sin(angSt);
					n[0] = Math.cos(angSt + rotateangle);
					n[1] = Math.sin(angSt + rotateangle);
					v[0] = -n[1];
					v[1] = n[0];
				} else {
					n2[0] = Math.cos(angExt + angSt);
					n2[1] = Math.sin(angExt + angSt);
					n[0] = Math.cos(angExt + angSt - rotateangle);
					n[1] = Math.sin(angExt + angSt - rotateangle);
					v[0] = n[1];
					v[1] = -n[0];
				}

				rdiff = 4 + geo.getLineThickness() / 2d;
				r = (arcSize) * view.getInvXscale();

				double[] p1 = new double[2]; // arrow tip
				p1[0] = m[0] + r * n2[0];
				p1[1] = m[1] + r * n2[1];

				double[] p2 = new double[2]; // arrow vertex 1
				double size = 4d + geo.getLineThickness() / 4d;
				size = size * 0.9d;
				p2[0] = p1[0]
						+ (1 * n[0] + 3 * v[0]) * size * view.getInvXscale();
				p2[1] = p1[1]
						+ (1 * n[1] + 3 * v[1]) * size * view.getInvYscale();

				double[] p3 = new double[2]; // arrow vertex 2
				p3[0] = p1[0]
						+ (-1 * n[0] + 3 * v[0]) * size * view.getInvXscale();
				p3[1] = p1[1]
						+ (-1 * n[1] + 3 * v[1]) * size * view.getInvYscale();

				view.toScreenCoords(p1);
				view.toScreenCoords(p2);
				view.toScreenCoords(p3);

				polygon.reset();
				polygon.moveTo(p1[0], p1[1]);
				polygon.lineTo(p2[0], p2[1]);
				polygon.lineTo(p3[0], p3[1]);
				polygon.closePath();
				break;
			}
			// END
		}

		// shape on screen?
		if (!view.intersects(shape)) {
			setNotVisible();
			return;
		}

		if (labelVisible) {
			// calculate label position
			double radius = r / 1.7;
			double labelAngle = angSt + angExt / 2.0;
			coords[0] = m[0] + radius * Math.cos(labelAngle);
			coords[1] = m[1] + radius * Math.sin(labelAngle);
			view.toScreenCoords(coords);

			labelDesc = angle.getLabelDescription();
			xLabel = (int) (coords[0] - 3);
			yLabel = (int) (coords[1] + 5);

			if (!addLabelOffset() && drawDot) {
				xLabel = (int) (coords[0] + 2 * geo.getLineThickness());
			}
		}

		// G.Sturr 2010-6-28 spreadsheet trace is now handled in
		// GeoElement.update()
		// if (angle.getSpreadsheetTrace())
		// recordToSpreadsheet(angle);

	}

	@Override
	final public void draw(GGraphics2D g2) {

		if (isVisible) {
			if (!show90degrees || view
					.getRightAngleStyle() != EuclidianStyleConstants.RIGHT_ANGLE_STYLE_L) {
				fill(g2, shape); // fill using default/hatching/image as
									// appropriate
			}

			if (isHighlighted()) {
				g2.setPaint(angle.getSelColor());
				g2.setStroke(selStroke);
				g2.draw(shape);
			}

			if (geo.getLineThickness() > 0) {
				g2.setPaint(getObjectColor());
				g2.setStroke(objStroke);
				g2.draw(shape);
			}

			// special handling of 90 degree dot
			if (show90degrees) {
				switch (view.getRightAngleStyle()) {
				case EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT:
					g2.fill(dot90degree);
					break;

				default:
					// nothing to do as square for
					// EuclidianView.RIGHT_ANGLE_STYLE_SQUARE
					// was already drawn as shape
				}
			} else {
				// if we don't have a special 90 degrees appearance we might
				// need to draw
				// other decorations
				switch (geo.getDecorationType()) {
				case GeoElementND.DECORATION_ANGLE_TWO_ARCS:
					g2.draw(shapeArc1);
					break;

				case GeoElementND.DECORATION_ANGLE_THREE_ARCS:
					g2.draw(shapeArc1);
					g2.draw(shapeArc2);
					break;

				case GeoElementND.DECORATION_ANGLE_ONE_TICK:
					g2.setStroke(decoStroke);
					g2.draw(tick[0]);
					break;

				case GeoElementND.DECORATION_ANGLE_TWO_TICKS:
					g2.setStroke(decoStroke);
					g2.draw(tick[0]);
					g2.draw(tick[1]);
					break;

				case GeoElementND.DECORATION_ANGLE_THREE_TICKS:
					g2.setStroke(decoStroke);
					g2.draw(tick[0]);
					g2.draw(tick[1]);
					g2.draw(tick[2]);
					break;
				case GeoElementND.DECORATION_ANGLE_ARROW_ANTICLOCKWISE:
				case GeoElementND.DECORATION_ANGLE_ARROW_CLOCKWISE:
					g2.setStroke(decoStroke);
					g2.fill(polygon);
					break;
				}
			}

			if (labelVisible) {
				g2.setPaint(angle.getLabelColor());
				g2.setFont(view.getFontAngle());
				drawLabel(g2);
			}
		}
	}

	// update coords for the tick decoration
	// tick is at distance radius and oriented towards angle
	// id = 0,1, or 2 for tick[0],tick[1] or tick[2]
	private void updateTick(double angle1, int radius, int id) {
		// coords have to be set to screen coords of m before calling this
		// method
		if (tick == null) {
			tick = new GLine2D[3];
			for (int i = 0; i < tick.length; i++) {
				tick[i] = AwtFactory.getPrototype().newLine2D();
			}
		}

		double cos = Math.cos(angle1);
		double sin = Math.sin(angle1);

		double length = 2.5 + geo.getLineThickness() / 4d;

		tick[id].setLine(coords[0] + (radius - length) * cos,
				coords[1] + (radius - length) * sin * view.getScaleRatio(),
				coords[0] + (radius + length) * cos,
				coords[1] + (radius + length) * sin * view.getScaleRatio());
	}

	@Override
	final public boolean hit(int x, int y, int hitThreshold) {
		return shape != null && shape.contains(x, y);
	}

	@Override
	final public boolean isInside(GRectangle rect) {
		return shape != null && rect.contains(shape.getBounds());
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		return shape != null && shape.intersects(rect);
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	final public GRectangle getBounds() {
		if (!geo.isDefined() || shape == null || !geo.isEuclidianVisible()) {
			return null;
		}

		// return selection circle's bounding box
		return shape.getBounds();
	}

	private void initPreview() {
		// init the conic for preview
		Construction cons = previewTempPoints[0].getConstruction();

		AlgoAnglePoints algoPreview = new AlgoAnglePoints(cons,
				previewTempPoints[0], previewTempPoints[1],
				previewTempPoints[2]);
		cons.removeFromConstructionList(algoPreview);

		geo = algoPreview.getAngle();
		angle = (GeoAngle) geo;
		geo.setEuclidianVisible(true);
		init();
		// initConic(algo.getCircle());
	}

	@Override
	final public void updatePreview() {

		if (geo == null || prevPoints.size() != 2) {
			setNotVisible();
			return;
		}

		for (int i = 0; i < prevPoints.size(); i++) {
			Coords p = view
					.getCoordsForView(prevPoints.get(i).getInhomCoordsInD3());
			previewTempPoints[i].setCoords(p, true);
		}
		previewTempPoints[0].updateCascade();

	}

	@Override
	final public void updateMousePos(double xRW, double yRW) {
		if (isVisible) {
			previewTempPoints[previewTempPoints.length - 1].setCoords(xRW, yRW,
					1.0);
			previewTempPoints[previewTempPoints.length - 1].updateCascade();
			update();
		}
	}

	@Override
	final public void drawPreview(GGraphics2D g2) {
		isVisible = geo != null && prevPoints.size() == 2;
		// shape may be null if the second point is placed and mouse did not yet
		// move away from it
		if (shape != null) {
			draw(g2);
		}
	}

	@Override
	public void disposePreview() {
		// do nothing
	}

	/**
	 * @param vertexScreen
	 *            RW vertex coordinates
	 */
	public void toScreenCoords(double[] vertexScreen) {
		view.toScreenCoords(vertexScreen);

	}

	/**
	 * @param d
	 *            maximal radius
	 */
	public void setMaxRadius(double d) {
		this.maxRadius = d;
	}

}
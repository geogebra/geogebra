/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package geogebra.common.euclidian.draw;

import geogebra.common.awt.GArc2D;
import geogebra.common.awt.GEllipse2DDouble;
import geogebra.common.awt.GGeneralPath;
import geogebra.common.awt.GLine2D;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GShape;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.Previewable;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoAnglePoints;
import geogebra.common.kernel.algos.AngleAlgo;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.plugin.EuclidianStyleConstants;

import java.util.ArrayList;

/**
 * 
 * @author Markus Hohenwarter, Loic De Coq
 */
public class DrawAngle extends Drawable implements Previewable {

	private GeoAngle angle;

	private boolean isVisible, labelVisible, show90degrees;

	private AngleAlgo algo;

	// private Arc2D.Double fillArc = new Arc2D.Double();
	private GArc2D drawArc = AwtFactory.prototype.newArc2D();
	private GGeneralPath polygon = AwtFactory.prototype.newGeneralPath(); // Michael Borcherds
														// 2007-11-19
	private GEllipse2DDouble dot90degree;
	private GShape shape;
	private double m[] = new double[2];
	private double coords[] = new double[2];
	private double[] firstVec = new double[2];
	
	private boolean drawDot;
	private GeoPoint[] previewTempPoints;

	// For decoration
	// added by Lo�c BEGIN
	private GShape shapeArc1, shapeArc2;
	private GArc2D decoArc = AwtFactory.prototype.newArc2D();
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
			angle.setDrawable(true);
			update();
		}
	}

	/**
	 * Creates a new DrawAngle for preview
	 * 
	 * @param view view
	 * @param points list of points
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
		firstVec = new double[]{0,1};
		m = new double[]{0,0};
		if(angle.getDrawAlgorithm() instanceof AngleAlgo){
			algo = ((AngleAlgo)angle.getDrawAlgorithm());
		}
	}

	/**
	 * 
	 * @param pt point
	 * @return true if coords are in this view
	 */
	public boolean inView(Coords pt) {
		return true;
	}

	/**
	 * 
	 * @param p point
	 * @return coords of the point in view 
	 */
	public Coords getCoordsInView(GeoPointND p){
		return p.getInhomCoordsInD(3);
	}
	
	/**
	 * @return raw value (0 to 2pi) of angle
	 */
	protected double getRawAngle() {
		return angle.getRawAngle();
	}

	@Override
	final public void update() {
		if (!geo.getDrawAlgorithm().equals(geo.getParentAlgorithm()))
			init();

		isVisible = geo.isEuclidianVisible();
		if (!isVisible) {
			shape = null;
			//we may return here; the object is not offscreen, but invisible.
			return;
		}
		labelVisible = geo.isLabelVisible();
		updateStrokes(angle);

		maxRadius = Double.POSITIVE_INFINITY;

		// set vertex and first vector to determine start angle
		isVisible &= algo != null && algo.updateDrawInfo(m, firstVec, this); 
				
				
		// calc start angle
		double angSt = Math.atan2(firstVec[1], firstVec[0]);
		if (Double.isNaN(angSt) || Double.isInfinite(angSt)) {
			isVisible = false;
			return;
		}
		// Michael Borcherds 2007-11-19 BEGIN
		// double angExt = angle.getValue();
		double angExt = getRawAngle();

		// if this angle was not allowed to become a reflex angle
		// (i.e. greater than pi) we got (2pi - angleValue) for angExt
		// if (angle.changedReflexAngle()) {
		// angSt = angSt - angExt;
		// }

		switch (angle.getAngleStyle()) {
		case GeoAngle.ANGLE_ISCLOCKWISE:
			angSt += angExt;
			angExt = 2.0 * Math.PI - angExt;
			break;

		case GeoAngle.ANGLE_ISNOTREFLEX:
			if (angExt > Math.PI) {
				angSt += angExt;
				angExt = 2.0 * Math.PI - angExt;
			}
			break;

		case GeoAngle.ANGLE_ISREFLEX:
			if (angExt < Math.PI) {
				angSt += angExt;
				angExt = 2.0 * Math.PI - angExt;
			}
			break;
		}
		// Michael Borcherds 2007-11-19 END

		double as = Math.toDegrees(angSt);
		double ae = Math.toDegrees(angExt);

		int arcSize = Math.min((int) maxRadius, angle.getArcSize());

		double r = arcSize * view.getInvXscale();

		// check whether we need to take care for a special 90 degree angle
		// appearance
		show90degrees = view.getRightAngleStyle() != EuclidianStyleConstants.RIGHT_ANGLE_STYLE_NONE
				&& angle.isEmphasizeRightAngle()
				&& Kernel.isEqual(angExt, Kernel.PI_HALF);

		// set coords to screen coords of vertex
		coords[0] = m[0];
		coords[1] = m[1];
		view.toScreenCoords(coords);

		// for 90 degree angle
		drawDot = false;

		// SPECIAL case for 90 degree angle, by Loic and Markus
		if (show90degrees) {
			switch (view.getRightAngleStyle()) {
			case EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE:
				// set 90 degrees square
				if (square == null)
					square = AwtFactory.prototype.newGeneralPath();
				else
					square.reset();
				double length = arcSize * 0.7071067811865;
				square.moveTo((float) coords[0], (float) coords[1]);
				square.lineTo(
						(float) (coords[0] + length * Math.cos(angSt)),
						(float) (coords[1] - length * Math.sin(angSt)
								* view.getScaleRatio()));
				square.lineTo(
						(float) (coords[0] + arcSize
								* Math.cos(angSt + Kernel.PI_HALF / 2)),
						(float) (coords[1] - arcSize
								* Math.sin(angSt + Kernel.PI_HALF / 2)
								* view.getScaleRatio()));
				square.lineTo(
						(float) (coords[0] + length
								* Math.cos(angSt + Kernel.PI_HALF)),
						(float) (coords[1] - length
								* Math.sin(angSt + Kernel.PI_HALF)
								* view.getScaleRatio()));
				square.lineTo((float) coords[0], (float) coords[1]);
				shape = square;
				break;

			case EuclidianStyleConstants.RIGHT_ANGLE_STYLE_L:
				// Belgian offset |_
				if (square == null)
					square = AwtFactory.prototype.newGeneralPath();
				else
					square.reset();
				length = arcSize * 0.7071067811865;
				double offset = length * 0.4;
				square.moveTo(
						(float) (coords[0] + length * Math.cos(angSt) + offset
								* Math.cos(angSt) + offset
								* Math.cos(angSt + Kernel.PI_HALF)),
						(float) (coords[1] - length * Math.sin(angSt)
								* view.getScaleRatio() - offset
								* Math.sin(angSt) - offset
								* Math.sin(angSt + Kernel.PI_HALF)));
				square.lineTo(
						(float) (coords[0] + offset * Math.cos(angSt) + offset
								* Math.cos(angSt + Kernel.PI_HALF)),
						(float) (coords[1] - offset * Math.sin(angSt) - offset
								* Math.sin(angSt + Kernel.PI_HALF)));
				square.lineTo(
						(float) (coords[0] + length
								* Math.cos(angSt + Kernel.PI_HALF)
								+ offset * Math.cos(angSt) + offset
								* Math.cos(angSt + Kernel.PI_HALF)),
						(float) (coords[1] - length
								* Math.sin(angSt + Kernel.PI_HALF)
								* view.getScaleRatio() - offset
								* Math.sin(angSt) - offset
								* Math.sin(angSt + Kernel.PI_HALF)));
				shape = square;  //FIXME

				break;

			case EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT:
				// set 90 degrees dot
				drawDot = true;

				if (dot90degree == null)
					dot90degree = AwtFactory.prototype.newEllipse2DDouble();
				int diameter = 2 * geo.lineThickness;
				double radius = r / 1.7;
				double labelAngle = angSt + angExt / 2.0;
				coords[0] = m[0] + radius * Math.cos(labelAngle);
				coords[1] = m[1] + radius * Math.sin(labelAngle);
				view.toScreenCoords(coords);
				dot90degree.setFrame(coords[0] - geo.lineThickness, coords[1]
						- geo.lineThickness, diameter, diameter);

				// set arc in real world coords and transform to screen coords
				drawArc.setArcByCenter(m[0], m[1], r, -as, -ae, GArc2D.PIE);
				shape = view.getCoordTransform().createTransformedShape(drawArc);
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
			// Added By Lo�c BEGIN
			switch (geo.decorationType) {
			case GeoElement.DECORATION_ANGLE_TWO_ARCS:
				rdiff = 4 + geo.lineThickness / 2d;
				r = (arcSize - rdiff) * view.getInvXscale();
				decoArc.setArcByCenter(m[0], m[1], r, -as, -ae, GArc2D.OPEN);
				// transform arc to screen coords
				shapeArc1 = view.getCoordTransform().createTransformedShape(decoArc);
				break;

			case GeoElement.DECORATION_ANGLE_THREE_ARCS:
				rdiff = 4 + geo.lineThickness / 2d;
				r = (arcSize - rdiff) * view.getInvXscale();
				decoArc.setArcByCenter(m[0], m[1], r, -as, -ae, GArc2D.OPEN);
				// transform arc to screen coords
				shapeArc1 = view.getCoordTransform().createTransformedShape(decoArc);
				r = (arcSize - 2 * rdiff) * view.getInvXscale();
				decoArc.setArcByCenter(m[0], m[1], r, -as, -ae, GArc2D.OPEN);
				// transform arc to screen coords
				shapeArc2 = view.getCoordTransform().createTransformedShape(decoArc);
				break;

			case GeoElement.DECORATION_ANGLE_ONE_TICK:
				angleTick[0] = -angSt - angExt / 2;
				updateTick(angleTick[0], arcSize, 0);
				break;

			case GeoElement.DECORATION_ANGLE_TWO_TICKS:
				angleTick[0] = -angSt - 2 * angExt / 5;
				angleTick[1] = -angSt - 3 * angExt / 5;
				if (Math.abs(angleTick[1] - angleTick[0]) > MAX_TICK_DISTANCE) {
					angleTick[0] = -angSt - angExt / 2 - MAX_TICK_DISTANCE / 2;
					angleTick[1] = -angSt - angExt / 2 + MAX_TICK_DISTANCE / 2;
				}
				updateTick(angleTick[0], arcSize, 0);
				updateTick(angleTick[1], arcSize, 1);
				break;

			case GeoElement.DECORATION_ANGLE_THREE_TICKS:
				angleTick[0] = -angSt - 3 * angExt / 8;
				angleTick[1] = -angSt - 5 * angExt / 8;
				if (Math.abs(angleTick[1] - angleTick[0]) > 2 * MAX_TICK_DISTANCE) {
					angleTick[0] = -angSt - angExt / 2 - MAX_TICK_DISTANCE;
					angleTick[1] = -angSt - angExt / 2 + MAX_TICK_DISTANCE;
				}
				updateTick(angleTick[0], arcSize, 0);
				updateTick(angleTick[1], arcSize, 1);
				// middle tick
				angleTick[0] = -angSt - angExt / 2;
				updateTick(angleTick[0], arcSize, 2);
				break;
			// Michael Borcherds 2007-11-19 START
			case GeoElement.DECORATION_ANGLE_ARROW_ANTICLOCKWISE:
			case GeoElement.DECORATION_ANGLE_ARROW_CLOCKWISE:
				double n2[] = new double[2]; // actual angle for arrow point
				double n[] = new double[2]; // adjusted to rotate arrow slightly
				double v[] = new double[2]; // adjusted to rotate arrow slightly

				double rotateangle = 0.25d; // rotate arrow slightly

				if (geo.decorationType == GeoElement.DECORATION_ANGLE_ARROW_CLOCKWISE) {
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

				double p1[] = new double[2];
				double p2[] = new double[2];
				double p3[] = new double[2];
				rdiff = 4 + geo.lineThickness / 2d;
				r = (arcSize) * view.getInvXscale();

				p1[0] = m[0] + r * n2[0];
				p1[1] = m[1] + r * n2[1]; // arrow tip

				double size = 4d + geo.lineThickness / 4d;
				size = size * 0.9d;

				p2[0] = p1[0] + (1 * n[0] + 3 * v[0]) * size * view.getInvXscale();
				p2[1] = p1[1] + (1 * n[1] + 3 * v[1]) * size * view.getInvYscale(); // arrow
																				// end
																				// 1

				p3[0] = p1[0] + (-1 * n[0] + 3 * v[0]) * size * view.getInvXscale();
				p3[1] = p1[1] + (-1 * n[1] + 3 * v[1]) * size * view.getInvYscale(); // arrow
																				// end
																				// 2

				view.toScreenCoords(p1);
				view.toScreenCoords(p2);
				view.toScreenCoords(p3);

				polygon.reset();
				polygon.moveTo((float) p1[0], (float) p1[1]);
				polygon.lineTo((float) p2[0], (float) p2[1]);
				polygon.lineTo((float) p3[0], (float) p3[1]);
				polygon.lineTo((float) p1[0], (float) p1[1]);

				polygon.moveTo((float) p1[0], (float) p1[1]);
				polygon.lineTo((float) p2[0], (float) p2[1]);
				polygon.lineTo((float) p3[0], (float) p3[1]);
				polygon.lineTo((float) p1[0], (float) p1[1]);
				polygon.closePath();

				break;
			// Michael Borcherds 2007-11-19 END

			}
			// END
		}

		// shape on screen?
		if (!shape.intersects(0, 0, view.getWidth(), view.getHeight())) {
			isVisible = false;
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

			if (!addLabelOffset() && drawDot)
				xLabel = (int) (coords[0] + 2 * geo.lineThickness);
		}

		// G.Sturr 2010-6-28 spreadsheet trace is now handled in
		// GeoElement.update()
		// if (angle.getSpreadsheetTrace())
		// recordToSpreadsheet(angle);

	}

	@Override
	final public void draw(geogebra.common.awt.GGraphics2D g2) {

		if (isVisible) {
			if (!show90degrees
					|| view.getRightAngleStyle() != EuclidianStyleConstants.RIGHT_ANGLE_STYLE_L) {
				fill(g2, shape, false); // fill using default/hatching/image as
										// appropriate
			}

			if (geo.doHighlighting()) {
				g2.setPaint(angle.getSelColor());
				g2.setStroke(selStroke);
				g2.draw(shape);
			}

			if (geo.lineThickness > 0) {
				g2.setPaint(angle
						.getObjectColor());
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
				switch (geo.decorationType) {
				case GeoElement.DECORATION_ANGLE_TWO_ARCS:
					g2.draw(shapeArc1);
					break;

				case GeoElement.DECORATION_ANGLE_THREE_ARCS:
					g2.draw(shapeArc1);
					g2.draw(shapeArc2);
					break;

				case GeoElement.DECORATION_ANGLE_ONE_TICK:
					g2.setStroke(decoStroke);
					g2.draw(tick[0]);
					break;

				case GeoElement.DECORATION_ANGLE_TWO_TICKS:
					g2.setStroke(decoStroke);
					g2.draw(tick[0]);
					g2.draw(tick[1]);
					break;

				case GeoElement.DECORATION_ANGLE_THREE_TICKS:
					g2.setStroke(decoStroke);
					g2.draw(tick[0]);
					g2.draw(tick[1]);
					g2.draw(tick[2]);
					break;
				// Michael Borcherds 2007-11-19 START
				case GeoElement.DECORATION_ANGLE_ARROW_ANTICLOCKWISE:
				case GeoElement.DECORATION_ANGLE_ARROW_CLOCKWISE:
					g2.setStroke(decoStroke);
					g2.fill(polygon);
					break;
				// Michael Borcherds 2007-11-19
				}
			}

			if (labelVisible) {
				g2.setPaint(angle
						.getLabelColor());
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
				tick[i] = AwtFactory.prototype.newLine2D();
			}
		}

		double cos = Math.cos(angle1);
		double sin = Math.sin(angle1);

		double length = 2.5 + geo.lineThickness / 4d;

		tick[id].setLine(coords[0] + (radius - length) * cos, coords[1]
				+ (radius - length) * sin * view.getScaleRatio(), coords[0]
				+ (radius + length) * cos, coords[1] + (radius + length) * sin
				* view.getScaleRatio());
	}

	@Override
	final public boolean hit(int x, int y) {
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

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	final public GRectangle getBounds() {
		if (!geo.isDefined() || shape == null || !geo.isEuclidianVisible())
			return null;

		// return selection circle's bounding box
		return shape.getBounds();
	}

	private void initPreview() {
		// init the conic for preview
		Construction cons = previewTempPoints[0].getConstruction();

		AlgoAnglePoints algoPreview = new AlgoAnglePoints(cons, previewTempPoints[0],
				previewTempPoints[1], previewTempPoints[2]);
		cons.removeFromConstructionList(algoPreview);

		geo = algoPreview.getAngle();
		angle = (GeoAngle) geo;
		geo.setEuclidianVisible(true);
		init();
		// initConic(algo.getCircle());
	}

	final public void updatePreview() {
		isVisible = geo != null && prevPoints.size() == 2;
		if (isVisible) {
			for (int i = 0; i < prevPoints.size(); i++) {
				Coords p = view.getCoordsForView(prevPoints.get(i)
						.getInhomCoordsInD(3));
				previewTempPoints[i].setCoords(p, true);
			}
			previewTempPoints[0].updateCascade();
		}
	}

	final public void updateMousePos(double xRW, double yRW) {
		if (isVisible) {
			previewTempPoints[previewTempPoints.length - 1].setCoords(xRW, yRW,
					1.0);
			previewTempPoints[previewTempPoints.length - 1].updateCascade();
			update();
		}
	}

	final public void drawPreview(geogebra.common.awt.GGraphics2D g2) {
		isVisible = geo != null && prevPoints.size() == 2;
		draw(g2);
	}

	public void disposePreview() {
		//do nothing
	}

	/**
	 * @param vertexScreen RW vertex coordinates
	 */
	public void toScreenCoords(double[] vertexScreen) {
		view.toScreenCoords(vertexScreen);
		
	}

	/**
	 * @param d maximal radius
	 */
	public void setMaxRadius(double d) {
		this.maxRadius = d;
		
	}
}
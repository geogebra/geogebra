/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * DrawConic.java
 *
 * Created on 16. Oktober 2001, 15:13
 */

package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GArc2D;
import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangularShape;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.euclidian.clipping.ClipShape;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.algos.AlgoCirclePointRadius;
import org.geogebra.common.kernel.algos.AlgoCircleThreePoints;
import org.geogebra.common.kernel.algos.AlgoCircleTwoPoints;
import org.geogebra.common.kernel.algos.AlgoConicFivePoints;
import org.geogebra.common.kernel.algos.AlgoEllipseHyperbolaFociPoint;
import org.geogebra.common.kernel.algos.AlgoParabolaPointLine;
import org.geogebra.common.kernel.algos.AlgoShearOrStretch;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElement.HitType;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

/**
 * 
 * @author Markus
 */
public class DrawConic extends SetDrawable implements Previewable {

	/** plotpoints per quadrant for hyperbola */
	protected static final int PLOT_POINTS = 32;
	/** maximum number of plot points */
	public static final int MAX_PLOT_POINTS = 300;
	/**
	 * maximum of pixels for a standard circle radius bigger circles are drawn
	 * via Arc2D
	 */
	public static final double HUGE_RADIUS = 1E12;

	/**
	 * the conic being drawn (not necessarily the same as geo, eg, for ineq
	 * drawing)
	 */
	protected GeoConicND conic;

	/** whether this is euclidian visible and onscreen */
	protected boolean isVisible;
	/** whether the label is visible */
	protected boolean labelVisible;
	private int type;
	/** label coordinates */
	protected double[] labelCoords = new double[2];

	// CONIC_SINGLE_POINT
	private boolean firstPoint = true;
	private GeoPoint point;
	private DrawPoint drawPoint;

	// CONIC_INTERSECTING_LINES
	private boolean firstLines = true;
	private GeoLine[] lines;
	private DrawLine[] drawLines;

	// CONIC_CIRCLE
	private boolean firstCircle = true;
	private GeoVec2D midpoint;
	private GArc2D arc;
	private GeneralPathClipped arcFiller;
	private GeneralPathClipped gp;
	private GRectangularShape circle;
	private double mx;
	private double my;
	private double radius;
	private double yradius;
	private double angSt;
	private double angEnd;

	/** transform for ellipse, hyperbola, parabola */
	protected GAffineTransform transform = AwtFactory.getPrototype()
			.newAffineTransform();
	/** shape to be filled (eg. ellipse, space between paralel lines) */
	protected GShape fillShape;

	// CONIC_ELLIPSE
	private boolean firstEllipse = true;
	/** lengths of half axes */
	protected double[] halfAxes;
	private GEllipse2DDouble ellipse;

	// CONIC_PARABOLA
	private boolean firstParabola = true;
	/** x coord of start point for parabola/hyperbola */
	protected double x0;
	/** y coord of start point for parabola/hyperbola */
	protected double y0;
	private double k2;
	private GeoVec2D vertex;
	/** parabolic path */
	protected GGeneralPath parabola;
	private double[] parpoints = new double[8];

	// CONIC_HYPERBOLA
	/** whether this is the first time we draw a hyperbola */
	protected boolean firstHyperbola = true;
	/** first half-axis */
	protected double a;
	private double b;
	private double y;
	/** number of points used for hyperbola path */
	protected int points = PLOT_POINTS;
	private GeneralPathClipped hypLeft;
	private GeneralPathClipped hypRight;
	private boolean hypLeftOnScreen;
	private boolean hypRightOnScreen;

	// preview of circle (two points or three points)
	private ArrayList<GeoPointND> prevPoints;
	private ArrayList<GeoSegmentND> prevSegments;
	private ArrayList<GeoLineND> prevLines;
	private ArrayList<GeoConicND> prevConics;
	private GeoPoint[] previewTempPoints;
	private GeoLineND previewTempLine;
	private GeoNumeric previewTempRadius;
	private int previewMode;
	private int neededPrevPoints;
	private boolean isPreview = false;
	private boolean ignoreSingularities;

	/** eigenvectors */
	protected Coords[] ev;
	private GeoLine diameter;

	@Override
	public GArea getShape() {
		GArea area = super.getShape() != null ? super.getShape()
				: (fillShape == null ? AwtFactory.getPrototype().newArea()
						: AwtFactory.getPrototype().newArea(fillShape));
		if (conic.isInverseFill()) {
			GArea complement = AwtFactory.getPrototype()
					.newArea(view.getBoundingPath());
			if (arcFiller != null) {
				complement = AwtFactory.getPrototype().newArea(arcFiller);
			}
			complement.subtract(area);
			return complement;
		}
		return area;
	}

	/**
	 * Creates new DrawConic
	 * 
	 * @param view
	 *            view
	 * @param c
	 *            conic
	 * @param ignoreSingularities
	 *            true to avoid drawing points
	 */
	public DrawConic(EuclidianView view, GeoConicND c,
			boolean ignoreSingularities) {
		this.view = view;
		isPreview = false;
		this.ignoreSingularities = ignoreSingularities;
		initConic(c);
		update();
	}

	private void initConic(GeoConicND c) {
		conic = c;
		geo = c;

		vertex = c.getTranslationVector(); // vertex
		midpoint = vertex;
		halfAxes = c.getHalfAxes();
		c.getAffineTransform();
	}

	/**
	 * Creates a new DrawConic for preview of a circle
	 * 
	 * @param view
	 *            view
	 * @param mode
	 *            preview mode
	 * @param points
	 *            preview points
	 */
	public DrawConic(EuclidianView view, int mode,
			ArrayList<GeoPointND> points) {
		this.view = view;
		prevPoints = points;
		previewMode = mode;

		Construction cons = view.getKernel().getConstruction();

		switch (mode) {
		default:
		case EuclidianConstants.MODE_CIRCLE_TWO_POINTS:
		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS:
			neededPrevPoints = 1;
			break;
		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
		case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
		case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
			neededPrevPoints = 2;
			break;
		case EuclidianConstants.MODE_CONIC_FIVE_POINTS:
			neededPrevPoints = 4;
			break;
		}

		previewTempPoints = new GeoPoint[neededPrevPoints + 1];
		for (int i = 0; i < previewTempPoints.length; i++) {
			previewTempPoints[i] = new GeoPoint(cons);
		}

		initPreview();
	}

	/**
	 * Creates a new DrawConic for preview of a parabola
	 * 
	 * @param view
	 *            view
	 * @param selectedLines
	 *            possible directrix
	 * @param points
	 *            preview points
	 */
	public DrawConic(EuclidianView view, ArrayList<GeoPointND> points,
			ArrayList<GeoLineND> selectedLines) {
		this.view = view;
		prevPoints = points;
		prevLines = selectedLines;
		neededPrevPoints = 1;
		previewMode = EuclidianConstants.MODE_PARABOLA;

		Construction cons = view.getKernel().getConstruction();

		if (selectedLines.size() == 0) {
			previewTempLine = new GeoLine(cons);
		} else {
			previewTempLine = selectedLines.get(0);
		}

		previewTempPoints = new GeoPoint[1];
		previewTempPoints[0] = new GeoPoint(cons);

		initPreview();
	}

	/**
	 * Creates a new DrawConic for preview of a compass circle (radius or
	 * segment first, then center point)
	 * 
	 * @param view
	 *            view
	 * @param mode
	 *            preview mode
	 * @param points
	 *            preview points
	 * @param segments
	 *            preview segments
	 * @param conics
	 *            preview conics
	 */
	public DrawConic(EuclidianView view, int mode, ArrayList<GeoPointND> points,
			ArrayList<GeoSegmentND> segments, ArrayList<GeoConicND> conics) {
		this.view = view;
		prevPoints = points;
		prevSegments = segments;
		prevConics = conics;
		previewMode = mode;

		Construction cons = view.getKernel().getConstruction();
		previewTempRadius = new GeoNumeric(cons);
		previewTempPoints = new GeoPoint[1];
		previewTempPoints[0] = new GeoPoint(cons);

		initPreview();
	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible) {
			return;
		}
		labelVisible = geo.isLabelVisible();

		updateStrokes(conic);
		type = conic.getType();

		switch (type) {
		default:
		case GeoConicNDConstants.CONIC_EMPTY:
			setShape(null);
			fillShape = null;
			break;
		case GeoConicNDConstants.CONIC_SINGLE_POINT:
			updateSinglePoint();
			break;

		case GeoConicNDConstants.CONIC_DOUBLE_LINE:
			updateDoubleLine();
			break;

		case GeoConicNDConstants.CONIC_INTERSECTING_LINES:
		case GeoConicNDConstants.CONIC_PARALLEL_LINES:
		case GeoConicNDConstants.CONIC_LINE:
			updateLines();
			break;

		case GeoConicNDConstants.CONIC_CIRCLE:
			updateCircle();
			break;

		case GeoConicNDConstants.CONIC_ELLIPSE:
			updateEllipse();
			break;

		case GeoConicNDConstants.CONIC_HYPERBOLA:
			updateHyperbola();
			break;

		case GeoConicNDConstants.CONIC_PARABOLA:
			updateParabola();
			break;
		}

		if (!isVisible) {
			return;
		}

		// shape on screen?
		GRectangle viewRect = view.getFrame();
		switch (type) {
		default:
			// do nothing
			break;
		case GeoConicNDConstants.CONIC_CIRCLE:
		case GeoConicNDConstants.CONIC_ELLIPSE:
		case GeoConicNDConstants.CONIC_PARABOLA:
			isVisible = checkCircleEllipseParabolaOnScreen(viewRect);
			break;

		case GeoConicNDConstants.CONIC_HYPERBOLA:
			isVisible = checkHyperbolaOnScreen(viewRect);
			break;
		}

		if (!isVisible) {
			return;
		}

		// draw trace
		if (conic.getTrace()) {
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

		if (labelVisible) {
			labelDesc = geo.getLabelDescription();
			addLabelOffset();
		}
	}

	/**
	 * check circle/ellipse/parabola intersects the screen
	 * 
	 * @param viewRect
	 *            view rectangle
	 * @return if hyperbola intersects the screen
	 */
	protected boolean checkCircleEllipseParabolaOnScreen(GRectangle viewRect) {
		boolean includesScreenCompletely = fillShape.contains(viewRect);

		// offScreen = includesScreenCompletely or the shape does not
		// intersect the view rectangle
		boolean offScreen = includesScreenCompletely
				|| !fillShape.getBounds2D().intersects(viewRect);
		if (!geo.isFilled()) {
			// no filling
			return !offScreen;
		}
		// filling
		if (includesScreenCompletely) {
			return true;
		}
		return !offScreen;
	}

	/**
	 * check hyperbola intersects the screen
	 * 
	 * @param viewRect
	 *            view rectangle
	 * @return if hyperbola intersects the screen
	 */
	protected boolean checkHyperbolaOnScreen(GRectangle viewRect) {
		// hyperbola wings on screen?
		hypLeftOnScreen = hypLeft.intersects(viewRect);
		hypRightOnScreen = hypRight.intersects(viewRect);
		if (!hypLeftOnScreen && !hypRightOnScreen) {
			return false;
		}
		return true;
	}

	final private void updateSinglePoint() {
		// we want to determine the sign of the result but we can't use fixed
		// point
		// as it may be equal to the single point. Point (b.x+1,0) differs in
		// one coord.

		fillShape = null;

		if (firstPoint) {
			firstPoint = false;
			point = conic.getSinglePoint();
			if (point == null) {
				point = new GeoPoint(conic.getConstruction());
			}
			drawPoint = new DrawPoint(view, point, isPreview);
			drawPoint.setGeoElement(conic);
			// drawPoint.font = view.fontConic;
		}

		setShape(!ignoreSingularities ? drawPoint.getDot() : null);

		// looks if it's on view
		Coords p = view.getCoordsForView(conic.getMidpoint3D());
		if (!DoubleUtil.isZero(p.getZ())) {
			isVisible = false;
			return;
		}

		double[] coords = new double[2];
		coords[0] = p.getX();
		coords[1] = p.getY();

		point.copyLabel(conic);
		point.setObjColor(conic.getObjectColor());
		point.setPointSize(conic.getLineThickness());

		drawPoint.update(coords);
	}

	/**
	 * Updates the double line and shape so that positive part is colored
	 */
	protected void updateDoubleLine() {
		updateLines();
	}

	/**
	 * Updates the lines and shape so that positive part is colored
	 */
	protected void updateLines() {
		fillShape = null;

		if (firstLines) {
			firstLines = false;
			lines = conic.getLines();
			drawLines = new DrawLine[2];
			drawLines[0] = new DrawLine(view, lines[0]);
			drawLines[1] = new DrawLine(view, lines[1]);
			drawLines[0].setGeoElement(geo);
			drawLines[1].setGeoElement(geo);
		}

		CoordMatrix m = null;
		if (!isPreview) {
			if (view.getMatrix() == null) {
				if (conic.isGeoElement3D()) {
					m = conic.getCoordSys().getMatrixOrthonormal().inverse();
				}
			} else {
				if (conic.isGeoElement3D()) {
					m = conic.getCoordSys().getMatrixOrthonormal().inverse()
							.mul(view.getMatrix());
				} else {
					m = view.getMatrix();
				}
			}
		}

		for (int i = 0; i < 2; i++) {
			drawLines[i].forceLineType(conic.lineType);
			drawLines[i].update(m);
			// thickness needs update #4087
			drawLines[i].updateStrokesJustLineThickness(geo);
		}

		if (conic.type == GeoConicNDConstants.CONIC_PARALLEL_LINES
				|| conic.type == GeoConicNDConstants.CONIC_INTERSECTING_LINES
				|| conic.type == GeoConicNDConstants.CONIC_LINE) {

			if (drawLines[0].isVisible()) {
				fillShape = drawLines[0].getShape(true);
				if (conic.type != GeoConicNDConstants.CONIC_LINE) {
					((GArea) fillShape).exclusiveOr(drawLines[1].getShape(true));
					// FIXME: buggy when conic(RW(0),RW(0))=0
				}

				if (negativeColored()) {
					GArea complement = AwtFactory.getPrototype()
							.newArea(view.getBoundingPath());
					complement.subtract((GArea) fillShape);
					fillShape = complement;

				}
			} else {
				fillShape = null;
			}
		}
	}

	private boolean negativeColored() {
		double[] xTry = new double[] { 0, 10, 20, 0, 10, 20 };
		double[] yTry = new double[] { 0, 0, 0, 10, 10, 20 };
		for (int i = 0; i < 6; i++) {
			double val1 = conic.evaluate(view.toRealWorldCoordX(xTry[i]),
					view.toRealWorldCoordY(yTry[i]));
			if (conic.type == GeoConicNDConstants.CONIC_INTERSECTING_LINES) {
				val1 *= conic.evaluate(conic.b.getX() + lines[0].x + lines[1].x,
						conic.b.getY() + lines[0].y + lines[1].y);
			}
			if (conic.type == GeoConicNDConstants.CONIC_PARALLEL_LINES) {
				val1 *= conic.evaluate(conic.b.getX(), conic.b.getY());
			}
			if (!DoubleUtil.isZero(val1)) {
				return (val1 > 0) ^ fillShape.contains(xTry[i], yTry[i]);
			}
		}
		return false;
	}

	/**
	 * Update method for circles
	 */
	protected void updateCircle() {
		setShape(null);
		boolean fullAngle = false;
		// calc screen pixel of radius
		radius = halfAxes[0] * view.getXscale();
		yradius = halfAxes[1] * view.getYscale(); // radius scaled in y
													// direction
		if (radius > DrawConic.HUGE_RADIUS || yradius > DrawConic.HUGE_RADIUS) {
			Log.debug("ellipse fallback");
			// ellipse drawing is handling those cases better
			updateEllipse();
			return;
		}

		if (firstCircle) {
			firstCircle = false;
			arc = AwtFactory.getPrototype().newArc2D();
			if (ellipse == null) {
				ellipse = AwtFactory.getPrototype().newEllipse2DDouble();
			}
		}

		int i = -1; // bugfix

		// if circle is very big, draw arc: this is very important
		// for graphical continuity

		// BIG RADIUS: larger than screen diagonal
		int BIG_RADIUS = view.getWidth() + view.getHeight(); // > view's
																// diagonal
		if (radius < BIG_RADIUS && yradius < BIG_RADIUS) {
			circle = ellipse;
			arcFiller = null;
			// calc screen coords of midpoint
			Coords M;
			if (isPreview) {
				M = conic.getMidpoint3D().getInhomCoords();
			} else {
				M = view.getCoordsForView(conic.getMidpoint3D());
				if (!DoubleUtil.isZero(M.getZ())) { // check if in view
					isVisible = false;
					return;
				}
				// check if eigen vec are in view
				for (int j = 0; j < 2; j++) {
					Coords evCoords = view
							.getCoordsForView(conic.getEigenvec3D(j));
					if (!DoubleUtil.isZero(evCoords.getZ())) { // check if in
																// view
						isVisible = false;
						return;
					}
				}
			}
			mx = M.getX() * view.getXscale() + view.getXZero();
			my = -M.getY() * view.getYscale() + view.getYZero();
			ellipse.setFrame(mx - radius, my - yradius, 2.0 * radius,
					2.0 * yradius);
		} else {
			// special case: really big circle
			// draw arc according to midpoint position
			// of the arc
			Coords M = view.getCoordsForView(conic.getMidpoint3D());
			if (!DoubleUtil.isZero(M.getZ())) { // check if in view
				isVisible = false;
				return;
			}
			// check if eigen vec are in view
			for (int j = 0; j < 2; j++) {
				Coords evCoords = view.getCoordsForView(conic.getEigenvec3D(j));
				if (!DoubleUtil.isZero(evCoords.getZ())) { // check if in view
					isVisible = false;
					return;
				}
			}
			mx = M.getX() * view.getXscale() + view.getXZero();
			my = -M.getY() * view.getYscale() + view.getYZero();

			angSt = Double.NaN;
			// left
			if (mx < 0.0) {
				// top
				if (my < 0.0) {
					angSt = -Math.acos(-mx / radius);
					angEnd = -Math.asin(-my / yradius);
					i = 0;
				}
				// bottom
				else if (my > view.getHeight()) {
					angSt = Math.asin((my - view.getHeight()) / yradius);
					angEnd = Math.acos(-mx / radius);
					i = 2;
				}
				// middle
				else {
					angSt = -Math.asin((view.getHeight() - my) / yradius);
					angEnd = Math.asin(my / yradius);
					i = 1;
				}
			}
			// right
			else if (mx > view.getWidth()) {
				// top
				if (my < 0.0) {
					angSt = Math.PI + Math.asin(-my / yradius);
					angEnd = Math.PI
							+ Math.acos((mx - view.getWidth()) / radius);
					i = 6;
				}
				// bottom
				else if (my > view.getHeight()) {
					angSt = Math.PI
							- Math.acos((mx - view.getWidth()) / radius);
					angEnd = Math.PI
							- Math.asin((my - view.getHeight()) / yradius);
					i = 4;
				}
				// middle
				else {
					angSt = Math.PI - Math.asin(my / yradius);
					angEnd = Math.PI
							+ Math.asin((view.getHeight() - my) / yradius);
					i = 5;
				}
			}
			// top middle
			else if (my < 0.0) {
				angSt = Math.PI + Math.acos(mx / radius);
				angEnd = 2 * Math.PI
						- Math.acos((view.getWidth() - mx) / radius);
				i = 7;
			}
			// bottom middle
			else if (my > view.getHeight()) {
				angSt = Math.acos((view.getWidth() - mx) / radius);
				angEnd = Math.PI - Math.acos(mx / radius);
				i = 3;
			}
			// center on screen
			else {
				// huge circle with center on screen: use screen rectangle
				// instead of circle for possible filling
				if (radius < BIG_RADIUS || yradius < BIG_RADIUS) {
					updateEllipse();
					return;
				}
				fillShape = circle = AwtFactory.getPrototype().newRectangle(-1, -1,
						view.getWidth() + 2, view.getHeight() + 2);

				arcFiller = null;
				xLabel = -100;
				yLabel = -100;
				return;
			}

			if (Double.isNaN(angSt) || Double.isNaN(angEnd)) {
				// to ensure drawing ...
				angSt = 0.0d;
				angEnd = 2 * Math.PI;
				arcFiller = null;
				fullAngle = true;
			}
			// set arc
			circle = arc;
			arc.setArc(mx - radius, my - yradius, 2.0 * radius, 2.0 * yradius,
					Math.toDegrees(angSt), Math.toDegrees(angEnd - angSt),
					GArc2D.OPEN);
			// set general path for filling the arc to screen borders
			if (conic.isFilled() && !fullAngle) {
				if (gp == null) {
					gp = new GeneralPathClipped(view);
				}
				gp.resetWithThickness(geo.getLineThickness());
				GPoint2D sp = arc.getStartPoint();
				GPoint2D ep = arc.getEndPoint();
				if (!conic.isInverseFill()) {
					getArcFillerGP(sp, ep, i);
				} else {
					getInverseArcFillerGP(sp, ep, i);
				}
				// gp.
				arcFiller = gp;
			}
		}
		fillShape = circle;
		// set label position
		xLabel = (int) (mx - radius / 2.0);
		yLabel = (int) (my - yradius * 0.85) + 20;
	}

	private void getArcFillerGP(GPoint2D sp, GPoint2D ep, int i) {
		switch (i) { // case number
		case 0: // left top
			gp.moveTo(0, 0);
			gp.lineTo(sp.getX(), sp.getY());
			gp.lineTo(ep.getX(), ep.getY());
			break;

		case 1: // left middle
			gp.moveTo(0, view.getHeight());
			gp.lineTo(sp.getX(), sp.getY());
			gp.lineTo(ep.getX(), ep.getY());
			gp.lineTo(0, 0);
			break;

		case 2: // left bottom
			gp.moveTo(0, view.getHeight());
			gp.lineTo(sp.getX(), sp.getY());
			gp.lineTo(ep.getX(), ep.getY());
			break;

		case 3: // middle bottom
			gp.moveTo(view.getWidth(), view.getHeight());
			gp.lineTo(sp.getX(), sp.getY());
			gp.lineTo(ep.getX(), ep.getY());
			gp.lineTo(0, view.getHeight());
			break;

		case 4: // right bottom
			gp.moveTo(view.getWidth(), view.getHeight());
			gp.lineTo(sp.getX(), sp.getY());
			gp.lineTo(ep.getX(), ep.getY());
			break;

		case 5: // right middle
			gp.moveTo(view.getWidth(), 0);
			gp.lineTo(sp.getX(), sp.getY());
			gp.lineTo(ep.getX(), ep.getY());
			gp.lineTo(view.getWidth(), view.getHeight());
			break;

		case 6: // right top
			gp.moveTo(view.getWidth(), 0);
			gp.lineTo(sp.getX(), sp.getY());
			gp.lineTo(ep.getX(), ep.getY());
			break;

		case 7: // top middle
			gp.moveTo(0, 0);
			gp.lineTo(sp.getX(), sp.getY());
			gp.lineTo(ep.getX(), ep.getY());
			gp.lineTo(view.getWidth(), 0);
			break;

		default:
			gp = null;
		}
	}

	private void getInverseArcFillerGP(GPoint2D sp, GPoint2D ep, int i) {
		switch (i) { // case number
		case 0: // left top
			gp.moveTo(view.getWidth(), view.getHeight());
			gp.lineTo(view.getWidth(), 0);
			gp.lineTo(ep.getX(), ep.getY());
			gp.lineTo(sp.getX(), sp.getY());
			gp.lineTo(sp.getX(), view.getHeight());
			break;

		case 1: // left middle
			getArcFillerGP(ep, sp, 5);
			break;

		case 2: // left bottom
			gp.moveTo(view.getWidth(), 0);
			gp.lineTo(view.getWidth(), view.getHeight());
			gp.lineTo(sp.getX(), sp.getY());
			gp.lineTo(ep.getX(), ep.getY());
			gp.lineTo(ep.getX(), 0);
			break;

		case 3: // middle bottom
			this.getArcFillerGP(ep, sp, 7);
			break;

		case 4: // right bottom
			gp.moveTo(0, 0);
			gp.lineTo(0, view.getHeight());
			gp.lineTo(ep.getX(), ep.getY());
			gp.lineTo(sp.getX(), sp.getY());
			gp.lineTo(sp.getX(), 0);
			break;

		case 5: // right middle
			getArcFillerGP(ep, sp, 1);
			break;

		case 6: // right top
			gp.moveTo(0, view.getHeight());
			gp.lineTo(0, 0);
			gp.lineTo(sp.getX(), sp.getY());
			gp.lineTo(ep.getX(), ep.getY());
			gp.lineTo(ep.getX(), view.getHeight());
			break;

		case 7: // top middle
			this.getArcFillerGP(ep, sp, 3);
			break;

		default:
			gp = null;
		}
	}

	/**
	 * Update in case this draws an ellipse
	 */
	protected void updateEllipse() {
		setShape(null);
		// check for huge pixel radius
		double xRadius = halfAxes[0] * view.getXscale();
		double yRadius = halfAxes[1] * view.getYscale();
		if (xRadius > DrawConic.HUGE_RADIUS
				|| yRadius > DrawConic.HUGE_RADIUS) {
			isVisible = false;
			return;
		}

		// check if in view
		Coords M;
		if (isPreview) { // midpoint has been calculated in view coords
			M = conic.getMidpoint3D().getInhomCoords();
		} else {
			M = view.getCoordsForView(conic.getMidpoint3D());
			if (!DoubleUtil.isZero(M.getZ())) { // check if in view
				isVisible = false;
				return;
			}
		}

		if (ev == null) {
			ev = new Coords[2];
		}
		if (isPreview) { // calculations were in view coords
			for (int j = 0; j < 2; j++) {
				ev[j] = conic.getEigenvec(j);
			}
		} else {
			for (int j = 0; j < 2; j++) {
				ev[j] = view.getCoordsForView(conic.getEigenvec3D(j));
				if (!DoubleUtil.isZero(ev[j].getZ())) { // check if in view
					isVisible = false;
					return;
				}
			}
		}

		if (firstEllipse) {
			firstEllipse = false;
			if (ellipse == null) {
				ellipse = AwtFactory.getPrototype().newEllipse2DDouble();
			}
		}

		// set transform
		transform.setTransform(view.getCoordTransform());
		transform.concatenate(view.getCompanion().getTransform(conic, M, ev));

		// set ellipse
		ellipse.setFrameFromCenter(0, 0, halfAxes[0], halfAxes[1]);

		// BIG RADIUS: larger than screen diagonal
		int BIG_RADIUS = view.getWidth() + view.getHeight(); // > view's
																// diagonal
		if (xRadius < BIG_RADIUS && yRadius < BIG_RADIUS) {
			fillShape = transform.createTransformedShape(ellipse);
		} else {
			// clip big arc at screen
			// shape=ClipShape.clipToRect(shape,ellipse, transform, new
			// Rectangle(-1,
			// -1, view.getWidth() + 2, view.getHeight() + 2));
			fillShape = ClipShape.clipToRect(ellipse, transform, -1, -1,
					view.getWidth() + 2, view.getHeight() + 2);

		}
		// set label coords
		labelCoords[0] = -halfAxes[0] / 2.0d;
		labelCoords[1] = halfAxes[1] * 0.85d - 20.0 / view.getYscale();
		transform.transform(labelCoords, 0, labelCoords, 0, 1);
		xLabel = (int) labelCoords[0];
		yLabel = (int) labelCoords[1];
	}

	/**
	 * draw only one edge for the hyperbola section
	 */
	protected void updateHyperbolaEdge() {
		// only used in DrawConicSection
		isVisible = false;
	}

	/**
	 * Update method for hyperbolas
	 */
	protected void updateHyperbola() {
		// check if in view
		Coords M;
		if (isPreview) { // midpoint has been calculated in view coords
			M = conic.getMidpoint3D().getInhomCoords();
		} else {
			M = view.getCoordsForView(conic.getMidpoint3D());
			if (!DoubleUtil.isZero(M.getZ())) { // check if in view
				isVisible = false;
				return;
			}
		}
		if (ev == null) {
			ev = new Coords[2];
		}
		if (isPreview) { // calculations were in view coords
			for (int j = 0; j < 2; j++) {
				ev[j] = conic.getEigenvec(j);
			}
		} else {
			for (int j = 0; j < 2; j++) {
				ev[j] = view.getCoordsForView(conic.getEigenvec3D(j));
				if (!DoubleUtil.isZero(ev[j].getZ())) { // check if in view
					isVisible = false;
					return;
				}
			}
		}

		updateHyperbolaResetPaths();

		a = halfAxes[0];
		b = halfAxes[1];

		updateHyperbolaX0();

		// init step width
		if (x0 <= a) { // hyperbola is not visible on screen
			isVisible = false;
			return;
		}

		// set number of plot points according to size of x0
		// add ten points per screen width
		int n = PLOT_POINTS
				+ (int) (Math.abs(x0 - a) / (view.getXmax() - view.getXmin()))
						* 10;
		// n < 0 might result from huge real
		if (points != n && n > 0) {
			points = Math.min(n, MAX_PLOT_POINTS);
		}

		// hyperbola is visible on screen
		double step = Math.sqrt((x0 - a) / (x0 + a)) / (points - 1);

		// build Polyline of parametric hyperbola
		// hyp(t) = 1/(1-t^2) {a(1+t^2), 2bt}, 0 <= t < 1
		// this represents the first quadrant's wing of a hypberola
		/*
		 * hypRight.addPoint(points - 1, a, 0); hypLeft.addPoint(points - 1, -a,
		 * 0);
		 */
		updateHyperbolaAddPoint(points - 1, a, 0);

		double t = step;
		int i = 1;
		int index0 = points; // points ... 2*points - 2
		int index1 = points - 2; // points-2 ... 0
		while (index1 >= 0) {
			double tsq = t * t;
			double denom = 1.0 - tsq;
			// calc coords of first quadrant
			double x = (a * (1.0 + tsq) / denom);
			y = (2.0 * b * t / denom);

			// first and second quadrants
			updateHyperbolaAddPoint(index0, x, y);
			// third and fourth quadrants
			updateHyperbolaAddPoint(index1, x, -y);

			/*
			 * // first quadrant hypRight.addPoint(index0, x, y); // second
			 * quadrant hypLeft.addPoint(index0, -x, y); // third quadrant
			 * hypLeft.addPoint(index1, -x, -y); // fourth quadrant
			 * hypRight.addPoint(index1, x, -y);
			 */

			index0++;
			index1--;
			i++;
			t = i * step;
		}

		updateHyperbolaClosePaths();

		// set transform for Graphics2D
		transform.setTransform(view.getCoordTransform());
		transform.concatenate(view.getCompanion().getTransform(conic, M, ev));

		updateHyperboalSetTransformToPaths();

		updateHyperbolaLabelCoords();
		transform.transform(labelCoords, 0, labelCoords, 0, 1);
		xLabel = (int) labelCoords[0];
		yLabel = (int) labelCoords[1];

		updateHyperbolaSetShape();
	}

	/** set label coords */
	protected void updateHyperbolaLabelCoords() {
		labelCoords[0] = 2.0 * a;
		// point on curve: y = b * sqrt(3) minus 20 pixels
		labelCoords[1] = b * 1.7 - 20.0 / view.getYscale();
	}

	/**
	 * reset paths for hyperbola
	 */
	protected void updateHyperbolaResetPaths() {
		if (firstHyperbola) {
			firstHyperbola = false;
			points = PLOT_POINTS;
			hypRight = new GeneralPathClipped(view); // right wing
			hypLeft = new GeneralPathClipped(view); // left wing
		}
		hypRight.resetWithThickness(geo.getLineThickness());
		hypLeft.resetWithThickness(geo.getLineThickness());
	}

	/**
	 * updates hyperbola x maximum value
	 */
	protected void updateHyperbolaX0() {
		// draw hyperbola wing from x=a to x=x0
		// the drawn hyperbola must be larger than the screen
		// get max distance from midpoint to screen edge
		x0 = Math.max(
				Math.max(Math.abs(midpoint.getX() - view.getXmin()),
						Math.abs(midpoint.getX() - view.getXmax())),
				Math.max(Math.abs(midpoint.getY() - view.getYmin()),
						Math.abs(midpoint.getY() - view.getYmax())));
		// ensure that rotated hyperbola is fully on screen:
		x0 *= 1.5;
	}

	/**
	 * add point to paths for hyperbola
	 * 
	 * @param index
	 *            index for the point
	 * @param x1
	 *            x coord
	 * @param y1
	 *            y coord
	 */
	protected void updateHyperbolaAddPoint(int index, double x1, double y1) {
		hypRight.addPoint(index, x1, y1);
		hypLeft.addPoint(index, -x1, y1);
	}

	/** build general paths of hyperbola wings and transform them */
	protected void updateHyperboalSetTransformToPaths() {
		hypLeft.transform(transform);
		hypRight.transform(transform);
	}

	/**
	 * close hyperbola branchs
	 */
	protected void updateHyperbolaClosePaths() {

		// we have drawn the hyperbola from x=a to x=x0
		// ensure correct filling by adding points at (2*x0, y)
		if (conic.isFilled()) {
			hypRight.lineTo(Float.MAX_VALUE, y);
			hypRight.lineTo(Float.MAX_VALUE, -y);
			hypLeft.lineTo(-Float.MAX_VALUE, y);
			hypLeft.lineTo(-Float.MAX_VALUE, -y);
		}
	}

	/**
	 * set shape for hyperbola
	 */
	protected void updateHyperbolaSetShape() {
		try {
			setShape(AwtFactory.getPrototype().newArea(hypLeft));
			// geogebra.awt.Area.getAWTArea(super.getShape()).add(new
			// Area(geogebra.awt.GenericShape.getAwtShape(hypRight)));
			super.getShape().add(AwtFactory.getPrototype().newArea(hypRight));
		} catch (Exception e) {
			setShape(null);
			Log.error("problem in updateHyperbolaSetShape: " + e.getMessage());
		}
	}

	/**
	 * draw only one edge for the parabola section
	 */
	protected void updateParabolaEdge() {
		// only used for conic section
		isVisible = false;
	}

	/**
	 * Update method for parabolas
	 */
	protected void updateParabola() {
		if (conic.p > DrawConic.HUGE_RADIUS) {
			isVisible = false;
			return;
		}

		// check if in view
		Coords M;
		if (isPreview) { // midpoint has been calculated in view coords
			M = conic.getMidpoint3D().getInhomCoords();
		} else {
			M = view.getCoordsForView(conic.getMidpoint3D());
			if (!DoubleUtil.isZero(M.getZ())) { // check if in view
				isVisible = false;
				return;
			}
		}
		if (ev == null) {
			ev = new Coords[2];
		}
		if (isPreview) { // calculations were in view coords
			for (int j = 0; j < 2; j++) {
				ev[j] = conic.getEigenvec(j);
			}
		} else {
			for (int j = 0; j < 2; j++) {
				ev[j] = view.getCoordsForView(conic.getEigenvec3D(j));
				if (!DoubleUtil.isZero(ev[j].getZ())) { // check if in view
					isVisible = false;
					return;
				}
			}
		}

		if (firstParabola) {
			firstParabola = false;
			parabola = AwtFactory.getPrototype().newGeneralPath();
		}
		GAffineTransform conicTransform = view.getCompanion()
				.getTransform(conic, M, ev);
		updateParabolaX0Y0(conicTransform);

		// set transform
		transform.setTransform(view.getCoordTransform());
		transform.concatenate(conicTransform);

		// setCurve(P0, P1, P2)
		// parabola.setCurve(x0, y0, -x0, 0.0, x0, -y0);
		// shape = transform.createTransformedShape(parabola);
		parpoints[0] = x0;
		parpoints[1] = y0;

		parpoints[2] = -x0 / 3;
		parpoints[3] = y0 / 3;

		parpoints[4] = -x0 / 3;
		parpoints[5] = -y0 / 3;

		parpoints[6] = x0;
		parpoints[7] = -y0;
		transform.transform(parpoints, 0, parpoints, 0, 4);

		updateParabolaPath();

		fillShape = parabola;

		updateParabolaLabelCoords();

		transform.transform(labelCoords, 0, labelCoords, 0, 1);
		xLabel = (int) labelCoords[0];
		yLabel = (int) labelCoords[1];
	}

	/**
	 * update label coords for parabola
	 */
	protected void updateParabolaLabelCoords() {
		// set label coords
		labelCoords[0] = 2 * conic.p;
		// y = 2p minus 20 pixels
		labelCoords[1] = labelCoords[0] - 20.0 / view.getYscale();

	}

	/**
	 * calc control points coords of parabola y^2 = 2 p x
	 * 
	 * @param conicTransform
	 *            transform from eigenvector CS to RW
	 */
	protected void updateParabolaX0Y0(GAffineTransform conicTransform) {
		GAffineTransform inverse = null;
		try {
			inverse = conicTransform.createInverse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (inverse == null) {
			return;
		}
		double[] corners = new double[10];
		inverse.transform(new double[] { vertex.getX(), vertex.getY(),
				view.getXmin(), view.getYmin(), view.getXmin(), view.getYmax(),
				view.getXmax(), view.getYmin(), view.getXmax(),
				view.getYmax() }, 0, corners, 0, 5);
		x0 = Math.max(Math.abs(corners[0] - corners[2]),
				Math.abs(corners[0] - corners[4]));
		x0 = Math.max(x0, Math.abs(corners[0] - corners[6]));
		x0 = Math.max(x0, Math.abs(corners[0] - corners[8]));

		y0 = Math.max(Math.abs(corners[1] - corners[3]),
				Math.abs(corners[1] - corners[5]));
		y0 = Math.max(y0, Math.abs(corners[1] - corners[7]));
		y0 = Math.max(y0, Math.abs(corners[1] - corners[9]));
		// we want to return either (y0^2/2/p,y0) or (x0,sqrt(2p*x0))
		double xForY0 = y0 * y0 / 2 / conic.p;
		if (x0 > xForY0) {
			x0 = xForY0;
			return;
		}
		x0 = 2 * x0 / conic.p;

		// avoid sqrt by choosing x = k*p with
		// i = 2*k is quadratic number
		// make parabola big enough: k*p >= 2*x0 -> 2*k >= 4*x0/p

		// changed these to doubles, see #654 y=x^2+100000x+1
		double i = 2;
		k2 = 4;

		do {
			i += 2;
			k2 = i * i;
		} while (k2 < x0);
		x0 = k2 / 2 * conic.p; // x = k*p
		y0 = i * conic.p; // y = sqrt(2k p^2) = i p
	}

	/**
	 * create path for parabola
	 */
	protected void updateParabolaPath() {
		parabola.reset();
		parabola.moveTo(parpoints[0], parpoints[1]);
		parabola.curveTo(parpoints[2], parpoints[3], parpoints[4], parpoints[5],
				parpoints[6], parpoints[7]);
	}

	@Override
	final public void draw(GGraphics2D g2) {

		if (!isVisible) {
			return;
		}
		g2.setColor(getObjectColor());
		switch (type) {
		case GeoConicNDConstants.CONIC_SINGLE_POINT:
			int pointType;
			if ((conic == geo && conic.isInverseFill())
					|| geo.isInverseFill() != conic.isInverseFill()) {
				fill(g2, getShape());
				pointType = EuclidianStyleConstants.POINT_STYLE_CIRCLE;
			} else {
				pointType = EuclidianStyleConstants.POINT_STYLE_DOT;
			}
			if (!ignoreSingularities) {
				drawPoint.setPointStyle(pointType);
				drawPoint.draw(g2);
			}
			break;

		case GeoConicNDConstants.CONIC_INTERSECTING_LINES:
		case GeoConicNDConstants.CONIC_DOUBLE_LINE:
		case GeoConicNDConstants.CONIC_PARALLEL_LINES:
			drawLines(g2);
			break;

		case GeoConicNDConstants.CONIC_LINE:
			drawLines[0].draw(g2);
			break;
		default:
		case GeoConicNDConstants.CONIC_EMPTY:
			if (conic.isInverseFill()) {
				fill(g2, getShape());
			}
			break;
		case GeoConicNDConstants.CONIC_CIRCLE:
		case GeoConicNDConstants.CONIC_ELLIPSE:
		case GeoConicNDConstants.CONIC_PARABOLA:

			fillEllipseParabola(g2);

			if (isHighlighted()) {
				g2.setStroke(selStroke);
				g2.setColor(geo.getSelColor());
				g2.draw(fillShape);
			}

			g2.setStroke(objStroke);
			g2.setColor(getObjectColor());
			if (geo.getLineThickness() > 0) {
				g2.draw(fillShape);
			}
			if (labelVisible) {
				g2.setFont(view.getFontConic());
				g2.setColor(geo.getLabelColor());
				drawLabel(g2);
			}
			break;

		case GeoConicNDConstants.CONIC_HYPERBOLA:
			drawHyperbola(g2);
			break;
		}
	}

	private void fillEllipseParabola(GGraphics2D g2) {
		if (conic.isInverseFill()) {
			fill(g2, getShape());
		} else {
			fill(g2, fillShape); // fill using default/hatching/image as
								// appropriate
		}
		if (arcFiller != null && !conic.isInverseFill()) {
			fill(g2, arcFiller); // fill using default/hatching/image
									// as appropriate
		}
	}

	/**
	 * draw lines
	 * 
	 * @param g2
	 *            graphic context
	 */
	protected void drawLines(GGraphics2D g2) {
		if (geo.getLineThickness() > 0) {
			drawLines[0].draw(g2);
			drawLines[1].draw(g2);
		}
		if (conic.isInverseFill()) {
			fill(g2, getShape());
		} else {
			fill(g2, fillShape == null ? getShape() : fillShape);
		}
	}

	/**
	 * draw hyperbola
	 * 
	 * @param g2
	 *            graphic context
	 */
	protected void drawHyperbola(GGraphics2D g2) {
		fillHyperbola(g2);

		if (isHighlighted()) {
			g2.setStroke(selStroke);
			g2.setColor(geo.getSelColor());

			if (hypLeftOnScreen) {
				g2.draw(hypLeft);
			}
			if (hypRightOnScreen) {
				g2.draw(hypRight);
			}
		}
		g2.setStroke(objStroke);
		g2.setColor(getObjectColor());
		if (geo.getLineThickness() > 0) {
			if (hypLeftOnScreen) {
				g2.draw(hypLeft);
			}
			if (hypRightOnScreen) {
				g2.draw(hypRight);
			}
		}

		if (labelVisible) {
			g2.setFont(view.getFontConic());
			g2.setColor(geo.getLabelColor());
			drawLabel(g2);
		}
	}

	private void fillHyperbola(GGraphics2D g2) {
		if (conic.isInverseFill()) {
			GArea a1 = AwtFactory.getPrototype().newArea(hypLeft);
			GArea a2 = AwtFactory.getPrototype().newArea(hypRight);
			GArea complement = AwtFactory.getPrototype()
					.newArea(view.getBoundingPath());
			complement.subtract(a1);
			complement.subtract(a2);
			fill(g2, complement);
		} else {
			if (hypLeftOnScreen) {
				fill(g2, hypLeft);
			}
			if (hypRightOnScreen) {
				fill(g2, hypRight);
			}
		}
	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 * 
	 * @return null when this Drawable is infinite or undefined
	 */
	@Override
	final public GRectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}

		switch (type) {
		case GeoConicNDConstants.CONIC_SINGLE_POINT:
			return drawPoint == null ? null : drawPoint.getBounds();
		case GeoConicNDConstants.CONIC_CIRCLE:
		case GeoConicNDConstants.CONIC_ELLIPSE:
			// shape is null for 3D ellipse
			if (conic.isShape()) {
				return rectForRotatedEllipse();
			}
			return fillShape == null ? null : fillShape.getBounds();
		case GeoConicNDConstants.CONIC_PARABOLA:
		case GeoConicNDConstants.CONIC_HYPERBOLA:
			// might need another formula for flat hyperbolae, max() prevents
			// flattening of xx-yy=1000
			double focX = Math.max(
					Math.abs(conic.linearEccentricity
							* conic.eigenvec[0].getX()),
					Math.abs(conic.linearEccentricity
							* conic.eigenvec[0].getY()));
			return rectAroundMidpoint(focX, focX);
		default:
			return null;
		}
	}

	private GRectangle rectForRotatedEllipse() {
		double sin = conic.eigenvec[1].getX();
		double cos = conic.eigenvec[1].getY();

		double halfWidth = Math.hypot(conic.getHalfAxis(1)  * sin,
				conic.getHalfAxis(0)  * cos);
		double halfHeight = Math.hypot(conic.getHalfAxis(1)  * cos,
				conic.getHalfAxis(0)  * sin);
		return rectAroundMidpoint(halfWidth, halfHeight);
	}

	private GRectangle rectAroundMidpoint(double focX, double focY) {
		int xmin = view.toScreenCoordX(midpoint.getX() - focX);
		int xmax = view.toScreenCoordX(midpoint.getX() + focX);
		int ymin = view.toScreenCoordY(midpoint.getY() - focY);
		int ymax = view.toScreenCoordY(midpoint.getY() + focY);

		return getTempFrame(xmin, ymax, xmax - xmin, ymin - ymax);
	}

	@Override
	final public void drawTrace(GGraphics2D g2) {
		g2.setColor(getObjectColor());
		switch (type) {
		default:
			// do nothing
			break;
		case GeoConicNDConstants.CONIC_SINGLE_POINT:
			drawPoint.drawTrace(g2);
			break;

		case GeoConicNDConstants.CONIC_INTERSECTING_LINES:
		case GeoConicNDConstants.CONIC_DOUBLE_LINE:
		case GeoConicNDConstants.CONIC_PARALLEL_LINES:
			drawLines[0].drawTrace(g2);
			drawLines[1].drawTrace(g2);
			break;

		case GeoConicNDConstants.CONIC_LINE:
			drawLines[0].drawTrace(g2);
			break;

		case GeoConicNDConstants.CONIC_CIRCLE:
		case GeoConicNDConstants.CONIC_ELLIPSE:
		case GeoConicNDConstants.CONIC_PARABOLA:
			fillEllipseParabola(g2);
			g2.setStroke(objStroke);
			g2.setColor(getObjectColor());
			g2.draw(fillShape);
			break;

		case GeoConicNDConstants.CONIC_HYPERBOLA:
			fillHyperbola(g2);
			g2.setStroke(objStroke);
			g2.setColor(getObjectColor());
			g2.draw(hypLeft);
			g2.draw(hypRight);
			break;
		}
	}

	/**
	 * 
	 * @return true if it has to check it's on filling
	 */
	protected boolean checkIsOnFilling() {
		return geo.isFilled() && type != GeoConicNDConstants.CONIC_SINGLE_POINT
				&& type != GeoConicNDConstants.CONIC_DOUBLE_LINE;
	}

	@Override
	final public boolean hit(int hitX, int hitY, int hitThreshold) {
		if (!isVisible) {
			return false;
		}
		// set a flag that says if the point is on the filling
		boolean isOnFilling = false;
		if (checkIsOnFilling()) {
			double realX = view.toRealWorldCoordX(hitX);
			double realY = view.toRealWorldCoordY(hitY);
			double x3 = view.toRealWorldCoordX(3) - view.toRealWorldCoordX(0);
			double y3 = view.toRealWorldCoordY(3) - view.toRealWorldCoordY(0);
			int insideNeigbors = (conic.isInRegion(realX, realY) ? 1 : 0)
					+ (conic.isInRegion(realX - x3, realY - y3) ? 1 : 0)
					+ (conic.isInRegion(realX + x3, realY - y3) ? 1 : 0)
					+ (conic.isInRegion(realX - x3, realY + y3) ? 1 : 0)
					+ (conic.isInRegion(realX + x3, realY + y3) ? 1 : 0);
			if (conic.isInverseFill()) {
				isOnFilling = (insideNeigbors < 5);
			} else {
				isOnFilling = (insideNeigbors > 0);
			}
		}
		// set a flag to say if point is on the boundary
		boolean isOnBoundary = false;
		switch (type) {
		default:
			// do nothing
			break;
		case GeoConicNDConstants.CONIC_SINGLE_POINT:
			isOnBoundary = drawPoint.hit(hitX, hitY, hitThreshold);
			break;
		case GeoConicNDConstants.CONIC_INTERSECTING_LINES:
		case GeoConicNDConstants.CONIC_DOUBLE_LINE:
		case GeoConicNDConstants.CONIC_PARALLEL_LINES:
			isOnBoundary = hitLines(hitX, hitY, hitThreshold);
			break;
		case GeoConicNDConstants.CONIC_LINE:
			isOnBoundary = drawLines[0].hit(hitX, hitY, hitThreshold);
			break;
		case GeoConicNDConstants.CONIC_CIRCLE:
		case GeoConicNDConstants.CONIC_PARABOLA:
			if (objStroke.getLineWidth() > 0) {
				if (strokedShape == null) {
					// AND-547, initial buffer size
					try {
						// org.geogebra.ggbjdk.java.awt.geom.IllegalPathStateException:
						// org.geogebra.ggbjdk.java.awt.geom.Path2D$Double.needRoom
						// (Path2D.java:263)
						strokedShape = objStroke.createStrokedShape(fillShape, 100);
					} catch (Exception e) {
						Log.error("problem creating circle/parabola shape: "
								+ e.getMessage());
						return false;
					}
				}
				isOnBoundary = strokedShape.intersects(hitX - hitThreshold,
						hitY - hitThreshold, 2 * hitThreshold,
						2 * hitThreshold);
			}
			break;

		case GeoConicNDConstants.CONIC_ELLIPSE:
			isOnBoundary = hitEllipse(hitX, hitY, hitThreshold);
			break;

		case GeoConicNDConstants.CONIC_HYPERBOLA:
			isOnBoundary = hitHyperbola(hitX, hitY, hitThreshold);
			break;
		}

		// Application.debug("isOnFilling="+isOnFilling+"\nisOnBoundary="+isOnBoundary);
		if (isOnFilling) {
			if (isOnBoundary) {
				conic.setLastHitType(HitType.ON_BOUNDARY);
				return true;
			}
			conic.setLastHitType(HitType.ON_FILLING);
			return true;
		}
		if (isOnBoundary) {
			conic.setLastHitType(HitType.ON_BOUNDARY);
			return true;
		}
		conic.setLastHitType(HitType.NONE);
		return false;
	}

	/**
	 * Says if the coords hit lines
	 * 
	 * @param hitX
	 *            x coord for hit
	 * @param hitY
	 *            y coord for hit
	 * @param hitThreshold
	 *            acceptable distance from line
	 * @return true if lines are hit
	 */
	public boolean hitLines(int hitX, int hitY, int hitThreshold) {
		return drawLines[0].hit(hitX, hitY, hitThreshold)
				|| drawLines[1].hit(hitX, hitY, hitThreshold);
	}

	/**
	 * Says if the coords hit hyperbola
	 * 
	 * @param hitX
	 *            x coord for hit
	 * @param hitY
	 *            y coord for hit
	 * @return true if lines are hitted
	 * @param hitThreshold
	 *            acceptable distance from line
	 */
	public boolean hitHyperbola(int hitX, int hitY, int hitThreshold) {
		if (objStroke.getLineWidth() <= 0) {
			return false;
		}
		if (strokedShape == null) {
			try {
				// AND-547, initial buffer size
				strokedShape = objStroke.createStrokedShape(hypLeft, 300);
				strokedShape2 = objStroke.createStrokedShape(hypRight, 300);
			} catch (Exception e) {
				Log.error(
						"problem creating hyperbola shape: " + e.getMessage());
				return false;
			}
		}
		return strokedShape.intersects(hitX - hitThreshold, hitY - hitThreshold,
				2 * hitThreshold, 2 * hitThreshold)
				|| strokedShape2.intersects(hitX - hitThreshold,
						hitY - hitThreshold, 2 * hitThreshold,
						2 * hitThreshold);
	}

	/**
	 * Says if the coords hit ellipse
	 * 
	 * @param hitX
	 *            x coord for hit
	 * @param hitY
	 *            y coord for hit
	 * @return true if lines are hitted
	 * @param hitThreshold
	 *            acceptable distance from line
	 */
	public boolean hitEllipse(int hitX, int hitY, int hitThreshold) {
		if (objStroke.getLineWidth() <= 0) {
			return false;
		}
		if (strokedShape == null) {
			// AND-547, initial buffer size
			try {
				strokedShape = objStroke.createStrokedShape(fillShape, 148);
			} catch (Exception e) {
				Log.error("problem creating ellipse shape: " + e.getMessage());
				return false;
			}
		}

		return strokedShape.intersects(hitX - hitThreshold,
				hitY - hitThreshold, 2 * hitThreshold, 2 * hitThreshold);
	}

	@Override
	final public boolean isInside(GRectangle rect) {
		switch (type) {
		case GeoConicNDConstants.CONIC_SINGLE_POINT:
			return drawPoint.isInside(rect);

		case GeoConicNDConstants.CONIC_CIRCLE:
		case GeoConicNDConstants.CONIC_ELLIPSE:
			return rect != null && fillShape != null
					&& rect.contains(fillShape.getBounds());
		}

		return false;
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		if (type == GeoConicNDConstants.CONIC_SINGLE_POINT) {
			return drawPoint.intersectsRectangle(rect);
		}
		if (type == GeoConicNDConstants.CONIC_DOUBLE_LINE) {
			return drawLines[0].intersectsRectangle(rect)
					|| drawLines[1].intersectsRectangle(rect);
		}
		if (geo.isFilled()) {
			return super.intersectsRectangle(rect);
		}
		if (fillShape != null) {
			return fillShape.intersects(rect) && !fillShape.contains(rect);
		}
		if (super.getShape() != null) {
			return super.getShape().intersects(rect)
					&& !super.getShape().contains(rect);
		}
		return false;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
		if (drawLines != null) {
			for (int i = 0; i < 2 && drawLines[i] != null; i++) {
				drawLines[i].setGeoElement(geo);
			}
		}
	}

	private void initPreview() {
		// init the conic for preview
		Construction cons = previewTempPoints[0].getConstruction();
		isPreview = true;

		switch (previewMode) {
		case EuclidianConstants.MODE_CIRCLE_TWO_POINTS:
			AlgoCircleTwoPoints algo = new AlgoCircleTwoPoints(cons,
					previewTempPoints[0], previewTempPoints[1]);
			cons.removeFromConstructionList(algo);
			initConic(algo.getCircle());
			break;

		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS:
			Coords p = view
					.getCoordsForView(prevPoints.get(0).getInhomCoordsInD3());
			previewTempPoints[0].setCoords(p.projectInfDim(), false);

			GeoNumberValue distance = new GeoNumeric(cons,
					previewTempPoints[1].distance(previewTempPoints[0]));
			AlgoCirclePointRadius algoCircleRadius = new AlgoCirclePointRadius(
					cons, previewTempPoints[0], distance);
			cons.removeFromConstructionList(algoCircleRadius);
			initConic(algoCircleRadius.getCircle());
			break;

		case EuclidianConstants.MODE_CONIC_FIVE_POINTS:
			GeoPoint[] pts = { previewTempPoints[0], previewTempPoints[1],
					previewTempPoints[2], previewTempPoints[3],
					previewTempPoints[4] };
			AlgoConicFivePoints algo0 = new AlgoConicFivePoints(cons, pts);
			cons.removeFromConstructionList(algo0);
			initConic(algo0.getConic());
			break;

		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
			AlgoCircleThreePoints algo2 = new AlgoCircleThreePoints(cons,
					previewTempPoints[0], previewTempPoints[1],
					previewTempPoints[2]);
			cons.removeFromConstructionList(algo2);
			initConic(algo2.getCircle());
			break;

		case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
			AlgoEllipseHyperbolaFociPoint algo3 = new AlgoEllipseHyperbolaFociPoint(
					cons, previewTempPoints[0], previewTempPoints[1],
					previewTempPoints[2], GeoConicNDConstants.CONIC_ELLIPSE);
			cons.removeFromConstructionList(algo3);
			initConic(algo3.getConic());
			break;

		case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
			AlgoEllipseHyperbolaFociPoint algo4 = new AlgoEllipseHyperbolaFociPoint(
					cons, previewTempPoints[0], previewTempPoints[1],
					previewTempPoints[2], GeoConicNDConstants.CONIC_HYPERBOLA);
			cons.removeFromConstructionList(algo4);
			initConic(algo4.getConic());
			break;

		case EuclidianConstants.MODE_COMPASSES:
			AlgoCirclePointRadius algo5 = new AlgoCirclePointRadius(cons,
					previewTempPoints[0], previewTempRadius);
			cons.removeFromConstructionList(algo5);
			initConic(algo5.getCircle());
			break;

		case EuclidianConstants.MODE_PARABOLA:
			AlgoParabolaPointLine algo6 = new AlgoParabolaPointLine(cons,
					previewTempPoints[0], previewTempLine);
			cons.removeFromConstructionList(algo6);
			initConic(algo6.getParabola());
			break;

		default:
			Log.debug("unknown conic type");
		}
		if (conic != null) {
			conic.setLabelVisible(false);
		}
	}

	// preview of circle with midpoint through a second point
	@Override
	final public void updatePreview() {

		switch (previewMode) {
		case EuclidianConstants.MODE_COMPASSES:
			// compass: set radius of preview circle
			// two points or one segment selected to define radius
			isVisible = conic != null && (prevPoints.size() == 2
					|| prevSegments.size() == 1 || prevConics.size() == 1);
			if (isVisible) {
				if (prevPoints.size() == 2) {
					GeoPointND p1 = prevPoints.get(0);
					GeoPointND p2 = prevPoints.get(1);
					previewTempRadius.setValue(p1.distance(p2));
				} else if (prevSegments.size() == 1) {
					GeoSegmentND seg = prevSegments.get(0);
					previewTempRadius.setValue(seg.getLength());
				} else if (prevConics.size() == 1) {
					GeoConicND prevCircle = prevConics.get(0);
					previewTempRadius.setValue(prevCircle.getCircleRadius());
				}
				previewTempRadius.updateCascade();
			}
			break;

		case EuclidianConstants.MODE_PARABOLA:

			isVisible = prevLines.size() == 1;

			if (prevLines.size() > 0 && previewTempLine instanceof GeoLine) {
				GeoLineND lND = prevLines.get(0);
				Coords equation = lND
						.getCartesianEquationVector(view.getMatrix());
				if (equation != null) {
					((GeoLine) previewTempLine).setCoords(equation.getX(),
						equation.getY(),
						equation.getZ());
				}
			}
			if (prevPoints.size() > 0) {
				Coords p = view.getCoordsForView(
						prevPoints.get(0).getInhomCoordsInD3());
				// Application.debug("p["+i+"]=\n"+p);
				previewTempPoints[0].setCoords(p.projectInfDim(), true);

				previewTempPoints[0].updateCascade();
			}

			break;

		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS:
			isVisible = conic != null && prevPoints.size() == neededPrevPoints;
			if (isVisible) {
				Coords p = view.getCoordsForView(
						prevPoints.get(0).getInhomCoordsInD3());
				previewTempPoints[0].setCoords(p.projectInfDim(), false);

				Construction cons = previewTempPoints[0].getConstruction();
				GeoNumberValue distance = new GeoNumeric(cons,
						previewTempPoints[1].distance(previewTempPoints[0]));
				AlgoCirclePointRadius algoCircleRadius = new AlgoCirclePointRadius(
						cons, previewTempPoints[0], distance);
				cons.removeFromConstructionList(algoCircleRadius);
				initConic(algoCircleRadius.getCircle());
				this.conic.updateCascade();
			}
			break;

		default:
			// all other conic preview modes: use points to define preview conic
			isVisible = conic != null && prevPoints.size() == neededPrevPoints;
			if (isVisible) {
				for (int i = 0; i < prevPoints.size(); i++) {
					Coords p = view.getCoordsForView(
							prevPoints.get(i).getInhomCoordsInD3());
					previewTempPoints[i].setCoords(p.projectInfDim(), false);
				}
				previewTempPoints[0].updateCascade();
			}
		}
	}

	@Override
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

	@Override
	final public void drawPreview(GGraphics2D g2) {
		draw(g2);
	}

	@Override
	public void disposePreview() {
		if (conic != null) {
			conic.remove();
		}
	}

	/**
	 * Returns the conic to be draw (might not be equal to geo, if this is part
	 * of bigger geo)
	 * 
	 * @return conic
	 */
	public GeoConicND getConic() {
		return conic;
	}

	/**
	 * @param ignore
	 *            to avoid drawing single point if part of ineq
	 */
	public void setIgnoreSingularities(boolean ignore) {
		this.ignoreSingularities = ignore;
	}

	/**
	 * resizing by drag of side handler for rotated ellipses
	 */
	private void stretchEllipse(GPoint2D p0, GPoint2D p, GPoint2D tangent) {
		GRectangle bounds = getBounds();
		if (bounds == null) {
			return;
		}
		double ratioX = (p.getX() - p0.getX()) / bounds.getWidth();
		double ratioY = (p.getY() - p0.getY()) / bounds.getHeight();
		boolean originalTangentIncreaseScreen = Math.abs(tangent.getY() - p.getY()) > Math
				.abs(p0.getY() - tangent.getY());
		boolean boxOrientationChanged = ratioX * ratioY < 0;
		if (ratioX != 0 && ratioY != 0) {
			applyStretch(0, 1, Math.abs(ratioX));
			applyStretch(1, 0, Math.abs(ratioY));
			updateDiameter();
			boolean tangentIncreaseScreen = diameter.getX() * diameter.getY() < 0;
			if (originalTangentIncreaseScreen ^ boxOrientationChanged != tangentIncreaseScreen) {
				applyStretch(1, 0, -1);
			}
			double centerX = view.toRealWorldCoordX((p.getX() + p0.getX()) / 2);
			double centerY = view.toRealWorldCoordY((p.getY() + p0.getY()) / 2);
			Coords corner = new Coords(centerX - conic.getMidpoint().getX(),
					centerY - conic.getMidpoint().getY(), 0);
			conic.translate(corner);
		}
	}

	private void applyStretch(double cos, double sin, double factor) {
		Coords corner = new Coords(conic.getMidpoint().getX(), conic.getMidpoint().getY(), 0);
		conic.translate(corner);
		AlgoShearOrStretch.stretch(conic, cos, sin, factor);
		corner.mulInside(-1);
		conic.translate(corner);
	}

	@Override
	public void fromPoints(ArrayList<GPoint2D> pts) {
		if (conic.getRotation() != 0) {
			stretchEllipse(pts.get(0), pts.get(1), pts.get(2));
		} else {
			updateRealGeo(pts);
		}
	}

	private void updateRealGeo(ArrayList<GPoint2D> pts) {
		double startX = view.toRealWorldCoordX(pts.get(0).getX());
		double startY = view.toRealWorldCoordY(pts.get(0).getY());
		double endX = view.toRealWorldCoordX(pts.get(1).getX());
		double endY = view.toRealWorldCoordY(pts.get(1).getY());
		double[] equ = getEquationOfConic(startX, startY, endX, endY);
		if (equ != null) {
			conic.setMatrix(equ);
		}
		conic.setSelected(true);
	}

	private static double[] getEquationOfConic(double startX, double startY, double endX,
			double endY) {
		if (Double.isNaN(startX) || Double.isNaN(startY) || Double.isNaN(endX) 
				|| Double.isNaN(endY)) {
			return null;
		}
		// coords of center
		double centerX = (startX + endX) / 2;
		double centerY = (startY + endY) / 2;
		// build equation (x-centerX)/b^2+(y-centerY)/a^2 = 1
		double overBsquared = 1 / Math.pow(centerX - endX, 2);
		double overAsquared = 1 / Math.pow(centerY - endY, 2);

		return new double[] { overBsquared, overAsquared,
				-1 + centerX * centerX * overBsquared + centerY * centerY * overAsquared, 0,
				-centerX * overBsquared, -centerY * overAsquared };
	}

	@Override
	protected List<GPoint2D> toPoints() {
		List<GPoint2D> ret = super.toPoints();
		updateDiameter();
		double tangentPointX = view.toRealWorldCoordX(ret.get(1).getX());
		double tangentPointY = diameter.value(tangentPointX);
		ret.add(new MyPoint(view.toScreenCoordXd(tangentPointX),
				view.toScreenCoordYd(tangentPointY)));
		return ret;
	}

	private void updateDiameter() {
		if (diameter == null) {
			diameter = new GeoLine(conic.getConstruction(), 0, 1, 1);
		}
		conic.diameterLine(0, 1, diameter);
	}
}

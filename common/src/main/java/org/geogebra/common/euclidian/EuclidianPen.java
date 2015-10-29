package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.linear.SingularValueDecomposition;
import org.apache.commons.math.linear.SingularValueDecompositionImpl;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoAttachCopyToView;
import org.geogebra.common.kernel.algos.AlgoCircleThreePoints;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoFocus;
import org.geogebra.common.kernel.algos.AlgoFunctionFreehand;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import org.geogebra.common.kernel.algos.AlgoPolyLine;
import org.geogebra.common.kernel.algos.AlgoPolygon;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.statistics.AlgoFitImplicit;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Handles pen and freehand tool
 *
 */
public class EuclidianPen {

	/**
	 * app
	 */
	protected App app;
	/**
	 * view
	 */
	protected EuclidianView view;

	/**
	 * minimum determinant for circles
	 * decrease to allow less "round" circles
	 */
	public double CIRCLE_MIN_DET = 0.95;

	/**
	 * increase to allow uglier circles
	 */
	public double CIRCLE_MAX_SCORE = 0.10;

	/**
	 * maximum deviation between the segment lengths
	 * increase to allow less beautiful rectangles
	 */
	public double RECTANGLE_LINEAR_TOLERANCE = 0.20;

	/**
	 * maximum deviation between the segment lengths
	 * increase to allow less beautiful polygons
	 */
	public double POLYGON_LINEAR_TOLERANCE = 0.20;

	/**
	 * maximum deviation between the angles of a rectangle
	 * increase to allow less beautiful rectangles
	 */
	public double RECTANGLE_ANGLE_TOLERANCE = 15 * Math.PI / 180;

	/**
	 * maximum determinant for lines (e.g. sides of a polygon)
	 * decrease to allow lines that are not so straight
	 */
	public double LINE_MAX_DET = 0.015;
	/** Polyline that conects stylebar to pen settings */
	public final GeoPolyLine DEFAULT_PEN_LINE;

	private AlgoElement lastAlgo = null;
	/** points created by pen */
	protected ArrayList<GPoint> penPoints = new ArrayList<GPoint>();
	private ArrayList<GPoint> temp = null;
	private int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
	private double score = 0;
	private double ARROW_MAXSIZE = 0.8; // max size of arrow tip relative to
										// main segment
	private double ARROW_ANGLE_MIN = (5 * Math.PI / 180); // arrow tip angles
															// relative to main
															// segment
	private double ARROW_ANGLE_MAX = (50 * Math.PI / 180);
	private double ARROW_ASYMMETRY_MAX_ANGLE = (30 * Math.PI / 180);
	private double ARROW_ASYMMETRY_MAX_LINEAR = 1.0; // size imbalance of two
														// legs of tip
	private double ARROW_TIP_LINEAR_TOLERANCE = 0.30; // gap tolerance on tip
														// segments
	private double ARROW_SIDEWAYS_GAP_TOLERANCE = 0.25; // gap tolerance in
														// lateral direction
	private double ARROW_MAIN_LINEAR_GAP_MIN = -0.3; // gap tolerance on main
														// segment
	private double ARROW_MAIN_LINEAR_GAP_MAX = 0.7; // gap tolerance on main
													// segment
	private int brk[];
	private int count = 0;
	private int recognizer_queue_length = 0;
	private int MAX_POLYGON_SIDES = 4;
	private double SLANT_TOLERANCE = 5 * Math.PI / 180;
	private Inertia a = null;
	private Inertia b = null;
	private Inertia c = null;
	private Inertia d = null;
	private RecoSegment reco_queue_a = new RecoSegment();
	private RecoSegment reco_queue_b = new RecoSegment();
	private RecoSegment reco_queue_c = new RecoSegment();
	private RecoSegment reco_queue_d = new RecoSegment();
	private RecoSegment reco_queue_e = new RecoSegment();
	/**
	 * String representation of slant movement.
	 */
	private static final String LEFT_UP = "Q";
	private static final String RIGHT_UP = "W";
	private static final String RIGHT_DOWN = "E";
	private static final String LEFT_DOWN = "T";
	/**
	 * String representation of left movement.
	 */
	private static final String LEFT_MOVE = "L";
	/**
	 * String representation of right movement.
	 */
	private static final String RIGHT_MOVE = "R";
	/**
	 * String representation of up movement.
	 */
	private static final String UP_MOVE = "U";
	/**
	 * String representation of down movement.
	 */
	private static final String DOWN_MOVE = "D";
	/**
	 * Grid size. Default is 30.
	 */
	private int gridSize = 15;
	private GPoint startPoint = null;
	/**
	 * String representation of gesture.
	 */
	private StringBuffer gesture = new StringBuffer();
	private int deltaX = 0;
	private int deltaY = 0;
	private int absDeltaX = 0;
	private int absDeltaY = 0;
	private float absTangent = 0;

	private final static int PEN_SIZE_FACTOR = 2;
	private static final double CONIC_AXIS_ERROR_RATIO = 10;

	private boolean startNewStroke = false;

	private int penSize;

	/**
	 * start point of the gesture
	 */
	protected GeoPoint initialPoint = null;
	/**
	 * delete initialPoint if no shape is found
	 */
	protected boolean deleteInitialPoint = false;

	/**
	 * @return pen size
	 */
	public int getPenSize() {
		return penSize;
	}

	/**
	 * @param penSize
	 *            pen size
	 */
	public void setPenSize(int penSize) {
		if (this.penSize != penSize) {
			startNewStroke = true;
		}
		this.penSize = penSize;
	}

	/**
	 * @return pen line style
	 */
	public int getPenLineStyle() {
		return penLineStyle;
	}

	/**
	 * @param penLineStyle
	 *            pen line style
	 */
	public void setPenLineStyle(int penLineStyle) {
		if (this.penLineStyle != penLineStyle) {
			startNewStroke = true;
		}
		this.penLineStyle = penLineStyle;
	}

	/**
	 * @return pen color
	 */
	public GColor getPenColor() {
		return penColor;
	}

	/**
	 * use one point as first point of the created shape
	 * 
	 * @param point
	 *            start point
	 * @param deletePoint
	 *            delete the point if no shape is found
	 */
	public void setInitialPoint(GeoPoint point, boolean deletePoint) {
		this.initialPoint = point;
		this.deleteInitialPoint = deletePoint;
	}

	private int eraserSize;
	private int penLineStyle;
	private GColor penColor;

	// being used for Freehand Function tool
	private boolean freehand = false;

	// being used for Freehand Shape tool (not done yet)
	// private boolean recognizeShapes = false;

	/************************************************
	 * Construct EuclidianPen
	 * 
	 * @param app
	 *            application
	 * @param view
	 *            view
	 */
	public EuclidianPen(App app, EuclidianView view) {
		this.view = view;
		this.app = app;

		setDefaults();

		DEFAULT_PEN_LINE = new GeoPolyLine(app.getKernel().getConstruction()) {
			@Override
			public void setObjColor(GColor color) {
				super.setObjColor(color);
				setPenColor(color);
			}

			@Override
			public void setLineThickness(int th) {
				super.setLineThickness(th);
				setPenSize(th);
			}

			@Override
			public void setLineType(int i) {
				super.setLineType(i);
				setPenLineStyle(i);
			}
		};
		DEFAULT_PEN_LINE.setObjColor(penColor);
		DEFAULT_PEN_LINE.setLineThickness(penSize);
	}

	// ===========================================
	// Getters/Setters
	// ===========================================

	/**
	 * Set default pen color, line style, thickness, eraser size
	 */
	public void setDefaults() {
		penSize = 3;
		eraserSize = EuclidianConstants.DEFAULT_ERASER_SIZE;
		penLineStyle = EuclidianStyleConstants.LINE_TYPE_FULL;
		penColor = GColor.BLACK;
		setAbsoluteScreenPosition(true);
	}

	/**
	 * 
	 * @param e
	 *            event
	 * @return Is this MouseEvent an erasing Event.
	 */
	public boolean isErasingEvent(AbstractEvent e) {
		return app.isRightClick(e) && !freehand;
	}

	/**
	 * Update the info about last geo so that we can continue a polyline
	 * 
	 * @param penGeo
	 *            last object created with pen
	 */
	public void setPenGeo(GeoElement penGeo) {

		if (penGeo == null) {
			lastAlgo = null;
		} else if (penGeo.getParentAlgorithm() instanceof AlgoPolyLine) {
			lastAlgo = penGeo.getParentAlgorithm();
		}
	}

	/**
	 * Make sure we start using a new polyline
	 */
	public void resetPenOffsets() {
		lastAlgo = null;
	}

	// ===========================================
	// Mouse Event Handlers
	// ===========================================

	/**
	 * Mouse dragged while in pen mode, decide whether erasing or new points.
	 * 
	 * @param e
	 *            mouse event
	 */
	public void handleMouseDraggedForPenMode(AbstractEvent e) {
		view.setTransparentCursor();
		if (isErasingEvent(e)) {
			view.getEuclidianController().getDeleteMode()
					.handleMouseDraggedForDelete(e, eraserSize, true);
			app.getKernel().notifyRepaint();
		} else {
			addPointPenMode(e, null);
		}
	}

	/**
	 * @param e
	 *            event
	 * @param hits
	 *            hits
	 */
	public void handleMousePressedForPenMode(AbstractEvent e, Hits hits) {
		if (!isErasingEvent(e)) {
			penPoints.clear();
			addPointPenMode(e, hits);
		}
	}

	/**
	 * add the saved points to the last stroke or create a new one
	 * 
	 * @param e
	 *            event
	 * @param h
	 *            hits
	 */
	public void addPointPenMode(AbstractEvent e, Hits h) {

		// if a PolyLine is selected, we can append to it.

		ArrayList<GeoElement> selGeos = app.getSelectionManager()
				.getSelectedGeos();

		if (selGeos.size() == 1 && selGeos.get(0) instanceof GeoPolyLine) {
			lastAlgo = selGeos.get(0).getParentAlgorithm();
		}

		view.setTransparentCursor();

		// if (g2D == null) g2D = penImage.createGraphics();

		GPoint newPoint = new GPoint(e.getX(), e.getY());
		GGraphics2D g2D = view.getGraphicsForPen();

		if (minX > e.getX())
			minX = e.getX();
		if (maxX < e.getX())
			maxX = e.getX();

		if (penPoints.size() == 0) {
			if(initialPoint != null){
				// also add the coordinates of the initialPoint to the penPoints
				Coords coords = initialPoint.getCoords();
				// calculate the screen coordinates
				int locationX = (int) (view.getXZero() + (coords.getX() / view.getInvXscale()));
				int locationY = (int) (view.getYZero() - (coords.getY() / view.getInvYscale()));

				GPoint p = new GPoint(locationX, locationY);
				penPoints.add(p);
				// draw a line between the initalPoint and the first point
				drawPenPreviewLine(g2D, newPoint, p);
			}
			penPoints.add(newPoint);
		}else {
			GPoint lastPoint = penPoints.get(penPoints.size() - 1);
			drawPenPreviewLine(g2D, newPoint, lastPoint);
			if (lastPoint.distance(newPoint) > 3)
				penPoints.add(newPoint);
		}
		GPoint point = e.getPoint();
		if (startPoint == null)
			startPoint = e.getPoint();
		deltaX = getDeltaX(startPoint, point);
		deltaY = getDeltaY(startPoint, point);
		absDeltaX = Math.abs(deltaX);
		absDeltaY = Math.abs(deltaY);
		absTangent = ((float) absDeltaX) / absDeltaY;
		if (!((absDeltaX < gridSize) && (absDeltaY < gridSize))) {
			if (absTangent < 0.5) {
				if (deltaY < 0)
					this.saveMove(UP_MOVE);
				else
					this.saveMove(DOWN_MOVE);
				startPoint = point;
			}
			if (absTangent >= 0.5 && absTangent <= 2) {
				if (deltaX > 0 && deltaY < 0)
					this.saveMove(LEFT_UP);
				if (deltaX < 0 && deltaY < 0)
					this.saveMove(RIGHT_UP);
				if (deltaX < 0 && deltaY > 0)
					this.saveMove(RIGHT_DOWN);
				if (deltaX > 0 && deltaY > 0)
					this.saveMove(LEFT_DOWN);
				startPoint = point;
			}
			if (absTangent > 2) {
				if (deltaX < 0)
					this.saveMove(LEFT_MOVE);
				else
					this.saveMove(RIGHT_MOVE);
				startPoint = point;
			}
		}
	}

	private static void drawPenPreviewLine(GGraphics2D g2D, GPoint point1,
			GPoint point2) {
		GLine2D line = org.geogebra.common.factories.AwtFactory.prototype
				.newLine2D();
		line.setLine(point1.getX(), point1.getY(), point2.getX(), point2.getY());
		g2D.setStroke(org.geogebra.common.factories.AwtFactory.prototype
				.newBasicStroke(12, GBasicStroke.CAP_ROUND,
						GBasicStroke.JOIN_ROUND));
		g2D.setColor(GColor.GRAY);
		g2D.fill(line);
		g2D.draw(line);
	}

	/**
	 * Clean up the pen mode stuff, add points.
	 * 
	 * @param right
	 *            true for right click
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * 
	 */
	public void handleMouseReleasedForPenMode(boolean right, int x, int y) {
		if (right && !freehand) {
			return;
		}

		if (freehand) {
			mouseReleasedFreehand(x, y);
			penPoints.clear();

			app.refreshViews(); // clear trace

			minX = Integer.MAX_VALUE;
			maxX = Integer.MIN_VALUE;

			return;
		}

		app.setDefaultCursor();

		// if (!erasing && recognizeShapes) {
		// checkShapes(e);
		// }

		// if (lastPenImage != null) penImage = lastPenImage.getImage();
		// //app.getExternalImage(lastPenImage);

		// Application.debug(penPoints.size()+"");

		addPointsToPolyLine(penPoints);

		penPoints.clear();
	}

	/**
	 * @param x
	 *            x-coord of new point
	 * @param y
	 *            y-coord of new point
	 * @return geo that fits current points + new point
	 */
	protected GeoElement checkShapes(int x, int y) {

		count = 0;
		App.debug(getGesture());
		this.clearTemporaryInfo();
		GPoint newPoint = new GPoint(x, y);
		penPoints.add(newPoint);
		// AbstractApplication.debug(penPoints);
		// if recognize_shape option is checked
		brk = new int[5];
		a = new Inertia();
		b = new Inertia();
		c = new Inertia();
		d = new Inertia();
		int j = 0;
		RecoSegment rs = null;
		Inertia ss = null;
		RecoSegment temp1 = null;
		// AbstractApplication.debug(penPoints);
		Inertia s = new Inertia();
		this.calc_inertia(0, penPoints.size() - 1, s);
		int n = this.findPolygonal(0, penPoints.size() - 1, MAX_POLYGON_SIDES,
				0, 0);
		// AbstractApplication.debug.println(n);
		if (n > 0) {
			this.optimize_polygonal(n);
			while (n + recognizer_queue_length > MAX_POLYGON_SIDES) {
				j = 1;
				temp1 = reco_queue_b;
				while (j < recognizer_queue_length && temp1.startpt != 0) {
					j++;
					if (j == 2)
						temp1 = reco_queue_c;
					if (j == 3)
						temp1 = reco_queue_d;
					if (j == 4)
						temp1 = reco_queue_e;
				}
				recognizer_queue_length = recognizer_queue_length - j;
				int te1 = 0;
				int te2 = j;
				RecoSegment t1 = null;
				RecoSegment t2 = null;
				for (int k = 0; k < recognizer_queue_length; ++k) {
					if (te1 == 0)
						t1 = reco_queue_a;
					if (te1 == 1)
						t1 = reco_queue_b;
					if (te1 == 2)
						t1 = reco_queue_c;
					if (te1 == 3)
						t1 = reco_queue_d;
					if (te1 == 4)
						t1 = reco_queue_e;
					if (te2 == 0)
						t2 = reco_queue_a;
					if (te2 == 1)
						t2 = reco_queue_b;
					if (te2 == 2)
						t2 = reco_queue_c;
					if (te2 == 3)
						t2 = reco_queue_d;
					if (te2 == 4)
						t2 = reco_queue_e;
					t1.startpt = t2.startpt;
					t1.endpt = t2.endpt;
					t1.xcenter = t2.xcenter;
					t1.ycenter = t2.ycenter;
					t1.angle = t2.angle;
					t1.radius = t2.radius;
					t1.x1 = t2.x1;
					t1.x2 = t2.x2;
					t1.y1 = t2.y2;
					t1.y2 = t2.y2;
					t1.reversed = t2.reversed;
					te1++;
					te2++;
				}
			}
			int temp_reco = recognizer_queue_length;
			recognizer_queue_length = recognizer_queue_length + n;
			for (j = 0; j < n; ++j) {
				if (temp_reco + j == 0)
					rs = reco_queue_a;
				if (temp_reco + j == 1)
					rs = reco_queue_b;
				if (temp_reco + j == 2)
					rs = reco_queue_c;
				if (temp_reco + j == 3)
					rs = reco_queue_d;
				if (temp_reco + j == 4)
					rs = reco_queue_e;
				if (j == 0)
					ss = a;
				if (j == 1)
					ss = b;
				if (j == 2)
					ss = c;
				if (j == 3)
					ss = d;
				rs.startpt = brk[j];
				rs.endpt = brk[j + 1];
				this.get_segment_geometry(brk[j], brk[j + 1], ss, rs);
			}

			GeoElement geo = try_rectangle();
			if (geo != null) {
				recognizer_queue_length = 0;
				App.debug("Rectangle Recognized");
				return geo;
			}
			geo = try_arrow();
			if (geo != null) {
				recognizer_queue_length = 0;
				App.debug("Arrow Recognized");
				return geo;
			}

			geo = try_closed_polygon(3);
			if (geo != null) {
				recognizer_queue_length = 0;
				App.debug("Triangle Recognized");
				return geo;
			}

			geo = try_closed_polygon(4);
			if (geo != null) {
				recognizer_queue_length = 0;
				App.debug("Quadrilateral Recognized");
				return geo;
			}

			if (n == 1)// then stroke is a line
			{
				App.debug("Current stroke is a line");
				if (Math.abs(rs.angle) < SLANT_TOLERANCE) {
					rs.angle = 0;
					rs.y1 = rs.y2 = rs.ycenter;
				}
				if (Math.abs(rs.angle) > Math.PI / 2 - SLANT_TOLERANCE) {
					rs.angle = (rs.angle > 0) ? (Math.PI / 2) : (-Math.PI / 2);
					rs.x1 = rs.x2 = rs.xcenter;
				}
				// line1=new Line2D();
				// System.out.println(penOffsetX);
				double x_first = view.toRealWorldCoordX(rs.x1);
				double y_first = view.toRealWorldCoordY(rs.y1);
				double x_last = view.toRealWorldCoordX(rs.x2);
				double y_last = view.toRealWorldCoordY(rs.y2);
				AlgoJoinPointsSegment algo = null;

				GeoPoint p;
				if (this.initialPoint != null) {
					p = initialPoint;
				} else {
					p = new GeoPoint(app.getKernel().getConstruction(), null,
							x_first, y_first, 1.0);
				}
				GeoPoint q = new GeoPoint(app.getKernel().getConstruction(),
						null, x_last, y_last, 1.0);
				algo = new AlgoJoinPointsSegment(app.getKernel()
						.getConstruction(), null, p, q);

				GeoElement line = algo.getGeoElements()[0];
				// line.setLineThickness(penSize * 2);
				// line.setLineType(penLineStyle);
				// line.setObjColor(penColor);
				line.updateRepaint();

				return line;
			}
		}
		if (EuclidianPen.I_det(s) > CIRCLE_MIN_DET) {
			score = this.score_circle(0, penPoints.size() - 1, s);
			if (score < CIRCLE_MAX_SCORE) {
				// create best fitting circle
				GeoConic circle = this.makeACircle(EuclidianPen.center_x(s),
						EuclidianPen.center_y(s), EuclidianPen.I_rad(s));

				// midpoint
				GeoPoint m = new GeoPoint(app.getKernel().getConstruction(), null, circle.getMidpoint().getX(),
						circle.getMidpoint().getY(), 1.0);

				// point on the circle
				GeoPoint p = circle.getPointsOnConic(1).get(0);
				p.setLabel(null);

				// delete the circle that was created in makeACircle
				circle.remove();

				// create a new circle with midpoint and point
				return app.getKernel().getAlgoDispatcher().Circle(null, m, p);
			}
		}

		if (this.deleteInitialPoint && this.initialPoint != null) {
			this.initialPoint.remove();
		}
		this.initialPoint = null;
		return makeAConic(); // might return null
	}

	private void addPointsToPolyLine(ArrayList<GPoint> penPoints2) {

		Construction cons = app.getKernel().getConstruction();
		// GeoList newPts;// = new GeoList(cons);
		GeoPoint[] newPts;// = new GeoList(cons);
		int offset;
		if (startNewStroke) {
			lastAlgo = null;
			startNewStroke = false;
		}
		if (lastAlgo == null) {
			// lastPolyLine = new GeoPolyLine(cons, "hello");
			newPts = new GeoPoint[penPoints2.size()];
			// newPts = new GeoList(cons);
			offset = 0;
		} else {
			// newPts = lastPolyLine.getPointsList();

			// force a gap
			// newPts.add(new GeoPoint2(cons, Double.NaN, Double.NaN, 1));

			GeoPointND[] pts = getAlgoPolyline(lastAlgo).getPointsND();

			newPts = new GeoPoint[penPoints2.size() + 1 + pts.length];

			for (int i = 0; i < pts.length; i++) {
				newPts[i] = (GeoPoint) pts[i].copyInternal(cons);
			}

			newPts[pts.length] = new GeoPoint(cons, Double.NaN, Double.NaN, 1);

			offset = pts.length + 1;

		}

		Iterator<org.geogebra.common.awt.GPoint> it = penPoints2.iterator();
		while (it.hasNext()) {
			GPoint p = it.next();
			// newPts.add(new GeoPoint2(cons, view.toRealWorldCoordX(p.getX()),
			// view.toRealWorldCoordY(p.getY()), 1));
			newPts[offset++] = new GeoPoint(cons, view.toRealWorldCoordX(p
					.getX()), view.toRealWorldCoordY(p.getY()), 1);
		}

		AlgoElement algo;
		AlgoPolyLine newPolyLine;
		if (!absoluteScreenPosition) {

			// set label
			newPolyLine = new AlgoPolyLine(cons, null, newPts, null, true);
			algo = newPolyLine;
		} else {

			// don't set label
			newPolyLine = new AlgoPolyLine(cons, newPts, null, true);

			EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();

			Kernel kernelA = app.getKernel();

			GeoPoint corner1 = new GeoPoint(kernelA.getConstruction());
			GeoPoint corner3 = new GeoPoint(kernelA.getConstruction());
			GeoPoint screenCorner1 = new GeoPoint(kernelA.getConstruction());
			GeoPoint screenCorner3 = new GeoPoint(kernelA.getConstruction());
			if (ev != null) {
				corner1.setCoords(ev.getXmin(), ev.getYmin(), 1);
				corner3.setCoords(ev.getXmax(), ev.getYmax(), 1);
				screenCorner1.setCoords(0, ev.getHeight(), 1);
				screenCorner3.setCoords(ev.getWidth(), 0, 1);
			}

			MyDouble evNo = new MyDouble(kernelA, ev.getEuclidianViewNo());

			cons.removeFromConstructionList(newPolyLine);

			algo = new AlgoAttachCopyToView(cons, null,
					newPolyLine.getGeoElements()[0], evNo, corner1, corner3,
					screenCorner1, screenCorner3);
		}

		newPolyLine.getGeoElements()[0].setTooltipMode(GeoElement.TOOLTIP_OFF);

		if (lastAlgo == null) {
			// lastPolyLine = new AlgoPolyLine(cons, null, newPts);
		} else {
			try {
				cons.replace(lastAlgo.getOutput(0), algo.getOutput(0));
				// String label = lastPolyLine.getPoly().getLabelSimple();
				// lastPolyLine.getPoly().remove();
				// lastPolyLine.remove();
				// newPolyLine.getPoly().setLabel(label);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// lastPolyLine.setPointsList(newPts);
		}

		lastAlgo = algo;

		GeoPolyLine poly = (GeoPolyLine) algo.getOutput(0);

		poly.setLineThickness(penSize * PEN_SIZE_FACTOR);
		poly.setLineType(penLineStyle);
		poly.setObjColor(penColor);

		app.getSelectionManager().clearSelectedGeos(false);
		app.getSelectionManager().addSelectedGeo(poly);

		// app.getKernel().getAlgebraProcessor().processAlgebraCommandNoExceptionsOrErrors("AttachCopyToView["+poly.getLabelSimple()+",1]",
		// false);

		poly.updateRepaint();

		app.storeUndoInfo();
	}

	private static AlgoPolyLine getAlgoPolyline(AlgoElement al) {
		if (al instanceof AlgoPolyLine)
			return (AlgoPolyLine) al;
		return (AlgoPolyLine) al.getInput()[0].getParentAlgorithm();
	}

	private void mouseReleasedFreehand(int x, int y) {
		int n = maxX - minX + 1;
		double[] freehand1 = new double[n];

		GeoElement shape = checkShapes(x, y);

		if (shape != null && shape.isGeoLine()) {
			// lines take priority over functions
			penPoints.clear();
			return;
		}

//		if (shape instanceof GeoConic && ((GeoConic) shape).isCircle()) {
//			String equation = shape
//					.getAlgebraDescription(StringTemplate.defaultTemplate);
//			try {
//				app.getKernel()
//						.getAlgebraProcessor()
//						.processAlgebraCommandNoExceptionHandling(equation,
//								true, false, true, true);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			penPoints.clear();
//			return;
//		}

		// now check if it can be a function (increasing or decreasing x)

		double monotonicTest = 0;

		for (int i = 0; i < penPoints.size() - 1; i++) {

			GPoint p1 = penPoints.get(i);
			GPoint p2 = penPoints.get(i + 1);

			if (Math.signum(p2.x - p1.x) != 1) {
				monotonicTest++;
			}

		}

		App.debug("mono" + monotonicTest + " " + monotonicTest
				/ penPoints.size());

		monotonicTest = monotonicTest / penPoints.size();

		// allow 10% error
		boolean monotonic = monotonicTest > 0.9 || monotonicTest < 0.1;

		if (!monotonic) {
			// may or may not have recognized a shape eg circle in checkShapes()
			// earlier
			penPoints.clear();
			return;
		}

		// now definitely a function

		if (shape != null) {
			for (GeoElement geo : shape.getParentAlgorithm().getInput()) {
				geo.remove();
			}
			shape.remove();
		}

		for (int i = 0; i < n; i++) {
			freehand1[i] = Double.NaN;
		}

		for (int i = 0; i < penPoints.size(); i++) {
			GPoint p = penPoints.get(i);
			if (Double.isNaN(freehand1[p.x - minX])) {
				freehand1[p.x - minX] = view.toRealWorldCoordY(p.y);
			}
		}

		// fill in any gaps (eg from fast mouse movement)
		double val = freehand1[0];
		int valIndex = 0;
		double nextVal = Double.NaN;
		int nextValIndex = -1;
		for (int i = 0; i < n; i++) {
			if (Double.isNaN(freehand1[i])) {
				if (i > nextValIndex) {
					nextValIndex = i;
					while (nextValIndex < n
							&& Double.isNaN(freehand1[nextValIndex]))
						nextValIndex++;
				}
				if (nextValIndex >= n)
					freehand1[i] = val;
				else {
					nextVal = freehand1[nextValIndex];
					freehand1[i] = (val * (nextValIndex - i) + nextVal
							* (i - valIndex))
							/ (nextValIndex - valIndex);
				}
			} else {
				val = freehand1[i];
				valIndex = i;
			}
		}

		Construction cons = app.getKernel().getConstruction();

		GeoList list = new GeoList(cons);
		// checkDecimalFraction() -> shorter XML
		list.add(new GeoNumeric(cons, Kernel.checkDecimalFraction(view
				.toRealWorldCoordX(minX))));
		list.add(new GeoNumeric(cons, Kernel.checkDecimalFraction(view
				.toRealWorldCoordX(maxX))));
		for (int i = 0; i < n; i++) {
			list.add(new GeoNumeric(cons, Kernel
					.checkDecimalFraction(freehand1[i])));
		}

		new AlgoFunctionFreehand(cons, null, list).getGeoElements();

		//GeoElement fun = algo.getGeoElements()[0];

		// fun.setLineThickness(penSize * PEN_SIZE_FACTOR);
		// fun.setLineType(penLineStyle);
		// fun.setObjColor(penColor);

		minX = Integer.MAX_VALUE;
		maxX = Integer.MIN_VALUE;
	}

	/**
	 * @param b
	 *            whether we are using freehand shape (ie shape recognition)
	 */
	public void setFreehand(boolean b) {
		freehand = b;
	}

	/*
	 * ported from xournal by Neel Shah
	 */
	private int findPolygonal(int start, int end, int n, int offset1,
			int offset2) {
		Inertia s = new Inertia();
		Inertia s1 = new Inertia();
		Inertia s2 = new Inertia();
		int k, i1 = 0, i2 = 0, n1 = 0, n2;
		double det1, det2;
		int nsides = n;
		// AbstractApplication.debug(start);
		// AbstractApplication.debug(end);
		if (end == start)
			return 0; // no way
		if (nsides <= 0)
			return 0;
		if (end - start < 5)
			nsides = 1; // too small for a polygon
		// look for a linear piece that's big enough
		for (k = 0; k < nsides; ++k) {
			i1 = start + (k * (end - start)) / nsides;
			// AbstractApplication.debug(i1);
			i2 = start + ((k + 1) * (end - start)) / nsides;
			// AbstractApplication.debug(i2);
			calc_inertia(i1, i2, s);
			if (EuclidianPen.I_det(s) < LINE_MAX_DET)
				break;
		}
		if (k == nsides)
			return 0;
		while (true) {
			if (i1 > start) {
				s1.mass = s.mass;
				s1.sx = s.sx;
				s1.sxx = s.sxx;
				s1.sxy = s.sxy;
				s1.syy = s.syy;
				s1.sy = s.sy;
				this.incr_inertia(i1 - 1, s1, 1);
				det1 = EuclidianPen.I_det(s1);
			} else
				det1 = 1;
			if (i2 < end) {
				s2.mass = s.mass;
				s2.sx = s.sx;
				s2.sxx = s.sxx;
				s2.sxy = s.sxy;
				s2.syy = s.syy;
				s2.sy = s.sy;
				this.incr_inertia(i2, s2, 1);
				det2 = EuclidianPen.I_det(s2);
			} else
				det2 = 1;
			if (det1 < det2 && det1 < LINE_MAX_DET) {
				i1--;
				s.mass = s1.mass;
				s.sx = s1.sx;
				s.sxx = s1.sxx;
				s.sxy = s1.sxy;
				s.syy = s1.syy;
				s.sy = s1.sy;
			} else if (det2 < det1 && det2 < LINE_MAX_DET) {
				i2++;
				s.mass = s2.mass;
				s.sx = s2.sx;
				s.sxx = s2.sxx;
				s.sxy = s2.sxy;
				s.syy = s2.syy;
				s.sy = s2.sy;
			} else
				break;
		}
		if (i1 > start) {
			n1 = this.findPolygonal(start, i1, (i2 == end) ? (nsides - 1)
					: (nsides - 2), offset1, offset2);
			if (n1 == 0)
				return 0;
		} else
			n1 = 0;
		brk[n1 + offset1] = i1;
		brk[n1 + 1 + offset1] = i2;
		if (offset2 + n1 == 0) {
			a.mass = s.mass;
			a.sx = s.sx;
			a.sxx = s.sxx;
			a.sxy = s.sxy;
			a.syy = s.syy;
			a.sy = s.sy;
		}
		if (offset2 + n1 == 1) {
			b.mass = s.mass;
			b.sx = s.sx;
			b.sxx = s.sxx;
			b.sxy = s.sxy;
			b.syy = s.syy;
			b.sy = s.sy;
		}
		if (offset2 + n1 == 2) {
			c.mass = s.mass;
			c.sx = s.sx;
			c.sxx = s.sxx;
			c.sxy = s.sxy;
			c.syy = s.syy;
			c.sy = s.sy;
		}
		if (offset2 + n1 == 3) {
			d.mass = s.mass;
			d.sx = s.sx;
			d.sxx = s.sxx;
			d.sxy = s.sxy;
			d.syy = s.syy;
			d.sy = s.sy;
		}
		if (i2 < end) {
			n2 = this.findPolygonal(i2, end, nsides - n1 - 1, offset1 + n1 + 1,
					offset2 + n1 + 1);
			if (n2 == 0.)
				return 0;
		} else
			n2 = 0;
		return n1 + n2 + 1;
	}

	private void calc_inertia(int start, int end, Inertia s) {
		int i;
		int coeff = 1;
		int temp1[] = new int[4];
		double dm = 0;
		s.mass = 0.;
		s.sx = 0.;
		s.sxx = 0.;
		s.sxy = 0.;
		s.sy = 0.;
		s.syy = 0.;
		temp1[0] = penPoints.get(start).x;
		temp1[1] = penPoints.get(start).y;
		temp1[2] = penPoints.get(start + 1).x;
		temp1[3] = penPoints.get(start + 1).y;
		dm = coeff * Math.hypot(temp1[2] - temp1[0], temp1[3] - temp1[1]);
		s.mass = s.mass + dm;
		s.sx = s.sx + (dm * temp1[0]);
		s.sxx = s.sxx + (dm * temp1[0] * temp1[0]);
		s.sxy = s.sxy + (dm * temp1[0] * temp1[1]);
		s.sy = s.sy + (dm * temp1[1]);
		s.syy = s.syy + (dm * temp1[1] * temp1[1]);
		for (i = start + 1; i < end; ++i) {
			temp1[0] = penPoints.get(i).x;
			temp1[1] = penPoints.get(i).y;
			temp1[2] = penPoints.get(i + 1).x;
			temp1[3] = penPoints.get(i + 1).y;
			dm = coeff * Math.hypot(temp1[2] - temp1[0], temp1[3] - temp1[1]);
			s.mass = s.mass + dm;
			s.sx = s.sx + (dm * temp1[0]);
			s.sxx = s.sxx + (dm * temp1[0] * temp1[0]);
			s.sxy = s.sxy + (dm * temp1[0] * temp1[1]);
			s.sy = s.sy + (dm * temp1[1]);
			s.syy = s.syy + (dm * temp1[1] * temp1[1]);
		}
	}

	private final static double I_det(Inertia s) {
		double ixx = I_xx(s);
		double iyy = I_yy(s);
		double ixy = I_xy(s);
		if (s.mass <= 0.)
			return 0.;
		if (ixx + iyy <= 0.)
			return 0.;
		return 4 * (ixx * iyy - ixy * ixy) / (ixx + iyy) / (ixx + iyy);
	}

	private static double I_xx(Inertia s) {
		if (s.mass <= 0.)
			return 0.;
		return (s.sxx - s.sx * s.sx / s.mass) / s.mass;
	}

	private static double I_xy(Inertia s) {
		if (s.mass <= 0.)
			return 0.;
		return (s.sxy - s.sx * s.sy / s.mass) / s.mass;
	}

	private static double I_yy(Inertia s) {
		if (s.mass <= 0.)
			return 0.;
		return (s.syy - s.sy * s.sy / s.mass) / s.mass;
	}

	private double score_circle(int start, int end, Inertia s) {
		double sum, x0, y0, r0, dm, deltar;
		int i;
		if (s.mass == 0.)
			return 0;
		sum = 0.;
		x0 = EuclidianPen.center_x(s);
		y0 = EuclidianPen.center_y(s);
		r0 = EuclidianPen.I_rad(s);
		for (i = start; i < end; ++i) {
			dm = Math.hypot(penPoints.get(i + 1).x - penPoints.get(i).x,
					penPoints.get(i + 1).y - penPoints.get(i).y);
			deltar = Math.hypot(penPoints.get(i).x - x0, penPoints.get(i).y
					- y0)
					- r0;
			sum = sum + (dm * Math.abs(deltar));
		}
		return sum / (s.mass * r0);
	}

	private static double center_x(Inertia s) {
		return s.sx / s.mass;
	}

	private static double center_y(Inertia s) {
		return s.sy / s.mass;
	}

	private static double I_rad(Inertia s) {
		double ixx = EuclidianPen.I_xx(s);
		double iyy = EuclidianPen.I_yy(s);
		if (ixx + iyy <= 0.)
			return 0.;
		return Math.sqrt(ixx + iyy);
	}

	private GeoConic makeACircle(double x, double y, double r) {
		temp = new ArrayList<GPoint>();
		int npts, i = 0;
		npts = (int) (2 * r);
		if (npts < 12)
			npts = 12;
		GPoint p;
		for (i = 0; i <= npts; i++) {
			p = new GPoint();
			p.x = (int) (x + r * Math.cos((2 * i * Math.PI) / npts));
			p.y = (int) (y + r * Math.sin((2 * i * Math.PI) / npts));
			temp.add(p);
		}
		int size = temp.size();
		double x1 = view.toRealWorldCoordX(temp.get(0).x);
		double y1 = view.toRealWorldCoordY(temp.get(0).y);
		double x2 = view.toRealWorldCoordX(temp.get(size / 3).x);
		double y2 = view.toRealWorldCoordY(temp.get(size / 3).y);
		double x3 = view.toRealWorldCoordX(temp.get(2 * size / 3).x);
		double y3 = view.toRealWorldCoordY(temp.get(2 * size / 3).y);
		if (x2 == x1) {
			x1 = view.toRealWorldCoordX(temp.get(size / 4).x);
			y1 = view.toRealWorldCoordY(temp.get(size / 4).y);
		}
		if (x2 == x3) {
			x3 = view.toRealWorldCoordX(temp.get(11 * size / 12).x);
			y3 = view.toRealWorldCoordY(temp.get(11 * size / 12).y);
		}
		GeoPoint p1 = new GeoPoint(app.getKernel().getConstruction(), x1, y1,
				1.0);
		GeoPoint q = new GeoPoint(app.getKernel().getConstruction(), x2, y2,
				1.0);
		GeoPoint z = new GeoPoint(app.getKernel().getConstruction(), x3, y3,
				1.0);
		AlgoCircleThreePoints algo = new AlgoCircleThreePoints(app.getKernel()
				.getConstruction(), null, p1, q, z);

		GeoConic circle = (GeoConic) algo.getCircle();
		// circle.setLineThickness(penSize * PEN_SIZE_FACTOR);
		// circle.setLineType(penLineStyle);
		// circle.setObjColor(penColor);
		circle.updateRepaint();

		return circle;

	}

	/**
	 * creates a conic form the points in penPoints, if there are enough points
	 * and a conic exists that fits good enough
	 * 
	 * @return the conic that fits best to the given points; null in case that
	 *         there are too few points or the thresholds cannot be fulfilled
	 */
	private GeoConic makeAConic() {
		double px, py;
		Construction cons = this.app.getKernel().getConstruction();

		// adapted from FitImplicit

		// order 2 ie conic
		int order = 2;

		// sample 10 points from what we're given
		int datasize = 10;

		if (this.penPoints.size() < datasize) {
			return null;
		}

		int step = this.penPoints.size() / datasize;

		Array2DRowRealMatrix M = new Array2DRowRealMatrix(datasize, order
				* (order + 1));

		double[] coeffs = new double[6];

		try {
			int r = 0;
			for (int j = 0; j < datasize; j++) {

				GPoint point = penPoints.get(r);
				r += step;

				px = view.toRealWorldCoordX(point.getX());
				py = view.toRealWorldCoordY(point.getY());

				// uncomment for debugging (to see which points were sampled)
				// new GeoPoint(app.getKernel().getConstruction(), null, px, py,
				// 1);

				int c1 = 0;

				// create powers eg x^2y^0, x^1y^1, x^0*y^2, x, y, 1
				for (int i = 0; i <= order; i++) {
					for (int xpower = 0; xpower <= i; xpower++) {

						int ypower = i - xpower;

						double val = AlgoFitImplicit.power(px, xpower)
								* AlgoFitImplicit.power(py, ypower);
						// App.debug(val + "x^"+xpower+" * y^"+ypower);

						M.setEntry(j, c1++, val);
					}
				}
			}

			SingularValueDecomposition svd = new SingularValueDecompositionImpl(
					M);
			RealMatrix V = svd.getV();
			RealVector coeffsRV = V.getColumnVector(5);

			// create powers eg x^2y^0, x^1y^1, x^0*y^2, x, y, 1
			for (int i = 0; i < 6; i++) {
				coeffs[5 - i] = coeffsRV.getEntry(i);
				// App.debug("coeff of " + i + " = "+ coeffs[i]);
			}

			// double eccentricity = conic.eccentricity;

			// GeoVec2D midpoint = conic.b;

			// App.debug("size of M = "+M.getColumnDimension()+" "+M.getRowDimension());
			// App.debug("size of V = "+V.getColumnDimension()+" "+V.getRowDimension());

		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}

		GeoConic conic = new GeoConic(this.app.getKernel().getConstruction(),
				null, coeffs);

		GeoPoint point = new GeoPoint(this.app.getKernel().getConstruction(),
				0, 0, 1);
		double error = 0;
		for (GPoint p : penPoints) {
			point.setCoords(view.toRealWorldCoordX(p.x),
					view.toRealWorldCoordY(p.y), 1);
			error += conic.distance(point);
		}
		error /= penPoints.size();

		if (conic.isDefined()
				&& conic.getHalfAxis(0) / error > CONIC_AXIS_ERROR_RATIO
				&& conic.getHalfAxis(1) / error > CONIC_AXIS_ERROR_RATIO) {
			AlgoFocus algo = new AlgoFocus(cons, new String[] { null, null },
					conic);
			GeoPointND[] focus = algo.getFocus();

			int type = conic.getType();
			GeoPoint pointOnConic = this.app.getKernel().getAlgoDispatcher()
					.Point(null, conic, null);
			conic.remove();

			GeoPoint f0 = new GeoPoint(cons, null, focus[0].getInhomX(),
					focus[0].getInhomY(), 1);
			GeoPoint f1 = new GeoPoint(cons, null, focus[1].getInhomX(),
					focus[1].getInhomY(), 1);
			GeoPoint additionalPoint = new GeoPoint(cons, null,
					pointOnConic.getInhomX(), pointOnConic.getInhomY(), 1);

			conic = (GeoConic) this.app.getKernel().getAlgoDispatcher()
					.EllipseHyperbola(null, f0, f1, additionalPoint, type);
		} else {
			conic.remove();
			conic = null;
		}

		return conic;
	}

	/**
	 * Returns delta x.
	 *
	 * @param startPoint2
	 *            First point
	 * @param point
	 *            Second point
	 * @return Delta x
	 */
	private static int getDeltaX(GPoint startPoint2, GPoint point) {
		return point.x - startPoint2.x;
	}

	/**
	 * Returns delta y.
	 *
	 * @param startPoint2
	 *            First point
	 * @param point
	 *            Second point
	 * @return Delta y
	 */
	private static int getDeltaY(GPoint startPoint2, GPoint point) {
		return point.y - startPoint2.y;
	}

	/**
	 * Adds movement to buffer.
	 *
	 * @param move
	 *            String representation of recognized movement
	 */
	private void saveMove(String move) {
		if ((gesture.length() == 0)
				|| (gesture.charAt(gesture.length() - 1) == move.charAt(0))
				|| count == 1)
			count++;
		else
			count = 1;
		// should not store two equal moves in succession
		if ((gesture.length() > 0)
				&& ((gesture.charAt(gesture.length() - 1) == move.charAt(0)) || count != 2))
			return;
		gesture.append(move);
	}

	/**
	 * Returns string representation of mouse gesture.
	 *
	 * @return String representation of mouse gesture. "L" for left, "R" for
	 *         right, "U" for up, "D" for down movements. For example: "ULD".
	 */
	private String getGesture() {
		return gesture.toString();
	}

	private void clearTemporaryInfo() {
		startPoint = null;
		gesture.delete(0, gesture.length());
	}

	private void optimize_polygonal(int nsides) {
		int i;
		double cost, newcost;
		boolean improved;
		Inertia temp1 = new Inertia();
		Inertia temp2 = new Inertia();
		for (i = 1; i < nsides; ++i) {
			if ((i - 1) == 0) {
				temp1.mass = a.mass;
				temp1.sx = a.sx;
				temp1.sxx = a.sxx;
				temp1.sxy = a.sxy;
				temp1.sy = a.sy;
				temp1.syy = a.syy;
				temp2.mass = b.mass;
				temp2.sx = b.sx;
				temp2.sxx = b.sxx;
				temp2.sxy = b.sxy;
				temp2.sy = b.sy;
				temp2.syy = b.syy;
			}
			if ((i - 1) == 1) {
				temp1.mass = b.mass;
				temp1.sx = b.sx;
				temp1.sxx = b.sxx;
				temp1.sxy = b.sxy;
				temp1.sy = b.sy;
				temp1.syy = b.syy;
				temp2.mass = c.mass;
				temp2.sx = c.sx;
				temp2.sxx = c.sxx;
				temp2.sxy = c.sxy;
				temp2.sy = c.sy;
				temp2.syy = c.syy;
			}
			if ((i - 1) == 2) {
				temp1.mass = c.mass;
				temp1.sx = c.sx;
				temp1.sxx = c.sxx;
				temp1.sxy = c.sxy;
				temp1.sy = c.sy;
				temp1.syy = c.syy;
				temp2.mass = d.mass;
				temp2.sx = d.sx;
				temp2.sxx = d.sxx;
				temp2.sxy = d.sxy;
				temp2.sy = d.sy;
				temp2.syy = d.syy;
			}
			cost = EuclidianPen.I_det(temp1) * EuclidianPen.I_det(temp1)
					+ EuclidianPen.I_det(temp2) * EuclidianPen.I_det(temp2);
			improved = false;
			while (brk[i] > brk[i - 1] + 1) {
				this.incr_inertia(brk[i] - 1, temp1, -1);
				this.incr_inertia(brk[i] - 1, temp2, 1);
				newcost = EuclidianPen.I_det(temp1) * EuclidianPen.I_det(temp1)
						+ EuclidianPen.I_det(temp2) * EuclidianPen.I_det(temp2);
				if (newcost >= cost)
					break;
				improved = true;
				cost = newcost;
				brk[i]--;
				if (i - 1 == 0) {
					a.mass = temp1.mass;
					a.sx = temp1.sx;
					a.sy = temp1.sy;
					a.sxx = temp1.sxx;
					a.sxy = temp1.sxy;
					a.syy = temp1.syy;
					b.mass = temp2.mass;
					b.sx = temp2.sx;
					b.sy = temp2.sy;
					b.sxx = temp2.sxx;
					b.sxy = temp2.sxy;
					b.syy = temp2.syy;
				}
				if (i - 1 == 1) {
					b.mass = temp1.mass;
					b.sx = temp1.sx;
					b.sy = temp1.sy;
					b.sxx = temp1.sxx;
					b.sxy = temp1.sxy;
					b.syy = temp1.syy;
					c.mass = temp2.mass;
					c.sx = temp2.sx;
					c.sy = temp2.sy;
					c.sxx = temp2.sxx;
					c.sxy = temp2.sxy;
					c.syy = temp2.syy;
				}
				if (i - 1 == 2) {
					c.mass = temp1.mass;
					c.sx = temp1.sx;
					c.sy = temp1.sy;
					c.sxx = temp1.sxx;
					c.sxy = temp1.sxy;
					c.syy = temp1.syy;
					d.mass = temp2.mass;
					d.sx = temp2.sx;
					d.sy = temp2.sy;
					d.sxx = temp2.sxx;
					d.sxy = temp2.sxy;
					d.syy = temp2.syy;
				}
			}
			if (improved)
				continue;
			if ((i - 1) == 0) {
				temp1.mass = a.mass;
				temp1.sx = a.sx;
				temp1.sxx = a.sxx;
				temp1.sxy = a.sxy;
				temp1.sy = a.sy;
				temp1.syy = a.syy;
				temp2.mass = b.mass;
				temp2.sx = b.sx;
				temp2.sxx = b.sxx;
				temp2.sxy = b.sxy;
				temp2.sy = b.sy;
				temp2.syy = b.syy;
			}
			if ((i - 1) == 1) {
				temp1.mass = b.mass;
				temp1.sx = b.sx;
				temp1.sxx = b.sxx;
				temp1.sxy = b.sxy;
				temp1.sy = b.sy;
				temp1.syy = b.syy;
				temp2.mass = c.mass;
				temp2.sx = c.sx;
				temp2.sxx = c.sxx;
				temp2.sxy = c.sxy;
				temp2.sy = c.sy;
				temp2.syy = c.syy;
			}
			if ((i - 1) == 2) {
				temp1.mass = c.mass;
				temp1.sx = c.sx;
				temp1.sxx = c.sxx;
				temp1.sxy = c.sxy;
				temp1.sy = c.sy;
				temp1.syy = c.syy;
				temp2.mass = d.mass;
				temp2.sx = d.sx;
				temp2.sxx = d.sxx;
				temp2.sxy = d.sxy;
				temp2.sy = d.sy;
				temp2.syy = d.syy;
			}
			while (brk[i] < brk[i + 1] - 1) {
				this.incr_inertia(brk[i], temp1, 1);
				this.incr_inertia(brk[i], temp2, -1);
				newcost = (EuclidianPen.I_det(temp1) * EuclidianPen
						.I_det(temp1))
						+ (EuclidianPen.I_det(temp2) * EuclidianPen
								.I_det(temp2));
				if (newcost >= cost)
					break;
				cost = newcost;
				brk[i]++;
				if (i - 1 == 0) {
					a.mass = temp1.mass;
					a.sx = temp1.sx;
					a.sy = temp1.sy;
					a.sxx = temp1.sxx;
					a.sxy = temp1.sxy;
					a.syy = temp1.syy;
					b.mass = temp2.mass;
					b.sx = temp2.sx;
					b.sy = temp2.sy;
					b.sxx = temp2.sxx;
					b.sxy = temp2.sxy;
					b.syy = temp2.syy;
				}
				if (i - 1 == 1) {
					b.mass = temp1.mass;
					b.sx = temp1.sx;
					b.sy = temp1.sy;
					b.sxx = temp1.sxx;
					b.sxy = temp1.sxy;
					b.syy = temp1.syy;
					c.mass = temp2.mass;
					c.sx = temp2.sx;
					c.sy = temp2.sy;
					c.sxx = temp2.sxx;
					c.sxy = temp2.sxy;
					c.syy = temp2.syy;
				}
				if (i - 1 == 2) {
					c.mass = temp1.mass;
					c.sx = temp1.sx;
					c.sy = temp1.sy;
					c.sxx = temp1.sxx;
					c.sxy = temp1.sxy;
					c.syy = temp1.syy;
					d.mass = temp2.mass;
					d.sx = temp2.sx;
					d.sy = temp2.sy;
					d.sxx = temp2.sxx;
					d.sxy = temp2.sxy;
					d.syy = temp2.syy;
				}
			}
		}
	}

	private void incr_inertia(int start, Inertia s, int coeff) {
		double pt1_x = penPoints.get(start).x;
		double pt1_y = penPoints.get(start).y;
		double pt2_x = penPoints.get(start + 1).x;
		double pt2_y = penPoints.get(start + 1).y;
		double dm = 0;
		dm = coeff * Math.hypot(pt2_x - pt1_x, pt2_y - pt1_y);
		s.mass = s.mass + dm;
		s.sx = s.sx + (dm * pt1_x);
		s.sy = s.sy + (dm * pt1_y);
		s.sxx = s.sxx + (dm * pt1_x * pt1_x);
		s.syy = s.syy + (dm * pt1_y * pt1_y);
		s.sxy = s.sxy + (dm * pt1_x * pt1_y);
	}

	private void get_segment_geometry(int begin, int end, Inertia s,
			RecoSegment r) {
		double a1, b1, c1, lmin, lmax, l;
		int i;
		int start = begin;
		r.xcenter = EuclidianPen.center_x(s);
		r.ycenter = EuclidianPen.center_y(s);
		a1 = EuclidianPen.I_xx(s);
		b1 = EuclidianPen.I_xy(s);
		c1 = EuclidianPen.I_yy(s);
		r.angle = Math.atan2(2 * b1, a1 - c1) / 2;
		r.radius = Math.sqrt(3 * (a1 + c1));
		lmin = lmax = 0;
		for (i = start; i <= end; ++i) {
			l = (penPoints.get(start).x - r.xcenter) * Math.cos(r.angle)
					+ (penPoints.get(start).y - r.ycenter) * Math.sin(r.angle);
			if (l < lmin)
				lmin = l;
			if (l > lmax)
				lmax = l;
			start++;
		}
		r.x1 = r.xcenter + lmin * Math.cos(r.angle);
		r.y1 = r.ycenter + lmin * Math.sin(r.angle);
		r.x2 = r.xcenter + lmax * Math.cos(r.angle);
		r.y2 = r.ycenter + lmax * Math.sin(r.angle);
	}

	private GeoElement try_rectangle() {
		RecoSegment rs = null;
		RecoSegment r1 = null;
		RecoSegment r2 = null;
		int i;
		double dist, avg_angle = 0;
		double pt[] = new double[2];
		Construction cons = app.getKernel().getConstruction();
		AlgoPolygon algo = null;
		double x_first = 0;
		double y_first = 0;
		double points[] = new double[10];

		if (recognizer_queue_length < 4)
			return null;
		if (recognizer_queue_length - 4 == 0)
			rs = reco_queue_a;
		if (recognizer_queue_length - 4 == 1)
			rs = reco_queue_b;
		if (recognizer_queue_length - 4 == 2)
			rs = reco_queue_c;
		if (recognizer_queue_length - 4 == 3)
			rs = reco_queue_d;
		if (recognizer_queue_length - 4 == 4)
			rs = reco_queue_e;
		// AbstractApplication.debug(rs.startpt);
		if (rs.startpt != 0)
			return null;
		for (i = 0; i <= 3; ++i) {
			if (recognizer_queue_length - 4 + i == 0)
				r1 = reco_queue_a;
			if (recognizer_queue_length - 4 + i == 1)
				r1 = reco_queue_b;
			if (recognizer_queue_length - 4 + i == 2)
				r1 = reco_queue_c;
			if (recognizer_queue_length - 4 + i == 3)
				r1 = reco_queue_d;
			if (recognizer_queue_length - 4 + i == 4)
				r1 = reco_queue_e;
			if (recognizer_queue_length - 4 + ((i + 1) % 4) == 0)
				r2 = reco_queue_a;
			if (recognizer_queue_length - 4 + ((i + 1) % 4) == 1)
				r2 = reco_queue_b;
			if (recognizer_queue_length - 4 + ((i + 1) % 4) == 2)
				r2 = reco_queue_c;
			if (recognizer_queue_length - 4 + ((i + 1) % 4) == 3)
				r2 = reco_queue_d;
			if (recognizer_queue_length - 4 + ((i + 1) % 4) == 4)
				r2 = reco_queue_e;
			// AbstractApplication.debug(Math.abs(Math.abs(r1.angle-r2.angle)-Math.PI/2)
			// > RECTANGLE_ANGLE_TOLERANCE);
			if (Math.abs(Math.abs(r1.angle - r2.angle) - Math.PI / 2) > RECTANGLE_ANGLE_TOLERANCE)
				return null;
			avg_angle = avg_angle + r1.angle;
			if (r2.angle > r1.angle)
				avg_angle = avg_angle + ((i + 1) * Math.PI / 2);
			else
				avg_angle = avg_angle - ((i + 1) * Math.PI / 2);
			r1.reversed = ((r1.x2 - r1.x1) * (r2.xcenter - r1.xcenter) + (r1.y2 - r1.y1)
					* (r2.ycenter - r1.ycenter)) < 0;
		}
		for (i = 0; i <= 3; ++i) {
			if (recognizer_queue_length - 4 + i == 0)
				r1 = reco_queue_a;
			if (recognizer_queue_length - 4 + i == 1)
				r1 = reco_queue_b;
			if (recognizer_queue_length - 4 + i == 2)
				r1 = reco_queue_c;
			if (recognizer_queue_length - 4 + i == 3)
				r1 = reco_queue_d;
			if (recognizer_queue_length - 4 + i == 4)
				r1 = reco_queue_e;
			if (recognizer_queue_length - 4 + ((i + 1) % 4) == 0)
				r2 = reco_queue_a;
			if (recognizer_queue_length - 4 + ((i + 1) % 4) == 1)
				r2 = reco_queue_b;
			if (recognizer_queue_length - 4 + ((i + 1) % 4) == 2)
				r2 = reco_queue_c;
			if (recognizer_queue_length - 4 + ((i + 1) % 4) == 3)
				r2 = reco_queue_d;
			if (recognizer_queue_length - 4 + ((i + 1) % 4) == 4)
				r2 = reco_queue_e;
			dist = Math.hypot((r1.reversed ? r1.x1 : r1.x2)
					- (r2.reversed ? r2.x2 : r2.x1), (r1.reversed ? r1.y1
					: r1.y2) - (r2.reversed ? r2.y2 : r2.y1));
			if (dist > RECTANGLE_LINEAR_TOLERANCE * (r1.radius + r2.radius))
				return null;
		}
		avg_angle = avg_angle / 4;
		if (Math.abs(avg_angle) < SLANT_TOLERANCE)
			avg_angle = 0;
		if (Math.abs(avg_angle) > Math.PI / 2 - SLANT_TOLERANCE)
			avg_angle = Math.PI / 2;
		for (i = 0; i <= 3; ++i) {
			if (recognizer_queue_length - 4 + i == 0)
				r1 = reco_queue_a;
			if (recognizer_queue_length - 4 + i == 1)
				r1 = reco_queue_b;
			if (recognizer_queue_length - 4 + i == 2)
				r1 = reco_queue_c;
			if (recognizer_queue_length - 4 + i == 3)
				r1 = reco_queue_d;
			if (recognizer_queue_length - 4 + i == 4)
				r1 = reco_queue_e;
			r1.angle = avg_angle + i * Math.PI / 2;
		}
		for (i = 0; i <= 3; ++i) {
			if (recognizer_queue_length - 4 + i == 0)
				r1 = reco_queue_a;
			if (recognizer_queue_length - 4 + i == 1)
				r1 = reco_queue_b;
			if (recognizer_queue_length - 4 + i == 2)
				r1 = reco_queue_c;
			if (recognizer_queue_length - 4 + i == 3)
				r1 = reco_queue_d;
			if (recognizer_queue_length - 4 + i == 4)
				r1 = reco_queue_e;
			if (recognizer_queue_length - 4 + (i + 1) % 4 == 0)
				r2 = reco_queue_a;
			if (recognizer_queue_length - 4 + (i + 1) % 4 == 1)
				r2 = reco_queue_b;
			if (recognizer_queue_length - 4 + (i + 1) % 4 == 2)
				r2 = reco_queue_c;
			if (recognizer_queue_length - 4 + (i + 1) % 4 == 3)
				r2 = reco_queue_d;
			if (recognizer_queue_length - 4 + (i + 1) % 4 == 4)
				r2 = reco_queue_e;
			EuclidianPen.calc_edge_isect(r1, r2, pt);
			points[2 * i + 2] = pt[0];
			points[2 * i + 3] = pt[1];
		}
		points[0] = points[8];
		points[1] = points[9];

		GeoPointND[] pts = new GeoPointND[4];

		// in case a initialPoint is defined, move the polygon so that its first
		// point matches the initialPoint
		double offsetInitialPointX = 0;
		double offsetInitialPointY = 0;

		// in case the initialPoint cannot be used and can be deleted safely, delete it
		if(initialPoint != null && !initialPoint.isIndependent() && deleteInitialPoint){
			this.initialPoint.remove();
			this.initialPoint = null;
		}

		for (i = 0; i < 4; ++i) {
			x_first = view.toRealWorldCoordX(points[2 * i])
					+ offsetInitialPointX;
			y_first = view.toRealWorldCoordY(points[2 * i + 1])
					+ offsetInitialPointY;

			if (i == 0 && this.initialPoint != null
					&& this.initialPoint.isIndependent()) {
				offsetInitialPointX = this.initialPoint.x - x_first;
				offsetInitialPointY = this.initialPoint.y - y_first;
				pts[0] = this.initialPoint;
			} else {
				// null -> created labeled point
				pts[i] = new GeoPoint(cons, null, x_first, y_first, 1.0);
			}
		}

		algo = new AlgoPolygon(cons, null, pts);

		GeoElement poly = algo.getGeoElements()[0];
		// poly.setLineThickness(penSize * PEN_SIZE_FACTOR);
		// poly.setLineType(penLineStyle);
		// poly.setObjColor(penColor);
		poly.updateRepaint();

		return poly;
	}

	private GeoElement try_arrow() {
		RecoSegment rs = null;
		RecoSegment temp1 = null;
		RecoSegment temp2 = null;
		int i, j;
		double alpha[] = new double[3];
		double pt[] = new double[2];
		double dist, delta;
		double x1, y1, x2, y2, angle;
		boolean rev[] = new boolean[3];
		Construction cons = app.getKernel().getConstruction();
		GeoPoint p = null;
		GeoPoint q = null;
		AlgoJoinPointsSegment algo = null;
		double x_first = 0;
		double y_first = 0;
		double x_last = 0;
		double y_last = 0;
		if (recognizer_queue_length < 3)
			return null;
		if (recognizer_queue_length - 3 == 0)
			rs = reco_queue_a;
		if (recognizer_queue_length - 3 == 1)
			rs = reco_queue_b;
		if (recognizer_queue_length - 3 == 2)
			rs = reco_queue_c;
		if (recognizer_queue_length - 3 == 3)
			rs = reco_queue_d;
		if (recognizer_queue_length - 3 == 4)
			rs = reco_queue_e;
		// AbstractApplication.debug(rs.startpt);
		if (rs.startpt != 0)
			return null;
		for (i = 1; i <= 2; ++i) {
			if (recognizer_queue_length - 3 + i == 0)
				temp1 = reco_queue_a;
			if (recognizer_queue_length - 3 + i == 1)
				temp1 = reco_queue_b;
			if (recognizer_queue_length - 3 + i == 2)
				temp1 = reco_queue_c;
			if (recognizer_queue_length - 3 + i == 3)
				temp1 = reco_queue_d;
			if (recognizer_queue_length - 3 + i == 4)
				temp1 = reco_queue_e;
			if (temp1.radius > ARROW_MAXSIZE * rs.radius)
				return null;
			rev[i] = (Math.hypot(temp1.xcenter - rs.x1, temp1.ycenter - rs.y1)) < (Math
					.hypot(temp1.xcenter - rs.x2, temp1.ycenter - rs.y2));
		}
		if (rev[1] != rev[2])
			return null;
		if (rev[1]) {
			x1 = rs.x2;
			y1 = rs.y2;
			x2 = rs.x1;
			y2 = rs.y1;
			angle = rs.angle + Math.PI;
		} else {
			x1 = rs.x1;
			y1 = rs.y1;
			x2 = rs.x2;
			y2 = rs.y2;
			angle = rs.angle;
		}
		for (i = 1; i <= 2; ++i) {
			if (recognizer_queue_length - 3 + i == 0)
				temp1 = reco_queue_a;
			if (recognizer_queue_length - 3 + i == 1)
				temp1 = reco_queue_b;
			if (recognizer_queue_length - 3 + i == 2)
				temp1 = reco_queue_c;
			if (recognizer_queue_length - 3 + i == 3)
				temp1 = reco_queue_d;
			if (recognizer_queue_length - 3 + i == 4)
				temp1 = reco_queue_e;
			temp1.reversed = false;
			alpha[i] = temp1.angle - angle;
			while (alpha[i] < -Math.PI / 2) {
				alpha[i] = alpha[i] + Math.PI;
				temp1.reversed = !temp1.reversed;
			}
			while (alpha[i] > Math.PI / 2) {
				alpha[i] = alpha[i] - Math.PI;
				temp1.reversed = !temp1.reversed;
			}
			if (Math.abs(alpha[i]) < ARROW_ANGLE_MIN
					|| Math.abs(alpha[i]) > ARROW_ANGLE_MAX)
				return null;
		}
		if (alpha[1] * alpha[2] > 0
				|| Math.abs(alpha[1] + alpha[2]) > ARROW_ASYMMETRY_MAX_ANGLE)
			return null;
		if (recognizer_queue_length - 2 == 0)
			temp1 = reco_queue_a;
		if (recognizer_queue_length - 2 == 1)
			temp1 = reco_queue_b;
		if (recognizer_queue_length - 2 == 2)
			temp1 = reco_queue_c;
		if (recognizer_queue_length - 2 == 3)
			temp1 = reco_queue_d;
		if (recognizer_queue_length - 2 == 4)
			temp1 = reco_queue_e;
		if (recognizer_queue_length - 1 == 0)
			temp2 = reco_queue_a;
		if (recognizer_queue_length - 1 == 1)
			temp2 = reco_queue_b;
		if (recognizer_queue_length - 1 == 2)
			temp2 = reco_queue_c;
		if (recognizer_queue_length - 1 == 3)
			temp2 = reco_queue_d;
		if (recognizer_queue_length - 1 == 4)
			temp2 = reco_queue_e;
		if (temp1.radius / temp2.radius > 1 + ARROW_ASYMMETRY_MAX_LINEAR)
			return null;
		if (temp2.radius / temp1.radius > 1 + ARROW_ASYMMETRY_MAX_LINEAR)
			return null;
		EuclidianPen.calc_edge_isect(temp1, temp2, pt);
		for (j = 1; j <= 2; ++j) {
			if (recognizer_queue_length - 3 + j == 0)
				temp1 = reco_queue_a;
			if (recognizer_queue_length - 3 + j == 1)
				temp1 = reco_queue_b;
			if (recognizer_queue_length - 3 + j == 2)
				temp1 = reco_queue_c;
			if (recognizer_queue_length - 3 + j == 3)
				temp1 = reco_queue_d;
			if (recognizer_queue_length - 3 + j == 4)
				temp1 = reco_queue_e;
			dist = Math.hypot(pt[0] - (temp1.reversed ? temp1.x1 : temp1.x2),
					pt[1] - (temp1.reversed ? temp1.y1 : temp1.y2));
			if (dist > ARROW_TIP_LINEAR_TOLERANCE * temp1.radius)
				return null;
		}
		dist = (pt[0] - x2) * Math.sin(angle) - (pt[1] - y2) * Math.cos(angle);
		if (recognizer_queue_length - 3 + 1 == 0)
			temp1 = reco_queue_a;
		if (recognizer_queue_length - 3 + 1 == 1)
			temp1 = reco_queue_b;
		if (recognizer_queue_length - 3 + 1 == 2)
			temp1 = reco_queue_c;
		if (recognizer_queue_length - 3 + 1 == 3)
			temp1 = reco_queue_d;
		if (recognizer_queue_length - 3 + 1 == 4)
			temp1 = reco_queue_e;
		if (recognizer_queue_length - 3 + 2 == 0)
			temp2 = reco_queue_a;
		if (recognizer_queue_length - 3 + 2 == 1)
			temp2 = reco_queue_b;
		if (recognizer_queue_length - 3 + 2 == 2)
			temp2 = reco_queue_c;
		if (recognizer_queue_length - 3 + 2 == 3)
			temp2 = reco_queue_d;
		if (recognizer_queue_length - 3 + 2 == 4)
			temp2 = reco_queue_e;
		dist = dist / (temp1.radius + temp2.radius);
		if (Math.abs(dist) > ARROW_SIDEWAYS_GAP_TOLERANCE)
			return null;
		dist = (pt[0] - x2) * Math.cos(angle) + (pt[1] - y2) * Math.sin(angle);
		dist = dist / (temp1.radius + temp2.radius);
		if (dist < ARROW_MAIN_LINEAR_GAP_MIN
				|| dist > ARROW_MAIN_LINEAR_GAP_MAX)
			return null;
		if (Math.abs(rs.angle) < SLANT_TOLERANCE) { // nearly horizontal
			angle = angle - rs.angle;
			y1 = y2 = rs.ycenter;
		}
		if (rs.angle > Math.PI / 2 - SLANT_TOLERANCE) { // nearly vertical
			angle = angle - (rs.angle - Math.PI / 2);
			x1 = x2 = rs.xcenter;
		}
		if (rs.angle < -Math.PI / 2 + SLANT_TOLERANCE) { // nearly vertical
			angle = angle - (rs.angle + Math.PI / 2);
			x1 = x2 = rs.xcenter;
		}
		delta = Math.abs(alpha[1] - alpha[2]) / 2;
		dist = (Math.hypot(temp1.x1 - temp1.x2, temp1.y1 - temp1.y2) + Math
				.hypot(temp2.x1 - temp2.x2, temp2.y1 - temp2.y2)) / 2;
		x_first = view.toRealWorldCoordX(x1);
		y_first = view.toRealWorldCoordY(y1);
		x_last = view.toRealWorldCoordX(x2);
		y_last = view.toRealWorldCoordY(y2);
		p = new GeoPoint(cons, x_first, y_first, 1.0);
		q = new GeoPoint(cons, x_last, y_last, 1.0);
		algo = new AlgoJoinPointsSegment(cons, null, p, q);
		GeoElement line = algo.getGeoElements()[0];
		// line.setLineThickness(penSize * 2);
		// line.setLineType(penLineStyle);
		// line.setObjColor(penColor);
		line.updateRepaint();

		x_first = view.toRealWorldCoordX((x2 - dist * Math.cos(angle + delta)));
		y_first = view.toRealWorldCoordY((y2 - dist * Math.sin(angle + delta)));
		p = new GeoPoint(cons, x_first, y_first, 1.0);
		algo = new AlgoJoinPointsSegment(cons, null, p, q);
		line = algo.getGeoElements()[0];
		// line.setLineThickness(penSize * 2);
		// line.setLineType(penLineStyle);
		// line.setObjColor(penColor);
		line.updateRepaint();

		x_first = view.toRealWorldCoordX((x2 - dist * Math.cos(angle - delta)));
		y_first = view.toRealWorldCoordY((y2 - dist * Math.sin(angle - delta)));
		p = new GeoPoint(cons, x_first, y_first, 1.0);
		algo = new AlgoJoinPointsSegment(cons, null, p, q);
		line = algo.getGeoElements()[0];
		// line.setLineThickness(penSize * 2);
		// line.setLineType(penLineStyle);
		// line.setObjColor(penColor);
		line.updateRepaint();
		return line;
	}

	private GeoElement try_closed_polygon(int nsides) {
		RecoSegment rs = null;
		RecoSegment r1 = null;
		RecoSegment r2 = null;
		int i;
		double dist = 0;
		double pt[] = new double[2];
		Construction cons = app.getKernel().getConstruction();
		AlgoPolygon algo = null;
		double x_first = 0;
		double y_first = 0;
		double points[] = new double[nsides * 2 + 2];

		if (recognizer_queue_length < nsides)
			return null;
		if (recognizer_queue_length - nsides == 0)
			rs = reco_queue_a;
		if (recognizer_queue_length - nsides == 1)
			rs = reco_queue_b;
		if (recognizer_queue_length - nsides == 2)
			rs = reco_queue_c;
		if (recognizer_queue_length - nsides == 3)
			rs = reco_queue_d;
		if (recognizer_queue_length - nsides == 4)
			rs = reco_queue_e;
		if (rs.startpt != 0)
			return null;
		for (i = 0; i < nsides; ++i) {
			if (recognizer_queue_length - nsides + i == 0)
				r1 = reco_queue_a;
			if (recognizer_queue_length - nsides + i == 1)
				r1 = reco_queue_b;
			if (recognizer_queue_length - nsides + i == 2)
				r1 = reco_queue_c;
			if (recognizer_queue_length - nsides + i == 3)
				r1 = reco_queue_d;
			if (recognizer_queue_length - nsides + i == 4)
				r1 = reco_queue_e;
			if (recognizer_queue_length - nsides + (i + 1) % nsides == 0)
				r2 = reco_queue_a;
			if (recognizer_queue_length - nsides + (i + 1) % nsides == 1)
				r2 = reco_queue_b;
			if (recognizer_queue_length - nsides + (i + 1) % nsides == 2)
				r2 = reco_queue_c;
			if (recognizer_queue_length - nsides + (i + 1) % nsides == 3)
				r2 = reco_queue_d;
			if (recognizer_queue_length - nsides + (i + 1) % nsides == 4)
				r2 = reco_queue_e;
			EuclidianPen.calc_edge_isect(r1, r2, pt);
			r1.reversed = (Math.hypot(pt[0] - r1.x1, pt[1] - r1.y1)) < (Math
					.hypot(pt[0] - r1.x2, pt[1] - r1.y2));
		}
		for (i = 0; i < nsides; ++i) {
			if (recognizer_queue_length - nsides + i == 0)
				r1 = reco_queue_a;
			if (recognizer_queue_length - nsides + i == 1)
				r1 = reco_queue_b;
			if (recognizer_queue_length - nsides + i == 2)
				r1 = reco_queue_c;
			if (recognizer_queue_length - nsides + i == 3)
				r1 = reco_queue_d;
			if (recognizer_queue_length - nsides + i == 4)
				r1 = reco_queue_e;
			if (recognizer_queue_length - nsides + (i + 1) % nsides == 0)
				r2 = reco_queue_a;
			if (recognizer_queue_length - nsides + (i + 1) % nsides == 1)
				r2 = reco_queue_b;
			if (recognizer_queue_length - nsides + (i + 1) % nsides == 2)
				r2 = reco_queue_c;
			if (recognizer_queue_length - nsides + (i + 1) % nsides == 3)
				r2 = reco_queue_d;
			if (recognizer_queue_length - nsides + (i + 1) % nsides == 4)
				r2 = reco_queue_e;
			EuclidianPen.calc_edge_isect(r1, r2, pt);
			dist = Math.hypot((r1.reversed ? r1.x1 : r1.x2) - pt[0],
					(r1.reversed ? r1.y1 : r1.y2) - pt[1])
					+ Math.hypot((r2.reversed ? r2.x2 : r2.x1) - pt[0],
							(r2.reversed ? r2.y2 : r2.y1) - pt[1]);
			if (dist > POLYGON_LINEAR_TOLERANCE * (r1.radius + r2.radius))
				return null;
		}
		for (i = 0; i < nsides; ++i) {
			if (recognizer_queue_length - nsides + i == 0)
				r1 = reco_queue_a;
			if (recognizer_queue_length - nsides + i == 1)
				r1 = reco_queue_b;
			if (recognizer_queue_length - nsides + i == 2)
				r1 = reco_queue_c;
			if (recognizer_queue_length - nsides + i == 3)
				r1 = reco_queue_d;
			if (recognizer_queue_length - nsides + i == 4)
				r1 = reco_queue_e;
			if (recognizer_queue_length - nsides + (i + 1) % nsides == 0)
				r2 = reco_queue_a;
			if (recognizer_queue_length - nsides + (i + 1) % nsides == 1)
				r2 = reco_queue_b;
			if (recognizer_queue_length - nsides + (i + 1) % nsides == 2)
				r2 = reco_queue_c;
			if (recognizer_queue_length - nsides + (i + 1) % nsides == 3)
				r2 = reco_queue_d;
			if (recognizer_queue_length - nsides + (i + 1) % nsides == 4)
				r2 = reco_queue_e;
			EuclidianPen.calc_edge_isect(r1, r2, pt);
			points[2 * i + 2] = pt[0];
			points[2 * i + 3] = pt[1];
		}
		points[0] = points[2 * nsides];
		points[1] = points[2 * nsides + 1];

		GeoPointND[] pts = new GeoPointND[nsides];

		for (i = 0; i < nsides; ++i) {
			x_first = view.toRealWorldCoordX(points[2 * i]);
			y_first = view.toRealWorldCoordY(points[2 * i + 1]);

			if (i == 0 && this.initialPoint != null) {
				pts[0] = initialPoint;
				initialPoint = null;
			} else {
				// null -> created labeled point
				pts[i] = new GeoPoint(cons, null, x_first, y_first, 1.0);
			}

		}

		algo = new AlgoPolygon(cons, null, pts);

		GeoElement poly = algo.getGeoElements()[0];
		// poly.setLineThickness(penSize * 2);
		// poly.setLineType(penLineStyle);
		// poly.setObjColor(penColor);
		poly.updateRepaint();

		return poly;
	}

	private static void calc_edge_isect(RecoSegment r1, RecoSegment r2,
			double pt[]) {
		double t;
		t = (r2.xcenter - r1.xcenter) * Math.sin(r2.angle)
				- (r2.ycenter - r1.ycenter) * Math.cos(r2.angle);
		t = t / Math.sin(r2.angle - r1.angle);
		pt[0] = r1.xcenter + t * Math.cos(r1.angle);
		pt[1] = r1.ycenter + t * Math.sin(r1.angle);
	}

	/**
	 * @param color
	 *            pen color
	 */
	public void setPenColor(GColor color) {
		if (!this.penColor.equals(color)) {
			startNewStroke = true;
		}
		this.penColor = color;

	}

	private boolean absoluteScreenPosition;

	/**
	 * @param b
	 *            true for absolute position (AttachCopyToView)
	 */
	public void setAbsoluteScreenPosition(boolean b) {
		absoluteScreenPosition = b;

	}

	private class RecoSegment {
		public RecoSegment() {
		}

		int startpt = 0, endpt = 0;
		double xcenter = 0, ycenter = 0, angle = 0, radius = 0;
		double x1 = 0, y1 = 0, x2 = 0, y2 = 0;
		boolean reversed;

	}

	private class Inertia {
		public Inertia() {
		}

		double mass = 0;
		double sx = 0;
		double sxx = 0;
		double sy = 0;
		double sxy = 0;
		double syy = 0;
	}

	/**
	 * used for subclasses to return the last shape that was created
	 * 
	 * NOT USED IN THIS CLASS
	 * 
	 * @return null
	 */
	public GeoElement getCreatedShape() {
		return null;
	}

}

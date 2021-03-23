package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;

/**
 * A GeneralPath implementation that does clipping of line segments at the
 * screen in double coordinates. This is important to avoid rendering problems
 * that occur with GeneralPath when coordinates are larger than Float.MAX_VALUE.
 * 
 * @author Markus Hohenwarter
 * @version October 2009
 */
public class GeneralPathClipped implements GShape {

	private final ArrayList<MyPoint> pathPoints;
	private final GGeneralPath gp;
	private static final double MAX_COORD_VALUE = 10000;

	/** view */
	protected EuclidianViewInterfaceSlim view;
	private int lineThickness;

	private double largestCoord;
	private boolean polygon = true;

	private boolean needClosePath;
	private GRectangle2D bounds;
	private double auxX;
	private double auxY;
	// first control point
	private double cont1X = Double.NaN;
	private double cont1Y = Double.NaN;
	// second control point
	private double cont2X = Double.NaN;
	private double cont2Y = Double.NaN;

	private GRectangle2D oldBounds;
	private final ClipAlgoSutherlandHodogman clipAlgoSutherlandHodogman;

	/**
	 * Creates new clipped general path
	 *
	 * @param view
	 *            view
	 */
	public GeneralPathClipped(EuclidianViewInterfaceSlim view) {
		this.view = view;
		pathPoints = new ArrayList<>();
		clipAlgoSutherlandHodogman = new ClipAlgoSutherlandHodogman();
		gp = AwtFactory.getPrototype().newGeneralPath();
	}

	/**
	 * @return first point of the path
	 */
	public MyPoint firstPoint() {
		if (pathPoints.size() == 0) {
			return null;
		}
		return pathPoints.get(0);
	}

	/**
	 * Clears all points and resets internal variables
	 */
	final public void reset() {
		pathPoints.clear();
		gp.reset();
		oldBounds = bounds;
		bounds = null;
		largestCoord = 0;
		needClosePath = false;
	}

	/**
	 * Clears all points and resets internal variables
	 * and the line thickness too.
	 * @param lineThickness line thickness
	 */
	final public void resetWithThickness(int lineThickness) {
		reset();
		this.lineThickness = lineThickness;
	}

	/**
	 * Closes path
	 */
	final public void closePath() {
		needClosePath = true;
	}

	/**
	 * @return this as GeneralPath
	 */
	public GGeneralPath getGeneralPath() {
		if (pathPoints.size() == 0) {
			return gp;
		}

		gp.reset();
		if (largestCoord < MAX_COORD_VALUE || !polygon) {
			addSimpleSegments();
		} else {
			addSegmentsWithSutherladHoloman();
		}

		// clear pathPoints to free up memory
		pathPoints.clear();

		return gp;
	}

	private void addSimpleSegments() {
		for (int i = 0; i < pathPoints.size(); i++) {
			MyPoint curP = pathPoints.get(i);
			/// https://play.google.com/apps/publish/?dev_acc=05873811091523087820#ErrorClusterDetailsPlace:p=org.geogebra.android&et=CRASH&lr=LAST_7_DAYS&ecn=java.lang.NullPointerException&tf=SourceFile&tc=org.geogebra.common.euclidian.GeneralPathClipped&tm=addSimpleSegments&nid&an&c&s=new_status_desc
			if (curP != null) {
				addToGeneralPath(curP, curP.getSegmentType());
			} else {
				Log.error("curP shouldn't be null here");
			}
		}
		if (needClosePath) {
			gp.closePath();
		}
	}

	private void addSegmentsWithSutherladHoloman() {
		int padding = lineThickness + 5;
		double[][] clipPoints = {
				{ -padding, -padding},
				{ -padding, view.getHeight() + padding},
				{ view.getWidth() + padding, view.getHeight() + padding},
				{ view.getWidth(), -padding},
		};

		if (needClosePath) {
			pathPoints.get(0).setLineTo(true);
		}

		ArrayList<MyPoint> result = clipAlgoSutherlandHodogman.process(pathPoints, clipPoints);

		for (MyPoint curP : result) {
			addToGeneralPath(curP, curP.getSegmentType());
		}

		if (result.size() > 0 && needClosePath) {
			gp.closePath();
		}
	}

	private void addToGeneralPath(GPoint2D q, SegmentType lineTo) {
		GPoint2D p = gp.getCurrentPoint();

		if (lineTo == SegmentType.CONTROL) {
			if (Double.isNaN(cont1X) && Double.isNaN(cont1Y)) {
				cont1X = q.getX();
				cont1Y = q.getY();
			} else {
				cont2X = q.getX();
				cont2Y = q.getY();
			}
		} else if (lineTo == SegmentType.CURVE_TO) {
			if (!Double.isNaN(cont1X) && !Double.isNaN(cont1Y)
					&& !Double.isNaN(cont2X) && !Double.isNaN(cont2Y)) {
				gp.curveTo(cont1X, cont1Y, cont2X, cont2Y, q.getX(), q.getY());
				cont1X = Double.NaN;
				cont1Y = Double.NaN;
				cont2X = Double.NaN;
				cont2Y = Double.NaN;
			}
		}
		else if (lineTo == SegmentType.AUXILIARY) {
			auxX = q.getX();
			auxY = q.getY();
		} else if (lineTo == SegmentType.ARC_TO && p != null) {
			try {

				double dx1 = (auxX - p.getX());
				double dy1 = (auxY - p.getY());
				double dx2 = (auxX - q.getX());
				double dy2 = (auxY - q.getY());
				double angle = MyMath.angle(dx1, dy1, dx2, dy2);
				double cv = btan(Math.PI - angle) * Math.tan(angle / 2);
				gp.curveTo(p.getX() + dx1 * cv, p.getY() + dy1 * cv,
						q.getX() + dx2 * cv, q.getY() + dy2 * cv, q.getX(),
						q.getY());

			} catch (Exception e) {
				gp.moveTo(q.getX(), q.getY());
			}
		}
		else if (lineTo == SegmentType.LINE_TO && p != null) {
			try {
				gp.lineTo(q.getX(), q.getY());
			} catch (Exception e) {
				gp.moveTo(q.getX(), q.getY());
			}
		} else {
			gp.moveTo(q.getX(), q.getY());
		}
	}

	private static double btan(double angle) {
		double increment = angle / 2.0;
		return 4.0 / 3.0 * Math.sin(increment) / (1.0 + Math.cos(increment));
	}

	/**
	 * Move to (x,y).
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 */
	final public void moveTo(double x, double y) {
		addPoint(x, y, SegmentType.MOVE_TO);
	}

	/**
	 * Line to (x,y).
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 */
	final public void lineTo(double x, double y) {
		addPoint(x, y, SegmentType.LINE_TO);
	}

	/**
	 * Adds point to point list and keeps track of largest coordinate.
	 * 
	 * @param pos
	 *            insert position
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 */
	final public void addPoint(int pos, double x, double y) {
		if (Double.isNaN(y)) {
			return;
		}

		MyPoint p = new MyPoint(x, y, SegmentType.LINE_TO);
		updateBounds(p);
		pathPoints.ensureCapacity(pos + 1);
		while (pathPoints.size() <= pos) {
			pathPoints.add(null);
		}
		pathPoints.set(pos, p);
	}

	/**
	 * Adds point to point list and keeps track of largest coordinate.
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param segmentType
	 *            path segment type
	 */
	protected final void addPoint(double x, double y, SegmentType segmentType) {
		if (Double.isNaN(y)) {
			return;
		}

		if (segmentType != SegmentType.LINE_TO && segmentType != SegmentType.MOVE_TO) {
			polygon = false;
		}

		MyPoint p = new MyPoint(x, y, segmentType);
		updateBounds(p);
		pathPoints.add(p);
	}

	private void updateBounds(GPoint2D point) {
		double x = point.getX();
		double y = point.getY();
		if (bounds == null) {
			bounds = oldBounds != null ? oldBounds
					: AwtFactory.getPrototype().newRectangle2D();
			bounds.setRect(x, y, 0, 0);
		}

		if (Math.abs(x) > largestCoord) {
			largestCoord = Math.abs(x);
		}
		if (Math.abs(y) > largestCoord) {
			largestCoord = Math.abs(y);
		}

		bounds.add(x, y);
	}

	/**
	 * @return current point
	 */
	public GPoint2D getCurrentPoint() {
		if (pathPoints.size() == 0) {
			return null;
		}
		return pathPoints.get(pathPoints.size() - 1);
	}

	/**
	 * Transforms this path
	 * 
	 * @param af
	 *            transformation
	 */
	public void transform(GAffineTransform af) {
		for (MyPoint p : pathPoints) {
			if (p != null) {
				af.transform(p, p);
			}
		}
	}

	/**
	 * @param p
	 *            point
	 * @return true if contains given point
	 */
	public boolean contains(GPoint2D p) {
		return getGeneralPath().contains(p);
	}

	/**
	 * @param rect
	 *            rectangle
	 * @return true if contains given rectangle
	 */
	@Override
	public boolean contains(GRectangle2D rect) {
		return getGeneralPath().contains(rect);
	}

	@Override
	public boolean contains(double x, double y) {
		return getGeneralPath().contains(x, y);
	}

	/**
	 * @param x
	 *            x min
	 * @param y
	 *            y min
	 * @param w
	 *            width
	 * @param h
	 *            height
	 * @return true if contains rectangle given by args
	 */
	public boolean contains(double x, double y, double w, double h) {
		return getGeneralPath().contains(x, y, w, h);
	}

	@Override
	public boolean contains(int x, int y) {
		// TODO Auto-generated method stub
		return getGeneralPath().contains(x, y);
	}

	/**
	 * @param rectangle
	 *            rectangle to be checked
	 * @return whether rectangle is contained in this path
	 */
	public boolean contains(GRectangle rectangle) {
		// TODO Auto-generated method stub
		return getGeneralPath().contains(rectangle);
	}

	@Override
	public GRectangle getBounds() {
		return bounds == null ? AwtFactory.getPrototype().newRectangle()
				: bounds.getBounds();
	}

	@Override
	public GRectangle2D getBounds2D() {
		return bounds == null ? AwtFactory.getPrototype().newRectangle2D()
				: bounds;
	}

	@Override
	public GPathIterator getPathIterator(GAffineTransform arg0) {
		return getGeneralPath().getPathIterator(arg0);
	}

	@Override
	public boolean intersects(GRectangle2D arg0) {
		return getGeneralPath().intersects(arg0);
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		return getGeneralPath().intersects(x, y, w, h);
	}

	@Override
	public boolean intersects(int x, int y, int w, int h) {
		return getGeneralPath().intersects(x, y, w, h);
	}

	/**
	 * @param x
	 *            center x-coord
	 * @param y
	 *            center y-coord
	 * @param radius
	 *            inradius of the square
	 * @return whether this intersects square with center (x,y) and inradius
	 *         radius
	 */
	public boolean intersects(int x, int y, int radius) {
		return getGeneralPath().intersects(x - radius, y - radius, 2 * radius,
				2 * radius);
	}
}

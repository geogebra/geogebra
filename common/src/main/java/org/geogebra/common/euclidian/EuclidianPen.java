package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.algos.AlgoLocusStroke;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;

import com.google.j2objc.annotations.Weak;
import com.google.j2objc.annotations.WeakOuter;

/**
 * Handles pen and freehand tool
 *
 */
public class EuclidianPen implements GTimerListener {

	/**
	 * app
	 */
	@Weak
	protected App app;
	/**
	 * view
	 */
	protected EuclidianView view;

	/** Polyline that conects stylebar to pen settings */
	@WeakOuter
	public final GeoPolyLine defaultPenLine;

	private AlgoLocusStroke lastAlgo = null;
	/** points created by pen */
	protected ArrayList<GPoint> penPoints = new ArrayList<>();

	// segment
	private final static int PEN_SIZE_FACTOR = 2;
	/** skip intermediate points on segments longer than this */
	private static final double MAX_POINT_DIST = 30;
	/** ignore consecutive pen points closer than this */
	private static final double MIN_POINT_DIST = 3;
	private static final double MAX_POINT_COS = Math.cos(Math.PI / 36);

	private boolean startNewStroke = false;

	private int penSize;
	private int lineOpacity;

	/**
	 * start point of the gesture
	 */
	protected GeoPoint initialPoint = null;

	/**
	 * delete initialPoint if no shape is found
	 */
	protected boolean deleteInitialPoint = false;

	private GTimer timer;

	private int penLineStyle;
	private GColor penColor = GColor.BLACK;
	private final PenPreviewLine penPreviewLine;
	protected final ArrayList<GPoint> previewPoints = new ArrayList<>();

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
		this.penPreviewLine = view.newPenPreview();
		timer = app.newTimer(this, 1500);

		@WeakOuter GeoPolyLine line = new GeoPolyLine(app.getKernel().getConstruction()) {
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

			@Override
			public void setLineOpacity(int lineOpacity) {
				super.setLineOpacity(lineOpacity);
				setPenOpacity(lineOpacity);
			}
		};
		defaultPenLine = line;
		setDefaults();
		defaultPenLine.setLineThickness(penSize);
		defaultPenLine.setLineOpacity(lineOpacity);
		defaultPenLine.setObjColor(penColor);
	}

	// ===========================================
	// Getters/Setters
	// ===========================================

	/**
	 * Set default pen color, line style, thickness, eraser size
	 */
	public void setDefaults() {
		penSize = EuclidianConstants.DEFAULT_PEN_SIZE;
		penLineStyle = EuclidianStyleConstants.LINE_TYPE_FULL;
		penColor = GColor.BLACK;
		lineOpacity = 85 * 255 / 100;
	}

	/**
	 * @return pen size
	 */
	public int getPenSize() {
		return penSize;
	}

	/**
	 * @param lineOpacity
	 *            Opacity
	 */
	public void setPenOpacity(int lineOpacity) {
		if (this.lineOpacity != lineOpacity) {
			startNewStroke = true;
		}
		this.lineOpacity = lineOpacity;
		setPenColor(penColor.deriveWithAlpha(lineOpacity));
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

	/**
	 *
	 * @param e
	 *            event
	 * @return Is this MouseEvent an erasing Event.
	 */
	public boolean isErasingEvent(AbstractEvent e) {
		return app.isRightClick(e) && !isFreehand();
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
		if (isErasingEvent(e)) {
			view.getEuclidianController().getDeleteMode()
					.handleMouseDraggedForDelete(e, true);
			app.getKernel().notifyRepaint();
		} else {
			// drawing in progress, so we need repaint
			addPointPenMode(e);
		}
	}

	/**
	 * @param e
	 *            event
	 */
	public void handleMousePressedForPenMode(AbstractEvent e) {
		if (!isErasingEvent(e)) {
			timer.stop();

			view.cacheGraphics();
			addPointPenMode(e);
		}
	}

	/**
	 * Method to repaint the whole preview line from (x, y) with a given width.
	 * 
	 * @param g2D
	 *            graphics for pen
	 * @param color
	 *            of the pen preview
	 * @param thickness
	 *            of the pen preview
	 * @param x
	 *            Start x coordinate
	 * @param y
	 *            Start y coordinate
	 * @param width
	 *            of the preview
	 */
	public void drawStylePreview(GGraphics2D g2D, GColor color, int thickness,
			int x, int y, int width) {
		GGeneralPath gp = AwtFactory.getPrototype().newGeneralPath();
		g2D.setStroke(EuclidianStatic.getStroke(thickness,
				EuclidianStyleConstants.LINE_TYPE_FULL));
		g2D.setColor(color);
		gp.reset();
		gp.moveTo(x, y);
		gp.lineTo(x + width, y);
		g2D.draw(gp);
	}

	/**
	 * add the saved points to the last stroke or create a new one
	 *
	 * @param e
	 *            event
	 */
	public void addPointPenMode(AbstractEvent e) {
		GPoint newPoint = new GPoint(e.getX(), e.getY());
		previewPoints.add(newPoint);
		view.repaintView();
		addPointPenMode(newPoint);
	}

	/**
	 * Append point to the list
	 * 
	 * @param newPoint
	 *            new point
	 */
	protected void addPointPenMode(GPoint newPoint) {
		if (penPoints.size() == 0) {
			if (initialPoint != null) {
				// also add the coordinates of the initialPoint to the penPoints
				Coords coords = initialPoint.getCoords();
				// calculate the screen coordinates
				int locationX = (int) (view.getXZero()
						+ (coords.getX() / view.getInvXscale()));
				int locationY = (int) (view.getYZero()
						- (coords.getY() / view.getInvYscale()));

				GPoint p = new GPoint(locationX, locationY);
				penPoints.add(p);
			}
			penPoints.add(newPoint);
		} else {
			GPoint p1 = penPoints.get(penPoints.size() - 1);
			double dist = p1.distance(newPoint);
			if (isFreehand()) {
				if (dist > MIN_POINT_DIST) {
					penPoints.add(newPoint);
				}
				return;
			}
			GPoint p2 = penPoints.size() >= 2
					? penPoints.get(penPoints.size() - 2) : null;
			GPoint p3 = tailStart(newPoint);
			if (dist > MIN_POINT_DIST) {
				if (dist > MAX_POINT_DIST || p3 == null || p2 == null) {
					penPoints.add(newPoint);
				} else {
					p2.x = (p1.x + p2.x) / 2;
					p2.y = (p1.y + p2.y) / 2;
					p1.x = newPoint.x;
					p1.y = newPoint.y;
				}
			}
		}

	}

	private GPoint tailStart(GPoint newPoint) {
		for (int i = 3; i < penPoints.size(); i++) {
			GPoint current = penPoints.get(penPoints.size() - i);
			if (current.distance(newPoint) > 2 * MAX_POINT_DIST) {
				return null;
			}
			boolean anglesOK = true;
			for (int j = 1; j < i; j++) {
				if (angle(newPoint, penPoints.get(penPoints.size() - j),
						current, MAX_POINT_COS)) {
					anglesOK = false;
				}
			}
			if (anglesOK) {
				return current;
			}
		}
		return null;
	}

	private static boolean angle(GPoint a, GPoint b, GPoint c, double max) {
		if (a == null || b == null || c == null) {
			return true;
		}
		double dx1 = a.x - b.x;
		double dx2 = c.x - b.x;
		double dy1 = a.y - b.y;
		double dy2 = c.y - b.y;
		double ret = Math.abs(dx1 * dx2 + dy1 * dy2) / Math.hypot(dx1, dy1)
				/ Math.hypot(dx2, dy2);
		return Double.isNaN(ret) || ret < max;
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
	public void handleMouseReleasedForPenMode(boolean right, int x, int y,
												 boolean isPinchZooming) {
		if (right || penPoints.size() == 0) {
			return;
		}

		if (isPinchZooming && penPoints.size() < 2) {
			penPoints.clear();
		}

		timer.start();
		String oldXML = null;
		if (lastAlgo != null && !startNewStroke) {
			oldXML = lastAlgo.getXML();
		}

		app.setDefaultCursor();

		addPointsToPolyLine(penPoints);

		penPoints.clear();
		previewPoints.clear();

		String label = lastAlgo.getOutput(0).getLabelSimple();

		if (oldXML == null) {
			app.getUndoManager().storeUndoableAction(EventType.ADD, label,
					lastAlgo.getXML());
		} else {
			app.getUndoManager().storeUndoableAction(EventType.UPDATE, label,
					oldXML, lastAlgo.getXML());
		}
	}

	/**
	 * start timer to check if polyline is same stroke
	 */
	public void startTimer() {
		timer.start();
	}

	/**
	 * Reset the first point
	 */
	protected void resetInitialPoint() {
		if (this.deleteInitialPoint && this.initialPoint != null) {
			this.initialPoint.remove();
		}
		this.initialPoint = null;
	}

	private void addPointsToPolyLine(ArrayList<GPoint> penPoints) {
		Construction cons = app.getKernel().getConstruction();
		if (startNewStroke) {
			lastAlgo = null;
			startNewStroke = false;
		}

		ArrayList<MyPoint> newPts = new ArrayList<>(penPoints.size());
		for (GPoint p : penPoints) {
			double x = view.toRealWorldCoordX(p.getX());
			double y = view.toRealWorldCoordY(p.getY());

			// change -2.4600000000000004 to -2.46 for smaller XML
			newPts.add(new MyPoint(DoubleUtil.checkDecimalFraction(x),
					DoubleUtil.checkDecimalFraction(y)));
		}

		// don't set label
		if (lastAlgo != null) {
			lastAlgo.getPenStroke().appendPointArray(newPts);
			lastAlgo.getOutput(0).updateRepaint();
			return;
		}

		lastAlgo = new AlgoLocusStroke(cons, newPts);

		GeoElement stroke = lastAlgo.getOutput(0);

		stroke.setLineThickness(penSize * PEN_SIZE_FACTOR);
		stroke.setLineType(penLineStyle);
		stroke.setLineOpacity(lineOpacity);
		stroke.setObjColor(penColor);
		stroke.updateVisualStyle(GProperty.COMBINED);
		stroke.setVisibility(view.getViewID(), true);

		// set label
		stroke.setLabel(null);
		stroke.setTooltipMode(GeoElementND.TOOLTIP_OFF);
	}

	/**
	 * @param penColor
	 *            pen color
	 */
	public void setPenColor(GColor penColor) {
		if (!this.penColor.equals(penColor)) {
			startNewStroke = true;
		}
		this.penColor = penColor;
	}

	/**
	 * Update state of the pen after geo is removed
	 * 
	 * @param geo
	 *            removed element
	 */
	public void remove(GeoElement geo) {
		if (geo.getParentAlgorithm() == this.lastAlgo) {
			lastAlgo = null;
		}

	}

	@Override
	public void onRun() {
		startNewStroke = true;
	}

	/**
	 * @return whether this is freehand pen tool
	 */
	public boolean isFreehand() {
		return false;
	}

	/**
	 * Set the correct stroke style, and repaint if needed
	 * @param g2 graphics
	 */
	public void setStyleAndRepaint(GGraphics2D g2) {
		g2.setStroke(EuclidianStatic.getStroke(getPenSize(),
				getPenLineStyle(), GBasicStroke.JOIN_ROUND));
		g2.setColor(getPenColor());
		repaintIfNeeded(g2);
	}

	/**
	 * Paint on graphics if needed
	 * @param g2 graphics
	 */
	public void repaintIfNeeded(GGraphics2D g2) {
		if (!previewPoints.isEmpty()) {
			penPreviewLine.drawPolyline(previewPoints, g2);
		}
	}
}

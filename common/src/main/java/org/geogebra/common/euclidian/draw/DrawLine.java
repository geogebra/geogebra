/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * DrawLine.java
 *
 * Created on 11. Oktober 2001, 23:59
 */

package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.Lineable2D;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.MyMath;

/**
 * Draws a line or a ray.
 */
public class DrawLine extends SetDrawable implements Previewable {

	// clipping attributes
	private static final int LEFT = 0;
	private static final int RIGHT = 1;
	private static final int TOP = 2;
	private static final int BOTTOM = 3;

	/** preview types */
	public enum PreviewType {
		/** none */
		NONE,
		/** line through points */
		LINE,
		/** parallel line */
		PARALLEL,
		/** perpendicular line */
		PERPENDICULAR,
		/** perpendicular bisector */
		PERPENDICULAR_BISECTOR,
		/** angle bisector */
		ANGLE_BISECTOR
	}

	private GeoLineND g;
	// private double [] coeffs = new double[3];

	private GLine2D line;
	/** y-coord of first endpoint */
	public double y1;
	/** y-coord of second endpoint */
	public double y2;
	/** x-coord of first endpoint */
	public double x1;
	/** x-coord of second endpoint */
	public double x2;

	private double k;
	private double d;
	private double gx;
	private double gy;
	private double gz;
	private int labelPos = LEFT;
	private int p1Pos;
	private int p2Pos;
	private int x;
	private int y;
	private boolean isVisible;
	private boolean labelVisible;

	private PreviewType previewMode = PreviewType.NONE;
	private boolean isPreviewVisible;

	private ArrayList<GeoPointND> points; // for preview
	private ArrayList<GeoLineND> lines; // for preview
	private ArrayList<GeoFunction> functions; // for preview
	private GeoPointND startPoint;
	private GeoPointND previewPoint2;

	private GPoint2D endPoint = new GPoint2D();
	private final Coords coordsForMousePos = new Coords(4);

	// clipping attributes
	private boolean[] attr1 = new boolean[4];
	private boolean[] attr2 = new boolean[4];

	/**
	 * Creates new DrawLine
	 * 
	 * @param view
	 *            view
	 * @param g
	 *            line
	 */
	public DrawLine(EuclidianView view, GeoLineND g) {
		this.view = view;
		this.g = g;
		geo = (GeoElement) g;
		update();
	}

	/**
	 * Creates a new DrawLine for preview.
	 * 
	 * @param view
	 *            view
	 * @param points
	 *            preview points
	 * @param previewMode
	 *            preview mode
	 */
	public DrawLine(EuclidianView view, ArrayList<GeoPointND> points,
			PreviewType previewMode) {
		this.previewMode = previewMode;
		this.view = view;
		this.points = points;
		g = new GeoLine(view.getKernel().getConstruction());
		geo = view.getKernel().getConstruction().getConstructionDefaults()
				.getDefaultGeo(ConstructionDefaults.DEFAULT_LINE);
		updatePreview();
	}

	/**
	 * Creates a new DrawLine for preview of parallel or perpendicular tool
	 * 
	 * @param view
	 *            view
	 * @param points
	 *            preview points
	 * @param lines
	 *            preview lines
	 * @param functions
	 * 			  preview functions
	 * @param parallel
	 *            true for paralel, false for perpendicular
	 */
	public DrawLine(EuclidianView view, ArrayList<GeoPointND> points,
					ArrayList<GeoLineND> lines, ArrayList<GeoFunction> functions,
					boolean parallel) {
		if (parallel) {
			previewMode = PreviewType.PARALLEL;
		} else {
			previewMode = PreviewType.PERPENDICULAR;
		}
		this.view = view;
		this.points = points;
		this.lines = lines;
		this.functions = functions;
		g = new GeoLine(view.getKernel().getConstruction());
		geo = view.getKernel().getConstruction().getConstructionDefaults()
				.getDefaultGeo(ConstructionDefaults.DEFAULT_LINE);
		updatePreview();
	}

	@Override
	public void update() {
		update(view.getMatrix());
	}

	/**
	 * update the drawable with a view matrix
	 * 
	 * @param matrix
	 *            view matrix
	 */
	public void update(CoordMatrix matrix) {
		// take line g here, not geo this object may be used for conics too
		isVisible = geo.isEuclidianVisible();
		if (isVisible) {
			labelVisible = getTopLevelGeo().isLabelVisible();
			updateStrokes(g);

			Coords equation = g.getCartesianEquationVector(matrix);

			if (equation == null || !equation.isFinite()) {
				isVisible = false;
				return;
			}

			gx = equation.getX();
			gy = equation.getY();
			gz = equation.getZ();

			setClippedLine(view.getMinXScreen(), view.getMinYScreen(),
					view.getMaxXScreen(), view.getMaxYScreen());

			// line on screen?
			if (!view.intersects(line)) {
				isVisible = false;
				// don't return here to make sure that getBounds() works for
				// offscreen points too
			}

			// draw trace
			if (g.getTrace()) {
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
				labelDesc = getTopLevelGeo().getLabelDescription();
				setLabelPosition();
				addLabelOffsetEnsureOnScreen(view.getFontLine());
			}
		}
	}

	// transform line to screen coords
	// write start and endpoint into (x1,y1), (x2,y2)
	private final void setClippedLine(double minx, double miny, double maxx,
			double maxy) {
		// first calc two points in screen coords that are on the line

		// abs(slope) < 1
		// y = k x + d
		// x1 = 0, x2 = width
		if (Math.abs(gx) * view.getScaleRatio() < Math.abs(gy)) {
			// calc points on line in screen coords
			k = gx / gy * view.getScaleRatio();
			d = view.getYZero() + gz / gy * view.getYscale()
					- k * view.getXZero();

			x1 = view.getMinXScreen() - EuclidianStatic.CLIP_DISTANCE;
			y1 = k * x1 + d;
			x2 = view.getMaxXScreen() + EuclidianStatic.CLIP_DISTANCE;
			y2 = k * x2 + d;
			p1Pos = LEFT;
			p2Pos = RIGHT;
			clipTopBottom(minx, miny, maxy);
		}
		// abs(slope) >= 1
		// x = k y + d
		// y1 = height, y2 = 0
		else {
			// calc points on line in screen coords
			k = gy / (gx * view.getScaleRatio());
			d = view.getXZero() - gz / gx * view.getXscale()
					- k * view.getYZero();

			y1 = maxy + EuclidianStatic.CLIP_DISTANCE;
			x1 = k * y1 + d;
			y2 = miny - EuclidianStatic.CLIP_DISTANCE;
			x2 = k * y2 + d;
			p1Pos = BOTTOM;
			p2Pos = TOP;
			clipLeftRight(minx, maxx);
		}

		if (line == null) {
			line = AwtFactory.getPrototype().newLine2D();
		}
		line.setLine(x1, y1, x2, y2);
	}

	// Cohen & Sutherland algorithm for line clipping on a rectangle
	// Computergraphics I (Prof. Held) pp.100
	// points (0, y1), (width, y2) -> clip on y=0 and y=height
	final private void clipTopBottom(double minx, double miny, double maxy) {
		// calc clip attributes for both points (x1,y1), (x2,y2)
		attr1[TOP] = y1 < minx - EuclidianStatic.CLIP_DISTANCE;
		attr1[BOTTOM] = y1 > maxy + EuclidianStatic.CLIP_DISTANCE;
		attr2[TOP] = y2 < miny - EuclidianStatic.CLIP_DISTANCE;
		attr2[BOTTOM] = y2 > maxy + EuclidianStatic.CLIP_DISTANCE;

		// both points outside (TOP or BOTTOM)
		if ((attr1[TOP] && attr2[TOP]) || (attr1[BOTTOM] && attr2[BOTTOM])) {
			return;
		}
		// at least one point inside -> clip
		// point1 TOP -> clip with y=0
		if (attr1[TOP]) {
			y1 = miny - EuclidianStatic.CLIP_DISTANCE;
			x1 = (y1 - d) / k;
			p1Pos = TOP;
		}
		// point1 BOTTOM -> clip with y=height
		else if (attr1[BOTTOM]) {
			y1 = maxy + EuclidianStatic.CLIP_DISTANCE;
			x1 = (y1 - d) / k;
			p1Pos = BOTTOM;
		}

		// point2 TOP -> clip with y=0
		if (attr2[TOP]) {
			y2 = miny - EuclidianStatic.CLIP_DISTANCE;
			x2 = (y2 - d) / k;
			p2Pos = TOP;
		}
		// point2 BOTTOM -> clip with y=height
		else if (attr2[BOTTOM]) {
			y2 = maxy + EuclidianStatic.CLIP_DISTANCE;
			x2 = (y2 - d) / k;
			p2Pos = BOTTOM;
		}
	}

	// Cohen & Sutherland algorithm for line clipping on a rectangle
	// Computergraphics I (Prof. Held) pp.100
	// points (x1, 0), (x2, height) -> clip on x=0 and x=width
	final private void clipLeftRight(double minx, double maxx) {
		// calc clip attributes for both points (x1,y1), (x2,y2)
		attr1[LEFT] = x1 < minx - EuclidianStatic.CLIP_DISTANCE;
		attr1[RIGHT] = x1 > maxx + EuclidianStatic.CLIP_DISTANCE;
		attr2[LEFT] = x2 < minx - EuclidianStatic.CLIP_DISTANCE;
		attr2[RIGHT] = x2 > maxx + EuclidianStatic.CLIP_DISTANCE;

		// both points outside (LEFT or RIGHT)
		if ((attr1[LEFT] && attr2[LEFT]) || (attr1[RIGHT] && attr2[RIGHT])) {
			return;
		}
		// at least one point inside -> clip
		// point1 LEFT -> clip with x=0
		if (attr1[LEFT]) {
			x1 = minx - EuclidianStatic.CLIP_DISTANCE;
			y1 = (x1 - d) / k;
			p1Pos = LEFT;
		}
		// point1 RIGHT -> clip with x=width
		else if (attr1[RIGHT]) {
			x1 = maxx + EuclidianStatic.CLIP_DISTANCE;
			y1 = (x1 - d) / k;
			p1Pos = RIGHT;
		}

		// point2 LEFT -> clip with x=0
		if (attr2[LEFT]) {
			x2 = minx - EuclidianStatic.CLIP_DISTANCE;
			y2 = (x2 - d) / k;
			p2Pos = LEFT;
		}
		// point2 RIGHT -> clip with x=width
		else if (attr2[RIGHT]) {
			x2 = maxx + EuclidianStatic.CLIP_DISTANCE;
			y2 = (x2 - d) / k;
			p2Pos = RIGHT;
		}
	}

	// set label position (xLabel, yLabel)
	private final void setLabelPosition() {
		// choose smallest position change
		// 1-Norm distance between old label position
		// and point 1, point 2
		if (Math.abs(xLabel - x1)
				+ Math.abs(yLabel - y1) > Math.abs(xLabel - x2)
						+ Math.abs(yLabel - y2)) {
			x = (int) x2;
			y = (int) y2;
			labelPos = p2Pos;
		} else {
			x = (int) x1;
			y = (int) y1;
			labelPos = p1Pos;
		}

		// constant to respect slope of line for additional space
		// slope for LEFT, RIGHT: k = gx/gy
		// slope for TOP, BOTTOM: 1/k = gy/gx
		switch (labelPos) {
		case LEFT:
			xLabel = 5;
			if (2 * y < view.getHeight()) {
				yLabel = y + 16 + (int) (16 * (gx / gy));
			} else {
				yLabel = y - 8 + (int) (16 * (gx / gy));
			}
			break;

		case RIGHT:
			xLabel = view.getWidth() - 15;
			if (2 * y < view.getHeight()) {
				yLabel = y + 16 - (int) (16 * (gx / gy));
			} else {
				yLabel = y - 8 - (int) (16 * (gx / gy));
			}
			break;

		case TOP:
			yLabel = 15;
			if (2 * x < view.getWidth()) {
				xLabel = x + 8 + (int) (16 * (gy / gx));
			} else {
				xLabel = x - 16 + (int) (16 * (gy / gx));
			}
			break;

		case BOTTOM:
			yLabel = view.getHeight() - 5;
			if (2 * x < view.getWidth()) {
				xLabel = x + 8 - (int) (16 * (gy / gx));
			} else {
				xLabel = x - 16 - (int) (16 * (gy / gx));
			}
			break;
		}
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (isVisible) {
			if (isHighlighted()) {
				// draw line
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				g2.draw(line);
			}

			// draw line
			g2.setPaint(getObjectColor());
			g2.setStroke(objStroke);
			g2.draw(line);

			// label
			if (labelVisible) {
				g2.setFont(view.getFontLine());
				g2.setColor(geo.getLabelColor());
				drawLabel(g2);
			}
		}
	}

	/**
	 * Draws a horizontal segment just used for preview line style
	 * 
	 * @param g2
	 *            Graphics to draw.
	 * @param marginX
	 *            Space from left and right.
	 * @param marginY
	 *            Space from top and bottom.
	 * @param width
	 *            The width of the segment.
	 * 
	 */
	public void drawStylePreview(GGraphics2D g2, int marginX, int marginY,
			int width) {
		updateStrokes(geo);
		g2.setStroke(objStroke);
		g2.setColor(geo.getObjectColor());
		g2.drawStraightLine(marginX, marginY, width - marginX, marginY);

	}

	@Override
	public final void drawTrace(GGraphics2D g2) {
		g2.setPaint(getObjectColor());
		g2.setStroke(objStroke);
		g2.draw(line);
	}

	@Override
	final public void updatePreview() {
		switch (previewMode) {
		default:
		case LINE:
		case PERPENDICULAR_BISECTOR:
			isVisible = (points.size() == 1);
			if (isVisible) {
				startPoint = points.get(0);
			}
			break;
		case PARALLEL:
		case PERPENDICULAR:
			isVisible = lines.size() == 1 || functions.size() == 1;
			break;
		case ANGLE_BISECTOR:
			isVisible = (points.size() == 2);
			if (isVisible) {
				startPoint = points.get(0);
				previewPoint2 = points.get(1);
			}
			break;
		}

	}

	@Override
	public void updateMousePos(double mouseRWx, double mouseRWy) {
		double xRW = mouseRWx;
		double yRW = mouseRWy;
		isPreviewVisible = false;

		if (isVisible) {

			switch (previewMode) {
			default:
			case LINE:

				// round angle to nearest 15 degrees if alt pressed
				if (points.size() == 1
						&& view.getEuclidianController().isAltDown()) {
					GeoPointND p = points.get(0);
					double px = p.getInhomX();
					double py = p.getInhomY();
					double angle = Math.atan2(yRW - py, xRW - px) * 180
							/ Math.PI;
					double radius = Math.sqrt(
							(py - yRW) * (py - yRW) + (px - xRW) * (px - xRW));

					// round angle to nearest 15 degrees
					angle = Math.round(angle / 15) * 15;

					xRW = px + radius * Math.cos(angle * Math.PI / 180);
					yRW = py + radius * Math.sin(angle * Math.PI / 180);

					endPoint.setLocation(xRW, yRW);
					view.getEuclidianController().setLineEndPoint(endPoint);
				} else {
					view.getEuclidianController().setLineEndPoint(null);
				}

				// line through first point and mouse position
				// coords = startPoint.getCoordsInD2().crossProduct(new
				// Coords(xRW, yRW, 1));
				this.coordsForMousePos.setCrossProduct4(
						view.getCoordsForView(startPoint.getInhomCoordsInD3())
								.projectInfDim(),
						new Coords(xRW, yRW, 1));

				((GeoLine) g).setCoords(coordsForMousePos.getX(),
						coordsForMousePos.getY(), coordsForMousePos.getZ());
				// GeoVec3D.cross(startPoint, xRW, yRW, 1.0, g);

				break;

			case PARALLEL:
				Lineable2D linePreview = null;

				if (functions.size() == 1) {
					linePreview = functions.get(0);
				} else if (lines.size() == 1) {
					linePreview = (Lineable2D) lines.get(0);
				} else {
					break;
				}

				// calc the line g through (xRW,yRW) and parallel to l
				GeoVec3D.cross(xRW, yRW, 1.0, linePreview.getY(), -linePreview.getX(),
						0.0, ((GeoLine) g));
				break;
			case PERPENDICULAR:
				linePreview = null;

				if (functions.size() == 1) {
					linePreview = functions.get(0);
				} else if (lines.size() == 1) {
					linePreview = (Lineable2D) lines.get(0);
				} else {
					break;
				}

				// calc the line g through (xRW,yRW) and perpendicular to l
				GeoVec3D.cross(xRW, yRW, 1.0, linePreview.getX(), linePreview.getY(),
						0.0, ((GeoLine) g));
				break;
			case PERPENDICULAR_BISECTOR:
				// calc the perpendicular bisector
				coordsForMousePos.set(startPoint.getInhomCoordsInD2());
				double startx = coordsForMousePos.getX();
				double starty = coordsForMousePos.getY();
				GeoVec3D.cross((xRW + startx) / 2, (yRW + starty) / 2, 1.0,
						-yRW + starty, xRW - startx, 0.0, ((GeoLine) g));

				break;
			case ANGLE_BISECTOR:
				GeoLine g1 = new GeoLine(view.getKernel().getConstruction());
				GeoLine h = new GeoLine(view.getKernel().getConstruction());

				// GeoVec3D.cross(previewPoint2, startPoint, g1);
				// GeoVec3D.cross(previewPoint2, xRW, yRW, 1.0, h);

				coordsForMousePos.setCrossProduct4(
						previewPoint2.getCoordsInD2(),
						startPoint.getCoordsInD2());
				g1.setCoords(coordsForMousePos.getX(), coordsForMousePos.getY(),
						coordsForMousePos.getZ());
				coordsForMousePos.setCrossProduct4(
						previewPoint2.getCoordsInD2(),
						new Coords(xRW, yRW, 1));
				h.setCoords(coordsForMousePos.getX(), coordsForMousePos.getY(),
						coordsForMousePos.getZ());

				// (gx, gy) is direction of g = B v A
				double g2x = g1.y;
				double g2y = -g1.x;
				double lenG = MyMath.length(g2x, g2y);
				g2x /= lenG;
				g2y /= lenG;

				// (hx, hy) is direction of h = B v C
				double hx = h.y;
				double hy = -h.x;
				double lenH = MyMath.length(hx, hy);
				hx /= lenH;
				hy /= lenH;

				// set direction vector of bisector: (wx, wy)
				double wx, wy;

				// calc direction vector (wx, wy) of angular bisector
				// check if angle between vectors is > 90 degrees
				double ip = g2x * hx + g2y * hy;
				if (ip >= 0.0) { // angle < 90 degrees
					// standard case
					wx = g2x + hx;
					wy = g2y + hy;
				} else { // ip <= 0.0, angle > 90 degrees
							// BC - BA is a normalvector of the bisector
					wx = hy - g2y;
					wy = g2x - hx;

					// if angle > 180 degree change orientation of direction
					// det(g,h) < 0
					if (g2x * hy < g2y * hx) {
						wx = -wx;
						wy = -wy;
					}
				}

				// make (wx, wy) a unit vector
				double length = MyMath.length(wx, wy);
				wx /= length;
				wy /= length;

				// wv.x = wx;
				// wv.y = wy;

				// set bisector
				this.coordsForMousePos.set(previewPoint2.getInhomCoordsInD2());
				((GeoLine) g).x = -wy;
				((GeoLine) g).y = wx;
				((GeoLine) g).z = -(coordsForMousePos.getX() * ((GeoLine) g).x
						+ coordsForMousePos.getY() * ((GeoLine) g).y);

				break;
			}

			if (((GeoLine) g).isZero()) {
				isVisible = false;
				return;
			}

			isPreviewVisible = true;
			gx = ((GeoLine) g).x;
			gy = ((GeoLine) g).y;
			gz = ((GeoLine) g).z;
			setClippedLine(view.getMinXScreen(), view.getMinYScreen(),
					view.getMaxXScreen(), view.getMaxYScreen());

		}

	}

	@Override
	final public void drawPreview(GGraphics2D g2) {
		if (isPreviewVisible) {
			g2.setPaint(getObjectColor());
			updateStrokes(geo);
			g2.setStroke(objStroke);
			g2.draw(line);
		}
	}

	@Override
	public void disposePreview() {
		// do nothing
	}

	/**
	 * was this object clicked at? (mouse pointer location (x,y) in screen
	 * coords)
	 */
	@Override
	final public boolean hit(int screenx, int screeny, int hitThreshold) {
		return isVisible && line.intersects(screenx - hitThreshold,
				screeny - hitThreshold, 2 * hitThreshold, 2 * hitThreshold);
	}

	@Override
	final public boolean isInside(GRectangle rect) {
		return false;
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		return line != null && line.intersects(rect);
	}

	@Override
	final public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	@Override
	public GArea getShape() {
		return getShape(false);
	}

	/**
	 * @param forConic
	 *            when true, we select the part containing top right screen
	 *            corner. otherwise we pick the one above the line.
	 * @return one halfplane wrt this line
	 */
	public GArea getShape(boolean forConic) {
		GeneralPathClipped gpc = new GeneralPathClipped(view);
		gpc.resetWithThickness(geo.getLineThickness());
		boolean invert = g.isInverseFill();
		if (x1 > x2) {
			double swap = x1;
			x1 = x2;
			x2 = swap;
			swap = y1;
			y1 = y2;
			y2 = swap;
		}
		gpc.moveTo(x1, y1);
		gpc.lineTo(x2, y2);
		// cross top and bottom
		if (x1 > 0 && x2 <= view.getWidth()) {
			if (y2 < y1) {
				gpc.lineTo(0, 0);
				gpc.lineTo(0, view.getHeight());
			} else {
				gpc.lineTo(0, view.getHeight());
				gpc.lineTo(0, 0);
				if (!forConic) {
					invert = !invert;
				}
			}
		}
		// cross top/bottom and right
		else if (x1 > 0 && x2 > view.getWidth()) {
			gpc.lineTo(view.getWidth(), y1);
			invert ^= forConic || (y1 > 0);
		}
		// cros left and bottom/top
		else if (x1 <= 0 && x2 <= view.getWidth()) {
			gpc.lineTo(0, y2);
			invert ^= y2 > 0;
		}
		// cross left and right
		else {
			gpc.lineTo(view.getWidth(), 0);
			gpc.lineTo(0, 0);

		}
		gpc.closePath();
		GArea gpcArea = AwtFactory.getPrototype().newArea(gpc);
		if (!invert) {
			return gpcArea;
		}
		GArea complement = AwtFactory.getPrototype()
				.newArea(view.getBoundingPath());
		complement.subtract(gpcArea);
		return complement;
	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 */
	@Override
	final public GRectangle getBounds() {
		if (line == null || !geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}
		return AwtFactory.getPrototype().newRectangle(line.getBounds());
	}

	/**
	 * @return whether this is visible
	 */
	public boolean isVisible() {
		return isVisible;
	}

}

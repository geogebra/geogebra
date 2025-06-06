/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * DrawSegment
 *
 * Created on 21. 8 . 2003
 */

package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.euclidian.clipping.ClipLine;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;

/**
 * 
 * @author Markus Hohenwarter
 */
public class DrawSegment extends SetDrawable implements Previewable, EndDecoratedDrawable {

	private GPoint2D[] tmpClipPoints = {new GPoint2D(), new GPoint2D()};

	private final GeoLineND segment;

	private boolean isVisible;
	private boolean labelVisible;
	private ArrayList<GeoPointND> points;

	private GLine2D line;
	private double[] coordsA = new double[2];
	private double[] coordsB = new double[2];

	// For drawing ticks
	private GLine2D[] decoTicks;

	private GPoint2D endPoint = new GPoint2D();
	private DrawSegmentWithEndings segmentWithEndings = null;

	/**
	 * Creates new DrawSegment
	 * 
	 * @param view
	 *            Euclidian view to be used
	 * @param segment
	 *            Segment to be drawn
	 */
	public DrawSegment(EuclidianView view, GeoLineND segment) {
		this.view = view;
		this.segment = segment;
		geo = (GeoElement) segment;
		if (geo instanceof GeoSegment) {
			segmentWithEndings = new DrawSegmentWithEndings(this, (GeoSegment) segment);
		}
		update();
	}

	/**
	 * Creates a new DrawSegment for preview.
	 * 
	 * @param view
	 *            Euclidian view to be used
	 * @param points
	 *            endpoints of the segment
	 */
	public DrawSegment(EuclidianView view, ArrayList<GeoPointND> points) {
		this.view = view;
		this.points = points;

		geo = view.getKernel().getConstruction().getConstructionDefaults()
				.getDefaultGeo(ConstructionDefaults.DEFAULT_SEGMENT);
		segment = (GeoSegment) geo;
		updatePreview();
	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible) {
			return;
		}

		Coords A = view.getCoordsForView(segment.getStartInhomCoords());
		// check if in view
		if (!DoubleUtil.isZero(A.getZ()) || !A.isFinite()) {
			isVisible = false;
			return;
		}
		Coords B = view.getCoordsForView(segment.getEndInhomCoords());
		// check if in view
		if (!DoubleUtil.isZero(B.getZ()) || !B.isFinite()) {
			isVisible = false;
			return;
		}

		update(A, B);
	}

	/**
	 * update with A, B for end points
	 * 
	 * @param A
	 *            end point
	 * @param B
	 *            end point
	 */
	final public void update(Coords A, Coords B) {

		labelVisible = geo.isLabelVisible();
		updateStrokes(geo);

		coordsA[0] = A.getX();
		coordsA[1] = A.getY();
		coordsB[0] = B.getX();
		coordsB[1] = B.getY();

		boolean onscreenA = view.toScreenCoords(coordsA);
		boolean onscreenB = view.toScreenCoords(coordsB);

		if (line == null) {
			line = AwtFactory.getPrototype().newLine2D();
		}

		if (onscreenA && onscreenB) {
			// A and B on screen
			line.setLine(coordsA[0], coordsA[1], coordsB[0], coordsB[1]);
		} else {
			// A or B off screen
			// clip at screen, that's important for huge coordinates
			isVisible = drawClipped(coordsA, coordsB, line,
					view.getMinXScreen() - EuclidianStatic.CLIP_DISTANCE,
					view.getMaxXScreen() + EuclidianStatic.CLIP_DISTANCE,
					view.getMinYScreen() - EuclidianStatic.CLIP_DISTANCE,
					view.getMaxYScreen() + EuclidianStatic.CLIP_DISTANCE,
					tmpClipPoints);
		}

		if (segmentWithEndings != null) {
			segmentWithEndings.update(objStroke);
		}
		drawAndUpdateTraceIfNeeded(segment.getTrace());

		// if no label and no decoration then we're done
		if (!labelVisible
				&& geo.getDecorationType() == GeoElementND.DECORATION_NONE) {
			return;
		}

		// calc midpoint (midX, midY) and perpendicular vector (nx, ny)
		double midX = (coordsA[0] + coordsB[0]) / 2.0;
		double midY = (coordsA[1] + coordsB[1]) / 2.0;
		double nx = coordsA[1] - coordsB[1];
		double ny = coordsB[0] - coordsA[0];
		double nLength = MyMath.length(nx, ny);

		// label position
		// use unit perpendicular vector to move away from line
		if (labelVisible) {
			labelDesc = geo.getLabelDescription();
			if (nLength > 0.0) {
				xLabel = (int) (midX + nx * 16 / nLength);
				yLabel = (int) (midY + ny * 16 / nLength);
			} else {
				xLabel = (int) midX;
				yLabel = (int) (midY + 16);
			}
			addLabelOffset();
		}

		// update decoration
		if (geo.getDecorationType() != GeoElementND.DECORATION_NONE
				&& nLength > 0) {
			if (decoTicks == null) {
				// only create these object when they are really needed
				decoTicks = new GLine2D[6];
				// changed from 3 to 6
				for (int i = 0; i < decoTicks.length; i++) {
					decoTicks[i] = AwtFactory.getPrototype().newLine2D();
				}
			}

			// tick spacing and length.
			double tickSpacing = 2.5 + geo.getLineThickness() / 2d;
			double tickLength = tickSpacing + 1;
			double arrowlength = 1.5;
			double vx, vy, factor;

			switch (geo.getDecorationType()) {
			default:
				// do nothing
				break;
			case GeoElementND.DECORATION_SEGMENT_ONE_TICK:
				// use perpendicular vector to set tick
				factor = tickLength / nLength;
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX - nx, midY - ny, midX + nx,
						midY + ny);
				break;

			case GeoElementND.DECORATION_SEGMENT_TWO_TICKS:
				// vector (vx, vy) to get 2 points around midpoint
				factor = tickSpacing / (2 * nLength);
				vx = -ny * factor;
				vy = nx * factor;
				// use perpendicular vector to set ticks
				factor = tickLength / nLength;
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX + vx - nx, midY + vy - ny,
						midX + vx + nx, midY + vy + ny);
				decoTicks[1].setLine(midX - vx - nx, midY - vy - ny,
						midX - vx + nx, midY - vy + ny);
				break;

			case GeoElementND.DECORATION_SEGMENT_THREE_TICKS:
				// vector (vx, vy) to get 2 points around midpoint
				factor = tickSpacing / nLength;
				vx = -ny * factor;
				vy = nx * factor;
				// use perpendicular vector to set ticks
				factor = tickLength / nLength;
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX + vx - nx, midY + vy - ny,
						midX + vx + nx, midY + vy + ny);
				decoTicks[1].setLine(midX - nx, midY - ny, midX + nx,
						midY + ny);
				decoTicks[2].setLine(midX - vx - nx, midY - vy - ny,
						midX - vx + nx, midY - vy + ny);
				break;

			case GeoElementND.DECORATION_SEGMENT_ONE_ARROW:
				// vector (vx, vy) to get 2 points around midpoint
				factor = tickSpacing / (1.5 * nLength);
				vx = -ny * factor;
				vy = nx * factor;
				// use perpendicular vector to set tick
				factor = tickLength / (1.5 * nLength);
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX - arrowlength * vx,
						midY - arrowlength * vy,
						midX - arrowlength * vx + arrowlength * (nx + vx),
						midY - arrowlength * vy + arrowlength * (ny + vy));
				decoTicks[1].setLine(midX - arrowlength * vx,
						midY - arrowlength * vy,
						midX - arrowlength * vx + arrowlength * (-nx + vx),
						midY - arrowlength * vy + arrowlength * (-ny + vy));
				break;

			case GeoElementND.DECORATION_SEGMENT_TWO_ARROWS:
				// vector (vx, vy) to get 2 points around midpoint
				factor = tickSpacing / (1.5 * nLength);
				vx = -ny * factor;
				vy = nx * factor;
				// use perpendicular vector to set ticks
				factor = tickLength / (1.5 * nLength);
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX - 2 * arrowlength * vx,
						midY - 2 * arrowlength * vy,
						midX - 2 * arrowlength * vx + arrowlength * (nx + vx),
						midY - 2 * arrowlength * vy + arrowlength * (ny + vy));
				decoTicks[1].setLine(midX - 2 * arrowlength * vx,
						midY - 2 * arrowlength * vy,
						midX - 2 * arrowlength * vx + arrowlength * (-nx + vx),
						midY - 2 * arrowlength * vy + arrowlength * (-ny + vy));

				decoTicks[2].setLine(midX, midY, midX + arrowlength * (nx + vx),
						midY + arrowlength * (ny + vy));
				decoTicks[3].setLine(midX, midY,
						midX + arrowlength * (-nx + vx),
						midY + arrowlength * (-ny + vy));
				break;

			case GeoElementND.DECORATION_SEGMENT_THREE_ARROWS:
				// vector (vx, vy) to get 2 points around midpoint
				factor = tickSpacing / (1.5 * nLength);
				vx = -ny * factor;
				vy = nx * factor;
				// use perpendicular vector to set ticks
				factor = tickLength / (1.5 * nLength);
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX - arrowlength * vx,
						midY - arrowlength * vy,
						midX - arrowlength * vx + arrowlength * (nx + vx),
						midY - arrowlength * vy + arrowlength * (ny + vy));
				decoTicks[1].setLine(midX - arrowlength * vx,
						midY - arrowlength * vy,
						midX - arrowlength * vx + arrowlength * (-nx + vx),
						midY - arrowlength * vy + arrowlength * (-ny + vy));

				decoTicks[2].setLine(midX + arrowlength * vx,
						midY + arrowlength * vy,
						midX + arrowlength * vx + arrowlength * (nx + vx),
						midY + arrowlength * vy + arrowlength * (ny + vy));
				decoTicks[3].setLine(midX + arrowlength * vx,
						midY + arrowlength * vy,
						midX + arrowlength * vx + arrowlength * (-nx + vx),
						midY + arrowlength * vy + arrowlength * (-ny + vy));

				decoTicks[4].setLine(midX - 3 * arrowlength * vx,
						midY - 3 * arrowlength * vy,
						midX - 3 * arrowlength * vx + arrowlength * (nx + vx),
						midY - 3 * arrowlength * vy + arrowlength * (ny + vy));
				decoTicks[5].setLine(midX - 3 * arrowlength * vx,
						midY - 3 * arrowlength * vy,
						midX - 3 * arrowlength * vx + arrowlength * (-nx + vx),
						midY - 3 * arrowlength * vy + arrowlength * (-ny + vy));
				break;
			}
		} else {
			// #4907 make sure decorations disappear for length 0 segments
			if (decoTicks != null) {
				for (int i = 0; i < decoTicks.length; i++) {
					decoTicks[i].setLine(Double.NaN, Double.NaN, Double.NaN,
							Double.NaN);
				}
			}

		}
	}

	/**
	 * @param coordsA
	 *            first point
	 * @param coordsB
	 *            second point
	 * @param line
	 *            line to be updated
	 * @param xmin
	 *            clip left border
	 * @param xmax
	 *            clip right border
	 * @param ymin
	 *            clip top
	 * @param ymax
	 *            clip bottom
	 * @param tmpClipPoints2
	 *            helper array for clipping
	 * @return whether line intersects clipping rectangle
	 */
	public static boolean drawClipped(double[] coordsA, double[] coordsB,
			GLine2D line, int xmin, int xmax, int ymin, int ymax,
			GPoint2D[] tmpClipPoints2) {
		GPoint2D[] clippedPoints = ClipLine.getClipped(coordsA[0], coordsA[1],
				coordsB[0], coordsB[1], xmin, xmax, ymin, ymax, tmpClipPoints2);
		if (clippedPoints == null) {
			return false;
		}
		line.setLine(clippedPoints[0].getX(), clippedPoints[0].getY(),
				clippedPoints[1].getX(), clippedPoints[1].getY());
		return true;

	}

	@Override
	final public void draw(GGraphics2D g2) {
		// segments of polygons can have zero thickness
		if (geo.getLineThickness() == 0) {
			return;
		}

		if (isVisible) {
			if (hasSegmentStyle()) {
				drawLineMiddleDecoration(g2);
				drawLabelIfVisible(g2);
				drawSegmentWithEndings(g2);
			} else {
				if (isHighlighted()) {
					setHighlightingStyle(g2);
					g2.draw(line);
				}
				drawLineMiddleDecoration(g2);
				drawLabelIfVisible(g2);
				drawSimpleSegment(g2);
			}
		}
	}

	private void drawSimpleSegment(GGraphics2D g2) {
		g2.setPaint(getObjectColor());
		g2.setStroke(objStroke);
		g2.draw(line);
	}

	private void drawSegmentWithEndings(GGraphics2D g2) {
		if (segmentWithEndings != null) {
			segmentWithEndings.draw(g2);
		}
	}

	private void drawLineMiddleDecoration(GGraphics2D g2) {
		// decoTicks is null for zero length segments
		g2.setColor(getObjectColor());
		if (geo.getDecorationType() != GeoElementND.DECORATION_NONE
				&& decoTicks != null) {
			g2.setStroke(decoStroke);

			switch (geo.getDecorationType()) {
			default:
				// do nothing
				break;
			case GeoElementND.DECORATION_SEGMENT_ONE_TICK:
				g2.draw(decoTicks[0]);
				break;

			case GeoElementND.DECORATION_SEGMENT_TWO_TICKS:
				g2.draw(decoTicks[0]);
				g2.draw(decoTicks[1]);
				break;

			case GeoElementND.DECORATION_SEGMENT_THREE_TICKS:
				g2.draw(decoTicks[0]);
				g2.draw(decoTicks[1]);
				g2.draw(decoTicks[2]);
				break;
			case GeoElementND.DECORATION_SEGMENT_ONE_ARROW:
				g2.draw(decoTicks[0]);
				g2.draw(decoTicks[1]);
				break;

			case GeoElementND.DECORATION_SEGMENT_TWO_ARROWS:
				g2.draw(decoTicks[0]);
				g2.draw(decoTicks[1]);
				g2.draw(decoTicks[2]);
				g2.draw(decoTicks[3]);
				break;

			case GeoElementND.DECORATION_SEGMENT_THREE_ARROWS:
				g2.draw(decoTicks[0]);
				g2.draw(decoTicks[1]);
				g2.draw(decoTicks[2]);
				g2.draw(decoTicks[3]);
				g2.draw(decoTicks[4]);
				g2.draw(decoTicks[5]);
				break;
			}
		}
	}

	private void drawLabelIfVisible(GGraphics2D g2) {
		if (labelVisible) {
			g2.setPaint(geo.getLabelColor());
			g2.setFont(view.getFontLine());
			drawLabel(g2);
		}
	}

	@Override
	protected final void drawTrace(GGraphics2D g2) {
		g2.setPaint(getObjectColor());
		g2.setStroke(objStroke);
		if (segmentWithEndings != null && hasSegmentStyle()) {
			segmentWithEndings.draw(g2);
		} else {
			g2.draw(line);
		}
	}

	@Override
	final public void updatePreview() {
		isVisible = points.size() == 1;
		if (isVisible) {

			// start point
			view.getCoordsForView(points.get(0).getInhomCoordsInD3())
					.get(coordsA);
			view.toScreenCoords(coordsA);

			if (line == null) {
				line = AwtFactory.getPrototype().newLine2D();
			}
		}
	}

	@Override
	final public void updateMousePos(double mouseRWx, double mouseRWy) {
		double xRW = mouseRWx;
		double yRW = mouseRWy;
		if (isVisible) {
			// double xRW = view.toRealWorldCoordX(mx);
			// double yRW = view.toRealWorldCoordY(my);

			int mx = view.toScreenCoordX(xRW);
			int my = view.toScreenCoordY(yRW);

			// round angle to nearest 15 degrees if alt pressed
			if (points.size() == 1
					&& view.getEuclidianController().isAltDown()) {
				GeoPointND p = points.get(0);
				double px = p.getInhomX();
				double py = p.getInhomY();
				double angle = Math.atan2(yRW - py, xRW - px) * 180 / Math.PI;
				double radius = Math.sqrt(
						(py - yRW) * (py - yRW) + (px - xRW) * (px - xRW));

				// round angle to nearest 15 degrees
				angle = Math.round(angle / 15) * 15;

				xRW = px + radius * Math.cos(angle * Math.PI / 180);
				yRW = py + radius * Math.sin(angle * Math.PI / 180);

				mx = view.toScreenCoordX(xRW);
				my = view.toScreenCoordY(yRW);

				endPoint.setLocation(xRW, yRW);
				view.getEuclidianController().setLineEndPoint(endPoint);
			} else {
				view.getEuclidianController().setLineEndPoint(null);
			}
			line.setLine(coordsA[0], coordsA[1], mx, my);
		}
	}

	@Override
	final public void drawPreview(GGraphics2D g2) {
		if (isVisible) {
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

	@Override
	final public boolean hit(int x, int y, int hitThreshold) {
		return getDecoratedShape() != null
				&& getDecoratedShape().intersects(x - hitThreshold, y - hitThreshold,
						2 * hitThreshold, 2 * hitThreshold);
	}

	private GShape getDecoratedShape() {
		return hasSegmentStyle()
				? segmentWithEndings.getShape()
				: line;
	}

	private boolean hasSegmentStyle() {
		return geo instanceof GeoSegment
			&& ((GeoSegment) geo).hasStyledEndpoint();
	}

	@Override
	final public boolean isInside(GRectangle rect) {
		GShape decoratedShape = getDecoratedShape();
		if (decoratedShape instanceof GLine2D) {
			GLine2D line = (GLine2D) decoratedShape;
			return rect.contains(line.getP1()) && rect.contains(line.getP2());
		}

		return decoratedShape != null && rect.contains(decoratedShape.getBounds());
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		return getDecoratedShape().intersects(rect);
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
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
	 * set visible
	 */
	public void setIsVisible() {
		isVisible = true;
	}

	@Override
	public void fromPoints(ArrayList<GPoint2D> pts) {
		segment.getStartPoint().setCoords(view.toRealWorldCoordX(pts.get(0).getX()),
				view.toRealWorldCoordY(pts.get(0).getY()), 1);
		segment.getEndPoint().setCoords(view.toRealWorldCoordX(pts.get(1).getX()),
				view.toRealWorldCoordY(pts.get(1).getY()), 1);
		segment.getParentAlgorithm().update();
	}

	@Override
	public ArrayList<GPoint2D> toPoints() {
		ArrayList<GPoint2D> ret = new ArrayList<>();
		addPoint(segment.getStartPoint(), ret);
		addPoint(segment.getEndPoint(), ret);
		return ret;
	}

	private void addPoint(GeoPointND point, ArrayList<GPoint2D> ret) {
		point.updateCoords2D();
		ret.add(new MyPoint(view.toScreenCoordXd(point.getX2D()),
				view.toScreenCoordYd(point.getY2D())));
	}

	@Override
	public GLine2D getLine() {
		return line;
	}

	@Override
	public double getX1() {
		return line.getX1();
	}

	@Override
	public double getX2() {
		return line.getX2();
	}

	@Override
	public double getY1() {
		return line.getY1();
	}

	@Override
	public double getY2() {
		return line.getY2();
	}

	@Override
	public double getAngle(boolean isStart) {
		return Math.atan2(line.getY2() - line.getY1(), line.getX2() - line.getX1());
	}

	@Override
	public void setHighlightingStyle(GGraphics2D g2) {
		g2.setPaint(geo.getSelColor());
		g2.setStroke(selStroke);
	}

	@Override
	public void setBasicStyle(GGraphics2D g2) {
		g2.setStroke(decoStroke);
		g2.setColor(getObjectColor());
	}

	@Override
	public void drawStroke(GGraphics2D g2) {
		g2.draw(line);
	}
}
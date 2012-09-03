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

package geogebra.common.euclidian.draw;

import geogebra.common.awt.GLine2D;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.Previewable;
import geogebra.common.euclidian.clipping.ClipLine;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.util.MyMath;

import java.util.ArrayList;

/**
 * 
 * @author Markus Hohenwarter
 */
public class DrawSegment extends Drawable implements Previewable {

	private GeoLineND s;

	private boolean isVisible, labelVisible;
	private ArrayList<GeoPointND> points;

	private GLine2D line;
	private double[] coordsA = new double[2];
	private double[] coordsB = new double[2];

	// For drawing ticks
	private GLine2D[] decoTicks;

	/**
	 * Creates new DrawSegment
	 * 
	 * @param view
	 *            Euclidian view to be used
	 * @param s
	 *            Segment to be drawn
	 */
	public DrawSegment(EuclidianView view, GeoLineND s) {
		this.view = view;
		hitThreshold = view.getCapturingThreshold();
		this.s = s;
		geo = (GeoElement) s;

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

		updatePreview();
	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible)
			return;
		labelVisible = geo.isLabelVisible();
		updateStrokes(geo);

		Coords A = view.getCoordsForView(s.getStartInhomCoords());
		Coords B = view.getCoordsForView(s.getEndInhomCoords());

		// check if in view
		if (!Kernel.isZero(A.getZ())
				|| !Kernel.isZero(B.getZ())) {
			isVisible = false;
			return;
		}

		/*
		 * if (s.getEndPoint().getLabel().equals("S3'"))
		 * Application.debug("start=\n"+s.getStartInhomCoords()+"\nA=\n"+A);
		 */

		coordsA[0] = A.getX();
		coordsA[1] = A.getY();
		coordsB[0] = B.getX();
		coordsB[1] = B.getY();

		boolean onscreenA = view.toScreenCoords(coordsA);
		boolean onscreenB = view.toScreenCoords(coordsB);

		if (line == null)
			line = AwtFactory.prototype.newLine2D();

		if (onscreenA && onscreenB) {
			// A and B on screen
			line.setLine(coordsA[0], coordsA[1], coordsB[0], coordsB[1]);
		} else {
			// A or B off screen
			// clip at screen, that's important for huge coordinates
			geogebra.common.awt.GPoint2D[] clippedPoints = ClipLine.getClipped(coordsA[0],
					coordsA[1], coordsB[0], coordsB[1],
					-EuclidianStatic.CLIP_DISTANCE, view.getWidth()
							+ EuclidianStatic.CLIP_DISTANCE,
					-EuclidianStatic.CLIP_DISTANCE, view.getHeight()
							+ EuclidianStatic.CLIP_DISTANCE);
			if (clippedPoints == null) {
				isVisible = false;
			} else {
				line.setLine(clippedPoints[0].getX(), clippedPoints[0].getY(),
						clippedPoints[1].getX(), clippedPoints[1].getY());
			}
		}

		// draw trace
		if (s.getTrace()) {
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

		// if no label and no decoration then we're done
		if (!labelVisible && geo.decorationType == GeoElement.DECORATION_NONE)
			return;

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
		// added by Lo�c and Markus BEGIN,
		if (geo.decorationType != GeoElement.DECORATION_NONE && nLength > 0) {
			if (decoTicks == null) {
				// only create these object when they are really needed
				decoTicks = new GLine2D[6]; // Michael Borcherds 20071006
													// changed from 3 to 6
				for (int i = 0; i < decoTicks.length; i++)
					decoTicks[i] = AwtFactory.prototype.newLine2D();
			}

			// tick spacing and length.
			double tickSpacing = 2.5 + geo.lineThickness / 2d;
			double tickLength = tickSpacing + 1;
			// Michael Borcherds 20071006 start
			double arrowlength = 1.5;
			// Michael Borcherds 20071006 end
			double vx, vy, factor;

			switch (geo.decorationType) {
			case GeoElement.DECORATION_SEGMENT_ONE_TICK:
				// use perpendicular vector to set tick
				factor = tickLength / nLength;
				nx *= factor;
				ny *= factor;
				decoTicks[0]
						.setLine(midX - nx, midY - ny, midX + nx, midY + ny);
				break;

			case GeoElement.DECORATION_SEGMENT_TWO_TICKS:
				// vector (vx, vy) to get 2 points around midpoint
				factor = tickSpacing / (2 * nLength);
				vx = -ny * factor;
				vy = nx * factor;
				// use perpendicular vector to set ticks
				factor = tickLength / nLength;
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX + vx - nx, midY + vy - ny, midX + vx
						+ nx, midY + vy + ny);
				decoTicks[1].setLine(midX - vx - nx, midY - vy - ny, midX - vx
						+ nx, midY - vy + ny);
				break;

			case GeoElement.DECORATION_SEGMENT_THREE_TICKS:
				// vector (vx, vy) to get 2 points around midpoint
				factor = tickSpacing / nLength;
				vx = -ny * factor;
				vy = nx * factor;
				// use perpendicular vector to set ticks
				factor = tickLength / nLength;
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX + vx - nx, midY + vy - ny, midX + vx
						+ nx, midY + vy + ny);
				decoTicks[1]
						.setLine(midX - nx, midY - ny, midX + nx, midY + ny);
				decoTicks[2].setLine(midX - vx - nx, midY - vy - ny, midX - vx
						+ nx, midY - vy + ny);
				break;
			// Michael Borcherds 20071006 start
			case GeoElement.DECORATION_SEGMENT_ONE_ARROW:
				// vector (vx, vy) to get 2 points around midpoint
				factor = tickSpacing / (1.5 * nLength);
				vx = -ny * factor;
				vy = nx * factor;
				// use perpendicular vector to set tick
				factor = tickLength / (1.5 * nLength);
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX - arrowlength * vx, midY
						- arrowlength * vy, midX - arrowlength * vx
						+ arrowlength * (nx + vx), midY - arrowlength * vy
						+ arrowlength * (ny + vy));
				decoTicks[1].setLine(midX - arrowlength * vx, midY
						- arrowlength * vy, midX - arrowlength * vx
						+ arrowlength * (-nx + vx), midY - arrowlength * vy
						+ arrowlength * (-ny + vy));
				break;

			case GeoElement.DECORATION_SEGMENT_TWO_ARROWS:
				// vector (vx, vy) to get 2 points around midpoint
				factor = tickSpacing / (1.5 * nLength);
				vx = -ny * factor;
				vy = nx * factor;
				// use perpendicular vector to set ticks
				factor = tickLength / (1.5 * nLength);
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX - 2 * arrowlength * vx, midY - 2
						* arrowlength * vy, midX - 2 * arrowlength * vx
						+ arrowlength * (nx + vx), midY - 2 * arrowlength * vy
						+ arrowlength * (ny + vy));
				decoTicks[1].setLine(midX - 2 * arrowlength * vx, midY - 2
						* arrowlength * vy, midX - 2 * arrowlength * vx
						+ arrowlength * (-nx + vx), midY - 2 * arrowlength * vy
						+ arrowlength * (-ny + vy));

				decoTicks[2].setLine(midX, midY,
						midX + arrowlength * (nx + vx), midY + arrowlength
								* (ny + vy));
				decoTicks[3].setLine(midX, midY, midX + arrowlength
						* (-nx + vx), midY + arrowlength * (-ny + vy));
				break;

			case GeoElement.DECORATION_SEGMENT_THREE_ARROWS:
				// vector (vx, vy) to get 2 points around midpoint
				factor = tickSpacing / (1.5 * nLength);
				vx = -ny * factor;
				vy = nx * factor;
				// use perpendicular vector to set ticks
				factor = tickLength / (1.5 * nLength);
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX - arrowlength * vx, midY
						- arrowlength * vy, midX - arrowlength * vx
						+ arrowlength * (nx + vx), midY - arrowlength * vy
						+ arrowlength * (ny + vy));
				decoTicks[1].setLine(midX - arrowlength * vx, midY
						- arrowlength * vy, midX - arrowlength * vx
						+ arrowlength * (-nx + vx), midY - arrowlength * vy
						+ arrowlength * (-ny + vy));

				decoTicks[2].setLine(midX + arrowlength * vx, midY
						+ arrowlength * vy, midX + arrowlength * vx
						+ arrowlength * (nx + vx), midY + arrowlength * vy
						+ arrowlength * (ny + vy));
				decoTicks[3].setLine(midX + arrowlength * vx, midY
						+ arrowlength * vy, midX + arrowlength * vx
						+ arrowlength * (-nx + vx), midY + arrowlength * vy
						+ arrowlength * (-ny + vy));

				decoTicks[4].setLine(midX - 3 * arrowlength * vx, midY - 3
						* arrowlength * vy, midX - 3 * arrowlength * vx
						+ arrowlength * (nx + vx), midY - 3 * arrowlength * vy
						+ arrowlength * (ny + vy));
				decoTicks[5].setLine(midX - 3 * arrowlength * vx, midY - 3
						* arrowlength * vy, midX - 3 * arrowlength * vx
						+ arrowlength * (-nx + vx), midY - 3 * arrowlength * vy
						+ arrowlength * (-ny + vy));
				break;
			// Michael Borcherds 20071006 end
			}
		}
	}

	@Override
	final public void draw(geogebra.common.awt.GGraphics2D g2) {
		// segments of polygons can have zero thickness
		if (geo.lineThickness == 0)
			return;

		if (isVisible) {
			if (geo.doHighlighting()) {
				g2.setPaint(
						geo.getSelColor());
				g2.setStroke(selStroke);
				g2.draw(line);
			}

			g2.setPaint(geo
					.getObjectColor());
			g2.setStroke(objStroke);
			g2.draw(line);

			// added by Lo�c BEGIN
			//decoTicks is null for zero length segments
			if (geo.decorationType != GeoElement.DECORATION_NONE && decoTicks!=null) {
				g2.setStroke(decoStroke);

				switch (geo.decorationType) {
				case GeoElement.DECORATION_SEGMENT_ONE_TICK:
					g2.draw(decoTicks[0]);
					break;

				case GeoElement.DECORATION_SEGMENT_TWO_TICKS:
					g2.draw(decoTicks[0]);
					g2.draw(decoTicks[1]);
					break;

				case GeoElement.DECORATION_SEGMENT_THREE_TICKS:
					g2.draw(decoTicks[0]);
					g2.draw(decoTicks[1]);
					g2.draw(decoTicks[2]);
					break;
				// Michael Borcherds 20071006 start
				case GeoElement.DECORATION_SEGMENT_ONE_ARROW:
					g2.draw(decoTicks[0]);
					g2.draw(decoTicks[1]);
					break;

				case GeoElement.DECORATION_SEGMENT_TWO_ARROWS:
					g2.draw(decoTicks[0]);
					g2.draw(decoTicks[1]);
					g2.draw(decoTicks[2]);
					g2.draw(decoTicks[3]);
					break;

				case GeoElement.DECORATION_SEGMENT_THREE_ARROWS:
					g2.draw(decoTicks[0]);
					g2.draw(decoTicks[1]);
					g2.draw(decoTicks[2]);
					g2.draw(decoTicks[3]);
					g2.draw(decoTicks[4]);
					g2.draw(decoTicks[5]);
					break;
				// Michael Borcherds 20071006 end
				}
			}
			// END

			if (labelVisible) {
				g2.setPaint(
						geo.getLabelColor());
				g2.setFont(view.getFontLine());
				drawLabel(g2);
			}
		}
	}

	@Override
	protected
	final void drawTrace(geogebra.common.awt.GGraphics2D g2) {
		g2.setPaint(geo.getObjectColor());
		g2.setStroke(objStroke);
		g2.draw(line);
	}

	final public void updatePreview() {
		isVisible = points.size() == 1;
		if (isVisible) {

			// start point
			coordsA = view.getCoordsForView(points.get(0).getInhomCoordsInD(3))
					.get();
			// coordsA = points.get(0).getInhomCoordsInD(2).get();
			view.toScreenCoords(coordsA);

			if (line == null)
				line = AwtFactory.prototype.newLine2D();
			line.setLine(coordsA[0], coordsA[1], coordsA[0], coordsA[1]);
		}
	}

	private geogebra.common.awt.GPoint2D endPoint = geogebra.common.factories.AwtFactory.prototype.newPoint2D();

	final public void updateMousePos(double mouseRWx, double mouseRWy) {
		double xRW = mouseRWx;
		double yRW = mouseRWy;
		if (isVisible) {
			// double xRW = view.toRealWorldCoordX(mx);
			// double yRW = view.toRealWorldCoordY(my);

			int mx = view.toScreenCoordX(xRW);
			int my = view.toScreenCoordY(yRW);

			// round angle to nearest 15 degrees if alt pressed
			if (points.size() == 1 && view.getEuclidianController().isAltDown()) {
				GeoPoint p = (GeoPoint) points.get(0);
				double px = p.inhomX;
				double py = p.inhomY;
				double angle = Math.atan2(yRW - py, xRW - px) * 180 / Math.PI;
				double radius = Math.sqrt((py - yRW) * (py - yRW) + (px - xRW)
						* (px - xRW));

				// round angle to nearest 15 degrees
				angle = Math.round(angle / 15) * 15;

				xRW = px + radius * Math.cos(angle * Math.PI / 180);
				yRW = py + radius * Math.sin(angle * Math.PI / 180);

				mx = view.toScreenCoordX(xRW);
				my = view.toScreenCoordY(yRW);

				endPoint.setX(xRW);
				endPoint.setY(yRW);
				view.getEuclidianController().setLineEndPoint(endPoint);
			} else
				view.getEuclidianController().setLineEndPoint(null);
			line.setLine(coordsA[0], coordsA[1], mx, my);
		}
	}

	final public void drawPreview(geogebra.common.awt.GGraphics2D g2) {
		if (isVisible) {
			g2.setPaint(ConstructionDefaults.colPreview);
			g2.setStroke(objStroke);
			g2.draw(line);
		}
	}

	public void disposePreview() {
		//do nothing
	}

	@Override
	final public boolean hit(int x, int y) {
		return line != null
				&& line.intersects(x - hitThreshold, y - hitThreshold,
						2 * hitThreshold, 2 * hitThreshold);
	}

	@Override
	final public boolean isInside(geogebra.common.awt.GRectangle rect) {
		return line != null && rect.contains(line.getP1())
				&& rect.contains(line.getP2());
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
	 * Returns the bounding box of this Drawable in screen coordinates.
	 */
	@Override
	final public geogebra.common.awt.GRectangle getBounds() {
		if (line == null || !geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}
		return AwtFactory.prototype.newRectangle(line.getBounds());
	}

}

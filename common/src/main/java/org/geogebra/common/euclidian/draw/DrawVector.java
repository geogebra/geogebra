/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * DrawVector.java
 *
 * Created on 16. Oktober 2001, 15:13
 */

package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.euclidian.clipping.ClipLine;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;

/**
 * 
 * @author Markus
 */
public class DrawVector extends Drawable implements Previewable {

	private GeoVectorND v;
	private GeoPointND P;

	private boolean isVisible;
	private boolean labelVisible;
	private boolean traceDrawingNeeded = false;

	private GLine2D line;
	private double[] coordsA = new double[2];
	private double[] coordsB = new double[2];
	private double[] coordsV = new double[2];
	private GGeneralPath gp; // for arrow
	private boolean arrowheadVisible;
	private boolean lineVisible;
	private ArrayList<GeoPointND> points;
	private GPoint2D endPoint = new GPoint2D();
	private GPoint2D[] tmpClipPoints = {new GPoint2D(), new GPoint2D()};

	/**
	 * Creates new DrawVector
	 * 
	 * @param view
	 *            view
	 * @param v
	 *            vector
	 */
	public DrawVector(EuclidianView view, GeoVectorND v) {
		this.view = view;
		this.v = v;
		geo = (GeoElement) v;

		update();
	}

	/**
	 * @param view
	 *            view
	 * @param points
	 *            start point and end point
	 */
	public DrawVector(EuclidianView view, ArrayList<GeoPointND> points) {
		this.view = view;
		this.points = points;
		geo = view.getKernel().getConstruction().getConstructionDefaults()
				.getDefaultGeo(ConstructionDefaults.DEFAULT_VECTOR);
		updatePreview();
	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible) {
			return;
		}
		labelVisible = geo.isLabelVisible();

		updateStrokes(v);

		Coords coords;

		// start point in real world coords
		P = v.getStartPoint();
		if (P != null && !P.isInfinite()) {
			coords = view.getCoordsForView(P.getInhomCoordsInD3());
			if (!DoubleUtil.isZero(coords.getZ())) {
				isVisible = false;
				return;
			}
			coordsA[0] = coords.getX();
			coordsA[1] = coords.getY();
		} else {
			coordsA[0] = 0;
			coordsA[1] = 0;
		}

		// vector
		coords = view.getCoordsForView(v.getCoordsInD3());
		if (!DoubleUtil.isZero(coords.getZ())) {
			isVisible = false;
			return;
		}
		coordsV[0] = coords.getX();
		coordsV[1] = coords.getY();

		// end point
		coordsB[0] = coordsA[0] + coordsV[0];
		coordsB[1] = coordsA[1] + coordsV[1];

		// set line and arrow of vector and converts all coords to screen
		setArrow(((GeoElement) v).getLineThickness());

		// label position
		if (labelVisible) {
			labelDesc = geo.getLabelDescription();
			// note that coordsV was normalized in setArrow()
			xLabel = (int) ((coordsA[0] + coordsB[0]) / 2.0 + coordsV[1]);
			yLabel = (int) ((coordsA[1] + coordsB[1]) / 2.0 - coordsV[0]);
			addLabelOffset();
		}

		// draw trace
		// a vector is a Locateable and it might
		// happen that there are several update() calls
		// before the new trace should be drawn
		// so the actual drawing is moved to draw()
		traceDrawingNeeded = v.getTrace();
		if (v.getTrace()) {
			isTracing = true;
		} else {
			if (isTracing) {
				isTracing = false;
				// view.updateBackground();
			}
		}
	}

	/**
	 * @param lineThickness
	 *            vector thickness
	 * @return arrow size
	 */
	static final public double getFactor(double lineThickness) {

		// changed to make arrow-heads a bit bigger for line thickness 8-13
		return lineThickness < 8 ? 12.0 + lineThickness : 3 * lineThickness;
	}

	/**
	 * Sets the line and arrow of the vector.
	 */
	private void setArrow(double lineThickness) {
		// screen coords of start and end point of vector
		final boolean onscreenA = view.toScreenCoords(coordsA);
		final boolean onscreenB = view.toScreenCoords(coordsB);
		coordsV[0] = coordsB[0] - coordsA[0];
		coordsV[1] = coordsB[1] - coordsA[1];

		// calculate endpoint F at base of arrow

		double factor = getFactor(lineThickness);

		double length = MyMath.length(coordsV[0], coordsV[1]);

		// decrease arrowhead size if it's longer than the vector
		if (length < factor) {
			factor = length;
		}

		if (length > 0.0) {
			coordsV[0] = (coordsV[0] * factor) / length;
			coordsV[1] = (coordsV[1] * factor) / length;
		}
		double[] coordsF = new double[2];
		coordsF[0] = coordsB[0] - coordsV[0];
		coordsF[1] = coordsB[1] - coordsV[1];

		// set clipped line
		if (line == null) {
			line = AwtFactory.getPrototype().newLine2D();
		}
		lineVisible = true;

		if (onscreenA && onscreenB) {
			// A and B on screen
			line.setLine(coordsA[0], coordsA[1], coordsF[0], coordsF[1]);
		} else {
			// A or B off screen
			// clip at screen, that's important for huge coordinates
			// check if any of vector is on-screen
			GPoint2D[] clippedPoints = ClipLine.getClipped(coordsA[0],
					coordsA[1], coordsB[0], coordsB[1],
					view.getMinXScreen() - EuclidianStatic.CLIP_DISTANCE,
					view.getMaxXScreen() + EuclidianStatic.CLIP_DISTANCE,
					view.getMinYScreen() - EuclidianStatic.CLIP_DISTANCE,
					view.getMaxYScreen() + EuclidianStatic.CLIP_DISTANCE,
					tmpClipPoints);
			if (clippedPoints == null) {
				isVisible = false;
				lineVisible = false;
				arrowheadVisible = false;
			} else {

				// now re-clip at A and F
				clippedPoints = ClipLine.getClipped(coordsA[0], coordsA[1],
						coordsF[0], coordsF[1], -EuclidianStatic.CLIP_DISTANCE,
						view.getWidth() + EuclidianStatic.CLIP_DISTANCE,
						-EuclidianStatic.CLIP_DISTANCE,
						view.getHeight() + EuclidianStatic.CLIP_DISTANCE,
						tmpClipPoints);
				if (clippedPoints != null) {
					line.setLine(clippedPoints[0].getX(),
							clippedPoints[0].getY(), clippedPoints[1].getX(),
							clippedPoints[1].getY());
				} else {
					lineVisible = false;
				}
			}
		}

		// add triangle if visible
		if (gp == null) {
			gp = AwtFactory.getPrototype().newGeneralPath();
		} else {
			gp.reset();
		}

		if (isVisible) {

			if (length > 0) {
				coordsV[0] /= 4.0;
				coordsV[1] /= 4.0;

				gp.moveTo(coordsB[0], coordsB[1]); // end point
				gp.lineTo((coordsF[0] - coordsV[1]), (coordsF[1] + coordsV[0]));
				gp.lineTo((coordsF[0] + coordsV[1]), (coordsF[1] - coordsV[0]));
				gp.closePath();
			}

			arrowheadVisible = onscreenB || view.intersects(gp);

		}
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (isVisible) {
			if (traceDrawingNeeded) {
				traceDrawingNeeded = false;
				GGraphics2D g2d = view.getBackgroundGraphics();
				if (g2d != null) {
					drawTrace(g2d);
				}
			}

			if (isHighlighted()) {
				g2.setPaint(((GeoElement) v).getSelColor());
				g2.setStroke(selStroke);
				if (lineVisible) {
					g2.draw(line);
				}
			}

			g2.setPaint(getObjectColor());
			g2.setStroke(objStroke);
			if (lineVisible) {
				g2.draw(line);
			}
			if (arrowheadVisible) {
				g2.fill(gp);
			}

			if (labelVisible) {
				g2.setFont(view.getFontVector());
				g2.setPaint(((GeoElement) v).getLabelColor());
				drawLabel(g2);
			}
		}
	}

	@Override
	protected final void drawTrace(GGraphics2D g2) {
		g2.setPaint(getObjectColor());
		g2.setStroke(objStroke);
		if (lineVisible) {
			g2.draw(line);
		}
		if (arrowheadVisible) {
			g2.fill(gp);
		}
	}

	@Override
	final public void updatePreview() {
		isVisible = points.size() == 1;
		if (isVisible) {
			// start point
			view.getCoordsForView(points.get(0).getInhomCoordsInD3())
					.get(coordsA);
			coordsB[0] = coordsA[0];
			coordsB[1] = coordsA[1];
		}
	}

	@Override
	final public void updateMousePos(double xRWmouse, double yRWmouse) {
		double xRW = xRWmouse;
		double yRW = yRWmouse;
		if (isVisible) {
			// double xRW = view.toRealWorldCoordX(x);
			// double yRW = view.toRealWorldCoordY(y);

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

				endPoint.setLocation(xRW, yRW);
				view.getEuclidianController().setLineEndPoint(endPoint);
			} else {
				view.getEuclidianController().setLineEndPoint(null);
			}

			// set start and end point in real world coords
			// GeoPoint P = (GeoPoint) points.get(0);
			// P.getInhomCoords(coordsA);
			if (points.size() > 0) {
				view.getCoordsForView(points.get(0).getInhomCoordsInD3())
						.get(coordsA);
			}

			coordsB[0] = xRW;
			coordsB[1] = yRW;
			setArrow(1);
		}
	}

	@Override
	final public void drawPreview(GGraphics2D g2) {
		if (isVisible) {
			g2.setPaint(getObjectColor());
			updateStrokes(geo);
			g2.setStroke(objStroke);
			if (arrowheadVisible) {
				g2.fill(gp);
			}
			if (lineVisible) {
				g2.draw(line);
			}
		}
	}

	@Override
	public void disposePreview() {
		// do nothing
	}

	@Override
	final public boolean hit(int x, int y, int hitThreshold) {
		return (lineVisible && line.intersects(x - 3, y - 3, 6, 6))
				|| (arrowheadVisible && gp.intersects(x - 3, y - 3, 6, 6));
	}

	@Override
	final public boolean isInside(GRectangle rect) {
		return (lineVisible && rect.contains(line.getBounds()))
				|| (arrowheadVisible && rect.contains(gp.getBounds()));
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		return (lineVisible && line.intersects(rect))
				|| (arrowheadVisible && gp.intersects(rect));
	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 */
	@Override
	final public GRectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible() || gp == null) {
			return null;
		}
		GRectangle ret = null;
		if (lineVisible && line != null) {
			ret = line.getBounds();
		}

		if (arrowheadVisible) {
			ret = (ret == null)
					? AwtFactory.getPrototype().newRectangle(gp.getBounds())
					: AwtFactory.getPrototype()
							.newRectangle(ret.union(gp.getBounds()));
		}

		return ret;
	}
}

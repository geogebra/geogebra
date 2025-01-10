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
 * Created on 16. October 2001, 15:13
 */

package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasHeadStyle;
import org.geogebra.common.kernel.geos.VectorHeadStyle;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * 
 * @author Markus
 */
public class DrawVector extends Drawable implements Previewable, DrawableVisibility {

	private GeoVectorND v;

	private boolean isVisible;
	private boolean labelVisible;
	private boolean traceDrawingNeeded = false;

	private final double[] tmpCoords = new double[2];
	private ArrayList<GeoPointND> points;
	private final GPoint2D endPoint = new GPoint2D();

	private final DrawStyledVector drawStyledVector;
	private final DrawVectorModel model = new DrawVectorModel();

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
		this.drawStyledVector = new DrawStyledVector(this, this.view);
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
		this.drawStyledVector = new DrawStyledVector(this, this.view);
		updatePreview();
	}

	@Override
	public final void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible) {
			return;
		}

		labelVisible = geo.isLabelVisible();
		updateStrokes(v);

		if (!updateStartPoint()) {
			return;
		}
		if (!updateVector()) {
			return;
		}
		model.calculateEndCoords();
		updateShape();
		if (labelVisible) {
			labelDesc = geo.getLabelDescription();
			updateLabelPosition();
		}
		updateTrace();
	}

	private boolean updateStartPoint() {
		// start point in real world coords
		if (isStartPointValid()) {
			Coords coords = view.getCoordsForView(v.getStartPoint().getInhomCoordsInD3());
			if (is3DCoords(coords)) {
				isVisible = false;
				return false;
			}
			model.setStartCoords(coords.getX(), coords.getY());
		} else {
			model.setStartCoords(0, 0);
		}
		return true;
	}

	private boolean updateVector() {
		Coords coords;
		coords = view.getCoordsForView(v.getCoordsInD3());
		if (is3DCoords(coords)) {
			isVisible = false;
			return false;
		}
		model.setVectorCoords(coords.getX(), coords.getY());
		return true;
	}

	private void updateShape() {
		model.update(v.getLineThickness(), objStroke);
		drawStyledVector.update(vectorShape());
	}

	private VectorShape vectorShape() {
		VectorHeadStyle headStyle = ((HasHeadStyle) geo).getHeadStyle();
		return headStyle.createShape(model);
	}

	private void updateTrace() {
		traceDrawingNeeded = v.getTrace();
		isTracing = v.getTrace();
	}

	private void updateLabelPosition() {
		model.updateLabelPosition(this);
		addLabelOffset();
	}

	private static boolean is3DCoords(Coords coords) {
		return !DoubleUtil.isZero(coords.getZ());
	}

	private boolean isStartPointValid() {
		return v.getStartPoint() != null && !v.getStartPoint().isInfinite();
	}

	/**
	 * @param lineThickness
	 *            vector thickness
	 * @return arrow size
	 */
	public static double getFactor(double lineThickness) {

		// changed to make arrow-heads a bit bigger for line thickness 8-13
		return lineThickness < 8 ? 12.0 + lineThickness : 3 * lineThickness;
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (!isVisible) {
			return;
		}

		if (traceDrawingNeeded) {
			traceDrawingNeeded = false;
			view.drawTrace(this);
		}

		if (isHighlighted()) {
			highlightStyledVector(g2);
		}

		drawStyledVector(g2);

		if (labelVisible) {
			drawVectorLabel(g2);
		}

	}

	@Override
	protected final void drawTrace(GGraphics2D g2) {
		if (g2 == null) {
			return;
		}

		g2.setPaint(getObjectColor());
		g2.setStroke(objStroke);
		drawStyledVector.fill(g2);
	}

	private void highlightStyledVector(GGraphics2D g2) {
		g2.setPaint(v.getSelColor());
		g2.setStroke(selStroke);
		drawStyledVector.draw(g2);
	}

	private void drawStyledVector(GGraphics2D g2) {
		g2.setPaint(getObjectColor());
		g2.setStroke(objStroke);
		drawStyledVector.draw(g2);
	}

	private void drawVectorLabel(GGraphics2D g2) {
		g2.setFont(view.getFontVector());
		g2.setPaint(v.getLabelColor());
		drawLabel(g2);
	}

	@Override
	public final void updatePreview() {
		isVisible = points.size() == 1;
		if (isVisible) {
			// start point
			view.getCoordsForView(points.get(0).getInhomCoordsInD3())
					.get(tmpCoords);
			model.setStartCoords(tmpCoords[0], tmpCoords[1]);
			model.setEndCoords(tmpCoords[0], tmpCoords[1]);
		}
	}

	@Override
	public final void updateMousePos(double xRWmouse, double yRWmouse) {
		double xRW = xRWmouse;
		double yRW = yRWmouse;
		if (isVisible) {
			model.update(1, objStroke);
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
				angle = Math.round(angle / 15) * 15.0;

				xRW = px + radius * Math.cos(angle * Math.PI / 180);
				yRW = py + radius * Math.sin(angle * Math.PI / 180);

				endPoint.setLocation(xRW, yRW);
				view.getEuclidianController().setLineEndPoint(endPoint);
			} else {
				view.getEuclidianController().setLineEndPoint(null);
			}

			if (!points.isEmpty()) {
				view.getCoordsForView(points.get(0).getInhomCoordsInD3())
						.get(tmpCoords);
				model.setStartCoords(tmpCoords[0], tmpCoords[1]);
			}

			model.setEndCoords(xRW, yRW);
			drawStyledVector.update(vectorShape());
		}
	}

	@Override
	public final void drawPreview(GGraphics2D g2) {
		if (isVisible) {
			g2.setPaint(getObjectColor());
			updateStrokes(geo);
			g2.setStroke(objStroke);
			drawStyledVector.fill(g2);
		}
	}

	@Override
	public void disposePreview() {
		// do nothing
	}

	@Override
	public final boolean hit(int x, int y, int hitThreshold) {
		return drawStyledVector.intersects(x - hitThreshold, y - hitThreshold,
				2 * hitThreshold, 2 * hitThreshold);
	}

	@Override
	public final boolean isInside(GRectangle rect) {
		GRectangle bounds = drawStyledVector.getBounds();
		return bounds != null && rect.contains(bounds);
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		return drawStyledVector.intersects((int) rect.getMinX(), (int) rect.getMinY(),
				(int) rect.getWidth(), (int) rect.getHeight());
	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 */
	@Override
	public final GRectangle getBounds() {
		return drawStyledVector.getBounds();
	}

	@Override
	public void setVisible(boolean visible) {
		this.isVisible = visible;
	}

	@Override
	public boolean isVisible() {
		return isVisible;
	}
}

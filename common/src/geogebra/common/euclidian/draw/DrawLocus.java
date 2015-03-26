/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.euclidian.draw;

import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.plot.CurvePlotter;
import geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import geogebra.common.kernel.MyPoint;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLocusND;
import geogebra.common.kernel.geos.Traceable;

import java.util.ArrayList;

/**
 * Drawable representation oflocus
 *
 */
public class DrawLocus extends Drawable {

	private GeoLocusND<? extends MyPoint> locus;

	private boolean isVisible, labelVisible;
	private GeneralPathClippedForCurvePlotter gp;
	private double[] lastPointCoords;

	/**
	 * Creates new drawable for given locus
	 * 
	 * @param view
	 *            view
	 * @param locus
	 *            locus
	 */
	public DrawLocus(EuclidianView view, GeoLocusND<? extends MyPoint> locus) {
		this.view = view;
		this.locus = locus;
		geo = locus;

		update();
	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible)
			return;

		buildGeneralPath(locus.getPoints());

		// line on screen?
		if (!gp.intersects(0, 0, view.getWidth(), view.getHeight())) {
			isVisible = false;
			// don't return here to make sure that getBounds() works for
			// offscreen points too
		}
		updateStrokes(geo);

		labelVisible = geo.isLabelVisible();
		if (labelVisible) {
			labelDesc = geo.getLabelDescription();
			xLabel = (int) (lastPointCoords[0] - 5);
			yLabel = (int) (lastPointCoords[1] + 4 + view.getFontSize());
			addLabelOffsetEnsureOnScreen(1.25, 1.0); // we add 25% width for
														// sure
		}

		// draw trace
		if (geo.isTraceable() && (geo instanceof Traceable)
				&& ((Traceable) geo).getTrace()) {
			isTracing = true;
			geogebra.common.awt.GGraphics2D g2 = view.getBackgroundGraphics();
			if (g2 != null)
				drawTrace(g2);
		} else {
			if (isTracing) {
				isTracing = false;
				// view.updateBackground();
			}
		}
		if (geo.isInverseFill()) {
			setShape(geogebra.common.factories.AwtFactory.prototype
					.newArea(view.getBoundingPath()));
			getShape().subtract(
					geogebra.common.factories.AwtFactory.prototype.newArea(gp));
		}

	}

	@Override
	protected final void drawTrace(geogebra.common.awt.GGraphics2D g2) {
		if (isVisible) {
			g2.setPaint(getObjectColor());
			g2.setStroke(objStroke);
			g2.drawWithValueStrokePure(gp);
		}
	}

	private void buildGeneralPath(ArrayList<? extends MyPoint> pointList) {
		if (gp == null)
			gp = new GeneralPathClippedForCurvePlotter(view);
		else
			gp.reset();

		lastPointCoords = CurvePlotter.draw(gp, pointList);
	}

	@Override
	final public void draw(geogebra.common.awt.GGraphics2D g2) {
		if (isVisible) {
			if (geo.doHighlighting()) {
				// draw locus
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				g2.drawWithValueStrokePure(gp);
			}

			// draw locus
			g2.setPaint(getObjectColor());
			g2.setStroke(objStroke);
			g2.drawWithValueStrokePure(gp);

			if (geo.isFillable()
					&& (geo.getAlphaValue() > 0 || geo.isHatchingEnabled())) {
				try {

					fill(g2, (geo.isInverseFill() ? getShape() : gp), false); // fill
																				// using
																				// default/hatching/image
																				// as
																				// appropriate

				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}

			// label
			if (labelVisible) {
				g2.setFont(view.getFontLine());
				g2.setColor(geo.getLabelColor());
				drawLabel(g2);
			}
		}
	}

	/**
	 * was this object clicked at? (mouse pointer location (x,y) in screen
	 * coords)
	 */
	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		geogebra.common.awt.GShape t = geo.isInverseFill() ? getShape() : gp;
		if (t == null) {
			return false; // hasn't been drawn yet (hidden)
		}
		if (gp.getCurrentPoint() == null) {
			return false; // 3D locus with no points in 2D
		}
		if (strokedShape == null) {
			strokedShape = objStroke.createStrokedShape(gp);
		}
		if (geo.getAlphaValue() > 0.0f || geo.isHatchingEnabled()) {
			return t.intersects(x - hitThreshold, y - hitThreshold,
					2 * hitThreshold, 2 * hitThreshold);
		}
		return strokedShape.intersects(x - hitThreshold, y - hitThreshold,
				2 * hitThreshold, 2 * hitThreshold);

		/*
		 * return gp.intersects(x-2,y-2,4,4) && !gp.contains(x-2,y-2,4,4);
		 */
	}

	@Override
	final public boolean isInside(geogebra.common.awt.GRectangle rect) {
		return rect.contains(gp.getBounds());
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		return gp.intersects(rect);
	}

	@Override
	final public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	final public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	final public geogebra.common.awt.GRectangle getBounds() {
		if (!geo.isDefined() || !locus.isClosedPath()
				|| !geo.isEuclidianVisible()) {
			return null;
		}
		return gp.getBounds();
	}

}

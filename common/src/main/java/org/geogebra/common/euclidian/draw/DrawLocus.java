/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.CurvePlotter;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocusND;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.util.debug.Log;

/**
 * Drawable representation oflocus
 *
 */
public class DrawLocus extends Drawable {

	private GeoLocusND<? extends MyPoint> locus;

	private boolean isVisible, labelVisible;
	private GeneralPathClippedForCurvePlotter gp;
	private double[] labelPosition;
	private CoordSys coordSys;

	/**
	 * Creates new drawable for given locus
	 * 
	 * @param view
	 *            view
	 * @param locus
	 *            locus
	 */
	public DrawLocus(EuclidianView view, GeoLocusND<? extends MyPoint> locus,
			CoordSys sys) {
		this.view = view;
		this.locus = locus;
		geo = locus;
		this.coordSys = sys;

		update();
	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible)
			return;

		buildGeneralPath(locus.getPoints());

		// line on screen?
		if (!geo.isInverseFill()
				&& !gp.intersects(0, 0, view.getWidth(), view.getHeight())) {
			isVisible = false;
			// don't return here to make sure that getBounds() works for
			// offscreen points too
		}
		updateStrokes(geo);

		labelVisible = geo.isLabelVisible();
		if (labelVisible) {
			labelDesc = geo.getLabelDescription();
			double xmin = view.getXmin();
			double xmax = view.getXmax();
			double ymin = view.getYmin();
			double ymax = view.getYmax();
			double x = labelPosition[0];
			double y = labelPosition[1];
			double width = view.getWidth();
			double height = view.getHeight();
			xLabel = (int) ((x - xmin) / (xmax - xmin) * width) + 5;
			yLabel = (int) (height - (y - ymin) / (ymax - ymin) * height) + 4
					+ view.getFontSize();
			/*
			 * Adding (5,4) will hopefully move the label out of the curve's
			 * direct hiding. This is just a hack, and it does not work always.
			 */
			addLabelOffsetEnsureOnScreen(1.0, 1.0, view.getFontLine());
		}

		// draw trace
		if (geo.isTraceable() && (geo instanceof Traceable)
				&& ((Traceable) geo).getTrace()) {
			isTracing = true;
			GGraphics2D g2 = view.getBackgroundGraphics();
			if (g2 != null)
				drawTrace(g2);
		} else {
			if (isTracing) {
				isTracing = false;
				// view.updateBackground();
			}
		}
		if (geo.isInverseFill()) {
			setShape(AwtFactory.prototype
					.newArea(view.getBoundingPath()));
			getShape().subtract(AwtFactory.prototype.newArea(gp));
		}

	}

	@Override
	protected final void drawTrace(GGraphics2D g2) {
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

		// Use the last plotted point for positioning the label:
		labelPosition = CurvePlotter.draw(gp, pointList, coordSys);
		/*
		 * Due to numerical instability of the curve plotter algorithm this
		 * position may be changing too quickly which results in an annoying
		 * vibration of the label. To avoid this, we prefer to find the
		 * bottom-left position of the curve, that is, for which the sum of
		 * coordinates is minimal.
		 */
		int plSize = pointList.size();
		for (int i = 0; i < plSize; ++i) {
			double px = ((MyPoint) pointList.get(i)).x;
			double py = ((MyPoint) pointList.get(i)).y;
			if (px + py < labelPosition[0] + labelPosition[1]) {
				labelPosition[0] = px;
				labelPosition[1] = py;
			}
		}
	}

	@Override
	final public void draw(GGraphics2D g2) {
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
					&& geo.isFilled()) {
				try {

					fill(g2, (geo.isInverseFill() ? getShape() : gp), false); // fill
																				// using
																				// default/hatching/image
																				// as
																				// appropriate

				} catch (Exception e) {
					Log.error(e.getMessage());
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
		GShape t = geo.isInverseFill() ? getShape() : gp;
		if (t == null) {
			return false; // hasn't been drawn yet (hidden)
		}


		if (geo.isFilled()) {
			return t.intersects(x - hitThreshold, y - hitThreshold,
					2 * hitThreshold, 2 * hitThreshold);
		}
		if (!isVisible) {
			return false;
		}
		if (strokedShape == null) {
			strokedShape = objStroke.createStrokedShape(gp);
		}
		return strokedShape.intersects(x - hitThreshold, y - hitThreshold,
				2 * hitThreshold, 2 * hitThreshold);

		/*
		 * return gp.intersects(x-2,y-2,4,4) && !gp.contains(x-2,y-2,4,4);
		 */
	}

	@Override
	final public boolean isInside(GRectangle rect) {
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
	final public GRectangle getBounds() {
		if (!geo.isDefined() || !locus.isClosedPath()
				|| !geo.isEuclidianVisible()) {
			return null;
		}
		return gp.getBounds();
	}

}

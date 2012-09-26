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
import geogebra.common.euclidian.GeneralPathClipped;
import geogebra.common.kernel.algos.AlgoFunctionAreaSums;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.App;

/**
 * Draws upper / lower sum of a GeoFunction
 * 
 * @author Markus Hohenwarter
 */
public class DrawUpperLowerSum extends Drawable {

	private GeoNumeric sum;
	private NumberValue a, b; // interval borders

	private boolean isVisible, labelVisible;
	private AlgoFunctionAreaSums algo;
	private GeneralPathClipped gp;
	private double[] coords = new double[2];
	private boolean trapeziums;
	private boolean histogram;
	private boolean barchartFreqs, barchartFreqsWidth;

	/**
	 * Creates graphical representation of the sum / barchart /...
	 * 
	 * @param view
	 *            Euclidian view to be drawn into
	 * @param n
	 *            The sum / barchart / boxplot / histogram to be drawn
	 */
	public DrawUpperLowerSum(EuclidianView view, GeoNumeric n) {
		this.view = view;
		sum = n;
		geo = n;

		n.setDrawable(true);

		init();
		update();
	}

	private void init() {
		algo = (AlgoFunctionAreaSums) geo.getDrawAlgorithm();
		this.trapeziums = algo.useTrapeziums();
		this.histogram = algo.isHistogram();
		this.barchartFreqs = algo.getType() == AlgoFunctionAreaSums.TYPE_BARCHART_FREQUENCY_TABLE;
		this.barchartFreqsWidth = algo.getType() == AlgoFunctionAreaSums.TYPE_BARCHART_FREQUENCY_TABLE_WIDTH;
		a = algo.getA();
		b = algo.getB();
	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible)
			return;
		if (!geo.getDrawAlgorithm().equals(geo.getParentAlgorithm()))
			init();
		labelVisible = geo.isLabelVisible();
		updateStrokes(sum);

		if (gp == null)
			gp = new GeneralPathClipped(view);

		if (barchartFreqs || histogram) {
			updateBarChart();
			return;
		}

		// init gp
		gp.reset();

		double aRW = a.getDouble();
		double bRW = b.getDouble();

		double ax = view.toScreenCoordXd(aRW);
		double bx = view.toScreenCoordXd(bRW);
		double y0 = view.getyZero();

		// plot upper/lower sum rectangles
		int N = algo.getIntervals();
		double[] leftBorder = algo.getLeftBorder();
		double[] yval = algo.getValues();

		// first point
		double x = ax;
		double y = y0;
		gp.moveTo(x, y);
		for (int i = 0; i < N; i++) {
			coords[0] = leftBorder[i];
			coords[1] = yval[i];
			view.toScreenCoords(coords);

			/*
			 * removed - so that getBounds() works // avoid too big y values if
			 * (coords[1] < 0 && !trapeziums) { coords[1] = -1; } else if
			 * (coords[1] > view.height && !trapeziums) { coords[1] =
			 * view.height + 1; }
			 */

			x = coords[0];

			if (trapeziums)
				gp.lineTo(x, coords[1]); // top
			else
				gp.lineTo(x, y); // top

			gp.lineTo(x, y0); // RHS
			gp.moveTo(x, y);
			y = coords[1];
			gp.moveTo(x, y0);
			gp.lineTo(x, y);
		}
		if (trapeziums) {
			coords[0] = leftBorder[N];
			coords[1] = yval[N];
			view.toScreenCoords(coords);
			gp.lineTo(bx, coords[1]); // last bar: top
		} else if (!barchartFreqsWidth) {
			gp.lineTo(bx, y); // last bar: top
		}

		if (histogram)
			gp.moveTo(bx, y0);
		else if (!barchartFreqsWidth)
			gp.lineTo(bx, y0);// last bar: right

		gp.lineTo(ax, y0);// all bars, along bottom

		// gp on screen?
		if (!gp.intersects(0, 0, view.getWidth(), view.getHeight())) {
			isVisible = false;
			// don't return here to make sure that getBounds() works for
			// offscreen points too
		}

		if (labelVisible) {
			xLabel = (int) Math.round((ax + bx) / 2) - 6;
			yLabel = (int) view.getyZero() - view.getFontSize();
			labelDesc = geo.getLabelDescription();
			addLabelOffset();
		}
	}

	private void updateBarChart() {
		gp.reset();
		double base = view.getyZero();

		int N = algo.getIntervals();
		double[] leftBorder = algo.getLeftBorder();
		double[] yval = algo.getValues();

		gp.moveTo(view.toScreenCoordXd(leftBorder[0]), base);

		for (int i = 0; i < N - 1; i++) {

			double x0 = view.toScreenCoordXd(leftBorder[i]);
			double height = view.toScreenCoordYd(yval[i]);
			double x1 = view.toScreenCoordXd(leftBorder[i + 1]);

			gp.lineTo(x0, height); // up
			gp.lineTo(x1, height); // along
			gp.lineTo(x1, base); // down

		}

		gp.lineTo(view.toScreenCoordXd(leftBorder[0]), base);

		// gp on screen?
		if (!gp.intersects(0, 0, view.getWidth(), view.getHeight())) {
			isVisible = false;
			// don't return here to make sure that getBounds() works for
			// offscreen points too
		}

		if (labelVisible) {
			xLabel = (view.toScreenCoordX(leftBorder[0]) + view
					.toScreenCoordX(leftBorder[N - 1])) / 2 - 6;
			yLabel = (int) view.getyZero() - view.getFontSize();
			labelDesc = geo.getLabelDescription();
			addLabelOffset();
		}

	}

	@Override
	final public void draw(geogebra.common.awt.GGraphics2D g2) {
		if (isVisible) {
			try {
				if (geo.doHighlighting()) {
					g2.setPaint(sum
							.getSelColor());
					g2.setStroke(selStroke);
					g2.draw(gp);
				}
			} catch (Exception e) {
				App.debug(e.getMessage());
			}

			try {
				fill(g2, gp, false); // fill using default/hatching/image as
										// appropriate
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if (geo.lineThickness > 0) {
					g2.setPaint(sum
							.getObjectColor());
					g2.setStroke(objStroke);
					g2.draw(gp);
				}
			} catch (Exception e) {
				App.debug(e.getMessage());
			}

			if (labelVisible) {
				g2.setFont(view.getFontConic());
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
			}
		}
	}

	@Override
	final public boolean hit(int x, int y) {
		return gp != null
				&& (gp.contains(x, y) || gp.intersects(x - 3, y - 3, 6, 6));
	}

	@Override
	final public boolean isInside(geogebra.common.awt.GRectangle rect) {
		return false;
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		return gp != null && gp.intersects(rect);
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
		if (!geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}
		return gp.getBounds();
	}
}

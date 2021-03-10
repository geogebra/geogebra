package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;
import java.util.Iterator;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.kernel.algos.AlgoBoxPlot;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.util.debug.Log;

/**
 * Drawable representation of box plots
 *
 */
public class DrawBoxPlot extends Drawable {
	private boolean isVisible;
	private boolean labelVisible;
	private double[] coords = new double[2];
	private GeneralPathClipped gp;
	private GeoNumeric sum;
	private AlgoBoxPlot algo;
	private NumberValue a;
	private NumberValue b;
	private double OUTLIER_SIZE = 4;

	/**
	 * @param view
	 *            view
	 * @param n
	 *            number (boxplot)
	 */
	public DrawBoxPlot(EuclidianView view, GeoNumeric n) {
		this.view = view;
		sum = n;
		geo = n;

		n.setDrawable(true);

		init();
		update();
	}

	private void init() {
		algo = (AlgoBoxPlot) geo.getDrawAlgorithm();
		a = algo.getA();
		b = algo.getB();
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (isVisible) {
			try {
				if (isHighlighted()) {
					g2.setPaint(sum.getSelColor());
					g2.setStroke(selStroke);
					g2.draw(gp);
				}
			} catch (Exception e) {
				Log.debug(e.getMessage());
			}

			try {
				fill(g2, gp); // fill using default/hatching/image as
								// appropriate
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if (geo.getLineThickness() > 0) {
					g2.setPaint(getObjectColor());
					g2.setStroke(objStroke);
					g2.draw(gp);
				}
			} catch (Exception e) {
				Log.debug(e.getMessage());
			}

			if (labelVisible) {
				g2.setFont(view.getFontConic());
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
			}
		}
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		return gp != null
				&& (gp.contains(x, y) || gp.intersects(x, y, hitThreshold));
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		return gp != null && gp.intersects(rect);
	}

	@Override
	public boolean isInside(GRectangle rect) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible) {
			return;
		}
		if (!geo.getDrawAlgorithm().equals(geo.getParentAlgorithm())) {
			init();
		}
		labelVisible = geo.isLabelVisible();
		updateStrokes(sum);

		if (gp == null) {
			gp = new GeneralPathClipped(view);
		}
		// init gp
		gp.resetWithThickness(geo.getLineThickness());
		double yOff = a.getDouble();
		double yScale = b.getDouble();

		// plot upper/lower sum rectangles
		double[] leftBorder = algo.getLeftBorders();

		coords[0] = leftBorder[0];
		coords[1] = -yScale + yOff;
		view.toScreenCoords(coords);
		gp.moveTo(coords[0], coords[1]);

		coords[0] = leftBorder[0];
		coords[1] = yScale + yOff;
		view.toScreenCoords(coords);
		gp.lineTo(coords[0], coords[1]);

		coords[0] = leftBorder[0];
		coords[1] = 0 + yOff;
		view.toScreenCoords(coords);
		gp.moveTo(coords[0], coords[1]);

		coords[0] = leftBorder[1];
		coords[1] = 0 + yOff;
		view.toScreenCoords(coords);
		gp.lineTo(coords[0], coords[1]);

		coords[0] = leftBorder[1];
		coords[1] = yScale + yOff;
		view.toScreenCoords(coords);
		gp.lineTo(coords[0], coords[1]);

		coords[0] = leftBorder[3];
		coords[1] = yScale + yOff;
		view.toScreenCoords(coords);
		gp.lineTo(coords[0], coords[1]);

		coords[0] = leftBorder[3];
		coords[1] = -yScale + yOff;
		view.toScreenCoords(coords);
		gp.lineTo(coords[0], coords[1]);

		coords[0] = leftBorder[1];
		coords[1] = -yScale + yOff;
		view.toScreenCoords(coords);
		gp.lineTo(coords[0], coords[1]);

		coords[0] = leftBorder[1];
		coords[1] = 0 + yOff;
		view.toScreenCoords(coords);
		gp.lineTo(coords[0], coords[1]);

		coords[0] = leftBorder[3];
		coords[1] = 0 + yOff;
		view.toScreenCoords(coords);
		gp.moveTo(coords[0], coords[1]);

		coords[0] = leftBorder[4];
		coords[1] = 0 + yOff;
		view.toScreenCoords(coords);
		gp.lineTo(coords[0], coords[1]);

		coords[0] = leftBorder[4];
		coords[1] = yScale + yOff;
		view.toScreenCoords(coords);
		gp.moveTo(coords[0], coords[1]);

		coords[0] = leftBorder[4];
		coords[1] = -yScale + yOff;
		view.toScreenCoords(coords);
		gp.lineTo(coords[0], coords[1]);

		coords[0] = leftBorder[2];
		coords[1] = yScale + yOff;
		view.toScreenCoords(coords);
		gp.moveTo(coords[0], coords[1]);

		coords[0] = leftBorder[2];
		coords[1] = -yScale + yOff;
		view.toScreenCoords(coords);
		gp.lineTo(coords[0], coords[1]);

		ArrayList<Double> outliers = algo.getOutliers();

		if (outliers != null) {
			Iterator<Double> it = outliers.iterator();

			while (it.hasNext()) {
				coords[0] = it.next().doubleValue();
				coords[1] = yOff;
				view.toScreenCoords(coords);

				// draw cross
				gp.moveTo(coords[0] - OUTLIER_SIZE, coords[1] - OUTLIER_SIZE);
				gp.lineTo(coords[0] + OUTLIER_SIZE, coords[1] + OUTLIER_SIZE);
				gp.moveTo(coords[0] - OUTLIER_SIZE, coords[1] + OUTLIER_SIZE);
				gp.lineTo(coords[0] + OUTLIER_SIZE, coords[1] - OUTLIER_SIZE);
			}
		}

		// gp on screen?
		if (!view.intersects(gp)) {
			isVisible = false;
			// don't return here to make sure that getBounds() works for
			// offscreen points too
		}

		if (labelVisible) {
			xLabel = (int) coords[0];
			yLabel = (int) coords[1] - view.getFontSize();
			labelDesc = geo.getLabelDescription();
			addLabelOffset();
		}

	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 */
	@Override
	final public GRectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible() || gp == null) {
			return null;
		}
		return gp.getBounds();
	}

}

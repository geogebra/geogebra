package geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.Iterator;

import geogebra.common.awt.GRectangle;
import geogebra.common.kernel.algos.AlgoBoxPlot;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.App;
/**
 * Drawable representation of box plots
 *
 */
public class DrawBoxPlot extends Drawable {
	private boolean isVisible, labelVisible;
	private double[] coords = new double[2];
	private GeneralPathClipped gp;
	private GeoNumeric sum;
	private AlgoBoxPlot algo;
	private NumberValue a, b;
	private double OUTLIER_SIZE = 4;

	/**
	 * @param view view
	 * @param n number (boxplot)
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
	public void draw(geogebra.common.awt.GGraphics2D g2) {
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
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public boolean hit(int x, int y) {
		return gp != null
				&& (gp.contains(x, y) || gp.intersects(x - 3, y - 3, 6, 6));
	}

	@Override
	public boolean isInside(GRectangle rect) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;

	}

	@Override
	public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible)
			return;
		if (!geo.getDrawAlgorithm().equals(geo.getParentAlgorithm()))
			init();
		labelVisible = geo.isLabelVisible();
		updateStrokes(sum);

		if (gp == null)
			gp = new GeneralPathClipped(view);
		// init gp
		gp.reset();
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
				gp.moveTo(coords[0] - OUTLIER_SIZE , coords[1] - OUTLIER_SIZE);
				gp.lineTo(coords[0] + OUTLIER_SIZE, coords[1] + OUTLIER_SIZE);
				gp.moveTo(coords[0] - OUTLIER_SIZE , coords[1] + OUTLIER_SIZE);
				gp.lineTo(coords[0] + OUTLIER_SIZE, coords[1] - OUTLIER_SIZE);
			}
		}

		// gp on screen?
		if (!gp.intersects(0, 0, view.getWidth(), view.getHeight())) {
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

}

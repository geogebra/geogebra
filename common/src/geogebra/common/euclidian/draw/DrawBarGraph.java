package geogebra.common.euclidian.draw;

import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.GeneralPathClipped;
import geogebra.common.kernel.algos.AlgoBarChart;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.App;

/**
 * Drawable representation of a bar graph
 * 
 */
public class DrawBarGraph extends Drawable {
	private boolean isVisible, labelVisible;
	private double[] coords = new double[2];
	private GeneralPathClipped gp;
	private GeoNumeric sum;
	private AlgoBarChart algo;

	private boolean isVertical = true;

	/**
	 * @param view
	 *            view
	 * @param n
	 *            number (boxplot)
	 */
	public DrawBarGraph(EuclidianView view, GeoNumeric n) {
		this.view = view;
		sum = n;
		geo = n;

		n.setDrawable(true);

		init();
		update();
	}

	private void init() {
		algo = (AlgoBarChart) geo.getDrawAlgorithm();
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
	
	@Override
	public void draw(geogebra.common.awt.GGraphics2D g2) {
		if (isVisible) {
			try {
				if (geo.doHighlighting()) {
					g2.setPaint(sum.getSelColor());
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
					g2.setPaint(sum.getObjectColor());
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

		double[] xVal = algo.getLeftBorder();
		double[] yVal = algo.getValues();

		
		double width = algo.getWidth();
		int N = algo.getIntervals();

		if (isVertical) {
			// draw vertical bars
			if (width <= 0) {

				for (int i = 0; i < N; i++) {

					coords[0] = xVal[i];
					coords[1] = 0;
					view.toScreenCoords(coords);
					gp.moveTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp.lineTo(coords[0], coords[1]);
				}

			} else {

				for (int i = 0; i < N; i++) {

					coords[0] = xVal[i];
					coords[1] = 0;
					view.toScreenCoords(coords);
					gp.moveTo(coords[0], coords[1]);

					coords[0] = xVal[i] ;
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp.lineTo(coords[0], coords[1]);

					coords[0] = xVal[i] + width;
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp.lineTo(coords[0], coords[1]);

					coords[0] = xVal[i] + width;
					coords[1] = 0;
					view.toScreenCoords(coords);
					gp.lineTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = 0;
					view.toScreenCoords(coords);
					gp.lineTo(coords[0], coords[1]);
				}

			}
			
			// horizontal bars
		} else {
			
			if (width <= 0) {

				for (int i = 0; i < N; i++) {

					coords[0] = 0;
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp.moveTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp.lineTo(coords[0], coords[1]);
				}

			} else {

				for (int i = 0; i < N; i++) {

					coords[0] = 0; 
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp.moveTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp.lineTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = yVal[i] + width;
					view.toScreenCoords(coords);
					gp.lineTo(coords[0], coords[1]);

					coords[0] = 0; 
					coords[1] = yVal[i] + width;
					view.toScreenCoords(coords);
					gp.lineTo(coords[0], coords[1]);

					coords[0] = 0; 
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp.lineTo(coords[0], coords[1]);
				}

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

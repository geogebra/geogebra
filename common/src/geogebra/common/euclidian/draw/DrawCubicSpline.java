package geogebra.common.euclidian.draw;

import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.GeneralPathClipped;
import geogebra.common.kernel.algos.AlgoCubicSpline;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * Draws a cubic spline 
 * 
 * @author Giuliano Bellucci
 * 
 */

public class DrawCubicSpline extends Drawable {

	private boolean isVisible;
	private AlgoCubicSpline algo;
	private int size;
	private GeoList list;
	private GeneralPathClipped gp;

	/**
	 * @param view
	 *            - Euclidian view
	 * @param geo
	 *            - List of parametric curves
	 */
	public DrawCubicSpline(EuclidianView view, GeoList geo) {
		this.view = view;
		this.geo = geo.toGeoElement();
		algo = (AlgoCubicSpline) geo.getParentAlgorithm();
		list = algo.getList();
		size = list.size();
		update();
	}

	@Override
	public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible)
			return;
		updateStrokes(geo);
	}

	@Override
	public void draw(GGraphics2D g2) {
		boolean highlighting = geo.doHighlighting();
		if (!isVisible) {
			return;
		}
		if (highlighting) {
			g2.setPaint(geo.getSelColor());
			g2.setStroke(selStroke);
		} else {
			g2.setPaint(geo.getObjectColor());
			g2.setStroke(objStroke);
		}
		gp = new GeneralPathClipped(view);
		algo.compute();
		float[][] currentPoints = algo.getPoints();
		float currentDrawX;
		float currentDrawY;
		double[] coords = { currentPoints[0][0], currentPoints[0][1] };
		view.toScreenCoords(coords);
		gp.moveTo((float) coords[0], (float) coords[1]);
		g2.setColor(geo.getObjectColor());
		int splineParameterIndex = 0;
		float[] splineParameters = algo.getParametersX();
		float[] lx = algo.getParameterIntervalLimits();
		float[] splineParametersY = algo.getParametersY();
		for (float p = 0; p <= 1; p = p + 0.01f) {
			splineParameterIndex = calculate(p, lx);
			currentDrawX = splineParameters[splineParameterIndex] * p * p * p
					+ splineParameters[splineParameterIndex + 1] * p * p
					+ splineParameters[splineParameterIndex + 2] * p
					+ splineParameters[splineParameterIndex + 3];
			currentDrawY = splineParametersY[splineParameterIndex] * p * p * p
					+ splineParametersY[splineParameterIndex + 1] * p * p
					+ splineParametersY[splineParameterIndex + 2] * p
					+ splineParametersY[splineParameterIndex + 3];
			double[] coordsC = { currentDrawX, currentDrawY };
			view.toScreenCoords(coordsC);
			gp.lineTo((float) coordsC[0], (float) coordsC[1]);
		}
		EuclidianStatic.drawWithValueStrokePure(gp, g2);

	}

	private static int calculate(float x, float[] m) {
		for (int i = m.length - 1; i > -1; i--) {
			if (x > m[i]) {
				return i * 4;
			}
		}
		return 0;
	}

	@Override
	public boolean hit(int x, int y) {
		Drawable d;
		for (int i = 0; i < size; i++) {
			d = new DrawParametricCurve(view, (GeoCurveCartesian) list.get(i));
			if (d.hit(x, y)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isInside(GRectangle rect) {
		Drawable d;
		for (int i = 0; i < size; i++) {
			d = new DrawParametricCurve(view, (GeoCurveCartesian) list.get(i));
			if (d.isInside(rect)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

}

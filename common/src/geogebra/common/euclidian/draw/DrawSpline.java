package geogebra.common.euclidian.draw;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.GeneralPathClipped;
import geogebra.common.kernel.algos.AlgoSpline;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumberValue;

/**
 * Draws a cubic spline
 * 
 * @author Giuliano Bellucci
 * 
 */

public class DrawSpline extends Drawable {

	private boolean isVisible;
	private AlgoSpline algo;
	private int size;
	private GeoList list;
	private GeneralPathClipped gp;
	private GeoNumberValue degree;
	private int degreeValue;
	/**
	 * @param view
	 *            - Euclidian view
	 * @param geo
	 *            - List of parametric curves
	 */
	public DrawSpline(EuclidianView view, GeoList geo) {
		this.view = view;
		this.geo = geo.toGeoElement();
		algo = (AlgoSpline) geo.getParentAlgorithm();
		list = algo.getSpline();
		algo.getSpline().setObjColor(GColor.black);
		degree=algo.getDegree();
		size = list.size();
		update();
	}

	@Override
	public void update() {
		isVisible = list.isEuclidianVisible();
		if (!isVisible ){		
			return;
		}
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
		degreeValue=(int)degree.getDouble()+1;
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
		float[] splineParametersX = algo.getParametersX();
		float[] lx = algo.getParameterIntervalLimits();
		float[] splineParametersY = algo.getParametersY();
		for (float p = 0; p <= 1; p = p + 0.01f) {
			splineParameterIndex = calculate(p, lx);
			currentDrawX=calc(splineParametersX,splineParameterIndex,p);
			currentDrawY=calc(splineParametersY,splineParameterIndex,p);
			double[] coordsC = { currentDrawX, currentDrawY };
			view.toScreenCoords(coordsC);
			gp.lineTo((float) coordsC[0], (float) coordsC[1]);
		}
		EuclidianStatic.drawWithValueStrokePure(gp, g2);
	}

	private float calc(float[] splineParameters, int splineParameterIndex, float p) {
		double value=0;
		for (int i=degreeValue-1;i>-1;i--){
			value+=Math.pow(p, i)*splineParameters[splineParameterIndex+degreeValue-1-i];
		}
		return (float) value;
	}

	private  int calculate(float x, float[] m) {
		for (int i = m.length - 1; i > -1; i--) {
			if (x > m[i]) {
				return i * degreeValue;
			}
		}
		return 0;
	}

	@Override
	public boolean hit(int x, int y) {
		if(!isVisible){
			return false;
		}
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
		if(!isVisible){
			return false;
		}
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

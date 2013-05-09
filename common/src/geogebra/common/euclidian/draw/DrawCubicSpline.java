package geogebra.common.euclidian.draw;

import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.algos.AlgoCubicSpline;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * Draws a cubic spline using a set of parametric curves
 * 
 * @author Giuliano Bellucci
 * 
 */

public class DrawCubicSpline extends Drawable {

	private boolean isVisible;
	private AlgoCubicSpline algo;

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
		update();
	}

	@Override
	public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible)
			return;
		updateStrokes(geo);
		algo.compute();
	}

	@Override
	public void draw(GGraphics2D g2) {
		boolean highlighting = geo.doHighlighting();
		if (!isVisible) {
			return;
		}

		GeoList list = algo.getList();
		Drawable d;
		GeoCurveCartesian c;
		for (int i = 0; i < list.size(); i++) {
			c = (GeoCurveCartesian) list.get(i);
			c.setVisibility(view.getViewID(), true);
			c.setEuclidianVisible(true);
			c.setObjColor(list.getObjectColor());
			c.setHighlighted(highlighting);
			d = new DrawParametricCurve(view, c);
			d.draw(g2);
		}
	}

	@Override
	public boolean hit(int x, int y) {
		Drawable d;
		GeoList list = algo.getList();
		for (int i = 0; i < list.size(); i++) {
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
		GeoList list = algo.getList();
		for (int i = 0; i < list.size(); i++) {
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

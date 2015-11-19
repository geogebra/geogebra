package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.euclidian.plot.CurvePlotter;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;

/**
 * @author ggb3D
 * 
 *         Drawable for GeoCurveCartesian3D
 * 
 */
public class DrawCurve3D extends Drawable3DCurves {

	/** handle to the curve */
	private CurveEvaluable curve;

	/**
	 * @param a_view3d
	 *            the 3D view where the curve is drawn
	 * @param curve
	 *            the 3D curve to draw
	 */
	public DrawCurve3D(EuclidianView3D a_view3d, CurveEvaluable curve) {
		super(a_view3d, (GeoElement) curve);
		this.curve = curve;

	}

	@Override
	public void drawGeometry(Renderer renderer) {

		renderer.getGeometryManager().draw(getGeometryIndex());

	}

	@Override
	protected boolean updateForItSelf() {

		EuclidianView3D view = getView3D();

		Renderer renderer = view.getRenderer();

		PlotterBrush brush = renderer.getGeometryManager().getBrush();
		brush.start(getReusableGeometryIndex());
		brush.setThickness(getGeoElement().getLineThickness(),
				(float) view.getScale());
		brush.setAffineTexture(0f, 0f);
		brush.setLength(1f);

		double min, max;
		if (curve instanceof GeoFunction) {
			if (((GeoFunction) curve).hasInterval()) {
				min = ((GeoFunction) curve).getIntervalMin();
				max = ((GeoFunction) curve).getIntervalMax();
				double minView = view.getXmin();
				double maxView = view.getXmax();
				if (min < minView)
					min = minView;
				if (max > maxView)
					max = maxView;
			} else {
				min = view.getXmin();
				max = view.getXmax();
			}
		} else {
			min = curve.getMinParameter();
			max = curve.getMaxParameter();
		}

		// App.debug(min+","+max);

		CurvePlotter.plotCurve(curve, min, max, view, brush, false,
				CurvePlotter.Gap.MOVE_TO);

		setGeometryIndex(brush.end());

		return true;

	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChangedByZoom()
				|| getView3D().viewChangedByTranslate()) {
			updateForItSelf();
		}
	}

	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_PATH;
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		addToDrawable3DLists(lists, DRAW_TYPE_CLIPPED_CURVES);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		removeFromDrawable3DLists(lists, DRAW_TYPE_CLIPPED_CURVES);
	}

	private GeoPoint3D hittingPoint;
	private Coords project;
	private double[] lineCoords;

	@Override
	public boolean hit(Hitting hitting) {

		if (waitForReset) { // prevent NPE
			return false;
		}
		
		GeoCurveCartesianND curveND = (GeoCurveCartesianND) getGeoElement();

		if (hittingPoint == null){
			hittingPoint = new GeoPoint3D(curveND.cons);
			project = new Coords(4);
			lineCoords = new double[2];
		}

		hittingPoint.setWillingCoords(hitting.origin);
		hittingPoint.setWillingDirection(hitting.direction);

		double t = curveND.getClosestParameter(hittingPoint, hittingPoint
				.getPathParameter().getT());
		
		// App.debug("" + t);
		
		hittingPoint.getPathParameter().setT(t);

		curveND.pathChanged(hittingPoint);

		Coords closestPoint = hittingPoint.getInhomCoordsInD3();
		closestPoint.projectLine(hitting.origin, hitting.direction, project,
				lineCoords);

		// App.debug("\n" + hitting.origin + "\nclosest point:\n" + closestPoint
		// + "\nclosest point on line:\n" + project);

		double d = project.distance(closestPoint);
		if (d * getView3D().getScale() <= getGeoElement().getLineThickness() + 2) {
			double z = -lineCoords[0];
			double dz = getGeoElement().getLineThickness()
					/ getView3D().getScale();
			setZPick(z + dz, z - dz);
			return true;
		}

		return false;


	}

	@Override
	public boolean hitForList(Hitting hitting) {
		if (hasGeoElementVisible() && getGeoElement().isPickable()) {
			return hit(hitting);
		}

		return false;
	}

}

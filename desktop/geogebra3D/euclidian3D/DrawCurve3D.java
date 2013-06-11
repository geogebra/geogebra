package geogebra3D.euclidian3D;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.ParametricCurve;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.plots.CurveMesh;
import geogebra3D.kernel3D.GeoCurveCartesian3D;

/**
 * @author ggb3D
 * 
 *         Drawable for GeoCurveCartesian3D
 * 
 */
public class DrawCurve3D extends Drawable3DCurves {

	private CurveMesh mesh;
	/** handle to the curve */
	private ParametricCurve curve;

	/** current domain for the function on the format {umin, umax} */
	private double[] domain = new double[2];

	/** Current culling box - set to view3d.(x|y|z)(max|min) */
	private double[] cullingBox = new double[6];

	/**
	 * @param a_view3d
	 *            the 3D view where the curve is drawn
	 * @param curve
	 *            the 3D curve to draw
	 */
	public DrawCurve3D(EuclidianView3D a_view3d, ParametricCurve curve) {
		super(a_view3d, (GeoElement) curve);
		this.curve = curve;

		updateDomain();
		updateCullingBox();
		mesh = new CurveMesh((GeoCurveCartesian3D) curve, cullingBox, (float) a_view3d.getScale());

	}

	@Override
	public void drawGeometry(Renderer renderer) {

		renderer.getGeometryManager().draw(getGeometryIndex());

	}



	private boolean updateDomain() {
		boolean changed = false;

		double t = curve.getMinParameter();
		if (t != domain[0]) {
			changed = true;
			domain[0] = t;
		}
		t = curve.getMaxParameter();
		if (t != domain[1]) {
			changed = true;
			domain[1] = t;
		}

		return changed;
	}

	/**
	 * gets the viewing radius based on the viewing frustum
	 */
	private void updateCullingBox() {
		EuclidianView3D view = getView3D();
		cullingBox[0] = view.getXMinMax()[0];
		cullingBox[1] = view.getXMinMax()[1];
		cullingBox[2] = view.getYMinMax()[0];
		cullingBox[3] = view.getYMinMax()[1];
		cullingBox[4] = view.getZMinMax()[0];
		cullingBox[5] = view.getZMinMax()[1];
	}

	@Override
	protected boolean updateForItSelf() {
		
		/*
		EuclidianView3D view = getView3D();
		
		Renderer renderer = view.getRenderer();

		PlotterBrush brush = renderer.getGeometryManager().getBrush();	
		brush.start(8);
		brush.setThickness(getGeoElement().getLineThickness(),(float) view.getScale());		
		brush.setAffineTexture(0f,0f);
		
		double min = curve.getMinParameter();
		double max = curve.getMaxParameter();
		if (curve instanceof GeoFunction) {
			double minView = view.getXmin();
			double maxView = view.getXmax();
			if (min < minView)
				min = minView;
			if (max > maxView)
				max = maxView;
		}
		
		App.debug(min+","+max);

		CurvePlotter.plotCurve(curve, min, max, view, brush, false, CurvePlotter.Gap.MOVE_TO);

		setGeometryIndex(brush.end());
		
		return true;
		*/
		
		boolean stillNeedsUpdate = true;


		if (updateDomain()) {
			//domain has changed - create a new mesh
			mesh = new CurveMesh((GeoCurveCartesian3D) curve, cullingBox, (float) getView3D().getScale());
		} else {
			//otherwise, update the surface
			mesh.updateParameters();
		}


		Renderer renderer = getView3D().getRenderer();
		mesh.setCullingBox(cullingBox);
		mesh.updateScale((float) getView3D().getScale());
		stillNeedsUpdate = mesh.optimize();

		PlotterBrush brush = renderer.getGeometryManager().getBrush();	
		brush.start(8);
		brush.setThickness(getGeoElement().getLineThickness(),(float) getView3D().getScale());		
		brush.setAffineTexture(0f,0f);

		brush.draw(mesh);

		setGeometryIndex(brush.end());

		return !stillNeedsUpdate;
	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChangedByZoom() || getView3D().viewChangedByTranslate()){
			updateCullingBox();
			setWaitForUpdate();
		}
	}

	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_1D;
	}

}

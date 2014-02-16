package geogebra3D.geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.euclidian.plot.CurvePlotter;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.kernelND.CurveEvaluable;
import geogebra3D.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import geogebra3D.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;

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
		brush.start(8);
		brush.setThickness(getGeoElement().getLineThickness(),(float) view.getScale());		
		brush.setAffineTexture(0f,0f);
		brush.setLength(1f);
		
		double min, max; 
		if (curve instanceof GeoFunction) {
			if (((GeoFunction) curve).hasInterval()){
				min = ((GeoFunction) curve).getIntervalMin();
				max = ((GeoFunction) curve).getIntervalMax();
				double minView = view.getXmin();
				double maxView = view.getXmax();
				if (min < minView)
					min = minView;
				if (max > maxView)
					max = maxView;
			}else{
				min = view.getXmin();
				max = view.getXmax();
			}
		}else{
			min = curve.getMinParameter();
			max = curve.getMaxParameter();
		}
		
		//App.debug(min+","+max);

		CurvePlotter.plotCurve(curve, min, max, view, brush, false, CurvePlotter.Gap.MOVE_TO);

		setGeometryIndex(brush.end());
		
		return true;
		
	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChangedByZoom() || getView3D().viewChangedByTranslate()){
			setWaitForUpdate();
		}
	}

	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_1D;
	}
	
	@Override
	public void addToDrawable3DLists(Drawable3DLists lists){
		addToDrawable3DLists(lists,DRAW_TYPE_CLIPPED_CURVES);
	}
    
    @Override
	public void removeFromDrawable3DLists(Drawable3DLists lists){
    	removeFromDrawable3DLists(lists,DRAW_TYPE_CLIPPED_CURVES);
    }

}


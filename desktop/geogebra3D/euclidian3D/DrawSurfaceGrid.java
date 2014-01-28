package geogebra3D.euclidian3D;

import geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra3D.euclidian3D.opengl.Renderer;

public class DrawSurfaceGrid extends Drawable3DCurves {
	
	private GeoCurveCartesian3D curve;
	
	CurveTriList triList;
	
	public DrawSurfaceGrid(EuclidianView3D view, GeoFunctionNVar func, short nLinesU, short nLinesV, float minU, float minV, float maxU, float maxV) {
		super(view, func);
		//TODO -- doesn't do anything 
		func.evaluate(StringTemplate.defaultTemplate);
		
		generate();
	}
	
	private void generate(){
		
	}

	@Override
	protected void updateForView() {
		
	}

	@Override
	protected boolean updateForItSelf() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void drawGeometry(Renderer renderer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getPickOrder() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}

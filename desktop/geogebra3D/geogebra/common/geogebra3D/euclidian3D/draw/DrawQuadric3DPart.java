package geogebra3D.geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import geogebra3D.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;

/**
 * draws a quadric part
 * @author mathieu
 *
 */
public class DrawQuadric3DPart extends DrawQuadric3D {

	public DrawQuadric3DPart(EuclidianView3D view, GeoQuadric3DPart quadric) {
		super(view, quadric);
	}


	protected double[] getMinMax(){

		GeoQuadric3DPart quadric = (GeoQuadric3DPart) getGeoElement();
		
		return new double[] {quadric.getMinParameter(1), quadric.getMaxParameter(1)};
	}
	
	
	protected void setSurfaceV(float min, float max, PlotterSurface surface){
		surface.setV(min,max);surface.setNbV(3);
	}
	
	
	
	protected void updateForView(){
		
	}
	

}

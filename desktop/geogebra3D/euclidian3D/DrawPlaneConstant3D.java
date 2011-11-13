package geogebra3D.euclidian3D;




import geogebra.kernel.Matrix.CoordMatrix4x4;
import geogebra.kernel.Matrix.CoordSys;
import geogebra.kernel.Matrix.Coords;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.opengl.Textures;
import geogebra3D.kernel3D.GeoPlane3D;

import java.awt.Color;

import javax.sql.XAConnection;




/**
 * Class for drawing 3D constant planes.
 * @author matthieu
 *
 */
public class DrawPlaneConstant3D extends DrawPlane3D {


	private DrawAxis3D xAxis, yAxis;
	
	/**
	 * Common constructor
	 * @param a_view3D
	 * @param a_plane3D
	 */
	public DrawPlaneConstant3D(EuclidianView3D a_view3D, GeoPlane3D a_plane3D,
			DrawAxis3D xAxis, DrawAxis3D yAxis){
		
		super(a_view3D, a_plane3D);
		
		this.xAxis = xAxis;
		this.yAxis = yAxis;
	}
	
	
	protected boolean updateForItSelf(){
		
		double[] xMinMax = xAxis.getDrawMinMax();
		double[] yMinMax = yAxis.getDrawMinMax();
		
		((GeoPlane3D) getGeoElement()).setGridCorners(
				xMinMax[0],yMinMax[0],
				xMinMax[1],yMinMax[1]
				);
		
		((GeoPlane3D) getGeoElement()).setGridDistances(
				xAxis.getNumbersDistance(), 
				yAxis.getNumbersDistance()
				);
		
		super.updateForItSelf(false);
		
		return !(xAxis.waitForUpdate() || yAxis.waitForUpdate());
	}
	
	protected void updateForView(){
		
	}
	
	protected boolean isGridVisible(){
		return ((GeoPlane3D)getGeoElement()).isGridVisible();
	}
	
	

}

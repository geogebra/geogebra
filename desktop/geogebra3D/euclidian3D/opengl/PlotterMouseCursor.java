package geogebra3D.euclidian3D.opengl;

import geogebra.common.kernel.Matrix.Coords;


/**
 * Class that describes the geometry of buttons for 3D view
 * 
 * @author ggb3D
 *
 */
public class PlotterMouseCursor {
	
	static private float coeff = 30f;	


	private int index;
	

	/** common constructor
	 * @param manager geometry manager
	 */
	public PlotterMouseCursor(Manager manager) {
		
		
		index = manager.startPolygons();
		manager.lineWidth(4);
		manager.pointSize(4);
		
		//normal
		Coords n = new Coords(0,0,1);
		
		//vertices
		Coords[] v = new Coords[7];		
		v[0] = new Coords(0,0,0);
		v[1] = new Coords(0.38 *coeff, -0.39 *coeff,0);
		v[2] = new Coords(0.21 *coeff, -0.38 *coeff,0);
		v[3] = new Coords(0.29 *coeff, -0.56 *coeff,0);
		v[4] = new Coords(0.21 *coeff, -0.60 *coeff,0);
		v[5] = new Coords(0.13 *coeff, -0.42 *coeff,0);
		v[6] = new Coords(0, -0.55 *coeff,0);
		
		//create geometry
		manager.drawPolygon(n, v);
		manager.endPolygons();
		
	}

	
	//////////////////////////////////
	// INDEX
	//////////////////////////////////	
	
	/** return geometry index for view in front of arrow
	 * @return geometry index for view in front of arrow
	 */
	public int getIndex(){
		return index;
	}
	
}

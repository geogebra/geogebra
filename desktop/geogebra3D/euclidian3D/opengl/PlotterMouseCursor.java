package geogebra3D.euclidian3D.opengl;

import geogebra.common.kernel.Matrix.Coords;

import java.awt.Color;

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
		
		
		index = manager.startPolygon(0, 0, 1);
		manager.lineWidth(4);
		manager.pointSize(4);
		manager.addVertexToPolygon(0,0,0);
		manager.addVertexToPolygon(0.38 *coeff, -0.39 *coeff,0);
		manager.addVertexToPolygon(0.21 *coeff, -0.38 *coeff,0);
		manager.addVertexToPolygon(0.29 *coeff, -0.56 *coeff,0);
		manager.addVertexToPolygon(0.21 *coeff, -0.60 *coeff,0);
		manager.addVertexToPolygon(0.13 *coeff, -0.42 *coeff,0);
		manager.addVertexToPolygon(0, -0.55 *coeff,0);
		manager.endPolygon();
		
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

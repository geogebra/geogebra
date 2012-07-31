package geogebra3D.euclidian3D.opengl;

import geogebra.common.kernel.Matrix.Coords;

import java.awt.Color;

/**
 * Class that describes the geometry of buttons for 3D view
 * 
 * @author ggb3D
 *
 */
public class PlotterViewInFrontOf {
	
	static private float start = 60f;	
	static private float end = 0f;


	private int[] index;
	
	private Manager manager;	

	/** common constructor
	 */
	public PlotterViewInFrontOf(Manager manager) {
		
		this.manager = manager;
		
		index = new int[1];
		
		//arrow
		PlotterBrush brush = manager.getBrush();

		brush.setArrowType(PlotterBrush.ARROW_TYPE_SIMPLE);

		//sets the thickness for arrows
		brush.setThickness(7,1f);

		//brush.setAffineTexture(0.5f, 0.125f);

		brush.start(16);
		brush.setColor(Color.GRAY,0.5f);
		//brush.setThickness(thickness);//re sets the thickness
		brush.segment(new Coords(0, 0, start, 1),new Coords(0,0,end, 1));
		index[0] =brush.end();


		brush.setArrowType(PlotterBrush.ARROW_TYPE_NONE);
	}

	
	//////////////////////////////////
	// INDEX
	//////////////////////////////////	
	
	/** return geometry index for view in front of arrow
	 * @return geometry index for view in front of arrow
	 */
	public int getIndex(){
		return index[0];
	}
	
}

package geogebra3D.euclidian3D.opengl;

import geogebra.common.kernel.Matrix.Coords;

import java.awt.Color;

/**
 * Class that describes the geometry of buttons for 3D view
 * 
 * @author ggb3D
 *
 */
public class PlotterViewButtons {
	
	
	final static public int TYPE_OK = 0;
	final static public int TYPE_CANCEL = 1;
	final static public int TYPE_HANDLE_ARROWS = 2;
	final static public int TYPE_HANDLE = 3;
	
	final static public int TYPE_LENGTH = 4;
	
	static private float size = 32f;
	static private float shift = 5f;
	static private float shift2 = size+shift;
	static private float transparency = 0.25f;

	/** shift (separation) value */
	static public float SHIFT = 10f;
	/** height of screen buttons value */
	static public float HEIGHT = size;
	/** width of screen buttons value */
	static public float WIDTH = size+shift2;
	
	static private float handleSize = 40f;
	static private float thickness = 2.5f;
	static private float handleSizeStart = 7f;

	private int[] index;
	
	private Manager manager;	

	/** common constructor
	 */
	public PlotterViewButtons(Manager manager) {
		
		this.manager = manager;
		
		index = new int[TYPE_LENGTH];
			
		//buttons
		for (int i=0; i<2; i++){
			index[i] = manager.startNewList();
			manager.startGeometry(Manager.QUADS);
			button(i);
			manager.endGeometry();
			manager.endList();
		}

		//handle arrows
		PlotterBrush brush = manager.getBrush();

		brush.setArrowType(PlotterBrush.ARROW_TYPE_SIMPLE);

		//sets the thickness for arrows
		brush.setThickness(1,1f);

		brush.setAffineTexture(0.5f, 0.125f);

		brush.start(16);
		brush.setColor(Color.GRAY,0.5f);
		brush.setThickness(thickness);//re sets the thickness
		brush.segment(new Coords(handleSizeStart, 0, 0, 1),new Coords(handleSize, 0, 0, 1));
		brush.setThickness(thickness);//re sets the thickness
		brush.segment(new Coords(-handleSizeStart, 0, 0, 1),new Coords(-handleSize, 0, 0, 1));
		index[TYPE_HANDLE_ARROWS] =brush.end();			
		
		//handle picked
		brush.setArrowType(PlotterBrush.ARROW_TYPE_CLOSED);
		brush.start(16);
		brush.setColor(Color.GRAY,0.5f);
		brush.setThickness(thickness*3);//re sets the thickness
		brush.segment(new Coords(0, 0, 0, 1),new Coords(handleSizeStart/2, 0, 0, 1));
		brush.setThickness(thickness*3);//re sets the thickness
		brush.segment(new Coords(0, 0, 0, 1),new Coords(-handleSizeStart/2, 0, 0, 1));
		index[TYPE_HANDLE] =brush.end();

		brush.setArrowType(PlotterBrush.ARROW_TYPE_NONE);
	}
	
	
	//////////////////////////////////
	// INDEX
	//////////////////////////////////	
	
	/** return geometry index for each type of button
	 * @param i
	 * @return geometry index for each type of button
	 */
	public int getIndex(int i){
		return index[i];
	}
	
	
	//////////////////////////////////
	// GEOMETRIES
	//////////////////////////////////
	
	private void button(int i){

		switch(i){
		case TYPE_OK:

			manager.color(1,1,1);

			manager.texture(1, 1);
			manager.vertex(size, size, 0);
			manager.texture(0, 1);
			manager.vertex(0, size, 0);
			manager.texture(0, 0);
			manager.vertex(0, 0, 0);
			manager.texture(1, 0);
			manager.vertex(size, 0, 0);
			
			break;
		case TYPE_CANCEL:

			manager.color(1,1,1);
			
			manager.texture(1, 1);
			manager.vertex(shift2+size, size, 0);
			manager.texture(0, 1);
			manager.vertex(shift2, size, 0);
			manager.texture(0, 0);
			manager.vertex(shift2, 0, 0);
			manager.texture(1, 0);
			manager.vertex(shift2+size, 0, 0);
			

			break;
		}
	}
	
}

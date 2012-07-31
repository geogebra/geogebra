package geogebra3D.euclidian3D.opengl;

import geogebra.common.kernel.Matrix.Coords;

import java.awt.Color;





/**
 * Class that describes the geometry of the 3D cursor
 * 
 * @author ggb3D
 *
 */
public class PlotterCursor {
	
	
	static public int TYPE_CROSS2D = 0;
	static public int TYPE_DIAMOND = 1;
	static public int TYPE_CYLINDER = 2;
	static public int TYPE_CROSS3D = 3;
	static public int TYPE_ALREADY_XY = 4;
	static public int TYPE_ALREADY_Z = 5;
	static public int TYPE_CUBE = 6;
	
	static private int TYPE_LENGTH = TYPE_CUBE +1;
	
	
	
	static private float size = 12f;
	static private float thickness = 1.25f;
	static private float thickness2 = 1.25f;
	static private float depth = 1f;

	static private float size_start_move = 7f;
	static private float size_move = 40f;
	static private float thickness3 = 2*thickness;
	
	static private float size_cube = size_start_move;
	
	private int[] index;
	
	private Manager manager;
	

	/** common constructor
	 * @param geometryRenderer
	 */
	public PlotterCursor(Manager manager) {
		
		this.manager = manager;
		
		index = new int[TYPE_LENGTH];
		
		
		//crosses
		for (int i=0; i<4; i++){
			index[i] = manager.startNewList();
			manager.startGeometry(Manager.QUADS);
			cursor(i);
			manager.endGeometry();
			manager.endList();
		}
		
		//moving cursors
		PlotterBrush brush = manager.getBrush();
		
		

		brush.setArrowType(PlotterBrush.ARROW_TYPE_SIMPLE);
		
		//sets the thickness for arrows
		brush.setThickness(1,1f);
		
		
		brush.setAffineTexture(0.5f, 0.125f);
		
		
		
		//xy
		brush.start(8);
		brush.setColor(Color.GRAY);
		brush.setThickness(thickness3);//re sets the thickness
		brush.segment(new Coords(size_start_move, 0, 0, 1),new Coords(size_move, 0, 0, 1));
		brush.setThickness(thickness3);//re sets the thickness
		brush.segment(new Coords(-size_start_move, 0, 0, 1),new Coords(-size_move, 0, 0, 1));
		brush.setThickness(thickness3);//re sets the thickness
		brush.segment(new Coords(0, size_start_move, 0, 1),new Coords(0, size_move, 0, 1));
		brush.setThickness(thickness3);//re sets the thickness
		brush.segment(new Coords(0, -size_start_move, 0, 1),new Coords(0, -size_move, 0, 1));
		index[TYPE_ALREADY_XY] =brush.end();


		//z
		brush.start(8);
		brush.setColor(Color.GRAY);
		brush.setThickness(thickness3);//re sets the thickness
		brush.segment(new Coords(0, 0, size_start_move, 1),new Coords(0, 0, size_move, 1));
		brush.setThickness(thickness3);//re sets the thickness
		brush.segment(new Coords(0, 0, -size_start_move, 1),new Coords(0, 0, -size_move, 1));
		index[TYPE_ALREADY_Z] =brush.end();

		brush.setArrowType(PlotterBrush.ARROW_TYPE_NONE);

		//cube
		index[TYPE_CUBE] = manager.startNewList();
		manager.startGeometry(Manager.QUADS);
		manager.color(0.5f,0.5f,0.5f);
		//up
		manager.normal(0,0,1);
		manager.vertex(size_cube, size_cube, size_cube);
		manager.vertex(-size_cube, size_cube, size_cube);
		manager.vertex(-size_cube, -size_cube, size_cube);
		manager.vertex(size_cube, -size_cube, size_cube);
		//down
		manager.normal(0,0,-1);
		manager.vertex(size_cube, size_cube, -size_cube);
		manager.vertex(size_cube, -size_cube, -size_cube);
		manager.vertex(-size_cube, -size_cube, -size_cube);
		manager.vertex(-size_cube, size_cube, -size_cube);
		//right
		manager.normal(1,0,0);
		manager.vertex(size_cube, size_cube, size_cube);
		manager.vertex(size_cube, -size_cube, size_cube);
		manager.vertex(size_cube, -size_cube, -size_cube);
		manager.vertex(size_cube, size_cube, -size_cube);
		//left
		manager.normal(-1,0,0);
		manager.vertex(-size_cube, size_cube, size_cube);
		manager.vertex(-size_cube, size_cube, -size_cube);
		manager.vertex(-size_cube, -size_cube, -size_cube);
		manager.vertex(-size_cube, -size_cube, size_cube);
		//back
		manager.normal(0,1,0);
		manager.vertex(size_cube, size_cube, size_cube);
		manager.vertex(size_cube, size_cube, -size_cube);
		manager.vertex(-size_cube, size_cube, -size_cube);
		manager.vertex(-size_cube, size_cube, size_cube);
		//front
		manager.normal(0,-1,0);
		manager.vertex(size_cube, -size_cube, size_cube);
		manager.vertex(-size_cube, -size_cube, size_cube);
		manager.vertex(-size_cube, -size_cube, -size_cube);
		manager.vertex(size_cube, -size_cube, -size_cube);

		
		manager.endGeometry();
		manager.endList();
	}

	
	
	/**
	 * used to say if light is on or not
	 * @param type
	 * @return true it type is of "already" (xy or z)
	 */
	static public boolean isTypeAlready(int type){
		return type==TYPE_ALREADY_XY || type==TYPE_ALREADY_Z || type==TYPE_CUBE;
	}


	//////////////////////////////////
	// INDEX
	//////////////////////////////////	
	
	/** return geometry index for each type of cursor
	 * @param i
	 * @return geometry index for each type of cursor
	 */
	public int getIndex(int i){
		return index[i];
	}
	
	//////////////////////////////////
	// GEOMETRIES
	//////////////////////////////////
	
	private void cursor(int i){

		switch(i){
		case 0:
			cursorCross2D();
			break;
		case 1:
			cursorDiamond();
			break;
		case 2:
			cursorCylinder();
			break;
		case 3:
			cursorCross3D();
			break;
		}
	}
	
	
	
	
	
	
	
	private void cursorCross2D(){
		


		
		//white parts
		manager.color(1,1,1);

		//up
		manager.vertex(thickness, size, depth);
		manager.vertex(-thickness, size, depth);
		manager.vertex(-thickness, -size, depth);
		manager.vertex(thickness, -size, depth);
				
		manager.vertex(size, thickness, depth);
		manager.vertex(thickness, thickness, depth);
		manager.vertex(thickness, -thickness, depth);
		manager.vertex(size, -thickness, depth);
		
		manager.vertex(-size, thickness, depth);
		manager.vertex(-size, -thickness, depth);
		manager.vertex(-thickness, -thickness, depth);
		manager.vertex(-thickness, thickness, depth);
		
		//down
		manager.vertex(thickness, size, -depth);
		manager.vertex(thickness, -size, -depth);
		manager.vertex(-thickness, -size, -depth);
		manager.vertex(-thickness, size, -depth);
				
		manager.vertex(size, thickness, -depth);
		manager.vertex(size, -thickness, -depth);
		manager.vertex(thickness, -thickness, -depth);
		manager.vertex(thickness, thickness, -depth);
		
		manager.vertex(-size, thickness, -depth);
		manager.vertex(-thickness, thickness, -depth);
		manager.vertex(-thickness, -thickness, -depth);
		manager.vertex(-size, -thickness, -depth);
		
		
		
		//black parts
		manager.color(0,0,0);
		

		//up and down
		quadSymxOyRotOz90SymOz(
				thickness, thickness, depth,
				thickness+thickness2, thickness+thickness2, depth,
				thickness+thickness2, size+thickness2, depth,
				thickness, size, depth
		);
		
		quadSymxOyRotOz90SymOz(
				thickness, -thickness, depth,
				thickness, -size, depth,
				thickness+thickness2, -size-thickness2, depth,
				thickness+thickness2, -thickness-thickness2, depth
		);
		
		quadSymxOyRotOz90SymOz(
				size, thickness,depth,
				size, -thickness,depth,
				size+thickness2, -thickness-thickness2,  depth,
				size+thickness2, thickness+thickness2,  depth
		);
		
		
		//edges
		quadSymxOyRotOz90SymOz(
				thickness+thickness2, thickness+thickness2, -depth,
				thickness+thickness2, size+thickness2, -depth,
				thickness+thickness2, size+thickness2, depth,
				thickness+thickness2, thickness+thickness2, depth
		);
		
		quadSymxOyRotOz90SymOz(
				thickness+thickness2, -thickness-thickness2, -depth,
				thickness+thickness2, -thickness-thickness2, depth,
				thickness+thickness2, -size-thickness2, depth,
				thickness+thickness2, -size-thickness2, -depth
		);
		
		quadRotOz90SymOz(
				size+thickness2, thickness+thickness2,  -depth,
				size+thickness2, thickness+thickness2,  depth,
				size+thickness2, -thickness-thickness2,  depth,
				size+thickness2, -thickness-thickness2,  -depth
		);	
		
		
		
	}
	
	
	
	private void cursorCross3D(){
		
		
		float t = (float) (thickness / Math.tan(Math.PI/8));
		
		float size2 = size+thickness2;
		
		//white parts
		manager.color(1,1,1);
		
		quadSymxOyRotOz90SymOz(
				thickness, t, t, 
				-thickness, t, t,
				-thickness, t, size2,
				thickness, t, size2
				);
		
		quadSymxOyRotOz90SymOz(
				thickness, t, t, 
				thickness, size2, t,
				-thickness, size2, t, 
				-thickness, t, t
				);
		
		quadRotOz90SymOz(
				t, t, thickness, 
				t, t, -thickness,
				t, size2, -thickness, 
				t, size2, thickness
				);
				
		quadRotOz90SymOz(
				-t, t, thickness, 
				-t, size2, thickness,
				-t, size2, -thickness, 
				-t, t, -thickness
				);
		
		
		quadRotOz90SymOz(
				thickness, size2+t-thickness, -thickness,
				-thickness, size2+t-thickness, -thickness,
				-thickness, size2+t-thickness, thickness,
				thickness, size2+t-thickness, thickness
				);	

		quadSymxOyRotOz90SymOz(
				thickness, -thickness, size2+t-thickness, 
				thickness, thickness, size2+t-thickness,
				-thickness, thickness, size2+t-thickness,
				-thickness, -thickness, size2+t-thickness
				);	
		
		
		//black parts
		manager.color(0,0,0);
		
		quadSymxOyRotOz90SymOz(
				t, t, t, 
				t, t, size2,
				t, thickness, size2,
				t, thickness, t
				);		
		
		quadSymxOyRotOz90SymOz(
				thickness, t, t, 
				thickness, t, size2,
				t, t, size2,
				t, t, t
		);

		
		quadSymxOyRotOz90SymOz(
				t, t, t, 
				t, t, thickness,
				t, size2, thickness, 
				t, size2, t
				);

		quadSymxOyRotOz90SymOz(
				thickness, t, t, 
				t, t, t,
				t, size2, t, 
				thickness, size2, t
				);
		

		
		quadSymxOyRotOz90SymOz(
				-t, t, t, 
				-t, size2, t,
				-t, size2, thickness, 
				-t, t, thickness
				);

		quadSymxOyRotOz90SymOz(
				-thickness, t, t, 
				-thickness, size2, t,
				-t, size2, t, 
				-t, t, t
				);

	
		
		quadSymxOyRotOz90SymOz(
				t, size2, t, 
				t, size2+t-thickness, t,
				-t, size2+t-thickness, t,
				-t, size2, t
				);	
		
		quadSymxOyRotOz90SymOz(
				t, size2+t-thickness, t, 
				t, size2+t-thickness, thickness,
				-t, size2+t-thickness, thickness,
				-t, size2+t-thickness, t
				);	
		

	
		
		quadRotOz90SymOz(
				t, size2, t, 
				t, size2, -t,
				t, size2+t-thickness, -t,
				t, size2+t-thickness, t
				);	

		quadRotOz90SymOz(
				t, size2+t-thickness, thickness, 
				t, size2+t-thickness, -thickness,
				thickness, size2+t-thickness, -thickness,
				thickness, size2+t-thickness, thickness
				);
		


		
		quadRotOz90SymOz(
				-t, size2, t, 
				-t, size2+t-thickness, t,
				-t, size2+t-thickness, -t,
				-t, size2, -t
				);	

		quadRotOz90SymOz(
				-t, size2+t-thickness, thickness, 
				-thickness, size2+t-thickness, thickness,
				-thickness, size2+t-thickness, -thickness,
				-t, size2+t-thickness, -thickness
				);	
		


		
		quadSymxOyRotOz90SymOz(
				t, t, size2,
				t, t, size2+t-thickness,
				t, -t, size2+t-thickness,
				t, -t, size2
				);	

		quadSymxOyRotOz90SymOz(
				t, t, size2+t-thickness,
				thickness, thickness, size2+t-thickness,
				thickness, -thickness, size2+t-thickness,
				t, -t, size2+t-thickness
				);	
		
	
	}
	
	

	
	
	private void cursorDiamond(){
		
    	float t1 = 0.15f;
    	float t2 = 1f-2*t1;
    	
    	//black parts
		manager.color(0,0,0);
    	
    	quadSymxOyRotOz90SymOz(1f, 0f, 0f,	        
    			t2, t1, t1,	        
    			t1, t1, t2,
    			0f, 0f, 1f);
    	
    	
    	quadSymxOyRotOz90SymOz(0f, 0f, 1f,
    			t1, t1, t2,
    			t1, t2, t1,	
    			0f, 1f, 0f);	

    	quadSymxOyRotOz90SymOz(0f, 1f, 0f,	
    			t1, t2, t1,	
    			t2, t1, t1,	        
    			1f, 0f, 0f);
    	
		//white parts
		manager.color(1,1,1);
		
		quadSymxOyRotOz90SymOz(
				t2, t1, t1,
				t2, t1, t1,	
				t1, t2, t1,	
				t1, t1, t2);

		
	}

	
	
	
	private void cursorCylinder(){
		
		int latitude = 8;
		float x1 = 4f;
		float r1 = PlotterBrush.LINE3D_THICKNESS/3f;
		float r2 = (float) (r1*Math.sqrt(2));
		float x2 = x1/3;
		
    	float da = (float) (Math.PI/latitude) ; 

    	float y1;
    	float z1;
    	float y0,z0;

    	
    	//white parts
		manager.color(1,1,1);
		  	
		//ring
    	y1 = 2 * r2 * (float) Math.sin ( da ); 
		z1 = 2 * r2 * (float) Math.cos ( da );
		
    	for( int i = 1; i <= latitude  ; i++ ) { 
    		y0 = y1; 
    		z0 = z1; 
    		y1 = 2 * r2 * (float) Math.sin ( (2*i+1) * da ); 
    		z1 = 2 * r2 * (float) Math.cos ( (2*i+1) * da ); 

    		manager.vertex(-x2,y0,z0); 
    		manager.vertex(x2,y0,z0); 
    		manager.vertex(x2,y1,z1); 
    		manager.vertex(-x2,y1,z1); 


    	} 
    	
    	//caps
    	y1 = 2 * r1 * (float) Math.sin ( da ); 
		z1 = 2 * r1 * (float) Math.cos ( da );
		
    	for( int i = 1; i < latitude/2  ; i++ ) { 
    		y0 = y1; 
    		z0 = z1; 
    		y1 = 2 * r1 * (float) Math.sin ( (2*i+1) * da ); 
    		z1 = 2 * r1 * (float) Math.cos ( (2*i+1) * da ); 

    		quadSymOz(
    				x1,y0,z0, 
    				x1,-y0,z0, 
    				x1,-y1,z1, 
    				x1,y1,z1); 

    	} 


    	//black parts
		manager.color(0,0,0);
		
		//ring
    	y1 = 2 * (float) Math.sin ( da ); 
		z1 = 2 * (float) Math.cos ( da );
		
    	for( int i = 1; i <= latitude  ; i++ ) { 
    		y0 = y1; 
    		z0 = z1; 
    		y1 = 2 * (float) Math.sin ( (2*i+1) * da ); 
    		z1 = 2 * (float) Math.cos ( (2*i+1) * da ); 

    		quadSymOz(x2,y0*r2,z0*r2,
    				x1,y0*r1,z0*r1, 
    				x1,y1*r1,z1*r1, 
    				x2,y1*r2,z1*r2); 


    	} 
    	
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void quadSymxOyRotOz90SymOz(float x1, float y1, float z1, 
			float x2, float y2, float z2,
			float x3, float y3, float z3,
			float x4, float y4, float z4){	
		
		quadRotOz90SymOz(
				x1, y1, z1, 
				x2, y2, z2, 
				x3, y3, z3, 
				x4, y4, z4
				);
		
		quadRotOz90SymOz(
				x1, y1, -z1, 
				x4, y4, -z4, 
				x3, y3, -z3, 
				x2, y2, -z2
				);
		
	}
	
	private void quadSymxOzSymxOyRotOz90SymOz(float x1, float y1, float z1, 
			float x2, float y2, float z2,
			float x3, float y3, float z3,
			float x4, float y4, float z4){	
		
		quadSymxOyRotOz90SymOz(
				x1, y1, z1, 
				x2, y2, z2, 
				x3, y3, z3, 
				x4, y4, z4
				);
		
		quadSymxOyRotOz90SymOz(
				x1, -y1, z1, 
				x4, -y4, z4, 
				x3, -y3, z3, 
				x2, -y2, z2
				);
		
	}
	private void quadRotOz90SymOz(float x1, float y1, float z1, 
			float x2, float y2, float z2,
			float x3, float y3, float z3,
			float x4, float y4, float z4){	
		
		quadSymOz(
				x1, y1, z1, 
				x2, y2, z2, 
				x3, y3, z3, 
				x4, y4, z4
				);
		
		quadSymOz(
				-y1, x1, z1, 
				-y2, x2, z2,
				-y3, x3, z3, 
				-y4, x4, z4
		);
		
		
		
	}
	
	private void quadSymOz(float x1, float y1, float z1, 
			float x2, float y2, float z2,
			float x3, float y3, float z3,
			float x4, float y4, float z4){
		
		manager.vertex(x1,y1,z1);
		manager.vertex(x2,y2,z2);
		manager.vertex(x3,y3,z3);
		manager.vertex(x4,y4,z4);
		
		manager.vertex(-x1,-y1,z1);
		manager.vertex(-x2,-y2,z2);
		manager.vertex(-x3,-y3,z3);
		manager.vertex(-x4,-y4,z4);
		
		/*
		manager.vertex(-x1,y1,z1);
		manager.vertex(-x4,y4,z4);
		manager.vertex(-x3,y3,z3);
		manager.vertex(-x2,y2,z2);
		*/
		
	}


	
}

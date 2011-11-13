package geogebra3D.euclidian3D.opengl;



import geogebra.main.Application;

import javax.media.opengl.GLCanvas; //JOGL1
//import javax.media.opengl.awt.GLCanvas; //JOGL2
//import javax.media.opengl.awt.GLJPanel; //JOGL2

/**
 * Simple class extending GL JPanel/Canvas
 * @author matthieu
 *
 */
public class Component3D extends GLCanvas{ //JOGL1
//public class Component3D extends GLJPanel{ //JOGL2

	
	public Component3D(){

		
		super(new Component3DCapabilities());
		Application.debug("create gl renderer");
		
    	
	}
	
}

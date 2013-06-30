package geogebra3D.euclidian3D.opengl;




import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
//import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.awt.GLJPanel;

/**
 * Simple class extending GL JPanel/Canvas
 * @author matthieu
 *
 */


//public class Component3D extends GLCanvas{
public class Component3D extends GLJPanel{ 

	private static final long serialVersionUID = 1L;
	  
	
	/**
	 * constructor
	 */
	public Component3D(){

		//super(caps); //GLCanvas
		super(new GLCapabilities( GLProfile.getDefault() )); //GLJPanel
		    	
	}

	

}

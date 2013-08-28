package geogebra3D.euclidian3D.opengl;




import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
//import javax.media.opengl.awt.GLJPanel;

/**
 * Simple class extending GL JPanel/Canvas
 * @author matthieu
 *
 */


public class Component3D extends GLCanvas{
//public class Component3D extends GLJPanel{ 

	private static final long serialVersionUID = 1L;
	  
	private static GLCapabilities caps;
	
	static {
		
		caps = new GLCapabilities(GLProfile.getDefault());
		//caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
		caps.setAlphaBits(8);

		
		//anti-aliasing
		caps.setSampleBuffers(true);
		caps.setNumSamples(4);    	
		//avoid flickering
		caps.setDoubleBuffered(true);
		      	
	}
	
	/**
	 * constructor
	 */
	public Component3D(){

		//super(new GLCapabilities( GLProfile.getDefault() )); //GLJPanel
		super(caps); //GLJPanel or GLCanvas	
	}

	

}

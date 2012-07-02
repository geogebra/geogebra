package geogebra3D.euclidian3D.opengl;




import javax.media.opengl.GLCanvas; 
import javax.media.opengl.GLCapabilities;

/**
 * Simple class extending GL JPanel/Canvas
 * @author matthieu
 *
 */
public class Component3D extends GLCanvas{ 
	private static final long serialVersionUID = 1L;
	
	private static GLCapabilities caps;

	static {	
		
		caps = new GLCapabilities();

		
		//anti-aliasing
		caps.setSampleBuffers(true);
		caps.setNumSamples(4);    	
		//avoid flickering
		caps.setDoubleBuffered(true);
		      	
	}
	
	public Component3D(){

		
		super(caps);
		//Application.debug("create gl renderer");
		
    	
	}
	

	
}

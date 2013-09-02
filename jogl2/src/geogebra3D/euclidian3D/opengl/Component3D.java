package geogebra3D.euclidian3D.opengl;




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
	  
	
	/**
	 * constructor
	 */
	public Component3D(){

		//super(new GLCapabilities( GLProfile.getDefault() )); //GLJPanel
		//super(RendererJogl.caps); //GLJPanel or GLCanvas	
		super();
	}

	

}

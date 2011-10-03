package geogebra3D.euclidian3D.opengl;


import javax.media.opengl.GLCapabilities;
//import javax.media.opengl.GLProfile; //JOGL2


import geogebra.main.Application;


/**
 * openGL capabilities for JPanel3D
 * @author matthieu
 *
 */
public class Component3DCapabilities extends GLCapabilities{

	public Component3DCapabilities() {
		//super(GLProfile.getDefault()); //JOGL2
		
		//anti-aliasing
    	setSampleBuffers(true);setNumSamples(4);    	
        //avoid flickering
    	setDoubleBuffered(true);	      	
        //stereo
    	Application.debug("stereo: "+getStereo()); 
	}
	
	final static public void initSingleton(){
		//GLProfile.initSingleton(true); //JOGL2
	}
	
	

}

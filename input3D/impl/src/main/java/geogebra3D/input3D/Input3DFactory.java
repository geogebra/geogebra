package geogebra3D.input3D;

import org.geogebra.common.euclidian3D.Input3D;

import geogebra3D.input3D.intelRealSense.InputIntelRealsense3D;
import geogebra3D.input3D.leonar3do.InputLeo3D;



/**
 * Factory to create the proper 3D input
 * @author mathieu
 *
 */
public class Input3DFactory {

	/**
	 * create a 3D input
	 * @return 3D input
	 */
	static public Input3D createInput3D(){
		//return null; //use this to switch off 3D input
		//return new InputLeo3D(); //use this for Leonar3do input
		return new InputIntelRealsense3D(); //use this for intel realsense input
	}
}

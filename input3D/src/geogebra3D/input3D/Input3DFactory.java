package geogebra3D.input3D;

import geogebra.common.euclidian3D.input3D.Input3D;
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
		return new InputLeo3D();
	}
}

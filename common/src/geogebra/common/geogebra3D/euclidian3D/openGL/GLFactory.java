package geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;


/**
 * factory for GL stuff
 * @author mathieu
 *
 */
public abstract class GLFactory {
	
	/**
	 * prototype to factor stuff
	 */
	public static GLFactory prototype = null;
	
	/**
	 * 
	 * @param array float array
	 * @return new float buffer
	 */
	abstract public GLBuffer newBuffer(ArrayList<Float> array);
}

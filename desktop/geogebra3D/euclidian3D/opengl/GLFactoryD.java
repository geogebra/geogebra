package geogebra3D.euclidian3D.opengl;

import geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import geogebra.common.geogebra3D.euclidian3D.openGL.GLFactory;

import java.util.ArrayList;

/**
 * GL factory for desktop
 * @author mathieu
 *
 */
public class GLFactoryD extends GLFactory{

	/**
	 * constructor
	 */
	public GLFactoryD(){
		
	}
	
	
	@Override
	final public GLBuffer newBuffer(ArrayList<Float> array){		
		return new GLBufferD(array);
	}
}

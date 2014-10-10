package geogebra3D.euclidian3D.opengl;

import geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import geogebra.common.geogebra3D.euclidian3D.openGL.GLFactory;

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
	final public GLBuffer newBuffer(){
		return new GLBufferD();
	}
}

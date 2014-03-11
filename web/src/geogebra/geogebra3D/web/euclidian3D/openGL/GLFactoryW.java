package geogebra.geogebra3D.web.euclidian3D.openGL;

import geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import geogebra.common.geogebra3D.euclidian3D.openGL.GLFactory;

import java.util.ArrayList;

/**
 * GL factory for web
 * @author mathieu
 *
 */
public class GLFactoryW extends GLFactory {
	
	/**
	 * constructor
	 */
	public GLFactoryW(){
		
	}
	
	@Override
	final public GLBuffer newBuffer(ArrayList<Float> array){		
		return new GLBufferW(array);
	}
}

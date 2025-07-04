package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import com.jogamp.opengl.glu.GLU;

public interface JoglAndGluProvider {

	/**
	 * @return JOGL
	 */
	RendererJogl getJogl();

	/**
	 * @return GLU
	 */
	GLU getGLU();
}

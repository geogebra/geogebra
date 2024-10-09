package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import com.jogamp.opengl.glu.GLU;

public interface JoglAndGluProvider {

	public RendererJogl getJogl();

	public GLU getGLU();
}

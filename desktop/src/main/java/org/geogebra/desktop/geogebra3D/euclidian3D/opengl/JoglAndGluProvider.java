package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import javax.media.opengl.glu.GLU;

public interface JoglAndGluProvider {

	public RendererJogl getJogl();

	public GLU getGLU();
}

package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.desktop.geogebra3D.euclidian3D.opengl.RendererJogl.GLlocal;

/**
 * openGL geometry manager for desktop
 * 
 * @author mathieu
 *
 */
public abstract class ManagerD extends Manager {

	/** geometries types */
	/*
	 * static final int TRIANGLE_STRIP = GLlocal.GL_TRIANGLE_STRIP; static final
	 * int TRIANGLE_FAN = GLlocal.GL_TRIANGLE_FAN; static final int QUAD_STRIP =
	 * GLlocal.GL_QUAD_STRIP; static final int QUADS = GLlocal.GL_QUADS; static
	 * final int TRIANGLES = GLlocal.GL_TRIANGLES; LINE_LOOP =
	 * GLlocal.GL_LINE_LOOP
	 */

	/**
	 * create a manager for geometries
	 * 
	 * @param renderer
	 *            openGL renderer
	 * @param view3D
	 *            3D view
	 */
	public ManagerD(Renderer renderer, EuclidianView3D view3D) {
		super(renderer, view3D);
	}

	/**
	 * 
	 * @param type
	 *            Manager type
	 * @return GL type
	 */
	protected static int getGLType(Type type) {
		switch (type) {
		case TRIANGLE_STRIP:
			return GLlocal.GL_TRIANGLE_STRIP;
		case TRIANGLE_FAN:
			return GLlocal.GL_TRIANGLE_FAN;
		case TRIANGLES:
			return GLlocal.GL_TRIANGLES;
		case LINE_LOOP:
			return GLlocal.GL_LINE_LOOP;
		case LINE_STRIP:
			return GLlocal.GL_LINE_STRIP;
		}

		return 0;
	}

}

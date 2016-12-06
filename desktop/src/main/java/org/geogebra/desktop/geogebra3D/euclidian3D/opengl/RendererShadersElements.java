package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import javax.media.opengl.GL;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShadersElementsGlobalBuffer;
import org.geogebra.desktop.geogebra3D.euclidian3D.opengl.RendererJogl.GLlocal;

/**
 * renderer using shaders and drawElements()
 * 
 * @author mathieu
 *
 */
public class RendererShadersElements extends RendererShaders {

	/**
	 * constructor
	 * 
	 * @param view
	 *            3D view
	 * @param useCanvas
	 *            say if we use GLCanvas or GLJPanel
	 */
	public RendererShadersElements(EuclidianView3D view, boolean useCanvas) {
		super(view, useCanvas);
	}

	@Override
	protected Manager createManager() {
		return new ManagerShadersElementsGlobalBuffer(this, view3D);
	}

	@Override
	public void draw(Manager.Type type, int length) {

		jogl.getGL2().glDrawElements(getGLType(type), length,
				GL.GL_UNSIGNED_SHORT, 0);
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
			return GLlocal.GL_TRIANGLE_STRIP;
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

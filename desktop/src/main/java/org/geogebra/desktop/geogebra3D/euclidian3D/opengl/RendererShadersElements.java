package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import javax.media.opengl.GL;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShadersBindBuffers;
import org.geogebra.common.main.App;

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
		App.debug("========== createManager");
		return new ManagerShadersBindBuffers(this, view3D);
	}

	@Override
	public void draw(Manager.Type type, int length) {

		jogl.getGL2().glDrawElements(ManagerD.getGLType(type), length,
				GL.GL_UNSIGNED_SHORT, 0);
	}


}

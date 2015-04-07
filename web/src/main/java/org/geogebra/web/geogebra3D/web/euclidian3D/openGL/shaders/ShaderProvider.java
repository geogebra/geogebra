package org.geogebra.web.geogebra3D.web.euclidian3D.openGL.shaders;

import com.google.gwt.resources.client.TextResource;
import com.googlecode.gwtgl.binding.WebGLRenderingContext;

/**
 * This class provides different shaders based on gpu/renderer information.
 */
public class ShaderProvider {

	/**
	 * @param glContext
	 *            used to get debug information about gpu
	 * @return Fragment shader
	 */
	public static String getFragmentShader(WebGLRenderingContext glContext) {
		TextResource resource = null;
		if (GpuBlacklist.isCurrentGpuBlacklisted(glContext)) {
			resource = Shaders.INSTANCE.fragmentShaderSmaller();
		} else {
			resource = Shaders.INSTANCE.fragmentShader();
		}
		return resource.getText();
	}

	/**
	 * @return Vertex shader
	 */
	public static String getVertexShader() {
		return Shaders.INSTANCE.vertexShader().getText();
	}
}

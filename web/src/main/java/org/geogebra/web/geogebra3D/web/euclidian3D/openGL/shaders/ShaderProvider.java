package org.geogebra.web.geogebra3D.web.euclidian3D.openGL.shaders;

import org.geogebra.common.geogebra3D.main.FragmentShader;
import org.geogebra.common.geogebra3D.main.VertexShader;

import com.google.gwt.resources.client.TextResource;

/**
 * This class provides different shaders based on gpu/renderer information.
 */
public class ShaderProvider {

	/**
	 * @param needsSmallFragmentShader
	 *            says if we need a small fragment shader
	 * @param shiny
	 *            says if we use specular light to get it shiny
	 * @return Fragment shader
	 */
	public static String getFragmentShader(boolean needsSmallFragmentShader,
			boolean shiny) {
		TextResource resource = null;
		if (!needsSmallFragmentShader && shiny) {
			return FragmentShader.getFragmentShaderShiny(0.2f, true);
			//resource = Shaders.INSTANCE.fragmentShaderShiny();
		} else {
			resource = Shaders.INSTANCE.fragmentShader();
		}
		return resource.getText();
	}

	/**
	 * @param needsSmallFragmentShader
	 *            says if we need a small fragment shader
	 * @param shiny
	 *            says if we use specular light to get it shiny
	 * @return Vertex shader
	 */
	public static String getVertexShader(boolean needsSmallFragmentShader,
			boolean shiny) {
		TextResource resource = null;
		if (!needsSmallFragmentShader && shiny) {
			return VertexShader.getVertexShaderShiny(true);
			//resource = Shaders.INSTANCE.vertexShaderShiny();
		} else {
			resource = Shaders.INSTANCE.vertexShader();
		}
		return resource.getText();
	}
}

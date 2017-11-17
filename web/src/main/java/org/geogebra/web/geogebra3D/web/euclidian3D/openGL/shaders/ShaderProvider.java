package org.geogebra.web.geogebra3D.web.euclidian3D.openGL.shaders;

import org.geogebra.common.geogebra3D.main.FragmentShader;
import org.geogebra.common.geogebra3D.main.VertexShader;

/**
 * This class provides different shaders based on gpu/renderer information.
 */
public class ShaderProvider {

	/**
	 * @param needsSmallFragmentShader
	 *            says if we need a small fragment shader
	 * @param shiny
	 *            says if we use specular light to get it shiny
	 * @param packed
	 *            says if we use packed buffers
	 * @return Fragment shader
	 */
	public static String getFragmentShader(boolean needsSmallFragmentShader,
			boolean shiny, boolean packed) {
		if (needsSmallFragmentShader) {
			return Shaders.INSTANCE.fragmentShaderSmaller().getText();
		}

		if (shiny) {
			if (packed) {
				return FragmentShader.getFragmentShaderShinyForPacking(0.2f, true);
			}
			return FragmentShader.getFragmentShaderShiny(0.2f, true);
		}

		return FragmentShader.getFragmentShader(true);
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
		if (!needsSmallFragmentShader && shiny) {
			return VertexShader.getVertexShaderShiny(true);
		}
		return VertexShader.getVertexShader(true);
	}
}

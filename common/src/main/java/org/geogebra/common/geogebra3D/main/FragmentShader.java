package org.geogebra.common.geogebra3D.main;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.RendererImplShaders;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Textures;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.TexturesShaders;

/**
 * Class providing fragment shader
 */
public class FragmentShader {

	final private static String fragmentHeaderDesktop = 
			"#if __VERSION__ >= 130\n"
			+ "  #define varying in\n"
			+ "  out vec4 mgl_FragColor;\n" 
			+ "  #define texture2D texture\n"
			+ "  #define gl_FragColor mgl_FragColor\n"
			+ "#endif\n"
			+ "#ifdef GL_ES\n"
			+ "  precision mediump float;\n"
			+ "  precision mediump int;\n"
			+ "#endif\n";

	final private static String fragmentHeaderHTML5 = "precision mediump float;\n";

	final private static String light =
			  "if (enableShine == 1){\n"
			+ "  float specular = dot(lightReflect, viewDirection);\n"
			+ "  if (specular > 0.0){\n"
			+ "    float specular2  = specular  * specular;\n"
			+ "    float specular4  = specular2 * specular2;\n"
			+ "    float specular16  = specular4 * specular4;\n"
			+ "    color.rgb = varying_Color.rgb + 0.2 * specular16 * vec3(1.0, 1.0, 1.0);\n"
			+ "    color.a = varying_Color.a;\n"
			+ "  }else{\n"
			+ "    color = varying_Color;\n"
			+ "  }\n"
			+ "}else{\n"
			+ "  color = varying_Color;\n"
			+ "}\n";

	/**
	 * @param isHTML5
	 *            whether to use html5 header
	 * @return shiny shader
	 */
	public static String getFragmentShaderShinyForPacking(boolean isHTML5) {
		return getFragmentShaderShinyForPacking(0.2f, isHTML5);
	}

	/**
	 * @param shine
	 *            shine level
	 * @param isHTML5
	 *            whether to use html5 header
	 * @return shiny shader
	 */
	public static String getFragmentShaderShinyForPacking(float shine, boolean isHTML5) {

		String header = isHTML5 ? fragmentHeaderHTML5 : fragmentHeaderDesktop;

		return header + "\nuniform int enableShine;\n"
				+ "uniform int textureType;\n"
				+ "uniform int enableClipPlanes;\n"
				+ "uniform vec3 clipPlanesMin;\n"
				+ "uniform vec3 clipPlanesMax;\n"
				+ "uniform float dashValues[4];\n"
				+ "uniform sampler2D Texture0;\n"
				// in (incoming varying data to the fragment shader sent from
				// the vertex shader)
				+ "varying vec4 varying_Color;  \n"
				+ "varying vec2 coordTexture;\n"
				+ "varying vec3 realWorldCoords;\n"
				+ "varying vec3 viewDirection;\n"
				+ "varying vec3 lightReflect;\n"

				+ "\nvoid main (void) \n"

				+ "{ \n"

				+ "\n// this occurs when buffer parts have been \"removed\"\n"
				+ "if (varying_Color.a < 0.0) {\n" 
				+ "  discard;\n" 
				+ "}\n"

				+ "\n// remove parts out of clipping box\n"
				+ "float x, y;\n"
				+ "if (enableClipPlanes == 1\n"
				+ "    && (realWorldCoords.x < clipPlanesMin.x "
				+ "|| realWorldCoords.x > clipPlanesMax.x\n"
				+ "     || realWorldCoords.y < clipPlanesMin.y "
				+ "|| realWorldCoords.y > clipPlanesMax.y \n"
				+ "     || realWorldCoords.z < clipPlanesMin.z "
				+ "|| realWorldCoords.z > clipPlanesMax.z \n"
				+ "   )){\n"
				+ "  discard;\n"
				+ "}\n"

				+ "\n// set color, with eventually shine effect\n"
				+ "vec4 color;\n" 
				+ (Renderer.TEST_DRAW_DEPTH_TO_COLOR
						? "color = varying_Color;\n"
						: light)

				+ "\n// fading texture (for planes etc.)\n"
				+ "if (textureType == "
				  + RendererImplShaders.TEXTURE_TYPE_FADING + "){\n"
				+ "  float factor;\n" + "  x = max(coordTexture.x, 0.0);\n"
				+ "  float y = max(coordTexture.y, 0.0);\n"
				+ "  gl_FragColor.rgb  = color.rgb;\n"
				+ "  gl_FragColor.a = "
				+ (Renderer.TEST_DRAW_DEPTH_TO_COLOR ? "1.0"
						: "color.a * (1.0 - x) * (1.0 - y)")
				+ ";\n"
				+ "  return;\n"
				+ "}\n"

				+ "\n// text texture\n"
				+ "if (textureType == "
				  + RendererImplShaders.TEXTURE_TYPE_TEXT + "){\n"
				+ "  vec4 textureVal = texture2D(Texture0, coordTexture);\n"
				+ "  if (textureVal.a < 0.25){\n"
				+ "    discard; // don't write\n"
				+ "  }\n"
				+ "  gl_FragColor.rgb = color.rgb;\n"
				+ "  gl_FragColor.a = "
				+ (Renderer.TEST_DRAW_DEPTH_TO_COLOR ? "1.0" : "textureVal.a")
				+ ";\n"
				+ "  return;\n"
				+ "}\n"

				+ "\n// packed dashed texture (for lines etc.)\n"
				+ "if (textureType == "
				  + (RendererImplShaders.TEXTURE_TYPE_DASH + Textures.DASH_PACKED)
				  + ") {\n"
				+ "  y = (coordTexture.y -  float(int((coordTexture.y + 0.5) / "
				  + Textures.DASH_ID_LENGTH + ".0) * " + Textures.DASH_ID_LENGTH
				  + ") + 0.5) / "
				  + TexturesShaders.DESCRIPTIONS_LENGTH + ".0;\n"
				+ "  vec4 textureDash = texture2D(Texture0, vec2(coordTexture.x, y));\n"
				+ "  if (textureDash.a < 0.5){\n"
				+ "    discard; // don't write\n" + "  }\n"
				+ "  gl_FragColor = color;\n"
				+ (Renderer.TEST_DRAW_DEPTH_TO_COLOR
						? "  gl_FragColor.a = 1.0;\n"
						: "")
				+ ";\n"
				+ "  return;\n"
				+ "}\n "
				
				+ "\n// packed hidden dashed texture (for lines etc.)\n"
				+ "if (textureType == "
				  + (RendererImplShaders.TEXTURE_TYPE_DASH
						+ Textures.DASH_PACKED_HIDDEN)
				  + ") {\n"
				+ "  y = (float(int((coordTexture.y+0.5) / "
				  + Textures.DASH_ID_LENGTH
				  + ".0)) + " + "0.5) / " + TexturesShaders.DESCRIPTIONS_LENGTH + ".0;\n"
				+ "  vec4 textureDash = texture2D(Texture0, vec2(coordTexture.x, y));\n"
				+ "  if (textureDash.a < 0.5){\n"
				+ "    discard; // don't write\n" + "  }\n"
				+ "  gl_FragColor = color;\n"
				+ (Renderer.TEST_DRAW_DEPTH_TO_COLOR
						? "  gl_FragColor.a = 1.0;\n"
						: "")
				+ ";\n"
				+ "  return;\n" 
				+ "}\n "

				+ "\n// default: no texture (e.g. for points)\n"
				+ "gl_FragColor = color;\n"
				+ (Renderer.TEST_DRAW_DEPTH_TO_COLOR
						? "  gl_FragColor.a = 1.0;\n"
						: "")
				+ ";\n"
				
				+ "} ";

	}
}

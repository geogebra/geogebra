package org.geogebra.common.geogebra3D.main;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;

/**
 * Class providing vertex shader
 *
 */
public class VertexShader {

	final private static String vertexHeaderDesktop = "#if __VERSION__ >= 130 "
			+ "// GLSL 130+ uses in and out\n"
			+ "  #define attribute in // instead of attribute and varying \n"
			+ "  #define varying out  // used by OpenGL 3 core and later. \n"
			+ "#endif \n"

			+ "#ifdef GL_ES \n"
			+ "precision mediump float;  // Precision Qualifiers\n"
			+ "precision mediump int;    // GLSL ES section 4.5.2\n"
			+ "#endif \n";

	final private static String inUniform =
			// in -- uniform
			"uniform mat4 matrix;\n" + "uniform vec3	lightPosition;\n"
					+ "uniform vec4	eyePosition;\n"
					+ "uniform vec2	ambiantDiffuse;\n"
					+ "uniform int	enableLight;\n" + "uniform int	culling;\n"
					+ "uniform vec4	color;\n" + "uniform vec3	normal;\n"
					+ "uniform vec4	center;\n"
					+ "uniform int	labelRendering;\n"
					+ "uniform vec3 labelOrigin;\n" + "uniform int	layer;\n"
					+ "uniform int	opaqueSurfaces;\n";

	final private static String shiny_packed =
			// in -- uniform
			inUniform

					// in -- attributes
					+ "attribute vec3  attribute_Position;  \n"
					+ "attribute vec3  attribute_Normal;  \n"
					+ "attribute vec4  attribute_Color;  \n"
					+ "attribute vec2	attribute_Texture;   \n"
					// out

					+ "varying vec4    varying_Color;  \n"
					+ "varying vec2	coordTexture;\n"
					+ "varying vec3    realWorldCoords;\n"
					+ "varying vec3 viewDirection;\n"

					+ "varying vec3 lightReflect;\n"

					// constants

					+ "const vec4 FAR_FAR_AWAY = vec4(0.0, 0.0, 2.0, 1.0); // z max is 1\n"

					+ "void main(void)\n"

					+ "{\n"

					// color

					+ "  vec4 c;\n" + "  int att_layer = 0;\n"
					+ "  if (color[0] < 0.0){ // then use per-vertex-color\n"
					+ "  	c = attribute_Color;\n"
					+ "     att_layer = int(c.a / "
					+ Renderer.LAYER_FACTOR_FOR_CODING + ".0);\n"
					+ "     c.a = c.a - " + Renderer.LAYER_FACTOR_FOR_CODING
					+ ".0 * float(att_layer);\n"
					+ "     att_layer = att_layer"
					+ Renderer.LAYER_MIN_STRING_WITH_OP
					+ ";\n"
					+ "  }else{ // use per-object-color\n"
					+ "  	c = color;\n" + "  }\n"

					// discard when alpha < 0 (actually will be discarded in
					// fragment shader)
					+ "  if (opaqueSurfaces == 1 && c.a < 0.99) {"
					+ "    c.a = -1.0;" + "  }\n"
					+ "  if (c.a < 0.0) {\n" + "	  varying_Color = c;\n"
					+ "  gl_Position = FAR_FAR_AWAY; // allows early Z test\n"
					+ "   return;\n" + "  }\n"

					// position

					+ "  vec3 position;\n"
					+ "  if (center.w > 0.0){ // use center\n"
					+ "  	position = vec3(center) + center.w * attribute_Position;\n"
					+ "  }else{\n"

					+ "  	position = attribute_Position;\n"

					+ "  }\n"
					+ "  gl_Position = matrix * vec4(position, 1.0); \n"

					+ "  // set layer a z-shift \n"
					+ "  float fLayer = float(layer + att_layer);\n"

					+ "	  vec3 n;\n"
					+ "	  if (normal.x > 1.5){ // then use per-vertex normal\n"
					+ "	  	n = attribute_Normal;\n"
					+ "	  }else{\n"
					+ "	  	n = normal;\n"
					+ "	  }\n"

					+ " float normalScreenZ = 0.0;"
                    + "	if (n.x > -1.5){ // otherwise there is no normal\n"
                    + "   vec4 normalScreen = matrix * vec4(n, 0.0);\n"
					+ "   normalScreen.w = 0.0;\n"
					+ "   normalScreen = normalize(normalScreen);\n"
                    + "   normalScreenZ = normalScreen.z;"
                    + " }"

					// shift z position to avoid z-fighting
					// use layer value
					// decrease when normal get orthogonal to screen;
					// constant values set by checking different screen
					// resolutions and different mobile devices
					+ " gl_Position.z = gl_Position.z"
					+ " - 0.0008 * (1.0 - 0.5 * abs(normalScreenZ)) * fLayer * gl_Position.w; \n"

					+ "  if (labelRendering == 1){ // use special origin for labels\n"
					+ "      realWorldCoords = labelOrigin;\n"

					+ "  }else{\n"

					+ "	  realWorldCoords = position;\n"

					+ "  }\n"

					// light

					+ "  if (enableLight == 1){// color with light\n"
					+ "	  float factor = dot(n, lightPosition);\n"
					+ "	  factor = float(culling) * factor;\n"
					+ "	  factor = max(0.0, factor);\n"
					+ "	  float ambiant = ambiantDiffuse[0];\n"
					+ "	  float diffuse = ambiantDiffuse[1];\n"

					// specular
					// makes natural specular
					+ "	  if (eyePosition[3] < 0.5){ // parallel projection\n"
					+ "	  	viewDirection = vec3(eyePosition);\n"
					+ "	  }else{ // perspective projection\n"
					+ "	  	viewDirection = normalize(position - vec3(eyePosition));\n"
					+ "	  }\n"

					+ "	  lightReflect = normalize(reflect(lightPosition, n));\n"
					// specular will be added in fragment shader
					+ "	  varying_Color.rgb = (ambiant + diffuse * factor) * c.rgb;\n"
					+ "	  varying_Color.a = c.a;\n" + "  }else{ //no light\n"
					+ "      lightReflect = vec3(0.0,0.0,0.0);\n"
					+ "	  varying_Color = c;\n"

					+ "  }\n"

					// texture

					+ "  coordTexture = attribute_Texture;\n"

					+ "}";

	/**
	 * @param isHTML5
	 *            whether to skip the desktop prefix
	 * @return shiny shader
	 */
	final public static String getVertexShaderShiny(boolean isHTML5) {
		if (isHTML5) {
				return shiny_packed;
		}
		return vertexHeaderDesktop + shiny_packed;
	}

}

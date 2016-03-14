package org.geogebra.common.geogebra3D.main;

public class VertexShader {

	final private static String vertexHeaderDesktop = "#if __VERSION__ >= 130 // GLSL 130+ uses in and out\n"
			+ "  #define attribute in // instead of attribute and varying \n"
			+ "  #define varying out  // used by OpenGL 3 core and later. \n"
			+ "#endif \n" + "\n" + "#ifdef GL_ES \n"
			+ "precision mediump float;  // Precision Qualifiers\n"
			+ "precision mediump int;    // GLSL ES section 4.5.2\n"
			+ "#endif \n";

	final private static String shiny = "//in -- uniform\n"
			+ "uniform mat4    matrix;  \n"
			+ "//uniform mat3	normalMatrix; // no need since light position is model view system based\n"
			+ "uniform vec3	lightPosition;\n" + "uniform vec4	eyePosition;\n"
			+ "uniform vec2	ambiantDiffuse;\n"
			+ "uniform int		enableLight;\n" + "uniform int		culling;\n"
			+ "uniform vec4	color;\n" + "uniform vec3	normal;\n"
			+ "uniform vec4	center;\n" + "\n" + "uniform int labelRendering;\n"
			+ "uniform vec3 labelOrigin;\n" + "\n" + "//in -- attributes\n"
			+ "attribute vec3  attribute_Position;  \n"
			+ "attribute vec3  attribute_Normal;  \n"
			+ "attribute vec4  attribute_Color;  \n"
			+ "attribute vec2	attribute_Texture;   \n" + "\n" + "\n"
			+ "//out\n" + "varying vec4    varying_Color;  \n"
			+ "varying vec2	coordTexture;\n"
			+ "varying vec3    realWorldCoords;\n" + "\n"
			+ "varying vec3 viewDirection;\n" + "varying vec3 lightReflect;\n"
			+ "\n" + "\n" + "void main(void)\n" + "{\n" + "   \n" + "  \n"
			+ "  // position\n" + "  vec3 position;\n"
			+ "  if (center.w > 0.0){ // use center\n"
			+ "  	position = vec3(center) + center.w * attribute_Position;\n"
			+ "  }else{\n" + "  	position = attribute_Position;\n" + "  }\n"
			+ "  gl_Position = matrix * vec4(position, 1.0); \n" + "  \n"
			+ "  if (labelRendering == 1){ // use special origin for labels\n"
			+ "      realWorldCoords = labelOrigin;\n" + "  }else{\n"
			+ "	  realWorldCoords = position;\n" + "  }\n" + "  \n" + "  \n"
			+ "  // color\n" + "  vec4 c;\n"
			+ "  if (color[0] < 0.0){ // then use per-vertex-color\n"
			+ "  	c = attribute_Color;\n"
			+ "  }else{ // use per-object-color\n" + "  	c = color;\n"
			+ "  }\n" + "  \n" + "  // light\n"
			+ "  if (enableLight == 1){// color with light\n" + "	  vec3 n;\n"
			+ "	  if (normal.x > 1.5){ // then use per-vertex normal\n"
			+ "	  	n = attribute_Normal;\n" + "	  }else{\n"
			+ "	  	n = normal;\n" + "	  }\n" + "	  \n"
			+ "	  float factor = dot(n, lightPosition);\n" + "	  \n"
			+ "	  factor = float(culling) * factor;\n" + "\n"
			+ "	  factor = max(0.0, factor);\n" + "	  \n"
			+ "	  float ambiant = ambiantDiffuse[0];\n"
			+ "	  float diffuse = ambiantDiffuse[1];\n" + "	  \n"
			+ "	  // specular\n" + "	  	  \n"
			+ "	  // makes natural specular\n"
			+ "	  if (eyePosition[3] < 0.5){ // parallel projection\n"
			+ "	  	viewDirection = vec3(eyePosition);\n"
			+ "	  }else{ // perspective projection\n"
			+ "	  	viewDirection = position - vec3(eyePosition);\n"
			+ "	  }\n" + "	  lightReflect = reflect(lightPosition, n);\n"
			+ "	  \n" + "	  //specular will be added in fragment shader\n"
			+ "	  varying_Color.rgb = (ambiant + diffuse * factor) * c.rgb;\n"
			+ "	  varying_Color.a = c.a;\n" + "	  \n" + "	  \n"
			+ "  }else{ //no light\n"
			+ "      lightReflect = vec3(0.0,0.0,0.0);\n"
			+ "	  varying_Color = c;\n" + "  }\n" + "\n" + "  \n" + "      \n"
			+ "  // texture\n" + "  coordTexture = attribute_Texture;\n"
			+ "  \n" + "}";

	private static String regular = "//in -- uniform\n"
			+ "uniform mat4    matrix;  \n"
			+ "//uniform mat3	normalMatrix; // no need since light position is model view system based\n"
			+ "uniform vec3	lightPosition;\n" + "uniform vec4	eyePosition;\n"
			+ "uniform vec2	ambiantDiffuse;\n"
			+ "uniform int		enableLight;\n" + "uniform int		culling;\n"
			+ "uniform vec4	color;\n" + "uniform vec3	normal;\n"
			+ "uniform vec4	center;\n" + "\n" + "uniform int labelRendering;\n"
			+ "uniform vec3 labelOrigin;\n" + "\n" + "//in -- attributes\n"
			+ "attribute vec3  attribute_Position;  \n"
			+ "attribute vec3  attribute_Normal;  \n"
			+ "attribute vec4  attribute_Color;  \n"
			+ "attribute vec2	attribute_Texture;   \n" + "\n" + "\n"
			+ "//out\n" + "varying vec4    varying_Color;  \n"
			+ "varying vec2	coordTexture;\n"
			+ "varying vec3    realWorldCoords;\n" + "\n" + "void main(void)\n"
			+ "{\n" + "   \n" + "  \n" + "  // position\n"
			+ "  vec3 position;\n" + "  if (center.w > 0.0){ // use center\n"
			+ "  	position = vec3(center) + center.w * attribute_Position;\n"
			+ "  }else{\n" + "  	position = attribute_Position;\n" + "  }\n"
			+ "  gl_Position = matrix * vec4(position, 1.0); \n" + "  \n"
			+ "  if (labelRendering == 1){ // use special origin for labels\n"
			+ "      realWorldCoords = labelOrigin;\n" + "  }else{\n"
			+ "	  realWorldCoords = position;\n" + "  }\n" + "  \n" + "  \n"
			+ "  // color\n" + "  vec4 c;\n"
			+ "  if (color[0] < 0.0){ // then use per-vertex-color\n"
			+ "  	c = attribute_Color;\n"
			+ "  }else{ // use per-object-color\n" + "  	c = color;\n"
			+ "  }\n" + "  \n" + "  // light\n"
			+ "  if (enableLight == 1){// color with light\n" + "	  vec3 n;\n"
			+ "	  if (normal.x > 1.5){ // then use per-vertex normal\n"
			+ "	  	n = attribute_Normal;\n" + "	  }else{\n"
			+ "	  	n = normal;\n" + "	  }\n" + "	  \n"
			+ "	  float factor = dot(n, lightPosition);\n" + "	  \n"
			+ "	  factor = float(culling) * factor;\n" + "\n"
			+ "	  factor = max(0.0, factor);\n" + "	  \n"
			+ "	  float ambiant = ambiantDiffuse[0];\n"
			+ "	  float diffuse = ambiantDiffuse[1];\n" + "	  \n" + "	  \n"
			+ "	  // no specular\n"
			+ "	  varying_Color.rgb = (ambiant + diffuse * factor) * c.rgb;\n"
			+ "	  varying_Color.a = c.a;\n" + "	  \n" + "  }else{ //no light\n"
			+ "	  varying_Color = c;\n" + "  }\n" + "\n" + "  \n" + "      \n"
			+ "  // texture\n" + "  coordTexture = attribute_Texture;\n"
			+ "  \n" + "}";

	final public static String getVertexShaderShiny(boolean isHTML5) {

		if (isHTML5) {
			return vertexHeaderDesktop + shiny;
		}

		return shiny;

	}

	final public static String getVertexShader(boolean isHTML5) {

		if (isHTML5) {
			return vertexHeaderDesktop + regular;
		}

		return regular;

	}

}

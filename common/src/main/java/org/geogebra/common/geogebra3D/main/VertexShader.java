package org.geogebra.common.geogebra3D.main;

public class VertexShader {

	final private static String vertexHeaderDesktop = "#if __VERSION__ >= 130 // GLSL 130+ uses in and out\n"
			+ "  #define attribute in // instead of attribute and varying \n"
			+ "  #define varying out  // used by OpenGL 3 core and later. \n"
			+ "#endif \n"

			+ "#ifdef GL_ES \n"
			+ "precision mediump float;  // Precision Qualifiers\n"
			+ "precision mediump int;    // GLSL ES section 4.5.2\n"
			+ "#endif \n";

	final private static String shiny = ""
			// in -- uniform
			+ "uniform mat4    matrix;\n"
			// + "//uniform mat3 normalMatrix; // no need since light position
			// is model view system based\n"
			+ "uniform vec3	lightPosition;\n"

			+ "uniform vec4	eyePosition;\n"
			+ "uniform vec2	ambiantDiffuse;\n"
			+ "uniform int		enableLight;\n"

			+ "uniform int		culling;\n"

			+ "uniform vec4	color;\n"

			+ "uniform vec3	normal;\n"

			+ "uniform vec4	center;\n"

			+ "uniform int labelRendering;\n"

			+ "uniform vec3 labelOrigin;\n"

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

			+ "void main(void)\n"

			+ "{\n"

	// position

			+ "  vec3 position;\n"
			+ "  if (center.w > 0.0){ // use center\n"
			+ "  	position = vec3(center) + center.w * attribute_Position;\n"
			+ "  }else{\n"

			+ "  	position = attribute_Position;\n"

			+ "  }\n"
			+ "  gl_Position = matrix * vec4(position, 1.0); \n"
			+ "  if (labelRendering == 1){ // use special origin for labels\n"
			+ "      realWorldCoords = labelOrigin;\n"

			+ "  }else{\n"

			+ "	  realWorldCoords = position;\n"

			+ "  }\n"

	// color

			+ "  vec4 c;\n"
			+ "  if (color[0] < 0.0){ // then use per-vertex-color\n"
			+ "  	c = attribute_Color;\n"
			+ "  }else{ // use per-object-color\n"

			+ "  	c = color;\n"

			+ "  }\n"

	// light

			+ "  if (enableLight == 1){// color with light\n"

			+ "	  vec3 n;\n"
			+ "	  if (normal.x > 1.5){ // then use per-vertex normal\n"
			+ "	  	n = attribute_Normal;\n"

			+ "	  }else{\n"

			+ "	  	n = normal;\n"

			+ "	  }\n"
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
			+ "	  	viewDirection = position - vec3(eyePosition);\n"
			+ "	  }\n"

			+ "	  lightReflect = reflect(lightPosition, n);\n"
			// specular will be added in fragment shader
			+ "	  varying_Color.rgb = (ambiant + diffuse * factor) * c.rgb;\n"
			+ "	  varying_Color.a = c.a;\n" 
			+ "  }else{ //no light\n"
			+ "      lightReflect = vec3(0.0,0.0,0.0);\n"
			+ "	  varying_Color = c;\n"

			+ "  }\n"

	// texture

			+ "  coordTexture = attribute_Texture;\n"

			+ "}";

	private static String regular = "//in -- uniform\n"
			+ "uniform mat4    matrix;  \n"
			// + "//uniform mat3 normalMatrix; // no need since light position
			// is model view system based\n"
			+ "uniform vec3	lightPosition;\n"

			+ "uniform vec4	eyePosition;\n"
			+ "uniform vec2	ambiantDiffuse;\n"
			+ "uniform int		enableLight;\n"

			+ "uniform int		culling;\n"

			+ "uniform vec4	color;\n"

			+ "uniform vec3	normal;\n"

			+ "uniform vec4	center;\n"

			+ "uniform int labelRendering;\n"

			+ "uniform vec3 labelOrigin;\n"

	// in -- attributes

			+ "attribute vec3  attribute_Position;\n"

			+ "attribute vec3  attribute_Normal;\n"
			+ "attribute vec4  attribute_Color;\n"
			+ "attribute vec2	attribute_Texture;\n"

	// out

			+ "varying vec4    varying_Color;  \n"
			+ "varying vec2	coordTexture;\n"
			+ "varying vec3    realWorldCoords;\n"

			+ "void main(void)\n"

			+ "{\n"
			// position
			+ "  vec3 position;\n"

			+ "  if (center.w > 0.0){ // use center\n"
			+ "  	position = vec3(center) + center.w * attribute_Position;\n"
			+ "  }else{\n"

			+ "  	position = attribute_Position;\n"

			+ "  }\n"
			+ "  gl_Position = matrix * vec4(position, 1.0); \n"
			+ "  if (labelRendering == 1){ // use special origin for labels\n"
			+ "      realWorldCoords = labelOrigin;\n"

			+ "  }else{\n"

			+ "	  realWorldCoords = position;\n"

			+ "  }\n"

	// color

			+ "  vec4 c;\n"
			+ "  if (color[0] < 0.0){ // then use per-vertex-color\n"
			+ "  	c = attribute_Color;\n"
			+ "  }else{ // use per-object-color\n"

			+ "  	c = color;\n"

			+ "  }\n"

	// light

			+ "  if (enableLight == 1){// color with light\n"

			+ "	  vec3 n;\n"
			+ "	  if (normal.x > 1.5){ // then use per-vertex normal\n"
			+ "	  	n = attribute_Normal;\n"

			+ "	  }else{\n"

			+ "	  	n = normal;\n"

			+ "	  }\n"

			+ "	  float factor = dot(n, lightPosition);\n"
			+ "	  factor = float(culling) * factor;\n"
			+ "	  factor = max(0.0, factor);\n"
			+ "	  float ambiant = ambiantDiffuse[0];\n"
			+ "	  float diffuse = ambiantDiffuse[1];\n" 
			// no specular
			+ "	  varying_Color.rgb = (ambiant + diffuse * factor) * c.rgb;\n"
			+ "	  varying_Color.a = c.a;\n"

			+ "  }else{ //no light\n"

			+ "	  varying_Color = c;\n"

			+ "  }\n"

	// texture

			+ "  coordTexture = attribute_Texture;\n"

 + "}";


	final public static String getVertexShaderShiny(boolean isHTML5) {

		if (isHTML5) {
			return shiny;
		}

		return vertexHeaderDesktop + shiny;

	}

	final public static String getVertexShader(boolean isHTML5) {

		if (isHTML5) {
			return regular;
		}

		return vertexHeaderDesktop + regular;

	}

}

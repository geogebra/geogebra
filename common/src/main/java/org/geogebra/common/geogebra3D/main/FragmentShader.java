package org.geogebra.common.geogebra3D.main;

public class FragmentShader {

	
	final private static String fragmentHeaderDesktop = "#if __VERSION__ >= 130\n"
			+ "  #define varying in\n" + "  out vec4 mgl_FragColor;\n"
			+ "  #define texture2D texture\n"
			+ "  #define gl_FragColor mgl_FragColor\n" + "#endif \n" + "\n"
			+ "#ifdef GL_ES \n" + "precision mediump float; \n"
			+ "precision mediump int; \n" + "#endif ";

	final private static String fragmentHeaderHTML5 = "precision mediump float;\n";

	final private static String regular = "// uniform\r\n"
			+ "uniform int textureType;\r\n"
			+ "\r\n" + "uniform int enableClipPlanes;\r\n"
			+ "uniform vec3 clipPlanesMin;\r\n"
			+ "uniform vec3 clipPlanesMax;\r\n" + "\r\n"
			+ "uniform float dashValues[4];\r\n" + "\r\n"
			+ "uniform sampler2D Texture0;\r\n" + "\r\n"
			+ "// in (incomming varying data to the frament shader sent from the vertex shader)\r\n"
			+ "varying   vec4    varying_Color;  \r\n"
			+ "varying   vec2	  coordTexture;  \r\n"
			+ "varying   vec3    realWorldCoords;\r\n" + "\r\n" + "\r\n"
			+ "void main (void) \r\n" + "{ \r\n" + "\r\n" + "	float x;\r\n"
			+ "\r\n" + "	if (enableClipPlanes == 1  // clip the scene\r\n"
			+ "		&& (   realWorldCoords.x < clipPlanesMin.x || realWorldCoords.x > clipPlanesMax.x\r\n"
			+ "			|| realWorldCoords.y < clipPlanesMin.y || realWorldCoords.y > clipPlanesMax.y \r\n"
			+ "			|| realWorldCoords.z < clipPlanesMin.z || realWorldCoords.z > clipPlanesMax.z 			\r\n"
			+ "		   )){\r\n" + "		discard;\r\n" + "		\r\n"
			+ "	}	\r\n" + "		\r\n" + "	// default\r\n"
			+ "	if (textureType == 0){\r\n"
			+ "		gl_FragColor = varying_Color;\r\n" + "		return;\r\n"
			+ "	}\r\n" + "	\r\n" + "			\r\n" + "	// fading	\r\n"
			+ "	if (textureType == 1){ // TEXTURE_TYPE_FADING = 1\r\n"
			+ "		float factor;\r\n"
			+ "		x = max(coordTexture.x, 0.0);\r\n"
			+ "		float y = max(coordTexture.y, 0.0);\r\n"
			+ "		gl_FragColor.rgb  = varying_Color.rgb;\r\n"
			+ "		gl_FragColor.a = varying_Color.a * (1.0 - x) * (1.0 - y);\r\n"
			+ "		return;\r\n" + "	}\r\n" + "		\r\n" + "		\r\n"
			+ "	// text	\r\n"
			+ "	if (textureType == 2){ // TEXTURE_TYPE_TEXT = 2;\r\n"
			+ "		vec4 textureVal = texture2D(Texture0, coordTexture);\r\n"
			+ "		if (textureVal.a < 0.25){\r\n"
			+ "	  		discard; // don't write\r\n" + "  		}\r\n"
			+ "  	\r\n" + "		gl_FragColor.rgb = varying_Color.rgb;\r\n"
			+ "		gl_FragColor.a = textureVal.a;\r\n" + "		\r\n"
			+ "		return;\r\n" + "	}\r\n" + "		\r\n" + "		\r\n"
			+ "		\r\n" + "	// dash\r\n" + "				\r\n"
			+ "		x =  mod(dashValues[0] * coordTexture.x, 1.0);\r\n"
			+ "		if (x > dashValues[1] || (x > dashValues[2] && x <= dashValues[3])){\r\n"
			+ "			discard;\r\n" + "		}\r\n" + "		\r\n"
			+ "		gl_FragColor = varying_Color;\r\n" + "\r\n" + "	\r\n"
			+ "	\r\n" + "		\r\n" + "	\r\n" + "				\r\n"
			+ "\r\n" + "} \r\n" + "	\r\n" + "	";

	final public static String getFragmentShader(boolean isHTML5) {

		if (!isHTML5) {
			return regular;
		}

		return fragmentHeaderHTML5 + regular;

	}

	final public static String getFragmentShaderShiny(float shine, boolean isHTML5) {

		String header = isHTML5 ? fragmentHeaderHTML5
				: fragmentHeaderDesktop;

		return header + "// uniform\r\n" + "uniform int	enableShine; \r\n"
				+ "uniform int textureType;\r\n" + "\r\n" + "\r\n"
				+ "uniform int enableClipPlanes;\r\n"
				+ "uniform vec3 clipPlanesMin;\r\n"
				+ "uniform vec3 clipPlanesMax;\r\n" + "\r\n"
				+ "uniform float dashValues[4];\r\n" + "\r\n"
				+ "uniform sampler2D Texture0;\r\n" + "\r\n"
				+ "// in (incomming varying data to the frament shader sent from the vertex shader)\r\n"
				+ "varying   vec4    varying_Color;  \r\n"
				+ "varying   vec2	  coordTexture;  \r\n"
				+ "varying   vec3    realWorldCoords;\r\n" + "\r\n"
				+ "varying vec3 viewDirection;\r\n"
				+ "varying vec3 lightReflect;\r\n" + "\r\n" + "\r\n"
				+ "void main (void) \r\n" + "{ \r\n" + "\r\n"
				+ "	float x;\r\n" + "\r\n"
				+ "	if (enableClipPlanes == 1  // clip the scene\r\n"
				+ "		&& (   realWorldCoords.x < clipPlanesMin.x || realWorldCoords.x > clipPlanesMax.x\r\n"
				+ "			|| realWorldCoords.y < clipPlanesMin.y || realWorldCoords.y > clipPlanesMax.y \r\n"
				+ "			|| realWorldCoords.z < clipPlanesMin.z || realWorldCoords.z > clipPlanesMax.z 			\r\n"
				+ "		   )){\r\n" + "		discard;\r\n" + "		\r\n"
				+ "	}\r\n" + "	\r\n" + "	vec4 color;\r\n" + "	\r\n"
				+ "	if (enableShine == 1){\r\n" + "		// adding specular\r\n"
				+ "		float specular = dot(normalize(lightReflect), normalize(viewDirection));	\r\n"
				+ "		if (specular > 0.0){\r\n"
				+ "		  	float specular2  = specular  * specular;\r\n"
				+ "		  	float specular4  = specular2 * specular2;\r\n"
				+ "		  	float specular16  = specular4 * specular4;\r\n"
				+ "		  	color.rgb = varying_Color.rgb + 0.2 * specular16 * vec3(1.0, 1.0, 1.0);\r\n"
				+ "		  	color.a = varying_Color.a;\r\n" + "		}else{\r\n"
				+ "		    color = varying_Color;\r\n" + "		}\r\n"
				+ "	}else{\r\n" + "		color = varying_Color;\r\n" + "	}\r\n"
				+ "	\r\n" + "		\r\n" + "	// default\r\n"
				+ "	if (textureType == 0){\r\n"
				+ "		gl_FragColor = color;\r\n" + "		return;\r\n"
				+ "	}\r\n" + "	\r\n" + "			\r\n"
				+ "	// fading	\r\n"
				+ "	if (textureType == 1){ // TEXTURE_TYPE_FADING = 1\r\n"
				+ "		float factor;\r\n"
				+ "		x = max(coordTexture.x, 0.0);\r\n"
				+ "		float y = max(coordTexture.y, 0.0);\r\n"
				+ "		gl_FragColor.rgb  = color.rgb;\r\n"
				+ "		gl_FragColor.a = color.a * (1.0 - x) * (1.0 - y);\r\n"
				+ "		return;\r\n" + "	}\r\n" + "		\r\n"
				+ "		\r\n" + "	// text	\r\n"
				+ "	if (textureType == 2){ // TEXTURE_TYPE_TEXT = 2;\r\n"
				+ "		vec4 textureVal = texture2D(Texture0, coordTexture);\r\n"
				+ "		if (textureVal.a < 0.25){\r\n"
				+ "	  		discard; // don't write\r\n" + "  		}\r\n"
				+ "  	\r\n" + "		gl_FragColor.rgb = color.rgb;\r\n"
				+ "		gl_FragColor.a = textureVal.a;\r\n" + "		\r\n"
				+ "		return;\r\n" + "	}\r\n" + "		\r\n"
				+ "		\r\n" + "		\r\n" + "	// dash\r\n"
				+ "				\r\n"
				+ "		x =  mod(dashValues[0] * coordTexture.x, 1.0);\r\n"
				+ "		if (x > dashValues[1] || (x > dashValues[2] && x <= dashValues[3])){\r\n"
				+ "			discard;\r\n" + "		}\r\n" + "		\r\n"
				+ "		gl_FragColor = color;\r\n" + "\r\n" + "	\r\n"
				+ "	\r\n" + "		\r\n" + "	\r\n" + "				\r\n"
				+ "\r\n" + "} ";

	}
}

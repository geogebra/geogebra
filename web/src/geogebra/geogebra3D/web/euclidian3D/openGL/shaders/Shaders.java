package geogebra.geogebra3D.web.euclidian3D.openGL.shaders;


import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface Shaders extends ClientBundle {
	public static Shaders INSTANCE=GWT.create(Shaders.class);

	@Source(value={"fragment-shader.txt"})
	TextResource fragmentShader();

	@Source(value={"vertex-shader.txt"})
	TextResource vertexShader();
}



package org.geogebra.web.geogebra3D.web.euclidian3D.openGL.shaders;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * Shader resource bundle
 *
 */
public interface Shaders extends ClientBundle {
	/** singleton instance */
	public static final Shaders INSTANCE = GWT.create(Shaders.class);

	/**
	 * @return shader for old GPUs
	 */
	@Source(value = { "fragment-shader-smaller.txt" })
	TextResource fragmentShaderSmaller(); // used for blacklisted GPUs

}

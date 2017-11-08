package org.geogebra.common.geogebra3D.euclidian3D.openGL;

/**
 * Extends Textures and disable some unused code
 * 
 */
public class TexturesShaders extends Textures {

	/**
	 * Simple constructor
	 * 
	 * @param renderer
	 */
	public TexturesShaders(Renderer renderer) {
		super(renderer);
	}

	@Override
	public void init() {
		renderer.createDummyTexture();
	}

}

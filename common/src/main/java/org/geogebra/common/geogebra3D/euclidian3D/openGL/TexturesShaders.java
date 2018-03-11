package org.geogebra.common.geogebra3D.euclidian3D.openGL;

/**
 * Extends Textures and disable some unused code
 * 
 */
public class TexturesShaders extends Textures {

	/**
	 * each description length must be equal and equal to a power of 2, number of
	 * descriptions must be <= each description length
	 */
	static private boolean[] DASH_DESCRIPTIONS = { 
			true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, // DASH_ID_FULL
			true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, // DASH_ID_FULL
																													// (hidden)
			true, true, false, false, true, true, false, false, true, true, false, false, true, true, false, false, // DASH_ID_DOTTED
			true, true, false, false, false, false, false, false, true, true, false, false, false, false, false, false, // DASH_ID_DOTTED
																														// (hidden)
			true, true, true, true, false, false, false, false, true, true, true, true, false, false, false, false, // DASH_ID_DASHED_SHORT
			true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, // DASH_ID_DASHED_SHORT
																														// (hidden)
			true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, // DASH_ID_DASHED_LONG
			true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, // DASH_ID_DASHED_LONG
																														// (hidden)
			true, true, true, true, true, true, true, false, false, false, false, true, false, false, false, false, // DASH_ID_DASHED_DOTTED
			false, false, true, true, true, false, false, false, false, false, false, true, false, false, false, false, // DASH_ID_DASHED_DOTTED
																														// (hidden)
			false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
			false // DASH_ID_NONE
	};

	static final public int DESCRIPTIONS_LENGTH = 16;

	private int packedDashIndex;

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
		// packed dashing
		int length = DESCRIPTIONS_LENGTH * DESCRIPTIONS_LENGTH;
		byte[] bytes = new byte[length];
		for (int i = 0; i < DASH_DESCRIPTIONS.length; i++) {
			if (DASH_DESCRIPTIONS[i]) {
				bytes[i] = (byte) 255;
			}
		}
		packedDashIndex = renderer.createAlphaTexture(DESCRIPTIONS_LENGTH, DESCRIPTIONS_LENGTH, bytes);
	}

	/**
	 * set texture for dashing
	 */
	public void setPackedDash() {
		renderer.bindTexture(packedDashIndex);
		renderer.setTextureNearest();
	}

}

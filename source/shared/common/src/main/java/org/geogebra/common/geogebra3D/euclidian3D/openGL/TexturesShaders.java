/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.geogebra3D.euclidian3D.openGL;

/**
 * Extends Textures and disable some unused code
 * 
 */
public class TexturesShaders extends Textures {

	private static final byte T = (byte) 255;
	private static final byte F = (byte) 0;

	/**
	 * each description length must be equal and equal to a power of 2, number of
	 * descriptions must be <= each description length
	 */
	static private byte[] DASH_DESCRIPTIONS = {

			T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, // DASH_ID_FULL
			T, T, T, T, T, T, T, T, F, F, F, F, F, F, F, F, // DASH_ID_FULL
															// (hidden)
			T, T, F, F, T, T, F, F, T, T, F, F, T, T, F, F, // DASH_ID_DOTTED
			T, T, F, F, F, F, F, F, T, T, F, F, F, F, F, F, // DASH_ID_DOTTED
															// (hidden)
			T, T, T, T, F, F, F, F, T, T, T, T, F, F, F, F, // DASH_ID_DASHED_SHORT
			T, T, T, T, F, F, F, F, F, F, F, F, F, F, F, F, // DASH_ID_DASHED_SHORT
															// (hidden)
			T, T, T, T, T, T, T, T, F, F, F, F, F, F, F, F, // DASH_ID_DASHED_LONG
			T, T, T, T, F, F, F, F, F, F, F, F, F, F, F, F, // DASH_ID_DASHED_LONG
															// (hidden)
			T, T, T, T, T, T, T, F, F, F, F, T, F, F, F, F, // DASH_ID_DASHED_DOTTED
			F, F, T, T, T, F, F, F, F, F, F, T, F, F, F, F, // DASH_ID_DASHED_DOTTED
															// (hidden)
			F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F // DASH_ID_NONE
	};

	static final public int DESCRIPTIONS_LENGTH = 16;

	private int packedDashIndex;

	/**
	 * Simple constructor
	 * 
	 * @param renderer
	 *            renderer
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
			bytes[i] = DASH_DESCRIPTIONS[i];
		}
		packedDashIndex = renderer.createAlphaTexture(DESCRIPTIONS_LENGTH, DESCRIPTIONS_LENGTH,
				bytes);
	}

	/**
	 * set texture for dashing
	 */
	public void setPackedDash() {
		renderer.getRendererImpl().bindTexture(packedDashIndex);
		renderer.setTextureNearest();
	}

}

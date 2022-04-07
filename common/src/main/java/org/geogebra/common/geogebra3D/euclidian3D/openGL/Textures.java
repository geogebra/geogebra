package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Class managing textures (dash, images, etc.)
 * 
 * @author mathieu
 *
 */
public class Textures {

	// private GL gl;

	// /////////////////
	// dash
	/** opengl organization of the dash textures */
	private int[] texturesIndex;
	/** no dash. */
	public static final int DASH_INIT = -1;
	/** no dash. */
	public static final int DASH_NONE = 0;
	/** simple dash: 1-(1), ... */

	public static final int DASH_SHORT = 1;
	/** long dash: 2-(2), ... */
	public static final int DASH_LONG = 7;
	/** dotted dash: 1-(1)-1-(1)-1-(1)-1-(1), ... */
	public static final int DASH_DOTTED = 3;
	/** dotted/dashed dash: 7-(4)-1-(4), ... */
	public static final int DASH_DOTTED_DASHED = 8;
	/** (hidden) no dash. */
	public static final int DASH_NONE_HIDDEN = 5;
	/** (hidden) simple dash: 1-(1), ... */
	public static final int DASH_SHORT_HIDDEN = 6;
	/** (hidden) long dash: 2-(2), ... */
	public static final int DASH_LONG_HIDDEN = 2;
	/** (hidden) dotted dash: 1-(3), ... */
	public static final int DASH_DOTTED_HIDDEN = 4;
	/** (hidden) dotted/dashed dash: 7-(4)-1-(4), ... */
	public static final int DASH_DOTTED_DASHED_HIDDEN = 9;

	/** dash for packed curves */
	public static final int DASH_PACKED = 10;
	/** dash for packed curves (hidden) */
	public static final int DASH_PACKED_HIDDEN = 11;

	/** dash ids for full line visible/hidden are 0/1 */
	public static final int DASH_ID_FULL = 0;
	/** dash ids for line DOTTED are 2/3 */
	public static final int DASH_ID_DOTTED = DASH_ID_FULL + 2;
	/** dash ids for line DASHED_SHORT are 4/5 */
	public static final int DASH_ID_DASHED_SHORT = DASH_ID_DOTTED + 2;
	/** dash ids for line DASHED_LONG are 6/7 */
	public static final int DASH_ID_DASHED_LONG = DASH_ID_DASHED_SHORT + 2;
	/** dash ids for line DASHED_DOTTED are 8/9 */
	public static final int DASH_ID_DASHED_DOTTED = DASH_ID_DASHED_LONG + 2;
	/** dash ids for line not visible is 10 */
	public static final int DASH_ID_NONE = DASH_ID_DASHED_DOTTED + 2;
	/** dash ids length */
	public static final int DASH_ID_LENGTH = DASH_ID_NONE + 1;

	/** number of dash styles */
	static private int DASH_NUMBER = 10;
	/** description of the dash styles */
	static private boolean[][] DASH_DESCRIPTION = { { true }, // DASH_NONE
			{ true, false, true, false }, // DASH_SHORT
			{ true, false, false, false }, // DASH_LONG_HIDDEN
			{ true, false, true, false, true, false, true, false }, // DASH_DOTTED
			{ true, false, false, false, true, false, false, false }, // DASH_DOTTED_HIDDEN
			{ true, true, false, false }, // DASH_NONE_HIDDEN
			{ true, false, false, false }, // DASH_SHORT_HIDDEN
			{ true, true, false, false }, // DASH_LONG
			{ true, true, true, true, true, true, true, false, false, false,
					false, true, false, false, false, false }, // DASH_DOTTED_DASHED
			{ false, false, true, true, true, false, false, false, false, false,
					false, true, false, false, false, false } // DASH_DOTTED_DASHED_HIDDEN

	};

	// /////////////////
	// fading
	/** fading texture for surfaces */
	public static final int FADING = DASH_NUMBER;

	static private int TEXTURES_NUMBER = FADING + 1;

	protected Renderer renderer;

	/**
	 * default constructor
	 * 
	 * @param renderer
	 *            renderer
	 */
	public Textures(Renderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * Initialize textures.
	 */
	public void init() {

		renderer.enableTextures2D();

		texturesIndex = new int[TEXTURES_NUMBER];
		renderer.getRendererImpl().genTextures2D(TEXTURES_NUMBER,
				texturesIndex);

		// dash textures
		for (int i = 0; i < DASH_NUMBER; i++) {
			initDashTexture(texturesIndex[i], DASH_DESCRIPTION[i]);
		}

		// fading textures
		initFadingTexture(texturesIndex[FADING]);

		renderer.disableTextures2D();
	}

	/**
	 * load a template texture (linear type)
	 * 
	 * @param index
	 *            index
	 */
	public void loadTextureLinear(int index) {
		setTextureLinear(texturesIndex[index]);
	}

	/**
	 * sets a computed texture (linear type)
	 * 
	 * @param index
	 *            index
	 */
	public void setTextureLinear(int index) {
		renderer.getRendererImpl().bindTexture(index);
		setTextureLinear();
	}

	/**
	 * remove the texture from memory
	 * 
	 * @param index
	 *            texture index
	 */
	public void removeTexture(int index) {
		// nothing done here
	}

	// ///////////////////////////////////////
	// DASH TEXTURES
	// ///////////////////////////////////////

	private void initDashTexture(int n, boolean[] description) {
		int sizeX = description.length;
		byte[] bytes = new byte[sizeX];
		for (int i = 0; i < sizeX; i++) {
			if (description[i]) {
				bytes[i] = (byte) 255;
			}
		}
		renderer.getRendererImpl().bindTexture(n);
		renderer.textureImage2D(sizeX, 1, bytes);
	}

	/**
	 * call the correct texture for the line type specified
	 * 
	 * @param lineType
	 *            line type (EuclidianStyleConstants.LINE_TYPE_*)
	 */
	public void setDashFromLineType(int lineType) {
		renderer.getRendererImpl()
				.setDashTexture(getDashFromLineType(lineType));
	}

	/**
	 * @param lineType
	 *            line type
	 * @return dash type (not hidden)
	 */
	final static public int getDashFromLineType(int lineType) {
		switch (lineType) {
		case EuclidianStyleConstants.LINE_TYPE_FULL:
			return DASH_NONE;
		case EuclidianStyleConstants.LINE_TYPE_DOTTED:
			return DASH_DOTTED;
		case EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT:
			return DASH_SHORT;
		case EuclidianStyleConstants.LINE_TYPE_DASHED_LONG:
			return DASH_LONG;
		case EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED:
			return DASH_DOTTED_DASHED;
		default:
			return DASH_NONE;
		}
	}

	/**
	 * call the correct texture for the line type specified for hidden parts
	 * 
	 * @param lineType
	 *            line type
	 */
	public void setDashFromLineTypeHidden(int lineType) {
		renderer.getRendererImpl()
				.setDashTexture(getDashFromLineTypeHidden(lineType));
	}

	/**
	 * @param lineType
	 *            line type
	 * @return dash type hidden
	 */
	final static public int getDashFromLineTypeHidden(int lineType) {
		switch (lineType) {
		case EuclidianStyleConstants.LINE_TYPE_FULL:
			return DASH_NONE_HIDDEN;
		case EuclidianStyleConstants.LINE_TYPE_DOTTED:
			return DASH_DOTTED_HIDDEN;
		case EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT:
			return DASH_SHORT_HIDDEN;
		case EuclidianStyleConstants.LINE_TYPE_DASHED_LONG:
			return DASH_LONG_HIDDEN;
		case EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED:
			return DASH_DOTTED_DASHED_HIDDEN;
		default:
			return DASH_NONE_HIDDEN;
		}
	}

	/**
	 * @param lineType
	 *            line type
	 * @param lineTypeHidden
	 *            line type for hidden parts
	 * @return dash id
	 */
	final static public int getDashIdFromLineType(int lineType, int lineTypeHidden) {
		int notHiddenId = getDashIdFromLineType(lineType);
		switch (lineTypeHidden) {
		case EuclidianStyleConstants.LINE_TYPE_HIDDEN_AS_NOT_HIDDEN:
			return notHiddenId * (1 + DASH_ID_LENGTH);
		case EuclidianStyleConstants.LINE_TYPE_HIDDEN_DASHED:
		default:
			return notHiddenId * (1 + DASH_ID_LENGTH) + DASH_ID_LENGTH;
		case EuclidianStyleConstants.LINE_TYPE_HIDDEN_NONE:
			return notHiddenId + DASH_ID_NONE * DASH_ID_LENGTH;
		}
	}

	static private int getDashIdFromLineType(int lineType) {
		switch (lineType) {
		case EuclidianStyleConstants.LINE_TYPE_FULL:
		default:
			return DASH_ID_FULL;
		case EuclidianStyleConstants.LINE_TYPE_DOTTED:
			return DASH_ID_DOTTED;
		case EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT:
			return DASH_ID_DASHED_SHORT;
		case EuclidianStyleConstants.LINE_TYPE_DASHED_LONG:
			return DASH_ID_DASHED_LONG;
		case EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED:
			return DASH_ID_DASHED_DOTTED;
		}
	}

	// ///////////////////////////////////////
	// DASH TEXTURES
	// ///////////////////////////////////////

	private void initFadingTexture(int index) {

		int n = 2;
		int sizeX = n, sizeY = n;
		boolean[] description = { true, false, false, false };

		byte[] bytes = new byte[sizeX * sizeY + 2]; // TODO understand why +2

		for (int i = 0; i < sizeX * sizeY; i++) {
			if (description[i]) {
				bytes[i] = (byte) 255;
			}
		}

		renderer.getRendererImpl().bindTexture(index);

		renderer.textureImage2D(sizeX, sizeY, bytes);
	}

	private void setTextureLinear() {
		renderer.setTextureLinear();
	}

	/**
	 * 
	 * @param index
	 *            index for creation
	 * @return texture index
	 */
	public int getIndex(int index) {
		return texturesIndex[index];
	}

}

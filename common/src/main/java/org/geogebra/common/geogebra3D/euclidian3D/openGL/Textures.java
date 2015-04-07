package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.ImageManager;

/**
 * Class managing textures (dash, images, etc.)
 * 
 * @author mathieu
 *
 */
public class Textures {

	// private GL gl;

	private ImageManager imageManager;

	// /////////////////
	// dash
	/** opengl organization of the dash textures */
	private int[] texturesIndex;
	/** no dash. */
	static public int DASH_INIT = -1;
	/** no dash. */
	static public int DASH_NONE = 0;
	/** simple dash: 1-(1), ... */

	static public int DASH_SHORT = 1;
	/** long dash: 2-(2), ... */
	static public int DASH_LONG = 7;
	/** dotted dash: 1-(1)-1-(1)-1-(1)-1-(1), ... */
	static public int DASH_DOTTED = 3;
	/** dotted/dashed dash: 7-(4)-1-(4), ... */
	static public int DASH_DOTTED_DASHED = 8;
	/** (hidden) no dash. */
	static public int DASH_NONE_HIDDEN = 5;
	/** (hidden) simple dash: 1-(1), ... */
	static public int DASH_SHORT_HIDDEN = 6;
	/** (hidden) long dash: 2-(2), ... */
	static public int DASH_LONG_HIDDEN = 2;
	/** (hidden) dotted dash: 1-(3), ... */
	static public int DASH_DOTTED_HIDDEN = 4;
	/** (hidden) dotted/dashed dash: 7-(4)-1-(4), ... */
	static public int DASH_DOTTED_DASHED_HIDDEN = 9;
	//

	/** number of dash styles */
	static private int DASH_NUMBER = 10;
	/** description of the dash styles */
	static private boolean[][] DASH_DESCRIPTION = {
			{ true }, // DASH_NONE
			{ true, false, true, false }, // DASH_SHORT
			{ true, false, false, false }, // DASH_LONG_HIDDEN
			{ true, false, true, false, true, false, true, false }, // DASH_DOTTED
			{ true, false, false, false, true, false, false, false }, // DASH_DOTTED_HIDDEN
			{ true, true, false, false }, // DASH_NONE_HIDDEN
			{ true, false, false, false }, // DASH_SHORT_HIDDEN
			{ true, true, false, false }, // DASH_LONG
			{ true, true, true, true, true, true, true, false, false, false,
					false, true, false, false, false, false }, // DASH_DOTTED_DASHED
			{ false, false, true, true, true, false, false, false, false,
					false, false, true, false, false, false, false } // DASH_DOTTED_DASHED_HIDDEN

	};

	/**
	 * dash values for shaders
	 */
	static public float[][] DASH_SHADERS_VALUES = {
			// coeff, a, b, c
			// in shaders : x = mod(dashValues[0] * coordTexture.x, 1.0)
			// if (x > a || (x > b && x <= c)) then discard
			{ 2.0f, 0.5f, -1f, -1f }, // {true, false, true, false}, //
										// DASH_SHORT
			{ 1.0f, 0.25f, -1f, -1f }, // {true, false, false, false}, //
										// DASH_LONG_HIDDEN
			{ 4.0f, 0.5f, -1f, -1f }, // {true, false, true, false, true, false,
										// true, false}, // DASH_DOTTED
			{ 2.0f, 0.25f, -1f, -1f }, // {true, false, false, false, true,
										// false, false, false}, //
										// DASH_DOTTED_HIDDEN
			{ 1.0f, 0.5f, -1f, -1f }, // {true, true, false, false}, //
										// DASH_NONE_HIDDEN
			{ 1.0f, 0.25f, -1f, -1f }, // {true, false, false, false}, //
										// DASH_SHORT_HIDDEN
			{ 1.0f, 0.5f, -1f, -1f }, // {true, true, false, false}, //
										// DASH_LONG
			{ 1.0f, 12f / 16f, 7f / 16f, 11f / 16f }, // {true,true,true,true,
														// true,true,true,false,
														// false,false,false,true,
														// false,false,false,false},
														// // DASH_DOTTED_DASHED
			{ 1.0f, 12f / 16f, 3f / 16f, 11f / 16f }, // {false,false,true,true,
														// true,false,false,false,
														// false,false,false,true,
														// false,false,false,false}
														// //
														// DASH_DOTTED_DASHED_HIDDEN

	};

	// /////////////////
	// fading
	/** fading texture for surfaces */
	static public int FADING = DASH_NUMBER;

	static private int TEXTURES_NUMBER = FADING + 1;

	private Renderer renderer;

	/**
	 * default constructor
	 * 
	 * @param gl
	 */
	public Textures(Renderer renderer, ImageManager abstractImageManager) {

		this.renderer = renderer;
		this.imageManager = abstractImageManager;

	}

	public void init() {

		renderer.enableTextures2D();

		texturesIndex = new int[TEXTURES_NUMBER];
		renderer.genTextures2D(TEXTURES_NUMBER, texturesIndex);

		// dash textures
		for (int i = 0; i < DASH_NUMBER; i++)
			initDashTexture(texturesIndex[i], DASH_DESCRIPTION[i]);

		// fading textures
		initFadingTexture(texturesIndex[FADING]);

		renderer.disableTextures2D();

	}

	/**
	 * load a template texture (linear type)
	 * 
	 * @param index
	 */
	public void loadTextureLinear(int index) {

		setTextureLinear(texturesIndex[index]);

	}

	/**
	 * sets a computed texture (linear type)
	 * 
	 * @param index
	 */
	public void setTextureLinear(int index) {

		renderer.bindTexture(index);
		setTextureLinear();

	}

	// ///////////////////////////////////////
	// DASH TEXTURES
	// ///////////////////////////////////////

	private void initDashTexture(int n, boolean[] description) {

		int sizeX = description.length;
		// int sizeY = 1;

		// byte[] bytes = new byte[4*sizeX*sizeY];
		byte[] bytes = new byte[sizeX];

		for (int i = 0; i < sizeX; i++)
			if (description[i])
				bytes[i] = (byte) 255;
		/*
		 * bytes[4*i+0]= bytes[4*i+1]= bytes[4*i+2]= bytes[4*i+3]= (byte) 255;
		 */

		renderer.bindTexture(n);

		// gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_ALPHA, sizeX, 1, 0,
		// GL.GL_ALPHA, GL.GL_UNSIGNED_BYTE, buf);
		renderer.textureImage2D(sizeX, 1, bytes);

	}

	/**
	 * call the correct texture for the line type specified
	 * 
	 * @param lineType
	 */
	public void setDashFromLineType(int lineType) {

		switch (lineType) {
		case EuclidianStyleConstants.LINE_TYPE_FULL:
			renderer.setDashTexture(DASH_NONE);
			break;

		case EuclidianStyleConstants.LINE_TYPE_DOTTED:
			renderer.setDashTexture(DASH_DOTTED);
			break;

		case EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT:
			renderer.setDashTexture(DASH_SHORT);
			break;

		case EuclidianStyleConstants.LINE_TYPE_DASHED_LONG:
			renderer.setDashTexture(DASH_LONG);
			break;

		case EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED:
			renderer.setDashTexture(DASH_DOTTED_DASHED);
			break;

		default:
			break;
		}
	}

	/**
	 * call the correct texture for the line type specified for hidden parts
	 * 
	 * @param lineType
	 */
	public void setDashFromLineTypeHidden(int lineType) {

		switch (lineType) {
		case EuclidianStyleConstants.LINE_TYPE_FULL:
			renderer.setDashTexture(DASH_NONE_HIDDEN);
			break;

		case EuclidianStyleConstants.LINE_TYPE_DOTTED:
			renderer.setDashTexture(DASH_DOTTED_HIDDEN);
			break;

		case EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT:
			renderer.setDashTexture(DASH_SHORT_HIDDEN);
			break;

		case EuclidianStyleConstants.LINE_TYPE_DASHED_LONG:
			renderer.setDashTexture(DASH_LONG_HIDDEN);
			break;

		case EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED:
			renderer.setDashTexture(DASH_DOTTED_DASHED_HIDDEN);
			break;

		default:
			break;
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

		for (int i = 0; i < sizeX * sizeY; i++)
			if (description[i])
				bytes[i] = (byte) 255;

		renderer.bindTexture(index);

		renderer.textureImage2D(sizeX, sizeY, bytes);

	}

	/*
	 * private void initViewButtonsTextures(int index, String name){
	 * 
	 * 
	 * 
	 * 
	 * try { //gets the image BufferedImage img =
	 * readImage("/geogebra3D/gui/images/"+name);
	 * 
	 * 
	 * //turn it into pixels for texture 2D boolean storeAlphaChannel = true;
	 * int[] packedPixels = new int[img.getWidth() * img.getHeight()];
	 * 
	 * PixelGrabber pixelgrabber = new PixelGrabber(img, 0, 0, img.getWidth(),
	 * img.getHeight(), packedPixels, 0, img.getWidth()); try {
	 * pixelgrabber.grabPixels(); } catch (InterruptedException e) { throw new
	 * RuntimeException(); }
	 * 
	 * int bytesPerPixel = storeAlphaChannel ? 4 : 3; ByteBuffer unpackedPixels
	 * = RendererJogl.newByteBuffer(packedPixels.length * bytesPerPixel);
	 * 
	 * 
	 * for (int row = img.getHeight() - 1; row >= 0; row--) { for (int col = 0;
	 * col < img.getWidth(); col++) { int packedPixel = packedPixels[row *
	 * img.getWidth() + col]; unpackedPixels.put((byte) ((packedPixel >> 16) &
	 * 0xFF)); unpackedPixels.put((byte) ((packedPixel >> 8) & 0xFF));
	 * unpackedPixels.put((byte) ((packedPixel >> 0) & 0xFF)); if
	 * (storeAlphaChannel) { unpackedPixels.put((byte) ((packedPixel >> 24) &
	 * 0xFF)); } } }
	 * 
	 * unpackedPixels.flip();
	 * 
	 * //create the texture gl.glBindTexture(GL.GL_TEXTURE_2D, index);
	 * gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, img.getWidth(),
	 * img.getHeight(), 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, unpackedPixels);
	 * 
	 * } catch (IOException e) { e.printStackTrace(); }
	 * 
	 * }
	 * 
	 * private BufferedImage readImage(String name) throws IOException { return
	 * ImageManager
	 * .toBufferedImage(imageManager.getImageResource(name),Transparency
	 * .TRANSLUCENT); }
	 */

	final private void setTextureLinear() {
		renderer.setTextureLinear();
	}

	final private void setTextureNearest() {
		renderer.setTextureNearest();
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

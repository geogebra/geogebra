package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawLabel3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.RendererWithImpl;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GBufferedImageD;
import org.geogebra.desktop.geogebra3D.euclidian3D.EuclidianView3DD;
import org.geogebra.desktop.geogebra3D.euclidian3D.opengl.RendererJogl.GLlocal;
import org.geogebra.desktop.gui.menubar.GeoGebraMenuBar;
import org.geogebra.desktop.gui.util.ImageSelection;
import org.geogebra.desktop.util.FrameCollector;

/**
 * Renderer checking if we can use shaders or not
 * 
 * @author mathieu
 * 
 */
public class RendererCheckGLVersionD extends RendererWithImpl implements
		GLEventListener {


	protected RendererJogl jogl;

	private Animator animator;

	/** canvas usable for a JPanel */
	public Component3D canvas;

	protected FrameCollector gifEncoder;

	protected BufferedImage bi;

	private BufferedImage[] equirectangularTilesLeft;

	private BufferedImage[] equirectangularTilesRight;

	/**
	 * Constructor
	 * 
	 * @param view
	 * @param useCanvas
	 */
	public RendererCheckGLVersionD(EuclidianView3D view, boolean useCanvas) {
		this(view, useCanvas, RendererType.NOT_SPECIFIED);
	}

	private boolean isGL2ES2;

	/**
	 * Constructor
	 * 
	 * @param view
	 * @param useCanvas
	 * @param type
	 */
	public RendererCheckGLVersionD(EuclidianView3D view, boolean useCanvas,
			RendererType type) {
		super(view, type);
		Log.debug("create jogl -- use Canvas : " + useCanvas);
		jogl = new RendererJogl();
		Log.debug("create jogl -- default profile");
		// init jogl profile
		isGL2ES2 = RendererJogl.setDefaultProfile();

		// canvas = view;
		Log.debug("create 3D component -- use Canvas : " + useCanvas);
		RendererJogl.initCaps(view.isStereoBuffered());
		canvas = RendererJogl.createComponent3D(useCanvas);

		Log.debug("add gl event listener");
		canvas.addGLEventListener(this);

		Log.debug("create animator");
		animator = RendererJogl.createAnimator(canvas, 60);
		// animator.setRunAsFastAsPossible(true);
		// animator.setRunAsFastAsPossible(false);

		Log.debug("start animator");
		animator.start();

	}

	/**
	 * Called by the drawable immediately after the OpenGL context is
	 * initialized for the first time. Can be used to perform one-time OpenGL
	 * initialization such as setup of lights and display lists.
	 * 
	 * @param drawable
	 *            The GLAutoDrawable object.
	 */
	@Override
	public void init(GLAutoDrawable drawable) {

		initCheckShaders(drawable);

		setGL(drawable);

		// check openGL version
		final String version = getGL().glGetString(GLlocal.GL_VERSION);

		// Check For VBO support
		final boolean VBOsupported = getGL().isFunctionAvailable(
				"glGenBuffersARB")
				&& getGL().isFunctionAvailable("glBindBufferARB")
				&& getGL().isFunctionAvailable("glBufferDataARB")
				&& getGL().isFunctionAvailable("glDeleteBuffersARB");

		Log.debug("openGL version : " + version + ", vbo supported : "
				+ VBOsupported);

		rendererImpl.initFBO();

		init();
	}

	private void initCheckShaders(GLAutoDrawable drawable) {
		// reset picking
		oldGeoToPickSize = -1;

		// start init
		String glInfo[] = RendererJogl.getGLInfos(drawable);

		String glCard = glInfo[6];
		String glVersion = glInfo[7];

		Log.debug("Init on " + Thread.currentThread()
				+ "\nChosen GLCapabilities: " + glInfo[0]
				+ "\ndouble buffered: " + glInfo[1] + "\nstereo: " + glInfo[2]
				+ "\nstencil: " + glInfo[3] + "\nINIT GL IS: " + glInfo[4]
				+ "\nGL_VENDOR: " + glInfo[5] + "\nGL_RENDERER: " + glCard
				+ "\nGL_VERSION: " + glVersion + "\nisGL2ES2: " + isGL2ES2);

		GeoGebraMenuBar.setGlCard(glInfo[6]);
		GeoGebraMenuBar.setGlVersion(glInfo[7]);

		// this is abstract method: don't create old GL / shaders here

		if (type == RendererType.NOT_SPECIFIED) {
			if (isGL2ES2) {
				try {
					// retrieving version, which should be first char, e.g.
					// "4.0 etc."
					String[] version = glVersion.split("\\.");
					int versionInt = Integer.parseInt(version[0]);
					Log.debug("==== GL version is " + glVersion
							+ " which means GL>=" + versionInt);
					if (versionInt < 3) {
						// GL 1.x: can't use shaders
						// GL 2.x so GLSL < 1.3: not supported
						type = RendererType.GL2;
					} else if (versionInt >= 4) {
						// GL 4.x or above: can use shaders (GLSL >= 4.0)
						type = RendererType.SHADER;
					} else {
						// GL 3.x so GLSL < 1.3: not supported
						if (version.length > 1) {
							versionInt = Integer.parseInt(version[1]);
							Log.debug("==== GL minor version is " + versionInt);
							if (versionInt < 3) {
								// GL 3.0 -- 3.2 so GLSL < 3.3: not supported
								type = RendererType.GL2;
							} else {
								// GL 3.3: can use shaders (GLSL = 3.3)
								type = RendererType.SHADER;
							}
						} else {
							// probably GL 3.0 so GLSL = 1.3: not supported
							type = RendererType.GL2;
						}
					}
				} catch (Exception e) {
					// exception: don't use shaders
					type = RendererType.GL2;
				}
			} else {
				// not GL2ES2 capable
				Log.debug("==== not GL2ES2 capable");
				type = RendererType.GL2;
			}
		}

		if (type == RendererType.SHADER) {
			rendererImpl = new RendererImplShadersD(this, view3D, jogl);
		} else {
			rendererImpl = new RendererImplGL2(this, view3D, jogl);
		}

	}



	@Override
	public void setLineWidth(double width) {

		getGL().glLineWidth((float) width);

	}


	protected static final int[] GL_CLIP_PLANE = { GLlocal.GL_CLIP_PLANE0,
			GLlocal.GL_CLIP_PLANE1, GLlocal.GL_CLIP_PLANE2,
			GLlocal.GL_CLIP_PLANE3, GLlocal.GL_CLIP_PLANE4,
			GLlocal.GL_CLIP_PLANE5 };






	@Override
	public void dispose(GLAutoDrawable drawable) {
		// DO NOT REMOVE THE METHOD HERE -- NEEDED TO AVOID ERRORS IN
		// INSTALLED/PORTABLE VERSIONS
		setGL(drawable);
		rendererImpl.dispose();
	}



	@Override
	public Component3D getCanvas() {
		return canvas;
	}

	@Override
	public void resumeAnimator() {
		animator.resume();
	}

	@Override
	public void display() {
		canvas.display();
	}

	/**
	 * 
	 * openGL method called when the display is to be computed.
	 * <p>
	 * First, it calls {@link #doPick()} if a picking is to be done. Then, for
	 * each {@link Drawable3D}, it calls:
	 * <ul>
	 * <li> {@link Drawable3D#drawHidden(EuclidianRenderer3D)} to draw hidden
	 * parts (dashed segments, lines, ...)</li>
	 * <li> {@link Drawable3D#drawTransp(EuclidianRenderer3D)} to draw
	 * transparent objects (planes, spheres, ...)</li>
	 * <li> {@link Drawable3D#drawSurfacesForHiding(EuclidianRenderer3D)} to draw
	 * in the z-buffer objects that hides others (planes, spheres, ...)</li>
	 * <li> {@link Drawable3D#drawTransp(EuclidianRenderer3D)} to re-draw
	 * transparent objects for a better alpha-blending</li>
	 * <li> {@link Drawable3D#drawOutline(EuclidianRenderer3D)} to draw not
	 * hidden parts (dash-less segments, lines, ...)</li>
	 * </ul>
	 */
	@Override
	public void display(GLAutoDrawable gLDrawable) {
	
		// Log.debug(gLDrawable+"");
		setGL(gLDrawable);
	
		drawScene();

		if (EuclidianView3DD.EXPORT_TO_PRINTER_3D) {
			if (type == RendererType.SHADER) {
				((EuclidianView3DD) view3D).exportToPrinter3D();
			}
		}
	}



	@Override
	protected final void exportImage() {
	
	
		switch (exportType) {
		case ANIMATEDGIF:
			Log.debug("Exporting frame: " + export_i);
	
			setExportImage();
			if (bi == null) {
				Log.error("image null");
			} else {
				gifEncoder.addFrame(bi);
			}
	
			export_val += export_step;
	
			if (export_val > export_max + 0.00000001
					|| export_val < export_min - 0.00000001) {
				export_val -= 2 * export_step;
				export_step *= -1;
			}
	
			export_i++;
	
			if (export_i >= export_n) {
				exportType = ExportType.NONE;
				gifEncoder.finish();
	
				Log.debug("GIF export finished");
				rendererImpl.endNeedExportImage();
	
			} else {
				export_num.setValue(export_val);
				export_num.updateRepaint();
			}
			break;
	
		case CLIPBOARD:
			exportType = ExportType.NONE;
			Log.debug("Exporting to clipboard");
	
			setExportImage();
	
			if (bi == null) {
				Log.error("image null");
			} else {
				ImageSelection imgSel = new ImageSelection(
						bi);
				Toolkit.getDefaultToolkit().getSystemClipboard()
						.setContents(imgSel, null);
			}
			rendererImpl.endNeedExportImage();
	
			break;
		case UPLOAD_TO_GEOGEBRATUBE:
			exportType = ExportType.NONE;
			Log.debug("Uploading to GeoGebraTube");
	
			setExportImage();
	
			if (bi == null) {
				Log.error("image null, uploading with no preview");
				// TODO: set 2D preview image
			}
	
			view3D.getApplication().uploadToGeoGebraTube();
			rendererImpl.endNeedExportImage();
			
			break;
	
		default:
			if (needExportImage) {
				setExportImage();
				if (!exportImageForThumbnail){
					// call write to file
					((EuclidianView3DD) view3D).writeExportImage();
				}
				rendererImpl.endNeedExportImage();
			}
			break;
		}
	
	}

	@Override
	protected void exportImageEquirectangular() {
	
		if (bi == null) {
			Log.error("image null");
		} else {
			ImageSelection imgSel = new ImageSelection(
					bi);
			Toolkit.getDefaultToolkit().getSystemClipboard()
					.setContents(imgSel, null);
		}
		rendererImpl.endNeedExportImage();
	}

	@Override
	protected void setDepthFunc() {
		getGL().glDepthFunc(GLlocal.GL_LEQUAL); // less or equal for
												// transparency
	}

	@Override
	protected void enablePolygonOffsetFill() {
		getGL().glEnable(GLlocal.GL_POLYGON_OFFSET_FILL);
	}

	@Override
	protected void setBlendFunc() {
		getGL().glBlendFunc(GLlocal.GL_SRC_ALPHA,
				GLlocal.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	protected void enableNormalNormalized() {
		getGL().glEnable(GLlocal.GL_NORMALIZE);
	}

	/**
	 * openGL method called when the canvas is reshaped.
	 */
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int w,
			int h) {
			
				setGL(drawable);
			
				setView(x, y, w, h);
				view3D.reset();
			
			}

	/**
	 * openGL method called when the display change. empty method
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
	}


	/**
	 * remove texture at index
	 * 
	 * @param index
	 *            texture index
	 */
	public void removeTexture(int index) {
		getGL().glDeleteTextures(1, new int[] { index }, 0);
	}

	@Override
	public GBufferedImage createBufferedImage(DrawLabel3D label) {
		return new GBufferedImageD(label.getWidth(), label.getHeight(),
				GBufferedImage.TYPE_INT_ARGB);
	}

	@Override
	public void createAlphaTexture(DrawLabel3D label, GBufferedImage bimg) {
	
		byte[] buffer = ARGBtoAlpha(label, ((GBufferedImageD) bimg).getData());
	
		label.setTextureIndex(createAlphaTexture(label.getTextureIndex(),
				label.waitForReset(), label.getWidthPowerOfTwo(),
				label.getHeightPowerOfTwo(), buffer));
	
	}

	/**
	 * @param textureIndex
	 *            texture index
	 * @param waitForReset
	 *            wait for reset
	 * @param sizeX
	 * @param sizeY
	 * @param buf
	 * @return a texture for alpha channel
	 */
	private int createAlphaTexture(int textureIndex, boolean waitForReset, int sizeX,
			int sizeY, byte[] buf) {
			
				if (textureIndex != 0 && !waitForReset) {
					removeTexture(textureIndex);
				}
			
				enableTextures2D();
			
				int[] index = new int[1];
				genTextures2D(1, index);
			
				bindTexture(index[0]);
			
				textureImage2D(sizeX, sizeY, buf);
			
				disableTextures2D();
			
				return index[0];
			}

	@Override
	public void textureImage2D(int sizeX, int sizeY, byte[] buf) {
		getGL().glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_ALPHA, sizeX, sizeY, 0,
				GL.GL_ALPHA, GL.GL_UNSIGNED_BYTE, ByteBuffer.wrap(buf));
	
	}

	@Override
	public void setTextureLinear() {
		getGL().glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_LINEAR);
		getGL().glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_LINEAR);
		getGL().glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
				GL.GL_CLAMP_TO_EDGE); // prevent repeating the texture
		getGL().glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
				GL.GL_CLAMP_TO_EDGE); // prevent repeating the texture
	
	}

	@Override
	public void setTextureNearest() {
		getGL().glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_NEAREST);
		getGL().glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_NEAREST);
	}

	/**
	 * 
	 * @return GL instance
	 */
	protected GL getGL() {
		return jogl.getGL();
	}

	/**
	 * set GL instance
	 * 
	 * @param gLDrawable
	 *            GL drawable
	 */
	public final void setGL(GLAutoDrawable gLDrawable) {
		jogl.setGL(gLDrawable);
	}


	@Override
	protected void disableStencilLines() {
		getGL().glDisable(GLlocal.GL_STENCIL_TEST);
		waitForDisableStencilLines = false;
	}

	@Override
	protected void setGIFEncoder(Object gifEncoder) {
		this.gifEncoder = (FrameCollector) gifEncoder;
	}


	@Override
	protected void initExportImageEquirectangularTiles() {
		if (equirectangularTilesLeft == null) {
			equirectangularTilesLeft = new BufferedImage[EXPORT_IMAGE_EQUIRECTANGULAR_LONGITUDE_STEPS];
			equirectangularTilesRight = new BufferedImage[EXPORT_IMAGE_EQUIRECTANGULAR_LONGITUDE_STEPS];
		}
	}

	@Override
	protected void setExportImageEquirectangularTileLeft(int i) {
		setExportImage();
		equirectangularTilesLeft[i] = bi;
	}

	@Override
	protected void setExportImageEquirectangularTileRight(int i) {
		setExportImage();
		equirectangularTilesRight[i] = bi;
	}

	private void setRGBFromTile(int i, int x, int y,
			int xTile, int yTile) {
				bi.setRGB(x, y, equirectangularTilesLeft[i].getRGB(xTile, yTile));
				bi.setRGB(x, y + EXPORT_IMAGE_EQUIRECTANGULAR_HEIGHT,
						equirectangularTilesRight[i].getRGB(xTile, yTile));
			}

	private void setWhite(int x, int y) {
		bi.setRGB(x, y, INT_RGB_WHITE);
		bi.setRGB(x, y + EXPORT_IMAGE_EQUIRECTANGULAR_HEIGHT, INT_RGB_WHITE);
	}

	@Override
	protected void setExportImageEquirectangularFromTiles() {
		bi = new BufferedImage(EXPORT_IMAGE_EQUIRECTANGULAR_WIDTH,
				EXPORT_IMAGE_EQUIRECTANGULAR_HEIGHT * 2,
				BufferedImage.TYPE_INT_RGB);
	
		int shiftY = (EXPORT_IMAGE_EQUIRECTANGULAR_HEIGHT - EXPORT_IMAGE_EQUIRECTANGULAR_HEIGHT_ELEMENT) / 2;
		int shiftAlpha = EXPORT_IMAGE_EQUIRECTANGULAR_HEIGHT / 2;
		for (int i = 0; i < EXPORT_IMAGE_EQUIRECTANGULAR_LONGITUDE_STEPS; i++) {
			int shiftX = i * EXPORT_IMAGE_EQUIRECTANGULAR_WIDTH_ELEMENT;
			for (int x = 0; x < EXPORT_IMAGE_EQUIRECTANGULAR_WIDTH_ELEMENT; x++) {
				// top white
				for (int y = 0; y < shiftY; y++) {
					setWhite(x + shiftX, y);
				}
				
				// first line will be missed by alpha
				setRGBFromTile(i, x + shiftX, shiftY, x, 0);
	
				// middle line
				setRGBFromTile(i, x + shiftX, shiftAlpha, x,
						EXPORT_IMAGE_EQUIRECTANGULAR_HEIGHT_ELEMENT / 2);
	
				// angle - tangent match
				for (int yAlpha = 1; yAlpha < EXPORT_IMAGE_EQUIRECTANGULAR_HEIGHT_ELEMENT / 2; yAlpha++) {
					double alpha = ((double) (2 * yAlpha * EXPORT_IMAGE_EQUIRECTANGULAR_LATITUTDE_MAX))
							/ EXPORT_IMAGE_EQUIRECTANGULAR_HEIGHT_ELEMENT;
					int y = (int) (EXPORT_IMAGE_EQUIRECTANGULAR_HEIGHT_ELEMENT
							* Math.tan(alpha * Math.PI / 180) / (2 * EXPORT_IMAGE_EQUIRECTANGULAR_LATITUTDE_MAX_TAN));
					setRGBFromTile(i, x + shiftX, shiftAlpha + yAlpha, x,
							EXPORT_IMAGE_EQUIRECTANGULAR_HEIGHT_ELEMENT / 2 + y);
					setRGBFromTile(i, x + shiftX, shiftAlpha - yAlpha, x,
							EXPORT_IMAGE_EQUIRECTANGULAR_HEIGHT_ELEMENT / 2 - y);
				}
				
				// bottom white
				for (int y = EXPORT_IMAGE_EQUIRECTANGULAR_HEIGHT_ELEMENT
						+ shiftY; y < EXPORT_IMAGE_EQUIRECTANGULAR_HEIGHT; y++) {
					setWhite(x + shiftX, y);
				}
	
			}
		}
	}

	/**
	 * creates an export image (and store it in BufferedImage bi)
	 */
	protected final void setExportImage() {
	
		bi = null;
		try {
			// will use screen buffer or offscreen buffer, depending
			// which is currently selected
			int width = right - left;
			int height = top - bottom;
			FloatBuffer buffer = FloatBuffer.allocate(3 * width * height);
			getGL().glReadPixels(0, 0, width, height, GLlocal.GL_RGB,
					GLlocal.GL_FLOAT, buffer);
			float[] pixels = buffer.array();
	
			bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	
			int i = 0;
			for (int y = height - 1; y >= 0; y--)
				for (int x = 0; x < width; x++) {
					int r = (int) (pixels[i] * 255);
					int g = (int) (pixels[i + 1] * 255);
					int b = (int) (pixels[i + 2] * 255);
					bi.setRGB(x, y, ((r << 16) | (g << 8) | b));
					i += 3;
				}
			bi.flush();
		} catch (Exception e) {
			Log.error("setExportImage: " + e.getMessage());
		}
	
	}

	@Override
	public final GBufferedImage getExportImage() {
		return new GBufferedImageD(bi);
	}



	private static final int INT_RGB_WHITE = ((255 << 16) | (255 << 8) | 255);

	@Override
	final public void enableTextures2D() {
		rendererImpl.glEnable(GL.GL_TEXTURE_2D);
	}

	@Override
	final public void disableTextures2D() {
		rendererImpl.glDisable(GL.GL_TEXTURE_2D);
	}

	@Override
	public void updateProjectionObliqueValues() {
		if (type == RendererType.GL2) {
			updateOrthoValues();
		}
		super.updateProjectionObliqueValues();
	}
}

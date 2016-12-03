package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;


import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawLabel3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GBufferedImageD;
import org.geogebra.desktop.geogebra3D.euclidian3D.EuclidianView3DD;
import org.geogebra.desktop.geogebra3D.euclidian3D.opengl.RendererJogl.GLlocal;
import org.geogebra.desktop.gui.menubar.GeoGebraMenuBar;
import org.geogebra.desktop.gui.util.ImageSelection;
import org.geogebra.desktop.util.FrameCollector;

/**
 * openGL renderers for desktop
 * 
 * @author mathieu
 *
 */
public abstract class RendererD extends Renderer implements GLEventListener {

	protected RendererJogl jogl;

	// openGL variables
	protected GLU glu = new GLU();

	private Animator animator;

	/** canvas usable for a JPanel */
	public Component3D canvas;

	/**
	 * constructor
	 * 
	 * @param view
	 *            3D view
	 * @param useCanvas
	 *            true if we want to use Canvas (instead of JPanel)
	 */
	public RendererD(EuclidianView3D view, boolean useCanvas, RendererType type) {
		super(view, type);

		jogl = new RendererJogl();

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
	}

	@Override
	protected void clearColorBuffer() {
		getGL().glClear(GLlocal.GL_COLOR_BUFFER_BIT);
	}

	@Override
	protected void clearDepthBuffer() {
		getGL().glClear(GLlocal.GL_DEPTH_BUFFER_BIT);
	}

	@Override
	protected void setStencilFunc(int value) {
		getGL().glStencilFunc(GLlocal.GL_EQUAL, value, 0xFF);
	}

	@Override
	final protected void exportImage() {


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
				endNeedExportImage();

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
			endNeedExportImage();

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
			endNeedExportImage();
			
			break;

		default:
			if (needExportImage) {
				setExportImage();
				if (!exportImageForThumbnail){
					// call write to file
					((EuclidianView3DD) view3D).writeExportImage();
				}
				endNeedExportImage();
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
		endNeedExportImage();
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// NOTHING TO DO HERE -- NEEDED TO AVOID ERRORS IN INSTALLED/PORTABLE
		// VERSIONS
	}

	/**
	 * init and check if shaders can be used
	 * 
	 * @param drawable
	 *            GL drawable
	 */
	protected void initCheckShaders(GLAutoDrawable drawable) {
		// reset picking
		oldGeoToPickSize = -1;

		// start init
		String glInfo[] = RendererJogl.getGLInfos(drawable);

		Log.debug("Init on " + Thread.currentThread()
				+ "\nChosen GLCapabilities: " + glInfo[0]
				+ "\ndouble buffered: " + glInfo[1] + "\nstereo: " + glInfo[2]
				+ "\nstencil: " + glInfo[3] + "\nINIT GL IS: " + glInfo[4]
				+ "\nGL_VENDOR: " + glInfo[5] + "\nGL_RENDERER: " + glInfo[6]
				+ "\nGL_VERSION: " + glInfo[7]);

		GeoGebraMenuBar.setGlCard(glInfo[6]);
		GeoGebraMenuBar.setGlVersion(glInfo[7]);

		// this is abstract method: don't create old GL / shaders here
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
		
		initFBO();

		init();
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
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {

		setGL(drawable);

		setView(x, y, w, h);
		view3D.reset();

	}

	/**
	 * openGL method called when the display change. empty method
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}

	@Override
	public void enableTextures2D() {
		getGL().glEnable(GL.GL_TEXTURE_2D);
	}

	@Override
	public void disableTextures2D() {
		getGL().glDisable(GL.GL_TEXTURE_2D);
	}

	@Override
	public void genTextures2D(int number, int[] index) {
		getGL().glGenTextures(number, index, 0);
	}

	@Override
	public void bindTexture(int index) {
		getGL().glBindTexture(GL.GL_TEXTURE_2D, index);
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

	/** shift for getting alpha value */
	private static final int ALPHA_SHIFT = 24;

	/**
	 * get alpha channel of the array ARGB description
	 * 
	 * @param pix
	 * @return the alpha channel of the array ARGB description
	 */
	private static byte[] ARGBtoAlpha(DrawLabel3D label, int[] pix) {

		// calculates 2^n dimensions
		int w = firstPowerOfTwoGreaterThan(label.getWidth());
		int h = firstPowerOfTwoGreaterThan(label.getHeight());

		// Application.debug("width="+width+",height="+height+"--w="+w+",h="+h);

		// get alpha channel and extends to 2^n dimensions
		byte[] bytes = new byte[w * h];
		byte b;
		int bytesIndex = 0;
		int pixIndex = 0;
		int xmin = w, xmax = 0, ymin = h, ymax = 0;
		for (int y = 0; y < label.getHeight(); y++) {
			for (int x = 0; x < label.getWidth(); x++) {
				b = (byte) (pix[pixIndex] >> ALPHA_SHIFT);
				if (b != 0) {
					if (x < xmin) {
						xmin = x;
					}
					if (x > xmax) {
						xmax = x;
					}
					if (y < ymin) {
						ymin = y;
					}
					if (y > ymax) {
						ymax = y;
					}

				}
				bytes[bytesIndex] = b;
				bytesIndex++;
				pixIndex++;
			}
			bytesIndex += w - label.getWidth();
		}

		// values for picking (ignore transparent bytes)
		label.setPickingDimension(xmin, ymin, xmax - xmin + 1, ymax - ymin + 1);

		// update width and height
		label.setDimensionPowerOfTwo(w, h);

		return bytes;
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
	private int createAlphaTexture(int textureIndex, boolean waitForReset,
			int sizeX, int sizeY, byte[] buf) {

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
	final public void setGL(GLAutoDrawable gLDrawable) {
		jogl.setGL(gLDrawable);
	}

	@Override
	public void enableCulling() {
		getGL().glEnable(GLlocal.GL_CULL_FACE);
	}

	@Override
	public void disableCulling() {
		getGL().glDisable(GLlocal.GL_CULL_FACE);
	}

	@Override
	public void setCullFaceFront() {
		getGL().glCullFace(GLlocal.GL_FRONT);
	}

	@Override
	public void setCullFaceBack() {
		getGL().glCullFace(GLlocal.GL_BACK);
	}

	@Override
	public void disableBlending() {
		getGL().glDisable(GLlocal.GL_BLEND);
	}

	@Override
	public void enableBlending() {
		getGL().glEnable(GLlocal.GL_BLEND);
	}

	@Override
	final public void enableMultisample() {
		getGL().glEnable(GLlocal.GL_MULTISAMPLE);
	}

	@Override
	final public void disableMultisample() {
		getGL().glDisable(GLlocal.GL_MULTISAMPLE);
	}

	@Override
	public void enableAlphaTest() {
		getGL().glEnable(GLlocal.GL_ALPHA_TEST);
	}

	@Override
	public void disableAlphaTest() {
		getGL().glDisable(GLlocal.GL_ALPHA_TEST);
	}

	@Override
	public void enableDepthMask() {
		getGL().glDepthMask(true);
	}

	@Override
	public void disableDepthMask() {
		getGL().glDepthMask(false);
	}

	@Override
	public void enableDepthTest() {
		getGL().glEnable(GLlocal.GL_DEPTH_TEST);
	}

	@Override
	public void disableDepthTest() {
		getGL().glDisable(GLlocal.GL_DEPTH_TEST);
	}

	@Override
	public void setColorMask(boolean r, boolean g, boolean b, boolean a) {
		getGL().glColorMask(r, g, b, a);
	}

	@Override
	public void setLineWidth(double width) {

		getGL().glLineWidth((float) width);

	}


	@Override
	public void setLayer(float l) {

		// 0<=l<10
		// l2-l1>=1 to see something
		// l=l/3f;
		getGL().glPolygonOffset(-l * 0.05f, -l * 10);

		// getGL().glPolygonOffset(-l*0.75f, -l*0.5f);

		// getGL().glPolygonOffset(-l, 0);
	}

	@Override
	public void setClearColor(float r, float g, float b, float a) {
		getGL().glClearColor(r, g, b, a);
	}

	@Override
	protected void disableStencilLines() {
		getGL().glDisable(GLlocal.GL_STENCIL_TEST);
		waitForDisableStencilLines = false;
	}

	protected FrameCollector gifEncoder;



	@Override
	protected void setGIFEncoder(Object gifEncoder) {
		this.gifEncoder = (FrameCollector) gifEncoder;
	}

	// ////////////////////////////////////
	// EXPORT IMAGE
	// ////////////////////////////////////


	
	
	
	private int fboID, fboColorTextureID, fboDepthTextureID;
	private int fboWidth = 1, fboHeight = 1;
	private int oldRight, oldLeft, oldTop, oldBottom;
	
	@Override
	final protected void selectFBO() {
		
		if (fboID < 0){
			view3D.setFontScale(1);
			return;
		}
		
		updateFBOBuffers();
		
		// bind the buffer
		getGL().glBindFramebuffer(GLlocal.GL_FRAMEBUFFER, fboID);


		// store view values
		oldRight = right; oldLeft = left; oldTop = top; oldBottom = bottom;
		
		// set view values for buffer
		setView(0, 0, fboWidth, fboHeight);
		
	}

	@Override
	final protected void unselectFBO() {
		
		if (fboID < 0){
			return;
		}
		
		// set back the view
		setView(0, 0, oldRight - oldLeft, oldTop - oldBottom);
		
		//unbind the framebuffer ...
		getGL().glBindFramebuffer(GLlocal.GL_FRAMEBUFFER, 0);
	}
	
	
	@Override
	final protected void needExportImage(double scale, int w, int h) {
		
		view3D.setFontScale(scale);
		setExportImageDimension(w, h);

		setNeedExportImage(true);
		display();

	}
	
	@Override
	final protected void setExportImageDimension(int w, int h) {
		fboWidth = w;
		fboHeight = h;

	}

	private void endNeedExportImage(){
		setNeedExportImage(false);

		// set no font scale
		view3D.setFontScale(1);
	}
	
	private void updateFBOBuffers(){

		// image texture
		getGL().glBindTexture(GLlocal.GL_TEXTURE_2D, fboColorTextureID);
		getGL().glTexParameterf(GLlocal.GL_TEXTURE_2D,
				GLlocal.GL_TEXTURE_MAG_FILTER, GLlocal.GL_NEAREST);
		getGL().glTexParameterf(GLlocal.GL_TEXTURE_2D,
				GLlocal.GL_TEXTURE_MIN_FILTER, GLlocal.GL_NEAREST);
		getGL().glTexImage2D(GLlocal.GL_TEXTURE_2D, 0, GLlocal.GL_RGBA,
				fboWidth, fboHeight, 0, GLlocal.GL_RGBA,
				GLlocal.GL_UNSIGNED_BYTE, null);

		getGL().glBindTexture(GLlocal.GL_TEXTURE_2D, 0);
        
        
        // depth buffer
		getGL().glBindRenderbuffer(GLlocal.GL_RENDERBUFFER, fboDepthTextureID);
		getGL().glRenderbufferStorage(GLlocal.GL_RENDERBUFFER,
				GLlocal.GL_DEPTH_COMPONENT, fboWidth, fboHeight);
        
		getGL().glBindRenderbuffer(GLlocal.GL_RENDERBUFFER, 0);
        
        
	}
	
	/**
	 * init frame buffer object for save image
	 */
	final protected void initFBO() {

		try{
			int[] result = new int[1];

			//allocate the colour texture ...
			getGL().glGenTextures(1, result, 0);
			fboColorTextureID = result[0];

			//allocate the depth texture ...
			getGL().glGenRenderbuffers(1, result, 0);
			fboDepthTextureID = result[0];

			updateFBOBuffers();


			//allocate the framebuffer object ...
			getGL().glGenFramebuffers(1, result, 0);
			fboID = result[0];
			getGL().glBindFramebuffer(GLlocal.GL_FRAMEBUFFER, fboID);

			//attach the textures to the framebuffer
			getGL().glFramebufferTexture2D(GLlocal.GL_FRAMEBUFFER,
					GLlocal.GL_COLOR_ATTACHMENT0,GLlocal.GL_TEXTURE_2D,fboColorTextureID,0);
			getGL().glFramebufferRenderbuffer(GLlocal.GL_FRAMEBUFFER,
					GLlocal.GL_DEPTH_ATTACHMENT,GLlocal.GL_RENDERBUFFER,fboDepthTextureID);

			getGL().glBindFramebuffer(GLlocal.GL_FRAMEBUFFER, 0);
			
			// check if frame buffer is complete
			if (getGL().glCheckFramebufferStatus(GLlocal.GL_FRAMEBUFFER) != GLlocal.GL_FRAMEBUFFER_COMPLETE) {
				Log.error("Frame buffer is not complete");
				fboID = -1;
			}
		}catch(Exception e){
			Log.error(e.getMessage());
			fboID = -1;
		}
		

	}

	protected BufferedImage bi;
	
	static private int getUnsigned(byte x){
		return x & 0x000000FF;
//		if (x < 0){
//			return -x + 128;
//		}
//		return x;
	}

	private BufferedImage[] equirectangularTilesLeft,
			equirectangularTilesRight;

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


	static private final int INT_RGB_WHITE = ((255 << 16) | (255 << 8)
			| 255);

	private void setRGBFromTile(int i, int x, int y, int xTile, int yTile) {
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
	final protected void setExportImage() {

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
	final public GBufferedImage getExportImage() {
		return new GBufferedImageD(bi);
	}

	/**
	 * set line width
	 * 
	 * @param width
	 *            width
	 */
	public void setLineWidth(int width) {
		getGL().glLineWidth(width);
	}
	

	@Override
	protected void setBufferLeft(){
		jogl.getGL2().glDrawBuffer(GLlocal.GL_BACK_LEFT); 
		//zspace seems to be swapped
		//jogl.getGL2().glDrawBuffer(GLlocal.GL_BACK_RIGHT); 
	}
	

	@Override
	protected void setBufferRight(){
		jogl.getGL2().glDrawBuffer(GLlocal.GL_BACK_RIGHT); 
		//zspace seems to be swapped
		//jogl.getGL2().glDrawBuffer(GLlocal.GL_BACK_LEFT); 
	}

}

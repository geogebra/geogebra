package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.util.debug.Log;

public abstract class RendererImpl {

	protected EuclidianView3D view3D;
	private int fboWidth = 1;

	private int fboHeight = 1;

	private Object fboID;

	private int fboColorTextureID;

	private Object fboDepthTextureID;

	private int oldRight;

	private int oldLeft;

	private int oldTop;

	private int oldBottom;

	protected Renderer renderer;

	/**
	 * @param renderer
	 *            renderer
	 * @param view
	 *            view
	 */
	public RendererImpl(Renderer renderer, EuclidianView3D view) {
		this.renderer = renderer;
		this.view3D = view;
	}

	/**
	 * @param scale
	 *            image scale
	 * @param w
	 *            pixel width
	 * @param h
	 *            pixel height
	 */
	public void needExportImage(double scale, int w, int h) {

		view3D.setFontScale(scale);
		setExportImageDimension(w, h);

		renderer.setNeedExportImage(true);
		renderer.display();

	}

	/**
	 * @param w
	 *            pixel width
	 * @param h
	 *            pixel height
	 */
	protected void setExportImageDimension(int w, int h) {
		fboWidth = w;
		fboHeight = h;
	}

	/**
	 * Finish image export.
	 */
	public void endNeedExportImage() {
		renderer.setNeedExportImage(false);

		// set no font scale
		view3D.setFontScale(1);
	}

	/**
	 * Select frame buffer object.
	 */
	public void selectFBO() {

		if (fboID == null) {
			view3D.setFontScale(1);
			return;
		}

		updateFBOBuffers();

		// bind the buffer
		bindFramebuffer(fboID);

		// store view values
		oldRight = renderer.getRight();
		oldLeft = renderer.getLeft();
		oldTop = renderer.getTop();
		oldBottom = renderer.getBottom();

		// set view values for buffer
		renderer.setView(0, 0, fboWidth, fboHeight);
	}

	/**
	 * Unselect frame buffer object.
	 */
	public void unselectFBO() {

		if (fboID == null) {
			return;
		}

		// set back the view
		renderer.setView(0, 0, oldRight - oldLeft, oldTop - oldBottom);

		// unbind the framebuffer ...
		unbindFramebuffer();
	}

	/**
	 * Update frame buffer object.
	 */
	public void updateFBOBuffers() {

		// image texture
		bindTexture(fboColorTextureID);
		textureParametersNearest();
		textureImage2DForBuffer(fboWidth, fboHeight);

		bindTexture(0);

		// depth buffer
		bindRenderbuffer(fboDepthTextureID);
		renderbufferStorage(fboWidth, fboHeight);

		unbindRenderbuffer();
	}

	/**
	 * init frame buffer object for save image
	 */
	public final void initFBO() {

		try {

			// allocate the colour texture ...
			int[] result = new int[1];
			genTextures2D(1, result);
			fboColorTextureID = result[0];

			// allocate the depth texture ...
			fboDepthTextureID = genRenderbuffer();

			updateFBOBuffers();

			// allocate the framebuffer object ...
			fboID = genFramebuffer();
			bindFramebuffer(fboID);

			// attach the textures to the framebuffer
			framebuffer(fboColorTextureID, fboDepthTextureID);

			unbindFramebuffer();

			// check if frame buffer is complete
			if (!checkFramebufferStatus()) {
				Log.error("Frame buffer is not complete");
				fboID = null;
			}
		} catch (Exception e) {
			Log.error(e.getMessage());
			fboID = null;
		}
	}

	protected abstract void bindFramebuffer(Object id);

	protected abstract void bindRenderbuffer(Object id);

	protected abstract void unbindFramebuffer();

	protected abstract void unbindRenderbuffer();

	protected abstract void textureParametersNearest();

	protected abstract void textureImage2DForBuffer(int width, int height);

	protected abstract void renderbufferStorage(int width, int height);

	protected abstract Object genRenderbuffer();

	protected abstract Object genFramebuffer();

	protected abstract void framebuffer(Object colorId, Object depthId);

	protected abstract boolean checkFramebufferStatus();

	public Textures getTextures() {
		return renderer.getTextures();
	}

	abstract protected void updateClipPlanes();

	/**
	 * set color mask
	 * 
	 * @param colorMask
	 *            color mask
	 */
	public void setColorMask(final int colorMask) {
		setColorMask(ColorMask.getRed(colorMask), ColorMask.getGreen(colorMask),
				ColorMask.getBlue(colorMask), ColorMask.getAlpha(colorMask));
	}

	/**
	 * Set color mask channels
	 * 
	 * @param r
	 *            red
	 * @param g
	 *            green
	 * @param b
	 *            blue
	 * @param a
	 *            alpha
	 */
	abstract public void setColorMask(boolean r, boolean g, boolean b,
			boolean a);

	/**
	 * clear depth buffer
	 */
	public void clearDepthBuffer() {
		glClear(getGL_DEPTH_BUFFER_BIT());
	}

	/**
	 * clear depth buffer for anaglyph glasses, between first and second eye
	 */
	public void clearDepthBufferForSecondAnaglyphFilter() {
		clearDepthBuffer();
	}

	/**
	 * next geometries have no normal
	 */
	public void setNormalToNone() {
		// used only with shaders
	}

	/**
	 * set dash texture
	 * 
	 * @param i
	 *            texture id
	 */
	abstract public void setDashTexture(int i);

	abstract public void draw(Manager.Type type, int length);

	abstract public void bindBufferForIndices(int buffer);

	abstract public boolean areTexturesEnabled();

	abstract public void loadVertexBuffer(GLBuffer fbVertices, int length);

	abstract public void loadColorBuffer(GLBuffer fbColors, int length);

	abstract public void loadTextureBuffer(GLBuffer fbTextures, int length);

	abstract public void disableTextureBuffer();

	abstract public void loadNormalBuffer(GLBuffer fbNormals, int length);

	abstract public void loadIndicesBuffer(GLBufferIndices arrayI, int length);

	/**
	 * enable flag
	 * 
	 * @param flag
	 *            GL flag
	 */
	public abstract void glEnable(int flag);

	/**
	 * disable flag
	 * 
	 * @param flag
	 *            GL flag
	 */
	public abstract void glDisable(int flag);

	/**
	 * clear buffers corresponding to flag
	 * 
	 * @param flag
	 *            buffers flag
	 */
	abstract public void glClear(int flag);

	// GL flags getters
	abstract public int getGL_BLEND();

	abstract public int getGL_CULL_FACE();

	abstract public int getGL_COLOR_BUFFER_BIT();

	abstract public int getGL_DEPTH_BUFFER_BIT();

	abstract public int getGL_DEPTH_TEST();

	abstract public void disableCulling();

	public abstract void setClipPlanes(double[][] minMax);

	/**
	 * @param r red
	 * @param g green
	 * @param b blue
	 * @param a alpha
	 */
	public abstract void setColor(float r, float g, float b, float a);

	public abstract void initMatrix();

	public abstract void initMatrixForFaceToScreen();

	public abstract void resetMatrix();

	public abstract void updateOrthoValues();

	public abstract void enableTextures();

	public abstract void disableTextures();

	public abstract void enableFading();

	public abstract void enableDash();

	public abstract void enableDashHidden();

	public abstract void setLight(int light);

	public abstract void setColorMaterial();

	public abstract void setLightModel();

	public abstract void setAlphaFunc();

	public abstract void setView();

	public abstract void glViewPort();

	public abstract void viewOrtho();

	public abstract void viewPersp();

	public abstract void viewGlasses();

	public abstract void viewOblique();

	public abstract Manager createManager();

	public abstract float[] getLightPosition();

	public abstract void drawSurfacesOutline();

	public abstract void enableClipPlanes();

	public abstract void disableClipPlanes();

	public abstract void useShaderProgram();

	public abstract void draw();

	public abstract void dispose();

	public abstract void updatePerspValues();

	public abstract void updateGlassesValues();

	public abstract void updateProjectionObliqueValues();

	public abstract void enableTexturesForText();

	public abstract void initRenderingValues();

	public abstract void drawFaceToScreenAbove();

	public abstract void drawFaceToScreenBelow();

	public abstract void enableLightingOnInit();

	public abstract void initCulling();

	public abstract void drawTranspNotCurved();

	public abstract void setCullFaceFront();

	public abstract void setCullFaceBack();

	abstract public void initShaders();

	public abstract void disableShine();

	public abstract void enableShine();

	abstract public void setBufferLeft();

	abstract public void setBufferRight();

	abstract public void setStencilFunc(int value);

	abstract public void enableDepthMask();

	abstract public void disableDepthMask();

	/**
	 * @param r red
	 * @param g green
	 * @param b blue
	 * @param a alpha
	 */
	abstract public void setClearColor(float r, float g, float b, float a);

	abstract public void setPolygonOffset(float factor, float units);

	abstract public void setLayer(int layer);

	abstract public void genTextures2D(int number, int[] index);

	abstract public void bindTexture(int index);

	abstract public void enableAlphaTest();

	abstract public void disableAlphaTest();

	abstract public void enableMultisample();

	abstract public void disableMultisample();

	public abstract void setLabelOrigin(float[] origin);

	/**
	 * @param location 3D coordinates of the currently drawn label
	 */
	public void setLabelLocation(float[] location) {
		// Used for rotation in some shaders
	}

	public abstract void enableLighting();

	public abstract void disableLighting();

	public abstract void initLighting();

	public abstract boolean useShaders();

	/**
	 * Set matrix view from screen matrix.
	 */
	final public void setMatrixView() {
		setMatrixView(renderer.getToScreenMatrix());
	}

	public abstract void setMatrixView(CoordMatrix4x4 matrix);

	public abstract void setProjectionMatrixViewForAR();

	public abstract void unsetMatrixView();

	public abstract void pushSceneMatrix();

	public abstract void setLightPosition(float[] values);

	/**
	 * Sets ambient and diffuse components of lighting.
	 * @param ambient0 ambient component (first source)
	 * @param diffuse0 diffuse component (first source)
	 * @param ambient1 ambient component (second source)
	 * @param diffuse1 diffuse component (second source)
	 */
	public abstract void setLightAmbientDiffuse(float ambient0, float diffuse0,
			float ambient1, float diffuse1);

	/**
	 * create a dummy texture to please the GL shader language, that needs
	 * something correct to be bound on texture 0
	 */
	abstract public void createDummyTexture();

	abstract public void attribPointers();

	/**
	 * draw not hidden parts
	 */
	abstract public void drawNotHidden();

	/**
	 * draw hidden textured parts
	 */
	abstract public void drawHiddenTextured();

	/**
	 * draw hidden not textured parts
	 */
	abstract public void drawHiddenNotTextured();

	/**
	 * draw transparent closed and curved surfaces
	 */
	abstract public void drawTranspClosedCurved();

	/**
	 * draw closed surfaces for hiding
	 */
	abstract public void drawClosedSurfacesForHiding();

	/**
	 * draw clipped surfaces for hiding
	 */
	abstract public void drawClippedSurfacesForHiding();

	/**
	 * draw transparent clipped surfaces
	 */
	abstract public void drawTranspClipped();

	/**
	 * draw surfaces for hiding
	 */
	abstract public void drawSurfacesForHiding();

	/**
	 * draw not transparent surfaces
	 */
	abstract public void drawOpaqueSurfaces();
}
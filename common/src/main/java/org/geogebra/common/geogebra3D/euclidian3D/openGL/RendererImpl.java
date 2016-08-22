package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.util.debug.Log;

public abstract class RendererImpl implements RendererShadersInterface {

	protected EuclidianView3D view3D;

	protected Renderer renderer;

	public RendererImpl(Renderer renderer, EuclidianView3D view) {
		this.renderer = renderer;
		this.view3D = view;
	}

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

	public abstract void setColor(float r, float g, float b, float a);

	public abstract void initMatrix();

	public abstract void initMatrixForFaceToScreen();

	public abstract void resetMatrix();

	public abstract void glLoadName(int loop);

	public abstract void updateOrthoValues();

	public abstract void enableTextures();

	public abstract void disableTextures();

	public abstract void enableFading();

	public abstract void enableDash();

	public abstract void setDashTexture(int index);

	public abstract void setLabelOrigin(float[] origin);

	public abstract void enableLighting();

	public abstract void disableLighting();

	public abstract void initLighting();

	public abstract boolean useShaders();

	public abstract void setMatrixView();

	public abstract void unsetMatrixView();

	public abstract void pushSceneMatrix();

	public abstract void setLightPosition(float[] values);

	public abstract void setLightAmbiantDiffuse(float ambiant0, float diffuse0,
			float ambiant1, float diffuse1);

	public abstract void setLight(int light);

	public abstract void setColorMaterial();

	public abstract void setLightModel();

	public abstract void setAlphaFunc();

	public abstract void setView();

	public abstract void setStencilLines();

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

	abstract public void setColorMask(boolean r, boolean g, boolean b, boolean a);

	abstract public void setClearColor(float r, float g, float b, float a);

	abstract public void setPolygonOffset(float factor, float units);

	abstract public void genTextures2D(int number, int[] index);

	abstract public void bindTexture(int index);

	abstract public void enableAlphaTest();

	abstract public void disableAlphaTest();

	abstract public void enableMultisample();

	abstract public void disableMultisample();

	final public void needExportImage(double scale, int w, int h) {

		view3D.setFontScale(scale);
		setExportImageDimension(w, h);

		renderer.setNeedExportImage(true);
		renderer.display();

	}

	private int fboWidth = 1;

	private int fboHeight = 1;

	private Object fboID;

	private int fboColorTextureID;

	private Object fboDepthTextureID;

	private int oldRight;

	private int oldLeft;

	private int oldTop;

	private int oldBottom;


	final public void setExportImageDimension(int w, int h) {
		fboWidth = w;
		fboHeight = h;

	}

	public void endNeedExportImage() {
		renderer.setNeedExportImage(false);

		// set no font scale
		view3D.setFontScale(1);
	}
	
	public final void selectFBO() {

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

	public final void unselectFBO() {

		if (fboID == null) {
			return;
		}

		// set back the view
		renderer.setView(0, 0, oldRight - oldLeft, oldTop - oldBottom);

		// unbind the framebuffer ...
		unbindFramebuffer();
	}

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


}
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

	/**
	 * @return the textures used by the associated renderer.
	 */
	public Textures getTextures() {
		return renderer.getTextures();
	}

	protected abstract void updateClipPlanes();

	/**
	 * set color mask
	 *
	 * @param colorMask
	 *            color mask
	 */
	public void setColorMask(final int colorMask) {
		setColorMask(
				ColorMask.getRed(colorMask),
				ColorMask.getGreen(colorMask),
				ColorMask.getBlue(colorMask),
				ColorMask.getAlpha(colorMask));
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
	public abstract void setColorMask(boolean r, boolean g, boolean b, boolean a);

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
	public abstract void setDashTexture(int i);

	/**
	 * Draw elements of given type
	 * @param type geometry type
	 * @param length length
	 */
	public abstract void draw(Manager.Type type, int length);

	/**
	 * Bind buffer for indices.
	 * @param buffer buffer index
	 */
	public abstract void bindBufferForIndices(int buffer);

	/**
	 * @return whether textures are enabled
	 */
	public abstract boolean areTexturesEnabled();

	/**
	 * Load vertex buffer.
	 * @param fbVertices vertex buffer
	 * @param length length
	 */
	public abstract void loadVertexBuffer(GLBuffer fbVertices, int length);

	/**
	 * Load color buffer.
	 * @param fbColors color buffer
	 * @param length length
	 */
	public abstract void loadColorBuffer(GLBuffer fbColors, int length);

	/**
	 * Load texture buffer.
	 * @param fbTextures texture buffer
	 * @param length length
	 */
	public abstract void loadTextureBuffer(GLBuffer fbTextures, int length);

	/**
	 * Disable texture buffer.
	 */
	public abstract void disableTextureBuffer();

	/**
	 * Load buffer of normals.
	 * @param fbNormals buffer of normals
	 * @param length length
	 */
	public abstract void loadNormalBuffer(GLBuffer fbNormals, int length);

	/**
	 * Load buffer of indices.
	 * @param arrayI buffer of indices
	 * @param length length
	 */
	public abstract void loadIndicesBuffer(GLBufferIndices arrayI, int length);

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
	public abstract void glClear(int flag);

	// GL flags getters

	/**
	 * @return BLEND constant
	 */
	public abstract int getGL_BLEND();

	/**
	 * @return CULL_FACE constant
	 */
	public abstract int getGL_CULL_FACE();

	/**
	 * @return COLOR_BUFFER_BIT constant
	 */
	public abstract int getGL_COLOR_BUFFER_BIT();

	/**
	 * @return DEPTH_BUFFER_BIT constant
	 */
	public abstract int getGL_DEPTH_BUFFER_BIT();

	/**
	 * @return DEPTH_TEST constant
	 */
	public abstract int getGL_DEPTH_TEST();

	/**
	 * Disable culling.
	 */
	public abstract void disableCulling();

	/**
	 * Set clip panes.
	 * @param minMax clipping ranges in each direction
	 */
	public abstract void setClipPlanes(double[][] minMax);

	/**
	 * @param r red
	 * @param g green
	 * @param b blue
	 * @param a alpha
	 */
	public abstract void setColor(float r, float g, float b, float a);

	/**
	 * Initialize transformation matrix.
	 */
	public abstract void initMatrix();

	/**
	 * Initialize matrix for face to screen.
	 */
	public abstract void initMatrixForFaceToScreen();

	/**
	 * Reset matrix.
	 */
	public abstract void resetMatrix();

	/**
	 * Update orthogonal values.
	 */
	public abstract void updateOrthoValues();

	/**
	 * Enable textures.
	 */
	public abstract void enableTextures();

	/**
	 * Disable textures.
	 */
	public abstract void disableTextures();

	/**
	 * Enable fading.
	 */
	public abstract void enableFading();

	/**
	 * Enable line dash.
	 */
	public abstract void enableDash();

	/**
	 * Enable dash for hidden parts.
	 */
	public abstract void enableDashHidden();

	/**
	 * Enable or disable light.
	 * @param light 0 or 1
	 */
	public abstract void setLight(int light);

	/**
	 * Enable COLOR_MATERIAL.
	 */
	public abstract void setColorMaterial();

	/**
	 * Set light model.
	 */
	public abstract void setLightModel();

	/**
	 * Set alpha function.
	 */
	public abstract void setAlphaFunc();

	/**
	 * Set view.
	 */
	public abstract void setView();

	/**
	 * Set viewport size.
	 */
	public abstract void glViewPort();

	/**
	 * Set orthogonal projection.
	 */
	public abstract void viewOrtho();

	/**
	 * Set perspective projection.
	 */
	public abstract void viewPersp();

	/**
	 * Set glasses projection.
	 */
	public abstract void viewGlasses();

	/**
	 * Set oblique projection.
	 */
	public abstract void viewOblique();

	/**
	 * @return new manager
	 */
	public abstract Manager createManager();

	/**
	 * @return light position
	 */
	public abstract float[] getLightPosition();

	/**
	 * Draw surface outline.
	 */
	public abstract void drawSurfacesOutline();

	/**
	 * Enable clip planes.
	 */
	public abstract void enableClipPlanes();

	/**
	 * Disable clip planes.
	 */
	public abstract void disableClipPlanes();

	/**
	 * Use shader program.
	 */
	public abstract void useShaderProgram();

	/**
	 * Prepare for drawing.
	 */
	public abstract void draw();

	/**
	 * Remove shader programs.
	 */
	public abstract void dispose();

	/**
	 * Update perspective value.
	 */
	public abstract void updatePerspValues();

	/**
	 * Update glasses value.
	 */
	public abstract void updateGlassesValues();

	/**
	 * Update oblique projection value.
	 */
	public abstract void updateProjectionObliqueValues();

	/**
	 * Enable textures for text.
	 */
	public abstract void enableTexturesForText();

	/**
	 * Initialize rendering values.
	 */
	public abstract void initRenderingValues();

	/**
	 * Draw face to screen above.
	 */
	public abstract void drawFaceToScreenAbove();

	/**
	 * Draw face to screen below.
	 */
	public abstract void drawFaceToScreenBelow();

	/**
	 * Enable lighting on initialization.
	 */
	public abstract void enableLightingOnInit();

	/**
	 * Initialize culling.
	 */
	public abstract void initCulling();

	/**
	 * Draw transparent, not curved shapes.
	 */
	public abstract void drawTranspNotCurved();

	/**
	 * Enable culling for front-facing facets.
	 */
	public abstract void setCullFaceFront();

	/**
	 * Enable culling for back-facing facets.
	 */
	public abstract void setCullFaceBack();

	/**
	 * Initialize shaders.
	 */
	public abstract void initShaders();

	/**
	 * Disable shine effect.
	 */
	public abstract void disableShine();

	/**
	 * Enable shine effect.
	 */
	public abstract void enableShine();

	/**
	 * Set color buffer to back left.
	 */
	public abstract void setBufferLeft();

	/**
	 * Set color buffer to back right.
	 */
	public abstract void setBufferRight();

	/**
	 * TODO unused
	 */
	public abstract void setStencilFunc(int value);

	/**
	 * Enable depth mask.
	 */
	public abstract void enableDepthMask();

	/**
	 * Disable depth mask.
	 */
	public abstract void disableDepthMask();

	/**
	 * @param r red
	 * @param g green
	 * @param b blue
	 * @param a alpha
	 */
	public abstract void setClearColor(float r, float g, float b, float a);

	/**
	 * Set polygon offset
	 * @param factor factor
	 * @param units units
	 */
	public abstract void setPolygonOffset(float factor, float units);

	/**
	 * Set layer.
	 * @param layer layer
	 */
	public abstract void setLayer(int layer);

	/**
	 * Generate texture names
	 * @param number number of textures to be generated
	 * @param index output array for names
	 */
	public abstract void genTextures2D(int number, int[] index);

	/**
	 * Bind texture.
	 * @param index index
	 */
	public abstract void bindTexture(int index);

	/**
	 * Enable alpha test.
	 */
	public abstract void enableAlphaTest();

	/**
	 * Disable alpha test.
	 */
	public abstract void disableAlphaTest();

	/**
	 * Enable multi-sample.
	 */
	public abstract void enableMultisample();

	/**
	 * Disable multi-sample.
	 */
	public abstract void disableMultisample();

	/**
	 * Set label origin.
	 * @param origin label origin
	 */
	public abstract void setLabelOrigin(float[] origin);

	/**
	 * @param location 3D coordinates of the currently drawn label
	 */
	public void setLabelLocation(float[] location) {
		// Used for rotation in some shaders
	}

	/**
	 * Enable lighting.
	 */
	public abstract void enableLighting();

	/**
	 * Disable lighting.
	 */
	public abstract void disableLighting();

	/**
	 * Initialize lighting.
	 */
	public abstract void initLighting();

	/**
	 * @return whether shaders are used
	 */
	public abstract boolean useShaders();

	/**
	 * Set matrix view from screen matrix.
	 */
	public final void setMatrixView() {
		setMatrixView(renderer.getToScreenMatrix());
	}

	/**
	 * Set view matrix.
	 * @param matrix view matrix
	 */
	public abstract void setMatrixView(CoordMatrix4x4 matrix);

	/**
	 * Set projection matrix for AR.
	 */
	public abstract void setProjectionMatrixViewForAR();

	/**
	 * Unset view matrix.
	 */
	public abstract void unsetMatrixView();

	/**
	 * Push scene matrix.
	 */
	public abstract void pushSceneMatrix();

	/**
	 * Set light position.
	 * @param values light coordinates
	 */
	public abstract void setLightPosition(float[] values);

	/**
	 * Sets ambient and diffuse components of lighting.
	 * @param ambient0 ambient component (first source)
	 * @param diffuse0 diffuse component (first source)
	 * @param ambient1 ambient component (second source)
	 * @param diffuse1 diffuse component (second source)
	 */
	public abstract void setLightAmbientDiffuse(
			float ambient0, float diffuse0, float ambient1, float diffuse1);

	/**
	 * create a dummy texture to please the GL shader language, that needs
	 * something correct to be bound on texture 0
	 */
	public abstract void createDummyTexture();

	/**
	 * attribute vertex pointers
	 */
	public abstract void attribPointers();

	/**
	 * draw not hidden parts
	 */
	public abstract void drawNotHidden();

	/**
	 * draw hidden textured parts
	 */
	public abstract void drawHiddenTextured();

	/**
	 * draw hidden not textured parts
	 */
	public abstract void drawHiddenNotTextured();

	/**
	 * draw transparent closed and curved surfaces
	 */
	public abstract void drawTranspClosedCurved();

	/**
	 * draw closed surfaces for hiding
	 */
	public abstract void drawClosedSurfacesForHiding();

	/**
	 * draw clipped surfaces for hiding
	 */
	public abstract void drawClippedSurfacesForHiding();

	/**
	 * draw transparent clipped surfaces
	 */
	public abstract void drawTranspClipped();

	/**
	 * draw surfaces for hiding
	 */
	public abstract void drawSurfacesForHiding();

	/**
	 * draw not transparent surfaces
	 */
	public abstract void drawOpaqueSurfaces();
}

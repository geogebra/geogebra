package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * Renderer using "implementation" for java/web/android/etc.
 * 
 * @author mathieu
 *
 */
public abstract class RendererWithImpl extends Renderer implements
		RendererShadersInterface {

	protected RendererImpl rendererImpl;

	/**
	 * basic constructor
	 * 
	 * @param view
	 *            3D view
	 * @param type
	 *            GL2/SHADER
	 */
	public RendererWithImpl(EuclidianView3D view, RendererType type) {
		super(view, type);
	}

	@Override
	public void setClipPlanes(double[][] minMax) {
		rendererImpl.setClipPlanes(minMax);
	}

	@Override
	protected void setMatrixView() {
		rendererImpl.setMatrixView();
	}

	@Override
	protected void unsetMatrixView() {
		rendererImpl.unsetMatrixView();
	}

	@Override
	public void setColor(float r, float g, float b, float a) {
		rendererImpl.setColor(r, g, b, a);
	}

	@Override
	public void initMatrix() {
		rendererImpl.initMatrix();
	}

	@Override
	public void initMatrixForFaceToScreen() {
		rendererImpl.initMatrixForFaceToScreen();
	}

	@Override
	public void resetMatrix() {
		rendererImpl.resetMatrix();
	}

	@Override
	protected void setGLForPicking() {
		// not used anymore
	}

	@Override
	protected void pushSceneMatrix() {
		rendererImpl.pushSceneMatrix();
	}

	@Override
	public void glLoadName(int loop) {
		rendererImpl.glLoadName(loop);
	}

	@Override
	protected void setLightPosition(float[] values) {
		rendererImpl.setLightPosition(values);
	}

	@Override
	protected void setLightAmbiantDiffuse(float ambiant0, float diffuse0,
			float ambiant1, float diffuse1) {

		rendererImpl.setLightAmbiantDiffuse(ambiant0, diffuse0, ambiant1,
				diffuse1);
	}

	@Override
	protected void setLight(int light) {
		rendererImpl.setLight(light);
	}

	@Override
	protected void setColorMaterial() {
		rendererImpl.setColorMaterial();
	}

	@Override
	protected void setLightModel() {
		rendererImpl.setLightModel();
	}

	@Override
	protected void setAlphaFunc() {
		rendererImpl.setAlphaFunc();
	}

	@Override
	protected void setView() {
		rendererImpl.setView();
	}

	@Override
	protected void setStencilLines() {
		rendererImpl.setStencilLines();
	}

	@Override
	protected void viewOrtho() {
		rendererImpl.viewOrtho();
	}

	@Override
	protected void viewPersp() {
		rendererImpl.viewPersp();
	}

	@Override
	protected void viewGlasses() {
		rendererImpl.viewGlasses();
	}

	@Override
	protected void viewOblique() {
		rendererImpl.viewOblique();
	}

	@Override
	protected Manager createManager() {
		return rendererImpl.createManager();
	}

	@Override
	final public void enableTextures() {
		rendererImpl.enableTextures();
	}

	@Override
	final public void disableTextures() {
		rendererImpl.disableTextures();

	}

	/**
	 * set line width
	 * 
	 * @param width
	 *            width
	 */
	public void setLineWidth(int width) {
		rendererImpl.setLineWidth(width);
	}

	@Override
	protected void doPick() {
		// no need here
	}

	@Override
	public boolean useLogicalPicking() {
		return true;
	}

	@Override
	protected void useShaderProgram() {
		rendererImpl.useShaderProgram();
	}

	@Override
	protected void draw() {
		rendererImpl.draw();
		super.draw();
	}

	@Override
	protected void updatePerspValues() {

		super.updatePerspValues();
		if (rendererImpl != null) {
			rendererImpl.updatePerspValues();
		}

	}

	@Override
	public void updateGlassesValues() {
		super.updateGlassesValues();

		if (rendererImpl != null) {
			rendererImpl.updateGlassesValues();
		}

	}

	@Override
	public void updateProjectionObliqueValues() {
		super.updateProjectionObliqueValues();
		if (rendererImpl != null) {
			rendererImpl.updateProjectionObliqueValues();
		}

	}

	@Override
	final public void updateOrthoValues() {
		if (rendererImpl != null) {
			rendererImpl.updateOrthoValues();
		}
	}

	@Override
	final public void enableTexturesForText() {
		super.enableTexturesForText();
		rendererImpl.enableTexturesForText();
	}

	@Override
	protected void initRenderingValues() {
		super.initRenderingValues();
		rendererImpl.initRenderingValues();
	}

	@Override
	protected void drawFaceToScreen() {
		rendererImpl.drawFaceToScreenAbove();
		super.drawFaceToScreen();
		rendererImpl.drawFaceToScreenBelow();
	}

	@Override
	protected void drawFaceToScreenEnd() {
		rendererImpl.drawFaceToScreenAbove();
		super.drawFaceToScreenEnd();
		rendererImpl.drawFaceToScreenBelow();
	}

	@Override
	protected void enableLightingOnInit() {
		rendererImpl.enableLightingOnInit();
	}

	@Override
	protected void initCulling() {
		rendererImpl.initCulling();
	}

	@Override
	protected void drawTranspNotCurved() {
		rendererImpl.drawTranspNotCurved();
	}

	@Override
	public void disableCulling() {
		rendererImpl.disableCulling();
	}

	@Override
	public void setCullFaceFront() {
		rendererImpl.setCullFaceFront();
	}

	@Override
	public void setCullFaceBack() {
		rendererImpl.setCullFaceBack();
	}

	@Override
	public void loadColorBuffer(GLBuffer fbColors, int length) {
		rendererImpl.loadColorBuffer(fbColors, length);

	}

	@Override
	public void loadNormalBuffer(GLBuffer fbNormals, int length) {
		rendererImpl.loadNormalBuffer(fbNormals, length);

	}

	@Override
	public void loadTextureBuffer(GLBuffer fbTextures, int length) {
		rendererImpl.loadTextureBuffer(fbTextures, length);

	}

	@Override
	public void loadVertexBuffer(GLBuffer fbVertices, int length) {
		rendererImpl.loadVertexBuffer(fbVertices, length);

	}

	@Override
	public void loadIndicesBuffer(GLBufferIndices arrayI, int length) {
		rendererImpl.loadIndicesBuffer(arrayI, length);

	}

	@Override
	public void setCenter(Coords center) {
		rendererImpl.setCenter(center);

	}

	@Override
	public void resetCenter() {
		rendererImpl.resetCenter();
	}

	@Override
	public boolean areTexturesEnabled() {
		return rendererImpl.areTexturesEnabled();
	}

	@Override
	public void draw(Type type, int length) {
		rendererImpl.draw(type, length);

	}

	@Override
	public void storeBuffer(GLBuffer fb, int length, int size,
			GPUBuffer buffers, int attrib) {
		rendererImpl.storeBuffer(fb, length, size, buffers, attrib);

	}

	@Override
	public void storeElementBuffer(short[] fb, int length, GPUBuffer buffers) {
		rendererImpl.storeElementBuffer(fb, length, buffers);

	}

	@Override
	public void bindBufferForIndices(GPUBuffer buffer) {
		rendererImpl.bindBufferForIndices(buffer);

	}

	@Override
	public void createArrayBuffer(GPUBuffer buffer) {
		rendererImpl.createArrayBuffer(buffer);

	}

	@Override
	public void createElementBuffer(GPUBuffer buffer) {
		rendererImpl.createElementBuffer(buffer);

	}

	@Override
	public void removeArrayBuffer(GPUBuffer buffer) {
		rendererImpl.removeArrayBuffer(buffer);

	}

	@Override
	public void removeElementBuffer(GPUBuffer buffer) {
		rendererImpl.removeElementBuffer(buffer);

	}

	@Override
	public void bindBufferForVertices(GPUBuffer buffer, int size) {
		rendererImpl.bindBufferForVertices(buffer, size);

	}

	@Override
	public void bindBufferForColors(GPUBuffer buffer, int size,
			GLBuffer fbColors) {
		rendererImpl.bindBufferForColors(buffer, size, fbColors);

	}

	@Override
	public void bindBufferForNormals(GPUBuffer buffer, int size,
			GLBuffer fbNormals) {
		rendererImpl.bindBufferForNormals(buffer, size, fbNormals);

	}

	@Override
	public void bindBufferForTextures(GPUBuffer buffer, int size,
			GLBuffer fbTextures) {
		rendererImpl.bindBufferForTextures(buffer, size, fbTextures);

	}

	@Override
	protected void initShaders() {
		rendererImpl.initShaders();
	}

	@Override
	public void disableShine() {
		rendererImpl.disableShine();
	}

	@Override
	public void enableShine() {
		rendererImpl.enableShine();
	}

	@Override
	protected void setBufferLeft() {
		rendererImpl.setBufferLeft();
	}

	@Override
	protected void setBufferRight() {
		rendererImpl.setBufferRight();
	}

	@Override
	protected void clearColorBuffer() {
		rendererImpl.clearColorBuffer();
	}

	@Override
	protected void clearDepthBuffer() {
		rendererImpl.clearDepthBuffer();
	}

	@Override
	protected void setStencilFunc(int value) {
		rendererImpl.setStencilFunc(value);
	}

	@Override
	public void enableCulling() {
		rendererImpl.enableCulling();
	}

	@Override
	public void disableBlending() {
		rendererImpl.disableBlending();
	}

	@Override
	public void enableBlending() {
		rendererImpl.enableBlending();
	}

	@Override
	public final void enableMultisample() {
		rendererImpl.enableMultisample();
	}

	@Override
	public final void disableMultisample() {
		rendererImpl.disableMultisample();
	}

	@Override
	public void enableAlphaTest() {
		rendererImpl.enableAlphaTest();
	}

	@Override
	public void disableAlphaTest() {
		rendererImpl.disableAlphaTest();
	}

	@Override
	public void enableDepthMask() {
		rendererImpl.enableDepthMask();
	}

	@Override
	public void disableDepthMask() {
		rendererImpl.disableDepthMask();
	}

	@Override
	public void enableDepthTest() {
		rendererImpl.enableDepthTest();
	}

	@Override
	public void disableDepthTest() {
		rendererImpl.disableDepthTest();
	}

	@Override
	public void setColorMask(boolean r, boolean g, boolean b, boolean a) {
		rendererImpl.setColorMask(r, g, b, a);
	}

	@Override
	public void setClearColor(float r, float g, float b, float a) {
		rendererImpl.setClearColor(r, g, b, a);
	}

	@Override
	public void setLayer(float l) {

		// 0<=l<10
		// l2-l1>=1 to see something
		// l=l/3f;
		// getGL().glPolygonOffset(-l * 0.05f, -l * 10);
		rendererImpl.setPolygonOffset(-l * 0.05f, -l * 10);
		// getGL().glPolygonOffset(-l*0.75f, -l*0.5f);

		// getGL().glPolygonOffset(-l, 0);
	}

	@Override
	public void enableTextures2D() {
		rendererImpl.enableTextures2D();
	}

	@Override
	public void disableTextures2D() {
		rendererImpl.disableTextures2D();
	}

	@Override
	public void genTextures2D(int number, int[] index) {
		rendererImpl.genTextures2D(number, index);
	}

	@Override
	public void bindTexture(int index) {
		rendererImpl.bindTexture(index);
	}

	@Override
	protected void enableClipPlanes() {
		rendererImpl.enableClipPlanes();
	}

	@Override
	protected void disableClipPlanes() {
		rendererImpl.disableClipPlanes();
	}

	@Override
	public void setLabelOrigin(Coords origin) {
		rendererImpl.setLabelOrigin(origin);
	}

	@Override
	public void enableLighting() {
		rendererImpl.enableLighting();
	}

	@Override
	public void disableLighting() {
		rendererImpl.disableLighting();
	}

	@Override
	public void initLighting() {
		rendererImpl.initLighting();
	}

	@Override
	public boolean useShaders() {
		return rendererImpl.useShaders();
	}


	@Override
	public void enableFading() {
		rendererImpl.enableFading();
	}

	@Override
	public void enableDash() {
		rendererImpl.enableDash();
	}

	@Override
	protected float[] getLightPosition() {
		return rendererImpl.getLightPosition();
	}

	@Override
	public void setDashTexture(int index) {
		rendererImpl.setDashTexture(index);
	}

	@Override
	protected void drawSurfacesOutline() {
		rendererImpl.drawSurfacesOutline();
	}
}

package org.geogebra.web.geogebra3D.web.euclidian3DnoWebGL;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawLabel3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.RendererW;

/**
 * (dummy) renderer for 3D view, for browsers that don't support webGL
 * 
 * @author mathieu
 *
 */
public class RendererWnoWebGL extends RendererW {

	/**
	 * constructor
	 * 
	 * @param view
	 *            3D view
	 */
	public RendererWnoWebGL(EuclidianView3DW view) {
		super(view);
	}

	@Override
	protected void createGLContext(boolean preserveDrawingBuffer) {
		// no webGL context here...
	}

	@Override
	protected void setGLViewPort(int w, int h) {
		// no webGL context here...
	}

	@Override
	protected void start() {
		// nothing to start here
	}

	@Override
	public void initShaders() {
		// no webGL context here...
	}

	@Override
	public void drawScene() {
		// no webGL context here...
	}

	@Override
	protected void draw() {
		// no webGL context here...
	}

	@Override
	protected void clearColorBuffer() {
		// no webGL context here...
	}

	@Override
	protected void clearDepthBuffer() {
		// no webGL context here...
	}

	@Override
	public void enableCulling() {
		// no webGL context here...
	}

	@Override
	public void disableCulling() {
		// no webGL context here...
	}

	@Override
	public void setCullFaceFront() {
		// no webGL context here...
	}

	@Override
	public void setCullFaceBack() {
		// no webGL context here...
	}

	@Override
	public void disableBlending() {
		// no webGL context here...
	}

	@Override
	public void enableBlending() {
		// no webGL context here...
	}

	@Override
	protected void setMatrixView() {
		// no webGL context here...
	}

	@Override
	public void enableDepthMask() {
		// no webGL context here...
	}

	@Override
	public void disableDepthMask() {
		// no webGL context here...
	}

	@Override
	public void enableDepthTest() {
		// no webGL context here...
	}

	@Override
	public void disableDepthTest() {
		// no webGL context here...
	}

	@Override
	public void setColor(float r, float g, float b, float a) {
		// no webGL context here...
	}

	@Override
	public void setLayer(float l) {

		// no webGL context here...
	}

	@Override
	public void initMatrix() {
		// no webGL context here...
	}

	@Override
	public void setColorMask(boolean r, boolean g, boolean b, boolean a) {
		// no webGL context here...
	}

	@Override
	protected void setLightPosition(float[] values) {
		// no webGL context here...
	}

	@Override
	protected void setLight(int light) {

		// no webGL context here...
	}

	@Override
	public void setClearColor(float r, float g, float b, float a) {
		// no webGL context here...
	}

	@Override
	protected void setView() {

		// no webGL context here...
	}

	@Override
	public void genTextures2D(int number, int[] index) {
		// no webGL context here...
	}

	@Override
	public void bindTexture(int index) {
		// no webGL context here...
	}

	@Override
	public void createAlphaTexture(DrawLabel3D label, GBufferedImage bimg) {
		// no webGL context here...
	}

	@Override
	public void textureImage2D(int sizeX, int sizeY, byte[] buf) {
		// no webGL context here...
	}

	@Override
	public void setTextureLinear() {
		// no webGL context here...
	}

	@Override
	public void setTextureNearest() {

		// no webGL context here...
	}

	@Override
	public void loadColorBuffer(GLBuffer fbColors, int length) {
		// no webGL context here...
	}

	@Override
	public void loadNormalBuffer(GLBuffer fbNormals, int length) {
		// no webGL context here...
	}

	@Override
	public void loadTextureBuffer(GLBuffer fbTextures, int length) {
		// no webGL context here...
	}

	@Override
	public void loadVertexBuffer(GLBuffer fbVertices, int length) {
		// no webGL context here...
	}

	@Override
	public void draw(Type type, int length) {
		// no webGL context here...
	}

	@Override
	protected void setDepthFunc() {
		// no webGL context here...
	}

	@Override
	protected void enablePolygonOffsetFill() {
		// no webGL context here...
	}

	@Override
	protected void setBlendFunc() {
		// no webGL context here...
	}

	@Override
	final public void disableTextures() {
		// no webGL context here...
	}

	@Override
	protected void setCurrentTextureType(int type) {
		// no webGL context here...
	}

	@Override
	public void enableLighting() {
		// no webGL context here...
	}

	@Override
	public void initLighting() {
		// no webGL context here...
	}

	@Override
	public void disableLighting() {
		// no webGL context here...
	}

	@Override
	protected void enableClipPlanes() {
		// no webGL context here...
	}

	@Override
	protected void disableClipPlanes() {
		// no webGL context here...
	}

	@Override
	public void setLabelOrigin(Coords origin) {
		// no webGL context here...
	}

}

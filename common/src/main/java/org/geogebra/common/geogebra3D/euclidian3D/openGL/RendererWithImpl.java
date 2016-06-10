package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D.IntersectionCurve;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.HittingSphere;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawLabel3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;

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

		if (((EuclidianController3D) view3D.getEuclidianController())
				.useInputDepthForHitting()) {
			hitting = new HittingSphere(view3D);
		} else {
			hitting = new Hitting(view3D);
		}
	}

	@Override
	final public void setClipPlanes(double[][] minMax) {
		if (rendererImpl != null) {
			rendererImpl.setClipPlanes(minMax);
		}
	}

	@Override
	final protected void setMatrixView() {
		rendererImpl.setMatrixView();
	}

	@Override
	final protected void unsetMatrixView() {
		rendererImpl.unsetMatrixView();
	}

	@Override
	final public void setColor(float r, float g, float b, float a) {
		rendererImpl.setColor(r, g, b, a);
	}

	@Override
	final public void initMatrix() {
		rendererImpl.initMatrix();
	}

	@Override
	final public void initMatrixForFaceToScreen() {
		rendererImpl.initMatrixForFaceToScreen();
	}

	@Override
	final public void resetMatrix() {
		rendererImpl.resetMatrix();
	}

	@Override
	final protected void setGLForPicking() {
		// not used anymore
	}

	@Override
	final protected void pushSceneMatrix() {
		rendererImpl.pushSceneMatrix();
	}

	@Override
	final public void glLoadName(int loop) {
		rendererImpl.glLoadName(loop);
	}

	@Override
	final protected void setLightPosition(float[] values) {
		rendererImpl.setLightPosition(values);
	}

	@Override
	final protected void setLightAmbiantDiffuse(float ambiant0, float diffuse0,
			float ambiant1, float diffuse1) {

		rendererImpl.setLightAmbiantDiffuse(ambiant0, diffuse0, ambiant1,
				diffuse1);
	}

	@Override
	final protected void setLight(int light) {
		rendererImpl.setLight(light);
	}

	@Override
	final protected void setColorMaterial() {
		rendererImpl.setColorMaterial();
	}

	@Override
	final protected void setLightModel() {
		rendererImpl.setLightModel();
	}

	@Override
	final protected void setAlphaFunc() {
		rendererImpl.setAlphaFunc();
	}

	@Override
	final protected void setView() {
		rendererImpl.setView();
	}

	@Override
	final protected void setStencilLines() {
		rendererImpl.setStencilLines();
	}

	@Override
	final protected void viewOrtho() {
		rendererImpl.viewOrtho();
	}

	@Override
	final protected void viewPersp() {
		rendererImpl.viewPersp();
	}

	@Override
	final protected void viewGlasses() {
		rendererImpl.viewGlasses();
	}

	@Override
	final protected void viewOblique() {
		rendererImpl.viewOblique();
	}

	@Override
	final protected Manager createManager() {
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


	@Override
	final protected void doPick() {
		// no need here
	}

	@Override
	final public boolean useLogicalPicking() {
		return true;
	}

	@Override
	final protected void useShaderProgram() {
		rendererImpl.useShaderProgram();
	}

	@Override
	final protected void draw() {
		rendererImpl.draw();
		super.draw();
	}

	@Override
	final protected void updatePerspValues() {

		super.updatePerspValues();
		if (rendererImpl != null) {
			rendererImpl.updatePerspValues();
		}

	}

	@Override
	final public void updateGlassesValues() {
		super.updateGlassesValues();

		if (rendererImpl != null) {
			rendererImpl.updateGlassesValues();
		}

	}

	@Override
	final public void updateProjectionObliqueValues() {
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
	final protected void initRenderingValues() {
		super.initRenderingValues();
		rendererImpl.initRenderingValues();
	}

	@Override
	final protected void drawFaceToScreen() {
		rendererImpl.drawFaceToScreenAbove();
		super.drawFaceToScreen();
		rendererImpl.drawFaceToScreenBelow();
	}

	@Override
	final protected void drawFaceToScreenEnd() {
		rendererImpl.drawFaceToScreenAbove();
		super.drawFaceToScreenEnd();
		rendererImpl.drawFaceToScreenBelow();
	}

	@Override
	final protected void enableLightingOnInit() {
		rendererImpl.enableLightingOnInit();
	}

	@Override
	final protected void initCulling() {
		rendererImpl.initCulling();
	}

	@Override
	final protected void drawTranspNotCurved() {
		rendererImpl.drawTranspNotCurved();
	}

	@Override
	final public void disableCulling() {
		rendererImpl.disableCulling();
	}

	@Override
	final public void setCullFaceFront() {
		rendererImpl.setCullFaceFront();
	}

	@Override
	final public void setCullFaceBack() {
		rendererImpl.setCullFaceBack();
	}

	@Override
	final public void loadColorBuffer(GLBuffer fbColors, int length) {
		rendererImpl.loadColorBuffer(fbColors, length);

	}

	@Override
	final public void loadNormalBuffer(GLBuffer fbNormals, int length) {
		rendererImpl.loadNormalBuffer(fbNormals, length);

	}

	@Override
	final public void loadTextureBuffer(GLBuffer fbTextures, int length) {
		rendererImpl.loadTextureBuffer(fbTextures, length);

	}

	@Override
	final public void loadVertexBuffer(GLBuffer fbVertices, int length) {
		rendererImpl.loadVertexBuffer(fbVertices, length);

	}

	@Override
	final public void loadIndicesBuffer(GLBufferIndices arrayI, int length) {
		rendererImpl.loadIndicesBuffer(arrayI, length);

	}

	@Override
	final public void setCenter(Coords center) {
		rendererImpl.setCenter(center);

	}

	@Override
	final public void resetCenter() {
		rendererImpl.resetCenter();
	}

	@Override
	final public boolean areTexturesEnabled() {
		return rendererImpl.areTexturesEnabled();
	}

	@Override
	final public void draw(Type type, int length) {
		rendererImpl.draw(type, length);

	}

	@Override
	final public void storeBuffer(GLBuffer fb, int length, int size,
			GPUBuffer buffers, int attrib) {
		rendererImpl.storeBuffer(fb, length, size, buffers, attrib);

	}

	@Override
	final public void storeElementBuffer(short[] fb, int length,
			GPUBuffer buffers) {
		rendererImpl.storeElementBuffer(fb, length, buffers);

	}

	@Override
	final public void bindBufferForIndices(GPUBuffer buffer) {
		rendererImpl.bindBufferForIndices(buffer);

	}

	@Override
	final public void createArrayBuffer(GPUBuffer buffer) {
		rendererImpl.createArrayBuffer(buffer);

	}

	@Override
	final public void createElementBuffer(GPUBuffer buffer) {
		rendererImpl.createElementBuffer(buffer);

	}

	@Override
	final public void removeArrayBuffer(GPUBuffer buffer) {
		rendererImpl.removeArrayBuffer(buffer);

	}

	@Override
	final public void removeElementBuffer(GPUBuffer buffer) {
		rendererImpl.removeElementBuffer(buffer);

	}

	@Override
	final public void bindBufferForVertices(GPUBuffer buffer, int size) {
		rendererImpl.bindBufferForVertices(buffer, size);

	}

	@Override
	final public void bindBufferForColors(GPUBuffer buffer, int size,
			GLBuffer fbColors) {
		rendererImpl.bindBufferForColors(buffer, size, fbColors);

	}

	@Override
	final public void bindBufferForNormals(GPUBuffer buffer, int size,
			GLBuffer fbNormals) {
		rendererImpl.bindBufferForNormals(buffer, size, fbNormals);

	}

	@Override
	final public void bindBufferForTextures(GPUBuffer buffer, int size,
			GLBuffer fbTextures) {
		rendererImpl.bindBufferForTextures(buffer, size, fbTextures);

	}

	@Override
	final protected void initShaders() {
		rendererImpl.initShaders();
	}

	@Override
	final public void disableShine() {
		rendererImpl.disableShine();
	}

	@Override
	final public void enableShine() {
		rendererImpl.enableShine();
	}

	@Override
	final protected void setBufferLeft() {
		rendererImpl.setBufferLeft();
	}

	@Override
	final protected void setBufferRight() {
		rendererImpl.setBufferRight();
	}

	@Override
	final protected void clearColorBuffer() {
		rendererImpl.glClear(rendererImpl.getGL_COLOR_BUFFER_BIT());
	}

	@Override
	final protected void clearDepthBuffer() {
		rendererImpl.glClear(rendererImpl.getGL_DEPTH_BUFFER_BIT());
	}

	@Override
	final protected void setStencilFunc(int value) {
		rendererImpl.setStencilFunc(value);
	}

	@Override
	final public void enableCulling() {
		rendererImpl.glEnable(rendererImpl.getGL_CULL_FACE());
	}

	@Override
	final public void disableBlending() {
		rendererImpl.glDisable(rendererImpl.getGL_BLEND());
	}

	@Override
	final public void enableBlending() {
		rendererImpl.glEnable(rendererImpl.getGL_BLEND());
	}

	@Override
	final public void enableMultisample() {
		rendererImpl.enableMultisample();
	}

	@Override
	public final void disableMultisample() {
		rendererImpl.disableMultisample();
	}

	@Override
	final public void enableAlphaTest() {
		rendererImpl.enableAlphaTest();
	}

	@Override
	final public void disableAlphaTest() {
		rendererImpl.disableAlphaTest();
	}

	@Override
	final public void enableDepthMask() {
		rendererImpl.enableDepthMask();
	}

	@Override
	final public void disableDepthMask() {
		rendererImpl.disableDepthMask();
	}

	@Override
	final public void enableDepthTest() {
		rendererImpl.glEnable(rendererImpl.getGL_DEPTH_TEST());
	}

	@Override
	final public void disableDepthTest() {
		rendererImpl.glDisable(rendererImpl.getGL_DEPTH_TEST());
	}

	@Override
	final public void setColorMask(boolean r, boolean g, boolean b, boolean a) {
		rendererImpl.setColorMask(r, g, b, a);
	}

	@Override
	final public void setClearColor(float r, float g, float b, float a) {
		rendererImpl.setClearColor(r, g, b, a);
	}

	@Override
	final public void setLayer(float l) {

		// 0<=l<10
		// l2-l1>=1 to see something
		// l=l/3f;
		// getGL().glPolygonOffset(-l * 0.05f, -l * 10);
		rendererImpl.setPolygonOffset(-l * 0.05f, -l * 10);
		// getGL().glPolygonOffset(-l*0.75f, -l*0.5f);

		// getGL().glPolygonOffset(-l, 0);
	}


	@Override
	final public void genTextures2D(int number, int[] index) {
		rendererImpl.genTextures2D(number, index);
	}

	@Override
	final public void bindTexture(int index) {
		rendererImpl.bindTexture(index);
	}

	@Override
	final protected void enableClipPlanes() {
		rendererImpl.enableClipPlanes();
	}

	@Override
	final protected void disableClipPlanes() {
		rendererImpl.disableClipPlanes();
	}

	@Override
	final public void setLabelOrigin(float[] origin) {
		rendererImpl.setLabelOrigin(origin);
	}

	@Override
	final public void enableLighting() {
		rendererImpl.enableLighting();
	}

	@Override
	final public void disableLighting() {
		rendererImpl.disableLighting();
	}

	@Override
	final public void initLighting() {
		rendererImpl.initLighting();
	}

	@Override
	final public boolean useShaders() {
		return rendererImpl.useShaders();
	}


	@Override
	final public void enableFading() {
		rendererImpl.enableFading();
	}

	@Override
	final public void enableDash() {
		rendererImpl.enableDash();
	}

	@Override
	final protected float[] getLightPosition() {
		return rendererImpl.getLightPosition();
	}

	@Override
	final public void setDashTexture(int index) {
		rendererImpl.setDashTexture(index);
	}

	@Override
	final protected void drawSurfacesOutline() {
		rendererImpl.drawSurfacesOutline();
	}

	@Override
	final public void setHits(GPoint mouseLoc, int threshold) {

		if (mouseLoc == null) {
			return;
		}

		hitting.setHits(mouseLoc, threshold);

	}

	private Hitting hitting;

	@Override
	final public Hitting getHitting() {
		return hitting;
	}

	@Override
	final public GeoElement getLabelHit(GPoint mouseLoc) {
		if (mouseLoc == null) {
			return null;
		}

		return hitting.getLabelHit(mouseLoc);
	}

	@Override
	final public void pickIntersectionCurves() {

		ArrayList<IntersectionCurve> curves = ((EuclidianController3D) view3D
				.getEuclidianController()).getIntersectionCurves();

		// picking objects
		for (IntersectionCurve intersectionCurve : curves) {
			Drawable3D d = intersectionCurve.drawable;
			d.updateForHitting(); // we may need an update
			if (!d.hit(hitting)
					|| d.getPickingType() != PickingType.POINT_OR_CURVE) { // we
																			// assume
																			// that
																			// hitting
																			// infos
																			// are
																			// updated
																			// from
																			// last
																			// mouse
																			// move
				d.setZPick(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
			}

		}

	}

	/** shift for getting alpha value */
	private static final int ALPHA_SHIFT = 24;

	/**
	 * get alpha channel of the array ARGB description
	 * 
	 * @param pix
	 * @return the alpha channel of the array ARGB description
	 */
 	protected static byte[] ARGBtoAlpha(DrawLabel3D label, int[] pix) {
		return ARGBtoAlpha(label, label.getWidth(), label.getHeight(), pix);
	}

	protected static byte[] ARGBtoAlpha(DrawLabel3D label, int labelWidthRes, int labelHeightRes, int[] pix) {

		// calculates 2^n dimensions
		int w = firstPowerOfTwoGreaterThan(labelWidthRes);
		int h = firstPowerOfTwoGreaterThan(labelHeightRes);

		// Application.debug("width="+width+",height="+height+"--w="+w+",h="+h);

		// get alpha channel and extends to 2^n dimensions
		byte[] bytes = new byte[w * h];
		byte b;
		int bytesIndex = 0;
		int pixIndex = 0;
		int xmin = w, xmax = 0, ymin = h, ymax = 0;
		for (int y = 0; y < labelHeightRes; y++) {
			for (int x = 0; x < labelWidthRes; x++) {
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
			bytesIndex += w - labelWidthRes;
		}

		// values for picking (ignore transparent bytes)
		label.setPickingDimension(xmin, ymin, xmax - xmin + 1, ymax - ymin + 1);

		// update width and height
		label.setDimensionPowerOfTwo(w, h);

		return bytes;
	}


	@Override
	protected void enableNormalNormalized() {
		// only need for non-shader renderers
	}

	// //////////////////////////////////////////////////
	// TODO implement methods below for export image (see
	// RendererCheckGLVersionD)

	@Override
	public void display() {
		// used in desktop and for export image
	}

	@Override
	protected void exportImageEquirectangular() {

	}

	@Override
	protected void initExportImageEquirectangularTiles() {

	}

	@Override
	protected void setExportImageEquirectangularTileLeft(int i) {

	}

	@Override
	protected void setExportImageEquirectangularTileRight(int i) {

	}

	@Override
	protected void setExportImageEquirectangularFromTiles() {

	}

	@Override
	protected void needExportImage(double scale, int w, int h) {

	}

	@Override
	protected void setExportImageDimension(int w, int h) {

	}

	@Override
	protected void exportImage() {

	}

	/**
	 * 
	 * @return true if it has export for 3D printers
	 */
	public boolean hasExport3DPrinter() {
		return false;
	}
}

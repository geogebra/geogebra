package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import java.util.ArrayList;

import javax.media.opengl.GLAutoDrawable;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D.IntersectionCurve;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.HittingSphere;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBufferIndices;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GPUBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.RendererShadersInterface;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.desktop.geogebra3D.euclidian3D.opengl.RendererJogl.GLlocal;
import org.geogebra.desktop.gui.menubar.GeoGebraMenuBar;

/**
 * Renderer checking if we can use shaders or not
 * 
 * @author mathieu
 * 
 */
public class RendererCheckGLVersionD extends RendererD implements
		RendererShadersInterface {

	private RendererImpl rendererImpl;

	private Hitting hitting;

	/**
	 * Constructor
	 * 
	 * @param view
	 * @param useCanvas
	 */
	public RendererCheckGLVersionD(EuclidianView3D view, boolean useCanvas) {
		super(view, useCanvas);

		if (((EuclidianController3D) view3D.getEuclidianController())
				.useInputDepthForHitting()) {
			hitting = new HittingSphere(view3D);
		} else {
			hitting = new Hitting(view3D);
		}

	}

	@Override
	protected void initCheckShaders(GLAutoDrawable drawable) {
		super.initCheckShaders(drawable);

		try {
			// retrieving version, which should be first char, e.g. "4.0 etc."
			String[] version = GeoGebraMenuBar.glVersion.split("\\.");
			int versionInt = Integer.parseInt(version[0]);
			App.debug("==== GL version is " + GeoGebraMenuBar.glVersion
					+ " which means GL>=" + versionInt);
			if (versionInt < 2) {
				// GL 1.x: can't use shaders
				rendererImpl = new RendererImplGL2(this, view3D, jogl);
			} else {
				// GL 2.x or above: can use shaders
				rendererImpl = new RendererImplShadersElements(this, view3D,
						jogl);
			}
		} catch (Exception e) {
			// exception: don't use shaders
			rendererImpl = new RendererImplGL2(this, view3D, jogl);
		}

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

	@Override
	public void setLineWidth(int width) {
		rendererImpl.setLineWidth(width);
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

	protected static final int[] GL_CLIP_PLANE = { GLlocal.GL_CLIP_PLANE0,
			GLlocal.GL_CLIP_PLANE1, GLlocal.GL_CLIP_PLANE2,
			GLlocal.GL_CLIP_PLANE3, GLlocal.GL_CLIP_PLANE4,
			GLlocal.GL_CLIP_PLANE5 };

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
	public Hitting getHitting() {
		return hitting;
	}

	@Override
	public void setHits(GPoint mouseLoc, int threshold) {

		if (mouseLoc == null) {
			return;
		}

		hitting.setHits(mouseLoc, threshold);

	}

	@Override
	public GeoElement getLabelHit(GPoint mouseLoc) {
		if (mouseLoc == null) {
			return null;
		}

		return hitting.getLabelHit(mouseLoc);
	}

	@Override
	public void pickIntersectionCurves() {

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
	public void dispose(GLAutoDrawable drawable) {
		System.out.println("cleanup, remember to release shaders");

		setGL(drawable);

		rendererImpl.dispose();

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
		super.disableCulling();
		rendererImpl.disableCulling();
	}

	@Override
	public void setCullFaceFront() {
		super.setCullFaceFront();
		rendererImpl.setCullFaceFront();
	}

	@Override
	public void setCullFaceBack() {
		super.setCullFaceBack();
		rendererImpl.setCullFaceBack();
	}

	public void loadColorBuffer(GLBuffer fbColors, int length) {
		rendererImpl.loadColorBuffer(fbColors, length);

	}

	public void loadNormalBuffer(GLBuffer fbNormals, int length) {
		rendererImpl.loadNormalBuffer(fbNormals, length);

	}

	public void loadTextureBuffer(GLBuffer fbTextures, int length) {
		rendererImpl.loadTextureBuffer(fbTextures, length);

	}

	public void loadVertexBuffer(GLBuffer fbVertices, int length) {
		rendererImpl.loadVertexBuffer(fbVertices, length);

	}

	public void loadIndicesBuffer(GLBufferIndices arrayI, int length) {
		rendererImpl.loadIndicesBuffer(arrayI, length);

	}

	public void setCenter(Coords center) {
		rendererImpl.setCenter(center);

	}

	@Override
	public void resetCenter() {
		rendererImpl.resetCenter();
	}

	public boolean areTexturesEnabled() {
		return rendererImpl.areTexturesEnabled();
	}

	public void draw(Type type, int length) {
		rendererImpl.draw(type, length);

	}

	public void storeBuffer(GLBuffer fb, int length, int size,
			GPUBuffer buffers, int attrib) {
		rendererImpl.storeBuffer(fb, length, size, buffers, attrib);

	}

	public void storeElementBuffer(short[] fb, int length, GPUBuffer buffers) {
		rendererImpl.storeElementBuffer(fb, length, buffers);

	}

	public void bindBufferForIndices(GPUBuffer buffer) {
		rendererImpl.bindBufferForIndices(buffer);

	}

	public void createArrayBuffer(GPUBuffer buffer) {
		rendererImpl.createArrayBuffer(buffer);

	}

	public void createElementBuffer(GPUBuffer buffer) {
		rendererImpl.createElementBuffer(buffer);

	}

	public void removeArrayBuffer(GPUBuffer buffer) {
		rendererImpl.removeArrayBuffer(buffer);

	}

	public void removeElementBuffer(GPUBuffer buffer) {
		rendererImpl.removeElementBuffer(buffer);

	}

	public void bindBufferForVertices(GPUBuffer buffer, int size) {
		rendererImpl.bindBufferForVertices(buffer, size);

	}

	public void bindBufferForColors(GPUBuffer buffer, int size,
			GLBuffer fbColors) {
		rendererImpl.bindBufferForColors(buffer, size, fbColors);

	}

	public void bindBufferForNormals(GPUBuffer buffer, int size,
			GLBuffer fbNormals) {
		rendererImpl.bindBufferForNormals(buffer, size, fbNormals);

	}

	public void bindBufferForTextures(GPUBuffer buffer, int size,
			GLBuffer fbTextures) {
		rendererImpl.bindBufferForTextures(buffer, size, fbTextures);

	}

	@Override
	public boolean drawQuadric(int type) {
		return rendererImpl.drawQuadric(type);
	}

	@Override
	protected void initShaders() {
		rendererImpl.initShaders();
	}
}

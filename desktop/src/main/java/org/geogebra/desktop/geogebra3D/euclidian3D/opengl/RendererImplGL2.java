package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3DListsForView;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBufferIndices;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.RendererImpl;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Textures;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Renderer implementation using GL2 (no shaders)
 * 
 * @author mathieu
 * 
 */
public class RendererImplGL2 extends RendererImpl
		implements JoglAndGluProvider {

	private RendererJogl jogl;

	private GLU glu = new GLU();

	private double[][] clipPlaneEquations;
	private boolean clipPlanesNeedUpdate;

	/** distance camera-near plane */
	private final static double PERSP_NEAR_MIN = 10;
	/** perspective near distance */
	private double[] perspNear = { PERSP_NEAR_MIN, PERSP_NEAR_MIN };
	/** perspective left position */
	private double[] perspLeft = new double[2];
	/** perspective right position */
	private double[] perspRight = new double[2];
	/** perspective bottom position */
	private double[] perspBottom = new double[2];
	/** perspective top position */
	private double[] perspTop = new double[2];
	/** perspective far position */
	private double[] perspFar = new double[2];
	/** perspective ratio */
	private double[] perspDistratio = new double[2];
	/** eye position for frustum */
	private double[] glassesEyeX1 = new double[2];
	/** eye position for frustum */
	private double[] glassesEyeY1 = new double[2];

	/**
	 * Constructor
	 * 
	 * @param renderer
	 *            GL renderer
	 * 
	 * @param view
	 *            view
	 * @param jogl
	 *            java openGL implementation
	 */
	public RendererImplGL2(Renderer renderer, EuclidianView3D view,
			RendererJogl jogl) {
		super(renderer, view);
		Log.debug(
				"============== Renderer with old GL created (shaders failed)");
		this.jogl = jogl;
		clipPlaneEquations = new double[6][];
		for (int n = 0; n < 6; n++) {
			clipPlaneEquations[n] = new double[4];
		}
		clipPlanesNeedUpdate = true;
	}

	@Override
	public void setClipPlanes(double[][] minMax) {

		CoordMatrix mInvTranspose = view3D.getToSceneMatrixTranspose();
		setClipPlane(0,
				mInvTranspose.mul(new Coords(1, 0, 0, -minMax[0][0])).get());
		setClipPlane(1,
				mInvTranspose.mul(new Coords(-1, 0, 0, minMax[0][1])).get());
		setClipPlane(2,
				mInvTranspose.mul(new Coords(0, 1, 0, -minMax[1][0])).get());
		setClipPlane(3,
				mInvTranspose.mul(new Coords(0, -1, 0, minMax[1][1])).get());
		setClipPlane(4,
				mInvTranspose.mul(new Coords(0, 0, 1, -minMax[2][0])).get());
		setClipPlane(5,
				mInvTranspose.mul(new Coords(0, 0, -1, minMax[2][1])).get());
	}

	private void setClipPlane(int n, double[] equation) {
		for (int i = 0; i < equation.length; i++) {
			if (!DoubleUtil.isEqual(equation[i], clipPlaneEquations[n][i])) {
				clipPlaneEquations[n][i] = equation[i];
				clipPlanesNeedUpdate = true;
			}
		}
	}

	@Override
	final protected void updateClipPlanes() {
		if (clipPlanesNeedUpdate && jogl.getGL2() != null) {
			for (int n = 0; n < 6; n++) {
				jogl.getGL2().glClipPlane(GL_CLIP_PLANE[n],
						clipPlaneEquations[n], 0);
			}
			clipPlanesNeedUpdate = false;
		}
	}

	@Override
	public void setMatrixView(CoordMatrix4x4 matrix) {
		jogl.getGL2().glPushMatrix();
		matrix.get(tmpDouble16);
		jogl.getGL2().glLoadMatrixd(tmpDouble16, 0);
	}

	@Override
	public void unsetMatrixView() {
		jogl.getGL2().glPopMatrix();
	}

	@Override
	public void setColor(float r, float g, float b, float a) {
		jogl.getGL2().glColor4f(r, g, b, a);
	}

	private double[] tmpDouble16 = new double[16];

	@Override
	public void initMatrix() {
		jogl.getGL2().glPushMatrix();
		renderer.getMatrix().get(tmpDouble16);
		jogl.getGL2().glMultMatrixd(tmpDouble16, 0);
	}

	@Override
	public void initMatrixForFaceToScreen() {
		initMatrix();
	}

	@Override
	public void resetMatrix() {
		jogl.getGL2().glPopMatrix();
	}

	@Override
	public void pushSceneMatrix() {
		// set the scene matrix
		jogl.getGL2().glPushMatrix();
		renderer.getToScreenMatrix().get(tmpDouble16);
		jogl.getGL2().glLoadMatrixd(tmpDouble16, 0);
	}

	@Override
	public void setLightPosition(float[] values) {
		jogl.getGL2().glLightfv(GLLightingFunc.GL_LIGHT0,
				GLLightingFunc.GL_POSITION, values,
				0);
		jogl.getGL2().glLightfv(GLLightingFunc.GL_LIGHT1,
				GLLightingFunc.GL_POSITION, values,
				0);
	}

	@Override
	public void setLightAmbiantDiffuse(float ambiant0, float diffuse0,
			float ambiant1, float diffuse1) {

		jogl.getGL2().glLightfv(GLLightingFunc.GL_LIGHT0,
				GLLightingFunc.GL_AMBIENT,
				new float[] { ambiant0, ambiant0, ambiant0, 1.0f }, 0);
		jogl.getGL2().glLightfv(GLLightingFunc.GL_LIGHT0,
				GLLightingFunc.GL_DIFFUSE,
				new float[] { diffuse0, diffuse0, diffuse0, 1.0f }, 0);

		jogl.getGL2().glLightfv(GLLightingFunc.GL_LIGHT1,
				GLLightingFunc.GL_AMBIENT,
				new float[] { ambiant1, ambiant1, ambiant1, 1.0f }, 0);
		jogl.getGL2().glLightfv(GLLightingFunc.GL_LIGHT1,
				GLLightingFunc.GL_DIFFUSE,
				new float[] { diffuse1, diffuse1, diffuse1, 1.0f }, 0);
	}

	/**
	 * 
	 * @return GL instance
	 */
	private GL getGL() {
		return jogl.getGL();
	}

	@Override
	public void setLight(int light) {
		if (light == 0) {
			getGL().glDisable(GLLightingFunc.GL_LIGHT1);
			getGL().glEnable(GLLightingFunc.GL_LIGHT0);
		} else {
			getGL().glDisable(GLLightingFunc.GL_LIGHT0);
			getGL().glEnable(GLLightingFunc.GL_LIGHT1);
		}
	}

	@Override
	public void setColorMaterial() {
		jogl.getGL2().glColorMaterial(GL.GL_FRONT_AND_BACK,
				GLLightingFunc.GL_AMBIENT_AND_DIFFUSE);
		getGL().glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
	}

	@Override
	public void setLightModel() {
		jogl.getGL2().glShadeModel(GLLightingFunc.GL_SMOOTH);
		jogl.getGL2().glLightModeli(GL2ES1.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_TRUE);
		jogl.getGL2().glLightModelf(GL2ES1.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_TRUE);
	}

	@Override
	public void setAlphaFunc() {
		jogl.getGL2().glAlphaFunc(GL.GL_NOTEQUAL, 0);// pixels with alpha=0
															// are not drawn
		// jogl.getGL2().glAlphaFunc(GLlocal.GL_GREATER, 0.8f);//pixels with
		// alpha=0 are not drawn
	}

	@Override
	public void setView() {
		jogl.getGL2().glViewport(0, 0, renderer.getWidth(),
				renderer.getHeight());

		jogl.getGL2().glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		jogl.getGL2().glLoadIdentity();

		renderer.setProjectionMatrix();

		jogl.getGL2().glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
	}

	private int orthoLeft, orthoRight, orthoBottom, orthoTop;
	private double orthoFar, orthoNear;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geogebra.desktop.geogebra3D.euclidian3D.opengl.RendererImpl#
	 * updateOrthoValues()
	 */
	@Override
	final public void updateOrthoValues() {
		orthoLeft = renderer.getLeft();
		orthoRight = renderer.getRight();
		orthoBottom = renderer.getBottom();
		orthoTop = renderer.getTop();
		orthoFar = renderer.getVisibleDepth() / 2;
		orthoNear = -orthoFar;
	}

	@Override
	public void viewOrtho() {

		jogl.getGL2().glOrtho(orthoLeft, orthoRight, orthoBottom, orthoTop,
				orthoNear, orthoFar);
	}

	@Override
	public void viewPersp() {

		jogl.getGL2().glFrustum(perspLeft[Renderer.EYE_LEFT],
				perspRight[Renderer.EYE_LEFT],
				perspBottom[Renderer.EYE_LEFT],
				perspTop[Renderer.EYE_LEFT],
				perspNear[Renderer.EYE_LEFT],
				perspFar[Renderer.EYE_LEFT]);
		jogl.getGL2().glTranslated(0, 0,
				-renderer.eyeToScreenDistance[Renderer.EYE_LEFT]);
	}

	@Override
	public void viewGlasses() {

		jogl.getGL2().glFrustum(
				perspLeft[renderer.eye] - glassesEyeX1[renderer.eye],
				perspRight[renderer.eye] - glassesEyeX1[renderer.eye],
				perspBottom[renderer.eye] - glassesEyeY1[renderer.eye],
				perspTop[renderer.eye] - glassesEyeY1[renderer.eye],
				perspNear[renderer.eye],
				perspFar[renderer.eye]);
		jogl.getGL2().glTranslated(-renderer.glassesEyeX[renderer.eye],
				-renderer.glassesEyeY[renderer.eye],
				-renderer.eyeToScreenDistance[renderer.eye]);
	}

	@Override
	public void viewOblique() {
		viewOrtho();

		jogl.getGL2()
				.glMultMatrixd(new double[] { 1, 0, 0, 0, 0, 1, 0, 0,
						renderer.obliqueX, renderer.obliqueY, 1, 0, 0, 0, 0,
						1 }, 0);
	}

	@Override
	public Manager createManager() {
		return new ManagerGLList(renderer, this, view3D);
	}

	@Override
	final public void enableTextures() {
		getGL().glEnable(GL.GL_TEXTURE_2D);
	}

	@Override
	final public void disableTextures() {
		// bindTexture(-1);
		getGL().glDisable(GL.GL_TEXTURE_2D);

	}

	@Override
	public void enableFading() {
		enableTextures();
		renderer.getTextures().loadTextureLinear(Textures.FADING);
	}

	private int currentDash = Textures.DASH_INIT;

	@Override
	public void enableDash() {
		currentDash = Textures.DASH_INIT;
		enableTextures();
	}

	@Override
	public void enableDashHidden() {
		enableDash();
	}

	@Override
	public float[] getLightPosition() {
		return Renderer.LIGHT_POSITION_D;
	}

	@Override
	public void setDashTexture(int index) {
		if (currentDash == index) {
			return;
		}

		currentDash = index;
		renderer.getRendererImpl()
				.bindTexture(renderer.getTextures().getIndex(index));
		renderer.setTextureNearest();
	}

	private Drawable3DListsForView getDrawList3D() {
		return view3D.getDrawList3D();
	}

	@Override
	public void drawSurfacesOutline() {

		jogl.getGL2().glPolygonMode(GL.GL_BACK, GL2GL3.GL_LINE);
		renderer.setLineWidth(5f);

		renderer.getRendererImpl().setCullFaceFront();
		disableLighting();
		renderer.disableBlending();

		getDrawList3D().drawTransp(renderer);
		getDrawList3D().drawTranspClosedNotCurved(renderer);
		drawTranspClosedCurved();
		if (getDrawList3D().containsClippedSurfacesInclLists()) {
			renderer.enableClipPlanesIfNeeded();
			drawTranspClipped();
			renderer.disableClipPlanesIfNeeded();
		}

		renderer.enableBlending();
		enableLighting();
		renderer.getRendererImpl().setCullFaceBack();

		jogl.getGL2().glPolygonMode(GL.GL_BACK, GL2GL3.GL_FILL);

	}

	private static final int[] GL_CLIP_PLANE = { GL2ES1.GL_CLIP_PLANE0,
			GL2ES1.GL_CLIP_PLANE1, GL2ES1.GL_CLIP_PLANE2, GL2ES1.GL_CLIP_PLANE3,
			GL2ES1.GL_CLIP_PLANE4, GL2ES1.GL_CLIP_PLANE5 };

	@Override
	public void enableClipPlanes() {
		for (int n = 0; n < 6; n++) {
			enableClipPlane(n);
		}
	}

	@Override
	public void disableClipPlanes() {
		for (int n = 0; n < 6; n++) {
			disableClipPlane(n);
		}
	}

	private void enableClipPlane(int n) {
		getGL().glEnable(GL_CLIP_PLANE[n]);
	}

	private void disableClipPlane(int n) {
		getGL().glDisable(GL_CLIP_PLANE[n]);
	}

	@Override
	public void setLabelOrigin(float[] origin) {
		// only used in shaders
	}

	@Override
	public void enableLighting() {
		if (view3D.getUseLight()) {
			getGL().glEnable(GLLightingFunc.GL_LIGHTING);
		}
	}

	@Override
	public void disableLighting() {
		if (view3D.getUseLight()) {
			getGL().glDisable(GLLightingFunc.GL_LIGHTING);
		}
	}

	@Override
	public void initLighting() {
		if (view3D.getUseLight()) {
			getGL().glEnable(GLLightingFunc.GL_LIGHTING);
		} else {
			getGL().glDisable(GLLightingFunc.GL_LIGHTING);
		}
	}

	@Override
	public boolean useShaders() {
		return false;
	}

	@Override
	public void useShaderProgram() {
		// no shaders here
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updatePerspValues() {
		for (int i = 0; i < 2; i++) {
			perspNear[i] = renderer.eyeToScreenDistance[i]
					- renderer.getVisibleDepth() / 2.0;
			if (perspNear[i] < PERSP_NEAR_MIN) {
				perspNear[i] = PERSP_NEAR_MIN;
			}

			// ratio so that distance on screen plane are not changed
			perspDistratio[i] = perspNear[i] / renderer.eyeToScreenDistance[i];

			// frustum
			perspLeft[i] = renderer.getLeft() * perspDistratio[i];
			perspRight[i] = renderer.getRight() * perspDistratio[i];
			perspBottom[i] = renderer.getBottom() * perspDistratio[i];
			perspTop[i] = renderer.getTop() * perspDistratio[i];

			// distance camera-far plane
			perspFar[i] = perspNear[i] + renderer.getVisibleDepth();
		}
	}

	@Override
	public void updateGlassesValues() {
		for (int i = 0; i < 2; i++) {
			// eye values for frustum
			glassesEyeX1[i] = renderer.glassesEyeX[i] * perspDistratio[i];
			glassesEyeY1[i] = renderer.glassesEyeY[i] * perspDistratio[i];
		}

	}

	@Override
	public void updateProjectionObliqueValues() {
		// TODO Auto-generated method stub

	}

	@Override
	public void enableTexturesForText() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initRenderingValues() {
		updateClipPlanes();
	}

	@Override
	public void drawFaceToScreenAbove() {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawFaceToScreenBelow() {
		// TODO Auto-generated method stub

	}

	@Override
	public void enableLightingOnInit() {
		enableLighting();

	}

	@Override
	public void initCulling() {
		renderer.enableCulling();
		renderer.getRendererImpl().setCullFaceBack();

	}

	@Override
	public void drawTranspNotCurved() {
		renderer.getRendererImpl().disableCulling();
		getDrawList3D().drawTransp(renderer);
		getDrawList3D().drawTranspClosedNotCurved(renderer);
		renderer.enableCulling();
	}

	@Override
	public void disableCulling() {
		glDisable(getGL_CULL_FACE());
	}

	@Override
	public void setCullFaceFront() {
		getGL().glCullFace(GL.GL_FRONT);
	}

	@Override
	public void setCullFaceBack() {
		getGL().glCullFace(GL.GL_BACK);
	}

	@Override
	public void loadColorBuffer(GLBuffer fbColors, int length) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadNormalBuffer(GLBuffer fbNormals, int length) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadTextureBuffer(GLBuffer fbTextures, int length) {
		// not needed here

	}

	@Override
	public void disableTextureBuffer() {
		// not needed here
	}

	@Override
	public void loadVertexBuffer(GLBuffer fbVertices, int length) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadIndicesBuffer(GLBufferIndices arrayI, int length) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean areTexturesEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void draw(Type type, int length) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bindBufferForIndices(int buffer) {
		// TODO Auto-generated method stub

	}

	public boolean drawQuadric(int type) {
		return type != GeoQuadricNDConstants.QUADRIC_ELLIPSOID
				&& type != GeoQuadricNDConstants.QUADRIC_HYPERBOLIC_CYLINDER
				&& type != GeoQuadricNDConstants.QUADRIC_HYPERBOLIC_PARABOLOID
				&& type != GeoQuadricNDConstants.QUADRIC_HYPERBOLOID_ONE_SHEET
				&& type != GeoQuadricNDConstants.QUADRIC_HYPERBOLOID_TWO_SHEETS
				&& type != GeoQuadricNDConstants.QUADRIC_PARABOLIC_CYLINDER
				&& type != GeoQuadricNDConstants.QUADRIC_PARABOLOID;
	}

	@Override
	public void initShaders() {
		// used in shaders
	}

	@Override
	public void disableShine() {
		// only implemented with shaders
	}

	@Override
	public void enableShine() {
		// only implemented with shaders
	}

	@Override
	public RendererJogl getJogl() {
		return jogl;
	}

	@Override
	public GLU getGLU() {
		return glu;
	}

	@Override
	public void setBufferLeft() {
		jogl.getGL2().glDrawBuffer(GL2GL3.GL_BACK_LEFT);
		// zspace seems to be swapped
		// jogl.getGL2().glDrawBuffer(GLlocal.GL_BACK_RIGHT);
	}

	@Override
	public void setBufferRight() {
		jogl.getGL2().glDrawBuffer(GL2GL3.GL_BACK_RIGHT);
		// zspace seems to be swapped
		// jogl.getGL2().glDrawBuffer(GLlocal.GL_BACK_LEFT);
	}

	@Override
	public void setStencilFunc(int value) {
		getGL().glStencilFunc(GL.GL_EQUAL, value, 0xFF);
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
	public void setColorMask(boolean r, boolean g, boolean b, boolean a) {
		getGL().glColorMask(r, g, b, a);
	}

	@Override
	public void setClearColor(float r, float g, float b, float a) {
		getGL().glClearColor(r, g, b, a);
	}

	@Override
	public void setPolygonOffset(float factor, float units) {
		getGL().glPolygonOffset(factor, units);
	}
	
	@Override
	public void setLayer(int layer){
		setPolygonOffset(-layer * 0.05f, -layer * 10);
	}

	@Override
	public void genTextures2D(int number, int[] index) {
		getGL().glGenTextures(number, index, 0);
	}

	@Override
	public void bindTexture(int index) {
		getGL().glBindTexture(GL.GL_TEXTURE_2D, index);
	}

	@Override
	public void glEnable(int flag) {
		getGL().glEnable(flag);
	}

	@Override
	public void glDisable(int flag) {
		getGL().glDisable(flag);
	}

	@Override
	public void enableAlphaTest() {
		glEnable(GL2ES1.GL_ALPHA_TEST);
	}

	@Override
	public void disableAlphaTest() {
		glDisable(GL2ES1.GL_ALPHA_TEST);
	}

	@Override
	public final void enableMultisample() {
		glEnable(GL.GL_MULTISAMPLE);
	}

	@Override
	public final void disableMultisample() {
		glDisable(GL.GL_MULTISAMPLE);
	}

	@Override
	public int getGL_BLEND() {
		return GL.GL_BLEND;
	}

	@Override
	public int getGL_CULL_FACE() {
		return GL.GL_CULL_FACE;
	}

	@Override
	public void glClear(int flag) {
		getGL().glClear(flag);
	}

	@Override
	public int getGL_COLOR_BUFFER_BIT() {
		return GL.GL_COLOR_BUFFER_BIT;
	}

	@Override
	public int getGL_DEPTH_BUFFER_BIT() {
		return GL.GL_DEPTH_BUFFER_BIT;
	}

	@Override
	public int getGL_DEPTH_TEST() {
		return GL.GL_DEPTH_TEST;
	}

	@Override
	protected void bindFramebuffer(Object id) {
		getGL().glBindFramebuffer(GL.GL_FRAMEBUFFER, (Integer) id);
	}

	@Override
	protected void bindRenderbuffer(Object id) {
		getGL().glBindRenderbuffer(GL.GL_RENDERBUFFER, (Integer) id);
	}

	@Override
	protected void unbindFramebuffer() {
		bindFramebuffer(0);
	}

	@Override
	protected void unbindRenderbuffer() {
		bindRenderbuffer(0);
	}

	@Override
	protected void textureParametersNearest() {
		getGL().glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_NEAREST);
		getGL().glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_NEAREST);
	}

	@Override
	protected void textureImage2DForBuffer(int width, int height) {
		getGL().glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, width, height, 0,
				GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, null);
	}

	@Override
	protected void renderbufferStorage(int width, int height) {
		getGL().glRenderbufferStorage(GL.GL_RENDERBUFFER,
				GL2ES2.GL_DEPTH_COMPONENT, width, height);
	}

	private int[] tmp = new int[1];

	@Override
	protected Object genRenderbuffer() {
		getGL().glGenRenderbuffers(1, tmp, 0);
		return tmp[0];
	}

	@Override
	protected Object genFramebuffer() {
		getGL().glGenFramebuffers(1, tmp, 0);
		return tmp[0];
	}

	@Override
	protected void framebuffer(Object colorId, Object depthId) {
		getGL().glFramebufferTexture2D(GL.GL_FRAMEBUFFER,
				GL.GL_COLOR_ATTACHMENT0, GL.GL_TEXTURE_2D,
				(Integer) colorId, 0);
		getGL().glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER,
				GL.GL_DEPTH_ATTACHMENT, GL.GL_RENDERBUFFER,
				(Integer) depthId);
	}

	@Override
	protected boolean checkFramebufferStatus() {
		return getGL().glCheckFramebufferStatus(
				GL.GL_FRAMEBUFFER) == GL.GL_FRAMEBUFFER_COMPLETE;
	}

	@Override
	public void createDummyTexture() {
		// only needed for shaders
	}

	@Override
	public void attribPointers() {
		// only needed for shaders
	}

	@Override
	public void setProjectionMatrixViewForAR() {
		// used in AR only
	}

	@Override
	public void glViewPort() {
		// used only with shaders
	}

	@Override
	public void drawNotHidden() {
		getDrawList3D().draw(renderer);
	}

	@Override
	public void drawHiddenTextured() {
		getDrawList3D().drawHiddenTextured(renderer);
	}

	@Override
	public void drawHiddenNotTextured() {
		getDrawList3D().drawHiddenNotTextured(renderer);
	}

	@Override
	public void drawTranspClosedCurved() {
		getDrawList3D().drawTranspClosedCurved(renderer);
	}

	@Override
	public void drawClosedSurfacesForHiding() {
		getDrawList3D().drawClosedSurfacesForHiding(renderer);
	}

	@Override
	public void drawClippedSurfacesForHiding() {
		getDrawList3D().drawClippedSurfacesForHiding(renderer);
	}

	@Override
	public void drawTranspClipped() {
		getDrawList3D().drawTranspClipped(renderer);
	}

	@Override
	public void drawSurfacesForHiding() {
		getDrawList3D().drawSurfacesForHiding(renderer);
	}

	@Override
	public void drawOpaqueSurfaces() {
		setLight(1);

		renderer.enableBlending();

		// TODO improve this !
		renderer.enableCulling();
		setCullFaceFront();
		getDrawList3D().drawNotTransparentSurfaces(renderer);
		getDrawList3D().drawNotTransparentSurfacesClosed(renderer);
		if (getDrawList3D().containsClippedSurfacesInclLists()) {
			renderer.enableClipPlanesIfNeeded();
			getDrawList3D()
					.drawNotTransparentSurfacesClipped(renderer);
			renderer.disableClipPlanesIfNeeded();
		}
		setCullFaceBack();
		getDrawList3D().drawNotTransparentSurfaces(renderer);
		getDrawList3D().drawNotTransparentSurfacesClosed(renderer);
		if (getDrawList3D().containsClippedSurfacesInclLists()) {
			renderer.enableClipPlanesIfNeeded();
			getDrawList3D().drawNotTransparentSurfacesClipped(renderer);
			renderer.disableClipPlanesIfNeeded();
		}

		setLight(0);
	}
}

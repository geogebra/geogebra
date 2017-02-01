package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import java.awt.Component;
import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBufferIndices;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GPUBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.RendererImpl;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Textures;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
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
		if (jogl.getGL2() != null) {
			jogl.getGL2().glClipPlane(GL_CLIP_PLANE[n], equation, 0);
		}
	}

	@Override
	public void setMatrixView() {
		jogl.getGL2().glPushMatrix();
		renderer.getToScreenMatrix().get(tmpDouble16);
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
	public void glLoadName(int loop) {
		jogl.getGL2().glLoadName(loop);
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

	@Override
	public void setStencilLines() {

		// disable clip planes if used
		if (renderer.enableClipPlanes) {
			disableClipPlanes();
		}

		final int w = renderer.getWidth();
		final int h = renderer.getHeight();
		// Log.debug(w+" * "+h+" = "+(w*h));

		// projection for real 2D
		jogl.getGL2().glViewport(0, 0, w, h);

		jogl.getGL2().glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		jogl.getGL2().glLoadIdentity();
		getGLU().gluOrtho2D(0, w, h, 0);

		jogl.getGL2().glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		jogl.getGL2().glLoadIdentity();

		jogl.getGL2().glEnable(GL.GL_STENCIL_TEST);

		// draw stencil pattern
		jogl.getGL2().glStencilMask(0xFF);
		jogl.getGL2().glClear(GL.GL_STENCIL_BUFFER_BIT); // needs mask=0xFF

		// no multisample here to prevent ghosts
		jogl.getGL2().glDisable(GL.GL_MULTISAMPLE);

		// data for stencil : one line = 0, one line = 1, etc.

		/*
		 * final int h2 = h+10;// (int) (h*1.1) ; //TODO : understand why buffer
		 * doens't match glDrawPixels dimension ByteBuffer data =
		 * newByteBuffer(w * h2); byte b = 0; for (int y=0; y<h2; y++){ b=(byte)
		 * (1-b); for (int x=0; x<w; x++){ data.put(b); } } data.rewind();
		 * 
		 * // check if we start with 0 or with 1 int y =
		 * (canvas.getLocationOnScreen().y) % 2;
		 * 
		 * gl.glRasterPos2i(0, h-y); //Log.debug("== "+w+" * "+h+" = "+(w*h));
		 * gl.glDrawPixels(w, h, GLlocal.GL_STENCIL_INDEX,
		 * GLlocal.GL_UNSIGNED_BYTE, data);
		 */

		ByteBuffer data = RendererJogl.newByteBuffer(w);
		byte b = 1;
		for (int x = 0; x < w; x++) {
			data.put(b);
		}

		data.rewind();

		// check if we start with 0 or with 1
		// seems to be sensible to canvas location on screen and to parent
		// relative location
		// (try docked with neighboors / undocked or docked alone)

		int y0 = ((Component) renderer.getCanvas()).getParent().getLocation().y
				+ (((Component) renderer.getCanvas()).getLocationOnScreen().y)
						% 2;

		// Log.debug("\nparent.y="+canvas.getParent().getLocation().y+"\ncanvas.y="+canvas.getLocation().y+"\nscreen.y="+canvas.getLocationOnScreen().y+"\nh="+h+"\ny0="+y0);
		// Log.debug("== "+w+" * "+h+" = "+(w*h)+"\ny0="+y0);

		for (int y = 0; y < h / 2; y++) {
			jogl.getGL2().glRasterPos2i(0, 2 * y + y0);
			jogl.getGL2().glDrawPixels(w, 1, GL2ES2.GL_STENCIL_INDEX,
					GL.GL_UNSIGNED_BYTE, data);
		}

		// current mask for stencil test
		jogl.getGL2().glStencilMask(0x00);

		// back to multisample
		jogl.getGL2().glEnable(GL.GL_MULTISAMPLE);

		renderer.waitForSetStencilLines = false;

		// restore clip planes
		if (renderer.enableClipPlanes) {
			enableClipPlanes();
		}

	}

	private int orthoLeft, orthoRight, orthoBottom, orthoTop, orthoNear,
			orthoFar;

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

		jogl.getGL2().glFrustum(renderer.perspLeft[Renderer.EYE_LEFT],
				renderer.perspRight[Renderer.EYE_LEFT],
				renderer.perspBottom[Renderer.EYE_LEFT],
				renderer.perspTop[Renderer.EYE_LEFT],
				renderer.perspNear[Renderer.EYE_LEFT],
				renderer.perspFar[Renderer.EYE_LEFT]);
		jogl.getGL2().glTranslated(0, 0,
				renderer.perspFocus[Renderer.EYE_LEFT]);
	}

	@Override
	public void viewGlasses() {

		jogl.getGL2().glFrustum(
				renderer.perspLeft[renderer.eye]
						- renderer.glassesEyeX1[renderer.eye],
				renderer.perspRight[renderer.eye]
						- renderer.glassesEyeX1[renderer.eye],
				renderer.perspBottom[renderer.eye]
						- renderer.glassesEyeY1[renderer.eye],
				renderer.perspTop[renderer.eye]
						- renderer.glassesEyeY1[renderer.eye],
				renderer.perspNear[renderer.eye],
				renderer.perspFar[renderer.eye]);
		jogl.getGL2().glTranslated(-renderer.glassesEyeX[renderer.eye],
				-renderer.glassesEyeY[renderer.eye],
				renderer.perspFocus[renderer.eye]);
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
	public float[] getLightPosition() {
		return Renderer.LIGHT_POSITION_D;
	}

	@Override
	public void setDashTexture(int index) {
		if (currentDash == index) {
			return;
		}

		currentDash = index;
		renderer.bindTexture(renderer.getTextures().getIndex(index));
		renderer.setTextureNearest();
	}

	@Override
	public void drawSurfacesOutline() {

		jogl.getGL2().glPolygonMode(GL.GL_BACK, GL2GL3.GL_LINE);
		renderer.setLineWidth(5f);

		renderer.setCullFaceFront();
		disableLighting();
		renderer.disableBlending();

		renderer.drawable3DLists.drawTransp(renderer);
		renderer.drawable3DLists.drawTranspClosedNotCurved(renderer);
		renderer.drawable3DLists.drawTranspClosedCurved(renderer);
		if (renderer.drawable3DLists.containsClippedSurfacesInclLists()) {
			renderer.enableClipPlanesIfNeeded();
			renderer.drawable3DLists.drawTranspClipped(renderer);
			renderer.disableClipPlanesIfNeeded();
		}

		renderer.enableBlending();
		enableLighting();
		renderer.setCullFaceBack();

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
		// TODO Auto-generated method stub

	}

	@Override
	public void updateGlassesValues() {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

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
		renderer.setCullFaceBack();

	}

	@Override
	public void drawTranspNotCurved() {

		renderer.disableCulling();
		renderer.drawable3DLists.drawTransp(renderer);
		renderer.drawable3DLists.drawTranspClosedNotCurved(renderer);

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
		// TODO Auto-generated method stub

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
	public void setCenter(Coords center) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetCenter() {
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
	public void storeBuffer(GLBuffer fb, int length, int size,
			GPUBuffer buffers, int attrib) {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeElementBuffer(short[] fb, int length, GPUBuffer buffers) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bindBufferForIndices(GPUBuffer buffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createArrayBuffer(GPUBuffer buffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createElementBuffer(GPUBuffer buffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeArrayBuffer(GPUBuffer buffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeElementBuffer(GPUBuffer buffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bindBufferForVertices(GPUBuffer buffer, int size) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bindBufferForColors(GPUBuffer buffer, int size,
			GLBuffer fbColors) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bindBufferForNormals(GPUBuffer buffer, int size,
			GLBuffer fbNormals) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bindBufferForTextures(GPUBuffer buffer, int size,
			GLBuffer fbTextures) {
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

}

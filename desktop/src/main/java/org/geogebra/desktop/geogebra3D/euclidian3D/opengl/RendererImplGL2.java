package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import java.nio.ByteBuffer;

import javax.media.opengl.GL;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBufferIndices;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GPUBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Textures;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import org.geogebra.common.main.App;
import org.geogebra.desktop.geogebra3D.euclidian3D.opengl.RendererJogl.GLlocal;

/**
 * Renderer implementation using GL2 (no shaders)
 * 
 * @author mathieu
 * 
 */
public class RendererImplGL2 implements RendererImpl {


	private EuclidianView3D view3D;

	private RendererJogl jogl;

	private RendererD renderer;

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
	public RendererImplGL2(RendererD renderer, EuclidianView3D view,
			RendererJogl jogl) {
		App.debug("============== Renderer with old GL created (shaders failed)");
		this.renderer = renderer;
		this.view3D = view;
		this.jogl = jogl;
	}

	@Override
	public void setClipPlanes(double[][] minMax) {
		CoordMatrix mInvTranspose = view3D.getToSceneMatrixTranspose();
		setClipPlane(0, mInvTranspose.mul(new Coords(1, 0, 0, -minMax[0][0]))
				.get());
		setClipPlane(1, mInvTranspose.mul(new Coords(-1, 0, 0, minMax[0][1]))
				.get());
		setClipPlane(2, mInvTranspose.mul(new Coords(0, 1, 0, -minMax[1][0]))
				.get());
		setClipPlane(3, mInvTranspose.mul(new Coords(0, -1, 0, minMax[1][1]))
				.get());
		setClipPlane(4, mInvTranspose.mul(new Coords(0, 0, 1, -minMax[2][0]))
				.get());
		setClipPlane(5, mInvTranspose.mul(new Coords(0, 0, -1, minMax[2][1]))
				.get());
	}

	private void setClipPlane(int n, double[] equation) {
		jogl.getGL2().glClipPlane(GL_CLIP_PLANE[n], equation, 0);
	}

	public void setMatrixView() {
		jogl.getGL2().glPushMatrix();
		view3D.getToScreenMatrix().get(tmpDouble16);
		jogl.getGL2().glLoadMatrixd(tmpDouble16, 0);
	}

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


	public void pushSceneMatrix() {
		// set the scene matrix
		jogl.getGL2().glPushMatrix();
		view3D.getToScreenMatrix().get(tmpDouble16);
		jogl.getGL2().glLoadMatrixd(tmpDouble16, 0);
	}

	@Override
	public void glLoadName(int loop) {
		jogl.getGL2().glLoadName(loop);
	}

	public void setLightPosition(float[] values) {
		jogl.getGL2().glLightfv(GLlocal.GL_LIGHT0, GLlocal.GL_POSITION, values,
				0);
		jogl.getGL2().glLightfv(GLlocal.GL_LIGHT1, GLlocal.GL_POSITION, values,
				0);
	}

	public void setLightAmbiantDiffuse(float ambiant0, float diffuse0,
			float ambiant1, float diffuse1) {

		jogl.getGL2().glLightfv(GLlocal.GL_LIGHT0, GLlocal.GL_AMBIENT,
				new float[] { ambiant0, ambiant0, ambiant0, 1.0f }, 0);
		jogl.getGL2().glLightfv(GLlocal.GL_LIGHT0, GLlocal.GL_DIFFUSE,
				new float[] { diffuse0, diffuse0, diffuse0, 1.0f }, 0);

		jogl.getGL2().glLightfv(GLlocal.GL_LIGHT1, GLlocal.GL_AMBIENT,
				new float[] { ambiant1, ambiant1, ambiant1, 1.0f }, 0);
		jogl.getGL2().glLightfv(GLlocal.GL_LIGHT1, GLlocal.GL_DIFFUSE,
				new float[] { diffuse1, diffuse1, diffuse1, 1.0f }, 0);
	}

	/**
	 * 
	 * @return GL instance
	 */
	private GL getGL() {
		return jogl.getGL();
	}

	public void setLight(int light) {
		if (light == 0) {
			getGL().glDisable(GLlocal.GL_LIGHT1);
			getGL().glEnable(GLlocal.GL_LIGHT0);
		} else {
			getGL().glDisable(GLlocal.GL_LIGHT0);
			getGL().glEnable(GLlocal.GL_LIGHT1);
		}
	}

	public void setColorMaterial() {
		jogl.getGL2().glColorMaterial(GLlocal.GL_FRONT_AND_BACK,
				GLlocal.GL_AMBIENT_AND_DIFFUSE);
		getGL().glEnable(GLlocal.GL_COLOR_MATERIAL);
	}

	public void setLightModel() {
		jogl.getGL2().glShadeModel(GLlocal.GL_SMOOTH);
		jogl.getGL2().glLightModeli(GLlocal.GL_LIGHT_MODEL_TWO_SIDE,
				GLlocal.GL_TRUE);
		jogl.getGL2().glLightModelf(GLlocal.GL_LIGHT_MODEL_TWO_SIDE,
				GLlocal.GL_TRUE);
	}

	public void setAlphaFunc() {
		jogl.getGL2().glAlphaFunc(GLlocal.GL_NOTEQUAL, 0);// pixels with alpha=0
															// are not drawn
		// jogl.getGL2().glAlphaFunc(GLlocal.GL_GREATER, 0.8f);//pixels with
		// alpha=0 are not drawn
	}

	public void setView() {
		jogl.getGL2().glViewport(0, 0, renderer.getWidth(),
				renderer.getHeight());

		jogl.getGL2().glMatrixMode(GLlocal.GL_PROJECTION);
		jogl.getGL2().glLoadIdentity();

		renderer.setProjectionMatrix();

		jogl.getGL2().glMatrixMode(GLlocal.GL_MODELVIEW);
	}
	

	public void setStencilLines() {

		// disable clip planes if used
		if (renderer.enableClipPlanes)
			disableClipPlanes();

		final int w = renderer.getWidth();
		final int h = renderer.getHeight();
		// App.debug(w+" * "+h+" = "+(w*h));

		// projection for real 2D
		jogl.getGL2().glViewport(0, 0, w, h);

		jogl.getGL2().glMatrixMode(GLlocal.GL_PROJECTION);
		jogl.getGL2().glLoadIdentity();
		renderer.glu.gluOrtho2D(0, w, h, 0);

		jogl.getGL2().glMatrixMode(GLlocal.GL_MODELVIEW);
		jogl.getGL2().glLoadIdentity();

		jogl.getGL2().glEnable(GLlocal.GL_STENCIL_TEST);

		// draw stencil pattern
		jogl.getGL2().glStencilMask(0xFF);
		jogl.getGL2().glClear(GLlocal.GL_STENCIL_BUFFER_BIT); // needs mask=0xFF

		// no multisample here to prevent ghosts
		jogl.getGL2().glDisable(GLlocal.GL_MULTISAMPLE);

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
		 * gl.glRasterPos2i(0, h-y); //App.debug("== "+w+" * "+h+" = "+(w*h));
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
		int y0 = (((RendererCheckGLVersionD) renderer).canvas.getParent()
				.getLocation().y + ((RendererCheckGLVersionD) renderer).canvas
				.getLocationOnScreen().y) % 2;

		// App.debug("\nparent.y="+canvas.getParent().getLocation().y+"\ncanvas.y="+canvas.getLocation().y+"\nscreen.y="+canvas.getLocationOnScreen().y+"\nh="+h+"\ny0="+y0);
		// App.debug("== "+w+" * "+h+" = "+(w*h)+"\ny0="+y0);

		for (int y = 0; y < h / 2; y++) {
			jogl.getGL2().glRasterPos2i(0, 2 * y + y0);
			jogl.getGL2().glDrawPixels(w, 1, GLlocal.GL_STENCIL_INDEX,
					GLlocal.GL_UNSIGNED_BYTE, data);
		}

		// current mask for stencil test
		jogl.getGL2().glStencilMask(0x00);

		// back to multisample
		jogl.getGL2().glEnable(GLlocal.GL_MULTISAMPLE);

		renderer.waitForSetStencilLines = false;

		// restore clip planes
		if (renderer.enableClipPlanes)
			enableClipPlanes();

	}

	private int orthoLeft, orthoRight, orthoBottom, orthoTop, orthoNear,
			orthoFar;

	/* (non-Javadoc)
	 * @see org.geogebra.desktop.geogebra3D.euclidian3D.opengl.RendererImpl#updateOrthoValues()
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

	public void viewOrtho() {

		jogl.getGL2().glOrtho(orthoLeft, orthoRight, orthoBottom, orthoTop,
				orthoNear, orthoFar);
	}

	public void viewPersp() {

		jogl.getGL2().glFrustum(renderer.perspLeft[Renderer.EYE_LEFT],
				renderer.perspRight[Renderer.EYE_LEFT],
				renderer.perspBottom[Renderer.EYE_LEFT],
				renderer.perspTop[Renderer.EYE_LEFT],
				renderer.perspNear[Renderer.EYE_LEFT],
				renderer.perspFar[Renderer.EYE_LEFT]);
		jogl.getGL2()
				.glTranslated(0, 0, renderer.perspFocus[Renderer.EYE_LEFT]);
	}

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

	public void viewOblique() {
		viewOrtho();

		jogl.getGL2().glMultMatrixd(
				new double[] { 1, 0, 0, 0, 0, 1, 0, 0, renderer.obliqueX,
						renderer.obliqueY, 1,
						0, 0, 0, 0, 1 }, 0);
	}

	public Manager createManager() {
		return new ManagerGLList(renderer, view3D);
	}

	@Override
	final public void enableTextures() {
		getGL().glEnable(GLlocal.GL_TEXTURE_2D);
	}

	@Override
	final public void disableTextures() {
		// bindTexture(-1);
		getGL().glDisable(GLlocal.GL_TEXTURE_2D);

	}

	@Override
	public void setLineWidth(int width) {
		jogl.getGL2().glLineWidth(width);
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

	public void drawSurfacesOutline() {

		jogl.getGL2().glPolygonMode(GLlocal.GL_BACK, GLlocal.GL_LINE);
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

		jogl.getGL2().glPolygonMode(GLlocal.GL_BACK, GLlocal.GL_FILL);

	}

	protected static final int[] GL_CLIP_PLANE = { GLlocal.GL_CLIP_PLANE0,
			GLlocal.GL_CLIP_PLANE1, GLlocal.GL_CLIP_PLANE2,
			GLlocal.GL_CLIP_PLANE3, GLlocal.GL_CLIP_PLANE4,
			GLlocal.GL_CLIP_PLANE5 };

	public void enableClipPlanes() {
		for (int n = 0; n < 6; n++)
			enableClipPlane(n);
	}

	public void disableClipPlanes() {
		for (int n = 0; n < 6; n++)
			disableClipPlane(n);
	}

	private void enableClipPlane(int n) {
		getGL().glEnable(GL_CLIP_PLANE[n]);
	}

	private void disableClipPlane(int n) {
		getGL().glDisable(GL_CLIP_PLANE[n]);
	}

	@Override
	public void setLabelOrigin(Coords origin) {
		// only used in shaders
	}

	@Override
	public void enableLighting() {
		if (view3D.getUseLight()){
			getGL().glEnable(GLlocal.GL_LIGHTING);
		}
	}

	@Override
	public void disableLighting() {
		if (view3D.getUseLight()){
			getGL().glDisable(GLlocal.GL_LIGHTING);
		}
	}


	@Override
	public void initLighting() {
		if (view3D.getUseLight()) {
			getGL().glEnable(GLlocal.GL_LIGHTING);
		} else {
			getGL().glDisable(GLlocal.GL_LIGHTING);
		}
	}

	@Override
	public boolean useShaders() {
		return false;
	}
	
	public void useShaderProgram() {
		// no shaders here
	}

	public void draw() {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void updatePerspValues() {
		// TODO Auto-generated method stub

	}

	public void updateGlassesValues() {
		// TODO Auto-generated method stub

	}

	public void updateProjectionObliqueValues() {
		// TODO Auto-generated method stub

	}

	public void enableTexturesForText() {
		// TODO Auto-generated method stub

	}

	public void initRenderingValues() {
		// TODO Auto-generated method stub

	}

	public void drawFaceToScreenAbove() {
		// TODO Auto-generated method stub

	}

	public void drawFaceToScreenBelow() {
		// TODO Auto-generated method stub

	}

	public void enableLightingOnInit() {
		enableLighting();

	}

	public void initCulling() {
		renderer.enableCulling();
		renderer.setCullFaceBack();

	}

	public void drawTranspNotCurved() {

		renderer.disableCulling();
		renderer.drawable3DLists.drawTransp(renderer);
		renderer.drawable3DLists.drawTranspClosedNotCurved(renderer);

		renderer.enableCulling();
	}

	public void disableCulling() {
		// TODO Auto-generated method stub

	}

	public void setCullFaceFront() {
		// TODO Auto-generated method stub

	}

	public void setCullFaceBack() {
		// TODO Auto-generated method stub

	}

	public void loadColorBuffer(GLBuffer fbColors, int length) {
		// TODO Auto-generated method stub

	}

	public void loadNormalBuffer(GLBuffer fbNormals, int length) {
		// TODO Auto-generated method stub

	}

	public void loadTextureBuffer(GLBuffer fbTextures, int length) {
		// TODO Auto-generated method stub

	}

	public void loadVertexBuffer(GLBuffer fbVertices, int length) {
		// TODO Auto-generated method stub

	}

	public void loadIndicesBuffer(GLBufferIndices arrayI, int length) {
		// TODO Auto-generated method stub

	}

	public void setCenter(Coords center) {
		// TODO Auto-generated method stub

	}

	public void resetCenter() {
		// TODO Auto-generated method stub

	}

	public boolean areTexturesEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public void draw(Type type, int length) {
		// TODO Auto-generated method stub

	}

	public void storeBuffer(GLBuffer fb, int length, int size,
			GPUBuffer buffers, int attrib) {
		// TODO Auto-generated method stub

	}

	public void storeElementBuffer(short[] fb, int length, GPUBuffer buffers) {
		// TODO Auto-generated method stub

	}

	public void bindBufferForIndices(GPUBuffer buffer) {
		// TODO Auto-generated method stub

	}

	public void createArrayBuffer(GPUBuffer buffer) {
		// TODO Auto-generated method stub

	}

	public void createElementBuffer(GPUBuffer buffer) {
		// TODO Auto-generated method stub

	}

	public void removeArrayBuffer(GPUBuffer buffer) {
		// TODO Auto-generated method stub

	}

	public void removeElementBuffer(GPUBuffer buffer) {
		// TODO Auto-generated method stub

	}

	public void bindBufferForVertices(GPUBuffer buffer, int size) {
		// TODO Auto-generated method stub

	}

	public void bindBufferForColors(GPUBuffer buffer, int size,
			GLBuffer fbColors) {
		// TODO Auto-generated method stub

	}

	public void bindBufferForNormals(GPUBuffer buffer, int size,
			GLBuffer fbNormals) {
		// TODO Auto-generated method stub

	}

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

	public void initShaders() {
		// used in shaders
	}
}

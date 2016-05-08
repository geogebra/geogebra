package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.geogebra3D.euclidian3D.opengl.RendererJogl.GLlocal;

import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUtessellator;

/**
 * 
 * Manager using GL lists
 * 
 * @author ggb3D
 *
 */
public class ManagerGLList extends ManagerD {

	// GL
	private GLUtessellator tesselator;

	protected Renderer renderer;

	private JoglAndGluProvider joglAndGluProvider;

	/**
	 * common constructor
	 * 
	 * @param renderer
	 * @param view3D
	 *            3D view
	 */
	public ManagerGLList(Renderer renderer,
			JoglAndGluProvider joglAndGluProvider, EuclidianView3D view3D) {

		super();

		Log.debug("ManagerGLList");
		this.joglAndGluProvider = joglAndGluProvider;
		init(renderer, view3D);
	}

	@Override
	protected void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}

	@Override
	protected Renderer getRenderer() {
		return renderer;
	}

	public RendererJogl getJogl() {
		return joglAndGluProvider.getJogl();
	}

	public GLU getGLU() {
		return joglAndGluProvider.getGLU();
	}

	// ///////////////////////////////////////////
	// LISTS METHODS
	// ///////////////////////////////////////////

	private int genLists(int nb) {
		return getJogl().getGL2().glGenLists(nb);
	}

	// ///////////////////////////////////////////
	// GEOMETRY METHODS
	// ///////////////////////////////////////////

	@Override
	public int startNewList(int old) {
		// generates a new list
		int ret = genLists(1);

		getJogl().getGL2().glNewList(ret, GLlocal.GL_COMPILE);

		return ret;
	}

	private void newList(int index) {
		getJogl().getGL2().glNewList(index, GLlocal.GL_COMPILE);
	}

	@Override
	public void endList() {
		getJogl().getGL2().glEndList();
	}

	@Override
	public void startGeometry(Type type) {
		getJogl().getGL2().glBegin(getGLType(type));
	}

	@Override
	public void endGeometry() {
		getJogl().getGL2().glEnd();
	}

	// ///////////////////////////////////////////
	// POLYGONS METHODS
	// ///////////////////////////////////////////

	@Override
	public int startPolygons(int old) {

		// generates a new list
		int ret = genLists(1);

		// Application.debug("ret = "+ret);

		// if ret == 0, there's no list
		if (ret == 0)
			return 0;

		RendererTesselCallBack tessCallback = new RendererTesselCallBack(this);

		tesselator = getGLU().gluNewTess();

		getGLU().gluTessCallback(tesselator, GLU.GLU_TESS_VERTEX,
				tessCallback);// vertexCallback);
		getGLU().gluTessCallback(tesselator, GLU.GLU_TESS_BEGIN,
				tessCallback);// beginCallback);
		getGLU()
				.gluTessCallback(tesselator, GLU.GLU_TESS_END, tessCallback);// endCallback);
		getGLU().gluTessCallback(tesselator, GLU.GLU_TESS_ERROR,
				tessCallback);// errorCallback);
		getGLU().gluTessCallback(tesselator, GLU.GLU_TESS_COMBINE,
				tessCallback);// combineCallback);

		newList(ret);

		return ret;
	}

	@Override
	public void drawPolygon(Coords n, Coords[] v) {

		// starts the polygon
		getGLU().gluTessBeginPolygon(tesselator, null);
		getGLU().gluTessBeginContour(tesselator);

		// set normal
		float nx = (float) n.getX();
		float ny = (float) n.getY();
		float nz = (float) n.getZ();
		getGLU().gluTessNormal(tesselator, nx, ny, nz);
		normal(nx, ny, nz);

		// set texture
		setDummyTexture();

		// set vertices
		for (int i = 0; i < v.length; i++) {
			double[] point = v[i].get();
			getGLU().gluTessVertex(tesselator, point, 0, point);
		}

		// end the polygon
		getGLU().gluTessEndContour(tesselator);
		getGLU().gluTessEndPolygon(tesselator);

	}

	@Override
	public void endPolygons() {

		getJogl().getGL2().glEndList();
		getGLU().gluDeleteTess(tesselator);
	}

	/**
	 * remove the polygon from gl memory
	 * 
	 * @param index
	 */
	@Override
	public void remove(int index) {

		getJogl().getGL2().glDeleteLists(index, 1);
	}

	// ///////////////////////////////////////////
	// DRAWING METHODS
	// ///////////////////////////////////////////

	@Override
	public void draw(int index) {
		getJogl().getGL2().glCallList(index);
	}

	@Override
	public void drawLabel(int index) {
		draw(index);
	}

	@Override
	protected void texture(double x, double y) {
		getJogl().getGL2().glTexCoord2d(x, y);
	}

	@Override
	protected void setDummyTexture() {
		texture(0, 0);
	}

	@Override
	protected void normal(double x, double y, double z) {

		getJogl().getGL2().glNormal3d(x, y, z);
	}

	@Override
	protected void vertex(double x, double y, double z) {

		getJogl().getGL2().glVertex3d(x, y, z);
	}

	@Override
	protected void vertexInt(double x, double y, double z) {

		// getJogl().getGL2().glVertex3i(x, y, z);
		vertex(x,y,z);
	}

	@Override
	protected void vertices(double[] vertices) {
		getJogl().getGL2().glVertex3dv(vertices, 0);
	}

	@Override
	protected void color(double r, double g, double b) {
		getJogl().getGL2().glColor3d(r, g, b);
	}

	@Override
	protected void color(double r, double g, double b, double a) {
		getJogl().getGL2().glColor4d(r, g, b, a);
	}

	@Override
	protected void pointSize(double size) {
		getJogl().getGL2().glPointSize((float) size);
	}

	@Override
	public void rectangleGeometry(double x, double y, double z, double width, double height) {
		getText().rectangle(x, y, z, width, height);
	}

	/*
	 * @Override public void rectangleBounds(int x, int y, int z, int width, int
	 * height){ getText().rectangleBounds(x, y, z, width, height); }
	 */

}

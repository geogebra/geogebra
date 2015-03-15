package geogebra3D.euclidian3D.opengl;

import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.kernel.Matrix.Coords;
import geogebra3D.euclidian3D.opengl.RendererJogl.GLlocal;

import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;

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

	protected RendererGL2 renderer;

	/**
	 * common constructor
	 * 
	 * @param renderer
	 * @param view3D
	 *            3D view
	 */
	public ManagerGLList(Renderer renderer, EuclidianView3D view3D) {
		super(renderer, view3D);
	}

	@Override
	protected void setRenderer(Renderer renderer) {
		this.renderer = (RendererGL2) renderer;
	}

	@Override
	protected RendererGL2 getRenderer() {
		return renderer;
	}

	// ///////////////////////////////////////////
	// LISTS METHODS
	// ///////////////////////////////////////////

	private int genLists(int nb) {
		return renderer.jogl.getGL2().glGenLists(nb);
	}

	// ///////////////////////////////////////////
	// GEOMETRY METHODS
	// ///////////////////////////////////////////

	@Override
	public int startNewList(int old) {
		// generates a new list
		int ret = genLists(1);

		renderer.jogl.getGL2().glNewList(ret, GLlocal.GL_COMPILE);

		return ret;
	}

	private void newList(int index) {
		renderer.jogl.getGL2().glNewList(index, GLlocal.GL_COMPILE);
	}

	@Override
	public void endList() {
		renderer.jogl.getGL2().glEndList();
	}

	@Override
	public void startGeometry(Type type) {
		renderer.jogl.getGL2().glBegin(getGLType(type));
	}

	@Override
	public void endGeometry() {
		renderer.jogl.getGL2().glEnd();
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

		RendererTesselCallBack tessCallback = new RendererTesselCallBack(
				renderer);

		tesselator = renderer.glu.gluNewTess();

		renderer.glu.gluTessCallback(tesselator, GLU.GLU_TESS_VERTEX,
				tessCallback);// vertexCallback);
		renderer.glu.gluTessCallback(tesselator, GLU.GLU_TESS_BEGIN,
				tessCallback);// beginCallback);
		renderer.glu
				.gluTessCallback(tesselator, GLU.GLU_TESS_END, tessCallback);// endCallback);
		renderer.glu.gluTessCallback(tesselator, GLU.GLU_TESS_ERROR,
				tessCallback);// errorCallback);
		renderer.glu.gluTessCallback(tesselator, GLU.GLU_TESS_COMBINE,
				tessCallback);// combineCallback);

		newList(ret);

		return ret;
	}

	@Override
	public void drawPolygon(Coords n, Coords[] v) {

		// starts the polygon
		renderer.glu.gluTessBeginPolygon(tesselator, null);
		renderer.glu.gluTessBeginContour(tesselator);

		// set normal
		float nx = (float) n.getX();
		float ny = (float) n.getY();
		float nz = (float) n.getZ();
		renderer.glu.gluTessNormal(tesselator, nx, ny, nz);
		normal(nx, ny, nz);

		// set texture
		setDummyTexture();

		// set vertices
		for (int i = 0; i < v.length; i++) {
			double[] point = v[i].get();
			renderer.glu.gluTessVertex(tesselator, point, 0, point);
		}

		// end the polygon
		renderer.glu.gluTessEndContour(tesselator);
		renderer.glu.gluTessEndPolygon(tesselator);

	}

	@Override
	public void endPolygons() {

		renderer.jogl.getGL2().glEndList();
		renderer.glu.gluDeleteTess(tesselator);
	}

	/**
	 * remove the polygon from gl memory
	 * 
	 * @param index
	 */
	@Override
	public void remove(int index) {

		renderer.jogl.getGL2().glDeleteLists(index, 1);
	}

	// ///////////////////////////////////////////
	// DRAWING METHODS
	// ///////////////////////////////////////////

	@Override
	public void draw(int index) {
		renderer.jogl.getGL2().glCallList(index);
	}

	@Override
	public void drawLabel(int index) {
		draw(index);
	}

	@Override
	protected void texture(double x, double y) {
		renderer.jogl.getGL2().glTexCoord2d(x, y);
	}

	@Override
	protected void setDummyTexture() {
		texture(0, 0);
	}

	@Override
	protected void normal(double x, double y, double z) {

		renderer.jogl.getGL2().glNormal3d(x, y, z);
	}

	@Override
	protected void vertex(double x, double y, double z) {

		renderer.jogl.getGL2().glVertex3d(x, y, z);
	}

	@Override
	protected void vertexInt(double x, double y, double z) {

		//renderer.jogl.getGL2().glVertex3i(x, y, z);
		vertex(x,y,z);
	}

	@Override
	protected void vertices(double[] vertices) {
		renderer.jogl.getGL2().glVertex3dv(vertices, 0);
	}

	@Override
	protected void color(double r, double g, double b) {
		renderer.jogl.getGL2().glColor3d(r, g, b);
	}

	@Override
	protected void color(double r, double g, double b, double a) {
		renderer.jogl.getGL2().glColor4d(r, g, b, a);
	}

	@Override
	protected void pointSize(double size) {
		renderer.jogl.getGL2().glPointSize((float) size);
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

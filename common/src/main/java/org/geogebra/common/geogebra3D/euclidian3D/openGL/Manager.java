package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.Matrix.Coords3;
import org.geogebra.common.kernel.discrete.PolygonTriangulation.TriangleFan;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

/**
 * Class that manage all geometry objects
 * 
 * @author mathieu
 *
 */
abstract public class Manager {

	public static enum Type { // quads and quad strips are not supported in
								// gwtgl
		TRIANGLE_STRIP, TRIANGLE_FAN, TRIANGLES, LINE_LOOP, LINE_STRIP
	}

	// geometries
	/** geometry : cursor */
	public PlotterCursor cursor;
	/** geometry : view in front of */
	private PlotterViewInFrontOf viewInFrontOf;
	/** brush */
	private PlotterBrush brush;
	/** surfaces */
	private PlotterSurface surface;
	/** text */
	private PlotterText text;
	/** mouse cursor */
	private PlotterMouseCursor mouseCursor;
	/** completing task cursor */
	private PlotterCompletingCursor completingCursor;

	// geogebra stuff
	private EuclidianView3D view3D;

	// coords stuff
	/** when drawing a cylinder, clock vectors to describe a circle */
	private Coords clockU = null;
	private Coords clockV = null;
	private Coords cylinderStart = null;
	private Coords cylinderEnd = null;
	private double cylinderThickness;
	private double textureStart, textureEnd;

	/**
	 * create a manager for geometries
	 * 
	 * @param renderer
	 *            openGL renderer
	 * @param view3D
	 *            3D view
	 */
	public Manager(Renderer renderer, EuclidianView3D view3D) {

		setRenderer(renderer);

		initGeometriesList();

		// creating geometries

		brush = newPlotterBrush();
		surface = newPlotterSurface();

		text = new PlotterText(this);

		cursor = new PlotterCursor(this);
		viewInFrontOf = new PlotterViewInFrontOf(this);

		mouseCursor = new PlotterMouseCursor(this);

		completingCursor = new PlotterCompletingCursor(this);

		// geogebra
		this.view3D = view3D;

	}

	/**
	 * 
	 * @return new plotter brush
	 */
	protected PlotterBrush newPlotterBrush() {
		return new PlotterBrush(this);
	}

	/**
	 * 
	 * @return new plotter surface
	 */
	protected PlotterSurface newPlotterSurface() {
		return new PlotterSurface(this);
	}

	/**
	 * init list of geometries
	 */
	protected void initGeometriesList() {
		// used only for shaders
	}

	/**
	 * set the renderer
	 * 
	 * @param renderer
	 *            renderer
	 */
	abstract protected void setRenderer(Renderer renderer);

	/**
	 * 
	 * @return the renderer
	 */
	abstract protected Renderer getRenderer();

	public PlotterViewInFrontOf getViewInFrontOf() {
		return viewInFrontOf;
	}

	public PlotterBrush getBrush() {
		return brush;
	}

	public PlotterSurface getSurface() {
		return surface;
	}

	protected PlotterText getText() {
		return text;
	}

	public PlotterMouseCursor getMouseCursor() {
		return mouseCursor;
	}

	public PlotterCompletingCursor getCompletingCursor() {
		return completingCursor;
	}

	// ///////////////////////////////////////////
	// GEOGEBRA METHODS
	// ///////////////////////////////////////////

	/**
	 * return the 3D view
	 * 
	 * @return the 3D view
	 */
	public EuclidianView3D getView3D() {
		return view3D;
	}

	// ///////////////////////////////////////////
	// LIST METHODS
	// ///////////////////////////////////////////

	abstract public int startNewList(int old);

	abstract public void endList();

	abstract public void startGeometry(Type type);
	
	/**
	 * direct write in buffer mode
	 * @param type geometry type
	 * @param size number of vertices
	 */
	public void startGeometryDirect(Type type, int size){
		startGeometry(type);
	}

	abstract public void endGeometry();
	
	/**
	 * end current geometry (direct buffer mode)
	 */
	public void endGeometryDirect(){
		endGeometry();
	}

	/**
	 * end current geometry (only with shaders + elements, with type)
	 * 
	 * @param size
	 *            geometry size
	 * @param type
	 *            geometry type
	 */
	public void endGeometry(int size, TypeElement type) {
		// not used for all managers
	}

	/**
	 * start drawing polygons
	 * 
	 * @return geometry index for the polygons
	 */
	abstract public int startPolygons(int old);

	/**
	 * draw a polygon
	 * 
	 * @param n
	 *            normal
	 * @param v
	 *            vertices
	 */
	abstract public void drawPolygon(Coords n, Coords[] v);

	/**
	 * draw a convex polygon
	 * 
	 * @param n
	 *            normal
	 * @param v
	 *            vertices
	 * @param length
	 *            vertices length (maybe different from v.length due to cache)
	 * @param reverse
	 *            vertex order has to be reversed
	 */
	public void drawPolygonConvex(Coords n, Coords[] v, int length,
			boolean reverse) {

		startGeometry(Type.TRIANGLE_FAN);

		// set texture
		setDummyTexture();

		// set normal
		normal(n);

		triangleFanApex(v[0]);

		if (reverse) {
			for (int i = length - 1; i > 0; i--) {
				triangleFanVertex(v[i]);
			}
		} else {
			for (int i = 1; i < length; i++) {
				triangleFanVertex(v[i]);
			}
		}

		endGeometry();
	}

	/**
	 * set dummy texture (needed for GLList)
	 */
	abstract protected void setDummyTexture();

	/**
	 * draw a triangle fan
	 * 
	 * @param n
	 *            normal
	 * @param v
	 *            vertices
	 * @param triFan
	 *            indices
	 */
	final private void drawTriangleFan(Coords n, Coords[] v, TriangleFan triFan) {
		startGeometry(Type.TRIANGLE_FAN);

		// set texture
		setDummyTexture();

		// set normal
		normal(n);

		// fan apex
		triangleFanApex(v[triFan.getApexPoint()]);

		// int i = 0;
		for (int i = 0; i < triFan.size(); i++) {
			triangleFanVertex(v[triFan.getVertexIndex(i)]);
		}

		endGeometry();
	}

	/**
	 * end the polygons
	 */
	abstract public void endPolygons();

	// ///////////////////////////////////////////
	// DRAWING METHODS
	// ///////////////////////////////////////////

	abstract public void draw(int index);

	abstract public void drawLabel(int index);

	/**
	 * draw in object format
	 * 
	 * @param geo
	 *            geo
	 * @param index
	 *            index
	 */
	public void drawInObjFormat(GeoElement geo, int index) {
		App.error(".obj format not possible with this manager");
	}

	abstract public void remove(int index);

	/**
	 * creates a vertex at coordinates (x,y,z)
	 * 
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param z
	 *            z coord
	 */
	abstract protected void vertex(double x, double y, double z);

	/**
	 * creates a vertex at coordinates (x,y,z) (direct buffer mode)
	 * 
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param z
	 *            z coord
	 */
	protected void vertexDirect(double x, double y, double z){
		vertex(x, y, z);
	}

	/**
	 * creates a vertex at coordinates (x,y,z)
	 * 
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param z
	 *            z coord
	 */
	abstract protected void vertexInt(double x, double y, double z);

	/**
	 * creates a vertex at coordinates v
	 * 
	 * @param v
	 */
	protected void vertex(Coords v) {
		vertex(v.getX(), v.getY(), v.getZ());
	}
	
	/**
	 * creates a vertex at coordinates v (direct buffer mode)
	 * 
	 * @param v
	 */
	protected void vertexDirect(Coords3 v) {
		vertexDirect(v.getXf(), v.getYf(), v.getZf());
	}

	/**
	 * set apex for triangle fan
	 * 
	 * @param v
	 *            apex coords
	 */
	protected void triangleFanApex(Coords v) {
		vertex(v);
	}

	/**
	 * set vertex for triangle fan
	 * 
	 * @param v
	 *            apex coords
	 */
	protected void triangleFanVertex(Coords v) {
		vertex(v);
	}

	/**
	 * fill array of vertices
	 * 
	 * @param vertices
	 *            array of vertices
	 */
	abstract protected void vertices(double[] vertices);

	/**
	 * creates a normal at coordinates (x,y,z)
	 * 
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param z
	 *            z coord
	 */
	abstract protected void normal(double x, double y, double z);

	/**
	 * creates a normal at coordinates (x,y,z) (direct buffer mode)
	 * 
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param z
	 *            z coord
	 */
	protected void normalDirect(double x, double y, double z){
		normal(x, y, z);
	}

	/**
	 * creates a normal at coordinates n
	 * 
	 * @param n
	 */
	protected void normal(Coords n) {
		normal(n.getX(), n.getY(), n.getZ());
	}

	/**
	 * creates a normal at coordinates n (direct buffer mode)
	 * 
	 * @param n
	 */
	protected void normalDirect(Coords3 n) {
		normalDirect(n.getXf(), n.getYf(), n.getZf());
	}

	/**
	 * creates a texture at coordinates (x,y)
	 * 
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 */
	abstract protected void texture(double x, double y);

	/**
	 * creates a color (r,g,b)
	 * 
	 * @param r
	 *            red
	 * @param g
	 *            green
	 * @param b
	 *            blue
	 * 
	 */
	abstract protected void color(double r, double g, double b);

	/**
	 * creates a color (r,g,b,a)
	 * 
	 * @param r
	 *            red
	 * @param g
	 *            green
	 * @param b
	 *            blue
	 * @param a
	 *            blue alpha
	 * 
	 */
	abstract protected void color(double r, double g, double b, double a);

	/**
	 * set the line width (for GL_LINE rendering)
	 * 
	 * @param width
	 *            width
	 */
	final protected void lineWidth(double width) {
		getRenderer().setLineWidth(width);
	}

	/**
	 * set the point size (for GL_POINT rendering)
	 * 
	 * @param size
	 *            size
	 */
	abstract protected void pointSize(double size);

	// ///////////////////////////////////////////
	// COORDS METHODS
	// ///////////////////////////////////////////

	/**
	 * set a cylinder coords regarding vector direction
	 * 
	 * @param p1
	 * @param p2
	 * @param thickness
	 * @param textureStart
	 * @param textureEnd
	 */
	public void setCylinder(Coords p1, Coords p2, double thickness,
			double textureStart, double textureEnd) {
		cylinderStart = p1;
		cylinderEnd = p2;
		cylinderThickness = thickness;
		this.textureStart = textureStart;
		this.textureEnd = textureEnd;
		Coords[] vn = p2.sub(p1).completeOrthonormal();
		clockU = vn[0];
		clockV = vn[1];
	}

	/**
	 * translate the current cylinder
	 * 
	 * @param v
	 */
	public void translateCylinder(Coords v) {
		cylinderStart = cylinderStart.add(v);
		cylinderEnd = cylinderEnd.add(v);
	}

	/**
	 * create a cylinder rule (for quad strip)
	 * 
	 * @param u
	 * @param v
	 * @param texturePos
	 */
	public void cylinderRule(double u, double v, double texturePos) {

		// normal vector
		Coords vn = clockV.mul(v).add(clockU.mul(u));
		normal(vn.getX(), vn.getY(), vn.getZ());

		// bottom vertex
		texture(textureStart, texturePos);
		vertex((cylinderStart.getX() + cylinderThickness * vn.getX()),
				(cylinderStart.getY() + cylinderThickness * vn.getY()),
				(cylinderStart.getZ() + cylinderThickness * vn.getZ()));
		// top vertex
		texture(textureEnd, texturePos);
		vertex((cylinderEnd.getX() + cylinderThickness * vn.getX()),
				(cylinderEnd.getY() + cylinderThickness * vn.getY()),
				(cylinderEnd.getZ() + cylinderThickness * vn.getZ()));
	}

	// ///////////////////////////////////////////
	// COLOR METHODS
	// ///////////////////////////////////////////

	/**
	 * return the color for highlighting object regarding time
	 * 
	 * @param color
	 *            normal color
	 * @param colorHighlighted
	 *            highlighting color
	 * @return the color for highlighting object
	 */
	public Coords getHigthlighting(Coords color, Coords colorHighlighted) {

		// return
		// color.mul(colorFactor).add(colorHighlighted.mul(1-colorFactor));
		return colorHighlighted;
	}

	/**
	 * draws a rectangle
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param width
	 * @param height
	 */
	final public int rectangle(double x, double y, double z, double width,
			double height, int old) {
		int index = startNewList(old);
		rectangleGeometry(x, y, z, width, height);
		endList();
		return index;
	}

	abstract protected void rectangleGeometry(double x, double y, double z,
			double width, double height);

	public int rectangleBounds(double x, double y, double z, double width,
			double height,
			int old) {
		int index = startNewList(old);
		getText().rectangleBounds(x, y, z, width, height);
		endList();
		return index;
	}

	/**
	 * 
	 * @return max number of longitudes
	 */
	public int getLongitudeMax() {
		return 1024;
	}

	/**
	 * 
	 * @return default number of longitudes
	 */
	public int getLongitudeDefault() {
		return 64;
	}
	
	/**
	 * 
	 * @param radius circle radius
	 * @param viewScale view scale
	 * @return correct longitudes size regarding radius * viewScale
	 */
	public int getLongitude(double radius, double viewScale){
		int longitude = 8;
		double size = radius * viewScale;
		// App.error(""+size);
		while (longitude < 2 * size
				&& longitude < getLongitudeDefault()) {// find the correct
															// longitude size
			longitude *= 2;
		}

		// App.debug("getLongitude="+longitude);
		return longitude;
	}

	/**
	 * draw a point
	 * 
	 * @param size
	 *            size
	 * @param center
	 *            center
	 * @return geometry index
	 */
	public int drawPoint(int size, Coords center, int index) {

		double radius = size / view3D.getScale()
				* DrawPoint3D.DRAW_POINT_FACTOR;
		center.setW(1); // changed for shaders (point size)

		return drawSphere(size, center, radius, index);
	}

	/**
	 * draws a sphere
	 * 
	 * @param size
	 *            point size
	 * @param center
	 *            center
	 * @param radius
	 *            sphere radius
	 * @return geometry index
	 */
	protected int drawSphere(int size, Coords center, double radius, int index) {
		surface.start(index);
		surface.drawSphere(size, center, radius);

		return surface.end();
	}

	/**
	 * draw indexed geometry with center information
	 * 
	 * @param index
	 *            geometry
	 * @param center
	 *            center
	 */
	public void draw(int index, Coords center) {
		draw(index);
	}


	/**
	 * draw all triangles fans
	 * 
	 * @param n
	 *            normal
	 * @param verticesWithIntersections
	 *            all vertices needed for fans
	 * @param length
	 *            points used
	 * @param triFanList
	 *            fans list
	 */
	public void drawTriangleFans(Coords n, Coords[] verticesWithIntersections,
			int length, ArrayList<TriangleFan> triFanList) {
		for (TriangleFan triFan : triFanList) {
			drawTriangleFan(n, verticesWithIntersections, triFan);
		}
	}

	/**
	 * (used only for elements)
	 * 
	 * @param size
	 *            size
	 * @return current geometry indices buffer with correct size
	 */
	public GLBufferIndices getCurrentGeometryIndices(int size) {
		return null;
	}


}

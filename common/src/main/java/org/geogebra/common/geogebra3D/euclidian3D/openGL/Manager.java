package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;
import org.geogebra.common.kernel.discrete.PolygonTriangulation.TriangleFan;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.kernel.matrix.Coords3;

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
	private Coords normalToScaleTmp = new Coords(3);

	/**
	 * current scaler (identity/3D view)
	 */
	protected ScalerXYZ scalerXYZ;

	private Coords boundsMin;
	private Coords boundsMax;

	private GLBufferIndices curvesIndices;
	private GLBufferIndices fanDirectIndices;
	private GLBufferIndices fanIndirectIndices;
	private int curvesIndicesSize;
	private int fanDirectIndicesSize;
	private int fanIndirectIndicesSize;

	/**
	 * create a manager for geometries
	 * 
	 * @param renderer
	 *            openGL renderer
	 * @param view3D
	 *            3D view
	 */
	public Manager(Renderer renderer, EuclidianView3D view3D) {
		init(renderer, view3D);
	}

	public Manager() {
		// empty constructor
	}

	final protected void init(Renderer renderer, EuclidianView3D newView3D) {

		// geogebra
		this.view3D = newView3D;
		setScalerView();

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

	}

	/**
	 * set the 3D view
	 * 
	 * @param view3D
	 *            3D view
	 */
	public Manager(EuclidianView3D view3D) {
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
		curvesIndicesSize = -1;
		fanDirectIndicesSize = -1;
		fanIndirectIndicesSize = -1;
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

	abstract public int startNewList(int old, boolean mayBePacked);

	abstract public void endList();

	abstract public void startGeometry(Type type);

	/**
	 * direct write in buffer mode
	 * 
	 * @param type
	 *            geometry type
	 * @param size
	 *            number of vertices
	 */
	public void startGeometryDirect(Type type, int size) {
		startGeometry(type);
	}

	abstract public void endGeometry();

	/**
	 * end current geometry (direct buffer mode)
	 */
	public void endGeometryDirect() {
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
		endGeometry();
	}

	/**
	 * start drawing polygons
	 * 
	 * @param d
	 *            3D drawable
	 * 
	 * @return geometry index for the polygons
	 */
	abstract public int startPolygons(Drawable3D d);

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
		normalToScale(n);

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
	private void drawTriangleFan(Coords n, Coords[] v,
			TriangleFan triFan) {
		startGeometry(Type.TRIANGLE_FAN);

		// set texture
		setDummyTexture();

		// set normal
		normalToScale(n);

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
	 * 
	 * @param d
	 *            3D drawable
	 */
	abstract public void endPolygons(Drawable3D d);

	// ///////////////////////////////////////////
	// DRAWING METHODS
	// ///////////////////////////////////////////

	abstract public void draw(int index);

	abstract public void drawLabel(int index);

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
	protected void vertexDirect(double x, double y, double z) {
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
	 *            vertex
	 */
	final protected void vertex(Coords v) {
		vertex(v.getX(), v.getY(), v.getZ());
	}

	/**
	 * scale vertex and draw it
	 * 
	 * @param v
	 *            vertex
	 */
	final protected void vertexToScale(Coords v) {
		vertexToScale(v.getX(), v.getY(), v.getZ());
	}

	/**
	 * scale vertex and draw it
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @param z
	 *            z
	 * 
	 */
	final protected void vertexToScale(double x, double y, double z) {
		vertex(x * getXscale(), y * getYscale(), z * getZscale());
	}

	/**
	 * creates a vertex at coordinates v (direct buffer mode)
	 * 
	 * @param v
	 *            vertex
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
		vertexToScale(v);
	}

	/**
	 * set vertex for triangle fan
	 * 
	 * @param v
	 *            apex coords
	 */
	protected void triangleFanVertex(Coords v) {
		vertexToScale(v);
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
	protected void normalDirect(double x, double y, double z) {
		normal(x, y, z);
	}

	/**
	 * creates a normal at coordinates n
	 * 
	 * @param n
	 *            normal
	 */
	protected void normal(Coords n) {
		normal(n.getX(), n.getY(), n.getZ());
	}
	
	/**
	 * scale normal and draw it
	 * 
	 * @param n
	 *            normal
	 */
	final protected void normalToScale(Coords n) {
		if (getScalerXYZ().scaleAndNormalizeNormalXYZ(n, normalToScaleTmp)) {
			normal(normalToScaleTmp);
		} else {
			normal(n);
		}
	}

	/**
	 * creates a normal at coordinates n (direct buffer mode)
	 * 
	 * @param n
	 *            normal
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
	 * creates a texture at coordinate (x)
	 * 
	 * @param x
	 *            x coord
	 */
	protected void texture(double x) {
		texture(x, 0);
	}

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
	// COLOR METHODS
	// ///////////////////////////////////////////

	/**
	 * draws a rectangle
	 * 
	 * @param x
	 *            vertex x-coord
	 * @param y
	 *            vertex y-coord
	 * @param z
	 *            vertex z-coord
	 * @param width
	 *            width
	 * @param height
	 *            height
	 * @param old
	 *            index
	 * @return new index
	 */
	final public int rectangle(double x, double y, double z, double width,
			double height, int old) {
		int index = startNewList(old, false);
		rectangleGeometry(x, y, z, width, height);
		endList();
		return index;
	}

	abstract protected void rectangleGeometry(double x, double y, double z,
			double width, double height);

	/**
	 * @param x
	 *            vertex x-coord
	 * @param y
	 *            vertex y-coord
	 * @param z
	 *            vertex z-coord
	 * @param width
	 *            width
	 * @param height
	 *            height
	 * @param old
	 *            index
	 * @param lineWidth
	 *            bounds line width
	 * @return new index
	 */
	public int rectangleBounds(double x, double y, double z, double width,
			double height, int old, double lineWidth) {
		int index = startNewList(old, false);
		getText().rectangleBounds(x, y, z, width, height, lineWidth);
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
	 * @param radius
	 *            circle radius
	 * @param viewScale
	 *            view scale
	 * @return correct longitudes size regarding radius * viewScale
	 */
	public int getLongitude(double radius, double viewScale) {
		int longitude = 8;
		double size = radius * viewScale;
		// App.error(""+size);
		while (longitude < 2 * size && longitude < getLongitudeDefault()) {
			// find the correct longitude size
			longitude *= 2;
		}

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
	public int drawPoint(DrawPoint3D d, float size, Coords center, int index) {

		double radius = size * DrawPoint3D.DRAW_POINT_FACTOR;
		scaleXYZ(center);
		center.setW(1); // changed for shaders (point size)

		setScalerIdentity();
		int ret = drawSphere(size, center, radius, index);
		setScalerView();

		return ret;
	}

	/**
	 * draw a dot (for right angle)
	 * 
	 * @param d
	 *            drawable
	 * @param size
	 *            dot size
	 * @param center
	 *            center
	 */
	public void drawPoint(Drawable3D d, float size, Coords center) {
		setScalerIdentity();
		getView3D().scaleXYZ(center);
		surface.drawSphere(center, 2.5 * size, 16);
		setScalerView();
	}

	/**
	 * 
	 * @param size
	 *            point size
	 */
	public void createPointTemplateIfNeeded(int size) {
		// implemented for packed buffers
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
	 * @param index
	 *            old geometry index
	 * @return geometry index
	 */
	final protected int drawSphere(float size, Coords center, double radius,
			int index) {
		surface.start(index);
		surface.drawSphere(size, center, radius);

		return surface.end();
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

	/**
	 * simple interface to scale coords
	 *
	 */
	public interface ScalerXYZ {
		/**
		 * scale x, y, z values
		 * 
		 * @param coords
		 *            coords
		 */
		public void scaleXYZ(Coords coords);
		
		/**
		 * scale and normalize x, y, z values
		 * 
		 * @param coords
		 *            coords
		 *            
		 * @return false if nothing scaled (then use coords instead of ret)
		 */
		public boolean scaleAndNormalizeNormalXYZ(Coords coords, Coords ret);

		/**
		 * @return scale on x-axis
		 */
		public double getXscale();

		/**
		 * @return scale on y-axis
		 */
		public double getYscale();

		/**
		 * @return scale on z-axis
		 */
		public double getZscale();
	}

	/**
	 * identity scaler
	 */
	protected static final ScalerXYZ scalerXYZIdentity = new ScalerXYZ() {
		@Override
		public void scaleXYZ(Coords coords) {
			// do nothing
		}
		
		@Override
		public boolean scaleAndNormalizeNormalXYZ(Coords coords, Coords ret) {
			// do nothing
			return false;
		}

		@Override
		public double getXscale() {
			return 1;
		}

		@Override
		public double getYscale() {
			return 1;
		}

		@Override
		public double getZscale() {
			return 1;
		}
	};
	
	protected ScalerXYZ getScalerXYZ() {
		return scalerXYZ;
	}

	/**
	 * scale coords using current scaler
	 * 
	 * @param coords
	 *            coords
	 */
	public void scaleXYZ(Coords coords) {
		getScalerXYZ().scaleXYZ(coords);
	}

	/**
	 * @return scale on x-axis
	 */
	public double getXscale() {
		return getScalerXYZ().getXscale();
	}

	/**
	 * @return scale on y-axis
	 */
	public double getYscale() {
		return getScalerXYZ().getYscale();
	}

	/**
	 * @return scale on z-axis
	 */
	public double getZscale() {
		return getScalerXYZ().getZscale();
	}

	/**
	 * set scaler to identity
	 */
	public void setScalerIdentity() {
		scalerXYZ = scalerXYZIdentity;
	}

	/**
	 * set scaler to view
	 */
	public void setScalerView() {
		scalerXYZ = view3D;
	}

	/**
	 * @return true if it packs the buffers
	 */
	public boolean packBuffers() {
		return false;
	}

	/**
	 * set manager in packing mode for surface
	 * 
	 * @param d
	 *            drawable
	 * @param clipped TODO
	 */
	public void setPackSurface(Drawable3D d, boolean clipped) {
		// not needed here
	}

	/**
	 * set manager in packing mode for curves
	 * 
	 * @param d
	 *            drawable calling
	 * @param clipped
	 *            curve is clipped
	 */
	public void setPackCurve(Drawable3D d, boolean clipped) {
		// not needed here
	}

	/**
	 * set manager off packing mode
	 * 
	 */
	public void endPacking() {
		// not needed here
	}

	/**
	 * update geometry color
	 * 
	 * @param color
	 *            new color
	 * @param layer
	 *            layer
	 * @param index
	 *            geometry index (for set)
	 */
	public void updateColorAndLayer(GColor color, int layer, int index) {
		// not needed here
	}

	/**
	 * update geometry visibility
	 * 
	 * @param visible
	 *            if visible
	 * @param index
	 *            geometry index (for set)
	 * @param alpha
	 *            geometry alpha
	 * @param layer
	 *            geometry layer
	 */
	public void updateVisibility(boolean visible, int index, int alpha, int layer) {
		// not needed here
	}

	/**
	 * update
	 * 
	 * @param reset
	 *            if needs reset
	 */
	public void update(boolean reset) {
		// not needed here
	}

	/**
	 * set bounds recorders
	 * 
	 * @param min
	 *            min
	 * @param max
	 *            max
	 */
	public void setBoundsRecorders(Coords min, Coords max) {
		boundsMin = min;
		boundsMax = max;
	}

	/**
	 * set this to have no bounds recorders
	 */
	public void setNoBoundsRecorders() {
		boundsMin = null;
		boundsMax = null;
	}

	/**
	 * enlarge bounds to contain this point
	 * 
	 * @param point
	 *            point
	 */
	public void enlargeBounds(Coords point) {
		if (boundsMin != null) {
			Drawable3D.enlargeBounds(boundsMin, boundsMax, point);
		}
	}

	/**
	 *
	 * @param size
	 *            sections size
	 * @return GPU buffer for curve indices, update it if current is not big
	 *         enough
	 */
	public final GLBufferIndices getBufferIndicesForCurve(int size) {

		if (size > curvesIndicesSize) {
			// creates indices buffer
			if (curvesIndices == null) {
				curvesIndices = GLFactory.getPrototype().newBufferIndices();
			}
			curvesIndices.allocate(3 * 2 * size * PlotterBrush.LATITUDES);

			for (int k = 0; k < size; k++) {
				for (int i = 0; i < PlotterBrush.LATITUDES; i++) {
					int iNext = (i + 1) % PlotterBrush.LATITUDES;
					// first triangle
					curvesIndices.put((short) (i + k * PlotterBrush.LATITUDES));
					curvesIndices.put(
							(short) (i + (k + 1) * PlotterBrush.LATITUDES));
					curvesIndices.put(
							(short) (iNext + (k + 1) * PlotterBrush.LATITUDES));
					// second triangle
					curvesIndices.put((short) (i + k * PlotterBrush.LATITUDES));
					curvesIndices.put(
							(short) (iNext + (k + 1) * PlotterBrush.LATITUDES));
					curvesIndices
							.put((short) (iNext + k * PlotterBrush.LATITUDES));
				}
			}
			curvesIndices.rewind();
			curvesIndicesSize = size;
		}

		return curvesIndices;
	}

	/**
	 *
	 * @param size
	 *            sections size
	 * @return GPU buffer for direct fan indices, update it if current is not
	 *         big enough
	 */
	public final GLBufferIndices getBufferIndicesForFanDirect(int size) {

		if (size > fanDirectIndicesSize) {
			// creates indices buffer
			if (fanDirectIndices == null) {
				fanDirectIndices = GLFactory.getPrototype().newBufferIndices();
			}
			fanDirectIndices.allocate(3 * (size - 2));

			short k = 1;
			short zero = 0;
			while (k < size - 1) {
				fanDirectIndices.put(zero);
				fanDirectIndices.put(k);
				k++;
				fanDirectIndices.put(k);
			}

			fanDirectIndices.rewind();
			fanDirectIndicesSize = size;
		}

		return fanDirectIndices;
	}

	/**
	 *
	 * @param size
	 *            sections size
	 * @return GPU buffer for indirect fan indices, update it if current is not
	 *         big enough
	 */
	public final GLBufferIndices getBufferIndicesForFanIndirect(int size) {

		if (size > fanIndirectIndicesSize) {

			// creates indices buffer
			if (fanIndirectIndices == null) {
				fanIndirectIndices = GLFactory.getPrototype()
						.newBufferIndices();
			}
			fanIndirectIndices.allocate(3 * (size - 2));

			short k2 = 2;
			short k = 1;
			short zero = 0;
			while (k < size - 1) {
				fanIndirectIndices.put(zero);
				fanIndirectIndices.put(k2);
				fanIndirectIndices.put(k);
				k++;
				k2++;
			}

			fanIndirectIndices.rewind();
			fanIndirectIndicesSize = size;
		}

		return fanIndirectIndices;
	}
}

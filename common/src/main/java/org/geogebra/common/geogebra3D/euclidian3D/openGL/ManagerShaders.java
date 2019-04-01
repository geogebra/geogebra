package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeMap;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.discrete.PolygonTriangulation.TriangleFan;

/**
 * 
 * Manager using shaders
 * 
 * @author ggb3D
 *
 */
abstract public class ManagerShaders extends Manager {

	protected Renderer renderer;
	private ArrayList<Double> vertices;
	private ArrayList<Double> normals;
	private ArrayList<Double> textures;
	private ArrayList<Double> colors;

	private int verticesLength;
	private int verticesSize;
	private int normalsLength;
	private int normalsSize;
	private int texturesLength;
	private int texturesSize;
	private int colorsLength;
	private int colorsSize;
	protected TreeMap<Integer, GeometriesSet> geometriesSetList;

	private int geometriesSetMaxIndex;

	protected GeometriesSet currentGeometriesSet;

	private Stack<Integer> indicesRemoved;

	private int currentOld;

	private GLBufferIndices curvesIndices;
	private GLBufferIndices fanDirectIndices;
	private GLBufferIndices fanIndirectIndices;
	private int curvesIndicesSize;
	private int fanDirectIndicesSize;
	private int fanIndirectIndicesSize;
	private GLBufferIndices bufferIndicesForDrawTriangleFans;

	boolean indicesDone = false;
	TypeElement oldType = TypeElement.NONE;

	/**
	 * number of templates for points
	 */
	final static public int POINT_TEMPLATES_COUNT = 3;

	private int[] pointGeometry;

	private Coords triangleFanApex;

	/**
	 * 
	 * @param pointSize
	 *            point size
	 * @return template index for this size
	 */
	static public int getIndexForPointSize(float pointSize) {
		return pointSize < 2.5f ? 0 : (pointSize > 5.5f ? 2 : 1);
	}

	/**
	 * 
	 * @param index
	 *            template index
	 * @return sphere size for template index
	 */
	static public int getSphereSizeForIndex(int index) {
		switch (index) {
		case 0:
			return 2;
		case 1:
			return 4;
		default:
			return 7;
		}
	}

	/**
	 * common constructor
	 * 
	 * @param renderer
	 *            rendereer
	 * @param view3D
	 *            3D view
	 */
	public ManagerShaders(Renderer renderer, EuclidianView3D view3D) {
		super(renderer, view3D);

		// points geometry templates
		setScalerIdentity();
		pointGeometry = new int[3];
		for (int i = 0; i < 3; i++) {
			pointGeometry[i] = drawSphere(getSphereSizeForIndex(i), Coords.O,
					1d, -1);
		}
		setScalerView();
	}

	public enum TypeElement {
		NONE, CURVE, SURFACE, FAN_DIRECT, FAN_INDIRECT, TRIANGLE_FAN,

		TRIANGLE_STRIP, TRIANGLES, TEMPLATE
	}

	@Override
	protected void initGeometriesList() {
		curvesIndicesSize = -1;
		fanDirectIndicesSize = -1;
		fanIndirectIndicesSize = -1;

		geometriesSetList = new TreeMap<>();
		geometriesSetMaxIndex = -1;
		indicesRemoved = new Stack<>();

		vertices = new ArrayList<>();
		verticesSize = 0;
		normals = new ArrayList<>();
		normalsSize = 0;
		textures = new ArrayList<>();
		texturesSize = 0;
		colors = new ArrayList<>();
		colorsSize = 0;
	}

	@Override
	protected void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}

	@Override
	protected Renderer getRenderer() {
		return renderer;
	}

	// ///////////////////////////////////////////
	// LISTS METHODS
	// ///////////////////////////////////////////

	@Override
	public int startNewList(int old, boolean mayBePacked) {

		currentOld = old;
		if (currentOld >= 0) {
			currentGeometriesSet = geometriesSetList.get(old);
			// don't use packed set for non-packed geometries
			if (currentGeometriesSet != null && currentGeometriesSet.usePacking() != mayBePacked) {
                currentGeometriesSet = null;
            }
		} else {
			currentGeometriesSet = null;
		}

		int index = currentOld;
		if (currentGeometriesSet == null) {
			currentOld = -1;

			if (indicesRemoved.empty()) {
				geometriesSetMaxIndex++;
				index = geometriesSetMaxIndex;
			} else {
				index = indicesRemoved.pop();
			}

			currentGeometriesSet = newGeometriesSet(mayBePacked);
			geometriesSetList.put(index, currentGeometriesSet);
			// Log.debug("newGeometriesSet : " + index);
		} else {
			currentGeometriesSet.reset();
			// Log.debug("reuse : " + index);
		}

		return index;
	}

	@Override
	public void endList() {
		// renderer.getGL2().glEndList();
	}

	// ///////////////////////////////////////////
	// GEOMETRY METHODS
	// ///////////////////////////////////////////

	@Override
	public void startGeometry(Type type) {
		currentGeometriesSet.startGeometry(type);
		verticesLength = 0;
		normalsLength = 0;
		texturesLength = 0;
		colorsLength = 0;
	}

	@Override
	public void endGeometry() {
		currentGeometriesSet.setVertices(vertices, verticesLength);
		currentGeometriesSet.setNormals(normals, normalsLength);
		currentGeometriesSet.setTextures(textures, texturesLength);
		currentGeometriesSet.setColors(colors, colorsLength);
		currentGeometriesSet.bindGeometry(-1, TypeElement.NONE); // TODO remove
																	// that
	}

	@Override
	public void endGeometry(int size, TypeElement type) {
		currentGeometriesSet.setVertices(vertices, verticesLength);
		currentGeometriesSet.setNormals(normals, normalsLength);
		currentGeometriesSet.setTextures(textures, texturesLength);
		currentGeometriesSet.setColors(colors, colorsLength);
		currentGeometriesSet.bindGeometry(size, type);
	}

	// ///////////////////////////////////////////
	// POLYGONS METHODS
	// ///////////////////////////////////////////

	@Override
	public int startPolygons(Drawable3D d) {
		int index = startNewList(d.getReusableSurfaceIndex(), true);
		return index;
	}

	@Override
	public void endPolygons(Drawable3D d) {
		endList();
	}

	@Override
	public void remove(int index) {

		if (index >= 0 && index != currentOld) { // negative index is for no
													// geometry
			indicesRemoved.push(index);
			removeGeometrySet(index);
		}

		currentOld = -1;
	}

	// ///////////////////////////////////////////
	// DRAWING METHODS
	// ///////////////////////////////////////////

	@Override
	public void draw(int index) {

		currentGeometriesSet = geometriesSetList.get(index);
		if (currentGeometriesSet != null) {
			for (int i = 0; i < currentGeometriesSet
					.getGeometriesLength(); i++) {
				currentGeometriesSet.get(i).draw(renderer);
			}
		}
	}

	/**
	 * @param index
	 *            index
	 * @return geometry
	 */
	public Geometry getGeometry(int index) {
		return geometriesSetList.get(index).get(0);
	}

	/**
	 * @param index
	 *            index
	 * @return geometry set
	 */
	public GeometriesSet getGeometrySet(int index) {
		return geometriesSetList.get(index);
	}

	@Override
	public void drawLabel(int index) {

		currentGeometriesSet = geometriesSetList.get(index);
		if (currentGeometriesSet != null && !currentGeometriesSet.usePacking()) {
			for (int i = 0; i < currentGeometriesSet
					.getGeometriesLength(); i++) {
				currentGeometriesSet.get(i).drawLabel(renderer);
			}
		}
	}

	@Override
	protected void texture(double x, double y) {

		if (texturesLength == texturesSize) {
			textures.add(x);
			textures.add(y);
			texturesSize += 2;
		} else {
			textures.set(texturesLength, x);
			textures.set(texturesLength + 1, y);
		}

		texturesLength += 2;
	}

	@Override
	protected void setDummyTexture() {
		// nothing needed for the shader
	}

	@Override
	protected void normal(double x, double y, double z) {

		if (normalsLength == normalsSize) {
			normals.add(x);
			normals.add(y);
			normals.add(z);
			normalsSize += 3;
		} else {
			normals.set(normalsLength, x);
			normals.set(normalsLength + 1, y);
			normals.set(normalsLength + 2, z);
		}

		normalsLength += 3;

	}

	@Override
	protected void vertex(double x, double y, double z) {

		if (verticesLength == verticesSize) {
			vertices.add(x);
			vertices.add(y);
			vertices.add(z);
			verticesSize += 3;
		} else {
			vertices.set(verticesLength, x);
			vertices.set(verticesLength + 1, y);
			vertices.set(verticesLength + 2, z);
		}

		verticesLength += 3;
	}

	@Override
	protected void vertexInt(double x, double y, double z) {
		vertex(x, y, z);
	}

	@Override
	protected void color(double r, double g, double b) {
		color(r, g, b, 1f);
	}

	@Override
	protected void color(double r, double g, double b, double a) {

		if (colorsLength == colorsSize) {
			colors.add(r);
			colors.add(g);
			colors.add(b);
			colors.add(a);
			colorsSize += 4;
		} else {
			colors.set(colorsLength, r);
			colors.set(colorsLength + 1, g);
			colors.set(colorsLength + 2, b);
			colors.set(colorsLength + 3, a);
		}

		colorsLength += 4;
	}

	@Override
	protected void pointSize(double size) {
		// renderer.getGL2().glPointSize(size);
	}

	@Override
	protected void vertices(double[] vert) {
		// TODO Auto-generated method stub
	}

	@Override
	public void rectangleGeometry(double x, double y, double z, double width,
			double height) {

		startGeometry(Manager.Type.TRIANGLE_STRIP);
		texture(0, 0);
		vertexInt(x, y, z);
		texture(1, 0);
		vertexInt(x + width, y, z);
		texture(0, 1);
		vertexInt(x, y + height, z);
		texture(1, 1);
		vertexInt(x + width, y + height, z);
		endGeometry();

	}

	/*
	 * @Override public void rectangleBounds(int x, int y, int z, int width, int
	 * height){ getText().rectangleBounds(x, y, z, width, height); }
	 */

	@Override
	public void startGeometryDirect(Type type, int size) {
		startGeometry(type);
		currentGeometriesSet.allocate(size);
	}

	@Override
	protected void vertexDirect(double x, double y, double z) {
		currentGeometriesSet.vertexDirect(x, y, z);
	}

	@Override
	protected void normalDirect(double x, double y, double z) {
		currentGeometriesSet.normalDirect(x, y, z);
	}

	@Override
	public void endGeometryDirect() {
		currentGeometriesSet.endGeometry();
	}

	@Override
	public int drawPoint(DrawPoint3D d, float size, Coords center, int index) {
		scaleXYZ(center);
		return pointGeometry[getIndexForPointSize(size)];
	}

	@Override
	public void draw(int index, Coords center) {
		renderer.getRendererImpl().setCenter(center);
		draw(index);
	}

	@Override
	protected void triangleFanApex(Coords v) {
		triangleFanApex = v.copyVector();
	}

	@Override
	protected void triangleFanVertex(Coords v) {
		vertexToScale(triangleFanApex);
		vertexToScale(v);
	}

	@Override
	public int getLongitudeMax() {
		return 64;
	}

	@Override
	public int getLongitudeDefault() {
		return getLongitudeMax();
	}

	/**
	 * 
	 * @param r
	 *            renderer
	 * @param size
	 *            sections size
	 * @return GPU buffer for curve indices, update it if current is not big
	 *         enough
	 */
	public final GLBufferIndices getBufferIndicesForCurve(Renderer r,
			int size) {

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
	 * @param r
	 *            renderer
	 * @param size
	 *            sections size
	 * @return GPU buffer for direct fan indices, update it if current is not
	 *         big enough
	 */
	public final GLBufferIndices getBufferIndicesForFanDirect(Renderer r,
			int size) {

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
	 * @param r
	 *            renderer
	 * @param size
	 *            sections size
	 * @return GPU buffer for indirect fan indices, update it if current is not
	 *         big enough
	 */
	public final GLBufferIndices getBufferIndicesForFanIndirect(Renderer r,
			int size) {

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

	/**
	 * 
	 * @param mayBePacked
	 *            true if this set may be in packed buffer
	 * @return new geometries set
	 */
	protected GeometriesSet newGeometriesSet(boolean mayBePacked) {
		return new GeometriesSetElementsGlobalBuffer(this);
	}

	@Override
	protected PlotterBrush newPlotterBrush() {
		return new PlotterBrushElements(this);
	}

	@Override
	protected PlotterSurface newPlotterSurface() {
		return new PlotterSurfaceElements(this);
	}

	@Override
	public GLBufferIndices getCurrentGeometryIndices(int size) {
		return ((GeometryElementsGlobalBuffer) currentGeometriesSet.currentGeometry)
				.getBufferI(size);
	}

	/**
	 * remove geometry set at index
	 * 
	 * @param index
	 *            index
	 */
	final protected void removeGeometrySet(int index) {
		GeometriesSet set = removeGeometrySetFromList(index);
		if (set != null) {
			((GeometriesSetElementsGlobalBuffer) set).removeBuffers();
		}
	}

	/**
	 * remove geometry set corresponding to index
	 * 
	 * @param index
	 *            geometry set index
	 * @return geometry set corresponding to index (if exists)
	 */
	protected GeometriesSet removeGeometrySetFromList(int index) {
		return geometriesSetList.remove(index);
	}

	@Override
	public void drawPolygonConvex(Coords n, Coords[] v, int length,
			boolean reverse) {

		startGeometry(Type.TRIANGLES);

		// set texture
		setDummyTexture();

		// set normal
		normalToScale(n);

		// set vertices
		for (int i = 0; i < length; i++) {
			vertexToScale(v[i]);
		}

		if (reverse) {
			endGeometry(length, TypeElement.FAN_INDIRECT);
		} else {
			endGeometry(length, TypeElement.FAN_DIRECT);
		}

	}

	@Override
	public void drawTriangleFans(Coords n, Coords[] verticesWithIntersections,
			int length, ArrayList<TriangleFan> triFanList) {

		startGeometry(Type.TRIANGLES);

		// set texture
		setDummyTexture();

		// set normal
		normalToScale(n);

		// set vertices
		for (int i = 0; i < length; i++) {
			vertexToScale(verticesWithIntersections[i]);
		}

		// indices
		int size = 0;
		for (TriangleFan triFan : triFanList) {
			size += triFan.size() - 1;
		}

		setIndicesForDrawTriangleFans(size);

		for (TriangleFan triFan : triFanList) {
			short apex = (short) triFan.getApexPoint();
			short current = (short) triFan.getVertexIndex(0);
			for (int i = 1; i < triFan.size(); i++) {
				putToIndicesForDrawTriangleFans(apex);
				putToIndicesForDrawTriangleFans(current);
				current = (short) triFan.getVertexIndex(i);
				putToIndicesForDrawTriangleFans(current);
			}
		}

		rewindIndicesForDrawTriangleFans();

		// end
		endGeometry(3 * size, TypeElement.SURFACE);
	}

	/**
	 * set indices reference when drawing triangle fans
	 * 
	 * @param size
	 *            number of triangles
	 */
	protected void setIndicesForDrawTriangleFans(int size) {
		bufferIndicesForDrawTriangleFans = getCurrentGeometryIndices(size * 3);
	}

	/**
	 * put new index to indices buffer
	 * 
	 * @param index
	 *            index
	 */
	protected void putToIndicesForDrawTriangleFans(short index) {
		bufferIndicesForDrawTriangleFans.put(index);
	}

	/**
	 * rewind indices buffer
	 */
	protected void rewindIndicesForDrawTriangleFans() {
		bufferIndicesForDrawTriangleFans.rewind();
	}

}

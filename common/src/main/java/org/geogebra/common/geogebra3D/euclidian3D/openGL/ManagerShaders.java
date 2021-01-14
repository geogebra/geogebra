package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeMap;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.kernel.discrete.PolygonTriangulation.TriangleFan;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * 
 * Manager using shaders
 * 
 * @author ggb3D
 *
 */
public class ManagerShaders extends Manager {

	private Renderer renderer;
	private ReusableArrayList<Double> vertices;
	private ReusableArrayList<Double> normals;
	private ReusableArrayList<Double> textures;
	private ReusableArrayList<Double> colors;

	private TreeMap<Integer, GeometriesSet> geometriesSetList;

	private int geometriesSetMaxIndex;

	private GeometriesSet currentGeometriesSet;

	private Stack<Integer> indicesRemoved;

	private int currentOld;

	private GLBufferIndices bufferIndicesForDrawTriangleFans;

	private Coords triangleFanApex;

	/**
	 * alpha value for invisible parts
	 */
	static final public float ALPHA_INVISIBLE_VALUE = -1f;
	/** color value for invisible parts */
	public static final GColor COLOR_INVISIBLE = GColor.newColor(0, 0, 0, 0);

	private GLBufferManagerCurves bufferManagerCurves;
	private GLBufferManagerCurvesClipped bufferManagerCurvesClipped;
	private GLBufferManagerSurfaces bufferManagerSurfaces;
	private GLBufferManagerSurfaces bufferManagerSurfacesClosed;
	private GLBufferManagerSurfacesClipped bufferManagerSurfacesClipped;
	private GLBufferManagerPoints bufferManagerPoints;
	private GLBufferManagerTemplatesForPoints bufferTemplates;
	private GLBufferManager currentBufferManager;
	private GColor currentColor;
	private int currentLayer;
	private int currentTextureType;
	private GLBufferIndicesArray indices;
	private float[] translate;
	private float scale;

	private boolean indicesDone;
	private TypeElement oldType;

	/** element type */
	public enum TypeElement {
		/** no known type */
		NONE,
		/** curve */
		CURVE,
		/** surface */
		SURFACE,
		/** fan, direct */
		FAN_DIRECT,
		/** fan, indirect */
		FAN_INDIRECT,
		/** triangle fan */
		TRIANGLE_FAN,
		/** triangle strip */
		TRIANGLE_STRIP,
		/** triangles */
		TRIANGLES,
		/** template */
		TEMPLATE
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

		indicesDone = false;
		oldType = TypeElement.NONE;

		setScalerView();

		bufferTemplates = new GLBufferManagerTemplatesForPoints();
		bufferManagerCurves = new GLBufferManagerCurves(this);
		bufferManagerCurvesClipped = new GLBufferManagerCurvesClipped();
		bufferManagerSurfaces = new GLBufferManagerSurfaces(this);
		bufferManagerSurfacesClosed = new GLBufferManagerSurfaces(this);
		bufferManagerSurfacesClipped = new GLBufferManagerSurfacesClipped(this);
		bufferManagerPoints = new GLBufferManagerPoints(this);
		currentBufferManager = null;
		translate = new float[3];
	}

	@Override
	protected void initGeometriesList() {
		super.initGeometriesList();
		geometriesSetList = new TreeMap<>();
		geometriesSetMaxIndex = -1;
		indicesRemoved = new Stack<>();

		vertices = new ReusableArrayList<>();
		normals = new ReusableArrayList<>();
		textures = new ReusableArrayList<>();
		colors = new ReusableArrayList<>();
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

		currentGeometriesSet.setIndex(index, currentColor, currentLayer);

		return index;
	}

	@Override
	public void endList() {
		currentGeometriesSet.hideLastGeometries();
	}

	// ///////////////////////////////////////////
	// GEOMETRY METHODS
	// ///////////////////////////////////////////

	@Override
	public void startGeometry(Type type) {
		currentGeometriesSet.startGeometry(type);
		vertices.setLength(0);
		normals.setLength(0);
		textures.setLength(0);
		colors.setLength(0);
	}

	@Override
	public void endGeometry() {
		currentGeometriesSet.setVertices(vertices, vertices.getLength());
		currentGeometriesSet.setNormals(normals, normals.getLength());
		currentGeometriesSet.setTextures(textures, textures.getLength());
		currentGeometriesSet.setColors(colors, colors.getLength());
		currentGeometriesSet.bindGeometry(-1, TypeElement.NONE); // TODO remove
																	// that
	}

	@Override
	public void endGeometry(int size, TypeElement type) {
		currentGeometriesSet.setVertices(vertices, vertices.getLength());
		currentGeometriesSet.setNormals(normals, normals.getLength());
		currentGeometriesSet.setTextures(textures, textures.getLength());
		currentGeometriesSet.setColors(colors, colors.getLength());
		currentGeometriesSet.bindGeometry(size, type);
	}

	// ///////////////////////////////////////////
	// POLYGONS METHODS
	// ///////////////////////////////////////////

	@Override
	public int startPolygons(Drawable3D d) {
		setPackSurface(d, false);
		int index = startNewList(d.getReusableSurfaceIndex(), true);
		return index;
	}

	@Override
	public void endPolygons(Drawable3D d) {
		endList();
		endPacking();
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
		textures.addValues(x, y);
	}

	@Override
	protected void setDummyTexture() {
		// nothing needed for the shader
	}

	@Override
	protected void normal(double x, double y, double z) {
		normals.addValues(x, y, z);
	}

	@Override
	protected void vertex(double x, double y, double z) {
		vertices.addValues(x, y, z);
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
		colors.addValues(r, g, b, a);
	}

	@Override
	protected void pointSize(double size) {
		// no need
	}

	@Override
	protected void vertices(double[] vert) {
		// no need
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
		// get/create point geometry with template buffer
		setCurrentBufferManager(bufferTemplates);
		bufferTemplates.selectSphereAndCreateIfNeeded(this, size);
		// draw in points manager
		setCurrentBufferManager(bufferManagerPoints);
		this.currentColor = d.getColor();
		this.currentLayer = Renderer.LAYER_DEFAULT;
		setPointValues(size, DrawPoint3D.DRAW_POINT_FACTOR, center);
		int ret = bufferManagerPoints.drawPoint(index);
		setCurrentBufferManager(null);
		return ret;
	}

	@Override
	public void drawPoint(Drawable3D d, float size, Coords center) {
		// get/create point geometry with template buffer
		bufferTemplates.selectSphere((int) size);
		// draw point in current curve
		this.currentColor = d.getColor();
		this.currentLayer = Renderer.LAYER_DEFAULT;
		setPointValues(size, 2.5f, center);
		bufferManagerCurves.drawPoint();
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
	 * @param mayBePacked
	 *            true if this set may be in packed buffer
	 * @return new geometries set
	 */
	protected GeometriesSet newGeometriesSet(boolean mayBePacked) {
		if (mayBePacked && currentBufferManager != null) {
			return new GeometriesSetPacking(this,
					currentBufferManager, currentColor, currentLayer);
		}
		return new GeometriesSet(this);
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
		if (currentBufferManager != null) {
			if (currentBufferManager.isTemplateForPoints()) {
				return bufferTemplates.getBufferIndicesArray();
			}
			if (currentBufferManager == bufferManagerSurfacesClosed
					|| currentBufferManager == bufferManagerSurfacesClipped) {
				initIndices(size);
				return indices;
			}
		}
		return currentGeometriesSet.getCurrentGeometry().getBufferI(size);
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
			set.removeBuffers();
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
		if (currentBufferManager == null) {
			bufferIndicesForDrawTriangleFans = getCurrentGeometryIndices(
					size * 3);
		} else {
			initIndices(size);
		}
	}

	/**
	 * put new index to indices buffer
	 * 
	 * @param index
	 *            index
	 */
	protected void putToIndicesForDrawTriangleFans(short index) {
		if (currentBufferManager == null) {
			bufferIndicesForDrawTriangleFans.put(index);
		} else {
			indices.addValue(index);
		}
	}

	/**
	 * rewind indices buffer
	 */
	protected void rewindIndicesForDrawTriangleFans() {
		if (currentBufferManager == null) {
			bufferIndicesForDrawTriangleFans.rewind();
		}
	}

	/**
	 * draw curves
	 * 
	 * @param renderer1
	 *            renderer
	 * @param hidden
	 *            if hidden
	 */
	public void drawCurves(Renderer renderer1, boolean hidden) {
		bufferManagerCurves.draw(renderer1, hidden);
	}

	/**
	 * draw clipped curves
	 * 
	 * @param renderer1
	 *            renderer
	 * @param hidden
	 *            if hidden
	 */
	public void drawCurvesClipped(Renderer renderer1, boolean hidden) {
		renderer1.enableClipPlanesIfNeeded();
		bufferManagerCurvesClipped.draw(renderer1, hidden);
		renderer1.disableClipPlanesIfNeeded();
	}

	/**
	 * draw surfaces
	 * 
	 * @param renderer1
	 *            renderer
	 */
	public void drawSurfaces(Renderer renderer1) {
		bufferManagerSurfaces.draw(renderer1);
	}

	/**
	 * draw points
	 * 
	 * @param renderer1
	 *            renderer
	 */
	public void drawPoints(Renderer renderer1) {
		bufferManagerPoints.draw(renderer1);
	}

	/**
	 * draw closed surfaces
	 * 
	 * @param renderer1
	 *            renderer
	 */
	public void drawSurfacesClosed(Renderer renderer1) {
		bufferManagerSurfacesClosed.draw(renderer1);
	}

	/**
	 * draw closed surfaces
	 * 
	 * @param renderer1
	 *            renderer
	 */
	public void drawSurfacesClipped(Renderer renderer1) {
		renderer1.enableClipPlanesIfNeeded();
		bufferManagerSurfacesClipped.draw(renderer1);
		renderer1.disableClipPlanesIfNeeded();
	}

	@Override
	public void setPackCurve(Drawable3D d, boolean clipped) {
		currentBufferManager = clipped ? bufferManagerCurvesClipped
				: bufferManagerCurves;
		this.currentColor = d.getColor();
		this.currentLayer = d.getLayer();
		this.currentTextureType = Textures
				.getDashIdFromLineType(d.getLineType(), d.getLineTypeHidden());
	}

	@Override
	public void updateColorAndLayer(GColor color, int layer, int index) {
		GeometriesSet geometrySet = getGeometrySet(index);
		if (geometrySet instanceof GeometriesSetPacking) {
			((GeometriesSetPacking) geometrySet)
					.updateColorAndLayer(color, layer);
		}
	}

	@Override
	public void updateVisibility(boolean visible, int index, int alpha,
			int layer) {
		GeometriesSet geometrySet = getGeometrySet(index);
		if (geometrySet instanceof GeometriesSetPacking) {
			((GeometriesSetPacking) geometrySet)
					.updateVisibility(visible, alpha, layer);
		}
	}

	@Override
	protected void texture(double x) {
		texture(x, currentTextureType);
	}

	@Override
	public boolean packBuffers() {
		return true;
	}

	@Override
	public void update(boolean reset) {
		if (reset) {
			bufferManagerCurves.reset();
			bufferManagerCurvesClipped.reset();
			bufferManagerSurfaces.reset();
			bufferManagerSurfacesClosed.reset();
			bufferManagerSurfacesClipped.reset();
			bufferManagerPoints.reset();
		} else {
			bufferManagerCurvesClipped.update();
			bufferManagerSurfacesClipped.update();
		}
	}

	@Override
	public void setPackSurface(Drawable3D d, boolean clipped) {
		currentBufferManager = clipped ? bufferManagerSurfacesClipped
				: (d.addedFromClosedSurface() ? bufferManagerSurfacesClosed
						: bufferManagerSurfaces);
		this.currentColor = d.getSurfaceColor();
		this.currentLayer = d.getLayer();
	}

	@Override
	public void endPacking() {
		currentBufferManager = null;
	}

	private void initIndices(int size) {
		if (indices == null) {
			indices = new GLBufferIndicesArray(size);
		}
		indices.setLength(0);
	}

	/**
	 * 
	 * @return current indices
	 */
	public ReusableArrayList<Short> getIndices() {
		return indices;
	}

	@Override
	public void createPointTemplateIfNeeded(int size) {
		setCurrentBufferManager(bufferTemplates);
		bufferTemplates.createSphereIfNeeded(this, size);
		setCurrentBufferManager(null);
	}

	private void setPointValues(float size, float sizeScale, Coords center) {
		scale = size * sizeScale;
		scaleXYZ(center);
		center.get(translate);
	}

	/**
	 * 
	 * @return translate (for point drawing)
	 */
	public float[] getTranslate() {
		return translate;
	}

	/**
	 * 
	 * @return scale (for point drawing)
	 */
	public float getScale() {
		return scale;
	}

	/**
	 * set current buffer manager
	 * 
	 * @param bufferManager
	 *            buffer manager
	 */
	public void setCurrentBufferManager(GLBufferManager bufferManager) {
		currentBufferManager = bufferManager;
	}

	/**
	 * end geometry with known size, elements length, vertices and normals
	 * arrays
	 * 
	 * @param size
	 *            indices size
	 * @param elementsLength
	 *            vertices, normals length
	 * @param vertices1
	 *            vertices array
	 * @param normals1
	 *            normals array
	 */
	public void endGeometry(int size, int elementsLength,
			ArrayList<Double> vertices1, ArrayList<Double> normals1) {
		currentGeometriesSet.setVertices(vertices1, elementsLength * 3);
		currentGeometriesSet.setNormals(normals1, elementsLength * 3);
		currentGeometriesSet.setTextures(null, 0);
		currentGeometriesSet.setColors(null, 0);
		currentGeometriesSet.bindGeometry(size, TypeElement.TEMPLATE);
	}

	/**
	 * 
	 * @return buffer templates (for points)
	 */
	public GLBufferManagerTemplatesForPoints getBufferTemplates() {
		return bufferTemplates;
	}

	/**
	 * set old type (used for last geometry)
	 * 
	 * @param type
	 *            type
	 */
	public void setOldType(TypeElement type) {
		oldType = type;
	}

	/**
	 * 
	 * @return last geometry type
	 */
	public TypeElement getOldType() {
		return oldType;
	}

	/**
	 * set if indices have been done once (at least)
	 * 
	 * @param flag
	 *            flag
	 */
	public void setIndicesDone(boolean flag) {
		indicesDone = flag;
	}

	/**
	 * 
	 * @return if indices have been done once (at least)
	 */
	public boolean getIndicesDone() {
		return indicesDone;
	}
}

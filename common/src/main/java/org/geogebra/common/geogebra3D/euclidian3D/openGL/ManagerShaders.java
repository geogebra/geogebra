package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeMap;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;

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
	}

	public enum TypeElement {
		NONE, CURVE, SURFACE, FAN_DIRECT, FAN_INDIRECT, TRIANGLE_FAN,

		TRIANGLE_STRIP, TRIANGLES, TEMPLATE
	}

	@Override
	protected void initGeometriesList() {
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

	/**
	 * Geometry (set of vertices, normals, etc. for e.g. triangles)
	 * 
	 * @author mathieu
	 *
	 */
	abstract public class Geometry {
		/**
		 * type of primitives
		 */
		protected Type type;

		protected GLBuffer v;
		protected GLBuffer n;
		protected GLBuffer t;
		protected GLBuffer c;

		private int length;

		/**
		 * Start a new geometry
		 * 
		 * @param type
		 *            of primitives
		 */
		public Geometry(Type type) {
			this.type = type;
			setBuffers();
		}

		/**
		 * set the buffers
		 */
		protected void setBuffers() {
			this.v = GLFactory.getPrototype().newBuffer();
			this.n = GLFactory.getPrototype().newBuffer();
			this.t = GLFactory.getPrototype().newBuffer();
			this.c = GLFactory.getPrototype().newBuffer();
		}

		/**
		 * set the geometry type and mark buffers as empty
		 * 
		 * @param type
		 *            geometry type
		 */
		public void setType(Type type) {
			this.type = type;
			this.v.setEmpty();
			this.n.setEmpty();
			this.t.setEmpty();
			this.c.setEmpty();
		}

		/**
		 * allocate memory for buffers (for direct write)
		 * 
		 * @param size
		 *            vertices size
		 */
		public void allocateBuffers(int size) {
			// Log.debug("allocateBuffers: "+size);
			v.allocate(size * 3);
			n.allocate(size * 3);
			length = 0;
		}

		/**
		 * put vertex values into buffer
		 * 
		 * @param x
		 *            x-coord
		 * @param y
		 *            y-coord
		 * @param z
		 *            z-coord
		 */
		public void vertexDirect(double x, double y, double z) {
			v.put(x);
			v.put(y);
			v.put(z);
			length++;
		}

		/**
		 * put normal values into buffer
		 * 
		 * @param x
		 *            x-coord
		 * @param y
		 *            y-coord
		 * @param z
		 *            z-coord
		 */
		public void normalDirect(double x, double y, double z) {
			n.put(x);
			n.put(y);
			n.put(z);
		}

		/**
		 * ends geometry
		 */
		public void end() {
			v.setLimit(length * 3);
			n.setLimit(length * 3);
		}

		/**
		 * 
		 * @return type of primitives
		 */
		public Type getType() {
			return type;
		}

		/**
		 * set double buffer for vertices
		 * 
		 * @param array
		 *            double array
		 * @param length
		 *            length to copy
		 */
		public void setVertices(ArrayList<Double> array, int length) {
			// this.v = GLFactory.prototype.newBuffer();
			this.v.set(array, length);
		}

		/**
		 * 
		 * @return vertices buffer
		 */
		public GLBuffer getVertices() {
			return v;
		}

		/**
		 * set double buffer for normals
		 * 
		 * @param array
		 *            double array
		 * @param length
		 *            length to copy
		 */
		public void setNormals(ArrayList<Double> array, int length) {
			this.n.set(array, length);
		}

		/**
		 * 
		 * @return normals buffer
		 */
		public GLBuffer getNormals() {
			return n;
		}

		/**
		 * set double buffer for texture
		 * 
		 * @param array
		 *            double array
		 * @param length
		 *            length to copy
		 */
		public void setTextures(ArrayList<Double> array, int length) {
			this.t.set(array, length);
		}

		/**
		 * set textures to empty
		 */
		public void setTexturesEmpty() {
			this.t.setEmpty();
		}

		/**
		 * set color to empty
		 */
		public void setColorsEmpty() {
			// not used here
		}

		/**
		 * 
		 * @return texture buffer
		 */
		public GLBuffer getTextures() {
			return t;
		}

		/**
		 * set double buffer for colors
		 * 
		 * @param array
		 *            double array
		 * @param length
		 *            length to copy
		 */
		public void setColors(ArrayList<Double> array, int length) {
			this.c.set(array, length);
		}

		/**
		 * 
		 * @return colors buffer
		 */
		public GLBuffer getColors() {
			return c;
		}

		/**
		 * set vertices length
		 * 
		 * @param l
		 *            vertices length
		 */
		public void setLength(int l) {
			this.length = l;
		}

		/**
		 * 
		 * @return vertices length
		 */
		public int getLength() {
			return length;
		}

		/**
		 * draw to renderer
		 * 
		 * @param r
		 *            renderer to draw into
		 */
		public void draw(RendererShadersInterface r) {
			r.loadVertexBuffer(getVertices(), getLength());
			r.loadNormalBuffer(getNormals(), getLength());
			r.loadColorBuffer(getColors(), getLength());
			if (r.areTexturesEnabled()) {
				r.loadTextureBuffer(getTextures(), getLength());
			}
			r.draw(getType(), getLength());
		}

		/**
		 * draw as label to renderer
		 * 
		 * @param r
		 *            renderer to draw into
		 */
		public void drawLabel(RendererShadersInterface r) {
			r.loadVertexBuffer(getVertices(), getLength());
			if (r.areTexturesEnabled()) {
				r.loadTextureBuffer(getTextures(), getLength());
			}
			r.draw(getType(), getLength());
		}

	}

	/**
	 * Set of geometries
	 * 
	 * @author mathieu
	 *
	 */
	@SuppressWarnings("serial")
	abstract public class GeometriesSet extends ArrayList<Geometry> {

		protected Geometry currentGeometry;

		protected int currentGeometryIndex;

		private int geometriesLength;

		/**
		 * Creates geometry set.
		 */
		public GeometriesSet() {
			reset();
		}

		/**
		 * set index and color
		 * 
		 * @param index
		 *            index
		 * @param color
		 *            color
		 * @param layer
		 *            layer
		 */
		public void setIndex(int index, GColor color, int layer) {
			// no need here
		}

		/**
		 * says this geometry set is reset
		 */
		public void reset() {
			currentGeometryIndex = 0;
			geometriesLength = 0;
		}

		/**
		 * hide last geometries when ending a list
		 */
		public void hideLastGeometries() {
			// nothing to do here
		}

		/**
		 * 
		 * @return geometries length
		 */
		public int getGeometriesLength() {
			return geometriesLength;
		}

		/**
		 * start a new geometry
		 * 
		 * @param type
		 *            type of primitives
		 */
		public void startGeometry(Type type) {
			if (currentGeometryIndex < size()) {
				currentGeometry = get(currentGeometryIndex);
				currentGeometry.setType(type);
			} else {
				currentGeometry = newGeometry(type);
				add(currentGeometry);
			}

			currentGeometryIndex++;
			geometriesLength++;
		}

		/**
		 * @param type
		 *            geometry type
		 * @return new geometry for the given type
		 */
		abstract  protected Geometry newGeometry(Type type);

		/**
		 * allocate buffers of current geometry
		 * 
		 * @param size
		 *            memory size
		 */
		public void allocate(int size) {
			currentGeometry.allocateBuffers(size);
		}

		/**
		 * put vertex values into buffer
		 * 
		  * @param x
		 *            x-coord
		 * @param y
		 *            y-coord
		 * @param z
		 *            z-coord
		 */
		public void vertexDirect(double x, double y, double z) {
			currentGeometry.vertexDirect(x, y, z);
		}

		/**
		 * put normal values into buffer
		 * 
		 * @param x
		 *            x-coord
		 * @param y
		 *            y-coord
		 * @param z
		 *            z-coord
		 */
		public void normalDirect(double x, double y, double z) {
			currentGeometry.normalDirect(x, y, z);
		}

		/**
		 * ends current geometry
		 */
		public void endGeometry() {
			currentGeometry.end();
		}

		/**
		 * bind current geometry to its buffer
		 * 
		 * @param size
		 *            indices size
		 * @param type
		 *            type for element indices
		 */
		public void bindGeometry(int size, TypeElement type) {
			// not used here
		}

		/**
		 * set vertices for current geometry
		 * 
		 * @param vertices
		 *            vertices
		 * @param length
		 *            vertices length
		 */
		public void setVertices(ArrayList<Double> vertices, int length) {
			currentGeometry.setVertices(vertices, length);
			currentGeometry.setLength(length / 3);
		}

		/**
		 * Set normals of current geometry.
		 * 
		 * @param normals
		 *            normals
		 * @param length
		 *            length to copy
		 */
		public void setNormals(ArrayList<Double> normals, int length) {
			if (length == 3) { // only one normal for all vertices
				currentGeometry.setNormals(normals, length);
			} else if (length == 3 * currentGeometry.getLength()) {
				currentGeometry.setNormals(normals, length);
			}
		}

		/**
		 * Set colors of current geometry.
		 * 
		 * @param textures
		 *            textures
		 * @param length
		 *            length to copy
		 */
		public void setTextures(ArrayList<Double> textures, int length) {
			if (length == 2 * currentGeometry.getLength()) {
				currentGeometry.setTextures(textures, length);
			} else {
				currentGeometry.setTexturesEmpty();
			}
		}

		/**
		 * Set colors of current geometry.
		 * 
		 * @param colors
		 *            colors
		 * @param length
		 *            length to copy
		 */
		public void setColors(ArrayList<Double> colors, int length) {
			if (length == 4 * currentGeometry.getLength()) {
				currentGeometry.setColors(colors, length);
			} else {
				currentGeometry.setColorsEmpty();
			}
		}

        /**
         *
         * @return true if this set use packing
         */
		public boolean usePacking() {
		    return false;
        }
	}

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

	/**
	 * 
	 * @param mayBePacked
	 *            true if this set may be in packed buffer
	 * @return new geometries set
	 */
	abstract protected GeometriesSet newGeometriesSet(boolean mayBePacked);

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

	/**
	 * remove geometry set at index
	 * 
	 * @param index
	 *            index
	 */
	protected void removeGeometrySet(int index) {
		geometriesSetList.remove(index);
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
				currentGeometriesSet.get(i)
						.draw((RendererShadersInterface) renderer);
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
				currentGeometriesSet.get(i)
						.drawLabel((RendererShadersInterface) renderer);
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
		normalZ();
		vertexInt(x, y, z);
		texture(1, 0);
		normalZ();
		vertexInt(x + width, y, z);
		texture(0, 1);
		normalZ();
		vertexInt(x, y + height, z);
		texture(1, 1);
		normalZ();
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

}

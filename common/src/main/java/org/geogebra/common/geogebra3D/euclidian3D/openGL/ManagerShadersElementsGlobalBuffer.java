package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D.GeometryForExport;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.discrete.PolygonTriangulation.TriangleFan;
import org.geogebra.common.util.debug.Log;

/**
 * manager using shaders and binding to gpu buffers
 * 
 * @author mathieu
 *
 */
public class ManagerShadersElementsGlobalBuffer
		extends ManagerShadersNoTriangleFan {

	private GLBufferIndices curvesIndices;
	private GLBufferIndices fanDirectIndices;
	private GLBufferIndices fanIndirectIndices;
	private int curvesIndicesSize;
	private int fanDirectIndicesSize;
	private int fanIndirectIndicesSize;
	private GLBufferIndices bufferIndicesForDrawTriangleFans;

	private boolean indicesDone = false;
	private TypeElement oldType = TypeElement.NONE;

	final static boolean DEBUG = false;

	/**
	 * debug if debug mode
	 * 
	 * @param message
	 *            message
	 */
	final static void debug(String message) {
		if (DEBUG) {
			Log.debug(message);
		}
	}

	/**
	 * debug if debug mode
	 * 
	 * @param message
	 *            message
	 * @param val
	 *            value
	 */
	final static void debug(String message, double val) {
		if (DEBUG) {
			Log.debug(message + val);
		}
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
	public final GLBufferIndices getBufferIndicesForCurve(
			RendererShadersInterface r, int size) {

		if (size > curvesIndicesSize) {

			debug("NEW curvesIndicesSize : ", size);

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
	public final GLBufferIndices getBufferIndicesForFanDirect(
			RendererShadersInterface r, int size) {

		if (size > fanDirectIndicesSize) {

			debug("NEW fanDirectIndicesSize : ", size);

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
	public final GLBufferIndices getBufferIndicesForFanIndirect(
			RendererShadersInterface r, int size) {

		if (size > fanIndirectIndicesSize) {

			debug("NEW fanIndirectIndicesSize : ", size);

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

	@SuppressWarnings("serial")
	protected class GeometriesSetElementsGlobalBuffer extends GeometriesSet {
		@Override
		protected Geometry newGeometry(Type type) {
			return new GeometryElementsGlobalBuffer(type);
		}

		@Override
		public void bindGeometry(int size, TypeElement type) {
			((GeometryElementsGlobalBuffer) currentGeometry)
					.bind((RendererShadersInterface) renderer, size, type);
		}

		/**
		 * remove GL buffers
		 */
		public void removeBuffers() {
			for (int i = 0; i < getGeometriesLength(); i++) {
				((GeometryElementsGlobalBuffer) get(i))
						.removeBuffers((RendererShadersInterface) renderer);
			}

		}
	}

	public class GeometryElementsGlobalBuffer extends Geometry implements GeometryForExport {

		private GLBufferIndices arrayI = null;

		private int indicesLength;

		private boolean hasSharedIndexBuffer = false;

		public GeometryElementsGlobalBuffer(Type type) {
			super(type);
		}

		/**
		 * remove buffers
		 * 
		 * @param r
		 *            GL renderer
		 */
		public void removeBuffers(RendererShadersInterface r) {
			// TODO not needed?
		}

		/**
		 * bind the geometry to its GL buffer
		 * 
		 * @param r
		 *            renderer
		 * @param size
		 *            indices size
		 * @param typeElement
		 *            type for elements indices
		 */
		public void bind(RendererShadersInterface r, int size,
				TypeElement typeElement) {

			switch (typeElement) {
			case NONE:
				if (hasSharedIndexBuffer) {
					// need specific index if was sharing one
					arrayI = null;
				}
				if (arrayI == null) {
					arrayI = GLFactory.getPrototype().newBufferIndices();
				}

				indicesLength = getLength();

				if (!indicesDone || typeElement != oldType
						|| arrayI.capacity() < indicesLength) {
					debug("NEW index buffer");
					arrayI.allocate(indicesLength);
					for (short i = 0; i < indicesLength; i++) {
						arrayI.put(i);
					}
					arrayI.rewind();
					indicesDone = true;
				} else {
					debug("keep same index buffer");
				}

				hasSharedIndexBuffer = false;
				break;

			case CURVE:
				debug("curve: shared index buffer");
				arrayI = getBufferIndicesForCurve(r, size);
				indicesLength = 3 * 2 * size * PlotterBrush.LATITUDES;
				hasSharedIndexBuffer = true;
				// debug("curve: NOT shared index buffer");
				// bufferI = getBufferIndicesForCurve(bufferI, r, size,
				// indicesLength / (3 * 2 * PlotterBrush.LATITUDES));
				// indicesLength = 3 * 2 * size * PlotterBrush.LATITUDES;
				// hasSharedIndexBuffer = false;
				break;

			case SURFACE:
				debug("surface -- keep same index buffer");
				indicesLength = size;
				hasSharedIndexBuffer = false;
				break;

			case FAN_DIRECT:
				debug("fan direct: shared index buffer");
				arrayI = getBufferIndicesForFanDirect(r, size);
				indicesLength = 3 * (size - 2);
				hasSharedIndexBuffer = true;
				break;
			case FAN_INDIRECT:
				debug("fan indirect: shared index buffer");
				arrayI = getBufferIndicesForFanIndirect(r, size);
				indicesLength = 3 * (size - 2);
				hasSharedIndexBuffer = true;
				break;
			default:
				Log.debug("Missing case: " + typeElement);
			}

			oldType = typeElement;
		}

		@Override
		public void draw(Renderer r) {

			if (arrayI == null) {
				return;
			}

			r.getRendererImpl().loadVertexBuffer(getVertices(), getLength());
			r.getRendererImpl().loadNormalBuffer(getNormals(), getLength());
			r.getRendererImpl().loadColorBuffer(getColors(), getLength());
			if (r.getRendererImpl().areTexturesEnabled()) {
				r.getRendererImpl().loadTextureBuffer(getTextures(),
						getLength());
			} else {
				r.getRendererImpl().disableTextureBuffer();
			}
			r.getRendererImpl().loadIndicesBuffer(arrayI, indicesLength);
			r.getRendererImpl().draw(getType(), indicesLength);

		}

		@Override
		public void drawLabel(Renderer r) {

			if (arrayI == null) {
				return;
			}

			r.getRendererImpl().loadVertexBuffer(getVertices(), getLength());
			if (r.getRendererImpl().areTexturesEnabled()) {
				r.getRendererImpl().loadTextureBuffer(getTextures(),
						getLength());
			}
			r.getRendererImpl().loadIndicesBuffer(arrayI, indicesLength);
			r.getRendererImpl().draw(getType(), indicesLength);
		}

		/**
		 * 
		 * @param size
		 *            size
		 * @return indices buffer with correct size
		 */
		public GLBufferIndices getBufferI(int size) {
			if (arrayI == null || hasSharedIndexBuffer) {
				arrayI = GLFactory.getPrototype().newBufferIndices();
			}
			arrayI.allocate(size);
			return arrayI;
		}

		@Override
		public void initForExport() {
			// no need here
		}

		@Override
		public GLBufferIndices getBufferIndices() {
			return arrayI;
		}

		@Override
		public int getIndicesLength() {
			return indicesLength;
		}

		@Override
		public int getElementsOffset() {
			return 0; // no offset here
		}

		@Override
		public int getLengthForExport() {
			return getLength();
		}

		@Override
		public GLBuffer getVerticesForExport() {
			return getVertices();
		}

		@Override
		public GLBuffer getNormalsForExport() {
			return getNormals();
		}

	}

	/**
	 * constructor
	 * 
	 * @param renderer
	 *            renderer
	 * @param view3d
	 *            3D view
	 */
	public ManagerShadersElementsGlobalBuffer(Renderer renderer,
			EuclidianView3D view3d) {
		super(renderer, view3d);
	}

	@Override
	protected void initGeometriesList() {
		curvesIndicesSize = -1;
		fanDirectIndicesSize = -1;
		fanIndirectIndicesSize = -1;
		super.initGeometriesList();
	}

	@Override
	protected GeometriesSet newGeometriesSet(boolean mayBePacked) {
		return new GeometriesSetElementsGlobalBuffer();
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

	@Override
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

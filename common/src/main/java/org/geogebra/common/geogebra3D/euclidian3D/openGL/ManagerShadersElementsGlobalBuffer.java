package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.discrete.PolygonTriangulation.TriangleFan;
import org.geogebra.common.util.debug.Log;

/**
 * manager using shaders and binding to gpu buffers
 * 
 * @author mathieu
 *
 */
public class ManagerShadersElementsGlobalBuffer extends
		ManagerShadersNoTriangleFan {

	
	private short[] curvesIndices, fanDirectIndices, fanIndirectIndices;
	private int curvesIndicesSize, fanDirectIndicesSize,
			fanIndirectIndicesSize;

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
	public final short[] getBufferIndicesForCurve(RendererShadersInterface r,
			int size) {

		if (size > curvesIndicesSize) {

			debug("NEW curvesIndicesSize : ", size);

			// creates indices buffer
			curvesIndices = new short[3 * 2 * size * PlotterBrush.LATITUDES];

			int index = 0;
			for (int k = 0; k < size; k++) {
				for (int i = 0; i < PlotterBrush.LATITUDES; i++) {
					int iNext = (i + 1) % PlotterBrush.LATITUDES;
					// first triangle
					curvesIndices[index] = (short) (i + k * PlotterBrush.LATITUDES);
					index++;
					curvesIndices[index] = (short) (i + (k + 1)
							* PlotterBrush.LATITUDES);
					index++;
					curvesIndices[index] = (short) (iNext + (k + 1)
							* PlotterBrush.LATITUDES);
					index++;
					// second triangle
					curvesIndices[index] = (short) (i + k * PlotterBrush.LATITUDES);
					index++;
					curvesIndices[index] = (short) (iNext + (k + 1)
							* PlotterBrush.LATITUDES);
					index++;
					curvesIndices[index] = (short) (iNext + k * PlotterBrush.LATITUDES);
					index++;
				}
			}

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
	public final short[] getBufferIndicesForFanDirect(
			RendererShadersInterface r, int size) {

		if (size > fanDirectIndicesSize) {

			debug("NEW fanDirectIndicesSize : ", size);

			// creates indices buffer
			fanDirectIndices = new short[3 * (size - 2)];

			int index = 0;
			short k = 1;
			while (k < size - 1) {
				fanDirectIndices[index] = 0;
				index++;
				fanDirectIndices[index] = k;
				index++;

				k++;
				fanDirectIndices[index] = k;
				index++;
			}

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
	public final short[] getBufferIndicesForFanIndirect(
			RendererShadersInterface r, int size) {


		if (size > fanIndirectIndicesSize) {

			debug("NEW fanIndirectIndicesSize : ", size);

			// creates indices buffer
			fanIndirectIndices = new short[3 * (size - 2)];

			int index = 0;
			short k = (short) (size - 1);
			while (k > 1) {
				fanIndirectIndices[index] = 0;
				index++;
				fanIndirectIndices[index] = k;
				index++;

				k--;
				fanIndirectIndices[index] = k;
				index++;
			}

			fanIndirectIndicesSize = size;
		}

		return fanIndirectIndices;
	}
	
	
	protected class GeometriesSetElementsGlobalBuffer extends GeometriesSet {
		@Override
		protected Geometry newGeometry(Type type) {
			return new GeometryElementsGlobalBuffer(type);
		}

		@Override
		public void bindGeometry(int size, TypeElement type) {
			((GeometryElementsGlobalBuffer) currentGeometry).bind(
					(RendererShadersInterface) renderer, size, type);
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

	protected class GeometryElementsGlobalBuffer extends Geometry {

		private short[] arrayI = null;

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

		}

		/**
		 * bind the geometry to its GL buffer
		 * 
		 * @param r
		 *            renderer
		 * @param size
		 *            indices size
		 * @param type
		 *            type for elements indices
		 */
		public void bind(RendererShadersInterface r, int size, TypeElement type) {


			switch (type) {
			case NONE:
				if (hasSharedIndexBuffer) {
					// need specific index if was sharing one
					arrayI = null;
				}
				if (arrayI == null
						|| (!indicesDone && arrayI.length != getLength())) {
					debug("NEW index buffer");
					arrayI = new short[getLength()];
					for (short i = 0; i < getLength(); i++) {
						arrayI[i] = i;
					}
				} else {
					debug("keep same index buffer");
				}
				indicesLength = arrayI.length;

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

			}


		}

		@Override
		public void draw(RendererShadersInterface r) {

			r.loadVertexBuffer(getVertices(), getLength());
			r.loadNormalBuffer(getNormals(), getLength());
			r.loadColorBuffer(getColors(), getLength());
			if (r.areTexturesEnabled()) {
				r.loadTextureBuffer(getTextures(), getLength());
			}
			r.loadIndicesBuffer(arrayI, indicesLength);
			r.draw(getType(), indicesLength);

		}

		@Override
		public void drawLabel(RendererShadersInterface r) {
			r.loadVertexBuffer(getVertices(), getLength());
			if (r.areTexturesEnabled()) {
				r.loadTextureBuffer(getTextures(), getLength());
			}
			r.loadIndicesBuffer(arrayI, indicesLength);
			r.draw(getType(), indicesLength);
		}

		private boolean indicesDone = false;

		/**
		 * 
		 * @param size
		 *            size
		 * @return indices buffer with correct size
		 */
		public short[] getBufferI(int size) {
			indicesDone = true;
			if (arrayI == null || arrayI.length < size) {
				arrayI = new short[size];
			}
			return arrayI;
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
	public ManagerShadersElementsGlobalBuffer(Renderer renderer, EuclidianView3D view3d) {
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
	protected GeometriesSet newGeometriesSet() {
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
	public short[] getCurrentGeometryIndices(int size) {
		return ((GeometryElementsGlobalBuffer) currentGeometriesSet.currentGeometry)
				.getBufferI(size);
	}

	@Override
	protected void removeGeometrySet(int index) {
		GeometriesSetElementsGlobalBuffer set = (GeometriesSetElementsGlobalBuffer) geometriesSetList
				.remove(index);
		set.removeBuffers();
		// App.debug("removeGeometrySet : " + index);
	}

	@Override
	public void drawPolygonConvex(Coords n, Coords[] v, int length,
			boolean reverse) {

		startGeometry(Type.TRIANGLES);

		// set texture
		setDummyTexture();

		// set normal
		normal(n);

		// set vertices
		for (int i = 0; i < length; i++) {
			vertex(v[i]);
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
		normal(n);

		// set vertices
		for (int i = 0; i < length; i++) {
			vertex(verticesWithIntersections[i]);
		}

		// indices
		int size = 0;
		for (TriangleFan triFan : triFanList) {
			size += triFan.size() - 1;
		}

		short[] arrayI = getCurrentGeometryIndices(size * 3);

		int index = 0;
		for (TriangleFan triFan : triFanList) {
			short apex = (short) triFan.getApexPoint();
			short current = (short) triFan.getVertexIndex(0);
			for (int i = 1; i < triFan.size(); i++) {
				arrayI[index] = apex;
				index++;
				arrayI[index] = current;
				index++;
				current = (short) triFan.getVertexIndex(i);
				arrayI[index] = current;
				index++;
			}
		}

		// end
		endGeometry(3 * size, TypeElement.SURFACE);
	}

}

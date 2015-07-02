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
public class ManagerShadersElements extends ManagerShadersNoTriangleFan {

	final static public int GLSL_ATTRIB_POSITION = 0;
	final static public int GLSL_ATTRIB_COLOR = 1;
	final static public int GLSL_ATTRIB_NORMAL = 2;
	final static public int GLSL_ATTRIB_TEXTURE = 3;
	final static public int GLSL_ATTRIB_INDEX = 4;
	
	private GPUBuffer curvesIndices, fanDirectIndices, fanIndirectIndices;
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
	 * @param buffer
	 *            GPU buffer may be null
	 * @return new GPU buffer if curret is null
	 */
	public final static GPUBuffer createBufferIfNeeded(
			RendererShadersInterface r,
			GPUBuffer buffer) {
		if (buffer == null){
			GPUBuffer ret = GLFactory.prototype.newGPUBuffer();
			r.createArrayBuffer(ret);
			return ret;
		}

		return buffer;
	}

	/**
	 * 
	 * @param r
	 *            renderer
	 * @param buffer
	 *            GPU buffer may be null
	 * @return new GPU buffer if curret is null
	 */
	public final static GPUBuffer createElementBufferIfNeeded(
			RendererShadersInterface r, GPUBuffer buffer) {
		if (buffer == null) {
			GPUBuffer ret = GLFactory.prototype.newGPUBuffer();
			r.createElementBuffer(ret);
			return ret;
		}

		return buffer;
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
	public final GPUBuffer getBufferIndicesForCurve(RendererShadersInterface r,
			int size) {

		curvesIndices = createElementBufferIfNeeded(r, curvesIndices);

		if (size > curvesIndicesSize) {

			debug("NEW curvesIndicesSize : ", size);

			// creates indices buffer
			short[] arrayI = new short[3 * 2 * size * PlotterBrush.LATITUDES];

			int index = 0;
			for (int k = 0; k < size; k++) {
				for (int i = 0; i < PlotterBrush.LATITUDES; i++) {
					int iNext = (i + 1) % PlotterBrush.LATITUDES;
					// first triangle
					arrayI[index] = (short) (i + k * PlotterBrush.LATITUDES);
					index++;
					arrayI[index] = (short) (i + (k + 1)
							* PlotterBrush.LATITUDES);
					index++;
					arrayI[index] = (short) (iNext + (k + 1)
							* PlotterBrush.LATITUDES);
					index++;
					// second triangle
					arrayI[index] = (short) (i + k * PlotterBrush.LATITUDES);
					index++;
					arrayI[index] = (short) (iNext + (k + 1)
							* PlotterBrush.LATITUDES);
					index++;
					arrayI[index] = (short) (iNext + k * PlotterBrush.LATITUDES);
					index++;
				}
			}

			curvesIndicesSize = size;
			r.storeElementBuffer(arrayI, arrayI.length, curvesIndices);
		}

		return curvesIndices;
	}

	/**
	 * 
	 * @param curveBufferOld
	 *            current curve buffer
	 * @param r
	 *            renderer
	 * @param size
	 *            sections size
	 * @param sizeOld
	 *            old size for curve
	 * @return GPU buffer for curve indices, update it if current is not same
	 *         size
	 */
	private final static GPUBuffer getBufferIndicesForCurve(
			GPUBuffer curveBufferOld, RendererShadersInterface r, int size,
			int sizeOld) {

		if (size == sizeOld) {
			debug("SAME curve size : ", size);
			return curveBufferOld;
		}

		GPUBuffer curveBuffer = createElementBufferIfNeeded(r, curveBufferOld);

		debug("NEW curve size : ", size);

		// creates indices buffer
		short[] arrayI = new short[3 * 2 * size * PlotterBrush.LATITUDES];

		int index = 0;
		for (int k = 0; k < size; k++) {
			for (int i = 0; i < PlotterBrush.LATITUDES; i++) {
				int iNext = (i + 1) % PlotterBrush.LATITUDES;
				// first triangle
				arrayI[index] = (short) (i + k * PlotterBrush.LATITUDES);
				index++;
				arrayI[index] = (short) (i + (k + 1) * PlotterBrush.LATITUDES);
				index++;
				arrayI[index] = (short) (iNext + (k + 1)
						* PlotterBrush.LATITUDES);
				index++;
				// second triangle
				arrayI[index] = (short) (i + k * PlotterBrush.LATITUDES);
				index++;
				arrayI[index] = (short) (iNext + (k + 1)
						* PlotterBrush.LATITUDES);
				index++;
				arrayI[index] = (short) (iNext + k * PlotterBrush.LATITUDES);
				index++;
			}
		}

		r.storeElementBuffer(arrayI, arrayI.length, curveBuffer);

		return curveBuffer;
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
	public final GPUBuffer getBufferIndicesForFanDirect(
			RendererShadersInterface r, int size) {

		fanDirectIndices = createElementBufferIfNeeded(r, fanDirectIndices);

		if (size > fanDirectIndicesSize) {

			debug("NEW fanDirectIndicesSize : ", size);

			// creates indices buffer
			short[] arrayI = new short[3 * (size - 2)];

			int index = 0;
			short k = 1;
			while (k < size - 1) {
				arrayI[index] = 0;
				index++;
				arrayI[index] = k;
				index++;

				k++;
				arrayI[index] = k;
				index++;
			}

			fanDirectIndicesSize = size;
			r.storeElementBuffer(arrayI, arrayI.length, fanDirectIndices);
		}

		return fanDirectIndices;
	}

	/**
	 * 
	 * @param bufferOld
	 *            old buffer
	 * @param r
	 *            renderer
	 * @param size
	 *            sections size
	 * @param sizeOld
	 *            old size
	 * @return GPU buffer for direct fan indices, update it if current is not
	 *         big enough
	 */
	private final static GPUBuffer getBufferIndicesForFanDirect(
			GPUBuffer bufferOld,
			RendererShadersInterface r, int size,
			int sizeOld) {


		if (size == sizeOld) {
			debug("SAME fanDirectIndicesSize : ", size);
			return bufferOld;
		}

		GPUBuffer buffer = createElementBufferIfNeeded(r, bufferOld);


		debug("NEW fanDirectIndicesSize : ", size);

		// creates indices buffer
		short[] arrayI = new short[3 * (size - 2)];

		int index = 0;
		short k = 1;
		while (k < size - 1) {
			arrayI[index] = 0;
			index++;
			arrayI[index] = k;
			index++;

			k++;
			arrayI[index] = k;
			index++;
		}

		r.storeElementBuffer(arrayI, arrayI.length, buffer);

		return buffer;
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
	public final GPUBuffer getBufferIndicesForFanIndirect(
			RendererShadersInterface r, int size) {

		fanIndirectIndices = createElementBufferIfNeeded(r, fanIndirectIndices);

		if (size > fanIndirectIndicesSize) {

			debug("NEW fanIndirectIndicesSize : ", size);

			// creates indices buffer
			short[] arrayI = new short[3 * (size - 2)];

			int index = 0;
			short k = (short) (size - 1);
			while (k > 1) {
				arrayI[index] = 0;
				index++;
				arrayI[index] = k;
				index++;

				k--;
				arrayI[index] = k;
				index++;
			}

			fanIndirectIndicesSize = size;
			r.storeElementBuffer(arrayI, arrayI.length, fanIndirectIndices);
		}

		return fanIndirectIndices;
	}
	
	
	/**
	 * 
	 * @param bufferOld
	 *            old buffer
	 * @param r
	 *            renderer
	 * @param size
	 *            sections size
	 * @param sizeOld
	 *            old size
	 * @return GPU buffer for direct fan indices, update it if current is not
	 *         big enough
	 */
	private final static GPUBuffer getBufferIndicesForFanIndirect(
			GPUBuffer bufferOld,
			RendererShadersInterface r, int size,
			int sizeOld) {


		if (size == sizeOld) {
			debug("SAME fanIndirectIndicesSize : ", size);
			return bufferOld;
		}

		GPUBuffer buffer = createElementBufferIfNeeded(r, bufferOld);


		debug("NEW fanIndirectIndicesSize : ", size);

		// creates indices buffer
		short[] arrayI = new short[3 * (size - 2)];

		int index = 0;
		short k = (short) (size - 1);
		while (k > 1) {
			arrayI[index] = 0;
			index++;
			arrayI[index] = k;
			index++;

			k--;
			arrayI[index] = k;
			index++;
		}

		r.storeElementBuffer(arrayI, arrayI.length, buffer);

		return buffer;
	}

	protected class GeometriesSetElements extends GeometriesSet {
		@Override
		protected Geometry newGeometry(Type type) {
			return new GeometryElements(type);
		}

		@Override
		public void bindGeometry(int size, TypeElement type) {
			((GeometryElements) currentGeometry).bind(
					(RendererShadersInterface) renderer, size, type);
		}

		/**
		 * remove GL buffers
		 */
		public void removeBuffers() {
			for (int i = 0; i < getGeometriesLength(); i++) {
				((GeometryElements) get(i))
						.removeBuffers((RendererShadersInterface) renderer);
			}

		}
	}

	protected class GeometryElements extends Geometry {

		private GPUBuffer bufferV = null, bufferN = null, bufferC = null,
				bufferT = null, bufferI = null;

		private short[] arrayI = null;

		private int indicesLength;

		private boolean hasSharedIndexBuffer = false;

		public GeometryElements(Type type) {
			super(type);
		}
		

		/**
		 * remove buffers
		 * 
		 * @param r
		 *            GL renderer
		 */
		public void removeBuffers(RendererShadersInterface r) {
			r.removeArrayBuffer(bufferV);
			if (bufferN != null) {
				r.removeArrayBuffer(bufferN);
			}
			if (bufferC != null) {
				r.removeArrayBuffer(bufferC);
			}
			if (bufferT != null) {
				r.removeArrayBuffer(bufferT);
			}
			if (!hasSharedIndexBuffer) {
				r.removeElementBuffer(bufferI);
			}
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

			bufferV = ManagerShadersElements
					.createBufferIfNeeded(r, bufferV);
			r.storeBuffer(getVertices(),
					getLength(), 3, bufferV, GLSL_ATTRIB_POSITION);

			if (getNormals() != null && !getNormals().isEmpty()
					&& getNormals().capacity() != 3) {
				bufferN = ManagerShadersElements.createBufferIfNeeded(r,
						bufferN);
				r.storeBuffer(getNormals(), getLength(), 3, bufferN,
						GLSL_ATTRIB_NORMAL);
			}

			if (getColors() != null && !getColors().isEmpty()) {
				bufferC = ManagerShadersElements.createBufferIfNeeded(r,
						bufferC);
				r.storeBuffer(getColors(), getLength(), 4, bufferC,
						GLSL_ATTRIB_COLOR);
			}

			if (getTextures() != null && !getTextures().isEmpty()) {
				bufferT = ManagerShadersElements.createBufferIfNeeded(r,
						bufferT);
				r.storeBuffer(getTextures(), getLength(), 2, bufferT,
						GLSL_ATTRIB_TEXTURE);
			}

			switch (type) {
			case NONE:
				if (hasSharedIndexBuffer) {
					// need specific index if was sharing one
					bufferI = null;
				}
				bufferI = ManagerShadersElements
						.createElementBufferIfNeeded(r,
						bufferI);
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

				r.storeElementBuffer(arrayI, indicesLength, bufferI);
				hasSharedIndexBuffer = false;
				break;

			case CURVE:
				debug("curve: shared index buffer");
				bufferI = getBufferIndicesForCurve(r, size);
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

				if (hasSharedIndexBuffer) {
					// need specific index if was sharing one
					bufferI = null;
				}

				bufferI = ManagerShadersElements
						.createElementBufferIfNeeded(r,
						bufferI);
				indicesLength = size;

				r.storeElementBuffer(arrayI, indicesLength, bufferI);
				hasSharedIndexBuffer = false;
				break;

			case FAN_DIRECT:
				debug("fan direct: shared index buffer");
				bufferI = getBufferIndicesForFanDirect(r, size);
				indicesLength = 3 * (size - 2);
				hasSharedIndexBuffer = true;
				// debug("fan direct: NOT shared index buffer");
				// bufferI = getBufferIndicesForFanDirect(bufferI, r, size,
				// indicesLength / 3 + 2);
				// indicesLength = 3 * (size - 2);
				// hasSharedIndexBuffer = false;
				break;
			case FAN_INDIRECT:
				debug("fan indirect: shared index buffer");
				bufferI = getBufferIndicesForFanIndirect(r, size);
				indicesLength = 3 * (size - 2);
				hasSharedIndexBuffer = true;
				// debug("fan indirect: NOT shared index buffer");
				// bufferI = getBufferIndicesForFanIndirect(bufferI, r, size,
				// indicesLength / 3 + 2);
				// indicesLength = 3 * (size - 2);
				// hasSharedIndexBuffer = false;
				break;

			}


		}

		@Override
		public void draw(RendererShadersInterface r) {

			r.bindBufferForVertices(bufferV, 3);
			r.bindBufferForNormals(bufferN, 3, getNormals());
			r.bindBufferForColors(bufferC, 4, getColors());
			if (r.areTexturesEnabled()) {
				r.bindBufferForTextures(bufferT, 2, getTextures());
			}
			r.bindBufferForIndices(bufferI);
			r.draw(getType(), indicesLength);
		}

		@Override
		public void drawLabel(RendererShadersInterface r) {
			r.bindBufferForVertices(bufferV, 3);
			if (r.areTexturesEnabled()) {
				r.bindBufferForTextures(bufferT, 2, getTextures());
			}
			r.bindBufferForIndices(bufferI);
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
	public ManagerShadersElements(Renderer renderer, EuclidianView3D view3d) {
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
		return new GeometriesSetElements();
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
		// return ((GeometryElements) currentGeometriesSet.currentGeometry)
		// .getBufferI(size);
		return null; // TODO
	}

	@Override
	protected void removeGeometrySet(int index) {
		GeometriesSetElements set = (GeometriesSetElements) geometriesSetList
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

		short[] arrayI = new short[size * 3];// getCurrentGeometryIndices(size *
												// 3); //TODO

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

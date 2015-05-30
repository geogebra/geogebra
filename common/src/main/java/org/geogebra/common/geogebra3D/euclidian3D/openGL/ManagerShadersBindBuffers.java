package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.main.App;

/**
 * manager using shaders and binding to gpu buffers
 * 
 * @author mathieu
 *
 */
public class ManagerShadersBindBuffers extends ManagerShadersNoTriangleFan {

	final static public int GLSL_ATTRIB_POSITION = 0;
	final static public int GLSL_ATTRIB_COLOR = 1;
	final static public int GLSL_ATTRIB_NORMAL = 2;
	final static public int GLSL_ATTRIB_TEXTURE = 3;
	final static public int GLSL_ATTRIB_INDEX = 4;
	
	private GPUBuffer curvesIndices;
	private int curvesIndicesSize = -1;

	final static boolean DEBUG = false;

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
			r.createBuffer(ret);
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

		curvesIndices = createBufferIfNeeded(r, curvesIndices);

		if (size > curvesIndicesSize) {

			App.debug("SIZE : " + size);

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

	protected class GeometriesSetBindBuffers extends GeometriesSet {
		@Override
		protected Geometry newGeometry(Type type) {
			return new GeometryBindBuffers(type);
		}

		@Override
		public void bindGeometry(int size, TypeElement type) {
			((GeometryBindBuffers) currentGeometry).bind(
					(RendererShadersInterface) renderer, size, type);
		}
	}

	protected class GeometryBindBuffers extends Geometry {

		private GPUBuffer bufferV = null, bufferN = null, bufferC = null,
				bufferT = null, bufferI = null;

		private short[] arrayI = null;

		private int indicesLength;

		public GeometryBindBuffers(Type type) {
			super(type);
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

			bufferV = ManagerShadersBindBuffers
					.createBufferIfNeeded(r, bufferV);
			r.storeBuffer(getVertices(),
					getLength(), 3, bufferV, GLSL_ATTRIB_POSITION);

			if (getNormals() != null && !getNormals().isEmpty()
					&& getNormals().capacity() != 3) {
				bufferN = ManagerShadersBindBuffers.createBufferIfNeeded(r,
						bufferN);
				r.storeBuffer(getNormals(), getLength(), 3, bufferN,
						GLSL_ATTRIB_NORMAL);
			}

			if (getColors() != null && !getColors().isEmpty()) {
				bufferC = ManagerShadersBindBuffers.createBufferIfNeeded(r,
						bufferC);
				r.storeBuffer(getColors(), getLength(), 4, bufferC,
						GLSL_ATTRIB_COLOR);
			}

			if (getTextures() != null && !getTextures().isEmpty()) {
				bufferT = ManagerShadersBindBuffers.createBufferIfNeeded(r,
						bufferT);
				r.storeBuffer(getTextures(), getLength(), 2, bufferT,
						GLSL_ATTRIB_TEXTURE);
			}

			switch (type) {
			case NONE:
				bufferI = ManagerShadersBindBuffers.createBufferIfNeeded(r,
						bufferI);
				if (arrayI == null
						|| (!indicesDone && arrayI.length != getLength())) {
					App.debug("NEW index buffer");
					arrayI = new short[getLength()];
					for (short i = 0; i < getLength(); i++) {
						arrayI[i] = i;
					}
				} else {
					if (DEBUG) {
						App.debug("keep same index buffer");
					}
				}
				indicesLength = arrayI.length;

				r.storeElementBuffer(arrayI, indicesLength, bufferI);
				break;

			case CURVE:
				if (DEBUG) {
					App.debug("curve: shared index buffer");
				}
				bufferI = getBufferIndicesForCurve(r, size);
				indicesLength = 3 * 2 * size * PlotterBrush.LATITUDES;
				break;

			case SURFACE:
				if (DEBUG) {
					App.debug("surface -- keep same index buffer");
				}

				bufferI = ManagerShadersBindBuffers.createBufferIfNeeded(r,
						bufferI);
				indicesLength = size;

				r.storeElementBuffer(arrayI, indicesLength, bufferI);
				break;
			}


		}

		@Override
		public void draw(RendererShadersInterface r) {

			r.bindBufferForVertices(bufferV, 3);
			r.bindBufferForNormals(bufferN, 3, getNormals());
			r.bindBufferForColors(bufferC, 4, getColors());
			r.bindBufferForTextures(bufferT, 2,
					getTextures());
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
			if (arrayI == null || arrayI.length != size) {
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
	public ManagerShadersBindBuffers(Renderer renderer, EuclidianView3D view3d) {
		super(renderer, view3d);
	}



	@Override
	protected GeometriesSet newGeometriesSet() {
		return new GeometriesSetBindBuffers();
	}

	@Override
	protected PlotterBrush newPlotterBrush() {
		return new PlotterBrushElements(this);
	}

	@Override
	protected PlotterSurface newPlotterSurface() {
		return new PlotterSurfaceElements(this);
	}

	/**
	 * @param size
	 *            size
	 * @return current geometry indices buffer with correct size
	 */
	public short[] getCurrentGeometryIndices(int size) {
		return ((GeometryBindBuffers) currentGeometriesSet.currentGeometry)
				.getBufferI(size);
	}

}

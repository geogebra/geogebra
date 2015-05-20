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

	protected class GeometriesSetBindBuffers extends GeometriesSet {
		@Override
		protected Geometry newGeometry(Type type) {
			return new GeometryBindBuffers(type);
		}

		@Override
		public void bindGeometry() {
			((GeometryBindBuffers) currentGeometry).bind();
		}
	}

	protected class GeometryBindBuffers extends Geometry {

		private GPUBuffers buffers = null;

		private short[] bufferI = null;

		public GeometryBindBuffers(Type type) {
			super(type);
		}

		/**
		 * bind the geometry to its GL buffer
		 */
		public void bind() {

			if (buffers == null) {
				buffers = GLFactory.prototype.newGPUBuffers();
				((RendererShadersInterface) renderer).createBuffers(buffers);
			}

			((RendererShadersInterface) renderer).storeBuffer(getVertices(),
					getLength(), 3, buffers, GLSL_ATTRIB_POSITION);

			if (getNormals() != null && !getNormals().isEmpty()
					&& getNormals().capacity() != 3) {
				((RendererShadersInterface) renderer).storeBuffer(getNormals(),
						getLength(), 3, buffers, GLSL_ATTRIB_NORMAL);
			}

			if (getColors() != null && !getColors().isEmpty()) {
				((RendererShadersInterface) renderer).storeBuffer(getColors(),
						getLength(), 4, buffers, GLSL_ATTRIB_COLOR);
			}

			if (getTextures() != null && !getTextures().isEmpty()) {
				((RendererShadersInterface) renderer).storeBuffer(
						getTextures(), getLength(), 2, buffers,
						GLSL_ATTRIB_TEXTURE);
			}

			if (bufferI == null
					|| (!indicesDone && bufferI.length != getLength())) {
				App.debug("NEW index buffer");
				bufferI = new short[getLength()];
				for (short i = 0; i < getLength(); i++) {
					bufferI[i] = i;
				}
			} else {
				App.debug("keep same index buffer");
			}

			((RendererShadersInterface) renderer).storeElementBuffer(bufferI,
					bufferI.length, buffers);


		}

		@Override
		public void draw(RendererShadersInterface r) {

			r.bindBufferForVertices(buffers, GLSL_ATTRIB_POSITION, 3);
			r.bindBufferForNormals(buffers, GLSL_ATTRIB_NORMAL, 3, getNormals());
			r.bindBufferForColors(buffers, GLSL_ATTRIB_COLOR, 4, getColors());
			r.bindBufferForTextures(buffers, GLSL_ATTRIB_TEXTURE, 2,
					getTextures());
			r.bindBufferForIndices(buffers);
			r.draw(getType(), bufferI.length);
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
			if (bufferI == null || bufferI.length != size) {
				bufferI = new short[size];
			}
			return bufferI;
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

	/**
	 * @param size
	 *            size
	 * @return current geometry indices buffer with correct size
	 */
	public short[] getCurrentGeometryIndices(int size) {
		return ((GeometryBindBuffers) currentGeometriesSet.currentGeometry).getBufferI(size);
	}


}

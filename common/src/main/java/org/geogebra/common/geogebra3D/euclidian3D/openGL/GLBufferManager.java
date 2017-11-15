package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;
import java.util.HashMap;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;

/**
 * manager for packing buffers
 */
public class GLBufferManager {

	private int currentIndex;
	private BufferSegment currentBufferSegment, currentBufferSegmentForIndices;
	private GLBuffer vertexBuffer, normalBuffer, colorBuffer;
	private GLBufferIndices curvesIndices;
	private int totalLength, indicesLength;
	private HashMap<Integer, BufferSegment> bufferSegments, bufferSegmentsForIndices;
	private int indicesIndex;

	private class BufferSegment {
		public int offset;
		public int length;

		public BufferSegment(int offset, int length) {
			this.offset = offset;
			this.length = length;
		}
	}

	/**
	 * constructor
	 */
	public GLBufferManager() {
		vertexBuffer = GLFactory.getPrototype().newBuffer();
		normalBuffer = GLFactory.getPrototype().newBuffer();
		colorBuffer = GLFactory.getPrototype().newBuffer();
		curvesIndices = GLFactory.getPrototype().newBufferIndices();

		int size1 = 2000, size2 = size1 * 3;
		vertexBuffer.allocate(size1 * 3);
		normalBuffer.allocate(size1 * 3);
		colorBuffer.allocate(size1 * 4);
		curvesIndices.allocate(size2);


		bufferSegments = new HashMap<Integer, GLBufferManager.BufferSegment>();
		bufferSegmentsForIndices = new HashMap<Integer, GLBufferManager.BufferSegment>();
	}

	/**
	 * set current geometry index
	 * 
	 * @param index
	 *            index
	 */
	public void setCurrentIndex(int index) {
		currentIndex = index;
	}

	/**
	 * 
	 * @return length
	 */
	public int getLength() {
		return totalLength;
	}

	/**
	 * 
	 * @param array
	 *            array
	 * @param length
	 *            length to set
	 */
	public void setVertexBuffer(ArrayList<Double> array, int length) {
		currentBufferSegment = bufferSegments.get(currentIndex);
		if (currentBufferSegment == null) {
			int l = length / 3;
			currentBufferSegment = new BufferSegment(totalLength, l);
			bufferSegments.put(currentIndex, currentBufferSegment);
			totalLength += l;
			vertexBuffer.setLimit(totalLength * 3);
			normalBuffer.setLimit(totalLength * 3);
			colorBuffer.setLimit(totalLength * 4);
		}
		vertexBuffer.set(array, currentBufferSegment.offset * 3, currentBufferSegment.length * 3);
	}

	/**
	 * 
	 * @param array
	 *            array
	 * @param length
	 *            length to set
	 */
	public void setNormalBuffer(ArrayList<Double> array, int length) {
		normalBuffer.set(array, currentBufferSegment.offset * 3, currentBufferSegment.length * 3);
	}

	/**
	 * set colors buffer
	 * 
	 * @param color
	 *            color
	 */
	public void setColorBuffer(GColor color) {
		int offset = currentBufferSegment.offset * 4;
		colorBuffer.set((float) color.getRed() / 255, offset, currentBufferSegment.length, 4);
		offset++;
		colorBuffer.set((float) color.getGreen() / 255, offset, currentBufferSegment.length, 4);
		offset++;
		colorBuffer.set((float) color.getBlue() / 255, offset, currentBufferSegment.length, 4);
		offset++;
		colorBuffer.set((float) color.getAlpha() / 255, offset, currentBufferSegment.length, 4);
	}

	/**
	 * set indices
	 * 
	 * @param size
	 *            size to set
	 */
	public void setIndices(int size) {
		currentBufferSegmentForIndices = bufferSegmentsForIndices.get(currentIndex);
		if (currentBufferSegmentForIndices == null) {
			int length = 3 * 2 * size * PlotterBrush.LATITUDES;
			currentBufferSegmentForIndices = new BufferSegment(indicesLength, length);
			bufferSegmentsForIndices.put(currentIndex, currentBufferSegmentForIndices);
			indicesLength += length;
			indicesIndex = currentBufferSegmentForIndices.offset;
			curvesIndices.setLimit(indicesLength);
			for (int k = 0; k < size; k++) {
				for (int i = 0; i < PlotterBrush.LATITUDES; i++) {
					int iNext = (i + 1) % PlotterBrush.LATITUDES;
					// first triangle
					putToIndices(i + k * PlotterBrush.LATITUDES);
					putToIndices(i + (k + 1) * PlotterBrush.LATITUDES);
					putToIndices(iNext + (k + 1) * PlotterBrush.LATITUDES);
					// second triangle
					putToIndices(i + k * PlotterBrush.LATITUDES);
					putToIndices(iNext + (k + 1) * PlotterBrush.LATITUDES);
					putToIndices(iNext + k * PlotterBrush.LATITUDES);
				}
			}
		}
	}

	private void putToIndices(int index) {
		curvesIndices.put(indicesIndex, (short) (currentBufferSegment.offset + index));
		indicesIndex++;
	}

	/**
	 * draw
	 * 
	 * @param r
	 *            renderer
	 */
	public void draw(RendererShadersInterface r) {

		vertexBuffer.rewind();
		normalBuffer.rewind();
		curvesIndices.rewind();

		r.loadVertexBuffer(vertexBuffer, totalLength * 3);
		r.loadNormalBuffer(normalBuffer, totalLength * 3);
		r.loadColorBuffer(colorBuffer, totalLength * 4);
		// if (r.areTexturesEnabled()) {
		// r.loadTextureBuffer(getTextures(), getLength());
		// } else {
		r.disableTextureBuffer();
		// }
		r.loadIndicesBuffer(curvesIndices, indicesLength);
		r.draw(Type.TRIANGLES, indicesLength);

	}
}

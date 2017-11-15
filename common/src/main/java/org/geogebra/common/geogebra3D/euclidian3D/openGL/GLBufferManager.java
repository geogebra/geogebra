package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;
import java.util.HashMap;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;

public class GLBufferManager {

	private class BufferSegment {
		public int offset;
		public int length;

		public BufferSegment(int offset, int length) {
			this.offset = offset;
			this.length = length;
		}
	}

	private int currentIndex;
	private BufferSegment currentBufferSegment, currentBufferSegmentForIndices;
	private GLBuffer vertexBuffer, normalBuffer;
	private GLBufferIndices curvesIndices;
	private int totalLength, indicesLength;
	private HashMap<Integer, BufferSegment> bufferSegments, bufferSegmentsForIndices;
	private int indicesIndex;

	public GLBufferManager() {
		vertexBuffer = GLFactory.getPrototype().newBuffer();
		normalBuffer = GLFactory.getPrototype().newBuffer();
		curvesIndices = GLFactory.getPrototype().newBufferIndices();

		int size1 = 2000, size2 = size1;
		vertexBuffer.allocate(size1);
		normalBuffer.allocate(size1);
		curvesIndices.allocate(size2);


		bufferSegments = new HashMap<Integer, GLBufferManager.BufferSegment>();
		bufferSegmentsForIndices = new HashMap<Integer, GLBufferManager.BufferSegment>();
	}

	public void setCurrentIndex(int index) {
		currentIndex = index;
	}

	public int getLength() {
		return totalLength;
	}

	public void setVertexBuffer(ArrayList<Double> array, int length) {
		currentBufferSegment = bufferSegments.get(currentIndex);
		if (currentBufferSegment == null) {
			currentBufferSegment = new BufferSegment(totalLength, length);
			bufferSegments.put(currentIndex, currentBufferSegment);
			totalLength += length;
			vertexBuffer.setLimit(totalLength);
			normalBuffer.setLimit(totalLength);
		}
		vertexBuffer.set(array, currentBufferSegment.offset, length);
	}

	public void setNormalBuffer(ArrayList<Double> array, int length) {
		normalBuffer.set(array, currentBufferSegment.offset, length);
	}

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
		curvesIndices.put(indicesIndex, (short) (currentBufferSegment.offset / 3 + index));
		indicesIndex++;
	}

	public void draw(RendererShadersInterface r) {

		vertexBuffer.rewind();
		normalBuffer.rewind();
		curvesIndices.rewind();

		r.loadVertexBuffer(vertexBuffer, totalLength);
		r.loadNormalBuffer(normalBuffer, totalLength);
		r.loadColorBuffer(null, 0);
		// if (r.areTexturesEnabled()) {
		// r.loadTextureBuffer(getTextures(), getLength());
		// } else {
		r.disableTextureBuffer();
		// }
		r.loadIndicesBuffer(curvesIndices, indicesLength);
		r.draw(Type.TRIANGLES, indicesLength);

	}
}

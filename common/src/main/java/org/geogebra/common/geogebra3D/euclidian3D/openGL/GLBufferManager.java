package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;

/**
 * manager for packing buffers
 */
public class GLBufferManager {

	private Index currentIndex;
	private Index currentLengths;
	private BufferSegment currentBufferSegment;
	private TreeMap<Index, BufferSegment> bufferSegments;
	private int indicesIndex;
	private TreeMap<Index, LinkedList<BufferSegment>> availableSegments;
	private BufferPack currentBufferPack;
	private ArrayList<BufferPack> bufferPackList;
	private ArrayList<Double> vertexArray, normalArray, textureArray;
	private int elementsLength;
	private GColor color;

	private class Index implements Comparable<Index> {
		private int v1, v2;

		/**
		 * simple constructor
		 */
		public Index() {
			// nothing done
		}

		/**
		 * create a copy
		 * 
		 * @param index
		 *            index
		 */
		public Index(Index index) {
			this.v1 = index.v1;
			this.v2 = index.v2;
		}

		public void set(int v1, int v2) {
			this.v1 = v1;
			this.v2 = v2;
		}

		@Override
		public int compareTo(Index o) {
			if (v1 < o.v1) {
				return -1;
			}
			if (v1 > o.v1) {
				return 1;
			}
			if (v2 < o.v2) {
				return -1;
			}
			if (v2 > o.v2) {
				return 1;
			}
			return 0;
		}

		public boolean equals(Object o) {
			if (!(o instanceof Index)) {
				return false;
			}
			Index index = (Index) o;
			return v1 == index.v1 && v2 == index.v2;
		}

		public String toString() {
			return v1 + ", " + v2;
		}
	}

	private class BufferSegment {
		private int elementsOffset, elementsLength;
		private int indicesOffset, indicesLength;
		private BufferPack bufferPack;

		public BufferSegment(BufferPack bufferPack, int elementsLength, int indicesLength) {
			this.bufferPack = bufferPack;
			elementsOffset = bufferPack.totalLength;
			indicesOffset = bufferPack.indicesLength;
			this.elementsLength = elementsLength;
			this.indicesLength = indicesLength;
		}

	}

	private static class BufferPack {
		private GLBuffer vertexBuffer, normalBuffer, textureBuffer, colorBuffer;
		private GLBufferIndices curvesIndices;
		private int totalLength, indicesLength;

		private static int elementsSize = Short.MAX_VALUE + 1;
		// private static int elementsSize = 432;
		private static int indicesSize = elementsSize * 3;

		/**
		 * creates a new buffer pack, using approx. 4MB (8 bytes per float * 32768 * 15
		 * = 3,932,160)
		 */
		public BufferPack() {
			vertexBuffer = GLFactory.getPrototype().newBuffer();
			normalBuffer = GLFactory.getPrototype().newBuffer();
			textureBuffer = GLFactory.getPrototype().newBuffer();
			colorBuffer = GLFactory.getPrototype().newBuffer();
			curvesIndices = GLFactory.getPrototype().newBufferIndices();
			vertexBuffer.allocate(elementsSize * 3);
			normalBuffer.allocate(elementsSize * 3);
			textureBuffer.allocate(elementsSize * 2);
			colorBuffer.allocate(elementsSize * 4);
			curvesIndices.allocate(indicesSize);
			totalLength = 0;
			indicesLength = 0;
		}

		public boolean canAdd(int elementsLength, int indicesLength) {
			return totalLength + elementsLength < elementsSize && this.indicesLength + indicesLength < indicesSize;
		}

		public void addToLength(int elementsLength, int indicesLength) {
			totalLength += elementsLength;
			vertexBuffer.setLimit(totalLength * 3);
			normalBuffer.setLimit(totalLength * 3);
			textureBuffer.setLimit(totalLength * 2);
			colorBuffer.setLimit(totalLength * 4);
			this.indicesLength += indicesLength;
			curvesIndices.setLimit(this.indicesLength);
		}

		public void setElements(ArrayList<Double> vertexArray, ArrayList<Double> normalArray,
				ArrayList<Double> textureArray,
				GColor color, int offset, int length) {
			vertexBuffer.set(vertexArray, offset * 3, length * 3);
			normalBuffer.set(normalArray, offset * 3, length * 3);
			textureBuffer.set(textureArray, offset * 2, length * 2);

			int colorOffset = offset * 4;
			colorBuffer.set((float) color.getRed() / 255, colorOffset, length, 4);
			colorOffset++;
			colorBuffer.set((float) color.getGreen() / 255, colorOffset, length, 4);
			colorOffset++;
			colorBuffer.set((float) color.getBlue() / 255, colorOffset, length, 4);
			colorOffset++;
			colorBuffer.set((float) color.getAlpha() / 255, colorOffset, length, 4);
		}

		public void draw(RendererShadersInterface r) {
			vertexBuffer.rewind();
			normalBuffer.rewind();
			curvesIndices.rewind();
			r.loadVertexBuffer(vertexBuffer, totalLength * 3);
			r.loadNormalBuffer(normalBuffer, totalLength * 3);
			r.loadColorBuffer(colorBuffer, totalLength * 4);
			if (r.areTexturesEnabled()) {
				r.loadTextureBuffer(textureBuffer, totalLength * 2);
			} else {
				r.disableTextureBuffer();
			}
			r.loadIndicesBuffer(curvesIndices, indicesLength);
			r.draw(Type.TRIANGLES, indicesLength);
		}
	}

	/**
	 * constructor
	 */
	public GLBufferManager() {
		currentIndex = new Index();
		currentBufferPack = new BufferPack();
		bufferPackList = new ArrayList<GLBufferManager.BufferPack>();
		bufferPackList.add(currentBufferPack);

		currentLengths = new Index();
		bufferSegments = new TreeMap<Index, GLBufferManager.BufferSegment>();
		availableSegments = new TreeMap<Index, LinkedList<BufferSegment>>();
	}

	/**
	 * set current geometry set and geometry indices
	 * 
	 * @param index
	 *            geometry set index
	 * @param geometryIndex
	 *            geometry index
	 */
	public void setCurrentIndex(int index, int geometryIndex) {
		currentIndex.set(index, geometryIndex);
	}

	/**
	 * 
	 * @param array
	 *            array
	 * @param length
	 *            length to set
	 */
	public void setVertexBuffer(ArrayList<Double> array, int length) {
		vertexArray = array;
		elementsLength = length / 3;
	}

	/**
	 * 
	 * @param array
	 *            array
	 * @param length
	 *            length to set
	 */
	public void setNormalBuffer(ArrayList<Double> array, int length) {
		normalArray = array;
	}

	/**
	 * 
	 * @param array
	 *            array
	 * @param length
	 *            length to set
	 */
	public void setTextureBuffer(ArrayList<Double> array, int length) {
		textureArray = array;
	}

	/**
	 * set colors buffer
	 * 
	 * @param color
	 *            color
	 */
	public void setColorBuffer(GColor color) {
		this.color = color;
	}

	private void setAlphaToTransparent() {
		currentBufferPack.colorBuffer.set(ManagerShadersElementsGlobalBufferPacking.ALPHA_INVISIBLE_VALUE,
				currentBufferSegment.elementsOffset * 4 + 3, currentBufferSegment.elementsLength, 4);
	}

	/**
	 * update color for all geometries from geometry set index
	 * 
	 * @param index
	 *            geometry set index
	 * @param geometriesLength
	 *            geometries length for this set
	 * @param color
	 *            new color
	 */
	public void updateColor(int index, int geometriesLength, GColor color) {
		for (int i = 0; i < geometriesLength; i++) {
			currentIndex.set(index, i);
			currentBufferSegment = bufferSegments.get(currentIndex);
			currentBufferPack = currentBufferSegment.bufferPack;
			setColorBuffer(color);
		}
	}

	/**
	 * update color for all geometries from geometry set index
	 * 
	 * @param index
	 *            geometry set index
	 * @param geometriesLength
	 *            geometries length for this set
	 */
	public void remove(int index, int geometriesLength) {
		for (int i = 0; i < geometriesLength; i++) {
			currentIndex.set(index, i);
			currentBufferSegment = bufferSegments.remove(currentIndex);
			currentBufferPack = currentBufferSegment.bufferPack;
			setAlphaToTransparent();
			addAvailableSegment(currentBufferSegment, availableSegments);
		}
	}

	private void addAvailableSegment(BufferSegment segment, TreeMap<Index, LinkedList<BufferSegment>> availableList) {
		currentLengths.set(segment.elementsLength, segment.indicesLength);
		LinkedList<BufferSegment> list = availableList.get(currentLengths);
		if (list == null) {
			list = new LinkedList<GLBufferManager.BufferSegment>();
			availableList.put(currentLengths, list);
		}
		list.add(segment);
	}


	private BufferSegment getAvailableSegment(Index index, TreeMap<Index, LinkedList<BufferSegment>> availableList) {
		LinkedList<BufferSegment> list = availableList.get(index);
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.pop();
	}

	/**
	 * set indices
	 * 
	 * @param size
	 *            size to set
	 */
	public void setIndices(int size) {
		// get buffer segment and pack
		currentBufferSegment = bufferSegments.get(currentIndex);
		if (currentBufferSegment == null) {
			int indicesLength = 3 * 2 * size * PlotterBrush.LATITUDES;
			currentLengths.set(elementsLength, indicesLength);
			// try to reuse available segment
			currentBufferSegment = getAvailableSegment(currentLengths, availableSegments);
			if (currentBufferSegment == null) {
				if (!currentBufferPack.canAdd(elementsLength, indicesLength)) {
					currentBufferPack = new BufferPack();
					bufferPackList.add(currentBufferPack);
				}
				currentBufferSegment = new BufferSegment(currentBufferPack, elementsLength, indicesLength);
				currentBufferPack.addToLength(elementsLength, indicesLength);
			}
			bufferSegments.put(new Index(currentIndex), currentBufferSegment);
			currentBufferPack = currentBufferSegment.bufferPack;

			// set indices
			indicesIndex = currentBufferSegment.indicesOffset;
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
		} else {
			currentBufferPack = currentBufferSegment.bufferPack;
		}

		// set elements
		currentBufferPack.setElements(vertexArray, normalArray, textureArray, color,
				currentBufferSegment.elementsOffset,
				currentBufferSegment.elementsLength);

		// release arrays
		vertexArray = null;
		normalArray = null;
		textureArray = null;
	}

	private void putToIndices(int index) {
		currentBufferPack.curvesIndices.put(indicesIndex, (short) (currentBufferSegment.elementsOffset + index));
		indicesIndex++;
	}

	/**
	 * draw
	 * 
	 * @param r
	 *            renderer
	 * @param hidden
	 *            if hidden
	 */
	public void draw(RendererShadersInterface r, boolean hidden) {
		((TexturesShaders) r.getTextures()).setPackedDash();
		r.setDashTexture(hidden ? Textures.DASH_PACKED_HIDDEN : Textures.DASH_PACKED);
		for (BufferPack bufferPack : bufferPackList) {
			if (bufferPack.totalLength > 0) {
				bufferPack.draw(r);
			}
		}
	}
}

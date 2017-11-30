package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;

/**
 * manager for packing buffers
 */
abstract class GLBufferManager {

	private Index currentIndex;
	private Index currentLengths;
	/** current buffer segment */
	protected BufferSegment currentBufferSegment;
	private TreeMap<Index, BufferSegment> bufferSegments;
	private int indicesIndex;
	private TreeMap<Index, LinkedList<BufferSegment>> availableSegments;
	private BufferPack currentBufferPack;
	private ArrayList<BufferPack> bufferPackList;
	private ArrayList<Double> vertexArray, normalArray, textureArray;
	private boolean oneNormal;
	private int elementsLength;
	private GColor color;

	static private class Index implements Comparable<Index> {
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

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Index)) {
				return false;
			}
			Index index = (Index) o;
			return v1 == index.v1 && v2 == index.v2;
		}

		@Override
		public int hashCode() {
			return 0; // we don't use it in hash table etc.
		}

		@Override
		public String toString() {
			return v1 + ", " + v2;
		}
	}

	/**
	 * segment in buffer for a geometry element
	 *
	 */
	static class BufferSegment {
		private int elementsOffset, elementsLength;
		private int indicesOffset, indicesLength;
		private BufferPack bufferPack;
		/** element type */
		TypeElement type;

		/**
		 * constructor
		 * 
		 * @param bufferPack
		 *            buffer pack
		 * @param elementsLength
		 *            elements length
		 * @param indicesLength
		 *            indices length
		 */
		public BufferSegment(BufferPack bufferPack, int elementsLength, int indicesLength) {
			this.bufferPack = bufferPack;
			elementsOffset = bufferPack.elementsLength;
			indicesOffset = bufferPack.indicesLength;
			this.elementsLength = elementsLength;
			this.indicesLength = indicesLength;
		}

	}

	private static class BufferPack {
		private GLBufferManager manager;
		private GLBuffer vertexBuffer, normalBuffer, textureBuffer, colorBuffer;
		private GLBufferIndices indicesBuffer;
		private int elementsLength, indicesLength;

		private static int elementsSize = Short.MAX_VALUE + 1;
		private static int indicesSize = elementsSize * 3;

		/**
		 * creates a new buffer pack, using approx. 2MB (4 bytes per float * 32768 * 15)
		 * 
		 * @param manager
		 *            geometries manager
		 */
		public BufferPack(GLBufferManager manager) {
			this.manager = manager;
			vertexBuffer = GLFactory.getPrototype().newBuffer();
			normalBuffer = GLFactory.getPrototype().newBuffer();
			textureBuffer = GLFactory.getPrototype().newBuffer();
			colorBuffer = GLFactory.getPrototype().newBuffer();
			indicesBuffer = GLFactory.getPrototype().newBufferIndices();
			vertexBuffer.allocate(elementsSize * 3);
			normalBuffer.allocate(elementsSize * 3);
			textureBuffer.allocate(elementsSize * 2);
			colorBuffer.allocate(elementsSize * 4);
			indicesBuffer.allocate(indicesSize);
			elementsLength = 0;
			indicesLength = 0;
		}

		public boolean canAdd(int elementsLength, int indicesLength) {
			return this.elementsLength + elementsLength < elementsSize
					&& this.indicesLength + indicesLength < indicesSize;
		}

		public void addToLength(int elementsLength, int indicesLength) {
			this.elementsLength += elementsLength;
			vertexBuffer.setLimit(this.elementsLength * 3);
			normalBuffer.setLimit(this.elementsLength * 3);
			textureBuffer.setLimit(this.elementsLength * 2);
			colorBuffer.setLimit(this.elementsLength * 4);
			this.indicesLength += indicesLength;
			indicesBuffer.setLimit(this.indicesLength);
		}

		public void setElements() {
			int offset = manager.currentBufferSegment.elementsOffset;
			int length = manager.currentBufferSegment.elementsLength;
			vertexBuffer.set(manager.vertexArray, offset * 3, length * 3);
			if (manager.oneNormal) {
				for (int i = 0; i < 3; i++) {
					normalBuffer.set(manager.normalArray.get(i).floatValue(), offset * 3 + i, length, 3);
				}
			} else {
				normalBuffer.set(manager.normalArray, offset * 3, length * 3);
			}
			if (manager.textureArray == null) {
				textureBuffer.set(0, offset * 2, length * 2, 1);
			} else {
				textureBuffer.set(manager.textureArray, offset * 2, length * 2);
			}
			setColor(manager.color, offset, length);
		}

		public void setColor(GColor color, int offset, int length) {
			int colorOffset = offset * 4;
			colorBuffer.set((float) color.getRed() / 255, colorOffset, length, 4);
			colorOffset++;
			colorBuffer.set((float) color.getGreen() / 255, colorOffset, length, 4);
			colorOffset++;
			colorBuffer.set((float) color.getBlue() / 255, colorOffset, length, 4);
			colorOffset++;
			colorBuffer.set((float) color.getAlpha() / 255, colorOffset, length, 4);
		}

		public void setAlpha(int alpha, int offset, int length) {
			colorBuffer.set((float) alpha / 255, offset * 4 + 3, length, 4);
		}

		public void draw(RendererShadersInterface r) {
			vertexBuffer.rewind();
			normalBuffer.rewind();
			indicesBuffer.rewind();
			r.loadVertexBuffer(vertexBuffer, elementsLength);
			r.loadNormalBuffer(normalBuffer, elementsLength);
			r.loadColorBuffer(colorBuffer, elementsLength);
			if (r.areTexturesEnabled()) {
				r.loadTextureBuffer(textureBuffer, elementsLength);
			} else {
				r.disableTextureBuffer();
			}
			r.loadIndicesBuffer(indicesBuffer, indicesLength);
			r.draw(Type.TRIANGLES, indicesLength);
		}

		public void reset() {
			elementsLength = 0;
			indicesLength = 0;
			vertexBuffer.setLimit(0);
			normalBuffer.setLimit(0);
			colorBuffer.setLimit(0);
			textureBuffer.setLimit(0);
			indicesBuffer.setLimit(0);
		}
	}

	/**
	 * constructor
	 */
	public GLBufferManager() {
		currentIndex = new Index();
		currentBufferPack = new BufferPack(this);
		bufferPackList = new ArrayList<>();
		bufferPackList.add(currentBufferPack);

		currentLengths = new Index();
		bufferSegments = new TreeMap<>();
		availableSegments = new TreeMap<>();
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
		oneNormal = length == 3;
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
			currentBufferPack.setColor(color, currentBufferSegment.elementsOffset, currentBufferSegment.elementsLength);
		}
	}

	/**
	 * update visibility for all geometries from geometry set index
	 * 
	 * @param index
	 *            geometry set index
	 * @param geometriesLength
	 *            geometries length for this set
	 * @param visible
	 *            if visible
	 */
	public void updateVisibility(int index, int geometriesLength, boolean visible) {
		int alpha = visible ? color.getAlpha() : -1;
		for (int i = 0; i < geometriesLength; i++) {
			currentIndex.set(index, i);
			currentBufferSegment = bufferSegments.get(currentIndex);
			if (currentBufferSegment != null) { // this may happen after undo from DrawIntersectionCurve3D
				currentBufferPack = currentBufferSegment.bufferPack;
				currentBufferPack.setAlpha(alpha, currentBufferSegment.elementsOffset,
						currentBufferSegment.elementsLength);
			}
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
			addCurrentToAvailableSegments();
		}
	}

	/**
	 * add current buffer segment to available list
	 */
	protected void addCurrentToAvailableSegments() {
		currentBufferPack = currentBufferSegment.bufferPack;
		setAlphaToTransparent();
		currentLengths.set(currentBufferSegment.elementsLength, currentBufferSegment.indicesLength);
		LinkedList<BufferSegment> list = availableSegments.get(currentLengths);
		if (list == null) {
			list = new LinkedList<>();
			availableSegments.put(new Index(currentLengths), list);
		}
		list.add(currentBufferSegment);
	}


	private BufferSegment getAvailableSegment(Index index, TreeMap<Index, LinkedList<BufferSegment>> availableList) {
		LinkedList<BufferSegment> list = availableList.get(index);
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.pop();
	}

	/**
	 * 
	 * @param size
	 *            geometry size
	 * @param type
	 *            element type
	 * @return indices length for this size
	 */
	abstract protected int calculateIndicesLength(int size, TypeElement type);

	/**
	 * put indices to buffer
	 * 
	 * @param size
	 *            geometry size
	 * @param type
	 *            element type
	 */
	abstract protected void putIndices(int size, TypeElement type);

	/**
	 * 
	 * @param indicesLength
	 *            indices length
	 * @param type
	 *            element type
	 * @return true if we can't reuse the same buffer segment
	 */
	protected boolean currentBufferSegmentDoesNotFit(int indicesLength, TypeElement type) {
		if (elementsLength != currentBufferSegment.elementsLength
				|| indicesLength != currentBufferSegment.indicesLength) {
			addCurrentToAvailableSegments();
			return true;
		}
		return false;
	}

	/**
	 * set indices
	 * 
	 * @param size
	 *            size to set
	 * @param type
	 *            element type
	 */
	public void setIndices(int size, TypeElement type) {
		// get buffer segment and pack
		currentBufferSegment = bufferSegments.get(currentIndex);
		int indicesLength = calculateIndicesLength(size, type);
		if (currentBufferSegment == null || currentBufferSegmentDoesNotFit(indicesLength, type)) {
			// try to reuse available segment
			currentLengths.set(elementsLength, indicesLength);
			currentBufferSegment = getAvailableSegment(currentLengths, availableSegments);
			if (currentBufferSegment == null) {
				if (!currentBufferPack.canAdd(elementsLength, indicesLength)) {
					currentBufferPack = new BufferPack(this);
					bufferPackList.add(currentBufferPack);
				}
				currentBufferSegment = new BufferSegment(currentBufferPack, elementsLength, indicesLength);
				currentBufferPack.addToLength(elementsLength, indicesLength);
			}
			currentBufferSegment.type = type;
			bufferSegments.put(new Index(currentIndex), currentBufferSegment);
			currentBufferPack = currentBufferSegment.bufferPack;

			// set indices
			indicesIndex = currentBufferSegment.indicesOffset;
			putIndices(size, type);
		} else {
			currentBufferPack = currentBufferSegment.bufferPack;
		}

		// set elements
		currentBufferPack.setElements();

		// release arrays
		vertexArray = null;
		normalArray = null;
		textureArray = null;
	}

	/**
	 * put index in indices buffer, using current buffer pack and segment
	 * 
	 * @param index
	 *            index to write
	 */
	protected void putToIndices(int index) {
		currentBufferPack.indicesBuffer.put(indicesIndex, (short) (currentBufferSegment.elementsOffset + index));
		indicesIndex++;
	}

	/**
	 * draw buffer packs
	 * 
	 * @param r
	 *            renderer
	 */
	protected void drawBufferPacks(RendererShadersInterface r) {
		for (BufferPack bufferPack : bufferPackList) {
			if (bufferPack.elementsLength > 0) {
				bufferPack.draw(r);
			}
		}
	}

	/**
	 * reset buffers
	 */
	public void reset() {
		availableSegments.clear();
		bufferSegments.clear();
		for (BufferPack bufferPack : bufferPackList) {
			bufferPack.reset();
		}

	}
}

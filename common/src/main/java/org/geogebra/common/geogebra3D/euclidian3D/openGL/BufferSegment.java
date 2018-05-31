package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;

/**
 * segment in buffer for a geometry element
 *
 */
class BufferSegment {
	/** offset for elements in BufferPack */
	int elementsOffset;
	/** length for elements in BufferPack */
	private int elementsLength;
	private int elementsAvailableLength;
	/** offset for indices in BufferPack */
	int indicesOffset;
	/** length for indices in BufferPack */
	private int indicesLength;
	private int indicesAvailableLength;
	/** BufferPack */
	BufferPackAbstract bufferPack;
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
	public BufferSegment(BufferPackAbstract bufferPack, int elementsLength,
			int indicesLength) {
		this(bufferPack, bufferPack.elementsLength, elementsLength,
				bufferPack.indicesLength, indicesLength);
	}

	/**
	 * constructor
	 * 
	 * @param bufferPack
	 *            buffer pack
	 * @param elementsOffset
	 *            elements offset
	 * @param elementsLength
	 *            elements length
	 * @param indicesOffset
	 *            indices offset
	 * @param indicesLength
	 *            indices length
	 */
	public BufferSegment(BufferPackAbstract bufferPack, int elementsOffset,
			int elementsLength, int indicesOffset,
			int indicesLength) {
		this.bufferPack = bufferPack;
		this.elementsOffset = elementsOffset;
		this.indicesOffset = indicesOffset;
		this.elementsLength = elementsLength;
		elementsAvailableLength = elementsLength;
		this.indicesLength = indicesLength;
		indicesAvailableLength = indicesLength;
	}

	/**
	 * 
	 * set index to where start elements and indices
	 * 
	 * @param index
	 *            index
	 */
	public void getStart(Index index) {
		index.set(elementsOffset, indicesOffset);
	}

	/**
	 * set index to where end elements and indices
	 * 
	 * @param index
	 *            index
	 */
	public void getEnd(Index index) {
		index.set(elementsOffset + elementsAvailableLength,
				indicesOffset + indicesAvailableLength);
	}

	/**
	 * 
	 * @return element length
	 */
	public int getElementsLength() {
		return elementsLength;
	}

	/**
	 * 
	 * @return elements available length
	 */
	public int getElementsAvailableLength() {
		return elementsAvailableLength;
	}

	/**
	 * 
	 * @return indices length
	 */
	public int getIndicesLength() {
		return indicesLength;
	}

	/**
	 * 
	 * @return indices available length
	 */
	public int getIndicesAvailableLength() {
		return indicesAvailableLength;
	}

	/**
	 * extend available lengths
	 * 
	 * @param bufferSegment
	 *            buffer segment
	 */
	public void addToAvailableLengths(BufferSegment bufferSegment) {
		elementsAvailableLength += bufferSegment.getElementsAvailableLength();
		indicesAvailableLength += bufferSegment.getIndicesAvailableLength();
	}

	/**
	 * set available lengths
	 * 
	 * @param elementsAvailableLength
	 *            for elements
	 * @param indicesAvailableLength
	 *            for indices
	 */
	public void setAvailableLengths(int elementsAvailableLength,
			int indicesAvailableLength) {
		this.elementsAvailableLength = elementsAvailableLength;
		this.indicesAvailableLength = indicesAvailableLength;
	}

	/**
	 * set lengths
	 * 
	 * @param index
	 *            index
	 */
	public void setLengths(Index index) {
		elementsLength = index.v1;
		indicesLength = index.v2;
	}

	@Override
	public String toString() {
		return elementsOffset + ">" + (elementsOffset + elementsLength) + "/"
				+ (elementsOffset + elementsAvailableLength) + "  "
				+ indicesOffset + ">" + (indicesOffset + indicesLength) + "/"
				+ (indicesOffset + indicesAvailableLength) + "  "
				+ bufferPack;
	}

}
/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
	BufferSegment(BufferPackAbstract bufferPack, int elementsLength,
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
	BufferSegment(BufferPackAbstract bufferPack, int elementsOffset,
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
	void getStart(Index index) {
		index.set(elementsOffset, indicesOffset);
	}

	/**
	 * set index to where end elements and indices
	 * 
	 * @param index
	 *            index
	 */
	void getEnd(Index index) {
		index.set(elementsOffset + elementsAvailableLength,
				indicesOffset + indicesAvailableLength);
	}

	/**
	 * 
	 * @return element length
	 */
	int getElementsLength() {
		return elementsLength;
	}

	/**
	 * 
	 * @return elements available length
	 */
	int getElementsAvailableLength() {
		return elementsAvailableLength;
	}

	/**
	 * 
	 * @return indices length
	 */
	int getIndicesLength() {
		return indicesLength;
	}

	/**
	 * 
	 * @return indices available length
	 */
	int getIndicesAvailableLength() {
		return indicesAvailableLength;
	}

	/**
	 * extend available lengths
	 * 
	 * @param bufferSegment
	 *            buffer segment
	 */
	void addToAvailableLengths(BufferSegment bufferSegment) {
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
	void setAvailableLengths(int elementsAvailableLength,
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
	void setLengths(Index index) {
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
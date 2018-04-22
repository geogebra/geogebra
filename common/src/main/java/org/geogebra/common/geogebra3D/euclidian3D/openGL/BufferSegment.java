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
	int elementsLength;
	/** offset for indices in BufferPack */
	int indicesOffset;
	/** length for indices in BufferPack */
	int indicesLength;
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
		this.bufferPack = bufferPack;
		elementsOffset = bufferPack.elementsLength;
		indicesOffset = bufferPack.indicesLength;
		this.elementsLength = elementsLength;
		this.indicesLength = indicesLength;
	}

}
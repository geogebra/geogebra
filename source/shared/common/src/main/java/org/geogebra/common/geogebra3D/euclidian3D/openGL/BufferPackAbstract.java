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

import java.util.TreeMap;

import org.geogebra.common.awt.GColor;

/**
 * Interface for buffer that packs several geometries
 *
 */
abstract class BufferPackAbstract {

	/** max size for elements */
	public static final int ELEMENT_SIZE_MAX = Short.MAX_VALUE + 1;

	/** elements length */
	int elementsLength;
	/** indices length */
	int indicesLength;

	private TreeMap<Index, BufferSegment> segmentEnds;
	private TreeMap<Index, BufferSegment> segmentStarts;

	/**
	 * 
	 * @param elementsLengthOther
	 *            geometry elements length
	 * @param indicesLengthOther
	 *            geometry indices length
	 * @return true if possible to add geometry to this pack
	 */
	abstract boolean canAdd(int elementsLengthOther, int indicesLengthOther);

	/**
	 * Prepare buffers to add geometry and update length
	 * 
	 * @param elementsLengthToAdd
	 *            geometry to add elements length
	 * @param indicesLengthToAdd
	 *            geometry to add indices length
	 */
	abstract void addToLength(int elementsLengthToAdd, int indicesLengthToAdd);

	/**
	 * set elements to buffers
	 */
	abstract void setElements();

	/**
	 * set elements to buffers
	 * 
	 * @param translate
	 *            translate all vertices
	 * @param scale
	 *            scale all vertices
	 * @param reuseSegment
	 *            says if it reuses an existing segment
	 */
	abstract void setElements(float[] translate, float scale,
			boolean reuseSegment);

	/**
	 * set color to buffer
	 * 
	 * @param color
	 *            color
	 * @param layer
	 *            layer
	 * @param offset
	 *            offset where to write
	 * @param length
	 *            length to write
	 */
	abstract void setColorAndLayer(GColor color, int layer, int offset, int length);

	/**
	 * set alpha to current buffer segment
	 * 
	 * @param alpha
	 *            alpha value
	 * @param layer
	 *            layer
	 */
	abstract void setAlphaAndLayer(int alpha, int layer);

	/**
	 * draw this pack
	 * 
	 * @param r
	 *            renderer
	 */
	abstract void draw(Renderer r);

	/**
	 * reset buffers and lengths
	 */
	protected void reset() {
		if (segmentEnds != null) {
			segmentEnds.clear();
		}
	}

	/**
	 * set alpha values to transparent
	 * 
	 * @param offset
	 *            elements offset
	 * @param length
	 *            elements length
	 */
	abstract void setAlphaToTransparent(int offset, int length);

	/**
	 * put index in indices buffer
	 * 
	 * @param indicesIndex
	 *            index in buffer
	 * @param value
	 *            value to put
	 * 
	 */
	abstract void putToIndices(int indicesIndex, short value);

	/**
	 * 
	 * @param position
	 *            position in buffer
	 * @return vertex buffer at position
	 */
	abstract GLBuffer getVertexBuffer(int position);

	/**
	 * 
	 * @param position
	 *            position in buffer
	 * @return normal buffer at position
	 */
	abstract GLBuffer getNormalBuffer(int position);

	/**
	 * 
	 * @param position
	 *            position in buffer
	 * @return vertex buffer at position
	 */
	abstract GLBufferIndices getIndicesBuffer(int position);

	/**
	 * 
	 * @return if can be reused (when adding available segments)
	 */
	public boolean canBeReused() {
		return true;
	}

	/**
	 * 
	 * @return segments ends
	 */
	public TreeMap<Index, BufferSegment> getSegmentEnds() {
		if (segmentEnds == null) {
			segmentEnds = new TreeMap<>();
		}
		return segmentEnds;
	}

	/**
	 * 
	 * @return segments starts
	 */
	public TreeMap<Index, BufferSegment> getSegmentStarts() {
		if (segmentStarts == null) {
			segmentStarts = new TreeMap<>();
		}
		return segmentStarts;
	}

	/**
	 * 
	 * @return true if used as a big buffer
	 */
	public boolean isBigBuffer() {
		return false;
	}
}
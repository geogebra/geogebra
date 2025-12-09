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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;

/**
 * manager for packing buffers
 */
abstract class GLBufferManager {

	/** alpha value for invisible geometries */
	static final int ALPHA_INVISIBLE = -1;

	private Index currentIndex;
	/** current elements/indices lengths */
	protected Index currentLengths;
	/** current buffer segment */
	protected BufferSegment currentBufferSegment;
	private TreeMap<Index, BufferSegment> bufferSegments;
	/** indices index for writing */
	protected int indicesIndex;
	/** segments available for reuse */
	protected TreeMap<Index, LinkedList<BufferSegment>> availableSegments;
	/** current buffer pack */
	protected BufferPackAbstract currentBufferPack;
	/** list of buffer packs */
	protected ArrayList<BufferPackAbstract> bufferPackList;
	/** vertex array for current geometry */
	ArrayList<Double> vertexArray;
	/** normal array for current geometry */
	ArrayList<Double> normalArray;
	/** texture array for current geometry */
	ArrayList<Double> textureArray;
	/** flag for if current geometry uses one normal */
	boolean oneNormal;
	/** elements length */
	protected int elementsLength;
	/** color for current geometry */
	GColor color;
	/** layer for current geometry */
	int layer;

	/**
	 * 
	 * @param size
	 *            curve size
	 * @return elements length for given curve size
	 */
	static public int getElementsLengthForCurve(int size) {
		return (size + 1) * PlotterBrush.LATITUDES;
	}

	/**
	 * 
	 * @param elementsLength
	 *            elements length
	 * @return size for given curve elements length
	 */
	static public int getSizeForCurveFromElements(int elementsLength) {
		return elementsLength / PlotterBrush.LATITUDES - 1;
	}

	/**
	 * 
	 * @param size
	 *            curve size
	 * @return indices length for given curve size
	 */
	static public int getIndicesLengthForCurve(int size) {
		return 3 * 2 * size * PlotterBrush.LATITUDES;
	}

	/**
	 * constructor
	 */
	public GLBufferManager() {
		currentIndex = new Index();
		bufferPackList = new ArrayList<>();

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
	 */
	public void setTextureBuffer(ArrayList<Double> array) {
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

	/**
	 * set layer
	 * 
	 * @param layer
	 *            layer
	 */
	public void setLayer(int layer) {
		this.layer = layer;
	}

	/**
	 * set current buffer segment alpha to transparent
	 */
	protected void setAlphaToTransparent() {
		currentBufferPack.setAlphaToTransparent(
				currentBufferSegment.elementsOffset,
				currentBufferSegment.getElementsLength());
	}

	/**
	 * update color for all geometries from geometry set index
	 * 
	 * @param index
	 *            geometry set index
	 * @param geometriesLength
	 *            geometries length for this set
	 * @param objColor
	 *            new color
	 * @param objLayer
	 *            layer
	 */
	public void updateColorAndLayer(int index, int geometriesLength,
			GColor objColor, int objLayer) {
		for (int i = 0; i < geometriesLength; i++) {
			currentIndex.set(index, i);
			currentBufferSegment = bufferSegments.get(currentIndex);
			if (currentBufferSegment != null) {
				currentBufferPack = currentBufferSegment.bufferPack;
				currentBufferPack.setColorAndLayer(objColor, objLayer,
						currentBufferSegment.elementsOffset,
						currentBufferSegment.getElementsLength());
			}
		}
	}

	/**
	 * update visibility for all geometries from geometry set index
	 * 
	 * @param index
	 *            geometry set index
	 * @param start
	 *            first geometry to update
	 * @param geometriesLength
	 *            geometries length for this set
	 * @param visible
	 *            if visible
	 * @param alpha
	 *            object alpha
	 * @param objLayer
	 *            object layer
	 */
	public void updateVisibility(int index, int start, int geometriesLength, boolean visible,
			int alpha, int objLayer) {
		int alphaOrInvisible = visible ? alpha : ALPHA_INVISIBLE;
		for (int i = start; i < geometriesLength; i++) {
			currentIndex.set(index, i);
			currentBufferSegment = bufferSegments.get(currentIndex);
			if (currentBufferSegment != null) {
				// this may happen after undo from DrawIntersectionCurve3D
				currentBufferPack = currentBufferSegment.bufferPack;
				currentBufferPack.setAlphaAndLayer(alphaOrInvisible, objLayer);
			}
		}
	}

	/**
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
		if (currentBufferSegment == null) {
			return;
		}
		currentBufferPack = currentBufferSegment.bufferPack;
		if (currentBufferPack.canBeReused()) {
			addCurrentToAvailableSegmentsMayMerge();
		} else {
			bufferPackList.remove(currentBufferPack);
		}
	}

	/**
	 * add current buffer segment to available list or merge it with previous
	 */
	protected void addCurrentToAvailableSegmentsMayMerge() {
		setAlphaToTransparent();
		currentLengths.setAvailableLengths(currentBufferSegment);
		addToAvailableSegments(currentBufferSegment);
	}

	/**
	 * add buffer segment to available list
	 * 
	 * @param bufferSegment
	 *            buffer segment
	 */
	protected void addToAvailableSegments(BufferSegment bufferSegment) {
		LinkedList<BufferSegment> list = availableSegments.get(currentLengths);
		if (list == null) {
			list = new LinkedList<>();
			availableSegments.put(new Index(currentLengths), list);
		}
		list.add(bufferSegment);
	}

	/**
	 * @return available segment for current length
	 */
	protected BufferSegment getAvailableSegment() {
		LinkedList<BufferSegment> list = availableSegments.get(currentLengths);
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
	 * @param reuseSegment
	 *            says if it is reusing a segment
	 */
	abstract protected void putIndices(int size, TypeElement type,
			boolean reuseSegment);

	private boolean currentBufferSegmentDoesNotFit(int indicesLength,
			TypeElement type) {
		if (checkCurrentBufferSegmentDoesNotFit(indicesLength, type)) {
			addCurrentToAvailableSegments();
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param indicesLength
	 *            indices length
	 * @param type
	 *            element type
	 * @return true if current buffer segment doesn't fit length and type
	 */
	protected boolean checkCurrentBufferSegmentDoesNotFit(int indicesLength,
			TypeElement type) {
		return elementsLength != currentBufferSegment.getElementsLength()
				|| indicesLength != currentBufferSegment.getIndicesLength();
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
		boolean reuseSegment = false;
		if (currentBufferSegment == null || currentBufferSegmentDoesNotFit(indicesLength, type)) {
			// try to reuse available segment
			currentLengths.set(elementsLength, indicesLength);
			currentBufferSegment = getAvailableSegment();
			if (currentBufferSegment == null) {
				if (currentBufferPack == null || !currentBufferPack
						.canAdd(elementsLength, indicesLength)) {
					useAnotherBufferPack();
				}
				currentBufferSegment = new BufferSegment(currentBufferPack, elementsLength,
						indicesLength);
				addToLengthToCurrentBufferPack(elementsLength, indicesLength);
			} else {
				reuseSegment = true;
			}
			currentBufferSegment.type = type;
			bufferSegments.put(new Index(currentIndex), currentBufferSegment);
			currentBufferPack = currentBufferSegment.bufferPack;

			// set indices
			indicesIndex = currentBufferSegment.indicesOffset;
			putIndices(size, type, reuseSegment);
		} else {
			currentBufferPack = currentBufferSegment.bufferPack;
			reuseSegment = true;
		}

		// set elements
		setElements(reuseSegment, type);

		// release arrays
		vertexArray = null;
		normalArray = null;
		textureArray = null;
	}

	protected void addToLengthToCurrentBufferPack(int elementsLengthToAdd,
			int indicesLengthToAdd) {
		currentBufferPack.addToLength(elementsLengthToAdd, indicesLengthToAdd);
	}

	/**
	 * set elements to current buffer pack
	 * 
	 * @param reuseSegment
	 *            says if segment is reused
	 * @param type
	 *            element type
	 */
	protected void setElements(boolean reuseSegment, TypeElement type) {
		currentBufferPack.setElements();
	}

	/**
	 * put index in indices buffer, using current buffer pack and segment
	 * 
	 * @param index
	 *            index to write
	 */
	protected void putToIndices(int index) {
		currentBufferPack.putToIndices(indicesIndex,
				(short) (currentBufferSegment.elementsOffset + index));
		indicesIndex++;
	}

	/**
	 * draw buffer packs
	 * 
	 * @param r
	 *            renderer
	 */
	protected void drawBufferPacks(Renderer r) {

		for (BufferPackAbstract bufferPack : bufferPackList) {
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
		ArrayList<BufferPackAbstract> buffersToRemove = new ArrayList<>();
		for (BufferPackAbstract bufferPack : bufferPackList) {
			if (bufferPack.canBeReused()) {
				bufferPack.reset();
			} else {
				buffersToRemove.add(bufferPack);
			}
		}
		bufferPackList.removeAll(buffersToRemove);
	}

	/**
	 * WARNING: must be power of 2, and less than ELEMENT_SIZE_MAX = Short.MAX_VALUE
	 * 
	 * @return elements size at start
	 */
	abstract protected int getElementSizeStart();

	/**
	 * 
	 * @return indices size at start
	 */
	abstract protected int getIndicesSizeStart();

	/**
	 * 
	 * @return current buffer segment elements length
	 */
	public int getCurrentElementsLength() {
		return currentBufferSegment.getElementsLength();
	}

	/**
	 * 
	 * @return vertex buffer positioned to current buffer segment offset
	 */
	public GLBuffer getCurrentBufferVertices() {
		return currentBufferSegment.bufferPack
				.getVertexBuffer(currentBufferSegment.elementsOffset * 3);
	}

	/**
	 * 
	 * @return normal buffer positioned to current buffer segment offset
	 */
	public GLBuffer getCurrentBufferNormals() {
		return currentBufferSegment.bufferPack
				.getNormalBuffer(currentBufferSegment.elementsOffset * 3);
	}

	/**
	 * 
	 * @return current buffer segment elements offset
	 */
	public int getCurrentElementsOffset() {
		return currentBufferSegment.elementsOffset;
	}

	/**
	 * 
	 * @return current buffer segment indices length
	 */
	public int getCurrentIndicesLength() {
		return currentBufferSegment.getIndicesLength();
	}

	/**
	 * 
	 * @return indices buffer positioned to current buffer segment offset
	 */
	public GLBufferIndices getCurrentBufferIndices() {
		return currentBufferSegment.bufferPack
				.getIndicesBuffer(currentBufferSegment.indicesOffset);
	}

	/**
	 * set current buffer segment to the one stored at current index
	 */
	public void setBufferSegmentToCurrentIndex() {
		currentBufferSegment = bufferSegments.get(currentIndex);
	}

	/**
	 * 
	 * @return true if buffer manager for creating points templates
	 */
	public boolean isTemplateForPoints() {
		return false;
	}

	/**
	 * 
	 * use another buffer pack
	 */
	protected void useAnotherBufferPack() {
		currentBufferPack = new BufferPack(this);
		bufferPackList.add(currentBufferPack);
	}

	/**
	 * put to indices for curve
	 * 
	 * @param size
	 *            curve size
	 */
	protected void putToIndicesForCurve(int size) {
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

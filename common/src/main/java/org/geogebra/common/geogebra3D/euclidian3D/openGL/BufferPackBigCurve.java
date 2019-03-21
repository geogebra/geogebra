package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.awt.GColor;

/**
 * Buffer for big curves (e.g. cartesian curves)
 * 
 *
 */
public class BufferPackBigCurve extends BufferPackAbstract {

	/**
	 * max size for a curve
	 */
	public static final int CURVE_SIZE_MAX = (Short.MAX_VALUE + 1)
			/ PlotterBrush.LATITUDES - 1;

	private static final int INDICES_LENGTH_MAX = GLBufferManager
			.getIndicesLengthForCurve(CURVE_SIZE_MAX);

	private GLBufferManager manager;
	private BufferPack[] bufferPacks;
	private BufferPack lastBuffer;
	private int bufferCount;
	private int lastBufferSize;
	private int lastBufferElementsLength;
	private int lastBufferIndicesLength;

	/**
	 * creates a new buffer pack for big geometries
	 * 
	 * @param manager
	 *            geometries manager
	 */
	public BufferPackBigCurve(GLBufferManager manager) {
		this.manager = manager;
		elementsLength = 0;
		indicesLength = 0;
	}

	@Override
	public boolean canAdd(int elementsLengthOther, int indicesLengthOther) {
		return false;
	}

	@Override
	public void addToLength(int elementsLengthToAdd, int indicesLengthToAdd) {
		elementsLength = elementsLengthToAdd;
		indicesLength = indicesLengthToAdd;
		int size = elementsLength / PlotterBrush.LATITUDES - 1;
		bufferCount = size / CURVE_SIZE_MAX;
		bufferPacks = new BufferPack[bufferCount];
		for (int i = 0; i < bufferCount; i++) {
			bufferPacks[i] = new BufferPack(manager, ELEMENT_SIZE_MAX,
					INDICES_LENGTH_MAX);
			bufferPacks[i].addToLength(ELEMENT_SIZE_MAX, INDICES_LENGTH_MAX);
		}
		lastBufferSize = size % CURVE_SIZE_MAX;
		if (lastBufferSize > 0) {
			lastBufferElementsLength = GLBufferManager
					.getElementsLengthForCurve(lastBufferSize);
			lastBufferIndicesLength = GLBufferManager
					.getIndicesLengthForCurve(lastBufferSize);
			lastBuffer = new BufferPack(manager, lastBufferElementsLength,
					lastBufferIndicesLength);
			lastBuffer.addToLength(lastBufferElementsLength,
					lastBufferIndicesLength);
		} else {
			lastBufferElementsLength = 0;
			lastBufferIndicesLength = 0;
		}

	}

	@Override
	public void setElements() {
		for (int i = 0; i < bufferCount; i++) {
			bufferPacks[i].setElementsForBigCurve(i, ELEMENT_SIZE_MAX);
		}
		if (lastBufferElementsLength > 0) {
			lastBuffer.setElementsForBigCurve(bufferCount,
					lastBufferElementsLength);
		}
	}

	@Override
	public void putToIndices(int indicesIndex, short value) {
		bufferPacks[0].putToIndices(indicesIndex, value);
		if (indicesIndex < lastBufferIndicesLength) {
			lastBuffer.putToIndices(indicesIndex, value);
		}
	}

	/**
	 * clone indices to all buffers
	 */
	public void cloneIndices() {
		BufferPack first = bufferPacks[0];
		for (int i = 1; i < bufferCount; i++) {
			bufferPacks[i].indicesBuffer = first.indicesBuffer;
		}
	}

	@Override
	public void setElements(float[] translate, float scale,
			boolean reuseSegment) {
		// not used
	}

	@Override
	public void setColorAndLayer(GColor color, int layer, int offset, int length) {
		for (BufferPack bufferPack : bufferPacks) {
			bufferPack.setColorAndLayer(color, layer, 0, ELEMENT_SIZE_MAX);
		}
		if (lastBufferElementsLength > 0) {
			lastBuffer.setColorAndLayer(color, layer, 0, lastBufferElementsLength);
		}
	}

	@Override
	public void setAlphaAndLayer(int alpha, int layer) {
		for (BufferPack bufferPack : bufferPacks) {
			bufferPack.setAlphaAndLayer(alpha, layer, ELEMENT_SIZE_MAX);
		}
		if (lastBufferElementsLength > 0) {
			lastBuffer.setAlphaAndLayer(alpha, layer, lastBufferElementsLength);
		}
	}

	@Override
	public void draw(Renderer r) {
		for (BufferPack bufferPack : bufferPacks) {
			bufferPack.draw(r);
		}
		if (lastBufferElementsLength > 0) {
			lastBuffer.draw(r);
		}
	}

	@Override
	public void reset() {
		// no need
	}

	@Override
	public void setAlphaToTransparent(int offset, int length) {
		for (BufferPack bufferPack : bufferPacks) {
			bufferPack.setAlphaToTransparent(0, ELEMENT_SIZE_MAX);
		}
		if (lastBufferElementsLength > 0) {
			lastBuffer.setAlphaToTransparent(0, lastBufferElementsLength);
		}
	}

	@Override
	public GLBuffer getVertexBuffer(int position) {
		// TODO export all sub buffers?
		return bufferPacks[0].getVertexBuffer(0);
	}

	@Override
	public GLBuffer getNormalBuffer(int position) {
		// TODO export all sub buffers?
		return bufferPacks[0].getNormalBuffer(0);
	}

	@Override
	public GLBufferIndices getIndicesBuffer(int position) {
		// TODO export all sub buffers?
		return bufferPacks[0].getIndicesBuffer(0);
	}

	@Override
	public boolean canBeReused() {
		return false;
	}

	@Override
	public boolean isBigBuffer() {
		return true;
	}

}

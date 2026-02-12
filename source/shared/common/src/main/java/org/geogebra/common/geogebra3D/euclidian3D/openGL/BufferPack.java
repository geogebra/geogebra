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

import org.geogebra.common.awt.GColor;

/**
 * Buffer that packs several geometries
 */
class BufferPack extends BufferPackAbstract {

	private GLBufferManager manager;
	/** buffer for vertices */
	private GLBuffer vertexBuffer;
	/** buffer for normals */
	private GLBuffer normalBuffer;
	private GLBuffer textureBuffer;
	/** buffer for colors */
	private GLBuffer colorBuffer;
	/** buffer for indices */
	protected GLBufferIndices indicesBuffer;

	private int elementsSize;
	private int indicesSize;

	/**
	 * creates a new buffer pack, using approx. 2MB (4 bytes per float * 32768 * 15)
	 * at max
	 * 
	 * @param manager
	 *            geometries manager
	 */
	protected BufferPack(GLBufferManager manager) {
		this(manager, manager.getElementSizeStart(),
				manager.getIndicesSizeStart());
	}

	/**
	 * creates a new buffer pack
	 * 
	 * @param manager
	 *            geometries manager
	 * @param elementsSize
	 *            elementsSize
	 * @param indicesSize
	 *            indicesSize
	 */
	protected BufferPack(GLBufferManager manager, int elementsSize,
			int indicesSize) {
		this.manager = manager;
		vertexBuffer = GLFactory.getPrototype().newBuffer();
		normalBuffer = GLFactory.getPrototype().newBuffer();
		textureBuffer = GLFactory.getPrototype().newBuffer();
		colorBuffer = GLFactory.getPrototype().newBuffer();
		indicesBuffer = GLFactory.getPrototype().newBufferIndices();

		this.elementsSize = elementsSize;
		this.indicesSize = indicesSize;
		vertexBuffer.allocate(elementsSize * 3);
		normalBuffer.allocate(elementsSize * 3);
		textureBuffer.allocate(elementsSize * 2);
		colorBuffer.allocate(elementsSize * 4);
		indicesBuffer.allocate(indicesSize);

		elementsLength = 0;
		indicesLength = 0;
	}

	private void reallocateElements(int size) {
		elementsSize = size;
		vertexBuffer.reallocate(size * 3);
		normalBuffer.reallocate(size * 3);
		textureBuffer.reallocate(size * 2);
		colorBuffer.reallocate(size * 4);
	}

	private void reallocateIndices(int size) {
		indicesSize = size;
		indicesBuffer.reallocate(indicesSize);
	}

	private static int multiplyByPowerOfTwoToMakeItGreaterThan(int current, int min) {
		int ret = current * 2;
		while (ret < min) {
			ret *= 2;
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeBufferPackInterface#canAdd(int, int)
	 */
	@Override
	public boolean canAdd(int elementsLengthOther, int indicesLengthOther) {
		return this.elementsLength + elementsLengthOther < ELEMENT_SIZE_MAX;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see BufferPackInterface#addToLength(int, int)
	 */
	@Override
	public void addToLength(int elementsLengthToAdd, int indicesLengthToAdd) {
		elementsLength += elementsLengthToAdd;
		if (elementsLength > elementsSize) {
			reallocateElements(
					multiplyByPowerOfTwoToMakeItGreaterThan(elementsSize, elementsLength));
		}
		vertexBuffer.setLimit(this.elementsLength * 3);
		normalBuffer.setLimit(this.elementsLength * 3);
		textureBuffer.setLimit(this.elementsLength * 2);
		colorBuffer.setLimit(this.elementsLength * 4);
		this.indicesLength += indicesLengthToAdd;
		if (indicesLength > indicesSize) {
			reallocateIndices(multiplyByPowerOfTwoToMakeItGreaterThan(indicesSize, indicesLength));
		}
		indicesBuffer.setLimit(this.indicesLength);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.common.geogebra3D.euclidian3D.openGL.BufferPackInterface#setElements()
	 */
	@Override
	public void setElements() {
		int offset = manager.currentBufferSegment.elementsOffset;
		int length = manager.currentBufferSegment.getElementsLength();
		vertexBuffer.set(manager.vertexArray, offset * 3, length * 3);
		if (manager.oneNormal) {
			for (int i = 0; i < 3; i++) {
				normalBuffer.set(manager.normalArray.get(i).floatValue(), offset * 3 + i, length,
						3);
			}
		} else {
			normalBuffer.set(manager.normalArray, offset * 3, length * 3);
		}
		if (manager.textureArray == null) {
			textureBuffer.set(0, offset * 2, length * 2, 1);
		} else {
			textureBuffer.set(manager.textureArray, offset * 2, length * 2);
		}
		if (manager.color != null) {
			setColorAndLayer(manager.color, manager.layer, offset, length);
		}
	}

	/**
	 * set elements for big curve
	 * 
	 * @param curve
	 *            curve index for array offset
	 * 
	 * @param length
	 *            length to write
	 */
	protected void setElementsForBigCurve(int curve, int length) {
		int arrayOffset = (ELEMENT_SIZE_MAX - PlotterBrush.LATITUDES) * curve;
		vertexBuffer.set(manager.vertexArray, arrayOffset * 3, 0, length * 3);
		normalBuffer.set(manager.normalArray, arrayOffset * 3, 0, length * 3);
		textureBuffer.set(manager.textureArray, arrayOffset * 2, 0, length * 2);

		if (manager.color != null) {
			setColorAndLayer(manager.color, manager.layer, 0, length);
		}
	}

	/* (non-Javadoc)
	 * @see org.geogebra.common.geogebra3D.euclidian3D.openGL.BufferPackInterface#setElements(float[], float, boolean)
	 */
	@Override
	public void setElements(float[] translate, float scale,
			boolean reuseSegment) {
		int offset = manager.currentBufferSegment.elementsOffset;
		int length = manager.currentBufferSegment.getElementsLength();
		vertexBuffer.set(manager.vertexArray, translate, scale, offset * 3,
				length * 3);
		if (!reuseSegment) {
			normalBuffer.set(manager.normalArray, offset * 3, length * 3);
			textureBuffer.set(0, offset * 2, length * 2, 1);
		}

		if (manager.color != null) {
			setColorAndLayer(manager.color, manager.layer, offset, length);
		}
	}

	/* (non-Javadoc)
	 * @see org.geogebra.common.geogebra3D.euclidian3D.openGL.BufferPackInterface#setColor(org.geogebra.common.awt.GColor, int, int)
	 */
	@Override
	public void setColorAndLayer(GColor color, int layer, int offset, int length) {
		int colorOffset = offset * 4;
		colorBuffer.set((float) color.getRed() / 255, colorOffset, length, 4);
		colorOffset++;
		colorBuffer.set((float) color.getGreen() / 255, colorOffset, length, 4);
		colorOffset++;
		colorBuffer.set((float) color.getBlue() / 255, colorOffset, length, 4);
		colorOffset++;
		setAlpha(color.getAlpha(), layer, colorOffset, length);
	}

	@Override
	public void setAlphaAndLayer(int alpha, int layer) {
		setAlpha(alpha, layer,
				manager.currentBufferSegment.elementsOffset * 4 + 3,
				manager.currentBufferSegment.getElementsLength());
	}

	/**
	 * set alpha
	 * 
	 * @param alpha
	 *            alpha value
	 * @param length
	 *            length
	 */
	protected void setAlphaAndLayer(int alpha, int layer, int length) {
		setAlpha(alpha, layer, 3, length);
	}

	private void setAlpha(int alpha, int layer, int offset, int length) {
		colorBuffer.set(alpha <= 0 ? GLBufferManager.ALPHA_INVISIBLE
				: (alpha >= 255 ? 1f : ((float) alpha / 255))
						+ Renderer.LAYER_FACTOR_FOR_CODING
								* (layer - Renderer.LAYER_MIN),
				offset, length, 4);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.common.geogebra3D.euclidian3D.openGL.BufferPackInterface#draw(org.geogebra.common.geogebra3D.euclidian3D.openGL.RendererShadersInterface)
	 */
	@Override
	public void draw(Renderer r) {
		vertexBuffer.rewind();
		normalBuffer.rewind();
		indicesBuffer.rewind();
		r.getRendererImpl().loadVertexBuffer(vertexBuffer, elementsLength);
		r.getRendererImpl().loadNormalBuffer(normalBuffer, elementsLength);
		r.getRendererImpl().loadColorBuffer(colorBuffer, elementsLength);
		if (r.getRendererImpl().areTexturesEnabled()) {
			r.getRendererImpl().loadTextureBuffer(textureBuffer,
					elementsLength);
		} else {
			r.getRendererImpl().disableTextureBuffer();
		}
		r.getRendererImpl().loadIndicesBuffer(indicesBuffer, indicesLength);
		r.getRendererImpl().draw(Manager.Type.TRIANGLES, indicesLength);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.common.geogebra3D.euclidian3D.openGL.BufferPackInterface#reset()
	 */
	@Override
	protected void reset() {
		super.reset();
		elementsLength = 0;
		indicesLength = 0;
		vertexBuffer.setLimit(0);
		normalBuffer.setLimit(0);
		colorBuffer.setLimit(0);
		textureBuffer.setLimit(0);
		indicesBuffer.setLimit(0);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.common.geogebra3D.euclidian3D.openGL.BufferPackInterface#setAlphaToTransparent(int, int)
	 */
	@Override
	public void setAlphaToTransparent(int offset, int length) {
		colorBuffer.set(
				ManagerShaders.ALPHA_INVISIBLE_VALUE,
				offset * 4 + 3, length, 4);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.common.geogebra3D.euclidian3D.openGL.BufferPackInterface#putToIndices(int, short)
	 */
	@Override
	public void putToIndices(int indicesIndex, short value) {
		indicesBuffer.put(indicesIndex, value);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.common.geogebra3D.euclidian3D.openGL.BufferPackInterface#getVertexBuffer(int)
	 */
	@Override
	public GLBuffer getVertexBuffer(int position) {
		vertexBuffer.position(position);
		return vertexBuffer;
	}

	/* (non-Javadoc)
	 * @see org.geogebra.common.geogebra3D.euclidian3D.openGL.BufferPackInterface#getNormalBuffer(int)
	 */
	@Override
	public GLBuffer getNormalBuffer(int position) {
		normalBuffer.position(position);
		return normalBuffer;
	}

	/* (non-Javadoc)
	 * @see org.geogebra.common.geogebra3D.euclidian3D.openGL.BufferPackInterface#getIndicesBuffer(int)
	 */
	@Override
	public GLBufferIndices getIndicesBuffer(int position) {
		indicesBuffer.position(position);
		return indicesBuffer;
	}

}
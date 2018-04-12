package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;

/**
 * Buffer that packs several geometries
 */
class BufferPack {

	static final private int ELEMENT_SIZE_MAX = Short.MAX_VALUE;

	private GLBufferManager manager;
	/** buffer for vertices */
	GLBuffer vertexBuffer;
	/** buffer for normals */
	GLBuffer normalBuffer;
	private GLBuffer textureBuffer;
	/** buffer for colors */
	GLBuffer colorBuffer;
	/** buffer for indices */
	GLBufferIndices indicesBuffer;
	/** elements length */
	int elementsLength;
	/** indices length */
	int indicesLength;

	private int elementsSize, indicesSize;

	/**
	 * creates a new buffer pack, using approx. 2MB (4 bytes per float * 32768 * 15)
	 * at max
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

		elementsSize = manager.getElementSizeStart();
		indicesSize = manager.getIndicesSizeStart();
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

	/**
	 * 
	 * @param elementsLength
	 *            geometry elements length
	 * @param indicesLength
	 *            geometry indices length
	 * @return true if possible to add geometry to this pack
	 */
	public boolean canAdd(int elementsLength, int indicesLength) {
		return this.elementsLength + elementsLength < ELEMENT_SIZE_MAX;
	}

	/**
	 * Prepare buffers to add geometry and update length
	 * 
	 * @param elementsLengthToAdd
	 *            geometry to add elements length
	 * @param indicesLengthToAdd
	 *            geometry to add indices length
	 */
	public void addToLength(int elementsLengthToAdd, int indicesLengthToAdd) {
		elementsLength += elementsLengthToAdd;
		if (elementsLength > elementsSize) {
			reallocateElements(multiplyByPowerOfTwoToMakeItGreaterThan(elementsSize, elementsLength));
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

	/**
	 * set elements to buffers
	 */
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
	public void setElements(float[] translate, float scale,
			boolean reuseSegment) {
		int offset = manager.currentBufferSegment.elementsOffset;
		int length = manager.currentBufferSegment.elementsLength;
		vertexBuffer.set(manager.vertexArray, translate, scale, offset * 3,
				length * 3);
		if (!reuseSegment) {
			normalBuffer.set(manager.normalArray, offset * 3, length * 3);
			textureBuffer.set(0, offset * 2, length * 2, 1);
		}
		setColor(manager.color, offset, length);
	}

	/**
	 * set color to buffer
	 * 
	 * @param color
	 *            color
	 * @param offset
	 *            offset where to write
	 * @param length
	 *            length to write
	 */
	public void setColor(GColor color, int offset, int length) {
		int colorOffset = offset * 4;
		colorBuffer.set((float) color.getRed() / 255, colorOffset, length, 4);
		colorOffset++;
		colorBuffer.set((float) color.getGreen() / 255, colorOffset, length, 4);
		colorOffset++;
		colorBuffer.set((float) color.getBlue() / 255, colorOffset, length, 4);
		colorOffset++;
		setAlpha(color.getAlpha(), colorOffset, length);
	}

	/**
	 * set alpha to current buffer segment
	 * 
	 * @param alpha
	 */
	public void setAlpha(int alpha) {
		setAlpha(alpha, manager.currentBufferSegment.elementsOffset * 4 + 3,
				manager.currentBufferSegment.elementsLength);
	}

	private void setAlpha(int alpha, int offset, int length) {
		colorBuffer.set(alpha <= 0 ? GLBufferManager.ALPHA_INVISIBLE : ((float) alpha / 255), offset, length, 4);
	}

	/**
	 * draw this pack
	 * 
	 * @param r
	 *            renderer
	 */
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

	/**
	 * reset buffers and lengths
	 */
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
package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;

/**
 * manager for packing buffers (for surfaces)
 */
public class GLBufferManagerSurfaces extends GLBufferManager {

	// complex materials need not more than 100
	static final private int ELEMENTS_SIZE_START = 128;
	// use 1.5 empirical factor observed from materials
	static final private int INDICES_SIZE_START = (ELEMENTS_SIZE_START * 3) / 2;

	private ManagerShaders manager;

	/**
	 * constructor
	 * 
	 * @param manager
	 *            geometries manager
	 */
	public GLBufferManagerSurfaces(ManagerShaders manager) {
		this.manager = manager;
	}

	@Override
	protected int calculateIndicesLength(int size, TypeElement type) {
		switch (type) {
		case FAN_DIRECT:
		case FAN_INDIRECT:
			return 3 * (size - 2);
		case SURFACE:
			return size;
		case TRIANGLE_FAN:
			return 3 * size;
		case TRIANGLE_STRIP:
			return 3 * size;
		case TRIANGLES:
			return 3 * size;
		default:
			// should not happen
			return 0;
		}
	}

	@Override
	protected void putIndices(int size, TypeElement type,
			boolean reuseSegment) {
		switch (type) {
		case FAN_DIRECT:
			short k = 1;
			short zero = 0;
			while (k < size - 1) {
				putToIndices(zero);
				putToIndices(k);
				k++;
				putToIndices(k);
			}
			break;
		case FAN_INDIRECT:
			short k2 = 2;
			k = 1;
			zero = 0;
			while (k < size - 1) {
				putToIndices(zero);
				putToIndices(k2);
				putToIndices(k);
				k++;
				k2++;
			}
			break;
		case SURFACE:
			ReusableArrayList<Short> indices = manager.getIndices();
			for (int i = 0; i < indices.getLength(); i++) {
				putToIndices(indices.get(i));
			}
			break;
		case TRIANGLE_FAN:
			// TODO: simplify Manager.triangleFanVertex() when possible to
			// minimize vertex count
			for (int i = 0; i < size; i++) {
				putToIndices(0);
				putToIndices(2 * i + 1);
				putToIndices(2 * i + 3);
			}
			break;
		case TRIANGLE_STRIP:
			for (int i = 0; i < size; i++) {
				putToIndices(i);
				putToIndices(i + 1 + (i % 2));
				putToIndices(i + 2 - (i % 2));
			}
			break;
		case TRIANGLES:
			for (int i = 0; i < 3 * size; i++) {
				putToIndices(i);
			}
			break;
		default:
			// should not happen
			break;
		}
	}

	/**
	 * draw
	 * 
	 * @param r
	 *            renderer
	 */
	public void draw(Renderer r) {
		drawBufferPacks(r);
	}

	@Override
	protected boolean checkCurrentBufferSegmentDoesNotFit(int indicesLength,
			TypeElement type) {
		return type == TypeElement.SURFACE
				|| currentBufferSegment.type == TypeElement.SURFACE
				|| type != currentBufferSegment.type
				|| super.checkCurrentBufferSegmentDoesNotFit(indicesLength,
						type);
	}

	@Override
	protected int getElementSizeStart() {
		return ELEMENTS_SIZE_START;
	}

	@Override
	protected int getIndicesSizeStart() {
		return INDICES_SIZE_START;
	}

}

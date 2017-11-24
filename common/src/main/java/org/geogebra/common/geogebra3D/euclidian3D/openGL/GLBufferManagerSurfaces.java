package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;

/**
 * manager for packing buffers (for surfaces)
 */
public class GLBufferManagerSurfaces extends GLBufferManager {

	@Override
	protected int calculateIndicesLength(int size) {
		return 3 * (size - 2);
	}

	@Override
	protected void putIndices(int size, TypeElement type) {
		if (type == TypeElement.FAN_DIRECT) {
			short k = 1;
			short zero = 0;
			while (k < size - 1) {
				putToIndices(zero);
				putToIndices(k);
				k++;
				putToIndices(k);
			}
		} else { // type == TypeElement.FAN_INDIRECT
			short k2 = 2;
			short k = 1;
			short zero = 0;
			while (k < size - 1) {
				putToIndices(zero);
				putToIndices(k2);
				putToIndices(k);
				k++;
				k2++;
			}
		}
	}

	/**
	 * draw
	 * 
	 * @param r
	 *            renderer
	 */
	public void draw(RendererShadersInterface r) {
		drawBufferPacks(r);
	}

}

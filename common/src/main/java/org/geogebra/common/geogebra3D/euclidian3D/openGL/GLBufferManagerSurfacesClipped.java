package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;

/**
 * manager for packing buffers (for surfaces)
 */
public class GLBufferManagerSurfacesClipped
		extends GLBufferManagerMergeSegments {

	// empirical values
	static final private int ELEMENTS_SIZE_START = 4096;
	static final private int INDICES_SIZE_START = ELEMENTS_SIZE_START * 3;

	private ManagerShadersElementsGlobalBufferPacking manager;

	/**
	 * constructor
	 * 
	 * @param manager
	 *            geometries manager
	 */
	public GLBufferManagerSurfacesClipped(ManagerShadersElementsGlobalBufferPacking manager) {
		this.manager = manager;
	}

	@Override
	protected int calculateIndicesLength(int size, TypeElement type) {
		// TypeElement == SURFACE
		return size;
	}

	@Override
	protected void putIndices(int size, TypeElement type,
			boolean reuseSegment) {
		// TypeElement == SURFACE
		ReusableArrayList<Short> indices = manager.getIndices();
		for (int i = 0; i < indices.getLength(); i++) {
			putToIndices(indices.get(i));
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

	@Override
	protected boolean checkCurrentBufferSegmentDoesNotFit(int indicesLength,
			TypeElement type) {
		return true;
	}

	@Override
	protected int getElementSizeStart() {
		return ELEMENTS_SIZE_START;
	}

	@Override
	protected int getIndicesSizeStart() {
		return INDICES_SIZE_START;
	}

	// TODO pull to GLBufferManagerMergeSegments when MOB_PACK_SURFACES_GRAPHS
	// released
	@Override
	protected void drawBufferPacks(RendererShadersInterface r) {
		int size = bufferPackList.size();
		if (size == 1) {
			BufferPackAbstract bufferPack = bufferPackList.get(0);
			if (bufferPack.elementsLength > 0) {
				bufferPack.draw(r);
			}
		} else {
			for (int i = size - 1; i >= 0; i--) {
				BufferPackAbstract bufferPack = bufferPackList.get(i);
				if (bufferPack.elementsLength > 0) {
					if (bufferPack.getSegmentEnds().size() == 1) {
						BufferSegment segment = bufferPack.getSegmentEnds()
								.firstEntry().getValue();
						if (segment.elementsOffset == 0 && segment
								.getElementsAvailableLength() == bufferPack.elementsLength) {
							bufferPackList.remove(i);
							removeFromAvailableSegments(segment);
						} else {
							bufferPack.draw(r);
						}
					} else {
						bufferPack.draw(r);
					}
				}
			}
		}
	}

}

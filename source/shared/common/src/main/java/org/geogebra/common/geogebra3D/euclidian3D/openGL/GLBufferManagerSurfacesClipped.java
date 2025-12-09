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
 * manager for packing buffers (for surfaces)
 */
public class GLBufferManagerSurfacesClipped
		extends GLBufferManagerMergeSegments {

	// empirical values
	static final private int ELEMENTS_SIZE_START = 4096;
	static final private int INDICES_SIZE_START = ELEMENTS_SIZE_START * 3;

	private ManagerShaders manager;

	/**
	 * constructor
	 * 
	 * @param manager
	 *            geometries manager
	 */
	public GLBufferManagerSurfacesClipped(ManagerShaders manager) {
		this.manager = manager;
	}

	@Override
	protected int calculateIndicesLength(int size, TypeElement type) {
		switch (type) {
		case SURFACE:
			return size;
		case TRIANGLES:
			return 3 * size;
		default:
			return size;
		}
	}

	@Override
	protected void putIndices(int size, TypeElement type,
			boolean reuseSegment) {
		switch (type) {
		case SURFACE:
			ReusableArrayList<Short> indices = manager.getIndices();
			for (int i = 0; i < indices.getLength(); i++) {
				putToIndices(indices.get(i));
			}
			break;
		case TRIANGLES:
			for (int i = 0; i < 3 * size; i++) {
				putToIndices(i);
			}
			break;
		default:
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

}

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

import java.util.List;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;

/**
 * manager for packing buffers (for curves)
 */
public class GLBufferManagerPoints extends GLBufferManager {

	// regular point is 98 vertices (others are 26 or 402), we start with up to
	// 20 points = 1960 vertices
	static final private int ELEMENTS_SIZE_START = 1960;
	// regular point is 576 indices (others are 144 or 2400), we start with up
	// to 20 points = 11520 indices
	static final private int INDICES_SIZE_START = 11520;

	private ManagerShaders manager;

	/**
	 * constructor
	 * 
	 * @param manager
	 *            geometries manager
	 */
	public GLBufferManagerPoints(ManagerShaders manager) {
		this.manager = manager;
	}

	@Override
	protected int calculateIndicesLength(int size, TypeElement type) {
		return size;
	}

	@Override
	protected void putIndices(int size, TypeElement type,
			boolean reuseSegment) {
		if (!reuseSegment) {
			List<Short> indicesArray = manager.getBufferTemplates()
					.getCurrentIndicesArray();
			for (short i : indicesArray) {
				putToIndices(i);
			}
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
	protected int getElementSizeStart() {
		return ELEMENTS_SIZE_START;
	}

	@Override
	protected int getIndicesSizeStart() {
		return INDICES_SIZE_START;
	}

	@Override
	protected void setElements(boolean reuseSegment, TypeElement type) {
		currentBufferPack.setElements(manager.getTranslate(),
				manager.getScale(), reuseSegment);
	}

	/**
	 * 
	 * @param index
	 *            old geometry index
	 * @return point geometry index
	 */
	public int drawPoint(int index) {
		int ret = manager.startNewList(index, true);
		manager.getBufferTemplates().drawSphere(manager);
		manager.endList();
		return ret;
	}

}

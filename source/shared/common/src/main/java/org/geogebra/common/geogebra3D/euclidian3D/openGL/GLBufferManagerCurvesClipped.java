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
 * manager for packing buffers (for curves)
 */
public class GLBufferManagerCurvesClipped extends GLBufferManagerMergeSegments {

	static final private int ELEMENTS_SIZE_START = 2048;
	static final private int INDICES_SIZE_START = ELEMENTS_SIZE_START * 6;

	@Override
	protected int calculateIndicesLength(int size, TypeElement type) {
		return 3 * 2 * size * PlotterBrush.LATITUDES;
	}

	@Override
	protected void putIndices(int size, TypeElement type,
			boolean reuseSegment) {
		if (currentBufferSegment.bufferPack instanceof BufferPackBigCurve) {
			BufferPackBigCurve bufferPack = (BufferPackBigCurve) currentBufferSegment.bufferPack;
			putToIndicesForCurve(BufferPackBigCurve.CURVE_SIZE_MAX);
			bufferPack.cloneIndices();
		} else {
			putToIndicesForCurve(size);
		}
	}

	/**
	 * draw
	 * 
	 * @param r
	 *            renderer
	 * @param hidden
	 *            if hidden
	 */
	public void draw(Renderer r, boolean hidden) {
		((TexturesShaders) r.getTextures()).setPackedDash();
		r.getRendererImpl().setDashTexture(
				hidden ? Textures.DASH_PACKED_HIDDEN : Textures.DASH_PACKED);
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
	protected void useAnotherBufferPack() {
		if (elementsLength > BufferPackAbstract.ELEMENT_SIZE_MAX) {
			currentBufferPack = new BufferPackBigCurve(this);
			bufferPackList.add(currentBufferPack);
		} else {
			super.useAnotherBufferPack();
		}
	}

}

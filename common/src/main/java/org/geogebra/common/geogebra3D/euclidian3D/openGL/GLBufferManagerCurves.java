package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;

/**
 * manager for packing buffers (for curves)
 */
public class GLBufferManagerCurves extends GLBufferManager {

	// complex materials need more than 1500
	static final private int ELEMENTS_SIZE_START = 2048;
	// for now on, only segments are drawn here, so we count 1 triangle per vertex,
	// and 3 indices per triangle
	static final private int INDICES_SIZE_START = ELEMENTS_SIZE_START * 3;

	@Override
	protected int calculateIndicesLength(int size, TypeElement type) {
		switch (type) {
		case CURVE:
			return 3 * 2 * size * PlotterBrush.LATITUDES;
		case TRIANGLES:
			return 3 * size;
		default: // should not happen
			return 0;
		}
	}

	@Override
	protected void putIndices(int size, TypeElement type,
			boolean reuseSegment) {
		switch (type) {
		case CURVE:
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
			break;
		case TRIANGLES:
			for (int i = 0; i < 3 * size; i++) {
				putToIndices(i);
			}
			break;
		default: // should not happen
			break;
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
	public void draw(RendererShadersInterface r, boolean hidden) {
		((TexturesShaders) r.getTextures()).setPackedDash();
		r.setDashTexture(hidden ? Textures.DASH_PACKED_HIDDEN : Textures.DASH_PACKED);
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

}

package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;

/**
 * manager for packing buffers (for curves)
 */
public class GLBufferManagerCurves extends GLBufferManager {

	@Override
	protected int calculateIndicesLength(int size) {
		return 3 * 2 * size * PlotterBrush.LATITUDES;
	}

	@Override
	protected void putIndices(int size, TypeElement type) {
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

}

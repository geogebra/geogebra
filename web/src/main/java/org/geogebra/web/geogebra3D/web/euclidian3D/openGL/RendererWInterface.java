package org.geogebra.web.geogebra3D.web.euclidian3D.openGL;

import org.gwtproject.canvas.client.Canvas;

/**
 * Renderer interface
 */
public interface RendererWInterface {

	/**
	 * @param pixelRatio
	 *            CSS pixel ratio
	 */
	void setPixelRatio(double pixelRatio);

	/**
	 * @param useBuffer
	 *            whether to use buffer
	 */
	void setBuffering(boolean useBuffer);

	Canvas getCanvas();
}

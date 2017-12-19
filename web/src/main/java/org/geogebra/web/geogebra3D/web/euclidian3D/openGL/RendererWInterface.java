package org.geogebra.web.geogebra3D.web.euclidian3D.openGL;

/**
 * Renderer interface
 */
public interface RendererWInterface {

	/**
	 * @param pixelRatio
	 *            CSS pixel ratio
	 */
	public void setPixelRatio(double pixelRatio);

	/**
	 * @param useBuffer
	 *            whether to use buffer
	 */
	public void setBuffering(boolean useBuffer);

}

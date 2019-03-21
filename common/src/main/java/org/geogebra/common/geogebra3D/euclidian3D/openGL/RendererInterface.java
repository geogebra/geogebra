package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawLabel3D;
import org.geogebra.common.kernel.geos.GeoElement;

public interface RendererInterface {
	/**
	 * 
	 * @return canvas (for desktop version at least)
	 */
	abstract public Object getCanvas();

	/**
	 * re-calc the display immediately
	 */

	/**
	 * set line width
	 * 
	 * @param width
	 *            line width
	 */
	abstract public void setLineWidth(double width);

	/**
	 * enable GL textures 2D
	 */
	abstract public void enableTextures2D();

	/**
	 * disable GL textures 2D
	 */
	abstract public void disableTextures2D();

	abstract public GBufferedImage createBufferedImage(DrawLabel3D label);

	/**
	 * create alpha texture for label from image
	 * 
	 * @param label
	 *            label
	 * @param bimg
	 *            buffered image
	 */
	abstract public void createAlphaTexture(DrawLabel3D label, GBufferedImage bimg);

	/**
	 * 
	 * @param sizeX
	 *            width
	 * @param sizeY
	 *            height
	 * @param buf
	 *            image data
	 * @return a texture for alpha channel
	 */
	abstract public int createAlphaTexture(int sizeX, int sizeY, byte[] buf);

	/**
	 * @param sizeX
	 *            width
	 * @param sizeY
	 *            height
	 * @param buf
	 *            image data
	 */
	abstract public void textureImage2D(int sizeX, int sizeY, byte[] buf);

	/**
	 * set texture linear parameters
	 */
	abstract public void setTextureLinear();

	/**
	 * set texture nearest parameters
	 */
	abstract public void setTextureNearest();

	/**
	 * set hits for mouse location
	 * 
	 * @param mouseLoc
	 *            mouse location
	 * @param threshold
	 *            threshold
	 */
	abstract public void setHits(GPoint mouseLoc, int threshold);

	/**
	 * set label hits for mouse location
	 * 
	 * @param mouseLoc
	 *            mouse location
	 * @return first label hitted geo
	 */
	abstract public GeoElement getLabelHit(GPoint mouseLoc);

	/**
	 * process picking for intersection curves SHOULD NOT BE CALLED OUTSIDE THE
	 * DISPLAY LOOP
	 */
	abstract public void pickIntersectionCurves();

	/**
	 * ensure that animation is on (needed when undocking/docking 3D view)
	 */
	abstract public void resumeAnimator();

	/**
	 * Restart AR session.
	 */
	public void setARShouldRestart();

}

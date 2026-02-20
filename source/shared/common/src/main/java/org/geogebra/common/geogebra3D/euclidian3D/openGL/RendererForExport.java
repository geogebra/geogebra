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

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawableTexture3D;

/**
 * Renderer in the background (no visible 3D view)
 *
 */
public class RendererForExport extends Renderer {

	private boolean reduceForClipping;

	/**
	 * constructor
	 * 
	 * @param view
	 *            3D view
	 */
	public RendererForExport(EuclidianView3D view) {
		super(view, RendererType.SHADER);
		reduceForClipping = true;
		setView(0, 0, 1600, 900); // set default values for "window" size
	}

	/**
	 * set the geometry manager
	 */
	public void setGeometryManager() {
		geometryManager = createManager();
	}

	@Override
	public Manager createManager() {
		return new ManagerShaders(this, view3D);
	}

	@Override
	public void drawScene() {
		updateViewAndDrawables();
	}

	@Override
	public boolean useShaders() {
		return true;
	}

	/**
	 * set x/y min/max
	 * 
	 * @param xmin
	 *            min for x
	 * @param xmax
	 *            max for x
	 * @param ymin
	 *            min for y
	 * @param ymax
	 *            max for y
	 */
	public void setXYMinMax(double xmin, double xmax, double ymin, double ymax) {
		left = (int) xmin;
		bottom = (int) ymin;
		right = (int) xmax;
		top = (int) ymax;
	}

	@Override
	public Object getCanvas() {
		return null;
	}

	@Override
	public void setLineWidth(double width) {
		// no need
	}

	@Override
	public void resumeAnimator() {
		// no need
	}

	@Override
	public void enableTextures2D() {
		// no need
	}

	@Override
	public void disableTextures2D() {
		// no need
	}

	@Override
	public GBufferedImage createBufferedImage(DrawableTexture3D label) {
		return null;
	}

	@Override
	public void createAlphaTexture(DrawableTexture3D label, GBufferedImage img) {
		// no need
	}

	@Override
	public int createAlphaTexture(int sizeX, int sizeY, byte[] buf) {
		return 0;
	}

	@Override
	public void textureImage2D(int sizeX, int sizeY, byte[] buf) {
		// no need
	}

	@Override
	public void setTextureLinear() {
		// no need
	}

	@Override
	public void setTextureNearest() {
		// no need
	}

	@Override
	protected void setDepthFunc() {
		// no need
	}

	@Override
	protected void enablePolygonOffsetFill() {
		// no need
	}

	@Override
	protected void setBlendFunc() {
		// no need
	}

	/**
	 * set up the view
	 */
	@Override
	public void setView() {
		// no need
	}

	@Override
	public boolean reduceForClipping() {
		return reduceForClipping;
	}

	/**
	 * set if we want to reduce bounds for clipping
	 * 
	 * @param flag
	 *            flag
	 */
	public void setReduceForClipping(boolean flag) {
		reduceForClipping = flag;
	}

	@Override
	public void setARShouldRestart() {
		// used in AR implementations
	}

	@Override
	protected void doStartAR() {
		// used in AR implementations
	}

}

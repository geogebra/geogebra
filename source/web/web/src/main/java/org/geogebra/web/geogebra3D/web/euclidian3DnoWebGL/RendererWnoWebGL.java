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

package org.geogebra.web.geogebra3D.web.euclidian3DnoWebGL;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawableTexture3D;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.RendererWithImplW;
import org.gwtproject.canvas.client.Canvas;

/**
 * (dummy) renderer for 3D view, for browsers that don't support webGL
 * 
 * @author mathieu
 *
 */
public class RendererWnoWebGL extends RendererWithImplW {

	/**
	 * constructor
	 * 
	 * @param view
	 *            3D view
	 */
	public RendererWnoWebGL(EuclidianView3DW view) {
		super();
		this.view3D = view;
		webGLCanvas = Canvas.createIfSupported();
		Log.debug("WebGL is not enabled");
	}

	@Override
	public void init() {
		// no webGL context here...
	}

	@Override
	public void drawScene() {
		// no webGL context here...
	}

	@Override
	public void createAlphaTexture(DrawableTexture3D label, GBufferedImage img) {
		// no webGL context here...
	}

	@Override
	public void textureImage2D(int sizeX, int sizeY, byte[] buf) {
		// no webGL context here...
	}

	@Override
	public void setTextureLinear() {
		// no webGL context here...
	}

	@Override
	public void setTextureNearest() {
		// no webGL context here...
	}

	@Override
	protected void setDepthFunc() {
		// no webGL context here...
	}

	@Override
	protected void enablePolygonOffsetFill() {
		// no webGL context here...
	}

	@Override
	protected void setBlendFunc() {
		// no webGL context here...
	}

	@Override
	public void createDummyTexture() {
		// no webGL context here...
	}

}

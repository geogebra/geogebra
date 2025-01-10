package org.geogebra.web.geogebra3D.web.euclidian3DnoWebGL;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawLabel3D;
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
	public void createAlphaTexture(DrawLabel3D label, GBufferedImage bimg) {
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

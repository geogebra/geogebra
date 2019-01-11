package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.util.debug.Log;

public abstract class RendererImpl implements RendererShadersInterface, RendererImplInterface {

	protected EuclidianView3D view3D;
	private int fboWidth = 1;

	private int fboHeight = 1;

	private Object fboID;

	private int fboColorTextureID;

	private Object fboDepthTextureID;

	private int oldRight;

	private int oldLeft;

	private int oldTop;

	private int oldBottom;

	protected Renderer renderer;

	/**
	 * @param renderer
	 *            renderer
	 * @param view
	 *            view
	 */
	public RendererImpl(Renderer renderer, EuclidianView3D view) {
		this.renderer = renderer;
		this.view3D = view;
	}

	/**
	 * @param scale
	 *            image scale
	 * @param w
	 *            pixel width
	 * @param h
	 *            pixel height
	 */
	final public void needExportImage(double scale, int w, int h) {

		view3D.setFontScale(scale);
		setExportImageDimension(w, h);

		renderer.setNeedExportImage(true);
		renderer.display();

	}

	/**
	 * @param w
	 *            pixel width
	 * @param h
	 *            pixel height
	 */
	final public void setExportImageDimension(int w, int h) {
		fboWidth = w;
		fboHeight = h;
	}

	/**
	 * Finish image export.
	 */
	public void endNeedExportImage() {
		renderer.setNeedExportImage(false);

		// set no font scale
		view3D.setFontScale(1);
	}

	/**
	 * Select frame buffer object.
	 */
	public final void selectFBO() {

		if (fboID == null) {
			view3D.setFontScale(1);
			return;
		}

		updateFBOBuffers();

		// bind the buffer
		bindFramebuffer(fboID);

		// store view values
		oldRight = renderer.getRight();
		oldLeft = renderer.getLeft();
		oldTop = renderer.getTop();
		oldBottom = renderer.getBottom();

		// set view values for buffer
		renderer.setView(0, 0, fboWidth, fboHeight);
	}

	/**
	 * Unselect frame buffer object.
	 */
	public final void unselectFBO() {

		if (fboID == null) {
			return;
		}

		// set back the view
		renderer.setView(0, 0, oldRight - oldLeft, oldTop - oldBottom);

		// unbind the framebuffer ...
		unbindFramebuffer();
	}

	/**
	 * Update frame buffer object.
	 */
	public void updateFBOBuffers() {

		// image texture
		bindTexture(fboColorTextureID);
		textureParametersNearest();
		textureImage2DForBuffer(fboWidth, fboHeight);

		bindTexture(0);

		// depth buffer
		bindRenderbuffer(fboDepthTextureID);
		renderbufferStorage(fboWidth, fboHeight);

		unbindRenderbuffer();
	}

	/**
	 * init frame buffer object for save image
	 */
	public final void initFBO() {

		try {

			// allocate the colour texture ...
			int[] result = new int[1];
			genTextures2D(1, result);
			fboColorTextureID = result[0];

			// allocate the depth texture ...
			fboDepthTextureID = genRenderbuffer();

			updateFBOBuffers();

			// allocate the framebuffer object ...
			fboID = genFramebuffer();
			bindFramebuffer(fboID);

			// attach the textures to the framebuffer
			framebuffer(fboColorTextureID, fboDepthTextureID);

			unbindFramebuffer();

			// check if frame buffer is complete
			if (!checkFramebufferStatus()) {
				Log.error("Frame buffer is not complete");
				fboID = null;
			}
		} catch (Exception e) {
			Log.error(e.getMessage());
			fboID = null;
		}
	}

	protected abstract void bindFramebuffer(Object id);

	protected abstract void bindRenderbuffer(Object id);

	protected abstract void unbindFramebuffer();

	protected abstract void unbindRenderbuffer();

	protected abstract void textureParametersNearest();

	protected abstract void textureImage2DForBuffer(int width, int height);

	protected abstract void renderbufferStorage(int width, int height);

	protected abstract Object genRenderbuffer();

	protected abstract Object genFramebuffer();

	protected abstract void framebuffer(Object colorId, Object depthId);

	protected abstract boolean checkFramebufferStatus();

	@Override
	public Textures getTextures() {
		return renderer.getTextures();
	}

	abstract protected void updateClipPlanes();

	/**
	 * set color mask
	 * 
	 * @param colorMask
	 *            color mask
	 */
	public void setColorMask(final int colorMask) {
		setColorMask(ColorMask.getRed(colorMask), ColorMask.getGreen(colorMask),
				ColorMask.getBlue(colorMask), ColorMask.getAlpha(colorMask));
	}

	/**
	 * Set color mask channels
	 * 
	 * @param r
	 *            red
	 * @param g
	 *            green
	 * @param b
	 *            blue
	 * @param a
	 *            alpha
	 */
	abstract public void setColorMask(boolean r, boolean g, boolean b,
			boolean a);

}
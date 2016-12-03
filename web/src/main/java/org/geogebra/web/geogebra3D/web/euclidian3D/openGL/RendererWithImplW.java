package org.geogebra.web.geogebra3D.web.euclidian3D.openGL;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.euclidian.MyZoomer;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawLabel3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.RendererWithImpl;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;
import org.geogebra.web.html5.gawt.GBufferedImageW;
import org.geogebra.web.html5.util.ImageLoadCallback;
import org.geogebra.web.html5.util.ImageWrapper;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.googlecode.gwtgl.binding.WebGLRenderingContext;

/**
 * 
 * Renderer for web, using impl.
 * 
 * @author mathieu
 *
 */
public class RendererWithImplW extends RendererWithImpl implements
		RendererWInterface {

	protected Canvas webGLCanvas;

	protected WebGLRenderingContext glContext;

	/**
	 * @param view
	 * @param type
	 */
	public RendererWithImplW(EuclidianView3D view) {
		super(view, RendererType.SHADER);

		webGLCanvas = Canvas.createIfSupported();

		rendererImpl = new RendererImplShadersW(this, view3D);

		createGLContext(false);

		// when window is unload, dispose openGL stuff
		Window.addCloseHandler(new CloseHandler<Window>() {
			public void onClose(CloseEvent<Window> event) {
				rendererImpl.dispose();
			}
		});

	}

	/**
	 * dummy renderer (when no GL available)
	 */
	public RendererWithImplW() {
		super();
	}


	@Override
	public Canvas getCanvas() {
		return webGLCanvas;
	}


	@Override
	public void setLineWidth(double width) {
		// TODO Auto-generated method stub

	}


	@Override
	public void resumeAnimator() {
		// no need in web

	}

	@Override
	protected void disableStencilLines() {
		// TODO Auto-generated method stub

	}

	@Override
	public GBufferedImage createBufferedImage(DrawLabel3D label) {
		// update width and height
		label.setDimensionPowerOfTwo(
				firstPowerOfTwoGreaterThan(label.getWidth()),
				firstPowerOfTwoGreaterThan(label.getHeight()));

		// create and return a buffered image with power-of-two dimensions
		return new GBufferedImageW(label.getWidthPowerOfTwo(),
				label.getHeightPowerOfTwo(), 1);
	}

	@Override
	public void createAlphaTexture(DrawLabel3D label, GBufferedImage bimg) {
		// values for picking (ignore transparent bytes)
		label.setPickingDimension(0, 0, label.getWidth(), label.getHeight());

		// check if image is ready
		ImageElement image = ((GBufferedImageW) bimg).getImageElement();
		if (!image.getPropertyBoolean("complete")) {
			ImageWrapper.nativeon(image, "load", new AlphaTextureCreator(label,
					image, (GBufferedImageW) bimg, this));
		} else {
			createAlphaTexture(label, image, (GBufferedImageW) bimg);
		}

	}


	@Override
	public void textureImage2D(int sizeX, int sizeY, byte[] buf) {
		// no need (dash and fading are made by the shader)
	}

	@Override
	public void setTextureLinear() {
		glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D,
				WebGLRenderingContext.TEXTURE_MAG_FILTER,
				WebGLRenderingContext.LINEAR);
		glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D,
				WebGLRenderingContext.TEXTURE_MIN_FILTER,
				WebGLRenderingContext.LINEAR);
		glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D,
				WebGLRenderingContext.TEXTURE_WRAP_S,
				WebGLRenderingContext.CLAMP_TO_EDGE); // prevent repeating the
														// texture
		glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D,
				WebGLRenderingContext.TEXTURE_WRAP_T,
				WebGLRenderingContext.CLAMP_TO_EDGE); // prevent repeating the
														// texture

	}

	@Override
	public void setTextureNearest() {
		glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D,
				WebGLRenderingContext.TEXTURE_MAG_FILTER,
				WebGLRenderingContext.NEAREST);
		glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D,
				WebGLRenderingContext.TEXTURE_MIN_FILTER,
				WebGLRenderingContext.NEAREST);

	}

	@Override
	protected void setDepthFunc() {
		glContext.depthFunc(WebGLRenderingContext.LEQUAL);
	}

	@Override
	protected void enablePolygonOffsetFill() {
		glContext.enable(WebGLRenderingContext.POLYGON_OFFSET_FILL);
	}

	@Override
	protected void setBlendFunc() {
		glContext.blendFunc(WebGLRenderingContext.SRC_ALPHA,
				WebGLRenderingContext.ONE_MINUS_SRC_ALPHA);
	}


	@Override
	public void enableTextures2D() {
		// glContext.enable(WebGLRenderingContext.TEXTURE_2D);

	}

	@Override
	public void disableTextures2D() {
		// glContext.disable(WebGLRenderingContext.TEXTURE_2D);

	}

	// ///////////////////////
	// WEB specific methods

	/**
	 * create the webGL context
	 */
	protected void createGLContext(boolean preserveDrawingBuffer) {
		if (preserveDrawingBuffer) {
			glContext = getBufferedContext(webGLCanvas.getElement());

		} else {
			glContext = (WebGLRenderingContext) webGLCanvas
					.getContext("experimental-webgl");
			((RendererImplShadersW) rendererImpl).setGL(glContext);
		}
		if (glContext == null) {
			Window.alert("Sorry, Your Browser doesn't support WebGL!");
		}



	}

	private static native WebGLRenderingContext getBufferedContext(
			Element element) /*-{
		return element.getContext("experimental-webgl", {
			preserveDrawingBuffer : true
		});
	}-*/;

	private double ratio = 1;

	public void setPixelRatio(double ratio) {
		this.ratio = ratio;
	}

	@Override
	public void setView(int x, int y, int w, int h) {
		if (glContext == null || webGLCanvas == null) {
			return;
		}
		webGLCanvas.setCoordinateSpaceWidth((int) (w * ratio));
		webGLCanvas.setCoordinateSpaceHeight((int) (h * ratio));
		webGLCanvas.setWidth(w + "px");
		webGLCanvas.setHeight(h + "px");
		super.setView(x, y, w, h);

		start();

	}

	@Override
	public int getWidthInPixels() {
		return (int) (getWidth() * ratio);
	}

	@Override
	public int getHeightInPixels() {
		return (int) (getHeight() * ratio);
	}

	private Timer loopTimer;

	private void start() {

		((EuclidianView3DW) view3D).setReadyToRender();

		// use loop timer for e.g. automatic rotation
		loopTimer = new Timer() {
			@Override
			public void run() {
				if (view3D.isAnimated()) {
					view3D.repaintView();
				}
			}
		};
		loopTimer.scheduleRepeating(MyZoomer.DELAY);

	}

	@Override
	public void drawScene() {

		super.drawScene();

		// clear alpha channel to 1.0 to avoid transparency to html background
		setColorMask(false, false, false, true);
		clearColorBuffer();
		setColorMask(true, true, true, true);

	}

	/**
	 * create alpha texture from image for the label
	 * 
	 * @param label
	 *            label
	 * @param image
	 *            image
	 * @param bimg
	 *            buffered image
	 */
	protected void createAlphaTexture(DrawLabel3D label, ImageElement image,
			GBufferedImageW bimg) {
		((RendererImplShadersW) rendererImpl).createAlphaTexture(label, image,
				bimg);
	}

	private static class AlphaTextureCreator implements ImageLoadCallback {

		private DrawLabel3D label;
		private ImageElement image;
		private GBufferedImageW bimg;
		private RendererWithImplW renderer;

		public AlphaTextureCreator(DrawLabel3D label, ImageElement image,
				GBufferedImageW bimg, RendererWithImplW renderer) {
			this.label = label;
			this.image = image;
			this.bimg = bimg;
			this.renderer = renderer;
		}

		@Override
		public void onLoad() {

			// image ready : create the texture
			renderer.createAlphaTexture(label, image, bimg);

			// repaint the view
			renderer.getView().repaintView();
		}
	}

	public void setBuffering(boolean b) {
		this.createGLContext(b);
	}

	@Override
	protected final void selectFBO() {
		// TODO implement?
	}

	@Override
	protected final void unselectFBO() {
		// TODO implement?
	}


	@Override
	protected void needExportImage(double scale, int w, int h) {
		// TODO implement?
	}

	@Override
	protected void setExportImageDimension(int w, int h) {
		// TODO implement?
	}

}

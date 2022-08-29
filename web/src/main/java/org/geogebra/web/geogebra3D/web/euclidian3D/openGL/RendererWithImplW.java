package org.geogebra.web.geogebra3D.web.euclidian3D.openGL;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.euclidian.CoordSystemAnimation;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawLabel3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Textures;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.TexturesShaders;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;
import org.geogebra.web.html5.gawt.GBufferedImageW;
import org.gwtproject.timer.client.Timer;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Element;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLImageElement;
import elemental2.webgl.WebGLRenderingContext;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * 
 * Renderer for web, using impl.
 * 
 * @author mathieu
 *
 */
public class RendererWithImplW extends Renderer implements
		RendererWInterface {
	/** canvas */
	protected Canvas webGLCanvas;
	/** context */
	protected WebGLRenderingContext glContext;
	private double ratio = 1;
	private Timer loopTimer;

	/**
	 * @param view
	 *            3D view
	 * @param c
	 *            canvas
	 * @param transparent whether to use transparent background
	 */
	public RendererWithImplW(EuclidianView3D view, Canvas c, boolean transparent) {
		super(view, RendererType.SHADER);
		this.transparent = transparent;
		webGLCanvas = c;

		setRendererImpl(new RendererImplShadersW(this, view3D));

		createGLContext(false);

		// when window is unload, dispose openGL stuff
		DomGlobal.window.addEventListener("unload", event -> dispose());

	}

	/**
	 * Dispose context when tab closed
	 */
	protected void dispose() {
		getRendererImpl().dispose();
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
	public GBufferedImage createBufferedImage(DrawLabel3D label) {
		// update width and height
		label.setDimensionPowerOfTwo(
				firstPowerOfTwoGreaterThan(label.getWidth()),
				firstPowerOfTwoGreaterThan(label.getHeight()));

		// create and return a buffered image with power-of-two dimensions
		return new GBufferedImageW(label.getWidthPowerOfTwo(),
				label.getHeightPowerOfTwo(), 1d);
	}

	@Override
	public void createAlphaTexture(DrawLabel3D label, GBufferedImage bimg) {
		// values for picking (ignore transparent bytes)
		label.setPickingDimension(0, 0, label.getWidth(), label.getHeight());
		GBufferedImageW imgw = (GBufferedImageW) bimg;
		if (imgw.hasCanvas()) {
			createAlphaTexture(label, null, imgw);
		} else {
			// check if image is ready
			HTMLImageElement image = imgw.getImageElement();
			if (!image.complete) {
				image.addEventListener("load", (event) -> {
						// image ready : create the texture
						createAlphaTexture(label, image, (GBufferedImageW) bimg);

						// repaint the view
						getView().repaintView();
				});
			} else {
				createAlphaTexture(label, image, imgw);
			}
		}
	}

	@Override
	public int createAlphaTexture(int sizeX, int sizeY, byte[] buf) {
		return ((RendererImplShadersW) getRendererImpl()).createAlphaTexture(sizeX, sizeY, buf);
	}

	@Override
	protected Textures newTextures() {
		return new TexturesShaders(this);
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
	 * 
	 * @param preserveDrawingBuffer
	 *            whether to use preserveDrawingBuffer flag
	 */
	protected void createGLContext(boolean preserveDrawingBuffer) {
		if (preserveDrawingBuffer) {
			glContext = getBufferedContext(webGLCanvas.getElement());

		} else {
			glContext = getWebGLContext(webGLCanvas.getElement(), transparent);
			((RendererImplShadersW) getRendererImpl()).setGL(glContext);
		}
		if (glContext == null) {
			Log.error("Sorry, Your Browser doesn't support WebGL!");
		}
	}

	protected static WebGLRenderingContext getWebGLContext(Element element, boolean transparent) {
		HTMLCanvasElement canvas = Js.uncheckedCast(element);

		JsPropertyMap<Object> options = JsPropertyMap.of();
		if (transparent) {
			options.set("premultipliedAlpha", false);
		} else {
			options.set("alpha", 0);
		}
		return Js.uncheckedCast(canvas.getContext("experimental-webgl", options));
	}

	private static WebGLRenderingContext getBufferedContext(
			Element element) {
		HTMLCanvasElement canvas = Js.uncheckedCast(element);
		JsPropertyMap<?> options = JsPropertyMap.of("preserveDrawingBuffer", true);
		return Js.uncheckedCast(canvas.getContext("experimental-webgl", options));
	}

	@Override
	public void setPixelRatio(double ratio) {
		if (ratio <= 0) {
			return;
		}
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
		loopTimer.scheduleRepeating(CoordSystemAnimation.DELAY);

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
	protected void createAlphaTexture(DrawLabel3D label, HTMLImageElement image,
			GBufferedImageW bimg) {
		((RendererImplShadersW) getRendererImpl()).createAlphaTexture(label, image,
				bimg);
	}

	@Override
	public void setBuffering(boolean b) {
		this.createGLContext(b);
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

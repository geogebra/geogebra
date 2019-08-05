package org.geogebra.web.html5.euclidian;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.CoordSystemAnimation;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianCursor;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.SymbolicEditor;
import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.common.euclidian.draw.DrawVideo;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.kernel.geos.GeoAxis;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.awt.PrintableW;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gawt.GBufferedImageW;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.ImgResourceHelper;
import org.geogebra.web.html5.javax.swing.GBoxW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.MyImageW;
import org.geogebra.web.html5.main.TimerSystemW;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.ImageLoadCallback;
import org.geogebra.web.html5.util.ImageWrapper;
import org.geogebra.web.html5.util.PDFEncoderW;
import org.geogebra.web.resources.JavaScriptInjector;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.GestureChangeEvent;
import com.google.gwt.event.dom.client.GestureEndEvent;
import com.google.gwt.event.dom.client.GestureStartEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Web implementation of graphics view
 */
public class EuclidianViewW extends EuclidianView implements
		EuclidianViewWInterface, PrintableW {

	/**
	 * For filtering events if they happen too often
	 */
	final public static int DELAY_BETWEEN_MOVE_EVENTS = 15;

	private GGraphics2DWI g2p = null;
	private GGraphics2DWI g2bg = null;
	private GGraphics2D g2dtemp;
	private GGraphics2DW g4copy = null;
	private GColor backgroundColor = GColor.WHITE;
	private int waitForRepaint = TimerSystemW.SLEEPING_FLAG;
	private String svgBackgroundUri = null;
	private MyImageW svgBackground = null;

	private AnimationCallback repaintCallback = new AnimationCallback() {
		@Override
		public void execute(double ts) {
			doRepaint2();
		}
	};

	private AnimationScheduler repaintScheduler = AnimationScheduler.get();

	private long lastRepaint;

	private boolean inFocus = false;

	AppW appW = (AppW) super.app;

	protected ImageElement resetImage;
	protected ImageElement playImage;
	protected ImageElement pauseImage;
	protected ImageElement upArrowImage;
	protected ImageElement downArrowImage;
	protected ImageElement playImageHL;
	protected ImageElement pauseImageHL;

	protected EuclidianPanelWAbstract evPanel;
	private PointerEventHandler pointerHandler;

	// firstInstance is necessary for proper cycling
	private static EuclidianViewWInterface firstInstance = null;

	// lastInstance is necessary for knowing when to cycle
	private static EuclidianViewWInterface lastInstance = null;

	// tells whether recently TAB is pressed in some Graphics View
	// in some applet, which SHOULD move focus to another Graphics View
	// of another (or the same) applet... when this happens, the
	// focus handler of the target applet runs, and sets this static
	// variable false again, so this is just a technical solution
	// for deciding, whether to select the first GeoElement in that
	// applet or not (because we shall not change selection in case
	// e.g. the spreadsheet view gives focus to Graphics view).
	private static boolean tabPressed = false;

	private GDimension preferredSize;

	private GBufferedImage cacheImage;

	private ReaderWidget screenReader;

	// needed to make sure outline doesn't get dashed
	private GBasicStroke outlineStroke = AwtFactory.getPrototype()
			.newBasicStroke(3, GBasicStroke.CAP_BUTT, GBasicStroke.JOIN_BEVEL);

	/**
	 * @param euclidianViewPanel
	 *            panel
	 * @param euclidiancontroller
	 *            controller
	 * @param evNo
	 *            Euclidian view number (1,2)
	 * @param settings
	 *            settings
	 */
	public EuclidianViewW(EuclidianPanelWAbstract euclidianViewPanel,
			EuclidianController euclidiancontroller, int evNo,
			EuclidianSettings settings) {

		super(euclidiancontroller, evNo, settings);
		viewTextField = new ViewTextFieldW(this);
		evPanel = euclidianViewPanel;

		initBaseComponents(euclidianViewPanel, euclidiancontroller, evNo,
				settings);
		initClickStartHandler();
	}

	private void initClickStartHandler() {
		if (g2p.getCanvas() == null) {
			return;
		}
		ClickStartHandler.init(g2p.getCanvas(), new ClickStartHandler() {
			@Override
			public void onClickStart(final int x, final int y,
					PointerEventType type) {
				getEuclidianController().closePopups(x, y, type);
				if (appW.isMenuShowing()) {
					appW.toggleMenu();
				}
			}
		});
		initAriaDefaults();
	}

	/**
	 * @param euclidiancontroller
	 *            controller
	 * @param viewNo
	 *            view number
	 * @param settings
	 *            settings
	 */
	public EuclidianViewW(EuclidianController euclidiancontroller, int viewNo,
	        EuclidianSettings settings) {
		super(euclidiancontroller, viewNo, settings);
		viewTextField = new ViewTextFieldW(this);
		evPanel = newMyEuclidianViewPanel();

		// It seems this constructor is only called from PlotPanelEuclidianViewW
		// currently,
		// so this -1 is changed to viewNo because EVNO_GENERAL is needed for
		// making sure that this view does not change the toolbar, code in
		// EuclidianControllerW
		// if you think it is not Okay, then use (-1) in EuclidianControllerW
		// instead of EVNO_GENERAL
		// at mouse events which call setActiveToolbarId #plotpanelevno
		// initBaseComponents(EVPanel, euclidiancontroller, -1);
		initBaseComponents(evPanel, euclidiancontroller, viewNo, settings);
		initClickStartHandler();
	}

	private void initAriaDefaults() {
		Element elem = g2p.getElement();
		elem.setAttribute("role", "application");
		elem.setAttribute("aria-label", "Graphics View " + evNo);
	}

	@Override
	public final GFont getFont() {
		return new GFontW(g2p.getFont());
	}

	@Override
	public final GColor getBackgroundCommon() {
		return backgroundColor;
	}

	@Override
	public final void setBackground(GColor bgColor) {
		if (bgColor != null) {
			backgroundColor = GColor.newColor(bgColor.getRed(),
			        bgColor.getGreen(), bgColor.getBlue(), bgColor.getAlpha());
		}
	}

	@Override
	public final GGraphics2D getTempGraphics2D(GFont fontForGraphics) {
		if (this.g2dtemp == null) {
			Canvas canvas = Canvas.createIfSupported();
			if (canvas == null) {
				this.g2dtemp = new GGraphics2DE();
			} else {
				this.g2dtemp = new GGraphics2DW(canvas);
			}
		}
		this.g2dtemp.setFont(fontForGraphics);
		return this.g2dtemp;
	}

	@Override
	protected final CoordSystemAnimation newZoomer() {
		return new CoordSystemAnimationW(this);
	}

	@Override
	public final void paintBackground(GGraphics2D g2) {
		GGraphics2DWI g2w = null;
		if (app.isWhiteboardActive()) {
			g2w = g2bg;
			g2w.clearAll();
		} else {
			g2w = (GGraphics2DWI) g2;
		}
		if (isGridOrAxesShown() || hasBackgroundImages() || isTraceDrawn()
				|| appW.showResetIcon()
		        || kernel.needToShowAnimationButton()) {
			g2w.drawImage(bgImage,
					0, 0);
		} else {
			g2w.fillWith(getBackgroundCommon());
		}
	}

	/**
	 * This doRepaint method should be used instead of repaintView in cases when
	 * the repaint should be done immediately
	 */
	public final void doRepaint2() {
		long time = System.currentTimeMillis();
		this.updateBackgroundIfNecessary();

		if (app.isWhiteboardActive()) {
			g2p.clearAll();
		}
		paint(g2p, g2bg);

		// if we have pen tool in action
		// repaint the preview line
		lastRepaint = System.currentTimeMillis() - time;
		GeoGebraProfiler.addRepaint(lastRepaint);
		app.getFpsProfiler().notifyRepaint();
	}

	/**
	 * Gets the coordinate space width of the &lt;canvas&gt;.
	 * 
	 * @return the logical width
	 */
	@Override
	public int getWidth() {
		return (int) (this.g2p.getCoordinateSpaceWidth()
						/ this.g2p.getDevicePixelRatio());
	}

	/**
	 * Gets the coordinate space height of the &lt;canvas&gt;.
	 * 
	 * @return the logical height
	 */
	@Override
	public int getHeight() {
		return (int) (this.g2p.getCoordinateSpaceHeight()
						/ this.g2p.getDevicePixelRatio());
	}

	@Override
	public void clearView() {
		resetLists();
		updateBackgroundImage(); // clear traces and images
		// resetMode();
		if (appW.getGuiManager() != null) {
			appW.getGuiManager().clearAbsolutePanels(); 
		}
		removeTextField();

	}

	@Override
	public final GGraphics2DWI getGraphicsForPen() {
		return g2p;
	}

	@Override
	public final boolean isShowing() {
		return g2p != null && g2p.getCanvas() != null
		        && g2p.getCanvas().isAttached() && g2p.getCanvas().isVisible();
	}

	/**
	 * @param scale
	 *            scale
	 * @param transparency
	 *            transparency
	 * @param greyscale
	 *            true for monochrome
	 * @return canvas containing copy of main canvas for this view
	 */
	public Canvas getExportImageCanvas(double scale, boolean transparency,
			boolean greyscale) {
		int width = (int) Math.floor(getExportWidth() * scale);
		int height = (int) Math.floor(getExportHeight() * scale);

		Canvas c4 = Canvas.createIfSupported();
		if (c4 == null) {
			return null; // mockito
		}
		c4.setCoordinateSpaceWidth(width);
		c4.setCoordinateSpaceHeight(height);
		c4.setWidth(width + "px");
		c4.setHeight(height + "px");

		g4copy = new GGraphics2DW(c4);
		this.appW.setExporting(ExportType.PNG, scale);
		exportPaintPre(g4copy, scale, transparency);
		drawObjects(g4copy);
		this.appW.setExporting(ExportType.NONE, 1);

		Canvas ret = g4copy.getCanvas();

		if (greyscale) {
			convertToGreyScale(ret.getContext2d(),
					ret.getCoordinateSpaceWidth(),
					ret.getCoordinateSpaceHeight());
		}

		return ret;
	}

	private native void convertToGreyScale(Context2d ctx, int width,
			int height) /*-{
		var imageData = ctx.getImageData(0, 0, width, height);

		for (y = 0; y < height; y++) {
			for (x = 0; x < width; x++) {
				var index = (y * 4) + (x * 4) * width;
				var r = imageData.data[index];
				var g = imageData.data[index + 1];
				var b = imageData.data[index + 2];
				var grey = Math.round((r + g + b) / 3);
				imageData.data[index] = grey;
				imageData.data[index + 1] = grey;
				imageData.data[index + 2] = grey;
			}
		}

		ctx.putImageData(imageData, 0, 0);
	}-*/;

	@Override
	public String getExportImageDataUrl(double scale, boolean transparency,
			boolean greyscale) {
		return getExportImageDataUrl(scale, transparency, ExportType.PNG,
				greyscale);
	}

	@Override
	public String getExportImageDataUrl(double scale, boolean transparency,
			ExportType format, boolean greyscale) {
		appW.getVideoManager().setPreviewOnly(true);
		String dataUrl = dataURL(
				getExportImageCanvas(scale, transparency, greyscale),
				format);
		appW.getVideoManager().setPreviewOnly(false);
		return dataUrl;
	}

	private static String dataURL(Canvas c, ExportType format) {
		try {
			return c == null ? ""
				: c.toDataUrl(
						format == ExportType.WEBP ? "image/webp" : "image/png");
		} catch (Throwable t) {
			Log.error(t.getMessage());
			return GuiResourcesSimple.INSTANCE.dialog_warning().getSafeUri()
					.asString();
		}
	}

	@Override
	public String getExportSVG(double scale, boolean transparency) {
		int width = (int) Math.floor(getExportWidth() * scale);
		int height = (int) Math.floor(getExportHeight() * scale);

		if (!canvas2svgLoaded()) {
			JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.canvas2Svg());
		}
		JavaScriptObject ctx = getCanvas2SVG(width, height);

		if (ctx == null) {
			Log.debug("canvas2SVG not found");
			return null;
		}

		g4copy = new GGraphics2DW((Context2d) ctx.cast());
		this.appW.setExporting(ExportType.SVG, scale);
		exportPaintPre(g4copy, scale, transparency);
		drawObjects(g4copy);
		this.appW.setExporting(ExportType.NONE, 1);
		return getSerializedSvg(ctx);
	}

	/**
	 * @param scale
	 *            scale
	 * @return PDF as a base64 String
	 */
	@Override
	public String getExportPDF(double scale) {

		boolean page2 = getViewID() == App.VIEW_EUCLIDIAN
				&& app.hasEuclidianView2(1);

		int width = (int) Math.floor(getExportWidth() * scale);
		int height = (int) Math.floor(getExportHeight() * scale);

		EuclidianView view2 = null;
		if (page2) {
			view2 = app.getEuclidianView2(1);
			width = (int) Math.max(width,
					Math.floor(view2.getExportWidth() * scale));
			height = (int) Math.max(height,
					Math.floor(view2.getExportHeight() * scale));
		}

		Context2d ctx = PDFEncoderW.getContext(width, height);

		if (ctx == null) {
			Log.debug("canvas2PDF not found");
			return "";
		}

		g4copy = new GGraphics2DW(ctx);
		this.appW.setExporting(ExportType.PDF_HTML5, scale);

		exportPaintPre(g4copy, scale, false);
		appW.getVideoManager().setPreviewOnly(true);
		drawObjects(g4copy);

		// include view 2 as 2nd page
		if (page2) {
			PDFEncoderW.addPagePDF(ctx);
			view2.exportPaintPre(g4copy, scale, false);
			view2.drawObjects(g4copy);
		}
		appW.getVideoManager().setPreviewOnly(false);

		this.appW.setExporting(ExportType.NONE, 1);
		return PDFEncoderW.getPDF(ctx);
	}

	private native JavaScriptObject getCanvas2SVG(double width,
			double height) /*-{
		if ($wnd.C2S) {
			return new $wnd.C2S(width, height);
		}

		return null;
	}-*/;

	private native boolean canvas2svgLoaded() /*-{
		return !!$wnd.C2S;
	}-*/;

	private native String getSerializedSvg(JavaScriptObject ctx) /*-{
		return ctx.getSerializedSvg(true);
	}-*/;

	@Override
	public GBufferedImageW getExportImage(double scale) {
		return getExportImage(scale, false);
	}

	/**
	 * @param scale
	 *            scale
	 * @param transparency
	 *            whether to use transparency
	 * @return exported image
	 */
	public GBufferedImageW getExportImage(double scale, boolean transparency) {
		int width = (int) Math.floor(getExportWidth() * scale);
		int height = (int) Math.floor(getExportHeight() * scale);
		GBufferedImageW img = new GBufferedImageW(width, height, 1, true);
		exportPaint(new GGraphics2DW(img.getCanvas()), scale, transparency,
				ExportType.PNG);
		return img;
	}

	/**
	 * @param canvas
	 *            canvas
	 * @param scale
	 *            ratio of desired size and current size of the graphics
	 */
	public void exportPaint(Canvas canvas, double scale) {
		exportPaint(new GGraphics2DW(canvas), scale, false, ExportType.PNG);
	}

	/**
	 * repaintView just calls this method
	 */
	@Override
	public void repaint() {

		// TODO: this is a temporary hack until the timer system can handle
		// TextPreview view
		// (or ignore timer system because text preview only draws one geo)
		if (getViewID() == App.VIEW_TEXT_PREVIEW || getViewID() < 0) {
			doRepaint();
			return;
		}

		getApplication().ensureTimerRunning();
		if (waitForRepaint == TimerSystemW.SLEEPING_FLAG) {
			waitForRepaint = TimerSystemW.EUCLIDIAN_LOOPS;
		}
	}

	/**
	 * schedule a repaint
	 */
	public void doRepaint() {
		repaintScheduler.requestAnimationFrame(repaintCallback);
	}

	/**
	 * timer system suggests a repaint
	 */
	@Override
	public boolean suggestRepaint() {
		if (waitForRepaint == TimerSystemW.SLEEPING_FLAG) {
			return false;
		}

		if (waitForRepaint == TimerSystemW.REPAINT_FLAG) {
			if (isShowing()) {
				doRepaint();
				waitForRepaint = TimerSystemW.SLEEPING_FLAG;
			}
			return true;
		}

		waitForRepaint--;
		return true;
	}

	/**
	 * Set logical size of the canvas.
	 * 
	 * @param width
	 *            width
	 * @param height
	 *            height
	 */
	public void setCoordinateSpaceSize(int width, int height) {
		g2p.setCoordinateSpaceSize(width, height);
		if (app.isWhiteboardActive()) {
			g2bg.setCoordinateSpaceSize(width, height);
		}
		try {
			// just resizing the AbsolutePanelSmart, not the whole of DockPanel
			g2p.getElement().getParentElement().getStyle()
			        .setWidth(width, Style.Unit.PX);
			g2p.getElement().getParentElement().getStyle()
			        .setHeight(height, Style.Unit.PX);
			getEuclidianController().calculateEnvironment();
		} catch (Exception exc) {
			Log.debug("Problem with the parent element of the canvas");
		}
	}

	/**
	 * Set canvas size coordinate from CSS size
	 */
	public void synCanvasSize() {
		setCoordinateSpaceSize(g2p.getOffsetWidth(), g2p.getOffsetHeight());
	}

	@Override
	public String getCanvasBase64WithTypeString() {
		if (g2p.getCanvas() == null) {
			return "";
		}
		return getCanvasBase64WithTypeString(g2p.getCoordinateSpaceWidth(),
		        g2p.getCoordinateSpaceHeight(), bgGraphics == null ? null
		                : ((GGraphics2DW) bgGraphics).getCanvas(),
		        g2p.getCanvas());
	}

	/**
	 * 
	 * @param width
	 *            width
	 * @param height
	 *            height
	 * @param background
	 *            background objects
	 * @param foreground
	 *            foreground objects
	 * @return base64 encoded PNG
	 */
	public static String getCanvasBase64WithTypeString(double width,
	        double height, Canvas background, Canvas foreground) {

		// TODO: make this more perfect, like in Desktop

		double ratio = width / height;
		double thx = MyXMLio.THUMBNAIL_PIXELS_X;
		double thy = MyXMLio.THUMBNAIL_PIXELS_Y;
		if (ratio < 1) {
			thx *= ratio;
		} else if (ratio > 1) {
			thy /= ratio;
		}

		Canvas canv = Canvas.createIfSupported();
		canv.setCoordinateSpaceHeight((int) thy);
		canv.setCoordinateSpaceWidth((int) thx);
		canv.setWidth((int) thx + "px");
		canv.setHeight((int) thy + "px");
		Context2d c2 = canv.getContext2d();

		if (background != null) {
			c2.drawImage(background.getCanvasElement(), 0, 0, (int) thx,
			        (int) thy);
		}
		c2.drawImage(foreground.getCanvasElement(), 0, 0, (int) thx,
		        (int) thy);

		return dataURL(canv, null);
	}

	@Override
	protected void updateSizeKeepDrawables() {
		if ((getWidth() <= 0) || (getHeight() <= 0)) {
			return;
		}

		// real world values
		companion.setXYMinMaxForUpdateSize();
		setRealWorldBounds();

		try {
			createImage();
		} catch (Exception e) {
			resetBackgroundAndCache();
		}

		updateBackgroundImage();
	}

	/**
	 * Initialize background image
	 */
	public void createImage() {
		bgImage = makeImage();
		bgGraphics = bgImage.createGraphics();
	}

	@Override
	public GBufferedImage getCacheGraphics() {
		if (cacheGraphics == null || cacheImage == null) {
			cacheImage = makeImage();
		}
		return cacheImage;
	}

	private GBufferedImage makeImage() {
		return new GBufferedImageW(g2p.getOffsetWidth(), g2p.getOffsetHeight(),
				appW == null || appW.getPixelRatio() == 0 ? 1
						: appW.getPixelRatio(),
				false);
	}

	@Override
	public long getLastRepaintTime() {
		return lastRepaint;
	}

	/**
	 * @return new panel
	 */
	protected MyEuclidianViewPanel newMyEuclidianViewPanel() {
		return new MyEuclidianViewPanel(this);
	}

	private void initBackgroundCanvas(EuclidianPanelWAbstract euclidianViewPanel) {
		final Canvas bg = euclidianViewPanel.getBackgroundCanvas();
		if (bg != null) {
			this.g2bg = new GGraphics2DW(bg);
			g2bg.setDevicePixelRatio(appW.getPixelRatio());
		} else {
			this.g2bg = new GGraphics2DE();
		}
	}

	/**
	 * @deprecated - double canvas should be used in all apps
	 */
	@Deprecated
	public void initBgCanvas() {
		if (this.g2bg == null) {
			this.initBackgroundCanvas(evPanel);
		}
	}

	private void initBaseComponents(EuclidianPanelWAbstract euclidianViewPanel,
			EuclidianController euclidiancontroller, int newEvNo,
			EuclidianSettings settings) {

		final Canvas canvas = euclidianViewPanel.getCanvas();
		this.evNo = newEvNo;
		if (app.isWhiteboardActive()) {
			initBackgroundCanvas(euclidianViewPanel);
		}

		if (canvas != null) {
			this.g2p = new GGraphics2DW(canvas);
			g2p.setDevicePixelRatio(appW.getPixelRatio());
			if (appW.getArticleElement().isDebugGraphics()) {
				g2p.startDebug();
			}
		} else {
			this.g2p = new GGraphics2DE();
		}
		updateFonts();
		initView(true);
		attachView();

		((EuclidianControllerW) euclidiancontroller).setView(this);

		if (this.getViewID() != App.VIEW_TEXT_PREVIEW) {
			registerKeyHandlers(canvas);
			registerMouseTouchGestureHandlers(euclidianViewPanel,
			        (EuclidianControllerW) euclidiancontroller);
		}

		registerDragDropHandlers(euclidianViewPanel,
				(EuclidianControllerW) euclidiancontroller);

		updateFirstAndLast(true, true);
		if (canvas == null) {
			return;
		}
		canvas.addAttachHandler(new AttachEvent.Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent ae) {
				if (ae.isAttached()) {
					// canvas just attached
					// if (canvas.isVisible()) {
						// if the canvas is set to visible,
						// we're also going to call this
					// but it seems the canvas is never
					// made invisible now (otherwise
					// we would need to override it maybe)

					// ... it is a good question whether the
					// respective methods of DockManagerW, i.e.
					// show, hide, maximize and drop call this?
					updateFirstAndLast(true, false);
					// }
				} else {
					// canvas just detached
					// here lazy update shall happen!
					// i.e. focus handler shall update
					// firstInstance and lastInstance
					// BUT also we shall make them null now
					updateFirstAndLast(false, false);
				}
			}
		});

		canvas.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent be) {
				focusLost();
				cycle(EuclidianViewW.this);

			}
		});

		canvas.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent fe) {
				focusGained();
				EuclidianViewW.selectNextGeoOnTab(EuclidianViewW.this);
			}
		});

		// euclidianViewPanel.addDomHandler((EuclidianController)euclidiancontroller,
		// KeyPressEvent.getType());
		// euclidianViewPanel.addKeyDownHandler(this.app.getGlobalKeyDispatcher());
		// euclidianViewPanel.addKeyUpHandler(this.app.getGlobalKeyDispatcher());
		// euclidianViewPanel.addKeyPressHandler(this.app.getGlobalKeyDispatcher());

		EuclidianSettings es = null;
		if (settings != null) {
			es = settings;
			// settings from XML for EV1, EV2
			// not for eg probability calculator
		} else if ((newEvNo == 1) || (newEvNo == 2)) {
			es = getApplication().getSettings().getEuclidian(newEvNo);
		}

		if (es != null) {
			settingsChanged(es);
			es.addListener(this);
		}

		addScreenReader();
	}

	/**
	 * @param ev
	 *            view
	 * @param anyway
	 *            whether to update even unattached view
	 */
	static final public void updateFirstAndLast(EuclidianViewWInterface ev,
			boolean anyway) {
		if (ev.getCanvasElement() == null) {
			return;
		}
		ev.getCanvasElement()
				.setTabIndex(GeoGebraFrameW.GRAPHICS_VIEW_TABINDEX);
		if (firstInstance == null) {
			firstInstance = ev;
		} else if (ev.isAttached()) {
			if (compareDocumentPosition(ev.getCanvasElement(),
					firstInstance.getCanvasElement())) {
				firstInstance = ev;
			}
		} else if (anyway) {
			// then compare to something equivalent!
			// if we are in different applet;
			// ... anything from this applet is right
			// if we are in the same applet;
			// ... does it matter? (yes, but just a little bit)
			// TODO: to be fixed in a better way later,
			// after it is seen whether this is really a little fix...
			if (compareDocumentPosition(
					((AppW) ev.getApplication()).getFrameElement(),
					firstInstance
							.getCanvasElement())) {
				firstInstance = ev;
			}
		}

		if (lastInstance == null) {
			lastInstance = ev;
		} else if (ev.isAttached()) {
			if (compareDocumentPosition(lastInstance.getCanvasElement(),
					ev.getCanvasElement())) {
				lastInstance = ev;
			}
		} else if (anyway) {
			if (compareDocumentPosition(lastInstance.getCanvasElement(),
					((AppW) ev.getApplication()).getFrameElement())) {
				lastInstance = ev;
			}
		}
	}

	@Override
	public void updateFirstAndLast(boolean attach, boolean anyway) {
		if (attach) {
			if ((evNo == 1) || (evNo == 2) || isViewForPlane()) {
				updateFirstAndLast(this, anyway);
			} else {
				// is this the best?
				getCanvasElement()
						.setTabIndex(
						GeoGebraFrameW.GRAPHICS_VIEW_TABINDEX - 1);
			}
		}
	}

	/**
	 * Used for comparing position in DOM (Document Object Model)
	 * 
	 * @param firstElement
	 *            it is right if this comes first
	 * @param secondElement
	 *            it is right if this comes second
	 * @return whether firstElement is really before secondElement
	 */
	public static native boolean compareDocumentPosition(
			Element firstElement,
			Element secondElement) /*-{
		if (firstElement) {
			if (secondElement) {
				if (firstElement === secondElement) {
					// let's interpret it as false result
					return false;
				}
				if (firstElement.compareDocumentPosition(secondElement)
						& $wnd.Node.DOCUMENT_POSITION_FOLLOWING) {
					return true;
				}
				// if any of them contain the other, let us interpret
				// as false result, and anyway, this shall not happen!
				// but probably this is DOCUMENT_POSITION_PRECEDING:
				return false;
			}
		}
		return false;
	}-*/;

	private void registerKeyHandlers(Canvas canvas) {
		if (canvas != null) {
		canvas.addKeyDownHandler(this.appW.getGlobalKeyDispatcher());
		canvas.addKeyUpHandler(this.appW.getGlobalKeyDispatcher());
		canvas.addKeyPressHandler(this.appW.getGlobalKeyDispatcher());
		}

	}

	private void registerMouseTouchGestureHandlers(
	        EuclidianPanelWAbstract euclidianViewPanel,
	        EuclidianControllerW euclidiancontroller) {
		Widget absPanel = euclidianViewPanel.getAbsolutePanel();
		absPanel.addDomHandler(euclidiancontroller, MouseWheelEvent.getType());
		if (!Browser.supportsPointerEvents(true)) {
			absPanel.addDomHandler(euclidiancontroller,
					MouseMoveEvent.getType());
			absPanel.addDomHandler(euclidiancontroller,
					MouseOverEvent.getType());
			absPanel.addDomHandler(euclidiancontroller, MouseOutEvent.getType());
			absPanel.addDomHandler(euclidiancontroller, MouseUpEvent.getType());
			if (appW.getLAF() == null || !appW.getLAF().isSmart()) {
				absPanel.addDomHandler(euclidiancontroller,
						MouseDownEvent.getType());
			}
		}

		if (Browser.supportsPointerEvents(true)) {
			pointerHandler = new PointerEventHandler((IsEuclidianController) euclidianController,
					euclidiancontroller.getOffsets());
			PointerEventHandler.attachTo(absPanel.getElement(), pointerHandler);
			CancelEventTimer.killTouch(absPanel);
			return;
		}

		if (appW.getLAF() != null) {
			if (appW.getLAF().registerHandlers(absPanel, euclidiancontroller)) {
				return;
			}
		}

		absPanel.addDomHandler(euclidiancontroller, TouchStartEvent.getType());
		absPanel.addDomHandler(euclidiancontroller, TouchEndEvent.getType());
		absPanel.addDomHandler(euclidiancontroller, TouchMoveEvent.getType());
		absPanel.addDomHandler(euclidiancontroller, TouchCancelEvent.getType());
		absPanel.addDomHandler(euclidiancontroller, GestureStartEvent.getType());
		absPanel.addDomHandler(euclidiancontroller, GestureChangeEvent.getType());
		absPanel.addDomHandler(euclidiancontroller, GestureEndEvent.getType());

	}

	private static void registerDragDropHandlers(
	        EuclidianPanelWAbstract euclidianViewPanel,
	        EuclidianControllerW euclidiancontroller) {
		Widget evPanel = euclidianViewPanel.getAbsolutePanel();
		evPanel.addDomHandler(euclidiancontroller, DropEvent.getType());
	}

	/* Code for dashed lines removed in r23713 */

	/**
	 * Gets pixel width of the &lt;canvas&gt;.
	 * 
	 * @return the physical width in pixels
	 */
	public int getPhysicalWidth() {
		return g2p.getOffsetWidth();
	}

	/**
	 * Gets pixel height of the &lt;canvas&gt;.
	 * 
	 * @return the physical height in pixels
	 */
	public int getPhysicalHeight() {
		return g2p.getOffsetHeight();
	}

	@Override
	public int getAbsoluteTop() {
		return g2p.getAbsoluteTop();
	}

	@Override
	public int getAbsoluteLeft() {
		return g2p.getAbsoluteLeft();
	}

	@Override
	public EuclidianControllerW getEuclidianController() {
		return (EuclidianControllerW) euclidianController;
	}

	@Override
	protected void initCursor() {
		setHitCursor();
	}

	@Override
	protected void setStyleBarMode(int mode) {
		if (hasStyleBar()) {
			getStyleBar().setMode(mode);
		}
	}

	private ImageElement getResetImage() {
		if (resetImage == null) {
			resetImage = this.appW.getRefreshViewImage();
		}
		return resetImage;
	}

	private ImageElement getPlayImage(boolean highlight) {
		if (playImage == null) {
			playImage = this.appW.getPlayImage();
			playImageHL = this.appW.getPlayImageHover();
		}
		return highlight ? playImageHL : playImage;
	}

	private ImageElement getPauseImage(boolean highlight) {
		if (pauseImage == null) {
			pauseImage = this.appW.getPauseImage();
			pauseImageHL = this.appW.getPauseImageHover();
		}
		return highlight ? pauseImageHL : pauseImage;
	}

	@Override
	public boolean hitAnimationButton(int x, int y) {
		// draw button in focused EV only
		if (!drawPlayButtonInThisView()) {
			return false;
		}

		return kernel.needToShowAnimationButton() && (x <= 27)
				&& (y >= (getHeight() - 27));
	}

	@Override
	public boolean requestFocusInWindow() {
		g2p.getElement().focus();
		focusGained();
		return true;
	}

	/**
	 * Mark as not focused
	 */
	public void focusLost() {
		this.inFocus = false;
	}

	/**
	 * Mark as focused and notify app.
	 */
	public void focusGained() {
		if (!inFocus) {
			this.inFocus = true;
			if (getCanvasElement() != null) {
				this.appW.focusGained(this, getCanvasElement());
			}
		}
	}

	@Override
	public boolean isInFocus() {
		return inFocus;
	}

	/**
	 * Use default cursor when mouse is over this view.
	 */
	public void setDefaultCursor() {
		setCursorClass("cursor_default");
	}

	private void setCursorClass(String className) {
		// IMPORTANT: do nothing if we already have the classname,
		// app.resetCursor is VERY expensive in IE
		if (g2p.getElement() != null
				&& !g2p.getElement().hasClassName(className)) {
			this.appW.resetCursor();
			g2p.getElement().setClassName("");
			g2p.getElement().addClassName(className);
		}
	}

	/**
	 * Set cursor to hit (hand)
	 */
	public void setHitCursor() {
		setCursorClass("cursor_hit");
	}

	@Override
	protected EuclidianStyleBar newEuclidianStyleBar() {
		if (appW.getGuiManager() == null) {
			return null;
		}
		return appW.getGuiManager().newEuclidianStylebar(this, this.getViewID());
	}

	@Override
	protected void addDynamicStylebarToEV(EuclidianStyleBar dynamicStylebar) {
		if (((Widget) dynamicStylebar).getParent() == null) {
			appW.getGuiManager().addStylebar(this, dynamicStylebar);
		}
	}

	@Override
	protected EuclidianStyleBar newDynamicStyleBar() {
		return appW.getGuiManager().newDynamicStylebar(this);
	}

	@Override
	final protected void drawAnimationButtons(final GGraphics2D g2) {

		// draw button in focused EV only
		if (!drawPlayButtonInThisView() || appW.isScreenshotGenerator()
				|| appW.isExporting()) {
			return;
		}

		final int x = 3;
		final int y = getHeight() - 27;

		// draw pause or play button
		final ImageElement img = kernel.isAnimationRunning()
				? getPauseImage(highlightAnimationButtons)
				: getPlayImage(highlightAnimationButtons);
		if (img.getPropertyBoolean("complete")) {
			((GGraphics2DW) g2).drawImage(img, x, y);
		} else {
			ImageWrapper.nativeon(img, "load", new ImageLoadCallback() {
				@Override
				public void onLoad() {
					((GGraphics2DW) g2).drawImage(img, x, y);
				}
			});
		}
	}

	@Override
	public void setPreferredSize(GDimension preferredSize) {
		if (this.preferredSize != null
		        && this.preferredSize.equals(preferredSize)) {
			return;
		}
		this.evPanel.reset();
		this.preferredSize = preferredSize;
		g2p.setPreferredSize(preferredSize);
		updateSize();
		setReIniting(false);
	}

	/**
	 * Updates the size of the canvas and coordinate system
	 * 
	 * @param width
	 *            the new width (in pixel)
	 * @param height
	 *            the new height (in pixel)
	 */
	public void setPreferredSize(int width, int height) {
		setPreferredSize(new GDimensionW(width, height));
	}

	private void setDragCursor() {
		if (this.appW.useTransparentCursorWhenDragging()) {
			setCursorClass("cursor_transparent");
		} else {
			setCursorClass("cursor_drag");
		}
	}

	@Override
	public void setToolTipText(String plainTooltip) {
		// no tooltips
	}

	private void setResizeXAxisCursor() {
		setCursorClass("cursor_resizeXAxis");
	}

	private void setResizeYAxisCursor() {
		setCursorClass("cursor_resizeYAxis");
	}

	private void setResizeNESWCursor() {
		setCursorClass("cursor_resizeNESW");
	}

	private void setResizeNWSECursor() {
		setCursorClass("cursor_resizeNWSE");
	}

	private void setResizeEWCursor() {
		setCursorClass("cursor_resizeEW");
	}

	private void setResizeNSCursor() {
		setCursorClass("cursor_resizeNS");
	}

	private void setMoveCursor() {
		setCursorClass("cursor_move");
	}

	private void setTransparentCursor() {
		setCursorClass("cursor_transparent");
	}

	private void setEraserCursor() {
		setCursorClass("cursor_eraser");
	}

	private void setPenCursor() {
		setCursorClass("cursor_pen");
	}

	private void setHighlighterCursor() {
		setCursorClass("cursor_highlighter");
	}

	private void setRotationCursor() {
		setCursorClass("cursor_rotation");
	}

	@Override
	public boolean hasFocus() {
		// changed to return true, otherwise Arrow keys don't work to pan the
		// view, see GlobalKeyDispatcher
		// return isInFocus;
		return true;
	}

	@Override
	public void add(GBox box) {
		if (evPanel != null) {
			evPanel.getAbsolutePanel().add(GBoxW.getImpl(box),
			        (int) box.getBounds().getX(), (int) box.getBounds().getY());
		}
	}

	@Override
	public void remove(GBox box) {
		if (evPanel != null) {
			evPanel.getAbsolutePanel().remove(GBoxW.getImpl(box));
		}
	}

	@Override
	protected void drawResetIcon(GGraphics2D g) {
		int w = getWidth();

		// omit for export
		if (!appW.isExporting()) {
			((GGraphics2DW) g).drawImage(getResetImage(), w - 24, 2);
		}
	}

	/* needed because set the id of canvas */
	@Override
	public void setEuclidianViewNo(int evNo) {
		if (evNo >= 2) {
			this.evNo = evNo;
		}
	}

	@Override
	public void requestFocus() {
		// this may be really necessary preventing a tabbing away issue
		// but the reasons of it are not well understood #5158
		// after better understanding, this can probably be merged
		// with the following method (requestFocusInWindow()):

		// TODO: or, one method shall do this for sure, the other one
		// should do this only when isInFocus is false
		requestFocusInWindow();

	}

	@Override
	public void resetPointerEventHandler() {
		if (pointerHandler != null) {
			pointerHandler.reset();
		}
	}

	@Override
	public Element getCanvasElement() {
		return g2p.getElement();
	}

	@Override
	public GGraphics2DWI getG2P() {
		return g2p;
	}

	@Override
	public void setPixelRatio(double pixelRatio) {
		if (DoubleUtil.isEqual(g2p.getDevicePixelRatio(), pixelRatio)) {
			return;
		}
		int realWidth = g2p.getOffsetWidth();
		int realHeight = g2p.getOffsetHeight();
		g2p.setDevicePixelRatio(pixelRatio);
		if (realHeight > 0 && realWidth > 0) {
			g2p.setCoordinateSpaceSize(realWidth, realHeight);
			this.createImage();
			this.updateBackground();
			repaint();
		}
	}

	/**
	 * Increase and decrease size of canvas to reset internal state, repaint.
	 * 
	 * @param view
	 *            view
	 */
	public static void forceResize(EuclidianView view) {
		if (view instanceof EuclidianViewWInterface) {
			((EuclidianViewWInterface) view).getG2P().forceResize();
			view.repaintView();
			view.suggestRepaint();
		}
	}

	@Override
	public void setAltText() {
		GeoElement alt = appW.getKernel().lookupLabel("altText" + evNo);
		if (alt == null) {
			alt = appW.getKernel().lookupLabel("altText");
		}
		String altStr = appW.getLocalization().getMenu("DrawingPad");
		if (alt instanceof GeoText) {
			altStr = ((GeoText) alt).getTextString();
			if (g2p.setAltText(altStr)) {
				getScreenReader().readText(altStr);
			}
		} else {
			g2p.setAltText(altStr);
		}
	}

	@Override
	public ReaderWidget getScreenReader() {
		return screenReader;
	}

	@Override
	protected SymbolicEditor createSymbolicEditor() {
		SymbolicEditorW editor = new SymbolicEditorW(app);
		getAbsolutePanel().add(editor);
		return editor;
	}

	@Override
	public void closeDropdowns() {
		closeAllDropDowns();
	}

	@Override
	public void cancelBlur() {
		CancelEventTimer.disableBlurEvent();
	}

	@Override
	public void getPrintable(final FlowPanel pPanel, Button btPrint) {
		double scale = getPrintingScale();
		double origXZero = getXZero();
		double origScale = getXscale();
		if (app.isWhiteboardActive()
				&& getSettings().getBackgroundType() != BackgroundType.NONE
				&& selectionRectangle == null) {
			setCoordSystem(525 / SCALE_STANDARD * origScale, getYZero(),
					origScale, origScale);
		}
		final Image prevImg = new Image();
		String urlText = getExportImageDataUrl(scale, false, false);

		if (app.isWhiteboardActive() && selectionRectangle == null) {
			setCoordSystem(origXZero, getYZero(), origScale, origScale);
		}
		prevImg.getElement().setAttribute("src", urlText);
		prevImg.addStyleName("prevImg");
		prevImg.setWidth(
				(getExportWidth() / getXscale()) * scale + "cm");
		prevImg.setHeight(
				(getExportHeight() / getYscale()) * scale + "cm");
		pPanel.clear();
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				pPanel.add(prevImg);
				Window.print();

				// PrintPreviewW.removePrintPanelFromDOM();
				NodeList<Element> pp = Dom
						.getElementsByClassName("printPanel");
				if (pp.getLength() != 0) {
					pp.getItem(0).removeFromParent();
				}
			}
		});
	}

	@Override
	public double getPixelRatio() {
		return appW.getPixelRatio();
	}

	@Override
	public void setCursor(EuclidianCursor cursor) {
		switch (cursor) {
		case HIT:
			setHitCursor();
			return;
		case DRAG:
			setDragCursor();
			return;
		case MOVE:
			setMoveCursor();
			return;
		case DEFAULT:
			setHitCursor();
			return;
		case RESIZE_X:
			setResizeXAxisCursor();
			return;
		case RESIZE_Y:
			setResizeYAxisCursor();
			return;
		case RESIZE_NESW:
			setResizeNESWCursor();
			return;
		case RESIZE_NWSE:
			setResizeNWSECursor();
			return;
		case RESIZE_EW:
			setResizeEWCursor();
			return;
		case RESIZE_NS:
			setResizeNSCursor();
			return;
		case TRANSPARENT:
			setTransparentCursor();
			return;
		case ERASER:
			if (appW.isWhiteboardActive() && getEuclidianController()
					.getDefaultEventType() != PointerEventType.MOUSE) {
				setTransparentCursor();
			} else {
				setEraserCursor();
			}
			return;
		case PEN:
			if (appW.isWhiteboardActive() && getEuclidianController()
					.getDefaultEventType() != PointerEventType.MOUSE) {
				setTransparentCursor();
			} else {
				setPenCursor();
			}
			return;
		case HIGHLIGHTER:
			if (appW.isWhiteboardActive() && getEuclidianController()
					.getDefaultEventType() != PointerEventType.MOUSE) {
				setTransparentCursor();
			} else {
				setHighlighterCursor();
			}
			return;
		case ROTATION:
			if (appW.isWhiteboardActive() && getEuclidianController()
					.getDefaultEventType() != PointerEventType.MOUSE) {
				setTransparentCursor();
			} else {
				setRotationCursor();
			}
			return;
		}
	}

	private void addScreenReader() {
		screenReader = new ReaderWidget(evNo, g2p.getElement());
		attachReaderWidget(screenReader, app);
	}

	/**
	 * @param screenReaderWidget
	 *            screen reader widget
	 * @param app
	 *            app it needs to be attached to
	 */
	public static void attachReaderWidget(ReaderWidget screenReaderWidget, App app) {
		if (((AppW) app).getPanel().getElement().getParentElement() != null) {
			((AppW) app).getPanel().getElement().getParentElement()
				.appendChild(screenReaderWidget.getElement());
		}
	}

	/**
	 * Focus next view on page.
	 * 
	 * @param from
	 *            current view
	 */
	public static void cycle(EuclidianView from) {
		if ((from == EuclidianViewW.lastInstance)
				&& EuclidianViewW.tabPressed) {
			// if this is the last to blur, and tabPressed
			// is true, i.e. want to select another applet,
			// let's go back to the first one!
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
				@Override
				public void execute() {
					// in theory, the tabPressed will not be set
					// to false
					// before this, because the element that
					// naturally
					// receives focus will not be an
					// EuclidianView,
					// for this is the last one, but why not
					// make sure?
					EuclidianViewW.tabPressed = true;

					// probably we have to wait for the
					// focus event that accompanies this
					// blur first, and only request for
					// new focus afterwards...
					EuclidianViewW.firstInstance.requestFocus();
				}
			});
		}

	}

	/**
	 * 
	 * @param success
	 *            whether tab was handled internally
	 * @return success || last applet left
	 */
	public static boolean checkTabPress(boolean success) {
		if (!success) {
			// should select first GeoElement in next applet
			// this should work well except from last to first
			// so there will be a blur handler there

			// it would be too hard to select the first GeoElement
			// from here, so this will be done in the focus handler
			// of the other applet, depending on whether really
			// this code called it, and it can be done by a static
			// variable for the short term
			EuclidianViewW.tabPressed = true;

			// except EuclidianViewW.lastInstance, do not prevent:
			if (EuclidianViewW.lastInstance.isInFocus()) {
				EuclidianViewW.lastInstance.getCanvasElement().blur();
				return true;
			}
			return false;
		}
		EuclidianViewW.tabPressed = false;
		return true;
	}

	/**
	 * Select next geo in given view when tab pressed
	 * 
	 * @param view
	 *            view
	 */
	public static void selectNextGeoOnTab(EuclidianView view) {
		if (EuclidianViewW.tabPressed) {
			// if focus is moved here from another applet,
			// select the first GeoElement of this Graphics view
			EuclidianViewW.tabPressed = false;
			view.getApplication().getSelectionManager().selectNextGeo(view);

			// .setFirstGeoSelectedForPropertiesView(); might not be
			// perfect,
			// for that GeoElement might not be visible in all Graphics
			// views
		}
	}

	/**
	 * Reset the tab flag
	 */
	public static void resetTab() {
		tabPressed = false;
	}

	@Override
	public void drawStringWithOutline(GGraphics2D g2c, String text, double x,
			double y, GColor col) {

		// Unicode doesn't work in PDF Export currently
		// so draw in LaTeX (uses shapes)
		if (appW.isExporting()) {

			// PDF export doesn't support Unicode
			// so use LaTeX when
			if (ExportType.PDF_HTML5.equals(appW.getExportType())
					&& !StringUtil.isASCII(text)) {
				// different corner for LaTeX
				int offsetY = getFontSize();

				// no callback as we're exporting
				// so font will be loaded already
				appW.getDrawEquation().drawEquation(appW, null, g2c, (int) x,
						(int) (y - offsetY), text, g2c.getFont(), false, col,
						getBackgroundCommon(), true, false, null);

				return;
			}

			// no outline when exporting
			super.drawStringWithOutline(g2c, text, x, y, col);
		}

		// no outline if label color == background color
		if (g2c instanceof GGraphics2DW && !col.equals(getBackgroundCommon())
				&& !app.fileVersionBefore(LABEL_OUTLINES_FROM)) {
			GGraphics2DW g2 = (GGraphics2DW) g2c;
			g2.setColor(getBackgroundCommon());
			String old = g2.getContext().getLineJoin();
			g2.setStroke(outlineStroke);
			g2.drawStringStroke(text, x, y);
			g2.getContext().setLineJoin(old);
		}

		// default (no outline)
		super.drawStringWithOutline(g2c, text, x, y, col);
	}

	/**
	 * 
	 * @return callback (for JLM)
	 */
	@Override
	public Runnable getCallBack(GeoElementND geo, boolean firstCall) {
		return firstCall ? new DrawLaTeXCallBack(geo) : null;
	}

	/**
	 * Schedule background update.
	 */
	public void deferredUpdateBackground() {
		app.invokeLater(new Runnable() {
			@Override
			public void run() {
				updateBackgroundImage();
			}
		});
	}

	private class DrawLaTeXCallBack implements Runnable {

		private GeoElementND geo;

		public DrawLaTeXCallBack(GeoElementND geo) {
			this.geo = geo;
		}

		/**
		 * GGB-2301 repaint after font loaded Special case needed for GeoAxis as
		 * drawn to background image (problem on retina)
		 */
		@Override
		public void run() {
			if (geo instanceof GeoAxis) {
				deferredUpdateBackground();
			}
			repaintView();
		}
	}

	@Override
	public int getThresholdForDrawable(PointerEventType type, Drawable d) {
		if (d instanceof DrawVideo && !Browser.isTabletBrowser()) {
			return DrawVideo.HANDLER_THRESHOLD;
		}
		return app.getCapturingThreshold(type);
	}

	private SVGResource getSVGRulingResource() {
		switch (getSettings().getBackgroundType()) {
		case ELEMENTARY12:
			return GuiResourcesSimple.INSTANCE.mow_ruling_elementary12();
		case ELEMENTARY12_HOUSE:
			return GuiResourcesSimple.INSTANCE.mow_ruling_elementary12house();
		case ELEMENTARY34:
			return GuiResourcesSimple.INSTANCE.mow_ruling_elementary34();
		case MUSIC:
			return GuiResourcesSimple.INSTANCE.mow_ruling_music();
		case SVG:
		case NONE:
		case RULER:
		case SQUARE_BIG:
		case SQUARE_SMALL:
		default:
			return null;
		}
	}

	@Override
	public void createSVGBackgroundIfNeeded() {
		SVGResource res = getSVGRulingResource();
		if (res != null) {
			String uri = ImgResourceHelper.safeURI(res);
			if (!uri.equals(svgBackgroundUri)) {
				Image img = new Image(uri);
				svgBackground = new MyImageW(ImageElement.as(img.getElement()), true);
				svgBackgroundUri = uri;
			}
		}
	}

	@Override
	public MyImage getSVGBackground() {
		return svgBackground;
	}

	/**
	 * @return absolute panel
	 */
	public final AbsolutePanel getAbsolutePanel() {
		return evPanel.getAbsolutePanel();
	}

	@Override
	public boolean isAttached() {
		return g2p != null && g2p.isAttached();
	}
}

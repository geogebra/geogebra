package org.geogebra.web.html5.euclidian;

import java.util.function.Consumer;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.CoordSystemAnimation;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianCursor;
import org.geogebra.common.euclidian.EuclidianPen;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.PenPreviewLine;
import org.geogebra.common.euclidian.SymbolicEditor;
import org.geogebra.common.euclidian.TextRendererSettings;
import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.common.euclidian.draw.DrawWidget;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.EdgeInsets;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.kernel.geos.GeoAxis;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.common.util.debug.Log;
import org.geogebra.ggbjdk.java.awt.DefaultBasicStroke;
import org.geogebra.ggbjdk.java.awt.geom.Dimension;
import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.awt.LayeredGGraphicsW;
import org.geogebra.web.html5.awt.PrintableW;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.export.Canvas2Pdf;
import org.geogebra.web.html5.export.Canvas2Svg;
import org.geogebra.web.html5.export.ExportLoader;
import org.geogebra.web.html5.gawt.GBufferedImageW;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.FocusUtil;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.MyImageW;
import org.geogebra.web.html5.main.TimerSystemW;
import org.geogebra.web.html5.multiuser.MultiuserManager;
import org.geogebra.web.html5.util.PDFEncoderW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.canvas.dom.client.CanvasPixelArray;
import org.gwtproject.canvas.dom.client.Context2d;
import org.gwtproject.canvas.dom.client.ImageData;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.style.shared.Position;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.event.dom.client.DomEvent;
import org.gwtproject.event.dom.client.DropEvent;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.user.client.ui.AbsolutePanel;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.DomGlobal;
import elemental2.dom.FrameRequestCallback;
import elemental2.dom.HTMLCollection;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.WheelEvent;
import jsinterop.base.Js;

/**
 * Web implementation of graphics view
 */
public class EuclidianViewW extends EuclidianView implements
		EuclidianViewWInterface, PrintableW {

	/**
	 * For filtering events if they happen too often
	 */
	final public static int DELAY_BETWEEN_MOVE_EVENTS = 15;
	/** CSS class of the absolute panel*/
	public static final String ABSOLUTE_PANEL_CLASS = "EuclidianPanel";

	private static final int OUTER_GLOW_WIDTH = 5;
	private static final int OUTER_GLOW_ALPHA = 48;
	private static final int INNER_GLOW_WIDTH = 2;
	private static final int INNER_GLOW_ALPHA = 101;
	private static final int SELECTION_ARC = 4;
	private static final int ICON_SIZE = 36;
	private static final int ICON_MARGIN = 4;
	private static final int PLAY_SIZE = 24;
	private static final int PLAY_MARGIN = 12;

	private GGraphics2DWI g2p = null;
	private GGraphics2D g2dtemp;
	private GGraphics2DW g4copy = null;
	private GGraphics2DWI overlayGraphics;

	private GColor backgroundColor = GColor.WHITE;
	private int waitForRepaint = TimerSystemW.SLEEPING_FLAG;
	private String svgBackgroundUri = null;
	private MyImageW svgBackground = null;

	private final FrameRequestCallback repaintCallback = ts -> doRepaint2();

	private long lastRepaint;
	/** application **/
	AppW appW = (AppW) super.app;

	private HTMLImageElement resetImage;
	private HTMLImageElement playImage;
	private HTMLImageElement pauseImage;
	private HTMLImageElement playImageHL;
	private HTMLImageElement pauseImageHL;
	/** parent panel */
	protected EuclidianPanelWAbstract evPanel;
	private PointerEventHandler pointerHandler;

	private GDimension preferredSize;

	private ReaderWidget screenReader;

	/**
	 * cache state
	 * true: currently using cache
	 * false: cache invalidated, not yet cleared
	 * null: cache was invalidated and cleared
	 */
	private Boolean cacheGraphics;

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
		initAriaDefaults();
		attachFocusinHandler();
	}

	private void attachFocusinHandler() {
		((AppW) app).getGlobalHandlers().addEventListener(DomGlobal.document.body, "focusin",
				e -> setResetIconSelected(false));
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
		initAriaDefaults();
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
			this.g2dtemp = new GGraphics2DW(canvas);
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
		if (isTransparent()) {
			((GGraphics2DWI) g2).clearAll();
		}
		if (isGridOrAxesShown() || hasBackgroundImages() || isTraceDrawn()
				|| appW.showResetIcon()
				|| kernel.needToShowAnimationButton()
				|| getBackgroundType() != BackgroundType.NONE) {
			g2.drawImage(bgImage, 0, 0);
		} else if (!isTransparent()) {
			((GGraphics2DWI) g2).fillWith(getBackgroundCommon());
		}
	}

	@Override
	public boolean isTransparent() {
		return ((AppW) app).getAppletParameters().getDataParamTransparentGraphics();
	}

	/**
	 * This doRepaint method should be used instead of repaintView in cases when
	 * the repaint should be done immediately
	 */
	public final void doRepaint2() {
		long time = System.currentTimeMillis();

		if (cacheGraphics != null && cacheGraphics) {
			overlayGraphics.clearRect(0, 0, getWidth(), getHeight());
			getEuclidianController().getPen().repaintIfNeeded(overlayGraphics);
		} else {
			g2p.resetLayer();
			updateBackgroundIfNecessary();
			paint(g2p);
			MultiuserManager.INSTANCE.paintInteractionBoxes(this, g2p);

			if (cacheGraphics != null && getEuclidianController().getSpotlight() == null) {
				cacheGraphics = null;
				overlayGraphics.clearAll();

			} else if (hasSpotlight()) {
				drawSpotlight();
			}
		}

		// if we have pen tool in action
		// repaint the preview line
		lastRepaint = System.currentTimeMillis() - time;
		GeoGebraProfiler.addRepaint(lastRepaint);
		app.getFpsProfiler().notifyRepaint();
	}

	@Override
	public void drawObjects(GGraphics2D g2) {
		if (!app.isExporting()) {
			MultiuserManager.INSTANCE.paintInteractionBackgrounds(this, g2);
		}
		super.drawObjects(g2);
	}

	/**
	 * Gets the coordinate space width of the &lt;canvas&gt;.
	 * 
	 * @return the logical width
	 */
	@Override
	public int getWidth() {
		return (int) getWidthd();
	}

	@Override
	public double getWidthd() {
		return this.g2p.getCoordinateSpaceWidth()
				/ this.g2p.getDevicePixelRatio();
	}

	/**
	 * Gets the coordinate space height of the &lt;canvas&gt;.
	 * 
	 * @return the logical height
	 */
	@Override
	public int getHeight() {
		return (int) getHeightd();
	}

	@Override
	public double getHeightd() {
		return this.g2p.getCoordinateSpaceHeight()
						/ this.g2p.getDevicePixelRatio();
	}

	@Override
	public void clearView() {
		resetInlineObjects();
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

	private void convertToGreyScale(Context2d ctx, int width, int height) {
		ImageData imageData = ctx.getImageData(0, 0, width, height);
		CanvasPixelArray content = imageData.getData();

		for (int index = 0; index < width * height; index++) {
			int r = content.get(4 * index);
			int g = content.get(4 * index + 1);
			int b = content.get(4 * index + 2);
			int grey = (int) Math.round((r + g + b) / 3.0);
			content.set(4 * index, grey);
			content.set(4 * index + 1, grey);
			content.set(4 * index + 2, grey);
		}

		ctx.putImageData(imageData, 0, 0);
	}

	@Override
	public String getExportImageDataUrl(double scale, boolean transparency,
			boolean greyscale) {
		return getExportImageDataUrl(scale, transparency, ExportType.PNG,
				greyscale);
	}

	@Override
	public String getExportImageDataUrl(double scale, boolean transparency,
			ExportType format, boolean greyscale) {
		return dataURL(getExportImageCanvas(scale, transparency, greyscale),
				format);
	}

	/**
	 * @param c canvas
	 * @param format export format
	 * @return canvas as data url of a PNG or WEBP
	 */
	public static String dataURL(Canvas c, ExportType format) {
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
	public void getExportSVG(boolean transparency, Consumer<String> callback) {
		int width = (int) Math.floor(getExportWidth());
		int height = (int) Math.floor(getExportHeight());

		ExportLoader.onCanvas2SvgLoaded(() -> {
			Canvas2Svg canvas2svg = new Canvas2Svg(width, height);
			CanvasRenderingContext2D ctx = Js.uncheckedCast(canvas2svg);
			g4copy = new GGraphics2DW(ctx);
			this.appW.setExporting(ExportType.SVG, 1);
			exportPaintPre(g4copy, 1, transparency);
			drawObjects(g4copy);
			this.appW.setExporting(ExportType.NONE, 1);
			String serializedSvg = canvas2svg.getSerializedSvg(true);
			BlobResolver blobResolver = new BlobResolver(serializedSvg);
			blobResolver.resolve(callback);
		});
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

		Canvas2Pdf.PdfContext ctx = PDFEncoderW.getContext(width, height);

		if (ctx == null) {
			Log.debug("canvas2PDF not found");
			return "";
		}

		g4copy = new GGraphics2DW(ctx);
		this.appW.setExporting(ExportType.PDF_HTML5, scale);

		exportPaintPre(g4copy, scale, false);
		drawObjects(g4copy);

		// include view 2 as 2nd page
		if (page2) {
			ctx.addPage();
			view2.exportPaintPre(g4copy, scale, false);
			view2.drawObjects(g4copy);
		}

		this.appW.setExporting(ExportType.NONE, 1);
		return ctx.getPDFbase64();
	}

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
		DomGlobal.requestAnimationFrame(repaintCallback);
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
		try {
			// just resizing the AbsolutePanelSmart, not the whole of DockPanel
			g2p.getElement().getParentElement().getStyle()
					.setWidth(width, Unit.PX);
			g2p.getElement().getParentElement().getStyle()
					.setHeight(height, Unit.PX);
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
		int size = Math.min(g2p.getCoordinateSpaceWidth(), g2p.getCoordinateSpaceHeight());
		return getExportImageDataUrl(MyXMLio.THUMBNAIL_PIXELS_X / size, false, false);
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

	@Override
	public void settingsChanged(AbstractSettings settings) {
		if (!app.isUsingFullGui()) {
			AppW appW = (AppW) app;
			((EuclidianSettings) settings).setPreferredSizeNoFire(
					new Dimension(appW.getInnerAppletWidth(), appW.getInnerAppletHeight()));
		}
		super.settingsChanged(settings);
	}

	public EuclidianPanelWAbstract getDockPanel() {
		return evPanel;
	}

	/**
	 * @return new panel
	 */
	protected EuclidianViewWrapperPanel newMyEuclidianViewPanel() {
		return new EuclidianViewWrapperPanel(this);
	}

	private void initBaseComponents(EuclidianPanelWAbstract euclidianViewPanel,
			EuclidianController euclidiancontroller, int newEvNo,
			EuclidianSettings settings) {

		final Canvas canvas = euclidianViewPanel.getCanvas();
		this.evNo = newEvNo;

		this.g2p = new LayeredGGraphicsW(canvas);
		g2p.setDevicePixelRatio(appW.getPixelRatio());
		if (appW.getAppletParameters().isDebugGraphics()) {
			g2p.startDebug();
		}

		updateFonts();
		initView(true);
		attachView();

		if (getViewID() == App.VIEW_EUCLIDIAN || getViewID() == App.VIEW_EUCLIDIAN2) {
			g2p.getElement().getStyle().setPosition(Position.ABSOLUTE);
		}

		euclidiancontroller.setView(this);

		if (getViewID() != App.VIEW_TEXT_PREVIEW) {
			registerKeyHandlers(canvas);
			registerMouseTouchGestureHandlers(euclidianViewPanel,
					(EuclidianControllerW) euclidiancontroller);
		}

		registerDragDropHandlers(euclidianViewPanel,
				(EuclidianControllerW) euclidiancontroller);

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
		Element absPanelElement = absPanel.getElement();
		Dom.addEventListener(absPanelElement, "wheel",
				(event) -> euclidiancontroller.onMouseWheel((WheelEvent) event));

		pointerHandler = new PointerEventHandler((IsEuclidianController) euclidianController,
				euclidiancontroller.getOffsets());
		// absolute panel has no parent in WebSimple
		Element pointerTarget = absPanelElement.getParentElement() == null
				? absPanelElement : absPanelElement.getParentElement();
		pointerHandler.attachTo(pointerTarget, ((AppW) app).getGlobalHandlers());
		CancelEventTimer.killTouch(absPanel);
		absPanel.addBitlessDomHandler(DomEvent::stopPropagation, MouseDownEvent.getType());
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

	private HTMLImageElement getResetImage() {
		if (resetImage == null) {
			resetImage = this.appW.getRefreshViewImage();
		}
		return resetImage;
	}

	private HTMLImageElement getPlayImage(boolean highlight) {
		if (playImage == null) {
			playImage = this.appW.getPlayImage();
			playImageHL = this.appW.getPlayImageHover();
		}
		return highlight ? playImageHL : playImage;
	}

	private HTMLImageElement getPauseImage(boolean highlight) {
		if (pauseImage == null) {
			pauseImage = this.appW.getPauseImage();
			pauseImageHL = this.appW.getPauseImageHover();
		}
		return highlight ? pauseImageHL : pauseImage;
	}

	@Override
	public boolean requestFocusInWindow() {
		FocusUtil.focusNoScroll(getCanvasElement());
		return true;
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
		Element cursorElement = getAbsolutePanel() == null ? null : getAbsolutePanel().getElement();
		if (cursorElement != null
				&& !cursorElement.hasClassName(className)) {
			this.appW.resetCursor();
			cursorElement.setClassName(ABSOLUTE_PANEL_CLASS);
			cursorElement.addClassName(className);
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
		return appW.getGuiManager().newEuclidianStylebar(this, getViewID());
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
		if (!drawPlayButtonInThisView() || appW.isScreenshotGenerator() || appW.isExporting()) {
			return;
		}
		GRectangle2D frame = getAnimationButtonFrame();
		int x = (int) frame.getX();
		int y = (int) frame.getY();

		// draw pause or play button
		final HTMLImageElement img = kernel.isAnimationRunning()
				? getPauseImage(highlightAnimationButtons)
				: getPlayImage(highlightAnimationButtons);
		if (img.complete) {
			((GGraphics2DW) g2).drawImage(img, x, y);
		} else {
			img.addEventListener("load", (event) -> ((GGraphics2DW) g2).drawImage(img, x, y));
		}
	}

	@Override
	public boolean hitAnimationButton(int x, int y) {
		// draw button in focused EV only
		if (!drawPlayButtonInThisView()) {
			return false;
		}
		GRectangle2D frame = getAnimationButtonFrame();
		GRectangle2D extendedFrame = AwtFactory.getPrototype().newRectangle(
				(int) frame.getX() - PLAY_MARGIN, (int) frame.getY() - PLAY_MARGIN,
				(int) frame.getWidth() + 2 * PLAY_MARGIN,
				(int) frame.getHeight() + 2 * PLAY_MARGIN
		);

		return kernel.needToShowAnimationButton() && extendedFrame.contains(x, y);
	}

	private GRectangle2D getAnimationButtonFrame() {
		EdgeInsets insets = getSafeAreaInsets();
		return AwtFactory.getPrototype().newRectangle(
				insets.getLeft(),
				getHeight() - insets.getBottom() - PLAY_SIZE,
				PLAY_SIZE, PLAY_SIZE
		);
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
		setPreferredSize(new Dimension(width, height));
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

	private void setMindmapCursor() {
		setCursorClass("cursor_mindmap");
	}

	private void setTableCursor() {
		setCursorClass("cursor_table");
	}

	private void setTextCursor() {
		setCursorClass("cursor_text");
	}

	private void setCrosshairCursor() {
		setCursorClass("cursor_crosshair");
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
	public void add(Widget box) {
		if (evPanel != null) {
			evPanel.getAbsolutePanel().add(box);
		}
	}

	@Override
	public Object getExportCanvas() {
		return getCanvasElement();
	}

	public void focusResetIcon() {
		setResetIconSelected(true);
	}

	@Override
	protected void drawResetIcon(GGraphics2D g) {
		// omit for export
		if (!appW.isExporting()) {
			GGraphics2DW graphics = (GGraphics2DW) g;
			HTMLImageElement resetIcon = getResetImage();
			int width = getWidth();
			int iconWidth = resetIcon.width;
			int iconHeight = resetIcon.height;

			graphics.drawImage(resetIcon,
					width - ICON_MARGIN - iconWidth - (ICON_SIZE - iconWidth) / 2,
					ICON_MARGIN + (ICON_SIZE - iconHeight) / 2);
			if (isResetIconSelected()) {
				drawHighlight(graphics, width - ICON_MARGIN - ICON_SIZE, ICON_MARGIN, ICON_SIZE);
			}
		}
	}

	private void drawHighlight(GGraphics2DW graphics, int x, int y, int highlightSize) {
		// Outer glow
		graphics.setStroke(new DefaultBasicStroke(OUTER_GLOW_WIDTH));
		graphics.setColor(GColor.BLACK.deriveWithAlpha(OUTER_GLOW_ALPHA));
		graphics.drawRoundRect(x, y, highlightSize, highlightSize, SELECTION_ARC, SELECTION_ARC);

		// Inner glow
		graphics.setStroke(new DefaultBasicStroke(INNER_GLOW_WIDTH));
		graphics.setColor(GColor.BLACK.deriveWithAlpha(INNER_GLOW_ALPHA));
		graphics.drawRoundRect(x, y, highlightSize, highlightSize, SELECTION_ARC, SELECTION_ARC);
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
			createImage();
			updateBackground();
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
	public ReaderWidget getScreenReader() {
		return screenReader;
	}

	@Override
	protected SymbolicEditor createSymbolicEditor(TextRendererSettings settings) {
		GuiManagerInterfaceW gm = ((AppW) app).getGuiManager();
		if (gm == null) {
			return null;
		}

		return gm.createSymbolicEditor(this, settings);
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
	public void getPrintable(final FlowPanel pPanel, Runnable enablePrintBtn) {
		double scale = getPrintingScale();
		double origXZero = getXZero();
		double origScale = getXscale();
		if (app.isWhiteboardActive()
				&& getBackgroundType() != BackgroundType.NONE
				&& selectionRectangle == null) {
			setCoordSystem(525 / EuclidianView.SCALE_STANDARD * origScale, getYZero(),
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
		Scheduler.get().scheduleDeferred(() -> {
			pPanel.add(prevImg);
			DomGlobal.window.print();

			// PrintPreviewW.removePrintPanelFromDOM();
			HTMLCollection<elemental2.dom.Element> pp = Dom
					.getElementsByClassName("printPanel");
			if (pp.getLength() != 0) {
				pp.getAt(0).remove();
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
		case DEFAULT:
		default:
			setHitCursor();
			return;
		case DRAG:
			setDragCursor();
			return;
		case MOVE:
			setMoveCursor();
			return;
		case RESIZE_X:
		case RESIZE_EW:
			setResizeEWCursor();
			return;
		case RESIZE_Y:
		case RESIZE_NS:
			setResizeNSCursor();
			return;
		case RESIZE_NESW:
			setResizeNESWCursor();
			return;
		case RESIZE_NWSE:
			setResizeNWSECursor();
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
			setPenCursor();
			return;
		case HIGHLIGHTER:
			setHighlighterCursor();
			return;
		case MINDMAP:
			setMindmapCursor();
			return;
		case TABLE:
			setTableCursor();
			return;
		case TEXT:
			setTextCursor();
			return;
		case CROSSHAIR:
			setCrosshairCursor();
			return;
		case ROTATION:
			if (appW.isWhiteboardActive() && getEuclidianController()
					.getDefaultEventType() != PointerEventType.MOUSE) {
				setTransparentCursor();
			} else {
				setRotationCursor();
			}
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
		if (((AppW) app).getAppletFrame().getElement().getParentElement() != null) {
			((AppW) app).getAppletFrame().getElement().getParentElement()
				.appendChild(screenReaderWidget.getElement());
			((AppW) app).setLastFocusableWidget(screenReaderWidget);
		}
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
				&& !app.fileVersionBefore(EuclidianView.LABEL_OUTLINES_FROM)) {
			GGraphics2DW g2 = (GGraphics2DW) g2c;
			g2.setColor(getBackgroundCommon());
			String old = g2.getContext().getLineJoin();
			GBasicStroke outlineStroke = createStringOutlineStroke(3);
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
		app.invokeLater(this::updateBackgroundImage);
	}

	private class DrawLaTeXCallBack implements Runnable {

		private final GeoElementND geo;

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

	private SVGResource getSVGRulingResource() {
		switch (getBackgroundType()) {
		case ELEMENTARY12:
			return GuiResourcesSimple.INSTANCE.mow_ruling_elementary12();
		case ELEMENTARY12_HOUSE:
			return GuiResourcesSimple.INSTANCE.mow_ruling_elementary12house();
		case ELEMENTARY12_COLORED:
			return GuiResourcesSimple.INSTANCE.mow_ruling_elementary12colored();
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

	private BackgroundType getBackgroundType() {
		return getSettings() == null ? BackgroundType.NONE : getSettings().getBackgroundType();
	}

	private void createSVGBackgroundIfNeeded() {
		SVGResource res = getSVGRulingResource();
		if (res != null) {
			String uri = NoDragImage.safeURI(res);
			if (!uri.equals(svgBackgroundUri)) {
				HTMLImageElement img = Dom.createImage();
				img.src = uri;
				svgBackground = new MyImageW(img, true);
				svgBackgroundUri = uri;
			}
		}
	}

	@Override
	public MyImage getSVGBackground() {
		createSVGBackgroundIfNeeded();
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

	@Override
	public PenPreviewLine newPenPreview() {
		return new PenPreviewLineW();
	}

	/**
	 * @return keyboard listener for active symbolic editor
	 */
	public MathKeyboardListener getKeyboardListener() {
		if (symbolicEditor instanceof HasMathKeyboardListener) {
			return ((HasMathKeyboardListener) symbolicEditor).getKeyboardListener();
		}
		return null;
	}

	@Override
	public AppW getApplication() {
		return (AppW) super.getApplication();
	}

	@Override
	public void embed(GGraphics2D g2, DrawWidget widget) {
		int layer = ((GGraphics2DWI) g2).embed();
		EmbedManager embedManager = getApplication().getEmbedManager();
		if (embedManager != null) {
			embedManager.setLayer(widget, layer);
		}
		g2.saveTransform();
		g2.transform(widget.getTransform());
		g2.clearRect(0, 0, (int) widget.getWidth(), (int) widget.getHeight());
		g2.restoreTransform();
	}

	@Override
	public void invalidateCache() {
		if (overlayGraphics != null) {
			cacheGraphics = false;
		}
	}

	/**
	 * Cache all drawables
	 */
	@Override
	public void cacheGraphics() {
		cacheGraphics = true;
		initOverlayGraphics();
	}

	private void initOverlayGraphics() {
		if (overlayGraphics == null) {
			Canvas pCanvas = Canvas.createIfSupported();
			overlayGraphics = new GGraphics2DW(pCanvas);
			overlayGraphics.getElement().getStyle().setPosition(Position.ABSOLUTE);
			overlayGraphics.setDevicePixelRatio(appW.getPixelRatio());
			g2p.getElement().getParentElement()
					.appendChild(overlayGraphics.getElement());
			overlayGraphics.getElement().addClassName("overlayGraphics");
		}
		EuclidianPen pen = getEuclidianController().getPen();
		overlayGraphics.setCoordinateSpaceSize(getWidth(), getHeight());
		overlayGraphics.setStroke(EuclidianStatic.getStroke(pen.getPenSize(),
				pen.getPenLineStyle(), GBasicStroke.JOIN_ROUND));
		overlayGraphics.setColor(pen.getPenColorWithOpacity());
	}

	@Override
	public void clearSpotlight() {
		super.clearSpotlight();
		overlayGraphics.clearAll();
	}

	private void drawSpotlight() {
		if (overlayGraphics == null) {
			initOverlayGraphics();
		}
		overlayGraphics.clearAll();
		GeoElementND spotlight = euclidianController.getSpotlight();
		Drawable d = (Drawable) getDrawableFor(spotlight);
		if (d != null) {
			d.draw(overlayGraphics);
		}
		if (getBoundingBox() != null) {
			getBoundingBox().draw(overlayGraphics);
		}
	}

}

package org.geogebra.web.geogebra3D.web.euclidian3D;

import java.util.function.Consumer;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.CoordSystemAnimation;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.ScreenReaderAdapter;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.Format;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.kernel.commands.CommandNotLoadedError;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.common.util.debug.Log;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.RendererWInterface;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.RendererWithImplW;
import org.geogebra.web.geogebra3D.web.euclidian3DnoWebGL.RendererWnoWebGL;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.euclidian.EuclidianPanelWAbstract;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.euclidian.EuclidianViewWrapperPanel;
import org.geogebra.web.html5.euclidian.GGraphics2DWI;
import org.geogebra.web.html5.euclidian.IsEuclidianController;
import org.geogebra.web.html5.euclidian.PointerEventHandler;
import org.geogebra.web.html5.euclidian.ReaderWidget;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.main.TimerSystemW;
import org.gwtproject.animation.client.AnimationScheduler;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.event.dom.client.DomEvent;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.user.client.ui.RequiresResize;
import org.gwtproject.user.client.ui.Widget;

import com.google.gwt.core.client.Scheduler;

import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.WheelEvent;
import jsinterop.base.Js;

/**
 * 3D view
 * 
 * @author mathieu
 *
 */
public class EuclidianView3DW extends EuclidianView3D implements
		EuclidianViewWInterface {

	private EuclidianPanelWAbstract evPanel;

	/** graphics */
	private GGraphics2DWI g2p = null;

	private final AnimationScheduler repaintScheduler = AnimationScheduler.get();
	private long lastRepaint;
	private int waitForRepaint = TimerSystemW.SLEEPING_FLAG;
	private int objectsWaitingForNewRepaint = 0;

	private ReaderWidget screenReader;

	/**
	 * constructor
	 * 
	 * @param ec
	 *            euclidian controller
	 * @param settings
	 *            euclidian settings
	 */
	public EuclidianView3DW(EuclidianController3D ec, EuclidianSettings settings) {
		super(ec, settings);
		initBaseComponents(evPanel, ec);

		getRenderer().init();
		initAriaDefaults();
		if (ec.getApplication().showToolBar()) {
			Scheduler.get().scheduleDeferred(this::loadCommands);
		}
	}

	private void loadCommands() {
		try {
			getKernel().getAlgebraProcessor().getCommandDispatcher()
					.getSpatialCommandProcessorFactory();
		} catch (CommandNotLoadedError ignore) {
			// loading
		}
	}

	private void initBaseComponents(EuclidianPanelWAbstract euclidianViewPanel,
	        EuclidianController euclidiancontroller) {

		Canvas canvas = euclidianViewPanel.getCanvas();
		setEvNo();
		this.g2p = new GGraphics2DW(canvas);

		updateFonts();
		initView(true);
		attachView();

		euclidiancontroller.setView(this);

		registerKeyHandlers(canvas);
		registerMouseTouchGestureHandlers(euclidianViewPanel,
				(EuclidianController3DW) euclidiancontroller);

		EuclidianSettings es = this.app.getSettings().getEuclidian(3);
		settingsChanged(es);
		es.addListener(this);
		addScreenReader();
	}

	private void initAriaDefaults() {
		Element elem = g2p.getElement();
		if (elem != null) {
			elem.setAttribute("role", "figure");
			elem.setAttribute("aria-label", "3D View");
		}
	}

	private void setEvNo() {
		this.evNo = EVNO_3D;
	}

	@Override
	public void setEuclidianViewNo(int evNo) {
		this.evNo = evNo;
	}

	private void registerKeyHandlers(Canvas canvas) {
		if (canvas == null) {
			return;
		}

		new EuclidianKeyHandler3DW((AppW) app).listenTo(canvas);
	}

	private void registerMouseTouchGestureHandlers(
	        EuclidianPanelWAbstract euclidianViewPanel,
	        EuclidianController3DW euclidiancontroller) {
		Widget absPanel = euclidianViewPanel.getAbsolutePanel();
		Dom.addEventListener(absPanel.getElement(), "wheel",
				(event) -> euclidiancontroller.onMouseWheel((WheelEvent) event));

		PointerEventHandler pointerHandler = new PointerEventHandler(
				(IsEuclidianController) euclidianController,
				euclidiancontroller.getOffsets());
		pointerHandler.attachTo(absPanel.getElement(),
				((AppW) app).getGlobalHandlers());
		CancelEventTimer.killTouch(absPanel);
		absPanel.addBitlessDomHandler(DomEvent::stopPropagation, MouseDownEvent.getType());
	}

	/**
	 * @return panel component
	 */
	public Widget getComponent() {
		return evPanel.getAbsolutePanel();
	}

	// //////////////////////////////////////////////////////////
	// MyEuclidianViewPanel
	// //////////////////////////////////////////////////////////

	/**
	 * @return EV panel
	 */
	protected EuclidianViewWrapperPanel newMyEuclidianViewPanel() {
		return new EuclidianViewWrapperPanel3D(this);
	}

	/**
	 * panel for 3D
	 * 
	 * @author mathieu
	 *
	 */
	private class EuclidianViewWrapperPanel3D extends EuclidianViewWrapperPanel implements
	        RequiresResize {

		/**
		 * constructor
		 * 
		 * @param ev
		 *            euclidian view
		 */
		public EuclidianViewWrapperPanel3D(EuclidianView ev) {
			super(ev);
		}

		@Override
		protected Canvas createCanvas() {
			Renderer pRenderer = getRenderer();
			return (Canvas) pRenderer.getCanvas();
		}

		@Override
		public void onResize() {
			super.onResize();
			getEuclidianController().calculateEnvironment();
		}
	}

	@Override
	public void setToolTipText(String plainTooltip) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void requestFocus() {
		// this may be really necessary preventing a tabbing away issue
		// but the reasons of it are not well understood #5158
		// after better understanding, this can probably be merged
		// with the following method (requestFocusInWindow()):
		requestFocusInWindow();
	}

	/**
	 * Gets the coordinate space width of the &lt;canvas&gt;.
	 * 
	 * @return the logical width
	 */
	@Override
	public int getWidth() {
		return g2p == null ? 0
				: (int) (this.g2p.getCoordinateSpaceWidth() / getPixelRatio());
	}

	/**
	 * Gets the coordinate space height of the &lt;canvas&gt;.
	 * 
	 * @return the logical height
	 */
	@Override
	public int getHeight() {
		return g2p == null ? 0
				: (int) (this.g2p.getCoordinateSpaceHeight() / getPixelRatio());
	}

	@Override
	public final boolean isShowing() {
		return g2p != null && g2p.getCanvas() != null
				&& g2p.getCanvas().isAttached() && g2p.getCanvas().isVisible();
	}

	@Override
	protected void createPanel() {
		evPanel = newMyEuclidianViewPanel();

	}

	@Override
	protected Renderer createRenderer() {
		Canvas webGLcanvas = Canvas.createIfSupported();
		if (webGLcanvas == null) {
			return new RendererWnoWebGL(this);
		}
		return new RendererWithImplW(this, webGLcanvas,
				((AppW) app).getAppletParameters().getDataParamTransparentGraphics());
	}

	@Override
	protected boolean getShiftDown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void setDefault2DCursor() {
		setCursorClass("cursor_hit");
	}

	@Override
	public GGraphics2D getTempGraphics2D(GFont fontForGraphics) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GFont getFont() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	final protected void setStyleBarMode(int mode) {
		if (hasStyleBar()) {
			getStyleBar().setMode(mode);
		}
	}

	@Override
	protected void updateSizeKeepDrawables() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean requestFocusInWindow() {
		g2p.getElement().focus();
		return true;
	}

	@Override
	public void setPreferredSize(GDimension preferredSize) {
		if (renderer != null) {
			((RendererWInterface) renderer).setPixelRatio(getPixelRatio());
			renderer.setView(0, 0, preferredSize.getWidth(),
					preferredSize.getHeight());
		}
		if (g2p != null && g2p.getContext() != null) {
			g2p.setPreferredSize(preferredSize);

			updateSize();
			setReIniting(false);
		}
	}

	@Override
	public double getPixelRatio() {
		return ((AppW) app).getPixelRatio();
	}

	@Override
	protected CoordSystemAnimation newZoomer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(Widget box) {
		if (evPanel != null) {
			evPanel.getAbsolutePanel().add(box);
		}
	}

	private void setCursorClass(String className) {
		// IMPORTANT: do nothing if we already have the classname,
		// app.resetCursor is VERY expensive in IE
		Canvas canvas = (Canvas) this.getRenderer().getCanvas();
		if (canvas != null && !canvas.getElement().hasClassName(className)) {
			((AppW) this.app).resetCursor();
			canvas.setStyleName("");
			canvas.addStyleName(className);
		}
	}

	@Override
	public void setTransparentCursor() {
		setCursorClass("cursor_transparent");
	}

	@Override
	protected EuclidianStyleBar newEuclidianStyleBar() {
		return new EuclidianStyleBar3DW(this);
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
	public Element getCanvasElement() {
		return g2p == null ? null : g2p.getElement();
	}

	/**
	 * the file has been set by the App
	 * 
	 * @param file
	 *            file
	 */
	public void setCurrentFile(GgbFile file) {
		// used only when no webGL
	}

	/**
	 * This doRepaint method should be used instead of repaintView in cases when
	 * the repaint should be done immediately
	 */
	public final void doRepaint2() {
		if (!isParentWindowVisible()) {
			return;
		}
		long time = System.currentTimeMillis();
		// ((DrawEquationWeb) this.app.getDrawEquation()).clearLaTeXes(this);
		this.updateBackgroundIfNecessary();

		// paint(this.g2p);
		if (renderer.isReadyToRender()) {
			renderer.drawScene();
		}

		lastRepaint = System.currentTimeMillis() - time;
		GeoGebraProfiler.addRepaint(lastRepaint);

		if (objectsWaitingForNewRepaint > 0) {
			waitForRepaint = TimerSystemW.EUCLIDIAN_LOOPS;
			objectsWaitingForNewRepaint--;
		} else {
			waitForRepaint = TimerSystemW.SLEEPING_FLAG;
		}
	}

	@Override
	public long getLastRepaintTime() {
		return lastRepaint;
	}

	@Override
	public void repaint() {
		getApplication().ensureTimerRunning();
		if (waitForRepaint == TimerSystemW.SLEEPING_FLAG) {
			waitForRepaint = TimerSystemW.EUCLIDIAN_LOOPS;
		}
	}

	@Override
	final public void waitForNewRepaint() {
		objectsWaitingForNewRepaint++;
	}

	/**
	 * schedule a repaint
	 */
	public void doRepaint() {
		repaintScheduler.requestAnimationFrame(ts -> doRepaint2());
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
			}
			return true;
		}

		waitForRepaint--;
		return true;
	}

	@Override
	public void exportPaintPre(GGraphics2D g2d, double scale, boolean transparency) {
		// TODO Auto-generated method stub

	}

	@Override
	public GGraphics2DWI getG2P() {
		return g2p;
	}

	@Override
	public void resetPointerEventHandler() {
		// TODO Auto-generated method stub
	}

	@Override
	public String getExportImageDataUrl(double scale, boolean transparent, boolean greyscale) {
		return getExportImageDataUrl(scale, transparent, ExportType.PNG,
				greyscale);
	}

	@Override
	public String getExportImageDataUrl(double scale, boolean transparent,
			ExportType format, boolean greyscale) {
		return getExportCanvas().toDataURL(
				format == ExportType.WEBP ? "image/webp" : "image/png");
	}

	@Override
	public String getCanvasBase64WithTypeString() {
		((RendererWInterface) this.renderer).setBuffering(true);
		this.doRepaint2();
		String ret = getCanvasBase64WithTypeString(
				this.getWidth(), getHeight());
		((RendererWInterface) this.renderer).setBuffering(false);
		return ret;
	}

	private String getCanvasBase64WithTypeString(double width, double height) {
		Canvas foreground = ((RendererWInterface) this.renderer).getCanvas();
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
		CanvasRenderingContext2D c2 = Js.uncheckedCast(canv.getContext2d());

		c2.drawImage(Js.<HTMLCanvasElement>uncheckedCast(foreground.getCanvasElement()),
				0, 0, (int) thx, (int) thy);

		return EuclidianViewW.dataURL(canv, null);
	}

	@Override
	public void setPixelRatio(double pixelRatio) {
		if (DoubleUtil.isEqual(g2p.getDevicePixelRatio(), pixelRatio)
				|| pixelRatio == 0) {
			// GGB-2355 we shouldn't set ratio to 0; quit fast before we get
			// into loop
			return;
		}
		int realWidth = g2p.getOffsetWidth();
		int realHeight = g2p.getOffsetHeight();
		g2p.setDevicePixelRatio(pixelRatio);
		if (realHeight > 0 && realWidth > 0) {
			((AppW) app).ggwGraphicsView3DDimChanged(realWidth, realHeight);
		}
	}

	/**
	 * @param width
	 *            canvas width
	 * @param height
	 *            canvas height
	 */
	public void setCoordinateSpaceSize(int width, int height) {

		// no transform nor color set since it's a WebGL context
		g2p.setCoordinateSpaceSizeNoTransformNoColor(width, height);
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

	@Override
	public ScreenReaderAdapter getScreenReader() {
		return screenReader;
	}

	@Override
	protected void drawBackgroundImage(GGraphics2D g2d) {
		// nothing to do here
	}

	@Override
	protected void addDynamicStylebarToEV(EuclidianStyleBar dynamicStylebar) {
		if (app.isUnbundled() && ((AppW) app).allowStylebar()) {
			if (((Widget) dynamicStylebar).getParent() == null) {
				((AppW) app).getGuiManager().addStylebar(this, dynamicStylebar);
			}
		}
	}

	@Override
	protected EuclidianStyleBar newDynamicStyleBar() {
		if (app.isUnbundled() && ((AppW) app).allowStylebar()) {
			return ((AppW) app).getGuiManager().newDynamicStylebar(this);
		}
		return null;
	}
	
	@Override
	public void setExport3D(Format format, boolean showDialog) {
		super.setExport3D(format, showDialog);
		repaint();
	}

	@Override
	public void getExportSVG(boolean transparency, Consumer<String> callback) {
		// not implemented
	}

	@Override
	public String getExportPDF(double scale) {
		return null;
	}

	/**
	 * @return whether the frame we are running in is visible
	 */
	private static boolean isParentWindowVisible() {
		return NavigatorUtil.getWindowWidth() > 0;
	}

	private void addScreenReader() {
		screenReader = new ReaderWidget(evNo, g2p.getElement());
		EuclidianViewW.attachReaderWidget(screenReader, app);
	}

	@Override
	public boolean isAttached() {
		return g2p != null && g2p.isAttached();
	}

	@Override
	public HTMLCanvasElement getExportCanvas() {
		RendererWInterface rendererW = (RendererWInterface) this.renderer;
		rendererW.setBuffering(true);
		this.doRepaint2();
		rendererW.setBuffering(true);
		return Js.uncheckedCast(rendererW.getCanvas().getCanvasElement());
	}
}

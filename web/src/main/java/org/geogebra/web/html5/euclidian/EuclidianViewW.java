package org.geogebra.web.html5.euclidian;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianCursor;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.MyZoomer;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.awt.GBasicStrokeW;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.awt.PrintableW;
import org.geogebra.web.html5.gawt.GBufferedImageW;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.javax.swing.GBoxW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.TimerSystemW;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.ImageLoadCallback;
import org.geogebra.web.html5.util.ImageWrapper;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class EuclidianViewW extends EuclidianView implements
		EuclidianViewWInterface, PrintableW {



	final public static int DELAY_BETWEEN_MOVE_EVENTS = 30;

	public GGraphics2DW g2p = null;
	private GGraphics2D g2dtemp;
	public GGraphics2DW g4copy = null;
	private GColor backgroundColor = GColor.WHITE;

	private AnimationScheduler.AnimationCallback repaintCallback = new AnimationScheduler.AnimationCallback() {
		@Override
		public void execute(double ts) {
			doRepaint2();
		}
	};

	private AnimationScheduler repaintScheduler = AnimationScheduler.get();

	private long lastRepaint;

	public boolean isInFocus = false;

	AppW app = (AppW) super.app;

	protected ImageElement resetImage, playImage, pauseImage, upArrowImage,
			downArrowImage, playImageHL, pauseImageHL;

	protected EuclidianPanelWAbstract EVPanel;
	private MsZoomer msZoomer;

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

	/**
	 * @param euclidianViewPanel
	 * @param euclidiancontroller
	 * @param showAxes
	 * @param showGrid
	 * @param evNo
	 * @param settings
	 */
	public EuclidianViewW(EuclidianPanelWAbstract euclidianViewPanel,
	        EuclidianController euclidiancontroller, boolean[] showAxes,
	        boolean showGrid, int evNo, EuclidianSettings settings) {

		super(euclidiancontroller, evNo, settings);
		viewTextField = new ViewTextFieldW(this);
		EVPanel = euclidianViewPanel;

		initBaseComponents(euclidianViewPanel, euclidiancontroller, evNo,
				settings);

		initClickStartHandler();
	}

	private void initClickStartHandler() {
		ClickStartHandler.init(g2p.getCanvas(), new ClickStartHandler() {
			@Override
			public void onClickStart(final int x, final int y,
					PointerEventType type) {

				getEuclidianController().closePopups(x, y, type);



			}
		});

	}

	/**
	 * @param euclidiancontroller
	 *            controller
	 * @param showAxes
	 *            whether to show axes
	 * @param showGrid
	 *            whether to show grid
	 * @param viewNo
	 *            view number
	 * @param settings
	 *            settings
	 */
	public EuclidianViewW(EuclidianController euclidiancontroller,
	        boolean[] showAxes, boolean showGrid, int viewNo,
	        EuclidianSettings settings) {

		this(euclidiancontroller, viewNo, settings);

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
		EVPanel = newMyEuclidianViewPanel();

		// It seems this constructor is only called from PlotPanelEuclidianViewW
		// currently,
		// so this -1 is changed to viewNo because EVNO_GENERAL is needed for
		// making sure that this view does not change the toolbar, code in
		// EuclidianControllerW
		// if you think it is not Okay, then use (-1) in EuclidianControllerW
		// instead of EVNO_GENERAL
		// at mouse events which call setActiveToolbarId #plotpanelevno
		// initBaseComponents(EVPanel, euclidiancontroller, -1);
		initBaseComponents(EVPanel, euclidiancontroller, viewNo, settings);

		initClickStartHandler();
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
			this.g2dtemp = new GGraphics2DW(Canvas.createIfSupported());
		}
		this.g2dtemp.setFont(fontForGraphics);
		return this.g2dtemp;
	}

	@Override
	protected final MyZoomer newZoomer() {
		return new MyZoomerW(this);
	}

	@Override
	public final void paintBackground(GGraphics2D g2) {
		if (isGridOrAxesShown() || hasBackgroundImages() || isTraceDrawn()
				|| app.showResetIcon()
		        || kernel.needToShowAnimationButton()) {
			((GGraphics2DW) g2).drawImage(bgImage,
					0, 0);
		} else {
			((GGraphics2DW) g2).fillWith(getBackgroundCommon());
		}

	}

	/**
	 * This doRepaint method should be used instead of repaintView in cases when
	 * the repaint should be done immediately
	 */
	public final void doRepaint2() {
		long time = System.currentTimeMillis();
		this.updateBackgroundIfNecessary();
		paint(this.g2p);
		// if we have pen tool in action
		// repaint the preview line
		getEuclidianController().setCollectedRepaints(false);
		lastRepaint = System.currentTimeMillis() - time;
		GeoGebraProfiler.addRepaint(lastRepaint);

	}

	/**
	 * Gets the coordinate space width of the &lt;canvas&gt;.
	 * 
	 * @return the logical width
	 */
	@Override
	public int getWidth() {
		return (int) (this.g2p.getCoordinateSpaceWidth() / this.g2p.devicePixelRatio);
	}

	/**
	 * Gets the coordinate space height of the &lt;canvas&gt;.
	 * 
	 * @return the logical height
	 */
	@Override
	public int getHeight() {
		return (int) (this.g2p.getCoordinateSpaceHeight() / this.g2p.devicePixelRatio);
	}

	@Override
	public void clearView() {
		resetLists();
		updateBackgroundImage(); // clear traces and images
		// resetMode();
		if (app.getGuiManager() != null) {
			app.getGuiManager().clearAbsolutePanels();
		}
		removeTextField();

	}

	@Override
	protected final void setHeight(int h) {
		// TODO: not clear what should we do
	}

	@Override
	protected final void setWidth(int h) {
		// TODO: not clear what should we do
	}

	@Override
	public final GGraphics2DW getGraphicsForPen() {
		return g2p;
	}

	@Override
	public final boolean isShowing() {
		return g2p != null && g2p.getCanvas() != null
		        && g2p.getCanvas().isAttached() && g2p.getCanvas().isVisible();
	}

	@Override
	public String getExportImageDataUrl(double scale, boolean transparency) {
		int width = (int) Math.floor(getExportWidth() * scale);
		int height = (int) Math.floor(getExportHeight() * scale);

		Canvas c4 = Canvas.createIfSupported();
		c4.setCoordinateSpaceWidth(width);
		c4.setCoordinateSpaceHeight(height);
		c4.setWidth(width + "px");
		c4.setHeight(height + "px");
		g4copy = new GGraphics2DW(c4);
		this.app.setExporting(ExportType.PNG, scale);
		exportPaintPre(g4copy, scale, transparency);
		drawObjects(g4copy);
		this.app.setExporting(ExportType.NONE, 1);
		return g4copy.getCanvas().toDataUrl();
	}

	@Override
	public GBufferedImageW getExportImage(double scale) {
		return getExportImage(scale, false);
	}

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
		if (getEuclidianController().isCollectingRepaints()) {
			getEuclidianController().setCollectedRepaints(true);
			return;
		}
		getApplication().ensureTimerRunning();
		if (waitForRepaint == TimerSystemW.SLEEPING_FLAG) {
			waitForRepaint = TimerSystemW.EUCLIDIAN_LOOPS;
		}
	}

	private int waitForRepaint = TimerSystemW.SLEEPING_FLAG;

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

	public void setCoordinateSpaceSize(int width, int height) {

		g2p.setCoordinateSpaceSize(width, height);
		try {
			// just resizing the AbsolutePanelSmart, not the whole of DockPanel
			g2p.getCanvas().getElement().getParentElement().getStyle()
			        .setWidth(width, Style.Unit.PX);
			g2p.getCanvas().getElement().getParentElement().getStyle()
			        .setHeight(height, Style.Unit.PX);
			getEuclidianController().calculateEnvironment();
		} catch (Exception exc) {
			Log.debug("Problem with the parent element of the canvas");
		}
	}

	public void synCanvasSize() {
		setCoordinateSpaceSize(g2p.getOffsetWidth(), g2p.getOffsetHeight());
	}

	@Override
	public String getCanvasBase64WithTypeString() {
		return getCanvasBase64WithTypeString(g2p.getCoordinateSpaceWidth(),
		        g2p.getCoordinateSpaceHeight(), bgGraphics == null ? null
		                : ((GGraphics2DW) bgGraphics).getCanvas(),
		        g2p.getCanvas());
	}

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

		// g2p.getCanvas().getContext2d().drawImage(((GGraphics2DW)bgGraphics).getCanvas().getCanvasElement(),
		// 0, 0, (int)thx, (int)thy);
		if (background != null) {
			c2.drawImage(background.getCanvasElement(), 0, 0, (int) thx,
			        (int) thy);
		}
		c2.drawImage(foreground.getCanvasElement(), 0, 0, (int) thx,
		        (int) thy);

		return canv.toDataUrl();
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
			bgImage = null;
			bgGraphics = null;
		}

		updateBackgroundImage();
	}

	public void createImage() {
		bgImage = new GBufferedImageW(g2p.getOffsetWidth(),
				g2p.getOffsetHeight(),
				app == null ? 1 : app
						.getPixelRatio(),
				false);
		bgGraphics = bgImage.createGraphics();
	}

	@Override
	public double getMinSamplePoints() {
		return 40;
	}

	/*
	 * public double getMaxBendOfScreen() { return MAX_BEND_OFF_SCREEN; }*
	 * 
	 * public double getMaxBend() { return MAX_BEND; }
	 * 
	 * public int getMaxDefinedBisections() { return MAX_DEFINED_BISECTIONS; }
	 */

	@Override
	public double getMinPixelDistance() {
		return this.g2p == null || g2p.getScale() <= 1 ? 0.5 : 1;
	}

	/*
	 * public int getMaxZeroCount() { return MAX_ZERO_COUNT; }
	 */

	@Override
	public double getMaxPixelDistance() {
		return this.g2p == null || g2p.getScale() <= 1 ? 15 : 30;
	}



	/*
	 * public int getMaxProblemBisections() { return MAX_PROBLEM_BISECTIONS; }
	 */
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

	private void initBaseComponents(EuclidianPanelWAbstract euclidianViewPanel,
			EuclidianController euclidiancontroller, int newEvNo,
			EuclidianSettings settings) {

		final Canvas canvas = euclidianViewPanel.getCanvas();
		this.evNo = newEvNo;

		this.g2p = new GGraphics2DW(canvas);
		g2p.devicePixelRatio = app.getPixelRatio();
		g2p.setView(this);

		updateFonts();
		initView(true);
		attachView();

		((EuclidianControllerW) euclidiancontroller).setView(this);

		if (this.getViewID() != App.VIEW_TEXT_PREVIEW) {
			registerKeyHandlers(canvas);
			registerMouseTouchGestureHandlers(euclidianViewPanel,
			        (EuclidianControllerW) euclidiancontroller);
		}

		registerDragDropHandlers(euclidianViewPanel,(EuclidianControllerW) euclidiancontroller);

		updateFirstAndLast(true, true);

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

		addDummyDiv();
	}

	/**
	 * @param ev
	 * @param anyway
	 */
	static final public void updateFirstAndLast(EuclidianViewWInterface ev,
			boolean anyway) {
		ev.getCanvas().setTabIndex(GeoGebraFrameW.GRAPHICS_VIEW_TABINDEX);
		if (firstInstance == null) {
			firstInstance = ev;
		} else if (ev.getCanvas().isAttached()) {
			if (compareDocumentPosition(ev.getCanvas().getCanvasElement(),
					firstInstance.getCanvas().getCanvasElement())) {
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
					.getCanvas().getCanvasElement())) {
				firstInstance = ev;
			}
		}

		if (lastInstance == null) {
			lastInstance = ev;
		} else if (ev.getCanvas().isAttached()) {
			if (compareDocumentPosition(lastInstance.getCanvas()
					.getCanvasElement(), ev.getCanvas().getCanvasElement())) {
				lastInstance = ev;
			}
		} else if (anyway) {
			if (compareDocumentPosition(lastInstance.getCanvas()
					.getCanvasElement(),
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
				getCanvas().setTabIndex(
						GeoGebraFrameW.GRAPHICS_VIEW_TABINDEX - 1);
			}
		} else {
			// ?
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

		canvas.addKeyDownHandler(this.app.getGlobalKeyDispatcher());
		canvas.addKeyUpHandler(this.app.getGlobalKeyDispatcher());
		canvas.addKeyPressHandler(this.app.getGlobalKeyDispatcher());

	}

	private void registerMouseTouchGestureHandlers(
	        EuclidianPanelWAbstract euclidianViewPanel,
	        EuclidianControllerW euclidiancontroller) {
		Widget evPanel = euclidianViewPanel.getAbsolutePanel();
		evPanel.addDomHandler(euclidiancontroller, MouseWheelEvent.getType());
		if (!app.has(Feature.PEN_EVENTS)
				|| !Browser.supportsPointerEvents(true)) {
			evPanel.addDomHandler(euclidiancontroller,
					MouseMoveEvent.getType());
			evPanel.addDomHandler(euclidiancontroller,
					MouseOverEvent.getType());
			evPanel.addDomHandler(euclidiancontroller, MouseOutEvent.getType());
			evPanel.addDomHandler(euclidiancontroller, MouseUpEvent.getType());
			if (app.getLAF() == null || !app.getLAF().isSmart()) {
				evPanel.addDomHandler(euclidiancontroller,
						MouseDownEvent.getType());
			}
		}



		if (Browser.supportsPointerEvents(app.has(Feature.PEN_EVENTS))) {
			msZoomer = new MsZoomer((IsEuclidianController) euclidianController,
					euclidiancontroller.getOffsets());
			MsZoomer.attachTo(evPanel.getElement(), msZoomer,
					app.has(Feature.PEN_EVENTS));

			if (app.has(Feature.PEN_EVENTS)) {
				CancelEventTimer.killTouch(evPanel);
			}
			return;
		}

		if (app.getLAF() != null) {
			if (app.getLAF().registerHandlers(evPanel, euclidiancontroller)) {
				return;
			}
		}

		evPanel.addDomHandler(euclidiancontroller, TouchStartEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, TouchEndEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, TouchMoveEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, TouchCancelEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, GestureStartEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, GestureChangeEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, GestureEndEvent.getType());

	}

	private static void registerDragDropHandlers(
	        EuclidianPanelWAbstract euclidianViewPanel,
	        EuclidianControllerW euclidiancontroller) {
		Widget evPanel = euclidianViewPanel.getAbsolutePanel();
		evPanel.addDomHandler(euclidiancontroller, DropEvent.getType());
	}
	// STROKES
	final protected static GBasicStrokeW standardStroke = new GBasicStrokeW(
			1.0, GBasicStroke.CAP_ROUND, GBasicStroke.JOIN_ROUND);

	final protected static GBasicStrokeW selStroke = new GBasicStrokeW(
			1.0 + EuclidianStyleConstants.SELECTION_ADD,
	        GBasicStroke.CAP_ROUND, GBasicStroke.JOIN_ROUND);

	protected boolean unitAxesRatio;

	private Object preferredSize;

	private SimplePanel dummyDiv;

	static public GBasicStrokeW getDefaultStroke() {
		return standardStroke;
	}

	static public GBasicStrokeW getDefaultSelectionStroke() {
		return selStroke;
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
		setDefaultCursor();
	}

	@Override
	protected void setStyleBarMode(int mode) {
		if (hasStyleBar()) {
			getStyleBar().setMode(mode);
		}
	}

	private ImageElement getResetImage() {
		if (resetImage == null) {
			resetImage = this.app.getRefreshViewImage();
		}
		return resetImage;
	}

	private ImageElement getPlayImage(boolean highlight) {
		if (playImage == null) {
			playImage = this.app.getPlayImage();
			playImageHL = this.app.getPlayImageHover();
		}
		return highlight ? playImageHL : playImage;
	}

	private ImageElement getPauseImage(boolean highlight) {
		if (pauseImage == null) {
			pauseImage = this.app.getPauseImage();
			pauseImageHL = this.app.getPauseImageHover();
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
		g2p.getCanvas().getCanvasElement().focus();
		focusGained();
		return true;
	}

	public void focusLost() {
		if (isInFocus) {
			this.isInFocus = false;
			if (getCanvas() != null) {
				// this.app.focusLost(this, getCanvas().getElement());
			}
		}
	}

	public void focusGained() {
		if (!isInFocus) {
			this.isInFocus = true;
			if (getCanvas() != null) {
				this.app.focusGained(this, getCanvas().getElement());
			}
		}
	}

	@Override
	public boolean isInFocus() {
		return isInFocus;
	}

	public void setDefaultCursor() {
		setCursorClass("cursor_default");
	}

	private void setCursorClass(String className) {

		// IMPORTANT: do nothing if we already have the classname,
		// app.resetCursor is VERY expensive in IE
		if (!g2p.getCanvas().getElement().hasClassName(className)) {
			this.app.resetCursor();
			g2p.getCanvas().setStyleName("");
			g2p.getCanvas().addStyleName(className);
		}
	}

	public void setHitCursor() {
		setCursorClass("cursor_hit");
	}

	@Override
	protected EuclidianStyleBar newEuclidianStyleBar() {
		if (app.getGuiManager() == null) {
			return null;
		}
		return app.getGuiManager().newEuclidianStylebar(this, this.getViewID());
	}

	@Override
	protected void addDynamicStylebarToEV(EuclidianStyleBar dynamicStylebar) {
		app.getGuiManager().addStylebar(this, dynamicStylebar);
	}

	@Override
	protected EuclidianStyleBar newDynamicStyleBar() {
		return app.getGuiManager().newDynamicStylebar(this);
	}

	@Override
	final protected void drawAnimationButtons(final GGraphics2D g2) {

		// draw button in focused EV only
		if (!drawPlayButtonInThisView() || app.isScreenshotGenerator()) {
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
		if (this.app.useTransparentCursorWhenDragging()) {
			setCursorClass("cursor_transparent");
		} else {
			setCursorClass("cursor_drag");
		}
	}

	@Override
	public void setToolTipText(String plainTooltip) {
		ToolTipManagerW.sharedInstance().showToolTip(plainTooltip);
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

	@Override
	public boolean hasFocus() {
		// changed to return true, otherwise Arrow keys don't work to pan the
		// view, see GlobalKeyDispatcher
		// return isInFocus;
		return true;
	}

	@Override
	public void add(GBox box) {
		if (EVPanel != null) {
			EVPanel.getAbsolutePanel().add(GBoxW.getImpl(box),
			        (int) box.getBounds().getX(), (int) box.getBounds().getY());
		}
	}

	@Override
	public void remove(GBox box) {
		if (EVPanel != null) {
			EVPanel.getAbsolutePanel().remove(GBoxW.getImpl(box));
		}
	}

	@Override
	protected void drawResetIcon(GGraphics2D g) {
		int w = getWidth();
		((GGraphics2DW) g).getCanvas().getContext2d()
		        .drawImage(getResetImage(), w - 24, 2);
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
	public void resetMsZoomer() {
		if (msZoomer != null) {
			msZoomer.reset();
		}
	}

	@Override
	public Canvas getCanvas() {
		return g2p.getCanvas();
	}

	@Override
	public GGraphics2DW getG2P() {
		return g2p;
	}

	@Override
	public void setPixelRatio(double pixelRatio) {
		if (Kernel.isEqual(g2p.devicePixelRatio, pixelRatio)) {
			return;
		}
		int realWidth = g2p.getOffsetWidth();
		int realHeight = g2p.getOffsetHeight();
		g2p.devicePixelRatio = pixelRatio;
		if (realHeight > 0 && realWidth > 0) {
			g2p.setCoordinateSpaceSize(realWidth, realHeight);
			this.createImage();
			this.updateBackground();
			repaint();
		}
	}

	@Override
	public void setAltText() {
		GeoElement alt = app.getKernel().lookupLabel("altText" + evNo);
		if (alt == null) {
			alt = app.getKernel().lookupLabel("altText");
		}
		if (alt instanceof GeoText) {
			String altStr = ((GeoText) alt).getTextString();
			if (g2p.setAltText(altStr)) {
				this.readText(altStr);
			}
		}

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
		Log.debug(getPrintingScale());
		final Image prevImg = new Image();
		String urlText = getExportImageDataUrl(getPrintingScale(), false);

		prevImg.getElement().setAttribute("src", urlText);
		prevImg.addStyleName("prevImg");
		pPanel.clear();
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				pPanel.add(prevImg);
				Window.print();

				// PrintPreviewW.removePrintPanelFromDOM();
				NodeList<com.google.gwt.dom.client.Element> pp = Dom
						.getElementsByClassName("printPanel");
				if (pp.getLength() != 0) {
					pp.getItem(0).removeFromParent();
				}
			}
		});
	}

	@Override
	public double getPixelRatio() {
		return app.getPixelRatio();
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
			setDefaultCursor();
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
			setEraserCursor();
			return;
		case PEN:
			setPenCursor();
			return;
		}

	}

	private void addDummyDiv() {
		dummyDiv = new SimplePanel();
		// can't be tabbed, but can get the focus programmatically
		dummyDiv.getElement().setTabIndex(-1);
		RootPanel.get().add(dummyDiv);
		dummyDiv.getElement().setAttribute("role", "status");
		dummyDiv.getElement().setAttribute("aria-live", "polite");
		dummyDiv.getElement().setAttribute("aria-atomic", "true");
		dummyDiv.getElement().setAttribute("aria-relevant", "text");

		dummyDiv.getElement().getStyle().setTop(-1000.0, Unit.PX);
		dummyDiv.getElement().getStyle().setPosition(Position.ABSOLUTE);
	}

	boolean temp = true;

	@Override
	public void readText(final String text) {
		Log.debug("read text: " + text);
		dummyDiv.getElement().setInnerText(text);
		dummyDiv.getElement().focus();
		g2p.getCanvas().getCanvasElement().focus();
	}

	public static native void printFocusedElement()/*-{
		$wnd.console.log($doc.activeElement);
	}-*/;

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

				EuclidianViewW.lastInstance.getCanvas().getElement().blur();
				return true;
			}
			return false;
		}
		EuclidianViewW.tabPressed = false;
		return true;
	}

	public static void selectNextGeoOnTab(EuclidianView view) {
		if (EuclidianViewW.tabPressed) {
			// if focus is moved here from another applet,
			// select the first GeoElement of this Graphics view
			EuclidianViewW.tabPressed = false;
			view.getApplication().getSelectionManager().selectNextGeo(view,
					true);

			// .setFirstGeoSelectedForPropertiesView(); might not be
			// perfect,
			// for that GeoElement might not be visible in all Graphics
			// views
		}

	}

	public static void resetTab() {
		tabPressed = false;
	}

}

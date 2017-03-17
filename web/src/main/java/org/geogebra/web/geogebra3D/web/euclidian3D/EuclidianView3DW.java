package org.geogebra.web.geogebra3D.web.euclidian3D;

import java.util.HashMap;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.MyZoomer;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.RendererWInterface;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.RendererWithImplW;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.euclidian.EuclidianPanelWAbstract;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.euclidian.IsEuclidianController;
import org.geogebra.web.html5.euclidian.MsZoomer;
import org.geogebra.web.html5.euclidian.MyEuclidianViewPanel;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.javax.swing.GBoxW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;
import org.geogebra.web.html5.main.TimerSystemW;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
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
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * 3D view
 * 
 * @author mathieu
 *
 */
public class EuclidianView3DW extends EuclidianView3D implements
        EuclidianViewWInterface {

	private EuclidianPanelWAbstract EVPanel;

	private boolean isInFocus = false;

	/** graphics */
	public GGraphics2DW g2p = null;

	private MsZoomer msZoomer;

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
		initBaseComponents(EVPanel, ec);

		// initView(true);

		getRenderer().init();
		ClickStartHandler.init(g2p.getCanvas(), new ClickStartHandler() {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				((AppW) getApplication()).closePopups();
			}
		});

	}



	private void initBaseComponents(EuclidianPanelWAbstract euclidianViewPanel,
	        EuclidianController euclidiancontroller) {

		Canvas canvas = euclidianViewPanel.getCanvas();
		setEvNo();

		this.g2p = new GGraphics2DW(canvas);
		g2p.setView(this);

		updateFonts();
		initView(true);
		attachView();

		((EuclidianController3DW) euclidiancontroller).setView(this);

		registerKeyHandlers(canvas);
		registerMouseTouchGestureHandlers(euclidianViewPanel,
		        (EuclidianController3DW) euclidiancontroller);

		updateFirstAndLast(true, true);

		canvas.addAttachHandler(new AttachEvent.Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent ae) {
				// see attach handler of EuclidianViewW
				updateFirstAndLast(ae.isAttached(), false);
			}
		});

		canvas.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent be) {
				focusLost();
				EuclidianViewW.cycle(EuclidianView3DW.this);
			}
		});

		canvas.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent fe) {
				focusGained();
				EuclidianViewW.selectNextGeoOnTab(EuclidianView3DW.this);
			}
		});

		EuclidianSettings es = this.app.getSettings().getEuclidian(3);
		settingsChanged(es);
		es.addListener(this);
	}

	@Override
	public void updateFirstAndLast(boolean attach, boolean anyway) {
		if (attach) {
			EuclidianViewW.updateFirstAndLast(this, anyway);
		} else {
			// ?
		}
	}

	private void setEvNo() {

		this.evNo = EVNO_3D;
	}

	@Override
	public void setEuclidianViewNo(int evNo) {
		this.evNo = evNo;
		// this.g2p.getCanvas().getElement().setId("View_"+App.VIEW_EUCLIDIAN3D);
	}

	private void registerKeyHandlers(Canvas canvas) {
		GlobalKeyDispatcherW gkd = ((AppW) this.app).getGlobalKeyDispatcher();
		canvas.addKeyDownHandler(gkd);
		canvas.addKeyUpHandler(gkd);
		canvas.addKeyPressHandler(gkd);

	}

	private void registerMouseTouchGestureHandlers(
	        EuclidianPanelWAbstract euclidianViewPanel,
	        EuclidianController3DW euclidiancontroller) {
		Widget evPanel = euclidianViewPanel.getAbsolutePanel();
		evPanel.addDomHandler(euclidiancontroller, MouseWheelEvent.getType());

		if (!Browser.supportsPointerEvents(app.has(Feature.PEN_EVENTS))
				|| !app.has(Feature.PEN_EVENTS)) {
			evPanel.addDomHandler(euclidiancontroller,
					MouseMoveEvent.getType());
			evPanel.addDomHandler(euclidiancontroller,
					MouseOverEvent.getType());
			evPanel.addDomHandler(euclidiancontroller, MouseOutEvent.getType());
			if (((AppW) app).getLAF() == null
					|| !((AppW) app).getLAF().isSmart()) {
				evPanel.addDomHandler(euclidiancontroller,
						MouseDownEvent.getType());
			}
			evPanel.addDomHandler(euclidiancontroller, MouseUpEvent.getType());
		}

		if (Browser.supportsPointerEvents(app.has(Feature.PEN_EVENTS))) {
			msZoomer = new MsZoomer((IsEuclidianController) euclidianController,
					euclidiancontroller.getOffsets());
			MsZoomer.attachTo(evPanel.getElement(), msZoomer,
					app.has(Feature.PEN_EVENTS));
			return;
		}
		evPanel.addDomHandler(euclidiancontroller, TouchStartEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, TouchEndEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, TouchMoveEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, TouchCancelEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, GestureStartEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, GestureChangeEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, GestureEndEvent.getType());

	}

	/**
	 * Callback for blur event
	 */
	public void focusLost() {
		if (isInFocus) {
			this.isInFocus = false;
			if (getCanvas() != null) {
				((AppW) this.app).focusLost(this, getCanvas().getElement());
			}
		}
	}

	/**
	 * Callback for focus event
	 */
	public void focusGained() {
		if (!isInFocus) {
			this.isInFocus = true;
			if (getCanvas() != null) {
				((AppW) this.app).focusGained(this, getCanvas().getElement());
			}
		}
	}

	@Override
	public boolean isInFocus() {
		return isInFocus;
	}

	/**
	 * @return panel component
	 */
	public Widget getComponent() {
		return EVPanel.getAbsolutePanel();
	}

	// //////////////////////////////////////////////////////////
	// MyEuclidianViewPanel
	// //////////////////////////////////////////////////////////

	/**
	 * @return EV panel
	 */
	protected MyEuclidianViewPanel newMyEuclidianViewPanel() {
		return new MyEuclidianViewPanel3D(this);
	}

	/**
	 * panel for 3D
	 * 
	 * @author mathieu
	 *
	 */
	private class MyEuclidianViewPanel3D extends MyEuclidianViewPanel implements
	        RequiresResize {

		private Renderer pRenderer;

		/**
		 * constructor
		 * 
		 * @param ev
		 *            euclidian view
		 */
		public MyEuclidianViewPanel3D(EuclidianView ev) {
			super(ev);
		}

		@Override
		protected void createCanvas() {
			pRenderer = getRenderer();
			canvas = (Canvas) pRenderer.getCanvas();
		}

		@Override
		public void onResize() {
			super.onResize();
			getEuclidianController().calculateEnvironment();
		}

	}


	private boolean readyToRender = false;

	/**
	 * tells the view that all is ready for GL rendering
	 */
	public void setReadyToRender() {
		readyToRender = true;
		repaintView();
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
		return (int) (this.g2p.getCoordinateSpaceWidth() / getPixelRatio());
	}

	/**
	 * Gets the coordinate space height of the &lt;canvas&gt;.
	 * 
	 * @return the logical height
	 */
	@Override
	public int getHeight() {
		return (int) (this.g2p.getCoordinateSpaceHeight() / getPixelRatio());
	}

	@Override
	public final boolean isShowing() {
		return g2p != null && g2p.getCanvas() != null
		        && g2p.getCanvas().isAttached() && g2p.getCanvas().isVisible();
	}

	@Override
	protected void createPanel() {
		EVPanel = newMyEuclidianViewPanel();

	}

	@Override
	protected Renderer createRenderer() {
			return new RendererWithImplW(this);

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
	protected void setHeight(int h) {
		// TODO: not clear what should we do

	}

	@Override
	protected void setWidth(int h) {
		// TODO: not clear what should we do

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
		g2p.getCanvas().getCanvasElement().focus();
		focusGained();
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
		if (((AppW) app).getArticleElement().disableHiRes3D()) {
			return 1;
		}
		return ((AppW) app).getPixelRatio();
	}

	@Override
	protected MyZoomer newZoomer() {
		// TODO Auto-generated method stub
		return null;
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

	private void setCursorClass(String className) {
		// IMPORTANT: do nothing if we already have the classname,
		// app.resetCursor is VERY expensive in IE
		Canvas canvas = (Canvas) this.getRenderer().getCanvas();
		if (!canvas.getElement().hasClassName(className)) {
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
	public Canvas getCanvas() {
		return g2p.getCanvas();
	}

	/**
	 * the file has been set by the App
	 * 
	 * @param file
	 *            file
	 */
	public void setCurrentFile(HashMap<String, String> file) {
		// used only when no webGL

	}

	private AnimationScheduler.AnimationCallback repaintCallback = new AnimationScheduler.AnimationCallback() {
		@Override
		public void execute(double ts) {
			doRepaint2();
		}
	};

	private AnimationScheduler repaintScheduler = AnimationScheduler.get();

	private long lastRepaint;

	/**
	 * This doRepaint method should be used instead of repaintView in cases when
	 * the repaint should be done immediately
	 */
	public final void doRepaint2() {

		long time = System.currentTimeMillis();
		// ((DrawEquationWeb) this.app.getDrawEquation()).clearLaTeXes(this);
		this.updateBackgroundIfNecessary();

		// paint(this.g2p);
		if (readyToRender) {
			renderer.drawScene();
		}

		getEuclidianController().setCollectedRepaints(false);
		lastRepaint = System.currentTimeMillis() - time;
		GeoGebraProfiler.addRepaint(lastRepaint);

		if (waitForNewRepaint) {
			kernel.notifyControllersMoveIfWaiting();
			waitForRepaint = TimerSystemW.EUCLIDIAN_LOOPS;
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
		// if (readyToRender){
		// renderer.drawScene();
		// }

		if (getEuclidianController().isCollectingRepaints()) {
			getEuclidianController().setCollectedRepaints(true);
			return;
		}

		getApplication().ensureTimerRunning();
		if (waitForRepaint == TimerSystemW.SLEEPING_FLAG) {
			waitForRepaint = TimerSystemW.EUCLIDIAN_LOOPS;
		}
	}

	@Override
	final public void waitForNewRepaint() {
		waitForNewRepaint = true;
	}

	private int waitForRepaint = TimerSystemW.SLEEPING_FLAG;
	private boolean waitForNewRepaint = false;

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
			}
			return true;
		}

		waitForRepaint--;
		return true;
	}

	@Override
	public void exportPaintPre(GGraphics2D g2d, double scale,
	        boolean transparency) {
		// TODO Auto-generated method stub

	}

	@Override
	public GGraphics2DW getG2P() {
		return g2p;
	}

	@Override
	public void resetMsZoomer() {
		// TODO Auto-generated method stub
	}

	@Override
	public String getExportImageDataUrl(double scale, boolean b) {
		((RendererWInterface) this.renderer).setBuffering(true);
		this.doRepaint2();

		String url = ((Canvas) renderer.getCanvas()).toDataUrl();
		((RendererWInterface) this.renderer).setBuffering(false);
		return url;
	}

	@Override
	public String getCanvasBase64WithTypeString() {
		((RendererWInterface) this.renderer).setBuffering(true);
		this.doRepaint2();
		String ret = EuclidianViewW.getCanvasBase64WithTypeString(
				this.getWidth(), getHeight(), null,
				(Canvas) renderer.getCanvas());
		((RendererWInterface) this.renderer).setBuffering(false);
		return ret;
		
	}

	@Override
	public void setPixelRatio(double pixelRatio) {
		if (((AppW) app).getArticleElement().disableHiRes3D()) {
			return;
		}
		if (Kernel.isEqual(g2p.devicePixelRatio, pixelRatio)) {
			return;
		}
		int realWidth = g2p.getOffsetWidth();
		int realHeight = g2p.getOffsetHeight();
		g2p.devicePixelRatio = pixelRatio;
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
			g2p.getCanvas().getElement().getParentElement().getStyle()
					.setWidth(width, Style.Unit.PX);
			g2p.getCanvas().getElement().getParentElement().getStyle()
					.setHeight(height, Style.Unit.PX);
			getEuclidianController().calculateEnvironment();
		} catch (Exception exc) {
			Log.debug("Problem with the parent element of the canvas");
		}
	}

	@Override
	public void setAltText() {
		GeoElement alt = app.getKernel().lookupLabel("altText3D1");
		if (alt == null) {
			alt = app.getKernel().lookupLabel("altText3D");
		}
		if (alt == null) {
			alt = app.getKernel().lookupLabel("altText");
		}
		if (alt instanceof GeoText) {
			String altStr = ((GeoText) alt).getTextString();
			if (renderer != null && renderer.getCanvas() != null) {
				((Canvas) renderer.getCanvas()).getElement()
						.setInnerText(altStr);

			} else {
				g2p.setAltText(altStr);
			}

		}

	}

	@Override
	public void readText(String text) {
		//String oldAltText = g2p.getAltText();
		//g2p.setAltText(text);
		//RootPanel.getBodyElement().focus();
		//this.requestFocus();

	}

	@Override
	protected void drawBackgroundImage(GGraphics2D g2d) {
		// nothing to do here
	}

	@Override
	protected EuclidianStyleBar newDynamicStyleBar() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void addDynamicStylebarToEV(EuclidianStyleBar dynamicStylebar) {
		// TODO Auto-generated method stub

	}

}

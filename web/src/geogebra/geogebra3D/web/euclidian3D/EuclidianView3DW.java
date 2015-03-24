package geogebra.geogebra3D.web.euclidian3D;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianStyleBar;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.MyZoomer;
import geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.javax.swing.GBox;
import geogebra.common.main.App;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.common.util.debug.GeoGebraProfiler;
import geogebra.geogebra3D.web.euclidian3D.openGL.RendererW;
import geogebra.geogebra3D.web.gui.layout.panels.EuclidianDockPanel3DW;
import geogebra.html5.Browser;
import geogebra.html5.euclidian.EuclidianPanelWAbstract;
import geogebra.html5.euclidian.EuclidianViewW;
import geogebra.html5.euclidian.EuclidianViewWInterface;
import geogebra.html5.euclidian.IsEuclidianController;
import geogebra.html5.euclidian.MsZoomer;
import geogebra.html5.euclidian.MyEuclidianViewPanel;
import geogebra.html5.javax.swing.GBoxW;
import geogebra.html5.main.AppW;
import geogebra.html5.main.TimerSystemW;

import java.util.HashMap;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.canvas.client.Canvas;
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

	protected EuclidianPanelWAbstract EVPanel;

	private AppW app = (AppW) super.app;
	public boolean isInFocus = false;

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

	}

	public geogebra.html5.awt.GGraphics2DW g2p = null;

	private MsZoomer msZoomer;

	private void initBaseComponents(EuclidianPanelWAbstract euclidianViewPanel,
	        EuclidianController euclidiancontroller) {

		Canvas canvas = euclidianViewPanel.getCanvas();
		setEvNo(canvas);

		this.g2p = new geogebra.html5.awt.GGraphics2DW(canvas);
		g2p.setView(this);

		updateFonts();
		initView(true);
		attachView();

		((EuclidianController3DW) euclidiancontroller).setView(this);

		registerKeyHandlers(canvas);
		registerMouseTouchGestureHandlers(euclidianViewPanel,
		        (EuclidianController3DW) euclidiancontroller);

		// Canvas should have a tab index to capture key events in Internet
		// Explorer
		canvas.setTabIndex(10000);

		canvas.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent be) {
				focusLost();
			}
		});

		canvas.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent fe) {
				focusGained();
			}
		});

		EuclidianSettings es = this.app.getSettings().getEuclidian(3);
		settingsChanged(es);
		es.addListener(this);
	}

	private void setEvNo(Canvas canvas) {

		canvas.getElement().setId("View_" + App.VIEW_EUCLIDIAN3D);
		this.evNo = EVNO_3D;
	}

	@Override
	public void setEuclidianViewNo(int evNo) {
		this.evNo = evNo;
		// this.g2p.getCanvas().getElement().setId("View_"+App.VIEW_EUCLIDIAN3D);
	}

	private void registerKeyHandlers(Canvas canvas) {

		canvas.addKeyDownHandler(this.app.getGlobalKeyDispatcher());
		canvas.addKeyUpHandler(this.app.getGlobalKeyDispatcher());
		canvas.addKeyPressHandler(this.app.getGlobalKeyDispatcher());

	}

	private void registerMouseTouchGestureHandlers(
	        EuclidianPanelWAbstract euclidianViewPanel,
	        EuclidianController3DW euclidiancontroller) {
		Widget evPanel = euclidianViewPanel.getAbsolutePanel();
		evPanel.addDomHandler(euclidiancontroller, MouseWheelEvent.getType());

		evPanel.addDomHandler(euclidiancontroller, MouseMoveEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, MouseOverEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, MouseOutEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, MouseDownEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, MouseUpEvent.getType());

		if (Browser.supportsPointerEvents()) {
			msZoomer = new MsZoomer((IsEuclidianController) euclidianController);
			MsZoomer.attachTo(evPanel.getElement(), msZoomer);
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

	public void focusLost() {
		if (isInFocus) {
			this.isInFocus = false;
			this.app.focusLost(this);
		}
	}

	public void focusGained() {
		if (!isInFocus && !App.isFullAppGui()) {
			this.isInFocus = true;
			this.app.focusGained(this);
		}
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
	 * current dockPanel (if exists)
	 */
	EuclidianDockPanel3DW dockPanel = null;

	/**
	 * 
	 * @param dockPanel
	 *            current dockPanel (if exists)
	 */
	public void setDockPanel(EuclidianDockPanel3DW dockPanel) {
		this.dockPanel = dockPanel;
	}

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

		private RendererW renderer;

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
			renderer = (RendererW) getRenderer();
			canvas = renderer.getGLCanvas();
		}

		@Override
		public void onResize() {
			super.onResize();
			if (dockPanel != null) {
				// making this deferred helps the Win8 app
				app.getGuiManager().invokeLater(new Runnable() {

					@Override
					public void run() {
						int w = dockPanel.getComponentInteriorWidth();
						int h = dockPanel.getComponentInteriorHeight();

						// if non positive values, use frame bounds (e.g. when
						// set
						// perspective)
						if (w <= 0 || h <= 0) {
							// GRectangle r = dockPanel.getFrameBounds();
							w = dockPanel.getEmbeddedDimWidth();
							h = dockPanel.getEmbeddedDimHeight();
						}

						// App.debug("------------------ resize -----------------------");
						// App.debug("w = "+w+" , h = "+h);
						renderer.setView(0, 0, w, h);
						getEuclidianController().calculateEnvironment();
					}
				});
			}
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
	public void repaintView() {

		repaint();
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
		// TODO Auto-generated method stub

	}

	@Override
	public int getWidth() {
		return this.g2p.getCoordinateSpaceWidth();
	}

	@Override
	public int getHeight() {
		return this.g2p.getCoordinateSpaceHeight();
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
		return new RendererW(this);
	}

	@Override
	protected boolean getShiftDown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void setDefault2DCursor() {
		// TODO Auto-generated method stub

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
		if (Browser.isIE()) {
			g2p.getCanvas().setTabIndex(10000);
		}
		g2p.getCanvas().getCanvasElement().focus();
		focusGained();
		return true;
	}

	@Override
	public void paintBackground(GGraphics2D g2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawActionObjects(GGraphics2D g) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setAntialiasing(GGraphics2D g2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBackground(GColor bgColor) {
		if (bgColor != null) {
			this.bgColor = bgColor;
			if (renderer != null) {
				renderer.setWaitForUpdateClearColor();
			}
		}
	}

	@Override
	public void setPreferredSize(GDimension preferredSize) {
		if (g2p != null && g2p.getContext() != null) {
			g2p.setPreferredSize(preferredSize);
			updateSize();
			setReIniting(false);
		}
	}

	@Override
	protected MyZoomer newZoomer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(GBox box) {
		if (EVPanel != null)
			EVPanel.getAbsolutePanel().add(GBoxW.getImpl(box),
			        (int) box.getBounds().getX(), (int) box.getBounds().getY());
	}

	@Override
	public void remove(GBox box) {
		if (EVPanel != null)
			EVPanel.getAbsolutePanel().remove(GBoxW.getImpl(box));
	}

	@Override
	public void setTransparentCursor() {
		// TODO Auto-generated method stub

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

		if (waitForRepaint == TimerSystemW.SLEEPING_FLAG) {
			getApplication().ensureTimerRunning();
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

	public geogebra.html5.awt.GGraphics2DW getG2P() {
		return g2p;
	}

	public void resetMsZoomer() {
		// TODO Auto-generated method stub
	}

	@Override
	public String getExportImageDataUrl(double scale, boolean b) {
		((RendererW) this.renderer).setBuffering(true);
		this.doRepaint2();

		String url = ((RendererW) renderer).getGLCanvas().toDataUrl();
		((RendererW) this.renderer).setBuffering(false);
		return url;
	}

	@Override
	public String getCanvasBase64WithTypeString() {
		((RendererW) this.renderer).setBuffering(true);
		this.doRepaint2();
		String ret = EuclidianViewW.getCanvasBase64WithTypeString(
		        this.getWidth(),
		        getHeight(), null, ((RendererW) renderer).getGLCanvas());
		((RendererW) this.renderer).setBuffering(false);
		return ret;
		
	}

}

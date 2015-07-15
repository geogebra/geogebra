package org.geogebra.web.geogebra3D.web.euclidian3D;

import java.util.HashMap;

import org.geogebra.common.awt.GColor;
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
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.RendererShadersElementsW;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.RendererW;
import org.geogebra.web.geogebra3D.web.gui.layout.panels.EuclidianDockPanel3DW;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianPanelWAbstract;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.euclidian.IsEuclidianController;
import org.geogebra.web.html5.euclidian.MsZoomer;
import org.geogebra.web.html5.euclidian.MyEuclidianViewPanel;
import org.geogebra.web.html5.gui.GeoGebraFrame;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.javax.swing.GBoxW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;
import org.geogebra.web.html5.main.TimerSystemW;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler;
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
import com.google.gwt.user.client.Timer;
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

	public int thisTabIndex = GeoGebraFrame.GRAPHICS_VIEW_TABINDEX;

	protected EuclidianPanelWAbstract EVPanel;

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
		ClickStartHandler.init(g2p.getCanvas(), new ClickStartHandler() {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				((AppW) app).closePopups();
			}
		});

	}

	public org.geogebra.web.html5.awt.GGraphics2DW g2p = null;

	private MsZoomer msZoomer;

	private void initBaseComponents(EuclidianPanelWAbstract euclidianViewPanel,
	        EuclidianController euclidiancontroller) {

		Canvas canvas = euclidianViewPanel.getCanvas();
		setEvNo(canvas);

		this.g2p = new org.geogebra.web.html5.awt.GGraphics2DW(canvas);
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
			public void onAttachOrDetach(AttachEvent ae) {
				// see attach handler of EuclidianViewW
				updateFirstAndLast(ae.isAttached(), false);
			}
		});

		canvas.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent be) {
				focusLost();
				if ((EuclidianView3DW.this == EuclidianViewW.lastInstance)
						&& EuclidianViewW.tabPressed) {
					// if this is the last to blur, and tabPressed
					// is true, i.e. want to select another applet,
					// let's go back to the first one!
					// but how?? maybe better than jQuery:
					Scheduler.get().scheduleDeferred(
							new Scheduler.ScheduledCommand() {
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
									if (App.isFullAppGui()) {
										EuclidianViewW.firstInstance
												.requestFocus();
									} else {
										// but this only seems to be important
										// in the applet case
										Timer tim = new Timer() {
											public void run() {
												EuclidianViewW.firstInstance
														.requestFocus();
											}
										};
										tim.schedule(1000);
									}
								}
							});
				}
			}
		});

		canvas.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent fe) {
				focusGained();
				if (EuclidianViewW.tabPressed) {
					// if focus is moved here from another applet,
					// select the first GeoElement of this Graphics view
					EuclidianViewW.tabPressed = false;
					app.getSelectionManager().selectNextGeo(
							EuclidianView3DW.this, true);

					// .setFirstGeoSelectedForPropertiesView(); might not be
					// perfect,
					// for that GeoElement might not be visible in all Graphics
					// views
				}
			}
		});

		EuclidianSettings es = this.app.getSettings().getEuclidian(3);
		settingsChanged(es);
		es.addListener(this);
	}

	@Override
	public void updateFirstAndLast(boolean attach, boolean anyway) {
		if (attach) {
			getCanvas().setTabIndex(GeoGebraFrame.GRAPHICS_VIEW_TABINDEX);

			if (EuclidianViewW.firstInstance == null) {
				EuclidianViewW.firstInstance = this;
			} else if (getCanvas().isAttached()) {
				if (EuclidianViewW.compareDocumentPosition(getCanvas()
						.getCanvasElement(), EuclidianViewW.firstInstance
						.getCanvas().getCanvasElement())) {
					EuclidianViewW.firstInstance = this;
				}
			} else if (anyway) {
				if (EuclidianViewW.compareDocumentPosition(((AppW) app)
						.getFrameElement(), EuclidianViewW.firstInstance
						.getCanvas().getCanvasElement())) {
					EuclidianViewW.firstInstance = this;
				}
			}

			if (EuclidianViewW.lastInstance == null) {
				EuclidianViewW.lastInstance = this;
			} else if (getCanvas().isAttached()) {
				if (EuclidianViewW.compareDocumentPosition(
						EuclidianViewW.lastInstance.getCanvas()
								.getCanvasElement(), getCanvas()
								.getCanvasElement())) {
					EuclidianViewW.lastInstance = this;
				}
			} else if (anyway) {
				if (EuclidianViewW.compareDocumentPosition(
						EuclidianViewW.lastInstance.getCanvas()
								.getCanvasElement(), ((AppW) app)
								.getFrameElement())) {
					EuclidianViewW.lastInstance = this;
				}
			}
		} else {
			// TODO: shall we unset tabindex?
			if (EuclidianViewW.firstInstance == this) {
				// note that we shall set it to another EV, probably
				// TODO: how to do it?
				// now setting it to null, because compareDocumentPosition
				// will not work anyway...
				// EuclidianViewW.firstInstance = null;
			}
			if (EuclidianViewW.lastInstance == this) {
				// note that we shall set it to another EV, probably
				// TODO: how to do it?
				// EuclidianViewW.lastInstance = null;
			}
		}
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
			if (getCanvas() != null) {
				((AppW) this.app).focusLost(this, getCanvas().getElement());
			}
		}
	}

	public void focusGained() {
		if (!isInFocus && !App.isFullAppGui()) {
			this.isInFocus = true;
			if (getCanvas() != null) {
				((AppW) this.app).focusGained(this, getCanvas().getElement());
			}
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
						renderer.setPixelRatio(((AppW) app).getPixelRatio());
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
		// this may be really necessary preventing a tabbing away issue
		// but the reasons of it are not well understood #5158
		// after better understanding, this can probably be merged
		// with the following method (requestFocusInWindow()):
		requestFocusInWindow();
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
		if (app.has(Feature.GL_ELEMENTS)) {
			return new RendererShadersElementsW(this);
		}
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

	public org.geogebra.web.html5.awt.GGraphics2DW getG2P() {
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

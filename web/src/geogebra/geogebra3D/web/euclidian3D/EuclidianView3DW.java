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
import geogebra.geogebra3D.web.euclidian3D.openGL.RendererW;
import geogebra.geogebra3D.web.euclidian3D.openGL.RendererWebGL;
import geogebra.geogebra3D.web.gui.layout.panels.EuclidianDockPanel3DW;
import geogebra.web.euclidian.EuclidianPanelWAbstract;
import geogebra.web.euclidian.MyEuclidianViewPanel;
import geogebra.web.main.AppW;

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
 * @author mathieu
 *
 */
public class EuclidianView3DW extends EuclidianView3D {
	
	protected EuclidianPanelWAbstract EVPanel;
	
	private AppW app = (AppW) super.app;
	public boolean isInFocus = false;

	/**
	 * constructor
	 * @param ec euclidian controller
	 * @param settings euclidian settings
	 */
	public EuclidianView3DW(EuclidianController3D ec, EuclidianSettings settings) {
	    super(ec, settings);
	    
	    initBaseComponents(EVPanel, ec);
	    
    }
	
	
	public geogebra.html5.awt.GGraphics2DW g2p = null;

	private void initBaseComponents(EuclidianPanelWAbstract euclidianViewPanel,
            EuclidianController euclidiancontroller) {
		
	    Canvas canvas = euclidianViewPanel.getCanvas();
		setEvNo(canvas);
	 
		this.g2p = new geogebra.html5.awt.GGraphics2DW(canvas);	
		g2p.setView(this);

		updateFonts();
		initView(true);
		attachView();
	
		((EuclidianController3DW)euclidiancontroller).setView(this);
		

		if(this.getViewID() != App.VIEW_TEXT_PREVIEW){
			registerKeyHandlers(canvas);
			registerMouseTouchGestureHandlers(euclidianViewPanel, (EuclidianController3DW) euclidiancontroller);
		}
		
		canvas.addBlurHandler(new BlurHandler() {
			public void onBlur(BlurEvent be) {
				focusLost();
			}
		});
		
		canvas.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent fe) {
				focusGained();
			}
		});
		
		/*
		if ((evNo == 1) || (evNo == 2)) {
			EuclidianSettings es = this.app.getSettings().getEuclidian(evNo);
			settingsChanged(es);
			es.addListener(this);
		}
		*/
    }
	
	
	private void setEvNo( Canvas canvas) {

		canvas.getElement().setId("View_"+ App.VIEW_EUCLIDIAN3D);
		this.evNo = 3;
	}
	
	private void registerKeyHandlers(Canvas canvas){
		
		canvas.addKeyDownHandler(this.app.getGlobalKeyDispatcher());
		canvas.addKeyUpHandler(this.app.getGlobalKeyDispatcher());
		canvas.addKeyPressHandler(this.app.getGlobalKeyDispatcher());
		
	}
	
	

	private void registerMouseTouchGestureHandlers(EuclidianPanelWAbstract euclidianViewPanel, EuclidianController3DW euclidiancontroller){
		Widget evPanel = euclidianViewPanel.getAbsolutePanel();
		evPanel.addDomHandler(euclidiancontroller, MouseWheelEvent.getType());
		
		evPanel.addDomHandler(euclidiancontroller, MouseMoveEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, MouseOverEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, MouseOutEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, MouseDownEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, MouseUpEvent.getType());
		
		/*
		if(Browser.supportsPointerEvents()){
			msZoomer = new MsZoomer((IsEuclidianController) euclidianController);
			MsZoomer.attachTo(evPanel.getElement(),msZoomer);
			return;
		}
		*/
		
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
			this.app.focusLost();
		}
	}

	public void focusGained() {
		if (!isInFocus && !App.isFullAppGui()) {
			this.isInFocus = true;
			this.app.focusGained();
		}
	}

	
	/**
	 * @return panel component
	 */
	public Widget getComponent() {
	    return EVPanel.getAbsolutePanel();
    }
	
	
	
	
	
	
	
	////////////////////////////////////////////////////////////
	// MyEuclidianViewPanel
	////////////////////////////////////////////////////////////
	
	/**
	 * current dockPanel (if exists)
	 */
	EuclidianDockPanel3DW dockPanel = null;
	
	/**
	 * 
	 * @param dockPanel current dockPanel (if exists)
	 */
	public void setDockPanel(EuclidianDockPanel3DW dockPanel){
		this.dockPanel = dockPanel;
	}
	

	
    protected MyEuclidianViewPanel newMyEuclidianViewPanel(){
		return new MyEuclidianViewPanel3D(this);
	}
	
	/**
	 * panel for 3D
	 * @author mathieu
	 *
	 */
	private class MyEuclidianViewPanel3D extends MyEuclidianViewPanel implements RequiresResize {
		
		private RendererWebGL renderer;
		
		/**
		 * constructor
		 * @param ev euclidian view
		 */
		public MyEuclidianViewPanel3D(EuclidianView ev) {
	        super(ev);
        }
		
		@Override
        protected void createCanvas(){
			renderer = new RendererWebGL();
			canvas = renderer.getGLCanvas();
		}
		
		@Override
		public void onResize() {
			super.onResize();
			if (dockPanel != null){
				int w = dockPanel.getComponentInteriorWidth();
				int h = dockPanel.getComponentInteriorHeight();

				//App.debug("------------------ resize -----------------------");
				//App.debug("w = "+w+" , h = "+h);
				renderer.setDimension(w, h);
				getEuclidianController().calculateEnvironment();
			}
		}
		
	}

	public void repaint() {
	    // TODO Auto-generated method stub
	    
    }



	public GColor getBackgroundCommon() {
	    // TODO Auto-generated method stub
	    return null;
    }



	public void setToolTipText(String plainTooltip) {
	    // TODO Auto-generated method stub
	    
    }



	public boolean hasFocus() {
	    // TODO Auto-generated method stub
	    return false;
    }



	public void requestFocus() {
	    // TODO Auto-generated method stub
	    
    }



	public int getWidth() {
	    // TODO Auto-generated method stub
	    return 0;
    }



	public int getHeight() {
	    // TODO Auto-generated method stub
	    return 0;
    }



	public boolean isShowing() {
	    // TODO Auto-generated method stub
	    return false;
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
	    // TODO Auto-generated method stub
	    
    }



	@Override
    protected void setWidth(int h) {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    protected void setStyleBarMode(int mode) {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    protected void updateSizeKeepDrawables() {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    public boolean requestFocusInWindow() {
	    // TODO Auto-generated method stub
	    return false;
    }



	@Override
    public void paintBackground(GGraphics2D g2) {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    protected void drawActionObjects(GGraphics2D g) {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    protected void setAntialiasing(GGraphics2D g2) {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    public void setBackground(GColor bgColor) {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    public void setPreferredSize(GDimension preferredSize) {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    protected MyZoomer newZoomer() {
	    // TODO Auto-generated method stub
	    return null;
    }



	@Override
    public void add(GBox box) {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    public void remove(GBox box) {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    public void setTransparentCursor() {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    protected EuclidianStyleBar newEuclidianStyleBar() {
	    // TODO Auto-generated method stub
	    return null;
    }

	
}

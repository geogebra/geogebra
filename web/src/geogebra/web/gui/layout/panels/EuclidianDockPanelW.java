package geogebra.web.gui.layout.panels;

import geogebra.common.euclidian.EuclidianStyleBar;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.main.App;
import geogebra.web.gui.view.consprotocol.ConstructionProtocolNavigationW;
import geogebra.web.main.AppW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class EuclidianDockPanelW extends EuclidianDockPanelWAbstract {

	EuclidianStyleBar espanel;
	EuclidianPanel euclidianpanel;

	Canvas eview1 = null;// static foreground

	private ConstructionProtocolNavigationW consProtNav;

	/**
	 * This constructor is used by the Application
	 * and by the other constructor
	 * 
	 * @param stylebar (is there stylebar?)
	 */
	public EuclidianDockPanelW(boolean stylebar) {
		super(
				App.VIEW_EUCLIDIAN,	// view id 
				"DrawingPad", 				// view title
				null,						// toolbar string
				stylebar,					// style bar?
				5,							// menu order
				'1' // ctrl-shift-1
			);
		
		//TODO: temporary fix to make applets work until
		// dockpanels works for applets
		
		if(stylebar){
			component = loadComponent();
		}else{
			component = loadComponent();
			buildDockPanel();
		}
	}
	
	/**
	 * This constructor is used by the applet
	 * @param application
	 * @param stylebar
	 */
	public EuclidianDockPanelW(AppW application, boolean stylebar) {
		this(stylebar);
		app = application;

		// GuiManager can be null at the startup of the application,
		// but then the addNavigationBar method will be called explicitly.
		// By the way, this method is only called from AppWapplet,
		// so this will be actually null here.
		if (app.getGuiManager() != null)
			addNavigationBar();
	}

	
	@Override
	protected Widget loadComponent() {
		if (euclidianpanel == null) {
			euclidianpanel = new EuclidianPanel(this);
			eview1 = Canvas.createIfSupported();
			eview1.getElement().getStyle().setPosition(Style.Position.RELATIVE);
			eview1.getElement().getStyle().setZIndex(0);
			euclidianpanel.getAbsolutePanel().add(eview1);
		}

		return euclidianpanel;
	}
	
	public void addNavigationBar(){
		consProtNav = (ConstructionProtocolNavigationW)(app.getGuiManager()
		        .getConstructionProtocolNavigation());
		consProtNav.getImpl().addStyleName("consProtNav");
		euclidianpanel.add(consProtNav.getImpl()); // may be invisible, but made visible later		
		updateNavigationBar();
	}
	
	public void updateNavigationBar(){
//		ConstructionProtocolSettings cps = app.getSettings()
//		        .getConstructionProtocol();
//		((ConstructionProtocolNavigationW) consProtNav).settingsChanged(cps);
//		cps.addListener((ConstructionProtocolNavigation)consProtNav);

		
		if (app.getShowCPNavNeedsUpdate()) {
			app.setShowConstructionProtocolNavigation(app
			        .showConsProtNavigation());
		}

		consProtNav.update();
		consProtNav.setVisible(app.showConsProtNavigation());
	}
	
	class EuclidianPanel extends FlowPanel implements RequiresResize {

		EuclidianDockPanelW dockPanel;
		AbsolutePanel absoluteEuclidianPanel;

		int oldHeight = 0;
		int oldWidth = 0;
		
		public EuclidianPanel(EuclidianDockPanelW dockPanel) {
			super();
			this.dockPanel = dockPanel;
			add(absoluteEuclidianPanel = new AbsolutePanel());
			absoluteEuclidianPanel.addStyleName("EuclidianPanel");
		}

		public void onResize() {
		
			if (app != null) {

				int h = dockPanel.getComponentInteriorHeight();
				int w = dockPanel.getComponentInteriorWidth();

				// TODO handle this better?
				// exit if new size cannot be determined
				if (h <= 0 || w <= 0) {
					return;
				}
				App.debug("h: " + h + "oh: " + oldHeight +", w:" + w + ", ow: " + oldWidth);
				if (h != oldHeight || w != oldWidth) {
					app.ggwGraphicsViewDimChanged(w, h);
					oldHeight = h;
					oldWidth = w;
					App.debug("changed h: " + h + "oh: " + oldHeight +", w:" + w + ", ow: " + oldWidth);
				}
			}
		}

		public void add(Widget w, int x, int y) {
	        absoluteEuclidianPanel.add(w,x,y);
        }

		public boolean remove(Widget w) {
			return absoluteEuclidianPanel.remove(w);
        }

		public AbsolutePanel getAbsolutePanel() {
			return absoluteEuclidianPanel;
        }
	}

	public void reset() {
		if (euclidianpanel != null) {
			euclidianpanel.oldWidth = 0;
			euclidianpanel.oldHeight = 0;
		}
	}

	@Override
	protected Widget loadStyleBar() {

		if (espanel == null) {
			espanel = app.getEuclidianView1().getStyleBar();
		}

		return (Widget) espanel;
	}

	public Canvas getCanvas() {
	    return eview1;
    }

	public Panel getEuclidianPanel() {
	    return euclidianpanel;
    }

	public void onResize() {
		super.onResize();
    }

	public void add(Widget w, int x, int y) {
	    euclidianpanel.add(w,x,y);
    }

	public void remove(Widget w) {
		euclidianpanel.remove(w);
    }

	public EuclidianDockPanelW getEuclidianView1Wrapper() {
		return this;
	}

	public AbsolutePanel getAbsolutePanel() {
		return euclidianpanel.getAbsolutePanel();
	}

	public EuclidianView getEuclidianView() {
		if (app != null)
			return app.getEuclidianView1();
		return null;
	}

	@Override
    public void showView(boolean b) {
	    // TODO Auto-generated method stub	    
    }
}

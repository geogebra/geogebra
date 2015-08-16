package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.euclidian.EuclidianPanelWAbstract;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.view.consprotocol.ConstructionProtocolNavigationW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class EuclidianDockPanelW extends EuclidianDockPanelWAbstract implements EuclidianPanelWAbstract{

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
				//ToolBar.getAllToolsNoMacros(true),  // toolbar string... TODO: ToolBarW.getAllTools(app);
				null,
				stylebar,					// style bar?
				5,							// menu order
				'1' // ctrl-shift-1
			);
		setViewImage(getResources().styleBar_graphicsView());

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
		attachApp(application);
	}

	public void attachApp(AppW application) {
		app = application;

		// GuiManager can be null at the startup of the application,
		// but then the addNavigationBar method will be called explicitly.
		// By the way, this method is only called from AppWapplet,
		// so this will be actually null here.
		if (app.getGuiManager() != null
				&& app.showConsProtNavigation(App.VIEW_EUCLIDIAN)) {
			addNavigationBar();
		}
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
				.getConstructionProtocolNavigation(App.VIEW_EUCLIDIAN));
		consProtNav.getImpl().addStyleName("consProtNav");
		euclidianpanel.add(consProtNav.getImpl()); // may be invisible, but made
													// visible later
		updateNavigationBar();
	}
	
	@Override
	public void updateNavigationBar(){
//		ConstructionProtocolSettings cps = app.getSettings()
//		        .getConstructionProtocol();
//		((ConstructionProtocolNavigationW) consProtNav).settingsChanged(cps);
//		cps.addListener((ConstructionProtocolNavigation)consProtNav);

		if (app.getShowCPNavNeedsUpdate(App.VIEW_EUCLIDIAN)) {
			app.setShowConstructionProtocolNavigation(
					app.showConsProtNavigation(App.VIEW_EUCLIDIAN),
					App.VIEW_EUCLIDIAN);
		}
		if (app.showConsProtNavigation(App.VIEW_EUCLIDIAN)
				&& consProtNav == null) {
			this.addNavigationBar();
		}
		if(consProtNav != null){
			consProtNav.update();
			consProtNav.setVisible(app
					.showConsProtNavigation(App.VIEW_EUCLIDIAN));
			euclidianpanel.onResize();
		}
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
				if (app.showConsProtNavigation(App.VIEW_EUCLIDIAN)) {
					h -= dockPanel.navHeight();
				}

				// TODO handle this better?
				// exit if new size cannot be determined
				if (h <= 0 || w <= 0) {
					return;
				}
				if (h != oldHeight || w != oldWidth) {
					app.ggwGraphicsViewDimChanged(w, h);
					oldHeight = h;
					oldWidth = w;
				} else {
					// it's possible that the width/height didn't change but the position of EV did
					app.getEuclidianView1().getEuclidianController().calculateEnvironment();
				}
			}
		}

		public void add(Widget w, int x, int y) {
	        absoluteEuclidianPanel.add(w,x,y);
        }

		@Override
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

	public int navHeight() {
	    if(this.consProtNav != null && this.consProtNav.getImpl().getOffsetHeight() != 0){
	    	return this.consProtNav.getImpl().getOffsetHeight();
	    }
	    return 30;
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

	public void add(Widget w, int x, int y) {
	    euclidianpanel.add(w,x,y);
    }

	public void remove(Widget w) {
		euclidianpanel.remove(w);
    }

	public EuclidianDockPanelW getEuclidianView1Wrapper() {
		return this;
	}

	@Override
    public AbsolutePanel getAbsolutePanel() {
		return euclidianpanel.getAbsolutePanel();
	}

	@Override
    public EuclidianView getEuclidianView() {
		if (app != null)
			return app.getEuclidianView1();
		return null;
	}

	@Override
    public ResourcePrototype getIcon() {
		return getResources().menu_icon_graphics();
	}
}

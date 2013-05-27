package geogebra.web.gui.layout.panels;

import geogebra.common.euclidian.EuclidianStyleBar;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.main.App;
import geogebra.web.gui.app.VerticalPanelSmart;
import geogebra.web.gui.view.consprotocol.ConstructionProtocolNavigationW;
import geogebra.web.main.AppW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class EuclidianDockPanelW extends EuclidianDockPanelWAbstract {

	DockLayoutPanel toplevel;

	VerticalPanelSmart ancestor;
	EuclidianStyleBar espanel;
	EuclidianPanel euclidianpanel;

	Canvas eview1 = null;// static foreground
	
	EuclidianDockPanelW thisPanel;
	
	private ConstructionProtocolNavigationW consProtNav;

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
			thisPanel = this;
		}else{
			loadComponent();
			buildGUI();
		}
	}
	
	public EuclidianDockPanelW(AppW application, boolean stylebar) {
		this(stylebar);
		app = application;
		addNavigationBar();
	}

	
	@Override
	protected Widget loadComponent() {
		if (euclidianpanel == null) {
			euclidianpanel = new EuclidianPanel(this);
			eview1 = Canvas.createIfSupported();
			eview1.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
			eview1.getElement().getStyle().setZIndex(0);
			euclidianpanel.add(eview1);
		}

		return euclidianpanel;
	}
	
	public void addNavigationBar(){
//		App.debug("app in addNavigationBar(): " + app);
		if (app == null) return;
		consProtNav = (ConstructionProtocolNavigationW)(app
		        .getConstructionProtocolNavigation());
		consProtNav.getImpl().addStyleName("consProtNav");
		euclidianpanel.add(consProtNav.getImpl()); // may be invisible, but made visible later		
		updateNavigationBar();
	}
	
	public void updateNavigationBar(){
//		if (consProtNav == null) return;
//		ConstructionProtocolSettings cps = app.getSettings()
//		        .getConstructionProtocol();
//		((ConstructionProtocolNavigationW) consProtNav).settingsChanged(cps);
//		cps.addListener((ConstructionProtocolNavigation)consProtNav);

		
		App.debug("getShowNavNeedsUpdate: "+app.getShowCPNavNeedsUpdate());
		App.debug("app.showConsProtNavigation(): "+app.showConsProtNavigation());
		
		if (app.getShowCPNavNeedsUpdate()) {
			app.setShowConstructionProtocolNavigation(app
			        .showConsProtNavigation());
		}

		consProtNav.update();
		consProtNav.setVisible(false); //consProtNav.setVisible(app.showConsProtNavigation());
	}
	
	class EuclidianPanel extends AbsolutePanel implements RequiresResize {

		EuclidianDockPanelW dockPanel;

		int oldHeight = 0;
		int oldWidth = 0;
		
		public EuclidianPanel(EuclidianDockPanelW dockPanel) {
			this.dockPanel = dockPanel;
		}

		public void onResize() {
		
			if (app != null) {

				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {

						int h = dockPanel.getComponentInteriorHeight();
						int w = dockPanel.getComponentInteriorWidth();

						// TODO handle this better?
						// exit if new size cannot be determined
						if (h < 0 || w < 0) {
							return;
						}
						if (h != oldHeight || w != oldWidth) {
							app.ggwGraphicsViewDimChanged(w, h);
							oldHeight = h;
							oldWidth = w;
						}
					}
				});

			}
		}
	}
	
	@Override
	protected Widget loadStyleBar() {

		if (espanel == null) {
			espanel = app.getActiveEuclidianView().getStyleBar();
		}

		return (Widget) espanel;
	}

	public Canvas getCanvas() {
	    return eview1;
    }

	public AbsolutePanel getAbsolutePanel() {
	    return euclidianpanel;
    }

	public void onResize() {
		super.onResize();
		//App.debug("resized");
		/*if (application != null) {

			//if (sview != null) {
				// If this is resized, we may know its width and height

				int width = this.getOffsetWidth();
				int height = this.getOffsetHeight();
				ancestor.setWidth(width+"px");
				ancestor.setHeight(height+"px");

				height -=
					((EuclidianStyleBarW)application.getActiveEuclidianView().getStyleBar()).
					getOffsetHeight();

				eview1.setWidth(width+"px");
				eview1.setHeight(height+"px");
				((EuclidianViewW)application.getActiveEuclidianView()).setPreferredSize(width, height);
			//}
		}*/
    }

	public void add(Widget w, int x, int y) {
	    euclidianpanel.add(w,x,y);
    }

	public void remove(Widget w) {
		euclidianpanel.remove(w);
    }

	public void attachApp(App app) {
		super.attachApp(app);
		
	}

	public EuclidianDockPanelW getEuclidianView1Wrapper() {
		return this;
	}

	public AbsolutePanel getEuclidianPanel() {
		return euclidianpanel;
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

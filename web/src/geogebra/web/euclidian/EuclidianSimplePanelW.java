package geogebra.web.euclidian;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.main.App;
import geogebra.web.main.AppW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class EuclidianSimplePanelW extends AbsolutePanel {

	AppW app;
	//EuclidianStyleBar espanel;
	//EuclidianPanel euclidianpanel;

	Canvas eview1 = null;// static foreground

	//private ConstructionProtocolNavigationW consProtNav;

	/**
	 * This constructor is used by the Application
	 * and by the other constructor
	 * 
	 * @param stylebar (is there stylebar?)
	 */
	public EuclidianSimplePanelW(boolean stylebar) {
		super();

		loadComponent();
	}

	/**
	 * This constructor is used by the applet
	 * @param application
	 * @param stylebar
	 */
	public EuclidianSimplePanelW(AppW application, boolean stylebar) {
		this(stylebar);
		app = application;
		//addNavigationBar();
	}

	protected Widget loadComponent() {
		/*if (euclidianpanel == null) {
			euclidianpanel = new EuclidianPanel(this);*/
			eview1 = Canvas.createIfSupported();
			eview1.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
			eview1.getElement().getStyle().setZIndex(0);
		/*	euclidianpanel.getAbsolutePanel().add(eview1);
		}*/
		add(eview1);

		//return euclidianpanel;
		return this;
	}
	
	/*public void addNavigationBar(){
		consProtNav = (ConstructionProtocolNavigationW)(app
		        .getConstructionProtocolNavigation());
		consProtNav.getImpl().addStyleName("consProtNav");
		euclidianpanel.add(consProtNav.getImpl()); // may be invisible, but made visible later		
		updateNavigationBar();
	}*/
	
	/*public void updateNavigationBar(){
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
	}*/
	
	/*class EuclidianPanel extends FlowPanel implements RequiresResize {

		EuclidianSimplePanelW dockPanel;
		AbsolutePanel absoluteEuclidianPanel;

		int oldHeight = 0;
		int oldWidth = 0;
		
		public EuclidianPanel(EuclidianSimplePanelW dockPanel) {
			super();
			this.dockPanel = dockPanel;
			add(absoluteEuclidianPanel = new AbsolutePanel());
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

		public void add(Widget w, int x, int y) {
	        absoluteEuclidianPanel.add(w,x,y);
        }

		public boolean remove(Widget w) {
			return absoluteEuclidianPanel.remove(w);
        }

		public AbsolutePanel getAbsolutePanel() {
			return absoluteEuclidianPanel;
        }
	}*/

	/*protected Widget loadStyleBar() {

		if (espanel == null) {
			espanel = app.getEuclidianView1().getStyleBar();
		}

		return (Widget) espanel;
	}*/

	public Canvas getCanvas() {
	    return eview1;
    }

	public Panel getEuclidianPanel() {
	    return this;
    }

	/*public void onResize() {
		// super.onResize();
    }*/

	/*public void add(Widget w, int x, int y) {
	    euclidianpanel.add(w,x,y);
    }

	public void remover(Widget w) {
		euclidianpanel.remove(w);
    }*/

	public void attachApp(App app) {
		//super.attachApp(app);
		this.app = (AppW)app;
	}

	public EuclidianSimplePanelW getEuclidianView1Wrapper() {
		return this;
	}

	public AbsolutePanel getAbsolutePanel() {
		return this;
	}

	public EuclidianView getEuclidianView() {
		if (app != null)
			return app.getEuclidianView1();
		return null;
	}
}

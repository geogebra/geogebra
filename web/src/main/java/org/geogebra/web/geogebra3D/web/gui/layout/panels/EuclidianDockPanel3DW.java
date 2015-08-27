package org.geogebra.web.geogebra3D.web.gui.layout.panels;

import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.main.App;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;
import org.geogebra.web.html5.euclidian.MyEuclidianViewPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.layout.panels.EuclidianDockPanelWAbstract;
import org.geogebra.web.web.gui.view.consprotocol.ConstructionProtocolNavigationW;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class EuclidianDockPanel3DW extends EuclidianDockPanelWAbstract {

	/**
	 * default width of this panel
	 */
	public static final int DEFAULT_WIDTH = 480;
	
	MyEuclidianViewPanel euclidianpanel;


	/**
	 * constructor
	 * 
	 * @param app
	 *            application
	 * 
	 */
	public EuclidianDockPanel3DW(App app) {
		super(App.VIEW_EUCLIDIAN3D, // view id
		        "GraphicsView3D", // view title
		        ToolBar.getAllToolsNoMacros3D(), // toolbar string
		        true, // style bar?
		        4, // menu order
		        '3' // ctrl-shift-3
		);
		setViewImage(getResources().styleBar_graphics3dView());

		this.app = (AppW) app;
		this.setOpenInFrame(true);
		this.setEmbeddedSize(DEFAULT_WIDTH);

		// this.app = app;
	}

	@Override
	protected Widget loadComponent() {
		EuclidianView3DW view = getEuclidianView();
		view.setDockPanel(this);
		euclidianpanel = (MyEuclidianViewPanel) view.getComponent();
		return euclidianpanel;

	}

	@Override
	protected Widget loadStyleBar() {
		return (Widget) getEuclidianView().getStyleBar();
	}

	@Override
	public EuclidianView3DW getEuclidianView() {
		if (app != null)
			return (EuclidianView3DW) app.getEuclidianView3D();
		return null;
	}

	@Override
	public void updateNavigationBar() {

		if (app.getShowCPNavNeedsUpdate(id)) {
			app.setShowConstructionProtocolNavigation(
					app.showConsProtNavigation(id), id);
		}
		if (app.showConsProtNavigation(id)
				&& consProtNav == null) {
			this.addNavigationBar();
		}
		if (consProtNav != null) {
			consProtNav.update();
			consProtNav.setVisible(app.showConsProtNavigation(id));
			euclidianpanel.onResize();
		}
	}
	
	private ConstructionProtocolNavigationW consProtNav;
	
	public void addNavigationBar(){
		consProtNav = (ConstructionProtocolNavigationW)(app.getGuiManager()
				.getConstructionProtocolNavigation(id));
		consProtNav.getImpl().addStyleName("consProtNav");
		euclidianpanel.add(consProtNav.getImpl()); // may be invisible, but made visible later		
		updateNavigationBar();
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
	
    public AbsolutePanel getAbsolutePanel() {
		return euclidianpanel.getAbsolutePanel();
	}
	
	
	class EuclidianPanel extends FlowPanel implements RequiresResize {

		EuclidianDockPanel3DW dockPanel;
		AbsolutePanel absoluteEuclidianPanel;

		int oldHeight = 0;
		int oldWidth = 0;
		
		public EuclidianPanel(EuclidianDockPanel3DW dockPanel) {
			super();
			this.dockPanel = dockPanel;
			add(absoluteEuclidianPanel = new AbsolutePanel());
			absoluteEuclidianPanel.addStyleName("EuclidianPanel");
		}

		public void onResize() {

			if (app != null) {

				int h = dockPanel.getComponentInteriorHeight();
				int w = dockPanel.getComponentInteriorWidth();
				if (app.showConsProtNavigation(App.VIEW_EUCLIDIAN3D)) {
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
					app.getEuclidianView3D().getEuclidianController()
							.calculateEnvironment();
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


	public int navHeight() {
	    if(this.consProtNav != null && this.consProtNav.getImpl().getOffsetHeight() != 0){
	    	return this.consProtNav.getImpl().getOffsetHeight();
	    }
	    return 30;
    }


}

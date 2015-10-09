package org.geogebra.web.geogebra3D.web.gui.layout.panels;

import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.main.App;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.layout.panels.EuclidianDockPanelWAbstract;
import org.geogebra.web.web.gui.view.consprotocol.ConstructionProtocolNavigationW;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class EuclidianDockPanel3DW extends EuclidianDockPanelWAbstract {

	/**
	 * default width of this panel
	 */
	public static final int DEFAULT_WIDTH = 480;
	
	EuclidianPanel euclidianpanel;


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
		if (!app.supportsView(App.VIEW_EUCLIDIAN3D)) {
			return new FlowPanel();
		}
		EuclidianView3DW view = (EuclidianView3DW) app.getEuclidianView3D();
		view.setDockPanel(this);
		euclidianpanel = new EuclidianPanel(this,
				(AbsolutePanel) view.getComponent());
		return euclidianpanel;

	}

	@Override
	protected Widget loadStyleBar() {
		if (getEuclidianView() == null) {
			return super.loadStyleBar();
		}
		return (Widget) getEuclidianView().getStyleBar();
	}

	@Override
	public EuclidianView3DW getEuclidianView() {
		// do NOT initialize the view if it wasn't done previously
		if (app != null && app.isEuclidianView3Dinited()) {
			return (EuclidianView3DW) app.getEuclidianView3D();
		}
		return null;
	}

	/*
	 * @Override public void updateNavigationBar() {
	 * 
	 * if (app.getShowCPNavNeedsUpdate(id)) {
	 * app.setShowConstructionProtocolNavigation(
	 * app.showConsProtNavigation(id), id); } if (app.showConsProtNavigation(id)
	 * && consProtNav == null) { this.addNavigationBar(); } if (consProtNav !=
	 * null) { consProtNav.update();
	 * consProtNav.setVisible(app.showConsProtNavigation(id)); //
	 * updateEuclidianPanel(); euclidianpanel.onResize(); } }
	 */
	
	private ConstructionProtocolNavigationW consProtNav;
	
	
	@Override
	public EuclidianPanel getEuclidianPanel() {
	    return euclidianpanel;
    }

	public void add(Widget w, int x, int y) {
	    euclidianpanel.add(w,x,y);
    }

	public void remove(Widget w) {
		euclidianpanel.remove(w);
    }
	

	@Override
	public void calculateEnvironment() {
		if (app.isEuclidianView3Dinited()) {
			app.getEuclidianView3D().getEuclidianController()
					.calculateEnvironment();
		}

	}

	@Override
	public void resizeView(int width, int height) {
		app.ggwGraphicsView3DDimChanged(width, height);

	}

}

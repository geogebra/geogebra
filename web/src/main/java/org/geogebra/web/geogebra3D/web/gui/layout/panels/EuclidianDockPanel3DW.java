package org.geogebra.web.geogebra3D.web.gui.layout.panels;

import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.main.App;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.layout.panels.EuclidianDockPanelWAbstract;

import com.google.gwt.user.client.ui.Widget;

public class EuclidianDockPanel3DW extends EuclidianDockPanelWAbstract {

	/**
	 * default width of this panel
	 */
	public static final int DEFAULT_WIDTH = 480;

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
		return view.getComponent();
	}

	@Override
	public void showView(boolean b) {

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

}

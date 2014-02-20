package geogebra.geogebra3D.web.gui.layout.panels;

import geogebra.common.gui.toolbar.ToolBar;
import geogebra.common.main.App;
import geogebra.geogebra3D.web.euclidian3D.EuclidianView3DW;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.ui.Widget;

public class EuclidianDockPanel3DW extends DockPanelW {

	/**
	 * default width of this panel
	 */
	public static final int DEFAULT_WIDTH = 480;


	/**
	 * constructor

	 * @param app application
	 * 
	 */
	public EuclidianDockPanel3DW(App app) {
		super(
				App.VIEW_EUCLIDIAN3D,	// view id 
				"GraphicsView3D", 				// view title
				ToolBar.getAllToolsNoMacros3D(),					// toolbar string
				true,					// style bar?
				4,							// menu order
				'3' // ctrl-shift-3
			);
		
		this.app = (AppW) app;
		this.setOpenInFrame(true);
		this.setEmbeddedSize(DEFAULT_WIDTH);
		
	    //this.app = app;
    }

	@Override
	protected Widget loadComponent() {
		EuclidianView3DW view = (EuclidianView3DW) app.getEuclidianView3D();
		view.setDockPanel(this);
		return view.getComponent();
	}

	@Override
	public void showView(boolean b) {
		
	}
	
	
	@Override
	protected Widget loadStyleBar() {
		return (Widget) ((EuclidianView3DW) app.getEuclidianView3D()).getStyleBar();
	}
	

	
}

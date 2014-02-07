package geogebra.geogebra3D.web.gui.layout.panels;

import geogebra.common.gui.toolbar.ToolBar;
import geogebra.common.main.App;
import geogebra.geogebra3D.web.euclidian3D.EuclidianViewW3D;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.ui.Widget;

public class EuclidianDockPanelW3D extends DockPanelW {

	/**
	 * default width of this panel
	 */
	public static final int DEFAULT_WIDTH = 480;


	/**
	 * constructor

	 * @param app application
	 * 
	 */
	public EuclidianDockPanelW3D(App app) {
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
		return ((EuclidianViewW3D) app.getEuclidianView3D()).getComponent();
	}

	@Override
	public void showView(boolean b) {
		
	}
	
	
	@Override
	protected Widget loadStyleBar() {
		return (Widget) ((EuclidianViewW3D) app.getEuclidianView3D()).getStyleBar();
	}
	
	
}

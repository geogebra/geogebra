package geogebra.geogebra3D.web.gui.layout.panels;

import geogebra.common.gui.toolbar.ToolBar;
import geogebra.common.main.App;
import geogebra.geogebra3D.web.euclidianForPlane.EuclidianViewForPlaneW;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.ui.Widget;

public class EuclidianDockPanelForPlaneW extends DockPanelW {

	/**
	 * default width of this panel
	 */
	public static final int DEFAULT_WIDTH = 480;

	//id of the first view
	private static int viewId = App.VIEW_EUCLIDIAN_FOR_PLANE_START;
	
	private EuclidianViewForPlaneW view;


	/**
	 * constructor

	 * @param app application
	 * 
	 */
	public EuclidianDockPanelForPlaneW(App app, EuclidianViewForPlaneW view) {
		super(
				viewId,	// view id 
				"GraphicsViewForPlaneA", 				// view title
				ToolBar.getAllToolsNoMacrosForPlane(),					// toolbar string
				true,					// style bar?
				-1,							// menu order
				'p' 
			);
		
		this.app = (AppW) app;
		this.setOpenInFrame(true);
		
		this.view = view;
		view.getCompanion().setDockPanel(this);
		
		this.setEmbeddedSize(DEFAULT_WIDTH);
		
		viewId++; //id of next view
		
	    //this.app = app;
    }

	@Override
	protected Widget loadComponent() {		
		view.getCompanion().setDockPanel(this);
		return view.getComponent();
	}

	@Override
	public void showView(boolean b) {
		
	}
	
	
	@Override
	protected Widget loadStyleBar() {
		return (Widget) view.getStyleBar();
	}
	

	
}

package geogebra.gui.layout.panels;

import geogebra.gui.layout.DockPanel;
import geogebra.main.Application;

import javax.swing.JComponent;

/**
 * Dock panel for the CAS view.
 */
public class CasDockPanel extends DockPanel {
	private static final long serialVersionUID = 1L;
	
	private Application app;
	
	/**
	 * @param app
	 */
	public CasDockPanel(Application app) {
		super(
			Application.VIEW_CAS, 	// view id
			"CAS", 					// view title phrase 
			getDefaultToolbar(),	// toolbar string
			false,					// style bar?
			4,						// menu order
			'K' // ctrl-shift-K
		);
		
		this.app = app;
	}
	
	protected JComponent loadComponent() {
		return app.getGuiManager().getCasView().getCASViewComponent();
	}
	
	private static String getDefaultToolbar() {
		return "1001 | 1002 | 1003  || 1005 | 1004 || 1006 | 1007 || 1008 1009";		
	}
	
	/**
	 * Sets the active toolbar and tells the CAS view about this so it
	 * can ignore mode changes which would otherwise result in cell computations.
	 */
	protected void setActiveToolBar(){		
		app.getGuiManager().getCasView().setToolbarIsUpdatedByDockPanel(true);
		super.setActiveToolBar();
		app.getGuiManager().getCasView().setToolbarIsUpdatedByDockPanel(false);
	}
}

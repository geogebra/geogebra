package geogebra.gui.layout.panels;

import geogebra.common.main.AbstractApplication;
import geogebra.gui.layout.DockPanel;
import geogebra.main.Application;

import javax.swing.JComponent;

/**
 * Dock panel for the CAS view.
 */
public class CasDockPanel extends DockPanel {
	private static final long serialVersionUID = 1L;
	
	private Application appCas;
	
	/**
	 * @param app application
	 */
	public CasDockPanel(Application app) {
		super(
			AbstractApplication.VIEW_CAS, 	// view id
			"CAS", 					// view title phrase 
			getDefaultToolbar(),	// toolbar string
			true,					// style bar?
			4,						// menu order
			'K' // ctrl-shift-K
		);
		
		this.appCas = app;
	}
	

	@Override
	protected JComponent loadStyleBar() {
		return appCas.getGuiManager().getCasView().getCASStyleBar();
	}
	
	@Override
	protected JComponent loadComponent() {
		return appCas.getGuiManager().getCasView().getCASViewComponent();
	}
	
	private static String getDefaultToolbar() {
		return "1001 | 1002 | 1003  || 1005 | 1004 || 1006 | 1007 | 1010 || 1008 1009 || 6";		
	}
	
	/**
	 * Sets the active toolbar and tells the CAS view about this so it
	 * can ignore mode changes which would otherwise result in cell computations.
	 */
	@Override
	protected void setActiveToolBar(){		
		appCas.getGuiManager().getCasView().setToolbarIsUpdatedByDockPanel(true);
		super.setActiveToolBar();
		appCas.getGuiManager().getCasView().setToolbarIsUpdatedByDockPanel(false);
	}
}

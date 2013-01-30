package geogebra.gui.layout.panels;

import geogebra.common.cas.view.CASView;
import geogebra.common.main.App;
import geogebra.gui.GuiManagerD;
import geogebra.gui.layout.DockPanel;
import geogebra.main.AppD;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 * Dock panel for the CAS view.
 */
public class CasDockPanel extends DockPanel {
	private static final long serialVersionUID = 1L;
	
	private AppD appCas;
	
	/**
	 * @param app application
	 */
	public CasDockPanel(AppD app) {
		super(
			App.VIEW_CAS, 	// view id
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
		return ((GuiManagerD)appCas.getGuiManager()).getCasView().getCASStyleBar();
	}
	
	@Override
	protected JComponent loadComponent() {
		return ((GuiManagerD)appCas.getGuiManager()).getCasView().getCASViewComponent();
	}
	
	private static String getDefaultToolbar() {
		return CASView.TOOLBAR_DEFINITION;		
	}
	
	/**
	 * Sets the active toolbar and tells the CAS view about this so it
	 * can ignore mode changes which would otherwise result in cell computations.
	 */
	@Override
	protected void setActiveToolBar(){		
		super.setActiveToolBar();
	}
	
	@Override
	public ImageIcon getIcon() { 
			return app.getImageIcon("view-cas24.png");
	}
}

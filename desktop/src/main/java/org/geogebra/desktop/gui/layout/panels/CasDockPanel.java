package org.geogebra.desktop.gui.layout.panels;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.geogebra.common.cas.view.CASView;
import org.geogebra.common.main.App;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.layout.DockPanel;
import org.geogebra.desktop.main.AppD;

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
			3,						// menu order
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
		return CASView.TOOLBAR_DEFINITION_D;		
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
		return app.getMenuIcon("menu_view_cas.png");
	}
}

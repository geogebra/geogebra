package geogebra.gui.layout.panels;

import geogebra.gui.layout.DockPanel;
import geogebra.main.Application;

import javax.swing.JComponent;

public class ConstructionProtocolDockPanel extends DockPanel {

	/**
	 * @param app
	 */
	public ConstructionProtocolDockPanel(Application app) {
		super(
			Application.VIEW_CONSTRUCTION_PROTOCOL, 	// view id
			"ConstructionProtocol", 					// view title phrase 
			null,	// toolbar string
			true,					// style bar?
			7,						// menu order
			'L' // ctrl-shift-L
		);
		
		this.app = app;
		this.setShowStyleBar(true);
	}

	protected JComponent loadComponent() {
		return (JComponent) app.getGuiManager().getConstructionProtocolView();
	}

	protected JComponent loadStyleBar() {
		return app.getGuiManager().getConstructionProtocolView().getStyleBar();
	}
	
}

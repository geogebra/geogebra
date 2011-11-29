package geogebra.gui.layout.panels;

import geogebra.common.main.AbstractApplication;
import geogebra.gui.layout.DockPanel;
import geogebra.main.Application;

import javax.swing.JComponent;

public class ConstructionProtocolDockPanel extends DockPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * @param app
	 */
	public ConstructionProtocolDockPanel(Application app) {
		super(
			AbstractApplication.VIEW_CONSTRUCTION_PROTOCOL, 	// view id
			"ConstructionProtocol", 					// view title phrase 
			null,	// toolbar string
			true,					// style bar?
			7,						// menu order
			'L' // ctrl-shift-L
		);
		
		this.app = app;
		this.setShowStyleBar(true);
	}

	@Override
	protected JComponent loadComponent() {
		return (JComponent) app.getGuiManager().getConstructionProtocolView();
	}

	@Override
	protected JComponent loadStyleBar() {
		return app.getGuiManager().getConstructionProtocolView().getStyleBar();
	}
	
}

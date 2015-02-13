package geogebra.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.gui.layout.DockPanel;
import geogebra.gui.view.consprotocol.ConstructionProtocolViewD;
import geogebra.main.AppD;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

public class ConstructionProtocolDockPanel extends DockPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * @param app
	 */
	public ConstructionProtocolDockPanel(AppD app) {
		super(
			App.VIEW_CONSTRUCTION_PROTOCOL, 	// view id
			"ConstructionProtocol", 					// view title phrase 
			null,	// toolbar string
			true,					// style bar?
			7,						// menu order
			'L' // ctrl-shift-L
		);
		
		setApp(app);
		this.setShowStyleBar(true);
	}

	@Override
	protected JComponent loadComponent() {
		return ((ConstructionProtocolViewD)(app.getGuiManager().getConstructionProtocolView())).getCpPanel();
	}

	@Override
	protected JComponent loadStyleBar() {
		return ((ConstructionProtocolViewD)app.getGuiManager().getConstructionProtocolView()).getStyleBar();
	}
	
	@Override
	public ImageIcon getIcon() { 
		return app.getMenuIcon("menu_view_construction_protocol.png");
	}
}

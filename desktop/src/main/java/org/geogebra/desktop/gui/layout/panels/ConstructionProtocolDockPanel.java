package org.geogebra.desktop.gui.layout.panels;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.geogebra.common.main.App;
import org.geogebra.desktop.gui.view.consprotocol.ConstructionProtocolViewD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

public class ConstructionProtocolDockPanel extends NavigableDockPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * @param app
	 */
	public ConstructionProtocolDockPanel(AppD app) {
		super(App.VIEW_CONSTRUCTION_PROTOCOL, // view id
				"ConstructionProtocol", // view title phrase
				null, // toolbar string
				true, // style bar?
				7, // menu order
				'L' // ctrl-shift-L
		);

		setApp(app);
		this.setShowStyleBar(true);
	}

	@Override
	protected JComponent getViewPanel() {
		return ((ConstructionProtocolViewD) (app.getGuiManager()
				.getConstructionProtocolView())).getCpPanel();
	}

	@Override
	protected JComponent loadStyleBar() {
		return ((ConstructionProtocolViewD) app.getGuiManager()
				.getConstructionProtocolView()).getStyleBar();
	}

	@Override
	public ImageIcon getIcon() {
		return app.getMenuIcon(GuiResourcesD.MENU_VIEW_CONSTRUCTION_PROTOCOL);
	}
}

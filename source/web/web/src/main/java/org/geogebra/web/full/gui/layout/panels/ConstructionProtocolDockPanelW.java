package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.view.consprotocol.ConstructionProtocolViewW;
import org.geogebra.web.full.gui.view.consprotocol.ConstructionProtocolViewW.ConsProtocolScrollPanel;
import org.geogebra.web.full.main.AppWFull;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.ui.Panel;

public class ConstructionProtocolDockPanelW extends NavigableDockPanelW {

	/**
	 * @param app
	 *            application
	 */
	public ConstructionProtocolDockPanelW(AppWFull app) {
		super(App.VIEW_CONSTRUCTION_PROTOCOL, null, false);
		this.app = app;
		this.setShowStyleBar(true);
		this.setEmbeddedSize(300);
	}

	@Override
    public ResourcePrototype getIcon() {
		return getResources().menu_icon_construction_protocol();
	}

	@Override
	protected Panel getViewPanel() {
		return ((ConstructionProtocolViewW) app.getGuiManager()
				.getConstructionProtocolView()).getOuterScrollPanel();
	}

	@Override
	protected ResourcePrototype getViewIcon() {
		return getResources().styleBar_ConstructionProtocol();
	}

	@Override
	public void resizeContent(Panel content) {
		super.resizeContent(content);
		if (content instanceof ConsProtocolScrollPanel) {
			((ConsProtocolScrollPanel) content).onResize();
		}
	}
}

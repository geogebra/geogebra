package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.view.consprotocol.ConstructionProtocolViewW;
import org.geogebra.web.full.gui.view.consprotocol.ConstructionProtocolViewW.MyPanel;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.resources.client.ResourcePrototype;

import com.google.gwt.user.client.ui.Panel;

public class ConstructionProtocolDockPanelW extends NavigableDockPanelW {

	/**
	 * @param app
	 *            application
	 */
	public ConstructionProtocolDockPanelW(AppW app) {
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
		if (content instanceof MyPanel) {
			((MyPanel) content).onResize();
		}
	}
}

package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.util.StyleBarW;
import org.geogebra.web.web.gui.view.consprotocol.ConstructionProtocolNavigationW;
import org.geogebra.web.web.gui.view.consprotocol.ConstructionProtocolViewW;

import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class ConstructionProtocolDockPanelW extends DockPanelW{

	private static final long serialVersionUID = 1L;
	private StyleBarW cpStyleBar;
	private ConstructionProtocolNavigationW consProtNav;
	private InnerPanel innerPanel;

	public int navHeight() {
		if (this.consProtNav != null
				&& this.consProtNav.getImpl().getOffsetHeight() != 0) {
			return this.consProtNav.getImpl().getOffsetHeight();
		}
		return 30;
	}
	/**
	 * @param app
	 */
	public ConstructionProtocolDockPanelW(AppW app) {
		super(
			App.VIEW_CONSTRUCTION_PROTOCOL, 	// view id
			"ConstructionProtocol", 					// view title phrase 
			null,	// toolbar string
			true,					// style bar?
			7,						// menu order
			'L' // ctrl-shift-L
		);
		

		this.app = app;
		this.setShowStyleBar(true);
		this.setEmbeddedSize(300);
	}

	@Override
	protected Widget loadComponent() {
		innerPanel = new InnerPanel(this, ((ConstructionProtocolViewW) app
				.getGuiManager().getConstructionProtocolView()).getCpPanel());
		setViewImage(getResources().styleBar_ConstructionProtocol());

		addNavigationBar();
		return innerPanel;
	}

	@Override
	protected Widget loadStyleBar() {
		if (cpStyleBar == null) {
			cpStyleBar = ((ConstructionProtocolViewW) app.getGuiManager().getConstructionProtocolView()).getStyleBar();
		}
		return cpStyleBar; 
		//return ((ConstructionProtocolView)app.getGuiManager().getConstructionProtocolView()).getStyleBar();
	}
	
	@Override
    public ResourcePrototype getIcon() {
		return getResources().menu_icon_construction_protocol();
	}
	
	public final void addNavigationBar() {
		consProtNav = (ConstructionProtocolNavigationW) (app.getGuiManager()
				.getConstructionProtocolNavigation(id));
		consProtNav.getImpl().addStyleName("consProtNav");
		if (getInnerPanel() == null) {
			loadComponent();
		}
		getInnerPanel().add(consProtNav.getImpl()); // may be invisible, but
													// made
		// visible later
		updateNavigationBar();
	}

	@Override
	public final void updateNavigationBar() {
		// ConstructionProtocolSettings cps = app.getSettings()
		// .getConstructionProtocol();
		// ((ConstructionProtocolNavigationW) consProtNav).settingsChanged(cps);
		// cps.addListener((ConstructionProtocolNavigation)consProtNav);

		if (app.getShowCPNavNeedsUpdate(id)) {
			app.setShowConstructionProtocolNavigation(
					app.showConsProtNavigation(id), id);
		}
		if (app.showConsProtNavigation(id) && consProtNav == null) {
			this.addNavigationBar();
		}
		if (consProtNav != null) {
			consProtNav.update();
			consProtNav.setVisible(app.showConsProtNavigation(id));
			onResize();
		}
	}

	private FlowPanel getInnerPanel() {
		return innerPanel;
	}

}

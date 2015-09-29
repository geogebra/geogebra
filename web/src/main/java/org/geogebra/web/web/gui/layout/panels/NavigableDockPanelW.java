package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.view.consprotocol.ConstructionProtocolNavigationW;

import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public abstract class NavigableDockPanelW extends DockPanelW {
	private ConstructionProtocolNavigationW consProtNav;
	private InnerPanel innerPanel;
	public NavigableDockPanelW(int id, String title, String toolbar,
			boolean hasStyleBar, int menuOrder, char menuShortcut) {
		super(id, title, toolbar, hasStyleBar, menuOrder, menuShortcut);
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

	@Override
	protected final Widget loadComponent() {
		setViewImage(getViewIcon());
		innerPanel = new InnerPanel(this, getViewPanel());
		setViewImage(getResources().styleBar_ConstructionProtocol());

		addNavigationBar();
		return innerPanel;
	}

	protected abstract ResourcePrototype getViewIcon();

	protected abstract Panel getViewPanel();

	public int navHeight() {
		if (this.consProtNav != null
				&& this.consProtNav.getImpl().getOffsetHeight() != 0) {
			return this.consProtNav.getImpl().getOffsetHeight();
		}
		return 30;
	}

	public final void addNavigationBar() {
		consProtNav = (ConstructionProtocolNavigationW) (app.getGuiManager()
				.getConstructionProtocolNavigation(id));
		consProtNav.getImpl().addStyleName("consProtNav");
		if (innerPanel == null) {
			loadComponent();
		}
		innerPanel.add(consProtNav.getImpl()); // may be invisible, but
													// made
		// visible later
		updateNavigationBar();
	}

}

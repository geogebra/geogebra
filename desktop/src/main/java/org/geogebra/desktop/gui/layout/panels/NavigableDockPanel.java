package org.geogebra.desktop.gui.layout.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.geogebra.common.main.settings.ConstructionProtocolSettings;
import org.geogebra.desktop.gui.layout.DockPanel;
import org.geogebra.desktop.gui.view.consprotocol.ConstructionProtocolNavigationD;

public abstract class NavigableDockPanel extends DockPanel {
	private JPanel panel = null;
	private ConstructionProtocolNavigationD consProtNav;
	public NavigableDockPanel(int id, String title, String toolbar,
			boolean hasStyleBar, int menuOrder) {
		super(id, title, toolbar, hasStyleBar, menuOrder);
	}

	public NavigableDockPanel(int id, String title, String toolbar,
			boolean hasStylebar, int menuOrder, char c) {
		super(id, title, toolbar, hasStylebar, menuOrder, c);
	}

	@Override
	protected JComponent loadComponent() {
		if (panel == null) {
			panel = new JPanel(new BorderLayout());

			panel.add(getViewPanel(), BorderLayout.CENTER);

			consProtNav = (ConstructionProtocolNavigationD) app.getGuiManager()
					.getConstructionProtocolNavigation(id);

			ConstructionProtocolSettings cps = app.getSettings()
					.getConstructionProtocol();
			consProtNav.settingsChanged(cps);
			cps.addListener(consProtNav);

			if (app.getShowCPNavNeedsUpdate(id)) {
				app.setShowConstructionProtocolNavigation(
						app.showConsProtNavigation(id), id);
			}
			consProtNav.getImpl().setBorder(
					BorderFactory
							.createMatteBorder(1, 0, 0, 0, Color.lightGray));
			consProtNav.getImpl().setVisible(app.showConsProtNavigation(id));

			panel.add(consProtNav.getImpl(), BorderLayout.SOUTH); // may be
																	// invisible,
																	// but made
																	// visible
																	// later
		}

		return panel;
	}

	protected abstract Component getViewPanel();

}

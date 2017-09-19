package org.geogebra.desktop.gui.layout.panels;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.geogebra.common.main.App;
import org.geogebra.desktop.gui.layout.DockPanelD;
import org.geogebra.desktop.main.AppD;

/**
 * Dock panel for error of loading (used for 3D panel not supported by ggb
 * version &lt; 5.0)
 */
public class ErrorDockPanel extends DockPanelD {
	private static final long serialVersionUID = 1L;

	/**
	 * @param app
	 * @param viewId
	 */
	public ErrorDockPanel(AppD app, int viewId) {
		super(App.VIEW_NONE, // view id
				"ErrorWindow (viewId=" + viewId + ")", // view title phrase
				null, // toolbar string
				false, // style bar?
				4, // menu order
				'3' // menu shortcut
		);

		// setVisible(false);

		setApp(app);
	}

	@Override
	protected JComponent loadComponent() {
		return new JPanel();
	}

	@Override
	public void updatePanel() {
		if (component == null && isVisible()) {
			component = loadComponent();
			add(component, BorderLayout.CENTER);
		}
	}

	// unused methods
	@Override
	public final void setFocus(boolean hasFocus, boolean updatePropertiesView) {
		// noting to do
	}

	@Override
	public void closePanel() {
		// nothing to do
	}
}

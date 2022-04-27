package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Settings for CAS in HTML5
 *
 */
public class OptionsCASW implements OptionPanelW {

	private AppW app;
	private FlowPanel optionsPanel;
	private ComponentCheckbox showRoots;
	private ComponentCheckbox showNavigation;

	/**
	 * @param app - app
	 */
	public OptionsCASW(AppW app) {
		this.app = app;
		createGUI();
    }

	private void createGUI() {
		showRoots = new ComponentCheckbox(app.getLocalization(), false,
				"CASShowRationalExponentsAsRoots", () -> {
			app.getSettings().getCasSettings().setShowExpAsRoots(showRoots.isSelected());
			updateGUI();
		});

		showNavigation = new ComponentCheckbox(app.getLocalization(), false, "NavigationBar",
				() -> {
			app.toggleShowConstructionProtocolNavigation(App.VIEW_CAS);
			updateGUI();
		});

		optionsPanel = new FlowPanel();
		optionsPanel.addStyleName("propertiesPanel");
		optionsPanel.addStyleName("simplePropertiesPanel");

		optionsPanel.add(showRoots);
		optionsPanel.add(showNavigation);

		setLabels();
		updateGUI();
	}

	/**
	 * Update the language
	 */
	public void setLabels() {
		showRoots.setLabels();
		showNavigation.setLabels();
	}

	@Override
	public void updateGUI() {
		showRoots.setSelected(app.getSettings().getCasSettings()
				.getShowExpAsRoots());
		showNavigation.setSelected(app.showConsProtNavigation(App.VIEW_CAS));
    }

	@Override
	public Widget getWrappedPanel() {
		return optionsPanel;
    }

	@Override
    public void onResize(int height, int width) {
	    // TODO Auto-generated method stub
    }

	@Override
	public MultiRowsTabPanel getTabPanel() {
		// TODO Auto-generated method stub
		return null;
	}
}

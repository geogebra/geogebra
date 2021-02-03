package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Settings for CAS in HTML5
 *
 */
public class OptionsCASW implements OptionPanelW, ClickHandler {

	private AppW app;
	private FlowPanel optionsPanel;
	private CheckBox showRoots;
	private CheckBox showNavigation;

	/**
	 * @param app
	 *            app
	 */
	public OptionsCASW(AppW app) {
		this.app = app;
		createGUI();
    }

	private void createGUI() {
		showRoots = new CheckBox();
		showRoots.addClickHandler(this);
		showRoots.setStyleName("checkBoxPanel");

		showNavigation = new CheckBox();
		showNavigation.addClickHandler(this);
		showNavigation.setStyleName("checkBoxPanel");

		optionsPanel = new FlowPanel();
		optionsPanel.addStyleName("propertiesPanel");
		optionsPanel.addStyleName("simplePropertiesPanel");

		// optionsPanel.add(cbShowFormulaBar);
		optionsPanel.add(showRoots);
		optionsPanel.add(showNavigation);

		// spacer
		// layoutOptions.add(Box.createVerticalStrut(16));

		setLabels();
		updateGUI();

	}

	/**
	 * Update the language
	 */
	public void setLabels() {
		Localization loc = app.getLocalization();
		showRoots.setText(loc.getMenu("CASShowRationalExponentsAsRoots"));
		showNavigation.setText(loc.getMenu("NavigationBar"));

	}

	@Override
	public void updateGUI() {
		showRoots.setValue(app.getSettings().getCasSettings()
				.getShowExpAsRoots());
		showNavigation.setValue(app.showConsProtNavigation(App.VIEW_CAS));
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
	public void onClick(ClickEvent event) {
		actionPerformed(event.getSource());

	}

	private void actionPerformed(Object source) {
		if (source == showRoots) {
			app.getSettings().getCasSettings()
					.setShowExpAsRoots(showRoots.getValue());
		}

		else if (source == showNavigation) {
			app.toggleShowConstructionProtocolNavigation(App.VIEW_CAS);
		}

		updateGUI();

	}

	@Override
	public MultiRowsTabPanel getTabPanel() {
		// TODO Auto-generated method stub
		return null;
	}

}

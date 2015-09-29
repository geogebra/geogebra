package org.geogebra.web.web.gui.dialog.options;

import org.geogebra.common.gui.dialog.options.OptionsCAS;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Settings for CAS in HTML5
 *
 */
public class OptionsCASW extends OptionsCAS implements OptionPanelW,
		ClickHandler {

	private AppW app;
	private FlowPanel optionsPanel;
	private CheckBox showRoots, showNavigation;

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
		showNavigation = new CheckBox();
		showNavigation.addClickHandler(this);
		optionsPanel = new FlowPanel();
		optionsPanel.addStyleName("objectPropertiesPanel");

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
		showRoots.setText(app.getPlain("CASShowRationalExponentsAsRoots"));
		showNavigation.setText(app.getMenu("NavigationBar"));

	}

	public void updateGUI() {
		showRoots.setValue(app.getSettings().getCasSettings()
				.getShowExpAsRoots());
		showNavigation.setValue(app.showConsProtNavigation(App.VIEW_CAS));
    }

	public Widget getWrappedPanel() {
		return optionsPanel;
    }

	@Override
    public void onResize(int height, int width) {
	    // TODO Auto-generated method stub
	    
    }

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

}

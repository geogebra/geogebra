package org.geogebra.web.web.gui.dialog.options;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.OptionsAdvanced;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class OptionsAlgebraW extends OptionsAdvanced
		implements OptionPanelW, SetLabels, ClickHandler {
	private AppW app;
	private FlowPanel optionsPanel;
	private CheckBox showAuxiliaryObjects;

	public OptionsAlgebraW(AppW app) {
		this.app = app;
		createGUI();
	}

	private void createGUI() {
		optionsPanel = new FlowPanel();
		showAuxiliaryObjects = new CheckBox();
		showAuxiliaryObjects.addClickHandler(this);
		optionsPanel.add(showAuxiliaryObjects);
		setLabels();
	}

	public void updateGUI() {
		showAuxiliaryObjects.setValue(app.showAuxiliaryObjects);
    }

	public Widget getWrappedPanel() {
		return optionsPanel;
    }

	@Override
    public void onResize(int height, int width) {
	    // TODO Auto-generated method stub
	    
    }

	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if (source == showAuxiliaryObjects) {
			app.setShowAuxiliaryObjects(showAuxiliaryObjects.getValue());
		}
	}

	public void setLabels() {
		showAuxiliaryObjects
				.setText(app.getLocalization().getPlain("AuxiliaryObjects"));
	}
}

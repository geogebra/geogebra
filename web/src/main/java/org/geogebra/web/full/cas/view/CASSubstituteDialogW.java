package org.geogebra.web.full.cas.view;

import java.util.ArrayList;
import java.util.Arrays;

import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonData;
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class CASSubstituteDialogW extends ComponentDialog {
	/**
	/**
	 * base dialog constructor
	 * @param app - see {@link AppW}
	 * @param dialogData - contains trans keys for title and buttons
	 */
	public CASSubstituteDialogW(AppW app, DialogData dialogData) {
		super(app, dialogData, false, true);
		addStyleName("substituteDialog");
		setPosBtnDisabled(true);
		buildGUI(app.getLocalization());
	}

	private void buildGUI(Localization loc) {
		RadioButtonData dataSym = new RadioButtonData("Symbolic evaluation", true);
		RadioButtonData dataNum = new RadioButtonData("Numeric evaluation", false);

		RadioButtonPanel radioBtnPanel = new RadioButtonPanel(loc,
				new ArrayList<>(Arrays.asList(dataSym, dataNum)));
		addDialogContent(radioBtnPanel);

		FlowPanel labelPanel = new FlowPanel();
		labelPanel.addStyleName("flexGroup");
		Label substLabel = new Label(loc.getMenu("Substitute"));
		labelPanel.add(substLabel);
		Label withLabel = new Label(loc.getMenu("With"));
		labelPanel.add(withLabel);
		addDialogContent(labelPanel);


	}
}

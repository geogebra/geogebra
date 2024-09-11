package org.geogebra.web.full.gui.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.gui.dialog.ButtonDialogModel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.components.CompDropDown;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.full.gui.util.ScriptArea;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

/**
 * Dialog for creating buttons and inputboxes
 *
 */
public class ButtonDialogW extends ComponentDialog
		implements HasKeyboardPopup {
	private ComponentInputField captionInput;
	private final ButtonDialogModel model;
	private ScriptArea tfScript;
	private final Localization loc;

	/**
	 * @param app
	 *            app
	 * @param x
	 *            position
	 * @param y
	 *            position
	 * @param data
	 *            dialog translation keys
	 * @param inputBox
	 *            whether this is for inputbox
	 */
	public ButtonDialogW(final AppW app, int x, int y,
			DialogData data, boolean inputBox) {
		super(app, data, false, true);

		this.loc = app.getLocalization();
		model = new ButtonDialogModel(app, x, y, inputBox);
		addStyleName(inputBox ? "inputboxDialog" : "buttonDialog");
		buildContent();
		setOnPositiveAction(() -> model.apply(captionInput.getText(), tfScript.getText()));
		if (!app.isWhiteboardActive()) {
			app.registerPopup(this);
		}

		this.addCloseHandler(event -> {
			app.unregisterPopup(this);
			app.hideKeyboard();
		});
	}

	private void buildContent() {
		// create caption panel

		String initString = model.getInitString();

		captionInput = new ComponentInputField((AppW) app, "",
				"Button.Caption", "", initString, -1, null);
		captionInput.getTextField().getTextComponent().setAutoComplete(false);

		Label scriptLabel = new Label(loc.getMenu("Script"));
		scriptLabel.addStyleName("coloredLabel");
		tfScript = new ScriptArea((AppW) app);
		tfScript.enableGGBKeyboard();

		FlowPanel scriptPanel = new FlowPanel();
		scriptPanel.add(scriptLabel);
		scriptPanel.add(tfScript);

		FlowPanel contentPanel = new FlowPanel();
		// create object list
		contentPanel.add(captionInput);

		if (model.isTextField()) {
			ArrayList<GeoElement> options = model.getLinkableObjects();
			List<String> optionNames = options.stream()
					.map(geo -> geo == null ? "" : geo.toString(StringTemplate.defaultTemplate))
					.collect(Collectors.toList());
			CompDropDown linkedDropDown = new CompDropDown((AppW) app, "LinkedObject",
					optionNames, 0);
			linkedDropDown.addChangeHandler(() -> updateModel(linkedDropDown, options));
			linkedDropDown.setDisabled(options.size() < 2);
			linkedDropDown.setFullWidth(true);
			contentPanel.add(linkedDropDown);
		} else {
			contentPanel.add(scriptPanel);
		}
		addDialogContent(contentPanel);
	}

	/**
	 * Update linked geo in model
	 * 
	 * @param cbAdd
	 *            list of geos
	 */
	protected void updateModel(CompDropDown cbAdd, ArrayList<GeoElement> options) {
		model.setLinkedGeo(options.get(cbAdd.getSelectedIndex()));
	}

}
package org.geogebra.web.full.gui.dialog;

import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.gui.dialog.ButtonDialogModel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.util.ScriptArea;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Dialog for creating buttons and inputboxes
 *
 */
public class ButtonDialogW extends ComponentDialog
		implements HasKeyboardPopup {
	private AutoCompleteTextFieldW tfCaption;
	private ButtonDialogModel model;
	private ScriptArea tfScript;
	private Localization loc;
	
	/**
	 * @param app
	 *            app
	 * @param x
	 *            position
	 * @param y
	 *            position
	 * @param data
	 * 			  dialog transkeys
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
		setOnPositiveAction(() -> model.apply(tfCaption.getText(), tfScript.getText()));
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
		Label captionLabel = new Label(
				app.isUnbundledOrWhiteboard() ? loc.getMenu("Button.Caption")
						: loc.getMenu("Button.Caption") + ":");
		if (app.isUnbundledOrWhiteboard()) {
			captionLabel.addStyleName("coloredLabel");
		}
		
		String initString = model.getInitString();
		InputPanelW ip = new InputPanelW(initString, app, 1, 25, true);
		tfCaption = ip.getTextComponent();
		if (tfCaption != null) {
			tfCaption.setAutoComplete(false);
		}

		VerticalPanel captionPanel = new VerticalPanel();
		captionPanel.add(captionLabel);
		captionPanel.add(ip);
		captionPanel.addStyleName("captionPanel");
				
		// combo box to link GeoElement to TextField
		TreeSet<GeoElement> sortedSet = app.getKernel().getConstruction()
				.getGeoSetNameDescriptionOrder();
		
		final ListBox cbAdd = new ListBox();
		cbAdd.addItem("");
		if (model.isTextField()) {
			// lists for combo boxes to select input and output objects
			// fill combobox models
			for (GeoElement geo : sortedSet) {
				if (!geo.isGeoImage() && !(geo.isGeoButton()) && !(geo.isGeoBoolean())) {
					String str = geo.toString(StringTemplate.defaultTemplate);
					cbAdd.addItem(str);
				}
			}	

			if (cbAdd.getItemCount() > 1) {
				cbAdd.addChangeHandler(event -> updateModel(cbAdd));
			}
		}

		Label scriptLabel = new Label(
				app.isUnbundledOrWhiteboard()
				? loc.getMenu("Script") : loc.getMenu("Script") + ":");
		if (app.isUnbundledOrWhiteboard()) {
			scriptLabel.addStyleName("coloredLabel");
		}
		tfScript = new ScriptArea((AppW) app);
		
		tfScript.enableGGBKeyboard();

		FlowPanel scriptPanel = new FlowPanel();
		scriptPanel.add(scriptLabel);
		scriptPanel.add(tfScript);

		VerticalPanel linkedPanel = new VerticalPanel();
		Label linkedLabel = new Label(
				app.isUnbundledOrWhiteboard() ? loc.getMenu("LinkedObject")
						: loc.getMenu("LinkedObject") + ":");
		if (app.isUnbundledOrWhiteboard()) {
			linkedLabel.addStyleName("coloredLabel");
		}
		linkedPanel.add(linkedLabel);
		linkedPanel.add(cbAdd);
			
		FlowPanel contentPanel = new FlowPanel();
		
		// create object list
		contentPanel.add(captionPanel);

		if (model.isTextField()) {
			contentPanel.add(linkedPanel);
		}
		else {
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
	protected void updateModel(ListBox cbAdd) {
		String text = cbAdd.getItemText(cbAdd.getSelectedIndex());
		if ("".equals(text.trim())) {
			model.setLinkedGeo(null);
		}
		GeoElement geo = getGeo(text);
		if (geo == null) {
			return;
		}
		model.setLinkedGeo(geo);
	}

	private GeoElement getGeo(String text) {
		TreeSet<GeoElement> sortedSet1 = app.getKernel().getConstruction()
				.getGeoSetNameDescriptionOrder();
		Iterator<GeoElement> it1 = sortedSet1.iterator();
		while (it1.hasNext()) {
			GeoElement geo = it1.next();
			if (text.equals(geo.toString(StringTemplate.defaultTemplate))) {
				return geo;
			}
		}
		return null;
	}
}
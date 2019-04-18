package org.geogebra.web.full.gui.dialog;

import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.gui.dialog.ButtonDialogModel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.util.ScriptArea;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Dialog for creating buttons and inputboxes
 *
 */
public class ButtonDialogW extends DialogBoxW implements ClickHandler, HasKeyboardPopup {

	private AutoCompleteTextFieldW tfCaption;
	private FlowPanel btPanel;
	private ButtonDialogModel model;
	private Button btOK;
	private Button btCancel;
	private FlowPanel optionPane;
	private ScriptArea tfScript;
	private Localization loc;
	
	/**
	 * @param app
	 *            app
	 * @param x
	 *            position
	 * @param y
	 *            position
	 * @param inputBox
	 *            whether this is for inputbox
	 */
	public ButtonDialogW(final AppW app, int x, int y, boolean inputBox) {
		super(false, true, null, app.getPanel(), app);

		this.loc = app.getLocalization();
		model = new ButtonDialogModel(app, x, y, inputBox);
		addStyleName("GeoGebraPopup");
		createGUI();	
		this.setGlassEnabled(true);
		this.setVisible(true);
		center();
		if (!app.isWhiteboardActive()) {
			app.registerPopup(this);
		}

		this.addCloseHandler(new CloseHandler<GPopupPanel>() {
			@Override
			public void onClose(CloseEvent<GPopupPanel> event) {
				app.unregisterPopup(ButtonDialogW.this);
				app.hideKeyboard();
			}
		});
	}

	private void createGUI() {
		if (model.isTextField()) {
			this.getCaption().setText(loc.getMenu("TextFieldAction"));
		}
		else {
			this.getCaption().setText(loc.getMenu("ButtonAction"));
		}
		
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
				cbAdd.addChangeHandler(new ChangeHandler() {

					@Override
					public void onChange(ChangeEvent event) {
						updateModel(cbAdd);
					}
				});
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
		
		// buttons
		btOK = new Button(loc.getMenu("OK"));
		btOK.getElement().setAttribute("action", "OK");
		btOK.addClickHandler(this);
		btCancel = new Button(loc.getMenu("Cancel"));
		btCancel.getElement().setAttribute("action", "Cancel");
		btCancel.addClickHandler(this);
		btCancel.addStyleName("cancelBtn");
		btPanel = new FlowPanel();
		btPanel.add(btOK);
		btPanel.add(btCancel);
		btPanel.addStyleName("DialogButtonPanel");
			
		optionPane = new FlowPanel();
		
		// create object list
		optionPane.add(captionPanel);

		if (model.isTextField()) {
			optionPane.add(linkedPanel);
		}
		else {
			optionPane.add(scriptPanel);
		}
		
		optionPane.add(btPanel);	
		//Make this dialog display it.	
		setWidget(optionPane);
		this.addStyleName("buttonDialog");
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

	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource();				
		if (source == btOK) {
			model.apply(tfCaption.getText(), tfScript.getText());
			hide();
			app.getActiveEuclidianView().requestFocusInWindow();
		} 
		else if (source == btCancel) {
			hide();
			app.getActiveEuclidianView().requestFocusInWindow();
		} 
	}

}

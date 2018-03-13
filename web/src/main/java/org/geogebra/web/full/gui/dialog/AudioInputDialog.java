package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 *
 */
public class AudioInputDialog extends DialogBoxW implements FastClickHandler {
	private AppW appW;
	private FlowPanel mainPanel;
	private FlowPanel inputPanel;
	private FlowPanel buttonPanel;
	private FormLabel inputLabel;
	private InputPanelW inputField;
	private FormLabel errorLabel;
	private StandardButton insertBtn;
	private StandardButton cancelBtn;

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	public AudioInputDialog(AppW app) {
		super(app.getPanel(), app);
		this.appW = app;
		initGui();
		initActions();
	}

	private void initGui() {
		mainPanel = new FlowPanel();
		inputPanel = new FlowPanel();
		inputPanel.setStyleName("mowAudioDialogContent");
		inputPanel.addStyleName("emptyState");
		inputLabel = new FormLabel();
		inputLabel.addStyleName("inputLabel");
		inputField = new InputPanelW("Paste link", appW, 1, 25, false);
		inputField.addStyleName("inputText");
		inputPanel.add(inputLabel);
		inputPanel.add(inputField);
		errorLabel = new FormLabel();
		errorLabel.addStyleName("errorLabel");
		inputPanel.add(errorLabel);
		// panel for buttons
		insertBtn = new StandardButton("", appW);
		insertBtn.addStyleName("insertBtn");
		insertBtn.setEnabled(false);
		cancelBtn = new StandardButton("", app);
		cancelBtn.addStyleName("cancelBtn");
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		buttonPanel.add(cancelBtn);
		buttonPanel.add(insertBtn);
		// add panels
		add(mainPanel);
		mainPanel.add(inputPanel);
		mainPanel.add(buttonPanel);
		// style
		addStyleName("GeoGebraPopup");
		addStyleName("audioDialog");
		setGlassEnabled(true);
		setLabels();
	}

	private void initActions() {
		insertBtn.addFastClickHandler(this);
		cancelBtn.addFastClickHandler(this);
	}

	/**
	 * set button labels and dialog title
	 */
	public void setLabels() {
		getCaption().setText(appW.getLocalization().getMenu("Audio")); // dialog
																		// title
		inputLabel.setText(appW.getLocalization().getMenu("Link"));
		errorLabel.setText(appW.getLocalization().getMenu("Error")
				+ ": " + appW.getLocalization().getError("InvalidInput"));
		insertBtn.setText(appW.getLocalization().getMenu("Insert")); // insert
		cancelBtn.setText(appW.getLocalization().getMenu("Cancel")); // cancel
	}

	@Override
	public void onClick(Widget source) {
		if (source == cancelBtn) {
			hide();
		} else if (source == insertBtn) {
			if (appW.getGuiManager() != null) {
				appW.getGuiManager().addAudio();
				hide();
			}
		}
	}

	@Override
	public void hide() {
		appW.getGuiManager().setMode(EuclidianConstants.MODE_MOVE,
				ModeSetter.TOOLBAR);
		super.hide();
	}
}

package org.geogebra.web.full.gui.dialog;

import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author csilla
 *
 */
public class AudioInputDialog extends DialogBoxW {
	private AppW appW;
	private FlowPanel mainPanel;
	private SimplePanel inputPanel;
	private FlowPanel buttonPanel;
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
	}

	private void initGui() {
		mainPanel = new FlowPanel();
		inputPanel = new SimplePanel();
		inputPanel.setStyleName("mowAudioSimplePanel");
		// panel for buttons
		insertBtn = new StandardButton("", appW);
		insertBtn.setEnabled(true);
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
		setGlassEnabled(true);
		setLabels();
	}

	/**
	 * set button labels and dialog title
	 */
	public void setLabels() {
		getCaption().setText(appW.getLocalization().getMenu("Audio")); // dialog
																		// title
		insertBtn.setText(appW.getLocalization().getMenu("Insert")); // insert
		cancelBtn.setText(appW.getLocalization().getMenu("Cancel")); // cancel
	}

}

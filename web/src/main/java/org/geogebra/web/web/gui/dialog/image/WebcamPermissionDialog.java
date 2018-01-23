package org.geogebra.web.web.gui.dialog.image;

import org.geogebra.common.GeoGebraConstants.Versions;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.dialog.DialogBoxW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * dialog to ask user for webcam permission and show error messages
 * 
 * @author Alicia
 *
 */
public class WebcamPermissionDialog extends DialogBoxW implements ClickHandler {
	private AppW app1;
	private FlowPanel mainPanel;
	private FlowPanel buttonPanel;
	private Button cancelBtn;
	private Label text;
	private DialogType dialogType;

	/**
	 * type of dialog
	 *
	 */
	protected enum DialogType {
		/** request permission to use webcam */
		PERMISSION_REQUEST,
		/** permission request denied by user */
		PERMISSION_DENIED,
		/** problem communicating with the webcam */
		ERROR,
		/** webcam not supported by browser */
		NOT_SUPPORTED
	}

	/**
	 * @param app
	 *            application
	 * @param dialogType
	 *            type of dialog (PERMISSION_REQUEST, PERMISSION_DENIED, ERROR,
	 *            NOT_SUPPORTED)
	 */
	public WebcamPermissionDialog(AppW app, DialogType dialogType) {
		super(app.getPanel(), app);
		this.app1 = app;
		this.dialogType = dialogType;
		initGUI();
	}

	private void initGUI() {
		text = new Label();
		cancelBtn = new Button("");
		cancelBtn.addClickHandler(this);
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		buttonPanel.add(cancelBtn);

		mainPanel = new FlowPanel();
		mainPanel.add(text);
		mainPanel.add(buttonPanel);
		add(mainPanel);
		addStyleName("GeoGebraPopup");
		addStyleName("mowPermissionDialog");
	}

	/**
	 * set button labels and dialog title
	 */
	public void setLabels() {
		Localization loc = app1.getLocalization();
		String message = "";
		String caption = "";
		cancelBtn.setText(loc.getMenu("OK"));

		switch (dialogType) {
		case PERMISSION_REQUEST:
			caption = loc.getMenu("Webcam.Request");
			if (app1.getVersion() == Versions.WEB_FOR_DESKTOP) {
				message = "";
			} else if (Browser.isFirefox()) {
				message = loc.getMenu("Webcam.Firefox");
			} else if (Browser.isEdge()) {
				message = loc.getMenu("Webcam.Edge");
			} else {
				message = loc.getMenu("Webcam.Chrome");
			}
			cancelBtn.setText(loc.getMenu("Cancel"));
			break;
		case PERMISSION_DENIED:
			caption = loc.getMenu("Webcam.Denied.Caption");
			message = loc.getMenu("Webcam.Denied.Message");
			break;
		case ERROR:
			caption = loc.getMenu("Webcam.Problem");
			message = loc.getMenu("Webcam.Problem.Message");
			break;
		case NOT_SUPPORTED:
			caption = loc.getMenu("Webcam.Notsupported.Caption");
			message = loc.getMenu("Webcam.Notsupported.Message");
			break;
		}
		text.setText(message);
		getCaption().setText(caption);
	}

	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if (source == cancelBtn) {
			cancel();
		}
	}

	@Override
	public void center() {
		super.center();
		setLabels();
	}

	private void cancel() {
		hide();
		app1.getGuiManager().setMode(EuclidianConstants.MODE_MOVE,
				ModeSetter.TOOLBAR);
	}
}

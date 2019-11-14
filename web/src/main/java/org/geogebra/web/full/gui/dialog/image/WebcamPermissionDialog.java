package org.geogebra.web.full.gui.dialog.image;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.laf.VendorSettings;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

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
	private Button dismissBtn;
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
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.add(text);
		add(mainPanel);
		addStyleName("GeoGebraPopup");
		addStyleName("mowPermissionDialog");

		if (dialogType != DialogType.PERMISSION_REQUEST) {
			dismissBtn = new Button("");
			dismissBtn.addClickHandler(this);
			FlowPanel buttonPanel = new FlowPanel();
			buttonPanel.setStyleName("DialogButtonPanel");
			buttonPanel.add(dismissBtn);
			mainPanel.add(buttonPanel);
		} else {
			addStyleName("noButtons");
		}
	}

	/**
	 * set button labels and dialog title
	 */
	public void setLabels() {
		Localization loc = app1.getLocalization();
		VendorSettings settings = app1.getVendorSettings();
		String messageKey;
		String captionKey;
		String message = "";
		String caption = "";
		if (dialogType != DialogType.PERMISSION_REQUEST) {
			dismissBtn.setText(loc.getMenu("OK"));
		}
		switch (dialogType) {
		case PERMISSION_REQUEST:
			captionKey = settings.getMenuLocalizationKey("Webcam.Request");
			messageKey = settings.getMenuLocalizationKey("Webcam.Request.Message");
			caption = loc.getMenu(captionKey);
			message = loc.getMenu(messageKey);
			break;
		case PERMISSION_DENIED:
			messageKey = settings.getMenuLocalizationKey(getPermissionDeniedMessageKey());
			caption = loc.getMenu(getPermissionDeniedTitleKey());
			message = loc.getMenu(messageKey);
			break;
		case ERROR:
			messageKey = settings.getMenuLocalizationKey("Webcam.Problem.Message");
			caption = loc.getMenu("Webcam.Problem");
			message = loc.getMenu(messageKey);
			break;
		case NOT_SUPPORTED:
			caption = loc.getMenu("Webcam.Notsupported.Caption");
			message = loc.getMenu("Webcam.Notsupported.Message");
			break;
		}
		text.setText(message);
		getCaption().setText(caption);
		if (message.length() < 80) {
			addStyleName("narrowDialog");
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if (source == dismissBtn) {
			cancel();
		}
	}

	@Override
	public void center() {
		setLabels();
		super.center();
	}

	private void cancel() {
		hide();
		app1.getGuiManager().setMode(EuclidianConstants.MODE_MOVE,
				ModeSetter.TOOLBAR);
	}

	private String getPermissionDeniedTitleKey() {
		return Browser.isElectron() && Browser.isMacOS() && !app1.isMebis() ? "permission.camera"
				+ ".denied" : "Webcam.Denied.Caption";
	}

	private String getPermissionDeniedMessageKey() {
		return Browser.isElectron() && Browser.isMacOS() && !app1.isMebis() ? "permission"
				+ ".request" : "Webcam.Denied.Message";
	}

}

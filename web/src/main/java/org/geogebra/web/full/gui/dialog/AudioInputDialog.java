package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 *
 */
public class AudioInputDialog extends MediaDialog {
	private AppW appW;
	private FlowPanel mainPanel;
	private FlowPanel buttonPanel;
	private FormLabel inputLabel;
	private Label errorLabel;
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
		inputField = new InputPanelW("", appW, 1, 25, false);
		inputLabel = new FormLabel().setFor(inputField.getTextComponent());
		inputLabel.addStyleName("inputLabel");
		inputField.getTextComponent().getTextBox().getElement().setAttribute(
				"placeholder", appW.getLocalization().getMenu("pasteLink"));
		inputField.addStyleName("inputText");
		inputPanel.add(inputLabel);
		inputPanel.add(inputField);
		errorLabel = new Label();
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
		inputField.getTextComponent().getTextBox()
				.addFocusHandler(new FocusHandler() {

					@Override
					public void onFocus(FocusEvent event) {
						getInputPanel().setStyleName("mowAudioDialogContent");
						getInputPanel().addStyleName("focusState");
					}
				});
		inputField.getTextComponent().getTextBox()
				.addBlurHandler(new BlurHandler() {

					@Override
					public void onBlur(BlurEvent event) {
						resetInputField();
					}
				});
		addInputHandler();
		inputField.getTextComponent().getTextBox()
				.addKeyUpHandler(new KeyUpHandler() {

					@Override
					public void onKeyUp(KeyUpEvent event) {
						if (event.getNativeEvent()
								.getKeyCode() == KeyCodes.KEY_ENTER) {
							processInput();
						} else {
							onInput();
						}
					}
				});

		addHoverHandlers();
		insertBtn.addFastClickHandler(this);
		cancelBtn.addFastClickHandler(this);
	}

	/**
	 * Resets input style after error.
	 */
	void resetInputField() {
		resetError();
	}

	/**
	 * @return insert button
	 */
	public StandardButton getInsertBtn() {
		return insertBtn;
	}

	/**
	 * set button labels and dialog title
	 */
	public void setLabels() {
		getCaption().setText(appW.getLocalization().getMenu("Audio")); // dialog
		// title
		inputLabel.setText(appW.getLocalization().getMenu("Link"));
		errorLabel.setText(appW.getLocalization().getMenu("Error") + ": "
				+ appW.getLocalization().getError("InvalidInput"));
		insertBtn.setText(appW.getLocalization().getMenu("Insert")); // insert
		cancelBtn.setText(appW.getLocalization().getMenu("Cancel")); // cancel
	}

	@Override
	public void onClick(Widget source) {
		if (source == cancelBtn) {
			hide();
		} else if (source == insertBtn) {
			processInput();
		}
	}

	/**
	 * Handles the URL user has typed.
	 */
	void processInput() {
		if (appW.getGuiManager() != null) {
			String url = getUrlWithProtocol();
			inputField.getTextComponent().setText(url);
			app.getSoundManager().checkURL(url,
					new AsyncOperation<Boolean>() {

						@Override
						public void callback(Boolean ok) {
							if (ok) {
								addAudio();
							} else {
								showError("error");
							}
						}
					});
		}
	}

	/**
	 * Adds the GeoAudio instance.
	 */
	void addAudio() {
		resetError();

		app.getGuiManager().addAudio(inputField.getText());
		hide();
	}

	private String getUrlWithProtocol() {
		String url = inputField.getText().trim();
		String value = isHTTPSOnly() ? url.replaceFirst(HTTP, "") : url;

		if (!url.startsWith(HTTPS)) {
			value = HTTPS + value;
		}

		return value;
	}

	/**
	 * 
	 * @return if accepted URLs are HTTPS only or not.
	 */
	public boolean isHTTPSOnly() {
		return true;
	}

	@Override
	public void hide() {
		appW.getGuiManager().setMode(EuclidianConstants.MODE_MOVE,
				ModeSetter.TOOLBAR);
		super.hide();
	}

	@Override
	public void showError(String msg) {
		inputPanel.setStyleName("mowAudioDialogContent");
		inputPanel.addStyleName("errorState");
		insertBtn.setEnabled(false);
	}

	@Override
	public void resetError() {
		getInputPanel().setStyleName("mowAudioDialogContent");
		getInputPanel().addStyleName("emptyState");
		inputPanel.removeStyleName("errorState");
		getInsertBtn().setEnabled(!"".equals(getInputField().getText()));
	}

	@Override
	public void onInput() {
		resetInputField();
		getInputPanel().addStyleName("focusState");
		getInputPanel().removeStyleName("emptyState");
	}
}

package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.FastClickHandler;
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
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 *
 */
public class AudioInputDialog extends DialogBoxW
		implements FastClickHandler, ErrorHandler {
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
		inputField = new InputPanelW("", appW, 1, 25, false);
		inputField.getTextComponent().getTextBox().getElement().setAttribute(
				"placeholder", appW.getLocalization().getMenu("pasteLink"));
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

		inputField.getTextComponent().getTextBox()
				.addKeyUpHandler(new KeyUpHandler() {

					@Override
					public void onKeyUp(KeyUpEvent event) {
						if (event.getNativeEvent()
								.getKeyCode() == KeyCodes.KEY_ENTER) {
							processInput();
						} else {
							resetInputField();
						}
					}
				});

		inputField.getTextComponent().getTextBox()
				.addMouseOverHandler(new MouseOverHandler() {

					@Override
					public void onMouseOver(MouseOverEvent event) {
						getInputPanel().addStyleName("hoverState");
					}
				});

		inputField.getTextComponent().getTextBox()
				.addMouseOutHandler(new MouseOutHandler() {

					@Override
					public void onMouseOut(MouseOutEvent event) {
						getInputPanel().removeStyleName("hoverState");
					}
				});
		insertBtn.addFastClickHandler(this);
		cancelBtn.addFastClickHandler(this);
	}

	void resetInputField() {
		resetError();
		getInsertBtn().setEnabled(!"".equals(getInputField().getText()));

	}

	/**
	 * @return panel holding input with label and error label
	 */
	public FlowPanel getInputPanel() {
		return inputPanel;
	}

	/**
	 * @return insert button
	 */
	public StandardButton getInsertBtn() {
		return insertBtn;
	}

	/**
	 * @return input field
	 */
	public InputPanelW getInputField() {
		return inputField;
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

	void processInput() {
		if (appW.getGuiManager() != null) {
			app.getSoundManager().checkURL(inputField.getText(),
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

	void addAudio() {
		resetError();
		app.getGuiManager().addAudio(inputField.getText());
		hide();
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
	}

	@Override
	public void showCommandError(String command, String message) {
		// not used but must be implemented
	}

	@Override
	public String getCurrentCommand() {
		return null;
	}

	@Override
	public boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		return false;
	}

	@Override
	public void resetError() {
		getInputPanel().setStyleName("mowAudioDialogContent");
		getInputPanel().addStyleName("emptyState");
		inputPanel.removeStyleName("errorState");
	}
}

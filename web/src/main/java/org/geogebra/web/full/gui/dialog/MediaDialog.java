package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Element;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Audio / video dialog.
 * 
 * @author Zbynek
 */
public abstract class MediaDialog extends OptionDialog
		implements ErrorHandler {
	/** http prefix */
	private static final String HTTP = "http://";
	/** https prefix */
	private static final String HTTPS = "https://";
	/**
	 * application
	 */
	protected AppW appW;
	private FlowPanel mainPanel;
	private FormLabel inputLabel;
	private Label errorLabel;
	/** input */
	protected InputPanelW inputField;
	private FlowPanel inputPanel;

	/**
	 * @param root
	 *            popup root
	 * @param app
	 *            app
	 */
	public MediaDialog(Panel root, AppW app) {
		super(root, app);
		this.appW = app;
		initGui();
		initActions();
	}

	private void initGui() {
		mainPanel = new FlowPanel();
		inputPanel = new FlowPanel();
		inputPanel.setStyleName("mowMediaDialogContent");
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
		// add panels
		add(mainPanel);
		mainPanel.add(inputPanel);
		mainPanel.add(getButtonPanel());
		// style
		addStyleName("GeoGebraPopup");
		addStyleName("mediaDialog");
		setGlassEnabled(true);
		setLabels();
	}

	private void initActions() {
		inputField.getTextComponent().getTextBox()
				.addFocusHandler(new FocusHandler() {

					@Override
					public void onFocus(FocusEvent event) {
						setFocusState();
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
	}

	/**
	 * Add handler for input event
	 */
	private void addInputHandler() {
		nativeon(inputField.getTextComponent().getInputElement());
	}

	/**
	 * Add mouse over/ out handlers
	 */
	private void addHoverHandlers() {
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
	}

	/**
	 * set button labels
	 */
	public void setLabels() {
		inputLabel.setText(appW.getLocalization().getMenu("Link"));
		errorLabel.setText(appW.getLocalization().getMenu("Error") + ": "
				+ appW.getLocalization().getError("InvalidInput"));
		updateButtonLabels("Insert");
	}

	/**
	 * @return url with https prefix
	 */
	protected String getUrlWithProtocol() {
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
	private static boolean isHTTPSOnly() {
		return true;
	}

	@Override
	public void hide() {
		appW.getGuiManager().setMode(EuclidianConstants.MODE_MOVE,
				ModeSetter.TOOLBAR);
		super.hide();
	}

	/**
	 * @return panel holding input with label and error label
	 */
	public FlowPanel getInputPanel() {
		return inputPanel;
	}

	@Override
	public void showError(String msg) {
		inputPanel.setStyleName("mowMediaDialogContent");
		inputPanel.addStyleName("errorState");
		setPrimaryButtonEnabled(false);
	}

	@Override
	public void resetError() {
		getInputPanel().setStyleName("mowMediaDialogContent");
		getInputPanel().addStyleName("emptyState");
		inputPanel.removeStyleName("errorState");
		setPrimaryButtonEnabled(!"".equals(inputField.getText()));
	}

	/**
	 * sets the style of InputPanel to focus state
	 */
	protected void setFocusState() {
		getInputPanel().setStyleName("mowMediaDialogContent");
		getInputPanel().addStyleName("focusState");
	}

	/**
	 * Resets input style on blur
	 */
	protected void resetInputField() {
		getInputPanel().removeStyleName("focusState");
		getInputPanel().addStyleName("emptyState");
	}

	/**
	 * Input changed (paste or key event happened)
	 */
	protected void onInput() {
		resetError();
		getInputPanel().addStyleName("focusState");
		getInputPanel().removeStyleName("emptyState");
	}

	private native void nativeon(Element img) /*-{
		var that = this;
		img.addEventListener("input", function() {
			that.@org.geogebra.web.full.gui.dialog.MediaDialog::onInput()();
		});
	}-*/;

	@Override
	public final void showCommandError(String command, String message) {
		// not used but must be implemented
	}

	@Override
	public final String getCurrentCommand() {
		return null;
	}

	@Override
	public final boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		return false;
	}
}

package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GeoVideo;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 *
 */
public class VideoInputDialog extends DialogBoxW
		implements FastClickHandler, ErrorHandler {
	private static final String HTTP = "http://";
	private static final String HTTPS = "https://";
	private AppW appW;
	private FlowPanel mainPanel;
	private FlowPanel inputPanel;
	private FlowPanel buttonPanel;
	private FormLabel inputLabel;
	private InputPanelW inputField;
	private Label errorLabel;
	private StandardButton insertBtn;
	private StandardButton cancelBtn;

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	public VideoInputDialog(AppW app) {
		super(app.getPanel(), app);
		this.appW = app;
		initGui();
		initActions();
	}

	private void initGui() {
		mainPanel = new FlowPanel();
		inputPanel = new FlowPanel();
		inputPanel.setStyleName("mowVideoDialogContent");
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
		addStyleName("videoDialog");
		setGlassEnabled(true);
		setLabels();
		inputField.getTextComponent().setText(GeoVideo.TEST_VIDEO_URL);
		insertBtn.setEnabled(true);

	}

	private void initActions() {
		inputField.getTextComponent().getTextBox()
				.addFocusHandler(new FocusHandler() {

					@Override
					public void onFocus(FocusEvent event) {
						getInputPanel().setStyleName("mowVideoDialogContent");
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
							getInputPanel().addStyleName("focusState");
							getInputPanel().removeStyleName("emptyState");
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
	 * Resets input style after error.
	 */
	public void resetInputField() {
		resetError();
		getInsertBtn().setEnabled(!"".equals(getInputField().getText()));
	}

	/**
	 * set button labels and dialog title
	 */
	public void setLabels() {
		getCaption().setText(appW.getLocalization().getMenu("Video")); // dialog
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
	protected void processInput() {
		if (appW.getGuiManager() != null) {
			String url = getUrlWithProtocol();
			inputField.getTextComponent().setText(url);
			app.getVideoManager().checkURL(url, new AsyncOperation<Boolean>() {

				@Override
				public void callback(Boolean ok) {
					if (ok) {
						addVideo();
					} else {
						showError("error");
					}
				}
			});
		}
	}

	/**
	 * Adds the GeoVideo instance.
	 */
	protected void addVideo() {
		resetError();
		app.getGuiManager().addVideo(inputField.getText());
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
	private static boolean isHTTPSOnly() {
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
		inputPanel.setStyleName("mowVideoDialogContent");
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
		getInputPanel().setStyleName("mowVideoDialogContent");
		getInputPanel().addStyleName("emptyState");
		inputPanel.removeStyleName("errorState");
	}
}

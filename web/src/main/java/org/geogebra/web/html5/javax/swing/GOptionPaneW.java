package org.geogebra.web.html5.javax.swing;

import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Generates simple option and input dialogs modeled after the dialogs generated
 * by the JOptionPane class.
 * 
 */
public class GOptionPaneW extends DialogBox implements GOptionPane,
        ClickHandler {

	private App app;
	private String title, message, initialSelectionValue;
	private Button btnOK, btnCancel;
	private Button[] optionButtons;
	private String[] optionNames;
	private int optionType, messageType;
	private boolean requiresReturnValue;
	private Localization loc;
	private AsyncOperation returnHandler;

	private AutoCompleteTextFieldW inputField;
	private FlowPanel mainPanel;
	private FlowPanel buttonPanel;
	private int returnOption;
	private String returnValue;
	private VerticalPanel messageTextPanel;

	private Image icon;
	private HorizontalPanel messagePanel;
	private String okLabel = null;

	private static FocusWidget caller;

	/**
	 * Singleton instance of GOptionPaneW. Provides entry point for all calls to
	 * show a dialog or access getters/setters.
	 */
	public static GOptionPaneW INSTANCE = new GOptionPaneW();

	/**
	 * A private constructor is used to force use of singleton instance.
	 */
	private GOptionPaneW() {
		super(false, true);
		createGUI();
	}

	private void showDialog(boolean autoComplete) {
		loc = app.getLocalization();
		updateGUI();
		center();
		if (inputField != null) {
			inputField.setAutoComplete(autoComplete);
		}
		show();
	}

	protected void close() {

		if (requiresReturnValue) {
			if (returnOption == GOptionPane.CANCEL_OPTION) {
				returnValue = initialSelectionValue;
			} else {
				returnValue = inputField.getText();
			}
		}

		if (returnHandler != null) {
			App.debug("option: " + returnOption + "  value: " + returnValue);
			String[] dialogResult = { returnOption + "", returnValue };
			returnHandler.callback(dialogResult);

		}

		// return the focus to the input field calling this dialog
		if (caller != null)
			caller.setFocus(true);
		caller = null;

		hide();
	}

	public static void setCaller(FocusWidget c) {
		caller = c;
	}

	@Override
	public void setGlassEnabled(boolean enabled) {
		super.setGlassEnabled(enabled);
	}

	private void createGUI() {

		setGlassEnabled(true);
		addStyleName("DialogBox");

		btnOK = new Button();
		btnOK.addClickHandler(this);

		btnCancel = new Button();
		btnCancel.addClickHandler(this);

		buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("DialogButtonPanel");

		messagePanel = new HorizontalPanel();
		messagePanel.addStyleName("Dialog-messagePanel");
		messagePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		messageTextPanel = new VerticalPanel();

		mainPanel = new FlowPanel();
		mainPanel.addStyleName("Dialog-content");

	}

	private void updateGUI() {

		mainPanel.clear();

		updateMessagePanel();
		mainPanel.add(messagePanel);

		if (requiresReturnValue) {
			updateInputField();
			mainPanel.add(inputField);
		}

		updateButtonPanel();
		mainPanel.add(buttonPanel);

		clear();
		add(mainPanel);
		setText(title);
	}

	private void updateButtonPanel() {

		buttonPanel.clear();

		switch (optionType) {
		case GOptionPane.CUSTOM_OPTION:
			optionButtons = new Button[optionNames.length];
			for (int i = optionNames.length - 1; i >= 0; i--) {
				optionButtons[i] = new Button(optionNames[i]);
				optionButtons[i].addClickHandler(this);
				buttonPanel.add(optionButtons[i]);
			}
			break;

		case GOptionPane.OK_OPTION:
		case GOptionPane.DEFAULT_OPTION:
			buttonPanel.add(btnOK);
			setLabels();
			break;

		case GOptionPane.OK_CANCEL_OPTION:
			buttonPanel.add(btnOK);
			buttonPanel.add(btnCancel);
			setLabels();
			break;

		default:
			buttonPanel.add(btnOK);
			setLabels();
		}

	}

	private void updateMessagePanel() {

		messagePanel.clear();
		messageTextPanel.clear();

		updateIcon();
		if (icon != null) {
			messagePanel.add(icon);
		}

		String[] lines = message.split("\n");
		for (String item : lines) {
			messageTextPanel.add(new Label(item));
		}
		messagePanel.add(messageTextPanel);

	}

	private void setLabels() {
		btnOK.setText(okLabel == null ? loc.getPlain("OK") : okLabel);
		btnCancel.setText(loc.getPlain("Cancel"));
	}

	private void updateInputField() {

		if (inputField == null) {
			inputField = new AutoCompleteTextFieldW(app);
		}
		inputField.setText(initialSelectionValue);

	}

	private void updateIcon() {

		if (icon != null) {
			return;
		}

		switch (messageType) {

		case GOptionPane.ERROR_MESSAGE:
			icon = new Image(GuiResourcesSimple.INSTANCE.dialog_error()
			        .getSafeUri());
			break;
		case GOptionPane.INFORMATION_MESSAGE:
			icon = new Image(GuiResourcesSimple.INSTANCE.dialog_info()
			        .getSafeUri());
			break;
		case GOptionPane.WARNING_MESSAGE:
			icon = new Image(GuiResourcesSimple.INSTANCE.dialog_warning()
			        .getSafeUri());
			break;
		case GOptionPane.QUESTION_MESSAGE:
			icon = new Image(GuiResourcesSimple.INSTANCE.dialog_question()
			        .getSafeUri());
			break;
		case GOptionPane.PLAIN_MESSAGE:
			icon = null;
			break;
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		App.debug("source is: " + source);

		if (source == btnOK) {
			App.debug("btnOk");
			returnOption = GOptionPane.OK_OPTION;
			close();
		}

		if (source == btnCancel) {
			App.debug("btnCancel");
			returnOption = GOptionPane.CANCEL_OPTION;
			close();
		}

		if (optionButtons == null) {
			return;
		}

		for (int i = 0; i < optionButtons.length; i++) {
			if (source == optionButtons[i]) {
				returnOption = i;
				close();
			}
		}

	}

	/**
	 * Close the dialog on key events ENTER or ESC.
	 */
	@Override
	protected void onPreviewNativeEvent(final NativePreviewEvent event) {
		super.onPreviewNativeEvent(event);

		if (!isShowing()) {
			return;
		}

		if (event.getTypeInt() == Event.ONKEYUP) {
			int keyCode = event.getNativeEvent().getKeyCode();
			if (keyCode == KeyCodes.KEY_ESCAPE) {
				returnOption = GOptionPane.CANCEL_OPTION;
				close();

			} else if (keyCode == KeyCodes.KEY_ENTER) {
				returnOption = GOptionPane.OK_OPTION;
				close();
			}
		}
	}

	// ===================================================================
	// Dialog Launching Methods
	// ===================================================================

	/**
	 * Launches a confirm dialog.
	 */
	public int showConfirmDialog(App app, String message, String title,
	        int optionType, int messageType, Object icon) {

		this.app = app;
		this.message = message;
		this.title = title;
		this.optionType = optionType;
		this.messageType = messageType;
		this.icon = (Image) icon;

		this.returnHandler = null;
		requiresReturnValue = false;

		showDialog(true);
		showDialog(true);

		return returnOption;

	}

	/**
	 * Launches a customizable option dialog.
	 * 
	 * The dialog result is returned in the parameter of the handler callback
	 * function as an array of two strings: <br>
	 * 
	 * dialogResult[0] = returnOption <br>
	 * dialogResult[1] = returnValue <br>
	 * 
	 * (Note that returnValue is meaningless here.)
	 */
	public void showOptionDialog(App app, String message, String title,
	        int optionType, int messageType, Object icon, String[] optionNames,
	        AsyncOperation handler) {

		this.app = app;
		this.message = message;
		this.title = title;
		this.optionType = optionType;
		this.messageType = messageType;
		this.icon = (Image) icon;

		this.optionNames = optionNames;
		this.returnHandler = handler;
		requiresReturnValue = false;

		showDialog(true);

	}

	/**
	 * Launches a simple input dialog. The dialog result is returned in the
	 * parameter of the handler callback function as an array of two strings: <br>
	 * 
	 * dialogResult[0] = returnOption <br>
	 * dialogResult[1] = returnValue
	 * 
	 */
	public void showInputDialog(App app, String message,
			String initialSelectionValue, Object icon, AsyncOperation handler,
			boolean autoComplete) {

		this.app = app;
		this.message = message;
		this.title = null;
		this.optionType = GOptionPane.OK_CANCEL_OPTION;
		this.messageType = GOptionPane.PLAIN_MESSAGE;
		this.icon = (Image) icon;

		this.initialSelectionValue = initialSelectionValue;
		this.returnHandler = handler;
		requiresReturnValue = true;

		showDialog(autoComplete);

	}

	public void showSaveDialog(App app, String message,
			String initialSelectionValue,
 Object icon, AsyncOperation handler,
			String okLabel) {

		this.app = app;
		this.message = message;
		this.title = null;
		this.okLabel = okLabel;
		this.optionType = GOptionPane.OK_CANCEL_OPTION;
		this.messageType = GOptionPane.PLAIN_MESSAGE;
		this.icon = (Image) icon;

		this.initialSelectionValue = initialSelectionValue;
		this.returnHandler = handler;
		requiresReturnValue = true;

		showDialog(false);

	}

	/**
	 * Launches a customizable input dialog. The dialog result is returned in
	 * the parameter of the handler callback function as an array of two
	 * strings: <br>
	 * 
	 * dialogResult[0] = returnOption <br>
	 * dialogResult[1] = returnValue
	 * 
	 */
	public void showInputDialog(App app, String message, String title,
	        String initialSelectionValue, int optionType, int messageType,
	        Object icon, String[] optionNames, AsyncOperation handler) {

		this.app = app;
		this.message = message;
		this.title = title;
		this.optionType = optionType;
		this.messageType = messageType;
		this.icon = (Image) icon;

		this.optionNames = optionNames;
		this.initialSelectionValue = initialSelectionValue;
		this.returnHandler = handler;
		requiresReturnValue = true;

		showDialog(true);

	}

	public void showInputDialog(App app, String message,
			String initialSelectionValue, Object icon,
 AsyncOperation handler) {

		showInputDialog(app, message, initialSelectionValue, icon,
 handler,
				true);

	}
	public void test(App app, int type) {

		switch (type) {
		case 1:
			showConfirmDialog(app, "Something went wrong.", "Error Dialog",
			        GOptionPane.OK_CANCEL_OPTION, GOptionPane.ERROR_MESSAGE,
			        null);
			break;
		case 2:
			showConfirmDialog(app, "The beer is free.", "Information Dialog",
			        GOptionPane.OK_CANCEL_OPTION,
			        GOptionPane.INFORMATION_MESSAGE, null);
			break;
		case 3:
			showConfirmDialog(app, "Is the beer free?", "Question Dialog",
			        GOptionPane.OK_CANCEL_OPTION, GOptionPane.QUESTION_MESSAGE,
			        null);
			break;
		case 4:
			showConfirmDialog(app, "Watch out!", "Warning Dialog",
			        GOptionPane.OK_CANCEL_OPTION, GOptionPane.WARNING_MESSAGE,
			        null);
		}
	}
}

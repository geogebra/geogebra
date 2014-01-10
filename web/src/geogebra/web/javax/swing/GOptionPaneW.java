package geogebra.web.javax.swing;

import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.util.AsyncOperation;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Generates simple option and input dialogs modeled after the dialogs generated
 * by the JOptionPane class.
 * 
 * TODO: support for icons  
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

	private AutoCompleteTextFieldW inputField;
	private FlowPanel mainPanel;
	private FlowPanel buttonPanel;
	private int returnOption;
	private String returnValue;
	private VerticalPanel messagePanel;

	/**
	 * Singleton instance of GOptionPaneW. Provides an entry point for all calls
	 * to show dialogs or access getters/setters.
	 */
	public static GOptionPane INSTANCE = new GOptionPaneW();

	private static FocusWidget caller;
	/**
	 * Constructor.
	 */
	public GOptionPaneW() {
		super(false, true);
		this.setGlassEnabled(true);
		this.addStyleName("DialogBox");
		createGUI();
	}

	private void showDialog() {
		loc = app.getLocalization();
		updateGUI();
		center();
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
		if (caller != null) caller.setFocus(true);
		caller = null;
		hide();
	}

	public static void setCaller(FocusWidget c){
		caller = c;
	}
	
	public String getReturnValue() {
		return returnValue;
	}

	public int getReturnOption() {
		return returnOption;
	}

	private void createGUI() {
		btnOK = new Button();
		btnOK.addClickHandler(this);

		btnCancel = new Button();
		btnCancel.addClickHandler(this);

		buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("DialogButtonPanel");

		messagePanel = new VerticalPanel();
		messagePanel.addStyleName("Dialog-messagePanel");

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
			for (int i = optionNames.length-1; i >= 0; i--) {
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
		}
	}

	private void updateMessagePanel() {

		messagePanel.clear();
		String[] lines = message.split("\n");
		for (String item : lines) {
			messagePanel.add(new Label(item));
		}

	}

	private void setLabels() {
		btnOK.setText(loc.getPlain("OK"));
		btnCancel.setText(loc.getPlain("Cancel"));
	}

	private void updateInputField() {

		if (inputField == null) {
			inputField = new AutoCompleteTextFieldW(app);
		}
		inputField.setText(initialSelectionValue);

	}

	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource();

		if (source == btnOK) {
			returnOption = GOptionPane.OK_OPTION;
			close();
		}

		if (source == btnCancel) {
			returnOption = GOptionPane.CANCEL_OPTION;
			close();
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
	 * 
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

	public int showConfirmDialog(App app, String message, String title,
	        int optionType, int messageType) {

		this.app = app;
		this.message = message;
		this.title = title;
		this.optionType = optionType;
		requiresReturnValue = false;

		showDialog();

		return returnOption;

	}

	//TODO: remove this method use showOptionDialog instead of this
	public void showOptionDialog2(App app, String message,
            String title, String[] options, final AsyncOperation handler) {
		
		DialogBox dialogbox = new DialogBox();
	
		final PopupPanel dialog = new PopupPanel(false, true);
		
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("DialogButtonPanel");
		for (int i = 0; i<options.length; i++){
			Button bt = new Button(options[i]);
			final int selectedOption = i;
			bt.addClickHandler(new ClickHandler(){
				public void onClick(ClickEvent event){
					handler.callback(selectedOption);
					dialog.hide();
				}
			});
			buttonPanel.add(bt);
		}
		
		FlowPanel panel = new FlowPanel();
		panel.add(new Label(message));
		panel.add(buttonPanel);

		dialog.setWidget(panel);
		dialog.center();
		dialog.show();
	
    }

	public void showOptionDialog(App app, String message, String title,
	        int optionType, int messageType, String[] optionNames,
	        Object closeHandler) {

		this.app = app;
		this.message = message;
		this.title = title;
		this.optionType = optionType;
		this.optionNames = optionNames;
		this.addCloseHandler((CloseHandler<PopupPanel>) closeHandler);
		requiresReturnValue = false;

		showDialog();

	}

	
	public void showInputDialog(App app, String message,
	        String initialSelectionValue, Object closeHandler) {

		this.app = app;
		this.message = message;
		this.title = null;
		this.initialSelectionValue = initialSelectionValue;
		this.addCloseHandler((CloseHandler<PopupPanel>) closeHandler);
		this.optionType = GOptionPane.OK_CANCEL_OPTION;
		requiresReturnValue = true;

		showDialog();

	}
}

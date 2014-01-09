package geogebra.web.gui.dialog;

import geogebra.common.main.App;
import geogebra.common.main.Localization;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Dialog to handle command input errors.
 * 
 */
public class ErrorMessageDialog extends DialogBox implements ClickHandler {

	private App app;
	private String command, message;
	private Button btnOK;
	private Button btnShowHelp;

	public ErrorMessageDialog(App app, String command, String message) {

		super(false, true);

		this.app = app;
		this.message = message;
		this.command = command;
		this.addStyleName("DialogBox");

		Localization loc = app.getLocalization();
		this.setText(loc.getPlain("ApplicationName") + " - "
		        + loc.getError("Error"));

		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("DialogButtonPanel");

		if (command != null) {
			btnShowHelp = new Button(loc.getPlain("ShowOnlineHelp"));
			btnShowHelp.addClickHandler(this);
			buttonPanel.add(btnShowHelp);
		}

		btnOK = new Button(loc.getPlain("OK"));
		btnOK.addClickHandler(this);
		buttonPanel.add(btnOK);

		VerticalPanel panel = new VerticalPanel();
		panel.setSpacing(6);
		String[] lines = message.split("\n");
		for (String item : lines) {
			panel.add(new Label(item));
		}

		panel.add(buttonPanel);

		add(panel);

	}

	protected void close() {
		hide();
		removeFromParent();
	}

	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource();

		if (source == btnOK) {
			close();
		} else if (source == this.btnShowHelp) {
			if (app.getGuiManager() != null) {
				app.getGuiManager().openCommandHelp(command);
			}
			close();
		}
	}

	
	/**
	 * Hide the dialog when on enter press.
	 */
	@Override
	protected void onPreviewNativeEvent(final NativePreviewEvent event) {
		super.onPreviewNativeEvent(event);
		
		if(event.getTypeInt() == Event.ONKEYUP &&
				event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER){
			close();
		}
	}

}

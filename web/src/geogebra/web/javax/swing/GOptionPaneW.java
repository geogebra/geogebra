package geogebra.web.javax.swing;

import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.main.App;
import geogebra.common.util.AsyncOperation;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.web.gui.view.algebra.InputPanelW;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GOptionPaneW implements GOptionPane{

	public static GOptionPane INSTANCE = new GOptionPaneW();

	public int showConfirmDialog(App app, String message,
			String title, int optionType, int messageType) {

		//		if (!(parentComponent instanceof Widget)){
		//			App.debug("First parameter of GOptionPaneD.showConfirmDialog(...) must be a Component.");
		//			return -1;		
		//		}

		final PopupPanel dialog = new PopupPanel(false, true);
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("DialogButtonPanel");

		if (optionType == GOptionPane.DEFAULT_OPTION) {

			Button ok = new Button("OK");
			ok.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					dialog.hide();
				}

			});
			buttonPanel.add(ok);
			ok.getElement().focus();
		} else {
			App.debug("Option type other then DEFAULT_OPTION - implementation needed");
		}

		VerticalPanel panel = new VerticalPanel();
		String[] lines = message.split("\n");
		for (String item : lines) {
			panel.add(new Label(item));
		}

		panel.add(buttonPanel);

		dialog.setWidget(panel);
		dialog.center();
		dialog.show();
		return 0;

	}

	public static void showInputDialog(AppW app, final String title, final AsyncOperation callback) {

		final PopupPanel dialog = new PopupPanel(false, true);
		FlowPanel mainPanel = new FlowPanel();
		
		Label titleLabel = new Label(title);
		mainPanel.add(titleLabel);
		
		final InputPanelW input = new InputPanelW(null, app, 1, -1, false);
		final AutoCompleteTextFieldW textField = input.getTextComponent();
;
		mainPanel.add(textField);
		textField.setText((String)callback.getData());
		Button ok = new Button("OK");
		ok.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				callback.setData(input.getText());
				callback.callback();
				dialog.hide();
			}
		});

		mainPanel.add(ok);
		ok.getElement().focus();

		Button cancel = new Button("Cancel");
		cancel.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				dialog.hide();
			}
		});

		mainPanel.add(cancel);


		dialog.setWidget(mainPanel);
		dialog.center();
		dialog.show();
	}

}

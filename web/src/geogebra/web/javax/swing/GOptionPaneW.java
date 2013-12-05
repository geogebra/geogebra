package geogebra.web.javax.swing;

import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.main.App;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
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

	public void showOptionDialog(App app, String message,
            String title, String[] options, final OptionHandler handler) {
		
		DialogBox dialogbox = new DialogBox();
	
		final PopupPanel dialog = new PopupPanel(false, true);
		
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("DialogButtonPanel");
		for (int i = 0; i<options.length; i++){
			Button bt = new Button(options[i]);
			final int selectedOption = i;
			bt.addClickHandler(new ClickHandler(){
				public void onClick(ClickEvent event){
					handler.selectOption(selectedOption);
					dialog.hide();
				}
			});
			buttonPanel.add(bt);
		}
		
		
//		if (optionType == GOptionPane.DEFAULT_OPTION) {
//
//			Button ok = new Button("OK");
//			ok.addClickHandler(new ClickHandler() {
//
//				public void onClick(ClickEvent event) {
//					dialog.hide();
//				}
//
//			});
//			buttonPanel.add(ok);
//			ok.getElement().focus();
//		} else {
//			App.debug("Option type other then DEFAULT_OPTION - implementation needed");
//		}
//
//		VerticalPanel panel = new VerticalPanel();
//		String[] lines = message.split("\n");
//		for (String item : lines) {
//			panel.add(new Label(item));
//		}
//
		
		FlowPanel panel = new FlowPanel();
		panel.add(new Label(message));
		panel.add(buttonPanel);

		dialog.setWidget(panel);
		dialog.center();
		dialog.show();
	
    }
	
	public abstract class OptionHandler{
		public abstract void selectOption(int i);
	}
	
}

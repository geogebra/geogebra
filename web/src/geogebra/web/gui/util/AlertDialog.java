package geogebra.web.gui.util;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author gabor
 *
 *	General Alert Dialog
 */
public class AlertDialog extends DialogBox {
	
	/**
	 * Creates an AlertDialog
	 */
	
	private VerticalPanel container;
	private Label msg;
	private Button ok;
	public static AlertDialog INSTANCE = null;
	
	protected AlertDialog() {
		setWidget(container = new VerticalPanel());
		HorizontalPanel textPanel = new HorizontalPanel();
		textPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		textPanel.add(msg = new Label(""));
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(ok = new Button("Ok"));
		buttonPanel.addStyleName("buttonPanel");
		ok.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				AlertDialog thisDialog = (AlertDialog) event.getSource();
				thisDialog.hide();
			}
		});
		container.add(textPanel);
		container.add(buttonPanel);
	}
	
	public static AlertDialog get(String text) {
		if (INSTANCE == null) {
			INSTANCE = new AlertDialog();
		}
		INSTANCE.msg.setText(text);
		INSTANCE.center();
		return INSTANCE;		
	}

}

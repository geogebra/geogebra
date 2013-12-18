package geogebra.touch.gui.dialogs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class ButtonPanel extends HorizontalPanel {

	private final Button ok = new Button();
	private final Button cancel = new Button();
	
	public ButtonPanel(final ButtonPanelListener listener) {
		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.setStyleName("buttonPanel");

		this.ok.addStyleName("ok");
		this.ok.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				listener.onOK();
			}
		});

		this.cancel.setStyleName("last");
		this.cancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				listener.onCancel();
			}
		});
		this.add(this.ok);
		this.add(this.cancel);
	}

	public void setOKText(String plain) {
		this.ok.setText(plain);
	}

	public void setCancelText(String plain) {
		this.cancel.setText(plain);
	}

}

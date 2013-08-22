package geogebra.touch;

import geogebra.common.main.Localization;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DefaultErrorHandler implements ErrorHandler {

	private ErrorPopup errorPopup;
	private final Localization loc;
	
	public DefaultErrorHandler(Localization loc){
		this.loc = loc;
	}
	private class ErrorPopup extends PopupPanel{
		private final Label title;
		private final Label infoText;
		private final VerticalPanel dialogPanel;
		private final Button okButton;
		
		public ErrorPopup(){
			super(true, true);
			this.title = new Label();
			this.infoText  = new Label();
			this.dialogPanel = new VerticalPanel();
			this.dialogPanel.add(this.title);
			this.dialogPanel.add(this.infoText);
			this.okButton = new Button();
			initOKButton();
			this.add(this.dialogPanel);
		}
		
		private void initOKButton() {
			
			this.okButton.addDomHandler(new ClickHandler() {

				@Override
				public void onClick(final ClickEvent event) {
					ErrorPopup.this.hide();
				}
			}, ClickEvent.getType());
			this.dialogPanel.add(this.okButton);
		}
		
		public void setLabels(Localization loc){
			this.title.setText(loc.getError("Error"));
			this.okButton.setText(loc.getPlain("OK"));
		}
		public void setText(String text){
			this.infoText.setText(text);
		}
	}
	
	
	
	
	
	@Override
	public void showError(String error) {
		if(this.errorPopup == null){
			this.errorPopup = new ErrorPopup();
			this.errorPopup.setLabels(this.loc);
		}
		this.errorPopup.setText(error);
		this.errorPopup.show();
		
	}

}

package geogebra.touch;

import geogebra.common.main.Localization;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

class DefaultErrorHandler implements ErrorHandler {

	private ErrorPopup errorPopup;
	private final Localization loc;
	
	DefaultErrorHandler(Localization loc){
		this.loc = loc;
	}
	private class ErrorPopup extends PopupPanel {
		private final VerticalPanel dialogPanel;
		
		private final FlowPanel titlePanel = new FlowPanel();
		private final Label title;
		
		private final HorizontalPanel textPanel;
		private final SVGResource iconWarning = TouchEntryPoint.getLookAndFeel().getIcons().icon_warning();
		private final Label infoText;
		
		private HorizontalPanel buttonContainer;
		private final Button okButton;
		
		public ErrorPopup(){
			super(true, true);
			this.dialogPanel = new VerticalPanel();
			
			this.title = new Label();
			this.addLabel();
			
			this.textPanel = new HorizontalPanel();
			this.infoText  = new Label();
			this.addText();
			
			this.okButton = new Button();
			initOKButton();
			
			this.add(this.dialogPanel);
			this.setStyleName("infoDialog");
		}
		
		private void addLabel() {
			this.title.setStyleName("title");
			this.titlePanel.add(this.title);
			this.titlePanel.setStyleName("titlePanel");
			this.dialogPanel.add(this.titlePanel);
		}
		
		private void addText() {
			final Panel iconPanel = new LayoutPanel();
			final String html = "<img src=\""
					+ this.iconWarning.getSafeUri().asString() + "\" />";
			iconPanel.getElement().setInnerHTML(html);
			iconPanel.setStyleName("iconPanel");
			this.textPanel.add(iconPanel);

			this.textPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			this.textPanel.add(this.infoText);

			this.textPanel.setStyleName("textPanel");
			this.dialogPanel.add(this.textPanel);
		}
		
		private void initOKButton() {
			this.buttonContainer = new HorizontalPanel();
			this.buttonContainer.setStyleName("buttonPanel");
			
			this.okButton.addDomHandler(new ClickHandler() {

				@Override
				public void onClick(final ClickEvent event) {
					ErrorPopup.this.hide();
				}
			}, ClickEvent.getType());
			this.okButton.addStyleName("last");
			this.buttonContainer.add(this.okButton);
			this.dialogPanel.add(this.buttonContainer);
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

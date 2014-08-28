package geogebra.web.gui.dialog;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.views.EventRenderable;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SaveUnsavedChanges extends DialogBox implements EventRenderable {
	
		private final Button cancelButton = new Button();
		private final Button saveButton = new Button();
		private final Button dontSaveButton = new Button();
		private final VerticalPanel dialogPanel;
		private HorizontalPanel buttonContainer;
		private Label infoText;
		private String consTitle;
		private final Localization loc;
		private Runnable callback = null;
		private final App app;

		public SaveUnsavedChanges(final App app) {
			super();
			this.app = app;
			this.loc = app.getLocalization();
			this.setGlassEnabled(true);
			this.addStyleName("saveUnsavedDialog");
			
			this.dialogPanel = new VerticalPanel();
			
			this.addText();
			this.addButtons();

			this.add(this.dialogPanel);

			setLabels();
			app.getLoginOperation().getView().add(this);
		}

		private void addButtons() {
			this.initCancelButton();
			this.initSaveButton();
			this.initDontSaveButton();

			this.buttonContainer = new HorizontalPanel();
			this.buttonContainer.add(this.saveButton);
			this.buttonContainer.add(this.dontSaveButton);
			this.buttonContainer.add(this.cancelButton);

			this.dialogPanel.add(this.buttonContainer);
		}

		private void addText() {
			this.infoText = new Label();
			this.infoText.addStyleName("infoText");
			this.dialogPanel.add(this.infoText);
		}

		private void initCancelButton() {
			this.cancelButton.addDomHandler(new ClickHandler() {

				@Override
				public void onClick(final ClickEvent event) {
					SaveUnsavedChanges.this.hide();
				}
			}, ClickEvent.getType());
		}

		private void initDontSaveButton() {
			this.dontSaveButton.addDomHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					onDontSave();
				}
			}, ClickEvent.getType());
		}

		void onDontSave() {
			this.app.setSaved();
			this.hide();

			if (this.callback != null) {
				this.callback.run();
			} else {
				App.debug("no callback");
			}
		}

		private void initSaveButton() {

			this.saveButton.addDomHandler(new ClickHandler() {

				@Override
				public void onClick(final ClickEvent event) {
					onSave();
				}
			}, ClickEvent.getType());
		}

		protected void onSave() {
			app.getGuiManager().save();
			this.hide();
			if (this.callback != null) {
				this.callback.run();
			} else {
				App.debug("no callback");
			}
		}

		public void setCallback(final Runnable callback) {
			this.callback = callback;
		}

		public void setLabels() {
			this.getCaption().setText(this.loc.getMenu("CloseFile"));
			this.infoText.setText(this.loc
					.getMenu("DoYouWantToSaveYourChanges"));
			this.cancelButton.setText(this.loc.getMenu("Cancel"));
			this.saveButton.setText(this.loc.getMenu("Save"));
			this.dontSaveButton.setText(this.loc.getMenu("DontSave"));
		}

		public void showIfNeeded() {
				if (!app.isSaved()) {
					show();
				} else {
					if (this.callback != null) {
						this.callback.run();
					}
				}
		}

		@Override
		public void show() {
			super.show();
			super.center();
		}
		
		@Override
        public void renderEvent(final BaseEvent event) {
	        // TODO Auto-generated method stub
	        
        }
}


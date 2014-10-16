package geogebra.web.gui.dialog;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.views.EventRenderable;
import geogebra.web.gui.GuiManagerW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SaveUnsavedChanges extends DialogBoxW implements EventRenderable {
	
		private final Button cancelButton = new Button();
		private final Button saveButton = new Button();
		private final Button dontSaveButton = new Button();
		private final VerticalPanel dialogPanel;
		private HorizontalPanel buttonContainer;
		private Label infoText;
		private String consTitle;
		private final Localization loc;
		Runnable runAfterSave = null;
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
			runCallback();
		}

		private void initSaveButton() {

			this.saveButton.addDomHandler(new ClickHandler() {

				@Override
				public void onClick(final ClickEvent event) {
					onSave();
				}
			}, ClickEvent.getType());
		}

		/**
		 * saves the file before running the callback (edit or new)
		 */
		protected void onSave() {
			((GuiManagerW) app.getGuiManager()).save(this.runAfterSave);
			hide();
		}

		/**
		 * shows the {@link SaveUnsavedChanges saveUnsavedChanges-Dialog} 
		 * if there are unsaved changes before edit another file or create a new one
		 */
		public void showIfNeeded() {
				if (!app.isSaved()) {
					show();
				} else {
					runCallback();
				}
		}

		/**
		 * run callback
		 */
		void runCallback() {
			if (runAfterSave != null) {
				runAfterSave.run();
				runAfterSave = null;
			} else {
				App.debug("no callback");
			}
		}
		
		/**
		 * set the callback to run after file was saved successfully (e.g. new / edit)
		 * @param callback Runnable
		 */
		public void setAfterSavedCallback(final Runnable callback) {
			this.runAfterSave = callback;
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

		public void setLabels() {
			this.getCaption().setText(this.loc.getMenu("Save"));
			this.infoText.setText(this.loc
					.getMenu("DoYouWantToSaveYourChanges"));
			this.cancelButton.setText(this.loc.getMenu("Cancel"));
			this.saveButton.setText(this.loc.getMenu("Save"));
			this.dontSaveButton.setText(this.loc.getMenu("DontSave"));
		}
}


package geogebra.web.gui.dialog;

import geogebra.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *  A dialog to ask the user to recover the autoSaved file
 */
public class RecoverAutoSavedDialog extends DialogBoxW {
	
	private AppW app;
	private Button cancelButton = new Button();
	private Button recoverButton = new Button();
	private VerticalPanel dialogPanel;
	private HorizontalPanel buttonContainer;
	private Label infoText;
	
	/**
	 * @param app {@link AppW}
	 */
	public RecoverAutoSavedDialog(AppW app) {
		super();
		this.app = app;
		initGUI();
		setLabels();
	}
	
	/**
	 * inits the dialogPanel with text and buttons and adds it to the Dialog.
	 */
	private void initGUI() {
		this.dialogPanel = new VerticalPanel();
		this.setWidget(this.dialogPanel);
		addText();
		addButtons();
	}
	
	/**
	 * adds the information-text to the dialogPanel
	 */
	private void addText() {
		this.infoText = new Label();
		this.infoText.addStyleName("infoText");
		this.dialogPanel.add(this.infoText);
	}
	
	/**
	 * inits the cancel and recover button and
	 * adds it to the buttonPanel.
	 */
	private void addButtons() {
		this.initCancelButton();
		this.initRecoverButton();

		this.buttonContainer = new HorizontalPanel();
		this.buttonContainer.add(this.recoverButton);
		this.buttonContainer.add(this.cancelButton);

		this.dialogPanel.add(this.buttonContainer);
	}
	
	private void initRecoverButton() {
	    this.recoverButton.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doRecover();
			}
		}, ClickEvent.getType());
    }
	
	/**
	 * opens the autoSaved file, deletes it from localStorage,
	 * starts autoSaving again and closes the dialog.
	 */
	void doRecover() {
		app.getFileManager().restoreAutoSavedFile();
		app.getFileManager().deleteAutoSavedFile();
		app.startAutoSave();
		this.hide();
	}

	private void initCancelButton() {
		this.cancelButton.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				 cancelRecover();
			}
		}, ClickEvent.getType());
	}

	/**
	 * Deletes the autoSaved file, starts autoSaving again and
	 * closes the dialog.
	 */
	void cancelRecover() {
		app.getFileManager().deleteAutoSavedFile();
		app.startAutoSave();
		this.hide();
	}
	
	/**
	 * set labels
	 */
	public void setLabels() {
		//TODO Translation needed
		this.getCaption().setText("Recover unsaved changes");
		this.infoText.setText("GeoGebra found unsaved changes. Do you want to recover or delete them?");
		this.cancelButton.setText(this.app.getLocalization().getMenu("Delete"));
		this.recoverButton.setText("Recover");
	}
	
	
	@Override
	public void show() {
		super.show();
		super.center();
	}
}

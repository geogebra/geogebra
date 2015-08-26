package org.geogebra.web.web.gui.dialog;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.main.AppWapplication;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *  A dialog to ask the user to recover the autoSaved file.
 *  Only used from {@link AppWapplication}
 */
public class RecoverAutoSavedDialog extends DialogBoxW {
	
	private AppWapplication app;
	private Button deleteButton = new Button();
	private Button recoverButton = new Button();
	private VerticalPanel dialogPanel;
	private FlowPanel buttonContainer;
	private Label infoText;
	
	/**
	 * only used from {@link AppWapplication}
	 * @param app {@link AppW}
	 */
	public RecoverAutoSavedDialog(AppWapplication app) {
		super(app.getPanel());
		this.addStyleName("RecoverAutoSavedDialog");
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
		this.initDeleteButton();
		this.initRecoverButton();

		this.buttonContainer = new FlowPanel();
		this.buttonContainer.addStyleName("buttonPanel");
		this.buttonContainer.add(this.recoverButton);
		this.buttonContainer.add(this.deleteButton);

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

	private void initDeleteButton() {
		this.deleteButton.addDomHandler(new ClickHandler() {

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
		this.getCaption().setText(app.getMenu("RecoverUnsaved"));
		this.infoText.setText(app.getMenu("UnsavedChangesFound"));
		this.deleteButton.setText(this.app.getLocalization().getPlain("Delete"));
		this.recoverButton.setText(app.getMenu("Recover"));
	}
	
	@Override
	public void show() {
		super.show();
		super.center();
	}
}
package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.main.Localization;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *  A dialog to ask the user to recover the autoSaved file.
 */
public class RecoverAutoSavedDialog extends DialogBoxW {
	
	private AppWFull appw;
	private Button deleteButton = new Button();
	private Button recoverButton = new Button();
	private VerticalPanel dialogPanel;
	private Label infoText;
	private String materialJSON;
	
	/**
	 * only used from {@link AppWFull} with menu
	 * 
	 * @param app
	 *            {@link AppW}
	 */
	public RecoverAutoSavedDialog(AppWFull app) {
		super(app.getPanel(), app);
		this.addStyleName("RecoverAutoSavedDialog");
		this.appw = app;
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

		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.addStyleName("buttonPanel");
		buttonContainer.add(this.recoverButton);
		buttonContainer.add(this.deleteButton);

		this.dialogPanel.add(buttonContainer);
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
		appw.getFileManager().restoreAutoSavedFile(materialJSON);
		appw.getFileManager().deleteAutoSavedFile();
		appw.startAutoSave();
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
		appw.getFileManager().deleteAutoSavedFile();
		appw.startAutoSave();
		this.hide();
	}
	
	/**
	 * set labels
	 */
	public void setLabels() {
		Localization loc = app.getLocalization();
		this.getCaption().setText(loc.getMenu("RecoverUnsaved"));
		this.infoText.setText(loc.getMenu("UnsavedChangesFound"));
		this.deleteButton.setText(loc.getMenu("Delete"));
		this.recoverButton.setText(loc.getMenu("Recover"));
	}
	
	@Override
	public void show() {
		super.show();
		super.center();
	}

	/**
	 * @param json
	 *            saved file json
	 */
	public void setJSON(String json) {
		this.materialJSON = json;
	}
}
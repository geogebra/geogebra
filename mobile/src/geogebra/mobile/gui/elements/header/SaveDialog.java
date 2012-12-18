package geogebra.mobile.gui.elements.header;

import geogebra.common.main.GWTKeycodes;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextBox;
import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.dialog.Dialog;
import com.googlecode.mgwt.ui.client.dialog.DialogPanel;
import com.googlecode.mgwt.ui.client.dialog.HasTitleText;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.theme.base.DialogCss;
import com.googlecode.mgwt.ui.client.widget.base.ButtonBase;

/**
 * A dialog with an InputBar, Save-Button and CANCLE-Button.
 * 
 * @see com.googlecode.mgwt.ui.client.dialog.ConfirmDialog ConfirmDialog
 * 
 */
public class SaveDialog implements HasText, HasTitleText, Dialog
{
	/**
	 * The callback used when buttons are taped.
	 */
	public interface SaveCallback
	{
		/**
		 * Called if save button is clicked.
		 */
		public void onSave();

		/**
		 * Called if cancel button is clicked.
		 */
		public void onCancel();
	}

	/**
	 * The Save-Button of the InputDialog.
	 * 
	 * @see com.googlecode.mgwt.ui.client.widget.base.ButtonBase ButtonBase
	 */
	private static class SaveButton extends ButtonBase
	{
		public SaveButton(DialogCss css, String text)
		{

			super(css);
			setText(text);
			addStyleName(css.okbutton());
		}
	}

	/**
	 * The Cancel-Button of the InputDialog.
	 * 
	 * @see com.googlecode.mgwt.ui.client.widget.base.ButtonBase ButtonBase
	 */
	private static class CancelButton extends ButtonBase
	{
		public CancelButton(DialogCss css, String text)
		{
			super(css);
			setText(text);
			addStyleName(css.cancelbutton());
		}
	}

	PopinDialog popinDialog;
	private DialogPanel dialogPanel;
	private TextBox textInput;
	private DialogCss css;
	private FlowPanel buttonContainer;
	SaveCallback callback;

	/**
	 * Construct a save-dialog.
	 * 
	 * @param title
	 *          - the title of the dialog
	 * @param saveCallback
	 *          - the callback used when a button of the dialog is taped
	 */

	public SaveDialog(String fileName, SaveCallback saveCallback)
	{
		this.callback = saveCallback;
		this.css = MGWTStyle.getTheme().getMGWTClientBundle().getDialogCss();
		this.popinDialog = new PopinDialog(this.css);
		setCloseOnBackgroundClick();

		initDialogPanel();
		addTextBox(fileName);
		setTitleText("Save");

		this.textInput.addKeyUpHandler(new KeyUpHandler()
		{			
			@Override
			public void onKeyUp(KeyUpEvent event)
			{
				if(event.getNativeKeyCode() == GWTKeycodes.KEY_ENTER){
					SaveDialog.this.callback.onSave();
					SaveDialog.this.close(); 
				}
			}
		});
		
		addButtonContainer();
	}
	
	/**
	 * Adds a textbox with the given text to the dialogPanel.
	 * 
	 * @param text
	 */
	private void addTextBox(String text)
	{
		this.textInput = new TextBox();
		this.textInput.addStyleName("savetextinput");
		setText(text);
		this.dialogPanel.getContent().add(this.textInput);
	}

	/**
	 * Initializes the dialogPanel and adds it to the popinDialog.
	 */
	private void initDialogPanel()
	{
		this.dialogPanel = new DialogPanel();
		this.dialogPanel.addStyleName("savedialog");
		this.popinDialog.add(this.dialogPanel);
	}

	/**
	 * Adds all the buttons to the dialog. Horizontal alignment.
	 */
	private void addButtonContainer()
	{
		this.buttonContainer = new FlowPanel();
		this.buttonContainer.addStyleName(this.css.footer());

		addSaveButton();
		addCancelButton();

		this.dialogPanel.getContent().add(this.buttonContainer);
	}

	/**
	 * Adds the OK-button to the dialog, adds a TapHandler.
	 */
	private void addSaveButton()
	{
		SaveButton saveButton = new SaveButton(this.css, "Save");
		saveButton.addDomHandler(new ClickHandler()
		{

			@Override
			public void onClick(ClickEvent event)
			{
				SaveDialog.this.popinDialog.hide();
				if (SaveDialog.this.callback != null)
				{
					SaveDialog.this.callback.onSave();
				}
			}
		}, ClickEvent.getType());

		this.dialogPanel.showOkButton(false); // don't show the default buttons from
																					// Daniel Kurka
		this.buttonContainer.add(saveButton);
	}

	/**
	 * Adds the CANCEL-button to the dialog, adds a TapHandler.
	 */
	private void addCancelButton()
	{
		CancelButton cancelButton = new CancelButton(this.css, "Cancel");
		cancelButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				SaveDialog.this.popinDialog.hide();
				if (SaveDialog.this.callback != null)
					SaveDialog.this.callback.onCancel();
			}

		}, ClickEvent.getType());
		this.dialogPanel.showCancelButton(false);
		this.buttonContainer.add(cancelButton);
	}

	/**
	 * @see com.googlecode.mgwt.ui.client.dialog.HasTitleText#setTitleText(java.lang.String)
	 */
	@Override
	public void setTitleText(String title)
	{
		this.dialogPanel.getDialogTitle().setHTML(title);
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasText#setText(java.lang.String)
	 */
	@Override
	public void setText(String text)
	{
		this.textInput.setText(text);
	}

	/**
	 * @see com.googlecode.mgwt.ui.client.dialog.HasTitleText#getTitleText()
	 */
	@Override
	public String getTitleText()
	{
		return this.dialogPanel.getDialogTitle().getHTML();
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasText#getText()
	 */
	@Override
	public String getText()
	{
		return this.textInput.getText();
	}

	/**
	 * Shows the dialog.
	 * 
	 * @see com.googlecode.mgwt.ui.client.dialog.Dialog#show()
	 */
	@Override
	public void show()
	{
		this.popinDialog.center();
	}

	/**
	 * Closes the dialog
	 * 
	 * @see com.googlecode.mgwt.ui.client.dialog.AnimatableDialogBase#hide()
	 */
	public void close()
	{
		this.popinDialog.hide();
	}

	/**
	 * @see com.googlecode.mgwt.ui.client.dialog.AnimatableDialogBase#setHideOnBackgroundClick(boolean)
	 */
	private void setCloseOnBackgroundClick()
	{
		this.popinDialog.setHideOnBackgroundClick(true);
	}
}
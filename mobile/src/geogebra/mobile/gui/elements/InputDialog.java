package geogebra.mobile.gui.elements;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextBox;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.dialog.Dialog;
import com.googlecode.mgwt.ui.client.dialog.DialogPanel;
import com.googlecode.mgwt.ui.client.dialog.HasTitleText;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.theme.base.DialogCss;
import com.googlecode.mgwt.ui.client.widget.Button;

/**
 * A dialog with an InputBar, OK-Button and CANCLE-Button.
 * 
 * @see com.googlecode.mgwt.ui.client.dialog.ConfirmDialog ConfirmDialog
 * 
 */
public class InputDialog implements HasText, HasTitleText, Dialog
{
	/**
	 * The callback used when buttons are taped.
	 */
	public interface InputCallback
	{
		/**
		 * Called if ok button is taped.
		 */
		public void onOk();

		/**
		 * Called if cancel button is taped.
		 */
		public void onCancel();
	}

	PopinDialog popinDialog;
	private DialogPanel dialogPanel;
	private TextBox textInput;
	InputCallback callback;

	/**
	 * Construct a Inputdialog.
	 * 
	 * @param title
	 *          - the title of the dialog
	 * @param text
	 *          - the text to write into the inputbar
	 * @param callback
	 *          - the callback used when a button of the dialog is taped
	 */
	public InputDialog(String title, InputCallback callback)
	{
		this(title, "", callback);
	}
	
	public InputDialog(String title, String text, InputCallback callback)
	{
		this.callback = callback;
		this.popinDialog = new PopinDialog(MGWTStyle.getTheme().getMGWTClientBundle().getDialogCss());
		setCloseOnBackgroundClick();
		
		initDialogPanel();
		addTextBox(text);
		setTitleText(title);
		
		addOkButton();
		addCancelButton();
	}
	
	/**
	 * Adds a textbox with the given text to the dialogPanel.
	 * 
	 * @param text
	 */
	private void addTextBox(String text)
	{
		this.textInput = new TextBox();
		this.textInput.addStyleName("inputtext");
		setText(text);
		this.dialogPanel.getContent().add(this.textInput);
	}
	
	/**
	 * Initializes the dialogPanel and adds it to the popinDialog.
	 */
	private void initDialogPanel()
	{
		this.dialogPanel = new DialogPanel();
		this.dialogPanel.addStyleName("inputdialog");
		this.dialogPanel.showCancelButton(true);
		this.dialogPanel.showOkButton(true);
		this.popinDialog.add(this.dialogPanel);
	}
	
	/**
	 * Adds the OK-button to the dialog, adds a TapHandler.
	 */
	private void addOkButton()
	{
		this.dialogPanel.getOkButton().addTapHandler(new TapHandler()
		{
			@Override
			public void onTap(TapEvent event)
			{
				InputDialog.this.popinDialog.hide();
				if (InputDialog.this.callback != null)
					InputDialog.this.callback.onOk();
			}
		});
		this.dialogPanel.setOkButtonText("Ok");
	}
	
	/**
	 * Adds the CANCEL-button to the dialog, adds a TapHandler.
	 */
	private void addCancelButton()
	{
		this.dialogPanel.getCancelButton().addTapHandler(new TapHandler()
		{
			@Override
			public void onTap(TapEvent event)
			{
				InputDialog.this.popinDialog.hide();
				if (InputDialog.this.callback != null)
					InputDialog.this.callback.onCancel();
			}
		});
		this.dialogPanel.setCancelButtonText("Cancel");
	}
	

	/* 
	 * @see
	 * com.googlecode.mgwt.ui.client.dialog.HasTitleText#setTitleText(java.lang.String)
	 */
	@Override
	public void setTitleText(String title)
	{
		this.dialogPanel.getDialogTitle().setHTML(title);
	}

	/*
	 * 
	 * @see com.google.gwt.user.client.ui.HasText#setText(java.lang.String)
	 */
	@Override
	public void setText(String text)
	{
		this.textInput.setText(text);
	}

	/*
	 * 
	 * @see com.googlecode.mgwt.ui.client.dialog.HasTitleText#getTitleText()
	 */
	@Override
	public String getTitleText()
	{
		return this.dialogPanel.getDialogTitle().getHTML();
	}

	/**
	 * 
	 * @see com.google.gwt.user.client.ui.HasText#getText()
	 */
	@Override
	public String getText()
	{
		return this.textInput.getText();
	}

	/**
	 * Shows the dialog.
	 * @see com.googlecode.mgwt.ui.client.dialog.Dialog#show()
	 */
	@Override
  public void show()
	{
		this.popinDialog.center();
	}

	/**
	 * Closes the dialog
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

	/**
	 * Is called from the {@link geogebra.mobile.gui.elements.toolbar.ToolBar ToolBar}, to handle commands from the input bar.
	 * @param handler the clickhandler
	 */
	
	
//	public InputDialog(ClickHandler handler)
//	{
//		this(handler, ""); 
//	}
//	
//	/**
//	 * Is called from {@link TabletHeaderPanel} to change the title of the app.
//	 * @param handler the clickhandler
//	 * @param titel the title of the app
//	 */
//	public InputDialog(ClickHandler handler, String titel)
//	{	
//		this.inputBar = new TextBox();
//		this.inputBar.setText(titel);
//				
//		setHideOnBackgroundClick(true);
//		//setCenterContent(true);
//
//		RoundPanel roundPanel = new RoundPanel();
//		roundPanel.add(this.inputBar);
//
//		Button button = new Button("ok");
//		button.addDomHandler(handler, ClickEvent.getType());
//		button.addStyleName("popinButton");
//		roundPanel.add(button);
//
//		add(roundPanel);
//		show();
//	}
//	
//	public String getText()
//	{
//		return this.inputBar.getText();
//	}
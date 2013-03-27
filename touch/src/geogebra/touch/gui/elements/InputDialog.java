package geogebra.touch.gui.elements;

import org.vectomatic.dom.svg.ui.SVGResource;

import geogebra.common.main.GWTKeycodes;
import geogebra.touch.gui.CommonResources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;


/**
 * A dialog with an InputBar, OK-Button and CANCLE-Button.
 * 
 * @see com.googlecode.mgwt.ui.client.dialog.ConfirmDialog ConfirmDialog
 * 
 */
public class InputDialog extends DialogBox
{
	/**
	 * The callback used when buttons are taped.
	 */
	public interface InputCallback
	{
		/**
		 * Called if ok button is clicked.
		 */
		public void onOk();

		/**
		 * Called if cancel button is clicked.
		 */
		public void onCancel();
	}

	private TextBox textInput;
	private FlowPanel buttonContainer;
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

		initDialogPanel();
		addTextBox(text);
		this.setTitle(title);

		this.textInput.addKeyUpHandler(new KeyUpHandler()
		{
			@Override
			public void onKeyUp(KeyUpEvent event)
			{
				if (event.getNativeKeyCode() == GWTKeycodes.KEY_ENTER)
				{
					InputDialog.this.callback.onOk();
					InputDialog.this.hide();
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
		this.textInput.addStyleName("inputtext");
		setText(text);
		this.add(this.textInput);
	}

	/**
	 * Initializes the dialogPanel and adds it to the popinDialog.
	 */
	private void initDialogPanel()
	{
		this.addStyleName("inputdialog");
	}

	/**
	 * Adds all the buttons to the dialog. Horizontal alignment.
	 */
	private void addButtonContainer()
	{
		// TODO add the buttons
	}

	/**
	 * Adds the OK-button to the dialog, adds a TapHandler.
	 */
	private void addOkButton()
	{
		SVGResource icon = CommonResources.INSTANCE.dialog_ok();
		Button okButton = new Button();
		String html = "<img src=\"" + icon.getSafeUri().asString() + "\" style=\"height:32px; width:32px; margin:auto;\">";
		okButton.getElement().setInnerHTML(html);
		okButton.addDomHandler(new ClickHandler()
		{

			@Override
			public void onClick(ClickEvent event)
			{
				if (InputDialog.this.callback != null)
				{
					InputDialog.this.callback.onOk();
				}
			}
		}, ClickEvent.getType());

		this.buttonContainer.add(okButton);
	}

	/**
	 * Adds the CANCEL-button to the dialog, adds a TapHandler.
	 */
	private void addCancelButton()
	{
		SVGResource icon = CommonResources.INSTANCE.dialog_cancel();
		Button cancelButton = new Button();
		String html = "<img src=\"" + icon.getSafeUri().asString() + "\" style=\"height:32px; width:32px; margin:auto;\">";
		cancelButton.getElement().setInnerHTML(html);
		cancelButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				if (InputDialog.this.callback != null)
					InputDialog.this.callback.onCancel();
			}

		}, ClickEvent.getType());
		this.buttonContainer.add(cancelButton);
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
	 * @see com.google.gwt.user.client.ui.HasText#getText()
	 */
	@Override
	public String getText()
	{
		return this.textInput.getText();
	}
}
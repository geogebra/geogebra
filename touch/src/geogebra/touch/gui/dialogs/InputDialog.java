package geogebra.touch.gui.dialogs;

import geogebra.touch.gui.CommonResources;
import geogebra.touch.gui.elements.StandardImageButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A dialog with an InputBar, OK-Button and CANCEL-Button.
 * 
 */
public class InputDialog extends PopupPanel
{
	public enum DialogType
	{
		Title, Input;
	}

	private VerticalPanel dialogPanel = new VerticalPanel();
	private Label title;
	private TextBox textBox = new TextBox();
	private HorizontalPanel buttonContainer = new HorizontalPanel();
	private StandardImageButton okButton = new StandardImageButton(CommonResources.INSTANCE.dialog_ok());
	private StandardImageButton cancelButton = new StandardImageButton(CommonResources.INSTANCE.dialog_cancel());

	private String text, input;

	public InputDialog(DialogType type)
	{
		// hide when clicked outside and set modal
		super(true, true);
		this.setGlassEnabled(true);
		this.title = new Label(type.toString());

		init();
	}

	private void init()
	{
		this.dialogPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.dialogPanel.add(this.title);
		addTextBox();
		addButtonContainer();
		this.add(this.dialogPanel);
	}

	private void addTextBox()
	{
		this.textBox.addKeyUpHandler(new KeyUpHandler()
		{

			@Override
			public void onKeyUp(KeyUpEvent event)
			{
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
				{
					InputDialog.this.onOK();
				}
			}
		});

		this.textBox.setVisibleLength(100);
		this.dialogPanel.add(this.textBox);
	}

	private void addButtonContainer()
	{
		addCancelButton();
		addOKButton();

		this.dialogPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		this.dialogPanel.add(this.buttonContainer);
	}

	private void addOKButton()
	{
		this.okButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				InputDialog.this.onOK();
			}
		}, ClickEvent.getType());

		this.buttonContainer.add(this.okButton);
	}

	private void addCancelButton()
	{
		this.cancelButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				InputDialog.this.onCancel();
			}

		}, ClickEvent.getType());

		this.buttonContainer.add(this.cancelButton);
	}

	protected void onOK()
	{
		this.input = this.textBox.getText();
		this.hide();
	}

	protected void onCancel()
	{
		this.input = this.text;
		this.hide();
	}

	@Override
	public void show()
	{
		super.show();
		super.center();
		this.textBox.setText(this.text);
		this.input = this.text;
		this.textBox.setFocus(true);
	}

	@Override
	public void hide()
	{
		super.hide();
		this.text = "";
	}

	/**
	 * Get the users input.
	 * 
	 * @return the new input if the users action was positive - the old input set
	 *         by setText, if the users action was negative
	 */
	public String getInput()
	{
		return this.input;
	}

	public void setText(String text)
	{
		this.text = text;
	}
}
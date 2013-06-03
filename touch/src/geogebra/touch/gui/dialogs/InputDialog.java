package geogebra.touch.gui.dialogs;

import geogebra.touch.TouchApp;
import geogebra.touch.gui.CommonResources;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.elements.customkeys.CustomKeyListener;
import geogebra.touch.gui.elements.customkeys.CustomKeysPanel;
import geogebra.touch.gui.elements.customkeys.CustomKeysPanel.CustomKey;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A dialog with an InputBar, OK-Button and CANCEL-Button.
 * 
 */
public class InputDialog extends PopupPanel implements CustomKeyListener
{

	public enum DialogType
	{
		Title, InputField;
	}

	private VerticalPanel dialogPanel = new VerticalPanel();
	private Label title = new Label();
	TextBox textBox = new TextBox();
	private HorizontalPanel buttonContainer = new HorizontalPanel();
	private StandardImageButton okButton = new StandardImageButton(CommonResources.INSTANCE.dialog_ok());
	private StandardImageButton cancelButton = new StandardImageButton(CommonResources.INSTANCE.dialog_cancel());
	private TouchApp app;
	private DialogType type;

	private String prevText, input;

	private CustomKeysPanel customKeys = new CustomKeysPanel();

	public InputDialog(TouchApp app, DialogType type)
	{
		// hide when clicked outside and don't set modal due to the
		// CustomKeyPanel
		super(true, false);
		this.setGlassEnabled(true);
		this.app = app;
		this.type = type;
		
		this.setPopupPosition(300, 62);
		
		this.setStyleName("inputDialog");

		init();
	}

	private void init()
	{
		setLabels();
		this.customKeys.addCustomKeyListener(this);
		this.dialogPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.dialogPanel.add(this.title);
		addTextBox();
		//addButtonContainer();
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

		this.textBox.addBlurHandler(new BlurHandler()
		{

			@Override
			public void onBlur(BlurEvent event)
			{
				InputDialog.this.textBox.setFocus(true);
			}
		});

		this.textBox.addFocusHandler(new FocusHandler()
		{

			@Override
			public void onFocus(FocusEvent event)
			{
				InputDialog.this.textBox.setFocus(true);
			}
		});

		this.textBox.setVisibleLength(107);
		this.dialogPanel.add(this.textBox);
		this.textBox.setFocus(true);
	}

	/*private void addButtonContainer()
	{
		addCancelButton();
		addOKButton();

		this.dialogPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		this.dialogPanel.add(this.buttonContainer);
	}*/

	/*private void addOKButton()
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
	}*/

	/*private void addCancelButton()
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
	}*/

	protected void onOK()
	{
		this.input = this.textBox.getText();
		this.hide();
	}

	protected void onCancel()
	{
		this.input = this.prevText;
		this.hide();
	}

	@Override
	public void show()
	{
		super.show();
		//super.center();
		this.textBox.setText(this.prevText);
		this.input = this.prevText;

		//this.customKeys.showRelativeTo(this);
		this.dialogPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.dialogPanel.add(this.customKeys);
		this.textBox.setFocus(true);
	}

	@Override
	public void hide()
	{
		super.hide();
		this.prevText = "";
		this.customKeys.hide();
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
		this.prevText = text;
	}

	public void setLabels()
	{
		switch (this.type)
		{
		case Title:
			this.title.setText(this.app.getLocalization().getPlain(this.type.toString()));
			break;
		case InputField:
			this.title.setText(this.app.getLocalization().getMenu(this.type.toString()));
			break;
		default:
			break;
		}
	}


	@Override
	public void onCustomKeyPressed(CustomKey c)
	{
		this.textBox.setText(this.textBox.getText() + c.toString());
	}
}
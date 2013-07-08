package geogebra.touch.gui.dialogs;

import geogebra.touch.TouchApp;
import geogebra.touch.gui.ResizeListener;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.elements.customkeys.CustomKeyListener;
import geogebra.touch.gui.elements.customkeys.CustomKeysPanel;
import geogebra.touch.gui.elements.customkeys.CustomKeysPanel.CustomKey;
import geogebra.touch.gui.laf.LookAndFeel;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A dialog with an InputBar, OK-Button and CANCEL-Button.
 * 
 */
public class InputDialog extends PopupPanel implements CustomKeyListener, ResizeListener
{

	public enum DialogType
	{
		Title, InputField, NumberValue, Angle;
	}

	private VerticalPanel dialogPanel = new VerticalPanel();
	private Label title = new Label();
	private RadioButton[] radioButton = new RadioButton[2];
	TextBox textBox = new TextBox();
	private TouchApp app;
	private DialogType type;
	private String prevText, input, mode;

	private CustomKeysPanel customKeys = new CustomKeysPanel();
	private LookAndFeel laf;

	public InputDialog(TouchApp app, DialogType type, TabletGUI gui)
	{
		// hide when clicked outside and don't set modal due to the
		// CustomKeyPanel
		super(true, false);
		this.setGlassEnabled(true);
		this.app = app;
		this.type = type;

		// this.setPopupPosition(Window.WINDOW_WIDTH/2, 62);
		this.laf = gui.getLAF();
		onResize(null);

		this.setStyleName("inputDialog");

		init();
		gui.addResizeListener(this);

		setAutoHideEnabled(true);
	}

	public void redefine(DialogType dialogType)
	{
		this.clear();
		if (this.dialogPanel != null)
		{
			this.dialogPanel.clear();
		}
		this.type = dialogType;
		init();
	}

	private void init()
	{
		// needs to be reset
		this.mode = "";

		this.customKeys.addCustomKeyListener(this);
		this.dialogPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.dialogPanel.add(this.title);
		addTextBox();

		if (this.type == DialogType.Angle)
		{
			addRadioButton();
		}

		// addButtonContainer();
		this.add(this.dialogPanel);
		setLabels();
	}

	private void addTextBox()
	{
		this.textBox.getElement().setAttribute("autocorrect", "off");
		this.textBox.getElement().setAttribute("autocapitalize", "off");

		this.textBox.addKeyDownHandler(new KeyDownHandler()
		{

			@Override
			public void onKeyDown(KeyDownEvent event)
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

	private void addRadioButton()
	{
		// "A" is just a label to group the two radioButtons (could be any String -
		// as long as the same is used twice)
		this.radioButton[0] = new RadioButton("A", this.app.getLocalization().getPlain("clockwise"), Direction.DEFAULT);
		this.dialogPanel.add(this.radioButton[0]);

		this.radioButton[1] = new RadioButton("A", this.app.getLocalization().getPlain("counterClockwise"), Direction.DEFAULT);
		this.dialogPanel.add(this.radioButton[1]);

		this.radioButton[0].setValue(new Boolean(true));
	}

	/*
	 * private void addButtonContainer() { addCancelButton(); addOKButton();
	 * 
	 * this.dialogPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	 * this.dialogPanel.add(this.buttonContainer); }
	 */

	/*
	 * private void addOKButton() { this.okButton.addDomHandler(new ClickHandler()
	 * {
	 * 
	 * @Override public void onClick(ClickEvent event) { InputDialog.this.onOK();
	 * } }, ClickEvent.getType());
	 * 
	 * this.buttonContainer.add(this.okButton); }
	 */

	/*
	 * private void addCancelButton() { this.cancelButton.addDomHandler(new
	 * ClickHandler() {
	 * 
	 * @Override public void onClick(ClickEvent event) {
	 * InputDialog.this.onCancel(); }
	 * 
	 * }, ClickEvent.getType());
	 * 
	 * this.buttonContainer.add(this.cancelButton); }
	 */

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
		// super.center();
		this.textBox.setText(this.prevText);
		this.input = this.prevText;

		if (this.radioButton[0] != null)
		{
			this.radioButton[0].setValue(new Boolean(true));
		}

		this.textBox.setFocus(true);

		// this.customKeys.showRelativeTo(this);
		this.dialogPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		if (this.type != DialogType.Title)
		{
			this.dialogPanel.add(this.customKeys);
		}

		this.textBox.setFocus(true);
		setLabels();
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
		if (this.input == null)
		{
			return "";
		}

		for (CustomKey c : CustomKey.values())
		{
			if (!c.getReplace().equals(""))
			{
				this.input = this.input.replace(c.toString(), c.getReplace());
			}
		}
		return this.input;
	}

	public boolean clockwise()
	{
		return this.type == DialogType.Angle && this.radioButton[1].getValue().booleanValue();
	}

	public void setText(String text)
	{
		this.prevText = text;
	}

	public void setMode(String mode)
	{
		this.mode = mode;
		setLabels();
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
		case NumberValue:
		case Angle:
			if (this.mode != null && this.mode.length() > 0)
			{
				this.title.setText(this.app.getLocalization().getMenu(this.mode));
			}
			break;
		default:
			break;
		}
	}

	public DialogType getType()
	{
		return this.type;
	}

	@Override
	public void onCustomKeyPressed(CustomKey c)
	{
		int pos = this.textBox.getCursorPos();
		this.textBox.setText(this.textBox.getText().substring(0, pos) + c.toString() + this.textBox.getText().substring(pos));
		this.textBox.setCursorPos(pos + 1);
	}

	@Override
	public void onResize(ResizeEvent e)
	{
		this.setPopupPosition((Window.getClientWidth() / 2 - 353), this.laf.getAppBarHeight());

	}
}
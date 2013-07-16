package geogebra.touch.gui.dialogs;

import geogebra.common.gui.InputHandler;
import geogebra.touch.ErrorHandler;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.ResizeListener;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.elements.customkeys.CustomKeyListener;
import geogebra.touch.gui.elements.customkeys.CustomKeysPanel;
import geogebra.touch.gui.elements.customkeys.CustomKeysPanel.CustomKey;
import geogebra.touch.gui.laf.LookAndFeel;
import geogebra.touch.model.GuiModel;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A dialog with an InputBar, OK-Button and CANCEL-Button.
 * 
 */
public class InputDialog extends PopupPanel implements CustomKeyListener, ResizeListener, ErrorHandler
{

	public enum DialogType
	{
		InputField, Redefine, NumberValue, Angle;
	}

	private VerticalPanel dialogPanel = new VerticalPanel();
	private Label title = new Label();
	private Label errorBox = new Label();
	private RadioButton[] radioButton = new RadioButton[2];
	VerticalPanel textPanel;
	TextBox textBox = new TextBox();
	Panel underline;
	private TouchApp app;
	private DialogType type;
	private String prevText, mode;

	private CustomKeysPanel customKeys = new CustomKeysPanel();
	private LookAndFeel laf;
	private GuiModel guiModel;
	boolean handlingExpected = false;
	private InputHandler inputHandler;

	public InputDialog(TouchApp app, DialogType type, TabletGUI gui, GuiModel guiModel)
	{
		// hide when clicked outside and don't set modal due to the
		// CustomKeyPanel
		super(true, false);
		this.setGlassEnabled(true);
		this.app = app;
		this.type = type;

		// this.setPopupPosition(Window.WINDOW_WIDTH/2, 62);
		this.laf = TouchEntryPoint.getLookAndFeel();
		onResize(null);

		this.setStyleName("inputDialog");

		init();
		gui.addResizeListener(this);
		this.guiModel = guiModel;

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
		this.title.setStyleName("title");
		addTextBox();
		this.errorBox.setVisible(false);
		this.textPanel.add(this.errorBox);
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
				if (!InputDialog.this.textBox.isVisible())
				{
					return;
				}

				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
				{
					InputDialog.this.handlingExpected = true;
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
				InputDialog.this.underline.removeStyleName("inactive");
				InputDialog.this.underline.addStyleName("active");
			}
		});

		this.textBox.addFocusHandler(new FocusHandler()
		{

			@Override
			public void onFocus(FocusEvent event)
			{
				InputDialog.this.textBox.setFocus(true);
				InputDialog.this.underline.removeStyleName("inactive");
				InputDialog.this.underline.addStyleName("active");
			}
		});

		this.textPanel = new VerticalPanel();
		this.textPanel.add(this.textBox);

		// Input Underline for Android
		this.underline = new LayoutPanel();
		this.underline.setStyleName("inputUnderline");
		this.underline.addStyleName("inactive");
		this.textPanel.add(this.underline);

		this.dialogPanel.add(this.textPanel);
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

	protected void onOK()
	{
		String input = this.textBox.getText();
		for (CustomKey c : CustomKey.values())
		{
			if (!c.getReplace().equals(""))
			{
				input = input.replace(c.toString(), c.getReplace());
			}
		}
		if (this.inputHandler.processInput(input))
		{
			this.hide();
		}
	}

	protected void onCancel()
	{
		this.hide();
	}

	@Override
	public void show()
	{
		setVisible(true);
		this.textBox.setVisible(true);

		super.show();
		this.guiModel.setActiveDialog(this);

		// super.center();
		this.textBox.setText(this.prevText);
		this.handlingExpected = false;

		if (this.radioButton[0] != null)
		{
			this.radioButton[0].setValue(new Boolean(true));
		}

		// this.customKeys.showRelativeTo(this);
		this.dialogPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.errorBox.setVisible(false);
		this.dialogPanel.add(this.customKeys);

		this.textBox.setFocus(true);
		setLabels();
		this.app.registerErrorHandler(this);
	}

	@Override
	public void hide()
	{
		this.app.unregisterErrorHandler(this);
		// super.hide(); -> leads to crash in some Android versions!
		this.prevText = "";
		setVisible(false);
		this.textBox.setVisible(false);
		this.customKeys.hide();
		CloseEvent.fire(this, this, false);
		this.guiModel.setActiveDialog(null);
		// prevent that the function is drawn twice
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

	/**
	 * 
	 * @param reset
	 *          if true handlingExpected will be set to false
	 * @return true if the input should be handled
	 */
	public boolean isHandlingExpected(boolean reset)
	{
		boolean ret = this.handlingExpected;
		if (reset)
		{
			this.handlingExpected = false;
		}
		return ret;
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

	@Override
	public void showError(String error)
	{
		this.errorBox.setText(error);
		this.errorBox.setVisible(true);
	}

	public void setInputHandler(InputHandler inputHandler)
	{
		this.inputHandler = inputHandler;
	}

	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event)
	{
		super.onPreviewNativeEvent(event);

		Event nativeEvent = Event.as(event.getNativeEvent());
		if (nativeEvent.getTypeInt() == Event.ONMOUSEDOWN && TouchEntryPoint.getLookAndFeel().isMouseDownIgnored())
		{
			event.cancel();
			nativeEvent.preventDefault();
			nativeEvent.stopPropagation();
		}
	}
}
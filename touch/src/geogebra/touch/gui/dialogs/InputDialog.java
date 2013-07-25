package geogebra.touch.gui.dialogs;

import geogebra.common.gui.InputHandler;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoNumeric;
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

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
		InputField, Redefine, NumberValue, Angle, Slider;
	}

	private VerticalPanel dialogPanel = new VerticalPanel();
	private FlexTable contentPanel = new FlexTable();
	private HorizontalPanel titlePanel = new HorizontalPanel();
	private Label title = new Label();
	private HorizontalPanel errorBox = new HorizontalPanel();
	private SVGResource iconWarning;
	private Label errorText = new Label();
	private FlowPanel radioButtonPanel = new FlowPanel();
	private RadioButton[] radioButton = new RadioButton[2];
	VerticalPanel textPanel;
	HorizontalPanel sliderPanel;
	TextBox textBox = new TextBox(), min = new TextBox(), max = new TextBox(), increment = new TextBox();
	VerticalPanel minPanel, maxPanel, incrementPanel;
	Panel underline;
	Panel minUnderline;
	Panel maxUnderline;
	Panel incrementUnderline;
	private TouchApp app;
	DialogType type;
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
		this.guiModel = guiModel;

		this.laf = TouchEntryPoint.getLookAndFeel();

		this.buildErrorBox();

		this.setStyleName("inputDialog");
		
		// mark it as not visible to prevent problems with onResize()
		this.setVisible(false);

		init();
		
		gui.addResizeListener(this);

		setAutoHideEnabled(true);
	}
	
	private void setAdditionalStyleName() {
		switch (this.getType()) {
			case InputField:
				break;
			case Redefine:
				break;
			case NumberValue:
			case Angle:
				break;
			case Slider:
				this.addStyleName("sliderDialog");
				break;
			default:
				break;
		}
	}

	private void buildErrorBox()
	{
		this.iconWarning = this.laf.getIcons().icon_warning();
		Panel iconPanel = new LayoutPanel();
		String html = "<img src=\"" + this.iconWarning.getSafeUri().asString() + "\" />";
		iconPanel.getElement().setInnerHTML(html);
		iconPanel.setStyleName("iconPanel");
		this.errorBox.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.errorBox.add(iconPanel);
		this.errorBox.add(this.errorText);
	}

	public void redefine(DialogType dialogType)
	{
		if(this.getType() == dialogType){
			return;
		}		
		this.clear();
		if (this.contentPanel != null && this.dialogPanel != null)
		{
			this.dialogPanel.clear();
			this.contentPanel.clear();
		}
		this.type = dialogType;
		init();
	}

	private void init()
	{
		// needs to be reset
		this.mode = "";
		
		setAdditionalStyleName();

		this.customKeys.addCustomKeyListener(this);
		this.dialogPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.dialogPanel.setStyleName("panelContainer");
		
		this.dialogPanel.add(this.titlePanel);

		this.titlePanel.add(this.title);
		this.titlePanel.setStyleName("titlePanel");
		this.title.setStyleName("title");

		// Padding-left needed for Win8 Dialog
		this.title.getElement().setAttribute("style", "padding-left: " + this.laf.getPaddingLeftOfDialog() + "px;");

		this.contentPanel.setStyleName("contentPanel");
		this.contentPanel.getElement().setAttribute("style", "margin-left: " + this.laf.getPaddingLeftOfDialog() + "px;");
		this.contentPanel.getCellFormatter().setStyleName(0, 0, "left");
		this.contentPanel.getCellFormatter().setStyleName(0, 1, "right");
		
		this.dialogPanel.add(this.contentPanel);
		
		addTextBox();

		if (this.type == DialogType.Slider)
		{
			createSliderDesign();
		}

		if (this.type == DialogType.Angle || this.type == DialogType.Slider)
		{
			addRadioButton();
		}

		// addButtonContainer();
		
		this.add(this.dialogPanel);
		
		setLabels();
	}

	private void createSliderDesign()
	{
		this.sliderPanel = new HorizontalPanel();
		this.sliderPanel.setStyleName("sliderPanel");
		
		this.minPanel = new VerticalPanel();
		this.maxPanel = new VerticalPanel();
		this.incrementPanel = new VerticalPanel();
		
		this.minUnderline = new LayoutPanel();
		this.minUnderline.setStyleName("inputUnderline");
		this.minUnderline.addStyleName("inactive");
		
		this.maxUnderline = new LayoutPanel();
		this.maxUnderline.setStyleName("inputUnderline");
		this.maxUnderline.addStyleName("inactive");
		
		this.incrementUnderline = new LayoutPanel();
		this.incrementUnderline.setStyleName("inputUnderline");
		this.incrementUnderline.addStyleName("inactive");
		
		Label minLabel = new Label(this.app.getLocalization().getPlain("min"));
		this.minPanel.add(minLabel);
		this.minPanel.add(this.min);
		this.minPanel.add(this.minUnderline);
		
		Label maxLabel = new Label(this.app.getLocalization().getPlain("max"));
		this.maxPanel.add(maxLabel);
		this.maxPanel.add(this.max);
		this.maxPanel.add(this.maxUnderline);
		
		Label incrementLabel = new Label(this.app.getLocalization().getMenu("Step"));
		this.incrementPanel.add(incrementLabel);
		this.incrementPanel.add(this.increment);
		this.incrementPanel.add(this.incrementUnderline);
		
		this.sliderPanel.add(this.minPanel);
		this.sliderPanel.add(this.maxPanel);
		this.sliderPanel.add(this.incrementPanel);

		//this.contentPanel.add(this.sliderPanel);
		this.contentPanel.setWidget(1, 0, this.sliderPanel);
	}
	
	void setSliderPreview() {
		if (this.type != DialogType.Slider)
		{
			return;
		}

		if (this.isNumber())
		{
			this.min.setText("-5");
			this.max.setText("5");
			this.increment.setText("0.1");
		}
		else
		{
			this.min.setText("0\u00B0"); // 0°
			this.max.setText("360\u00B0");
			this.increment.setText("1\u00B0");
		}
	}

	private void addTextBox()
	{
		this.textBox = new TextBox();
		this.textBox.addStyleName("textBox");

		this.textBox.getElement().setAttribute("autocorrect", "off");
		this.textBox.getElement().setAttribute("autocapitalize", "off");
		this.textBox.addStyleName("inactive");

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
				if (InputDialog.this.type != DialogType.Slider)
				{
					InputDialog.this.textBox.setFocus(true);
					InputDialog.this.underline.removeStyleName("active");
					InputDialog.this.underline.addStyleName("inactive");
					InputDialog.this.textBox.removeStyleName("active");
					InputDialog.this.textBox.addStyleName("inactive");
				} 
				else 
				{
					InputDialog.this.textBox.setFocus(false);
				}
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
				InputDialog.this.textBox.removeStyleName("inactive");
				InputDialog.this.textBox.addStyleName("active");
			}
		});

		this.textPanel = new VerticalPanel();
		
		Label textBoxLabel = new Label(this.app.getLocalization().getCommand("Name"));

		this.errorBox.setVisible(false);
		this.errorBox.setStyleName("errorBox");
		this.errorBox.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		this.textPanel.add(this.errorBox);
		
		if (this.getType() == DialogType.Slider) {
			this.textPanel.add(textBoxLabel);
		}
		
		
		this.textPanel.add(this.textBox);

		// Input Underline for Android
		this.underline = new LayoutPanel();
		this.underline.setStyleName("inputUnderline");
		this.underline.addStyleName("inactive");
		this.textPanel.add(this.underline);
		this.textPanel.setStyleName("textPanel");

		//this.contentPanel.add(this.textPanel);
		this.contentPanel.setWidget(0, 0, this.textPanel);
		this.textBox.setFocus(true);
	}

	private void addRadioButton()
	{
		String[] s = { "", "" };

		if (this.type == DialogType.Angle)
		{

			s[0] = this.app.getLocalization().getPlain("counterClockwise");
			s[1] = this.app.getLocalization().getPlain("clockwise");
		}
		else
		{
			s[0] = this.app.getLocalization().getMenu("Number");
			s[1] = this.app.getLocalization().getMenu("Angle");
		}

		// "A" is just a label to group the two radioButtons (could be any String -
		// as long as the same is used twice)
		this.radioButton[0] = new RadioButton("A", s[0], Direction.DEFAULT);
		this.radioButton[1] = new RadioButton("A", s[1], Direction.DEFAULT);

		if (this.type == DialogType.Slider)
		{
			ValueChangeHandler<Boolean> handler = new ValueChangeHandler<Boolean>()
			{
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event)
				{
					setSliderPreview();
				}
			};

			this.radioButton[0].addValueChangeHandler(handler);
			this.radioButton[1].addValueChangeHandler(handler);
		}
		
		this.radioButtonPanel.setStyleName("radioButtonPanel");
		
		this.radioButtonPanel.add(this.radioButton[0]);
		this.radioButtonPanel.add(this.radioButton[1]);

		//this.contentPanel.add(this.radioButton[0]);
		//this.contentPanel.add(this.radioButton[1]);
		this.contentPanel.setWidget(0, 1, this.radioButtonPanel);

		this.radioButton[0].setValue(new Boolean(true));
		this.setSliderPreview();
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
		if (this.inputHandler == null || this.inputHandler.processInput(input))
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

		super.center();
		this.textBox.setText(this.prevText);
		this.handlingExpected = false;

		if (this.radioButton[0] != null)
		{
			this.radioButton[0].setValue(new Boolean(true));
		}

		// this.customKeys.showRelativeTo(this);
		this.dialogPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.errorBox.setVisible(false);
		
		//this.contentPanel.add(this.customKeys);
		this.dialogPanel.add(this.customKeys);
		
		setLabels();

		Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				InputDialog.this.textBox.setFocus(true);
			}
		});

		this.app.registerErrorHandler(this);
	}

	@Override
	public void hide()
	{
		this.app.unregisterErrorHandler(this);
		super.hide(); 
		this.prevText = "";

		// prevent that the function is drawn twice
		this.guiModel.setActiveDialog(null);
	}

	public boolean isClockwise()
	{
		return this.type == DialogType.Angle && this.radioButton[1].getValue().booleanValue();
	}

	public boolean isNumber()
	{
		return this.type == DialogType.Slider && this.radioButton[0].getValue().booleanValue();
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
			this.title.setText(this.app.getLocalization().getMenu("InputField"));
			break;
		case Redefine:
			this.title.setText(this.app.getLocalization().getPlain("Redefine"));
			break;
		case NumberValue:
		case Angle:
		case Slider:
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
		if (this.isVisible() && this.isShowing())
		{
			super.center();
		}
	}

	@Override
	public void showError(String error)
	{
		this.errorText.setText(error);
		this.errorBox.setVisible(true);
	}

	public void setInputHandler(InputHandler inputHandler)
	{
		this.inputHandler = inputHandler;
	}

	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event)
	{
		if (!this.isVisible())
		{
			return;
		}

		super.onPreviewNativeEvent(event);

		Event nativeEvent = Event.as(event.getNativeEvent());
		if (nativeEvent.getTypeInt() == Event.ONMOUSEDOWN && TouchEntryPoint.getLookAndFeel().isMouseDownIgnored())
		{
			event.cancel();
			nativeEvent.preventDefault();
			nativeEvent.stopPropagation();
		}
	}

	public String getMin()
	{
		return this.min.getText();
	}

	public String getMax()
	{
		return this.max.getText();
	}

	public String getIncrement()
	{
		return this.increment.getText();
	}

	public void setFromSlider(GeoNumeric geo) {
		redefine(DialogType.Slider);
		this.radioButton[0].setValue(Boolean.valueOf(!geo.isAngle()));
		this.radioButton[1].setValue(Boolean.valueOf(geo.isAngle()));
		this.increment.setText(geo.getAnimationStepObject().getLabel(StringTemplate.editTemplate));
		this.max.setText(geo.getIntervalMaxObject().getLabel(StringTemplate.editTemplate));
		this.min.setText(geo.getIntervalMaxObject().getLabel(StringTemplate.editTemplate));
	}
}
package geogebra.touch.gui.dialogs;

import geogebra.common.gui.InputHandler;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.touch.ErrorHandler;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.ResizeListener;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.elements.InputField;
import geogebra.touch.gui.elements.StandardRadioButton;
import geogebra.touch.gui.elements.customkeys.CustomKeyListener;
import geogebra.touch.gui.elements.customkeys.CustomKeysPanel;
import geogebra.touch.gui.elements.customkeys.CustomKeysPanel.CustomKey;
import geogebra.touch.gui.laf.LookAndFeel;
import geogebra.touch.model.TouchModel;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * A dialog with an InputBar, OK-Button and CANCEL-Button.
 * 
 */
public class InputDialog extends PopupPanel implements CustomKeyListener,
		ResizeListener, ErrorHandler {

	public enum DialogType {
		InputField, Redefine, NumberValue, Angle, Slider, RedefineSlider;
	}

	// panelContainer contains all elements
	private final FlowPanel dialogPanel = new FlowPanel();
	private final FlowPanel titlePanel = new FlowPanel();
	private final FlowPanel contentPanel = new FlowPanel();
	private HorizontalPanel buttonPanel;

	private final Label title = new Label();

	private final HorizontalPanel errorBox = new HorizontalPanel();
	private SVGResource iconWarning;
	private final Label errorText = new Label();

	private final FlowPanel radioButtonPanel = new FlowPanel();
	private final StandardRadioButton[] radioButton = new StandardRadioButton[2];
	private final FlowPanel inputFieldPanel = new FlowPanel();
	private HorizontalPanel sliderPanel;
	private InputField textBox = new InputField();
	private InputField min, max, increment;

	private final TouchApp app;
	private DialogType type;
	private String prevText, mode;

	private final CustomKeysPanel customKeys = new CustomKeysPanel();
	private final LookAndFeel laf;
	private final TouchModel model;
	private boolean handlingExpected = false;
	private InputHandler inputHandler;

	public InputDialog(final TouchApp app, final DialogType type,
			final TouchModel touchModel) {
		// hide when clicked outside and don't set modal due to the
		// CustomKeyPanel
		super(true, false);
		this.setGlassEnabled(true);
		this.app = app;
		this.type = type;
		this.model = touchModel;
		this.laf = TouchEntryPoint.getLookAndFeel();

		this.buildErrorBox();

		this.setStyleName("inputDialog");

		this.init();

		((TabletGUI) app.getTouchGui()).addResizeListener(this);

		this.setInputHandler(new InputHandler() {
			@Override
			public boolean processInput(final String inputString) {
				return handleInput(inputString);
			}
		});

		this.setAutoHideEnabled(true);
	}

	protected boolean handleInput(final String inputString) {
		return this.model.inputPanelClosed(inputString);
	}

	private void addRadioButton() {
		final String[] s = { "", "" };

		if (this.type == DialogType.Angle) {
			s[0] = this.app.getLocalization().getPlain("counterClockwise");
			s[1] = this.app.getLocalization().getPlain("clockwise");
		} else {
			s[0] = this.app.getLocalization().getMenu("Number");
			s[1] = this.app.getLocalization().getMenu("Angle");
		}

		// "A" is just a label to group the two radioButtons (could be any
		// String -
		// as long as the same is used twice)
		this.radioButton[0] = new StandardRadioButton("A", s[0]);
		this.radioButton[1] = new StandardRadioButton("A", s[1]);

		if (this.type == DialogType.Slider) {
			final ValueChangeHandler<Boolean> handler = new ValueChangeHandler<Boolean>() {
				@Override
				public void onValueChange(final ValueChangeEvent<Boolean> event) {
					InputDialog.this.setSliderPreview();
				}
			};

			this.radioButton[0].addValueChangeHandler(handler);
			this.radioButton[1].addValueChangeHandler(handler);
		}

		this.radioButtonPanel.setStyleName("radioButtonPanel");
		this.radioButtonPanel.add(this.radioButton[0]);
		this.radioButtonPanel.add(this.radioButton[1]);
		this.contentPanel.add(this.radioButtonPanel);

		this.radioButton[0].setValue(new Boolean(true));
	}

	private void addTextBox() {
		this.textBox = new InputField(this.type == DialogType.Slider ? this.app
				.getLocalization().getCommand("Name") : null, true);

		this.textBox.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(final KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					InputDialog.this.onOK();
				}
			}
		});
		this.textBox.addStyleName("name");

		this.errorBox.setVisible(false);
		this.errorBox.setStyleName("errorBox");
		this.errorBox.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		this.textBox.addErrorBox(this.errorBox);

		this.inputFieldPanel.add(this.textBox);
	}

	private void buildErrorBox() {
		this.iconWarning = this.laf.getIcons().icon_warning();
		final Panel iconPanel = new LayoutPanel();
		final String html = "<img src=\""
				+ this.iconWarning.getSafeUri().asString() + "\" />";
		iconPanel.getElement().setInnerHTML(html);
		iconPanel.setStyleName("iconPanel");
		this.errorBox.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.errorBox.add(iconPanel);
		this.errorBox.add(this.errorText);
	}

	private void createSliderDesign() {
		this.min = new InputField(this.app.getLocalization().getPlain("min"),
				true);
		this.max = new InputField(this.app.getLocalization().getPlain("max"),
				true);
		this.increment = new InputField(this.app.getLocalization().getMenu(
				"Step"), true);
		this.increment.addStyleName("last");

		final InputField[] box = new InputField[] { this.min, this.max,
				this.increment, this.textBox };

		this.min.setTextBoxToLoseFocus(box);
		this.max.setTextBoxToLoseFocus(box);
		this.increment.setTextBoxToLoseFocus(box);

		this.sliderPanel = new HorizontalPanel();

		this.sliderPanel.setStyleName("sliderPanel");
		this.sliderPanel.add(this.min);
		this.sliderPanel.add(this.max);
		this.sliderPanel.add(this.increment);

		this.inputFieldPanel.add(this.sliderPanel);

		this.addRadioButton();

		this.buttonPanel = new HorizontalPanel();
		this.buttonPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.buttonPanel.setStyleName("buttonPanel");
		final Button ok = new Button();
		ok.addStyleName("ok");
		ok.setText(this.app.getLocalization().getPlain("Apply"));
		ok.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				InputDialog.this.onOK();
			}
		});
		final Button cancel = new Button();
		cancel.setStyleName("last");
		cancel.setText(this.app.getLocalization().getPlain("Cancel"));
		cancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				InputDialog.this.onCancel();
			}
		});
		this.buttonPanel.add(ok);
		this.buttonPanel.add(cancel);

		this.contentPanel.add(this.buttonPanel);
	}

	public String getIncrement() {
		return this.increment.getText();
	}

	public String getMax() {
		return this.max.getText();
	}

	public String getMin() {
		return this.min.getText();
	}

	public DialogType getType() {
		return this.type;
	}

	@Override
	public void hide() {
		this.app.unregisterErrorHandler(this);
		super.hide();
		this.prevText = "";

		// prevent that the function is drawn twice
		this.model.getGuiModel().setActiveDialog(null);
	}

	private void init() {
		// needs to be reset
		this.mode = "";

		this.setAdditionalStyleName();

		this.customKeys.addCustomKeyListener(this);
		this.dialogPanel.setStyleName("panelContainer");

		this.dialogPanel.add(this.titlePanel);

		this.titlePanel.add(this.title);
		this.titlePanel.setStyleName("titlePanel");
		this.title.setStyleName("title");

		// Padding-left needed for Win8 Dialog
		this.title.getElement().setAttribute("style",
				"padding-left: " + this.laf.getPaddingLeftOfDialog() + "px;");

		this.contentPanel.setStyleName("contentPanel");
		this.contentPanel.getElement().setAttribute("style",
				"margin-left: " + this.laf.getPaddingLeftOfDialog() + "px;");

		this.dialogPanel.add(this.contentPanel);

		addTextBox();
		this.contentPanel.add(this.inputFieldPanel);
		this.inputFieldPanel.setStyleName("inputFieldPanel");

		if (this.type == DialogType.Slider
				|| this.type == DialogType.RedefineSlider) {
			this.createSliderDesign();
		}

		if (this.type == DialogType.Angle) {
			this.addRadioButton();
		}

		this.add(this.dialogPanel);

		this.setLabels();

		this.addDomHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(final KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_TAB) {
					event.preventDefault();
					return;
				}
			}
		}, KeyDownEvent.getType());
	}

	public boolean isClockwise() {
		return this.type == DialogType.Angle
				&& this.radioButton[1].getValue().booleanValue();
	}

	/**
	 * 
	 * @param reset
	 *            if true handlingExpected will be set to false
	 * @return true if the input should be handled
	 */
	public boolean isHandlingExpected(final boolean reset) {
		final boolean ret = this.handlingExpected;
		if (reset) {
			this.handlingExpected = false;
		}
		return ret;
	}

	public boolean isNumber() {
		return this.type == DialogType.Slider
				&& this.radioButton[0].getValue().booleanValue();
	}

	protected void onCancel() {
		this.hide();
	}

	@Override
	public void onCustomKeyPressed(final CustomKey c) {
		final int pos = this.textBox.getCursorPos();
		this.textBox.setText(this.textBox.getText().substring(0, pos)
				+ c.toString() + this.textBox.getText().substring(pos));
		this.textBox.setCursorPos(pos + 1);
	}

	protected void onOK() {
		InputDialog.this.handlingExpected = true;

		String input = this.textBox.getText();
		for (final CustomKey c : CustomKey.values()) {
			if (!c.getReplace().equals("")) {
				input = input.replace(c.toString(), c.getReplace());
			}
		}
		if (this.inputHandler == null || this.inputHandler.processInput(input)) {
			this.hide();
		}
	}

	@Override
	protected void onPreviewNativeEvent(final NativePreviewEvent event) {
		if (!this.isVisible()) {
			return;
		}

		super.onPreviewNativeEvent(event);

		final Event nativeEvent = Event.as(event.getNativeEvent());
		if (nativeEvent.getTypeInt() == Event.ONMOUSEDOWN
				&& TouchEntryPoint.getLookAndFeel().isMouseDownIgnored()) {
			event.cancel();
			nativeEvent.preventDefault();
			nativeEvent.stopPropagation();
		}
	}

	@Override
	public void onResize() {
		if (this.isVisible() && this.isShowing()) {
			super.center();
		}
	}

	public void redefine(final DialogType dialogType) {
		if (this.getType() == dialogType) {
			return;
		}
		this.clear();
		if (this.contentPanel != null && this.dialogPanel != null) {
			this.dialogPanel.clear();
			this.contentPanel.clear();
			this.inputFieldPanel.clear();
			this.radioButtonPanel.clear();
		}
		this.type = dialogType;
		this.init();
	}

	private void setAdditionalStyleName() {
		this.setStyleName("inputDialog");
		switch (this.getType()) {
		case InputField:
			break;
		case Redefine:
			break;
		case NumberValue:
		case Angle:
			this.addStyleName("angleDialog");
			break;
		case RedefineSlider:
			this.addStyleName("redefine");
			//$FALL-THROUGH$
		case Slider:
			this.addStyleName("sliderDialog");
			break;
		default:
			break;
		}
	}

	public void setFromSlider(final GeoNumeric geo) {
		this.redefine(DialogType.RedefineSlider);
		this.radioButton[0].setValue(Boolean.valueOf(!geo.isAngle()));
		this.radioButton[1].setValue(Boolean.valueOf(geo.isAngle()));
		this.textBox.setText(geo.getLabel(StringTemplate.defaultTemplate));
		this.increment.setText(geo.getAnimationStepObject().getLabel(
				StringTemplate.editTemplate));
		this.max.setText(geo.getIntervalMaxObject().getLabel(
				StringTemplate.editTemplate));
		this.min.setText(geo.getIntervalMinObject().getLabel(
				StringTemplate.editTemplate));
	}

	public void setInputHandler(final InputHandler inputHandler) {
		this.inputHandler = inputHandler;
	}

	public void setLabels() {
		switch (this.type) {
		case InputField:
			this.title
					.setText(this.app.getLocalization().getMenu("InputField"));
			break;
		case Redefine:
			this.title.setText(this.app.getLocalization().getPlain("Redefine"));
			break;
		case NumberValue:
		case Angle:
		case Slider:
			if (this.mode != null && this.mode.length() > 0) {
				this.title.setText(this.app.getLocalization()
						.getMenu(this.mode));
			}
			break;
		default:
			break;
		}
	}

	public void setMode(final String mode) {
		this.mode = mode;
		this.setLabels();
	}

	void setSliderPreview() {
		if (this.type != DialogType.Slider) {
			return;
		}

		if (this.isNumber()) {
			final GeoNumeric num = new GeoNumeric(this.app.getKernel()
					.getConstruction());
			this.textBox.setText(num.getFreeLabel(null));

			this.min.setText("-5");
			this.max.setText("5");
			this.increment.setText("0.1");
		} else {
			final GeoAngle angle = new GeoAngle(this.app.getKernel()
					.getConstruction());
			this.textBox.setText(angle.getFreeLabel(null));

			this.min.setText("0\u00B0"); // 0°
			this.max.setText("360\u00B0");
			this.increment.setText("1\u00B0");
		}
	}

	public void setText(final String text) {
		this.prevText = text;
	}

	@Override
	public void show() {
		super.show();
		this.model.getGuiModel().setActiveDialog(this);

		super.center();

		if (this.type == DialogType.RedefineSlider) {
			// do not overwrite label
		} else if (this.type != DialogType.Slider) {
			this.textBox.setText(this.prevText);
		} else {
			this.setSliderPreview();
		}

		this.handlingExpected = false;

		if (this.radioButton[0] != null) {
			this.radioButton[0].setValue(new Boolean(true));
		}

		this.errorBox.setVisible(false);

		// if (this.type != DialogType.Slider) {
		if (this.type != DialogType.Slider
				&& this.type != DialogType.RedefineSlider
				&& this.type != DialogType.Angle) {
			this.dialogPanel.add(this.customKeys);
		}

		this.setLabels();
		this.textBox.setFocus(true);
		this.textBox.setCursorPos(this.textBox.getText().length());

		this.app.registerErrorHandler(this);
	}

	@Override
	public void showError(final String error) {
		if (this.model.getActualSlider() != null) {
			this.model.getActualSlider().remove();
		}

		this.errorText.setText(error);
		this.errorBox.setVisible(true);
	}
}
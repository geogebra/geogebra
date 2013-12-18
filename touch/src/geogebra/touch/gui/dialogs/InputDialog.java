package geogebra.touch.gui.dialogs;

import geogebra.common.gui.InputHandler;
import geogebra.touch.ErrorHandler;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.ResizeListener;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.elements.InputField;
import geogebra.touch.gui.elements.customkeys.CustomKeyListener;
import geogebra.touch.gui.elements.customkeys.CustomKeysPanel;
import geogebra.touch.gui.elements.customkeys.CustomKeysPanel.CustomKey;
import geogebra.touch.gui.elements.radioButton.StandardRadioButton;
import geogebra.touch.gui.elements.radioButton.StandardRadioGroup;
import geogebra.touch.gui.laf.LookAndFeel;
import geogebra.touch.model.TouchModel;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;

public class InputDialog extends DialogT implements CustomKeyListener,
		ResizeListener, ErrorHandler {

	public enum DialogType {
		InputField, Redefine, NumberValue, Angle, Slider, RedefineSlider, Button;
	}

	// panelContainer contains all elements
	private final FlowPanel dialogPanel = new FlowPanel();
	private final FlowPanel titlePanel = new FlowPanel();
	protected FlowPanel contentPanel = new FlowPanel();
	private final CustomKeysPanel customKeys = new CustomKeysPanel();

	private final Label title = new Label();
	private final HorizontalPanel errorBox = new HorizontalPanel();
	private SVGResource iconWarning;
	private final Label errorText = new Label();

	private final FlowPanel radioButtonPanel = new FlowPanel();
	protected StandardRadioGroup radioGroup = new StandardRadioGroup();
	protected StandardRadioButton[] radioButton = new StandardRadioButton[2];
	protected FlowPanel inputFieldPanel = new FlowPanel();
	private InputField textBox = new InputField();

	protected TouchApp app;
	private DialogType type;
	private String mode;

	private final LookAndFeel laf;
	private final TouchModel model;
	private boolean handlingExpected = false;
	private InputHandler inputHandler;

	public InputDialog(final TouchApp app, final DialogType type,
			final TouchModel touchModel) {
		super(true, false);

		this.app = app;
		this.type = type;
		this.model = touchModel;
		this.laf = TouchEntryPoint.getLookAndFeel();

		init();
	}

	private void init() {
		this.mode = "";
		this.setStyleName("inputDialog");
		this.setGlassEnabled(true);

		this.dialogPanel.setStyleName("panelContainer");
		this.setAutoHideEnabled(true);

		buildErrorBox();
		addTitlePanel();
		addContentPanel();
		addTextBox();
		addRadioButton();
		addCustomKeys();

		this.add(this.dialogPanel);

		((TabletGUI) this.app.getTouchGui()).addResizeListener(this);

		this.addDomHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(final KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_TAB) {
					event.preventDefault();
					return;
				}
			}
		}, KeyDownEvent.getType());

		this.setInputHandler(new InputHandler() {
			@Override
			public boolean processInput(final String inputString) {
				return handleInput(inputString);
			}
		});
	}

	private void addCustomKeys() {
		this.customKeys.addCustomKeyListener(this);
		this.customKeys.setVisible(false);
		this.contentPanel.add(this.customKeys);
	}

	private void addContentPanel() {
		this.contentPanel.setStyleName("contentPanel");
		//this.contentPanel.getElement().getStyle().setMarginLeft(this.laf.getPaddingLeftOfDialog(), Unit.PX);
		//this.contentPanel.getElement().getStyle().setMarginRight(this.laf.getPaddingLeftOfDialog(), Unit.PX);
		
		this.inputFieldPanel.setStyleName("inputFieldPanel");
		this.contentPanel.add(this.inputFieldPanel);
		this.dialogPanel.add(this.contentPanel);
	}

	private void addTitlePanel() {
		this.titlePanel.setStyleName("titlePanel");
		this.title.setStyleName("title");

		// Padding-left needed for Win8 Dialog
		this.laf.center(this.title);
		this.titlePanel.add(this.title);
		this.dialogPanel.add(this.titlePanel);
	}

	protected void addRadioButton() {
		this.radioButton[0] = new StandardRadioButton(this.radioGroup);
		this.radioButton[1] = new StandardRadioButton(this.radioGroup);

		this.radioButtonPanel.setStyleName("radioButtonPanel");
		this.radioButtonPanel.add(this.radioButton[0]);
		this.radioButtonPanel.add(this.radioButton[1]);
		this.contentPanel.add(this.radioButtonPanel);

		this.radioButtonPanel.setVisible(false);
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

	protected boolean handleInput(final String inputString) {
		return this.model.inputPanelClosed(inputString, this.type);
	}

	public DialogType getType() {
		return this.type;
	}

	public boolean isClockwise() {
		return this.type == DialogType.Angle
				&& this.radioButton[1].isActivated();
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

	public void onCancel() {
		this.hide();
	}

	public void onOK() {
		this.handlingExpected = true;
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
	public void onCustomKeyPressed(final CustomKey c) {
		final int pos = this.textBox.getCursorPos();
		setInputText(this.textBox.getText().substring(0, pos) + c.toString()
				+ this.textBox.getText().substring(pos));
		this.textBox.setCursorPos(pos + 1);
	}

	public void redefine(final DialogType dialogType, final String oldValue) {
		this.type = dialogType;
		setInputText(oldValue);
	}

	public void setInputHandler(final InputHandler inputHandler) {
		this.inputHandler = inputHandler;
	}

	public void setLabels() {
		switch (this.type) {
		case Redefine:
			setTitle(this.app.getLocalization().getPlain("Redefine"));
			break;
		case NumberValue:
		case InputField:
		case Angle:
			if (this.mode != null && this.mode.length() > 0) {
				setTitle(this.app.getLocalization().getMenu(this.mode));
			}
			break;
		case Button:
			setTitle(this.app.getLocalization().getCommand("Button"));
			break;
		default:
			break;
		}
	}

	public void setMode(final String mode) {
		this.mode = mode;
		setLabels();
	}

	public void setType(final DialogType type) {
		this.type = type;
		if (this.type == DialogType.Angle) {
			this.addStyleName("angleDialog");
		} else {
			this.removeStyleName("angleDialog");
		}
	}

	public void setInputText(final String text) {
		this.textBox.setText(text);
	}

	protected void showRadioButtons(final String firstVal, final String secVal) {
		this.radioButton[0].setLabel(firstVal);
		this.radioButton[1].setLabel(secVal);
		this.radioButton[0].setActive(true);
		this.radioButtonPanel.setVisible(true);
	}

	@Override
	public void show() {
		super.show();
		this.onResize();

		this.model.getGuiModel().setActiveDialog(this);

		this.handlingExpected = false;
		this.errorBox.setVisible(false);

		this.radioButtonPanel.setVisible(false);
		if (this.type == DialogType.Angle) {
			showRadioButtons(
					this.app.getLocalization().getPlain("counterClockwise"),
					this.app.getLocalization().getPlain("clockwise"));
		}

		
		if (this.type != DialogType.Slider
				&& this.type != DialogType.RedefineSlider) {
			this.customKeys.setVisible(true);
		}else{
			this.customKeys.setVisible(false);
		}

		setLabels();
		this.textBox.setFocus(true);
		this.app.registerErrorHandler(this);
	}

	@Override
	public void hide() {
		super.hide();
		this.app.unregisterErrorHandler(this);

		// prevent that the function is drawn twice
		this.model.getGuiModel().setActiveDialog(null);
	}

	@Override
	public void showError(final String error) {
		if (this.model.getActualSlider() != null) {
			this.model.getActualSlider().remove();
		}

		this.errorText.setText(error);
		this.errorBox.setVisible(true);
	}

	@Override
	public void onResize() {
		if (this.isVisible() && this.isShowing()) {
			this.laf.setPopupCenter(this);
		}
	}

	// only used for win
	@Override
	public void setPopupPosition(final int left, final int top) {
		super.setPopupPosition(left, top);
		centerContent();

	}

	// only used for win
	private void centerContent() {
		if (this.title != null && this.contentPanel != null) {
			this.laf.center(this.title);
			this.laf.center(this.contentPanel);
		}
	}

	@Override
	public void setTitle(final String title) {
		this.title.setText(title);
	}

}
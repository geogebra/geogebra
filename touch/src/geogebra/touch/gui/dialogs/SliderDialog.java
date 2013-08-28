package geogebra.touch.gui.dialogs;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.touch.TouchApp;
import geogebra.touch.gui.elements.InputField;
import geogebra.touch.gui.elements.radioButton.RadioChangeEvent;
import geogebra.touch.gui.elements.radioButton.RadioChangeHandler;
import geogebra.touch.model.TouchModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class SliderDialog extends InputDialog {
	private HorizontalPanel sliderPanel;
	private InputField min, max, increment;
	private HorizontalPanel buttonPanel;
	private final Button ok = new Button();
	private final Button cancel = new Button();

	public SliderDialog(final TouchApp app, final DialogType type,
			final TouchModel touchModel) {
		super(app, type, touchModel);
		createSliderDesign();
	}

	private void createSliderDesign() {
		this.addStyleName("sliderDialog");

		addValuePanel();
		addRadioButtonChangeHandler();
		addButtonPanel();
	}

	private void addValuePanel() {
		this.min = new InputField(this.app.getLocalization().getPlain("min"),
				true);
		this.max = new InputField(this.app.getLocalization().getPlain("max"),
				true);
		this.increment = new InputField(this.app.getLocalization().getMenu(
				"Step"), true);
		this.increment.addStyleName("last");

		this.sliderPanel = new HorizontalPanel();
		this.sliderPanel.setStyleName("sliderPanel");
		this.sliderPanel.add(this.min);
		this.sliderPanel.add(this.max);
		this.sliderPanel.add(this.increment);

		this.inputFieldPanel.add(this.sliderPanel);
	}

	private void addRadioButtonChangeHandler() {

		final RadioChangeHandler handler = new RadioChangeHandler() {
			@Override
			public void onRadioChange(final RadioChangeEvent event) {
				SliderDialog.this.setDefaultValues();
			}
		};

		this.radioGroup.addRadioChangeHandler(handler);
	}

	private void addButtonPanel() {
		this.buttonPanel = new HorizontalPanel();
		this.buttonPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.buttonPanel.setStyleName("buttonPanel");

		this.ok.addStyleName("ok");
		this.ok.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				onOK();
			}
		});

		this.cancel.setStyleName("last");
		this.cancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				onCancel();
			}
		});
		this.buttonPanel.add(this.ok);
		this.buttonPanel.add(this.cancel);
		this.contentPanel.add(this.buttonPanel);
	}

	public void redefineSlider(final GeoNumeric geo) {
		setInputText(geo.getLabel(StringTemplate.defaultTemplate));
		this.increment.setText(geo.getAnimationStepObject().getLabel(
				StringTemplate.editTemplate));
		this.max.setText(geo.getIntervalMaxObject().getLabel(
				StringTemplate.editTemplate));
		this.min.setText(geo.getIntervalMinObject().getLabel(
				StringTemplate.editTemplate));
	}

	protected void setDefaultValues() {
		if (this.isNumber()) {
			final GeoNumeric num = new GeoNumeric(this.app.getKernel()
					.getConstruction());
			setInputText(num.getFreeLabel(null));

			this.min.setText("-5");
			this.max.setText("5");
			this.increment.setText("0.1");
		} else {
			final GeoAngle angle = new GeoAngle(this.app.getKernel()
					.getConstruction());
			setInputText(angle.getFreeLabel(null));

			this.min.setText("0\u00B0"); // 0°
			this.max.setText("360\u00B0");
			this.increment.setText("1\u00B0");
		}
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

	public boolean isNumber() {
		return this.radioButton[0].isActivated();
	}

	@Override
	public void show() {
		super.show();
		if (getType() == DialogType.Slider) {
			showRadioButtons(this.app.getLocalization().getMenu("Number"),
					this.app.getLocalization().getMenu("Angle"));
			setDefaultValues();
		}
	}

	@Override
	public void hide() {
		super.hide();
		setType(DialogType.Slider);

		// removes focus and highlighting of the textBoxes
		this.min.setInactive();
		this.max.setInactive();
		this.increment.setInactive();
	}

	@Override
	public void setLabels() {
		setTitle(this.app.getLocalization().getMenu("Slider"));
		this.ok.setText(this.app.getLocalization().getPlain("Apply"));
		this.cancel.setText(this.app.getLocalization().getPlain("Cancel"));
		this.min.setLabelText(this.app.getLocalization().getPlain("min"));
		this.max.setLabelText(this.app.getLocalization().getPlain("max"));
		this.increment.setLabelText(this.app.getLocalization().getMenu("Step"));
		this.radioButton[0].setLabel(this.app.getLocalization().getMenu(
				"Number"));
		this.radioButton[1].setLabel(this.app.getLocalization()
				.getMenu("Angle"));
	}
}

package org.geogebra.web.full.gui.dialog;

import java.util.EnumSet;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.Export3dDialogInterface;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.validator.NumberValidator;
import org.geogebra.common.kernel.validator.exception.NumberValueOutOfBoundsException;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.NumberFormatAdapter;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW.InsertHandler;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW.OnBackSpaceHandler;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Dialog for export 3D
 *
 */
public class Export3dDialog extends OptionDialog
		implements Export3dDialogInterface, SetLabels {

	final static private double MM_TO_CM = 0.1;

	private Runnable onExportButtonPressed;
	private ParsableComponentInputField lineThicknessValue;
	private double lastUpdatedScale;
	private double lastUpdatedThickness;

	/**
	 * number formating for dimensions
	 */
	static final NumberFormatAdapter dimensionNF;
	/**
	 * number formating for scale
	 */
	static final NumberFormatAdapter scaleNF;

	static {
		dimensionNF = FormatFactory.getPrototype().getNumberFormat("#.#", 1);
		scaleNF = FormatFactory.getPrototype().getNumberFormat("#.##", 2);
	}

	private class ParsableComponentInputField extends ComponentInputField {

		private NumberValidator numberValidator;
		private Localization localization;
		private double parsedValue;

		public ParsableComponentInputField(AppW app, String placeholder,
				String labelTxt, String errorTxt, String defaultValue,
				int width, String suffixTxt) {
			super(app, placeholder, labelTxt, errorTxt, defaultValue, width,
					suffixTxt);
			numberValidator = new NumberValidator(
					app.getKernel().getAlgebraProcessor());
			localization = app.getLocalization();
		}

		public void setValue(double v, NumberFormatAdapter nf) {
			parsedValue = v;
			setInputText(nf.format(v));
		}

		public double getParsedValue() {
			return parsedValue;
		}

		/**
		 * parse current input text to double value
		 * 
		 * @param showError
		 *            if error should be shown
		 * @return true if parsed ok
		 */
		public boolean parse(boolean showError) {
			try {
				parsedValue = numberValidator.getDouble(getText(), 0d);
				setErrorResolved();
				return true;
			} catch (NumberValueOutOfBoundsException e) {
				if (showError) {
					showError(localization.getError(
							NumberValidator.NUMBER_NEGATIVE_ERROR_MESSAGE_KEY));
				}
				return false;
			} catch (Exception e) {
				if (showError) {
					showError(localization.getError(
							NumberValidator.NUMBER_FORMAT_ERROR_MESSAGE_KEY));
				}
				return false;
			}
		}

	}

	private enum DimensionField {
		WIDTH(dimensionNF) {
			@Override
			protected void createUpdateSet() {
				updateSet = EnumSet.of(LENGTH, HEIGHT, SCALE_CM);
			}
		},
		LENGTH(dimensionNF) {
			@Override
			protected void createUpdateSet() {
				updateSet = EnumSet.of(WIDTH, HEIGHT, SCALE_CM);
			}
		},
		HEIGHT(dimensionNF) {
			@Override
			protected void createUpdateSet() {
				updateSet = EnumSet.of(WIDTH, LENGTH, SCALE_CM);
			}
		},
		SCALE_CM(scaleNF) {
			@Override
			protected void createUpdateSet() {
				updateSet = EnumSet.of(WIDTH, LENGTH, HEIGHT);
			}

			@Override
			protected void setValue(double v) {
				if (v > 1) {
					super.setValue(v);
					SCALE_UNIT.setValue(1);
				} else {
					super.setValue(1);
					SCALE_UNIT.setValue(1 / v);
				}
			}

			@Override
			protected double calcCurrentRatio() {
				return super.calcCurrentRatio()
						/ SCALE_UNIT.inputField.getParsedValue();
			}

		},
		SCALE_UNIT(scaleNF) {
			@Override
			protected void createUpdateSet() {
				updateSet = EnumSet.of(WIDTH, LENGTH, HEIGHT);
			}

			@Override
			protected double calcCurrentRatio() {
				return SCALE_CM.calcCurrentRatio();
			}
		};

		ParsableComponentInputField inputField;
		double initValue;
		final private NumberFormatAdapter nf;
		protected EnumSet<DimensionField> updateSet;

		private DimensionField(NumberFormatAdapter nf) {
			this.nf = nf;
		}

		public void setInputField(ParsableComponentInputField field) {
			this.inputField = field;
		}

		public void setInitValue(double v) {
			this.initValue = v * MM_TO_CM;
			setValue(initValue);
		}

		protected void setValue(double v) {
			inputField.setValue(v, nf);
		}

		public void setController() {
			// from hardware keyboard
			inputField.getTextField().getTextComponent()
					.addKeyUpHandler(new KeyUpHandler() {
						public void onKeyUp(KeyUpEvent e) {
							parseAndUpdateOthers();
						}
					});

			// from soft keyboard
			inputField.getTextField().getTextComponent()
					.addInsertHandler(new InsertHandler() {
						public void onInsert(String text) {
							parseAndUpdateOthers();
						}
					});
			inputField.getTextField().getTextComponent()
					.addOnBackSpaceHandler(new OnBackSpaceHandler() {
						public void onBackspace() {
							parseAndUpdateOthers();
						}
					});
		}

		void parseAndUpdateOthers() {
			if (inputField.parse(false)) {
				updateOthers(calcCurrentRatio());
			}
		}

		private void updateOthers(double ratio) {
			for (DimensionField dimension : getUpdateSet()) {
				dimension.update(ratio);
			}
		}

		protected double calcCurrentRatio() {
			return inputField.getParsedValue() / initValue;
		}

		abstract protected void createUpdateSet();
		
		private EnumSet<DimensionField> getUpdateSet() {
			if (updateSet == null) {
				createUpdateSet();
			}
			return updateSet;
		}

		private void update(double ratio) {
			setValue(initValue * ratio);
		}

		static public double calcScale() {
			return (SCALE_CM.inputField.getParsedValue()
					/ SCALE_UNIT.inputField.getParsedValue())
					/ MM_TO_CM;
		}
	}

	/**
	 * Constructor
	 * 
	 * @param app
	 *            app
	 * @param view
	 *            exported view
	 */
	public Export3dDialog(final AppW app, final View view) {
		super(app.getPanel(), app, false);
		buildGui();
		setPrimaryButtonEnabled(true);
		this.addCloseHandler(new CloseHandler<GPopupPanel>() {
			@Override
			public void onClose(CloseEvent<GPopupPanel> event) {
				app.getKernel().detach(view);
				app.unregisterPopup(Export3dDialog.this);
				app.hideKeyboard();
			}
		});
		setGlassEnabled(true);
	}

	private void buildGui() {
		addStyleName("export3dDialog");
		FlowPanel contentPanel = new FlowPanel();
		buildDimensionsPanel(contentPanel);
		buildScalePanel(contentPanel);
		buildLineThicknessPanel(contentPanel);
		buildButtonPanel(contentPanel);
		add(contentPanel);
		setLabels();
		createController();
	}

	private void buildDimensionsPanel(FlowPanel root) {
		FlowPanel dimensionsPanel = new FlowPanel();
		dimensionsPanel.setStyleName("panelRow");
		DimensionField.WIDTH
				.setInputField(addTextField("Width", "cm", dimensionsPanel));
		DimensionField.LENGTH
				.setInputField(addTextField("Length", "cm", dimensionsPanel));
		DimensionField.HEIGHT
				.setInputField(addTextField("Height", "cm", dimensionsPanel));
		root.add(dimensionsPanel);
	}

	private void buildScalePanel(FlowPanel root) {
		FlowPanel scalePanel = new FlowPanel();
		scalePanel.setStyleName("panelRow");
		DimensionField.SCALE_UNIT
				.setInputField(addTextField("Scale", "units", scalePanel));
		Label equalLabel = new Label();
		equalLabel.setText("=");
		equalLabel.addStyleName("equal");
		scalePanel.add(equalLabel);
		DimensionField.SCALE_CM
				.setInputField(addTextField(null, "cm", scalePanel));
		root.add(scalePanel);
	}

	private void buildLineThicknessPanel(FlowPanel root) {
		lineThicknessValue = addTextField("Thickness", "mm", root);
	}

	private ParsableComponentInputField addTextField(String labelText,
			String suffixText, FlowPanel root) {
		final ParsableComponentInputField field = new ParsableComponentInputField(
				(AppW) app, null, labelText, null, "", 3, suffixText);
		root.add(field);
		return field;
	}

	private void buildButtonPanel(FlowPanel root) {
		root.add(getButtonPanel());
	}

	@Override
	protected void processInput() {
		// check if everything can be parsed ok
		boolean ok = true;
		for (DimensionField f : DimensionField.values()) {
			ok = f.inputField.parse(true) & ok;
		}
		ok = lineThicknessValue.parse(true) & ok;
		if (ok) {
			updateScaleAndThickness();
			hide();
			if (onExportButtonPressed != null) {
				onExportButtonPressed.run();
			}
		}
	}

	@Override
	public void setLabels() {
		getCaption()
				.setText(app.getLocalization().getMenu("DownloadAsStl"));
		for (DimensionField f : DimensionField.values()) {
			f.inputField.setLabels();
		}
		lineThicknessValue.setLabels();
		updateButtonLabels("Download");
	}

	private void initValues(double width, double length, double height,
			double scale, double thickness) {
		DimensionField.WIDTH.setInitValue(width);
		DimensionField.LENGTH.setInitValue(length);
		DimensionField.HEIGHT.setInitValue(height);
		DimensionField.SCALE_CM.setInitValue(scale);
		lineThicknessValue.setInputText(dimensionNF.format(thickness));
	}

	@Override
	public void show(double width, double length, double height, double scale,
			double thickness, Runnable exportAction) {
		initValues(width, length, height, scale, thickness);
		this.onExportButtonPressed = exportAction;
		((AppW) app).registerPopup(this);
		super.show();
		centerAndResize(((AppW) app).getAppletFrame().getKeyboardHeight());
	}

	static private void createController() {
		for (DimensionField dimension : DimensionField.values()) {
			dimension.setController();
		}
	}

	private void updateScaleAndThickness() {
		lastUpdatedScale = DimensionField.calcScale();
		lastUpdatedThickness = lineThicknessValue.getParsedValue();
	}

	@Override
	public double getCurrentScale() {
		return lastUpdatedScale;
	}

	@Override
	public double getCurrentThickness() {
		return lastUpdatedThickness;
	}

}

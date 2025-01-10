package org.geogebra.web.full.gui.dialog;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.gui.dialog.Export3dDialogInterface;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.validator.NumberValidator;
import org.geogebra.common.kernel.validator.exception.NumberValueOutOfBoundsException;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.NumberFormatAdapter;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

/**
 * Dialog for export 3D
 */
public class Export3dDialog extends ComponentDialog
		implements Export3dDialogInterface, HasKeyboardPopup {
	final static private double MM_TO_CM = 0.1;

	private Runnable onExportButtonPressed;
	private ParsableComponentInputField lineThicknessValue;
	private String oldLineThicknessValue;
	private ComponentCheckbox filledSolid;
	private double lastUpdatedScale;
	private double lastUpdatedThickness;

	/**
	 * number formatting for dimensions
	 */
	private static final NumberFormatAdapter dimensionNF;
	/**
	 * number formatting for scale
	 */
	private static final NumberFormatAdapter scaleNF;

	static {
		dimensionNF = FormatFactory.getPrototype().getNumberFormat("#.#", 1);
		scaleNF = FormatFactory.getPrototype().getNumberFormat("#.##", 2);
	}

	static private class ParsableComponentInputField
			extends ComponentInputField {

		private final NumberValidator numberValidator;
		private final Localization localization;
		private double parsedValue;

		public ParsableComponentInputField(AppW app, String placeholder,
				String labelTxt, String errorTxt, String defaultValue,
				int width, String suffixTxt) {
			super(app, placeholder, labelTxt, errorTxt, defaultValue, width,
					suffixTxt, false);
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
		 * @param canBeEqual
		 *            if value can be equal to min value
		 * @param canBeEmpty
		 *            if text field can be empty (value is 0)
		 * @return true if parsed ok
		 */
		public boolean parse(boolean showError, boolean canBeEqual,
				boolean canBeEmpty) {
			if (canBeEmpty && StringUtil.emptyTrim(getText())) {
				parsedValue = 0;
				return true;
			}
			try {
				parsedValue = canBeEqual
						? numberValidator.getDoubleGreaterOrEqual(getText(), 0d)
						: numberValidator.getDouble(getText(), 0d);
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

	DimensionField WIDTH = new DimensionField(dimensionNF) {
		@Override
		protected void createUpdateSet() {
			updateSet = Arrays.asList(LENGTH, HEIGHT, SCALE_CM);
		}
	};
	DimensionField LENGTH = new DimensionField(dimensionNF) {
		@Override
		protected void createUpdateSet() {
			updateSet = Arrays.asList(WIDTH, HEIGHT, SCALE_CM);
		}
	};
	DimensionField HEIGHT = new DimensionField(dimensionNF) {
		@Override
		protected void createUpdateSet() {
			updateSet = Arrays.asList(WIDTH, LENGTH, SCALE_CM);
		}
	};
	DimensionField SCALE_UNIT = new DimensionField(scaleNF) {
		@Override
		protected void createUpdateSet() {
			updateSet = Arrays.asList(WIDTH, LENGTH, HEIGHT);
		}

		@Override
		protected double calcCurrentRatio() {
			return SCALE_CM.calcCurrentRatio();
		}
	};
	DimensionField SCALE_CM = new DimensionField(scaleNF) {
		@Override
		protected void createUpdateSet() {
			updateSet = Arrays.asList(WIDTH, LENGTH, HEIGHT);
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

	};

	private abstract static class DimensionField {

		ParsableComponentInputField inputField;
		double initValue;
		final private NumberFormatAdapter nf;
		protected List<DimensionField> updateSet;
		private boolean isUsed;

		DimensionField(NumberFormatAdapter nf) {
			this.nf = nf;
			isUsed = true;
		}

		public void setInputField(ParsableComponentInputField field) {
			this.inputField = field;
		}

		public void setInitValue(double v) {
			this.initValue = v * MM_TO_CM;
			setValue(initValue);
			if (DoubleUtil.isZero(initValue)) {
				isUsed = false;
				inputField.setVisible(false);
			}
		}

		protected void setValue(double v) {
			inputField.setValue(v, nf);
		}

		public void setController() {
			// from hardware keyboard
			inputField.getTextField().getTextComponent()
					.addKeyUpHandler(e -> parseAndUpdateOthers());

			// from soft keyboard
			inputField.getTextField().getTextComponent()
					.addInsertHandler(text -> parseAndUpdateOthers());
			inputField.getTextField().getTextComponent()
					.addOnBackSpaceHandler(this::parseAndUpdateOthers);
		}

		void parseAndUpdateOthers() {
			if (inputField.parse(false, false, false)) {
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
		
		private List<DimensionField> getUpdateSet() {
			if (updateSet == null) {
				createUpdateSet();
			}
			return updateSet;
		}

		private void update(double ratio) {
			setValue(initValue * ratio);
		}

		public boolean parse() {
			return !isUsed || inputField.parse(true, false, false);
		}
	}

	private double calcScale() {
		return (SCALE_CM.inputField.getParsedValue()
				/ SCALE_UNIT.inputField.getParsedValue())
				/ MM_TO_CM;
	}

	/**
	 * Constructor
	 * 
	 * @param app
	 *            app
	 * @param data
	 *            dialog translation keys
	 * @param view
	 *            exported view
	 */
	public Export3dDialog(final AppW app, DialogData data, final View view) {
		super(app, data, false, true);
		addStyleName("export3dDialog");
		buildContent();
		this.addCloseHandler(event -> {
			app.getKernel().detach(view);
			app.unregisterPopup(this);
			app.hideKeyboard();
		});
	}

	private void buildContent() {
		FlowPanel contentPanel = new FlowPanel();
		buildDimensionsPanel(contentPanel);
		buildScalePanel(contentPanel);
		buildLineThicknessPanel(contentPanel);
		addDialogContent(contentPanel);
		createController();
	}

	private void buildDimensionsPanel(FlowPanel root) {
		FlowPanel dimensionsPanel = new FlowPanel();
		dimensionsPanel.setStyleName("panelRow");
		WIDTH
				.setInputField(addTextField("Width", "cm", dimensionsPanel));
		LENGTH
				.setInputField(addTextField("Length", "cm", dimensionsPanel));
		HEIGHT
				.setInputField(addTextField("Height", "cm", dimensionsPanel));
		root.add(dimensionsPanel);
	}

	private void buildScalePanel(FlowPanel root) {
		FlowPanel scalePanel = new FlowPanel();
		scalePanel.setStyleName("panelRow");
		SCALE_UNIT
				.setInputField(addTextField("Scale", "units", scalePanel));
		Label equalLabel = new Label();
		equalLabel.setText("=");
		equalLabel.addStyleName("equal");
		scalePanel.add(equalLabel);
		SCALE_CM
				.setInputField(addTextField(null, "cm", scalePanel));
		root.add(scalePanel);
	}

	private void buildLineThicknessPanel(FlowPanel root) {
		FlowPanel thicknessPanel = new FlowPanel();
		thicknessPanel.setStyleName("panelRow");
		lineThicknessValue = addTextField("STL.Thickness", "mm",
				thicknessPanel);
		filledSolid = new ComponentCheckbox(app.getLocalization(), false,
				"STL.FilledSolid", this::onFilledSolidAction);
		thicknessPanel.add(filledSolid);
		root.add(thicknessPanel);
	}

	private void onFilledSolidAction(boolean filled) {
		if (filled) {
			oldLineThicknessValue = lineThicknessValue.getText();
			lineThicknessValue.setInputText("");
		} else {
			String current = lineThicknessValue.getText();
			if (oldLineThicknessValue != null && StringUtil.emptyTrim(current)) {
				lineThicknessValue
						.setInputText(oldLineThicknessValue);
			}
		}
	}

	private ParsableComponentInputField addTextField(String labelText,
			String suffixText, FlowPanel root) {
		final ParsableComponentInputField field = new ParsableComponentInputField(
				(AppW) app, null, labelText, null, "", 0, suffixText);
		root.add(field);
		return field;
	}

	private static boolean checkOkAndSetFocus(boolean ok, boolean currentOk,
			ComponentInputField inputField) {
		if (ok) {
			if (!currentOk) {
				inputField.focusDeferred();
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public void onPositiveAction() {
		// check if everything can be parsed ok
		boolean ok = true;
		for (DimensionField f : getDimensionFields()) {
			ok = checkOkAndSetFocus(ok, f.parse(), f.inputField);

		}
		ok = checkOkAndSetFocus(ok,
				lineThicknessValue.parse(true, true, true),
				lineThicknessValue);
		if (ok) {
			updateScaleAndThickness();
			hide();
			if (onExportButtonPressed != null) {
				onExportButtonPressed.run();
			}
		}
	}

	private List<DimensionField> getDimensionFields() {
		return Arrays.asList(WIDTH, LENGTH, HEIGHT, SCALE_UNIT, SCALE_CM);
	}

	private void initValues(double width, double length, double height,
			double scale, double thickness) {
		WIDTH.setInitValue(width);
		LENGTH.setInitValue(length);
		HEIGHT.setInitValue(height);
		SCALE_CM.setInitValue(scale);
		lineThicknessValue.setInputText(dimensionNF.format(thickness));
	}

	@Override
	public void show(double width, double length, double height, double scale,
			double thickness, Runnable exportAction) {
		initValues(width, length, height, scale, thickness);
		this.onExportButtonPressed = exportAction;
		((AppW) app).registerPopup(this);
		super.show();
	}

	private void createController() {
		for (DimensionField dimension : getDimensionFields()) {
			dimension.setController();
		}
	}

	private void updateScaleAndThickness() {
		lastUpdatedScale = calcScale();
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

	@Override
	public boolean wantsFilledSolids() {
		return filledSolid.isSelected()
				|| DoubleUtil.isZero(getCurrentThickness());
	}
}
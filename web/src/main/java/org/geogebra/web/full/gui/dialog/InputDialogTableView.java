package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.dialog.validator.TableValuesDialogValidator;
import org.geogebra.common.gui.view.table.InvalidValuesException;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;
import org.geogebra.web.shared.components.ComponentDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * dialog opened from av context menu of functions and lines by clicking
 * on Table of values
 *
 */
public class InputDialogTableView extends ComponentDialog
		implements HasKeyboardPopup {
	private ComponentInputField startValue;
	private ComponentInputField endValue;
	private ComponentInputField step;
	private GeoElement geo;
	private Label errorLabel;
	private TableValuesDialogValidator validator;

	/**
	 * Create new dialog. NOT modal to make sure onscreen keyboard still works.
	 * 
	 * @param app
	 *            see {@link AppW}
	 * @param data
	 * 			  dialog transkeys
	 */
	public InputDialogTableView(final AppW app, DialogData data) {
		super(app, data, false, true);
		addStyleName("tableOfValuesDialog");
		buildContent();
		validator = new TableValuesDialogValidator(app);
		this.addCloseHandler(event -> {
			app.unregisterPopup(this);
			app.hideKeyboard();
		});
	}

	/**
	 * @return input field for start value
	 */
	public ComponentInputField getStartField() {
		return startValue;
	}

	private void buildContent() {
		FlowPanel contentPanel = new FlowPanel();
		buildTextFieldPanel(contentPanel);
		errorLabel = new Label();
		errorLabel.setStyleName("globalErrorLabel");
		contentPanel.add(errorLabel);
		addDialogContent(contentPanel);
	}

	private void buildTextFieldPanel(FlowPanel root) {
		startValue = addTextField("StartValueX", root);
		endValue = addTextField("EndValueX", root);
		step = addTextField("Step", root);
		// last input text field shouldn't have any bottom margin
		step.addStyleName("noBottomMarg");
		TestHarness.setAttr(startValue, "startValue");
		TestHarness.setAttr(endValue, "endValue");
		TestHarness.setAttr(step, "stepValue");
	}

	private ComponentInputField addTextField(String labelText, FlowPanel root) {
		final ComponentInputField field = new ComponentInputField((AppW) app,
				null, labelText, null, "", 20, 1,
				false);
		root.add(field);
		return field;
	}

	@Override
	public void show() {
		endValue.resetInputField();
		step.resetInputField();
		super.show();
		startValue.focusDeferred();
	}

	/**
	 * @param functionGeo
	 *            function
	 */
	public void show(GeoElement functionGeo) {
		this.geo = functionGeo;
		TableValuesView tv = (TableValuesView) app.getGuiManager().getTableValuesView();
		startValue.setInputText(tv.getValuesMinStr());
		endValue.setInputText(tv.getValuesMaxStr());
		step.setInputText(tv.getValuesStepStr());
		errorLabel.setText("");
		((AppW) app).registerPopup(this);
		show();
	}

	private void openTableView() {
		double[] inputFieldValues = validator.getDoubles(startValue, endValue, step);
		if (startValue.hasError()) {
			startValue.focusDeferred();
		} else if (endValue.hasError()) {
			endValue.focusDeferred();
		} else if (step.hasError()) {
			step.focusDeferred();
		}
		if (inputFieldValues != null) {
			try {
				initTableValuesView(inputFieldValues[0], inputFieldValues[1], inputFieldValues[2]);
				hide();
			} catch (InvalidValuesException ex) {
				errorLabel
						.setText(ex.getLocalizedMessage(app.getLocalization()));
				errorLabel.getElement().scrollIntoView();
			}
		} else {
			errorLabel.setText("");
		}
	}

	@Override
	public void onPositiveAction() {
		openTableView();
	}

	/**
	 * Initializes Table View
	 * 
	 * @param min
	 *            min x-value.
	 * @param max
	 *            max x-value.
	 * @param stepVal
	 *            x step value.
	 * @throws InvalidValuesException
	 *             if (max-min)/step is too big
	 */
	private void initTableValuesView(double min, double max, double stepVal)
			throws InvalidValuesException {
		GuiManagerW gui = (GuiManagerW) app.getGuiManager();
		gui.getTableValuesView().setValues(min, max, stepVal);
		dispatchSetTVValues(min, max, stepVal);
		if (geo != null) {
			gui.addGeoToTableValuesView(geo);
			app.getKernel().attach(gui.getTableValuesView());
		} else {
			gui.getUnbundledToolbar().resize();
		}
	}

	private void dispatchSetTVValues(double valuesMin, double valuesMax, double valuesStep) {
		String values = valuesMin + "," + valuesMax + ","
				+ valuesStep;
		app.dispatchEvent(new Event(EventType.SET_VALUES_TV, null, values));
	}
}
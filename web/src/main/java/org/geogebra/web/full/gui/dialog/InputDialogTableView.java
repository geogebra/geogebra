package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.table.InvalidValuesException;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.parser.stringparser.StringParser;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * @author csilla
 * 
 *         dialog opened from av context menu of functions and lines by clicking
 *         on Table of values
 *
 */
public class InputDialogTableView extends OptionDialog
		implements SetLabels {
	private ComponentInputField startValue;
	private ComponentInputField endValue;
	private ComponentInputField step;
	private GeoElement geo;
	private boolean hasErrors = false;
	private Label errorLabel;

	/**
	 * Create new dialog. NOT modal to make sure onscreen keyboard still works.
	 * 
	 * @param app
	 *            see {@link AppW}
	 */
	public InputDialogTableView(AppW app) {
		super(app.getPanel(), app, false);
		buildGui();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				getStartField().getTextField().getTextComponent()
						.setFocus(true);
			}
		});
		setPrimaryButtonEnabled(true);
	}

	/**
	 * @return input field for start value
	 */
	public ComponentInputField getStartField() {
		return startValue;
	}

	private void buildGui() {
		addStyleName("tableOfValuesDialog");
		FlowPanel contentPanel = new FlowPanel();
		errorLabel = new Label();
		buildTextFieldPanel(contentPanel);
		contentPanel.add(errorLabel);
		buildButtonPanel(contentPanel);
		add(contentPanel);
		setLabels();
	}

	private void buildTextFieldPanel(FlowPanel root) {
		startValue = addTextField("StartValueX", root);
		endValue = addTextField("EndValueX", root);
		step = addTextField("Step", root);
	}

	private ComponentInputField addTextField(String labelText, FlowPanel root) {
		final ComponentInputField field = new ComponentInputField((AppW) app,
				null, labelText, null, "", 20);
		root.add(field);
		return field;
	}

	private void buildButtonPanel(FlowPanel root) {
		root.add(getButtonPanel());
	}

	@Override
	public void setLabels() {
		getCaption().setText(app.getLocalization().getMenu("TableOfValues"));
		startValue.setLabels();
		endValue.setLabels();
		step.setLabels();
		updateButtonLabels("OK");
	}

	@Override
	public void show() {
		super.show();
		super.centerAndResize(
				((AppW) app).getAppletFrame().getKeyboardHeight());
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
		show();
	}

	private void openTableView() {
		hasErrors = false;
		// -inf to make sure the min/max comparison works for max field
		double start = validate(startValue, new StringParser(app),
				Double.NEGATIVE_INFINITY);
		double end = validate(endValue,
				StringParser.minValueConverter(app, start), 0);
		double stepVal = validate(step,
				StringParser.positiveDoubleConverter(app), 0);
		if (!hasErrors) {
			try {
				initTableValuesView(start, end, stepVal);
				hide();
			} catch (InvalidValuesException ex) {
				errorLabel
						.setText(ex.getLocalizedMessage(app.getLocalization()));
			}
		}
	}

	private double validate(ComponentInputField startValue2,
			StringParser stringParser, double fallback) {
		double start = fallback;
		try {
			start = stringParser.parse(startValue2.getInputText());
			startValue2.setError(null);
		} catch (NumberFormatException e) {
			startValue2
					.setError(e.getMessage());
			this.hasErrors = true;
		}
		return start;
	}

	@Override
	protected void processInput() {
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
		if (geo != null) {
			gui.addGeoToTableValuesView(geo);
			app.getKernel().attach(gui.getTableValuesView());
		} else {
			gui.getUnbundledToolbar().resize();
		}
	}
}

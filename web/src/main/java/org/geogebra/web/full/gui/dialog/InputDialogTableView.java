package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.parser.stringparser.StringParser;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FlowPanel;

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

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	public InputDialogTableView(AppW app) {
		super(app.getPanel(), app);
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
		buildTextFieldPanel(contentPanel);
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
		double start = validate(startValue, new StringParser(app));
		double end = validate(endValue, new StringParser(app));
		double stepVal = validate(step,
				StringParser.positiveDoubleConverter(app));
		if (!hasErrors) {
			try {
				((GuiManagerInterfaceW) app.getGuiManager())
						.initTableValuesView(start, end, stepVal, geo);
			} catch (Exception e) {
				ToolTipManagerW.sharedInstance().showBottomMessage(
						app.getLocalization().getError("InvalidInput"), true,
						(AppW) app);
			} finally {
				hide();
			}
		}
	}

	private double validate(ComponentInputField startValue2,
			StringParser stringParser) {
		double start = 0;
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
}

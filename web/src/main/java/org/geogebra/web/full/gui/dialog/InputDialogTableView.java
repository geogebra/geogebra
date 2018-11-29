package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.geos.GeoElement;
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
		ComponentInputField field = new ComponentInputField((AppW) app, null,
				labelText, null, "", 20);
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
		double start = 0;
		try {
			start = Double.parseDouble(startValue.getInputText());
		} catch (Exception e) {
			startValue.setError(app.getLocalization().getError("InvalidInput"));
			return;
		}
		try {
			double end = Double.parseDouble(endValue.getInputText());
			double stepVal = Double.parseDouble(step.getInputText());
			((GuiManagerInterfaceW) app.getGuiManager()).initTableValuesView(start, end, stepVal,
					geo);
		} catch (Exception e) {
			ToolTipManagerW.sharedInstance()
					.showBottomMessage(app.getLocalization().getError("InvalidInput"), true,
							(AppW) app);
		} finally {
			hide();
		}
	}

	@Override
	protected void processInput() {
		openTableView();
	}
}

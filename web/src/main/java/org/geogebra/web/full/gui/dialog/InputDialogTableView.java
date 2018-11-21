package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 * 
 *         dialog opened from av context menu of functions and lines by clicking
 *         on Table of values
 *
 */
public class InputDialogTableView extends DialogBoxW
		implements SetLabels, FastClickHandler {
	private ComponentInputField startValue;
	private ComponentInputField endValue;
	private ComponentInputField step;
	private FlowPanel buttonPanel;
	private StandardButton cancelBtn;
	private StandardButton okBtn;
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
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		cancelBtn = createTxtButton(buttonPanel, "cancelBtn", true);
		okBtn = createTxtButton(buttonPanel, "okBtn", true);
		root.add(buttonPanel);
	}

	private StandardButton createTxtButton(FlowPanel root, String styleName,
			boolean isEnabled) {
		StandardButton btn = new StandardButton("", app);
		btn.addStyleName(styleName);
		btn.setEnabled(isEnabled);
		btn.addFastClickHandler(this);
		root.add(btn);
		return btn;
	}

	@Override
	public void setLabels() {
		getCaption().setText(app.getLocalization().getMenu("TableOfValues"));
		startValue.setLabels();
		endValue.setLabels();
		step.setLabels();
		cancelBtn.setText(app.getLocalization().getMenu("Cancel"));
		okBtn.setText(app.getLocalization().getMenu("OK"));
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
		try {
			double start = Double.parseDouble(startValue.getInputText());
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
	public void onClick(Widget source) {
		if (source == cancelBtn) {
			hide();
		} else if (source == okBtn) {
			openTableView();
		}
	}
}

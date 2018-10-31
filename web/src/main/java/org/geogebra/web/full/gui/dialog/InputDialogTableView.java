package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.SetLabels;
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
	private AppW appW;
	private FlowPanel contentPanel;
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
		this.appW = app;
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
		contentPanel = new FlowPanel();
		buildTextFieldPanel();
		buildButtonPanel(contentPanel);
		this.add(contentPanel);
		setLabels();
	}

	private void buildTextFieldPanel() {
		startValue = addTextField("StartValueX", "-2");
		endValue = addTextField("EndValueX", "2");
		step = addTextField("Step", "1");
	}

	private ComponentInputField addTextField(String labelText, String defaultValue) {
		ComponentInputField field = new ComponentInputField(appW, null,
				labelText,
				null, defaultValue, 20);
		contentPanel.add(field);
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
		StandardButton btn = new StandardButton("", appW);
		btn.addStyleName(styleName);
		btn.setEnabled(isEnabled);
		btn.addFastClickHandler(this);
		root.add(btn);
		return btn;
	}

	@Override
	public void setLabels() {
		getCaption().setText(appW.getLocalization().getMenu("TableOfValues"));
		startValue.setLabels();
		endValue.setLabels();
		step.setLabels();
		cancelBtn.setText(appW.getLocalization().getMenu("Cancel"));
		okBtn.setText(appW.getLocalization().getMenu("OK"));
	}

	@Override
	public void show() {
		super.show();
		super.center();
	}

	public void show(GeoElement geo) {
		this.geo = geo;
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
			;
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

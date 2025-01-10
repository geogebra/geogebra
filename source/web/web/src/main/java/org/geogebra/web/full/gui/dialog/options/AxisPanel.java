package org.geogebra.web.full.gui.dialog.options;

import java.util.Arrays;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.model.AxisModel;
import org.geogebra.common.gui.dialog.options.model.AxisModel.IAxisModelListener;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.components.CompDropDown;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.components.ComponentCombobox;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;

import com.himamis.retex.editor.share.util.Unicode;

public class AxisPanel extends FlowPanel
		implements SetLabels, IAxisModelListener {

	protected AxisModel model;

	protected ComponentCheckbox cbShowAxis;
	protected ComponentCheckbox cbAxisNumber;
	protected ComponentCheckbox cbManualTicks;
	protected ComponentCheckbox cbPositiveAxis;
	protected ComponentCheckbox cbDrawAtBorder;
	protected ComponentCheckbox cbAllowSelection;

	protected ComponentCombobox ncbTickDist;
	protected CompDropDown lbTickStyle;
	private ComponentCombobox comboAxisLabel;
	private ComponentCombobox comboUnitLabel;
	protected AutoCompleteTextFieldW tfCross;

	private FormLabel crossAt;
	private FormLabel axisTicks;
	private FormLabel axisLabel;
	private FormLabel axisUnitLabel;

	private EuclidianView view;

	private Localization loc;

	/******************************************************
	 * @param app
	 *            application
	 * @param view
	 *            view
	 * @param axis
	 *            axis
	 * @param view3D
	 *            3D?
	 */
	public AxisPanel(AppW app, EuclidianView view, int axis, boolean view3D) {
		this.loc = app.getLocalization();
		this.view = view;
		model = new AxisModel(app, view, axis, this);

		String strAxisEn = model.getAxisName();

		// show axis
		cbShowAxis = new ComponentCheckbox(loc, true, "Show" + strAxisEn,
				model::showAxis);
		cbShowAxis.addStyleName("block");

		// show numbers
		cbAxisNumber = new ComponentCheckbox(loc, true, "ShowAxisNumbers",
				model::showAxisNumbers);
		cbAxisNumber.addStyleName("block");

		// show positive axis only
		cbPositiveAxis = new ComponentCheckbox(loc, false, "PositiveDirectionOnly",
				model::applyPositiveAxis);
		cbPositiveAxis.addStyleName("block");

		// allow axis selection
		cbAllowSelection = new ComponentCheckbox(loc, false, "SelectionAllowed",
				model::applyAllowSelection);

		// ticks
		lbTickStyle = new CompDropDown(app, "", model.getTickOptions(),
				model.getTickStyleIndex(axis));
		axisTicks = new FormLabel(loc.getMenu("AxisTicks") + ":")
				.setFor(lbTickStyle);
		axisTicks.addStyleName("dropDownLabel");
		lbTickStyle.addChangeHandler(() -> {
			int type = lbTickStyle.getSelectedIndex();
			model.applyTickStyle(type);
		});

		FlowPanel showTicksPanel = new FlowPanel();
		showTicksPanel.add(axisTicks);
		showTicksPanel.add(lbTickStyle);

		// distance
		cbManualTicks = new ComponentCheckbox(loc, false, "TickDistance",
				this::onDistanceSelected);

		ncbTickDist = new ComponentCombobox(app, "", Arrays.asList("1",
				Unicode.PI_STRING, Unicode.PI_HALF_STRING));
		ncbTickDist.addChangeHandler(() -> {
				model.applyTickDistance(ncbTickDist.getSelectedText());
			});

		FlowPanel distancePanel = new FlowPanel();
		distancePanel.add(cbManualTicks);
		distancePanel.add(ncbTickDist);

		// axis and unit label
		comboAxisLabel = new ComponentCombobox(app, "", model.getAxisLabelOptions());
		comboAxisLabel.addChangeHandler(() -> {
			String text = comboAxisLabel.getSelectedText().trim();
			model.applyAxisLabel(text);
		});
		comboAxisLabel.setDisabled(false);

		axisLabel = new FormLabel(loc.getMenu("AxisLabel") + ":")
				.setFor(comboAxisLabel);

		comboUnitLabel = new ComponentCombobox(app, "", model.getUnitLabelOptions());
		comboUnitLabel.addChangeHandler(() -> {
				String text = comboUnitLabel.getSelectedText().trim();
				model.applyUnitLabel(text);
		});

		axisUnitLabel = new FormLabel(loc.getMenu("AxisUnitLabel") + ":")
				.setFor(comboUnitLabel);
		axisUnitLabel.addStyleName("dropDownLabel");
		comboUnitLabel.setDisabled(false);
		FlowPanel labelPanel = new FlowPanel();
		labelPanel.add(axisLabel);
		labelPanel.add(comboAxisLabel);

		FlowPanel unitPanel = new FlowPanel();
		unitPanel.add(axisUnitLabel);
		unitPanel.add(comboUnitLabel);

		// cross at and stick to edge

		InputPanelW input = new InputPanelW(null, app,  false);
		tfCross = input.getTextComponent();
		tfCross.setAutoComplete(false);
		tfCross.removeSymbolTable();

		tfCross.addKeyHandler(e -> {
			if (e.isEnterKey()) {
				model.applyCrossing(tfCross.getText());
			}
		});

		tfCross.addBlurHandler(event -> model.applyCrossing(tfCross.getText()));

		crossAt = new FormLabel(loc.getMenu("CrossAt") + ":").setFor(tfCross);
		cbDrawAtBorder = new ComponentCheckbox(loc, false, "StickToEdge",
				model::applyDrawAtBorder);

		FlowPanel crossPanel = LayoutUtilW.panelRow(crossAt, tfCross,
				cbDrawAtBorder);

		distancePanel.setStyleName("listBoxPanel tickPanel");
		showTicksPanel.setStyleName("listBoxPanel");
		labelPanel.setStyleName("listBoxPanel tickPanel");
		unitPanel.setStyleName("listBoxPanel tickPanel");
		tfCross.setStyleName("numberInput");

		// add all panels
		add(cbShowAxis);
		add(cbAxisNumber);
		add(cbPositiveAxis);
		add(distancePanel);
		add(showTicksPanel);
		add(labelPanel);
		add(unitPanel);
		if (!view3D) {
			add(crossPanel);
		}
		add(cbAllowSelection);
		// no need to update panel,
		// updateGui called right after addTabs in OptionsEuclidianW
	}

	private void onDistanceSelected(boolean isTickDistanceOn) {
		model.applyTickDistance(isTickDistanceOn);
		ncbTickDist.setDisabled(!isTickDistanceOn);
		if (isTickDistanceOn) {
			model.applyTickDistance(ncbTickDist.getSelectedText());
		}
	}

	/**
	 * @param eView
	 *            view to set
	 */
	public void updateView(EuclidianView eView) {
		this.view = eView;
		model.setView(eView);
	}

	/**
	 * Update UI
	 */
	public void updatePanel() {
		int axis = model.getAxis();
		cbAxisNumber.setSelected(view.getShowAxesNumbers()[axis]);

		cbManualTicks.setSelected(!view.isAutomaticAxesNumberingDistance()[axis]);
		ncbTickDist.setDisabled(!cbManualTicks.isSelected());
		comboAxisLabel.setValue(view.getAxesLabels(true)[axis]);
		comboUnitLabel.setValue(view.getAxesUnitLabels()[axis]);

		int type = view.getAxesTickStyles()[axis];
		lbTickStyle.setSelectedIndex(type);

		setShowAxis(view.getShowAxis(axis));

		if (view.getDrawBorderAxes()[axis]) {
			tfCross.setText("");
		} else {
			tfCross.setText("" + view.getAxesCross()[axis]);
		}

		tfCross.setVisible(!view.getDrawBorderAxes()[axis]);
		cbPositiveAxis.setSelected(view.getPositiveAxes()[axis]);

		cbDrawAtBorder.setSelected(view.getDrawBorderAxes()[axis]);
		cbAllowSelection.setSelected(getModel().isSelectionAllowed());

	}

	/**
	 * @param value
	 *            whether to show axis
	 */
	public void setShowAxis(boolean value) {
		cbShowAxis.setSelected(value);
	}

	@Override
	public void setLabels() {
		cbShowAxis.setLabels();
		cbAxisNumber.setLabels();
		cbManualTicks.setLabels();
		axisTicks.setText(loc.getMenu("AxisTicks") + ":");
		cbPositiveAxis.setLabels();
		axisLabel.setText(loc.getMenu("AxisLabel") + ":");
		axisUnitLabel.setText(loc.getMenu("AxisUnitLabel") + ":");
		crossAt.setText(loc.getMenu("CrossAt") + ":");
		cbDrawAtBorder.setLabels();
		cbAllowSelection.setLabels();
		comboUnitLabel.setLabels();
		ncbTickDist.setLabels();
	}

	@Override
	public void setCrossText(String text) {
		tfCross.setText(text);
	}

	public AxisModel getModel() {
		return model;
	}
}

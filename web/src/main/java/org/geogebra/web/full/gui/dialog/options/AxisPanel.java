package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.model.AxisModel;
import org.geogebra.common.gui.dialog.options.model.AxisModel.IAxisModelListener;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.util.ComboBoxW;
import org.geogebra.web.full.gui.util.NumberListBox;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;

public class AxisPanel extends FlowPanel
		implements SetLabels, IAxisModelListener {

	protected AxisModel model;

	protected CheckBox cbShowAxis;
	protected CheckBox cbAxisNumber;
	protected CheckBox cbManualTicks;
	protected CheckBox cbPositiveAxis;
	protected CheckBox cbDrawAtBorder;
	protected CheckBox cbAllowSelection;

	protected NumberListBox ncbTickDist;
	protected ListBox lbTickStyle;
	private ComboBoxW comboAxisLabel;
	private ComboBoxW comboUnitLabel;
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
		// this.setBorder(LayoutUtil.titleBorder(loc.getMenu(strAxisEn)));

		// show axis
		cbShowAxis = new CheckBox(loc.getMenu("Show" + strAxisEn));
		cbShowAxis.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				model.showAxis(cbShowAxis.getValue());
			}
		});

		// show numbers
		cbAxisNumber = new CheckBox(loc.getMenu("ShowAxisNumbers"));
		cbAxisNumber.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				model.showAxisNumbers(cbAxisNumber.getValue());
			}
		});

		// show positive axis only
		cbPositiveAxis = new CheckBox(loc.getMenu("PositiveDirectionOnly"));
		cbPositiveAxis.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				model.applyPositiveAxis(cbPositiveAxis.getValue());
			}
		});

		// allow axis selection
		cbAllowSelection = new CheckBox(loc.getMenu("SelectionAllowed"));
		cbAllowSelection.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				model.applyAllowSelection(cbAllowSelection.getValue());
			}
		});

		// ticks
		lbTickStyle = new ListBox();
		axisTicks = new FormLabel(loc.getMenu("AxisTicks") + ":")
				.setFor(lbTickStyle);
		model.fillTicksCombo();

		lbTickStyle.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				int type = lbTickStyle.getSelectedIndex();
				model.applyTickStyle(type);
				// view.updateBackground();
				// updatePanel();
			}
		});

		FlowPanel showTicksPanel = new FlowPanel();
		showTicksPanel.add(axisTicks);
		showTicksPanel.add(lbTickStyle);

		// distance
		cbManualTicks = new CheckBox(loc.getMenu("TickDistance") + ":");
		cbManualTicks.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				boolean isTickDistanceOn = cbManualTicks.getValue();
				model.applyTickDistance(isTickDistanceOn);
				ncbTickDist.setEnabled(isTickDistanceOn);
				if (isTickDistanceOn) {
					model.applyTickDistance(ncbTickDist.getValue());
				}

			}
		});

		ncbTickDist = new NumberListBox(app) {

			@Override
			protected void onValueChange(String value) {
				model.applyTickDistance(ncbTickDist.getValue());

			}
		};

		FlowPanel distancePanel = new FlowPanel();
		distancePanel.add(cbManualTicks);
		distancePanel.add(ncbTickDist);

		// axis and unit label
		comboAxisLabel = new ComboBoxW(app) {

			@Override
			protected void onValueChange(String value) {
				String text = getValue().trim();
				model.applyAxisLabel(text);
			}
		};
		comboAxisLabel.setEnabled(true);
		model.fillAxisCombo();

		axisLabel = new FormLabel(loc.getMenu("AxisLabel") + ":")
				.setFor(comboAxisLabel);

		comboUnitLabel = new ComboBoxW(app) {

			@Override
			protected void onValueChange(String value) {
				String text = getValue().trim();
				model.applyUnitLabel(text);
			}
		};

		model.fillUnitLabel();
		axisUnitLabel = new FormLabel(loc.getMenu("AxisUnitLabel") + ":")
				.setFor(comboUnitLabel);
		comboUnitLabel.setEnabled(true);
		FlowPanel labelPanel = new FlowPanel();
		labelPanel.add(axisLabel);
		labelPanel.add(comboAxisLabel);

		FlowPanel unitPanel = new FlowPanel();
		unitPanel.add(axisUnitLabel);
		unitPanel.add(comboUnitLabel);

		// cross at and stick to edge

		InputPanelW input = new InputPanelW(null, app, 1, -1, true);
		tfCross = input.getTextComponent();
		tfCross.setAutoComplete(false);
		tfCross.removeSymbolTable();

		tfCross.addKeyHandler(new KeyHandler() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					model.applyCrossing(tfCross.getText());
				}
			}
		});

		tfCross.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				model.applyCrossing(tfCross.getText());
			}
		});

		crossAt = new FormLabel(loc.getMenu("CrossAt") + ":").setFor(tfCross);
		cbDrawAtBorder = new CheckBox(loc.getMenu("StickToEdge"));
		cbDrawAtBorder.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				model.applyDrawAtBorder(cbDrawAtBorder.getValue());
			}
		});

		FlowPanel crossPanel = LayoutUtilW.panelRow(crossAt, tfCross,
				cbDrawAtBorder);

		cbShowAxis.setStyleName("checkBoxPanel");
		cbAxisNumber.setStyleName("checkBoxPanel");
		cbPositiveAxis.setStyleName("checkBoxPanel");
		distancePanel.setStyleName("listBoxPanel");
		showTicksPanel.setStyleName("listBoxPanel");
		labelPanel.setStyleName("listBoxPanel");
		unitPanel.setStyleName("listBoxPanel");
		tfCross.setStyleName("numberInput");
		cbAllowSelection.setStyleName("checkBoxPanel");

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
		updatePanel();
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
		cbAxisNumber.setValue(view.getShowAxesNumbers()[axis]);

		cbManualTicks.setValue(!view.isAutomaticAxesNumberingDistance()[axis]);
		ncbTickDist.setSelectedId(model.getAxisDistance());
		ncbTickDist.setEnabled(cbManualTicks.getValue());

		comboAxisLabel.setSelectedId(view.getAxesLabels(true)[axis]);
		comboUnitLabel.setSelectedId(view.getAxesUnitLabels()[axis]);

		int type = view.getAxesTickStyles()[axis];
		lbTickStyle.setSelectedIndex(type);

		// cbShowAxis.setSelected(axis == 0 ? view.getShowXaxis() :
		// view.getShowYaxis());
		setShowAxis(view.getShowAxis(axis));

		if (view.getDrawBorderAxes()[axis]) {
			tfCross.setText("");
		} else {
			tfCross.setText("" + view.getAxesCross()[axis]);
		}

		tfCross.setVisible(!view.getDrawBorderAxes()[axis]);
		cbPositiveAxis.setValue(view.getPositiveAxes()[axis]);

		cbDrawAtBorder.setValue(view.getDrawBorderAxes()[axis]);
		cbAllowSelection.setValue(getModel().isSelectionAllowed());

	}

	/**
	 * @param value
	 *            whether to show axis
	 */
	public void setShowAxis(boolean value) {
		cbShowAxis.setValue(value);
	}

	@Override
	public void setLabels() {
		String strAxisEn = model.getAxisName();
		// this.setBorder(LayoutUtil.titleBorder(loc.getMenu(strAxisEn)));
		// this.setBorder(LayoutUtil.titleBorder(null));
		cbShowAxis.setText(loc.getMenu("Show" + strAxisEn));
		cbAxisNumber.setText(loc.getMenu("ShowAxisNumbers"));
		cbManualTicks.setText(loc.getMenu("TickDistance") + ":");
		axisTicks.setText(loc.getMenu("AxisTicks") + ":");
		cbPositiveAxis.setText(loc.getMenu("PositiveDirectionOnly"));
		axisLabel.setText(loc.getMenu("AxisLabel") + ":");
		axisUnitLabel.setText(loc.getMenu("AxisUnitLabel") + ":");
		crossAt.setText(loc.getMenu("CrossAt") + ":");
		cbDrawAtBorder.setText(loc.getMenu("StickToEdge"));
		cbAllowSelection.setText(loc.getMenu("SelectionAllowed"));
	}

	//
	// public void updateFont() {
	// Font font = app.getPlainFont();
	//
	// setFont(font);
	//
	// cbShowAxis.setFont(font);
	// cbAxisNumber.setFont(font);
	// cbManualTicks.setFont(font);
	// axisTicks.setFont(font);
	// cbPositiveAxis.setFont(font);
	// axisLabel.setFont(font);
	// axisUnitLabel.setFont(font);
	// crossAt.setFont(font);
	// stickToEdge.setFont(font);
	//
	// ncbTickDist.setFont(font);
	//
	// lbTickStyle.setFont(font);
	// lbAxisLabel.setFont(font);
	// lbUnitLabel.setFont(font);
	//
	// tfCross.setFont(font);
	// }

	@Override
	public void addTickItem(String item) {
		lbTickStyle.addItem(item);
	}

	@Override
	public void addAxisLabelItem(String item) {
		comboAxisLabel.addItem(item == null ? "" : item);
	}

	@Override
	public void addUnitLabelItem(String item) {
		comboUnitLabel.addItem(item == null ? "" : item);
	}

	@Override
	public void setCrossText(String text) {
		tfCross.setText(text);
	}

	public AxisModel getModel() {
		return model;
	}
}

package org.geogebra.desktop.gui.dialog.options;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.model.AxisModel;
import org.geogebra.common.gui.dialog.options.model.AxisModel.IAxisModelListener;
import org.geogebra.common.util.StringUtil;
import org.geogebra.desktop.gui.NumberComboBox;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.gui.util.FullWidthLayout;
import org.geogebra.desktop.gui.util.LayoutUtil;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;

public class AxisPanel extends JPanel implements ActionListener, ItemListener,
		FocusListener, SetLabels, IAxisModelListener {

	private static final long serialVersionUID = 1L;

	private AxisModel model;

	protected JCheckBox cbShowAxis;
	protected JCheckBox cbAxisNumber;
	protected JCheckBox cbManualTicks;
	protected JCheckBox cbPositiveAxis;
	protected JCheckBox cbDrawAtBorder;
	protected JCheckBox cbAllowSelection;
	protected NumberComboBox ncbTickDist;
	protected JComboBox<String> cbTickStyle;
	protected JComboBox<String> cbAxisLabel;
	protected JComboBox<String> cbUnitLabel;
	protected JTextField tfCross;

	private final JLabel crossAt;

	private final JLabel axisTicks;

	private final JLabel axisLabel;

	private final JLabel axisUnitLabel;

	private final JLabel stickToEdge;

	private final AppD app;
	protected EuclidianView view;

	private final LocalizationD loc;

	/**
	 * @param app application
	 * @param view graphics view
	 * @param axis axis index
	 */
	public AxisPanel(AppD app, EuclidianView view, int axis) {

		this.app = app;
		this.loc = app.getLocalization();
		this.view = view;
		model = new AxisModel(app, view, axis, this);

		setLayout(new FullWidthLayout());

		String strAxisEn = model.getAxisName();
		this.setBorder(LayoutUtil.titleBorder(loc.getMenu(strAxisEn)));

		// show axis
		cbShowAxis = new JCheckBox(loc.getMenu("Show" + strAxisEn));
		cbShowAxis.addActionListener(this);

		// show numbers
		cbAxisNumber = new JCheckBox(loc.getMenu("ShowAxisNumbers"));
		cbAxisNumber.addActionListener(this);

		// show positive axis only
		cbPositiveAxis = new JCheckBox(loc.getMenu("PositiveDirectionOnly"));
		cbPositiveAxis.addActionListener(this);

		// allow selection
		cbAllowSelection = new JCheckBox(loc.getMenu("SelectionAllowed"));
		cbAllowSelection.addActionListener(this);

		// ticks
		axisTicks = new JLabel(loc.getMenu("AxisTicks") + ":");
		cbTickStyle = new JComboBox<>();

		model.getTickOptions().forEach(cbTickStyle::addItem);

		cbTickStyle.addActionListener(this);
		cbTickStyle.setEditable(false);

		// distance
		cbManualTicks = new JCheckBox(loc.getMenu("TickDistance") + ":");
		cbManualTicks.addActionListener(this);
		ncbTickDist = new NumberComboBox(app);
		ncbTickDist.addItemListener(this);

		// axis and unit label
		cbAxisLabel = new JComboBox<>();
		model.getAxisLabelOptions().forEach(cbAxisLabel::addItem);
		cbAxisLabel.addActionListener(this);
		cbAxisLabel.setEditable(true);
		axisLabel = new JLabel(loc.getMenu("AxisLabel") + ":");

		axisUnitLabel = new JLabel(loc.getMenu("AxisUnitLabel") + ":");
		cbUnitLabel = new JComboBox<>();
		cbUnitLabel.setEditable(true);
		model.getUnitLabelOptions().forEach(cbUnitLabel::addItem);
		cbUnitLabel.addActionListener(this);

		// cross at and stick to edge
		tfCross = new MyTextFieldD(app, 6);
		tfCross.addActionListener(this);
		crossAt = new JLabel(loc.getMenu("CrossAt") + ":");
		cbDrawAtBorder = new JCheckBox();
		cbDrawAtBorder.addActionListener(this);
		stickToEdge = new JLabel(loc.getMenu("StickToEdge"));

		// add all panels
		JPanel showAxisPanel = LayoutUtil.flowPanel(cbShowAxis);
		add(showAxisPanel);
		JPanel numberPanel = LayoutUtil.flowPanel(cbAxisNumber);
		add(numberPanel);
		JPanel showPosPanel = LayoutUtil.flowPanel(cbPositiveAxis);
		add(showPosPanel);
		JPanel distancePanel = LayoutUtil.flowPanel(cbManualTicks, ncbTickDist);
		add(distancePanel);
		JPanel showTicksPanel = LayoutUtil.flowPanel(axisTicks, cbTickStyle);
		add(showTicksPanel);
		JPanel labelPanel = LayoutUtil.flowPanel(axisLabel, cbAxisLabel,
				axisUnitLabel, cbUnitLabel);
		add(labelPanel);
		JPanel crossPanel = LayoutUtil.flowPanel(crossAt, tfCross,
				cbDrawAtBorder, stickToEdge);
		addCrossPanel(crossPanel);
		JPanel allowSelectionPanel = LayoutUtil.flowPanel(cbAllowSelection);
		add(allowSelectionPanel);

		updatePanel();
	}

	/**
	 * @param view graphics view
	 */
	public void updateView(EuclidianView view) {
		this.view = view;
		model.setView(view);
	}

	protected void addCrossPanel(JPanel crossPanel) {
		add(crossPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		doActionPerformed(e.getSource());
	}

	private void doActionPerformed(Object source) {

		if (source == cbShowAxis) {
			model.showAxis(cbShowAxis.isSelected());
		}

		else if (source == cbAxisNumber) {
			model.showAxisNumbers(cbAxisNumber.isSelected());
		}

		else if (source == cbManualTicks) {
			model.applyTickDistance(cbManualTicks.isSelected());
		}

		else if (source == cbUnitLabel) {

			Object ob = cbUnitLabel.getSelectedItem();
			String text = (ob == null) ? null : ob.toString().trim();
			model.applyUnitLabel(text);
		}

		else if (source == cbAxisLabel) {
			Object ob = cbAxisLabel.getSelectedItem();
			String text = (ob == null) ? null : ob.toString().trim();
			model.applyAxisLabel(text);
		}

		else if (source == cbTickStyle) {
			int type = cbTickStyle.getSelectedIndex();
			model.applyTickStyle(type);
		}

		else if (source == tfCross) {
			model.applyCrossing(tfCross.getText());
		}

		else if (source == cbPositiveAxis) {
			model.applyPositiveAxis(cbPositiveAxis.isSelected());
		}

		else if (source == cbDrawAtBorder) {
			model.applyDrawAtBorder(cbDrawAtBorder.isSelected());
		}

		else if (source == cbAllowSelection) {
			model.applyAllowSelection(cbAllowSelection.isSelected());
		}

		updatePanel();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {

		if (e.getStateChange() != ItemEvent.SELECTED) {
			return;
		}
		Object source = e.getSource();
		if (source == ncbTickDist) {
			model.applyTickDistance(ncbTickDist.getValue());
		}

		view.updateBackground();
		updatePanel();
	}

	/**
	 * Update the UI
	 */
	public void updatePanel() {
		int axis = model.getAxis();
		cbAxisNumber.removeActionListener(this);
		cbAxisNumber.setSelected(view.getShowAxesNumbers()[axis]);
		cbAxisNumber.addActionListener(this);

		cbManualTicks.removeActionListener(this);
		ncbTickDist.removeItemListener(this);

		cbManualTicks
				.setSelected(!view.isAutomaticAxesNumberingDistance()[axis]);
		ncbTickDist.setSelectedItem(model.getAxisDistance());
		ncbTickDist.setEnabled(cbManualTicks.isSelected());

		cbManualTicks.addActionListener(this);
		ncbTickDist.addItemListener(this);

		cbAxisLabel.removeActionListener(this);
		cbAxisLabel.setSelectedItem(view.getAxesLabels(true)[axis]);
		cbAxisLabel.addActionListener(this);

		cbUnitLabel.removeActionListener(this);
		cbUnitLabel.setSelectedItem(view.getAxesUnitLabels()[axis]);
		cbUnitLabel.addActionListener(this);

		/*
		 * cbShowAxis.removeActionListener(this);
		 * cbShowAxis.setSelected(view.getShowXaxis());
		 * cbShowAxis.addActionListener(this);
		 */

		cbTickStyle.removeActionListener(this);
		int type = view.getAxesTickStyles()[axis];
		cbTickStyle.setSelectedIndex(type);
		cbTickStyle.addActionListener(this);

		cbShowAxis.removeActionListener(this);
		// cbShowAxis.setSelected(axis == 0 ? view.getShowXaxis() :
		// view.getShowYaxis());
		cbShowAxis.setSelected(view.getShowAxis(axis));
		cbShowAxis.addActionListener(this);

		tfCross.removeActionListener(this);
		if (view.getDrawBorderAxes()[axis]) {
			tfCross.setText("");
		} else {
			tfCross.setText("" + view.getAxesCross()[axis]);
		}
		tfCross.setEnabled(!view.getDrawBorderAxes()[axis]);
		tfCross.addActionListener(this);
		tfCross.addFocusListener(this);

		cbPositiveAxis.removeActionListener(this);
		cbPositiveAxis.setSelected(view.getPositiveAxes()[axis]);
		cbPositiveAxis.addActionListener(this);

		cbDrawAtBorder.removeActionListener(this);
		cbDrawAtBorder.setSelected(view.getDrawBorderAxes()[axis]);
		cbDrawAtBorder.addActionListener(this);

		cbAllowSelection.removeActionListener(this);
		cbAllowSelection.setSelected(getModel().isSelectionAllowed());
		cbAllowSelection.addActionListener(this);

	}

	@Override
	public void focusGained(FocusEvent e) {
		// do nothing
	}

	@Override
	public void focusLost(FocusEvent e) {
		// (needed for textfields)
		doActionPerformed(e.getSource());
	}

	@Override
	public void setLabels() {
		String strAxisEn = model.getAxisName();
		this.setBorder(LayoutUtil.titleBorder(loc.getMenu(strAxisEn)));
		this.setBorder(LayoutUtil.titleBorder(null));
		cbShowAxis.setText(loc.getMenu("Show" + strAxisEn));
		cbAxisNumber.setText(loc.getMenu("ShowAxisNumbers"));
		cbManualTicks.setText(loc.getMenu("TickDistance") + ":");
		axisTicks.setText(loc.getMenu("AxisTicks") + ":");
		cbPositiveAxis.setText(loc.getMenu("PositiveDirectionOnly"));
		axisLabel.setText(loc.getMenu("AxisLabel") + ":");
		axisUnitLabel.setText(loc.getMenu("AxisUnitLabel") + ":");
		crossAt.setText(loc.getMenu("CrossAt") + ":");
		stickToEdge.setText(loc.getMenu("StickToEdge"));
		cbAllowSelection.setText(loc.getMenu("SelectionAllowed"));
	}

	protected double parseDouble(String text) {
		if (StringUtil.empty(text)) {
			return Double.NaN;
		}
		return app.getKernel().getAlgebraProcessor().evaluateToDouble(text);
	}

	protected String getString() {
		return model.getAxisName();
	}

	/**
	 * Update font
	 */
	public void updateFont() {
		Font font = app.getPlainFont();

		setFont(font);

		cbShowAxis.setFont(font);
		cbAxisNumber.setFont(font);
		cbManualTicks.setFont(font);
		axisTicks.setFont(font);
		cbPositiveAxis.setFont(font);
		axisLabel.setFont(font);
		axisUnitLabel.setFont(font);
		crossAt.setFont(font);
		stickToEdge.setFont(font);

		ncbTickDist.setFont(font);

		cbTickStyle.setFont(font);
		cbAxisLabel.setFont(font);
		cbUnitLabel.setFont(font);

		tfCross.setFont(font);
	}

	@Override
	public void setCrossText(String text) {
		tfCross.setText(text);
	}

	public AxisModel getModel() {
		return model;
	}
}

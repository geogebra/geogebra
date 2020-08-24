package org.geogebra.desktop.gui.dialog;

import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.geogebra.common.gui.dialog.options.model.BooleanOptionModel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;

class CheckboxPanel extends OptionPanel
		implements BooleanOptionModel.IBooleanOptionListener, ItemListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final LocalizationD loc;
	private BooleanOptionModel model;
	private JCheckBox checkbox;
	private AppD app;
	private String title;
	private UpdateTabs tabs;

	public CheckboxPanel(AppD app, final String title, UpdateTabs tabs, BooleanOptionModel model) {
		this(app, title, tabs);
		this.model = model;
		model.setListener(this);
	}

	public CheckboxPanel(AppD app, final String title, UpdateTabs tabs) {
		super();
		this.app = app;
		loc = app.getLocalization();
		this.title = title;
		this.tabs = tabs;
		checkbox = new JCheckBox();
		checkbox.addItemListener(this);
		add(checkbox);
	}

	@Override
	public JPanel updatePanel(Object[] geos) {
		model.setGeos(geos);
		if (!model.checkGeos()) {
			return null;
		}

		checkbox.removeItemListener(this);

		model.updateProperties();
		// set object visible checkbox

		checkbox.addItemListener(this);
		return this;
	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFonts() {
		Font font = app.getPlainFont();
		checkbox.setFont(font);
	}

	@Override
	public void setLabels() {
		checkbox.setText(loc.getMenu(title));
		app.setComponentOrientation(this);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();

		if (source == checkbox) {
			apply(checkbox.isSelected());
		}
	}

	public void apply(boolean value) {
		model.applyChanges(value);
		tabs.updateTabs(model.getGeos());
	}

	@Override
	public void updateCheckbox(boolean value) {
		checkbox.setSelected(value);
	}

	public void setModel(BooleanOptionModel model) {
		this.model = model;
	}

	public JCheckBox getCheckbox() {
		return checkbox;
	}
}

/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.gui.dialog;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.UpdateFonts;
import org.geogebra.common.gui.dialog.options.model.CommonOptionsModel;
import org.geogebra.common.gui.dialog.options.model.GeoComboListener;
import org.geogebra.common.gui.dialog.options.model.MultipleOptionsModel;
import org.geogebra.common.gui.dialog.options.model.OptionsModel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.desktop.gui.properties.UpdateablePropertiesPanel;
import org.geogebra.desktop.main.AppD;

class ComboPanel extends JPanel implements ActionListener,
		SetLabels, UpdateFonts, UpdateablePropertiesPanel,
		GeoComboListener {
	private static final long serialVersionUID = 1L;
	private final Localization loc;
	private JLabel label;
	protected JComboBox comboBox;
	private OptionsModel model;
	private String title;
	private AppD app;

	public ComboPanel(AppD app, final String title) {
		this.app = app;
		this.loc = app.getLocalization();
		this.title = title;
		label = new JLabel();
		comboBox = new JComboBox();

		setLayout(new FlowLayout(FlowLayout.LEFT));
		if (hasLabel()) {
			add(label);
		}

		add(comboBox);
	}

	public ComboPanel(MultipleOptionsModel model, AppD app) {
		this(app, model.getTitle());
		setModel(model);
		model.setListener(this);
	}

	@Override
	public void setLabels() {
		if (hasLabel()) {
			label.setText(getTitle() + ":");
		}

		rebuildItems();
	}

	/**
	 * Rebuild combo items.
	 */
	public void rebuildItems() {
		int selectedIndex = comboBox.getSelectedIndex();
		comboBox.removeActionListener(this);
		comboBox.removeAllItems();
		if (isCommonOptionsModel()) {
			((CommonOptionsModel) model).fillModes(loc);
		} else {
			getMultipleModel().fillModes(loc);
		}
		comboBox.setSelectedIndex(selectedIndex);

		if (selectedIndex < comboBox.getItemCount()) {
			comboBox.setSelectedIndex(selectedIndex);
		}
		comboBox.addActionListener(this);
	}

	@Override
	public JPanel updatePanel(Object[] geos) {
		model.setGeos(geos);
		if (!model.checkGeos()) {
			return null;
		}

		comboBox.removeActionListener(this);

		model.updateProperties();

		comboBox.addActionListener(this);
		return this;
	}

	@Override
	public void updateFonts() {
		Font font = app.getPlainFont();
		if (hasLabel()) {
			label.setFont(font);
		}
		comboBox.setFont(font);
	}

	private boolean hasLabel() {
		return !StringUtil.empty(title);
	}

	@Override
	public void setSelectedIndex(int index) {
		comboBox.setSelectedIndex(index);
	}

	@Override
	public void addItem(String item) {
		comboBox.addItem(item);
	}

	/**
	 * action listener implementation for label mode combobox
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == comboBox) {
			if (isCommonOptionsModel()) {
				((CommonOptionsModel) model).applyChanges(
						comboBox.getSelectedItem());
			} else {
				getMultipleModel().applyChanges(comboBox.getSelectedIndex());
			}
		}
	}

	protected boolean isCommonOptionsModel() {
		return model instanceof CommonOptionsModel;
	}

	public JLabel getLabel() {
		return label;
	}

	public MultipleOptionsModel getMultipleModel() {
		return (MultipleOptionsModel) model;
	}

	public void setModel(OptionsModel model) {
		this.model = model;
	}

	public String getTitle() {
		return loc.getMenu(title);
	}

	@Override
	public void setSelectedItem(String item) {
		comboBox.setSelectedItem(item);
	}

	@Override
	public void clearItems() {
		comboBox.removeAllItems();
	}

	@Override
	public void addItem(GeoElement geo) {
		if (geo != null) {
			addItem(geo.getLabel(StringTemplate.editTemplate));
		} else {
			addItem("");
		}
	}
}

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

import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.model.DynamicCaptionModel;
import org.geogebra.common.gui.dialog.options.model.EnableDynamicCaptionModel;
import org.geogebra.common.gui.dialog.options.model.IComboListener;
import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;
import org.geogebra.desktop.gui.util.SpringUtilities;
import org.geogebra.desktop.main.AppD;

public class DynamicCaptionPanelD extends OptionPanel implements SetLabels, IComboListener {
	private final EnableDynamicCaptionPanel enableDynamicCaption;
	private final ComboPanel captions;
	private final SpringLayout springLayout;

	/**
	 * @param app app
	 * @param textField caption input
	 * @param tabs properties view
	 */
	public DynamicCaptionPanelD(AppD app, AutoCompleteTextFieldD textField, UpdateTabs tabs) {
		super();
		captions = new ComboPanel(app, "");
		enableDynamicCaption = new EnableDynamicCaptionPanel(app, textField,
				captions, tabs, new EnableDynamicCaptionModel(null, app));
		DynamicCaptionModel dynamicCaptionModel = new DynamicCaptionModel(app);
		captions.setModel(dynamicCaptionModel);
		dynamicCaptionModel.setListener(this);
		add(enableDynamicCaption);
		add(captions);
		setLayout(this.springLayout = new SpringLayout());
	}

	@Override
	public void setLabels() {
		enableDynamicCaption.setLabels();
		captions.setLabels();
	}

	@Override
	public void updateFonts() {
		enableDynamicCaption.updateFonts();
		captions.updateFonts();
	}

	@Override
	public void setSelectedIndex(int index) {
		captions.setSelectedIndex(index);
	}

	@Override
	public JPanel updatePanel(Object[] geos) {
		JPanel checkboxOrNull = enableDynamicCaption.updatePanel(geos);
		captions.rebuildItems();
		captions.updatePanel(geos);
		updateLayout();
		return checkboxOrNull == null ? null : this;
	}

	private void updateLayout() {
		int rows = enableDynamicCaption.isSelected() ? 2 : 1;
		SpringUtilities.makeCompactGrid(this, springLayout, rows, 1,
				0, 0, 5, 5);
		validate();
	}

	@Override
	public void addItem(String plainText) {
		captions.addItem(plainText);
	}

	@Override
	public void clearItems() {
		captions.clearItems();
	}
}

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

package org.geogebra.web.full.euclidian;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.properties.impl.facade.FlagListPropertyListFacade;
import org.geogebra.web.full.javax.swing.GCheckMarkLabel;
import org.geogebra.web.full.javax.swing.GCheckMarkPanel;
import org.gwtproject.user.client.Command;
import org.gwtproject.user.client.ui.FlowPanel;

public class LabelSettingsPanel extends FlowPanel implements SetLabels {

	private final FlagListPropertyListFacade<?> labelStyleProperty;
	private final List<GCheckMarkLabel> checkmarks = new ArrayList<>();

	/**
	 * Constructor
	 * @param labelStyleProperty - name property
	 */
	public LabelSettingsPanel(FlagListPropertyListFacade<?> labelStyleProperty) {
		super();
		this.labelStyleProperty = labelStyleProperty;
		createDialog();
	}

	private void createDialog() {
		Command nameValueCmd = this::applyCheckboxes;
		for (String label: labelStyleProperty.getFlagNames()) {
			checkmarks.add(new GCheckMarkLabel(label, true, nameValueCmd));
		}
		checkmarks.forEach(this::add);
		updateUI();
	}

	/**
	 * Submit the change
	 */
	protected void onEnter() {
		applyCheckboxes();
	}

	@Override
	public void setLabels() {
		List<String> flagNames = labelStyleProperty.getFlagNames();
		for (int i = 0; i < checkmarks.size(); i++) {
			checkmarks.get(i).setText(flagNames.get(i));
		}
	}

	/**
	 * Apply settings to selected geo(s).
	 */
	void applyCheckboxes() {
		List<Boolean> values = checkmarks.stream().map(GCheckMarkPanel::isChecked).collect(
				Collectors.toList());
		labelStyleProperty.setValue(values);
		updateUI();
	}

	private void updateUI() {
		List<Boolean> labelStyle = labelStyleProperty.getValue();
		for (int i = 0; i < labelStyle.size(); i++) {
			checkmarks.get(i).setChecked(labelStyle.get(i));
		}
	}

}

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

package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.gui.dialog.options.model.BooleanOptionModel;
import org.geogebra.common.gui.dialog.options.model.BooleanOptionModel.IBooleanOptionListener;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.properties.OptionPanel;
import org.gwtproject.user.client.ui.FlowPanel;

public class CheckboxPanel extends OptionPanel implements
		IBooleanOptionListener {
	private final ComponentCheckbox checkbox;

	/**
	 * @param loc - localization
	 * @param m - model
	 */
	public CheckboxPanel(Localization loc,
			BooleanOptionModel m) {
		this(m.getTitle(), loc);
		setModel(m);
		m.setListener(this);
	}

	/**
	 * @param title
	 *            title
	 * @param loc
	 *            localization
	 */
	public CheckboxPanel(final String title, Localization loc) {
		FlowPanel holderPanel = new FlowPanel();
		holderPanel.addStyleName("checkboxHolder");
		checkbox = new ComponentCheckbox(loc, false, title, this::onClick);
		holderPanel.add(checkbox);
		setWidget(holderPanel);
	}

	@Override
	public void updateCheckbox(boolean value) {
		getCheckbox().setSelected(value);
	}

	@Override
	public void setLabels() {
		getCheckbox().setLabels();
	}

	public ComponentCheckbox getCheckbox() {
		return checkbox;
	}

	private void onClick(boolean checked) {
		((BooleanOptionModel) getModel()).applyChanges(checked);
		onChecked();
	}

	/** Override this to do stuff after setting checkbox */
	public void onChecked() {
		// Override and put your code here.
	}
}

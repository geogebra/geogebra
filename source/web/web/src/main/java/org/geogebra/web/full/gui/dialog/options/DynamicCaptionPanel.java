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

import org.geogebra.common.gui.dialog.options.model.DynamicCaptionModel;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.properties.ListBoxPanel;
import org.geogebra.web.full.gui.properties.OptionPanel;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.gwtproject.user.client.ui.FlowPanel;

/**
 * Panel for setting text objects as captions.
 *
 * @author Laszlo
 */
public class DynamicCaptionPanel extends OptionPanel {
	private final CheckboxPanel enableDynamicCaption;
	private final ListBoxPanel captions;

	/**
	 *
	 * @param app the application
	 * @param captionField needs to be disabled/re-enabled
	 *            as dynamic caption is enabled/disabled.
	 */
	public DynamicCaptionPanel(App app, AutoCompleteTextFieldW captionField) {
		enableDynamicCaption = new EnableDynamicCaptionPanel(app, captionField);
		captions = new ListBoxPanel(app.getLocalization(), "");
		DynamicCaptionModel dynamicCaptionModel = new DynamicCaptionModel(app);
		captions.setModel(dynamicCaptionModel);
		dynamicCaptionModel.setListener(captions);
		FlowPanel main = new FlowPanel();
		main.add(enableDynamicCaption.getWidget());
		main.add(captions.getWidget());
		setWidget(main);
		captions.getWidget().setStyleName("listBoxPanel-noLabel");
	}

	@Override
	public void setLabels() {
		enableDynamicCaption.setLabels();
		captions.setLabels();
	}

	@Override
	public OptionPanel updatePanel(Object[] geos) {
		enableDynamicCaption.updatePanel(geos);
		captions.updatePanel(geos);
		return this;
	}
}

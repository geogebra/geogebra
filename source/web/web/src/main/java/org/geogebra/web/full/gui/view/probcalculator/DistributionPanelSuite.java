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

package org.geogebra.web.full.gui.view.probcalculator;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.properties.PropertyView;
import org.geogebra.common.properties.PropertyViewFactory;
import org.geogebra.web.full.gui.properties.ui.PropertiesPanelAdapter;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

public class DistributionPanelSuite extends FlowPanel {
	private final ProbabilityCalculatorViewW view;
	private final AppW appW;
	private final List<PropertyView> props = new ArrayList<>();

	/**
	 * Builds distribution panel for suite.
	 * @param view probability calculator view
	 * @param appW {@link AppW}
	 */
	public DistributionPanelSuite(ProbabilityCalculatorViewW view, AppW appW) {
		this.view = view;
		this.appW = appW;
		addStyleName("distrPanel");
		addStyleName("suiteDistrTab");
		buildGUI();
		addAttachHandler(evt -> {
			if (!evt.isAttached()) {
				detachProperties();
			} else if (props.isEmpty()) {
				clear();
				buildGUI();
			}
		});
	}

	private void buildGUI() {
		List<PropertyView> propertyViewList = PropertyViewFactory
				.propertyViewOfDistributionSettings(appW.getLocalization(),
						appW.getKernel().getAlgebraProcessor(),
						view, appW.appScope.propertiesRegistry);
		PropertiesPanelAdapter adapter = new PropertiesPanelAdapter(
				appW.getLocalization(), appW);
		for (PropertyView propertyView : propertyViewList) {
			Widget widget = adapter.getWidget(propertyView);
			add(widget);
			props.add(propertyView);
		}
		adapter.addAccessibility(AccessibilityGroup.PROBABILITY);
	}

	/**
	 * Detach all owned properties.
	 */
	public void detachProperties() {
		props.forEach(PropertyView::detach);
		props.clear();
	}
}

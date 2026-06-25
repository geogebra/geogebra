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

public class DistributionPanel extends FlowPanel {
	private final ProbabilityCalculatorViewW view;
	private final AppW appW;
	private final List<PropertyView> props = new ArrayList<>();

	/**
	 * Builds distribution panel for probability calculator.
	 * @param view probability calculator view
	 * @param appW {@link AppW}
	 */
	public DistributionPanel(ProbabilityCalculatorViewW view, AppW appW) {
		this.view = view;
		this.appW = appW;
		addStyleName("distributionPanel");
		buildGUI();
		addAttachHandler(evt -> {
			if (!evt.isAttached()) {
				detachProperties();
			} else if (props.isEmpty()) {
				rebuild();
			}
		});
	}

	private void buildGUI() {
		PropertiesPanelAdapter adapter = new PropertiesPanelAdapter(
				appW.getLocalization(), appW);
		if (appW.isUnbundled()) {
			List<PropertyView> propertyViewList = PropertyViewFactory
					.propertyViewOfDistributionSettings(appW.getLocalization(),
							appW.getKernel().getAlgebraProcessor(),
							view, appW.appScope.propertiesRegistry);
			fillPanelWithWidgets(propertyViewList, adapter);
		} else {
			List<PropertyView> distributionParametersProperties = PropertyViewFactory
					.propertyClassicDistributionParametersSettings(appW.getLocalization(),
							appW.getKernel().getAlgebraProcessor(),
							view, appW.appScope.propertiesRegistry);
			FlowPanel holder = new FlowPanel();
			holder.addStyleName("holder");
			for (PropertyView propertyView : distributionParametersProperties) {
				Widget widget = adapter.getWidget(propertyView);
				holder.add(widget);
				props.add(propertyView);
			}

			add(holder);
			List<PropertyView> distributionViewProperties = PropertyViewFactory
					.propertyClassicDistributionViewSettings(appW.getLocalization(),
							appW.getKernel().getAlgebraProcessor(),
							view, appW.appScope.propertiesRegistry);
			fillPanelWithWidgets(distributionViewProperties, adapter);
		}
		adapter.addAccessibility(AccessibilityGroup.PROBABILITY);
	}

	private void fillPanelWithWidgets(List<PropertyView> propertyViewList,
			PropertiesPanelAdapter adapter) {
		for (PropertyView propertyView : propertyViewList) {
			Widget widget = adapter.getWidget(propertyView);
			add(widget);
			props.add(propertyView);
		}
	}

	/**
	 * Detach all owned properties.
	 */
	public void detachProperties() {
		props.forEach(PropertyView::detach);
		props.clear();
	}

	/**
	 * Rebuild the whole panel.
	 */
	public void rebuild() {
		clear();
		buildGUI();
	}
}

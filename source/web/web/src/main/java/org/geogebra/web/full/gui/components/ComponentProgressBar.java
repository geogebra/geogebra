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

package org.geogebra.web.full.gui.components;

import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.gwtproject.user.client.ui.FlowPanel;

public class ComponentProgressBar extends FlowPanel {

	private FlowPanel indicatorPrimary;

	/**
	 * Default linear determinate progress bar
	 */
	public ComponentProgressBar() {
		this(false, true);
	}

	/**
	 * Progress bar UI element
	 * @param isDarkTheme - whether is dark theme
	 * @param isDeterminate - whether is determinate or not (indeterminate)
	 */
	public ComponentProgressBar(boolean isDarkTheme, boolean
			isDeterminate) {
		buildGui(isDeterminate);
		addStyleName("progressBar");
		if (isDarkTheme) {
			addStyleName("dark");
		}
	}

	private void buildGui(boolean isDeterminate) {
		FlowPanel track = BaseWidgetFactory.INSTANCE.newPanel("track");
		add(track);

		indicatorPrimary = BaseWidgetFactory.INSTANCE.newPanel("indicator");
		add(indicatorPrimary);

		if (!isDeterminate) {
			indicatorPrimary.addStyleName("animBar1");
			FlowPanel indicatorSecondary = BaseWidgetFactory.INSTANCE
					.newPanel("indicator animBar2");
			add(indicatorSecondary);
		}
	}

	/**
	 * update indicator width
	 * @param percent - percentage
	 */
	public void setIndicatorWidth(double percent) {
		indicatorPrimary.setWidth(percent + "%");
	}

}

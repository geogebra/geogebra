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

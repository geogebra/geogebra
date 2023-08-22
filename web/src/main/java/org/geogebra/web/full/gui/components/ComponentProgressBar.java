package org.geogebra.web.full.gui.components;

import org.gwtproject.user.client.ui.FlowPanel;

public class ComponentProgressBar extends FlowPanel {

	private FlowPanel indicator;

	/**
	 * Progress bar UI element
	 * @param isDarkTheme - whether is dark theme
	 */
	public ComponentProgressBar(boolean isDarkTheme) {
		buildGui();
		addStyleName("progressBar");
		if (isDarkTheme) {
			addStyleName("dark");
		}
	}

	private void buildGui() {
		FlowPanel track = new FlowPanel();
		track.addStyleName("track");

		indicator = new FlowPanel();
		indicator.addStyleName("indicator");

		add(track);
		add(indicator);
	}

	/**
	 * update indicator width
	 * @param percent - percentage
	 */
	public void setIndicatorWidth(double percent) {
		indicator.setWidth(percent + "%");
	}

}

package org.geogebra.web.full.gui.components;

import org.gwtproject.user.client.ui.FlowPanel;

public class ComponentProgressBar extends FlowPanel {

	private FlowPanel indicator;

	public ComponentProgressBar() {
		buildGui();
		addStyleName("progressBar1");
	}

	public ComponentProgressBar(boolean isDarkTheme) {
		this();
		addStyleName("dark");
	}

	private void buildGui() {
		FlowPanel track = new FlowPanel();
		track.addStyleName("track");

		indicator = new FlowPanel();
		indicator.addStyleName("indicator");

		add(track);
		add(indicator);
	}

	public void setIndicatorWidth(double percent) {
		indicator.setWidth(percent + "%");
	}

}

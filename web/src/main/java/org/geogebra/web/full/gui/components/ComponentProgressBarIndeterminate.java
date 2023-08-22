package org.geogebra.web.full.gui.components;

import org.gwtproject.user.client.ui.FlowPanel;

public class ComponentProgressBarIndeterminate extends FlowPanel {

	public ComponentProgressBarIndeterminate(boolean isDarkTheme) {
		buildGui();
		addStyleName("progressBarIndeterminate");
		if (isDarkTheme) {
			addStyleName("dark");
		}
	}

	private void buildGui() {
		FlowPanel track = new FlowPanel();
		track.addStyleName("track");

		FlowPanel indicatorBar1 = new FlowPanel();
		indicatorBar1.addStyleName("indicator animBar1");

		FlowPanel indicatorBar2 = new FlowPanel();
		indicatorBar2.addStyleName("indicator animBar2");

		add(track);
		add(indicatorBar1);
		add(indicatorBar2);
	}
}

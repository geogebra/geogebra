package org.geogebra.web.web.gui.toolbarpanel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class ToolbarPanel extends FlowPanel {

	private class Header extends FlowPanel {
		public Header() {
			addStyleName("avHeader");
		}
	}
	public ToolbarPanel() {
		initGUI();
	}

	private void initGUI() {
		clear();
		addStyleName("avStyle");
		add(new Header());
		add(new Label("AAAAAAAAAAAAAAAAAAAAA"));
	}

	public void open() {

	}

	public void close() {

	}
}

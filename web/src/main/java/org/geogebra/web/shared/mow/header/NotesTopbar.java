package org.geogebra.web.shared.mow.header;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.RootPanel;

public class NotesTopbar extends FlowPanel implements SetLabels {
	private final AppW appW;

	/**
	 * constructor
	 * @param appW - application
	 */
	public NotesTopbar(AppW appW) {
		this.appW = appW;
		addStyleName("topbar");
		buildGui();
	}

	private void buildGui() {
		// to fill later
	}

	@Override
	public void setLabels() {
		// for later
	}
}

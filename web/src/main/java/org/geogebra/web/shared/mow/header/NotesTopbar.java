package org.geogebra.web.shared.mow.header;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.RootPanel;

import com.google.gwt.user.client.ui.FlowPanel;

public class NotesTopbar extends FlowPanel implements SetLabels {
	private final AppW appW;

	public NotesTopbar(AppW appW) {
		this.appW = appW;
		addStyleName("topbar");
		RootPanel.get().add((IsWidget) this);
		buildGui();
	}

	private void buildGui() {

	}

	@Override
	public void setLabels() {
		// for later
	}
}

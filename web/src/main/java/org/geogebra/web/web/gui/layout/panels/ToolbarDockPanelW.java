package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.common.main.App;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.toolbarpanel.ToolbarPanel;

import com.google.gwt.user.client.ui.Widget;

public class ToolbarDockPanelW extends DockPanelW {

	public ToolbarDockPanelW(App app1) {
		super(
				App.VIEW_ALGEBRA, // view id
				"ToolbarWindow", 			// view title phrase
				null,						// toolbar string
				false,						// style bar?
				2, 							// menu order
				'A'							// menu shortcut
			);
	}

	@Override
	protected Widget loadComponent() {
		return new ToolbarPanel(app);
	}


}

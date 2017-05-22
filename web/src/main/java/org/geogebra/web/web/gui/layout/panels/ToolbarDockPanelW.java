package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.common.main.App;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.toolbarpanel.ToolbarPanel;

import com.google.gwt.user.client.ui.Widget;

public class ToolbarDockPanelW extends DockPanelW {

	private static final int CLOSING_WIDTH = 86;
	private ToolbarPanel toolbar;

	public ToolbarDockPanelW() {
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
		toolbar = new ToolbarPanel(app);
		return toolbar;
	}


	@Override
	public void onResize() {
		if (toolbar.getOffsetWidth() < CLOSING_WIDTH) {
			toolbar.close();
		} else {
			toolbar.open();
		}
		toolbar.resize();
	}

	public ToolbarPanel getToolbar() {
		return toolbar;
	}

}

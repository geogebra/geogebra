package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.main.AppWFull;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.ui.Widget;

/**
 * @author gabor
 * 
 * FunctionInspector dockpanel for Web
 *
 */
public class FunctionInspectorDockPanelW extends DockPanelW {
	
	/**
	 * default width of this panel
	 */
	public static final int DEFAULT_WIDTH = 480;

	/**
	 * @param app App
	 * Creates panel
	 */
	public FunctionInspectorDockPanelW(AppWFull app) {
		super(App.VIEW_FUNCTION_INSPECTOR, // view id
				null, // toolbar string
				true); // style bar?
		this.app = app;
		this.setEmbeddedSize(DEFAULT_WIDTH);
	}

	@Override
	protected Widget loadComponent() {
		return app.getDialogManager().getFunctionInspector().getWrappedPanel();
	}

	@Override
	protected Widget loadStyleBar() {
		return null;
	}
	
	@Override
	public boolean isStyleBarEmpty() {
		return true;
	}

	@Override
	protected ResourcePrototype getViewIcon() {
		return null;
	}

}

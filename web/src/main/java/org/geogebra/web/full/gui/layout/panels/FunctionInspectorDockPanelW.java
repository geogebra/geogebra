package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.resources.client.ResourcePrototype;

import com.google.gwt.user.client.ui.Widget;

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
	public FunctionInspectorDockPanelW(AppW app) {
		super(App.VIEW_FUNCTION_INSPECTOR, // view id
				null, // toolbar string
				true); // style bar?
		this.app = app;
		this.setEmbeddedSize(DEFAULT_WIDTH);
	}

	@Override
	protected Widget loadComponent() {
		return ((DialogManagerW) app.getDialogManager()).getFunctionInspector().getWrappedPanel();
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

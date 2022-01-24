package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.properties.PropertiesViewW;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.resources.client.ResourcePrototype;

import com.google.gwt.user.client.ui.Widget;

public class PropertiesDockPanelW extends DockPanelW {

	private PropertiesViewW view;
	
	/**
	 * @param app
	 *            application
	 */
	public PropertiesDockPanelW(AppW app) {
		super(
			App.VIEW_PROPERTIES, 	// view id
			null,	// toolbar string
			false // style bar?
		);
		
		this.app = app;
		super.setDialog(true);
		this.setShowStyleBar(true);
	}

	private void getPropertiesView() {
		view = (PropertiesViewW) app.getGuiManager().getPropertiesView();
	}

	@Override
	protected Widget loadComponent() {
		getPropertiesView();
		return view.getWrappedPanel();
	}

	@Override
	public void onResize() {
		// this hack may be temporary
		if (view != null) {
			view.onResize();
			view.repaintView();
		}
	}
	
	@Override
	public boolean isStyleBarEmpty() {
		return false;
	}

	@Override
	protected ResourcePrototype getViewIcon() {
		return null;
	}

}

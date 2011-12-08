package geogebra.web;

import geogebra.web.mvp4g.Mvp4gModule;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;



/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Web implements EntryPoint {

	public void onModuleLoad() {
		//just mimic not real :-)
		Mvp4gModule module = (Mvp4gModule) GWT.create(Mvp4gModule.class);
		module.createAndStartModule();
		//tempRootLayoutPanel.get().add((Widget) module.getStartView());
	}
}

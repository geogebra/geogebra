package geogebra.web;


import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.main.AbstractApplication;
import geogebra.web.main.Application;
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
	public void t(String s,AlgebraProcessor ap) throws Exception{
		ap.processAlgebraCommandNoExceptionHandling(s, false,false, true);
	}

	public void onModuleLoad() {
		//just mimic not real :-)
		Mvp4gModule module = (Mvp4gModule) GWT.create(Mvp4gModule.class);
		module.createAndStartModule();
		//tempRootLayoutPanel.get().add((Widget) module.getStartView());
		Application app = new Application();
		AlgebraProcessor ap = app.getKernel().getAlgebraProcessor();
		app.updateCommandDictionary();
		try{
		t("A=(1,1)",ap);
		t("B=(3,3)",ap);
		t("C=Midpoint[A,B]",ap);
		t("a=Line[A,B]",ap);
		t("c=Circle[A,B]",ap);
		t("d=Curve[t^2,t^3,t,0,1]",ap);
		AbstractApplication.debug(app.getKernel().lookupLabel("C"));
		AbstractApplication.debug(app.getKernel().lookupLabel("a"));
		AbstractApplication.debug(app.getKernel().lookupLabel("c"));
		AbstractApplication.debug(app.getKernel().lookupLabel("d"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

package org.geogebra.web.solver;

import org.geogebra.common.factories.CASFactoryDummy;
import org.geogebra.web.editor.AppWsolver;
import org.geogebra.web.html5.gui.GeoGebraFrameSimple;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.geogebra.web.html5.util.SuperDevUncaughtExceptionHandler;
import org.geogebra.web.html5.util.debug.LoggerW;
import org.geogebra.web.resources.StyleInjector;
import org.geogebra.web.simple.Stub3DFragment;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.web.FactoryProviderGWT;

public class StepsEntry implements EntryPoint {

	@Override
	public void onModuleLoad() {
		SuperDevUncaughtExceptionHandler.register();

		GeoGebraElement geoGebraElement = GeoGebraElement.as(DOM.getElementById("ggw"));
		AppletParameters parameters = new AppletParameters(geoGebraElement);

		parameters.setAttribute("marginTop", "64");

		new StyleInjector(GWT.getModuleBaseURL())
				.inject("css", "solver")
				.inject("css", "shared")
				.inject("css", "step-tree")
				.inject("css", "dialog-styles")
				.inject("css", "keyboard-styles");

		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderGWT());
		}

		GeoGebraFrameSimple geogebraFrame =
				new GeoGebraFrameSimple(geoGebraElement, parameters, new CASFactoryDummy());

		AppWsolver app = new AppWsolver(geoGebraElement, parameters, geogebraFrame);
		LoggerW.startLogger(app.getAppletParameters());

		RootPanel.get(geoGebraElement.getId()).add(geogebraFrame);

		AbsolutePanel solverRoot = new AbsolutePanel();
		new Solver(app, solverRoot).setupApplication();

		geogebraFrame.add(solverRoot);
		Stub3DFragment.load();
	}
}

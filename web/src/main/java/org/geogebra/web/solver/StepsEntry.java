package org.geogebra.web.solver;

import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.editor.AppWsolver;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.GeoGebraFrameSimple;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.geogebra.web.html5.util.SuperDevUncaughtExceptionHandler;
import org.geogebra.web.html5.util.debug.LoggerW;
import org.geogebra.web.resources.StyleInjector;
import org.geogebra.web.shared.SharedResources;
import org.geogebra.web.simple.Stub3DFragment;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.web.FactoryProviderGWT;

public class StepsEntry implements EntryPoint {

	private AppWsolver app;
	private AbsolutePanel solverRoot;
	private AbsolutePanel practiceRoot;

	private GeoGebraFrameSimple geogebraFrame;

	@Override
	public void onModuleLoad() {
		SuperDevUncaughtExceptionHandler.register();

		GeoGebraElement geoGebraElement = GeoGebraElement.as(DOM.getElementById("ggw"));
		AppletParameters parameters = new AppletParameters(geoGebraElement);

		parameters.setAttribute("marginTop", "64");

		StyleInjector.inject(SharedResources.INSTANCE.solverStyleScss());
		StyleInjector.inject(GuiResourcesSimple.INSTANCE.sharedStyleScss());
		StyleInjector.inject(SharedResources.INSTANCE.stepTreeStyleScss());
		StyleInjector.inject(SharedResources.INSTANCE.dialogStylesScss());
		StyleInjector.inject(KeyboardResources.INSTANCE.keyboardStyle());

		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderGWT());
		}

		geogebraFrame = new GeoGebraFrameSimple(geoGebraElement, parameters);

		app = new AppWsolver(geoGebraElement, parameters, geogebraFrame);
		LoggerW.startLogger(app.getAppletParameters());

		String type = geoGebraElement.getAttribute("data-param-appType");
		RootPanel.get(geoGebraElement.getId()).add(geogebraFrame);

		switchMode(type);
		Stub3DFragment.load();
	}

	/**
	 * @param mode
	 *            "solver" or "practice"
	 */
	public void switchMode(String mode) {
		switch (mode) {
		case "solver":
			if (practiceRoot != null) {
				practiceRoot.setVisible(false);
			}

			if (solverRoot == null) {
				solverRoot = new AbsolutePanel();
				new Solver(app, solverRoot).setupApplication();

				geogebraFrame.add(solverRoot);
			} else {
				solverRoot.setVisible(true);
			}

			break;

		case "practice":
			if (solverRoot != null) {
				solverRoot.setVisible(false);
			}

			if (practiceRoot == null) {
				practiceRoot = new AbsolutePanel();
				new Exercise(app, practiceRoot).setupApplication();

				geogebraFrame.add(practiceRoot);
			} else {
				practiceRoot.setVisible(true);
			}

			break;

		default:
			// shouldn't happen
		}
	}
}

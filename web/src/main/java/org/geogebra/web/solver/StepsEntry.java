package org.geogebra.web.solver;

import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.editor.AppWsolver;
import org.geogebra.web.html5.WebSimple;
import org.geogebra.web.html5.gui.GeoGebraFrameSimple;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.debug.LoggerW;
import org.geogebra.web.resources.JavaScriptInjector;
import org.geogebra.web.resources.StyleInjector;
import org.geogebra.web.shared.SharedResources;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.web.FactoryProviderGWT;

public class StepsEntry implements EntryPoint {

	private AppWsolver app;
	private RootPanel rootPanel;
	private AbsolutePanel solverRoot;
	private AbsolutePanel practiceRoot;

	@Override
	public void onModuleLoad() {
		WebSimple.registerSuperdevExceptionHandler();

		ArticleElement articleElement = ArticleElement.getGeoGebraMobileTags().get(0);

		LoggerW.startLogger(articleElement);

		StyleInjector.inject(SharedResources.INSTANCE.solverStyleScss());
		StyleInjector.inject(SharedResources.INSTANCE.sharedStyleScss());
		StyleInjector.inject(SharedResources.INSTANCE.stepTreeStyleScss());
		StyleInjector.inject(SharedResources.INSTANCE.dialogStylesScss());
		StyleInjector.inject(KeyboardResources.INSTANCE.keyboardStyle());
		JavaScriptInjector.inject(KeyboardResources.INSTANCE.wavesScript());
		StyleInjector.inject(KeyboardResources.INSTANCE.wavesStyle());

		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderGWT());
		}

		GeoGebraFrameSimple fr = new GeoGebraFrameSimple(false);

		app = new AppWsolver(articleElement, fr);

		String type = articleElement.getAttribute("data-param-appType");

		rootPanel = RootPanel.get("ggbApplet");

		switchMode(type);
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

				rootPanel.add(solverRoot);
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

				rootPanel.add(practiceRoot);
			} else {
				practiceRoot.setVisible(true);
			}

			break;

		default:
			// shouldn't happen
		}
	}
}

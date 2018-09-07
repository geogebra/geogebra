package org.geogebra.web.solver;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.web.FactoryProviderGWT;
import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.editor.AppWsolver;
import org.geogebra.web.html5.WebSimple;
import org.geogebra.web.html5.gui.GeoGebraFrameSimple;
import org.geogebra.web.html5.main.TestArticleElement;
import org.geogebra.web.html5.util.debug.LoggerW;
import org.geogebra.web.resources.JavaScriptInjector;
import org.geogebra.web.resources.StyleInjector;
import org.geogebra.web.shared.SharedResources;

public class StepsEntry implements EntryPoint {

	@Override
	public void onModuleLoad() {
		WebSimple.registerSuperdevExceptionHandler();

		TestArticleElement articleElement = new TestArticleElement("true",
				"Solver");
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

		AppWsolver app = new AppWsolver(articleElement, fr);

		String type = getContainer().getAttribute("data-param-appType");

		String id = "appContainer" + DOM.createUniqueId();
		getContainer().setId(id);
		RootPanel rootPanel = RootPanel.get(id);

		if ("solver".equals(type)) {
			new Solver(app, rootPanel).setupApplication();
		} else {
			new Exercise(app, rootPanel).setupApplication();
		}
	}

	private native Element getContainer() /*-{
        return $wnd.getContainer();
    }-*/;
}

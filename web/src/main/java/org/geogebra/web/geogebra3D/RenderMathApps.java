package org.geogebra.web.geogebra3D;

import org.geogebra.gwtutil.JsConsumer;
import org.geogebra.web.full.gui.applet.AppletFactory;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.html5.bridge.AttributeProvider;
import org.geogebra.web.html5.bridge.RenderGgbElement.RenderGgbElementFunction;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.GeoGebraElement;

import elemental2.dom.DomGlobal;

public final class RenderMathApps implements RenderGgbElementFunction {


	private final GLookAndFeel laf;
	private final AppletFactory factory;

	public RenderMathApps(GLookAndFeel laf, AppletFactory factory) {
		this.laf = laf;
		this.factory = factory;
	}

	@Override
	public void render(Object options, JsConsumer<Object> callback) {
		DomGlobal.console.debug("Im up and rendering");
		AttributeProvider provider = AttributeProvider.as(options);
		GeoGebraElement element = GeoGebraElement.as(provider.getElement());
		AppletParameters parameters = new AppletParameters(provider);
		final GeoGebraFrameFull full = new GeoGebraFrameFull(factory, laf,
				null, element, parameters);
		DomGlobal.window.addEventListener("resize", evt -> onResize(full));
		full.renderArticleElementWithFrame(element, provider, callback);
		full.updateArticleHeight();
	}

	private void onResize(GeoGebraFrameFull full) {
		DomGlobal.console.debug("onResize");
		full.setSize(800, 600);
	}

}

package org.geogebra.web.geogebra3D;

import org.geogebra.gwtutil.JsConsumer;
import org.geogebra.web.full.gui.applet.AppletFactory;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.html5.bridge.AttributeProvider;
import org.geogebra.web.html5.bridge.RenderGgbElement.RenderGgbElementFunction;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.GeoGebraElement;

public final class RenderMathApps implements RenderGgbElementFunction {

	private final GLookAndFeel laf;
	private final AppletFactory factory;

	/**
	 * Constructor
	 * @param laf {@link GLookAndFeel}
	 * @param factory {@link AppletFactory}
	 */
	public RenderMathApps(GLookAndFeel laf, AppletFactory factory) {
		this.laf = laf;
		this.factory = factory;
	}

	@Override
	public void render(Object options, JsConsumer<Object> callback) {
		AttributeProvider provider = AttributeProvider.as(options);
		AppletParameters parameters = new AppletParameters(provider);
		GeoGebraElement element = GeoGebraElement.as(provider.getElement());
		final GeoGebraFrameFull full = new GeoGebraFrameFull(factory, laf,
				null, element, parameters);
		full.renderArticleElementWithFrame(element, provider, callback);
		full.updateArticleHeight();
	}
}

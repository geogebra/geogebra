/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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

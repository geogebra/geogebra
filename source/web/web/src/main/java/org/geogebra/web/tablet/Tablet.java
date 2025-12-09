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

package org.geogebra.web.tablet;

import org.geogebra.gwtutil.JsConsumer;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.geogebra3D.AppletFactory3D;
import org.geogebra.web.html5.bridge.AttributeProvider;
import org.geogebra.web.html5.bridge.RenderGgbElement;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.geogebra.web.html5.util.SuperDevUncaughtExceptionHandler;
import org.geogebra.web.tablet.main.TabletDevice;
import org.gwtproject.user.client.ui.RootPanel;

import com.google.gwt.core.client.EntryPoint;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Tablet implements EntryPoint {

	/**
	 * set true if Google Api Js loaded
	 */

	@Override
	public void onModuleLoad() {
		if (RootPanel.getBodyElement().getAttribute("data-param-laf") != null
				&& !"".equals(RootPanel.getBodyElement().getAttribute("data-param-laf"))) {
			// loading touch, ignore.
			return;
		}

		exportGGBElementRenderer();

		loadAppletAsync();

		SuperDevUncaughtExceptionHandler.register();
	}

	/**
	 * Load in applet mode
	 */
	public static void loadAppletAsync() {
		GeoGebraFrameFull.main(GeoGebraElement.getGeoGebraMobileTags(),
				new AppletFactory3D(),
				new TabletLookAndFeel(),
				new TabletDevice());
	}

	private void exportGGBElementRenderer() {
		RenderGgbElement.setRenderGGBElement(this::renderArticleElement);
		RenderGgbElement.renderGGBElementReady();
	}

	/**
	 * @param options
	 *            article element
	 * @param clb
	 *            rendering finished callback
	 */
	public void renderArticleElement(final Object options,
			JsConsumer<Object> clb) {
		GeoGebraFrameFull.renderArticleElement(AttributeProvider.as(options),
				new AppletFactory3D(), new TabletLookAndFeel(), clb);
	}

}

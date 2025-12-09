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

package org.geogebra.web.simple;

import java.util.ArrayList;

import org.geogebra.web.cas.giac.CASFactoryW;
import org.geogebra.web.html5.bridge.AttributeProvider;
import org.geogebra.web.html5.bridge.RenderGgbElement;
import org.geogebra.web.html5.gui.GeoGebraFrameSimple;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.geogebra.web.html5.util.SuperDevUncaughtExceptionHandler;

import com.google.gwt.core.client.EntryPoint;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WebSimple implements EntryPoint {

	/**
	 * set true if Google Api Js loaded
	 */
	@Override
	public void onModuleLoad() {
		exportGGBElementRenderer();

		// instead, load it immediately
		startGeoGebra(GeoGebraElement.getGeoGebraMobileTags());
		SuperDevUncaughtExceptionHandler.register();
		Stub3DFragment.load();
	}

	static void startGeoGebra(ArrayList<GeoGebraElement> geoGebraMobileTags) {
		GeoGebraFrameSimple.main(geoGebraMobileTags, new CASFactoryW());
	}

	private void exportGGBElementRenderer() {
		RenderGgbElement.setRenderGGBElement((el, callback) -> {
			GeoGebraFrameSimple.renderArticleElement(AttributeProvider.as(el), callback,
					new CASFactoryW());
		});
		RenderGgbElement.renderGGBElementReady();
	}

}

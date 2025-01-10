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

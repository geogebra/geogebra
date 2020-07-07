package org.geogebra.web.simple;

import java.util.ArrayList;

import org.geogebra.web.html5.gui.GeoGebraFrameSimple;
import org.geogebra.web.html5.util.ArticleElement;
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
		startGeoGebra(ArticleElement.getGeoGebraMobileTags());
		SuperDevUncaughtExceptionHandler.register();
		Stub3DFragment.load();
	}

	static void startGeoGebra(ArrayList<ArticleElement> geoGebraMobileTags) {
		GeoGebraFrameSimple.main(geoGebraMobileTags);
	}

	private native void exportGGBElementRenderer() /*-{
		$wnd.renderGGBElement = $entry(@org.geogebra.web.html5.gui.GeoGebraFrameSimple::renderArticleElement(Lcom/google/gwt/dom/client/Element;Lcom/google/gwt/core/client/JavaScriptObject;))
		@org.geogebra.web.html5.gui.GeoGebraFrameW::renderGGBElementReady()();
	}-*/;

}

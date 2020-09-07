package org.geogebra.web.tablet;

import java.util.ArrayList;

import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.geogebra3D.AppletFactory3D;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.geogebra.web.html5.util.SuperDevUncaughtExceptionHandler;
import org.geogebra.web.tablet.main.TabletDevice;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

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
		        && !"".equals(RootPanel.getBodyElement().getAttribute(
		                "data-param-laf"))) {
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
		startGeoGebra(GeoGebraElement.getGeoGebraMobileTags());
	}

	private native void exportGGBElementRenderer() /*-{
   		$wnd.renderGGBElement = $entry(@org.geogebra.web.tablet.Tablet::renderArticleElement(Lcom/google/gwt/dom/client/Element;Lcom/google/gwt/core/client/JavaScriptObject;))
   		@org.geogebra.web.html5.gui.GeoGebraFrameW::renderGGBElementReady()();
   	}-*/;

	/**
	 * @param el
	 *            article element
	 * @param clb
	 *            rendering finished callback
	 */
	public static void renderArticleElement(final Element el,
	        JavaScriptObject clb) {
		GeoGebraFrameFull.renderArticleElement(el,
				new AppletFactory3D(), new TabletLookAndFeel(), clb);
	}

	/**
	 * @param geoGebraMobileTags
	 *            article elements
	 */
	static void startGeoGebra(final ArrayList<GeoGebraElement> geoGebraMobileTags) {
		GeoGebraFrameFull.main(geoGebraMobileTags,
				new AppletFactory3D(),
				new TabletLookAndFeel(),
				new TabletDevice());
	}

}

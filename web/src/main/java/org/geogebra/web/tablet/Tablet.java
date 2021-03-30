package org.geogebra.web.tablet;

import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.geogebra3D.AppletFactory3D;
import org.geogebra.web.html5.GeoGebraGlobal;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
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
		GeoGebraFrameFull.main(GeoGebraElement.getGeoGebraMobileTags(),
				new AppletFactory3D(),
				new TabletLookAndFeel(),
				new TabletDevice());
	}

	private void exportGGBElementRenderer() {
		GeoGebraGlobal.setRenderGGBElement(this::renderArticleElement);
		GeoGebraFrameW.renderGGBElementReady();
	}

	/**
	 * @param el
	 *            article element
	 * @param clb
	 *            rendering finished callback
	 */
	public void renderArticleElement(final Element el,
	        JavaScriptObject clb) {
		GeoGebraFrameFull.renderArticleElement(el,
				new AppletFactory3D(), new TabletLookAndFeel(), clb);
	}

}

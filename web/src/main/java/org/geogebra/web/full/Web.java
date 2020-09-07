package org.geogebra.web.full;

import java.util.ArrayList;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.web.full.gui.applet.AppletFactory;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.laf.BundleLookAndFeel;
import org.geogebra.web.full.gui.laf.ChromeLookAndFeel;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.gui.laf.MebisLookAndFeel;
import org.geogebra.web.full.gui.laf.OfficeLookAndFeel;
import org.geogebra.web.full.gui.laf.SmartLookAndFeel;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.geogebra.web.html5.util.SuperDevUncaughtExceptionHandler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * @author Arpad
 */
public abstract class Web implements EntryPoint {

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

		SuperDevUncaughtExceptionHandler.register();
		exportGGBElementRenderer();

		loadAppletAsync();
		allowRerun();
	}

	// TODO: what about global preview events?
	// these are an issue even if we register them elsewhere
	// maybe do not register them again in case of rerun?
	// this could be done easily now with a boolean parameter
	private native void allowRerun() /*-{
		var that = this;
		$wnd.ggbRerun = function() {
			that.@org.geogebra.web.full.Web::loadAppletAsync()();
		}
	}-*/;

	/**
	 * Load UI of all applets.
	 */
	public void loadAppletAsync() {
		startGeoGebra(GeoGebraElement.getGeoGebraMobileTags());
	}

	private native void exportGGBElementRenderer() /*-{
		$wnd.renderGGBElement = $entry(this.@org.geogebra.web.full.Web::renderArticleElement(Lcom/google/gwt/dom/client/Element;Lcom/google/gwt/core/client/JavaScriptObject;))
		@org.geogebra.web.html5.gui.GeoGebraFrameW::renderGGBElementReady()();
		//CRITICAL: "window" below is OK, we need to redirect messages from window to $wnd
		window.addEventListener("message",function(event){$wnd.postMessage(event.data,"*");});
	}-*/;

	/**
	 * @param el
	 *            article element
	 * @param clb
	 *            callback
	 */
	public void renderArticleElement(Element el, JavaScriptObject clb) {
		GeoGebraFrameFull.renderArticleElement(el, getAppletFactory(),
				getLAF(), clb);
	}

	/**
	 * @param geoGebraMobileTags
	 *            article elements
	 */
	protected void startGeoGebra(ArrayList<GeoGebraElement> geoGebraMobileTags) {
		GeoGebraFrameFull.main(geoGebraMobileTags,
				getAppletFactory(), getLAF(), null);
	}

	protected abstract AppletFactory getAppletFactory();

	/**
	 * @return look and feel based the first article that has laf parameter
	 */
	public static GLookAndFeel getLAF() {
		NodeList<Element> nodes = Dom
				.getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);
		for (int i = 0; i < nodes.getLength(); i++) {
			String laf = new AppletParameters((GeoGebraElement) nodes.getItem(i)).getDataParamLAF();
			switch (laf) {
			case "smart":
				return new SmartLookAndFeel();
			case "office":
				return new OfficeLookAndFeel();
			case "bundle":
				return new BundleLookAndFeel();
			case "mebis":
				return new MebisLookAndFeel();
			case "chrome":
				return new ChromeLookAndFeel();
			}
		}

		return new GLookAndFeel();
	}

}

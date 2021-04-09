package org.geogebra.web.full;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.web.full.gui.applet.AppletFactory;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.laf.BundleLookAndFeel;
import org.geogebra.web.full.gui.laf.ChromeLookAndFeel;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.gui.laf.MebisLookAndFeel;
import org.geogebra.web.full.gui.laf.OfficeLookAndFeel;
import org.geogebra.web.full.gui.laf.SmartLookAndFeel;
import org.geogebra.web.html5.GeoGebraGlobal;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
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
	}

	/**
	 * Load UI of all applets.
	 */
	public void loadAppletAsync() {
		GeoGebraFrameFull.main(GeoGebraElement.getGeoGebraMobileTags(),
				getAppletFactory(), getLAF(), null);
	}

	private void exportGGBElementRenderer() {
		GeoGebraGlobal.setRenderGGBElement(this::renderArticleElement);
		GeoGebraFrameW.renderGGBElementReady();
		forwardMessages();
	}

	public static native void forwardMessages() /*-{
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

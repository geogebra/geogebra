package org.geogebra.web.full;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.gwtutil.JsConsumer;
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
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.geogebra.web.html5.util.SuperDevUncaughtExceptionHandler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

import elemental2.core.JsArray;
import elemental2.dom.HTMLCollection;
import jsinterop.base.Js;

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
		removeBackingObject(Parser.getLookaheadSuccess());
		removeBackingObject(com.himamis.retex.editor.share.io.latex.Parser.getLookaheadSuccess());
		GeoGebraFrameFull.main(GeoGebraElement.getGeoGebraMobileTags(),
				getAppletFactory(), getLAF(), null);
	}

	/**
	 * Calling Parser.getLookaheadSuccess() makes sure parser doesn't keep a link to App.
	 * By removing the backing object's stacktrace we make sure it has no link to Web either.
	 */
	private void removeBackingObject(Throwable t) {
		Object back  = Js.asPropertyMap(t).get("backingJsObject");
		if (Js.isTruthy(back)) {
			Js.asPropertyMap(back).set("stack", JsArray.of());
		}
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
	public void renderArticleElement(Element el, JsConsumer<Object> clb) {
		GeoGebraFrameFull.renderArticleElement(el, getAppletFactory(),
				getLAF(), clb);
	}

	protected abstract AppletFactory getAppletFactory();

	/**
	 * @return look and feel based the first article that has laf parameter
	 */
	public static GLookAndFeel getLAF() {
		HTMLCollection<elemental2.dom.Element> nodes =
				Dom.getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);
		for (int i = 0; i < nodes.getLength(); i++) {
			String laf = nodes.getAt(i).getAttribute("data-param-laf");
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

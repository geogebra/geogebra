package org.geogebra.web.web;

import java.util.ArrayList;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.GeoGebraConstants.Versions;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.common.util.debug.SilentProfiler;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.WebSimple;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.web.gui.applet.AppletFactory;
import org.geogebra.web.web.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.web.gui.laf.BundleLookAndFeel;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.gui.laf.OfficeLookAndFeel;
import org.geogebra.web.web.gui.laf.SmartLookAndFeel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author apa
 *
 */
/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Web implements EntryPoint {



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
		Browser.checkFloat64();
		// use GeoGebraProfilerW if you want to profile, SilentProfiler for
		// production
		// GeoGebraProfiler.init(new GeoGebraProfilerW());
		GeoGebraProfiler.init(new SilentProfiler());

		GeoGebraProfiler.getInstance().profile();

		WebSimple.registerSuperdevExceptionHandler();
		exportGGBElementRenderer();

		// setLocaleToQueryParam();

		run();
		allowRerun();
		// just debug for now

	}

	private static void run() {

			// we dont want to parse out of the box sometimes...
			if (!calledFromExtension()) {
				loadAppletAsync();
			} else {
				loadExtensionAsync();
			}


	}

	// TODO: what about global preview events?
	// these are an issue even if we register them elsewhere
	// maybe do not register them again in case of rerun?
	// this could be done easily now with a boolean parameter
	private native void allowRerun() /*-{
		$wnd.ggbRerun = function() {
			@org.geogebra.web.web.Web::run()();
		}
	}-*/;



	private static void loadExtensionAsync() {
		// GWT.runAsync(new RunAsyncCallback() {

		// public void onSuccess() {
		ResourcesInjector.injectResources(false);

		exportArticleTagRenderer();
		// export other methods if needed
		// call the registered methods if any
		GGW_ext_webReady();
		// }

		// public void onFailure(Throwable reason) {
		// TODO Auto-generated method stub

		// }
		// });

	}

	public static void loadAppletAsync() {
		// GWT.runAsync(new RunAsyncCallback() {

		// public void onSuccess() {
		startGeoGebra(ArticleElement.getGeoGebraMobileTags());
		// }

		// ublic void onFailure(Throwable reason) {
		// TODO Auto-generated method stub

		// }
		// });
	}





	native static void exportArticleTagRenderer() /*-{
		$wnd.GGW_ext.render = $entry(@org.geogebra.web.web.Web::renderArticleElement(Lcom/google/gwt/dom/client/Element;Lcom/google/gwt/core/client/JavaScriptObject;))
	}-*/;

	private native void exportGGBElementRenderer() /*-{
		$wnd.renderGGBElement = $entry(@org.geogebra.web.web.Web::renderArticleElement(Lcom/google/gwt/dom/client/Element;Lcom/google/gwt/core/client/JavaScriptObject;))
		@org.geogebra.web.html5.gui.GeoGebraFrameW::renderGGBElementReady()();
		//CRITICAL: "window" below is intentional, the point is to redirect messages from window to $wnd
		window.addEventListener("message",function(event){$wnd.postMessage(event.data,"*");});
	}-*/;

	private native static boolean calledFromExtension() /*-{
		return (typeof $wnd.GGW_ext !== "undefined");
	}-*/;

	public static void renderArticleElement(Element el, JavaScriptObject clb) {

		GeoGebraFrameBoth.renderArticleElement(el,
				(AppletFactory) GWT.create(AppletFactory.class),
				getLAF(ArticleElement.getGeoGebraMobileTags()), clb);
	}

	/*
	 * This method should never be called. Only copyed to external javascript
	 * files, if we like to use GeoGebraWeb as an library, and call its methods
	 * depending on it is loaded or not.
	 */
	private native void copyThisJsIfYouLikeToUseGeoGebraWebAsExtension() /*-{
		//GGW_ext namespace must be a property of the global scope
		$wnd.GGW_ext = {
			startupFunctions : []
		};

		//register methods that will be called if web is loaded,
		//or if it is loaded, will be called immediately
		//GGW_ext.webReady("render",articleelement);
		GGW_ext.webReady = function(functionName, args) {
			if (typeof GGW_ext[functionName] === "function") {
				//web loaded
				this[functionName].apply(args);
			} else {
				this.startupFunctions.push([ functionName, args ]);
			}
		}
	}-*/;

	private static native void GGW_ext_webReady() /*-{
		var functions = null, i, l;
		if (typeof $wnd.GGW_ext === "object") {
			if ($wnd.GGW_ext.startupFunctions
					&& $wnd.GGW_ext.startupFunctions.length) {
				functions = $wnd.GGW_ext.startupFunctions;
				for (i = 0, l = functions.length; i < l; i++) {
					if (typeof $wnd.GGW_ext[functions[i][0]] === "function") {
						$wnd.GGW_ext[functions[i][0]](functions[i][1]);
					}
				}
			}
		}
	}-*/;

	static void startGeoGebra(ArrayList<ArticleElement> geoGebraMobileTags) {

		GeoGebraFrameBoth.main(geoGebraMobileTags,
				(AppletFactory) GWT.create(AppletFactory.class),
				getLAF(geoGebraMobileTags), null);

	}

	public static GLookAndFeel getLAF(
			ArrayList<ArticleElement> geoGebraMobileTags) {
		NodeList<Element> nodes = Dom
				.getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);
		for (int i = 0; i < nodes.getLength(); i++) {
			if ("smart".equals(nodes.getItem(i).getAttribute("data-param-laf"))) {
				return new SmartLookAndFeel();
			}

			if ("office"
					.equals(nodes.getItem(i).getAttribute("data-param-laf"))) {
				return new OfficeLookAndFeel();
			}

			if ("bundle"
					.equals(nodes.getItem(i).getAttribute("data-param-laf"))) {
				return new BundleLookAndFeel();
			}
		}
		if (!((CASFactory) GWT.create(CASFactory.class)).isEnabled()) {
			return new GLookAndFeel() {
				@Override
				public Versions getVersion(int dim, boolean app) {
					return Versions.NO_CAS;
				}
			};
		}

		return new GLookAndFeel();

	}

}

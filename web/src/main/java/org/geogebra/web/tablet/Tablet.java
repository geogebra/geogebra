package org.geogebra.web.tablet;

import java.util.ArrayList;
import java.util.Date;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.common.util.debug.SilentProfiler;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.cas.giac.PNaCl;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.CustomElements;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.tablet.main.TabletDevice;
import org.geogebra.web.touch.PhoneGapManager;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.app.GeoGebraAppFrame;
import org.geogebra.web.web.gui.applet.AppletFactory;
import org.geogebra.web.web.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.web.gui.browser.BrowseGUI;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.main.GDevice;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.gwtphonegap.client.event.BackButtonPressedEvent;
import com.googlecode.gwtphonegap.client.event.BackButtonPressedHandler;

/**
 * @author apa
 *
 */
/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Tablet implements EntryPoint {

	// zum testen von private zu public
	public static GeoGebraAppFrame appFrame;

	public void t(final String s, final AlgebraProcessor ap) throws Exception {
		ap.processAlgebraCommandNoExceptionHandling(s, false, false, true,
		        false);
	}

	private static ArrayList<ArticleElement> getGeoGebraMobileTags() {
		final NodeList<Element> nodes = Dom
		        .getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);
		final ArrayList<ArticleElement> articleNodes = new ArrayList<ArticleElement>();
		for (int i = 0; i < nodes.getLength(); i++) {
			final Date creationDate = new Date();
			nodes.getItem(i).setId(
			        GeoGebraConstants.GGM_CLASS_NAME + i
			                + creationDate.getTime());
			articleNodes.add(ArticleElement.as(nodes.getItem(i)));
		}
		return articleNodes;
	}

	private static boolean checkAppNeeded() {
		final NodeList<Element> nodes = Dom
		        .getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);
		for (int i = 0; i < nodes.getLength(); i++) {
			if ("true".equals(nodes.getItem(i).getAttribute("data-param-app"))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * set true if Google Api Js loaded
	 */

	public void onModuleLoad() {
		if (RootPanel.getBodyElement().getAttribute("data-param-laf") != null
		        && !"".equals(RootPanel.getBodyElement().getAttribute(
		                "data-param-laf"))) {
			// loading touch, ignore.
			return;
		}
		Browser.checkFloat64();
		PhoneGapManager.initializePhoneGap(new BackButtonPressedHandler() {

			@Override
			public void onBackButtonPressed(final BackButtonPressedEvent event) {
				goBack();
			}
		});
		// use GeoGebraProfilerW if you want to profile, SilentProfiler for
		// production
		// GeoGebraProfiler.init(new GeoGebraProfilerW());
		GeoGebraProfiler.init(new SilentProfiler());

		GeoGebraProfiler.getInstance().profile();

		CustomElements.registerGeoGebraWebElement();
		exportGGBElementRenderer();

		// setLocaleToQueryParam();

		if (!Tablet.checkAppNeeded()) {
			// we dont want to parse out of the box sometimes...
			if (!calledFromExtension()) {
				loadAppletAsync();
			} else {
				loadExtensionAsync();
			}
		} else {
			loadAppAsync();
		}

		// just debug for now
		PNaCl.exportPNaCltoConsole();

		// phoneGap.initializePhoneGap();

	}

	public static void goBack() {
		if (appFrame != null && appFrame.app != null) {
			if (appFrame.isBrowserShowing()) {
				GuiManagerW guiManager = (GuiManagerW) appFrame.app
				        .getGuiManager();
				appFrame.hideBrowser((BrowseGUI) guiManager.getBrowseView());
			}
		}
	}

	private void loadExtensionAsync() {
		// GWT.runAsync(new RunAsyncCallback() {

		// public void onSuccess() {
		ResourcesInjector.injectResources();
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
		startGeoGebra(getGeoGebraMobileTags());
		// }

		// ublic void onFailure(Throwable reason) {
		// TODO Auto-generated method stub

		// }
		// });
	}

	private void loadAppAsync() {
		// GWT.runAsync(new RunAsyncCallback() {
		// public void onSuccess() {
		ResourcesInjector.injectResources();
		createGeoGebraAppFrame();
		// }

		// public void onFailure(Throwable reason) {
		// Log.debug(reason);
		// }
		// });

	}

	/**
	 * create app frame
	 */
	protected void createGeoGebraAppFrame() {
		appFrame = new GeoGebraAppFrame((GLookAndFeel) GWT.create(TabletLookAndFeel.class),
		        (GDevice) GWT.create(TabletDevice.class),
		        (AppletFactory) GWT.create(AppletFactory.class));
		appFrame.addStyleName(((TabletLookAndFeel) GWT
				.create(TabletLookAndFeel.class)).getFrameStyleName());
	}

	native void exportArticleTagRenderer() /*-{
   		$wnd.GGW_ext.render = $entry(@org.geogebra.web.tablet.Tablet::renderArticleElement(Lcom/google/gwt/dom/client/Element;Lcom/google/gwt/core/client/JavaScriptObject;))
   	}-*/;

	private native void exportGGBElementRenderer() /*-{
   		$wnd.renderGGBElement = $entry(@org.geogebra.web.tablet.Tablet::renderArticleElement(Lcom/google/gwt/dom/client/Element;Lcom/google/gwt/core/client/JavaScriptObject;))
   		@org.geogebra.web.html5.gui.GeoGebraFrame::renderGGBElementReady()();
   	}-*/;

	private native boolean calledFromExtension() /*-{
		return (typeof $wnd.GGW_ext !== "undefined");
	}-*/;

	public static void renderArticleElement(final Element el,
	        JavaScriptObject clb) {
		GeoGebraFrameBoth.renderArticleElement(el,
		        (AppletFactory) GWT.create(AppletFactory.class),
		        (GLookAndFeel) GWT.create(TabletLookAndFeel.class), clb);
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

	private native void GGW_ext_webReady() /*-{
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

	static void startGeoGebra(final ArrayList<ArticleElement> geoGebraMobileTags) {
		org.geogebra.web.web.gui.applet.GeoGebraFrameBoth.main(geoGebraMobileTags,
		        (AppletFactory) GWT.create(AppletFactory.class),
		        (GLookAndFeel) GWT.create(TabletLookAndFeel.class));
	}

}

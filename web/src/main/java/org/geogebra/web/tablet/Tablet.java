package org.geogebra.web.tablet;

import java.util.ArrayList;

import org.geogebra.web.full.gui.applet.AppletFactory;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.geogebra.web.html5.util.SuperDevUncaughtExceptionHandler;
import org.geogebra.web.tablet.main.TabletDevice;
import org.geogebra.web.touch.PhoneGapManager;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.gwtphonegap.client.event.BackButtonPressedEvent;
import com.googlecode.gwtphonegap.client.event.BackButtonPressedHandler;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Tablet implements EntryPoint {

	// zum testen von private zu public
	private static GeoGebraFrameFull appFrame;

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

		if (useCordova()) {
			PhoneGapManager.initializePhoneGap(new BackButtonPressedHandler() {

				@Override
				public void onBackButtonPressed(final BackButtonPressedEvent event) {
					goBack();
				}
			});
		}

		exportGGBElementRenderer();

		loadAppletAsync();

		// phoneGap.initializePhoneGap();
		SuperDevUncaughtExceptionHandler.register();
	}

	/**
	 * (Android) back button handler
	 */
	public static void goBack() {
		if (appFrame != null) {
			appFrame.onBackPressed();
		}
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
		        (AppletFactory) GWT.create(AppletFactory.class),
		        (GLookAndFeel) GWT.create(TabletLookAndFeel.class), clb);
	}

	/**
	 * @param geoGebraMobileTags
	 *            article elements
	 */
	static void startGeoGebra(final ArrayList<GeoGebraElement> geoGebraMobileTags) {
		GeoGebraFrameFull.main(geoGebraMobileTags,
		        (AppletFactory) GWT.create(AppletFactory.class),
				(GLookAndFeel) GWT.create(TabletLookAndFeel.class),
				(GDevice) GWT.create(TabletDevice.class));
	}

	/**
	 * @return whether cordova is needed, based on global war
	 */
	public static native boolean useCordova() /*-{
		if ($wnd.android) {
			if ($wnd.android.noCordova) {
				return false;
			}
		}
		return true;
	}-*/;

}

package org.geogebra.web.html5.gui;

import java.util.ArrayList;

import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.AppWsimple;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.geogebra.web.html5.util.debug.LoggerW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Frame for simple applets (only EV showing)
 *
 */
public class GeoGebraFrameSimple extends GeoGebraFrameW {
	/**
	 * Frame for simple applets (only EV showing)
	 *
	 * @param articleElement
	 *            article with parameters
	 */
	public GeoGebraFrameSimple(AppletParameters articleElement) {
		super(null, articleElement);
	}

	@Override
	protected AppW createApplication(AppletParameters article,
			GLookAndFeelI laf) {
		return new AppWsimple(article, this, false);
	}

	/**
	 * Main entry points called by geogebra.web.html5.WebSimple.startGeoGebra()
	 *
	 * @param geoGebraMobileTags
	 *            list of &lt;article&gt; elements of the web page
	 */
	public static void main(ArrayList<GeoGebraElement> geoGebraMobileTags) {
		for (final GeoGebraElement geoGebraElement : geoGebraMobileTags) {
			final GeoGebraFrameW inst = new GeoGebraFrameSimple(geoGebraElement);
			LoggerW.startLogger(geoGebraElement);
			inst.createSplash();
			RootPanel.get(geoGebraElement.getId()).add(inst);
		}
	}

	/**
	 * @param el
	 *            html element to render into
	 * @param clb
	 *            callback
	 */
	public static void renderArticleElement(GeoGebraElement el, JavaScriptObject clb) {
		GeoGebraFrameW.renderArticleElementWithFrame(el,
				new GeoGebraFrameSimple(el), clb);
	}

	@Override
	public boolean isKeyboardShowing() {
		return false;
	}

	@Override
	public void showKeyboardOnFocus() {
		// no keyboard
	}

	@Override
	public void updateKeyboardHeight() {
		// no keyboard
	}

	@Override
	public double getKeyboardHeight() {
		return 0;
	}

	@Override
	public void runAsyncAfterSplash() {
		super.runAsyncAfterSplash();
		app.buildApplicationPanel(); // in webSimple we need to init the size
										// before we load file
	}

	@Override
	public void initPageControlPanel(AppW appW) {
		// no page control
	}
}

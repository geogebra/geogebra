package org.geogebra.web.html5.gui;

import java.util.ArrayList;

import org.geogebra.common.factories.CASFactory;
import org.geogebra.gwtutil.JsConsumer;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.AppWsimple;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.geogebra.web.html5.util.debug.LoggerW;

import com.google.gwt.user.client.ui.RootPanel;

/**
 * Frame for simple applets (only EV showing)
 *
 */
public class GeoGebraFrameSimple extends GeoGebraFrameW {
	private final CASFactory casFactory;

	/**
	 * Frame for simple applets (only EV showing)
	 *
	 * @param articleElement
	 *            article with parameters
	 */
	public GeoGebraFrameSimple(GeoGebraElement articleElement, AppletParameters parameters,
			CASFactory factory) {
		super(null, articleElement, parameters);
		this.casFactory = factory;
	}

	@Override
	protected AppW createApplication(GeoGebraElement article,
			AppletParameters parameters, GLookAndFeelI laf) {
		return new AppWsimple(article, parameters, this, false);
	}

	/**
	 * Main entry points called by geogebra.web.html5.WebSimple.startGeoGebra()
	 *
	 * @param geoGebraMobileTags
	 *            list of &lt;article&gt; elements of the web page
	 */
	public static void main(ArrayList<GeoGebraElement> geoGebraMobileTags, CASFactory factory) {
		for (final GeoGebraElement geoGebraElement : geoGebraMobileTags) {
			AppletParameters parameters = new AppletParameters(geoGebraElement);
			GeoGebraFrameW inst = new GeoGebraFrameSimple(geoGebraElement, parameters, factory);
			LoggerW.startLogger(parameters);
			inst.createSplash();
			RootPanel.get(geoGebraElement.getId()).add(inst);
		}
	}

	/**
	 * @param el
	 *            html element to render into
	 * @param clb
	 *            callback
	 * @param factory CAS factory
	 */
	public static void renderArticleElement(GeoGebraElement el, JsConsumer<Object> clb,
			CASFactory factory) {
		AppletParameters parameters = new AppletParameters(el);
		new GeoGebraFrameSimple(el, parameters, factory).renderArticleElementWithFrame(el, clb);
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
	protected void initSize() {
		app.buildApplicationPanel(); // in webSimple we need to init the size
										// before we load file
	}

	@Override
	public void initPageControlPanel(AppW appW) {
		// no page control
	}

	/** Init  CAS factory if needed */
	public void initCasFactory() {
		if (!CASFactory.isInitialized()) {
			CASFactory.setPrototype(casFactory);
		}
	}
}

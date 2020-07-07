package org.geogebra.web.html5.gui;

import java.util.ArrayList;

import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.AppWsimple;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.ArticleElementInterface;
import org.geogebra.web.html5.util.debug.LoggerW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
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
	public GeoGebraFrameSimple(ArticleElementInterface articleElement) {
		super(null, articleElement);
	}

	@Override
	protected AppW createApplication(ArticleElementInterface article,
			GLookAndFeelI laf) {
		return new AppWsimple(article, this, false);
	}

	/**
	 * Main entry points called by geogebra.web.html5.WebSimple.startGeoGebra()
	 *
	 * @param geoGebraMobileTags
	 *            list of &lt;article&gt; elements of the web page
	 */
	public static void main(ArrayList<ArticleElement> geoGebraMobileTags) {
		for (final ArticleElement articleElement : geoGebraMobileTags) {
			final GeoGebraFrameW inst = new GeoGebraFrameSimple(articleElement);
			LoggerW.startLogger(articleElement);
			inst.createSplash();
			RootPanel.get(articleElement.getId()).add(inst);
		}
	}

	/**
	 * @param el
	 *            html element to render into
	 * @param clb
	 *            callback
	 */
	public static void renderArticleElement(Element el, JavaScriptObject clb) {
		GeoGebraFrameW.renderArticleElementWithFrame(el,
				new GeoGebraFrameSimple(ArticleElement.as(el)),
				clb);
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

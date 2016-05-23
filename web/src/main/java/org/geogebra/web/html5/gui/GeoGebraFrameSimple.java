package org.geogebra.web.html5.gui;

import java.util.ArrayList;

import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.AppWsimple;
import org.geogebra.web.html5.util.ArticleElement;
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
	 */
	public GeoGebraFrameSimple() {
		super(null);
	}

	@Override
	protected AppW createApplication(ArticleElement article,
	        GLookAndFeelI laf) {
		return new AppWsimple(article, this);
	}

	/**
	 * Main entry points called by geogebra.web.Web.startGeoGebra()
	 * 
	 * @param geoGebraMobileTags
	 *            list of &lt;article&gt; elements of the web page
	 */
	public static void main(ArrayList<ArticleElement> geoGebraMobileTags) {

		for (final ArticleElement articleElement : geoGebraMobileTags) {
			final GeoGebraFrameW inst = new GeoGebraFrameSimple();
			inst.ae = articleElement;
			LoggerW.startLogger(inst.ae);
			inst.createSplash(articleElement);
			RootPanel.get(articleElement.getId()).add(inst);
		}

		if (geoGebraMobileTags.size() > 0) {
			// now we can create dummy elements before & after each applet
			// with tabindex 10000, for ticket #5158
			tackleFirstDummy(geoGebraMobileTags.get(0));
			tackleLastDummy(geoGebraMobileTags
					.get(geoGebraMobileTags.size() - 1));
			programFocusEvent(firstDummy, lastDummy);
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
				new GeoGebraFrameSimple(), clb);

		GeoGebraFrameW.reCheckForDummies(el);
	}

	@Override
	public void showKeyBoard(boolean b, MathKeyboardListener textField,
	        boolean forceShow) {
		// no keyboard either
	}

	@Override
	public boolean isKeyboardShowing() {
		return false;
	}

	public void showKeyboardOnFocus() {
		// no keyboard
	}

	public void updateKeyboardHeight() {
		// no keyboard
	}

	public double getKeyboardHeight() {
		return 0;
	}
}

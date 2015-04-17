package org.geogebra.web.html5.gui;

import java.util.ArrayList;

import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.AppWsimple;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.debug.GeoGebraLogger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class GeoGebraFrameSimple extends GeoGebraFrame {

	public GeoGebraFrameSimple() {
		super(null);
	}

	protected AppW createApplication(ArticleElement ae,
	        GLookAndFeelI laf) {
		AppW app = new AppWsimple(ae, this);
		return app;
	}

	/**
	 * Main entry points called by geogebra.web.Web.startGeoGebra()
	 * 
	 * @param geoGebraMobileTags
	 *            list of &lt;article&gt; elements of the web page
	 */
	public static void main(ArrayList<ArticleElement> geoGebraMobileTags) {

		for (final ArticleElement articleElement : geoGebraMobileTags) {
			final GeoGebraFrame inst = new GeoGebraFrameSimple();
			inst.ae = articleElement;
			GeoGebraLogger.startLogger(inst.ae);
			inst.createSplash(articleElement);
			RootPanel.get(articleElement.getId()).add(inst);
		}
	}

	/**
	 * @param el
	 *            html element to render into
	 */
	public static void renderArticleElement(Element el, JavaScriptObject clb) {
		GeoGebraFrame.renderArticleElementWithFrame(el,
		        new GeoGebraFrameSimple(), clb);
	}

	@Override
	public void showBrowser(HeaderPanel bg) {
		// no browsing in simple applets

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
}

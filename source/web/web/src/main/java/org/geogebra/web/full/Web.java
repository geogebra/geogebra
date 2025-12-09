/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
import org.geogebra.web.html5.bridge.AttributeProvider;
import org.geogebra.web.html5.bridge.RenderGgbElement;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.geogebra.web.html5.util.SuperDevUncaughtExceptionHandler;
import org.gwtproject.user.client.ui.RootPanel;

import com.google.gwt.core.client.EntryPoint;

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
		removeBackingObject(org.geogebra.editor.share.io.latex.Parser.getLookaheadSuccess());
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
		RenderGgbElement.setRenderGGBElement(this::renderArticleElement);
		RenderGgbElement.renderGGBElementReady();
		forwardMessages();
	}

	public static native void forwardMessages() /*-{
		//CRITICAL: "window" below is OK, we need to redirect messages from window to $wnd
		window.addEventListener("message",function(event){$wnd.postMessage(event.data,"*");});
	}-*/;

	/**
	 * @param options
	 *            article element
	 * @param clb
	 *            callback
	 */
	public void renderArticleElement(Object options, JsConsumer<Object> clb) {
		GeoGebraFrameFull.renderArticleElement(AttributeProvider.as(options), getAppletFactory(),
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

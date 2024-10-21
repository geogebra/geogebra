package org.geogebra.web.html5.main;

import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.Browser;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import elemental2.dom.Node;
import elemental2.dom.URLSearchParams;
import elemental2.promise.Promise;
import jsinterop.base.Js;

public class UserPreferredLanguage {
	private static final String DATA_TRANS_KEY = "data-trans-key";

	/**
	 * Gets user preferred language in a specific order
	 * 
	 * @param app {@link AppW}
	 * @return the preferred language.
	 */
	public static Promise<String> get(AppW app) {
		LogInOperation op = app.getLoginOperation();
		boolean loggedIn = op != null && op.isLoggedIn();
		if (loggedIn) {
			String userLang = op.getUserLanguage();
			if (!StringUtil.empty(userLang)) {
				return Promise.resolve(userLang);
			}
		}

		Promise<String> storedLang = app.getLAF() == null
				? Promise.resolve((String) null) : app.getLAF().loadLanguage();
		return storedLang.then(lang ->
				Promise.resolve(getFallbackLanguage(lang, app, loggedIn)));
	}

	private static String getFallbackLanguage(String lang, AppW app, boolean loggedIn) {
		if (!StringUtil.empty(lang)) {
			return lang;
		}

		String urlLang = app.getAppletParameters().getDataParamApp()
				? new URLSearchParams(DomGlobal.location.search).get("lang") : "";

		if (!StringUtil.empty(urlLang) && !loggedIn) {
			return urlLang;
		}

		String htmlLang = DomGlobal.document.documentElement.lang;
		if (!StringUtil.empty(htmlLang)) {
			return htmlLang;
		}

		return Browser.navigatorLanguage();
	}

	/**
	 * Translates an element recursively using data-trans-key attribute.
	 * 
	 * @param app  {@link AppW}
	 * @param selector HTML element to translate.
	 */
	public static void translate(App app, String selector) {
		Element elem = DomGlobal.document.querySelector(selector);
		// childNodes can be null in GwtMockito ...
		if (Js.isTruthy(elem) && elem.childNodes != null) {
			translate(app, elem);
		}
	}

	private static void translate(App app, Element elem) {
		for (int i = 0; i < elem.childNodes.length; i++) {
			Node child = elem.childNodes.item(i);
			if (child.nodeType == Node.ELEMENT_NODE) {
				HTMLElement childEl = Js.uncheckedCast(child);
				if (childEl.hasAttribute(DATA_TRANS_KEY)) {
					childEl.textContent = app.getLocalization()
							.getMenu(childEl.getAttribute(DATA_TRANS_KEY));
				} else {
					translate(app, childEl);
				}
			}
		}
	}
}

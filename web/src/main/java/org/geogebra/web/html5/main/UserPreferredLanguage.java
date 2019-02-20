package org.geogebra.web.html5.main;

import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.Browser;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window.Location;

public class UserPreferredLanguage {
	public static String get(AppW app) {
		String cookieLang = Cookies.getCookie("GeoGebraLangUI");
		if (!StringUtil.empty(cookieLang)) {
			return cookieLang;
		}

		Storage localStorage = Storage.getLocalStorageIfSupported();
		String storageLang = localStorage.getItem("GeoGebraLangUI");
		if (!StringUtil.empty(storageLang)) {
			return storageLang;
		}

		String urlLang = app.getArticleElement().getDataParamApp() ? Location.getParameter("lang") : "";
		boolean loggedIn = app.getLoginOperation() != null && app.getLoginOperation().isLoggedIn();
		if (!StringUtil.empty(urlLang) && !loggedIn) {
			return urlLang;
		}

		return Browser.navigatorLanguage();
	}
	
	public static void translate(AppW app, Element elem) {
		for (int i = 0; i < elem.getChildCount(); i++) {
			Node child = elem.getChild(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) child;
				if (child.getChildCount() == 1) {
					if (e.hasAttribute("data-trans-key")) {
						String key = e.getAttribute("data-trans-key").replace(" ", "");
						e.setInnerText(app.getLocalization().getMenu(key));
					}
				} else {
					translate(app, e);
				}
			}
		}
	}
}

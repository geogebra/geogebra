package geogebra.html5.gui;

import geogebra.common.main.Localization;
import geogebra.common.util.Language;
import geogebra.common.util.Unicode;
import geogebra.web.main.AppW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class LanguageGUI extends MyHeaderPanel {
	public static final String LOCALE_PARAMETER = "locale";

	private final AppW app;
	private LanguageHeaderPanel header;

	public LanguageGUI(AppW app) {
		this.app = app;
		addHeader();
		addContent();
	}

	private void addContent() {
		FlowPanel fp = new FlowPanel();
		for (Language l : Language.values()) {

			StringBuilder sb = new StringBuilder();

			if (l.enableInGWT) {

				String text = l.name;

				if (text != null) {

					char ch = text.toUpperCase().charAt(0);
					if (ch == Unicode.LeftToRightMark
					        || ch == Unicode.RightToLeftMark) {
						ch = text.charAt(1);
					} else {
						// make sure brackets are correct in Arabic, ie not )US)
						sb.setLength(0);
						sb.append(Unicode.LeftToRightMark);
						sb.append(text);
						sb.append(Unicode.LeftToRightMark);
						text = sb.toString();
					}

					Label label = new Label(text);
					final Language current = l;
					label.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							boolean newDirRTL = Localization
							        .rightToLeftReadingOrder(current.localeGWT);
							if (newDirRTL) {
								setCookies(LOCALE_PARAMETER, "ar");
							} else {
								setCookies(LOCALE_PARAMETER, "en");
							}

							app.setUnsaved();

							// On changing language from LTR/RTL the page will
							// reload.
							// The current workspace will be saved, and load
							// back after page reloading.
							// Otherwise only the language will change, and the
							// setting related with language.
							if (newDirRTL != app.getLocalization().rightToLeftReadingOrder) {
								JavaScriptObject callback = saveBase64ToLocalStorage();
								app.getGgbApi().getBase64(false, callback);
							} else {
								app.setLanguage(current.localeGWT);
							}
							LanguageGUI.this.close();
						}
					});
					fp.add(label);
				}
			}
		}
		this.setContentWidget(fp);
	}

	native static JavaScriptObject saveBase64ToLocalStorage() /*-{
		return function(base64) {
			try {
				localStorage.setItem("reloadBase64String", base64);
				@geogebra.web.gui.app.GeoGebraAppFrame::removeCloseMessage()();
			} catch (e) {
				@geogebra.common.main.App::debug(Ljava/lang/String;)("Base64 sting not saved in local storage");
			} finally {
				$wnd.location.reload();
			}
		}
	}-*/;

	public static void setCookies(String cookieParameter, String localeCode) {
		if (Cookies.getCookie(cookieParameter) == null
		        || "".equals(Cookies.getCookie(cookieParameter))) {
			Cookies.setCookie(cookieParameter, localeCode);
		} else if (!Cookies.getCookie(cookieParameter).equals(localeCode)) {
			Cookies.removeCookie(cookieParameter);
			Cookies.setCookie(cookieParameter, localeCode);
		}
	}

	private void addHeader() {
		this.header = new LanguageHeaderPanel(app.getLocalization(), this);

		this.setHeaderWidget(this.header);
		// this.addResizeListener(this.header);

	}

}

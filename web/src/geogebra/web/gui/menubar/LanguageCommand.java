package geogebra.web.gui.menubar;

import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;

public class LanguageCommand implements Command {

	public static final String LOCALE_PARAMETER = "locale";
	
	private String localeCode;
	private String localeParameter;
	
	
	public LanguageCommand() {
		localeCode = "en";
		localeParameter = "Locale";
	}
	
	public LanguageCommand(String newLocaleCode) {
		this.localeCode = newLocaleCode;
		this.localeParameter = LOCALE_PARAMETER;
	}
	
	public LanguageCommand(String localeParam, String newLocalCode) {
		localeCode = newLocalCode;
		localeParam = localeParam;
	}

	public void setLocaleCode(String aLocale) {
		localeCode = aLocale;
	}
	
	public void setLocaleParameter(String param) {
		localeParameter = param;
	}
	
	public void setNewLocale(String param, String newLocale) {
		localeParameter = param;
		localeCode = newLocale;
	}

	public void execute() {
//		Window.alert("Soon! Language support..." + localeCode);
		changeLocale(localeParameter, localeCode);
		setCookies(LOCALE_PARAMETER, localeCode);
	}
	
	public void changeLocale(String localeParamName, String newLocale) {
		UrlBuilder newUrl = Window.Location.createUrlBuilder();
		newUrl.setParameter(localeParamName, newLocale);
		Window.Location.assign(newUrl.buildString());
	}
	
	public void setCookies(String cookieParameter, String localeCode) {
		if (Cookies.getCookie(cookieParameter) == null) {
			Cookies.setCookie(cookieParameter, localeCode);
		} else if(!Cookies.getCookie(cookieParameter).equals(localeCode)) {
			Cookies.setCookie(cookieParameter, localeCode);
		}
	}
}

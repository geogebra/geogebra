package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.common.util.Language;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;

/**
 * @author Rana
 * This class implements the com.google.gwt.user.client.Command. It changes the locale of the application depending 
 * on what language is pressed.
 * The "default" language is the one that is got from the user-agent's geoip. The chosen locale is saved in a cookie with the name "Locale" 
 *  
 */
public class LanguageCommand implements Command {

	
	/**
	 * LOCALE_PARAMETER: this is the name of the locale parameter for both URL Query Parameter and Cookie Parameter
	 */
	public static final String LOCALE_PARAMETER = "locale";
	
	private String localeCode;
	private String localeParameter;

	private AppW app;
	
	
	/**
	 * Default constructor
	 */
	public LanguageCommand() {
		localeCode = "en";
		localeParameter = "Locale";
	}
	
	/**
	 * @param newLocaleCode
	 */
	public LanguageCommand(Language newLocaleCode, AppW app) {
		this.localeCode = newLocaleCode.localeGWT;
		this.localeParameter = LOCALE_PARAMETER;
		this.app = app;
	}
	
	/**
	 * @param localeParam
	 * @param newLocalCode
	 */
	public LanguageCommand(String localeParam, String newLocalCode) {
		localeCode = newLocalCode;
		localeParameter = localeParam;
	}

	/**
	 * @param aLocale
	 */
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
		//changeLocale(localeParameter, localeCode);
		app.setLanguage(localeCode);
		setCookies(LOCALE_PARAMETER, localeCode);
	}
	
	/**
	 * @param localeParamName
	 * @param newLocale
	 */
	public static void changeLocale(String localeParamName, String newLocale) {
		App.printStacktrace("");
		//UrlBuilder newUrl = Window.Location.createUrlBuilder();
		//newUrl.setParameter(localeParamName, newLocale);
		//Window.Location.assign(newUrl.buildString());
	}
	
	/**
	 * @param cookieParameter
	 * @param localeCode
	 */
	public void setCookies(String cookieParameter, String localeCode) {
		if (Cookies.getCookie(cookieParameter) == null || "".equals(Cookies.getCookie(cookieParameter))) {
			Cookies.setCookie(cookieParameter, localeCode);
		} else if(!Cookies.getCookie(cookieParameter).equals(localeCode)) {
			Cookies.removeCookie(cookieParameter);
			Cookies.setCookie(cookieParameter, localeCode);
		}
	}
}

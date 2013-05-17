package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.util.Language;
import geogebra.html5.main.GgbAPI;
import geogebra.web.main.AppW;

import com.google.gwt.core.client.JavaScriptObject;
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
	private AppW app;
	
	
	/**
	 * Default constructor
	 */
	public LanguageCommand() {
		localeCode = "en";
	}
	
	/**
	 * @param newLocaleCode
	 */
	public LanguageCommand(Language newLocaleCode, AppW app) {
		this.localeCode = newLocaleCode.localeGWT;
		this.app = app;
	}
	
	/**
	 * @param localeParam
	 * @param newLocalCode
	 */
	public LanguageCommand(String localeParam, String newLocalCode) {
		localeCode = newLocalCode;
	}

	/**
	 * @param aLocale
	 */
    public void setLocaleCode(String aLocale) {
		localeCode = aLocale;
	}
	
	public void setNewLocale(String param, String newLocale) {
		localeCode = newLocale;
	}

	public void execute() {
		Cookies.setCookie("GGWlang", localeCode);
		if (Localization.rightToLeftReadingOrder(localeCode)){
			LanguageCommand.setCookies(LanguageCommand.LOCALE_PARAMETER, "ar");
		} else {
			setCookies(LOCALE_PARAMETER, localeCode);
		}
	
		String oldLang = app.getLanguageFromCookie();
		//TODO: change "en" for the default language
		//if there is no cookie yet, it starts with the default language
		if (oldLang==null) oldLang="en";
		boolean oldRTLOrder = Localization.rightToLeftReadingOrder(oldLang);
			
		//On changing language from LTR/RTL the page will reload.
		//The current workspace will be saved, and load back after page reloading.
		//Otherwise only the language will change, and the setting related with language.
		if (oldRTLOrder != app.getLocalization().rightToLeftReadingOrder){
			JavaScriptObject callback = saveBase64ToLocalStorage();
			((GgbAPI) app.getGgbApi()).getBase64(callback);
		} else {
			app.setLanguage(localeCode);
		}
		
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
	public static void setCookies(String cookieParameter, String localeCode) {
		if (Cookies.getCookie(cookieParameter) == null || "".equals(Cookies.getCookie(cookieParameter))) {
			Cookies.setCookie(cookieParameter, localeCode);
		} else if(!Cookies.getCookie(cookieParameter).equals(localeCode)) {
			Cookies.removeCookie(cookieParameter);
			Cookies.setCookie(cookieParameter, localeCode);
		}
	}
}

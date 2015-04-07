package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.Language;
import org.geogebra.web.html5.main.AppW;

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
		setCookies("GGWlang", localeCode);
		boolean newDirRTL = Localization.rightToLeftReadingOrder(localeCode);
		if (newDirRTL){
			setCookies(AppW.LOCALE_PARAMETER, "ar");
		} else {
			setCookies(AppW.LOCALE_PARAMETER, "en");
		}
		
		app.setUnsaved();
			
		
			
		//On changing language from LTR/RTL the page will reload.
		//The current workspace will be saved, and load back after page reloading.
		//Otherwise only the language will change, and the setting related with language.
		if (newDirRTL != app.getLocalization().rightToLeftReadingOrder){
			JavaScriptObject callback = saveBase64ToLocalStorage();
			app.getGgbApi().getBase64(false, callback);
		} else {
			app.setLanguage(localeCode);			
		}
		
	}
	
	native static JavaScriptObject saveBase64ToLocalStorage() /*-{
		return function(base64) {
			try {
				localStorage.setItem("reloadBase64String", base64);
				@org.geogebra.web.web.gui.app.GeoGebraAppFrame::removeCloseMessage()();
			} catch (e) {
				@org.geogebra.common.main.App::debug(Ljava/lang/String;)("Base64 sting not saved in local storage");
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

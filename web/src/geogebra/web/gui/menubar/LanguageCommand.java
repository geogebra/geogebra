package geogebra.web.gui.menubar;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

public class LanguageCommand implements Command {

	private String localeCode;
	private String localeParameter;
	
	
	public LanguageCommand() {
		localeCode = "en";
		localeParameter = "Locale";
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
		Window.alert("Soon! Language support..." + localeCode);
	}
}

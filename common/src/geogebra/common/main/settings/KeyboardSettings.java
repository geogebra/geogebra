package geogebra.common.main.settings;

import geogebra.common.main.App;
import geogebra.common.util.Language;

import java.util.ArrayList;
import java.util.LinkedList;


/**
 * Settings for Virtual Keyboard
 * @author Zbynek Konecny
 *
 */
public class KeyboardSettings extends AbstractSettings {
	
	/**
	 * List of supported locales as strings, e.g. hu, en_GB
	 */
	public static ArrayList<String> supportedLocales = new ArrayList<String>();
	static {
		supportedLocales.add("ar"); // Arabic
		
		// same keyboard layout (TODO: maybe combine)
		supportedLocales.add(Language.Croatian.locale); // Croatian
		supportedLocales.add(Language.Serbian.locale); // Serbian
		supportedLocales.add(Language.Slovenian.locale); // Slovenian
		
		supportedLocales.add(Language.Czech.locale); // Czech
		supportedLocales.add(Language.English_UK.locale); // English (UK)
		supportedLocales.add(Language.French.locale); // French
		supportedLocales.add(Language.German.locale); // German
		supportedLocales.add(Language.Greek.locale); // Greek	
		supportedLocales.add(Language.Finnish.locale); // Finnish	
		supportedLocales.add(Language.Hebrew.locale); // Hebrew
		supportedLocales.add(Language.Hindi.locale); // Hindi		
		supportedLocales.add(Language.Hungarian.locale); // Hungarian		
		supportedLocales.add(Language.Korean.locale); // Korean
		supportedLocales.add(Language.Macedonian.locale); // Macedonian
		supportedLocales.add(Language.Malayalam.locale); // Malayalam
		supportedLocales.add("no"); // Norwegian (covers both)
		supportedLocales.add(Language.Persian.locale); // Persian
		//supportedLocales.add("pt_PT"); // Portuguese (Portugal)
		supportedLocales.add(Language.Russian.locale); // Russian
		supportedLocales.add(Language.Slovak.locale); // Slovak
		supportedLocales.add(Language.Spanish.locale); // Spanish
		supportedLocales.add(Language.Yiddish.locale);
	}

	private float keyboardOpacity = 0.7f;
	private int keyboardWidth = 400;
	private int keyboardHeight = 235;
	private String keyboardLocale = null;
	private boolean showKeyboardOnStart = false;
	
	public KeyboardSettings(LinkedList<SettingListener> listeners) {
		super(listeners);
	}

	public KeyboardSettings() {
		super();
	}

	public float getKeyboardOpacity() {		
		return keyboardOpacity;
	}

	public int getKeyboardWidth() {					
		return keyboardWidth;
	}

	public int getKeyboardHeight() {		
		return keyboardHeight;
	}
	
	public String getKeyboardLocale(){
		return keyboardLocale;
	}

	/*public void setKeyboardLocale(String loc) {		
		keyboardLocale = loc;
		settingChanged();
	}*/
	
	public void setKeyboardWidth(int windowWidth) {			
		keyboardWidth = windowWidth;
		settingChanged();
	}
	
	public void setKeyboardHeight(int windowHeight) {		
		keyboardHeight = windowHeight;
		settingChanged();
	}

	/**
	 * @param showKeyboardOnStart the showKeyboardOnStart to set
	 */
	public void setShowKeyboardOnStart(boolean showKeyboardOnStart) {
		this.showKeyboardOnStart = showKeyboardOnStart;
		settingChanged();
	}

	/**
	 * @return the showKeyboardOnStart
	 */
	public boolean isShowKeyboardOnStart() {
		return showKeyboardOnStart;
	}
	
	public void setKeyboardOpacity(float opacity) {			
		keyboardOpacity = opacity;
		settingChanged();
	}

	public void keyboardResized(int windowWidth, int windowHeight) {
		keyboardWidth = windowWidth;
		keyboardHeight = windowHeight;		
	}

	public void setKeyboardLocale(String string) {
		if(string == null)
			return;
		for(int i=0;i<supportedLocales.size();i++){
			if(supportedLocales.get(i).toString().equals(string)){
				keyboardLocale = supportedLocales.get(i);
				settingChanged();
				return;
			}
		}
		App.debug("Unsupported keyboard locale: "+string);
	}	

}

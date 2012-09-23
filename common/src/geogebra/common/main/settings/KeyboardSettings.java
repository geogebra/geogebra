package geogebra.common.main.settings;

import geogebra.common.main.App;

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
		
		// same keyboard layout currently
		supportedLocales.add("hr"); // Croatian
		supportedLocales.add("sr"); // Serbian
		
		supportedLocales.add("cs"); // Czech
		supportedLocales.add("en_GB"); // English (UK)
		supportedLocales.add("fr"); // French
		supportedLocales.add("de"); // German
		supportedLocales.add("el"); // Greek	
		supportedLocales.add("fi"); // Finnish	
		supportedLocales.add("iw"); // Hebrew
		supportedLocales.add("hi"); // Hindi		
		supportedLocales.add("hu"); // Hungarian		
		supportedLocales.add("ko"); // Korean
		supportedLocales.add("mk"); // Macedonian
		supportedLocales.add("ml"); // Malayalam
		supportedLocales.add("no"); // Norwegian
		supportedLocales.add("fa"); // Persian
		supportedLocales.add("pt_PT"); // Portuguese (Portugal)
		supportedLocales.add("ru"); // Russian
		supportedLocales.add("sk"); // Slovak
		supportedLocales.add("es"); // Spanish
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

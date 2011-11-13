package geogebra.main.settings;

import geogebra.main.Application;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;


/**
 * Settings for Virtual Keyboard
 * @author Zbynek Konecny
 *
 */
public class KeyboardSettings extends AbstractSettings {
	
	public static ArrayList<Locale> supportedLocales = new ArrayList<Locale>();
	static {
		supportedLocales.add(new Locale("ar")); // Arabic
		supportedLocales.add(new Locale("hr")); // Croatian
		supportedLocales.add(new Locale("cs")); // Czech
		supportedLocales.add(new Locale("en", "GB")); // English (UK)
		supportedLocales.add(new Locale("fr")); // French
		supportedLocales.add(new Locale("de")); // German
		supportedLocales.add(new Locale("el")); // Greek	
		supportedLocales.add(new Locale("iw")); // Hebrew
		supportedLocales.add(new Locale("hi")); // Hindi		
		supportedLocales.add(new Locale("hu")); // Hungarian		
		supportedLocales.add(new Locale("ko")); // Korean
		supportedLocales.add(new Locale("ml")); // Malayalam
		supportedLocales.add(new Locale("no")); // Norwegian
		supportedLocales.add(new Locale("fa")); // Persian
		supportedLocales.add(new Locale("ru")); // Russian
		supportedLocales.add(new Locale("sk")); // Slovak
		supportedLocales.add(new Locale("es")); // Spanish
	}

	private float keyboardOpacity = 0.7f;
	private int keyboardWidth = 400;
	private int keyboardHeight = 235;
	private Locale keyboardLocale = null;
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
	
	public Locale getKeyboardLocale(){
		return keyboardLocale;
	}

	public void setKeyboardLocale(Locale loc) {		
		keyboardLocale = loc;
		settingChanged();
	}
	
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
				setKeyboardLocale(supportedLocales.get(i));
				return;
			}
		}
		Application.debug("Unsupported keyboard locale: "+string);
	}	

}

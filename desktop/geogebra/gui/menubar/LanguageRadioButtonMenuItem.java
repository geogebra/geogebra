package geogebra.gui.menubar;

import javax.swing.JRadioButtonMenuItem;

/**
 * tagging subclass to make sure correct font is used for eg Sinhala
 * see GeoGebraMenuBar.setMenuFontRecursive()
 *
 */
@SuppressWarnings("serial")
public class LanguageRadioButtonMenuItem extends JRadioButtonMenuItem {

	/**
	 * @param text text
	 */
	public LanguageRadioButtonMenuItem(String text) {
		super(text);
	}

}

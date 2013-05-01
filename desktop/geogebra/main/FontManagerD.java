package geogebra.main;

import geogebra.common.main.FontManager;
import geogebra.common.util.Language;
import geogebra.common.util.Unicode;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;

import javax.swing.UIManager;

/**
 * Manages fonts for different languages. Use setLanguage() and setFontSize() to
 * initialize the default fonts.
 */
public class FontManagerD extends FontManager {

	private Font					boldFont, italicFont, plainFont, smallFont, serifFont, serifFontBold, javaSans,
									javaSerif, forcedFont;
	private int						fontSize;
	private String					sansName, serifName;
	
	/**
	 * support for eg --forceFont="Esprit LT Book"
	 */
	public static String forcedFontName = null;

	private HashMap			fontMap					= new HashMap();
	private StringBuilder	key						= new StringBuilder();

	public static final String[]	FONT_NAMES_SANSSERIF	= {
		"SansSerif", // Java
		"Arial Unicode MS", // Windows
		"Helvetica", // Mac OS X
		"LucidaGrande", // Mac OS X
		"ArialUnicodeMS" // Mac OS X
	};
	public static final String[]	FONT_NAMES_SERIF		= {
		"Serif", // Java
		"Times New Roman", // Windows
		"Times" // Mac OS X
	};

	public FontManagerD() {
		setFontSize(12);
	}

	/**
	 * Sets default font that works with the given language.
	 */
	public void setLanguage(final Locale locale) throws Exception {
		final String lang = locale.getLanguage();

		// new font names for language
		String fontNameSansSerif = null;
		String fontNameSerif = null;

		// certain languages need special fonts to display its characters
		final StringBuilder testCharacters = new StringBuilder();
		final LinkedList<String> tryFontsSansSerif = new LinkedList<String>(Arrays.asList(FONT_NAMES_SANSSERIF));
		final LinkedList<String> tryFontsSerif = new LinkedList<String>(Arrays.asList(FONT_NAMES_SERIF));

		final String testChar = Language.getTestChar(lang);
		if (testChar != null) {
			testCharacters.append(testChar);
			// Application.debug("Using test char:"+Util.toHexString(testChar.charValue()));
		} // else Application.debug("No language specific test char");

		// CHINESE
		if ("zh".equals(lang)) {
			// last CJK unified ideograph in unicode alphabet
			// testCharacters.append('\u984F');
			tryFontsSansSerif.addFirst("\u00cb\u00ce\u00cc\u00e5");
			tryFontsSerif.addFirst("\u00cb\u00ce\u00cc\u00e5");
		}

		// GEORGIAN
		else if ("ka".equals(lang)) {
			// some Georgian letter
			// testCharacters.append('\u10d8');
			tryFontsSerif.addFirst("Sylfaen");
		}

		// HEBREW
		// Guy Hed, 26.4.2009 - added Yiddish, which also use Hebrew letters.
		else if ("iw".equals(lang) || "ji".equals(lang)) {
			// Hebrew letter "tav"
			// testCharacters.append('\u05ea');

			// move Java fonts to end of list
			tryFontsSansSerif.remove("SansSerif");
			tryFontsSansSerif.addLast("SansSerif");
			tryFontsSerif.remove("Serif");
			tryFontsSerif.addLast("Serif");
		}

		/*
		 * replaced by Unicode.getTestChar()
		 * 
		 * // JAPANESE
		 * else if ("ja".equals(lang)) {
		 * // Katakana letter N
		 * testCharacters.append('\uff9d');
		 * }
		 * 
		 * // TAMIL
		 * else if ("ta".equals(lang)) {
		 * // Tamil digit 1
		 * testCharacters.append('\u0be7');
		 * }
		 * 
		 * // Punjabi
		 * else if ("pa".equals(lang)) {
		 * testCharacters.append('\u0be7');
		 * }
		 * // Hindi
		 * else if ("hi".equals(lang)) {
		 * testCharacters.append('\u0be7');
		 * }
		 * // Urdu
		 * else if ("ur".equals(lang)) {
		 * testCharacters.append('\u0be7');
		 * }
		 * // Gujarati
		 * else if ("gu".equals(lang)) {
		 * testCharacters.append('\u0be7');
		 * } else if ("si".equals(lang)) {
		 * testCharacters.append('\u0d9a'); // letter a
		 * }
		 */

		// we need roman (English) characters if possible
		// eg the language menu :)
		testCharacters.append('a');

		// make sure we use a font that can display the Euler character
		// add at end -> lowest priority
		testCharacters.append(Unicode.eulerChar);

		// get fonts that can display all test characters
		fontNameSansSerif = getFontCanDisplay(tryFontsSansSerif, testCharacters.toString());
		fontNameSerif = getFontCanDisplay(tryFontsSerif, testCharacters.toString());

		// make sure we have sans serif and serif fonts
		if (fontNameSansSerif == null) {
			fontNameSansSerif = "SansSerif";
		}
		if (fontNameSerif == null) {
			fontNameSerif = "Serif";
		}

		// update application fonts if changed
		updateDefaultFonts(fontSize, fontNameSansSerif, fontNameSerif);
	}

	/**
	 * Set default font size.
	 */
	@Override
	public void setFontSize(final int size) {
		// current sans and sansserif font names
		final String sans = plainFont == null ? "SansSerif" : plainFont.getFontName();
		final String serif = serifFont == null ? "Serif" : serifFont.getFontName();

		// update size
		updateDefaultFonts(size, sans, serif);
	}

	private void updateDefaultFonts(final int size, final String sans, final String serif) {
		if ((size == fontSize) && sans.equals(sansName) && serif.equals(serifName)) {
			return;
		}
		fontSize = size;
		sansName = sans;
		serifName = serif;

		// Java fonts
		javaSans = getFont("SansSerif", Font.PLAIN, size);
		javaSerif = getFont("Serif", Font.PLAIN, size);

		// create similar font with the specified size
		plainFont = getFont(sans, Font.PLAIN, size);
		boldFont = getFont(sans, Font.BOLD, size);
		italicFont = getFont(sans, Font.ITALIC, size);
		smallFont = getFont(sans, Font.PLAIN, size - 2);
		if (forcedFontName != null) {
			forcedFont = getFont(forcedFontName, Font.PLAIN, size);
		}

		// serif
		serifFont = getFont(serif, Font.PLAIN, size);
		serifFontBold = getFont(serif, Font.BOLD, size);

		// TODO: causes problems with multiple windows (File -> New Window)
		setLAFFont(plainFont);

		// System.out.println("Fonts updated: sans: " + sans + ", serif: " + serif);
	}

	/**
	 * Returns a font with the specified attributes.
	 * 
	 * @param serif
	 * @param style
	 * @param size
	 */
	public Font getFont(final boolean serif, final int style, final int size) {
		final String name = serif ?
				getSerifFont().getFontName() :
				getPlainFont(false).getFontName();
		return getFont(name, style, size);
	}

	/**
	 * Gets a font from a HashMap to avoid multiple creations
	 * of the same font.
	 */
	private Font getFont(final String name, final int style, final int size) {
		// build font's key name for HashMap
		key.setLength(0);
		key.append(name);
		key.append('_');
		key.append(style);
		key.append('_');
		key.append(size);

		// look if we have this font already in the HashMap
		Font f = (Font) fontMap.get(key.toString());
		if (f == null) {
			// new font: create it and keep it in the HashMap
			f = new Font(name, style, size);
			fontMap.put(key.toString(), f);

			// System.out.println("NEW font: " + f);
		}

		return f;
	}

	/**
	 * Returns a font that can display testString.
	 */
	public Font getFontCanDisplayAwt(final String testString, final boolean serif, final int fontStyle,
			final int fontSize) {
		
		if (forcedFont != null) {
			if (forcedFont.canDisplayUpTo(testString) == -1) {
				return getFont(forcedFontName, fontStyle, fontSize);
			}
			
		}

		final Font appFont = serif ? serifFont : plainFont;
		if (appFont == null) {
			return plainFont;
		}

		// check if default font is ok
		if ((testString == null) || (appFont.canDisplayUpTo(testString) == -1))
		{
			if (appFont.getSize() == fontSize) {
				if (appFont.getStyle() == fontStyle) {
					return appFont;
				}
				else if (fontStyle == Font.BOLD) {
					return serif ? serifFontBold : boldFont;
				}
			}

			// need to compute new font
			return getFont(appFont.getFontName(), fontStyle, fontSize);
		}
		

		// check if standard Java fonts can be used
		final Font javaFont = serif ? javaSerif : javaSans;
		if (javaFont.canDisplayUpTo(testString) == -1) {
			return getFont(javaFont.getName(), fontStyle, fontSize);
		}

		// no standard fonts worked: try harder and go through all
		// fonts to find one that can display the testString
		try {
			final LinkedList tryFonts = serif ?
					new LinkedList(Arrays.asList(FONT_NAMES_SERIF)) :
					new LinkedList(Arrays.asList(FONT_NAMES_SANSSERIF));
			final String fontName = getFontCanDisplay(tryFonts, testString);
			return getFont(fontName, fontStyle, fontSize);
		} catch (final Exception e) {
			return appFont;
		}
	}

	/**
	 * Tries to find a font that can display all given unicode characters.
	 * Starts with tryFontNames first.
	 */
	public String getFontCanDisplay(final LinkedList tryFontNames, final String testCharacters) throws Exception {
		// System.out.println("expensive test getFontCanDisplay, " + testCharacters);

		//if (true) {
		//	return "Comic Sans MS";
		//}
		
		// try given fonts
		if (tryFontNames != null) {
			final Iterator it = tryFontNames.iterator();
			while (it.hasNext()) {
				// create font for name
				final String fontName = (String) it.next();
				final Font font = getFont(fontName, Font.PLAIN, 12);

				// check if creating font worked
				if (font.getFamily().startsWith(fontName)) {
					// test if this font can display all test characters
					if (font.canDisplayUpTo(testCharacters) == -1) {
						return font.getFontName();
					}
				}
			}
		}

		int maxDisplayedChars = 0;
		int bestFont = -1;

		// Determine which fonts best support the characters in testCharacters
		final Font[] allfonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAllFonts();
		for (int j = 0; j < allfonts.length; j++) {
			//App.debug(allfonts[j].toString());
			final int charsDisplayed = allfonts[j].canDisplayUpTo(testCharacters);
			if (charsDisplayed == -1) {
				// avoid "Monospace" font here
				if (!allfonts[j].getFamily().equals("Monospaced")) {
					return allfonts[j].getFontName();
				}
			}

			// no exact match, but store how much matches
			if (charsDisplayed > maxDisplayedChars) {
				bestFont = j;
				maxDisplayedChars = charsDisplayed;
				// Application.debug(allfonts[bestFont].getFontName()+" matched "+charsDisplayed);
			}

		}

		// no exact match, return the font that matches the most characters
		if (bestFont > -1) {
			return allfonts[bestFont].getFontName();
		}

		throw new Exception(
				"Sorry, there is no font for this language available on your computer.");
	}

	final public Font getBoldFont() {
		return boldFont;
	}

	final public Font getItalicFont() {
		return italicFont;
	}

	final public Font getPlainFont(boolean force) {
		if (force && 
				forcedFont != null) {
			return forcedFont;
		}
		return plainFont;
	}

	final public Font getSmallFont() {
		return smallFont;
	}

	final public Font getSerifFont() {
		return serifFont;
	}

	
	private static void setLAFFont(final Font plain) {
		UIManager.put("ColorChooser.font", plain);
		UIManager.put("FileChooser.font", plain);

		// Panel, Pane, Bars
		UIManager.put("Panel.font", plain);
		UIManager.put("TextPane.font", plain);
		UIManager.put("OptionPane.font", plain);
		UIManager.put("OptionPane.messageFont", plain);
		UIManager.put("OptionPane.buttonFont", plain);
		UIManager.put("EditorPane.font", plain);
		UIManager.put("ScrollPane.font", plain);
		UIManager.put("TabbedPane.font", plain);
		UIManager.put("ToolBar.font", plain);
		UIManager.put("ProgressBar.font", plain);
		UIManager.put("Viewport.font", plain);
		UIManager.put("TitledBorder.font", plain);

		// Buttons
		UIManager.put("Button.font", plain);
		UIManager.put("RadioButton.font", plain);
		UIManager.put("ToggleButton.font", plain);
		UIManager.put("ComboBox.font", plain);
		UIManager.put("CheckBox.font", plain);

		// Menus
		UIManager.put("Menu.font", plain);
		UIManager.put("Menu.acceleratorFont", plain);
		UIManager.put("PopupMenu.font", plain);
		UIManager.put("MenuBar.font", plain);
		UIManager.put("MenuItem.font", plain);
		UIManager.put("MenuItem.acceleratorFont", plain);
		UIManager.put("CheckBoxMenuItem.font", plain);
		UIManager.put("RadioButtonMenuItem.font", plain);

		// Fields, Labels
		UIManager.put("Label.font", plain);
		UIManager.put("Table.font", plain);
		UIManager.put("TableHeader.font", plain);
		UIManager.put("Tree.font", plain);
		UIManager.put("Tree.rowHeight", new Integer(plain.getSize() + 5));
		UIManager.put("List.font", plain);
		UIManager.put("TextField.font", plain);
		UIManager.put("PasswordField.font", plain);
		UIManager.put("TextArea.font", plain);
		UIManager.put("ToolTip.font", plain);
	}

	@Override
	public geogebra.common.awt.GFont getFontCanDisplay(final String testString,
			final boolean serif, final int fontStyle, final int fontSize) {
		return new geogebra.awt.GFontD(getFontCanDisplayAwt(testString, serif, fontStyle, fontSize));
	}

}

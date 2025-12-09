/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.main;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.UIManager;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.jre.main.LocalizationJre;
import org.geogebra.common.main.FontManager;
import org.geogebra.desktop.awt.GFontD;
import org.geogebra.editor.share.util.Unicode;

/**
 * Manages fonts for different languages. Use setLanguage() and setFontSize() to
 * initialize the default fonts.
 */
public class FontManagerD extends FontManager {

	private GFont boldFont;
	private GFont italicFont;
	private GFont smallFont;
	private GFont plainFont;
	private GFont serifFont;
	private GFont serifFontBold;
	private GFont javaSans;
	private GFont javaSerif;

	private int fontSize;
	private String sansName;
	private String serifName;

	private final HashMap<String, Font> fontMap = new HashMap<>();
	private final StringBuilder key = new StringBuilder();

	private static final String[] FONT_NAMES_SANSSERIF = { "SansSerif", // Java
			"Arial Unicode MS", // Windows
			"Helvetica", // Mac OS X
			"LucidaGrande", // Mac OS X
			"ArialUnicodeMS" // Mac OS X
	};
	private static final String[] FONT_NAMES_SERIF = { "Serif", // Java
			"Times New Roman", // Windows
			"Times" // Mac OS X
	};

	public static class NoFontException extends Exception {
		public NoFontException() {
			super("Sorry, there is no font for this language available on your computer.");
		}
	}

	public FontManagerD() {
		setFontSize(12);
	}

	/**
	 * Sets default font that works with the given language.
	 * @throws NoFontException if no font works for given locale
	 */
	public void setLanguage(final LocalizationJre localization) throws NoFontException {
		final String lang = localization.getLocale().getLanguage();

		// new font names for language
		String fontNameSansSerif;
		String fontNameSerif;

		// certain languages need special fonts to display its characters
		final StringBuilder testCharacters = new StringBuilder();
		final LinkedList<String> tryFontsSansSerif = new LinkedList<>(
				Arrays.asList(FONT_NAMES_SANSSERIF));
		final LinkedList<String> tryFontsSerif = new LinkedList<>(
				Arrays.asList(FONT_NAMES_SERIF));

		final String testChar = localization.getLanguage().getTestChar();
		if (testChar != null) {
			testCharacters.append(testChar);
		}

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

		// HEBREW, YIDDISH (both use Hebrew letters)
		else if ("iw".equals(lang) || "ji".equals(lang)) {
			// Hebrew letter "tav"
			// testCharacters.append('\u05ea');

			// move Java fonts to end of list
			tryFontsSansSerif.remove("SansSerif");
			tryFontsSansSerif.addLast("SansSerif");
			tryFontsSerif.remove("Serif");
			tryFontsSerif.addLast("Serif");
		}

		// we need roman (English) characters if possible
		// eg the language menu :)
		testCharacters.append('a');

		// make sure we use a font that can display the Euler character
		// add at end -> lowest priority
		testCharacters.append(Unicode.EULER_CHAR);

		// get fonts that can display all test characters
		fontNameSansSerif = getFontCanDisplay(tryFontsSansSerif,
				testCharacters.toString());
		fontNameSerif = getFontCanDisplay(tryFontsSerif,
				testCharacters.toString());

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
		final String sans = plainFont == null ? "SansSerif"
				: plainFont.getFontName();
		final String serif = serifFont == null ? "Serif"
				: serifFont.getFontName();

		// update size
		updateDefaultFonts(size, sans, serif);
	}

	/**
	 * @param size font size
	 * @param sans sans-serif font name
	 * @param serif serif font name
	 */
	public void updateDefaultFonts(final int size, final String sans,
			final String serif) {
		if ((size == fontSize) && sans.equals(sansName)
				&& serif.equals(serifName)) {
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

		// serif
		serifFont = getFont(serif, Font.PLAIN, size);
		serifFontBold = getFont(serif, Font.BOLD, size);

		// TODO: causes problems with multiple windows (File -> New Window)
		setLAFFont(((GFontD) plainFont).getAwtFont());
	}

	/**
	 * @return a font with the specified attributes.
	 * 
	 * @param serif whether the font is serif
	 * @param style font style
	 * @param size font size
	 */
	public GFont getFont(final boolean serif, final int style, final int size) {
		final String name = serif ? getSerifFont().getFontName()
				: getPlainFont().getFontName();
		return getFont(name, style, size);
	}

	/**
	 * Gets a font from a HashMap to avoid multiple creations of the same font.
	 */
	private GFont getFont(final String name, final int style, final int size) {
		// build font's key name for HashMap
		key.setLength(0);
		key.append(name);
		key.append('_');
		key.append(style);
		key.append('_');
		key.append(size);

		// look if we have this font already in the HashMap
		Font f = fontMap.get(key.toString());
		if (f == null) {
			// new font: create it and keep it in the HashMap
			f = new Font(name, style, size);
			fontMap.put(key.toString(), f);
		}

		return new GFontD(f);
	}

	/**
	 * Returns a font that can display testString.
	 */
	@Override
	public GFont getFontCanDisplay(final String testString,
			final boolean serif, final int fontStyle, final int fontSize) {

		final GFont appFont = serif ? serifFont : plainFont;
		if (appFont == null) {
			return plainFont;
		}

		// check if default font is ok
		if ((testString == null)
				|| (appFont.canDisplayUpTo(testString) == -1)) {
			if (appFont.getSize() == fontSize) {
				if (appFont.getStyle() == fontStyle) {
					return appFont;
				} else if (fontStyle == Font.BOLD) {
					return serif ? serifFontBold : boldFont;
				}
			}

			// need to compute new font
			return getFont(appFont.getFontName(), fontStyle, fontSize);
		}

		// check if standard Java fonts can be used
		final GFont javaFont = serif ? javaSerif : javaSans;
		if (javaFont.canDisplayUpTo(testString) == -1) {
			return getFont(((GFontD) javaFont).getAwtFont().getName(),
					fontStyle, fontSize);
		}

		// no standard fonts worked: try harder and go through all
		// fonts to find one that can display the testString
		try {
			final LinkedList<String> tryFonts = serif
					? new LinkedList<>(Arrays.asList(FONT_NAMES_SERIF))
					: new LinkedList<>(Arrays.asList(FONT_NAMES_SANSSERIF));
			final String fontName = getFontCanDisplay(tryFonts, testString);
			return getFont(fontName, fontStyle, fontSize);
		} catch (final Exception e) {
			return appFont;
		}
	}

	/**
	 * Tries to find a font that can display all given unicode characters.
	 * Starts with tryFontNames first.
	 * @return font name
	 * @throws NoFontException if no font works for given locale
	 */
	public String getFontCanDisplay(final LinkedList<String> tryFontNames,
			final String testCharacters) throws NoFontException {

		// try given fonts
		if (tryFontNames != null) {
			for (String fontName : tryFontNames) {
				// create font for name
				final GFont font = getFont(fontName, Font.PLAIN, 12);

				// check if creating font worked
				if (((GFontD) font).getAwtFont().getFamily()
						.startsWith(fontName)) {
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
		final Font[] allfonts = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getAllFonts();
		for (int j = 0; j < allfonts.length; j++) {
			// Log.debug(allfonts[j].toString());
			final int charsDisplayed = allfonts[j]
					.canDisplayUpTo(testCharacters);
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
			}

		}

		// no exact match, return the font that matches the most characters
		if (bestFont > -1) {
			return allfonts[bestFont].getFontName();
		}

		throw new NoFontException();
	}

	final public GFont getBoldFont() {
		return boldFont;
	}

	final public GFont getItalicFont() {
		return italicFont;
	}

	final public GFont getPlainFont() {
		return plainFont;
	}

	final public GFont getSmallFont() {
		return smallFont;
	}

	final public GFont getSerifFont() {
		return serifFont;
	}

	private static void setLAFFont(final Font font) {
		UIManager.put("ColorChooser.font", font);
		UIManager.put("FileChooser.font", font);

		// Panel, Pane, Bars
		UIManager.put("Panel.font", font);
		UIManager.put("TextPane.font", font);
		UIManager.put("OptionPane.font", font);
		UIManager.put("OptionPane.messageFont", font);
		UIManager.put("OptionPane.buttonFont", font);
		UIManager.put("EditorPane.font", font);
		UIManager.put("ScrollPane.font", font);
		UIManager.put("TabbedPane.font", font);
		UIManager.put("ToolBar.font", font);
		UIManager.put("ProgressBar.font", font);
		UIManager.put("Viewport.font", font);
		UIManager.put("TitledBorder.font", font);

		// Buttons
		UIManager.put("Button.font", font);
		UIManager.put("RadioButton.font", font);
		UIManager.put("ToggleButton.font", font);
		UIManager.put("ComboBox.font", font);
		UIManager.put("CheckBox.font", font);

		// Menus
		UIManager.put("Menu.font", font);
		UIManager.put("Menu.acceleratorFont", font);
		UIManager.put("PopupMenu.font", font);
		UIManager.put("MenuBar.font", font);
		UIManager.put("MenuItem.font", font);
		UIManager.put("MenuItem.acceleratorFont", font);
		UIManager.put("CheckBoxMenuItem.font", font);
		UIManager.put("RadioButtonMenuItem.font", font);

		// Fields, Labels
		UIManager.put("Label.font", font);
		UIManager.put("Table.font", font);
		UIManager.put("TableHeader.font", font);
		UIManager.put("Tree.font", font);
		UIManager.put("Tree.rowHeight", Integer.valueOf(font.getSize() + 5));
		UIManager.put("List.font", font);
		UIManager.put("TextField.font", font);
		UIManager.put("PasswordField.font", font);
		UIManager.put("TextArea.font", font);
		UIManager.put("ToolTip.font", font);
	}

	/**
	 * @return font size
	 */
	public int getFontSize() {
		return fontSize;
	}

}

package org.geogebra.web.html5.gui.laf;

public enum FontFamily {

	ARIAL("Arial", "Arial, sans-serif"),
	CALIBRI("Calibri", "Calibri, sans-serif"),
	CENTURY_GOTHIC("Century Gothic", "Century Gothic, sans-serif"),
	COMIC_SANS("Comic Sans", "Comic Sans MS, sans-serif"),
	COURIER("Courier", "Courier, monospace"),
	GEORGIA("Georgia", "Georgia, serif"),
	DYSLEXIC("Open dyslexic mit Fibel a", "OpenDyslexicAlta"
			+ ", sans-serif"),
	PALATINO("Palatino", "Palatino Linotype, serif"),
	QUICKSAND("Quicksand", "Quicksand, sans-serif"),
	ROBOTO("Roboto", "Roboto, sans-serif"),
	SCHULBUCH_BAYERN("Schulbuch Bayern", "schulbuchbayerncomp-webfont, sans-serif"),
	SF_MONO("SF Mono", "SF Mono, monospace"),
	SF_PRO("SF Pro", "SF Pro, sans-serif"),
	SOURCE_SANS_PRO("Source Sans Pro", "SourceSansPro, sans-serif"),
	TIMES("Times", "Times, serif"),
	TITILLIUM("Titillium Web", "TitilliumWeb, sans-serif"),
	TREBUCHET("Trebuchet", "Trebuchet MS, sans-serif"),
	VERDANA("Verdana", "Verdana, sans-serif"),
	ABeZehBlueRedEDUBold("ABeZehBlueRedEDU-Bold", "ABeZehBlueRedEDU-Bold, sans-serif"),
	ABeZehBlueRedEDULight("ABeZehBlueRedEDU-Light", "ABeZehBlueRedEDU-Light, sans-serif"),
	ABeZehBlueRedEDURegular("ABeZehBlueRedEDU-Regular", "ABeZehBlueRedEDU-Regular, sans-serif"),
	ABeZehEDUBold("ABeZehEDU-Bold", "ABeZehEDU-Bold, sans-serif"),
	ABeZehEDUBoldItalic("ABeZehEDU-BoldItalic", "ABeZehEDU-BoldItalic, sans-serif"),
	ABeZehEDUItalic("ABeZehEDU-Italic", "ABeZehEDU-Italic, sans-serif"),
	ABeZehEDULight("ABeZehEDU-Light", "ABeZehEDU-Light, sans-serif"),
	ABeZehEDULightItalic("ABeZehEDU-LightItalic", "ABeZehEDU-LightItalic, sans-serif"),
	ABeZehEDURegular("ABeZehEDU-Regular", "ABeZehEDU-Regular, sans-serif"),
	ABeZehHokuspokusEDUDEBold("ABeZehHokuspokusEDUDE-Bold",
			"ABeZehHokuspokusEDUDE-Bold, sans-serif"),
	ABeZehHokuspokusEDUDERegular("ABeZehHokuspokusEDUDE-Regular",
			"ABeZehHokuspokusEDUDE-Regular, sans-serif"),
	ABeZehHokuspokusEDUENBold("ABeZehHokuspokusEDUEN-Bold",
			"ABeZehHokuspokusEDUEN-Bold, sans-serif"),
	ABeZehHokuspokusEDUENRegular("ABeZehHokuspokusEDUEN-Regular",
			"ABeZehHokuspokusEDUEN-Regular, sans-serif"),
	ABeZehIconsEDUDeutsch("ABeZehIconsEDU-Deutsch", "ABeZehIconsEDU-Deutsch, sans-serif"),
	ABeZehIconsEDUEnglish("ABeZehIconsEDU-English", "ABeZehIconsEDU-English, sans-serif"),
	ABeZehIconsEDUFrancais("ABeZehIconsEDU-Francais", "ABeZehIconsEDU-Francais, sans-serif"),
	ABeZehLinieEDULight("ABeZehLinieEDU-Light", "ABeZehLinieEDU-Light, sans-serif"),
	ABeZehLinieEDURegular("ABeZehLinieEDU-Regular", "ABeZehLinieEDU-Regular, sans-serif"),
	ABeZehPfeilEDULight("ABeZehPfeilEDU-Light", "ABeZehPfeilEDU-Light, sans-serif"),
	ABeZehPfeilEDURegular("ABeZehPfeilEDU-Regular", "ABeZehPfeilEDU-Regular, sans-serif"),
	ABeZehPfeilEDULINKSLight("ABeZehPfeilEDULINKS-Light", "ABeZehPfeilEDULINKS-Light, sans-serif"),
	ABeZehPunktEDULight("ABeZehPunktEDU-Light", "ABeZehPunktEDU-Light, sans-serif"),
	ABeZehPunktEDURegular("ABeZehPunktEDU-Regular", "ABeZehPunktEDU-Regular, sans-serif");

	private final String displayName;
	private final String cssName;

	FontFamily(String displayName, String cssName) {
		this.displayName = displayName;
		this.cssName = cssName;
	}

	public String displayName() {
		return displayName;
	}

	public String cssName() {
		return cssName;
	}
}

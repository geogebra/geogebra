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
	VERDANA("Verdana", "Verdana, sans-serif");

	private String displayName;
	private String cssName;

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

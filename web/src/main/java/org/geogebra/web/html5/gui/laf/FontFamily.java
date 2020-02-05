package org.geogebra.web.html5.gui.laf;

public enum FontFamily {
	ARIAL("Arial", "arial, sans-serif"),
	CALIBRI("Calibri", "Calibri, sans-serif"),
	CENTURY_GOTHIC("Century Gothic", "Century Gothic, sans-serif"),
	COMIC_SANS("Comic Sans", "Comic Sans MS, sans-serif"),
	COURIER("Courier", "Courier, monospace"),
	GEORGIA("Georgia", "Georgia, serif"),
	DYSLEXIC("Open dyslexic mit Fibel a", "Open dyslexic mit Fibel a"
			+ ", sans-serif"),
	PALATINO("Palatino", "Palatino Linotype, times"),
	QUICKSAND("Qicksand", "Qicksand, sans-serif"),
	ROBOTO("Roboto", "Roboto, sans-serif"),
	SCHULBUCH_BAYERN("Schulbuch Bayern", "Schulbuch Bayern, sans-serif"),
	SF_MONO("SF Mono", "SF Mono, monospace"),
	SF_PRO("SF Pro", "SF Pro, sans-serif"),
	TIMES("Times", "Times, serif"),
	TITILIUM("Titilium Web", "Titilium Web, sans-serif"),
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

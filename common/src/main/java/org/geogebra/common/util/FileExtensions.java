package org.geogebra.common.util;

public enum FileExtensions {
	PNG("png", true),

	GIF("gif", true),

	EPS("eps", false),

	SVG("svg", false),

	BMP("bmp", true),

	PDF("pdf", false),

	EMF("emf", false),

	HTML("html", false),

	HTM("htm", false),

	TEX("tex", false),

	ASYMTOTE("asy", false),

	OFF("off", false),

	TXT("txt", false),

	CSV("csv", false),

	DAT("dat", false),

	JPG("jpg", true),

	JPEG("jpeg", true),

	UNKNOWN("?", false),

	GEOGEBRA("ggb", false),

	GEOGEBRA_TOOL("ggt", false);

	public boolean bitmap;
	public String ext;

	private FileExtensions(String extension, boolean bitmap) {
		this.bitmap = bitmap;
		this.ext = extension;

	}

	@Override
	public String toString() {
		return ext;
	}

	public static FileExtensions get(String ext0) {
		for (FileExtensions fe : FileExtensions.values()) {
			if (fe.ext.equals(ext0)) {
				return fe;
			}
		}

		return UNKNOWN;

	}

}

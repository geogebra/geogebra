package org.geogebra.common.util;

public enum FileExtensions {

	// only these 4 PNG/JPG/JPEG/SVG are allowed image formats in .ggb files
	// all others are converted to PNG
	PNG("png", true),

	JPG("jpg", true),

	JPEG("jpeg", true),

	SVG("svg", true),

	BMP("bmp", true),

	GIF("gif", false),

	EPS("eps", false),

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

	UNKNOWN("?", false),

	GEOGEBRA("ggb", false),

	GEOGEBRA_TOOL("ggt", false);

	public boolean allowedImage;
	public String ext;

	private FileExtensions(String extension, boolean allowedImage) {
		this.allowedImage = allowedImage;
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

	public boolean isAllowedImage() {
		return allowedImage;
	}

}

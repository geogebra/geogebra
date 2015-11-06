package org.geogebra.common.util;

public enum FileExtensions {

	// only these 4 PNG/JPG/JPEG/SVG are allowed image formats in .ggb files
	// all others are converted to PNG
	PNG("png", true, true),

	JPG("jpg", true, true),

	JPEG("jpeg", true, true),

	SVG("svg", true, true),

	BMP("bmp", false, true),

	GIF("gif", false, true),

	TIFF("tiff", false, true),

	TIF("tif", false, true),

	EPS("eps", false, false),

	PDF("pdf", false, false),

	EMF("emf", false, false),

	HTML("html", false, false),

	HTM("htm", false, false),

	TEX("tex", false, false),

	ASYMTOTE("asy", false, false),

	OFF("off", false, false),

	TXT("txt", false, false),

	CSV("csv", false, false),

	DAT("dat", false, false),

	UNKNOWN("?", false, false),

	GEOGEBRA("ggb", false, false),

	GEOGEBRA_TOOL("ggt", false, false);

	private boolean allowedImage;
	private boolean isImage;
	public String ext;

	private FileExtensions(String extension, boolean allowedImage,
			boolean isImage) {
		this.allowedImage = allowedImage;
		this.isImage = isImage;
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

	/**
	 * @return is image that can be plotted in EV (ie not EPS, PDF)
	 */
	public boolean isImage() {
		return isImage;
	}

}

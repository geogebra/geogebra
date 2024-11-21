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

	HEIC("heic", false, true),

	HTML("html", false, false),

	HTM("htm", false, false),

	TEX("tex", false, false),

	ASYMTOTE("asy", false, false),

	OFF("off", false, false),

	TXT("txt", false, false),

	CSV("csv", false, false),

	DAT("dat", false, false),

	COLLADA("dae", false, false),

	STL("stl", false, false),

	UNKNOWN("?", false, false),

	GEOGEBRA("ggb", false, false),

	GEOGEBRA_TOOL("ggt", false, false);

	final private boolean allowedImage;
	final private boolean isImage;
	final private String ext;

	private FileExtensions(String extension, boolean allowedImage,
			boolean isImage) {
		this.allowedImage = allowedImage;
		this.isImage = isImage;
		this.ext = extension;
	}

	@Override
	final public String toString() {
		return ext;
	}

	/**
	 * @param ext0
	 *            string file extension
	 * @return corresponding enum value or UNDEFINED
	 */
	public static FileExtensions get(String ext0) {
		for (FileExtensions fe : FileExtensions.values()) {
			if (fe.ext.equals(ext0)) {
				return fe;
			}
		}

		return UNKNOWN;
	}

	/**
	 * @return whether file may be saved in GGB without renaming or converting
	 */
	final public boolean isAllowedImage() {
		return allowedImage;
	}

	/**
	 * @return is image that can be plotted in EV (ie not EPS, PDF)
	 */
	final public boolean isImage() {
		return isImage;
	}

	/**
	 * @return mime type
	 */
	public String getMime() {
		if (this == JPG) {
			return JPEG.getMime();
		}
		return isImage ? "image/" + ext : "text/plain";
	}
}

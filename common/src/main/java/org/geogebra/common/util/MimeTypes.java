package org.geogebra.common.util;

import org.geogebra.common.main.App;

public enum MimeTypes {
	GEOGEBRA("application/vnd.geogebra.file", FileExtensions.GEOGEBRA.toString()),
	GEOGEBRA_TOOL("application/vnd.geogebra.tool", FileExtensions.GEOGEBRA_TOOL.toString()),
	GEOGEBRA_NOTES("application/zip", "ggs"),
	;

	private final String type;
	private final String extension;
	private final String dotExtension;

	MimeTypes(String type, String extension) {
		this.type = type;
		this.extension = extension;
		dotExtension = "." + extension;
	}

	/**
	 *
	 * @return the mime type, like 'application/zip'.
	 */
	public String type() {
		return type;
	}

	/**
	 *
	 * @return the extension.
	 */
	public String extension() {
		return extension;
	}

	/**
	 *
	 * @return the extension with dot like ".ggb" .
	 */
	public String dotExtension() {
		return dotExtension;
	}

	/**
	 *
	 * @param app {@link App}
	 * @return the MimeType that suits to the current application.
	 */
	public static MimeTypes forApplication(App app) {
		if (app.isWhiteboardActive()) {
			return MimeTypes.GEOGEBRA_NOTES;
		} else if (app.getEditMacro() != null) {
			return MimeTypes.GEOGEBRA_TOOL;
		}
		return MimeTypes.GEOGEBRA;
	}
}
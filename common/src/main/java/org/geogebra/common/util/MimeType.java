package org.geogebra.common.util;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

public enum MimeType {
	GEOGEBRA("application/vnd.geogebra.file", FileExtensions.GEOGEBRA.toString(),
			"Download.GeoGebraFile"),
	GEOGEBRA_TOOL("application/vnd.geogebra.tool", FileExtensions.GEOGEBRA_TOOL.toString(),
			"GeoGebra Tool"), // description not needed,
	GEOGEBRA_NOTES("application/vnd.geogebra.slides", "ggs", "Download.SlidesGgs"),
	;

	private final String type;
	private final String extension;
	private final String dotExtension;
	private String descriptionKey;

	MimeType(String type, String extension, String descriptionKey) {
		this.type = type;
		this.extension = extension;
		this.descriptionKey = descriptionKey;
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
	public static MimeType forApplication(App app) {
		if (app.isWhiteboardActive()) {
			return MimeType.GEOGEBRA_NOTES;
		} else if (app.getEditMacro() != null) {
			return MimeType.GEOGEBRA_TOOL;
		}
		return MimeType.GEOGEBRA;
	}

	/**
	 * @param loc localization
	 * @return human readable description
	 */
	public String getDescription(Localization loc) {
		// "GeoGebra file (.ggb)" => "GeoGebra file"
		return loc.getMenu(descriptionKey).split("\\(")[0].trim();
	}
}
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

package org.geogebra.common.util;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

/**
 * Mime types that can be opened by the apps.
 */
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
	private final String descriptionKey;

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
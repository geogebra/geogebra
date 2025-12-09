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

package org.geogebra.web.html5.safeimage;

import java.util.List;

import org.geogebra.common.util.FileExtensions;
import org.geogebra.web.html5.util.ArchiveEntry;

/**
 * Produces a safe image from the original
 * ensuring it has no suspicious content
 *
 * @author laszlo
 */
public class SafeImage {
	private final FileExtensions extension;
	private SafeImageProvider provider;
	private List<ImagePreprocessor> preprocessors;
	private ArchiveEntry imageFile;

	/**
	 * @param imageFile the original image.
	 * @param provider to notify when safe image is done.
	 * @param preprocessors list of preprocessors
	 * @param originalExtension extension of the original file,
	 *          null to use the extension of {@code imageFile}
	 */
	public SafeImage(ArchiveEntry imageFile, SafeImageProvider provider,
			List<ImagePreprocessor> preprocessors, FileExtensions originalExtension) {
		this.imageFile = imageFile;
		this.provider = provider;
		this.preprocessors = preprocessors;
		this.extension = originalExtension == null ? imageFile.getExtension() : originalExtension;
	}

	/**
	 * Pre-process image.
	 */
	public void process() {
		for (ImagePreprocessor preprocessor : preprocessors) {
			if (preprocessor.match(extension, imageFile.getSizeKB())) {
				preprocessor.process(imageFile, provider);
				return;
			}
		}

		// If it is not PNG, JPG, or SVG we just pass it back
		provider.onReady(imageFile);
	}
}

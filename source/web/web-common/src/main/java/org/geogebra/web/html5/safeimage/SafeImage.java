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

package org.geogebra.web.html5.safeimage;

import java.util.List;

/**
 * Produces a safe image from the original
 * ensuring it has no suspicious content
 *
 * @author laszlo
 */
public class SafeImage {
	private SafeImageProvider provider;
	private List<ImagePreprocessor> preprocessors;
	private ImageFile imageFile;

	/**
	 * @param imageFile the original image.
	 * @param provider to notify when safe image is done.
	 * @param preprocessors list of preprocessors
	 */
	public SafeImage(ImageFile imageFile, SafeImageProvider provider,
			List<ImagePreprocessor> preprocessors) {
		this.imageFile = imageFile;
		this.provider = provider;
		this.preprocessors = preprocessors;
	}

	/**
	 * Pre-process image.
	 */
	public void process() {
		for (ImagePreprocessor preprocessor : preprocessors) {
			if (preprocessor.match(imageFile.getExtension())) {
				preprocessor.process(imageFile, provider);
				return;
			}
		}

		// If it is not PNG, JPG, or SVG we just pass it back
		provider.onReady(imageFile);
	}
}

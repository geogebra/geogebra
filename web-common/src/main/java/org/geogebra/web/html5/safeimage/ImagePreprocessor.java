package org.geogebra.web.html5.safeimage;

import org.geogebra.common.util.FileExtensions;
import org.geogebra.web.html5.util.ArchiveEntry;

/**
 * Interface to pre-process image to
 * make it safer.
 */
public interface ImagePreprocessor {

	/**
	 *
	 * @param extension of the image.
	 * @return if preprocessor should be applied.
	 */
	boolean match(FileExtensions extension, int size);

	/**
	 * Do the pre-processing work.
	 * @param imageFile the original image content.
	 * @param safeImageProvider to give back the safe content
	 */
	void process(ArchiveEntry imageFile, SafeImageProvider safeImageProvider);
}

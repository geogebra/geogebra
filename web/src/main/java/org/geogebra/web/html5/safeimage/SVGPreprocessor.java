package org.geogebra.web.html5.safeimage;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.ImageManager;
import org.geogebra.web.html5.Browser;

public class SVGPreprocessor implements ImagePreprocessor {
	private static final List<String> tagsToCut = Arrays.asList("script",
			"foreignObject");
	private XMLUtil xml = new XMLUtil();

	@Override
	public boolean match(FileExtensions extension) {
		return FileExtensions.SVG.equals(extension);
	}

	@Override
	public void process(ImageFile imageFile, SafeImageProvider provider) {
		xml.setContent(imageFile.getContent());
		removeTags();
		provider.onReady(new ImageFile(imageFile.getFileName(), encodeSVG()));
	}

	private void removeTags() {
		for (String tag: tagsToCut) {
			xml.removeTag(tag);
		}
	}

	private String encodeSVG() {
		return Browser.encodeSVG(ImageManager.fixSVG(xml.getContent()));
	}
}

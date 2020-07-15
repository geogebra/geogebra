package org.geogebra.web.html5.safeimage;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.util.FileExtensions;
import org.geogebra.web.html5.Browser;

public class SVGPreprocessor implements ImagePreprocessor {
	private static final List<String> tagsToCut = Arrays.asList("script",
			"foreignObject");
	public static final String BASE_64 = "base64,";
	private final XMLUtil xml = new XMLUtil();

	@Override
	public boolean match(FileExtensions extension) {
		return SVGUtil.match(extension);
	}

	@Override
	public void process(ImageFile imageFile, SafeImageProvider provider) {
		String content = toDecoded(imageFile.getContent());
		xml.setContent(content);
		removeTags();
		provider.onReady(new ImageFile(imageFile.getFileName(), encodeSVG()));
	}

	private String toDecoded(String content) {
		if (content.contains(BASE_64)) {
			return Browser.decodeBase64(content.split(BASE_64)[1]);
		}
		return content;
	}

	private void removeTags() {
		for (String tag: tagsToCut) {
			xml.removeTag(tag);
		}
	}

	private String encodeSVG() {
		return SVGUtil.fixAndEncode(xml.getContent());
	}
}

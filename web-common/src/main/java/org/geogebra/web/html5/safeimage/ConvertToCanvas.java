package org.geogebra.web.html5.safeimage;

import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.util.ArchiveEntry;

import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLImageElement;
import jsinterop.base.Js;

public class ConvertToCanvas implements ImagePreprocessor {
	private final HTMLCanvasElement canvas;

	public ConvertToCanvas() {
		canvas = (HTMLCanvasElement) DomGlobal.document.createElement("canvas");
	}

	@Override
	public boolean match(FileExtensions extension) {
		return FileExtensions.PNG.equals(extension)
				|| FileExtensions.JPG.equals(extension)
				|| FileExtensions.JPEG.equals(extension)
				|| FileExtensions.BMP.equals(extension);
	}

	@Override
	public void process(final ArchiveEntry imageFile, final SafeImageProvider provider) {
		HTMLImageElement image = Dom.createImage();

		image.addEventListener("load", (event) -> {
			drawImageToCanvas(image);
			String fileName = StringUtil.changeFileExtension(imageFile.getFileName(),
					FileExtensions.PNG);

			provider.onReady(new ArchiveEntry(fileName, canvas.toDataURL()));
		});

		image.src = imageFile.createUrl();
	}

	private void drawImageToCanvas(HTMLImageElement image)  {
		canvas.width = image.width;
		canvas.height = image.height;
		CanvasRenderingContext2D ctx = Js.uncheckedCast(canvas.getContext("2d"));
		ctx.drawImage(image, 0, 0);
	}
}

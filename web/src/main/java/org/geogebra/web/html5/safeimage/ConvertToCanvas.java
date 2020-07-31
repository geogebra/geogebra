package org.geogebra.web.html5.safeimage;

import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.util.ImageLoadCallback;
import org.geogebra.web.html5.util.ImageWrapper;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;

public class ConvertToCanvas implements ImagePreprocessor {
	private final Canvas canvas;

	public ConvertToCanvas() {
		canvas = Canvas.createIfSupported();
	}

	@Override
	public boolean match(FileExtensions extension) {
		return FileExtensions.PNG.equals(extension)
				|| FileExtensions.JPG.equals(extension)
				|| FileExtensions.JPEG.equals(extension)
				|| FileExtensions.BMP.equals(extension);
	}

	@Override
	public void process(final ImageFile imageFile, final SafeImageProvider provider) {
		final Image image = new Image();

		ImageWrapper.nativeon(image.getElement(), "load", new ImageLoadCallback() {
			@Override
			public void onLoad() {
				drawImageToCanvas(image);
				String fileName = StringUtil.changeFileExtension(imageFile.getFileName(),
						FileExtensions.PNG);

				provider.onReady(new ImageFile(fileName, canvas.toDataUrl()));
			}
		});

		image.setUrl(imageFile.getContent());
	}

	private void drawImageToCanvas(Image image)  {
		canvas.setCoordinateSpaceWidth(image.getWidth());
		canvas.setCoordinateSpaceHeight(image.getHeight());
		canvas.getContext2d().drawImage(ImageElement.as(image.getElement()), 0, 0);
	}
}

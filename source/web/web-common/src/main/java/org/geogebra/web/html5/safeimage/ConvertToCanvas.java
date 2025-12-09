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

import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.util.ArchiveEntry;

import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLImageElement;
import jsinterop.base.Js;

public class ConvertToCanvas implements ImagePreprocessor {
	private final HTMLCanvasElement canvas;
	private final int maxSize;
	private final boolean alwaysApply;

	/**
	 * @param maxSize size to resize to
	 * @param alwaysApply whether to convert also images &lt; maxSize
	 */
	public ConvertToCanvas(int maxSize, boolean alwaysApply) {
		canvas = (HTMLCanvasElement) DomGlobal.document.createElement("canvas");
		this.maxSize = maxSize;
		this.alwaysApply = alwaysApply;
	}

	@Override
	public boolean match(FileExtensions extension, int size) {
		return (matchExtension(extension) && alwaysApply)
				|| (maxSize > 0 && size > maxSize)
				|| !extension.isAllowedImage();
	}

	private boolean matchExtension(FileExtensions extension) {
		return FileExtensions.PNG.equals(extension)
				|| FileExtensions.JPG.equals(extension)
				|| FileExtensions.JPEG.equals(extension);
	}

	@Override
	public void process(final ArchiveEntry imageFile, final SafeImageProvider provider) {
		HTMLImageElement image = Dom.createImage();

		image.addEventListener("load", (event) -> {
			drawImageToCanvas(image, 1);
			String fileName = StringUtil.changeFileExtension(imageFile.getFileName(),
					FileExtensions.PNG);

			provider.onReady(new ArchiveEntry(fileName, canvas.toDataURL()));
		});

		image.src = imageFile.createUrl();
	}

	private void drawImageToCanvas(HTMLImageElement image, double scale)  {
		canvas.width = (int) (image.width * scale);
		canvas.height = (int) (image.height * scale);
		CanvasRenderingContext2D ctx = Js.uncheckedCast(canvas.getContext("2d"));
		ctx.drawImage(image, 0, 0, canvas.width, canvas.height);
		String data = canvas.toDataURL();
		double size = ArchiveEntry.dataUrlToBinarySizeKB(data);
		if (size > maxSize && maxSize > 0) {
			Log.debug("Size " + size + " exceeds limit " + maxSize);
			double factor = Math.min(Math.sqrt(maxSize / size), 0.9);
			ctx.clearRect(0, 0, canvas.width, canvas.height);
			drawImageToCanvas(image, scale * factor);
		}
	}
}

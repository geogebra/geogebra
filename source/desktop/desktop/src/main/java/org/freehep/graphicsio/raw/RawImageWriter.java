// Copyright 2003, FreeHEP
package org.freehep.graphicsio.raw;

import java.awt.image.RenderedImage;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;

import org.freehep.util.images.ImageUtilities;

/**
 * 
 * @version $Id: RawImageWriter.java,v 1.4 2009-08-17 21:44:45 murkle Exp $
 */
public class RawImageWriter extends ImageWriter {

	public RawImageWriter(RawImageWriterSpi originatingProvider) {
		super(originatingProvider);
	}

	@Override
	public void write(IIOMetadata streamMetadata, IIOImage image,
			ImageWriteParam param) throws IOException {
		if (image == null) {
			throw new IllegalArgumentException("image == null");
		}

		if (image.hasRaster()) {
			throw new UnsupportedOperationException("Cannot write rasters");
		}

		Object output = getOutput();
		if (output == null) {
			throw new IllegalStateException("output was not set");
		}

		if (param == null) {
			param = getDefaultWriteParam();
		}

		ImageOutputStream ios = (ImageOutputStream) output;
		RenderedImage ri = image.getRenderedImage();

		RawImageWriteParam rawParam = (RawImageWriteParam) param;
		byte[] bytes = ImageUtilities.getBytes(ri, rawParam.getBackground(),
				rawParam.getCode(), rawParam.getPad());
		ios.write(bytes);
		ios.close();
	}

	@Override
	public IIOMetadata convertStreamMetadata(IIOMetadata inData,
			ImageWriteParam param) {
		return null;
	}

	@Override
	public IIOMetadata convertImageMetadata(IIOMetadata inData,
			ImageTypeSpecifier imageType, ImageWriteParam param) {
		return null;
	}

	@Override
	public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType,
			ImageWriteParam param) {
		return null;
	}

	@Override
	public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
		return null;
	}

	@Override
	public ImageWriteParam getDefaultWriteParam() {
		return new RawImageWriteParam(getLocale());
	}
}

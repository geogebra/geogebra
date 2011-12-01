package geogebra.util;

import java.awt.image.BufferedImage;

import geogebra.common.awt.BufferedImageAdapter;


public class BufferedImageAdapterDesktop extends BufferedImage implements BufferedImageAdapter {

	public BufferedImageAdapterDesktop(int width, int height, int imageType) {
		super(width, height, imageType);
		// TODO Auto-generated constructor stub
	}

	public BufferedImageAdapterDesktop(BufferedImage image) {
		this(image.getWidth(), image.getHeight(), image.getType());
		setData(image.getData());
	}
}

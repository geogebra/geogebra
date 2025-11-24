package org.geogebra.common.awt;

public interface RenderingHints {

	public static final int KEY_ANTIALIASING = 1;
	public static final int VALUE_ANTIALIAS_ON = 1;

	public static final int KEY_RENDERING = 2;
	public static final int VALUE_RENDER_QUALITY = 2;

	public static final int KEY_TEXT_ANTIALIASING = 3;
	public static final int VALUE_TEXT_ANTIALIAS_ON = 3;

	public static final int KEY_INTERPOLATION = 4;
	public static final int VALUE_INTERPOLATION_BILINEAR = 4;
	public static final int VALUE_INTERPOLATION_NEAREST_NEIGHBOR = 5;
	public static final int VALUE_INTERPOLATION_BICUBIC = 6;
}
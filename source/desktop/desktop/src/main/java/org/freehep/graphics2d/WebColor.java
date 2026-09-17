// Copyright 2002, FreeHEP.
package org.freehep.graphics2d;

import java.awt.Color;

/**
 * WebColor which adheres to the web color set consisting of 216 equally spaced
 * colors, which include black and white. The spacing is 0x33, which makes the
 * smallest value 0x00 and the largest 0xFF. These colors are guaranteed to work
 * in browsers without dithering. WebColors are opaque.
 *
 * @author Mark Donszelmann
 * @version $Id: WebColor.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
@SuppressWarnings("serial")
public class WebColor extends Color {

	private static final int space = 0x33;

	private static final int space2 = space / 2;

	// redefine all java colors in terms of WebColors
	public static final WebColor white = new WebColor(Color.WHITE);

	public static final WebColor WHITE = white;

	public static final WebColor lightGray = new WebColor(Color.LIGHT_GRAY);

	public static final WebColor LIGHT_GRAY = lightGray;

	public static final WebColor gray = new WebColor(Color.GRAY);

	public static final WebColor GRAY = gray;

	public static final WebColor darkGray = new WebColor(Color.DARK_GRAY);

	public static final WebColor DARK_GRAY = darkGray;

	public static final WebColor black = new WebColor(Color.BLACK);

	public static final WebColor BLACK = black;

	public static final WebColor red = new WebColor(Color.RED);

	public static final WebColor RED = red;

	public static final WebColor pink = new WebColor(Color.PINK);

	public static final WebColor PINK = pink;

	public static final WebColor orange = new WebColor(Color.ORANGE);

	public static final WebColor ORANGE = orange;

	public static final WebColor yellow = new WebColor(Color.YELLOW);

	public static final WebColor YELLOW = yellow;

	public static final WebColor green = new WebColor(Color.GREEN);

	public static final WebColor GREEN = green;

	public static final WebColor magenta = new WebColor(Color.MAGENTA);

	public static final WebColor MAGENTA = magenta;

	public static final WebColor cyan = new WebColor(Color.CYAN);

	public static final WebColor CYAN = cyan;

	public static final WebColor blue = new WebColor(Color.BLUE);

	public static final WebColor BLUE = blue;

	public WebColor(int red, int green, int blue) {
		super(
				((red + space2) / space) * space,
				((green + space2) / space) * space,
				((blue + space2) / space) * space);
	}

	public WebColor(Color color) {
		this(color.getRed(), color.getGreen(), color.getBlue());
	}

	public WebColor(float red, float green, float blue) {
		this((int) (red * 255), (int) (green * 255), (int) (blue * 255));
	}

	public static WebColor create(Color color) {
		if (color == null) {
			return null;
		}
		if (color instanceof WebColor) {
			return (WebColor) color;
		}
		return new WebColor(color);
	}
}

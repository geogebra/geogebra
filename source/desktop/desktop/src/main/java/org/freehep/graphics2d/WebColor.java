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

	private final static int space = 0x33;

	private final static int space2 = space / 2;

	// redefine all java colors in terms of WebColors
	public final static WebColor white = new WebColor(Color.WHITE);

	public final static WebColor WHITE = white;

	public final static WebColor lightGray = new WebColor(Color.LIGHT_GRAY);

	public final static WebColor LIGHT_GRAY = lightGray;

	public final static WebColor gray = new WebColor(Color.GRAY);

	public final static WebColor GRAY = gray;

	public final static WebColor darkGray = new WebColor(Color.DARK_GRAY);

	public final static WebColor DARK_GRAY = darkGray;

	public final static WebColor black = new WebColor(Color.BLACK);

	public final static WebColor BLACK = black;

	public final static WebColor red = new WebColor(Color.RED);

	public final static WebColor RED = red;

	public final static WebColor pink = new WebColor(Color.PINK);

	public final static WebColor PINK = pink;

	public final static WebColor orange = new WebColor(Color.ORANGE);

	public final static WebColor ORANGE = orange;

	public final static WebColor yellow = new WebColor(Color.YELLOW);

	public final static WebColor YELLOW = yellow;

	public final static WebColor green = new WebColor(Color.GREEN);

	public final static WebColor GREEN = green;

	public final static WebColor magenta = new WebColor(Color.MAGENTA);

	public final static WebColor MAGENTA = magenta;

	public final static WebColor cyan = new WebColor(Color.CYAN);

	public final static WebColor CYAN = cyan;

	public final static WebColor blue = new WebColor(Color.BLUE);

	public final static WebColor BLUE = blue;

	public WebColor(int red, int green, int blue) {
		super(((red + space2) / space) * space,
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

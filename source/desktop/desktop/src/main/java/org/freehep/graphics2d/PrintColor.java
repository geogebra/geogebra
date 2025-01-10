// Copyright 2002, FreeHEP.
package org.freehep.graphics2d;

import java.awt.Color;
import java.awt.color.ColorSpace;

/**
 * Print color for printing and display in color, grayscale and black/white.
 * 
 * @author Mark Donszelmann
 * @version $Id: PrintColor.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
@SuppressWarnings("serial")
public class PrintColor extends Color {

	public static final int COLOR = 0;

	public static final int GRAYSCALE = 1;

	public static final int BLACK_AND_WHITE = 2;

	final private static Color[] defaultColors = { Color.RED, Color.GREEN,
			Color.BLUE, Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.ORANGE,
			Color.PINK, Color.WHITE, Color.LIGHT_GRAY, Color.GRAY,
			Color.DARK_GRAY, Color.BLACK };

	protected float asGray;

	protected boolean asBlack;

	private static void testColorValueRange(float asGray) {
		boolean rangeError = false;
		String badComponentString = "";
		if (asGray < 0.0f || asGray > 1.0f) {
			rangeError = true;
			badComponentString = badComponentString + " asGray";
		}
		if (rangeError == true) {
			throw new IllegalArgumentException(
					"PrintColor parameter outside of expected range:"
							+ badComponentString);
		}
	}

	public PrintColor(float red, float green, float blue, float asGray,
			boolean asBlack) {
		this(red, green, blue, 1.0f, asGray, asBlack);
	}

	public PrintColor(float red, float green, float blue, float alpha,
			float asGray, boolean asBlack) {
		super(red, green, blue, alpha);
		this.asGray = asGray;
		this.asBlack = asBlack;
		testColorValueRange(asGray);
	}

	public PrintColor(Color color, float asGray, boolean asBlack) {
		super(color.getRed(), color.getGreen(), color.getBlue(),
				color.getAlpha());
		this.asGray = asGray;
		this.asBlack = asBlack;
		testColorValueRange(asGray);
	}

	public float getAsGray() {
		return asGray;
	}

	public boolean getAsBlack() {
		return asBlack;
	}

	public PrintColor getColor(int mode) {
		// FIXME does not handle invisibility
		switch (mode) {
		case COLOR:
			return this;
		case GRAYSCALE:
			return new PrintColor(getAsGray(), getAsGray(), getAsGray(),
					getAlpha() / 255.0f, getAsGray(), getAsBlack());
		case BLACK_AND_WHITE:
			if (getAsBlack()) {
				return new PrintColor(Color.black, getAsGray(), getAsBlack());
			}
			return new PrintColor(Color.white, getAsGray(), getAsBlack());
		default:
			throw new IllegalArgumentException(
					"ColorMode on PrintColor out of range: " + mode);
		}
	}

	public static PrintColor createPrintColor(Color color) {
		if (color instanceof PrintColor) {
			return (PrintColor) color;
		}

		// convert a awt.Color to some reasonable PrintColor.
		// pure white converts to black, and vice versa.
		float[] gray = ColorSpace.getInstance(ColorSpace.CS_GRAY)
				.fromRGB(color.getRGBComponents(null));
		if (gray[0] == 0.0f) {
			gray[0] = 1.0f;
		} else if (gray[0] == 1.0f) {
			gray[0] = 0.0f;
		}
		return new PrintColor(color, gray[0], !color.equals(Color.black));
	}

	/**
	 * @return a color from the standard java colors
	 */
	public static Color getDefaultColor(int index) {
		if ((index < 0) || (index >= defaultColors.length)) {
			throw new IllegalArgumentException(
					"PrintColor.getDefaultColor index outside of expected range: "
							+ index);
		}
		return createPrintColor(defaultColors[index]);
	}

	// FIXME, should return PrintColor
	public static Color mixColor(Color c1, Color c2) {
		int red = (c1.getRed() + c2.getRed()) / 2;
		int green = (c1.getGreen() + c2.getGreen()) / 2;
		int blue = (c1.getBlue() + c2.getBlue()) / 2;
		return new Color(red, green, blue);
	}

	@Override
	public int hashCode() {
		// FIXME could make something better here
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && obj instanceof PrintColor
				&& ((PrintColor) obj).asGray == this.asGray
				&& ((PrintColor) obj).asBlack == this.asBlack;
	}

	@Override
	public String toString() {
		return super.toString() + ", asGray: " + asGray + ", asBlack: "
				+ asBlack;
	}

	public static PrintColor invert(Color color) {
		PrintColor printColor = createPrintColor(color);
		return new PrintColor(new Color(printColor.getRGB() ^ 0x00808080),
				(printColor.getAsGray() + 0.5f) % 1.0f,
				!printColor.getAsBlack());
	}
}

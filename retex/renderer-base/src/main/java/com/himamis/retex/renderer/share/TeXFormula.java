/* TeXFormula.java
 * =========================================================================
 * This file is originally part of the JMathTeX Library - http://jmathtex.sourceforge.net
 *
 * Copyright (C) 2004-2007 Universiteit Gent
 * Copyright (C) 2009-2018 DENIZET Calixte
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * Linking this library statically or dynamically with other modules
 * is making a combined work based on this library. Thus, the terms
 * and conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce
 * an executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under terms
 * of your choice, provided that you also meet, for each linked independent
 * module, the terms and conditions of the license of that module.
 * An independent module is a module which is not derived from or based
 * on this library. If you modify this library, you may extend this exception
 * to your version of the library, but you are not obliged to do so.
 * If you do not wish to do so, delete this exception statement from your
 * version.
 *
 */

/* Modified by Calixte Denizet */

package com.himamis.retex.renderer.share;

import java.util.Map;

import com.himamis.retex.renderer.share.exception.ParseException;
import com.himamis.retex.renderer.share.platform.Graphics;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.Image;
import com.himamis.retex.renderer.share.platform.graphics.Insets;

/**
 * Represents a logical mathematical formula that will be displayed (by creating
 * a {@link TeXIcon} from it and painting it) using algorithms that are based on
 * the TeX algorithms.
 * <p>
 * These formula's can be built using the built-in primitive TeX parser (methods
 * with String arguments) or using other TeXFormula objects. Most methods have
 * (an) equivalent(s) where one or more TeXFormula arguments are replaced with
 * String arguments. These are just shorter notations, because all they do is
 * parse the string(s) to TeXFormula's and call an equivalent method with (a)
 * TeXFormula argument(s). Most methods also come in 2 variants. One kind will
 * use this TeXFormula to build another mathematical construction and then
 * change this object to represent the newly build construction. The other kind
 * will only use other TeXFormula's (or parse strings), build a mathematical
 * construction with them and insert this newly build construction at the end of
 * this TeXFormula. Because all the provided methods return a pointer to this
 * (modified) TeXFormula (except for the createTeXIcon method that returns a
 * TeXIcon pointer), method chaining is also possible.
 * <p>
 * <b> Important: All the provided methods modify this TeXFormula object, but
 * all the TeXFormula arguments of these methods will remain unchanged and
 * independent of this TeXFormula object!</b>
 */
public class TeXFormula {

	public static final String VERSION = "1.0.3";

	// TODO remove after jlm2 merge (check MathFieldA/I)
	public static final int SERIF = TeXFont.SERIF;
	public static final int SANSSERIF = TeXFont.SANSSERIF;
	public static final int BOLD = TeXFont.BOLD;
	public static final int ITALIC = TeXFont.ITALIC;
	public static final int ROMAN = TeXFont.ROMAN;
	public static final int TYPEWRITER = TeXFont.TYPEWRITER;

	// point-to-pixel conversion
	public static double PIXELS_PER_POINT = 1.;

	// font scale for deriving
	public static double FONT_SCALE_FACTOR = 100.;

	// for comparing doubles with 0
	protected static final double PREC = 0.0000001;

	private TeXParser parser;

	static {
		// setDefaultDPI();
	}

	/**
	 * Set the DPI of target
	 * 
	 * @param dpi
	 *            the target DPI
	 */
	public static void setDPITarget(double dpi) {
		PIXELS_PER_POINT = dpi / 72.;
	}

	/**
	 * Set the default target DPI to the screen dpi (only if we're in
	 * non-headless mode)
	 */
	// public static void setDefaultDPI() {
	// if (!GraphicsEnvironment.isHeadless()) {
	// setDPITarget(
	// (double) Toolkit.getDefaultToolkit().getScreenResolution());
	// }
	// }

	// the root atom of the "atom tree" that represents the formula
	public Atom root = null;

	// the current text style
	private int textStyle = TextStyle.NONE;

	public boolean isColored = false;

	/**
	 * Creates an empty TeXFormula.
	 *
	 */
	public TeXFormula() {
		parser = new TeXParser(false, "");
	}

	/**
	 * Creates a new TeXFormula by parsing the given string (using a primitive
	 * TeX parser).
	 *
	 * @param s
	 *            the string to be parsed
	 * @throws ParseException
	 *             if the string could not be parsed correctly
	 */
	TeXFormula(final String s, final boolean isPartial) throws ParseException {
		parser = new TeXParser(isPartial, s);
		run();
	}

	public TeXFormula(final String s, final Map<String, String> xmlMap)
			throws ParseException {
		parser = new TeXParser(false, s);
		parser.setXMLMap(xmlMap);
		run();
	}

	/**
	 * Creates a new TeXFormula by parsing the given string (using a primitive
	 * TeX parser).
	 *
	 * @param s
	 *            the string to be parsed
	 * @throws ParseException
	 *             if the string could not be parsed correctly
	 */
	public TeXFormula(final String s) throws ParseException {
		this(s, false);
	}

	public TeXFormula(final String s, final String textStyle)
			throws ParseException {
		this.textStyle = TextStyle.getStyle(textStyle);
		parser = new TeXParser(false, s);
		run();
	}

	protected void run() {
		parser.parse();
		root = parser.get();
	}

	public static TeXFormula getAsText(final String text,
			final TeXConstants.Align alignment) throws ParseException {
		final TeXFormula formula = new TeXFormula();
		if (text == null || text.isEmpty()) {
			formula.root = EmptyAtom.get();
			return formula;
		}

		final String[] arr = text.split("\n|\\\\\\\\|\\\\cr");
		final ArrayOfAtoms atoms = new ArrayOfAtoms();
		final TeXParser parser = new TeXParser(false, arr[0]);
		parser.parse();
		atoms.add(new RomanAtom(parser.get()));
		for (int i = 1; i < arr.length; ++i) {
			parser.reset(arr[i]);
			parser.parse();
			atoms.add(new AlignedAtom(new RomanAtom(parser.get()), alignment));
			atoms.add(EnvArray.RowSep.get());
		}
		atoms.checkDimensions();
		formula.root = new MultlineAtom(atoms, MultlineAtom.MULTLINE);

		return formula;
	}

	/**
	 * @param a
	 *            formula
	 * @return a partial TeXFormula containing the valid part of formula
	 */
	public static TeXFormula getPartialTeXFormula(final String formula) {
		final TeXFormula f = new TeXFormula();
		if (formula == null) {
			f.root = EmptyAtom.get();
			return f;
		}

		final TeXParser parser = new TeXParser(true, formula);
		try {
			parser.parse();
		} catch (Exception e) {
		}
		f.parser = parser;
		f.root = parser.get();

		return f;
	}

	/**
	 * @param b
	 *            true if the fonts should be registered (Java 1.6 only) to be
	 *            used with FOP.
	 */
	// public static void registerFonts(boolean b) {
	// FontLoader.registerFonts(b);
	// }

	/**
	 * Change the text of the TeXFormula and regenerate the root
	 *
	 * @param ltx
	 *            the latex formula
	 */
	public void setLaTeX(final String ltx) throws ParseException {
		parser.reset(ltx);
		if (ltx != null && ltx.length() != 0) {
			run();
		}
	}

	/**
	 * Centers the current TeXformula vertically on the axis (defined by the
	 * parameter "axisheight" in the resource "TeXFont.xml".
	 *
	 * @return the modified TeXFormula
	 */
	public TeXFormula centerOnAxis() {
		root = new VCenteredAtom(root);
		return this;
	}

	public Atom getAtom() {
		return root;
	}

	/*
	 * Convert this TeXFormula into a box, starting form the given style
	 */
	public Box createBox(TeXEnvironment env) {
		if (root == null) {
			return StrutBox.getEmpty();
		} else {
			return root.createBox(env);
		}
	}

	private TeXFont createFont(final double size, final int type) {
		final TeXFont dtf = new TeXFont(size);
		if (type == TeXFont.SERIF) {
			dtf.setSs(false);
		}
		if ((type & TeXFont.ROMAN) != 0) {
			dtf.setRoman(true);
		}
		if ((type & TeXFont.TYPEWRITER) != 0) {
			dtf.setTt(true);
		}
		if ((type & TeXFont.SANSSERIF) != 0) {
			dtf.setSs(true);
		}
		if ((type & TeXFont.ITALIC) != 0) {
			dtf.setIt(true);
		}
		if ((type & TeXFont.BOLD) != 0) {
			dtf.setBold(true);
		}

		return dtf;
	}

	/**
	 * Apply the Builder pattern instead of using the createTeXIcon(...)
	 * factories
	 * 
	 * @author Felix Natter
	 *
	 */
	public class TeXIconBuilder {
		private Integer style;
		private Double size;
		private Integer type;
		private Color fgcolor;
		private boolean trueValues = false;
		private TeXConstants.Align align;

		/**
		 * Specify the style for rendering the given TeXFormula
		 * 
		 * @param style
		 *            the style
		 * @return the builder, used for chaining
		 */
		public TeXIconBuilder setStyle(final int style) {
			this.style = style;
			return this;
		}

		/**
		 * Specify the font size for rendering the given TeXFormula
		 * 
		 * @param size
		 *            the size
		 * @return the builder, used for chaining
		 */
		public TeXIconBuilder setSize(final double size) {
			this.size = size;
			return this;
		}

		/**
		 * Specify the font type for rendering the given TeXFormula
		 * 
		 * @param type
		 *            the font type
		 * @return the builder, used for chaining
		 */
		public TeXIconBuilder setType(final int type) {
			this.type = type;
			return this;
		}

		/**
		 * Specify the background color for rendering the given TeXFormula
		 * 
		 * @param fgcolor
		 *            the foreground color
		 * @return the builder, used for chaining
		 */
		public TeXIconBuilder setFGColor(final Color fgcolor) {
			this.fgcolor = fgcolor;
			return this;
		}

		/**
		 * Specify the "true values" parameter for rendering the given
		 * TeXFormula
		 * 
		 * @param trueValues
		 *            the "true values" value
		 * @return the builder, used for chaining
		 */
		public TeXIconBuilder setTrueValues(final boolean trueValues) {
			this.trueValues = trueValues;
			return this;
		}

		/**
		 * Specify the width of the formula (may be exact or maximum width, see
		 * {@link #setIsMaxWidth(boolean)})
		 * 
		 * @param widthUnit
		 *            the width unit
		 * @param textWidth
		 *            the width
		 * @param align
		 *            the alignment
		 * @return the builder, used for chaining
		 */
		public TeXIconBuilder setAlign(final TeXConstants.Align align) {
			this.align = align;
			trueValues = true;
			return this;
		}

		/**
		 * Create a TeXIcon from the information gathered by the (chained)
		 * setXXX() methods. (see Builder pattern)
		 * 
		 * @return the TeXIcon
		 */
		public TeXIcon build() {
			if (style == null) {
				throw new IllegalStateException(
						"A style is required. Use setStyle()");
			}
			if (size == null) {
				throw new IllegalStateException(
						"A size is required. Use setStyle()");
			}
			TeXFont font = (type == null) ? new TeXFont(size)
					: createFont(size, type);
			TeXEnvironment te = new TeXEnvironment(style, font, textStyle);

			Box box = createBox(te);
			TeXIcon ti;
			final double textwidth = te.lengthSettings().getLength("textwidth", te);
			if (!Double.isInfinite(textwidth) && !Double.isNaN(textwidth)) {
				final double baselineskip = te.lengthSettings().getLength("baselineskip",
						te);
				box = BreakFormula.split(box, textwidth, baselineskip, align);
			}
			ti = new TeXIcon(box, size, trueValues);

			if (fgcolor != null) {
				ti.setForeground(fgcolor);
			}
			ti.isColored = te.isColored;
			return ti;
		}
	}

	/**
	 * Creates a TeXIcon from this TeXFormula using the default TeXFont in the
	 * given point size and starting from the given TeX style. If the given
	 * integer value does not represent a valid TeX style, the default style
	 * TeXConstants.STYLE_DISPLAY will be used.
	 *
	 * @param style
	 *            a TeX style constant (from {@link TeXConstants}) to start from
	 * @param size
	 *            the default TeXFont's point size
	 * @return the created TeXIcon
	 */
	public TeXIcon createTeXIcon(int style, double size) {
		return new TeXIconBuilder().setStyle(style).setSize(size).build();
	}

	public TeXIcon createTeXIcon(int style, double size, int type) {
		return new TeXIconBuilder().setStyle(style).setSize(size).setType(type)
				.build();
	}

	public TeXIcon createTeXIcon(int style, double size, int type,
			Color fgcolor) {
		return new TeXIconBuilder().setStyle(style).setSize(size).setType(type)
				.setFGColor(fgcolor).build();
	}

	public TeXIcon createTeXIcon(int style, double size, boolean trueValues) {
		return new TeXIconBuilder().setStyle(style).setSize(size)
				.setTrueValues(trueValues).build();
	}

	public TeXIcon createTeXIcon(int style, double size,
			TeXConstants.Align align) {
		return createTeXIcon(style, size, 0, align);
	}

	public TeXIcon createTeXIcon(int style, double size, int type,
			TeXConstants.Align align) {
		return new TeXIconBuilder().setStyle(style).setSize(size).setType(type)
				.setAlign(align).build();
	}

	// public void createImage(String format, int style, double size, String
	// out,
	// Color bg, Color fg, boolean transparency) {
	// TeXIcon icon = createTeXIcon(style, size);
	// icon.setInsets(new Insets(1, 1, 1, 1));
	// int w = icon.getIconWidth(), h = icon.getIconHeight();
	//
	// Image image = new Graphics().createImage(w, h, transparency
	// ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
	// Graphics2DInterface g2 = image.createGraphics2D();
	// if (bg != null && !transparency) {
	// g2.setColor(bg);
	// g2.fillRect(0, 0, w, h);
	// }
	//
	// icon.setForeground(fg);
	// icon.paintIcon(null, g2, 0, 0);
	// try {
	// FileImageOutputStream imout = new FileImageOutputStream(
	// new File(out));
	// ImageIO.write(image, format, imout);
	// imout.flush();
	// imout.close();
	// } catch (IOException ex) {
	// System.err.println("I/O error : Cannot generate " + out);
	// }
	//
	// g2.dispose();
	// }
	//
	// public void createPNG(int style, double size, String out, Color bg,
	// Color fg) {
	// createImage("png", style, size, out, bg, fg, bg == null);
	// }
	//
	// public void createGIF(int style, double size, String out, Color bg,
	// Color fg) {
	// createImage("gif", style, size, out, bg, fg, bg == null);
	// }
	//
	// public void createJPEG(int style, double size, String out, Color bg,
	// Color fg) {
	// // There is a bug when a BufferedImage has a component alpha so we
	// // disable it
	// createImage("jpeg", style, size, out, bg, fg, false);
	// }

	/**
	 * @param formula
	 *            the formula
	 * @param style
	 *            the style
	 * @param size
	 *            the size
	 * @param transparency,
	 *            if true the background is transparent
	 * @return the generated image
	 */
	public static Image createBufferedImage(String formula, int style,
			double size, Color fg, Color bg) throws ParseException {
		TeXFormula f = new TeXFormula(formula);
		TeXIcon icon = f.createTeXIcon(style, size);
		icon.setInsets(new Insets(2, 2, 2, 2));
		int w = icon.getIconWidth(), h = icon.getIconHeight();

		Image image = new Graphics().createImage(w, h,
				bg == null ? Image.TYPE_INT_ARGB : Image.TYPE_INT_RGB);
		Graphics2DInterface g2 = image.createGraphics2D();
		if (bg != null) {
			g2.setColor(bg);
			g2.fillRect(0, 0, w, h);
		}

		icon.setForeground(fg == null ? Colors.BLACK : fg);
		icon.paintIcon(null, g2, 0, 0);
		g2.dispose();

		return image;
	}

	/**
	 * @param formula
	 *            the formula
	 * @param style
	 *            the style
	 * @param size
	 *            the size
	 * @param transparency,
	 *            if true the background is transparent
	 * @return the generated image
	 */
	public Image createBufferedImage(int style, double size, Color fg, Color bg)
			throws ParseException {
		TeXIcon icon = createTeXIcon(style, size);
		icon.setInsets(new Insets(2, 2, 2, 2));
		int w = icon.getIconWidth(), h = icon.getIconHeight();

		Image image = new Graphics().createImage(w, h,
				bg == null ? Image.TYPE_INT_ARGB : Image.TYPE_INT_RGB);
		Graphics2DInterface g2 = image.createGraphics2D();
		if (bg != null) {
			g2.setColor(bg);
			g2.fillRect(0, 0, w, h);
		}

		icon.setForeground(fg == null ? Colors.BLACK : fg);
		icon.paintIcon(null, g2, 0, 0);
		g2.dispose();

		return image;
	}

}

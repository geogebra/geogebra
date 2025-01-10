/* TeXEnvironment.java
 * =========================================================================
 * This file is originally part of the JMathTeX Library - http://jmathtex.sourceforge.net
 *
 * Copyright (C) 2004-2007 Universiteit Gent
 * Copyright (C) 2009 DENIZET Calixte
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

import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.graphics.Color;

/**
 * Contains the used TeXFont-object, color settings and the current style in
 * which a formula must be drawn. It's used in the createBox-methods. Contains
 * methods that apply the style changing rules for subformula's.
 */
public class TeXEnvironment {

	// colors
	private Color background;
	private Color color;

	// current style
	private int style;

	// TeXFont used
	private TeXFont tf;

	// Java Font to use
	private Font javaFont;

	// last used font
	private FontInfo lastFont;

	private int textStyle;

	private boolean smallCap;
	private double scaleFactor = 1.;
	public boolean isColored = false;

	private TeXLengthSettings lengthSettings;

	public TeXEnvironment(int style, TeXFont tf, int textStyle) {
		this.style = style;
		this.tf = tf;
		this.textStyle = textStyle;
		this.lengthSettings = new TeXLengthSettings();
	}

	private TeXEnvironment(int style, double scaleFactor, TeXFont tf, Color bg,
			Color c, int textStyle, boolean smallCap, Font javaFont,
			TeXLengthSettings lengthSettings) {
		this.style = style;
		this.scaleFactor = scaleFactor;
		this.tf = tf;
		this.background = bg;
		this.color = c;
		this.textStyle = textStyle;
		this.smallCap = smallCap;
		this.javaFont = javaFont;
		this.lengthSettings = lengthSettings;
	}

	public void setScaleFactor(double f) {
		scaleFactor = f;
	}

	public double getScaleFactor() {
		return scaleFactor;
	}

	protected TeXEnvironment copy() {
		return new TeXEnvironment(style, scaleFactor, tf, background, color,
				textStyle, smallCap, javaFont, lengthSettings);
	}

	protected TeXEnvironment copy(TeXFont tf) {
		return new TeXEnvironment(style, scaleFactor, tf,
				background, color, textStyle, smallCap, javaFont, lengthSettings);
	}

	/**
	 * @return a copy of the environment, but in a cramped style.
	 */
	public TeXEnvironment crampStyle() {
		TeXEnvironment s = copy();
		s.style = style | 1;
		return s;
	}

	/**
	 *
	 * @return a copy of the environment, but in denominator style.
	 */
	public TeXEnvironment denomStyle() {
		TeXEnvironment s = copy();
		s.style = style <= 3 ? ((style & 2) + 3) : 7;
		return s;
	}

	/**
	 *
	 * @return a copy of the environment, but in numerator style.
	 */
	public TeXEnvironment numStyle() {
		TeXEnvironment s = copy();
		s.style = (style <= 5 ? 2 : 0) + style;
		return s;
	}

	/**
	 *
	 * @return a copy of the environment, but in subscript style.
	 */
	public TeXEnvironment subStyle() {
		TeXEnvironment s = copy();
		s.style = style <= 3 ? 5 : 7;
		return s;
	}

	/**
	 *
	 * @return a copy of the environment, but in superscript style.
	 */
	public TeXEnvironment supStyle() {
		TeXEnvironment s = copy();
		s.style = (style <= 3 ? 4 : 6) + (style & 1);
		return s;
	}

	/**
	 *
	 * @return the background color setting
	 */
	public Color getBackground() {
		return background;
	}

	/**
	 *
	 * @return the foreground color setting
	 */
	public Color getColor() {
		return color;
	}

	/**
	 *
	 * @return the point size of the TeXFont
	 */
	public double getSize() {
		return tf.getSize();
	}

	/**
	 *
	 * @return the current style
	 */
	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	/**
	 * @return the current textStyle
	 */
	public int getTextStyle() {
		return textStyle;
	}

	public void setTextStyle(int textStyle) {
		this.textStyle = textStyle;
	}

	/**
	 * @return the current java Font
	 */
	public Font getJavaFont() {
		return javaFont;
	}

	public void setJavaFont(Font javaFont) {
		this.javaFont = javaFont;
	}

	/**
	 * @return the current textStyle
	 */
	public boolean getSmallCap() {
		return smallCap;
	}

	public void setSmallCap(boolean smallCap) {
		this.smallCap = smallCap;
	}

	/**
	 *
	 * @return the TeXFont to be used
	 */
	public TeXFont getTeXFont() {
		return tf;
	}

	/**
	 * Resets the color settings.
	 *
	 */
	public void resetColors() {
		color = null;
		background = null;
	}

	/**
	 *
	 * @return a copy of the environment, but with the style changed for roots
	 */
	public TeXEnvironment rootStyle() {
		TeXEnvironment s = copy();
		s.style = TeXConstants.STYLE_SCRIPT_SCRIPT;
		return s;
	}

	/**
	 *
	 * @param c
	 *            the background color to be set
	 */
	public void setBackground(Color c) {
		background = c;
	}

	/**
	 *
	 * @param c
	 *            the foreground color to be set
	 */
	public void setColor(Color c) {
		color = c;
	}

	public double getSpace() {
		return tf.getSpace(style) * tf.getScaleFactor();
	}

	public void setLastFont(FontInfo font) {
		lastFont = font;
	}

	public FontInfo getLastFont() {
		// if there was no last font (whitespace boxes only), use default "mu
		// font"
		return lastFont == null ? TeXFont.MUFONT : lastFont;
	}

	public TeXLengthSettings lengthSettings() {
		return lengthSettings;
	}
}
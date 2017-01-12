/*
 * SVG Salamander
 * Copyright (c) 2004, Mark McKay
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 *   - Redistributions of source code must retain the above 
 *     copyright notice, this list of conditions and the following
 *     disclaimer.
 *   - Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials 
 *     provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE. 
 * 
 * Mark McKay can be contacted at mark@kitfox.com.  Salamander and other
 * projects can be found at http://www.kitfox.com
 *
 * Created on January 26, 2004, 1:56 AM
 */
package com.kitfox.svg;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kitfox.svg.xml.StyleAttribute;

//import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;
/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class Text extends ShapeElement {
	public static final String TAG_NAME = "text";

	float x = 0;
	float y = 0;
	AffineTransform transform = null;
	String fontFamily;
	float fontSize;
	// List of strings and tspans containing the content of this node
	LinkedList content = new LinkedList();
	Shape textShape;
	public static final int TXAN_START = 0;
	public static final int TXAN_MIDDLE = 1;
	public static final int TXAN_END = 2;
	int textAnchor = TXAN_START;
	public static final int TXST_NORMAL = 0;
	public static final int TXST_ITALIC = 1;
	public static final int TXST_OBLIQUE = 2;
	int fontStyle;
	public static final int TXWE_NORMAL = 0;
	public static final int TXWE_BOLD = 1;
	public static final int TXWE_BOLDER = 2;
	public static final int TXWE_LIGHTER = 3;
	public static final int TXWE_100 = 4;
	public static final int TXWE_200 = 5;
	public static final int TXWE_300 = 6;
	public static final int TXWE_400 = 7;
	public static final int TXWE_500 = 8;
	public static final int TXWE_600 = 9;
	public static final int TXWE_700 = 10;
	public static final int TXWE_800 = 11;
	public static final int TXWE_900 = 12;
	int fontWeight;

	/**
	 * Creates a new instance of Stop
	 */
	public Text() {
	}

	@Override
	public String getTagName() {
		return TAG_NAME;
	}

	public void appendText(String text) {
		content.addLast(text);
	}

	public void appendTspan(Tspan tspan) throws SVGElementException {
		super.loaderAddChild(null, tspan);
		content.addLast(tspan);
	}

	/**
	 * Discard cached information
	 */
	public void rebuild() throws SVGException {
		build();
	}

	public java.util.List getContent() {
		return content;
	}

	/**
	 * Called after the start element but before the end element to indicate
	 * each child tag that has been processed
	 */
	@Override
	public void loaderAddChild(SVGLoaderHelper helper, SVGElement child)
			throws SVGElementException {
		super.loaderAddChild(helper, child);

		content.addLast(child);
	}

	/**
	 * Called during load process to add text scanned within a tag
	 */
	@Override
	public void loaderAddText(SVGLoaderHelper helper, String text) {
		Matcher matchWs = Pattern.compile("\\s*").matcher(text);
		if (!matchWs.matches()) {
			content.addLast(text);
		}
	}

	@Override
	public void build() throws SVGException {
		super.build();

		StyleAttribute sty = new StyleAttribute();

		if (getPres(sty.setName("x"))) {
			x = sty.getFloatValueWithUnits();
		}

		if (getPres(sty.setName("y"))) {
			y = sty.getFloatValueWithUnits();
		}

		if (getStyle(sty.setName("font-family"))) {
			fontFamily = sty.getStringValue();
		} else {
			fontFamily = "Sans Serif";
		}

		if (getStyle(sty.setName("font-size"))) {
			fontSize = sty.getFloatValueWithUnits();
		} else {
			fontSize = 12f;
		}

		if (getStyle(sty.setName("font-style"))) {
			String s = sty.getStringValue();
			if ("normal".equals(s)) {
				fontStyle = TXST_NORMAL;
			} else if ("italic".equals(s)) {
				fontStyle = TXST_ITALIC;
			} else if ("oblique".equals(s)) {
				fontStyle = TXST_OBLIQUE;
			}
		} else {
			fontStyle = TXST_NORMAL;
		}

		if (getStyle(sty.setName("font-weight"))) {
			String s = sty.getStringValue();
			if ("normal".equals(s)) {
				fontWeight = TXWE_NORMAL;
			} else if ("bold".equals(s)) {
				fontWeight = TXWE_BOLD;
			}
		} else {
			fontWeight = TXWE_NORMAL;
		}

		if (getStyle(sty.setName("text-anchor"))) {
			String s = sty.getStringValue();
			if (s.equals("middle")) {
				textAnchor = TXAN_MIDDLE;
			} else if (s.equals("end")) {
				textAnchor = TXAN_END;
			} else {
				textAnchor = TXAN_START;
			}
		} else {
			textAnchor = TXAN_START;
		}

		// text anchor
		// text-decoration
		// text-rendering

		buildFont();
	}

	protected void buildFont() throws SVGException {
		int style;
		switch (fontStyle) {
		case TXST_ITALIC:
			style = java.awt.Font.ITALIC;
			break;
		default:
			style = java.awt.Font.PLAIN;
			break;
		}

		int weight;
		switch (fontWeight) {
		case TXWE_BOLD:
		case TXWE_BOLDER:
			weight = java.awt.Font.BOLD;
			break;
		default:
			weight = java.awt.Font.PLAIN;
			break;
		}

		// Get font
		Font font = diagram.getUniverse().getFont(fontFamily);
		if (font == null) {
			// System.err.println("Could not load font");

			java.awt.Font sysFont = new java.awt.Font(fontFamily,
					style | weight, (int) fontSize);
			buildSysFont(sysFont);
			return;
		}

		// font = new java.awt.Font(font.getFamily(), style | weight,
		// font.getSize());

		// Area textArea = new Area();
		GeneralPath textPath = new GeneralPath();
		textShape = textPath;

		float cursorX = x, cursorY = y;

		FontFace fontFace = font.getFontFace();
		// int unitsPerEm = fontFace.getUnitsPerEm();
		int ascent = fontFace.getAscent();
		float fontScale = fontSize / ascent;

		// AffineTransform oldXform = g.getTransform();
		AffineTransform xform = new AffineTransform();

		for (Iterator it = content.iterator(); it.hasNext();) {
			Object obj = it.next();

			if (obj instanceof String) {
				String text = (String) obj;
				if (text != null) {
					text = text.trim();
				}

				strokeWidthScalar = 1f / fontScale;

				for (int i = 0; i < text.length(); i++) {
					xform.setToIdentity();
					xform.setToTranslation(cursorX, cursorY);
					xform.scale(fontScale, fontScale);
					// g.transform(xform);

					String unicode = text.substring(i, i + 1);
					MissingGlyph glyph = font.getGlyph(unicode);

					Shape path = glyph.getPath();
					if (path != null) {
						path = xform.createTransformedShape(path);
						textPath.append(path, false);
					}
					// else glyph.render(g);

					cursorX += fontScale * glyph.getHorizAdvX();

					// g.setTransform(oldXform);
				}

				strokeWidthScalar = 1f;
			} else if (obj instanceof Tspan) {
				Tspan tspan = (Tspan) obj;

				xform.setToIdentity();
				xform.setToTranslation(cursorX, cursorY);
				xform.scale(fontScale, fontScale);
				// tspan.setCursorX(cursorX);
				// tspan.setCursorY(cursorY);

				Shape tspanShape = tspan.getShape();
				tspanShape = xform.createTransformedShape(tspanShape);
				textPath.append(tspanShape, false);
				// tspan.render(g);
				// cursorX = tspan.getCursorX();
				// cursorY = tspan.getCursorY();
			}

		}

		switch (textAnchor) {
		case TXAN_MIDDLE: {
			AffineTransform at = new AffineTransform();
			at.translate(-textPath.getBounds().getWidth() / 2, 0);
			textPath.transform(at);
			break;
		}
		case TXAN_END: {
			AffineTransform at = new AffineTransform();
			at.translate(-textPath.getBounds().getWidth(), 0);
			textPath.transform(at);
			break;
		}
		}
	}

	private void buildSysFont(java.awt.Font font) throws SVGException {
		GeneralPath textPath = new GeneralPath();
		textShape = textPath;

		float cursorX = x, cursorY = y;

		// FontMetrics fm = g.getFontMetrics(font);
		FontRenderContext frc = new FontRenderContext(null, true, true);

		// FontFace fontFace = font.getFontFace();
		// int unitsPerEm = fontFace.getUnitsPerEm();
		// int ascent = fm.getAscent();
		// float fontScale = fontSize / (float)ascent;

		// AffineTransform oldXform = g.getTransform();
		// AffineTransform xform = new AffineTransform();

		for (Iterator it = content.iterator(); it.hasNext();) {
			Object obj = it.next();

			if (obj instanceof String) {
				String text = (String) obj;
				text = text.trim();

				Shape textShape = font.createGlyphVector(frc, text)
						.getOutline(cursorX, cursorY);
				textPath.append(textShape, false);
				// renderShape(g, textShape);
				// g.drawString(text, cursorX, cursorY);

				Rectangle2D rect = font.getStringBounds(text, frc);
				cursorX += (float) rect.getWidth();
			} else if (obj instanceof Tspan) {
				/*
				 * Tspan tspan = (Tspan)obj;
				 * 
				 * xform.setToIdentity(); xform.setToTranslation(cursorX,
				 * cursorY);
				 * 
				 * Shape tspanShape = tspan.getShape(); tspanShape =
				 * xform.createTransformedShape(tspanShape); textArea.add(new
				 * Area(tspanShape));
				 * 
				 * cursorX += tspanShape.getBounds2D().getWidth();
				 */

				Tspan tspan = (Tspan) obj;
				Point2D cursor = new Point2D.Float(cursorX, cursorY);
				// tspan.setCursorX(cursorX);
				// tspan.setCursorY(cursorY);
				tspan.appendToShape(textPath, cursor);
				// cursorX = tspan.getCursorX();
				// cursorY = tspan.getCursorY();
				cursorX = (float) cursor.getX();
				cursorY = (float) cursor.getY();

			}
		}

		switch (textAnchor) {
		case TXAN_MIDDLE: {
			AffineTransform at = new AffineTransform();
			at.translate(-textPath.getBounds().getWidth() / 2, 0);
			textPath.transform(at);
			break;
		}
		case TXAN_END: {
			AffineTransform at = new AffineTransform();
			at.translate(-Math.ceil(textPath.getBounds().getWidth()), 0);
			textPath.transform(at);
			break;
		}
		}
	}

	@Override
	public void render(Graphics2D g) throws SVGException {
		beginLayer(g);
		renderShape(g, textShape);
		finishLayer(g);
	}

	@Override
	public Shape getShape() {
		return shapeToParent(textShape);
	}

	@Override
	public Rectangle2D getBoundingBox() throws SVGException {
		return boundsToParent(includeStrokeInBounds(textShape.getBounds2D()));
	}

	/**
	 * Updates all attributes in this diagram associated with a time event. Ie,
	 * all attributes with track information.
	 *
	 * @return - true if this node has changed state as a result of the time
	 *         update
	 */
	@Override
	public boolean updateTime(double curTime) throws SVGException {
		// if (trackManager.getNumTracks() == 0) return false;
		boolean changeState = super.updateTime(curTime);

		// Get current values for parameters
		StyleAttribute sty = new StyleAttribute();
		boolean shapeChange = false;

		if (getPres(sty.setName("x"))) {
			float newVal = sty.getFloatValueWithUnits();
			if (newVal != x) {
				x = newVal;
				shapeChange = true;
			}
		}

		if (getPres(sty.setName("y"))) {
			float newVal = sty.getFloatValueWithUnits();
			if (newVal != y) {
				y = newVal;
				shapeChange = true;
			}
		}

		if (getPres(sty.setName("font-family"))) {
			String newVal = sty.getStringValue();
			if (!newVal.equals(fontFamily)) {
				fontFamily = newVal;
				shapeChange = true;
			}
		}

		if (getPres(sty.setName("font-size"))) {
			float newVal = sty.getFloatValueWithUnits();
			if (newVal != fontSize) {
				fontSize = newVal;
				shapeChange = true;
			}
		}

		if (getStyle(sty.setName("font-style"))) {
			String s = sty.getStringValue();
			int newVal = fontStyle;
			if ("normal".equals(s)) {
				newVal = TXST_NORMAL;
			} else if ("italic".equals(s)) {
				newVal = TXST_ITALIC;
			} else if ("oblique".equals(s)) {
				newVal = TXST_OBLIQUE;
			}
			if (newVal != fontStyle) {
				fontStyle = newVal;
				shapeChange = true;
			}
		}

		if (getStyle(sty.setName("font-weight"))) {
			String s = sty.getStringValue();
			int newVal = fontWeight;
			if ("normal".equals(s)) {
				newVal = TXWE_NORMAL;
			} else if ("bold".equals(s)) {
				newVal = TXWE_BOLD;
			}
			if (newVal != fontWeight) {
				fontWeight = newVal;
				shapeChange = true;
			}
		}

		if (shapeChange) {
			build();
			// buildFont();
			// return true;
		}

		return changeState || shapeChange;
	}
}

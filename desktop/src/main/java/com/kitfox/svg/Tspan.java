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
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.kitfox.svg.xml.StyleAttribute;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class Tspan extends ShapeElement {

	public static final String TAG_NAME = "tspan";
	float[] x = null;
	float[] y = null;
	float[] dx = null;
	float[] dy = null;
	float[] rotate = null;
	private String text = "";
	// float cursorX;
	// float cursorY;

	// Shape tspanShape;
	/**
	 * Creates a new instance of Stop
	 */
	public Tspan() {
	}

	@Override
	public String getTagName() {
		return TAG_NAME;
	}

	// public float getCursorX()
	// {
	// return cursorX;
	// }
	//
	// public float getCursorY()
	// {
	// return cursorY;
	// }
	//
	// public void setCursorX(float cursorX)
	// {
	// this.cursorX = cursorX;
	// }
	//
	// public void setCursorY(float cursorY)
	// {
	// this.cursorY = cursorY;
	// }
	/*
	 * public void loaderStartElement(SVGLoaderHelper helper, Attributes attrs,
	 * SVGElement parent) { //Load style string super.loaderStartElement(helper,
	 * attrs, parent);
	 * 
	 * String x = attrs.getValue("x"); String y = attrs.getValue("y"); String dx
	 * = attrs.getValue("dx"); String dy = attrs.getValue("dy"); String rotate =
	 * attrs.getValue("rotate");
	 * 
	 * if (x != null) this.x = XMLParseUtil.parseFloatList(x); if (y != null)
	 * this.y = XMLParseUtil.parseFloatList(y); if (dx != null) this.dx =
	 * XMLParseUtil.parseFloatList(dx); if (dy != null) this.dy =
	 * XMLParseUtil.parseFloatList(dy); if (rotate != null) { this.rotate =
	 * XMLParseUtil.parseFloatList(rotate); for (int i = 0; i <
	 * this.rotate.length; i++) this.rotate[i] =
	 * (float)Math.toRadians(this.rotate[i]); } }
	 */

	/**
	 * Called during load process to add text scanned within a tag
	 */
	@Override
	public void loaderAddText(SVGLoaderHelper helper, String text) {
		this.text += text;
	}

	@Override
	protected void build() throws SVGException {
		super.build();

		StyleAttribute sty = new StyleAttribute();

		if (getPres(sty.setName("x"))) {
			x = sty.getFloatList();
		}

		if (getPres(sty.setName("y"))) {
			y = sty.getFloatList();
		}

		if (getPres(sty.setName("dx"))) {
			dx = sty.getFloatList();
		}

		if (getPres(sty.setName("dy"))) {
			dy = sty.getFloatList();
		}

		if (getPres(sty.setName("rotate"))) {
			rotate = sty.getFloatList();
			for (int i = 0; i < this.rotate.length; i++) {
				rotate[i] = (float) Math.toRadians(this.rotate[i]);
			}

		}
	}

	public void appendToShape(GeneralPath addShape, Point2D cursor)
			throws SVGException {
		// if (x != null)
		// {
		// cursorX = x[0];
		// } else if (dx != null)
		// {
		// cursorX += dx[0];
		// }
		//
		// if (y != null)
		// {
		// cursorY = y[0];
		// } else if (dy != null)
		// {
		// cursorY += dy[0];
		// }

		StyleAttribute sty = new StyleAttribute();

		String fontFamily = null;
		if (getStyle(sty.setName("font-family"))) {
			fontFamily = sty.getStringValue();
		}

		float fontSize = 12f;
		if (getStyle(sty.setName("font-size"))) {
			fontSize = sty.getFloatValueWithUnits();
		}

		float letterSpacing = 0;
		if (getStyle(sty.setName("letter-spacing"))) {
			letterSpacing = sty.getFloatValueWithUnits();
		}

		// Get font
		Font font = diagram.getUniverse().getFont(fontFamily);
		if (font == null) {
			addShapeSysFont(addShape, null, fontFamily, fontSize, letterSpacing,
					cursor);
			return;
		}

		FontFace fontFace = font.getFontFace();
		int ascent = fontFace.getAscent();
		float fontScale = fontSize / ascent;

		AffineTransform xform = new AffineTransform();

		strokeWidthScalar = 1f / fontScale;

		float cursorX = (float) cursor.getX();
		float cursorY = (float) cursor.getY();

		// int i = 0;

		String drawText = this.text;
		drawText = drawText.trim();
		for (int i = 0; i < drawText.length(); i++) {
			if (x != null && i < x.length) {
				cursorX = x[i];
			} else if (dx != null && i < dx.length) {
				cursorX += dx[i];
			}

			if (y != null && i < y.length) {
				cursorY = y[i];
			} else if (dy != null && i < dy.length) {
				cursorY += dy[i];
			}
			// i++;

			xform.setToIdentity();
			xform.setToTranslation(cursorX, cursorY);
			xform.scale(fontScale, fontScale);
			if (rotate != null) {
				xform.rotate(rotate[i]);
			}

			String unicode = drawText.substring(i, i + 1);
			MissingGlyph glyph = font.getGlyph(unicode);

			Shape path = glyph.getPath();
			if (path != null) {
				path = xform.createTransformedShape(path);
				addShape.append(path, false);
			}

			cursorX += fontScale * glyph.getHorizAdvX() + letterSpacing;
		}

		// Save final draw point so calling method knows where to begin next
		// text draw
		cursor.setLocation(cursorX, cursorY);
		strokeWidthScalar = 1f;
	}

	private void addShapeSysFont(GeneralPath addShape, Font font,
			String fontFamily, float fontSize, float letterSpacing,
			Point2D cursor) {

		java.awt.Font sysFont = new java.awt.Font(fontFamily,
				java.awt.Font.PLAIN, (int) fontSize);

		FontRenderContext frc = new FontRenderContext(null, true, true);
		String renderText = this.text.trim();

		AffineTransform xform = new AffineTransform();

		float cursorX = (float) cursor.getX();
		float cursorY = (float) cursor.getY();
		// int i = 0;
		for (int i = 0; i < renderText.length(); i++) {
			if (x != null && i < x.length) {
				cursorX = x[i];
			} else if (dx != null && i < dx.length) {
				cursorX += dx[i];
			}

			if (y != null && i < y.length) {
				cursorY = y[i];
			} else if (dy != null && i < dy.length) {
				cursorY += dy[i];
			}
			// i++;

			xform.setToIdentity();
			xform.setToTranslation(cursorX, cursorY);
			if (rotate != null) {
				xform.rotate(rotate[Math.min(i, rotate.length - 1)]);
			}

			// String unicode = renderText.substring(i, i + 1);
			GlyphVector textVector = sysFont.createGlyphVector(frc,
					renderText.substring(i, i + 1));
			Shape glyphOutline = textVector.getGlyphOutline(0);
			GlyphMetrics glyphMetrics = textVector.getGlyphMetrics(0);

			glyphOutline = xform.createTransformedShape(glyphOutline);
			addShape.append(glyphOutline, false);

			// cursorX += fontScale * glyph.getHorizAdvX() + letterSpacing;
			cursorX += glyphMetrics.getAdvance() + letterSpacing;
		}

		cursor.setLocation(cursorX, cursorY);
	}

	@Override
	public void render(Graphics2D g) throws SVGException {
		float cursorX = 0;
		float cursorY = 0;

		if (x != null) {
			cursorX = x[0];
			cursorY = y[0];
		} else if (dx != null) {
			cursorX += dx[0];
			cursorY += dy[0];
		}

		StyleAttribute sty = new StyleAttribute();

		String fontFamily = null;
		if (getPres(sty.setName("font-family"))) {
			fontFamily = sty.getStringValue();
		}

		float fontSize = 12f;
		if (getPres(sty.setName("font-size"))) {
			fontSize = sty.getFloatValueWithUnits();
		}

		// Get font
		Font font = diagram.getUniverse().getFont(fontFamily);
		if (font == null) {
			System.err.println("Could not load font");
			java.awt.Font sysFont = new java.awt.Font(fontFamily,
					java.awt.Font.PLAIN, (int) fontSize);
			renderSysFont(g, sysFont);
			return;
		}

		FontFace fontFace = font.getFontFace();
		int ascent = fontFace.getAscent();
		float fontScale = fontSize / ascent;

		AffineTransform oldXform = g.getTransform();
		AffineTransform xform = new AffineTransform();

		strokeWidthScalar = 1f / fontScale;

		int posPtr = 1;

		for (int i = 0; i < text.length(); i++) {
			xform.setToTranslation(cursorX, cursorY);
			xform.scale(fontScale, fontScale);
			g.transform(xform);

			String unicode = text.substring(i, i + 1);
			MissingGlyph glyph = font.getGlyph(unicode);

			Shape path = glyph.getPath();
			if (path != null) {
				renderShape(g, path);
			} else {
				glyph.render(g);
			}

			if (x != null && posPtr < x.length) {
				cursorX = x[posPtr];
				cursorY = y[posPtr++];
			} else if (dx != null && posPtr < dx.length) {
				cursorX += dx[posPtr];
				cursorY += dy[posPtr++];
			}

			cursorX += fontScale * glyph.getHorizAdvX();

			g.setTransform(oldXform);
		}

		strokeWidthScalar = 1f;
	}

	protected void renderSysFont(Graphics2D g, java.awt.Font font)
			throws SVGException {
		float cursorX = 0;
		float cursorY = 0;

		FontRenderContext frc = g.getFontRenderContext();

		Shape textShape = font.createGlyphVector(frc, text).getOutline(cursorX,
				cursorY);
		renderShape(g, textShape);
		Rectangle2D rect = font.getStringBounds(text, frc);
		cursorX += (float) rect.getWidth();
	}

	@Override
	public Shape getShape() {
		return null;
		// return shapeToParent(tspanShape);
	}

	@Override
	public Rectangle2D getBoundingBox() {
		return null;
		// return boundsToParent(tspanShape.getBounds2D());
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
		// Tspan does not change
		return false;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}

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
 * Created on February 20, 2004, 10:00 PM
 */
package com.kitfox.svg;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.kitfox.svg.app.data.Handler;
import com.kitfox.svg.xml.StyleAttribute;

/**
 * Implements an image.
 *
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class ImageSVG extends RenderableElement {
	public static final String TAG_NAME = "image";

	float x = 0f;
	float y = 0f;
	float width = 0f;
	float height = 0f;
	// BufferedImage href = null;
	URL imageSrc = null;
	AffineTransform xform;
	Rectangle2D bounds;

	/**
	 * Creates a new instance of Font
	 */
	public ImageSVG() {
	}

	@Override
	public String getTagName() {
		return TAG_NAME;
	}

	@Override
	protected void build() throws SVGException {
		super.build();

		StyleAttribute sty = new StyleAttribute();

		if (getPres(sty.setName("x"))) {
			x = sty.getFloatValueWithUnits();
		}

		if (getPres(sty.setName("y"))) {
			y = sty.getFloatValueWithUnits();
		}

		if (getPres(sty.setName("width"))) {
			width = sty.getFloatValueWithUnits();
		}

		if (getPres(sty.setName("height"))) {
			height = sty.getFloatValueWithUnits();
		}

		try {
			if (getPres(sty.setName("xlink:href"))) {
				URI src = sty.getURIValue(getXMLBase());
				if ("data".equals(src.getScheme())) {
					imageSrc = new URL(null, src.toASCIIString(),
							new Handler());
				} else {
					try {
						imageSrc = src.toURL();
					} catch (Exception e) {
						Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
								"Could not parse xlink:href " + src, e);
						// e.printStackTrace();
						imageSrc = null;
					}
				}
			}
		} catch (Exception e) {
			throw new SVGException(e);
		}

		diagram.getUniverse().registerImage(imageSrc);

		// Set widths if not set
		BufferedImage img = diagram.getUniverse().getImage(imageSrc);
		if (img == null) {
			xform = new AffineTransform();
			bounds = new Rectangle2D.Float();
			return;
		}

		if (width == 0) {
			width = img.getWidth();
		}
		if (height == 0) {
			height = img.getHeight();
		}

		// Determine image xform
		xform = new AffineTransform();
		// xform.setToScale(this.width / img.getWidth(), this.height /
		// img.getHeight());
		// xform.translate(this.x, this.y);
		xform.translate(this.x, this.y);
		xform.scale(this.width / img.getWidth(), this.height / img.getHeight());

		bounds = new Rectangle2D.Float(this.x, this.y, this.width, this.height);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	@Override
	void pick(Point2D point, boolean boundingBox, List retVec)
			throws SVGException {
		if (getBoundingBox().contains(point)) {
			retVec.add(getPath(null));
		}
	}

	@Override
	void pick(Rectangle2D pickArea, AffineTransform ltw, boolean boundingBox,
			List retVec) throws SVGException {
		if (ltw.createTransformedShape(getBoundingBox()).intersects(pickArea)) {
			retVec.add(getPath(null));
		}
	}

	@Override
	public void render(Graphics2D g) throws SVGException {
		StyleAttribute styleAttrib = new StyleAttribute();
		if (getStyle(styleAttrib.setName("visibility"))) {
			if (!styleAttrib.getStringValue().equals("visible")) {
				return;
			}
		}

		beginLayer(g);

		float opacity = 1f;
		if (getStyle(styleAttrib.setName("opacity"))) {
			opacity = styleAttrib.getRatioValue();
		}

		if (opacity <= 0) {
			return;
		}

		Composite oldComp = null;

		if (opacity < 1) {
			oldComp = g.getComposite();
			Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					opacity);
			g.setComposite(comp);
		}

		BufferedImage img = diagram.getUniverse().getImage(imageSrc);
		if (img == null) {
			return;
		}

		AffineTransform curXform = g.getTransform();
		g.transform(xform);

		g.drawImage(img, 0, 0, null);

		g.setTransform(curXform);
		if (oldComp != null) {
			g.setComposite(oldComp);
		}

		finishLayer(g);
	}

	@Override
	public Rectangle2D getBoundingBox() {
		return boundsToParent(bounds);
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

		if (getPres(sty.setName("width"))) {
			float newVal = sty.getFloatValueWithUnits();
			if (newVal != width) {
				width = newVal;
				shapeChange = true;
			}
		}

		if (getPres(sty.setName("height"))) {
			float newVal = sty.getFloatValueWithUnits();
			if (newVal != height) {
				height = newVal;
				shapeChange = true;
			}
		}

		try {
			if (getPres(sty.setName("xlink:href"))) {
				URI src = sty.getURIValue(getXMLBase());

				URL newVal;
				if ("data".equals(src.getScheme())) {
					newVal = new URL(null, src.toASCIIString(), new Handler());
				} else {
					newVal = src.toURL();
				}

				if (!newVal.equals(imageSrc)) {
					imageSrc = newVal;
					shapeChange = true;
				}
			}
		} catch (IllegalArgumentException ie) {
			Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
					"Image provided with illegal value for href: \""
							+ sty.getStringValue() + '"',
					ie);
		} catch (Exception e) {
			Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
					"Could not parse xlink:href", e);
		}

		if (shapeChange) {
			build();
			// diagram.getUniverse().registerImage(imageSrc);
			//
			// //Set widths if not set
			// BufferedImage img = diagram.getUniverse().getImage(imageSrc);
			// if (img == null)
			// {
			// xform = new AffineTransform();
			// bounds = new Rectangle2D.Float();
			// }
			// else
			// {
			// if (width == 0) width = img.getWidth();
			// if (height == 0) height = img.getHeight();
			//
			// //Determine image xform
			// xform = new AffineTransform();
			//// xform.setToScale(this.width / img.getWidth(), this.height /
			// img.getHeight());
			//// xform.translate(this.x, this.y);
			// xform.translate(this.x, this.y);
			// xform.scale(this.width / img.getWidth(), this.height /
			// img.getHeight());
			//
			// bounds = new Rectangle2D.Float(this.x, this.y, this.width,
			// this.height);
			// }
			//
			// return true;
		}

		return changeState || shapeChange;
	}
}

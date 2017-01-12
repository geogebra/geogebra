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
 * Created on January 26, 2004, 1:54 AM
 */
package com.kitfox.svg;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.net.URI;

import com.kitfox.svg.xml.StyleAttribute;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class Use extends ShapeElement {
	public static final String TAG_NAME = "use";

	float x = 0f;
	float y = 0f;
	float width = 1f;
	float height = 1f;
	// SVGElement href = null;
	URI href = null;
	AffineTransform refXform;

	/**
	 * Creates a new instance of LinearGradient
	 */
	public Use() {
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

		if (getPres(sty.setName("xlink:href"))) {
			URI src = sty.getURIValue(getXMLBase());
			href = src;
			// href = diagram.getUniverse().getElement(src);
		}

		// Determine use offset/scale
		refXform = new AffineTransform();
		refXform.translate(this.x, this.y);
	}

	@Override
	public void render(Graphics2D g) throws SVGException {
		beginLayer(g);

		// AffineTransform oldXform = g.getTransform();
		AffineTransform oldXform = g.getTransform();
		g.transform(refXform);

		SVGElement ref = diagram.getUniverse().getElement(href);

		if (ref == null || !(ref instanceof RenderableElement)) {
			return;
		}

		RenderableElement rendEle = (RenderableElement) ref;
		rendEle.pushParentContext(this);
		rendEle.render(g);
		rendEle.popParentContext();

		g.setTransform(oldXform);

		finishLayer(g);
	}

	@Override
	public Shape getShape() {
		SVGElement ref = diagram.getUniverse().getElement(href);
		if (ref instanceof ShapeElement) {
			Shape shape = ((ShapeElement) ref).getShape();
			shape = refXform.createTransformedShape(shape);
			shape = shapeToParent(shape);
			return shape;
		}

		return null;
	}

	@Override
	public Rectangle2D getBoundingBox() throws SVGException {
		SVGElement ref = diagram.getUniverse().getElement(href);
		if (ref instanceof ShapeElement) {
			ShapeElement shapeEle = (ShapeElement) ref;
			shapeEle.pushParentContext(this);
			Rectangle2D bounds = shapeEle.getBoundingBox();
			shapeEle.popParentContext();

			bounds = refXform.createTransformedShape(bounds).getBounds2D();
			bounds = boundsToParent(bounds);

			return bounds;
		}

		return null;
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

		if (getPres(sty.setName("xlink:href"))) {
			URI src = sty.getURIValue(getXMLBase());
			// SVGElement newVal = diagram.getUniverse().getElement(src);
			if (!src.equals(href)) {
				href = src;
				shapeChange = true;
			}
		}
		/*
		 * if (getPres(sty.setName("xlink:href"))) { URI src =
		 * sty.getURIValue(getXMLBase()); href =
		 * diagram.getUniverse().getElement(src); }
		 * 
		 * //Determine use offset/scale refXform = new AffineTransform();
		 * refXform.translate(this.x, this.y); refXform.scale(this.width,
		 * this.height);
		 */
		if (shapeChange) {
			build();
			// Determine use offset/scale
			// refXform.setToTranslation(this.x, this.y);
			// refXform.scale(this.width, this.height);
			// return true;
		}

		return changeState || shapeChange;
	}
}

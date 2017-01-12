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
 * Created on January 26, 2004, 9:00 AM
 */
package com.kitfox.svg;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import com.kitfox.svg.xml.StyleAttribute;

/**
 * Maintains bounding box for this element
 *
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
abstract public class TransformableElement extends SVGElement {
	AffineTransform xform = null;

	/**
	 * Creates a new instance of BoundedElement
	 */
	public TransformableElement() {
	}

	public TransformableElement(String id, SVGElement parent) {
		super(id, parent);
	}

	/**
	 * Fetches a copy of the cached AffineTransform. Note that this value will
	 * only be valid after the node has been updated.
	 *
	 * @return
	 */
	public AffineTransform getXForm() {
		return xform == null ? null : new AffineTransform(xform);
	}
	/*
	 * public void loaderStartElement(SVGLoaderHelper helper, Attributes attrs,
	 * SVGElement parent) { //Load style string super.loaderStartElement(helper,
	 * attrs, parent);
	 * 
	 * String transform = attrs.getValue("transform"); if (transform != null) {
	 * xform = parseTransform(transform); } }
	 */

	@Override
	protected void build() throws SVGException {
		super.build();

		StyleAttribute sty = new StyleAttribute();

		if (getPres(sty.setName("transform"))) {
			xform = parseTransform(sty.getStringValue());
		}
	}

	protected Shape shapeToParent(Shape shape) {
		if (xform == null) {
			return shape;
		}
		return xform.createTransformedShape(shape);
	}

	protected Rectangle2D boundsToParent(Rectangle2D rect) {
		if (xform == null) {
			return rect;
		}
		return xform.createTransformedShape(rect).getBounds2D();
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
		StyleAttribute sty = new StyleAttribute();

		if (getPres(sty.setName("transform"))) {
			AffineTransform newXform = parseTransform(sty.getStringValue());
			if (!newXform.equals(xform)) {
				xform = newXform;
				return true;
			}
		}

		return false;
	}
}

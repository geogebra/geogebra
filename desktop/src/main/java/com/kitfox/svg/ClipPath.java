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

import java.awt.Shape;
import java.awt.geom.Area;
import java.util.Iterator;

import com.kitfox.svg.xml.StyleAttribute;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class ClipPath extends SVGElement {

	public static final String TAG_NAME = "clippath";
	public static final int CP_USER_SPACE_ON_USE = 0;
	public static final int CP_OBJECT_BOUNDING_BOX = 1;
	int clipPathUnits = CP_USER_SPACE_ON_USE;

	/**
	 * Creates a new instance of Stop
	 */
	public ClipPath() {
	}

	@Override
	public String getTagName() {
		return TAG_NAME;
	}

	/**
	 * Called after the start element but before the end element to indicate
	 * each child tag that has been processed
	 */
	@Override
	public void loaderAddChild(SVGLoaderHelper helper, SVGElement child)
			throws SVGElementException {
		super.loaderAddChild(helper, child);
	}

	@Override
	protected void build() throws SVGException {
		super.build();

		StyleAttribute sty = new StyleAttribute();

		clipPathUnits = (getPres(sty.setName("clipPathUnits"))
				&& sty.getStringValue().equals("objectBoundingBox"))
						? CP_OBJECT_BOUNDING_BOX : CP_USER_SPACE_ON_USE;
	}

	public int getClipPathUnits() {
		return clipPathUnits;
	}

	public Shape getClipPathShape() {
		if (children.isEmpty()) {
			return null;
		}
		if (children.size() == 1) {
			return ((ShapeElement) children.get(0)).getShape();
		}

		Area clipArea = null;
		for (Iterator it = children.iterator(); it.hasNext();) {
			ShapeElement se = (ShapeElement) it.next();

			if (clipArea == null) {
				Shape shape = se.getShape();
				if (shape != null) {
					clipArea = new Area(se.getShape());
				}
				continue;
			}

			Shape shape = se.getShape();
			if (shape != null) {
				clipArea.intersect(new Area(shape));
			}
		}

		return clipArea;
	}

	/**
	 * Updates all attributes in this diagram associated with a time event. Ie,
	 * all attributes with track information.
	 *
	 * @param curTime
	 * @return - true if this node has changed state as a result of the time
	 *         update
	 * @throws com.kitfox.svg.SVGException
	 */
	@Override
	public boolean updateTime(double curTime) throws SVGException {
		// Get current values for parameters
		StyleAttribute sty = new StyleAttribute();
		boolean shapeChange = false;

		if (getPres(sty.setName("clipPathUnits"))) {
			String newUnitsStrn = sty.getStringValue();
			int newUnits = newUnitsStrn.equals("objectBoundingBox")
					? CP_OBJECT_BOUNDING_BOX : CP_USER_SPACE_ON_USE;

			if (newUnits != clipPathUnits) {
				clipPathUnits = newUnits;
				shapeChange = true;
			}
		}

		if (shapeChange) {
			build();
		}

		for (int i = 0; i < children.size(); ++i) {
			SVGElement ele = (SVGElement) children.get(i);
			ele.updateTime(curTime);
		}

		return shapeChange;
	}
}

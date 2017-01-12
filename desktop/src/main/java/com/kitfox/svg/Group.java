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
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

import com.kitfox.svg.xml.StyleAttribute;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class Group extends ShapeElement {
	public static final String TAG_NAME = "group";

	// Cache bounding box for faster clip testing
	Rectangle2D boundingBox;
	Shape cachedShape;

	/**
	 * Creates a new instance of Stop
	 */
	public Group() {
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

	protected boolean outsideClip(Graphics2D g) throws SVGException {
		Shape clip = g.getClip();
		if (clip == null) {
			return false;
		}
		// g.getClipBounds(clipBounds);
		Rectangle2D rect = getBoundingBox();

		if (clip.intersects(rect)) {
			return false;
		}

		return true;
	}

	@Override
	void pick(Point2D point, boolean boundingBox, List retVec)
			throws SVGException {
		Point2D xPoint = new Point2D.Double(point.getX(), point.getY());
		if (xform != null) {
			try {
				xform.inverseTransform(point, xPoint);
			} catch (NoninvertibleTransformException ex) {
				throw new SVGException(ex);
			}
		}

		for (Iterator it = children.iterator(); it.hasNext();) {
			SVGElement ele = (SVGElement) it.next();
			if (ele instanceof RenderableElement) {
				RenderableElement rendEle = (RenderableElement) ele;

				rendEle.pick(xPoint, boundingBox, retVec);
			}
		}
	}

	@Override
	void pick(Rectangle2D pickArea, AffineTransform ltw, boolean boundingBox,
			List retVec) throws SVGException {
		if (xform != null) {
			ltw = new AffineTransform(ltw);
			ltw.concatenate(xform);
		}

		for (Iterator it = children.iterator(); it.hasNext();) {
			SVGElement ele = (SVGElement) it.next();
			if (ele instanceof RenderableElement) {
				RenderableElement rendEle = (RenderableElement) ele;

				rendEle.pick(pickArea, ltw, boundingBox, retVec);
			}
		}
	}

	@Override
	public void render(Graphics2D g) throws SVGException {
		// Don't process if not visible
		StyleAttribute styleAttrib = new StyleAttribute();
		if (getStyle(styleAttrib.setName("visibility"))) {
			if (!styleAttrib.getStringValue().equals("visible")) {
				return;
			}
		}

		// Do not process offscreen groups
		boolean ignoreClip = diagram.ignoringClipHeuristic();
		// if (!ignoreClip && outsideClip(g))
		// {
		// return;
		// }

		beginLayer(g);

		Iterator it = children.iterator();

		// try
		// {
		// g.getClipBounds(clipBounds);
		// }
		// catch (Exception e)
		// {
		// //For some reason, getClipBounds can throw a null pointer exception
		// for
		// // some types of Graphics2D
		// ignoreClip = true;
		// }

		Shape clip = g.getClip();
		while (it.hasNext()) {
			SVGElement ele = (SVGElement) it.next();
			if (ele instanceof RenderableElement) {
				RenderableElement rendEle = (RenderableElement) ele;

				// if (shapeEle == null) continue;

				if (!(ele instanceof Group)) {
					// Skip if clipping area is outside our bounds
					if (!ignoreClip && clip != null
							&& !clip.intersects(rendEle.getBoundingBox())) {
						continue;
					}
				}

				rendEle.render(g);
			}
		}

		finishLayer(g);
	}

	/**
	 * Retrieves the cached bounding box of this group
	 */
	@Override
	public Shape getShape() {
		if (cachedShape == null) {
			calcShape();
		}
		return cachedShape;
	}

	public void calcShape() {
		Area retShape = new Area();

		for (Iterator it = children.iterator(); it.hasNext();) {
			SVGElement ele = (SVGElement) it.next();

			if (ele instanceof ShapeElement) {
				ShapeElement shpEle = (ShapeElement) ele;
				Shape shape = shpEle.getShape();
				if (shape != null) {
					retShape.add(new Area(shape));
				}
			}
		}

		cachedShape = shapeToParent(retShape);
	}

	/**
	 * Retrieves the cached bounding box of this group
	 */
	@Override
	public Rectangle2D getBoundingBox() throws SVGException {
		if (boundingBox == null) {
			calcBoundingBox();
		}
		// calcBoundingBox();
		return boundingBox;
	}

	/**
	 * Recalculates the bounding box by taking the union of the bounding boxes
	 * of all children. Caches the result.
	 */
	public void calcBoundingBox() throws SVGException {
		// Rectangle2D retRect = new Rectangle2D.Float();
		Rectangle2D retRect = null;

		for (Iterator it = children.iterator(); it.hasNext();) {
			SVGElement ele = (SVGElement) it.next();

			if (ele instanceof RenderableElement) {
				RenderableElement rendEle = (RenderableElement) ele;
				Rectangle2D bounds = rendEle.getBoundingBox();
				if (bounds != null && (bounds.getWidth() != 0
						|| bounds.getHeight() != 0)) {
					if (retRect == null) {
						retRect = bounds;
					} else {
						if (retRect.getWidth() != 0
								|| retRect.getHeight() != 0) {
							retRect = retRect.createUnion(bounds);
						}
					}
				}
			}
		}

		// if (xform != null)
		// {
		// retRect = xform.createTransformedShape(retRect).getBounds2D();
		// }

		// If no contents, use degenerate rectangle
		if (retRect == null) {
			retRect = new Rectangle2D.Float();
		}

		boundingBox = boundsToParent(retRect);
	}

	@Override
	public boolean updateTime(double curTime) throws SVGException {
		boolean changeState = super.updateTime(curTime);
		Iterator it = children.iterator();

		// Distribute message to all members of this group
		while (it.hasNext()) {
			SVGElement ele = (SVGElement) it.next();
			boolean updateVal = ele.updateTime(curTime);

			changeState = changeState || updateVal;

			// Update our shape if shape aware children change
			if (ele instanceof ShapeElement) {
				cachedShape = null;
			}
			if (ele instanceof RenderableElement) {
				boundingBox = null;
			}
		}

		return changeState;
	}
}

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
 * Created on February 18, 2004, 5:04 PM
 */

package com.kitfox.svg;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Top level structure in an SVG tree.
 *
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class SVGDiagram implements Serializable {
	public static final long serialVersionUID = 0;

	// Indexes elements within this SVG diagram
	final HashMap idMap = new HashMap();

	SVGRoot root;
	final SVGUniverse universe;

	/**
	 * This is used by the SVGRoot to determine the width of the
	 */
	private Rectangle deviceViewport = new Rectangle(100, 100);

	/**
	 * If true, no attempt will be made to discard geometry based on it being
	 * out of bounds. This trades potentially drawing many out of bounds shapes
	 * with having to recalculate bounding boxes every animation iteration.
	 */
	protected boolean ignoreClipHeuristic = false;

	/**
	 * URL which uniquely identifies this document
	 */
	// final URI docRoot;

	/**
	 * URI that uniquely identifies this document. Also used to resolve relative
	 * urls. Default base for document.
	 */
	final URI xmlBase;

	/** Creates a new instance of SVGDiagram */
	public SVGDiagram(URI xmlBase, SVGUniverse universe) {
		this.universe = universe;
		// this.docRoot = docRoot;
		this.xmlBase = xmlBase;
	}

	/**
	 * Draws this diagram to the passed graphics context
	 */
	public void render(Graphics2D g) throws SVGException {
		root.render(g);
	}

	/**
	 * Searches thorough the scene graph for all RenderableElements that have
	 * shapes that contain the passed point.
	 * 
	 * For every shape which contains the pick point, a List containing the path
	 * to the node is added to the return list. That is, the result of
	 * SVGElement.getPath() is added for each entry.
	 *
	 * @return the passed in list
	 */
	public List pick(Point2D point, List retVec) throws SVGException {
		return pick(point, false, retVec);
	}

	public List pick(Point2D point, boolean boundingBox, List retVec)
			throws SVGException {
		if (retVec == null) {
			retVec = new ArrayList();
		}

		root.pick(point, boundingBox, retVec);

		return retVec;
	}

	public List pick(Rectangle2D pickArea, List retVec) throws SVGException {
		return pick(pickArea, false, retVec);
	}

	public List pick(Rectangle2D pickArea, boolean boundingBox, List retVec)
			throws SVGException {
		if (retVec == null) {
			retVec = new ArrayList();
		}

		root.pick(pickArea, new AffineTransform(), boundingBox, retVec);

		return retVec;
	}

	public SVGUniverse getUniverse() {
		return universe;
	}

	public URI getXMLBase() {
		return xmlBase;
	}

	// public URL getDocRoot()
	// {
	// return docRoot;
	// }

	public float getWidth() {
		if (root == null) {
			return 0;
		}
		return root.getDeviceWidth();
	}

	public float getHeight() {
		if (root == null) {
			return 0;
		}
		return root.getDeviceHeight();
	}

	/**
	 * Returns the viewing rectangle of this diagram in device coordinates.
	 */
	public Rectangle2D getViewRect(Rectangle2D rect) {
		if (root != null) {
			return root.getDeviceRect(rect);
		}
		return rect;
	}

	public Rectangle2D getViewRect() {
		return getViewRect(new Rectangle2D.Double());
	}

	public SVGElement getElement(String name) {
		return (SVGElement) idMap.get(name);
	}

	public void setElement(String name, SVGElement node) {
		idMap.put(name, node);
	}

	public void removeElement(String name) {
		idMap.remove(name);
	}

	public SVGRoot getRoot() {
		return root;
	}

	public void setRoot(SVGRoot root) {
		this.root = root;
		root.setDiagram(this);
	}

	public boolean ignoringClipHeuristic() {
		return ignoreClipHeuristic;
	}

	public void setIgnoringClipHeuristic(boolean ignoreClipHeuristic) {
		this.ignoreClipHeuristic = ignoreClipHeuristic;
	}

	/**
	 * Updates all attributes in this diagram associated with a time event. Ie,
	 * all attributes with track information.
	 */
	public void updateTime(double curTime) throws SVGException {
		if (root == null) {
			return;
		}
		root.updateTime(curTime);
	}

	public Rectangle getDeviceViewport() {
		return deviceViewport;
	}

	/**
	 * Sets the dimensions of the device being rendered into. This is used by
	 * SVGRoot when its x, y, width or height parameters are specified as
	 * percentages.
	 */
	public void setDeviceViewport(Rectangle deviceViewport) {
		this.deviceViewport.setBounds(deviceViewport);
		if (root != null) {
			try {
				root.build();
			} catch (SVGException ex) {
				Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
						"Could not build document", ex);
			}
		}
	}
}

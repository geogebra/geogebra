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
 * Created on February 18, 2004, 5:09 PM
 */

package com.kitfox.svg;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class SVGLoader extends DefaultHandler {
	final HashMap nodeClasses = new HashMap();
	// final HashMap attribClasses = new HashMap();
	final LinkedList buildStack = new LinkedList();

	final HashSet ignoreClasses = new HashSet();

	final SVGLoaderHelper helper;

	/**
	 * The diagram that represents the base of this SVG document we're loading.
	 * Will be augmented to include node indexing info and other useful stuff.
	 */
	final SVGDiagram diagram;

	// SVGElement loadRoot;

	// Used to keep track of document elements that are not part of the SVG
	// namespace
	int skipNonSVGTagDepth = 0;
	int indent = 0;

	final boolean verbose;

	/** Creates a new instance of SVGLoader */
	public SVGLoader(URI xmlBase, SVGUniverse universe) {
		this(xmlBase, universe, false);
	}

	public SVGLoader(URI xmlBase, SVGUniverse universe, boolean verbose) {
		this.verbose = verbose;

		diagram = new SVGDiagram(xmlBase, universe);

		// Compile a list of important builder classes
		nodeClasses.put("a", A.class);
		// nodeClasses.put("animate", Animate.class);
		// nodeClasses.put("animatecolor", AnimateColor.class);
		// nodeClasses.put("animatemotion", AnimateMotion.class);
		// nodeClasses.put("animatetransform", AnimateTransform.class);
		nodeClasses.put("circle", Circle.class);
		nodeClasses.put("clippath", ClipPath.class);
		nodeClasses.put("defs", Defs.class);
		nodeClasses.put("desc", Desc.class);
		nodeClasses.put("ellipse", Ellipse.class);
		nodeClasses.put("filter", Filter.class);
		nodeClasses.put("font", Font.class);
		nodeClasses.put("font-face", FontFace.class);
		nodeClasses.put("g", Group.class);
		nodeClasses.put("glyph", Glyph.class);
		nodeClasses.put("hkern", Hkern.class);
		nodeClasses.put("image", ImageSVG.class);
		nodeClasses.put("line", Line.class);
		nodeClasses.put("lineargradient", LinearGradient.class);
		nodeClasses.put("marker", Marker.class);
		nodeClasses.put("metadata", Metadata.class);
		nodeClasses.put("missing-glyph", MissingGlyph.class);
		nodeClasses.put("path", Path.class);
		nodeClasses.put("pattern", PatternSVG.class);
		nodeClasses.put("polygon", Polygon.class);
		nodeClasses.put("polyline", Polyline.class);
		nodeClasses.put("radialgradient", RadialGradient.class);
		nodeClasses.put("rect", Rect.class);
		// nodeClasses.put("set", SetSmil.class);
		nodeClasses.put("shape", ShapeElement.class);
		nodeClasses.put("stop", Stop.class);
		nodeClasses.put("style", Style.class);
		nodeClasses.put("svg", SVGRoot.class);
		nodeClasses.put("symbol", Symbol.class);
		nodeClasses.put("text", Text.class);
		nodeClasses.put("title", Title.class);
		nodeClasses.put("tspan", Tspan.class);
		nodeClasses.put("use", Use.class);

		ignoreClasses.add("midpointstop");

		// attribClasses.put("clip-path", StyleUrl.class);
		// attribClasses.put("color", StyleColor.class);

		helper = new SVGLoaderHelper(xmlBase, universe, diagram);
	}

	private static String printIndent(int indent, String indentStrn) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < indent; i++) {
			sb.append(indentStrn);
		}
		return sb.toString();
	}

	@Override
	public void startDocument() throws SAXException {
		// System.err.println("Start doc");

		// buildStack.clear();
	}

	@Override
	public void endDocument() throws SAXException {
		// System.err.println("End doc");
	}

	@Override
	public void startElement(String namespaceURI, String sName, String qName,
			Attributes attrs) throws SAXException {
		if (verbose) {
			System.err.println(printIndent(indent, " ")
					+ "Starting parse of tag " + sName + ": " + namespaceURI);
		}
		indent++;

		if (skipNonSVGTagDepth != 0 || (!namespaceURI.equals("")
				&& !namespaceURI.equals(SVGElement.SVG_NS))) {
			skipNonSVGTagDepth++;
			return;
		}

		sName = sName.toLowerCase();

		// javax.swing.JOptionPane.showMessageDialog(null, sName);

		Object obj = nodeClasses.get(sName);
		if (obj == null) {
			if (!ignoreClasses.contains(sName)) {
				if (verbose) {
					System.err.println("SVGLoader: Could not identify tag '"
							+ sName + "'");
				}
			}
			return;
		}

		// Debug info tag depth
		// for (int i = 0; i < buildStack.size(); i++) System.err.print(" ");
		// System.err.println("+" + sName);

		try {
			Class cls = (Class) obj;
			SVGElement svgEle = (SVGElement) cls.newInstance();

			SVGElement parent = null;
			if (buildStack.size() != 0) {
				parent = (SVGElement) buildStack.getLast();
			}
			svgEle.loaderStartElement(helper, attrs, parent);

			buildStack.addLast(svgEle);
		} catch (Exception e) {
			Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
					"Could not load", e);
			throw new SAXException(e);
		}

	}

	@Override
	public void endElement(String namespaceURI, String sName, String qName)
			throws SAXException {
		indent--;
		if (verbose) {
			System.err.println(printIndent(indent, " ") + "Ending parse of tag "
					+ sName + ": " + namespaceURI);
		}

		if (skipNonSVGTagDepth != 0) {
			skipNonSVGTagDepth--;
			return;
		}

		sName = sName.toLowerCase();

		Object obj = nodeClasses.get(sName);
		if (obj == null) {
			return;
		}

		// Debug info tag depth
		// for (int i = 0; i < buildStack.size(); i++) System.err.print(" ");
		// System.err.println("-" + sName);

		try {
			SVGElement svgEle = (SVGElement) buildStack.removeLast();

			svgEle.loaderEndElement(helper);

			SVGElement parent = null;
			if (buildStack.size() != 0) {
				parent = (SVGElement) buildStack.getLast();
			}
			// else loadRoot = (SVGElement)svgEle;

			if (parent != null) {
				parent.loaderAddChild(helper, svgEle);
			} else {
				diagram.setRoot((SVGRoot) svgEle);
			}

		} catch (Exception e) {
			Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
					"Could not parse", e);
			throw new SAXException(e);
		}
	}

	@Override
	public void characters(char buf[], int offset, int len)
			throws SAXException {
		if (skipNonSVGTagDepth != 0) {
			return;
		}

		if (buildStack.size() != 0) {
			SVGElement parent = (SVGElement) buildStack.getLast();
			String s = new String(buf, offset, len);
			parent.loaderAddText(helper, s);
		}
	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
		// Check for external style sheet
	}

	// public SVGElement getLoadRoot() { return loadRoot; }
	public SVGDiagram getLoadedDiagram() {
		return diagram;
	}
}

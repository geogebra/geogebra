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
 * Created on February 18, 2004, 11:43 PM
 */
package com.kitfox.svg;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;

import org.geogebra.common.jre.util.Base64;
import org.geogebra.common.util.debug.Log;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.kitfox.svg.app.beans.SVGIcon;

/**
 * Many SVG files can be loaded at one time. These files will quite likely need
 * to reference one another. The SVG universe provides a container for all these
 * files and the means for them to relate to each other.
 *
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class SVGUniverse implements Serializable {

	public static final long serialVersionUID = 0;
	transient private PropertyChangeSupport changes = new PropertyChangeSupport(
			this);
	/**
	 * Maps document URIs to their loaded SVG diagrams. Note that URIs for
	 * documents loaded from URLs will reflect their URLs and URIs for documents
	 * initiated from streams will have the scheme <i>svgSalamander</i>.
	 */
	final HashMap loadedDocs = new HashMap();
	final HashMap loadedFonts = new HashMap();
	final HashMap loadedImages = new HashMap();
	public static final String INPUTSTREAM_SCHEME = "svgSalamander";
	/**
	 * Current time in this universe. Used for resolving attributes that are
	 * influenced by track information. Time is in milliseconds. Time 0
	 * coresponds to the time of 0 in each member diagram.
	 */
	protected double curTime = 0.0;
	private boolean verbose = false;
	// Cache reader for efficiency
	XMLReader cachedReader;

	/**
	 * Creates a new instance of SVGUniverse
	 */
	public SVGUniverse() {
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

	/**
	 * Release all loaded SVG document from memory
	 */
	public void clear() {
		loadedDocs.clear();
		loadedFonts.clear();
		loadedImages.clear();
	}

	/**
	 * Returns the current animation time in milliseconds.
	 */
	public double getCurTime() {
		return curTime;
	}

	public void setCurTime(double curTime) {
		double oldTime = this.curTime;
		this.curTime = curTime;
		changes.firePropertyChange("curTime", new Double(oldTime),
				new Double(curTime));
	}

	/**
	 * Updates all time influenced style and presentation attributes in all SVG
	 * documents in this universe.
	 */
	public void updateTime() throws SVGException {
		for (Iterator it = loadedDocs.values().iterator(); it.hasNext();) {
			SVGDiagram dia = (SVGDiagram) it.next();
			dia.updateTime(curTime);
		}
	}

	/**
	 * Called by the Font element to let the universe know that a font has been
	 * loaded and is available.
	 */
	void registerFont(Font font) {
		loadedFonts.put(font.getFontFace().getFontFamily(), font);
	}

	public Font getDefaultFont() {
		for (Iterator it = loadedFonts.values().iterator(); it.hasNext();) {
			return (Font) it.next();
		}
		return null;
	}

	public Font getFont(String fontName) {
		return (Font) loadedFonts.get(fontName);
	}

	URL registerImage(URI imageURI) {
		String scheme = imageURI.getScheme();
		if (scheme.equals("data")) {
			String path = imageURI.getRawSchemeSpecificPart();
			int idx = path.indexOf(';');
			path.substring(0, idx);
			String content = path.substring(idx + 1);

			if (content.startsWith("base64")) {
				content = content.substring(6);
				try {
					byte[] buf = Base64.decode(content);
					ByteArrayInputStream bais = new ByteArrayInputStream(buf);
					BufferedImage img = ImageIO.read(bais);

					URL url;
					int urlIdx = 0;
					while (true) {
						url = new URL("inlineImage", "localhost",
								"img" + urlIdx);
						if (!loadedImages.containsKey(url)) {
							break;
						}
						urlIdx++;
					}

					SoftReference ref = new SoftReference(img);
					loadedImages.put(url, ref);

					return url;
				} catch (IOException ex) {
					Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
							"Could not decode inline image", ex);
				}
			}
			return null;
		}
		try {
			URL url = imageURI.toURL();
			registerImage(url);
			return url;
		} catch (MalformedURLException ex) {
			Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING, "Bad url",
					ex);
		}
		return null;
	}

	void registerImage(URL imageURL) {
		if (loadedImages.containsKey(imageURL)) {
			return;
		}

		SoftReference ref;
		try {
			String fileName = imageURL.getFile();
			if (".svg".equals(
					fileName.substring(fileName.length() - 4).toLowerCase())) {
				SVGIcon icon = new SVGIcon();
				icon.setSvgURI(imageURL.toURI());

				BufferedImage img = new BufferedImage(icon.getIconWidth(),
						icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = img.createGraphics();
				icon.paintIcon(null, g, 0, 0);
				g.dispose();
				ref = new SoftReference(img);
			} else {
				BufferedImage img = ImageIO.read(imageURL);
				ref = new SoftReference(img);
			}
			loadedImages.put(imageURL, ref);
		} catch (Exception e) {
			Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
					"Could not load image: " + imageURL, e);
		}
	}

	BufferedImage getImage(URL imageURL) {
		SoftReference ref = (SoftReference) loadedImages.get(imageURL);
		if (ref == null) {
			return null;
		}

		BufferedImage img = (BufferedImage) ref.get();
		// If image was cleared from memory, reload it
		if (img == null) {
			try {
				img = ImageIO.read(imageURL);
			} catch (Exception e) {
				Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
						"Could not load image", e);
			}
			ref = new SoftReference(img);
			loadedImages.put(imageURL, ref);
		}

		return img;
	}

	/**
	 * Returns the element of the document at the given URI. If the document is
	 * not already loaded, it will be.
	 */
	public SVGElement getElement(URI path) {
		return getElement(path, true);
	}

	public SVGElement getElement(URL path) {
		try {
			URI uri = new URI(path.toString());
			return getElement(uri, true);
		} catch (Exception e) {
			Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
					"Could not parse url " + path, e);
		}
		return null;
	}

	/**
	 * Looks up a href within our universe. If the href refers to a document
	 * that is not loaded, it will be loaded. The URL #target will then be
	 * checked against the SVG diagram's index and the coresponding element
	 * returned. If there is no coresponding index, null is returned.
	 */
	public SVGElement getElement(URI path, boolean loadIfAbsent) {
		try {
			// Strip fragment from URI
			URI xmlBase = new URI(path.getScheme(),
					path.getSchemeSpecificPart(), null);

			SVGDiagram dia = (SVGDiagram) loadedDocs.get(xmlBase);
			if (dia == null && loadIfAbsent) {
				// System.err.println("SVGUnivserse: " + xmlBase.toString());
				// javax.swing.JOptionPane.showMessageDialog(null,
				// xmlBase.toString());

				try {
					URL url = xmlBase.toURL();
					loadSVG(url, false);
				} catch (java.net.MalformedURLException e) {
					Log.error(e.getMessage());
					return null;
				}

				dia = (SVGDiagram) loadedDocs.get(xmlBase);
				if (dia == null) {
					return null;
				}
			}

			String fragment = path.getFragment();
			return fragment == null ? dia.getRoot() : dia.getElement(fragment);
		} catch (Exception e) {
			Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
					"Could not parse path " + path, e);
			return null;
		}
	}

	public SVGDiagram getDiagram(URI xmlBase) {
		return getDiagram(xmlBase, true);
	}

	/**
	 * Returns the diagram that has been loaded from this root. If diagram is
	 * not already loaded, returns null.
	 */
	public SVGDiagram getDiagram(URI xmlBase, boolean loadIfAbsent) {
		if (xmlBase == null) {
			return null;
		}

		SVGDiagram dia = (SVGDiagram) loadedDocs.get(xmlBase);
		if (dia != null || !loadIfAbsent) {
			return dia;
		}

		// Load missing diagram
		try {
			URL url;
			if ("jar".equals(xmlBase.getScheme()) && xmlBase.getPath() != null
					&& !xmlBase.getPath().contains("!/")) {
				// Workaround for resources stored in jars loaded by Webstart.
				// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6753651
				url = SVGUniverse.class.getResource("xmlBase.getPath()");
			} else {
				url = xmlBase.toURL();
			}

			loadSVG(url, false);
			dia = (SVGDiagram) loadedDocs.get(xmlBase);
			return dia;
		} catch (Exception e) {
			Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
					"Could not parse", e);
		}

		return null;
	}

	/**
	 * Wraps input stream in a BufferedInputStream. If it is detected that this
	 * input stream is GZIPped, also wraps in a GZIPInputStream for inflation.
	 *
	 * @param is
	 *            Raw input stream
	 * @return Uncompressed stream of SVG data
	 * @throws java.io.IOException
	 */
	private static InputStream createDocumentInputStream(InputStream is)
			throws IOException {
		BufferedInputStream bin = new BufferedInputStream(is);
		bin.mark(2);
		int b0 = bin.read();
		int b1 = bin.read();
		bin.reset();

		// Check for gzip magic number
		if ((b1 << 8 | b0) == GZIPInputStream.GZIP_MAGIC) {
			GZIPInputStream iis = new GZIPInputStream(bin);
			return iis;
		}
		// Plain text
		return bin;
	}

	public URI loadSVG(URL docRoot) {
		return loadSVG(docRoot, false);
	}

	/**
	 * Loads an SVG file and all the files it references from the URL provided.
	 * If a referenced file already exists in the SVG universe, it is not
	 * reloaded.
	 *
	 * @param docRoot
	 *            - URL to the location where this SVG file can be found.
	 * @param forceLoad
	 *            - if true, ignore cached diagram and reload
	 * @return - The URI that refers to the loaded document
	 */
	public URI loadSVG(URL docRoot, boolean forceLoad) {
		try {
			URI uri = new URI(docRoot.toString());
			if (loadedDocs.containsKey(uri) && !forceLoad) {
				return uri;
			}

			InputStream is = docRoot.openStream();
			return loadSVG(uri, new InputSource(createDocumentInputStream(is)));
		} catch (URISyntaxException ex) {
			Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
					"Could not parse", ex);
		} catch (IOException e) {
			Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
					"Could not parse", e);
		}

		return null;
	}

	public URI loadSVG(InputStream is, String name) throws IOException {
		return loadSVG(is, name, false);
	}

	public URI loadSVG(InputStream is, String name, boolean forceLoad)
			throws IOException {
		URI uri = getStreamBuiltURI(name);
		if (uri == null) {
			return null;
		}
		if (loadedDocs.containsKey(uri) && !forceLoad) {
			return uri;
		}

		return loadSVG(uri, new InputSource(createDocumentInputStream(is)));
	}

	public URI loadSVG(Reader reader, String name) {
		return loadSVG(reader, name, false);
	}

	/**
	 * This routine allows you to create SVG documents from data streams that
	 * may not necessarily have a URL to load from. Since every SVG document
	 * must be identified by a unique URL, Salamander provides a method to fake
	 * this for streams by defining it's own protocol - svgSalamander - for SVG
	 * documents without a formal URL.
	 *
	 * @param reader
	 *            - A stream containing a valid SVG document
	 * @param name
	 *            -
	 *            <p>
	 *            A unique name for this document. It will be used to construct
	 *            a unique URI to refer to this document and perform resolution
	 *            with relative URIs within this document.
	 *            </p>
	 *            <p>
	 *            For example, a name of "/myScene" will produce the URI
	 *            svgSalamander:/myScene. "/maps/canada/toronto" will produce
	 *            svgSalamander:/maps/canada/toronto. If this second document
	 *            then contained the href "../uk/london", it would resolve by
	 *            default to svgSalamander:/maps/uk/london. That is, SVG
	 *            Salamander defines the URI scheme svgSalamander for it's own
	 *            internal use and uses it for uniquely identfying documents
	 *            loaded by stream.
	 *            </p>
	 *            <p>
	 *            If you need to link to documents outside of this scheme, you
	 *            can either supply full hrefs (eg,
	 *            href="url(http://www.kitfox.com/index.html)") or put the
	 *            xml:base attribute in a tag to change the defaultbase URIs are
	 *            resolved against
	 *            </p>
	 *            <p>
	 *            If a name does not start with the character '/', it will be
	 *            automatically prefixed to it.
	 *            </p>
	 * @param forceLoad
	 *            - if true, ignore cached diagram and reload
	 *
	 * @return - The URI that refers to the loaded document
	 */
	public URI loadSVG(Reader reader, String name, boolean forceLoad) {
		// System.err.println(url.toString());
		// Synthesize URI for this stream
		URI uri = getStreamBuiltURI(name);
		if (uri == null) {
			return null;
		}
		if (loadedDocs.containsKey(uri) && !forceLoad) {
			return uri;
		}

		return loadSVG(uri, new InputSource(reader));
	}

	/**
	 * Synthesize a URI for an SVGDiagram constructed from a stream.
	 *
	 * @param name
	 *            - Name given the document constructed from a stream.
	 */
	public URI getStreamBuiltURI(String name) {
		if (name == null || name.length() == 0) {
			return null;
		}

		if (name.charAt(0) != '/') {
			name = '/' + name;
		}

		try {
			// Dummy URL for SVG documents built from image streams
			return new URI(INPUTSTREAM_SCHEME, name, null);
		} catch (Exception e) {
			Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
					"Could not parse", e);
			return null;
		}
	}

	private XMLReader getXMLReaderCached() throws SAXException {
		if (cachedReader == null) {
			cachedReader = XMLReaderFactory.createXMLReader();
		}
		return cachedReader;
	}

	protected URI loadSVG(URI xmlBase, InputSource is) {
		// Use an instance of ourselves as the SAX event handler
		SVGLoader handler = new SVGLoader(xmlBase, this, verbose);

		// Place this docment in the universe before it is completely loaded
		// so that the load process can refer to references within it's current
		// document
		loadedDocs.put(xmlBase, handler.getLoadedDiagram());

		try {
			// Parse the input
			XMLReader reader = getXMLReaderCached();
			reader.setEntityResolver(new EntityResolver() {
				@Override
				public InputSource resolveEntity(String publicId,
						String systemId) {
					// Ignore all DTDs
					return new InputSource(
							new ByteArrayInputStream(new byte[0]));
				}
			});
			reader.setContentHandler(handler);
			reader.parse(is);

			handler.getLoadedDiagram().updateTime(curTime);
			return xmlBase;
		} catch (SAXParseException sex) {
			System.err.println("Error processing " + xmlBase);
			System.err.println(sex.getMessage());

			loadedDocs.remove(xmlBase);
			return null;
		} catch (Throwable e) {
			Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
					"Could not load SVG " + xmlBase, e);
		}

		return null;
	}

	/**
	 * Get list of uris of all loaded documents and subdocuments.
	 * 
	 * @return
	 */
	public ArrayList getLoadedDocumentURIs() {
		return new ArrayList(loadedDocs.keySet());
	}

	/**
	 * Remove loaded document from cache.
	 * 
	 * @param uri
	 */
	public void removeDocument(URI uri) {
		loadedDocs.remove(uri);
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Uses serialization to duplicate this universe.
	 */
	public SVGUniverse duplicate() throws IOException, ClassNotFoundException {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(bs);
		os.writeObject(this);
		os.close();

		ByteArrayInputStream bin = new ByteArrayInputStream(bs.toByteArray());
		ObjectInputStream is = new ObjectInputStream(bin);
		SVGUniverse universe = (SVGUniverse) is.readObject();
		is.close();

		return universe;
	}
}

// Copyright 2001-2005, FreeHEP
package org.freehep.graphicsio.pdf;

import java.awt.Color;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.freehep.graphicsio.ImageConstants;

/**
 * Delay <tt>Image</tt> objects for writing XObjects to the pdf file when the
 * pageStream is complete. Caches identical images to only write them once.
 * 
 * @author Simon Fischer
 * @author Mark Donszelmann
 * @version $Id: PDFImageDelayQueue.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class PDFImageDelayQueue {

	private int currentNumber = 0;

	private class Entry {
		private RenderedImage image;

		private String name, maskName;

		private Color bkg;

		private String writeAs;

		private boolean written;

		private Entry(RenderedImage image, Color bkg, String writeAs) {
			this.image = image;
			this.bkg = bkg;
			this.writeAs = writeAs;
			this.name = "Img" + (currentNumber++);
			if (image.getColorModel().hasAlpha() && (bkg == null)) {
				maskName = name + "Mask";
			} else {
				maskName = null;
			}
			this.written = false;
		}
	}

	private Map/* <RenderedImage,Entry> */ imageMap;

	private List/* <entry> */ imageList;

	private PDFWriter pdf;

	public PDFImageDelayQueue(PDFWriter pdf) {
		this.pdf = pdf;
		this.imageMap = new HashMap();
		this.imageList = new LinkedList();
	}

	public PDFName delayImage(RenderedImage image, Color bkg, String writeAs) {
		Entry entry = (Entry) imageMap.get(image);
		if (entry == null) {
			entry = new Entry(image, bkg, writeAs);
			imageMap.put(image, entry);
			imageList.add(entry);
		}

		return pdf.name(entry.name);
	}

	/** Creates a stream for every delayed image that is not written yet. */
	public void processAll() throws IOException {
		for (Iterator i = imageList.iterator(); i.hasNext();) {
			Entry entry = (Entry) i.next();

			if (!entry.written) {
				entry.written = true;

				String[] encode;
				if (entry.writeAs.equals(ImageConstants.ZLIB)
						|| (entry.maskName != null)) {
					encode = new String[] { "Flate", "ASCII85" };
				} else if (entry.writeAs.equals(ImageConstants.JPG)) {
					encode = new String[] { "DCT", "ASCII85" };
				} else {
					encode = new String[] { null, "ASCII85" };
				}

				PDFStream img = pdf.openStream(entry.name);
				img.entry("Subtype", pdf.name("Image"));
				if (entry.maskName != null) {
					img.entry("SMask", pdf.ref(entry.maskName));
				}
				img.image(entry.image, entry.bkg, encode);
				pdf.close(img);

				if (entry.maskName != null) {
					PDFStream mask = pdf.openStream(entry.maskName);
					mask.entry("Subtype", pdf.name("Image"));
					mask.imageMask(entry.image, encode);
					pdf.close(mask);
				}
			}
		}
	}

	/**
	 * Adds all names to the dictionary which should be the value of the
	 * resources dicionrary's /XObject entry.
	 */
	public int addXObjects() throws IOException {
		if (imageList.size() > 0) {
			PDFDictionary xobj = pdf.openDictionary("XObjects");
			for (Iterator i = imageList.iterator(); i.hasNext();) {
				Entry entry = (Entry) i.next();
				xobj.entry(entry.name, pdf.ref(entry.name));
				if (entry.maskName != null) {
					xobj.entry(entry.maskName, pdf.ref(entry.maskName));
				}
			}
			pdf.close(xobj);
		}
		return imageList.size();
	}
}

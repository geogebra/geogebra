// Copyright 2000-2006 FreeHEP
package org.freehep.graphicsio.pdf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.freehep.graphics2d.TagString;
import org.freehep.graphics2d.font.Lookup;
import org.freehep.graphicsio.AbstractVectorGraphicsIO;
import org.freehep.graphicsio.FontConstants;
import org.freehep.graphicsio.ImageConstants;
import org.freehep.graphicsio.ImageGraphics2D;
import org.freehep.graphicsio.InfoConstants;
import org.freehep.graphicsio.MultiPageDocument;
import org.freehep.graphicsio.PageConstants;
import org.freehep.graphicsio.font.FontUtilities;
import org.freehep.util.UserProperties;

/**
 * Implementation of <tt>VectorGraphics</tt> that writes the output to a PDF
 * file. Users of this class have to generate a <tt>PDFWriter</tt> and create
 * an instance by invoking the factory method or the constructor. Document
 * specific settings like page size can then be made by the appropriate setter
 * methods. Before starting to draw, <tt>startExport()</tt> must be called.
 * When drawing is finished, call <tt>endExport()</tt>.
 * 
 * @author Simon Fischer
 * @author Mark Donszelmann
 * @version $Id: PDFGraphics2D.java,v 1.7 2009-08-17 21:44:44 murkle Exp $
 */
public class PDFGraphics2D extends AbstractVectorGraphicsIO implements
		MultiPageDocument, FontUtilities.ShowString {

	/*
	 * ================================================================================
	 * Table of Contents: ------------------ 1. Constructors & Factory Methods
	 * 2. Document Settings 3. Header, Trailer, Multipage & Comments 3.1 Header &
	 * Trailer 3.2 MultipageDocument methods 4. Create & Dispose 5. Drawing
	 * Methods 5.1. shapes (draw/fill) 5.1.1. lines, rectangles, round
	 * rectangles 5.1.2. polylines, polygons 5.1.3. ovals, arcs 5.1.4. shapes
	 * 5.2. Images 5.3. Strings 6. Transformations 7. Clipping 8. Graphics State /
	 * Settings 8.1. stroke/linewidth 8.2. paint/color 8.3. font 8.4. rendering
	 * hints 9. Private/Utility Methods 9.1. drawing, shape creation 9.2. font,
	 * strings 9.3. images 9.4. transformations 10. Auxiliary
	 * ================================================================================
	 */

	private static final String rootKey = PDFGraphics2D.class.getName();

	public static final String VERSION6 = "Acrobat Reader 6.x";

	public static final String VERSION5 = "Acrobat Reader 5.x";

	public static final String VERSION4 = "Acrobat Reader 4.x";

	public static final String TRANSPARENT = rootKey + "."
			+ PageConstants.TRANSPARENT;

	public static final String BACKGROUND = rootKey + "."
			+ PageConstants.BACKGROUND;

	public static final String BACKGROUND_COLOR = rootKey + "."
			+ PageConstants.BACKGROUND_COLOR;

	public static final String PAGE_SIZE = rootKey + "."
			+ PageConstants.PAGE_SIZE;

	public static final String PAGE_MARGINS = rootKey + "."
			+ PageConstants.PAGE_MARGINS;

	public static final String ORIENTATION = rootKey + "."
			+ PageConstants.ORIENTATION;

	public static final String FIT_TO_PAGE = rootKey + "."
			+ PageConstants.FIT_TO_PAGE;

	public static final String EMBED_FONTS = rootKey + "."
			+ FontConstants.EMBED_FONTS;

	public static final String EMBED_FONTS_AS = rootKey + "."
			+ FontConstants.EMBED_FONTS_AS;

	public static final String THUMBNAILS = rootKey + ".Thumbnails";

	public static final String THUMBNAIL_SIZE = rootKey + ".ThumbnailSize";

	public static final String COMPRESS = rootKey + ".Compress";

	public static final String VERSION = rootKey + ".Version";

	public static final String WRITE_IMAGES_AS = rootKey + "."
			+ ImageConstants.WRITE_IMAGES_AS;

	public static final String AUTHOR = rootKey + "." + InfoConstants.AUTHOR;

	public static final String TITLE = rootKey + "." + InfoConstants.TITLE;

	public static final String SUBJECT = rootKey + "." + InfoConstants.SUBJECT;

	public static final String KEYWORDS = rootKey + "."
			+ InfoConstants.KEYWORDS;

	private static final UserProperties defaultProperties = new UserProperties();
	static {
		defaultProperties.setProperty(TRANSPARENT, true);
		defaultProperties.setProperty(BACKGROUND, false);
		defaultProperties.setProperty(BACKGROUND_COLOR, Color.GRAY);

		defaultProperties.setProperty(VERSION, VERSION5);
		defaultProperties.setProperty(COMPRESS, true);
		defaultProperties.setProperty(PAGE_SIZE, PageConstants.INTERNATIONAL);
		defaultProperties.setProperty(PAGE_MARGINS, PageConstants
				.getMargins(PageConstants.SMALL));
		defaultProperties.setProperty(ORIENTATION, PageConstants.PORTRAIT);
		defaultProperties.setProperty(FIT_TO_PAGE, true);
		defaultProperties.setProperty(EMBED_FONTS, false);
		defaultProperties.setProperty(EMBED_FONTS_AS,
				FontConstants.EMBED_FONTS_TYPE3);
		defaultProperties.setProperty(THUMBNAILS, defaultProperties
				.getProperty(VERSION).equals(VERSION4));
		defaultProperties.setProperty(THUMBNAIL_SIZE, new Dimension(128, 128));
		defaultProperties.setProperty(WRITE_IMAGES_AS, ImageConstants.SMALLEST);

		defaultProperties.setProperty(AUTHOR, "");
		defaultProperties.setProperty(TITLE, "");
		defaultProperties.setProperty(SUBJECT, "");
		defaultProperties.setProperty(KEYWORDS, "");

		defaultProperties.setProperty(CLIP, true);
		defaultProperties.setProperty(TEXT_AS_SHAPES, true);
	}

	public static Properties getDefaultProperties() {
		return defaultProperties;
	}

	public static void setDefaultProperties(Properties newProperties) {
		defaultProperties.setProperties(newProperties);
	}

	public static final String version = "$Revision: 1.7 $";

	private static final String PDF_VERSION = "1.4";

	private static final String[] COMPRESS_FILTERS = { "Flate", "ASCII85" };

	private static final String[] NO_FILTERS = {};

	private static final double FONTSIZE_CORRECTION = 1.0;

	/*
	 * Not Used private static final CharTable STANDARD_CHAR_TABLES[] = {
	 * Lookup.getInstance().getTable("PDFLatin"),
	 * Lookup.getInstance().getTable("Symbol"),
	 * Lookup.getInstance().getTable("Zapfdingbats") };
	 * 
	 * private static final Font STANDARD_FONT[] = { null, new Font("Symbol",
	 * Font.PLAIN, 10), new Font("ZapfDingbats", Font.PLAIN, 10), };
	 */

	// output
	private OutputStream ros;

	private PDFWriter os;

	private PDFStream pageStream;

	// remember some things to do
	private PDFFontTable fontTable; // remember which standard fonts were used

	private PDFImageDelayQueue delayImageQueue; // remember images XObjects to

	// include in the file

	private PDFPaintDelayQueue delayPaintQueue; // remember patterns to include

	// in the file

	// multipage
	private int currentPage;

	private boolean multiPage;

	private TagString[] headerText;

	private int headerUnderline;

	private Font headerFont;

	private TagString[] footerText;

	private int footerUnderline;

	private Font footerFont;

	private List titles;

	// extra pointers
	int alphaIndex;

	Map extGStates;

	private Dimension pageSize = null;

	/*
	 * ================================================================================ |
	 * 1. Constructors & Factory Methods
	 * ================================================================================
	 */

	public PDFGraphics2D(File file, Dimension size)
			throws FileNotFoundException {
		this(new FileOutputStream(file), size);
	}

	public PDFGraphics2D(File file, Component component)
			throws FileNotFoundException {
		this(new FileOutputStream(file), component);
	}

	public PDFGraphics2D(OutputStream ros, Dimension size) {
		super(size, false);
		init(ros);
	}

	public PDFGraphics2D(OutputStream ros, Component component) {
		super(component, false);
		init(ros);
	}

	private void init(OutputStream ros) {
		this.ros = new BufferedOutputStream(ros);

		currentPage = 0;
		multiPage = false;
		titles = new ArrayList();
		initProperties(defaultProperties);
	}

	/** Cloneconstructor */
	protected PDFGraphics2D(PDFGraphics2D graphics, boolean doRestoreOnDispose) {
		super(graphics, doRestoreOnDispose);

		this.os = graphics.os;
		this.pageStream = graphics.pageStream;

		this.delayImageQueue = graphics.delayImageQueue;
		this.delayPaintQueue = graphics.delayPaintQueue;
		this.fontTable = graphics.fontTable;

		this.currentPage = graphics.currentPage;
		this.multiPage = graphics.multiPage;
		this.titles = graphics.titles;

		this.alphaIndex = graphics.alphaIndex;
		this.extGStates = graphics.extGStates;
	}

	/*
	 * ================================================================================ |
	 * 2. Document Settings
	 * ================================================================================
	 */
	public void setMultiPage(boolean multiPage) {
		this.multiPage = multiPage;
	}

	public boolean isMultiPage() {
		return multiPage;
	}

	/**
	 * Set the clipping enabled flag. This will affect all output operations
	 * after this call completes. In some circumstances the clipping region is
	 * set incorrectly (not yet understood; AWT seems to not correctly dispose
	 * of graphic contexts). A workaround is to simply switch it off.
	 */
	public static void setClipEnabled(boolean enabled) {
		defaultProperties.setProperty(CLIP, enabled);
	}

	/*
	 * ================================================================================ |
	 * 3. Header, Trailer, Multipage & Comments
	 * ================================================================================
	 */
	/* 3.1 Header & Trailer */

	/**
	 * Writes the catalog, docinfo, preferences, and (as we use only single page
	 * output the page tree.
	 */
	public void writeHeader() throws IOException {
		os = new PDFWriter(new BufferedOutputStream(ros), PDF_VERSION);

		delayImageQueue = new PDFImageDelayQueue(os);
		delayPaintQueue = new PDFPaintDelayQueue(os, delayImageQueue);

		fontTable = new PDFFontTable(os);

		String producer = getClass().getName();
		if (!isDeviceIndependent()) {
			producer += " " + version.substring(1, version.length() - 1);
		}

		PDFDocInfo info = os.openDocInfo("DocInfo");
		info.setTitle(getProperty(TITLE));
		info.setAuthor(getProperty(AUTHOR));
		info.setSubject(getProperty(SUBJECT));
		info.setKeywords(getProperty(KEYWORDS));

		info.setCreator(getCreator());
		info.setProducer(producer);
		if (!isDeviceIndependent()) {
			Calendar now = Calendar.getInstance();
			info.setCreationDate(now);
			info.setModificationDate(now);
		}
		info.setTrapped("False");
		os.close(info);

		// catalog
		PDFCatalog catalog = os.openCatalog("Catalog", "RootPage");
		catalog.setOutlines("Outlines");
		catalog.setPageMode("UseOutlines");
		catalog.setViewerPreferences("Preferences");
		catalog.setOpenAction(new Object[] { os.ref("Page1"), os.name("Fit") });
		os.close(catalog);

		// preferences
		PDFViewerPreferences prefs = os.openViewerPreferences("Preferences");
		prefs.setFitWindow(true);
		prefs.setCenterWindow(false);
		os.close(prefs);

		// extra stuff
		alphaIndex = 1;
		extGStates = new HashMap();

		// hide the multipage functionality to the user in case of single page
		// output by opening the first and only page immediately
		if (!isMultiPage())
			openPage(getSize(), null, getComponent());
	}

	public void writeBackground() {
		if (isProperty(TRANSPARENT)) {
			setBackground(null);
		} else if (isProperty(BACKGROUND)) {
			setBackground(getPropertyColor(BACKGROUND_COLOR));
			clearRect(0.0, 0.0, getSize().width, getSize().height);
		} else {
			setBackground(getComponent() != null ? getComponent()
					.getBackground() : Color.WHITE);
			clearRect(0.0, 0.0, getSize().width, getSize().height);
		}
	}

	public void writeTrailer() throws IOException {
		if (!isMultiPage())
			closePage();

		// pages
		PDFPageTree pages = os.openPageTree("RootPage", null);
		for (int i = 1; i <= currentPage; i++) {
			pages.addPage("Page" + i);
		}
		Dimension pageSize = getSize(getProperty(PAGE_SIZE),
				getProperty(ORIENTATION));
		pages.setMediaBox(0, 0, pageSize.getWidth(), pageSize.getHeight());
		pages.setResources("Resources");
		os.close(pages);

		// ProcSet
		os.object("PageProcSet", new Object[] { os.name("PDF"),
				os.name("Text"), os.name("ImageC") });

		// Font
		int nFonts = fontTable.addFontDictionary();

		// XObject
		int nXObjects = delayImageQueue.addXObjects();

		// Pattern
		int nPatterns = delayPaintQueue.addPatterns();

		// ExtGState
		if (extGStates.size() > 0) {
			PDFDictionary extGState = os.openDictionary("ExtGState");

			for (Iterator i = extGStates.keySet().iterator(); i.hasNext();) {
				Float alpha = (Float) i.next();
				String alphaName = (String) extGStates.get(alpha);
				PDFDictionary alphaDictionary = extGState
						.openDictionary(alphaName);
				alphaDictionary.entry("ca", alpha.floatValue());
				alphaDictionary.entry("CA", alpha.floatValue());
				alphaDictionary.entry("BM", os.name("Normal"));
				alphaDictionary.entry("AIS", false);
				extGState.close(alphaDictionary);
			}
			os.close(extGState);
		}

		// resources
		PDFDictionary resources = os.openDictionary("Resources");
		resources.entry("ProcSet", os.ref("PageProcSet"));
		if (nFonts > 0)
			resources.entry("Font", os.ref("FontList"));
		if (nXObjects > 0)
			resources.entry("XObject", os.ref("XObjects"));
		if (nPatterns > 0)
			resources.entry("Pattern", os.ref("Pattern"));
		if (extGStates.size() > 0)
			resources.entry("ExtGState", os.ref("ExtGState"));
		os.close(resources);

		// outlines
		PDFOutlineList outlines = os.openOutlineList("Outlines", "Outline1",
				"Outline" + currentPage);
		os.close(outlines);

		for (int i = 1; i <= currentPage; i++) {
			String prev = i > 1 ? "Outline" + (i - 1) : null;
			String next = i < currentPage ? "Outline" + (i + 1) : null;
			PDFOutline outline = os.openOutline("Outline" + i, (String) titles
					.get(i - 1), "Outlines", prev, next);
			outline
					.setDest(new Object[] { os.ref("Page" + i), os.name("Fit") });
			os.close(outline);
		}

		// delayed objects (images, patterns, fonts)
		processDelayed();
	}

	public void closeStream() throws IOException {
		os.close();
	}

	private void processDelayed() throws IOException {
		delayImageQueue.processAll();
		delayPaintQueue.processAll();
		fontTable.embedAll(getFontRenderContext(), isProperty(EMBED_FONTS),
				getProperty(EMBED_FONTS_AS));
	}

	/* 3.2 MultipageDocument methods */
	public void openPage(Component component) throws IOException {
		openPage(component.getSize(), component.getName(), component);
	}

	public void openPage(Dimension size, String title) throws IOException {
		openPage(size, title, null);
	}

	private void openPage(Dimension size, String title, Component component)
			throws IOException {
		if (size == null)
			size = component.getSize();

		resetClip(new Rectangle(0, 0, size.width, size.height));

		if (pageStream != null) {
			writeWarning("Page " + currentPage + " already open. "
					+ "Call closePage() before starting a new one.");
			return;
		}

		BufferedImage thumbnail = null;
		// prepare thumbnail if possible
		if ((component != null) && isProperty(PDFGraphics2D.THUMBNAILS)) {
			thumbnail = ImageGraphics2D.generateThumbnail(component,
					getPropertyDimension(PDFGraphics2D.THUMBNAIL_SIZE));
		}

		currentPage++;

		if (title == null)
			title = "Page " + currentPage + " (untitled)";
		titles.add(title);

		PDFPage page = os.openPage("Page" + currentPage, "RootPage");
		page.setContents("PageContents" + currentPage);

		if (thumbnail != null)
			page.setThumb("Thumb" + currentPage);

		os.close(page);

		if (thumbnail != null) {
			PDFStream thumbnailStream = os.openStream("Thumb" + currentPage);
			thumbnailStream.image(thumbnail, Color.black, COMPRESS_FILTERS);
			os.close(thumbnailStream);
		}

		pageStream = os.openStream("PageContents" + currentPage,
				isProperty(COMPRESS) ? COMPRESS_FILTERS : NO_FILTERS);

		// transform the coordinate system as necessary
		// 1. flip the coordinate system down and translate it upwards again
		// so that the origin is the upper left corner of the page.
		AffineTransform pageTrafo = new AffineTransform();
		pageTrafo.scale(1, -1);
		Dimension pageSize = getSize(getProperty(PAGE_SIZE),
				getProperty(ORIENTATION));
		Insets margins = PageConstants.getMargins(
				getPropertyInsets(PAGE_MARGINS), getProperty(ORIENTATION));
		pageTrafo
				.translate(margins.left, -(pageSize.getHeight() - margins.top));

		// in between write the header and footer (which should not be scaled!)
		writeHeadline(pageTrafo);
		writeFootline(pageTrafo);

		// 2. check whether we have to rescale the image to fit onto the page
		double scaleFactor = Math.min(getWidth() / size.width, getHeight()
				/ size.height);
		if ((scaleFactor < 1) || isProperty(FIT_TO_PAGE)) {
			pageTrafo.scale(scaleFactor, scaleFactor);
		} else {
			scaleFactor = 1;
		}

		// 3. center the image on the page
		double dx = (getWidth() - size.width * scaleFactor) / 2 / scaleFactor;
		double dy = (getHeight() - size.height * scaleFactor) / 2 / scaleFactor;
		pageTrafo.translate(dx, dy);

		writeTransform(pageTrafo);

		// save the graphics context resets before setClip
		writeGraphicsSave();

		clipRect(0, 0, size.width, size.height);

		// save the graphics context resets before setClip
		writeGraphicsSave();

		delayPaintQueue.setPageMatrix(pageTrafo);

		writeGraphicsState();
		writeBackground();
	}

	public void closePage() throws IOException {
		if (pageStream == null) {
			writeWarning("Page " + currentPage + " already closed. "
					+ "Call openPage() to start a new one.");
			return;
		}
		os.close(pageStream);
		pageStream = null;

		processDelayed(); // This does not work properly with acrobat reader
		// 4!
	}

	public void setHeader(Font font, TagString left, TagString center,
			TagString right, int underlineThickness) {
		this.headerFont = font;
		this.headerText = new TagString[3];
		this.headerText[0] = left;
		this.headerText[1] = center;
		this.headerText[2] = right;
		this.headerUnderline = underlineThickness;
	}

	public void setFooter(Font font, TagString left, TagString center,
			TagString right, int underlineThickness) {
		this.footerFont = font;
		this.footerText = new TagString[3];
		this.footerText[0] = left;
		this.footerText[1] = center;
		this.footerText[2] = right;
		this.footerUnderline = underlineThickness;
	}

	private void writeHeadline(AffineTransform pageTrafo) throws IOException {
		if (headerText != null) {
			LineMetrics metrics = headerFont.getLineMetrics("mM",
					getFontRenderContext());
			writeLine(pageTrafo, headerFont, headerText, -metrics.getLeading()
					- headerFont.getSize2D() / 2, TEXT_BOTTOM, -headerFont
					.getSize2D() / 2, headerUnderline);

		}
	}

	private void writeFootline(AffineTransform pageTrafo) throws IOException {
		if (footerText != null) {
			LineMetrics metrics = footerFont.getLineMetrics("mM",
					getFontRenderContext());
			double y = getHeight() + footerFont.getSize2D() / 2;
			writeLine(pageTrafo, footerFont, footerText, y
					+ metrics.getLeading(), TEXT_TOP, y, footerUnderline);
		}
	}

	private void writeLine(AffineTransform trafo, Font font, TagString[] text,
			double ty, int yAlign, double ly, int underline) throws IOException {
		writeGraphicsSave();
		setColor(Color.black);
		setFont(font);
		writeTransform(trafo);
		if (text[0] != null)
			drawString(text[0], 0, ty, TEXT_LEFT, yAlign);
		if (text[1] != null)
			drawString(text[1], getWidth() / 2, ty, TEXT_CENTER, yAlign);
		if (text[2] != null)
			drawString(text[2], getWidth(), ty, TEXT_RIGHT, yAlign);
		if (underline >= 0) {
			setLineWidth((double) underline);
			drawLine(0, ly, getWidth(), ly);
		}
		writeGraphicsRestore();
	}

	/*
	 * ================================================================================ |
	 * 4. Create & Dispose
	 * ================================================================================
	 */

	public Graphics create() {
		try {
			writeGraphicsSave();
		} catch (IOException e) {
			handleException(e);
		}
		return new PDFGraphics2D(this, true);
	}

	public Graphics create(double x, double y, double width, double height) {
		try {
			writeGraphicsSave();
		} catch (IOException e) {
			handleException(e);
		}
		PDFGraphics2D graphics = new PDFGraphics2D(this, true);
		graphics.translate(x, y);
		graphics.clipRect(0, 0, width, height);
		return graphics;
	}

	protected void writeGraphicsSave() throws IOException {
		pageStream.save();
	}

	protected void writeGraphicsRestore() throws IOException {
		pageStream.restore();
	}

	/*
	 * ================================================================================ |
	 * 5. Drawing Methods
	 * ================================================================================ /*
	 * 5.1.4. shapes
	 */
	public void draw(Shape s) {
		try {
			if (getStroke() instanceof BasicStroke) {
				// in this case we've already handled the stroke
				pageStream.drawPath(s);
				pageStream.stroke();
			} else {
				// otherwise handle it now
				pageStream.drawPath(getStroke().createStrokedShape(s));
				pageStream.fill();
			}
		} catch (IOException e) {
			handleException(e);
		}
	}

	public void fill(Shape s) {
		try {
			boolean eofill = pageStream.drawPath(s);
			if (eofill) {
				pageStream.fillEvenOdd();
			} else {
				pageStream.fill();
			}
		} catch (IOException e) {
			handleException(e);
		}
	}

	// TODO: Does not use current stroke yet
	public void fillAndDraw(Shape s, Color fillColor) {
		try {
			writeGraphicsSave();
			setNonStrokeColor(fillColor);
			boolean eofill = pageStream.drawPath(s);
			if (eofill) {
				pageStream.fillEvenOddAndStroke();
			} else {
				pageStream.fillAndStroke();
			}
			writeGraphicsRestore();
		} catch (IOException e) {
			handleException(e);
		}
	}

	/* 5.2 Images */
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		writeWarning(getClass()
				+ ": copyArea(int, int, int, int, int, int) not implemented.");
	}

	protected void writeImage(RenderedImage image, AffineTransform xform,
			Color bkg) throws IOException {
		PDFName ref = delayImageQueue.delayImage(image, bkg,
				getProperty(WRITE_IMAGES_AS));

		AffineTransform imageTransform = new AffineTransform(image.getWidth(),
				0.0, 0.0, -image.getHeight(), 0.0, image.getHeight());
		xform.concatenate(imageTransform);

		writeGraphicsSave();
		pageStream.matrix(xform);
		pageStream.xObject(ref);
		writeGraphicsRestore();
	}

	/* 5.3. Strings */
	protected void writeString(String str, double x, double y)
			throws IOException {
		// save the graphics context, especially the transformation matrix
		writeGraphicsSave();

		// translate the offset to x and y
		AffineTransform at = new AffineTransform(1, 0, 0, 1, x, y);
		// transform for font
		at.concatenate(getFont().getTransform());
		// mirror the matrix
		at.scale(1, -1);

		// write transform
		writeTransform(at);

		pageStream.beginText();
		pageStream.text(0, 0);
		showCharacterCodes(str);
		pageStream.endText();

		// restore the transformation matrix
		writeGraphicsRestore();
	}

	/*
	 * ================================================================================ |
	 * 6. Transformations
	 * ================================================================================
	 */
	/** Write the given transformation matrix to the file. */
	protected void writeTransform(AffineTransform t) throws IOException {
		pageStream.matrix(t);
	}

	/*
	 * ================================================================================ |
	 * 7. Clipping
	 * ================================================================================
	 */
	protected void writeSetClip(Shape s) throws IOException {
		// clear old clip
		try {
			AffineTransform at = getTransform();
			Stroke stroke = getStroke();

			writeGraphicsRestore();
			writeGraphicsSave();

			writeStroke(stroke);
			writeTransform(at);
		} catch (IOException e) {
			handleException(e);
		}

		// write clip
		writeClip(s);
	}

	protected void writeClip(Shape s) throws IOException {
		if (s == null || !isProperty(CLIP)) {
			return;
		}

		if (s instanceof Rectangle2D) {
			pageStream.move(((Rectangle2D) s).getMinX(), ((Rectangle2D) s)
					.getMinY());
			pageStream.line(((Rectangle2D) s).getMaxX(), ((Rectangle2D) s)
					.getMinY());
			pageStream.line(((Rectangle2D) s).getMaxX(), ((Rectangle2D) s)
					.getMaxY());
			pageStream.line(((Rectangle2D) s).getMinX(), ((Rectangle2D) s)
					.getMaxY());
			pageStream.closePath();
			pageStream.clip();
			pageStream.endPath();
		} else {
			boolean eoclip = pageStream.drawPath(s);
			if (eoclip) {
				pageStream.clipEvenOdd();
			} else {
				pageStream.clip();
			}

			pageStream.endPath();
		}
	}

	/*
	 * ================================================================================ |
	 * 8. Graphics State
	 * ================================================================================
	 */
	/* 8.1. stroke/linewidth */
	protected void writeWidth(float width) throws IOException {
		pageStream.width(width);
	}

	protected void writeCap(int cap) throws IOException {
		switch (cap) {
		default:
		case BasicStroke.CAP_BUTT:
			pageStream.cap(0);
			break;
		case BasicStroke.CAP_ROUND:
			pageStream.cap(1);
			break;
		case BasicStroke.CAP_SQUARE:
			pageStream.cap(2);
			break;
		}
	}

	protected void writeJoin(int join) throws IOException {
		switch (join) {
		default:
		case BasicStroke.JOIN_MITER:
			pageStream.join(0);
			break;
		case BasicStroke.JOIN_ROUND:
			pageStream.join(1);
			break;
		case BasicStroke.JOIN_BEVEL:
			pageStream.join(2);
			break;
		}
	}

	protected void writeMiterLimit(float limit) throws IOException {
		pageStream.mitterLimit(limit);
	}

	protected void writeDash(float[] dash, float phase) throws IOException {
		pageStream.dash(dash, phase);
	}

	/* 8.2. paint/color */
	public void setPaintMode() {
		writeWarning(getClass() + ": setPaintMode() not implemented.");
	}

	public void setXORMode(Color c1) {
		writeWarning(getClass() + ": setXORMode(Color) not implemented.");
	}

	protected void writePaint(Color c) throws IOException {
		float[] cc = c.getRGBComponents(null);
		// System.out.println("alpha = "+cc[3]);
		Float alpha = new Float(cc[3]);
		String alphaName = (String) extGStates.get(alpha);
		if (alphaName == null) {
			alphaName = "Alpha" + alphaIndex;
			alphaIndex++;
			extGStates.put(alpha, alphaName);
		}
		pageStream.state(os.name(alphaName));
		pageStream.colorSpace(cc[0], cc[1], cc[2]);
		pageStream.colorSpaceStroke(cc[0], cc[1], cc[2]);
	}

	protected void writePaint(GradientPaint c) throws IOException {
		writePaint((Paint) c);
	}

	protected void writePaint(TexturePaint c) throws IOException {
		writePaint((Paint) c);
	}

	protected void writePaint(Paint paint) throws IOException {
		pageStream.colorSpace(os.name("Pattern"));
		pageStream.colorSpaceStroke(os.name("Pattern"));
		PDFName shadingName = delayPaintQueue.delayPaint(paint, getTransform(),
				getProperty(WRITE_IMAGES_AS));
		pageStream.colorSpace(null, shadingName);
		pageStream.colorSpaceStroke(new double[] {}, shadingName);
	}

	protected void setNonStrokeColor(Color c) throws IOException {
		float[] cc = c.getRGBColorComponents(null);
		pageStream.colorSpace(cc[0], cc[1], cc[2]);
	}

	protected void setStrokeColor(Color c) throws IOException {
		float[] cc = c.getRGBColorComponents(null);
		pageStream.colorSpaceStroke(cc[0], cc[1], cc[2]);
	}

	/* 8.3. font */
	protected void writeFont(Font font) throws IOException {
		// written when needed
	}

	/*
	 * ================================================================================ |
	 * 9. Auxiliary
	 * ================================================================================
	 */
	public GraphicsConfiguration getDeviceConfiguration() {
		writeWarning(getClass() + ": getDeviceConfiguration() not implemented.");
		return null;
	}

	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		writeWarning(getClass()
				+ ": hit(Rectangle, Shape, boolean) not implemented.");
		return false;
	}

	public void writeComment(String comment) throws IOException {
		// comments are ignored and disabled, because they confuse compressed
		// streams
	}

	public String toString() {
		return "PDFGraphics2D";
	}

	/*
	 * ================================================================================ |
	 * 10. Private/Utility
	 * ================================================================================
	 */
	public void showString(Font font, String str) throws IOException {
		String fontRef = fontTable.fontReference(font, isProperty(EMBED_FONTS),
				getProperty(EMBED_FONTS_AS));
		pageStream.font(os.name(fontRef), font.getSize() * FONTSIZE_CORRECTION);
		pageStream.show(str);
	}

	/**
	 * See the comment of VectorGraphicsUtitlies1.
	 * 
	 * @see FontUtilities#showString(java.awt.Font, String,
	 *      org.freehep.graphics2d.font.CharTable,
	 *      org.freehep.graphicsio.font.FontUtilities.ShowString)
	 */
	private void showCharacterCodes(String str) throws IOException {
		FontUtilities.showString(getFont(), str, Lookup.getInstance().getTable(
				"PDFLatin"), this);
	}

	private double getWidth() {
		Dimension pageSize = getSize(getProperty(PAGE_SIZE),
				getProperty(ORIENTATION));
		Insets margins = PageConstants.getMargins(
				getPropertyInsets(PAGE_MARGINS), getProperty(ORIENTATION));
		return pageSize.getWidth() - margins.left - margins.right;
	}

	private double getHeight() {
		Dimension pageSize = getSize(getProperty(PAGE_SIZE),
				getProperty(ORIENTATION));
		Insets margins = PageConstants.getMargins(
				getPropertyInsets(PAGE_MARGINS), getProperty(ORIENTATION));
		return pageSize.getHeight() - margins.top - margins.bottom;
	}
	
	
	/**
	 * Michael Borcherds (GeoGebra)
	 * added to allow override of page size
	 * 
	 * @param size
	 * @param orientation
	 * @return
	 */
	private Dimension getSize(String size, String orientation) {
		if (pageSize != null) {
			return pageSize;
		}
		
		return PageConstants.getSize(size, orientation);
	}

	/**
	 * Michael Borcherds (GeoGebra)
	 * added to allow override of page size
	 * 
	 * @param d
	 */
	public void setPageSize(Dimension d) {
		pageSize  = d;	
	}

}

// Copyright 2000-2006, FreeHEP
package org.freehep.graphicsio;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Paint;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.util.Arrays;
import java.util.Map;

import org.freehep.graphics2d.font.FontEncoder;
import org.freehep.util.images.ImageUtilities;

/**
 * This class provides an abstract VectorGraphicsIO class for specific output
 * drivers.
 *
 * @author Charles Loomis
 * @author Mark Donszelmann
 * @author Steffen Greiffenberg
 * @version $Id: AbstractVectorGraphicsIO.java,v 1.6 2009-08-17 21:44:45 murkle
 *          Exp $
 */
public abstract class AbstractVectorGraphicsIO extends VectorGraphicsIO {

	private static final String rootKey = AbstractVectorGraphicsIO.class
			.getName();

	public static final String EMIT_WARNINGS = rootKey + ".EMIT_WARNINGS";

	public static final String TEXT_AS_SHAPES = rootKey + ".TEXT_AS_SHAPES";

	public static final String EMIT_ERRORS = rootKey + ".EMIT_ERRORS";

	public static final String CLIP = rootKey + ".CLIP";

	/*
	 * =========================================================================
	 * ======= Table of Contents: ------------------ 1. Constructors & Factory
	 * Methods 2. Document Settings 3. Header, Trailer, Multipage & Comments 3.1
	 * Header & Trailer 3.2 MultipageDocument methods 4. Create & Dispose 5.
	 * Drawing Methods 5.1. shapes (draw/fill) 5.1.1. lines, rectangles, round
	 * rectangles 5.1.2. polylines, polygons 5.1.3. ovals, arcs 5.1.4. shapes
	 * 5.2. Images 5.3. Strings 6. Transformations 7. Clipping 8. Graphics State
	 * / Settings 8.1. stroke/linewidth 8.2. paint/color 8.3. font 8.4.
	 * rendering hints 9. Auxiliary 10. Private/Utility Methods
	 * =========================================================================
	 * =======
	 */

	private Dimension size;

	private Component component;

	private boolean doRestoreOnDispose;

	private Rectangle deviceClip;

	/**
	 * Untransformed clipping Area defined by the user
	 */
	private Area userClip;

	private AffineTransform currentTransform;

	// only for use in writeSetTransform to calculate the difference.
	private AffineTransform oldTransform = new AffineTransform();

	private Composite currentComposite;

	private Stroke currentStroke;

	private RenderingHints hints;

	/*
	 * =========================================================================
	 * ======= 1. Constructors & Factory Methods
	 * =========================================================================
	 * =======
	 */

	/**
	 * Constructs a Graphics context with the following graphics state:
	 * <UL>
	 * <LI>Paint: black
	 * <LI>Font: Dailog, Plain, 12pt
	 * <LI>Stroke: Linewidth 1.0; No Dashing; Miter Join Style; Miter Limit 10;
	 * Square Endcaps;
	 * <LI>Transform: Identity
	 * <LI>Composite: AlphaComposite.SRC_OVER
	 * <LI>Clip: Rectangle(0, 0, size.width, size.height)
	 * </UL>
	 *
	 * @param size
	 *            rectangle specifying the bounds of the image
	 * @param doRestoreOnDispose
	 *            true if writeGraphicsRestore() should be called when this
	 *            graphics context is disposed of.
	 */
	protected AbstractVectorGraphicsIO(Dimension size,
			boolean doRestoreOnDispose) {
		super();

		this.size = size;
		this.component = null;
		this.doRestoreOnDispose = doRestoreOnDispose;

		deviceClip = (size != null
				? new Rectangle(0, 0, size.width, size.height) : null);
		userClip = null;
		currentTransform = new AffineTransform();
		currentComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
		currentStroke = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE,
				BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);

		super.setColor(Color.BLACK);
		super.setBackground(Color.BLACK);
		super.setFont(new Font("Dialog", Font.PLAIN, 12));

		// Initialize the rendering hints.
		hints = new RenderingHints(null);
	}

	/**
	 * Constructs a Graphics context with the following graphics state:
	 * <UL>
	 * <LI>Paint: The color of the component.
	 * <LI>Font: The font of the component.
	 * <LI>Stroke: Linewidth 1.0; No Dashing; Miter Join Style; Miter Limit 10;
	 * Square Endcaps;
	 * <LI>Transform: The getDefaultTransform for the GraphicsConfiguration of
	 * the component.
	 * <LI>Composite: AlphaComposite.SRC_OVER
	 * <LI>Clip: The size of the component, Rectangle(0, 0, size.width,
	 * size.height)
	 * </UL>
	 *
	 * @param component
	 *            to be used to initialize the values of the graphics state
	 * @param doRestoreOnDispose
	 *            true if writeGraphicsRestore() should be called when this
	 *            graphics context is disposed of.
	 */
	protected AbstractVectorGraphicsIO(Component component,
			boolean doRestoreOnDispose) {
		super();

		this.size = component.getSize();
		this.component = component;
		this.doRestoreOnDispose = doRestoreOnDispose;

		deviceClip = (size != null
				? new Rectangle(0, 0, size.width, size.height) : null);
		userClip = null;
		GraphicsConfiguration gc = component.getGraphicsConfiguration();
		currentTransform = (gc != null) ? gc.getDefaultTransform()
				: new AffineTransform();
		currentComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
		currentStroke = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE,
				BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);

		super.setFont(component.getFont());
		super.setBackground(component.getBackground());
		super.setColor(component.getForeground());

		// Initialize the rendering hints.
		hints = new RenderingHints(null);
	}

	/**
	 * Constructs a subgraphics context.
	 *
	 * @param graphics
	 *            context to clone from
	 * @param doRestoreOnDispose
	 *            true if writeGraphicsRestore() should be called when this
	 *            graphics context is disposed of.
	 */
	protected AbstractVectorGraphicsIO(AbstractVectorGraphicsIO graphics,
			boolean doRestoreOnDispose) {
		super(graphics);
		this.doRestoreOnDispose = doRestoreOnDispose;

		size = new Dimension(graphics.size);
		component = graphics.component;

		deviceClip = new Rectangle(graphics.deviceClip);
		userClip = (graphics.userClip != null) ? new Area(graphics.userClip)
				: null;
		currentTransform = new AffineTransform(graphics.currentTransform);
		currentComposite = graphics.currentComposite;
		currentStroke = graphics.currentStroke;
		hints = graphics.hints;
	}

	/*
	 * =========================================================================
	 * ======= | 2. Document Settings
	 * =========================================================================
	 * =======
	 */
	@Override
	public Dimension getSize() {
		return size;
	}

	public Component getComponent() {
		return component;
	}

	/*
	 * =========================================================================
	 * ======= | 3. Header, Trailer, Multipage & Comments
	 * =========================================================================
	 * =======
	 */
	/* 3.1 Header & Trailer */
	@Override
	public void startExport() {
		try {
			writeHeader();

			// delegate this to openPage if it is a MultiPage document
			if (!(this instanceof MultiPageDocument)) {
				writeGraphicsState();
				writeBackground();
			}
		} catch (IOException e) {
			handleException(e);
		}
	}

	@Override
	public void endExport() {
		try {
			dispose();
			writeTrailer();
			closeStream();
		} catch (IOException e) {
			handleException(e);
		}
	}

	/**
	 * Called to write the header part of the output.
	 */
	public abstract void writeHeader() throws IOException;

	/**
	 * Called to write the initial graphics state.
	 */
	public void writeGraphicsState() throws IOException {
		writePaint(getPrintColor(getColor()));

		writeSetTransform(getTransform());

		// writeStroke(getStroke());

		setClip(getClip());

		// Silly assignment, Font is written when String is drawed and "extra"
		// writeFont does not exist
		// setFont(getFont());

		// Silly assignment and "extra" writeComposite does not exist
		// setComposite(getComposite);
	}

	public abstract void writeBackground() throws IOException;

	/**
	 * Called to write the trailing part of the output.
	 */
	public abstract void writeTrailer() throws IOException;

	/**
	 * Called to close the stream you are writing to.
	 */
	public abstract void closeStream() throws IOException;

	@Override
	public void printComment(String comment) {
		try {
			writeComment(comment);
		} catch (IOException e) {
			handleException(e);
		}
	}

	/**
	 * Called to Write out a comment.
	 *
	 * @param comment
	 *            to be written
	 */
	public abstract void writeComment(String comment) throws IOException;

	/* 3.2 MultipageDocument methods */
	protected void resetClip(Rectangle clip) {
		deviceClip = clip;
		userClip = null;
	}

	/*
	 * =========================================================================
	 * ======= 4. Create & Dispose
	 * =========================================================================
	 * =======
	 */
	/**
	 * Disposes of the graphics context. If on creation restoreOnDispose was
	 * true, writeGraphicsRestore() will be called.
	 */
	@Override
	public void dispose() {
		try {
			// Swing sometimes calls dispose several times for a given
			// graphics object. Ensure that the grestore is only written
			// once if this happens.
			if (doRestoreOnDispose) {
				writeGraphicsRestore();
				doRestoreOnDispose = false;
			}
		} catch (IOException e) {
			handleException(e);
		}
	}

	/**
	 * Writes out the save of a graphics context for a later restore. Some
	 * implementations keep track of this by hand if the output format does not
	 * support it.
	 */
	protected abstract void writeGraphicsSave() throws IOException;

	/**
	 * Writes out the restore of a graphics context. Some implementations keep
	 * track of this by hand if the output format does not support it.
	 */
	protected abstract void writeGraphicsRestore() throws IOException;

	/*
	 * =========================================================================
	 * ======= | 5. Drawing Methods
	 * =========================================================================
	 * =======
	 */

	/* 5.3. Images */
	@Override
	public boolean drawImage(Image image, int x, int y,
			ImageObserver observer) {
		int imageWidth = image.getWidth(observer);
		int imageHeight = image.getHeight(observer);
		return drawImage(image, x, y, x + imageWidth, y + imageHeight, 0, 0,
				imageWidth, imageHeight, null, observer);
	}

	@Override
	public boolean drawImage(Image image, int x, int y, int width, int height,
			ImageObserver observer) {
		int imageWidth = image.getWidth(observer);
		int imageHeight = image.getHeight(observer);
		return drawImage(image, x, y, x + width, y + height, 0, 0, imageWidth,
				imageHeight, null, observer);
	}

	@Override
	public boolean drawImage(Image image, int x, int y, int width, int height,
			Color bgColor, ImageObserver observer) {
		int imageWidth = image.getWidth(observer);
		int imageHeight = image.getHeight(observer);
		return drawImage(image, x, y, x + width, y + height, 0, 0, imageWidth,
				imageHeight, bgColor, observer);
	}

	@Override
	public boolean drawImage(Image image, int x, int y, Color bgColor,
			ImageObserver observer) {
		int imageWidth = image.getWidth(observer);
		int imageHeight = image.getHeight(observer);
		return drawImage(image, x, y, x + imageWidth, y + imageHeight, 0, 0,
				imageWidth, imageHeight, bgColor, observer);
	}

	@Override
	public boolean drawImage(Image image, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		return drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null,
				observer);
	}

	@Override
	public boolean drawImage(Image image, AffineTransform xform,
			ImageObserver observer) {
		drawRenderedImage(
				ImageUtilities.createRenderedImage(image, observer, null),
				xform);
		return true;
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		drawImage(img, x, y, null);
	}

	// NOTE: not tested yet!!!
	@Override
	public void drawRenderableImage(RenderableImage image,
			AffineTransform xform) {
		drawRenderedImage(image.createRendering(
				new RenderContext(new AffineTransform(), getRenderingHints())),
				xform);
	}

	/**
	 * Draw and resizes (transparent) image. Calls writeImage(...).
	 *
	 * @param image
	 *            image to be drawn
	 * @param dx1
	 *            destination image bounds
	 * @param dy1
	 *            destination image bounds
	 * @param dx2
	 *            destination image bounds
	 * @param dy2
	 *            destination image bounds
	 * @param sx1
	 *            source image bounds
	 * @param sy1
	 *            source image bounds
	 * @param sx2
	 *            source image bounds
	 * @param sy2
	 *            source image bounds
	 * @param bgColor
	 *            background color
	 * @param observer
	 *            for updates if image still incomplete
	 * @return true if successful
	 */
	@Override
	public boolean drawImage(Image image, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, Color bgColor,
			ImageObserver observer) {
		try {
			int srcX = Math.min(sx1, sx2);
			int srcY = Math.min(sy1, sy2);
			int srcWidth = Math.abs(sx2 - sx1);
			int srcHeight = Math.abs(sy2 - sy1);
			int width = Math.abs(dx2 - dx1);
			int height = Math.abs(dy2 - dy1);

			if ((srcX != 0) || (srcY != 0)
					|| (srcWidth != image.getWidth(observer))
					|| (srcHeight != image.getHeight(observer))) {
				// crop the source image
				ImageFilter crop = new CropImageFilter(srcX, srcY, srcWidth,
						srcHeight);
				image = Toolkit.getDefaultToolkit().createImage(
						new FilteredImageSource(image.getSource(), crop));
				MediaTracker mediaTracker = new MediaTracker(new Panel());
				mediaTracker.addImage(image, 0);
				try {
					mediaTracker.waitForAll();
				} catch (InterruptedException e) {
					handleException(e);
				}
			}

			boolean flipHorizontal = (dx2 < dx1) ^ (sx2 < sx1); // src flipped
																// and not dest
																// flipped or
																// vice versa
			boolean flipVertical = (dy2 < dy1) ^ (sy2 < sy1); // <=> source
																// flipped XOR
																// dest flipped

			double tx = (flipHorizontal) ? (double) dx2 : (double) dx1;
			double ty = (flipVertical) ? (double) dy2 : (double) dy1;

			double sx = (double) width / srcWidth;
			sx = flipHorizontal ? -1 * sx : sx;
			double sy = (double) height / srcHeight;
			sy = flipVertical ? -1 * sy : sy;

			writeImage(
					ImageUtilities.createRenderedImage(image, observer,
							bgColor),
					new AffineTransform(sx, 0, 0, sy, tx, ty), bgColor);
			return true;
		} catch (IOException e) {
			handleException(e);
			return false;
		}
	}

	/*
	 * // first use the original orientation int clippingWidth = Math.abs(sx2 -
	 * sx1); int clippingHeight = Math.abs(sy2 - sy1); int sulX = Math.min(sx1,
	 * sx2); int sulY = Math.min(sy1, sy2); Image background = null; if (bgColor
	 * != null) { // Draw the image on the background color // maybe we could
	 * crop it and fill the transparent pixels in one go // by means of a
	 * filter. background = new BufferedImage(clippingWidth, clippingHeight,
	 * BufferedImage.TYPE_INT_ARGB); Graphics bgGfx = background.getGraphics();
	 * bgGfx.drawImage(image, 0, 0, clippingWidth, clippingWidth, sulX, sulY,
	 * sulX+clippingWidth, sulY+clippingHeight, getPrintColor(bgColor),
	 * observer); } else { // crop the source image ImageFilter crop = new
	 * CropImageFilter(sulX, sulY, clippingWidth, clippingHeight); background =
	 * Toolkit.getDefaultToolkit().createImage(new
	 * FilteredImageSource(image.getSource(), crop)); MediaTracker mediaTracker
	 * = new MediaTracker(new Panel()); mediaTracker.addImage(background, 0);
	 * try { mediaTracker.waitForAll(); } catch (InterruptedException e) {
	 * handleException(e); } } // now flip the image if necessary boolean
	 * flipHorizontal = (dx2<dx1) ^ (sx2<sx1); // src flipped and not dest
	 * flipped or vice versa boolean flipVertical = (dy2<dy1) ^ (sy2<sy1); //
	 * <=> source flipped XOR dest flipped int destWidth = Math.abs(dx2-dx1);
	 * int destHeight = Math.abs(dy2-dy1); try { return writeImage(background,
	 * flipHorizontal ? dx2 : dx1, flipVertical ? dy2 : dy1, flipHorizontal ?
	 * -destWidth : destWidth, flipVertical ? -destHeight : destHeight, (bgColor
	 * == null), observer); } catch (IOException e) { return false; } }
	 */
	/**
	 * Draws a rendered image using a transform.
	 *
	 * @param image
	 *            to be drawn
	 * @param xform
	 *            transform to be used on the image
	 */
	@Override
	public void drawRenderedImage(RenderedImage image, AffineTransform xform) {
		try {
			writeImage(image, xform, null);
		} catch (Exception e) {
			handleException(e);
		}
	}

	protected abstract void writeImage(RenderedImage image,
			AffineTransform xform, Color bkg) throws IOException;

	/**
	 * Clears rectangle by painting it with the backgroundColor.
	 *
	 * @param x
	 *            rectangle to be cleared.
	 * @param y
	 *            rectangle to be cleared.
	 * @param width
	 *            rectangle to be cleared.
	 * @param height
	 *            rectangle to be cleared.
	 */
	@Override
	public void clearRect(double x, double y, double width, double height) {
		Paint temp = getPaint();
		setPaint(getBackground());
		fillRect(x, y, width, height);
		setPaint(temp);
	}

	/**
	 * Draws the string at (x, y). If TEXT_AS_SHAPES is set
	 * {@link #drawGlyphVector(java.awt.font.GlyphVector, float, float)} is
	 * used, otherwise {@link #writeString(String, double, double)} for a more
	 * direct output of the string.
	 *
	 * @param string
	 * @param x
	 * @param y
	 */
	@Override
	public void drawString(String string, double x, double y) {
		// something to draw?
		if (string == null || string.equals("")) {
			return;
		}

		// draw strings directly?
		if (isProperty(TEXT_AS_SHAPES)) {

			Font font = getFont();

			// NOTE, see FVG-199, createGlyphVector does not seem to create the
			// proper glyphcodes
			// for either ZapfDingbats or Symbol. We use our own encoding which
			// seems to work...
			String fontName = font.getName();
			if (fontName.equals("Symbol") || fontName.equals("ZapfDingbats")) {
				string = FontEncoder.getEncodedString(string, fontName);
				// use a standard font, not Symbol.
				font = new Font("Serif", font.getStyle(), font.getSize());
			}

			// create glyph
			GlyphVector gv = font.createGlyphVector(getFontRenderContext(),
					string);

			// draw it
			drawGlyphVector(gv, (float) x, (float) y);
		} else {
			// write string directly
			try {
				writeString(string, x, y);
			} catch (IOException e) {
				handleException(e);
			}
		}
	}

	protected abstract void writeString(String string, double x, double y)
			throws IOException;

	/**
	 * Use the transformation of the glyphvector and draw it
	 *
	 * @param g
	 * @param x
	 * @param y
	 */
	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		fill(g.getOutline(x, y));
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x,
			float y) {

		// TextLayout draws the iterator as glyph vector
		// thats why we use it only in the case of TEXT_AS_SHAPES,
		// otherwise tagged strings are always written as glyphs
		if (isProperty(TEXT_AS_SHAPES)) {
			// draws all attributes
			TextLayout tl = new TextLayout(iterator, getFontRenderContext());
			tl.draw(this, x, y);
		} else {
			// reset to that font at the end
			Font font = getFont();

			// initial attributes, we us TextAttribute.equals() rather
			// than Font.equals() because using Font.equals() we do
			// not get a 'false' if underline etc. is changed
			Map/* <TextAttribute, ?> */ attributes = font.getAttributes();

			// stores all characters which are written with the same font
			// if font is changed the buffer will be written and cleared
			// after it
			StringBuffer sb = new StringBuffer();

			for (char c = iterator
					.first(); c != AttributedCharacterIterator.DONE; c = iterator
							.next()) {

				// append c if font is not changed
				if (attributes.equals(iterator.getAttributes())) {
					sb.append(c);

				} else {
					// draw sb if font is changed
					drawString(sb.toString(), x, y);

					// change the x offset for the next drawing
					// FIXME: change y offset for vertical text
					TextLayout tl = new TextLayout(sb.toString(), attributes,
							getFontRenderContext());

					// calculate real width
					x = x + Math.max(tl.getAdvance(),
							(float) tl.getBounds().getWidth());

					// empty sb
					sb = new StringBuffer();
					sb.append(c);

					// change the font
					attributes = iterator.getAttributes();
					setFont(new Font(attributes));
				}
			}

			// draw the rest
			if (sb.length() > 0) {
				drawString(sb.toString(), x, y);
			}

			// use the old font for the next string drawing
			setFont(font);
		}
	}

	/*
	 * =========================================================================
	 * ======= | 6. Transformations
	 * =========================================================================
	 * =======
	 */
	/**
	 * Get the current transform.
	 *
	 * @return current transform
	 */
	@Override
	public AffineTransform getTransform() {
		return new AffineTransform(currentTransform);
	}

	/**
	 * Set the current transform. Calls writeSetTransform(Transform).
	 *
	 * @param transform
	 *            to be set
	 */
	@Override
	public void setTransform(AffineTransform transform) {
		// Fix for FREEHEP-569
		oldTransform.setTransform(currentTransform);
		currentTransform.setTransform(transform);
		try {
			writeSetTransform(transform);
		} catch (IOException e) {
			handleException(e);
		}
	}

	/**
	 * Transforms the current transform. Calls writeTransform(Transform)
	 *
	 * @param transform
	 *            to be applied
	 */
	@Override
	public void transform(AffineTransform transform) {
		currentTransform.concatenate(transform);
		try {
			writeTransform(transform);
		} catch (IOException e) {
			handleException(e);
		}
	}

	/**
	 * Translates the current transform. Calls writeTransform(Transform)
	 *
	 * @param x
	 *            amount by which to translate
	 * @param y
	 *            amount by which to translate
	 */
	@Override
	public void translate(double x, double y) {
		currentTransform.translate(x, y);
		try {
			writeTransform(new AffineTransform(1, 0, 0, 1, x, y));
		} catch (IOException e) {
			handleException(e);
		}
	}

	/**
	 * Rotate the current transform over the Z-axis. Calls
	 * writeTransform(Transform). Rotating with a positive angle theta rotates
	 * points on the positive x axis toward the positive y axis.
	 *
	 * @param theta
	 *            radians over which to rotate
	 */
	@Override
	public void rotate(double theta) {
		currentTransform.rotate(theta);
		try {
			writeTransform(new AffineTransform(Math.cos(theta), Math.sin(theta),
					-Math.sin(theta), Math.cos(theta), 0, 0));
		} catch (IOException e) {
			handleException(e);
		}
	}

	/**
	 * Scales the current transform. Calls writeTransform(Transform).
	 *
	 * @param sx
	 *            amount used for scaling
	 * @param sy
	 *            amount used for scaling
	 */
	@Override
	public void scale(double sx, double sy) {
		currentTransform.scale(sx, sy);
		try {
			writeTransform(new AffineTransform(sx, 0, 0, sy, 0, 0));
		} catch (IOException e) {
			handleException(e);
		}
	}

	/**
	 * Shears the current transform. Calls writeTransform(Transform).
	 *
	 * @param shx
	 *            amount for shearing
	 * @param shy
	 *            amount for shearing
	 */
	@Override
	public void shear(double shx, double shy) {
		currentTransform.shear(shx, shy);
		try {
			writeTransform(new AffineTransform(1, shy, shx, 1, 0, 0));
		} catch (IOException e) {
			handleException(e);
		}
	}

	/**
	 * Writes out the transform as it needs to be concatenated to the internal
	 * transform of the output format. If there is no implementation of an
	 * internal transform, then this method needs to do nothing, BUT all
	 * coordinates need to be transformed by the currentTransform before being
	 * written out.
	 *
	 * @param transform
	 *            to be written
	 */
	protected abstract void writeTransform(AffineTransform transform)
			throws IOException;

	/**
	 * Clears any existing transformation and sets the a new one. The default
	 * implementation calls writeTransform using the inverted affine transform
	 * to calculate it.
	 * 
	 * new version by Calixte Denizet fixes eg writing output from JLaTeXMath to
	 * PDF
	 *
	 * @param transform
	 *            to be written
	 */
	protected void writeSetTransform(AffineTransform transform)
			throws IOException {
		try {
			AffineTransform deltaTransform = new AffineTransform(
					oldTransform.createInverse());
			deltaTransform.concatenate(transform);
			writeTransform(deltaTransform);
		} catch (NoninvertibleTransformException e) {
			// ignored...
		}
	}

	/*
	 * old version protected void writeSetTransform(AffineTransform transform)
	 * throws IOException { try { AffineTransform deltaTransform = new
	 * AffineTransform(transform);
	 * transform.concatenate(oldTransform.createInverse());
	 * writeTransform(deltaTransform); } catch (NoninvertibleTransformException
	 * e) { // ignored... } }
	 */

	/*
	 * =========================================================================
	 * ======= | 7. Clipping
	 * =========================================================================
	 * =======
	 */

	/**
	 * Gets the current clip in form of a Shape (Rectangle).
	 *
	 * @return current clip
	 */
	@Override
	public Shape getClip() {
		return (userClip != null) ? new Area(untransformShape(userClip)) : null;
	}

	/**
	 * Gets the current clip in form of a Rectangle.
	 *
	 * @return current clip
	 */
	@Override
	public Rectangle getClipBounds() {
		Shape clip = getClip();
		return (clip != null) ? getClip().getBounds() : null;
	}

	/**
	 * Gets the current clip in form of a Rectangle.
	 *
	 * @return current clip
	 */
	@Override
	public Rectangle getClipBounds(Rectangle r) {
		Rectangle bounds = getClipBounds();
		if (bounds != null) {
			r.setBounds(bounds);
		}
		return r;
	}

	/**
	 * Clips rectangle. Calls clip(Rectangle).
	 *
	 * @param x
	 *            rectangle for clipping
	 * @param y
	 *            rectangle for clipping
	 * @param width
	 *            rectangle for clipping
	 * @param height
	 *            rectangle for clipping
	 */
	@Override
	public void clipRect(int x, int y, int width, int height) {
		clip(new Rectangle(x, y, width, height));
	}

	/**
	 * Clips rectangle. Calls clip(Rectangle2D).
	 *
	 * @param x
	 *            rectangle for clipping
	 * @param y
	 *            rectangle for clipping
	 * @param width
	 *            rectangle for clipping
	 * @param height
	 *            rectangle for clipping
	 */
	@Override
	public void clipRect(double x, double y, double width, double height) {
		clip(new Rectangle2D.Double(x, y, width, height));
	}

	/**
	 * Clips rectangle. Calls clip(Rectangle).
	 *
	 * @param x
	 *            rectangle for clipping
	 * @param y
	 *            rectangle for clipping
	 * @param width
	 *            rectangle for clipping
	 * @param height
	 *            rectangle for clipping
	 */
	@Override
	public void setClip(int x, int y, int width, int height) {
		setClip(new Rectangle(x, y, width, height));
	}

	/**
	 * Clips rectangle. Calls clip(Rectangle2D).
	 *
	 * @param x
	 *            rectangle for clipping
	 * @param y
	 *            rectangle for clipping
	 * @param width
	 *            rectangle for clipping
	 * @param height
	 *            rectangle for clipping
	 */
	@Override
	public void setClip(double x, double y, double width, double height) {
		setClip(new Rectangle2D.Double(x, y, width, height));
	}

	/**
	 * Clips shape. Clears userClip and calls clip(Shape).
	 *
	 * @param s
	 *            used for clipping
	 */
	@Override
	public void setClip(Shape s) {

		Shape ts = transformShape(s);
		userClip = (ts != null) ? new Area(ts) : null;

		try {
			writeSetClip(s);
		} catch (IOException e) {
			handleException(e);
		}
	}

	/**
	 * Clips using given shape. Dispatches to writeClip(Rectangle),
	 * writeClip(Rectangle2D) or writeClip(Shape).
	 *
	 * @param s
	 *            used for clipping
	 */
	@Override
	public void clip(Shape s) {
		Shape ts = transformShape(s);
		if (userClip != null) {
			if (ts != null) {
				userClip.intersect(new Area(ts));
			} else {
				userClip = null;
			}
		} else {
			userClip = (ts != null) ? new Area(ts) : null;
		}

		try {
			writeClip(s);
		} catch (IOException e) {
			handleException(e);
		}
	}

	/**
	 * Write out Shape clip.
	 *
	 * @param shape
	 *            to be used for clipping
	 */
	protected abstract void writeClip(Shape shape) throws IOException;

	/**
	 * Write out Shape clip.
	 *
	 * @param shape
	 *            to be used for clipping
	 */
	protected abstract void writeSetClip(Shape shape) throws IOException;

	/*
	 * =========================================================================
	 * ======= | 8. Graphics State
	 * =========================================================================
	 * =======
	 */
	/* 8.1. stroke/linewidth */
	/**
	 * Get the current stroke.
	 *
	 * @return current stroke
	 */
	@Override
	public Stroke getStroke() {
		return currentStroke;
	}

	/**
	 * Sets the current stroke. Calls writeStroke if stroke is unequal to the
	 * current stroke.
	 *
	 * @param stroke
	 *            to be set
	 */
	@Override
	public void setStroke(Stroke stroke) {
		if (stroke.equals(currentStroke)) {
			return;
		}
		try {
			writeStroke(stroke);
		} catch (IOException e) {
			handleException(e);
		}
		currentStroke = stroke;
	}

	/**
	 * Writes the current stroke. If stroke is an instance of BasicStroke it
	 * will call writeWidth, writeCap, writeJoin, writeMiterLimit and writeDash,
	 * if any were different than the current stroke.
	 */
	protected void writeStroke(Stroke stroke) throws IOException {
		if (stroke instanceof BasicStroke) {
			BasicStroke ns = (BasicStroke) stroke;

			// get the current values for comparison if available,
			// otherwise set them to -1="undefined"
			int currentCap = -1, currentJoin = -1;
			float currentWidth = -1, currentLimit = -1, currentDashPhase = -1;
			float[] currentDashArray = null;
			if ((currentStroke != null)
					&& (currentStroke instanceof BasicStroke)) {
				BasicStroke cs = (BasicStroke) currentStroke;
				currentCap = cs.getEndCap();
				currentJoin = cs.getLineJoin();
				currentWidth = cs.getLineWidth();
				currentLimit = cs.getMiterLimit();
				currentDashArray = cs.getDashArray();
				currentDashPhase = cs.getDashPhase();
			}

			// Check the linewidth.
			float width = ns.getLineWidth();
			if (currentWidth != width) {
				writeWidth(width);
			}

			// Check the line caps.
			int cap = ns.getEndCap();
			if (currentCap != cap) {
				writeCap(cap);
			}

			// Check the line joins.
			int join = ns.getLineJoin();
			if (currentJoin != join) {
				writeJoin(join);
			}

			// Check the miter limit and validity of value
			float limit = ns.getMiterLimit();
			if ((currentLimit != limit) && (limit >= 1.0f)) {
				writeMiterLimit(limit);
			}

			// Check to see if there are differences in the phase or dash
			if (!Arrays.equals(currentDashArray, ns.getDashArray())
					|| (currentDashPhase != ns.getDashPhase())) {

				// write the dashing parameters
				if (ns.getDashArray() != null) {
					writeDash(ns.getDashArray(), ns.getDashPhase());
				} else {
					writeDash(new float[0], ns.getDashPhase());
				}
			}
		}
	}

	/**
	 * Writes out the width of the stroke.
	 *
	 * @param width
	 *            of the stroke
	 */
	protected void writeWidth(float width) throws IOException {
		writeWarning(getClass() + ": writeWidth() not implemented.");
	}

	/**
	 * Writes out the cap of the stroke.
	 *
	 * @param cap
	 *            of the stroke
	 */
	protected void writeCap(int cap) throws IOException {
		writeWarning(getClass() + ": writeCap() not implemented.");
	}

	/**
	 * Writes out the join of the stroke.
	 *
	 * @param join
	 *            of the stroke
	 */
	protected void writeJoin(int join) throws IOException {
		writeWarning(getClass() + ": writeJoin() not implemented.");
	}

	/**
	 * Writes out the miter limit of the stroke.
	 *
	 * @param limit
	 *            miter limit of the stroke
	 */
	protected void writeMiterLimit(float limit) throws IOException {
		writeWarning(getClass() + ": writeMiterLimit() not implemented.");
	}

	/**
	 * Writes out the dash of the stroke.
	 *
	 * @param dash
	 *            dash pattern, empty array is solid line
	 * @param phase
	 *            of the dash pattern
	 */
	abstract protected void writeDash(float[] dash, float phase)
			throws IOException;
	// {
	// // for backward compatibility
	// double[] dd = new double[dash.length];
	// for (int i = 0; i < dash.length; i++) {
	// dd[i] = dash[i];
	// }
	// writeDash(dd, phase);
	// }

	// /**
	// * Writes out the dash of the stroke.
	// *
	// * @deprecated use writeDash(float[], float)
	// * @param dash
	// * dash pattern, empty array is solid line
	// * @param phase
	// * of the dash pattern
	// */
	// @Deprecated
	// protected void writeDash(double[] dash, double phase) throws IOException
	// {
	// writeWarning(getClass() + ": writeDash() not implemented.");
	// }

	/* 8.2 Paint */
	@Override
	public void setColor(Color color) {
		if (color == null) {
			return;
		}

		if (color.equals(getColor())) {
			return;
		}

		try {
			super.setColor(color);
			writePaint(getPrintColor(color));
		} catch (IOException e) {
			handleException(e);
		}
	}

	/**
	 * Sets the current paint. Dispatches to writePaint(Color),
	 * writePaint(GradientPaint), writePaint(TexturePaint paint) or
	 * writePaint(Paint). In the case paint is a Color the current color is also
	 * changed.
	 *
	 * @param paint
	 *            to be set
	 */
	@Override
	public void setPaint(Paint paint) {
		if (paint == null) {
			return;
		}

		if (paint.equals(getPaint())) {
			return;
		}

		try {
			if (paint instanceof Color) {
				setColor((Color) paint);
			} else if (paint instanceof GradientPaint) {
				super.setPaint(paint);
				writePaint((GradientPaint) paint);
			} else if (paint instanceof TexturePaint) {
				super.setPaint(paint);
				writePaint((TexturePaint) paint);
			} else {
				super.setPaint(paint);
				writePaint(paint);
			}
		} catch (IOException e) {
			handleException(e);
		}
	}

	/**
	 * Writes out paint as the given color.
	 *
	 * @param color
	 *            to be written
	 */
	protected abstract void writePaint(Color color) throws IOException;

	/**
	 * Writes out paint as the given gradient.
	 *
	 * @param paint
	 *            to be written
	 */
	protected abstract void writePaint(GradientPaint paint) throws IOException;

	/**
	 * Writes out paint as the given texture.
	 *
	 * @param paint
	 *            to be written
	 */
	protected abstract void writePaint(TexturePaint paint) throws IOException;

	/**
	 * Writes out paint.
	 *
	 * @param paint
	 *            to be written
	 */
	protected abstract void writePaint(Paint paint) throws IOException;

	/* 8.3. font */
	/**
	 * Gets the current font render context. This returns an standard
	 * FontRenderContext with an upside down matrix, anti-aliasing and uses
	 * fractional metrics.
	 *
	 * @return current font render context
	 */
	@Override
	public FontRenderContext getFontRenderContext() {
		// NOTE: not sure?
		return new FontRenderContext(new AffineTransform(1, 0, 0, -1, 0, 0),
				true, true);
	}

	/**
	 * Gets the fontmetrics.
	 *
	 * @param font
	 *            to be used for retrieving fontmetrics
	 * @return fontmetrics for given font
	 */
	@Override
	public FontMetrics getFontMetrics(Font font) {
		return Toolkit.getDefaultToolkit().getFontMetrics(font);
	}

	/* 8.4. rendering hints */
	/**
	 * Gets a copy of the rendering hints.
	 *
	 * @return clone of table of rendering hints.
	 */
	@Override
	public RenderingHints getRenderingHints() {
		return (RenderingHints) hints.clone();
	}

	/**
	 * Adds to table of rendering hints.
	 *
	 * @param hints
	 *            table to be added
	 */
	@Override
	public void addRenderingHints(Map hints) {
		this.hints.putAll(hints);
	}

	/**
	 * Sets table of rendering hints.
	 *
	 * @param hints
	 *            table to be set
	 */
	@Override
	public void setRenderingHints(Map hints) {
		this.hints.clear();
		this.hints.putAll(hints);
	}

	/**
	 * Gets a given rendering hint.
	 *
	 * @param key
	 *            hint key
	 * @return hint associated to key
	 */
	@Override
	public Object getRenderingHint(RenderingHints.Key key) {
		return hints.get(key);
	}

	/**
	 * Sets a given rendering hint.
	 *
	 * @param key
	 *            hint key
	 * @param hint
	 *            to be associated with key
	 */
	@Override
	public void setRenderingHint(RenderingHints.Key key, Object hint) {
		// extra protection, failed on under MacOS X 10.2.6, jdk 1.4.1_01-39/14
		if ((key == null) || (hint == null)) {
			return;
		}
		hints.put(key, hint);
	}

	/**
	 * Sets the current font.
	 *
	 * @param font
	 *            to be set
	 */
	@Override
	public void setFont(Font font) {
		if (font == null) {
			return;
		}

		// FIXME: maybe add delayed setting
		super.setFont(font);

		// write the font
		try {
			writeFont(font);
		} catch (IOException e) {
			handleException(e);
		}
	}

	/**
	 * Writes the font
	 *
	 * @param font
	 *            to be written
	 */
	protected abstract void writeFont(Font font) throws IOException;

	/*
	 * =========================================================================
	 * ======= | 9. Auxiliary
	 * =========================================================================
	 * =======
	 */
	/**
	 * Gets current composite.
	 *
	 * @return current composite
	 */
	@Override
	public Composite getComposite() {
		return currentComposite;
	}

	/**
	 * Sets current composite.
	 *
	 * @param composite
	 *            to be set
	 */
	@Override
	public void setComposite(Composite composite) {
		currentComposite = composite;
	}

	/**
	 * Handles an exception which has been caught. Dispatches exception to
	 * writeWarning for UnsupportedOperationExceptions and writeError for others
	 *
	 * @param exception
	 *            to be handled
	 */
	protected void handleException(Exception exception) {
		if (exception instanceof UnsupportedOperationException) {
			writeWarning(exception);
		} else {
			writeError(exception);
		}
	}

	/**
	 * Writes out a warning, by default to System.err.
	 *
	 * @param exception
	 *            warning to be written
	 */
	protected void writeWarning(Exception exception) {
		writeWarning(exception.getMessage());
	}

	/**
	 * Writes out a warning, by default to System.err.
	 *
	 * @param warning
	 *            to be written
	 */
	protected void writeWarning(String warning) {
		if (isProperty(EMIT_WARNINGS)) {
			System.err.println(warning);
		}
	}

	/**
	 * Writes out an error, by default the stack trace is printed.
	 *
	 * @param exception
	 *            error to be reported
	 */
	protected void writeError(Exception exception) {
		throw new RuntimeException(exception);
		// FIXME decide what we should do
		/*
		 * if (isProperty(EMIT_ERRORS)) { System.err.println(exception);
		 * exception.printStackTrace(System.err); }
		 */
	}

	@Override
	protected Shape createShape(double[] xPoints, double[] yPoints, int nPoints,
			boolean close) {
		GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		if (nPoints > 0) {
			path.moveTo((float) xPoints[0], (float) yPoints[0]);
			for (int i = 1; i < nPoints; i++) {
				path.lineTo((float) xPoints[i], (float) yPoints[i]);
			}
			if (close) {
				path.closePath();
			}
		}
		return path;
	}

	private static Shape transformShape(AffineTransform at, Shape s) {
		if (s == null) {
			return null;
		}
		if (at == null) {
			return s;
		}
		return at.createTransformedShape(s);
	}

	private Shape transformShape(Shape s) {
		return transformShape(getTransform(), s);
	}

	private Shape untransformShape(Shape s) {
		if (s == null) {
			return null;
		}
		if (getTransform() == null) {
			return s;
		}
		try {
			return transformShape(getTransform().createInverse(), s);
		} catch (NoninvertibleTransformException e) {
			return null;
		}
	}

	/**
	 * Draws an overline for the text at (x, y). The method is usesefull for
	 * drivers that do not support overlines by itself.
	 *
	 * @param text
	 *            text for width calulation
	 * @param font
	 *            font for width calulation
	 * @param x
	 *            position of text
	 * @param y
	 *            position of text
	 */
	protected void overLine(String text, Font font, float x, float y) {
		TextLayout layout = new TextLayout(text, font, getFontRenderContext());
		float width = Math.max(layout.getAdvance(),
				(float) layout.getBounds().getWidth());

		GeneralPath path = new GeneralPath();
		path.moveTo(x,
				y + (float) layout.getBounds().getY() - layout.getAscent());
		path.lineTo(x + width, y + (float) layout.getBounds().getY()
				- layout.getAscent() - layout.getAscent());
		draw(path);
	}
}

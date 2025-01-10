// Copyright 2006, FreeHEP
package org.freehep.graphicsio.emf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.RenderedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.AbstractVectorGraphicsIO;
import org.freehep.graphicsio.PageConstants;
import org.freehep.graphicsio.emf.gdi.EOF;
import org.freehep.graphicsio.emf.gdiplus.Clear;
import org.freehep.graphicsio.emf.gdiplus.DrawImage;
import org.freehep.graphicsio.emf.gdiplus.DrawPath;
import org.freehep.graphicsio.emf.gdiplus.EndOfFile;
import org.freehep.graphicsio.emf.gdiplus.FillPath;
import org.freehep.graphicsio.emf.gdiplus.GDIPlusObject;
import org.freehep.graphicsio.emf.gdiplus.Header;
import org.freehep.graphicsio.emf.gdiplus.MultiplyWorldTransform;
import org.freehep.graphicsio.emf.gdiplus.ResetClip;
import org.freehep.graphicsio.emf.gdiplus.Restore;
import org.freehep.graphicsio.emf.gdiplus.Save;
import org.freehep.graphicsio.emf.gdiplus.SetAntiAliasMode;
import org.freehep.graphicsio.emf.gdiplus.SetClipPath;
import org.freehep.graphicsio.emf.gdiplus.SetWorldTransform;
import org.freehep.util.UserProperties;
import org.freehep.util.Value;

/**
 * Converts calls to Graphics2D to EMF+ Format.
 * 
 * @author Mark Donszelmann
 * @version $Id: EMFPlusGraphics2D.java,v 1.1 2009-08-17 21:44:45 murkle Exp $
 */
public class EMFPlusGraphics2D extends AbstractVectorGraphicsIO {
	public static final String version = "$Revision: 1.1 $";

	private OutputStream ros;
	private EMFOutputStream os;
	private Rectangle imageBounds;
	// FIXME do we need this?
	private EMFHandleManager handleManager;
	private Value containerIndex;
	private Paint restorePaint;

	private static final String rootKey = EMFPlusGraphics2D.class.getName();

	public static final String TRANSPARENT = rootKey + "."
			+ PageConstants.TRANSPARENT;

	public static final String BACKGROUND = rootKey + "."
			+ PageConstants.BACKGROUND;

	public static final String BACKGROUND_COLOR = rootKey + "."
			+ PageConstants.BACKGROUND_COLOR;

	private static final UserProperties defaultProperties = new UserProperties();
	static {
		defaultProperties.setProperty(TRANSPARENT, true);
		defaultProperties.setProperty(BACKGROUND, false);
		defaultProperties.setProperty(BACKGROUND_COLOR, Color.GRAY);
		defaultProperties.setProperty(CLIP, true);
		defaultProperties.setProperty(TEXT_AS_SHAPES, true);
	}

	public static Properties getDefaultProperties() {
		return defaultProperties;
	}

	// FIXME
	@Override
	public FontRenderContext getFontRenderContext() {
		// NOTE: not sure?
		return new FontRenderContext(new AffineTransform(-1, 0, 0, 1, 0, 0),
				true, true);
	}

	public static void setDefaultProperties(Properties newProperties) {
		defaultProperties.setProperties(newProperties);
	}

	public EMFPlusGraphics2D(File file, Dimension size)
			throws FileNotFoundException {
		this(new FileOutputStream(file), size);
	}

	public EMFPlusGraphics2D(File file, Component component)
			throws FileNotFoundException {
		this(new FileOutputStream(file), component);
	}

	public EMFPlusGraphics2D(OutputStream os, Dimension size) {
		super(size, false);
		this.imageBounds = new Rectangle(0, 0, size.width, size.height);
		init(os);
	}

	public EMFPlusGraphics2D(OutputStream os, Component component) {
		super(component, false);
		this.imageBounds = new Rectangle(0, 0, getSize().width,
				getSize().height);
		init(os);
	}

	private void init(OutputStream os) {
		handleManager = new EMFHandleManager();
		ros = os;
		containerIndex = new Value();
		containerIndex.set(0);
		initProperties(defaultProperties);
	}

	protected EMFPlusGraphics2D(EMFPlusGraphics2D graphics,
			boolean doRestoreOnDispose) {
		super(graphics, doRestoreOnDispose);
		// Create a graphics context from a given graphics context.
		// This constructor is used by the system to clone a given graphics
		// context.
		// doRestoreOnDispose is used to call writeGraphicsRestore(),
		// when the graphics context is being disposed off.
		os = graphics.os;
		imageBounds = graphics.imageBounds;
		handleManager = graphics.handleManager;
		containerIndex = graphics.containerIndex;
		restorePaint = graphics.getPaint();
	}

	@Override
	public void writeHeader() throws IOException {
		ros = new BufferedOutputStream(ros);

		// GeoGebra: disabled
		// Dimension device = isDeviceIndependent() ? new Dimension(1024, 768)
		// : Toolkit.getDefaultToolkit().getScreenSize();
		Dimension device = new Dimension(1024, 768);

		String producer = getClass().getName();
		if (!isDeviceIndependent()) {
			producer += " " + version.substring(1, version.length() - 1);
		}
		os = new EMFOutputStream(ros, imageBounds, handleManager, getCreator(),
				producer, device, 0x4001);

		os.writeTag(new Header());

		// leave this on since we do text as shapes.
		os.writeTag(new SetAntiAliasMode(true));

		// Point orig = new Point(imageBounds.x, imageBounds.y);
		// Dimension size = new Dimension(imageBounds.width,
		// imageBounds.height);

		// FIXME check what to write
		// os.writeTag(new SetMapMode(MM_ANISOTROPIC));
		// os.writeTag(new SetWindowOrgEx(orig));
		// os.writeTag(new SetWindowExtEx(size));
		// os.writeTag(new SetViewportOrgEx(orig));
		// os.writeTag(new SetViewportExtEx(size));
		// os.writeTag(new SetTextAlign(TA_BASELINE));
		// os.writeTag(new SetTextColor(getColor()));
		// os.writeTag(new SetPolyFillMode(EMFConstants.WINDING));
	}

	@Override
	public void writeBackground() throws IOException {
		if (isProperty(TRANSPARENT)) {
			setBackground(null);
			// FIXME
			// os.writeTag(new Clear(new Color(0xFF, 0xFF, 0xFF, 0x00)));
		} else if (isProperty(BACKGROUND)) {
			setBackground(getPropertyColor(BACKGROUND_COLOR));
			os.writeTag(new Clear(getBackground()));
		} else {
			setBackground(getComponent() != null
					? getComponent().getBackground() : Color.WHITE);
			os.writeTag(new Clear(getBackground()));
		}
	}

	@Override
	public void writeTrailer() throws IOException {
		// delete any remaining objects
		for (;;) {
			int handle = handleManager.highestHandleInUse();
			if (handle < 0) {
				break;
			}
			// os.writeTag(new DeleteObject(handle));
			handleManager.freeHandle(handle);
		}
		os.writeTag(new EndOfFile());
		os.writeTag(new EOF());
	}

	@Override
	public void closeStream() throws IOException {
		os.close();
	}

	@Override
	public Graphics create() {
		// Create a new graphics context from the current one.
		try {
			// Save the current context for restore later.
			writeGraphicsSave();
		} catch (IOException e) {
			handleException(e);
		}
		// The correct graphics context should be created.
		return new EMFPlusGraphics2D(this, true);
	}

	@Override
	public Graphics create(double x, double y, double width, double height) {
		// Create a new graphics context from the current one.
		try {
			// Save the current context for restore later.
			writeGraphicsSave();
		} catch (IOException e) {
			handleException(e);
		}
		// The correct graphics context should be created.
		VectorGraphics graphics = new EMFPlusGraphics2D(this, true);
		graphics.translate(x, y);
		graphics.clipRect(0, 0, width, height);
		return graphics;
	}

	@Override
	protected void writeGraphicsSave() throws IOException {
		os.writeTag(new Save(containerIndex.getInt()));
		containerIndex.set(containerIndex.getInt() + 1);
	}

	@Override
	protected void writeGraphicsRestore() throws IOException {
		containerIndex.set(containerIndex.getInt() - 1);
		os.writeTag(new Restore(containerIndex.getInt()));
		if (restorePaint != null) {
			writePaint(restorePaint);
		}
	}

	@Override
	public void draw(Shape shape) {
		try {
			Stroke stroke = getStroke();
			if ((stroke instanceof BasicStroke)
					&& (((BasicStroke) stroke).getLineWidth() == 0)) {
				os.writeTag(new GDIPlusObject(1, shape, false));
				os.writeTag(
						new GDIPlusObject(2, new BasicStroke(0), getPaint()));
				os.writeTag(new DrawPath(1, 2));
			} else {
				Shape strokedShape = getStroke().createStrokedShape(shape);
				fill(new Area(strokedShape));
			}
		} catch (IOException e) {
			handleException(e);
		}
	}

	@Override
	public void fill(Shape shape) {
		try {
			os.writeTag(new GDIPlusObject(1, shape, false));
			os.writeTag(new FillPath(1, 0));
		} catch (IOException e) {
			handleException(e);
		}
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		writeWarning(getClass()
				+ ": copyArea(int, int, int, int, int, int) not implemented.");
		// Mostly unimplemented.
	}

	@Override
	protected void writeImage(RenderedImage image, AffineTransform xform,
			Color bkg) throws IOException {
		// FIXME use BKG and xform
		writeGraphicsSave();
		os.writeTag(new GDIPlusObject(5, image));
		os.writeTag(new MultiplyWorldTransform(xform, true));
		os.writeTag(new DrawImage(5, image));
		writeGraphicsRestore();
	}

	@Override
	protected void writeString(String string, double x, double y)
			throws IOException {
		// text is drawn as shapes
	}

	@Override
	protected void writeTransform(AffineTransform t) throws IOException {
		os.writeTag(new MultiplyWorldTransform(t, true));
	}

	@Override
	protected void writeSetTransform(AffineTransform t) throws IOException {
		os.writeTag(new SetWorldTransform(t));
	}

	@Override
	protected void writeClip(Shape s) throws IOException {
		os.writeTag(new GDIPlusObject(4, s, false));
		os.writeTag(new SetClipPath(4, SetClipPath.INTERSECT));
	}

	@Override
	protected void writeSetClip(Shape s) throws IOException {
		if (s != null) {
			os.writeTag(new GDIPlusObject(4, s, false));
			os.writeTag(new SetClipPath(4, SetClipPath.REPLACE));
		} else {
			os.writeTag(new ResetClip());
		}
	}

	@Override
	protected void writeWidth(float width) throws IOException {
		// settings convert to shape
	}

	@Override
	protected void writeCap(int cap) throws IOException {
		// settings convert to shape
	}

	@Override
	protected void writeJoin(int join) throws IOException {
		// settings convert to shape
	}

	@Override
	protected void writeMiterLimit(float limit) throws IOException {
		// settings convert to shape
	}

	@Override
	protected void writeDash(float[] dash, float phase) throws IOException {
		// settings convert to shape
	}

	@Override
	public void setPaintMode() {
		writeWarning(getClass() + ": setPaintMode() not implemented.");
		// Mostly unimplemented.
	}

	@Override
	public void setXORMode(Color c1) {
		writeWarning(getClass() + ": setXORMode(Color) not implemented.");
		// Mostly unimplemented.
	}

	@Override
	protected void writePaint(Color p) throws IOException {
		os.writeTag(new GDIPlusObject(0, p));
	}

	@Override
	protected void writePaint(GradientPaint p) throws IOException {
		os.writeTag(new GDIPlusObject(0, p));
	}

	@Override
	protected void writePaint(TexturePaint p) throws IOException {
		os.writeTag(new GDIPlusObject(0, p));
	}

	@Override
	protected void writePaint(Paint p) throws IOException {
		os.writeTag(new GDIPlusObject(0, p));
	}

	@Override
	protected void writeFont(Font font) throws IOException {
		// text converts to shapes
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		writeWarning(
				getClass() + ": getDeviceConfiguration() not implemented.");
		// Mostly unimplemented
		return null;
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		writeWarning(getClass()
				+ ": hit(Rectangle, Shape, boolean) not implemented.");
		// Mostly unimplemented
		return false;
	}

	@Override
	public void writeComment(String comment) throws IOException {
		writeWarning(getClass() + ": writeComment(String) not implemented.");
		// Write out the comment.
	}

	@Override
	protected void writeWarning(String string) {
		System.err.println(string);
	}

	@Override
	public String toString() {
		return "EMFPlusGraphics2D";
	}
}

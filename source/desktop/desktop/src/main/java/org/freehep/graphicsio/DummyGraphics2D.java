// Copyright 2000-2006, FreeHEP
package org.freehep.graphicsio;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.IOException;

import org.freehep.graphics2d.VectorGraphics;

/**
 * This class provides a dummy Graphics2D class, which issues warnings for all
 * non implemented methods. It also describes how to implement these methods. It
 * serves as an example to start the implementation for a new output format.
 *
 * @author Mark Donszelmann
 * @version $Id: DummyGraphics2D.java,v 1.5 2009-08-17 21:44:45 murkle Exp $
 */
public class DummyGraphics2D extends AbstractVectorGraphicsIO {
	/*
	 * =========================================================================
	 * ======= Table of Contents: ------------------ 1. Constructors & Factory
	 * Methods 2. Document Settings 3. Header, Trailer, Multipage & Comments 3.1
	 * Header & Trailer 3.2 MultipageDocument methods 4. Create & Dispose 5.
	 * Drawing Methods 5.1. shapes (draw/fill) 5.1.1. lines, rectangles, round
	 * rectangles 5.1.2. polylines, polygons 5.1.3. ovals, arcs 5.1.4. shapes
	 * 5.2. Images 5.3. Strings 6. Transformations 7. Clipping 8. Graphics State
	 * / Settings 8.1. stroke/linewidth 8.2. paint/color 8.3. font 8.4.
	 * rendering hints 9. Auxiliary 10. Private/Utility Methos
	 * =========================================================================
	 * =======
	 */

	/*
	 * =========================================================================
	 * ======= 1. Constructors & Factory Methods
	 * =========================================================================
	 * =======
	 */
	public DummyGraphics2D(Dimension size, boolean doRestoreOnDispose) {
		super(size, doRestoreOnDispose);
		// Create a graphics context with given imageBounds.
		// This constructor is used by the user to create the initial graphics
		// context.
		// doRestoreOnDispose is used to call writeGraphicsRestore(),
		// when the graphics context is being disposed off.
	}

	protected DummyGraphics2D(AbstractVectorGraphicsIO graphics,
			boolean doRestoreOnDispose) {
		super(graphics, doRestoreOnDispose);
		// Create a graphics context from a given graphics context.
		// This constructor is used by the system to clone a given graphics
		// context.
		// doRestoreOnDispose is used to call writeGraphicsRestore(),
		// when the graphics context is being disposed off.
	}

	/*
	 * =========================================================================
	 * ======= | 2. Document Settings
	 * =========================================================================
	 * =======
	 */

	/*
	 * =========================================================================
	 * ======= | 3. Header, Trailer, Multipage & Comments
	 * =========================================================================
	 * =======
	 */
	/* 3.1 Header & Trailer */
	@Override
	public void writeHeader() throws IOException {
		writeWarning(getClass() + ": writeHeader() not implemented.");
		// Write out the header to the output stream.
	}

	@Override
	public void writeBackground() throws IOException {
		writeWarning(getClass() + ": writeBackground() not implemented.");
		// Write out the background to the output stream.
	}

	@Override
	public void writeTrailer() throws IOException {
		writeWarning(getClass() + ": writeTrailer() not implemented.");
		// Write out the trailer to the output stream.
	}

	@Override
	public void closeStream() throws IOException {
		writeWarning(getClass() + ": closeStream() not implemented.");
		// Close the output stream.
	}

	/* 3.2 MultipageDocument methods */

	/*
	 * =========================================================================
	 * ======= 4. Create & Dispose
	 * =========================================================================
	 * =======
	 */

	@Override
	public Graphics create() {
		// Create a new graphics context from the current one.
		try {
			// Save the current context for restore later.
			writeGraphicsSave();
		} catch (IOException e) {
		}
		// The correct graphics context should be created.
		return new DummyGraphics2D(this, true);
	}

	@Override
	public Graphics create(double x, double y, double width, double height) {
		// Create a new graphics context from the current one.
		try {
			// Save the current context for restore later.
			writeGraphicsSave();
		} catch (IOException e) {
		}
		// The correct graphics context should be created.
		VectorGraphics graphics = new DummyGraphics2D(this, true);
		graphics.clipRect(x, y, width, height);
		return graphics;
	}

	@Override
	protected void writeGraphicsSave() throws IOException {
		writeWarning(getClass() + ": writeGraphicsSave() not implemented.");
		// Write a graphics context save.
		// If the output format does not support this, keep a stack yourself.
	}

	@Override
	protected void writeGraphicsRestore() throws IOException {
		writeWarning(getClass() + ": writeGraphicsRestore() not implemented.");
		// Write a graphics context restore.
		// If the output format does not support this, keep a stack yourself.
	}

	/*
	 * =========================================================================
	 * ======= | 5. Drawing Methods
	 * =========================================================================
	 * =======
	 */
	/* 5.1.4. shapes */

	@Override
	public void draw(Shape shape) {
		writeWarning(getClass() + ": draw(Shape) not implemented.");
		// Write out the stroke of the shape.
	}

	@Override
	public void fill(Shape shape) {
		writeWarning(getClass() + ": fill(Shape) not implemented.");
		// Write out the fill of the shape.
	}

	public void fillAndDraw(Shape shape, Color fillColor) {
		writeWarning(
				getClass() + ": fillAndDraw(Shape, Color) not implemented.");
		// Write out the fill with fillColor and stroke of the shape in
		// getColor().
	}

	/* 5.2. Images */
	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		writeWarning(getClass()
				+ ": copyArea(int, int, int, int, int, int) not implemented.");
		// Mostly unimplemented.
	}

	@Override
	protected void writeImage(RenderedImage image, AffineTransform xform,
			Color bkg) throws IOException {
		writeWarning(getClass()
				+ ": writeImage(RenderedImage, AffineTransform, Color) not implemented.");
		// Write out the image.
	}

	/* 5.3. Strings */
	@Override
	protected void writeString(String string, double x, double y)
			throws IOException {
		writeWarning(getClass()
				+ ": drawString(String, double, double) not implemented.");
		// Write out the string.
	}

	/*
	 * =========================================================================
	 * ======= | 6. Transformations
	 * =========================================================================
	 * =======
	 */
	@Override
	protected void writeTransform(AffineTransform t) throws IOException {
		writeWarning(getClass()
				+ ": writeTransform(AffineTransform) not implemented.");
		// Write out the transform to be applied to the internal transform of
		// the output
		// format.
		// You can also use the currentTransform.
	}

	@Override
	protected void writeSetTransform(AffineTransform t) throws IOException {
		writeWarning(getClass()
				+ ": writeTransform(AffineTransform) not implemented.");
		// Clear the currentTransform and write out the transform to
		// be applied to the internal transform of the output format.
	}

	/*
	 * =========================================================================
	 * ======= | 7. Clipping
	 * =========================================================================
	 * =======
	 */
	@Override
	protected void writeClip(Shape s) throws IOException {
		writeWarning(getClass() + ": writeClip(Shape) not implemented.");
		// Write out the clip shape.
	}

	@Override
	protected void writeSetClip(Shape s) throws IOException {
		writeWarning(getClass() + ": writeSetClip(Shape) not implemented.");
		// Write out the clip shape.
	}

	/*
	 * =========================================================================
	 * ======= | 8. Graphics State
	 * =========================================================================
	 * =======
	 */
	/* 8.1. stroke/linewidth */
	@Override
	protected void writeWidth(float width) throws IOException {
		writeWarning(getClass() + ": writeWidth(float) not implemented.");
		// Write out the stroke width.
	}

	@Override
	protected void writeCap(int cap) throws IOException {
		writeWarning(getClass() + ": writeCap(int) not implemented.");
		// Write out the stroke cap.
	}

	@Override
	protected void writeJoin(int join) throws IOException {
		writeWarning(getClass() + ": writeJoin(int) not implemented.");
		// Write out the stroke join.
	}

	@Override
	protected void writeMiterLimit(float limit) throws IOException {
		writeWarning(getClass() + ": writeMiterLimit(float) not implemented.");
		// Write out the stroke miter limit.
	}

	@Override
	protected void writeDash(float[] dash, float phase) throws IOException {
		writeWarning(
				getClass() + ": writeDash(float[], float) not implemented.");
		// Write out the stroke dash.
	}

	/* 8.2. paint/color */
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
		writeWarning(getClass() + ": writePaint(Color) not implemented.");
		// Write out the color paint.
	}

	@Override
	protected void writePaint(GradientPaint p) throws IOException {
		writeWarning(
				getClass() + ": writePaint(GradientPaint) not implemented.");
		// Write out the gradient paint.
	}

	@Override
	protected void writePaint(TexturePaint p) throws IOException {
		writeWarning(
				getClass() + ": writePaint(TexturePaint) not implemented.");
		// Write out the texture paint.
	}

	@Override
	protected void writePaint(Paint p) throws IOException {
		writeWarning(getClass() + ": writePaint(Paint) not implemented for "
				+ p.getClass());
		// Write out the paint.
	}

	/* 8.3. font */
	@Override
	protected void writeFont(Font font) throws IOException {
		writeWarning(getClass() + ": writeFont(Font) not implemented.");
	}

	/* 8.4. rendering hints */

	/*
	 * =========================================================================
	 * ======= | 9. Auxiliary
	 * =========================================================================
	 * =======
	 */
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
	public String toString() {
		return "DummyGraphics";
	}
}

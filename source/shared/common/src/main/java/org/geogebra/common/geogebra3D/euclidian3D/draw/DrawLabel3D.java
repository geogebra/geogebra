/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.geogebra3D.euclidian3D.draw;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.RenderingHints;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.draw.DrawText;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.App;
import org.geogebra.common.util.MyMath;

/**
 * Class for drawing labels of 3D elements
 * 
 * @author mathieu
 *
 */
public class DrawLabel3D extends DrawableTexture3D {

	private Runnable callBack = null;

	protected Drawable3D drawable;

	protected boolean hasIndex = false;

	protected @CheckForNull CaptionText caption;
	private CaptionProperties properties;

	/**
	 * common constructor
	 * 
	 * @param view
	 *            3D view
	 * @param drawable
	 *            drawable linked to this label
	 */
	public DrawLabel3D(EuclidianView3D view, Drawable3D drawable) {
		this.view = view;
		this.drawable = drawable;
		properties = new CaptionProperties(view);
	}

	/**
	 * update the label
	 * 
	 * @param caption
	 *            the CaptionText object
	 * @param font0
	 *            font
	 * @param v
	 *            coordinates
	 * @param xOffset0
	 *            abs offset in x
	 * @param yOffset0
	 *            abs offset in y
	 * @param zOffset0
	 *            abs offset in z
	 * @param measuringGraphics auxiliary graphics for text measurements
	 */
	public void update(CaptionText caption, GFont font0, Coords v,
			float xOffset0, float yOffset0, float zOffset0, GGraphics2D measuringGraphics) {
		setCaption(caption);
		if (view.drawsLabels()) {
			update(caption.text(), font0, v, xOffset0, yOffset0, zOffset0, measuringGraphics);
		}
	}

	protected void setCaption(CaptionText caption) {
		this.caption = caption;
		properties.update(caption);
	}

	/**
	 * update the label
	 *
	 * @param text0
	 *            text
	 * @param font0
	 *            font
	 * @param fgColor
	 *            color
	 * @param v
	 *            coordinates
	 * @param xOffset0
	 *            abs offset in x
	 * @param yOffset0
	 *            abs offset in y
	 * @param zOffset0
	 *            abs offset in z
	 * @param measuringGraphics auxiliary graphics for text measurements
	 */
	public void update(String text0, GFont font0, GColor fgColor, Coords v,
			float xOffset0, float yOffset0, float zOffset0, GGraphics2D measuringGraphics) {

		if (view.drawsLabels()) {
			update(text0, font0, v, xOffset0, yOffset0, zOffset0, measuringGraphics);
		}
	}

	/**
	 * update the label
	 * 
	 * @param text0
	 *            text
	 * @param font0
	 *            font
	 * @param v
	 *            coordinates
	 * @param xOffset0
	 *            abs offset in x
	 * @param yOffset0
	 *            abs offset in y
	 * @param zOffset0
	 *            abs offset in z
	 * @param measuringGraphics auxiliary graphics for text measurements
	 */
	public void update(String text0, GFont font0, Coords v,
			float xOffset0, float yOffset0, float zOffset0, GGraphics2D measuringGraphics) {
				this.origin = v;
		if (text0.isEmpty() || caption == null) {
			setIsVisible(false);
			return;
		}
		CaptionText cpt = caption;
		properties.update();
		setIsVisible(true);

		cpt.createFont(font0);
		measuringGraphics.setFont(cpt.font());

		GRectangle rectangle = getBounds(cpt, measuringGraphics);

		int xMin = (int) rectangle.getMinX() - 1;
		int xMax = (int) rectangle.getMaxX() + 1;
		int yMin = (int) rectangle.getMinY() - 1;
		int yMax = (int) rectangle.getMaxY() + 1;

		width = xMax - xMin;
		height = yMax - yMin;
		xOffset2 = xMin;
		yOffset2 = -yMax;

		// creates the buffered image
		GBufferedImage bimg = draw(cpt, measuringGraphics);

		// creates the texture
		view.getRenderer().createAlphaTexture(this, bimg);

		waitForReset = false;

		this.xOffset = xOffset0;
		this.yOffset = yOffset0;
		this.zOffset = zOffset0;
	}

	/**
	 * make sure caption and it's properties is initialized for drawing label in 3d (like axis)
	 * @param caption - caption
	 */
	public void initCaption(CaptionText caption) {
		this.caption = caption;
		if (properties == null) {
			properties = new CaptionProperties(view);
		}
		properties.update(caption);
	}

	/**
	 * create graphics2D instance from buffered image
	 * 
	 * @param bimg
	 *            buffered image
	 * @param cpt caption
	 * @return graphics2D
	 */
	protected GGraphics2D createGraphics2D(GBufferedImage bimg, @Nonnull CaptionText cpt) {
		GGraphics2D g2d = bimg.createGraphics();

		GAffineTransform gt = AwtFactory.getPrototype().newAffineTransform();
		gt.scale(1, -1d);
		gt.translate(-xOffset2, yOffset2); // put the baseline on the label
											// anchor
		g2d.transform(gt);
		g2d.setColor(cpt.foregroundColor());
		g2d.setFont(cpt.font());

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		return g2d;
	}

	/**
	 * @return buffered image
	 */
	protected GBufferedImage createBufferedImage() {
		return view.getRenderer().createBufferedImage(this);
	}

	protected GRectangle getBounds(@Nonnull CaptionText cpt, GGraphics2D measuringGraphics) {
		GRectangle rectangle = EuclidianStatic.drawMultiLineText(
				view.getApplication(), cpt.text(), 0, 0, measuringGraphics, false,
				cpt.font(),
				AwtFactory.getPrototype().newRectangle(), null, DrawText.DEFAULT_MARGIN);
		if (properties.hasSubscript()) { // text contains subscript
			hasIndex = true;
			EuclidianStatic.drawIndexedString(view.getApplication(),
					measuringGraphics, cpt.text(), 0, 0, false);
		} else {
			hasIndex = false;
		}

		return rectangle;
	}

	/**
	 * @return buffered image with label drawn in it
	 */
	protected GBufferedImage draw(@Nonnull CaptionText cpt, GGraphics2D measuringGraphics) {
		GBufferedImage bimg;
		GGraphics2D g2d;

		if (cpt.isLaTeX()) {
			GeoElement geo = cpt.getGeoElement();

			// make sure LaTeX labels
			// don't go off bottom of screen
			int offsetY = 10 + view.getFontSize();

			height += offsetY;
			bimg = createBufferedImage();
			g2d = createGraphics2D(bimg, cpt);

			App app = view.getApplication();
			app.getDrawEquation().drawEquation(app,
					geo, g2d, 0, -offsetY, cpt.textToDraw(),
					cpt.font(), cpt.isSerifFont(), cpt.foregroundColor(),
					cpt.backgroundColor(),
					true, false, getCallBack());
			return bimg;
		}

		bimg = createBufferedImage();
		g2d = createGraphics2D(bimg, cpt);
		g2d.setFont(cpt.font());

		if (hasIndex) {
			EuclidianStatic.drawIndexedString(view.getApplication(), g2d, cpt.text(),
					0, 0, false);
		} else {
			drawPlainTextLabel(g2d, cpt);
		}

		return bimg;
	}

	private void drawPlainTextLabel(GGraphics2D g2d, @Nonnull CaptionText cpt) {
		GFont font0 = view.getApplication().getFontCanDisplay(cpt.text(),
				cpt.isSerifFont(), cpt.font().getStyle(), cpt.font().getSize());
		g2d.setFont(font0);
		g2d.drawString(cpt.text(), 0, 0);
	}

	/**
	 * 
	 * @return callback (for JLM)
	 */
	protected Runnable getCallBack() {
		if (callBack == null) {
			callBack = new DrawLaTeXCallBack(this);
		}

		return callBack;
	}

	protected class DrawLaTeXCallBack implements Runnable {

		private final DrawLabel3D label;

		DrawLaTeXCallBack(DrawLabel3D label) {
			this.label = label;
		}

		@Override
		public void run() {
			label.drawable.setLabelWaitForReset();
			view.repaintView();
		}

	}

	/**
	 * draws the label
	 * 
	 * @param renderer
	 *            renderer
	 */
	public void draw(Renderer renderer) {
		draw(renderer, false);
	}

	/**
	 * 
	 * @return z position (in screen coords) where the label is drawn
	 */
	public double getDrawZ() {
		return drawZ;
	}

	/**
	 * 
	 * update position for axes labels (x/y/z)
	 * 
	 * @param xOffset1
	 *            x offset
	 * @param yOffset1
	 *            y offset
	 * @param zOffset1
	 *            z offset
	 * @param tickSize
	 *            tick size
	 */
	public void updateDrawPositionAxes(float xOffset1, float yOffset1,
			float zOffset1, int tickSize) {
		this.xOffset = xOffset1;
		this.yOffset = yOffset1;
		this.zOffset = zOffset1;
		updateDrawPositionAxes(tickSize);
	}

	/**
	 * update position for axes numbers
	 * 
	 * @param tickSize
	 *            tick sizes
	 */
	private void updateDrawPositionAxes(int tickSize) {
		drawX = (int) vScreen.getX();
		drawY = (int) vScreen.getY();
		drawZ = (int) vScreen.getZ();

		double radius = MyMath.length(pickingW, pickingH) / 2 / getFontScale();
		drawX += radius * xOffset;
		drawY += radius * yOffset;
		drawZ += radius * zOffset;

		double f = 1.5;
		drawX += f * tickSize * xOffset;
		drawY += f * tickSize * yOffset;
		drawZ += f * tickSize * zOffset;
	}

	/**
	 * 
	 * @param x
	 *            mouse x position
	 * @param y
	 *            mouse y position
	 * @return true if mouse hits the label
	 */
	public boolean hit(double x, double y) {
		if (properties.hasBackgroundColor()) {
			return drawX <= x && drawX + width >= x && drawY <= y
					&& drawY + height >= y;
		}

		return drawX + pickingX <= x && drawX + pickingX + pickingW >= x
				&& drawY + pickingY <= y && drawY + pickingY + pickingH >= y;
	}

	/**
	 * 
	 * @param o
	 *            mouse origin
	 * @param direction
	 *            mouse direction
	 * @return true if mouse hits the label
	 */
	public boolean hit(Coords o, Coords direction) {
		double x = o.getX()
				+ (drawZ - o.getZ()) * direction.getX() / direction.getZ();
		double y = o.getY()
				+ (drawZ - o.getZ()) * direction.getY() / direction.getZ();
		return hit(x, y);
	}

	/**
	 * draw at (x,y,z)
	 * 
	 * @param renderer
	 *            renderer
	 */
	protected void drawText(Renderer renderer) {
		// draw text
		renderer.setColor(properties.foregroundColorNormalized());
		renderer.getRendererImpl().enableTextures();
		renderer.getRendererImpl().setLayer(Renderer.LAYER_FOR_TEXTS);
		renderer.getTextures().setTextureLinear(textureIndex);
		renderer.getGeometryManager().drawLabel(textIndex);
		renderer.getRendererImpl().setLayer(Renderer.LAYER_DEFAULT);

	}

	@Override
	public boolean isPickable() {
		return drawable.hasPickableLabel();
	}

	@Override
	protected Coords getBackgroundColor() {
		return properties.hasBackgroundColor() ? properties.backgroundColorNormalized() : null;
	}

	@Override
	protected void drawContent(Renderer renderer) {
		drawText(renderer);
	}
}

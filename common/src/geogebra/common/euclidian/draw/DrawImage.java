/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * DrawPoint.java
 *
 * Created on 11. Oktober 2001, 23:59
 */

package geogebra.common.euclidian.draw;

import geogebra.common.awt.GBufferedImage;
import geogebra.common.awt.GPoint2D;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoPoint;

/**
 * 
 * @author Markus
 */
public final class DrawImage extends Drawable {

	private GeoImage geoImage;
	private boolean isVisible;
	private GBufferedImage image;

	private boolean absoluteLocation;
	private geogebra.common.awt.GAlphaComposite alphaComp;
	private float alpha = -1;
	private boolean isInBackground = false;
	private geogebra.common.awt.GAffineTransform at, atInverse, tempAT;
	private boolean needsInterpolationRenderingHint;
	private int screenX, screenY;
	private geogebra.common.awt.GRectangle boundingBox;
	private geogebra.common.awt.GGeneralPath highlighting;

	/**
	 * Creates new drawble image
	 * @param view view
	 * @param geoImage image
	 */
	public DrawImage(EuclidianView view, GeoImage geoImage) {
		this.view = view;
		this.geoImage = geoImage;
		geo = geoImage;

		// temp
		at = AwtFactory.prototype.newAffineTransform();
		tempAT = AwtFactory.prototype.newAffineTransform();
		boundingBox = AwtFactory.prototype.newRectangle();

		selStroke = AwtFactory.prototype.newMyBasicStroke(1.5f);

		update();
	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();

		if (!isVisible)
			return;

		if (geo.getAlphaValue() != alpha) {
			alpha = geo.getAlphaValue();
			alphaComp = AwtFactory.prototype.newAlphaComposite(
					geogebra.common.awt.GAlphaComposite.SRC_OVER,alpha);
		}

		image = geoImage
				.getFillImage();
		int width = image.getWidth();
		int height = image.getHeight();
		absoluteLocation = geoImage.isAbsoluteScreenLocActive();

		// ABSOLUTE SCREEN POSITION
		if (absoluteLocation) {
			screenX = geoImage.getAbsoluteScreenLocX();
			screenY = geoImage.getAbsoluteScreenLocY() - height;
			labelRectangle.setBounds(screenX, screenY, width, height);
		}

		// RELATIVE SCREEN POSITION
		else {
			GeoPoint A = geoImage.getCorner(0);
			GeoPoint B = geoImage.getCorner(1);
			GeoPoint D = geoImage.getCorner(2);

			double ax = 0;
			double ay = 0;
			if (A != null) {
				if (!A.isDefined()) {
					isVisible = false;
					return;
				}
				ax = A.inhomX;
				ay = A.inhomY;
			}

			// set transform according to corners
			at.setTransform(view.getCoordTransform()); // last transform: real world
													// -> screen
			at.translate(ax, ay); // translate to first corner A

			if (B == null) {
				// we only have corner A
				if (D == null) {
					// use original pixel width and heigt of image
					at.scale(view.getInvXscale(), -view.getInvXscale());
				}
				// we have corners A and D
				else {
					if (!D.isDefined()) {
						isVisible = false;
						return;
					}
					// rotate to coord system (-ADn, AD)
					double ADx = D.inhomX - ax;
					double ADy = D.inhomY - ay;
					tempAT.setTransform(ADy, -ADx, ADx, ADy, 0, 0);
					at.concatenate(tempAT);

					// scale height of image to 1
					double yscale = 1.0 / height;
					at.scale(yscale, -yscale);
				}
			} else {
				if (!B.isDefined()) {
					isVisible = false;
					return;
				}

				// we have corners A and B
				if (D == null) {
					// rotate to coord system (AB, ABn)
					double ABx = B.inhomX - ax;
					double ABy = B.inhomY - ay;
					tempAT.setTransform(ABx, ABy, -ABy, ABx, 0, 0);
					at.concatenate(tempAT);

					// scale width of image to 1
					double xscale = 1.0 / width;
					at.scale(xscale, -xscale);
				} else { // we have corners A, B and D
					if (!D.isDefined()) {
						isVisible = false;
						return;
					}

					// shear to coord system (AB, AD)
					double ABx = B.inhomX - ax;
					double ABy = B.inhomY - ay;
					double ADx = D.inhomX - ax;
					double ADy = D.inhomY - ay;
					tempAT.setTransform(ABx, ABy, ADx, ADy, 0, 0);
					at.concatenate(tempAT);

					// scale width and height of image to 1
					at.scale(1.0 / width, -1.0 / height);
				}
			}

			// move image up so that A becomes lower left corner
			at.translate(0, -height);
			labelRectangle.setBounds(0, 0, width, height);

			// calculate bounding box for isInside
			boundingBox.setBounds(0, 0, width, height);
			geogebra.common.awt.GShape shape = at.createTransformedShape(boundingBox);
			boundingBox = shape.getBounds();

			try {
				// for hit testing
				atInverse = at.createInverse();
			} catch (Exception e) {
				isVisible = false;
				return;
			}

			// improve rendering for sheared and scaled images (translations
			// don't need this)
			// turns false if the image doen't want interpolation
			needsInterpolationRenderingHint = (geoImage.isInterpolate())
					&& !(Kernel.isEqual(at.getScaleX(), 1.0,
							Kernel.MAX_PRECISION)
							&& Kernel.isEqual(at.getScaleY(), 1.0,
									Kernel.MAX_PRECISION)
							&& Kernel.isEqual(at.getShearX(), 0.0,
									Kernel.MAX_PRECISION) && Kernel
								.isEqual(at.getShearY(), 0.0,
										Kernel.MAX_PRECISION));
		}
		
		if (isInBackground != geoImage.isInBackground()) {
			isInBackground = !isInBackground;
			if (isInBackground) {
				view.addBackgroundImage(this);
			} else {
				view.removeBackgroundImage(this);
				view.updateBackgroundImage();
			}
		}

		if (isInBackground)
			view.updateBackgroundImage();
	}

	@Override
	final public void draw(geogebra.common.awt.GGraphics2D g3) {
		if (isVisible) {
			geogebra.common.awt.GComposite oldComp = g3.getComposite();
			if (alpha >= 0f && alpha < 1f) {
				if (alphaComp == null)
					alphaComp = AwtFactory.prototype.newAlphaComposite(
							geogebra.common.awt.GAlphaComposite.SRC_OVER, alpha);
				g3.setComposite(alphaComp);
			}

			if (absoluteLocation) {
				g3.drawImage(image, null, screenX, screenY);
				if (!isInBackground && geo.doHighlighting()) {
					// draw rectangle around image
					g3.setStroke(selStroke);
					g3.setPaint(geogebra.common.awt.GColor.lightGray);
					g3.draw(labelRectangle);
				}
			} else {
				geogebra.common.awt.GAffineTransform oldAT = g3.getTransform();
				g3.transform(at);

				// improve rendering quality for transformed images
				Object oldInterpolationHint = geogebra.common.euclidian.EuclidianStatic.setInterpolationHint(g3,needsInterpolationRenderingHint);

				// g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

				g3.drawImage(image, null, 0, 0);
				if (!isInBackground && geo.doHighlighting()) {
					// draw rectangle around image
					g3.setStroke(selStroke);
					g3.setPaint(geogebra.common.awt.GColor.lightGray);

					// changed to code below so that the line thicknesses aren't
					// transformed
					// g2.draw(labelRectangle);

					// draw parallelogram around edge
					drawHighlighting(at, g3);
					GPoint2D corner1 = AwtFactory.prototype.newPoint2D(
							labelRectangle.getMinX(), labelRectangle.getMinY());
					GPoint2D corner2 = AwtFactory.prototype.newPoint2D(
							labelRectangle.getMinX(), labelRectangle.getMaxY());
					GPoint2D corner3 = AwtFactory.prototype.newPoint2D(
							labelRectangle.getMaxX(), labelRectangle.getMaxY());
					GPoint2D corner4 = AwtFactory.prototype.newPoint2D(
							labelRectangle.getMaxX(), labelRectangle.getMinY());
					at.transform(corner1, corner1);
					at.transform(corner2, corner2);
					at.transform(corner3, corner3);
					at.transform(corner4, corner4);
					if (highlighting == null)
						highlighting = AwtFactory.prototype.newGeneralPath();
					else
						highlighting.reset();
					highlighting.moveTo((float) corner1.getX(),
							(float) corner1.getY());
					highlighting.lineTo((float) corner2.getX(),
							(float) corner2.getY());
					highlighting.lineTo((float) corner3.getX(),
							(float) corner3.getY());
					highlighting.lineTo((float) corner4.getX(),
							(float) corner4.getY());
					highlighting.lineTo((float) corner1.getX(),
							(float) corner1.getY());
					g3.setTransform(oldAT);
					g3.draw(highlighting);

				}

				// reset previous values
				EuclidianStatic.resetInterpolationHint(g3,oldInterpolationHint);
				g3.setTransform(oldAT);
			}

			g3.setComposite(oldComp);
		}
	}

	/**
	 * Draws highligting (not implemented)
	 * @param at2 transform
	 * @param g2 graphics 
	 */
	private void drawHighlighting(geogebra.common.awt.GAffineTransform at2,
			geogebra.common.awt.GGraphics2D g2) {
		// TODO Auto-generated method stub

	}
	/**
	 * Returns whether this is background image
	 * @return true for background images
	 */
	boolean isInBackground() {
		return geoImage.isInBackground();
	}

	/**
	 * was this object clicked at? (mouse pointer location (x,y) in screen
	 * coords)
	 */
	@Override
	final public boolean hit(int x, int y) {
		if (!isVisible || geoImage.isInBackground())
			return false;

		hitCoords[0] = x;
		hitCoords[1] = y;

		// convert screen to image coordinate system
		if (!geoImage.isAbsoluteScreenLocActive()) {
			atInverse.transform(hitCoords, 0, hitCoords, 0, 1);
		}
		return labelRectangle.contains(hitCoords[0], hitCoords[1]);
	}

	private double[] hitCoords = new double[2];

	@Override
	final public boolean isInside(geogebra.common.awt.GRectangle rect) {
		if (!isVisible || geoImage.isInBackground())
			return false;
		return rect.contains(boundingBox);
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	final public geogebra.common.awt.GRectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}
		return boundingBox;
	}

	/**
	 * Returns false
	 */
	@Override
	public boolean hitLabel(int x, int y) {
		return false;
	}

	@Override
	final public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	final public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

}

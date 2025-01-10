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
 * Created on 11. October 2001, 23:59
 */

package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GAlphaComposite;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GComposite;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.App;
import org.geogebra.common.util.DoubleUtil;

/**
 * 
 * @author Markus
 */
public class DrawImage extends Drawable {
	protected GeoImage geoImage;
	protected GAffineTransform atInverse;
	private boolean isVisible;

	private boolean absoluteLocation;
	private GAlphaComposite alphaComp;
	private double alpha = -1;
	private boolean isInBackground = false;
	private GAffineTransform at;
	private GAffineTransform tempAT;
	private boolean needsInterpolationRenderingHint;
	private int screenX;
	private int screenY;
	private GRectangle classicBoundingBox;
	private GGeneralPath highlighting;
	private double[] hitCoords = new double[2];

	/**
	 * the croped image should have at least 50px width
	 */
	public final static int IMG_CROP_THRESHOLD = 50;

	/**
	 * Creates new drawable image
	 * 
	 * @param view
	 *            view
	 * @param geoImage
	 *            image
	 */
	public DrawImage(EuclidianView view, GeoImage geoImage) {
		this.view = view;
		this.geoImage = geoImage;

		geo = geoImage;
		// temp
		at = AwtFactory.getPrototype().newAffineTransform();
		tempAT = AwtFactory.getPrototype().newAffineTransform();
		classicBoundingBox = AwtFactory.getPrototype().newRectangle();

		selStroke = AwtFactory.getPrototype().newMyBasicStroke(2);
	}
	
	@Override
	public void update() {
		isVisible = geo.isEuclidianVisible();

		if (isVisible) {
			updateAssumingVisible();
		}
	}

	protected void updateAssumingVisible() {
		if (geo.getAlphaValue() != alpha) {
			alpha = geo.getAlphaValue();
			alphaComp = AwtFactory.getPrototype().newAlphaComposite(alpha);
		}

		MyImage image = geoImage.getFillImage();
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
			boolean center = geoImage.isCentered();
			int number = center ? 3 : 0;
			GeoPoint A = geoImage.getStartPoint(number);
			GeoPoint B = center ? null : geoImage.getStartPoint(1);
			GeoPoint D = center ? null : geoImage.getStartPoint(2);
			double ax = 0;
			double ay = 0;
			if (A != null) {
				if (!A.isDefined() || A.isInfinite()) {
					isVisible = false;
					return;
				}
				ax = A.inhomX;
				ay = A.inhomY;
			}

			// set transform according to corners
			at.setTransform(view.getCoordTransform()); // last transform: real
														// world
														// -> screen

			at.translate(ax, ay); // translate to first corner A

			if (B == null) {
				// we only have corner A
				if (D == null) {
					// use original pixel width and height of image
					at.scale(view.getInvXscale(),
							// make sure old files work
							// https://dev.geogebra.org/trac/changeset/57611
							geo.getKernel().getApplication().fileVersionBefore(
									5, 0, 397, 0)
											? -view.getInvXscale()
											: -view.getInvYscale());
				}
				// we have corners A and D
				else {
					if (!D.isDefined() || D.isInfinite()) {
						isVisible = false;
						return;
					}
					// rotate to coord system (-normal(AD), AD)
					double ADx = D.inhomX - ax;
					double ADy = D.inhomY - ay;
					tempAT.setTransform(ADy, -ADx, ADx, ADy, 0, 0);
					at.concatenate(tempAT);

					// scale height of image to 1
					double yscale = 1.0 / height;
					at.scale(yscale, -yscale);
				}
			} else {
				if (!B.isDefined() || B.isInfinite()) {
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
					if (!D.isDefined() || D.isInfinite()) {
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

			if (geoImage.isCentered()) {
				// move image to the center
				at.translate(-width / 2.0, -height / 2.0);
			} else {
				// move image up so that A becomes lower left corner
				at.translate(0, -height);
			}
			labelRectangle.setBounds(0, 0, width, height);

			// calculate bounding box for isInside
			classicBoundingBox.setBounds(0, 0, width, height);
			GShape shape = at.createTransformedShape(classicBoundingBox);
			classicBoundingBox = shape.getBounds();

			try {
				// for hit testing
				atInverse = at.createInverse();
			} catch (Exception e) {
				isVisible = false;
				return;
			}

			// improve rendering for sheared and scaled images (translations
			// don't need this)
			// turns false if the image doesn't want interpolation
			needsInterpolationRenderingHint = (geoImage.isInterpolate())
					&& (!isTranslation(at) || view.getPixelRatio() != 1);
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

		if (!view.isBackgroundUpdating() && isInBackground) {
			view.updateBackgroundImage();
		}
	}

	private static boolean isTranslation(GAffineTransform at2) {
		return DoubleUtil.isEqual(at2.getScaleX(), 1.0, Kernel.MAX_PRECISION)
				&& DoubleUtil.isEqual(at2.getScaleY(), 1.0, Kernel.MAX_PRECISION)
				&& DoubleUtil.isEqual(at2.getShearX(), 0.0, Kernel.MAX_PRECISION)
				&& DoubleUtil.isEqual(at2.getShearY(), 0.0, Kernel.MAX_PRECISION);
	}

	/**
	 * If background flag changed, do immediate update. Otherwise mark for
	 * update after next repaint.
	 * 
	 * @return whether it was in background for the whole time
	 */
	public boolean checkInBackground() {
		if (isInBackground != geoImage.isInBackground()) {
			update();
		} else {
			setNeedsUpdate(true);
		}
		return isInBackground && geoImage.isInBackground();
	}

	@Override
	public void draw(GGraphics2D g3) {
		if (geoImage.isMeasurementTool() && view.getApplication().isExporting()) {
			return;
		}
		if (isVisible) {
			GComposite oldComp = g3.getComposite();
			if (alpha >= 0f && alpha < 1f) {
				if (alphaComp == null) {
					alphaComp = AwtFactory.getPrototype()
							.newAlphaComposite(alpha);
				}
				g3.setComposite(alphaComp);
			}
			MyImage image = geoImage.getFillImage();
			if (absoluteLocation) {
				g3.drawImage(image, screenX, screenY);
				if (!isInBackground && isHighlighted()) {
					// draw rectangle around image
					g3.setStroke(selStroke);
					g3.setPaint(GColor.HIGHLIGHT_GRAY);
					drawHighlightRect(g3);
				}
			} else {
				g3.saveTransform();
				g3.transform(at);

				// improve rendering quality for transformed images
				Object oldInterpolationHint = g3
						.setInterpolationHint(needsInterpolationRenderingHint);
				if (view.getBoundingBox() != null && view.getBoundingBox().isCropBox()
						&& geo.isSelected()) {
					g3.setComposite(AwtFactory.getPrototype().newAlphaComposite(0.5f));
					g3.drawImage(image, 0, 0);
					g3.setComposite(AwtFactory.getPrototype().newAlphaComposite(1.0f));
				}
				if (!geoImage.isCropped()) {
					g3.drawImage(image, 0, 0);
				} else {
					GRectangle2D rect = geoImage.getCropBoxRelative();

					g3.drawImage(image, (int) rect.getX(), (int) rect.getY(),
							(int) rect.getWidth(), (int) rect.getHeight(), (int) rect.getX(),
							(int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
				}
				
				g3.restoreTransform();
				if (!isInBackground && isHighlighted()) {
					// draw rectangle around image
					g3.setStroke(selStroke);
					g3.setPaint(GColor.HIGHLIGHT_GRAY);

					// changed to code below so that the line thicknesses aren't
					// transformed
					// g2.draw(labelRectangle);

					App app = geoImage.getKernel().getApplication();

					// no highlight if we have bounding box for mow
					if (!app.isWhiteboardActive()) {
						drawHighlighting(g3);
					}

				}

				// reset previous values
				g3.resetInterpolationHint(oldInterpolationHint);
			}

			g3.setComposite(oldComp);
		}
	}

	@Override
	public boolean isHighlighted() {
		return view.getApplication().getSelectionManager().isKeyboardFocused(geo);
	}

	private void drawHighlighting(GGraphics2D g3) {
		// draw parallelogram around edge
		double offX = HIGHLIGHT_OFFSET
				* Math.abs(atInverse.getScaleX() + atInverse.getShearX());
		double minX = labelRectangle.getMinX();
		double offY = HIGHLIGHT_OFFSET
				* Math.abs(atInverse.getScaleY() + atInverse.getShearY());
		double minY = labelRectangle.getMinY();
		double maxX = labelRectangle.getMaxX();
		double maxY = labelRectangle.getMaxY();
		double rx = offX / 2;
		double ry = offY / 2;
		if (highlighting == null) {
			highlighting = AwtFactory.getPrototype()
					.newGeneralPath();
		} else {
			highlighting.reset();
		}
		highlighting.moveTo(minX, minY - offY);
		highlighting.lineTo(maxX, minY - offY); // bottom edge
		highlighting.curveTo(maxX + offX - rx, minY - offY,
				maxX + offX, minY - offY + ry, maxX + offX, minY);
		highlighting.lineTo(maxX + offX, maxY); // right edge
		highlighting.curveTo(maxX + offX, maxY + offY - ry,
				maxX + offX - rx, maxY + offY, maxX, maxY + offY);
		highlighting.lineTo(minX, maxY + offY); // top edge
		highlighting.curveTo(minX - offX + rx, maxY + offY,
				minX - offX, maxY + offY - ry, minX - offX, maxY);
		highlighting.lineTo(minX - offX, minY); // left edge
		highlighting.curveTo(minX - offX, minY - offY + ry,
				minX - offX + rx, minY - offY, minX, minY - offY);
		highlighting.closePath();
		GShape shape = highlighting.createTransformedShape(at);
		g3.draw(shape);
	}

	/**
	 * Returns whether this is background image
	 * 
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
	public boolean hit(int x, int y, int hitThreshold) {
		if (geoImage.isInBackground()) {
			return false;
		}

		hitCoords[0] = x;
		hitCoords[1] = y;

		// convert screen to image coordinate system
		if (!geoImage.isAbsoluteScreenLocActive() && atInverse != null) {
			atInverse.transform(hitCoords, 0, hitCoords, 0, 1);
		}
		if (geoImage.isCropped()) {
			return geoImage.getCropBoxRelative().contains(hitCoords[0],
					hitCoords[1]);
		}
		return labelRectangle.contains(hitCoords[0], hitCoords[1]);
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		if (!isVisible || geoImage.isInBackground()) {
			return false;
		}
		return rect.intersects(view.getApplication().isWhiteboardActive()
				? getBoundingBox().getRectangle() : classicBoundingBox);
	}

	@Override
	public boolean isInside(GRectangle rect) {
		if (!isVisible || geoImage.isInBackground()) {
			return false;
		}
		return rect.contains(view.getApplication().isWhiteboardActive()
				? getBoundingBox().getRectangle() : classicBoundingBox);
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	public GRectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}
		return classicBoundingBox.getBounds();
	}

	/**
	 * Returns false
	 */
	@Override
	public boolean hitLabel(int x, int y) {
		return false;
	}

	@Override
	public GRectangle2D getBoundsClipped() {
		updateIfNeeded();
		return super.getBoundsClipped();
	}

	protected GAffineTransform getTransform() {
		return at;
	}

}
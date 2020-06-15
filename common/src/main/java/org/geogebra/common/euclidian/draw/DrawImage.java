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

package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GAlphaComposite;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GComposite;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.CropBox;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianBoundingBoxHandler;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.App;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;

/**
 * 
 * @author Markus
 */
public final class DrawImage extends Drawable {
	private GeoImage geoImage;
	private boolean isVisible;
	private MyImage image;

	private boolean absoluteLocation;
	private GAlphaComposite alphaComp;
	private double alpha = -1;
	private boolean isInBackground = false;
	private GAffineTransform at;
	private GAffineTransform atInverse;
	private GAffineTransform tempAT;
	private boolean needsInterpolationRenderingHint;
	private int screenX;
	private int screenY;
	private GRectangle classicBoundingBox;
	private GGeneralPath highlighting;
	private double[] hitCoords = new double[2];
	private CropBox boundingBox;
	private double originalRatio = Double.NaN;
	/**
	 * ratio of the whole image and the crop box width
	 */
	private double imagecropRatioX = Double.NaN;
	/**
	 * ratio of the whole image and the crop box width
	 */
	private double imagecropRatioY = Double.NaN;
	/**
	 * the image should have at least 50px width
	 */
	public final static int IMG_WIDTH_THRESHOLD = 50;
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

		selStroke = AwtFactory.getPrototype().newMyBasicStroke(1.5d);
		update();
	}
	
	@Override
	public void update() {
		isVisible = geo.isEuclidianVisible();

		if (!isVisible) {
			return;
		}

		if (geo.getAlphaValue() != alpha) {
			alpha = geo.getAlphaValue();
			alphaComp = AwtFactory.getPrototype().newAlphaComposite(alpha);
		}

		image = geoImage.getFillImage();
		int width = image.getWidth();
		int height = image.getHeight();
		absoluteLocation = geoImage.isAbsoluteScreenLocActive();

		// ABSOLUTE SCREEN POSITION
		if (absoluteLocation) {
			// scaleX and scaleY should be 1 if there is no MOW_PIN_IMAGE
			// feature flag, so in that case there is no any effect of these
			double scaleX = geoImage.getScaleX();
			double scaleY = geoImage.getScaleY();
			screenX = geoImage.getAbsoluteScreenLocX();
			screenY = (int) (geoImage.getAbsoluteScreenLocY() - height * scaleY);
			if (geo.getKernel().getApplication().isWhiteboardActive()) {
				classicBoundingBox.setBounds(screenX, screenY,
						(int) (width * scaleX), (int) (height * scaleY));
			}
			labelRectangle.setBounds(screenX, screenY, (int) (width * scaleX),
					(int) (height * scaleY));
		}

		// RELATIVE SCREEN POSITION
		else {
			boolean center = geoImage.isCentered();
			GeoPoint A = geoImage.getCorner(center ? 3 : 0);
			GeoPoint B = center ? null : geoImage.getCorner(1);
			GeoPoint D = center ? null : geoImage.getCorner(2);
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
									new int[] { 5, 0, 397, 0 })
											? -view.getInvXscale()
											: -view.getInvYscale());
				}
				// we have corners A and D
				else {
					if (!D.isDefined() || D.isInfinite()) {
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
			// turns false if the image doen't want interpolation
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

		if (geo.getKernel().getApplication().isWhiteboardActive()) {
			if (geoImage.isCropped() && geoImage.getCropBoxRelative() != null) {
				getBoundingBox().setRectangle(getCropBox());
			} else if (getBounds() != null) {
				getBoundingBox().setRectangle(getBounds());
			}
		}
	}

	@Override
	public double getWidthThreshold() {
		return IMG_WIDTH_THRESHOLD;
	}

	@Override
	public double getHeightThreshold() {
		return IMG_WIDTH_THRESHOLD;
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
		if (isVisible) {
			GComposite oldComp = g3.getComposite();
			if (alpha >= 0f && alpha < 1f) {
				if (alphaComp == null) {
					alphaComp = AwtFactory.getPrototype()
							.newAlphaComposite(alpha);
				}
				g3.setComposite(alphaComp);
			}

			if (absoluteLocation) {
				g3.saveTransform();
				g3.translate(screenX, screenY);
				g3.scale(geoImage.getScaleX(), geoImage.getScaleY());
				g3.translate(-screenX, -screenY);
				g3.drawImage(image, screenX, screenY);
				g3.restoreTransform();
				if (!isInBackground && isHighlighted()) {
					// draw rectangle around image
					g3.setStroke(selStroke);
					g3.setPaint(GColor.LIGHT_GRAY);
					g3.draw(labelRectangle);
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
					GRectangle2D drawRectangle = getCropBox();
					GPoint2D ptDst = new GPoint2D();
					GPoint2D ptDst2 = new GPoint2D();
					GPoint2D ptScr = new GPoint2D(
							drawRectangle.getX(), drawRectangle.getY());
					GPoint2D ptScr2 = new GPoint2D(
							drawRectangle.getMaxX(), drawRectangle.getMaxY());
					atInverse.transform(ptScr, ptDst);
					atInverse.transform(ptScr2, ptDst2);

					int cropWidth = Math.min(image.getWidth(),
							(int) Math.abs(ptDst2.getX() - ptDst.getX()));
					int cropHeight = Math.min(image.getHeight(),
							(int) Math.abs(ptDst2.getY() - ptDst.getY()));
					if (ptDst.getX() < 0) {
						ptDst.setX(0);
					}
					if (ptDst.getY() < 0) {
						ptDst.setY(0);
					}

					g3.drawImage(image, (int) ptDst.getX(), (int) ptDst.getY(),
							cropWidth, cropHeight, (int) ptDst.getX(),
							(int) ptDst.getY(), cropWidth, cropHeight);
				}
				
				g3.restoreTransform();
				if (!isInBackground && isHighlighted()) {
					// draw rectangle around image
					g3.setStroke(selStroke);
					g3.setPaint(GColor.LIGHT_GRAY);

					// changed to code below so that the line thicknesses aren't
					// transformed
					// g2.draw(labelRectangle);

					// draw parallelogram around edge
					GPoint2D corner1 = new GPoint2D(
							labelRectangle.getMinX(), labelRectangle.getMinY());
					GPoint2D corner2 = new GPoint2D(
							labelRectangle.getMinX(), labelRectangle.getMaxY());
					GPoint2D corner3 = new GPoint2D(
							labelRectangle.getMaxX(), labelRectangle.getMaxY());
					GPoint2D corner4 = new GPoint2D(
							labelRectangle.getMaxX(), labelRectangle.getMinY());
					at.transform(corner1, corner1);
					at.transform(corner2, corner2);
					at.transform(corner3, corner3);
					at.transform(corner4, corner4);

					App app = geoImage.getKernel().getApplication();

					// show highlighting only if toolbar showing
					// needed for eg Reflect tool
					// no highlight if we have bounding box for mow
					if (!app.isWhiteboardActive() && app.showToolBar()) {
						if (highlighting == null) {
							highlighting = AwtFactory.getPrototype()
									.newGeneralPath();
						} else {
							highlighting.reset();
						}
						highlighting.moveTo(corner1.getX(), corner1.getY());
						highlighting.lineTo(corner2.getX(), corner2.getY());
						highlighting.lineTo(corner3.getX(), corner3.getY());
						highlighting.lineTo(corner4.getX(), corner4.getY());
						highlighting.lineTo(corner1.getX(), corner1.getY());

						g3.draw(highlighting);
					}

				}

				// reset previous values
				g3.resetInterpolationHint(oldInterpolationHint);
			}

			g3.setComposite(oldComp);
		}
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
		if (!isVisible || geoImage.isInBackground()) {
			return false;
		}

		hitCoords[0] = x;
		hitCoords[1] = y;

		// convert screen to image coordinate system
		if (!geoImage.isAbsoluteScreenLocActive()) {
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
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public CropBox getBoundingBox() {
		if (boundingBox == null) {
			boundingBox = new CropBox();
			boundingBox.setColor(view.getApplication().getPrimaryColor());
		}
		boundingBox.updateFrom(geo);
		return boundingBox;
	}

	private void updateOriginalRatio() {
		double width, height;
		if (geoImage.isCropped()) {
			width = getBoundingBox().getRectangle().getWidth();
			height = getBoundingBox().getRectangle().getHeight();
		} else {
			width = geoImage.getImageScreenWidth();
			height = geoImage.getImageScreenHeight();
		}
		originalRatio = height / width;
	}

	@Override
	public void updateByBoundingBoxResize(GPoint2D p,
			EuclidianBoundingBoxHandler handler) {
		if (!geo.getKernel().getApplication().isWhiteboardActive()) {
			return;
		}
		if (boundingBox.isCropBox()) {
			geoImage.setCropped(true);
			if (Double.isNaN(originalRatio)) {
				updateOriginalRatio();
			}
			updateImageCrop(p, handler);
		}
	}

	@Override
	public void fromPoints(ArrayList<GPoint2D> points) {
		GeoPoint A, B, D;
		double cropMinX, cropMaxX, cropMinY, cropMaxY;
		GRectangle2D cropBox = null;
		if (geoImage.isCropped()) {
			if (!hasImageCropRatio()) {
				updateImageCropRatio();
			}
			cropBox = getCropBox();
			cropMinX = view.toRealWorldCoordX(cropBox.getMinX());
			cropMaxX = view.toRealWorldCoordX(cropBox.getMaxX());
			cropMinY = view.toRealWorldCoordY(cropBox.getMinY());
			cropMaxY = view.toRealWorldCoordY(cropBox.getMaxY());

			A = new GeoPoint(geo.getConstruction(), cropMinX, cropMaxY, 1.0);
			A.remove();
			B = new GeoPoint(geo.getConstruction(), cropMaxX, cropMaxY, 1.0);
			B.remove();
			D = new GeoPoint(geo.getConstruction(), cropMinX, cropMinY, 1.0);
			D.remove();
		} else {
			A = geoImage.getCorner(0);
			B = geoImage.getCorner(1);
			D = geoImage.getCorner(2);
		}

		if (A == null) {
			A = new GeoPoint(geoImage.cons);
			geoImage.calculateCornerPoint(A, 1);
		}
		if (B == null) {
			B = new GeoPoint(geoImage.cons);
			geoImage.calculateCornerPoint(B, 2);
		}
		if (D == null) {
			D = new GeoPoint(geoImage.cons);
			geoImage.calculateCornerPoint(D, 3);
		}
		double screenLeft = Math.min(points.get(0).getX(), points.get(1).getX());
		double screenRight = Math.max(points.get(0).getX(), points.get(1).getX());
		double screenTop = Math.min(points.get(0).getY(), points.get(1).getY());
		double screenBottom = Math.max(points.get(0).getY(), points.get(1).getY());
		A.setCoords(view.toRealWorldCoordX(screenLeft), view.toRealWorldCoordY(screenBottom), 1);
		A.updateRepaint();
		B.setCoords(view.toRealWorldCoordX(screenRight), view.toRealWorldCoordY(screenBottom), 1);
		B.updateRepaint();
		D.setCoords(view.toRealWorldCoordX(screenLeft), view.toRealWorldCoordY(screenTop), 1);
		setCorner(D, 2);
		D.updateRepaint();

		if (geoImage.isCropped()) {
			updateCropHorizontal(A, B, cropBox);
			updateCropVerical(A, D, cropBox);
			updateCascadeCorners();
		}
	}

	private void updateCascadeCorners() {
		geoImage.getCorner(0).updateCoords();
		geoImage.getCorner(0).updateRepaint();
		geoImage.getCorner(1).updateCoords();
		geoImage.getCorner(1).updateRepaint();
		if (geoImage.getCorner(2) != null) {
			geoImage.getCorner(2).updateCoords();
			geoImage.getCorner(2).updateRepaint();
		}
	}

	private void updateImageCrop(GPoint2D p,
			EuclidianBoundingBoxHandler handler) {
		double eventX = p.getX();
		double eventY = p.getY();
		double newWidth = 1;
		double newHeight = 1;
		GRectangle2D rect = AwtFactory.getPrototype().newRectangle2D();
		GRectangle2D boundingBoxRectangle = getBoundingBox().getRectangle();
		switch (handler) {
		case BOTTOM:
			eventY = MyMath.clamp(eventY,
					boundingBoxRectangle.getMinY()
							+ Math.min(IMG_CROP_THRESHOLD, image.getHeight()),
					getBounds().getMaxY());
			rect.setRect(boundingBoxRectangle.getX(),
					boundingBoxRectangle.getY(),
					boundingBoxRectangle.getWidth(),
					eventY - boundingBoxRectangle.getY());
			originalRatio = Double.NaN;
			break;
		case TOP:
			eventY = MyMath.clamp(eventY, getBounds().getMinY(),
					boundingBoxRectangle.getMaxY()
							- Math.min(IMG_CROP_THRESHOLD, image.getHeight()));
			rect.setRect(boundingBoxRectangle.getX(), eventY,
					boundingBoxRectangle.getWidth(),
					boundingBoxRectangle.getMaxY() - eventY);
			originalRatio = Double.NaN;
			break;
		case LEFT:
			eventX = MyMath.clamp(eventX,
					getBounds().getMinX(),
					boundingBoxRectangle.getMaxX()
							- Math.min(IMG_CROP_THRESHOLD, image.getWidth()));
			rect.setRect(eventX, boundingBoxRectangle.getY(),
					boundingBoxRectangle.getMaxX() - eventX,
					boundingBoxRectangle.getHeight());
			originalRatio = Double.NaN;
			break;
		case RIGHT:
			eventX = MyMath.clamp(eventX,
					boundingBoxRectangle.getMinX()
							+ Math.min(IMG_CROP_THRESHOLD, image.getWidth()),
					getBounds().getMaxX());
			rect.setRect(boundingBoxRectangle.getX(),
					boundingBoxRectangle.getY(),
					eventX - boundingBoxRectangle.getX(),
					boundingBoxRectangle.getHeight());
			originalRatio = Double.NaN;
			break;
		case BOTTOM_RIGHT:
			newWidth = MyMath.clamp(
					eventX - boundingBoxRectangle.getMinX(),
					Math.min(IMG_CROP_THRESHOLD, image.getWidth()),
					getBounds().getMaxX()
							- boundingBoxRectangle.getMinX());
			newHeight = MyMath.clamp(originalRatio * newWidth,
					Math.min(IMG_CROP_THRESHOLD, image.getHeight()),
					getBounds().getMaxY()
							- boundingBoxRectangle.getMinY());
			rect.setRect(boundingBoxRectangle.getX(),
					boundingBoxRectangle.getY(), newWidth,
					newHeight);
			break;
		case BOTTOM_LEFT:
			newWidth = MyMath.clamp(
					boundingBoxRectangle.getMaxX() - eventX,
					Math.min(IMG_CROP_THRESHOLD, image.getWidth()),
					boundingBoxRectangle.getMaxX()
							- getBounds().getMinX());
			newHeight = MyMath.clamp(originalRatio * newWidth,
					Math.min(IMG_CROP_THRESHOLD, image.getHeight()),
					getBounds().getMaxY()
							- boundingBoxRectangle.getMinY());
			rect.setRect(boundingBoxRectangle.getMaxX() - newWidth,
					boundingBoxRectangle.getY(), newWidth,
					newHeight);
			break;
		case TOP_RIGHT:
			newWidth = MyMath.clamp(
					eventX - boundingBoxRectangle.getMinX(),
					Math.min(IMG_CROP_THRESHOLD, image.getWidth()),
					getBounds().getMaxX()
							- boundingBoxRectangle.getMinX());
			newHeight = MyMath.clamp(originalRatio * newWidth,
					Math.min(IMG_CROP_THRESHOLD, image.getHeight()),
					boundingBoxRectangle.getMaxY()
							- getBounds().getMinY());
			rect.setRect(boundingBoxRectangle.getX(),
					boundingBoxRectangle.getMaxY() - newHeight,
					newWidth,
					newHeight);
			break;
		case TOP_LEFT:
			newWidth = MyMath.clamp(
					boundingBoxRectangle.getMaxX() - eventX,
					Math.min(IMG_CROP_THRESHOLD, image.getWidth()),
					boundingBoxRectangle.getMaxX()
							- getBounds().getMinX());
			newHeight = MyMath.clamp(originalRatio * newWidth,
					Math.min(IMG_CROP_THRESHOLD, image.getHeight()),
					boundingBoxRectangle.getMaxY()
							- getBounds().getMinY());
			rect.setRect(boundingBoxRectangle.getMaxX() - newWidth,
					boundingBoxRectangle.getMaxY() - newHeight,
					newWidth, newHeight);
			break;
		default:
			break;
		}
		boundingBox.setRectangle(rect);
		// remember last crop box position
		setCropBox(rect);
		updateImageCropRatio();
	}

	private void updateImageCropRatio() {
		imagecropRatioX = geoImage.getFillImage().getWidth()
				/ geoImage.getCropBoxRelative().getWidth();
		imagecropRatioY = geoImage.getFillImage().getHeight()
				/ geoImage.getCropBoxRelative().getHeight();
	}

	private boolean hasImageCropRatio() {
		return !Double.isNaN(imagecropRatioX) && !Double.isNaN(imagecropRatioY);
	}

	/**
	 * Gets the ratio the current width of the image drawn on canvas and the
	 * original width of image.
	 */
	private double getOriginalRatioX() {
		return (view.getXscale() * geoImage.getImageScreenWidth())
				/ geoImage.getFillImage().getWidth();

	}

	/**
	 * Gets the ratio the current height of the image drawn on canvas and the
	 * original width of image.
	 */
	private double getOriginalRatioY() {
		return (view.getYscale() * geoImage.getImageScreenHeight())
				/ geoImage.getFillImage().getHeight();
	}

	private double getImageTop() {
		if (geoImage.getCorner(2) != null) {
			return view.toScreenCoordYd(geoImage.getCorner(2).getY());
		} else if (geoImage.getCorner(0) != null) {
			return view.toScreenCoordYd(geoImage.getCorner(0).getY())
					- geoImage.getFillImage().getHeight();
		}
		return 0;
	}

	private void setCropBox(GRectangle2D rect) {
		double locX = view.toScreenCoordXd(geoImage.getRealWorldLocX());
		double locY = getImageTop();
		GRectangle2D cb = AwtFactory.getPrototype().newRectangle2D();
		cb.setRect((rect.getMinX() - locX) / getOriginalRatioX(),
				(rect.getMinY() - locY) / getOriginalRatioY(),
				rect.getWidth() / getOriginalRatioX(),
				rect.getHeight() / getOriginalRatioY());
		geoImage.setCropBoxRelative(cb);
	}

	private GRectangle2D getCropBox() {
		GRectangle2D rect = geoImage.getCropBoxRelative();
		double locX = view.toScreenCoordXd(geoImage.getRealWorldX(0));
		double locY = getImageTop();
		GRectangle2D cb = AwtFactory.getPrototype().newRectangle2D();
		cb.setRect(rect.getMinX() * getOriginalRatioX() + locX,
				rect.getMinY() * getOriginalRatioY() + locY,
				rect.getWidth() * getOriginalRatioX(),
				rect.getHeight() * getOriginalRatioY());
		return cb;
	}

	private void updateCropVerical(GeoPoint A, GeoPoint D, GRectangle2D cropBox) {
		double screenAY = view.toScreenCoordYd(A.getInhomY());
		double screenDY = view.toScreenCoordYd(D.getInhomY());
		double screenCropHeight = screenAY - screenDY;
		double curScaleY = screenCropHeight / cropBox.getHeight();
		double imageScreenAy = view.toScreenCoordYd(geoImage.getCorner(0).getY());
		double newImageHeight = screenCropHeight * this.imagecropRatioY;
		double oldDistBottomSide = imageScreenAy - cropBox.getMaxY();
		double newDistBottomSide = oldDistBottomSide * curScaleY;
		double newBottomSideImgScr = screenAY + newDistBottomSide;
		double newBottomSideImg = view.toRealWorldCoordY(newBottomSideImgScr);
		geoImage.getCorner(0).setY(newBottomSideImg);
		geoImage.getCorner(1).setY(newBottomSideImg);
		if (geoImage.getCorner(2) != null) {
			double newTopSideImg = view.toRealWorldCoordY(newBottomSideImgScr - newImageHeight);
			geoImage.getCorner(2).setY(newTopSideImg);
		}
	}

	private void updateCropHorizontal(GeoPoint A, GeoPoint B,
			GRectangle2D cropBox) {
		double screenAX = view.toScreenCoordXd(A.getInhomX());
		double screenBX = view.toScreenCoordXd(B.getInhomX());
		double screenCropWidth = screenBX - screenAX;
		double curScaleX = screenCropWidth / cropBox.getWidth();
		double imageScreenAx = view.toScreenCoordXd(geoImage.getCorner(0).getX());
		double newImageWidth = screenCropWidth * this.imagecropRatioX;
		double oldDistLeftSide = cropBox.getMinX() - imageScreenAx;
		double newDistLeftSide = oldDistLeftSide * curScaleX;
		double newLeftSideImgScr = screenAX - newDistLeftSide;
		double newLeftSideImg = view.toRealWorldCoordX(newLeftSideImgScr);
		double newRightSideImg = view.toRealWorldCoordX(newLeftSideImgScr + newImageWidth);
		geoImage.getCorner(1).setX(newRightSideImg);
		geoImage.getCorner(0).setX(newLeftSideImg);
		if (geoImage.getCorner(2) != null) {
			geoImage.getCorner(2).setX(newLeftSideImg);
		}
	}

	private void setCorner(GeoPoint point, int corner) {
		if (!geoImage.isCropped()) {
			geoImage.setCorner(point, corner);
		}
	}

	@Override
	public GRectangle2D getBoundsForStylebarPosition() {
		if (geoImage.isCropped() && !view.getBoundingBox().isCropBox()) {
			return getCropBox();
		}
		return getBounds();
	}

	@Override
	public GRectangle2D getBoundsClipped() {
		updateIfNeeded();
		return super.getBoundsClipped();
	}
}

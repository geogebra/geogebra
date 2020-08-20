package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.EuclidianBoundingBoxHandler;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.MediaBoundingBox;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.util.MyMath;

public class DrawImageResizable extends DrawImage {
	private final TransformableRectangle transformableRectangle;
	private MediaBoundingBox boundingBox;

	/**
	 * Creates new drawable image
	 * @param view view
	 * @param geoImage image
	 */
	public DrawImageResizable(EuclidianView view,
			GeoImage geoImage) {
		super(view, geoImage);
		transformableRectangle =
				new TransformableRectangle(view, geoImage, true);
	}

	@Override
	public MediaBoundingBox getBoundingBox() {
		if (boundingBox == null) {
			boundingBox = transformableRectangle.getBoundingBox();
			boundingBox.setColor(view.getApplication().getPrimaryColor());
		}
		boundingBox.updateFrom(geo);
		return boundingBox;
	}

	@Override
	public List<GPoint2D> toPoints() {
		return transformableRectangle.toPoints();
	}

	@Override
	public void fromPoints(ArrayList<GPoint2D> points) {
		transformableRectangle.fromPoints(points);
	}

	private void updateImageCrop(GPoint2D p,
			EuclidianBoundingBoxHandler handler) {
		double newWidth;
		double newHeight;
		MyImage image = geoImage.getFillImage();
		int minWidth = Math.min(IMG_CROP_THRESHOLD, image.getWidth());
		int minHeight = Math.min(IMG_CROP_THRESHOLD, image.getHeight());
		GPoint2D event = atInverse.transform(p, null);
		geoImage.ensureCropBox();
		GRectangle2D cropBoxRelative = geoImage.getCropBoxRelative();
		double cropTop = cropBoxRelative.getY();
		double cropLeft = cropBoxRelative.getX();
		double cropBottom = cropTop + cropBoxRelative.getHeight();
		double cropRight = cropLeft + cropBoxRelative.getWidth();
		int imageWidth = geoImage.getFillImage().getWidth();
		int imageHeight = geoImage.getFillImage().getHeight();
		double originalRatio = transformableRectangle.getAspectRatio();
		switch (handler) {
		case BOTTOM:
			newHeight = MyMath.clamp(event.y - cropTop,
					minHeight, imageHeight - cropTop);
			cropBoxRelative.setFrame(cropLeft, cropTop,
					cropBoxRelative.getWidth(), newHeight);
			break;
		case TOP:
			newHeight = MyMath.clamp(cropBottom - event.y,
					minHeight, cropBottom);
			cropBoxRelative.setFrame(cropLeft, cropBottom - newHeight,
					cropBoxRelative.getWidth(), newHeight);
			break;
		case LEFT:
			newWidth = MyMath.clamp(cropRight - event.x,
					minWidth, cropRight);
			cropBoxRelative.setFrame(cropRight - newWidth, cropTop,
					newWidth, cropBoxRelative.getHeight());
			break;
		case RIGHT:
			newWidth = MyMath.clamp(event.x - cropLeft,
					minWidth, imageWidth - cropLeft);
			cropBoxRelative.setFrame(cropBoxRelative.getX(), cropBoxRelative.getY(),
					newWidth, cropBoxRelative.getHeight());
			break;
		case BOTTOM_RIGHT:
			newWidth = MyMath.clamp(event.x - cropLeft,
					minWidth, imageWidth - cropLeft);
			newHeight = MyMath.clamp(originalRatio * newWidth,
					minHeight, imageHeight	- cropTop);
			cropBoxRelative.setFrame(cropLeft, cropTop,
					newWidth, newHeight);
			break;
		case BOTTOM_LEFT:
			newWidth = MyMath.clamp(cropRight - event.x,
					minWidth, cropRight);
			newHeight = MyMath.clamp(originalRatio * newWidth,
					minHeight, imageHeight	- cropTop);
			cropBoxRelative.setFrame(cropRight - newWidth , cropTop,
					newWidth, newHeight);
			break;
		case TOP_RIGHT:
			newWidth = MyMath.clamp(event.x - cropLeft,
					minWidth, imageWidth - cropLeft);
			newHeight = MyMath.clamp(originalRatio * newWidth,
					minHeight, cropBottom);
			cropBoxRelative.setFrame(cropLeft, cropBottom - newHeight,
					newWidth, newHeight);
			break;
		case TOP_LEFT:
			newWidth = MyMath.clamp(cropRight - event.x,
					minWidth, cropRight);
			newHeight = MyMath.clamp(originalRatio * newWidth,
					minHeight,	cropBottom);
			cropBoxRelative.setFrame(cropRight - newWidth , cropBottom - newHeight,
					newWidth, newHeight);
			break;
		default:
			break;
		}
		geoImage.update();
	}

	@Override
	public GRectangle2D getBoundsForStylebarPosition() {
		if (geoImage.isCropped() && view.getBoundingBox() != null
				&& !view.getBoundingBox().isCropBox()) {
			return transformableRectangle.getBounds();
		}
		return getBounds();
	}

	@Override
	public void updateByBoundingBoxResize(GPoint2D point,
			EuclidianBoundingBoxHandler handler) {
		if (boundingBox.isCropBox()) {
			geoImage.setCropped(true);
			transformableRectangle.updateAspectRatio(geoImage, handler);
			updateImageCrop(point, handler);
		} else {
			transformableRectangle.updateByBoundingBoxResize(point, handler);
		}
	}

	@Override
	protected void updateAssumingVisible() {
		transformableRectangle.updateSelfAndBoundingBox();
		super.updateAssumingVisible();
	}
}

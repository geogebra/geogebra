package org.geogebra.common.euclidian;

import org.geogebra.common.awt.MyImage;

public class MeasurementToolBoundingBox extends MediaBoundingBox {

	/**
	 * Ruler bounding box
	 * @param rotationImage - rotation icon
	 */
	public MeasurementToolBoundingBox(MyImage rotationImage) {
		super(rotationImage);
	}

	@Override
	protected void updateHandlers() {
		double width = geo.getWidth();
		double height = geo.getHeight();
		setHandlerTransformed(0, 0, 0);
		setHandlerTransformed(1, 0, height);
		setHandlerTransformed(2, width, height);
		setHandlerTransformed(3, width, 0);
		setHandlerTransformed(8, width / 2,
				height + BoundingBox.ROTATION_HANDLER_DISTANCE);
	}
}

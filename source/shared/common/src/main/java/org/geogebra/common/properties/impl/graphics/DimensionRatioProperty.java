package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractProperty;

public class DimensionRatioProperty extends AbstractProperty {
	private EuclidianView euclidianView;
	private double xRatio = 1;
	private double yRatio = 1;

	/**
	 * Creates a ratio property for dimension of graphics view
	 * @param localization localization
	 * @param euclidianView euclidian view
	 */
	public DimensionRatioProperty(Localization localization, EuclidianView euclidianView) {
		super(localization, "Ratio");
		this.euclidianView = euclidianView;
	}

	/**
	 * @param xRatio x ratio
	 */
	public void setXRatio(String xRatio) {
		double xDouble = Double.parseDouble(xRatio);
		if (!Double.isInfinite(xDouble) && !Double.isNaN(xDouble)) {
			this.xRatio = xDouble;
			updateCoordSystem();
		}
	}

	/**
	 * @param yRatio y ratio
	 */
	public void setYRatio(String yRatio) {
		double yDouble = Double.parseDouble(yRatio);
		if (!Double.isInfinite(yDouble) && !Double.isNaN(yDouble)) {
			this.yRatio = yDouble;
			updateCoordSystem();
		}
	}

	public String getXRatio() {
		return String.valueOf(xRatio);
	}

	public String getYRatio() {
		return String.valueOf(yRatio);
	}

	private void updateCoordSystem() {
		euclidianView.setCoordSystem(euclidianView.getXZero(), euclidianView.getYZero(),
				euclidianView.getXscale(), euclidianView.getXscale() * xRatio / yRatio);
	}

	/**
	 * @param locked whether ratio is locked or not
	 */
	public void setRatioLocked(boolean locked) {
		if (locked) {
			euclidianView.getSettings().setLockedAxesRatio(xRatio / yRatio);
		} else {
			euclidianView.getSettings().setLockedAxesRatio(-1);
		}
	}

	public boolean isRatioLocked() {
		return euclidianView.isLockedAxesRatio();
	}

	public boolean isRatioEnabled() {
		return euclidianView.isZoomable() && !euclidianView.isLockedAxesRatio();
	}
}

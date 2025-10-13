package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.impl.AbstractNumericProperty;
import org.geogebra.common.properties.impl.AbstractProperty;

public class DimensionRatioProperty extends AbstractProperty {
	private final EuclidianViewInterfaceCommon euclidianView;
	private final AlgebraProcessor algebraProcessor;
	private final Kernel kernel;
	private final RatioNumericProperty xRatio;
	private final RatioNumericProperty yRatio;

	/**
	 * Creates a ratio property for dimension of graphics view
	 * @param localization localization
	 * @param euclidianView euclidian view
	 */
	public DimensionRatioProperty(Localization localization,
			EuclidianViewInterfaceCommon euclidianView) {
		super(localization, "Ratio");
		this.euclidianView = euclidianView;
		this.kernel = euclidianView.getKernel();
		this.algebraProcessor = euclidianView.getKernel().getAlgebraProcessor();
		xRatio = new RatioNumericProperty("xAxis");
		yRatio = new RatioNumericProperty("yAxis");
	}

	public AbstractNumericProperty getXRatioProperty() {
		return xRatio;
	}

	public AbstractNumericProperty getYRatioProperty() {
		return yRatio;
	}

	private void updateCoordSystem() {
		euclidianView.setCoordSystem(euclidianView.getXZero(), euclidianView.getYZero(),
				euclidianView.getXscale(), euclidianView.getXscale() * getRatio());
	}

	/**
	 * @param locked whether ratio is locked or not
	 */
	public void setRatioLocked(boolean locked) {
		double ratio = getRatio();
		if (locked && Double.isFinite(ratio)) {
			euclidianView.getSettings().setLockedAxesRatio(ratio);
		} else {
			euclidianView.getSettings().setLockedAxesRatio(EuclidianSettings.UNSET_LOCK_RATIO);
		}
	}

	private double getRatio() {
		return xRatio.value / yRatio.value;
	}

	public boolean isRatioLocked() {
		return euclidianView.isLockedAxesRatio();
	}

	public boolean isRatioEnabled() {
		return euclidianView.isZoomable() && !euclidianView.isLockedAxesRatio();
	}

	private class RatioNumericProperty extends AbstractNumericProperty
			implements SettingsDependentProperty {

		private double value = 1;

		public RatioNumericProperty(String label) {
			super(DimensionRatioProperty.this.algebraProcessor,
					DimensionRatioProperty.this.kernel.getLocalization(), label);
		}

		@Override
		protected void setNumberValue(GeoNumberValue value) {
			if (value != null) {
				this.value = value.getDouble();
				updateCoordSystem();
			}
		}

		@Override
		protected NumberValue getNumberValue() {
			return new MyDouble(kernel, this.value);
		}

		@Override
		public boolean isEnabled() {
			return isRatioEnabled();
		}

		@Override
		public AbstractSettings getSettings() {
			return euclidianView.getSettings();
		}
	}
}

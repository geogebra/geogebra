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

package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractNumericProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

public class DimensionRatioProperty extends AbstractPropertyCollection<Property> {
	/**
	 * Creates a ratio property for dimension of graphics view
	 * @param localization localization
	 * @param euclidianView euclidian view
	 */
	public DimensionRatioProperty(Localization localization,
			EuclidianViewInterfaceCommon euclidianView) {
		super(localization, "Ratio");
		setProperties(new Property[] {
				new RatioNumericProperty(euclidianView, 0),
				new RatioNumericProperty(euclidianView, 1),
				new LockedRatioProperty(localization, euclidianView)});
	}

	private static final class RatioNumericProperty extends AbstractNumericProperty
			implements SettingsDependentProperty {
		private final EuclidianViewInterfaceCommon euclidianView;
		private final int axis;

		RatioNumericProperty(EuclidianViewInterfaceCommon euclidianView, int axis) {
			super(euclidianView.getKernel().getAlgebraProcessor(),
					euclidianView.getKernel().getLocalization(), axis == 0 ? "xAxis" : "yAxis");
			this.euclidianView = euclidianView;
			this.axis = axis;
		}

		@Override
		protected NumberValue getNumberValue() {
			return new MyDouble(euclidianView.getKernel(),
					axis == 0 ? euclidianView.getXscale() : euclidianView.getYscale());
		}

		@Override
		protected void setNumberValue(GeoNumberValue value) {
			if (value == null) {
				return;
			}
			double xScale;
			double yScale;
			if (axis == 0) {
				xScale = value.getDouble();
				yScale = euclidianView.getYscale();
			} else {
				xScale = euclidianView.getXscale();
				yScale = value.getDouble();
			}
			euclidianView.setCoordSystem(euclidianView.getXZero(), euclidianView.getYZero(),
					xScale, yScale);
		}

		@Override
		public boolean isEnabled() {
			return euclidianView.isZoomable() && !euclidianView.isLockedAxesRatio();
		}

		@Override
		public AbstractSettings getSettings() {
			return euclidianView.getSettings();
		}
	}

	private static final class LockedRatioProperty extends AbstractValuedProperty<Boolean>
			implements BooleanProperty, SettingsDependentProperty {
		private final EuclidianViewInterfaceCommon euclidianView;

		LockedRatioProperty(Localization localization, EuclidianViewInterfaceCommon euclidianView) {
			super(localization, "");
			this.euclidianView = euclidianView;
		}

		@Override
		protected void doSetValue(Boolean value) {
			double ratio = euclidianView.getXscale() / euclidianView.getYscale();
			if (value && Double.isFinite(ratio)) {
				euclidianView.getSettings().setLockedAxesRatio(ratio);
			} else {
				euclidianView.getSettings().setLockedAxesRatio(EuclidianSettings.UNSET_LOCK_RATIO);
			}
		}

		@Override
		public Boolean getValue() {
			return euclidianView.isLockedAxesRatio();
		}

		@Override
		public boolean isEnabled() {
			return euclidianView.isZoomable() && !euclidianView.isLockedAxesRatio();
		}

		@Override
		public AbstractSettings getSettings() {
			return euclidianView.getSettings();
		}
	}
}

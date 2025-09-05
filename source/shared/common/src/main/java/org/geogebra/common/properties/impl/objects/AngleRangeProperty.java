package org.geogebra.common.properties.impl.objects;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class AngleRangeProperty extends AbstractNamedEnumeratedProperty<GeoAngle.AngleStyle> {
	private final GeoAngle angle;

	/**
	 * Constructs an AbstractNamedEnumeratedProperty
	 * @param localization the localization used
	 * @param geo construction element
	 */
	public AngleRangeProperty(Localization localization, GeoElement geo)
			throws NotApplicablePropertyException {
		super(localization, "AngleStyle");
		if (!(geo instanceof GeoAngle)) {
			throw new NotApplicablePropertyException(geo);
		}
		this.angle = (GeoAngle) geo;
		setValues(getOptions(angle.isDrawable(), angle.hasOrientation()));
	}

	private static List<GeoAngle.AngleStyle> getOptions(
			boolean isDrawable, boolean hasOrientation) {
		List<GeoAngle.AngleStyle> result = new ArrayList<>();

		if (hasOrientation) {
			result.add(GeoAngle.AngleStyle.ANTICLOCKWISE);
			result.add(GeoAngle.AngleStyle.NOTREFLEX);
			result.add(GeoAngle.AngleStyle.ISREFLEX);
			if (!isDrawable) {
				// don't want to allow (-inf, +inf)
				result.add(GeoAngle.AngleStyle.UNBOUNDED);
			}
		} else { // only 180degree wide intervals are possible
			result.add(GeoAngle.AngleStyle.NOTREFLEX);
			result.add(GeoAngle.AngleStyle.ISREFLEX);
		}
		return result;
	}

	@Override
	public String[] getValueNames() {
		return getValues().stream().map(value -> getLocalization().getPlain(
				"AandB", value.getMin(), value.getMax()
		)).toArray(String[]::new);
	}

	@Override
	protected void doSetValue(GeoAngle.AngleStyle value) {
		angle.setAngleStyle(value);
		angle.updateRepaint();
	}

	@Override
	public GeoAngle.AngleStyle getValue() {
		return angle.getAngleStyle();
	}
}

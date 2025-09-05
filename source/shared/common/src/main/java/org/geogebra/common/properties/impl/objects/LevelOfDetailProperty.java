package org.geogebra.common.properties.impl.objects;

import java.util.List;
import java.util.Map;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class LevelOfDetailProperty extends
		AbstractNamedEnumeratedProperty<SurfaceEvaluable.LevelOfDetail> {
	private GeoElement element;

	/**
	 * @param localization localization
	 * @param element construction element
	 */
	public LevelOfDetailProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "LevelOfDetail");
		if (!element.hasLevelOfDetail()) {
			throw new NotApplicablePropertyException(element);
		}
		setNamedValues(List.of(
				Map.entry(SurfaceEvaluable.LevelOfDetail.SPEED, "Speed"),
				Map.entry(SurfaceEvaluable.LevelOfDetail.QUALITY, "Quality")
		));
		this.element = element;
	}

	@Override
	protected void doSetValue(SurfaceEvaluable.LevelOfDetail value) {
		((SurfaceEvaluable) element).setLevelOfDetail(value);
		element.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public SurfaceEvaluable.LevelOfDetail getValue() {
		return ((SurfaceEvaluable) element).getLevelOfDetail();
	}
}

package org.geogebra.common.properties.impl.objects;

import java.util.List;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasHeadStyle;
import org.geogebra.common.kernel.geos.VectorHeadStyle;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class VectorHeadProperty extends AbstractEnumeratedProperty<VectorHeadStyle>
		implements IconsEnumeratedProperty<VectorHeadStyle> {

	private final HasHeadStyle vector;

	/**
	 * @param localization localization
	 * @param element construction element
	 */
	public VectorHeadProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Properties.Style");
		if (!(element instanceof HasHeadStyle)) {
			throw new NotApplicablePropertyException(element);
		}
		setValues(List.of(VectorHeadStyle.values()));
		vector = (HasHeadStyle) element;
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return new PropertyResource[] {
				PropertyResource.ICON_VECTOR_DECO_DEFAULT,
				PropertyResource.ICON_VECTOR_DECO_ARROW
		};
	}

	@Override
	protected void doSetValue(VectorHeadStyle value) {
		vector.setHeadStyle(value);
		vector.updateVisualStyleRepaint(GProperty.DECORATION);
	}

	@Override
	public VectorHeadStyle getValue() {
		return vector.getHeadStyle();
	}
}

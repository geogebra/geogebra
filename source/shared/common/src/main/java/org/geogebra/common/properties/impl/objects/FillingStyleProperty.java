package org.geogebra.common.properties.impl.objects;

import java.util.List;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.FillingStylePropertyDelegate;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Filling style
 */
public class FillingStyleProperty extends AbstractEnumeratedProperty<FillType>
			implements IconsEnumeratedProperty<FillType> {

	private static final PropertyResource[] icons = {
			PropertyResource.ICON_NO_FILLING, PropertyResource.ICON_FILLING_HATCHED,
			PropertyResource.ICON_FILLING_DOTTED, PropertyResource.ICON_FILLING_CROSSHATCHED,
			PropertyResource.ICON_FILLING_HONEYCOMB,
	};

	private final GeoElementDelegate delegate;

	/***/
	public FillingStyleProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Filling");
		delegate = new FillingStylePropertyDelegate(element);
		setValues(List.of(FillType.STANDARD,
				FillType.HATCH,
				FillType.DOTTED,
				FillType.CROSSHATCHED,
				FillType.HONEYCOMB));
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}

	@Override
	protected void doSetValue(FillType fillType) {
		GeoElement element = delegate.getElement();
		element.setFillType(fillType);
		element.updateVisualStyleRepaint(GProperty.HATCHING);
	}

	@Override
	public boolean isEnabled() {
		return delegate.isEnabled();
	}

	@Override
	public FillType getValue() {
		return delegate.getElement().getFillType();
	}
}

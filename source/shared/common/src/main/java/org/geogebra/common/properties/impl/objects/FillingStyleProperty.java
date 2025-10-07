package org.geogebra.common.properties.impl.objects;

import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.AbstractGeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.FillingStylePropertyDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Filling style
 */
public class FillingStyleProperty extends AbstractEnumeratedProperty<FillType>
			implements IconsEnumeratedProperty<FillType> {

	private static final Map<FillType, PropertyResource> icons = Map.of(
			FillType.STANDARD, PropertyResource.ICON_NO_FILLING,
			FillType.HATCH, PropertyResource.ICON_FILLING_HATCHED,
			FillType.CROSSHATCHED, PropertyResource.ICON_FILLING_CROSSHATCHED,
			FillType.CHESSBOARD, PropertyResource.ICON_FILLING_CHESSBOARD,
			FillType.DOTTED, PropertyResource.ICON_FILLING_DOTTED,
			FillType.HONEYCOMB, PropertyResource.ICON_FILLING_HONEYCOMB,
			FillType.BRICK, PropertyResource.ICON_FILLING_BRICK,
			FillType.WEAVING, PropertyResource.ICON_FILLING_WEAVING,
			FillType.SYMBOLS, PropertyResource.ICON_FILLING_SYMBOL,
			FillType.IMAGE, PropertyResource.ICON_FILLING_IMAGE);

	private final AbstractGeoElementDelegate delegate;

	/***/
	public FillingStyleProperty(Localization localization, GeoElement element, boolean extended)
			throws NotApplicablePropertyException {
		super(localization, "Filling");
		delegate = new FillingStylePropertyDelegate(element);
		setValues(extended ? List.of(FillType.values()) : List.of(FillType.STANDARD,
				FillType.HATCH,
				FillType.DOTTED,
				FillType.CROSSHATCHED,
				FillType.HONEYCOMB));
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return getValues().stream().map(icons::get).toArray(PropertyResource[]::new);
	}

	@Override
	public @CheckForNull String[] getLabels() {
		return null;
	}

	@Override
	protected void doSetValue(FillType fillType) {
		GeoElement element = delegate.getElement();
		element.setFillType(fillType);
		element.updateVisualStyleRepaint(GProperty.HATCHING);
	}

	@Override
	public boolean isEnabled() {
		return delegate.getElement().isEuclidianVisible();
	}

	@Override
	public FillType getValue() {
		return delegate.getElement().getFillType();
	}
}

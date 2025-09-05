package org.geogebra.common.properties.impl.objects;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class FillSymbolProperty  extends AbstractValuedProperty<String> implements StringProperty {

	private final GeoElement element;

	/**
	 * @param loc localization
	 * @param element construction element
	 * @throws NotApplicablePropertyException if filling is not "symbol"
	 */
	public FillSymbolProperty(Localization loc, GeoElement element) throws
			NotApplicablePropertyException {
		super(loc, "Symbol");
		if (!element.isFillable() || element.getFillType() != FillType.SYMBOLS) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = element;
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		return null;
	}

	@Override
	protected void doSetValue(String value) {
		element.setFillSymbol(value);
		element.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public String getValue() {
		return element.getFillSymbol();
	}
}
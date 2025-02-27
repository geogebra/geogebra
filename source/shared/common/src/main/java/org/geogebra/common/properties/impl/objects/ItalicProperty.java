package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.FontStyleDelegate;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class ItalicProperty extends AbstractValuedProperty<Boolean> implements BooleanProperty {
	private final GeoElementDelegate delegate;

	/**
	 * Italic property
	 * @param localization localization
	 * @param element element
	 */
	public ItalicProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Italic");
		delegate = new FontStyleDelegate(element);
	}

	@Override
	protected void doSetValue(Boolean value) {
		GeoElement element = delegate.getElement();
		if (element instanceof TextProperties) {
			TextProperties textProperties = (TextProperties) element;
			int oldStyle = textProperties.getFontStyle();
			int newStyle = value ? (oldStyle | GFont.ITALIC) : (oldStyle & ~GFont.ITALIC);
			if (oldStyle != newStyle) {
				textProperties.setFontStyle(newStyle);
				textProperties.updateVisualStyleRepaint(GProperty.FONT);
			}
		} else if (element instanceof HasTextFormatter) {
			HasTextFormatter hasTextFormatter = (HasTextFormatter) element;
			if (getLocalization() != null && !value.equals(hasTextFormatter.getFormatter()
					.getFormat("italic", false))) {
				hasTextFormatter.getFormatter().format("italic", value);
			}
			element.updateVisualStyle(GProperty.COMBINED);
		}
	}

	@Override
	public Boolean getValue() {
		GeoElement element = delegate.getElement();
		if (element instanceof TextProperties) {
			TextProperties textProperties = (TextProperties) element;
			return (textProperties.getFontStyle() & GFont.ITALIC) != 0;
		} else if (element instanceof HasTextFormatter) {
			HasTextFormatter hasTextFormatter = (HasTextFormatter) element;
			return hasTextFormatter.getFormatter().getFormat("italic", false);
		}

		return false;
	}
}

package org.geogebra.common.properties.impl.objects;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.util.StringPropertyWithSuggestions;

/**
 * {@code Property} responsible for setting an element's caption in the graphics view.
 * The caption can either be static or dynamic:
 * <ul>
 *     <li>Dynamic in case the value matches the label of a text,
 *         displaying the value of that label</li>
 *     <li>Static otherwise, displaying the given value</li>
 * </ul>
 * The suggested elements are the text labels: the potential dynamic caption values.
 */
public class CaptionProperty extends AbstractValuedProperty<String> implements
		StringPropertyWithSuggestions {
	private final GeoElement geoElement;

	/**
	 * @param localization this is used to localize the name
	 * @param geoElement the construction element
	 */
	public CaptionProperty(Localization localization, GeoElement geoElement) {
		super(localization, "Caption");
		this.geoElement = geoElement;
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		return null;
	}

	@Override
	protected void doSetValue(String value) {
		GeoText caption = (GeoText) geoElement.getKernel().lookupLabel(value);
		if (caption != null) {
			geoElement.setCaption(null);
			geoElement.setDynamicCaption(caption);
		} else {
			geoElement.removeDynamicCaption();
			geoElement.setCaption(value);
		}
		geoElement.updateRepaint();
	}

	@Override
	public String getValue() {
		return geoElement.hasDynamicCaption()
				? geoElement.getDynamicCaption().getLabelSimple()
				: geoElement.getCaptionSimple();
	}

	@Override
	public List<String> getSuggestions() {
		return geoElement.getConstruction().getGeoSetConstructionOrder().stream()
				.filter(GeoElement::isGeoText)
				.map(GeoElement::getLabelSimple)
				.collect(Collectors.toList());
	}
}
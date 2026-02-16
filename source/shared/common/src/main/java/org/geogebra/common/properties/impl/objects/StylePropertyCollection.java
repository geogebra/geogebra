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

package org.geogebra.common.properties.impl.objects;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.facade.BooleanPropertyListFacade;
import org.geogebra.common.properties.impl.facade.ColorPropertyListFacade;
import org.geogebra.common.properties.impl.facade.IconsEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.facade.NamedEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.facade.RangePropertyListFacade;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code PropertyCollection} containing {@code Property}s related to the style of objects.
 */
public class StylePropertyCollection extends AbstractPropertyCollection<Property> {
	/**
	 * Constructs the property for the given elements.
	 * @param propertiesFactory properties factory for creating property facades for the given list
	 * of elements
	 * @param localization localization for translating property names
	 * @param elements the elements to create the property for
	 * @throws NotApplicablePropertyException if the property is not applicable for the given
	 * elements
	 */
	public StylePropertyCollection(
			GeoElementPropertiesFactory propertiesFactory, Localization localization,
			List<GeoElement> elements) throws NotApplicablePropertyException {
		super(localization, "Style");
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		setProperties(Stream.<Property>of(
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new ChartSegmentSelectionProperty(localization, element,
								chartSegmentSelection),
						NamedEnumeratedPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new ObjectColorProperty(localization, element),
						ColorPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new ChartStyleGeoColorProperty(localization, element,
								chartSegmentSelection),
						ColorPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new OpacityProperty(localization, element),
						RangePropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new ChartStyleGeoOpacityProperty(
								localization, element, chartSegmentSelection),
						RangePropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new PointSizeProperty(localization, element),
						RangePropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new PointStyleProperty(localization, element),
						IconsEnumeratedPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new AngleArcSizeProperty(localization, element),
						RangePropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new SlopeSizeProperty(localization, element),
						RangePropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new LineStyleProperty(localization, element),
						IconsEnumeratedPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new ThicknessProperty(localization, element),
						RangePropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new LineOpacityProperty(localization, element),
						RangePropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new SegmentStartProperty(localization, element),
						IconsEnumeratedPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new SegmentEndProperty(localization, element),
						IconsEnumeratedPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new VectorHeadProperty(localization, element),
						IconsEnumeratedPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new SegmentDecorationProperty(localization, element),
						IconsEnumeratedPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new AngleDecorationProperty(localization, element),
						IconsEnumeratedPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new EmphasizeRightAngleProperty(localization, element),
						BooleanPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new InequalityOnAxisProperty(localization, element),
						BooleanPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new ImageInterpolationProperty(localization, element),
						BooleanPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new TrimmedIntersectionsProperty(localization, element),
						BooleanPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new HiddenLineStyleProperty(localization, element),
						NamedEnumeratedPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new LevelOfDetailProperty(localization, element),
						NamedEnumeratedPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new SliderOrientationProperty(localization, element),
						NamedEnumeratedPropertyListFacade::new)
		).filter(Objects::nonNull).toArray(Property[]::new));
		if (getProperties().length == 0) {
			throw new NotApplicablePropertyException(elements.get(0));
		}
	}
}

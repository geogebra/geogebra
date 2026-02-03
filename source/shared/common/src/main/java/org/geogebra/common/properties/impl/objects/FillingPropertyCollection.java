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

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.facade.BooleanPropertyListFacade;
import org.geogebra.common.properties.impl.facade.IconsEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.facade.ImagePropertyListFacade;
import org.geogebra.common.properties.impl.facade.NamedEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.facade.RangePropertyListFacade;
import org.geogebra.common.properties.impl.facade.StringPropertyListFacade;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.util.ImageManager;

/**
 * Collection of filling-related properties (fill type group, pattern style, hatching,
 * symbol, image, and inverse fill) for a list of {@link GeoElement}s.
 */
public class FillingPropertyCollection extends AbstractPropertyCollection<Property> {

	/**
	 * @param propertiesFactory factory for creating property facades
	 * @param localization localization
	 * @param elements geo elements
	 * @throws NotApplicablePropertyException if the elements do not support filling
	 */
	public FillingPropertyCollection(GeoElementPropertiesFactory propertiesFactory,
			Localization localization, ImageManager imageManager, List<GeoElement> elements)
			throws NotApplicablePropertyException {
		super(localization, "Filling");
		setProperties(new Property[]{
				propertiesFactory.createOptionalPropertyFacade(elements,
						element -> new InverseFillProperty(localization, element),
						BooleanPropertyListFacade::new),
				propertiesFactory.createPropertyFacadeThrowing(elements,
						element -> new FillCategoryProperty(localization, element),
						NamedEnumeratedPropertyListFacade::new),
				propertiesFactory.createPropertyFacadeThrowing(elements,
						element -> new FillSymbolProperty(localization, element),
						StringPropertyListFacade::new),
				propertiesFactory.createPropertyFacadeThrowing(elements,
						element -> new FillImageProperty(localization, imageManager, element),
						ImagePropertyListFacade::new),
				propertiesFactory.createPropertyFacadeThrowing(elements,
						element -> new PatternFillStyleProperty(localization, element),
						IconsEnumeratedPropertyListFacade::new),
				propertiesFactory.createPropertyFacadeThrowing(elements,
						element -> new HatchingDistanceProperty(localization, element),
						RangePropertyListFacade::new),
				propertiesFactory.createPropertyFacadeThrowing(elements,
						element -> new HatchingAngleProperty(localization, element),
						RangePropertyListFacade::new),
		});
	}
}

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

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertyCollectionWithLead;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.facade.BooleanPropertyListFacade;
import org.geogebra.common.properties.impl.facade.IconsEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.facade.ImagePropertyListFacade;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.util.ImageManager;

/** Properties for enabling a button icon and choosing a preset or custom image/icon. */
public class ButtonIconPropertyCollection extends PropertyCollectionWithLead {

	/**
	 * Constructs the property collection.
	 * @param propertiesFactory factory for creating properties
	 * @param localization localization for property names
	 * @param imageManager image manager for resolving preset button icons
	 * @param kernel kernel for registering the default button icon
	 * @param elements list of GeoElements to create properties for
	 * @throws NotApplicablePropertyException if the elements do not support icon
	 */
	public ButtonIconPropertyCollection(GeoElementPropertiesFactory propertiesFactory, Localization
			localization, ImageManager imageManager, Kernel kernel, List<GeoElement> elements)
			throws NotApplicablePropertyException {
		super(localization, "Icon",
				propertiesFactory.createPropertyFacadeThrowing(elements,
						element -> new ButtonIconShownProperty(localization, imageManager, element),
						BooleanPropertyListFacade::new),
				propertiesFactory.createPropertyFacadeThrowing(elements,
						element -> new ButtonIconProperty(localization, imageManager, element),
						IconsEnumeratedPropertyListFacade::new),
				propertiesFactory.createPropertyFacadeThrowing(elements,
						element -> new CustomButtonImageProperty(localization, imageManager,
								kernel, element),
						ImagePropertyListFacade::new));
	}
}

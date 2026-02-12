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

package org.geogebra.common.properties.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.properties.PropertiesRegistryListener;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyKey;
import org.geogebra.common.properties.impl.general.AngleUnitProperty;
import org.geogebra.common.properties.impl.general.GlobalLanguageProperty;
import org.junit.Before;
import org.junit.Test;

public class DefaultPropertiesRegistryTests extends BaseUnitTest
		implements PropertiesRegistryListener {

	private DefaultPropertiesRegistry propertiesRegistry;
	private List<Property> registeredProperties;

	@Before
	public void setUp() {
		propertiesRegistry = new DefaultPropertiesRegistry();
		propertiesRegistry.addListener(this);
		registeredProperties = new ArrayList<>();
	}

	@Test
	public void testRegister() {
		Property angleUnitProperty = new AngleUnitProperty(getKernel(), getLocalization());
		propertiesRegistry.register(angleUnitProperty);
		assertEquals(angleUnitProperty, propertiesRegistry.lookup(angleUnitProperty.getKey()));
	}

	@Test
	public void testUnregister() {
		Property angleUnitProperty = new AngleUnitProperty(getKernel(), getLocalization());
		propertiesRegistry.register(angleUnitProperty);
		propertiesRegistry.unregister(angleUnitProperty);
		assertNull(propertiesRegistry.lookup(angleUnitProperty.getKey()));
	}

	@Test
	public void testPropertiesRegistryListener() {
		GlobalLanguageProperty languageProperty = new GlobalLanguageProperty(getLocalization());
		propertiesRegistry.register(languageProperty);
		assertEquals(languageProperty, registeredProperties.get(0));
	}

	@Test
	public void testRelease() {
		Property angleUnitProperty = new AngleUnitProperty(getKernel(), getLocalization());

		propertiesRegistry.register(angleUnitProperty);
		propertiesRegistry.releaseProperties();

		PropertyKey key = PropertyKey.of(AngleUnitProperty.class);
		assertNull(propertiesRegistry.lookup(key));
	}

	// PropertiesRegistryListener

	@Override
	public void propertyRegistered(Property property) {
		registeredProperties.add(property);
	}

	@Override
	public void propertyUnregistered(Property property) {
	}
}

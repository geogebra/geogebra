package org.geogebra.common.properties.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.properties.PropertiesRegistryListener;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.general.AngleUnitProperty;
import org.geogebra.common.properties.impl.general.GlobalLanguageProperty;
import org.junit.Before;
import org.junit.Test;

public class DefaultPropertiesRegistryTests extends BaseUnitTest
		implements PropertiesRegistryListener {

	private DefaultPropertiesRegistry propertiesRegistry;
	private List<Property> registeredProperties;
	private List<Object> registeredPropertyContexts;

	@Before
	public void setUp() {
		propertiesRegistry = new DefaultPropertiesRegistry();
		propertiesRegistry.addListener(this);
		registeredProperties = new ArrayList<>();
		registeredPropertyContexts = new ArrayList<>();
	}

	@Test
	public void testRegister() {
		Property angleUnitProperty = new AngleUnitProperty(getKernel(), getLocalization());
		propertiesRegistry.register(angleUnitProperty);
		assertEquals(angleUnitProperty, propertiesRegistry.lookup(angleUnitProperty.getRawName()));
	}

	@Test
	public void testUnregister() {
		Property angleUnitProperty = new AngleUnitProperty(getKernel(), getLocalization());
		propertiesRegistry.register(angleUnitProperty);
		propertiesRegistry.unregister(angleUnitProperty);
		assertNull(propertiesRegistry.lookup(angleUnitProperty.getRawName()));
	}

	@Test
	public void testRegisterInDifferentContexts() {
		Property angleUnitProperty1 = new AngleUnitProperty(getKernel(), getLocalization());
		Object context1 = new Object();
		Property angleUnitProperty2 = new AngleUnitProperty(getKernel(), getLocalization());
		Object context2 = new Object();

		propertiesRegistry.register(angleUnitProperty1, context1);
		propertiesRegistry.register(angleUnitProperty2, context2);
		assertEquals(angleUnitProperty1, propertiesRegistry.lookup("AngleUnit", context1));
		assertEquals(angleUnitProperty2, propertiesRegistry.lookup("AngleUnit", context2));
	}

	@Test
	public void testPropertiesRegistryListener() {
		GlobalLanguageProperty languageProperty = new GlobalLanguageProperty(getLocalization());
		propertiesRegistry.register(languageProperty);
		assertEquals(languageProperty, registeredProperties.get(0));
		assertNull(registeredPropertyContexts.get(0));
	}

	@Test
	public void testRelease() {
		Property angleUnitProperty1 = new AngleUnitProperty(getKernel(), getLocalization());
		Object context1 = new Object();
		Property angleUnitProperty2 = new AngleUnitProperty(getKernel(), getLocalization());
		Object context2 = new Object();

		propertiesRegistry.register(angleUnitProperty1, context1);
		propertiesRegistry.register(angleUnitProperty2, context2);
		propertiesRegistry.releaseProperties(context1);

		assertNull(propertiesRegistry.lookup("AngleUnit", context1));
		assertEquals(angleUnitProperty2, propertiesRegistry.lookup("AngleUnit", context2));
	}

	// PropertiesRegistryListener

	@Override
	public void propertyRegistered(Property property, Object context) {
		registeredProperties.add(property);
		registeredPropertyContexts.add(context);
	}

	@Override
	public void propertyUnregistered(Property property, Object context) {
	}
}

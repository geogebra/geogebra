package org.geogebra.common.properties.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.general.AngleUnitProperty;
import org.junit.Before;
import org.junit.Test;

public class DefaultPropertiesRegistryTests extends BaseUnitTest {

	private DefaultPropertiesRegistry propertiesRegistry;

	@Before
	public void setUp() {
		propertiesRegistry = new DefaultPropertiesRegistry();
	}

	@Test
	public void testRegister() {
		Property angleUnitProperty = new AngleUnitProperty(getKernel(), getLocalization(), null);
		propertiesRegistry.register(angleUnitProperty);
		assertNotNull(propertiesRegistry.lookup(angleUnitProperty.getRawName()));
	}

	@Test
	public void testUnregister() {
		Property angleUnitProperty = new AngleUnitProperty(getKernel(), getLocalization(), null);
		propertiesRegistry.register(angleUnitProperty);
		propertiesRegistry.unregister(angleUnitProperty);
		assertNull(propertiesRegistry.lookup(angleUnitProperty.getRawName()));
	}

	@Test // TODO
	public void testAddListener() {
	}
}

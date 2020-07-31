package org.geogebra.common.properties.impl.objects;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.junit.Test;

public class NamePropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorSucceeds() {
		GeoElement slider = addAvInput("1");
		try {
			new NameProperty(getLocalization(), slider);
		} catch (NotApplicablePropertyException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testGivingTheSameNameDoesNotCreateIndex() throws NotApplicablePropertyException {
		GeoElement element = getElementFactory().createGeoLine();
		element.setLabel("line");
		NameProperty property = new NameProperty(getLocalization(), element);
		property.setValue("line");
		assertThat(element.getLabel(StringTemplate.defaultTemplate), is("line"));
	}
}
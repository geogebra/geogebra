package org.geogebra.common.properties.impl.objects;

import static org.junit.Assert.fail;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.junit.Test;

public class StepPropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorSucceeds() {
		GeoNumeric slider = addAvInput("1");
		slider.setEuclidianVisible(true);
		try {
			new AnimationStepProperty(getKernel().getAlgebraProcessor(), getLocalization(), slider);
		} catch (NotApplicablePropertyException e) {
			fail(e.getMessage());
		}
	}
}
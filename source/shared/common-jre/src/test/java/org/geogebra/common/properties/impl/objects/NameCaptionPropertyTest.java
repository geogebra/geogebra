package org.geogebra.common.properties.impl.objects;

import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_CAPTION;
import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_NAME;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.junit.Assert;
import org.junit.Test;

public class NameCaptionPropertyTest extends BaseUnitTest {

	@Test
	public void testChangePointName() throws NotApplicablePropertyException {
		GeoElement point = addAvInput("(1,2)");
		NameCaptionProperty property = new NameCaptionProperty(getLocalization(), point);
		Assert.assertEquals("A", property.getValue());
		Assert.assertEquals(LABEL_NAME, point.getLabelMode());
		point.setLabel("B");
		Assert.assertEquals("B", property.getValue());
		Assert.assertEquals(LABEL_NAME, point.getLabelMode());
	}

	@Test
	public void testInvalidNameBecomesCaption() throws NotApplicablePropertyException {
		GeoElement point = addAvInput("(1,2)");
			NameCaptionProperty property = new NameCaptionProperty(getLocalization(), point);
		Assert.assertEquals("A", property.getValue());
		property.doSetValue("point caption");
		Assert.assertEquals("A", point.getLabelSimple());
		Assert.assertEquals("point caption", point.getCaptionSimple());
		Assert.assertEquals(LABEL_CAPTION, point.getLabelMode());
	}

	@Test
	public void testNameToCaptionAndBack() throws NotApplicablePropertyException {
		GeoElement point = addAvInput("(1,2)");
		NameCaptionProperty property = new NameCaptionProperty(getLocalization(), point);
		Assert.assertEquals("A", property.getValue());
		property.doSetValue("point caption");
		property.doSetValue("A");
		Assert.assertEquals("A", point.getLabelSimple());
		Assert.assertEquals(LABEL_NAME, point.getLabelMode());
	}
}

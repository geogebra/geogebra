package org.geogebra.web.full.main;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.description.DefaultLabelDescriptionConverter;
import org.geogebra.common.kernel.geos.description.ProtectiveLabelDescriptionConverter;
import org.geogebra.common.util.ToStringConverter;
import org.geogebra.web.test.AppMocker;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ TextAreaElement.class })
public class AppWFullTest {

	@Test
	public void graphingUsesProtectiveFilter() {
		ToStringConverter<GeoElement> outputFilter =
				AppMocker.mockGraphing(getClass()).getLabelDescriptionConverter();
		assertThat(outputFilter instanceof ProtectiveLabelDescriptionConverter, is(true));
	}

	@Test
	public void geometryUsesNoFilter() {
		ToStringConverter<GeoElement> outputFilter =
				AppMocker.mockGeometry(getClass()).getLabelDescriptionConverter();
		assertThat(outputFilter instanceof DefaultLabelDescriptionConverter, is(true));
	}

	@Test
	public void casUsesNoFilter() {
		ToStringConverter<GeoElement> outputFilter =
				AppMocker.mockCas(getClass()).getLabelDescriptionConverter();
		assertThat(outputFilter instanceof DefaultLabelDescriptionConverter, is(true));
	}
}